package jmsynth.oscillator;

public class LowSamplingSinWaveOscillator extends AbstractWaveGenOscillator {

    public LowSamplingSinWaveOscillator() {
    }

    @Override
    public byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makeSinWaveForLowSampling(f, overallLevel, oscConfig.isWaveReverse());
    }

}
