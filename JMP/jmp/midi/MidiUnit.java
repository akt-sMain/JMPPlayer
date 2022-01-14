package jmp.midi;

import java.io.File;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import jlib.midi.IMidiUnit;
import jmp.player.MidiPlayer;

public class MidiUnit implements IMidiUnit {
    
    private MidiPlayer midiPlayer;

    public MidiUnit(MidiPlayer midiPlayer) {
        this.midiPlayer = midiPlayer;
    }

    private Sequencer getSequencer() {
        return this.midiPlayer.getSequencer();
    }
    
    public void exportMidiFile(File file) throws Exception {
        midiPlayer.saveFile(file);
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
