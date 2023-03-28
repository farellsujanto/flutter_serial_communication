// import 'package:flutter/services.dart';
// import 'package:flutter_test/flutter_test.dart';
// import 'package:flutter_serial_communication/flutter_serial_communication_method_channel.dart';

// void main() {
//   MethodChannelFlutterSerialCommunication platform = MethodChannelFlutterSerialCommunication();
//   const MethodChannel channel = MethodChannel('flutter_serial_communication');

//   TestWidgetsFlutterBinding.ensureInitialized();

//   setUp(() {
//     channel.setMockMethodCallHandler((MethodCall methodCall) async {
//       return '42';
//     });
//   });

//   tearDown(() {
//     channel.setMockMethodCallHandler(null);
//   });

//   test('getPlatformVersion', () async {
//     expect(await platform.getPlatformVersion(), '42');
//   });
// }
