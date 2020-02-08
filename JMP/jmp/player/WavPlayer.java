package jmp.player;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import jlib.Player;

public class WavPlayer extends Player {

    private Clip clip = null;

    public WavPlayer() {
    }

    public void setClip(Clip c) {
        if (clip != null) {
            // クリップの破棄
            try {
                clip.stop();
                clip.close();
                clip = null;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        clip = c;
    }

    @Override
    public void play() {
        if (isValid() == false) {
            return;
        }
        clip.start();
    }

    @Override
    public void stop() {
        if (isValid() == false) {
            return;
        }
        clip.stop();
    }

    @Override
    public boolean isRunnable() {
        if (isValid() == false) {
            return false;
        }
        return clip.isRunning();
    }

    @Override
    public void setPosition(long pos) {
        if (isValid() == false) {
            return;
        }
        clip.setFramePosition((int) pos);
    }

    @Override
    public long getPosition() {
        if (isValid() == false) {
            return 0;
        }
        return clip.getFramePosition();
    }

    @Override
    public long getLength() {
        if (isValid() == false) {
            return 0;
        }
        return clip.getFrameLength();
    }

    @Override
    public boolean isValid() {
        if (clip == null) {
            return false;
        }
        return true;
    }

    @Override
    public int getPositionSecond() {
        if (isValid() == false) {
            return 0;
        }
        long pos = clip.getFramePosition();
        float flame = clip.getFormat().getSampleRate();
        int time = (int) (pos / flame);
        return time;
    }

    @Override
    public int getLengthSecond() {
        if (isValid() == false) {
            return 0;
        }
        long length = clip.getFrameLength();
        float flame = clip.getFormat().getSampleRate();
        int time = (int) (length / flame);
        return time;
    }

    @Override
    public void setVolume(float volume) {
        if (isValid() == false) {
            return;
        }
    }

    @Override
    public float getVolume() {
        if (isValid() == false) {
            return 0;
        }
        return 0;
        // FloatControl control =
        // (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
        // float range = control.getMaximum() - control.getMinimum();
        // float volume = control.getValue() - control.getMinimum();
        // int pos = (int) ((volume * volumeSlider.getMaximum()) / range);
        // return pos;
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        loadWavFile(file);
        return true;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return false;
    }

    /**
     * Clip作成
     *
     * @param path
     *            ファイルパス
     * @return クリップ
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     */
    protected Clip createClip(String path) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        File file = new File(path);
        return createClip(file);
    }

    /**
     * Clip作成
     *
     * @param file
     *            ファイル
     * @return クリップ
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected Clip createClip(File file) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        Clip clip = null;

        // オーディオ入力ストリーム取得
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);
        // オーディオフォーマット取得
        AudioFormat audioFormat = ais.getFormat();
        DataLine.Info dataLine = new DataLine.Info(Clip.class, audioFormat);
        clip = (Clip) AudioSystem.getLine(dataLine);
        clip.open(ais);
        return clip;
    }

    protected void loadWavFile(File file) throws Exception {
        try {
            Clip clip = createClip(file);
            setClip(clip);
        }
        catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
            throw e;
        }
        finally {
        }
    }

}
