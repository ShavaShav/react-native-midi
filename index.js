import { NativeEventEmitter, NativeModules } from 'react-native';

const { Midi } = NativeModules;
const { on, off } = Midi;

const eventEmitter = new NativeEventEmitter(Midi);

Midi.on = (event, callback) => {
  on(event, err => {
    if (err)
        throw new Exception(err);

    eventEmitter.addListener(event, callback);
  });
}

Midi.off = (event, callback) => {
  off(event, err => {
    if (err)
        throw new Exception(err);

    eventEmitter.removeListener(event, callback);
  });
}

export default Midi;