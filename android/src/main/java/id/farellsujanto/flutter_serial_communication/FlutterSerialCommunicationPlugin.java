package id.farellsujanto.flutter_serial_communication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;

import androidx.annotation.NonNull;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FlutterSerialCommunicationPlugin */
public class FlutterSerialCommunicationPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, SerialInputOutputManager.Listener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private static final String INTENT_ACTION_GRANT_USB = BuildConfig.LIBRARY_PACKAGE_NAME + ".GRANT_USB";
  public static final String SERIAL_STREAM_CHANNEL = "id.farellsujanto.flutter_serial_communication.flutter_event_channel/serialStreamChannel";
  public static final String DEVICE_CONNECTION_STREAM_CHANNEL = "id.farellsujanto.flutter_serial_communication.flutter_event_channel/deviceConnectionStreamChannel";
  private static final int WRITE_WAIT_MILLIS = 2000;

  private SerialMessageHandler serialMessageHandler = new SerialMessageHandler();
  private DeviceConnectionHandler deviceConnectionHandler = new DeviceConnectionHandler();
  private SerialInputOutputManager usbIoManager;
  private MethodChannel channel;
  private FlutterActivity activity;
  private BinaryMessenger binaryMessenger;
  private UsbSerialPort usbSerialPort;
  private  UsbManager usbManager;

  private boolean connected = false;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_serial_communication");
    channel.setMethodCallHandler(this);
    binaryMessenger = flutterPluginBinding.getBinaryMessenger();

    new EventChannel(binaryMessenger, SERIAL_STREAM_CHANNEL).setStreamHandler(serialMessageHandler);
    new EventChannel(binaryMessenger, DEVICE_CONNECTION_STREAM_CHANNEL).setStreamHandler(deviceConnectionHandler);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "getAvailableDevices": {
        result.success(getConvertedAvailableDevices(getAvailableDevices()));
        break;
      }
      case "write": {
        byte[] data = call.arguments();
        result.success(write(data));
        break;
      }
      case "connect": {
        Map<String, String> connectArgs = call.arguments();
        String name = connectArgs.get("name");
        int baudRate = Integer.valueOf(connectArgs.get("baudRate"));
        result.success(connect(name, baudRate));
        break;
      }
      case "disconnect": {
        disconnect();
        break;
      }
      default: {
        result.notImplemented();
      }
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = (FlutterActivity) binding.getActivity();
    usbManager = (UsbManager) activity.getSystemService(Context.USB_SERVICE);
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {}

  @Override
  public void onDetachedFromActivity() {}

  public List<DeviceInfo> getAvailableDevices() {
    List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);

    List<DeviceInfo> deviceInfoList = new ArrayList<>(availableDrivers.size());

    for (UsbSerialDriver driver : availableDrivers) {
      UsbDevice device = driver.getDevice();
      deviceInfoList.add(new DeviceInfo(device));
    }

    return deviceInfoList;
  }

  public String getConvertedAvailableDevices(List<DeviceInfo> deviceInfoList) {
    List<Map<String, String>> convertedDeviceInfoList = new ArrayList<>(deviceInfoList.size());

    for (DeviceInfo deviceInfo : deviceInfoList) {
      convertedDeviceInfoList.add(deviceInfo.toMap());
    }

    return convertedDeviceInfoList.toString();
  }

  public boolean write(byte[] data) {
    try {
      usbSerialPort.write(data, WRITE_WAIT_MILLIS);
      return true;
    } catch (IOException e) {
      Log.e("Error Sending", e.getMessage());
      disconnect();
    }
    return false;
  }

  public boolean connect(String name, int baudRate) {
    if (connected) {
      return false;
    }

    List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
    List<DeviceInfo> availableDevices = getAvailableDevices();
    if (availableDrivers.size() == 0 || availableDevices.size() == 0) {
      return false;
    }

    int index = -1;
    for (int i = 0; i < availableDevices.size(); i++) {
      if (availableDevices.get(i).deviceName.equals(name)) {
        index = i;
        break;
      }
    }

    if (index == -1) {
      return false;
    }

    UsbSerialDriver driver = availableDrivers.get(index);
    if(!usbManager.hasPermission(driver.getDevice())) {
      int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_MUTABLE : 0;
      PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(activity, 0, new Intent(INTENT_ACTION_GRANT_USB), flags);
      usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
    } else {
      try {
        usbSerialPort = driver.getPorts().get(0);
        UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
        usbSerialPort.open(usbConnection);
        usbSerialPort.setParameters(baudRate, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        connected = true;
        deviceConnectionHandler.success(true);
        usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
        usbIoManager.start();

        return true;
      } catch (IOException e) {
        e.printStackTrace();
        disconnect();
      }
    }
    return false;

  }

  public void disconnect() {
    connected = false;
    deviceConnectionHandler.success(false);
    try {
      if(usbIoManager != null) {
        usbIoManager.setListener(null);
        usbIoManager.stop();
      }
      usbIoManager = null;
      usbSerialPort.close();
    } catch (IOException ignored) {}
    usbSerialPort = null;
  }

  @Override
  public void onNewData(byte[] data) {
    activity.runOnUiThread(() -> { serialMessageHandler.success(data); });
  }

  @Override
  public void onRunError(Exception e) {
    disconnect();
  }
}