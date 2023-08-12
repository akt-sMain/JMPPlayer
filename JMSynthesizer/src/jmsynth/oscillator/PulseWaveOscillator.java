package jmsynth.oscillator;

public class PulseWaveOscillator extends AbstractWaveGenOscillator {
    private double duty = 0.5;

    public PulseWaveOscillator(double duty) {
        this.duty = duty;
    }

    @Override
    byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makePulseWave(f, overallLevel, this.duty, oscConfig.isWaveReverse(), oscConfig.isValidNesSimulate());
    }
}
