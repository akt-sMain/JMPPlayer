package jmsynth.oscillator;

public class SawWaveOscillator extends AbstractWaveGenOscillator {
    private boolean reverseMode = false;

    public SawWaveOscillator() {
        this.reverseMode = false;
    }

    public SawWaveOscillator(boolean reverseMode) {
        this.reverseMode = reverseMode;
    }

    @Override
    byte makeWave(double f, int overallLevel) {
        return WaveGenerater.makeSawWave(f, overallLevel, reverseMode);
    }

}
