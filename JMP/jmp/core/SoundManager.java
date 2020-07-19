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
import jlib.midi.IMidiToolkit;
import jlib.player.IPlayer;
import jmp.CommonRegister;
import jmp.JMPFlags;
import jmp.JmpUtil;
import jmp.lang.DefineLanguage.LangID;
import jmp.midi.MidiByteMessage;
import jmp.midi.toolkit.MidiToolkitManager;
import jmp.player.FFmpegPlayer;
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

    // プレイヤーアクセッサ
    private static PlayerAccessor PlayerAccessor = null;

    // プレイヤーインスタンス
    public static MidiPlayer MidiPlayer = null;
    public static WavPlayer WavPlayer = null;
    public static MusicXmlPlayer MusicXmlPlayer = null;
    public static FFmpegPlayer FFmpegPlayer = null;

    FloatControl volumeCtrl;

    private IMidiToolkit midiToolkit = null;

    SoundManager(int pri) {
        super(pri, "sound");
    }

    @Override
    protected boolean initFunc() {
        if (JMPCore.getDataManager().isShowStartupDeviceSetup() == false) {
            // 自動接続フラグを立てる
            JMPFlags.StartupAutoConectSynth = true;
        }

        // MidiToolKitを更新する
        updateMidiToolkit();

        PlayerAccessor = new PlayerAccessor();

        // プレイヤーインスタンス作成
        SystemManager system = JMPCore.getSystemManager();
        String[] exMIDI = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(CommonRegister.COMMON_REGKEY_EXTENSION_MIDI));
        String[] exWAV = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(CommonRegister.COMMON_REGKEY_EXTENSION_WAV));
        String[] exMUSICXML = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(CommonRegister.COMMON_REGKEY_EXTENSION_MUSICXML));

        MidiPlayer = new MidiPlayer();
        MidiPlayer.setSupportExtentions(exMIDI);
        PlayerAccessor.register(MidiPlayer);

        WavPlayer = new WavPlayer();
        WavPlayer.setSupportExtentions(exWAV);
        PlayerAccessor.register(WavPlayer);

        MusicXmlPlayer = new MusicXmlPlayer();
        MusicXmlPlayer.setSupportExtentions(exMUSICXML);
        PlayerAccessor.register(MusicXmlPlayer);

        FFmpegPlayer = new FFmpegPlayer();
        FFmpegPlayer.setSupportExtentions("*");
        PlayerAccessor.register(FFmpegPlayer);

        // デフォルトはMIDIプレイヤーにする
        PlayerAccessor.change(MidiPlayer);

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
        PlayerAccessor.stopAllPlayer();
        PlayerAccessor.close();
        return true;
    }

    public boolean openPlayer() {
        /* プレイヤーロード */
        boolean result = true;
        try {
            if (PlayerAccessor.open() == false) {
                result = false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
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
        return PlayerAccessor.getCurrent();
    }

    public void clearPlayList() {
        playListModel.removeAllElements();
    }

    public void initPlay() {
        IPlayer player = getCurrentPlayer();
        if (player != null && player.isValid() == true) {
            initPosition();
            play();
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
        LanguageManager lm = JMPCore.getLanguageManager();
        IJmpMainWindow mainWindow = JMPCore.getWindowManager().getMainWindow();
        if (isValidPlayListIndex(index) == false) {
            mainWindow.setStatusText(lm.getLanguageStr(LangID.FILE_ERROR_6), false);
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
            JMPCore.getFileManager().loadFile(path);
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

    /**
     * リスト構成・DataManager設定から次の再生曲を指定する
     */
    public void playNextForList() {
        boolean isPlayNext = false;
        DataManager dm = JMPCore.getDataManager();
        if (dm.isLoopPlay() == true) {
            if (dm.isAutoPlay() == true) {
                if (isValidPlayList() == true) {
                    if (isValidPlayListNext() == true) {
                        // 次の曲
                        playNext();
                        isPlayNext = true;
                    }
                    else {
                        playForList(0);
                        isPlayNext = true;
                    }
                }
            }
            else {
                // ループ再生
                initPlay();
                isPlayNext = true;
            }
        }
        else if (dm.isAutoPlay() == true) {
            if (isValidPlayList() == true) {
                if (isValidPlayListNext() == true) {
                    // 次の曲
                    playNext();
                    isPlayNext = true;
                }
            }
        }

        if (isPlayNext == false && (dm.isLoopPlay() == true || dm.isAutoPlay() == true)) {
            // 再生できなかった場合、Tickを終了位置にする
            endPosition();
        }
    }

    /**
     * リスト構成・DataManager設定から前の再生曲を指定する
     */
    public void playPrevForList() {
        if (getCurrentPlayer().isValid() == false) {
            return;
        }

        boolean isPlayPrev = false;
        DataManager dm = JMPCore.getDataManager();
        if (getPosition() < getAmount()) {
            if (dm.isAutoPlay() == true) {
                if (isValidPlayList() == true) {
                    if (isValidPlayListPrev() == true) {
                        // 前の曲
                        playPrev();
                        isPlayPrev = true;
                    }
                    else {
                        int index = playListModel.size() - 1;
                        if (isValidPlayListIndex(index) == true) {
                            playForList(index);
                            isPlayPrev = true;
                        }
                    }
                }
            }
        }

        if (isPlayPrev == false) {
            // 再生できなかった場合、Tickを開始位置にする
            initPosition();
        }
    }

    void loadFile(File file) throws Exception {
        Player tmpPlayer = PlayerAccessor.getCurrent();

        boolean loadResult = true;

        try {
            changePlayer(file);

            if (PlayerAccessor.getCurrent().loadFile(file) == false) {
                loadResult = false;
            }
        }
        catch (Exception e) {
            if (JMPFlags.DebugMode == true) {
                e.printStackTrace();
            }
            loadResult = false;
            throw e;
        }
        finally {
            if (loadResult == false) {
                // ロードに失敗した場合は、プレイヤーを元に戻す
                PlayerAccessor.change(tmpPlayer);
                throw new Exception();
            }
        }
    }

    public boolean isSupportedExtensionAccessor(String extension) {
        return PlayerAccessor.isSupportedExtension(extension);
    }

    /**
     * 指定ディレクトリにあるファイルを列挙する
     *
     * @param dir
     *            ディレクトリ
     * @return ファイルリスト(key:ファイル名, value:Fileオブジェクト)
     */
    public Map<String, File> getFileList(File dir) {
        HashMap<String, File> result = new HashMap<String, File>();
        if (dir.isDirectory() == false) {
            return result;
        }

        for (File file : dir.listFiles()) {
            if (file == null) {
                continue;
            }

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

    public void changeMidiPlayer() {
        // MidiPlayerに変更する
        if (PlayerAccessor.getCurrent() != SoundManager.MidiPlayer) {
            PlayerAccessor.getCurrent().stop();
            PlayerAccessor.change(SoundManager.MidiPlayer);
        }
    }

    public void changePlayer(File file) {
        String ex = Utility.getExtension(file);
        if (PlayerAccessor.isSupportedExtension(ex) == false) {
            return;
        }

        PlayerAccessor.getCurrent().stop();
        if (PlayerAccessor.change(ex) == true) {
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

    @Override
    protected void notifyUpdateCommonRegister(String key) {
        SystemManager system = JMPCore.getSystemManager();
        if (key.equals(CommonRegister.COMMON_REGKEY_USE_MIDI_TOOLKIT) == true) {
            updateMidiToolkit();
        }
        else if (key.equals(CommonRegister.COMMON_REGKEY_EXTENSION_MIDI) == true) {
            String val = system.getCommonRegisterValue(key);
            String[] exs = JmpUtil.genStr2Extensions(val);
            MidiPlayer.setSupportExtentions(exs);
        }
        else if (key.equals(CommonRegister.COMMON_REGKEY_EXTENSION_WAV) == true) {
            String val = system.getCommonRegisterValue(key);
            String[] exs = JmpUtil.genStr2Extensions(val);
            WavPlayer.setSupportExtentions(exs);
        }
        else if (key.equals(CommonRegister.COMMON_REGKEY_EXTENSION_MUSICXML) == true) {
            String val = system.getCommonRegisterValue(key);
            String[] exs = JmpUtil.genStr2Extensions(val);
            MusicXmlPlayer.setSupportExtentions(exs);
        }
        super.notifyUpdateCommonRegister(key);
    }

    public void updateMidiToolkit() {
        // 使用するツールキットを更新
        SystemManager system = JMPCore.getSystemManager();
        String toolkitName = system.getCommonRegisterValue(CommonRegister.COMMON_REGKEY_USE_MIDI_TOOLKIT);
        midiToolkit = MidiToolkitManager.getInstance().getMidiToolkit(toolkitName);
    }

    @Override
    public IMidiToolkit getMidiToolkit() {
        return midiToolkit;
    }
}
