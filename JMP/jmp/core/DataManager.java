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
import jmp.ConfigDatabaseWrapper;
import jmp.IJmpConfigDatabase;
import jmp.JMPLoader;

public class DataManager extends AbstractManager implements IDataManager, IJmpConfigDatabase {
    public static final String CONFIG_FILE = "config.txt";
    public static final String HISTORY_FILE = "history.txt";

    private DefaultListModel<String> historyModel = null;
    private JList<String> history = null;

    // 変換したファイルをログ
    private List<File> convertedFiles = null;

    // 固有変数
    private int transpose = 0;

    private ConfigDatabase database = null;

    public static final String CFG_KEY_PLAYLIST = "PLAYLIST";
    public static final String CFG_KEY_MIDIOUT = "MIDIOUT";
    public static final String CFG_KEY_MIDIIN = "MIDIIN";
    public static final String CFG_KEY_AUTOPLAY = "AUTOPLAY";
    public static final String CFG_KEY_LOOPPLAY = "LOOPPLAY";
    public static final String CFG_KEY_SHOW_STARTUP_DEVICE_SETUP = "SHOW_STARTUP_DEVICE_SETUP";
    public static final String CFG_KEY_LANGUAGE = "LANGUAGE";
    public static final String CFG_KEY_LOADED_FILE = "LOADED_FILE";
    public static final String CFG_KEY_FFMPEG_PATH = "FFMPEG_PATH";
    public static final String CFG_KEY_FFMPEG_OUTPUT = "FFMPEG_OUTPUT";
    public static final String CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE = "FFMPEG_LEAVE_OUTPUT_FILE";
    public static final String CFG_KEY_USE_FFMPEG_PLAYER = "USE_FFMPEG_PLAYER";
    // ↓KEY追加後、必ずCFG_KEYSETに追加すること!!
    public static final String[] CFG_KEYSET = { CFG_KEY_PLAYLIST, CFG_KEY_MIDIOUT, CFG_KEY_MIDIIN, CFG_KEY_AUTOPLAY, CFG_KEY_LOOPPLAY,
            CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, CFG_KEY_LANGUAGE, CFG_KEY_LOADED_FILE, CFG_KEY_FFMPEG_PATH, CFG_KEY_FFMPEG_OUTPUT,
            CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, CFG_KEY_USE_FFMPEG_PLAYER, };

    // 初期化キー
    public static final String CFG_KEY_INITIALIZE = "INITIALIZE";

    /**
     * コンストラクタ
     */
    DataManager(int pri) {
        super(pri, "data");
    }

    protected boolean initFunc() {
        super.initFunc();
        historyModel = new DefaultListModel<String>();
        history = new JList<String>(historyModel);
        if (database == null) {
            ConfigDatabaseWrapper wrap = new ConfigDatabaseWrapper();
            database = wrap.getConfigDatabase();
            readingConfigFile();
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
        if (super.endFunc() == false) {
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
        if (JMPLoader.UseConfigFile == true) {
            outputConfigFile();
        }
        if (JMPLoader.UseHistoryFile == true) {
            outputHistoryFile();
        }
        return true;
    }

    public void setConfigDatabase(ConfigDatabase db) {
        database = db;
    }

    public void initializeConfigDatabase() {

        ConfigDatabaseWrapper configWrap = new ConfigDatabaseWrapper(database);

        // 言語設定は初期化しないためバックアップする
        int backupLanguage = configWrap.getLanguage();

        // 初期化実行
        configWrap.initialize();

        // 言語設定を復元
        configWrap.setLanguage(backupLanguage);

        // 設定変更通知
        AbstractManager.callNotifyUpdateConfig(CFG_KEY_INITIALIZE);
    }

    private boolean readingConfigFile() {
        String path = Utility.pathCombin(JMPCore.getSystemManager().getSavePath(), CONFIG_FILE);
        return database.reading(path);
    }

    private boolean readingHistoryFile() {
        boolean ret = true;
        String path = Utility.pathCombin(JMPCore.getSystemManager().getSavePath(), HISTORY_FILE);
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
        String path = Utility.pathCombin(JMPCore.getSystemManager().getSavePath(), CONFIG_FILE);
        return database.output(path);
    }

    private boolean outputHistoryFile() {
        boolean ret = true;
        String path = Utility.pathCombin(JMPCore.getSystemManager().getSavePath(), HISTORY_FILE);

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
        if (key.equals(CFG_KEY_INITIALIZE) == false) {
            database.setConfigParam(key, value);
        }

        // 設定変更通知
        AbstractManager.callNotifyUpdateConfig(key);
    }

    @Override
    public String getConfigParam(String key) {
        return database.getConfigParam(key);
    }

    @Override
    public String[] getKeySet() {
        return database.getKeySet();
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

    public void addConvertedFile(File file) {
        convertedFiles.add(file);
    }

    @Override
    public void setFFmpegPath(String filePath) {
        IJmpConfigDatabase.super.setFFmpegPath(filePath);
    }

    @Override
    public boolean isUseFFmpegPlayer() {
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            return false;
        }
        return IJmpConfigDatabase.super.isUseFFmpegPlayer();
    }
}
