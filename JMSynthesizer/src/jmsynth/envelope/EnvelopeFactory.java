package jmsynth.envelope;

import java.util.Vector;

public class EnvelopeFactory implements Runnable {

    public static final double DEFAULT_A = 0.0;
    public static final double DEFAULT_D = 0.0;
    public static final double DEFAULT_S = 1.0;
    public static final double DEFAULT_R = 0.1;
    public static final long DEFAULT_MAX_A = 1000;
    public static final long DEFAULT_MAX_D = 1000;
    public static final long DEFAULT_MAX_R = 1000;

    private boolean isRunnable = true;
    private Thread timerThread = null;
    private Vector<Envelope> targets = null;

    public EnvelopeFactory() {
        targets = new Vector<Envelope>();
        timerThread = new Thread(this);
        timerThread.setPriority(Thread.MAX_PRIORITY);
    }

    public Envelope newEnvelopeInstance() {
        return newEnvelopeInstance(DEFAULT_A, DEFAULT_D, DEFAULT_S, DEFAULT_R, DEFAULT_MAX_A, DEFAULT_MAX_D, DEFAULT_MAX_R);
    }

    public Envelope newEnvelopeInstance(double a, double d, double s, double r) {
        return newEnvelopeInstance(a, d, s, r, DEFAULT_MAX_A, DEFAULT_MAX_D, DEFAULT_MAX_R);
    }

    public Envelope newEnvelopeInstance(double a, double d, double s, double r, long ma, long md, long mr) {
        Envelope e = new Envelope();
        e.setAttackTime(a);
        e.setDecayTime(d);
        e.setSustainLevel(s);
        e.setReleaseTime(r);
        e.setMaxAttackMills(ma);
        e.setMaxDecayMills(md);
        e.setMaxReleaseMills(mr);
        targets.add(e);
        return e;
    }

    public void timerStart() {
        timerThread.start();
    }

    @Override
    public void run() {
        while (isRunnable) {
            try {
                for (int i = 0; i < targets.size(); i++) {
                    Envelope e = targets.get(i);
                    if (e == null) {
                        continue;
                    }
                    e.process();
                }
                Thread.sleep(1);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void dispose() {
        isRunnable = false;
        try {
            timerThread.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
