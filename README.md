# react-native-midi

React-native module for accessing USB and bluetooth midi controllers (on Android)

## Getting started

`$ npm install ShavaShav/react-native-midi --save`

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
```javascript
import Midi from 'react-native-midi';

// Example of registering for common events:
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


// Stop listening to NOTE_ON event
Midi.off(Midi.NOTE_ON);

// Getters
const devices = await Midi.getDevices(); // Currently attached devices
const deviceCount = await Midi.getDeviceCount(); // Number of devices currently attached
const device = await Midi.getDevice(123); // Get device with id '123'
```

### Device:
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

### Events

All events contain a `device` and `cable` property for event context.

#### Constants
##### Device Attach/Detach Events
- DEVICE_ATTACHED
- DEVICE_DETACHED
- INPUT_DEVICE_ATTACHED
- INPUT_DEVICE_DETACHED
- OUTPUT_DEVICE_ATTACHED
- OUTPUT_DEVICE_DETACHED

##### Midi Events
- MISC_FUNCTION_CODES
- CABLE_EVENTS
- SYSTEM_COMMON_MESSAGE
- SYSTEM_EXCLUSIVE
- NOTE_ON
- NOTE_OFF
- POLYPHONIC_AFTERTOUCH
- CONTROL_CHANGE
- PROGRAM_CHANGE
- CHANNEL_AFTERTOUCH
- PITCH_WHEEL
- SINGLE_BYTE
- TIME_CODE_QUARTER_FRAME
- SONG_SELECT
- SONG_POSITION_POINTER
- TUNE_REQUEST
- TIMING_CLOCK
- START
- CONTINUE
- STOP
- ACTIVE_SENSING
- RESET

## TODO

- iOS support
- Bluetooth support
- Document event bodies
