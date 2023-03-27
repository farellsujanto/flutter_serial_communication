import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_serial_communication_platform_interface.dart';

/// An implementation of [FlutterSerialCommunicationPlatform] that uses method channels.
class MethodChannelFlutterSerialCommunication
    extends FlutterSerialCommunicationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_serial_communication');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<List<String>?> getAvailableDevices() async {
    final availableDevices =
        await methodChannel.invokeMethod<List<dynamic>>('getAvailableDevices');
    return availableDevices?.map((e) => e.toString()).toList();
  }

  @override
  Future<bool> connect(int index, int baudRate) async {
    final connectionData = <String, String>{
      'index': index.toString(),
      'baudRate': baudRate.toString(),
    };

    final isConnected =
        await methodChannel.invokeMethod<bool>('connect', connectionData);
    return isConnected ?? false;
  }

  @override
  Future<void> disconnect() async {
    await methodChannel.invokeMethod<bool>('disconnect');
  }

  @override
  Future<bool> write(Uint8List data) async {print(data);
    final isSent = await methodChannel.invokeMethod<bool>('write', data);
    return isSent ?? false;
  }
}
