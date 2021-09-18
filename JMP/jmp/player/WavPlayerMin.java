package jmp.player;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import jlib.player.Player;
import jmp.util.JmpUtil;

public class WavPlayerMin extends Player {

    private PlayThread thread = null;
    private long frameOffset = 0;

    public class PlayThread extends Thread {
        static final int STOP = 0;
        static final int PLAY = 1;
        static final int RESET = 2;
        static final int EXIT = 3;

        public int state = STOP;

        public File file;
        public AudioInputStream ais;
        public AudioFormat af;
        public DataLine.Info dataLine;
        public SourceDataLine sourceDataLine;

        public PlayThread(File file) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
            super();

            this.file = file;
            this.ais = AudioSystem.getAudioInputStream(file);
            af = ais.getFormat();
            dataLine = new DataLine.Info(SourceDataLine.class, af);
            sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLine);

            sourceDataLine.open();
            sourceDataLine.start();
        }

        @Override
        public void run() {
            byte[] data = new byte[sourceDataLine.getBufferSize()];

            //ais.skip((int)af.getSampleRate() * af.getSampleSizeInBits() * af.getChannels() / 8 * 3);
            try {
                ais.skip(0);
                frameOffset = 0;

                boolean runnableFlag = true;
                int size = -1;

                while (true) {
                    switch (state) {
                        case STOP:
                            JmpUtil.threadSleep(10);
                            continue;
                        case PLAY:
                            break;
                        case RESET:
                            sourceDataLine.drain();
                            sourceDataLine.stop();
                            sourceDataLine.close();
                            sourceDataLine = null;

                            this.ais = AudioSystem.getAudioInputStream(file);
                            dataLine = new DataLine.Info(SourceDataLine.class, af);
                            sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLine);
                            thread.ais.skip(skipValue);

                            sourceDataLine.open();
                            sourceDataLine.start();

                            data = new byte[sourceDataLine.getBufferSize()];
                            state = backupState;
                            continue;
                        case EXIT:
                            runnableFlag = false;
                            break;
                        default:
                            break;
                    }
                    if (runnableFlag == false) {
                        break;
                    }

                    size = ais.read(data);
                    if (size == -1) {
                        state = STOP;
                        continue;
                    }
                    sourceDataLine.write(data, 0, size);
                }

                sourceDataLine.drain();
                sourceDataLine.stop();
                sourceDataLine.close();
            }
            catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            }
        }

        public void forcedStop() {
            state = STOP;
            sourceDataLine.flush();
        }
    }

    public WavPlayerMin() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    public void play() {
        thread.state = PlayThread.PLAY;
        thread.sourceDataLine.start();
    }

    @Override
    public void stop() {
        thread.state = PlayThread.STOP;
        thread.sourceDataLine.stop();
    }

    @Override
    public boolean isRunnable() {
        if (thread.state == PlayThread.PLAY) {
            return true;
        }
        return false;
    }

    private static int backupState = 0;
    private static long skipValue = 0;

    @Override
    public void setPosition(long pos) {
        if (isValid() == false) {
            return;
        }
        backupState = thread.state;
        thread.state = PlayThread.RESET;

        int rate = ((int)thread.af.getSampleRate() * thread.af.getSampleSizeInBits() * thread.af.getChannels() / 8);
        int sec = (int)(pos / thread.af.getFrameRate());
        skipValue = rate * sec;
        frameOffset = pos;
    }

    @Override
    public long getPosition() {
        if (isValid() == false) {
            return 0;
        }
        return thread.sourceDataLine.getFramePosition() + frameOffset;
    }

    @Override
    public long getLength() {
        if (isValid() == false) {
            return 0;
        }
        return thread.ais.getFrameLength();
    }

    @Override
    public boolean isValid() {
        if (thread == null) {
            return false;
        }
        if (thread.ais == null || thread.sourceDataLine == null || thread.af == null) {
            return false;
        }
        return true;
    }

    @Override
    public int getPositionSecond() {
        if (isValid() == false) {
            return 0;
        }
        return (int) (getPosition() / thread.af.getFrameRate());
    }

    @Override
    public int getLengthSecond() {
        if (isValid() == false) {
            return 0;
        }
        return (int) (getLength() / thread.af.getFrameRate());
    }

    @Override
    public void setVolume(float volume) {
    }

    @Override
    public float getVolume() {
        return 0;
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        if (thread != null) {
            thread.state = PlayThread.EXIT;
            thread.join();
        }

        thread = new PlayThread(file);
        thread.state = PlayThread.STOP;
        thread.start();
        return true;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return false;
    }

}
