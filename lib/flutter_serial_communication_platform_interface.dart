import 'package:flutter/services.dart';
import 'package:flutter_serial_communication/models/device_info.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_serial_communication_method_channel.dart';

abstract class FlutterSerialCommunicationPlatform extends PlatformInterface {
  /// Constructs a FlutterSerialCommunicationPlatform.
  FlutterSerialCommunicationPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterSerialCommunicationPlatform _instance =
      MethodChannelFlutterSerialCommunication();

  /// The default instance of [FlutterSerialCommunicationPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterSerialCommunication].
  static FlutterSerialCommunicationPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterSerialCommunicationPlatform] when
  /// they register themselves.
  static set instance(FlutterSerialCommunicationPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<List<DeviceInfo>> getAvailableDevices() {
    throw UnimplementedError('getAvailableDevices() has not been implemented.');
  }

  Future<bool> connect(DeviceInfo deviceInfo, int baudRate) {
    throw UnimplementedError('connect() has not been implemented.');
  }

  Future<void> disconnect() {
    throw UnimplementedError('disconnect() has not been implemented.');
  }

  Future<bool> write(Uint8List data) {
    throw UnimplementedError('write() has not been implemented.');
  }

  EventChannel getSerialMessageListener() {
    throw UnimplementedError(
        'getSerialMessageListener() has not been implemented.');
  }

  EventChannel getDeviceConnectionListener() {
    throw UnimplementedError(
        'getDeviceConnectionListener() has not been implemented.');
  }

  Future<void> setDTR(bool set) {
    throw UnimplementedError('setDTR() has not been implemented.');
  }

  Future<void> setRTS(bool set) {
    throw UnimplementedError('setRTS() has not been implemented.');
  }

  Future<void> setParameters(
      int baudRate, int dataBits, int stopBits, int parity) {
    throw UnimplementedError('setParameters() has not been implemented.');
  }

  Future<void> purgeHwBuffers(bool purgeWriteBuffers, bool purgeReadBuffers) {
    throw UnimplementedError('purgeHwBuffers() has not been implemented.');
  }
}
