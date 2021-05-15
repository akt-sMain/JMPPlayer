package jmp.midi;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import jlib.midi.IMidiUnit;
import jmp.core.SoundManager;

public class MidiUnit implements IMidiUnit {

    public MidiUnit() {
    }

    private Sequencer getSequencer() {
        return SoundManager.SMidiPlayer.getSequencer();
    }

    @Override
    public boolean isRunning() {
        return getSequencer().isRunning();
    }

    @Override
    public double getTempoInBPM() {
        return getSequencer().getTempoInBPM();
    }

    @Override
    public long getTickPosition() {
        return getSequencer().getTickPosition();
    }

    @Override
    public long getTickLength() {
        return getSequencer().getTickLength();
    }

    @Override
    public Sequence getSequence() {
        if (getSequencer() == null) {
            return null;
        }
        return getSequencer().getSequence();
    }

    @Override
    public long getMicrosecondPosition() {
        return getSequencer().getMicrosecondPosition();
    }

    @Override
    public long getMicrosecondLength() {
        return getSequencer().getMicrosecondLength();
    }

}
