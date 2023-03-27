import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_serial_communication/flutter_serial_communication.dart';
import 'package:flutter_serial_communication/flutter_serial_communication_platform_interface.dart';
import 'package:flutter_serial_communication/flutter_serial_communication_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFlutterSerialCommunicationPlatform
    with MockPlatformInterfaceMixin
    implements FlutterSerialCommunicationPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final FlutterSerialCommunicationPlatform initialPlatform = FlutterSerialCommunicationPlatform.instance;

  test('$MethodChannelFlutterSerialCommunication is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFlutterSerialCommunication>());
  });

  test('getPlatformVersion', () async {
    FlutterSerialCommunication flutterSerialCommunicationPlugin = FlutterSerialCommunication();
    MockFlutterSerialCommunicationPlatform fakePlatform = MockFlutterSerialCommunicationPlatform();
    FlutterSerialCommunicationPlatform.instance = fakePlatform;

    expect(await flutterSerialCommunicationPlugin.getPlatformVersion(), '42');
  });
}
