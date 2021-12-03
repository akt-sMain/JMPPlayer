package jmsynth.oscillator;

public class OscillatorConfig {

    private boolean isWaveReverse = false;

    public OscillatorConfig() {
        setWaveReverse(false);
    }

    /**
     * 逆波形
     *
     * @return
     */
    public boolean isWaveReverse() {
        return isWaveReverse;
    }

    /**
     * 逆波形
     *
     * @param isReverse
     */
    public void setWaveReverse(boolean isWaveReverse) {
        this.isWaveReverse = isWaveReverse;
    }
}
