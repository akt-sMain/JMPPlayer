package jmsynth.oscillator;

public class SinWaveOscillator extends AbstractWaveGenOscillator {

    public SinWaveOscillator() {
    }

    @Override
    byte makeWave(double f, int overallLevel, OscillatorConfig oscConfig) {
        if (oscConfig.isValidNesSimulate() == true) {
            return WaveGenerater.makeSinWave(f, overallLevel, oscConfig.isWaveReverse(), 95.0);
        }
        else {
            return WaveGenerater.makeSinWave(f, overallLevel, oscConfig.isWaveReverse());
        }
    }

}
