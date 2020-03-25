package com.shaverz.midi;

public interface MidiEventEmitter {
    public void emit(MidiEvent event, Object options);
    public void addListener(MidiEvent event);
    public void removeListener(MidiEvent event);
    public boolean isListening(MidiEvent event);
}
