## About Flutter Serial Communication

This library aims to ease Flutter Developers to do serial communcation with their peripherals without too much hassle.

Currently supports **Android USB (OTG)** to communicate with Arduinos and other USB serial hardware on Android **WITHOUT** using **root access**.

## Installing

### Android
**1.** Add jitpack.io repository to your build.gradle (root)
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
**2.** Add library to Android dependencies (root build gradle / app build gradle)
```gradle
dependencies {
    ...
    implementation 'com.github.mik3y:usb-serial-for-android:3.5.1'
}
```
**3.** Install it to your flutter project
```
flutter pub add shelf_static
```
or by adding dependencies to your package's pubspec.yaml then run `dart pub get`
```
dependencies:
  flutter_serial_communication: 0.1.0
```

**4.** Import it in your .dart file
```dart
import 'package:flutter_serial_communication/flutter_serial_communication.dart';
```


## Features
- Doesn't require root in Android
- Easy to use
- Using listener for reading message & device connection status

## Functions

- **Get All Available Devices:**
  - Requires: none
  - Returns: Future<List\<String>>
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

List<String> newConnectedDevices = await _flutterSerialCommunicationPlugin.getAvailableDevices() ?? [];
```

- **Connect:**
  - Requires: index(int), baudRate(int)
  - Returns: Future\<bool>
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

int index = 0; // Index of List<String> from getAvailableDevices()
int baudRate = 0; // Your device's baud rate
bool isConnectionSuccess = await _flutterSerialCommunicationPlugin.connect(index, baudRate);
```

- **Disconnect:**
  - Requires: none
  - Returns: Future\<void>
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

await _flutterSerialCommunicationPlugin.disconnect();
```

- **Write:**
  - Requires: data(Uint8List)
  - Returns: Future\<bool>
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

bool isMessageSent = await _flutterSerialCommunicationPlugin.write(Uint8List.fromList([0xBB, 0x00, 0x22, 0x00, 0x00, 0x22, 0x7E]));
debugPrint("Is Message Sent:  $isMessageSent");
```
- **Read (Listener):**
  - Requires: none
  - Returns: EventChannel
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

EventChannel eventChannel = _flutterSerialCommunicationPlugin.getSerialMessageListener();
eventChannel.receiveBroadcastStream().listen((event) {
  debugPrint("Received From Native:  $event");
});
```

- **Device Connection Status (Listener):**
  - Requires: none
  - Returns: EventChannel
```dart
final _flutterSerialCommunicationPlugin = FlutterSerialCommunication();

EventChannel eventChannel = _flutterSerialCommunicationPlugin.getDeviceConnectionListener();
eventChannel.receiveBroadcastStream().listen((event) {
  debugPrint("Received From Native:  $event");
});
```


## Implementation
* Android: Bridging from native Android library [usb-serial-for-android](https://github.com/mik3y/usb-serial-for-android)