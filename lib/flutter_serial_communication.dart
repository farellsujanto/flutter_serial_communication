import 'package:flutter/services.dart';
import 'package:flutter_serial_communication/models/device_info.dart';

import 'flutter_serial_communication_platform_interface.dart';

class FlutterSerialCommunication {
  Future<List<DeviceInfo>> getAvailableDevices() {
    return FlutterSerialCommunicationPlatform.instance.getAvailableDevices();
  }

  Future<bool> connect(DeviceInfo deviceInfo, int baudRate) async {
    return FlutterSerialCommunicationPlatform.instance
        .connect(deviceInfo, baudRate);
  }

  Future<void> disconnect() async {
    return FlutterSerialCommunicationPlatform.instance.disconnect();
  }

  Future<bool> write(Uint8List data) async {
    final isSent =
        await FlutterSerialCommunicationPlatform.instance.write(data);
    return isSent;
  }

  EventChannel getSerialMessageListener() {
    return FlutterSerialCommunicationPlatform.instance
        .getSerialMessageListener();
  }

  EventChannel getDeviceConnectionListener() {
    return FlutterSerialCommunicationPlatform.instance
        .getDeviceConnectionListener();
  }

  Future<void> setDTR(bool set) async {
    return FlutterSerialCommunicationPlatform.instance.setDTR(set);
  }

  Future<void> setRTS(bool set) async {
    return FlutterSerialCommunicationPlatform.instance.setRTS(set);
  }

  Future<void> setParameters(
      int baudRate, int dataBits, int stopBits, int parity) async {
    return FlutterSerialCommunicationPlatform.instance
        .setParameters(baudRate, dataBits, stopBits, parity);
  }

  Future<void> purgeHwBuffers(
      bool purgeWriteBuffers, bool purgeReadBuffers) async {
    return FlutterSerialCommunicationPlatform.instance
        .purgeHwBuffers(purgeWriteBuffers, purgeReadBuffers);
  }
}
