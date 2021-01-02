package jmsynth.oscillator;

public class PulseWaveOscillator extends AbstractWaveGenOscillator {
    public PulseWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel) {
        return WaveGenerater.makePulseWave(f, overallLevel, false);
    }
}
