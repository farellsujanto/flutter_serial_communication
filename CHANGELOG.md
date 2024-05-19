## 0.2.5
- Implementations setParameters & purgeHwBuffers
- Fix for `usbSerialPort.getControlLines();` causing crash when connecting to a device

Thanks to [nmiglio](https://github.com/nmiglio) for the contributions!

## 0.2.4
- Implementations for setting usb DTR and RTS
- Connect device after permission is granted
- Fix for APP crash and force close when serial status connected and cable detached from device [#6](https://github.com/farellsujanto/flutter_serial_communication/issues/6)

Thanks to [DominikStarke](https://github.com/DominikStarke) for the fix!

## 0.2.3
- Change gradle implementation to api, so updating build gradle wont be needed when installing the library

## 0.2.2
- Add `vendorId` to `DeviceInfo`

## 0.2.1
- Remove equal operator from `DeviceInfo`

## 0.2.0 BREAKING
- Change `getAvailableDevices` to detailed device info version
- Change `connect` to require `DeviceInfo` from `getAvailableDevices`

## 0.1.2
- Add `getAvailableDevices`

## 0.1.1
- Fix typo in document

## 0.1.0
- Update to minor version v0.1.0

## 0.0.3
- Remove uneeded libraries to reduce package size

## 0.0.2
- Update descriptions
- Follow flutter lint formats

## 0.0.1

- **Android** Add basic functionalities for serial communication: `getAvailableDevices`, `connect`, `disconnect`, `write`, `getSerialMessageListener`, `getDeviceConnectionListener`.
