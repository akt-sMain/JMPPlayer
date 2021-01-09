package jmsynth.modulate;

import java.util.Vector;

import jmsynth.sound.Tone;

public class Modulator {

    private Vector<Tone> targetTones = null;
    private int depth = 0;
    private double depthOffset = 0;

    private long pastTime = 0;

    public static final long MODULATION_CYCLE_TIME = 160;
    public static final int MODULATION_MAX_VALUE = 16382;//ピッチベンドのインターフェースに合わせる
    public static final double MODULATION_MAX_OFFSET = 0.5;//ピッチベンド最大値の半分まで

    Modulator() {}

    public void process() {
        if (targetTones != null) {
            long current = System.currentTimeMillis();
            long t = current - pastTime;
            if (t > MODULATION_CYCLE_TIME) {
                t -= MODULATION_CYCLE_TIME;
                pastTime = current;
            }
            double f = (double)t / (double)MODULATION_CYCLE_TIME;
            int value = (int)(makeSinWave(f, MODULATION_MAX_VALUE, false) * MODULATION_MAX_OFFSET);
            for (int i = 0; i < targetTones.size(); i++) {
                try {
                    Tone tone = (Tone) targetTones.get(i);
                    tone.setModulationValue((int)(value * depthOffset));
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

    public void setDepth(int depth) {
        if (this.depth == 0 && depth > 0) {
            pastTime = System.currentTimeMillis();
        }
        this.depth = depth;
        this.depthOffset = (double)depth / (double)127;
    }

}
