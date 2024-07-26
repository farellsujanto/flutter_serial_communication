package id.farellsujanto.flutter_serial_communication;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.List;
import java.util.Map;

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

/**
 * FlutterSerialCommunicationPlugin
 */
public class FlutterSerialCommunicationPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, SerialInputOutputManager.Listener {
  private SerialMessageHandler serialMessageHandler = new SerialMessageHandler();
  private DeviceConnectionHandler deviceConnectionHandler = new DeviceConnectionHandler();

  private SerialInputOutputManager usbIoManager;
  private MethodChannel channel;
  private BinaryMessenger binaryMessenger;
  private UsbSerialPort usbSerialPort;
  private UsbManager usbManager;
  private UsbSerialDriver driver;

  private Result connectResult;
  private int write_wait_millis = 2000;
  private int baudRate = 9600;
  private int dataBits = UsbSerialPort.DATABITS_8;
  private int stopBits = UsbSerialPort.STOPBITS_1;
  private int parity = UsbSerialPort.PARITY_NONE;
  private boolean connected = false;
  private USBGrantReceiver usbGrantReceiver = null;
  private boolean purgeWriteBuffers = false;
  private boolean purgeReadBuffers = false;

  private FlutterActivity activity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(),
        "flutter_serial_communication");

    channel.setMethodCallHandler(this);
    binaryMessenger = flutterPluginBinding.getBinaryMessenger();

    new EventChannel(binaryMessenger,
        PluginConfig.SERIAL_STREAM_CHANNEL)
        .setStreamHandler(serialMessageHandler);

    new EventChannel(binaryMessenger,
        PluginConfig.DEVICE_CONNECTION_STREAM_CHANNEL)
        .setStreamHandler(deviceConnectionHandler);
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
      case "setRTS": {
        setRTS(call.arguments(), result);
        break;
      }
      case "setDTR": {
        setDTR(call.arguments(), result);
        break;
      }
      case "setParameters": {
        baudRate = call.argument("baudRate");
        dataBits = call.argument("dataBits");
        stopBits = call.argument("stopBits");
        parity = call.argument("parity");
        setParameters(baudRate, dataBits, stopBits, parity, result);
        break;
      }
      case "connect": {
        String name = call.argument("name");
        baudRate = call.argument("baudRate");

        connectResult = result;
        connect(name);
        break;
      }
      case "disconnect": {
        disconnect();
        result.success(true);
        break;
      }
      case "purgeHwBuffers": {
        purgeWriteBuffers = call.argument("purgeWriteBuffers");
        purgeReadBuffers = call.argument("purgeReadBuffers");
        purgeHwBuffers(purgeWriteBuffers, purgeReadBuffers, result);
        break;
      }
      default: {
        result.notImplemented();
      }
    }
  }

  void setRTS(boolean set, Result result) {
    boolean success = false;
    if(usbSerialPort != null) {
      try {
        usbSerialPort.setRTS(set);
        success = true;
      } catch (IOException exception) {

      }
    }
    result.success(success);
  }

  void setDTR(boolean set, Result result) {
    boolean success = false;
    if(usbSerialPort != null) {
      try {
        usbSerialPort.setDTR(set);
        success = true;
      } catch (IOException exception) {

      }
    }
    result.success(success);
  }

  void setParameters(int baudRate, int dataBits, int stopBits, int parity, Result result) {
    boolean success = false;
    if(usbSerialPort != null) {
      try {
        usbSerialPort.setParameters(baudRate, dataBits, stopBits, parity);
        success = true;
      } catch (IOException exception) {

      }
    }
    result.success(success);
  }

  void purgeHwBuffers(boolean purgeWriteBuffers, boolean purgeReadBuffers, Result result) {
    boolean success = false;
    if(usbSerialPort != null) {
      try {
        usbSerialPort.purgeHwBuffers(purgeWriteBuffers, purgeReadBuffers);
        success = true;
      } catch (IOException exception) {

      }
    }
    result.success(success);
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

  private List<DeviceInfo> getAvailableDevices() {
    List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);

    List<DeviceInfo> deviceInfoList = new ArrayList<>(availableDrivers.size());

    for (UsbSerialDriver driver : availableDrivers) {
      UsbDevice device = driver.getDevice();
      deviceInfoList.add(new DeviceInfo(device));
    }

    return deviceInfoList;
  }

  private String getConvertedAvailableDevices(List<DeviceInfo> deviceInfoList) {
    List<Map<String, String>> convertedDeviceInfoList = new ArrayList<>(deviceInfoList.size());

    for (DeviceInfo deviceInfo : deviceInfoList) {
      convertedDeviceInfoList.add(deviceInfo.toMap());
    }

    return convertedDeviceInfoList.toString();
  }

  private boolean write(byte[] data) {
    try {
      usbSerialPort.write(data, write_wait_millis);
      return true;
    } catch (IOException e) {
      Log.e("Error Sending", e.getMessage());
      disconnect();
    }
    return false;
  }

  private void connect(String name) {
    if (connected) {
      handleConnectResult(false);
      return;
    }

    List<UsbSerialDriver> availableDrivers = UsbSerialProber
        .getDefaultProber().findAllDrivers(usbManager);
    List<DeviceInfo> availableDevices = getAvailableDevices();
    if (availableDrivers.size() == 0 || availableDevices.size() == 0) {
      handleConnectResult(false);
      return;
    }

    int index = -1;
    for (int i = 0; i < availableDevices.size(); i++) {
      if (availableDevices.get(i).deviceName.equals(name)) {
        index = i;
        break;
      }
    }

    if (index < 0) {
      handleConnectResult(false);
      return;
    }

    driver = availableDrivers.get(index);

    if (usbManager.hasPermission(driver.getDevice()) == false) {
      int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
          ? PendingIntent.FLAG_MUTABLE : 0;
      usbGrantReceiver = new USBGrantReceiver(this);
      if (Build.VERSION.SDK_INT >= 34) {
        activity.registerReceiver(
          usbGrantReceiver,
          new IntentFilter(PluginConfig.INTENT_ACTION_GRANT_USB),
          Context.RECEIVER_EXPORTED
        );
      } else {
        activity.registerReceiver(
          usbGrantReceiver,
          new IntentFilter(PluginConfig.INTENT_ACTION_GRANT_USB)
        );
      }

      PendingIntent usbGrantIntent = PendingIntent.getBroadcast(activity,
          0,
          new Intent(PluginConfig.INTENT_ACTION_GRANT_USB), flags);

      usbManager.requestPermission(driver.getDevice(), usbGrantIntent);
    } else {
      openPort();
    }
  }

  public void openPort() {
    try {
      usbSerialPort = driver.getPorts().get(0);
      //usbSerialPort.getControlLines();
      usbSerialPort. getSupportedControlLines();

      UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
      usbSerialPort.open(usbConnection);

      usbSerialPort.setParameters(baudRate, UsbSerialPort.DATABITS_8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
      connected = true;
      deviceConnectionHandler.success(true);
      usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
      usbIoManager.start();

      handleConnectResult(true);
      return;
    } catch (IOException e) {
      e.printStackTrace();
      disconnect();
    }

    handleConnectResult(false);
  }

  public void onUsbPermissionDenied() {
    handleConnectResult(false);
  }

  private void disconnect() {
    connected = false;
    deviceConnectionHandler.success(false);
    try {
      if (usbIoManager != null) {
        usbIoManager.setListener(null);
        usbIoManager.stop();
      }
      usbIoManager = null;
      if(usbSerialPort.isOpen()) {
       usbSerialPort.close();
      }
    } catch (IOException ignored) {
    }
  }

  private void handleConnectResult(boolean value) {
    if(connectResult != null) {
      connectResult.success(value);
      connectResult = null;
    }
    if(usbGrantReceiver != null) {
      activity.unregisterReceiver(usbGrantReceiver);
      usbGrantReceiver = null;
    }
  }



  @Override
  public void onNewData(byte[] data) {
    activity.runOnUiThread(() -> {
      serialMessageHandler.success(data);
    });
  }

  @Override
  public void onRunError(Exception e) {
    activity.runOnUiThread(() -> {
      disconnect();
    });
  }
}
