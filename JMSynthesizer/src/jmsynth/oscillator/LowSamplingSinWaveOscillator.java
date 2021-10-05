package jmsynth.oscillator;

public class LowSamplingSinWaveOscillator extends AbstractWaveGenOscillator {

    public LowSamplingSinWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel) {
        return WaveGenerater.makeSinWaveForLowSampling(f, overallLevel, isWaveReverse());
    }

}
