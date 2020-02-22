package jmp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.FloatControl;
import javax.swing.DefaultListModel;
import javax.swing.JList;

import function.Utility;
import jlib.IJmpMainWindow;
import jlib.IMidiEventListener;
import jlib.manager.ISoundManager;
import jlib.manager.JMPCoreAccessor;
import jmp.player.MidiPlayer;
import jmp.player.Player;
import jmp.player.PlayerAccessor;
import jmp.player.WavPlayer;

/**
 * サウンド管理クラス
 *
 * @author abs
 *
 */
public class SoundManager implements ISoundManager {
    private DefaultListModel<String> playListModel = new DefaultListModel<>();
    private JList<String> playList = new JList<String>(playListModel);

    // MIDIプレイヤーインスタンス
    public static MidiPlayer MidiPlayer = null;
    public static WavPlayer WavPlayer = null;

    // 初期化フラグ
    private boolean initializeFlag = false;

    FloatControl volumeCtrl;

    SoundManager() {
        // アクセッサに登録
        JMPCoreAccessor.register(this);
    }

    public boolean initFunc() {
        if (JMPCore.getDataManager().isShowStartupDeviceSetup() == false) {
            // 自動接続フラグを立てる
            JMPFlags.StartupAutoConectSynth = true;
        }

        // プレイヤーインスタンス作成
        MidiPlayer = new MidiPlayer();
        WavPlayer = new WavPlayer();

        MidiPlayer.setSupportExtentions(DataManager.ExtentionForMIDI);
        PlayerAccessor.getInstance().register(MidiPlayer);

        WavPlayer.setSupportExtentions(DataManager.ExtentionForWAV);
        PlayerAccessor.getInstance().register(WavPlayer);

        // デフォルトはMIDIプレイヤーにする
        PlayerAccessor.getInstance().change(MidiPlayer);

        // Port lineIn;

        // try {
        // Mixer mixer = AudioSystem.getMixer(null);
        // lineIn = (Port) mixer.getLine(Port.Info.LINE_OUT);
        // if (lineIn.isOpen() == false) {
        // lineIn.open();
        // }
        // volumeCtrl = (FloatControl)
        // lineIn.getControl(FloatControl.Type.VOLUME);
        // }
        // catch (Exception e) {
        // e.printStackTrace();
        // System.out.println("Not open LineIn.");
        // volumeCtrl = null;
        // }

        /* プレイヤーロード */
        try {
            if (PlayerAccessor.getInstance().open() == false) {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if (initializeFlag == false) {
            initializeFlag = true;
        }
        return true;
    }

    public boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }

        stop();
        PlayerAccessor.getInstance().close();
        return true;
    }

    /**
     * MIDIシーケンサーを取得
     *
     * @return MIDIシーケンサー
     */
    public Sequencer getSequencer() {
        return MidiPlayer.getSequencer();
    }

    public void initPlay() {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        Player player = accessor.getCurrent();
        if (player != null && player.isValid() == true) {
            stop();
            initPosition();
            play();
        }
    }

    public boolean isValidPlayList() {
        if ((playListModel.isEmpty() == true) || (playListModel.size() <= 0)) {
            return false;
        }
        return true;
        // return playList.
    }

    public boolean isValidPlayListIndex(int index) {
        if (0 > index || index >= playListModel.size()) {
            // インデックスが有効か判定
            return false;
        }
        return true;
    }

    public boolean isValidPlayListNext() {
        int index = getPlayList().getSelectedIndex();
        return isValidPlayListIndex(index + 1);
    }

    public boolean isValidPlayListPrev() {
        int index = getPlayList().getSelectedIndex();
        return isValidPlayListIndex(index - 1);
    }

    public void playForList(int index) {
        playForList(index, false);
    }

    public void playForList(int index, boolean isLoadOnly) {
        IJmpMainWindow mainWindow = JMPCore.getSystemManager().getMainWindow();
        if (isValidPlayListIndex(index) == false) {
            mainWindow.setStatusText("※再生するデータがありません", false);
            return;
        }

        String path = playListModel.getElementAt(index);
        try {
            PlayerAccessor accessor = PlayerAccessor.getInstance();
            accessor.getCurrent().stop();
            if (isLoadOnly == false) {
                // 自動再生フラグ
                JMPFlags.LoadToPlayFlag = true;
            }
            mainWindow.loadFile(path);
        }
        catch (Exception e) {
        }

        getPlayList().setSelectedIndex(index);

    }

    public void playCurrent() {
        playCurrent(false);
    }

    public void playCurrent(boolean isLoadOnly) {
        int index = getPlayList().getSelectedIndex();
        playForList(index, isLoadOnly);
    }

    public void playNext() {
        playNext(false);
    }

    public void playNext(boolean isLoadOnly) {
        int index = getPlayList().getSelectedIndex();
        playForList(index + 1, isLoadOnly);
    }

    public void playPrev() {
        playPrev(false);
    }

    public void playPrev(boolean isLoadOnly) {
        int index = getPlayList().getSelectedIndex();
        playForList(index - 1, isLoadOnly);
    }

    public void loadFile(File file) throws Exception {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        Player tmpPlayer = accessor.getCurrent();

        try {
            changePlayer(file);

            accessor.getCurrent().loadFile(file);
        }
        catch (Exception e) {
            // ロードに失敗した場合は、プレイヤーを元に戻す
            accessor.change(tmpPlayer);
            throw e;
        }
    }

    /**
     * 指定ディレクトリにあるMidiファイルを列挙する
     *
     * @param dir
     *            ディレクトリ
     * @return Midiファイルリスト(key:ファイル名, value:Fileオブジェクト)
     */
    public Map<String, File> getMidiFileList(File dir) {
        HashMap<String, File> result = new HashMap<String, File>();
        if (dir.isDirectory() == false) {
            return result;
        }

        for (File file : dir.listFiles()) {
            if (file.exists() == false) {
                // 除外ケース
                continue;
            }
            result.put(file.getName(), file);
        }
        return result;
    }

    public JList<String> getPlayList() {
        return playList;
    }

    @Override
    public boolean isPlay() {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        if (accessor.isValid() == false) {
            return false;
        }
        return accessor.getCurrent().isRunnable();
    }

    @Override
    public void play() {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        if (accessor.getCurrent().isValid() == false) {
            return;
        }

        if (accessor.getCurrent().getPosition() >= accessor.getCurrent().getLength()) {
            // 最初から再生
            initPosition();
        }
        accessor.getCurrent().play();
    }

    @Override
    public void stop() {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        if (accessor.getCurrent().isValid() == false) {
            return;
        }
        accessor.getCurrent().stop();
    }

    @Override
    public void setPosition(long pos) {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        if (accessor.getCurrent().isValid() == false) {
            return;
        }
        accessor.getCurrent().setPosition(pos);
    }

    @Override
    public long getPosition() {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        if (accessor.getCurrent().isValid() == false) {
            return 0;
        }
        return accessor.getCurrent().getPosition();
    }

    @Override
    public long getLength() {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        if (accessor.getCurrent().isValid() == false) {
            return 0;
        }
        return accessor.getCurrent().getLength();
    }

    @Override
    public int getPositionSecond() {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        if (accessor.getCurrent().isValid() == false) {
            return 0;
        }
        return accessor.getCurrent().getPositionSecond();
    }

    @Override
    public int getLengthSecond() {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        if (accessor.getCurrent().isValid() == false) {
            return 0;
        }
        return accessor.getCurrent().getLengthSecond();
    }

    @Override
    public boolean isSupportedExtension(String extension) {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        if (accessor.getCurrent().isValid() == false) {
            return false;
        }
        return accessor.getCurrent().isSupportedExtension(extension);
    }

    @Override
    public boolean sendMidiMessage(MidiMessage msg, long timeStamp) {
        if (MidiPlayer.getCurrentReciver() == null) {
            return false;
        }

        MidiPlayer.getCurrentReciver().send(msg, timeStamp);

        JMPCore.getPluginManager().catchMidiEvent(msg, timeStamp, IMidiEventListener.SENDER_MIDI_IN);
        return true;
    }

    public void changePlayer(File file) {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        String ex = Utility.getExtension(file);
        if (accessor.isSupportedExtension(ex) == false) {
            return;
        }

        accessor.getCurrent().stop();
        if (accessor.change(ex) == true) {
            JMPCore.getPluginManager().closeNonSupportPlugins(ex);
        }
    }

    public void setLineVolume(float value) {
        if (volumeCtrl == null) {
            return;
        }
        volumeCtrl.setValue(value);
    }

    public float getLineVolume() {
        if (volumeCtrl == null) {
            return 0.0f;
        }
        return volumeCtrl.getValue();
    }

    public float getMaxLineVolume() {
        if (volumeCtrl == null) {
            return 0.0f;
        }
        return volumeCtrl.getMaximum();
    }

    public float getMinLineVolume() {
        if (volumeCtrl == null) {
            return 0.0f;
        }
        return volumeCtrl.getMinimum();
    }
}
