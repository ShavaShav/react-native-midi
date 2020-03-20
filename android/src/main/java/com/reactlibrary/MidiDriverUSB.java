package com.reactlibrary;

import android.hardware.usb.UsbDevice;
import android.os.Build;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jp.kshoji.driver.midi.device.MidiInputDevice;
import jp.kshoji.driver.midi.device.MidiOutputDevice;
import jp.kshoji.driver.midi.util.UsbMidiDriver;

public class MidiDriverUSB extends UsbMidiDriver {
    private final MidiEventEmitter mEmitter;
    private final Map<Integer, UsbDevice> mDevices;

    public MidiDriverUSB(ReactApplicationContext context, MidiEventEmitter emitter) {
        super(context);
        mEmitter = emitter;
        mDevices = new HashMap<Integer, UsbDevice>();
    }

    public Map<Integer, UsbDevice> getDevices() {
        return mDevices;
    };

    private WritableMap convertMidiInputDeviceToMap(MidiInputDevice midiInputDevice) {
        WritableMap params = Arguments.createMap();
        params.putString("deviceAddress", midiInputDevice.getDeviceAddress());
        params.putString("manufacturerName", midiInputDevice.getManufacturerName());
        params.putString("productName", midiInputDevice.getProductName());
        addUSBDeviceToParams(midiInputDevice.getUsbDevice(), params);
        return params;
    }

    private WritableMap convertMidiOutputDeviceToMap(MidiOutputDevice midiOutputDevice) {
        WritableMap params = Arguments.createMap();
        params.putString("deviceAddress", midiOutputDevice.getDeviceAddress());
        params.putString("manufacturerName", midiOutputDevice.getManufacturerName());
        params.putString("productName", midiOutputDevice.getProductName());
        addUSBDeviceToParams(midiOutputDevice.getUsbDevice(), params);
        return params;
    }

    private WritableMap addUSBDeviceToParams(UsbDevice usbDevice, WritableMap params) {
        params.putInt("id", usbDevice.getDeviceId());
        params.putInt("protocol", usbDevice.getDeviceProtocol());
        params.putInt("subclass", usbDevice.getDeviceSubclass());
        params.putString("name", usbDevice.getDeviceName());
        params.putInt("productId", usbDevice.getProductId());
        params.putInt("vendorId", usbDevice.getVendorId());
        params.putInt("interfaces", usbDevice.getInterfaceCount());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            params.putInt("configurations", usbDevice.getConfigurationCount());
            params.putString("serialNumber", usbDevice.getSerialNumber());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                params.putString("version", usbDevice.getVersion());
            }
        }
        return params;
    }

    private WritableMap getMidiEventMap(UsbDevice device, int cable) {
        final WritableMap params = Arguments.createMap();
        params.putInt("device", device.getDeviceId());
        params.putInt("cable", cable);
        return params;
    }

    private WritableMap getMidiEventMap(MidiInputDevice device, int cable) {
        return getMidiEventMap(device.getUsbDevice(), cable);
    }

    @Override
    public void onDeviceAttached(UsbDevice usbDevice) {
        synchronized (mDevices) {
            mDevices.put(usbDevice.getDeviceId(), usbDevice);
        }
        mEmitter.emit(MidiEvent.DEVICE_ATTACHED, addUSBDeviceToParams(usbDevice, Arguments.createMap()));
    }

    @Override
    public void onDeviceDetached(UsbDevice usbDevice) {
        synchronized (mDevices) {
            mDevices.remove(usbDevice.getDeviceId());
        }
        mEmitter.emit(MidiEvent.DEVICE_DETACHED, addUSBDeviceToParams(usbDevice, Arguments.createMap()));
    }

    @Override
    public void onMidiInputDeviceAttached(MidiInputDevice midiInputDevice) {
        mEmitter.emit(MidiEvent.INPUT_DEVICE_ATTACHED, convertMidiInputDeviceToMap(midiInputDevice));
    }

    @Override
    public void onMidiOutputDeviceAttached(MidiOutputDevice midiOutputDevice) {
        mEmitter.emit(MidiEvent.OUTPUT_DEVICE_ATTACHED, convertMidiOutputDeviceToMap(midiOutputDevice));
    }

    @Override
    public void onMidiInputDeviceDetached(MidiInputDevice midiInputDevice) {
        mEmitter.emit(MidiEvent.INPUT_DEVICE_DETACHED, convertMidiInputDeviceToMap(midiInputDevice));
    }

    @Override
    public void onMidiOutputDeviceDetached(MidiOutputDevice midiOutputDevice) {
        mEmitter.emit(MidiEvent.OUTPUT_DEVICE_DETACHED, convertMidiOutputDeviceToMap(midiOutputDevice));
    }

    @Override
    public void onMidiMiscellaneousFunctionCodes(MidiInputDevice midiInputDevice, int cable, int byte1, int byte2, int byte3) {
        // Misc function codes are received ALOT. Bail out extra early if we're not listening (to prevent unnecessary array building)
        if (!mEmitter.isListening(MidiEvent.MISC_FUNCTION_CODES))
            return;

        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putArray("codes", Arguments.fromList(Arrays.asList(byte1, byte2, byte3)));
        mEmitter.emit(MidiEvent.MISC_FUNCTION_CODES, params);
    }

    @Override
    public void onMidiCableEvents(MidiInputDevice midiInputDevice, int cable, int byte1, int byte2, int byte3) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putArray("events", Arguments.fromList(Arrays.asList(byte1, byte2, byte3)));
        mEmitter.emit(MidiEvent.CABLE_EVENTS, params);
    }

    @Override
    public void onMidiSystemCommonMessage(MidiInputDevice midiInputDevice, int cable, byte[] bytes) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putString("message", new String(bytes)); // pass bytes as string
        mEmitter.emit(MidiEvent.SYSTEM_COMMON_MESSAGE, params);
    }

    @Override
    public void onMidiSystemExclusive(MidiInputDevice midiInputDevice, int cable, byte[] systemExclusive) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putString("message", new String(systemExclusive)); // pass bytes as string
        mEmitter.emit(MidiEvent.SYSTEM_EXCLUSIVE, params);
    }

    @Override
    public void onMidiNoteOff(MidiInputDevice midiInputDevice, int cable, int channel, int note, int velocity) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("channel", channel);
        params.putInt("note", note);
        params.putInt("velocity", velocity);
        mEmitter.emit(MidiEvent.NOTE_OFF, params);
    }

    @Override
    public void onMidiNoteOn(MidiInputDevice midiInputDevice, int cable, int channel, int note, int velocity) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("channel", channel);
        params.putInt("note", note);
        params.putInt("velocity", velocity);
        mEmitter.emit(MidiEvent.NOTE_ON, params);
    }

    @Override
    public void onMidiPolyphonicAftertouch(MidiInputDevice midiInputDevice, int cable, int channel, int note, int pressure) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("channel", channel);
        params.putInt("note", note);
        params.putInt("pressure", pressure);
        mEmitter.emit(MidiEvent.POLYPHONIC_AFTERTOUCH, params);
    }

    @Override
    public void onMidiControlChange(MidiInputDevice midiInputDevice, int cable, int channel, int function, int value) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("channel", channel);
        params.putInt("function", function);
        params.putInt("value", value);
        mEmitter.emit(MidiEvent.CONTROL_CHANGE, params);
    }

    @Override
    public void onMidiProgramChange(MidiInputDevice midiInputDevice, int cable, int channel, int program) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("channel", channel);
        params.putInt("program", program);
        mEmitter.emit(MidiEvent.PROGRAM_CHANGE, params);
    }

    @Override
    public void onMidiChannelAftertouch(MidiInputDevice midiInputDevice, int cable, int channel, int pressure) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("channel", channel);
        params.putInt("pressure", pressure);
        mEmitter.emit(MidiEvent.CHANNEL_AFTERTOUCH, params);
    }

    @Override
    public void onMidiPitchWheel(MidiInputDevice midiInputDevice, int cable, int channel, int amount) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("channel", channel);
        params.putInt("amount", amount);
        mEmitter.emit(MidiEvent.PITCH_WHEEL, params);
    }

    @Override
    public void onMidiSingleByte(MidiInputDevice midiInputDevice, int cable, int byte1) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("byte", byte1);
        mEmitter.emit(MidiEvent.SINGLE_BYTE, params);
    }

    @Override
    public void onMidiTimeCodeQuarterFrame(MidiInputDevice midiInputDevice, int cable, int timing) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("timing", timing);
        mEmitter.emit(MidiEvent.TIME_CODE_QUARTER_FRAME, params);
    }

    @Override
    public void onMidiSongSelect(MidiInputDevice midiInputDevice, int cable, int song) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("song", song);
        mEmitter.emit(MidiEvent.SONG_SELECT, params);
    }

    @Override
    public void onMidiSongPositionPointer(MidiInputDevice midiInputDevice, int cable, int position) {
        final WritableMap params = getMidiEventMap(midiInputDevice, cable);
        params.putInt("position", position);
        mEmitter.emit(MidiEvent.SONG_POSITION_POINTER, params);
    }

    @Override
    public void onMidiTuneRequest(MidiInputDevice midiInputDevice, int cable) {
        mEmitter.emit(MidiEvent.TUNE_REQUEST, getMidiEventMap(midiInputDevice, cable));
    }

    @Override
    public void onMidiTimingClock(MidiInputDevice midiInputDevice, int cable) {
        mEmitter.emit(MidiEvent.TIMING_CLOCK, getMidiEventMap(midiInputDevice, cable));
    }

    @Override
    public void onMidiStart(MidiInputDevice midiInputDevice, int cable) {
        mEmitter.emit(MidiEvent.START, getMidiEventMap(midiInputDevice, cable));
    }

    @Override
    public void onMidiContinue(MidiInputDevice midiInputDevice, int cable) {
        mEmitter.emit(MidiEvent.CONTINUE, getMidiEventMap(midiInputDevice, cable));
    }

    @Override
    public void onMidiStop(MidiInputDevice midiInputDevice, int cable) {
        mEmitter.emit(MidiEvent.STOP, getMidiEventMap(midiInputDevice, cable));
    }

    @Override
    public void onMidiActiveSensing(MidiInputDevice midiInputDevice, int cable) {
        mEmitter.emit(MidiEvent.ACTIVE_SENSING, getMidiEventMap(midiInputDevice, cable));
    }

    @Override
    public void onMidiReset(MidiInputDevice midiInputDevice, int cable) {
        mEmitter.emit(MidiEvent.RESET, getMidiEventMap(midiInputDevice, cable));
    }
}
