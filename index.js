import { NativeEventEmitter, NativeModules } from 'react-native';

const { Midi } = NativeModules;
const eventEmitter = new NativeEventEmitter(Midi);

export default {
  on: (event = '', callback = () => {}) => {
    Midi.on(event, err => {
      if (err)
          throw new Exception(err);
  
      eventEmitter.addListener(event, callback);
    });
  },
  off: (event = '', callback = () => {}) => {
    Midi.off(event, err => {
      if (err)
          throw new Exception(err);
  
      eventEmitter.removeListener(event, callback);
    });
  },
  getDevices: Midi.getDevices,
  getDeviceCount: Midi.getDeviceCount,
  getDevice: Midi.getDevice
}