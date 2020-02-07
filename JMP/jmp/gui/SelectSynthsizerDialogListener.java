package jmp.gui;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public interface SelectSynthsizerDialogListener {

    abstract void commitMidiOut(Receiver rec) throws MidiUnavailableException;

    abstract void commitMidiIn(Transmitter trans) throws MidiUnavailableException;
}
