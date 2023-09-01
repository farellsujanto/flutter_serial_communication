package id.farellsujanto.flutter_serial_communication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

class USBGrantReceiver extends BroadcastReceiver {
    FlutterSerialCommunicationPlugin plugin;

    public USBGrantReceiver(FlutterSerialCommunicationPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (PluginConfig.INTENT_ACTION_GRANT_USB.equals(action)) {
            boolean granted = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
            if (granted) {
                plugin.openPort();
            } else {
                plugin.onUsbPermissionDenied();
            }
        }
    }
}