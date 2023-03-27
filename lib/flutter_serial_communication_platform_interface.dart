import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_serial_communication_method_channel.dart';

abstract class FlutterSerialCommunicationPlatform extends PlatformInterface {
  /// Constructs a FlutterSerialCommunicationPlatform.
  FlutterSerialCommunicationPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterSerialCommunicationPlatform _instance = MethodChannelFlutterSerialCommunication();

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

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<List<String>?> getAvailableDevices() {
    throw UnimplementedError('getAvailableDevices() has not been implemented.');
  }
}
