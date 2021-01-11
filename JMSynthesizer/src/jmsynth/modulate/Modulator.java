package jmsynth.modulate;

import java.util.Vector;

import jmsynth.sound.Tone;

public class Modulator {

    private Vector<Tone> targetTones = null;
    private int depth = 0;
    private double depthOffset = 0.0;

    private long pastTime = 0;

    public static final int MAX_MODULATION_VALUE = 127;

    public static final long MODULATION_RATE_TIME = 160;
    public static final int MODULATION_MAX_VALUE = 8191;//ピッチベンドのインターフェースに合わせる

    Modulator() {}

    public void process() {
        if (targetTones != null) {
            long current = System.currentTimeMillis();
            long t = current - pastTime;
            if (t > MODULATION_RATE_TIME) {
                t %= MODULATION_RATE_TIME;
                pastTime = current;
            }
            double f = (double)t / (double)MODULATION_RATE_TIME;
            int base = MODULATION_MAX_VALUE / 2;
            int value = (int)((double)makeSinWave(f, base, false));
            float fVal = (float)value * (float)depthOffset;
            for (int i = 0; i < targetTones.size(); i++) {
                try {
                    Tone tone = (Tone) targetTones.get(i);
                    tone.setModulationValue(fVal / (float)MODULATION_MAX_VALUE);
                }
                catch (Exception e) {
                }
            }
        }
    }

    public static int makeSinWave(double f, int overallLeval, boolean reverse) {
        double ff = (reverse == false) ? (1.0 - f) : f;
        return (int)((Math.sin(2.0 * Math.PI * ff) * overallLeval));
    }

    public void setTargetTones(Vector<Tone> targetTones) {
        this.targetTones = targetTones;
    }

    public int getDepth() {
        return this.depth;
    }
    public void setDepth(int depth) {
        if (this.depth == 0 && depth > 0) {
            pastTime = System.currentTimeMillis();
        }
        this.depth = depth;
        this.depthOffset = (double)depth / (double)MAX_MODULATION_VALUE;
    }

}
