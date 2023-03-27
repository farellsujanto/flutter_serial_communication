import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_serial_communication_platform_interface.dart';

/// An implementation of [FlutterSerialCommunicationPlatform] that uses method channels.
class MethodChannelFlutterSerialCommunication extends FlutterSerialCommunicationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_serial_communication');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<List<String>?> getAvailableDevices() async {
    final availableDevices = await methodChannel.invokeMethod<List<dynamic>>('getAvailableDevices');
    return availableDevices?.map((e) => e.toString()).toList();
  }
}
