package jmp.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.FloatControl;
import javax.swing.DefaultListModel;
import javax.swing.JList;

import function.Utility;
import jlib.core.ISoundManager;
import jlib.gui.IJmpMainWindow;
import jlib.gui.IJmpWindow;
import jlib.midi.IMidiEventListener;
import jlib.midi.MidiByteMessage;
import jlib.player.IPlayer;
import jmp.JMPFlags;
import jmp.player.MidiPlayer;
import jmp.player.MusicXmlPlayer;
import jmp.player.Player;
import jmp.player.PlayerAccessor;
import jmp.player.WavPlayer;

/**
 * サウンド管理クラス
 *
 * @author abs
 *
 */
public class SoundManager extends AbstractManager implements ISoundManager {

    public static final String PLAYER_TIME_FORMAT = "%02d:%02d";

    private DefaultListModel<String> playListModel = new DefaultListModel<>();
    private JList<String> playList = new JList<String>(playListModel);

    // MIDIプレイヤーインスタンス
    public static MidiPlayer MidiPlayer = null;
    public static WavPlayer WavPlayer = null;
    public static MusicXmlPlayer MusicXmlPlayer = null;

    FloatControl volumeCtrl;

    SoundManager(int pri) {
        super(pri, "sound");
    }

    @Override
    protected boolean initFunc() {
        if (JMPCore.getDataManager().isShowStartupDeviceSetup() == false) {
            // 自動接続フラグを立てる
            JMPFlags.StartupAutoConectSynth = true;
        }

        // プレイヤーインスタンス作成
        MidiPlayer = new MidiPlayer();
        WavPlayer = new WavPlayer();
        MusicXmlPlayer = new MusicXmlPlayer();

        MidiPlayer.setSupportExtentions(DataManager.ExtentionForMIDI);
        PlayerAccessor.getInstance().register(MidiPlayer);

        WavPlayer.setSupportExtentions(DataManager.ExtentionForWAV);
        PlayerAccessor.getInstance().register(WavPlayer);

        MusicXmlPlayer.setSupportExtentions(DataManager.ExtentionForMusicXML);
        PlayerAccessor.getInstance().register(MusicXmlPlayer);

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

        if (initializeFlag == false) {
            initializeFlag = true;
        }
        return true;
    }

    @Override
    protected boolean endFunc() {
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

    public IPlayer getCurrentPlayer() {
        PlayerAccessor accessor = PlayerAccessor.getInstance();
        return accessor.getCurrent();
    }

    public void initPlay() {
        IPlayer player = getCurrentPlayer();
        if (player != null && player.isValid() == true) {
            initPosition();
        }
    }

    public boolean isValidPlayList() {
        if ((playListModel.isEmpty() == true) || (playListModel.size() <= 0)) {
            return false;
        }
        return true;
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
            IPlayer player = getCurrentPlayer();
            player.stop();
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
        IPlayer player = getCurrentPlayer();
        if (player.isValid() == false) {
            return false;
        }
        return player.isRunnable();
    }

    @Override
    public void play() {
        IPlayer player = getCurrentPlayer();
        if (player.isValid() == false) {
            return;
        }

        if (player.getPosition() >= player.getLength()) {
            // 最初から再生
            initPosition();
        }
        player.play();
    }

    @Override
    public void stop() {
        IPlayer player = getCurrentPlayer();
        if (player.isValid() == false) {
            return;
        }

        /* TODO MIDIメッセージモニターのデッドロック問題の暫定対策(停止時に閉じる) */
        IJmpWindow midiMinitor = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_MONITOR);
        boolean backupVisible = midiMinitor.isWindowVisible();
        if (backupVisible == true) {
            midiMinitor.hideWindow();
        }

        player.stop();

        if (backupVisible == true) {
            midiMinitor.showWindow();
        }
    }

    @Override
    public void setPosition(long pos) {
        IPlayer player = getCurrentPlayer();
        if (player.isValid() == false) {
            return;
        }

        /* TODO MIDIメッセージモニターのデッドロック問題の暫定対策(停止時に閉じる) */
        IJmpWindow midiMinitor = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_MONITOR);
        boolean backupVisible = midiMinitor.isWindowVisible();
        if (backupVisible == true) {
            midiMinitor.hideWindow();
        }

        player.setPosition(pos);

        if (backupVisible == true) {
            midiMinitor.showWindow();
        }
    }

    @Override
    public long getPosition() {
        IPlayer player = getCurrentPlayer();
        if (player.isValid() == false) {
            return 0;
        }
        return player.getPosition();
    }

    @Override
    public long getLength() {
        IPlayer player = getCurrentPlayer();
        if (player.isValid() == false) {
            return 0;
        }
        return player.getLength();
    }

    @Override
    public int getPositionSecond() {
        IPlayer player = getCurrentPlayer();
        if (player.isValid() == false) {
            return 0;
        }
        return player.getPositionSecond();
    }

    @Override
    public int getLengthSecond() {
        IPlayer player = getCurrentPlayer();
        if (player.isValid() == false) {
            return 0;
        }
        return player.getLengthSecond();
    }

    @Override
    public boolean isSupportedExtension(String extension) {
        IPlayer player = getCurrentPlayer();
        if (player.isValid() == false) {
            return false;
        }
        return player.isSupportedExtension(extension);
    }

    public boolean sendMidiMessage(byte[] data, long timeStamp) {
        return sendMidiMessage(new MidiByteMessage(data), timeStamp);
    }

    @Override
    public boolean sendMidiMessage(MidiMessage msg, long timeStamp) {
        if (getCurrentReciever() == null) {
            return false;
        }

        getCurrentReciever().send(msg, timeStamp);

        JMPCore.getPluginManager().catchMidiEvent(msg, timeStamp, IMidiEventListener.SENDER_MIDI_IN);
        return true;
    }

    public Receiver getCurrentReciever() {
        return MidiPlayer.getCurrentReciver();
    }

    public Transmitter getCurrentTransmitter() {
        return MidiPlayer.getCurrentTransmitter();
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

    public void resetMidiEvent() {
        for (int i = 0; i < 16; i++) {
            resetProgramChange(i); // プログラムチェンジリセット
        }
    }

    public void resetProgramChange(int ch) {
        try {
            JMPCore.getSoundManager().sendProgramChange(ch, 0, 0);
        }
        catch (InvalidMidiDataException e1) {
        }
    }

    public String getPositionTimeString() {
        int time = getCurrentPlayer().getPositionSecond();
        return getPlayerTimeString(time);
    }

    public String getLengthTimeString() {
        int time = getCurrentPlayer().getLengthSecond();
        return getPlayerTimeString(time);
    }

    public String getPlayerTimeString(int time) {
        int min = 0;
        int sec = 0;
        if (time > 0) {
            min = time / 60;
            sec = time % 60;
            if (min > 99) {
                // 100分以上の表示は不可
                min = 99;
                sec = 59;
            }
        }
        String str = String.format(PLAYER_TIME_FORMAT, min, sec);
        return str;
    }
}
