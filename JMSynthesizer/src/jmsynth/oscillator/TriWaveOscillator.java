package jmsynth.oscillator;

public class TriWaveOscillator extends AbstractWaveGenOscillator {

    public TriWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makeTriangleWave(f, overallLevel, oscConfig.isWaveReverse(), true);
    }

}
