package jmsynth.oscillator;

public class SinWaveOscillator extends AbstractWaveGenOscillator {

    public SinWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makeSinWave(f, overallLevel, oscConfig.isWaveReverse());
    }

}
