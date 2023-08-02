package jmsynth.envelope;

import jmsynth.sound.Tone;

public class Envelope {

    public static final int MAX_MILLS = 5000;
    public static final int MIN_MILLS = 500;

    private long maxAttackMills = 1000;
    private long maxDecayMills = 1000;
    private long maxReleaseMills = 1000;

    private Tone[] targetTones = null;
    private double attackTime = 0.0;
    private double decayTime = 0.0;
    private double sustainLevel = 1.0;
    private double releaseTime = 0.0;

    Envelope() {
    }

    public long getAttackMills() {
        return (long) ((double) maxAttackMills * attackTime);
    }

    public long getDecayMills() {
        return (long) ((double) maxDecayMills * decayTime);
    }

    public long getReleaseMills() {
        return (long) ((double) maxReleaseMills * releaseTime);
    }

    public void startEnv() {
        /* スレッド開始時のコールバック */
    }

    public void endEnv() {
        /* スレッド終了時のコールバック */
    }

    public void process() {
        if (targetTones != null) {
            long current = System.currentTimeMillis();

            long a = getAttackMills();
            long d = getDecayMills();
            long r = getReleaseMills();
            for (int i = 0; i < targetTones.length; i++) {
                try {
                    Tone tone = (Tone) targetTones[i];
                    if (tone == null) {
                        continue;
                    }

                    double offset = tone.getEnvelopeOffset();
                    long startTime = tone.getStartMills();
                    long elapsedTime = current - startTime;
                    if (tone.isReleaseFlag() == true) {
                        if (elapsedTime < r) {
                            offset = sustainLevel * (1.0 - ((double) elapsedTime / (double) r));
                        }
                        else {
                            offset = 0.0;
                        }
                        // System.out.println("r : " + offset);
                    }
                    else {
                        if (elapsedTime < a) {
                            offset = (double) ((elapsedTime * 1.0) / a);
                            // System.out.println("a : " + offset);
                        }
                        else if ((elapsedTime - a) < d) {
                            offset = 1.0 - (double) (((elapsedTime - a) * (1.0 - sustainLevel)) / d);
                            // System.out.println("d : " + offset);
                        }
                        else {
                            offset = sustainLevel;
                            // System.out.println("s : " + offset);
                        }
                    }

                    if (offset < 0.0) {
                        // System.out.println("" + elapsedTime);
                        offset = 0.0;
                    }
                    tone.setEnvelopeOffset(offset);
                }
                catch (ArrayIndexOutOfBoundsException aiobe) {
                    // ArrayIndexOutOfBoundsExceptionは起きがち
                    // スレッドのタイミング次第なためとりあえず無視...
                    // aiobe.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTargetTones(Tone[] targetTones) {
        this.targetTones = targetTones;
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
        if (MAX_MILLS < maxAttackMills) {
            maxAttackMills = MAX_MILLS;
        }
        this.maxAttackMills = maxAttackMills;
    }

    public long getMaxDecayMills() {
        return maxDecayMills;
    }

    public void setMaxDecayMills(long maxDecayMills) {
        if (MAX_MILLS < maxAttackMills) {
            maxDecayMills = MAX_MILLS;
        }
        this.maxDecayMills = maxDecayMills;
    }

    public long getMaxReleaseMills() {
        return maxReleaseMills;
    }

    public void setMaxReleaseMills(long maxReleaseMills) {
        if (MAX_MILLS < maxAttackMills) {
            maxReleaseMills = MAX_MILLS;
        }
        this.maxReleaseMills = maxReleaseMills;
    }
}
