package jmp.core;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import function.Platform;
import function.Platform.KindOfPlatform;
import function.Utility;
import jlib.core.IDataManager;
import jmp.ConfigDatabase;
import jmp.JMPFlags;

public class DataManager extends AbstractManager implements IDataManager {

    public static final String[] ExtentionForMIDI = { "mid", "midi" };
    public static final String[] ExtentionForWAV = { "wav" };
    public static final String[] ExtentionForMusicXML = { "xml", "musicxml", "mxl" };
    public static final String[] ExtentionForFFmpeg = { "*" };
    public static final String CONFIG_FILE = "config.txt";
    public static final String HISTORY_FILE = "history.txt";

    // 作成済みの設定値を反映した場合、ファイルに出力しない
    private boolean makedConfigDatabaseFlag = false;

    private DefaultListModel<String> historyModel = null;
    private JList<String> history = null;

    // 変換したファイルをログ
    private List<File> convertedFiles = null;

    // 固有変数
    private int transpose = 0;

    private ConfigDatabase database = null;

    /**
     * コンストラクタ
     */
    DataManager(int pri) {
        super(pri, "data");
    }

    protected boolean initFunc() {
        if (initializeFlag == false) {
            initializeFlag = true;
        }

        historyModel = new DefaultListModel<String>();
        history = new JList<String>(historyModel);
        if (database == null) {
            database = new ConfigDatabase();
            readingConfigFile();
            makedConfigDatabaseFlag = false;
        }
        else {
            makedConfigDatabaseFlag = true;
        }
        readingHistoryFile();

        convertedFiles = new LinkedList<File>();

        // 設定ファイルのFFmpegパスを同期
        JMPCore.getSystemManager().setFFmpegWrapperPath(getFFmpegPath());

        // ロードファイルの初期化
        setLoadedFile("");

        return true;
    }

    protected boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        if (isFFmpegLeaveOutputFile() == false) {
            if (convertedFiles.size() > 0) {
                for (File f : convertedFiles) {
                    if (f.exists() == true) {
                        Utility.deleteFileDirectory(f);
                        Utility.threadSleep(200);
                    }
                }
            }
        }
        if (makedConfigDatabaseFlag == false) {
            outputConfigFile();
        }
        if (JMPFlags.LibraryMode == false) {
            outputHistoryFile();
        }
        return true;
    }

    public void setConfigDatabase(ConfigDatabase db) {
        database = db;
    }

    public void initializeConfigDatabase() {
        database.initialize();
    }

    private boolean readingConfigFile() {
        String path = Platform.getCurrentPath() + CONFIG_FILE;
        return database.reading(path);
    }

    private boolean readingHistoryFile() {
        boolean ret = true;
        String path = Platform.getCurrentPath() + HISTORY_FILE;

        File file = new File(path);
        if (file.exists() == false) {
            return false;
        }

        try {
            List<String> textContents = Utility.getTextFileContents(path);

            for (String line : textContents) {
                historyModel.addElement(line);
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    private boolean outputConfigFile() {
        String path = Platform.getCurrentPath() + CONFIG_FILE;
        return database.output(path);
    }

    private boolean outputHistoryFile() {
        boolean ret = true;
        String path = Platform.getCurrentPath() + HISTORY_FILE;

        List<String> list = new LinkedList<String>();
        for (int i = 0; i < historyModel.getSize(); i++) {
            list.add(historyModel.get(i));
        }

        try {
            Utility.outputTextFile(path, list);
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    @Override
    public void setConfigParam(String key, String value) {
        database.setConfigParam(key, value);

        // 設定変更通知
        AbstractManager.callNotifyUpdateConfig(key);
    }

    @Override
    public String getConfigParam(String key) {
        return database.getConfigParam(key);
    }

    @Override
    public String[] getKeySet() {
        return ConfigDatabase.CFG_KEYSET;
    }

    public static final int MAX_HISTORY_SIZE = 50;

    public void addHistory(String path) {
        historyModel.add(0, path);

        int size = history.getModel().getSize();
        if (size > MAX_HISTORY_SIZE) {
            historyModel.remove(size - 1);
        }
    }

    public JList<String> getHistory() {
        return history;
    }

    public void clearHistory() {
        historyModel.removeAllElements();
    }

    public int getTranspose() {
        return transpose;
    }

    public void setTranspose(int transpose) {
        this.transpose = transpose;
    }

    public boolean isAutoPlay() {
        String sValue = getConfigParam(ConfigDatabase.CFG_KEY_AUTOPLAY);
        return Utility.tryParseBoolean(sValue, false);
    }

    public void setAutoPlay(boolean isAutoPlay) {
        setConfigParam(ConfigDatabase.CFG_KEY_AUTOPLAY, isAutoPlay ? "TRUE" : "FALSE");
    }

    public boolean isLoopPlay() {
        String sValue = getConfigParam(ConfigDatabase.CFG_KEY_LOOPPLAY);
        return Utility.tryParseBoolean(sValue, false);
    }

    public void setLoopPlay(boolean isLoopPlay) {
        setConfigParam(ConfigDatabase.CFG_KEY_LOOPPLAY, isLoopPlay ? "TRUE" : "FALSE");
    }

    public boolean isShowStartupDeviceSetup() {
        String sValue = getConfigParam(ConfigDatabase.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP);
        return Utility.tryParseBoolean(sValue, false);
    }

    public void setShowStartupDeviceSetup(boolean isShow) {
        setConfigParam(ConfigDatabase.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, isShow ? "TRUE" : "FALSE");
    }

    public int getLanguage() {
        String sValue = getConfigParam(ConfigDatabase.CFG_KEY_LANGUAGE);
        return Utility.tryParseInt(sValue, 0);
    }

    public void setLanguage(int language) {
        setConfigParam(ConfigDatabase.CFG_KEY_LANGUAGE, String.valueOf(language));
    }

    public String getPlayListPath() {
        return getConfigParam(ConfigDatabase.CFG_KEY_PLAYLIST);
    }

    public void setPlayListPath(String filePath) {
        setConfigParam(ConfigDatabase.CFG_KEY_PLAYLIST, filePath);
    }

    public String getLoadedFile() {
        return getConfigParam(ConfigDatabase.CFG_KEY_LOADED_FILE);
    }

    public void setLoadedFile(String filePath) {
        setConfigParam(ConfigDatabase.CFG_KEY_LOADED_FILE, filePath);
    }

    public String getFFmpegPath() {
        return getConfigParam(ConfigDatabase.CFG_KEY_FFMPEG_PATH);
    }

    public void setFFmpegPath(String filePath) {
        setConfigParam(ConfigDatabase.CFG_KEY_FFMPEG_PATH, filePath);

        JMPCore.getSystemManager().setFFmpegWrapperPath(filePath);
    }

    public String getFFmpegOutputPath() {
        return getConfigParam(ConfigDatabase.CFG_KEY_FFMPEG_OUTPUT);
    }

    public void setFFmpegOutputPath(String filePath) {
        setConfigParam(ConfigDatabase.CFG_KEY_FFMPEG_OUTPUT, filePath);
    }

    public void addConvertedFile(File file) {
        convertedFiles.add(file);
    }

    public boolean isFFmpegLeaveOutputFile() {
        String sValue = getConfigParam(ConfigDatabase.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE);
        return Utility.tryParseBoolean(sValue, false);
    }

    public void setFFmpegLeaveOutputFile(boolean isLeave) {
        setConfigParam(ConfigDatabase.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, isLeave ? "TRUE" : "FALSE");
    }

    public boolean isUseFFmpegPlayer() {
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            return false;
        }
        String sValue = getConfigParam(ConfigDatabase.CFG_KEY_USE_FFMPEG_PLAYER);
        return Utility.tryParseBoolean(sValue, false);
    }

    public void setUseFFmpegPlayer(boolean isUse) {
        setConfigParam(ConfigDatabase.CFG_KEY_USE_FFMPEG_PLAYER, isUse ? "TRUE" : "FALSE");
    }
}
