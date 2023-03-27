
import 'flutter_serial_communication_platform_interface.dart';

class FlutterSerialCommunication {
  Future<String?> getPlatformVersion() {
    return FlutterSerialCommunicationPlatform.instance.getPlatformVersion();
  }

  Future<List<String>?> getAvailableDevices() {
    return FlutterSerialCommunicationPlatform.instance.getAvailableDevices();
  }
}
