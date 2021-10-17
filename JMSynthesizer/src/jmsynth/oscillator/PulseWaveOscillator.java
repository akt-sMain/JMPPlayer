package jmsynth.oscillator;

public class PulseWaveOscillator extends AbstractWaveGenOscillator {
    private double duty = 0.5;
    public PulseWaveOscillator(double duty) {
        this.duty = duty;
    }

    @Override
    byte makeWave(double f, int overallLevel) {
        return WaveGenerater.makePulseWave(f, overallLevel, this.duty, isWaveReverse(), true);
    }
}
