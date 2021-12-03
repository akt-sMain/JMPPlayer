package jmsynth.oscillator;

public class SawWaveOscillator extends AbstractWaveGenOscillator {

    public SawWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        return WaveGenerater.makeSawWave(f, overallLevel, oscConfig.isWaveReverse());
    }

}
