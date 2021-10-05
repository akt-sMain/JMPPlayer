package jmsynth.oscillator;

public class SinWaveOscillator extends AbstractWaveGenOscillator {

    public SinWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel) {
        return WaveGenerater.makeSinWave(f, overallLevel, isWaveReverse());
    }

}
