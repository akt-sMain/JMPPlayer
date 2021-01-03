package jmsynth.midi;

import jmsynth.oscillator.OscillatorSet;
import jmsynth.oscillator.OscillatorSet.WaveType;

public class ProgramChangeTable {

    protected OscillatorSet[] sets = null;
    public ProgramChangeTable() {
        sets = new OscillatorSet[128];
        for (int i=0; i<sets.length; i++) {
            sets[i] = new OscillatorSet(WaveType.SINE);
        }
    }

    public void setOscillatorSet(int pc, OscillatorSet set) {
        if (0 <= pc && pc < sets.length) {
            sets[pc] = set;
        }
    }

    public OscillatorSet getOscillatorSet(int pc) {
        if (pc < 0) {
            return sets[0];
        }
        else if (pc >= sets.length) {
            return sets[sets.length - 1];
        }
        return sets[pc];
    }
}