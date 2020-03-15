import { NativeEventEmitter, NativeModules } from 'react-native';

const { Midi } = NativeModules;

const eventEmitter = new NativeEventEmitter(Midi);

Midi.on = (event, callback) => {
  eventEmitter.addListener(event, callback)
}

export default Midi;
