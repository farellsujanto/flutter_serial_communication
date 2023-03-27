import 'package:flutter/foundation.dart';

import 'flutter_serial_communication_platform_interface.dart';

class FlutterSerialCommunication {
  Future<String?> getPlatformVersion() {
    return FlutterSerialCommunicationPlatform.instance.getPlatformVersion();
  }

  Future<List<String>?> getAvailableDevices() {
    return FlutterSerialCommunicationPlatform.instance.getAvailableDevices();
  }

  Future<bool> connect(int index, int baudRate) async {
    return FlutterSerialCommunicationPlatform.instance.connect(index, baudRate);
  }

  Future<void> disconnect() async {
    return FlutterSerialCommunicationPlatform.instance.disconnect();
  }

  Future<bool> write(Uint8List data) async {
    final isSent =
        await FlutterSerialCommunicationPlatform.instance.write(data);
    return isSent;
  }
}
