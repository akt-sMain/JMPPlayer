package jmsynth;

import jmsynth.app.component.IWaveRepaintListener;
import jmsynth.enverope.Envelope;
import jmsynth.enverope.EnvelopeFactory;
import jmsynth.oscillator.IOscillator.WaveType;
import jmsynth.sound.ISynthController;
import jmsynth.sound.SoundSourceChannel;

public class JMSoftSynthesizer implements ISynthController {

    protected SoundSourceChannel[] channels = null;
    private EnvelopeFactory enveropeFactory = null;

    /**
     *
     * @param polyphony
     *            同時発音可能数
     */
    public JMSoftSynthesizer(int polyphony) {
        makeChannel(polyphony);
    }

    /**
     *
     */
    public JMSoftSynthesizer() {
        makeChannel(64);
    }

    private void makeChannel(int polyphony) {

        enveropeFactory = new EnvelopeFactory();

        channels = new SoundSourceChannel[] { // Midiチャンネル分
                new SoundSourceChannel(0, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 1ch
                new SoundSourceChannel(1, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 2ch
                new SoundSourceChannel(2, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 3ch
                new SoundSourceChannel(3, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 4ch
                new SoundSourceChannel(4, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 5ch
                new SoundSourceChannel(5, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 6ch
                new SoundSourceChannel(6, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 7ch
                new SoundSourceChannel(7, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 8ch
                new SoundSourceChannel(8, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 9ch
                new SoundSourceChannel(9, WaveType.NOISE, polyphony, enveropeFactory.newEnveropeInstance(0.0, 0.25, 0.0, 0.0)), // 10ch
                new SoundSourceChannel(10, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 11ch
                new SoundSourceChannel(11, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 12ch
                new SoundSourceChannel(12, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 13ch
                new SoundSourceChannel(13, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 14ch
                new SoundSourceChannel(14, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 15ch
                new SoundSourceChannel(15, WaveType.SINE, polyphony, enveropeFactory.newEnveropeInstance()), // 16ch
        };
    }

    public void openDevice() {
        for (int i = 0; i < channels.length; i++) {
            channels[i].openDevice();
        }
        enveropeFactory.timerStart();
    }

    @Override
    public void closeDevice() {
        for (int i = 0; i < channels.length; i++) {
            channels[i].closeDevice();
        }

        if (enveropeFactory != null) {
            enveropeFactory.dispose();
        }
    }

    public SoundSourceChannel getChannel(int ch) {
        return channels[ch];
    }

    public void setWaveRepaintListener(int ch, IWaveRepaintListener waveRepaintListener) {
        channels[ch].setWaveRepaintListener(waveRepaintListener);
    }

    @Override
    public boolean checkChannel(int ch) {
        return true;
    }

    @Override
    public void noteOn(int ch, int note, int velocity) {
        channels[ch].noteOn(ch, note, velocity);
    }

    @Override
    public void noteOff(int ch, int note) {
        channels[ch].noteOff(ch, note);
    }

    @Override
    public void pitchBend(int ch, int pitch) {
        channels[ch].pitchBend(ch, pitch);
    }

    @Override
    public void setExpression(int ch, int exp) {
        channels[ch].setExpression(ch, exp);
    }

    @Override
    public void pitchBendSenc(int ch, int sc) {
        channels[ch].pitchBendSenc(ch, sc);
    }

    @Override
    public void setNRPN(int ch, int nRPN) {
        channels[ch].setNRPN(ch, nRPN);
    }

    @Override
    public int getNRPN(int ch) {
        return channels[ch].getNRPN(ch);
    }

    @Override
    public void resetAllController(int ch) {
        channels[ch].resetAllController(ch);
    }

    @Override
    public void allNoteOff(int ch) {
        channels[ch].allNoteOff(ch);
    }

    @Override
    public void setVibratoRate(int ch, int rate) {
        channels[ch].setVibratoRate(ch, rate);
    }

    @Override
    public void setVibratoDepth(int ch, int depth) {
        channels[ch].setVibratoDepth(ch, depth);
    }

    @Override
    public void setVibratoDelay(int ch, int delay) {
        channels[ch].setVibratoDelay(ch, delay);
    }

    @Override
    public void setVariation(int ch, int val) {
        channels[ch].setVariation(ch, val);
    }

    @Override
    public void setPan(int ch, int pan) {
        channels[ch].setPan(ch, pan);
    }

    @Override
    public void setVolume(int ch, float volume) {
        channels[ch].setVolume(ch, volume);
    }

    @Override
    public void setOscillator(int ch, WaveType oscType) {
        channels[ch].setOscillator(ch, oscType);
    }

    @Override
    public Envelope getEnvelope(int ch) {
        return channels[ch].getEnvelope();
    }

    @Override
    public WaveType getWaveType(int ch) {
        return channels[ch].getWaveType(ch);
    }

    @Override
    public void systemReset() {
        for (int i=0; i<16; i++) {
            channels[i].systemReset();
        }
    }
}
