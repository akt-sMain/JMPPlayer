package jmsynth.modulate;

import java.util.Vector;

public class ModulatorFactory implements Runnable {

    public static final int DEFAULT_DEPTH = 0;

    private boolean isRunnable = true;
    private Thread timerThread = null;
    private Vector<Modulator> targets = null;

    public ModulatorFactory() {
        targets = new Vector<Modulator>();
        timerThread = new Thread(this);
        timerThread.setPriority(Thread.MAX_PRIORITY);
    }

    public Modulator newModulatorInstance() {
        Modulator m = new Modulator();
        m.setDepth(DEFAULT_DEPTH);
        targets.add(m);
        return m;
    }

    public void timerStart() {
        timerThread.start();
    }

    @Override
    public void run() {
        while (isRunnable) {
            try {
                for (int i = 0; i < targets.size(); i++) {
                    Modulator m = targets.get(i);
                    if (m == null) {
                        continue;
                    }
                    m.process();
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
