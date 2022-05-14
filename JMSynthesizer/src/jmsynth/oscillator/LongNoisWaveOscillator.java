package jmsynth.oscillator;

import java.util.Random;

public class LongNoisWaveOscillator extends NoisWaveOscillator {

    public LongNoisWaveOscillator() {
        super();
    }
    
    @Override
    protected double generateNoiseCoefficient() {
        // 完全ランダム
        Random rn = new Random();
        int iRan = rn.nextInt(NOISE_VAR);
        return moiseTable[iRan];
    }

}
