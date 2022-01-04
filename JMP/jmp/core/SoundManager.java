package jmp.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
import javax.swing.DefaultListModel;
import javax.swing.JList;

import function.Utility;
import jlib.core.ISoundManager;
import jlib.gui.IJmpMainWindow;
import jlib.gui.IJmpWindow;
import jlib.midi.IMidiController;
import jlib.midi.IMidiEventListener;
import jlib.midi.IMidiFilter;
import jlib.midi.IMidiToolkit;
import jlib.midi.IMidiUnit;
import jlib.midi.MidiByte;
import jlib.player.IPlayer;
import jlib.player.Player;
import jmp.FileResult;
import jmp.JMPFlags;
import jmp.lang.DefineLanguage.LangID;
import jmp.midi.MidiByteMessage;
import jmp.midi.MidiController;
import jmp.midi.MidiUnit;
import jmp.midi.toolkit.MidiToolkitManager;
import jmp.player.FFmpegPlayer;
import jmp.player.MidiPlayer;
import jmp.player.MoviePlayer;
import jmp.player.MusicMacroPlayer;
import jmp.player.MusicXmlPlayer;
import jmp.player.PlayerAccessor;
import jmp.player.WavPlayer;
import jmp.util.JmpUtil;

/**
 * サウンド管理クラス
 *
 * @author abs
 *
 */
public class SoundManager extends AbstractManager implements ISoundManager {

    /** NULLレシーバー */
    public static final String NULL_RECEIVER_NAME = "NULL";

    public static final String PLAYLIST_FILE_EXTENTION = "plst";
    public static final String BACKUP_PLAYLIST_FILE_NAME = "backup." + PLAYLIST_FILE_EXTENTION;

    public static final String PLAYER_TIME_FORMAT = "%02d:%02d";

    private DefaultListModel<String> playListModel = new DefaultListModel<>();
    private JList<String> playList = new JList<String>(playListModel);

    // プレイヤーアクセッサ
    private static PlayerAccessor PlayerAccessor = null;

    // プレイヤーインスタンス
    public static MidiPlayer SMidiPlayer = null;
    public static WavPlayer SWavPlayer = null;
    //public static WavPlayerMin SWavPlayer = null;
    public static MusicXmlPlayer SMusicXmlPlayer = null;
    public static MusicMacroPlayer SMusicMacloPlayer = null;
    public static FFmpegPlayer SFFmpegPlayer = null;
    public static MoviePlayer SMoviePlayer = null;

    // 固有変数
    private int[] transpose = new int[16];

    public static final int MAX_TRANSPOSE = 12;
    public static final int MIN_TRANSPOSE = -12;

    private IMidiFilter defaultMidiFilter = null;
    private IMidiToolkit midiToolkit = null;
    private IMidiController midiInController = null;
    private IMidiController midiOutController = null;
    private IMidiUnit midiUnit = null;

    // Line情報
    private static Line.Info[] LineInfos = new Line.Info[] { Port.Info.SPEAKER, Port.Info.LINE_OUT, Port.Info.HEADPHONE };

    // 音量をネイティブ変数として保持しておく
    private float nativeVolume = -1.0f;

    // MIDIデバイス設定をコミットしたか保持する必要がある
    private boolean isCommitDeviceSelectAction = false;
    public void setCommitDeviceSelectAction(boolean b) {
        isCommitDeviceSelectAction = b;
    }

    SoundManager() {
        super("sound");
    }

    @Override
    protected boolean initFunc() {

        // MidiToolKitを更新する
        updateMidiToolkit();

        PlayerAccessor = new PlayerAccessor();

        /* プレイヤーインスタンス作成 */
        SystemManager system = JMPCore.getSystemManager();
        String[] exMIDI = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MIDI));
        String[] exWAV = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_WAV));
        String[] exMUSICXML = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MUSICXML));
        String[] exMML = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MML));

        // midi
        SMidiPlayer = new MidiPlayer();
        SMidiPlayer.setSupportExtentions(exMIDI);
        PlayerAccessor.register(SMidiPlayer);

        // wav
        SWavPlayer = new WavPlayer();
        //SWavPlayer = new WavPlayerMin();
        SWavPlayer.setSupportExtentions(exWAV);
        PlayerAccessor.register(SWavPlayer);

        // musicxml
        SMusicXmlPlayer = new MusicXmlPlayer();
        SMusicXmlPlayer.setSupportExtentions(exMUSICXML);
        PlayerAccessor.register(SMusicXmlPlayer);

        // mml
        SMusicMacloPlayer = new MusicMacroPlayer();
        SMusicMacloPlayer.setSupportExtentions(exMML);
        PlayerAccessor.register(SMusicMacloPlayer);
        
        // movie
        SMoviePlayer = new MoviePlayer();
        SMoviePlayer.setSupportExtentions("mp4", "mp3");
        PlayerAccessor.register(SMoviePlayer);

        // ffmpeg
        SFFmpegPlayer = new FFmpegPlayer();
        SFFmpegPlayer.setSupportExtentions("*");
        PlayerAccessor.register(SFFmpegPlayer);

        // デフォルトはMIDIプレイヤーにする
        PlayerAccessor.change(SMidiPlayer);

        // Midiユニットインスタンス生成
        midiUnit = new MidiUnit();

        // デフォルトMIDIフィルター
        defaultMidiFilter = new IMidiFilter() {
            @Override
            public boolean filter(MidiMessage message, short senderType) {
                IMidiToolkit toolkit = getMidiToolkit();
                int transpose = getTranspose();
                if (transpose != 0) {
                    if (toolkit.isNoteOn(message) == true || toolkit.isNoteOff(message) == true) {
                        byte[] data = message.getMessage();
                        int length = message.getLength();

                        int channel = MidiByte.getChannel(data, length);
                        if (channel == 9) {
                            // ドラムトラックは対象外
                            return true;
                        }

                        int status = message.getStatus();
                        int data1 = MidiByte.getData1(data, length);
                        int data2 = MidiByte.getData2(data, length);

                        data1 += transpose;

                        /* TODO instanceofを指定して処理を分岐するのはよろしくない 【改善策はないか...】 */
                        if (message instanceof ShortMessage) {
                            try {
                                ((ShortMessage) message).setMessage(status, data1, data2);
                            }
                            catch (InvalidMidiDataException e) {
                            }
                        }
                        else if (message instanceof MidiByteMessage) {
                            MidiByteMessage bMes = (MidiByteMessage) message;
                            bMes.changeByte(0, status);
                            bMes.changeByte(1, data1);
                            bMes.changeByte(2, data2);
                        }
                    }
                }
                return true;
            }
        };
        addFilter(defaultMidiFilter);

        // Midiコントローラーの準備
        midiInController = new MidiController(IMidiEventListener.SENDER_MIDI_IN); // INコントローラインターフェース
        midiOutController = new MidiController(IMidiEventListener.SENDER_MIDI_OUT); // OUTコントローラインターフェース

        // 音量を更新する(ネイティブ変数を更新するため起動時に必ず呼ぶ必要がある)
        syncLineVolume();

        /* プレイリストの復元 */
        String path = Utility.pathCombin(JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SAVE_DIR), BACKUP_PLAYLIST_FILE_NAME);
        if (Utility.isExsistFile(path) == true) {
            try {
                loadPlayList(path);
            }
            catch (Exception e) {
            }
        }

        super.initFunc();
        return true;

    }

    @Override
    protected boolean endFunc() {
        if (super.endFunc() == false) {
            return false;
        }

        /* プレイリストの保存 */
        String path = Utility.pathCombin(JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_SAVE_DIR), BACKUP_PLAYLIST_FILE_NAME);
        try {
            savePlayList(path);
        }
        catch (Exception e) {
        }

        stop();
        PlayerAccessor.stopAllPlayer();
        PlayerAccessor.close();
        return true;
    }

    public boolean startupDeviceSetup() {
        /* MIDI設定の初期処理 */
        boolean wasCommit = true;
        if (JMPFlags.StartupAutoConectSynth == false) {
            // Midiデバイス選択ダイアログの表示
            JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_SETUP).showWindow();

//            SelectSoundFontDIalog dialog = new SelectSoundFontDIalog();
//            dialog.start();
//            isCommitDeviceSelectAction = true;

            wasCommit = isCommitDeviceSelectAction;
        }

        // Midiデバイスの読み込み
        JMPCore.getSoundManager().reloadMidiDevice(true, true);
        return wasCommit;
    }

    public boolean filter(MidiMessage message, short senderType) {
        if (SMidiPlayer.filter(message, senderType) == false) {
            return false;
        }
        return true;
    }

    public void addFilter(IMidiFilter f) {
        SMidiPlayer.addFilter(f);
    }

    public void removeFilter(IMidiFilter f) {
        SMidiPlayer.removeFilter(f);
    }

    @Override
    public IMidiUnit getMidiUnit() {
        return midiUnit;
    }

    @Override
    public IMidiController getMidiController(short senderType) {
        IMidiController controller = null;
        switch (senderType) {
            case IMidiEventListener.SENDER_MIDI_IN:
                controller = midiInController;
                break;
            case IMidiEventListener.SENDER_MIDI_OUT:
            default:
                controller = midiOutController;
                break;
        }
        return controller;
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

    public IPlayer getCurrentPlayer() {
        return PlayerAccessor.getCurrent();
    }

    public Player.Info getCurrentPlayerInfo() {
        return PlayerAccessor.getCurrent().getInfo();
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
        if (JMPFlags.PlayListExtention == true) {
            if ((playListModel.isEmpty() == true) || (playListModel.size() <= 0)) {
                return false;
            }
        }
        else {
            if (JMPCore.getFileManager().getFileListModel().getRowCount() <= 0) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidPlayListIndex(int index) {
        if (JMPFlags.PlayListExtention == true) {
            if (0 > index || index >= playListModel.size()) {
                // インデックスが有効か判定
                return false;
            }
        }
        else {
            if (0 > index || index >= JMPCore.getFileManager().getFileListModel().getRowCount()) {
                // インデックスが有効か判定
                return false;
            }
        }
        return true;
    }

    public int getCurrentPlayListIndex() {
        if (JMPFlags.PlayListExtention == true) {
            return getPlayList().getSelectedIndex();
        }
        else {
            return JMPCore.getFileManager().getFileList().getSelectedRow();
        }
    }

    public boolean isValidPlayListNext() {
        int index = getCurrentPlayListIndex();
        return isValidPlayListIndex(index + 1);
    }

    public boolean isValidPlayListPrev() {
        int index = getCurrentPlayListIndex();
        return isValidPlayListIndex(index - 1);
    }

    public boolean checkMusicFileExtention(File file) {
        SystemManager system = JMPCore.getSystemManager();
        String[] exts = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MIDI));
        if (Utility.checkExtensions(file, exts) == true) {
            return true;
        }
        exts = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_WAV));
        if (Utility.checkExtensions(file, exts) == true) {
            return true;
        }
        if (JMPCore.getSystemManager().isValidFFmpegWrapper() == true && JMPCore.getDataManager().isUseFFmpegPlayer() == true) {
            exts = JmpUtil.genStr2Extensions(system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_EXTENSION_MUSIC));
            if (Utility.checkExtensions(file, exts) == true) {
                return true;
            }
        }
        return false;
    }

    public void playForList(int index) {
        playForList(index, false);
    }

    public void playForList(int index, boolean isLoadOnly) {
        LanguageManager lm = JMPCore.getLanguageManager();
        IJmpMainWindow mainWindow = JMPCore.getWindowManager().getMainWindow();

        if (JMPFlags.PlayListExtention == true) {
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
        else {

            String name = JMPCore.getFileManager().getFileListModel().getValueAt(index, 1).toString();
            File dir = new File(JMPCore.getDataManager().getPlayListPath());
            Map<String, File> map = JMPCore.getFileManager().getFileMap(dir);
            File file = map.get(name);

            try {
                IPlayer player = getCurrentPlayer();
                player.stop();
                if (isLoadOnly == false) {
                    // 自動再生フラグ
                    JMPFlags.LoadToPlayFlag = true;
                }
                JMPCore.getFileManager().loadFile(file);
            }
            catch (Exception e) {
            }

            JMPCore.getFileManager().getFileList().changeSelection(index, 1, false, false);
        }

    }

    public void playCurrent() {
        playCurrent(false);
    }

    public void playCurrent(boolean isLoadOnly) {
        int index = getCurrentPlayListIndex();
        playForList(index, isLoadOnly);
    }

    public void playNext() {
        playNext(false);
    }

    public void playNext(boolean isLoadOnly) {
        DataManager dm = JMPCore.getDataManager();

        int index = getCurrentPlayListIndex();
        if (dm.isRandomPlay() == true) {
            if (JMPFlags.PlayListExtention == false) {
                String selected = JMPCore.getFileManager().getFileListModel().getValueAt(index, 1).toString();
                File dir = new File(dm.getPlayListPath());
                Map<String, File> map = JMPCore.getFileManager().getFileMap(dir);

                List<String> pickup = new ArrayList<String>();
                for (String k : map.keySet()) {
                    File f = map.get(k);
                    if (f.isDirectory() == false && f.canRead() == true && checkMusicFileExtention(f) == true) {
                        pickup.add(k);
                    }
                }

                if (pickup.isEmpty() == true) {
                    JMPFlags.NextPlayFlag = false;
                    return;
                }

                Random random = new Random();
                String newName = selected;
                if (JMPCore.getFileManager().getFileList().getRowCount() >= 2) {
                    while(true) {
                        int ranValue = random.nextInt(pickup.size());
                        newName = pickup.get(ranValue);
                        if (newName.equals(selected) == false) {
                            break;
                        }
                    }
                }
                for (int i=0; i<JMPCore.getFileManager().getFileList().getRowCount(); i++) {
                    if (newName.equals(JMPCore.getFileManager().getFileListModel().getValueAt(i, 1).toString()) == true) {
                        index = i;
                    }
                }
            }
            else {
                if (playListModel.size() <= 0) {
                    JMPFlags.NextPlayFlag = false;
                    return;
                }

                Random random = new Random();

                int newIndex = index;
                if (playListModel.size() >= 2) {
                    while(true) {
                        newIndex = random.nextInt(playListModel.size());
                        if (newIndex != index) {
                            break;
                        }
                    }
                }
                index = newIndex;

            }
            playForList(index, isLoadOnly);
        }
        else {
            if (JMPFlags.PlayListExtention == false) {
                if (JMPCore.getDataManager().isAutoPlay() == true) {
                    File dir = new File(dm.getPlayListPath());
                    Map<String, File> map = JMPCore.getFileManager().getFileMap(dir);

                    if (isValidPlayListIndex(index + 1) == false) {
                        if (dm.isLoopPlay() == true) {
                            index = 0;
                        }
                        else {
                            JMPFlags.NextPlayFlag = false;
                            return;
                        }
                    }
                    String name = JMPCore.getFileManager().getFileListModel().getValueAt(index + 1, 1).toString();
                    File file = map.get(name);

                    int cnt = 0;
                    while(true) {
                        if (file.isDirectory() == false && file.canRead() == true && checkMusicFileExtention(file) == true) {
                            break;
                        }
                        index++;
                        if (isValidPlayListIndex(index + 1) == false) {
                            if (dm.isLoopPlay() == true) {
                                index = 0;
                            }
                            else {
                                JMPFlags.NextPlayFlag = false;
                                return;
                            }
                        }
                        name = JMPCore.getFileManager().getFileListModel().getValueAt(index + 1, 1).toString();
                        file = map.get(name);

                        cnt++;
                        if (cnt >= JMPCore.getFileManager().getFileListModel().getRowCount()) {
                            JMPFlags.NextPlayFlag = false;
                            return;
                        }
                    }
                }
            }
            playForList(index + 1, isLoadOnly);
        }
    }

    public void playPrev() {
        playPrev(false);
    }

    public void playPrev(boolean isLoadOnly) {
        DataManager dm = JMPCore.getDataManager();
        int index = getCurrentPlayListIndex();
        if (JMPFlags.PlayListExtention == false) {
            if (JMPCore.getDataManager().isAutoPlay() == true) {
                File dir = new File(dm.getPlayListPath());
                Map<String, File> map = JMPCore.getFileManager().getFileMap(dir);

                if (isValidPlayListIndex(index - 1) == false) {
                    if (dm.isLoopPlay() == true) {
                        index = JMPCore.getFileManager().getFileListModel().getRowCount() - 1;
                    }
                    else {
                        return;
                    }
                }
                String name = JMPCore.getFileManager().getFileListModel().getValueAt(index - 1, 1).toString();
                File file = map.get(name);

                int cnt = 0;
                while(true) {
                    if (file.isDirectory() == false && file.canRead() == true && checkMusicFileExtention(file) == true) {
                        break;
                    }
                    index--;
                    if (isValidPlayListIndex(index - 1) == false) {
                        if (dm.isLoopPlay() == true) {
                            index = JMPCore.getFileManager().getFileListModel().getRowCount() - 1;
                        }
                        else {
                            return;
                        }
                    }
                    name = JMPCore.getFileManager().getFileListModel().getValueAt(index - 1, 1).toString();
                    file = map.get(name);

                    cnt++;
                    if (cnt >= JMPCore.getFileManager().getFileListModel().getRowCount()) {
                        return;
                    }
                }
            }
        }
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

    public void loadPlayList(String path) throws IOException {
        List<String> lst = JmpUtil.readTextFile(path);
        playListModel.clear();
        for (int i = 0; i < lst.size(); i++) {
            playListModel.addElement(lst.get(i));
        }
    }

    public void savePlayList(String path) throws FileNotFoundException, UnsupportedEncodingException {
        List<String> lst = new LinkedList<String>();
        for (int i = 0; i < playListModel.size(); i++) {
            lst.add(playListModel.get(i));
        }
        JmpUtil.writeTextFile(path, lst);
    }

    @Override
    protected void loadFileForCore(File file, FileResult result) {
        super.loadFileForCore(file, result);

        Player tmpPlayer = PlayerAccessor.getCurrent();

        boolean loadResult = true;

        try {
            changePlayer(file);
            if (tmpPlayer != PlayerAccessor.getCurrent()) {
            	tmpPlayer.changingPlayer();
            }

            if (PlayerAccessor.getCurrent().loadFile(file) == false) {
                loadResult = false;
            }
        }
        catch (Exception e) {
            if (JMPFlags.DebugMode == true) {
                e.printStackTrace();
            }
            loadResult = false;
        }
        finally {
            if (loadResult == false) {
                // ロードに失敗した場合は、プレイヤーを元に戻す
                PlayerAccessor.change(tmpPlayer);
            }
        }

        result.status = loadResult;
        if (result.status == false) {
            /* ファイルオープンに例外が発生した場合、プレーヤーを切り替える */
            changeMidiPlayer();
            JMPCore.getWindowManager().getMainWindow().clearStatusMessage();
            LanguageManager lm = JMPCore.getLanguageManager();
            result.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_5);
        }
    }

    public boolean isSupportedExtensionAccessor(String extension) {
        return PlayerAccessor.isSupportedExtension(extension);
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

        JMPCore.getWindowManager().processingBeforePlay();

        if (player.getPosition() >= player.getLength()) {
            // 最初から再生
            initPosition();
        }
        player.play();

        JMPCore.getWindowManager().processingAfterPlay();

        JMPFlags.NextPlayFlag = true;
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

        JMPCore.getWindowManager().processingBeforeStop();

        player.stop();

        JMPCore.getWindowManager().processingAfterStop();

        if (backupVisible == true) {
            JMPFlags.WindowAutomationPosFlag = false;
            midiMinitor.showWindow();
            JMPFlags.WindowAutomationPosFlag = true;
        }

        JMPFlags.NextPlayFlag = false;
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
            JMPFlags.WindowAutomationPosFlag = false;
            midiMinitor.showWindow();
            JMPFlags.WindowAutomationPosFlag = true;
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

    public Receiver getCurrentReciever() {
        return SMidiPlayer.getCurrentReciver();
    }

    public Transmitter getCurrentTransmitter() {
        return SMidiPlayer.getCurrentTransmitter();
    }

    public void changeMidiPlayer() {
        // MidiPlayerに変更する
        if (PlayerAccessor.getCurrent() != SoundManager.SMidiPlayer) {
            PlayerAccessor.getCurrent().stop();
            PlayerAccessor.change(SoundManager.SMidiPlayer);
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

    public void resetMidiEvent() {
        for (int i = 0; i < 16; i++) {
            resetProgramChange(i); // プログラムチェンジリセット
        }
    }

    public void resetProgramChange(int ch) {
        try {
            JMPCore.getSoundManager().getMidiController().sendProgramChange(ch, 0, 0);
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
        if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_USE_MIDI_TOOLKIT)) == true) {
            updateMidiToolkit();
        }
        else if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_EXTENSION_MIDI)) == true) {
            String val = system.getCommonRegisterValue(key);
            String[] exs = JmpUtil.genStr2Extensions(val);
            SMidiPlayer.setSupportExtentions(exs);
        }
        else if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_EXTENSION_WAV)) == true) {
            String val = system.getCommonRegisterValue(key);
            String[] exs = JmpUtil.genStr2Extensions(val);
            SWavPlayer.setSupportExtentions(exs);
        }
        else if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_EXTENSION_MUSICXML)) == true) {
            String val = system.getCommonRegisterValue(key);
            String[] exs = JmpUtil.genStr2Extensions(val);
            SMusicXmlPlayer.setSupportExtentions(exs);
        }
        else if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_EXTENSION_MML)) == true) {
            String val = system.getCommonRegisterValue(key);
            String[] exs = JmpUtil.genStr2Extensions(val);
            SMusicMacloPlayer.setSupportExtentions(exs);
        }
        else if (key.equals(system.getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_EXTENSION_MUSIC)) == true) {
        }
        super.notifyUpdateCommonRegister(key);
    }

    @Override
    protected void notifyUpdateConfig(String key) {
        super.notifyUpdateConfig(key);

        if (key.equals(DataManager.CFG_KEY_MIDIIN) == true) {
            if (JMPCore.isFinishedInitialize() == true) {
                reloadMidiDevice(false, true);
            }
        }
        else if (key.equals(DataManager.CFG_KEY_MIDIOUT) == true) {
            if (JMPCore.isFinishedInitialize() == true) {
                reloadMidiDevice(true, false);
            }
        }
    }

    public void updateMidiToolkit() {
        // 使用するツールキットを更新
        SystemManager system = JMPCore.getSystemManager();
        String toolkitName = system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_USE_MIDI_TOOLKIT);
        midiToolkit = MidiToolkitManager.getInstance().getMidiToolkit(toolkitName);
    }

    public void sendMidiSystemSetupMessage() {
        IMidiController controller = getMidiController();

        // GMシステムオン
        controller.sendMidiMessage(MidiByte.GM_SYSTEM_ON, 0);
        Utility.threadSleep(50);
        // XGシステムオン
        controller.sendMidiMessage(MidiByte.XG_SYSTEM_ON, 0);
        Utility.threadSleep(50);
        // GSリセット
        controller.sendMidiMessage(MidiByte.GS_RESET, 0);
        Utility.threadSleep(100);
    }

    @Override
    public IMidiToolkit getMidiToolkit() {
        return midiToolkit;
    }

    public int getTranspose() {
        return transpose[0];
    }

    @Override
    public int getTranspose(int channel) {
        if (channel == 9) {
            return 0;
        }
        return getTranspose();
    }

    public void setTranspose(int transpose) {
        for (int ch=0; ch<16; ch++) {
            setTranspose(ch, transpose);
        }
    }

    @Override
    public void setTranspose(int channel, int transpose) {
        if (MAX_TRANSPOSE < transpose) {
            transpose = MAX_TRANSPOSE;
        }
        else if (MIN_TRANSPOSE > transpose) {
            transpose = MIN_TRANSPOSE;
        }
        this.transpose[channel] = transpose;

        IMidiController controller = getMidiController();
        try {
            controller.sendMidiMessage(getMidiToolkit().createAllNoteOffMessage(channel), 0);
        }
        catch (InvalidMidiDataException e) {
        }
    }

    public void syncLineVolume() {
        nativeVolume = -1.0f;
        updateLineVolume();
    }

    public void updateLineVolume() {
        int supported = 0;
        for (int i = 0; i < LineInfos.length; i++) {
            Line.Info source = LineInfos[i];
            if (AudioSystem.isLineSupported(source) == true) {
                try {
                    Port outline = (Port) AudioSystem.getLine(source);
                    outline.open();
                    FloatControl volumeControl = (FloatControl) outline.getControl(FloatControl.Type.VOLUME);
                    if (nativeVolume >= 0.0f) {
                        volumeControl.setValue(nativeVolume);
                    }
                    nativeVolume = volumeControl.getValue();
                    supported++;
                }
                catch (LineUnavailableException ex) {
                    System.out.println("source not supported");
                }
            }
        }

        // サポートするLineが存在しない場合は0にする
        if (supported == 0) {
            nativeVolume = 0.0f;
        }
    }

    @Override
    public void setLineVolume(float v) {
        nativeVolume = v;
        updateLineVolume();
    }

    @Override
    public float getLineVolume() {
        return nativeVolume;
    }

    public void reloadMidiDevice(boolean out, boolean in) {
        // DataManagerのデバイスを再設定する
        String outName = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIOUT);
        String inName = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIIN);
        if (out == true) {
            SMidiPlayer.updateMidiOut(outName);
            JMPFlags.Log.cprintln("Device update[OUT] : " + (outName.isEmpty() ? "Auto selection" : outName));
        }
        if (in == true) {
            SMidiPlayer.updateMidiIn(inName);
            JMPFlags.Log.cprintln("Device update[IN]  : " + (inName.isEmpty() ? "None" : inName));
        }
    }
}
