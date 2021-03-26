# react-native-midi

[![NPM](https://nodei.co/npm/react-native-midi.png?compact=true)](https://nodei.co/npm/react-native-midi/)

React-native module for accessing USB midi controllers (on Android). Plans to include Bluetooth and iOS support eventually.

## Getting started

`$ npm install react-native-midi --save`

## Installation

Add USB feature in `android/src/main/AndroidManifest`:
```xml
<manifest>

    <uses-feature android:name="android.hardware.usb.host" /> <!-- HERE!-->
    <uses-feature android:name="android.software.midi" android:required="true"/> <!-- HERE!-->

    <application>
      ...
    </application>
</manifest>
```

## Usage

The library is mostly event driven, with a few helper functions for getting the current state of the midi controllers.

##### Example
```javascript
import Midi from 'react-native-midi';

// Register for some common events:
Midi.on(Midi.INPUT_DEVICE_ATTACHED, device => {
  console.log(`Device "${device.id}" connected`);
});

Midi.on(Midi.INPUT_DEVICE_DETACHED, device => {
  console.log(`Device "${device.id}" disconnected`);
});

Midi.on(Midi.NOTE_ON, event => {
  console.log(`Device "${event.device}" started note: ${event.note}`);
});

Midi.on(Midi.NOTE_OFF, event => {
  console.log(`Device "${event.device}" stopped note: ${event.note}`);
});

// Stop listening to note events
Midi.off(Midi.NOTE_ON);
Midi.off(Midi.NOTE_OFF);

// Getters
const devices = await Midi.getDevices(); // Currently attached devices
const deviceCount = await Midi.getDeviceCount(); // Number of devices currently attached
const device = await Midi.getDevice(123); // Get device with id '123'
```

### Functions

| Function | Arguments       | Returns | Description |
|----------|-----------------|---------|-----------------------------|
| on       | Event, callback |         | Registers listener for Event. Invokes callback until unregistered |
| off      | Event           |         | Unregisters listener |
| getDevices | | Promise\<Device\> | Gets connected devices |
| getDeviceCount | | Promise\<Number\> | Gets number of connected devices |
| getDevice | deviceId | Promise\<Device\> | Gets connected device by id |

### Events

#### Device Attach/Detach Events
- `DEVICE_ATTACHED`
- `DEVICE_DETACHED`
- `INPUT_DEVICE_ATTACHED`
- `INPUT_DEVICE_DETACHED`
- `OUTPUT_DEVICE_ATTACHED`
- `OUTPUT_DEVICE_DETACHED`

All of these attach/detach events invoke the callback with the following `Device` object:

```javascript
{
  id, // Id matches the 'device' property of midi events
  protocol,
  subclass,
  name,
  productId,
  vendorId,
  interfaces,
  configurations, // >= Android "Lollipop"
  serialNumber, // >= Android "Lollipop"
  version, // >= Android "M"
  deviceAddress, // I/O device only
  manufacturerName, // I/O device only
  productName // I/O device only
}

```
##### Example
```javascript
Midi.on(Midi.INPUT_DEVICE_ATTACHED, device => {
  console.log(`Device "${device.name}" by ${device.vendorId} has attached`);
});
```

#### Midi Events

All midi event listeners invoke the callback with an object containing a `device` (int) and `cable` (int) property, *in addition* to those properties listed below:

- `MISC_FUNCTION_CODES`
  - `codes` (byte[])
- `CABLE_EVENTS`
  - `events` (byte[])
- `SYSTEM_COMMON_MESSAGE`
  - `message` (string)
- `SYSTEM_EXCLUSIVE`
  - `message` (string)
- `NOTE_ON`
  - `channel` (int)
  - `note` (int)
  - `velocity` (int)
- `NOTE_OFF`
  - `channel` (int)
  - `note` (int)
  - `velocity` (int)
- `POLYPHONIC_AFTERTOUCH`
  - `channel` (int)
  - `note` (int)
  - `pressure` (int)
- `CONTROL_CHANGE`
  - `channel` (int)
  - `function` (int)
  - `value` (int)
- `PROGRAM_CHANGE`
  - `channel` (int)
  - `program` (int)
- `CHANNEL_AFTERTOUCH`
  - `channel` (int)
  - `pressure` (int)
- `PITCH_WHEEL`
  - `channel` (int)
  - `amount` (int)
- `SINGLE_BYTE`
  - `byte` (byte)
- `TIME_CODE_QUARTER_FRAME`
  - `timing` (int)
- `SONG_SELECT`
  - `song` (int)
- `SONG_POSITION_POINTER`
  - `position` (int)
- `TUNE_REQUEST`
- `TIMING_CLOCK`
- `START`
- `CONTINUE`
- `STOP`
- `ACTIVE_SENSING`
- `RESET`

##### Example
```javascript
Midi.on(Midi.NOTE_ON, event => {
  console.log(`Device "${event.device}" started note: ${event.note}`);
});
```

## Contributing

Open to pull requests! I'm looking for someone to take on the iOS support in particular as I'm lacking devices to develop on.

## TODO

- iOS support
- Bluetooth support
- ~~Document event bodies~~
