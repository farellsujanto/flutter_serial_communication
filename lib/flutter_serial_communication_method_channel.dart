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

  /// Get list of available [DeviceInfo]
  @override
  Future<List<DeviceInfo>> getAvailableDevices() async {
    final availableDevices =
        await methodChannel.invokeMethod<String?>('getAvailableDevices');

    if (availableDevices == null || availableDevices == '[]') {
      return [];
    }

    final cleanedString = availableDevices.replaceAll('=', ':');
    final List<dynamic> rawDataList = jsonDecode(cleanedString);

    List<DeviceInfo> deviceInfos = [];
    deviceInfos = rawDataList.map((e) => DeviceInfo.fromMap(e)).toList();

    return deviceInfos;
  }

  /// Connect to device using [DeviceInfo]
  ///
  /// [DeviceInfo] got from returned array of [getAvailableDevices] also
  /// requires device's [baudRate] then
  /// returns [bool] to indicate whether the connection is success or not
  @override
  Future<bool> connect(DeviceInfo deviceInfo, int baudRate) async {
    final connectionData = <String, String>{
      'name': deviceInfo.deviceName.toString(),
      'baudRate': baudRate.toString(),
    };

    final isConnected =
        await methodChannel.invokeMethod<bool>('connect', connectionData);
    return isConnected ?? false;
  }

  /// Disconnects from connected device
  @override
  Future<void> disconnect() async {
    await methodChannel.invokeMethod<bool>('disconnect');
  }

  /// Send data to serial port using [Uint8List]
  @override
  Future<bool> write(Uint8List data) async {
    final isSent = await methodChannel.invokeMethod<bool>('write', data);
    return isSent ?? false;
  }

  /// Listen to message received from serial port
  @override
  EventChannel getSerialMessageListener() {
    const EventChannel stream =
        EventChannel('$eventChannelID/serialStreamChannel');
    return stream;
  }

  /// Listen to device connection status
  @override
  EventChannel getDeviceConnectionListener() {
    const EventChannel stream =
        EventChannel('$eventChannelID/deviceConnectionStreamChannel');
    return stream;
  }
}
