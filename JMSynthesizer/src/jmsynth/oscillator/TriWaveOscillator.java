package jmsynth.oscillator;

public class TriWaveOscillator extends AbstractWaveGenOscillator {

    public TriWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel) {
        return WaveGenerater.makeTriangleWave(f, overallLevel, false);
    }

}
