package jlib.midi;

import javax.sound.midi.ShortMessage;

public interface IMidiFilter {
    abstract void transpose(ShortMessage sMes, int transpose);
}