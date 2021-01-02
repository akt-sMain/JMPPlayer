package jmsynth.envelope;

import java.util.Vector;

import jmsynth.sound.Tone;

public class Envelope {

    private long maxAttackMills = 1000;
    private long maxDecayMills = 1000;
    private long maxReleaseMills = 1000;

    private Vector<Tone> targetTones = null;
    private double attackTime = 0.0;
    private double decayTime = 0.0;
    private double sustainLevel = 1.0;
    private double releaseTime = 0.0;

    Envelope() {}

    public void process() {
        if (targetTones != null) {
            long current = System.currentTimeMillis();

            long a = (long) (maxAttackMills * attackTime);
            long d = (long) (maxDecayMills * decayTime);
            long r = (long) (maxReleaseMills * releaseTime);
            for (int i = 0; i < targetTones.size(); i++) {
                try {
                    Tone tone = (Tone) targetTones.get(i);

                    double offset = tone.getEnvelopeOffset();
                    long startTime = tone.getStartMills();
                    long elapsedTime = current - startTime;
                    if (tone.isReleaseFlag() == true) {
                        if (elapsedTime < r) {
                            offset = sustainLevel * (1.0 - ((double)elapsedTime / (double)r));
                        }
                        else {
                            offset = 0.0;
                        }
                        //System.out.println("r : " + offset);
                    }
                    else {
                        if (elapsedTime < a) {
                            offset = (double) ((elapsedTime * 1.0) / a);
                            //System.out.println("a : " + offset);
                        }
                        else if ((elapsedTime - a) < d) {
                            offset = 1.0 - (double) (((elapsedTime - a) * (1.0 - sustainLevel)) / d);
                            //System.out.println("d : " + offset);
                        }
                        else {
                            offset = sustainLevel;
                            //System.out.println("s : " + offset);
                        }
                    }

                    if (offset < 0.0) {
                        //System.out.println("" + elapsedTime);
                        offset = 0.0;
                    }
                    tone.setEnvelopeOffset(offset);
                }
                catch (ArrayIndexOutOfBoundsException aiobe) {
                    // ArrayIndexOutOfBoundsExceptionは起きがち
                    // スレッドのタイミング次第なためとりあえず無視...
                    //aiobe.printStackTrace();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setTargetTones(Vector<Tone> targetTones) {
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
