package jmsynth.oscillator;

import java.util.ArrayList;
import java.util.List;

public class OscillatorSet {

    public static final OscillatorSet DRUM_OSCILLATOR_SET = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);

    public static enum WaveType {
        SINE,
        SAW,
        SAW_REVERSE,
        SQUARE,
        TRIANGLE,
        PULSE,
        NOISE
    }

    // 複数のオシレータ設計を考慮（出力側は未実装）
    private List<WaveType> oscs = null;

    private double attackTime = 0.0;
    private double decayTime = 0.0;
    private double sustainLevel = 1.0;
    private double releaseTime = 0.0;

    private long maxAttackMills = 1000;
    private long maxDecayMills = 1000;
    private long maxReleaseMills = 1000;

    public OscillatorSet(WaveType... osc) {
        setOscillators(osc);
        attackTime = 0.0;
        decayTime = 0.0;
        sustainLevel = 1.0;
        releaseTime = 0.0;
        maxAttackMills = 1000;
        maxDecayMills = 1000;
        maxReleaseMills = 1000;
    }

    public OscillatorSet(double a, double d, double s, double r, WaveType... osc) {
        setOscillators(osc);
        attackTime = a;
        decayTime = d;
        sustainLevel = s;
        releaseTime = r;
        maxAttackMills = 1000;
        maxDecayMills = 1000;
        maxReleaseMills = 1000;
    }

    public OscillatorSet(double a, double d, double s, double r, long ma, long md, long mr, WaveType... osc) {
        setOscillators(osc);
        attackTime = a;
        decayTime = d;
        sustainLevel = s;
        releaseTime = r;
        maxAttackMills = ma;
        maxDecayMills = md;
        maxReleaseMills = mr;
    }

    public void setOscillators(WaveType... osc) {
        oscs = new ArrayList<WaveType>();
        for (int i = 0; i < osc.length; i++) {
            oscs.add(osc[i]);
        }
    }
    public WaveType getOscillator() {
        return getOscillator(0);
    }

    public WaveType getOscillator(int index) {
        if (size() <= 0) {
            // 例外
            return WaveType.NOISE;
        }
        return oscs.get(index);
    }

    public int size() {
        return oscs.size();
    }

    public double getAttackTime() {
        return attackTime;
    }

    public void setAttackTime(double attackTime) {
        this.attackTime = attackTime;
    }

    public double getDecayTime() {
        return decayTime;
    }

    public void setDecayTime(double decayTime) {
        this.decayTime = decayTime;
    }

    public double getSustainLevel() {
        return sustainLevel;
    }

    public void setSustainLevel(double sustainLevel) {
        this.sustainLevel = sustainLevel;
    }

    public double getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(double releaseTime) {
        this.releaseTime = releaseTime;
    }

    public long getMaxAttackMills() {
        return maxAttackMills;
    }

    public void setMaxAttackMills(long maxAttackMills) {
        this.maxAttackMills = maxAttackMills;
    }

    public long getMaxDecayMills() {
        return maxDecayMills;
    }

    public void setMaxDecayMills(long maxDecayMills) {
        this.maxDecayMills = maxDecayMills;
    }

    public long getMaxReleaseMills() {
        return maxReleaseMills;
    }

    public void setMaxReleaseMills(long maxReleaseMills) {
        this.maxReleaseMills = maxReleaseMills;
    }
}
