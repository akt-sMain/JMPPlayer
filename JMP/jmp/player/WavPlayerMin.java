package jmp.player;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import jlib.player.Player;

public class WavPlayerMin extends Player {

    public class PlayThread extends Thread {

        static final int STOP = 0;
        static final int PLAY = 1;
        static final int EXIT = 2;

        public int state = STOP;
        private AudioInputStream ais;
        private AudioFormat af;
        private DataLine.Info dataLine;
        private SourceDataLine sourceDataLine;

        public PlayThread(AudioInputStream ais) throws LineUnavailableException, IOException {
            super();

            this.ais = ais;
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

                boolean runnableFlag = true;
                int size = -1;

                while (true) {
                    switch (state) {
                        case STOP:
                            continue;
                        case PLAY:
                            break;
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
            catch (IOException e) {
            }
        }
    }

    public WavPlayerMin() {
        // TODO 自動生成されたコンストラクター・スタブ
    }

    @Override
    public void play() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void stop() {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public boolean isRunnable() {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    @Override
    public void setPosition(long pos) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public long getPosition() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public long getLength() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public boolean isValid() {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    @Override
    public int getPositionSecond() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public int getLengthSecond() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public void setVolume(float volume) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public float getVolume() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);
        AudioFormat af = ais.getFormat();
        DataLine.Info dataLine = new DataLine.Info(SourceDataLine.class, af);
        SourceDataLine s = (SourceDataLine)AudioSystem.getLine(dataLine);



        return false;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

}
