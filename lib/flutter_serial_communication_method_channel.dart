import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_serial_communication/models/device_info.dart';

import 'flutter_serial_communication_platform_interface.dart';

const eventChannelID =
    'id.farellsujanto.flutter_serial_communication.flutter_event_channel';

/// An implementation of [FlutterSerialCommunicationPlatform] that uses method channels.
class MethodChannelFlutterSerialCommunication
    extends FlutterSerialCommunicationPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_serial_communication');

  @override
  Future<List<String>?> getAvailableDevices() async {
    final availableDevices =
        await methodChannel.invokeMethod<List<dynamic>>('getAvailableDevices');
    return availableDevices?.map((e) => e.toString()).toList();
  }

  @override
  Future<List<DeviceInfo>> getDetailedAvailableDevices() async {
    final availableDevices = await methodChannel
        .invokeMethod<String?>('getDetailedAvailableDevices');

    if (availableDevices == null || availableDevices == '[]') {
      return [];
    }

    final cleanedString = availableDevices.replaceAll('=', ':');
    final List<dynamic> rawDataList = jsonDecode(cleanedString);

    List<DeviceInfo> deviceInfos = [];
    deviceInfos = rawDataList.map((e) => DeviceInfo.fromMap(e)).toList();

    return deviceInfos;
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
  Future<bool> write(Uint8List data) async {
    final isSent = await methodChannel.invokeMethod<bool>('write', data);
    return isSent ?? false;
  }

  @override
  EventChannel getSerialMessageListener() {
    const EventChannel stream =
        EventChannel('$eventChannelID/serialStreamChannel');
    return stream;
  }

  @override
  EventChannel getDeviceConnectionListener() {
    const EventChannel stream =
        EventChannel('$eventChannelID/deviceConnectionStreamChannel');
    return stream;
  }
}
