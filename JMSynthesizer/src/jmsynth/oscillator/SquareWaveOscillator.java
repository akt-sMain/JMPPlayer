package jmsynth.oscillator;

public class SquareWaveOscillator extends AbstractWaveGenOscillator {

    public SquareWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makeSquareWave(f, overallLevel, oscConfig.isWaveReverse(), true);
    }

}
