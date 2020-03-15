import { NativeModules } from 'react-native';

const { Midi } = NativeModules;

Midi.jsWrappedFunction = (myArg) => { return "JS STRING!! arg: " + myArg  };

export default Midi;
