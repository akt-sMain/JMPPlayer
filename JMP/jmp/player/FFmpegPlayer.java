package jmp.player;

import java.io.File;

import function.Utility;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.SoundManager;
import jmp.core.SystemManager;

public class FFmpegPlayer extends Player {

    private Player wavPlayer = SoundManager.SWavPlayer;

    @Override
    public void play() {
        wavPlayer.play();
    }

    @Override
    public void stop() {
        wavPlayer.stop();
    }

    @Override
    public boolean isRunnable() {
        return wavPlayer.isRunnable();
    }

    @Override
    public void setPosition(long pos) {
        wavPlayer.setPosition(pos);
    }

    @Override
    public long getPosition() {
        return wavPlayer.getPosition();
    }

    @Override
    public long getLength() {
        return wavPlayer.getLength();
    }

    @Override
    public boolean isValid() {
        return wavPlayer.isValid();
    }

    @Override
    public int getPositionSecond() {
        return wavPlayer.getPositionSecond();
    }

    @Override
    public int getLengthSecond() {
        return wavPlayer.getLengthSecond();
    }

    @Override
    public void setVolume(float volume) {
        wavPlayer.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return wavPlayer.getVolume();
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        SystemManager system = JMPCore.getSystemManager();
        boolean tmpWaitFor = system.isFFmpegWrapperWaitFor();
        system.setFFmpegWrapperWaitFor(true);

        String inpath = file.getPath();

        String inname = Utility.getFileNameNotExtension(inpath);
        File outdir = new File(JMPCore.getDataManager().getFFmpegOutputPath());

        String outpath = Utility.pathCombin(outdir.getPath(), (inname + ".wav"));

        File in = new File(inpath);
        File out = new File(outpath);

        system.setFFmpegWrapperCallback(null);

        try {
            system.executeConvert(in.getPath(), out.getPath());
        }
        catch (Exception e1) {
            return false;
        }

        system.setFFmpegWrapperWaitFor(tmpWaitFor);

        if (out.canRead() == false) {
            // 読み込めない場合は変換失敗とする
            return false;
        }
        return wavPlayer.loadFile(out);
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return wavPlayer.saveFile(file);
    }

    @Override
    public boolean isAllSupported() {
        SystemManager system = JMPCore.getSystemManager();
        DataManager dm = JMPCore.getDataManager();
        if (system.isValidFFmpegWrapper() == false) {
            return false;
        }
        if (dm.isUseFFmpegPlayer() == false) {
            return false;
        }

        return super.isAllSupported();
    }

    @Override
    public boolean isSupportedExtension(String extension) {
        SystemManager system = JMPCore.getSystemManager();
        DataManager dm = JMPCore.getDataManager();
        if (system.isValidFFmpegWrapper() == false) {
            return false;
        }
        if (dm.isUseFFmpegPlayer() == false) {
            return false;
        }

        return super.isSupportedExtension(extension);
    }

}
