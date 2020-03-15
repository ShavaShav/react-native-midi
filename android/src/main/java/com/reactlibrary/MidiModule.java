package com.reactlibrary;

import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MidiModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    // Device Attach/Detach Events
    protected static final String DEVICE_ATTACHED = "DEVICE_ATTACHED";
    protected static final String DEVICE_DETACHED = "DEVICE_DETACHED";
    protected static final String INPUT_DEVICE_ATTACHED = "INPUT_DEVICE_ATTACHED";
    protected static final String INPUT_DEVICE_DETACHED = "INPUT_DEVICE_DETACHED";
    protected static final String OUTPUT_DEVICE_ATTACHED = "OUTPUT_DEVICE_ATTACHED";
    protected static final String OUTPUT_DEVICE_DETACHED = "OUTPUT_DEVICE_DETACHED";

    // Midi Events
    protected static final String MISC_FUNCTION_CODES = "MISC_FUNCTION_CODES";
    protected static final String CABLE_EVENTS = "CABLE_EVENTS";
    protected static final String SYSTEM_COMMON_MESSAGE = "SYSTEM_COMMON_MESSAGE";
    protected static final String SYSTEM_EXCLUSIVE = "SYSTEM_EXCLUSIVE";
    protected static final String NOTE_ON = "NOTE_ON";
    protected static final String NOTE_OFF = "NOTE_OFF";
    protected static final String POLYPHONIC_AFTERTOUCH = "POLYPHONIC_AFTERTOUCH";
    protected static final String CONTROL_CHANGE = "CONTROL_CHANGE";
    protected static final String PROGRAM_CHANGE = "PROGRAM_CHANGE";
    protected static final String CHANNEL_AFTERTOUCH = "CHANNEL_AFTERTOUCH";
    protected static final String PITCH_WHEEL = "PITCH_WHEEL";
    protected static final String SINGLE_BYTE = "SINGLE_BYTE";
    protected static final String TIME_CODE_QUARTER_FRAME = "TIME_CODE_QUARTER_FRAME";
    protected static final String SONG_SELECT = "SONG_SELECT";
    protected static final String SONG_POSITION_POINTER = "SONG_POSITION_POINTER";
    protected static final String TUNE_REQUEST = "TUNE_REQUEST";
    protected static final String TIMING_CLOCK = "TIMING_CLOCK";
    protected static final String START = "START";
    protected static final String CONTINUE = "CONTINUE";
    protected static final String STOP = "STOP";
    protected static final String ACTIVE_SENSING = "ACTIVE_SENSING";
    protected static final String RESET = "RESET";

    private final ReactApplicationContext reactContext;
    private final MidiDriverUSB usbDriver;

    public MidiModule(final ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.reactContext.addLifecycleEventListener(this);

        // Driver needs to be opened on main thread (for UI dialog)
        usbDriver = new MidiDriverUSB(reactContext);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                usbDriver.open();
            }
        });
    }

    @Override
    public void onHostResume() { }

    @Override
    public void onHostPause() { }

    @Override
    public void onHostDestroy() {
        usbDriver.close();
    }

    @Override
    public String getName() {
        return "Midi";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DEVICE_ATTACHED, DEVICE_ATTACHED);
        constants.put(DEVICE_DETACHED, DEVICE_DETACHED);
        constants.put(INPUT_DEVICE_ATTACHED, INPUT_DEVICE_ATTACHED);
        constants.put(INPUT_DEVICE_DETACHED, INPUT_DEVICE_DETACHED);
        constants.put(OUTPUT_DEVICE_ATTACHED, OUTPUT_DEVICE_ATTACHED);
        constants.put(OUTPUT_DEVICE_DETACHED, OUTPUT_DEVICE_DETACHED);
        constants.put(MISC_FUNCTION_CODES, MISC_FUNCTION_CODES);
        constants.put(CABLE_EVENTS, CABLE_EVENTS);
        constants.put(SYSTEM_COMMON_MESSAGE, SYSTEM_COMMON_MESSAGE);
        constants.put(SYSTEM_EXCLUSIVE, SYSTEM_EXCLUSIVE);
        constants.put(NOTE_ON, NOTE_ON);
        constants.put(NOTE_OFF, NOTE_OFF);
        constants.put(POLYPHONIC_AFTERTOUCH, POLYPHONIC_AFTERTOUCH);
        constants.put(CONTROL_CHANGE, CONTROL_CHANGE);
        constants.put(PROGRAM_CHANGE, PROGRAM_CHANGE);
        constants.put(CHANNEL_AFTERTOUCH, CHANNEL_AFTERTOUCH);
        constants.put(PITCH_WHEEL, PITCH_WHEEL);
        constants.put(SINGLE_BYTE, SINGLE_BYTE);
        constants.put(TIME_CODE_QUARTER_FRAME, TIME_CODE_QUARTER_FRAME);
        constants.put(SONG_SELECT, SONG_SELECT);
        constants.put(SONG_POSITION_POINTER, SONG_POSITION_POINTER);
        constants.put(TUNE_REQUEST, TUNE_REQUEST);
        constants.put(TIMING_CLOCK, TIMING_CLOCK);
        constants.put(START, START);
        constants.put(CONTINUE, CONTINUE);
        constants.put(STOP, STOP);
        constants.put(ACTIVE_SENSING, ACTIVE_SENSING);
        constants.put(RESET, RESET);
        return constants;
    }

    @ReactMethod
    public void getDevices(Callback callback) {
        callback.invoke(new ArrayList(usbDriver.getDevices().values()));
    }

    @ReactMethod
    public void getDeviceCount(Callback callback) {
        callback.invoke(usbDriver.getDevices().size());
    }

    @ReactMethod
    public void getDevice(int id, Callback callback) {
        callback.invoke(usbDriver.getDevices().get(id));
    }
}
