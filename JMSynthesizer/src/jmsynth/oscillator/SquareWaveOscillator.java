package jmsynth.oscillator;

public class SquareWaveOscillator extends AbstractWaveGenOscillator {

    public SquareWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel) {
        return WaveGenerater.makeSquareWave(f, overallLevel, false);
    }

}
