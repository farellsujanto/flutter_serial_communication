package id.farellsujanto.flutter_serial_communication;

import android.hardware.usb.UsbDevice;
import android.os.Build;
import java.util.HashMap;

class DeviceInfo {
    int deviceId;
    String version;
    String deviceName;
    String manufacturerName;
    String productName;
    int productId;
    int vendorId;
    String serialNumber;

    DeviceInfo(UsbDevice device) {
        this.deviceId = device.getDeviceId();
        this.deviceName = device.getDeviceName();
        this.productId = device.getProductId();
        this.vendorId = device.getVendorId();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.manufacturerName = device.getManufacturerName();
            this.productName = device.getProductName();
            // this.serialNumber = device.getSerialNumber();
        } else {
            this.manufacturerName = "";
            this.serialNumber = "";
            this.productName = "";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.version = device.getVersion();
        } else {
            this.version = "";
        }
    }

    HashMap<String, String> toMap() {
        HashMap<String, String> hm = new HashMap<>();

        hm.put("\"deviceId\"", "\"" + this.deviceId + "\"");
        hm.put("\"deviceName\"", "\"" + this.deviceName + "\"");
        hm.put("\"productId\"", "\"" + this.productId + "\"");
        hm.put("\"vendorId\"", "\"" + this.vendorId + "\"");
        hm.put("\"manufacturerName\"", "\"" + this.manufacturerName + "\"");
        hm.put("\"productName\"", "\"" + this.productName + "\"");
        hm.put("\"serialNumber\"", "\"" + this.serialNumber + "\"");
        hm.put("\"version\"", "\"" + this.version + "\"");

        return hm;
    }
}
