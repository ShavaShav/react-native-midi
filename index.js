import { NativeEventEmitter, NativeModules } from 'react-native';

const { Midi } = NativeModules;
const eventEmitter = new NativeEventEmitter(Midi);

export default {
  ...Midi,
  // override on/off event listener for easy hookup to NativeEventEmitter
  on: (event = '', callback = () => {}) => {
    Midi.on(event, err => {
      if (err)
          throw new Error(err);
  
      eventEmitter.addListener(event, callback);
    });
  },
  off: (event = '', callback = () => {}) => {
    Midi.off(event, err => {
      if (err)
          throw new Error(err);
  
      eventEmitter.removeListener(event, callback);
    });
  }
}