package jmsynth.envelope;

import java.util.Vector;

public class EnvelopeFactory implements Runnable {

    private boolean isRunnable = true;
    private Thread timerThread = null;
    private Vector<Envelope> targets = null;

    public EnvelopeFactory() {
        targets = new Vector<Envelope>();
        timerThread = new Thread(this);
        timerThread.setPriority(Thread.MAX_PRIORITY);
    }

    public Envelope newEnvelopeInstance() {
        return newEnvelopeInstance(0.0, 0.0, 1.0, 0.1);
    }
    public Envelope newEnvelopeInstance(double a, double d, double s, double r) {
        Envelope e = new Envelope();
        e.setAttackTime(a);
        e.setDecayTime(d);
        e.setSustainLevel(s);
        e.setReleaseTime(r);
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
