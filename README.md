## About Flutter Serial Communication

This library aims to ease Flutter Developers to do serial communcation with their peripherals without too much hassle.

Currently supports **Android USB (OTG)** to communicate with Arduinos and other USB serial hardware on Android **WITHOUT** using **root access**.

## Installing

**1.** Install it to your flutter project
```
flutter pub add flutter_serial_communication
```
or by adding dependencies to your package's pubspec.yaml then run `dart pub get`
```
dependencies:
  flutter_serial_communication: 0.2.5
```

**2.** Import it in your .dart file
```dart
import 'package:flutter_serial_communication/flutter_serial_communication.dart';
```


## Features
- Doesn't require root in Android
- Easy to use
- Using listener for reading message & device connection status

## Functions

- **Get All Available Devices:**
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

List<DeviceInfo> availableDevices = await _flutterSerialCommunicationPlugin.getAvailableDevices();
```


- **Connect:**
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

DeviceInfo device = DeviceInfo(); // DeviceInfo from getAvailableDevices()
int baudRate = 0; // Your device's baud rate
bool isConnectionSuccess = await _flutterSerialCommunicationPlugin.connect(device, baudRate);
```

- **Disconnect:**
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

await _flutterSerialCommunicationPlugin.disconnect();
```

- **Write:**

```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

bool isMessageSent = await _flutterSerialCommunicationPlugin.write(Uint8List.fromList([0xBB, 0x00, 0x22, 0x00, 0x00, 0x22, 0x7E]));
debugPrint("Is Message Sent:  $isMessageSent");
```
- **Read (Listener):**
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

EventChannel eventChannel = _flutterSerialCommunicationPlugin.getSerialMessageListener();
eventChannel.receiveBroadcastStream().listen((event) {
  debugPrint("Received From Native:  $event");
});
```

- **Device Connection Status (Listener):**
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

EventChannel eventChannel = _flutterSerialCommunicationPlugin.getDeviceConnectionListener();
eventChannel.receiveBroadcastStream().listen((event) {
  debugPrint("Received From Native:  $event");
});
```


## Implementation
* Android: Bridging from native Android library [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)