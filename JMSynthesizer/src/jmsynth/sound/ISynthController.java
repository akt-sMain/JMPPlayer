package jmsynth.sound;

import jmsynth.enverope.Envelope;
import jmsynth.oscillator.IOscillator.WaveType;
import jmsynth.oscillator.OscillatorSet;

public interface ISynthController {
    abstract void openDevice();
    abstract void closeDevice();
    abstract boolean checkChannel(int ch);

    abstract void noteOn(int ch, int note, int velocity);

    abstract void noteOff(int ch, int note);

    abstract void pitchBend(int ch, int pitch);

    abstract void setExpression(int ch, int exp);

    abstract void pitchBendSenc(int ch, int sc);

    abstract void setNRPN(int ch, int nRPN);

    abstract int getNRPN(int ch);

    abstract void resetAllController(int ch);

    abstract void allNoteOff(int ch);

    abstract void setVibratoRate(int ch, int rate);

    abstract void setVibratoDepth(int ch, int depth);

    abstract void setVibratoDelay(int ch, int delay);

    abstract void setVariation(int ch, int val);

    abstract void setPan(int ch, int pan);

    abstract void setVolume(int ch, float volume);

    abstract void setOscillator(int ch, WaveType oscType);

    default void setOscillator(int ch, OscillatorSet oscSet) {
        Envelope e = getEnvelope(ch);
        if (e != null) {
            e.setAttackTime(oscSet.getAttackTime());
            e.setDecayTime(oscSet.getDecayTime());
            e.setSustainLevel(oscSet.getSustainLevel());
            e.setReleaseTime(oscSet.getReleaseTime());
            e.setMaxAttackMills(oscSet.getMaxAttackMills());
            e.setMaxDecayMills(oscSet.getMaxDecayMills());
            e.setMaxReleaseMills(oscSet.getMaxReleaseMills());
        }
        setOscillator(ch, oscSet.getOscillator());
    }

    abstract WaveType getWaveType(int ch);

    abstract Envelope getEnvelope(int ch);

    abstract void systemReset();
}
