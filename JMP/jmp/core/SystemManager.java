package jmp.core;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import function.Platform;
import function.Utility;
import jlib.core.ISystemManager;
import jlib.gui.IJmpMainWindow;
import jlib.plugin.IPlugin;
import jmp.JMPFlags;
import jmp.JMPLoader;
import jmp.lang.DefineLanguage.LangID;
import jmp.midi.toolkit.DefaultMidiToolkit;
import jmp.task.ICallbackFunction;
import wffmpeg.FFmpegWrapper;
import wrapper.IProcessingCallback;
import wrapper.ProcessingFFmpegWrapper;

/**
 * システム管理クラス
 *
 * @author abs
 *
 */
public class SystemManager extends AbstractManager implements ISystemManager {

    /** 例外メッセージのテンポラリ */
    public static Exception TempResisterEx = null;

    /** データディレクトリ名 */
    public static final String PLUGINS_DIR_NAME = "plugins";

    /** データディレクトリ名 */
    public static final String DATA_DIR_NAME = "data";

    /** リソースディレクトリ名 */
    public static final String RES_DIR_NAME = "res";

    /** JMSディレクトリ名 */
    public static final String JMS_DIR_NAME = "jms";

    /** JARディレクトリ名 */
    public static final String JAR_DIR_NAME = "jar";

    /** ZIPパッケージディレクトリ名 */
    public static final String ZIP_DIR_NAME = "zip";

    /** Outputディレクトリ名 */
    public static final String OUTPUT_DIR_NAME = "output";

    /** デフォルトプレイヤーカラー */
    public static final Color DEFAULT_PLAYER_BACK_COLOR = Color.DARK_GRAY;

    /** メインウィンドウ */
    public IJmpMainWindow mainWindow = null;

    /** 共通レジスタ */
    private CommonRegister cReg = null;

    /** デフォルトMidiToolkit名 */
    public static final String USE_MIDI_TOOLKIT_CLASSNAME = DefaultMidiToolkit.class.getSimpleName();

    /** FFmpeg wrapper インスタンス */
    private FFmpegWrapper ffmpegWrapper = null;

    // システムパス変数
    private String dataFileLocationPath = "";
    private String resFileLocationPath = "";
    private String pluginsDirPath = "";
    private String jmsDirPath = "";
    private String jarDirPath = "";
    private String zipDirPath = "";
    private String outputPath = "";
    private String activateFileLocationPath = "";

    public class CommonRegisterINI {
        public String key, value;
        public CommonRegisterINI(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static String genExtensions2Str(String... ex) {
        String ret = "";
        for (int i = 0; i < ex.length; i++) {
            if (i > 0) {
                ret += ",";
            }
            ret += ex[i];
        }
        return ret;
    }
    public static String[] genStr2Extensions(String str) {
        String[] ret = str.split(",");
        return ret;
    }

    public class CommonRegister {

        public static final String COMMON_REGKEY_CH_COLOR_FORMAT = "ch_color_%d";

        public static final String COMMON_REGKEY_PLAYER_BACK_COLOR = "player_back_color";
        public static final String COMMON_REGKEY_EXTENSION_MIDI = "extension_midi";
        public static final String COMMON_REGKEY_EXTENSION_WAV = "extension_wav";
        public static final String COMMON_REGKEY_EXTENSION_MUSICXML = "extension_musicxml";
        public static final String COMMON_REGKEY_USE_MIDI_TOOLKIT = "use_midi_toolkit";

        private String[] cRegKeySet = null;
        private List<CommonRegisterINI> iniList = new ArrayList<CommonRegisterINI>() {
            {
                add(new CommonRegisterINI(COMMON_REGKEY_EXTENSION_MIDI, genExtensions2Str(DataManager.ExtentionForMIDI)));
                add(new CommonRegisterINI(COMMON_REGKEY_EXTENSION_WAV, genExtensions2Str(DataManager.ExtentionForWAV)));
                add(new CommonRegisterINI(COMMON_REGKEY_EXTENSION_MUSICXML, genExtensions2Str(DataManager.ExtentionForMusicXML)));
                add(new CommonRegisterINI(COMMON_REGKEY_USE_MIDI_TOOLKIT, USE_MIDI_TOOLKIT_CLASSNAME));
                add(new CommonRegisterINI(COMMON_REGKEY_PLAYER_BACK_COLOR, Utility.convertHtmlColorToCode(DEFAULT_PLAYER_BACK_COLOR)));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 1), "#8ec21f"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 2), "#3dc21f"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 3), "#1fc253"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 4), "#1fc2a4"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 5), "#1f8ec2"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 6), "#1f3dc2"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 7), "#531fc2"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 8), "#a41fc2"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 9), "#ffc0cb"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 10), "#c21f3d"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 11), "#c2531f"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 12), "#c2a41f"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 13), "#3d00c2"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 14), "#ffff29"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 15), "#bbff29"));
                add(new CommonRegisterINI(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 16), "#f98608"));
            }
        };

        private HashMap<String, String> map;

        public CommonRegister() {
            map = new HashMap<String, String>();
        }

        public void load() {
        }

        public void init() {
            cRegKeySet = new String[iniList.size()];
            for (int i=0; i<iniList.size(); i++) {
                CommonRegisterINI cri = iniList.get(i);
                add(cri.key, cri.value);

                // キーセットを作成
                cRegKeySet[i] = cri.key;
            }
        }

        public void add(String key, String value) {
            map.put(key, value);
        }

        public boolean setValue(String key, String value) {
            if (map.containsKey(key) == true) {
                map.put(key, value);
                return true;
            }
            return false;
        }

        public String getValue(String key) {
            if (map.containsKey(key) == true) {
                return map.get(key);
            }
            return "";
        }

        public String[] getKeySet() {
            return cRegKeySet;
        }
    }

    SystemManager(int pri) {
        super(pri, "system");
    }

    protected boolean initFunc() {
        boolean result = true;
        TempResisterEx = null;

        // 共通レジスタのインスタンス生成
        cReg = new CommonRegister();
        cReg.init();
        cReg.load();

        // ResourceとcRegの同期
        setCommonRegisterValue(CommonRegister.COMMON_REGKEY_PLAYER_BACK_COLOR,
                Utility.convertHtmlColorToCode(JMPCore.getResourceManager().getAppBackgroundColor()));

        // ルックアンドフィールの設定
        setupLookAndFeel();

        // FFmpegWrapperインスタンス生成
        ffmpegWrapper = new ProcessingFFmpegWrapper();
        ffmpegWrapper.setOverwrite(true);

        if (JMPLoader.UsePluginDirectory == true) {
            File pluginsDir = new File(getPluginsDirPath());
            if (pluginsDir.exists() == false) {
                // プラグインフォルダ作成
                pluginsDir.mkdirs();
            }
        }
        File dataDir = new File(getDataFileLocationPath());
        if (dataDir.exists() == false) {
            // データフォルダ作成
            dataDir.mkdirs();
        }
        File resDir = new File(getResFileLocationPath());
        if (resDir.exists() == false) {
            // resフォルダ作成
            resDir.mkdirs();
        }
        if (JMPLoader.UsePluginDirectory == true) {
            File jarDir = new File(getJarDirPath());
            if (jarDir.exists() == false) {
                // Jarフォルダ作成
                jarDir.mkdirs();
            }
            File jmsDir = new File(getJmsDirPath());
            if (jmsDir.exists() == false) {
                // jmsフォルダ作成
                jmsDir.mkdirs();
            }
            File outDir = new File(getOutputPath());
            if (outDir.exists() == false) {
                // outputフォルダ作成
                outDir.mkdirs();
            }
        }

        // アクティベート処理
        executeActivate();

        if (initializeFlag == false) {
            initializeFlag = true;
        }
        return result;
    }

    protected boolean endFunc() {
        /* アクティベート処理 */
        boolean activateOutFlag = false;
        if (JMPFlags.ActivateFlag == true) {
            activateOutFlag = true;
        }
        if (JMPFlags.DebugMode == true || JMPCore.isEnableStandAlonePlugin() == true || JMPFlags.LibraryMode == true) {
            // デバッグ・スタンドアロン実行は発行しない
            activateOutFlag = false;
        }
        // ライセンス発行
        if (activateOutFlag == true) {
            if (Utility.isExsistFile(getActivateFileLocationPath()) == false) {
                try {
                    String text = "ライセンス認証のためのファイルです。" + Platform.getNewLine() + "このファイルを削除してもソフトウェアの動作には影響ありません。" + Platform.getNewLine()
                            + Platform.getNewLine() + "File for license authentication." + Platform.getNewLine()
                            + "Deleting this file does not affect the operation of the software.";

                    Utility.outputTextFile(getActivateFileLocationPath(), text);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @Override
    public boolean isEnableStandAlonePlugin() {
        return JMPCore.isEnableStandAlonePlugin();
    }

    public void initializeAllSetting() {
        // 全てのWindowを閉じる
        JMPCore.getWindowManager().setVisibleAll(false);
        JMPCore.getPluginManager().closeAllPlugins();

        JMPCore.getDataManager().initializeConfigDatabase();
        // JMPCore.getDataManager().clearHistory();
        // JMPCore.getSoundManager().clearPlayList();
        JMPCore.getWindowManager().initializeLayout();

        // 言語更新
        JMPCore.getWindowManager().updateLanguage();

        // 設定ファイルのFFmpegパスを同期
        setFFmpegWrapperPath(JMPCore.getDataManager().getFFmpegPath());
    }

    public void executeActivate() {
        /* アクティベート状況の確認 */
        if (Utility.isExsistFile(getActivateFileLocationPath()) == true) {
            JMPFlags.ActivateFlag = true;
        }
        else {
            JMPFlags.ActivateFlag = false;
        }

        /* スタンドアロンモード or デバッグモード or ライブラリモードの際はアクティベートする */
        if (JMPFlags.DebugMode == true || JMPCore.isEnableStandAlonePlugin() == true || JMPFlags.LibraryMode == true) {
            JMPFlags.ActivateFlag = true;
        }
    }

    public boolean setCommonRegisterValue(String key, String value) {
        if (cReg == null) {
            return false;
        }

        boolean ret = cReg.setValue(key, value);
        if (ret == true) {
            // 全てのマネージャーに通知
            AbstractManager.callNotifyUpdateCommonRegister(key);
        }
        return ret;
    }

    public String getCommonRegisterValue(String key) {
        if (cReg == null) {
            return "";
        }
        return cReg.getValue(key);
    }

    public String[] getCommonKeySet() {
        return cReg.getKeySet();
    }

    public void makeSystemPath() {

        // プラグイン格納ディレクトリパス
        pluginsDirPath = Utility.stringsCombin(Platform.getCurrentPath(), PLUGINS_DIR_NAME);

        // データファイル格納ディレクトリパス
        if (JMPLoader.UsePluginDirectory == true) {
            dataFileLocationPath = Utility.stringsCombin(pluginsDirPath, Platform.getSeparator(), DATA_DIR_NAME);
        }
        else {
            dataFileLocationPath = Utility.stringsCombin(Platform.getCurrentPath(), DATA_DIR_NAME);
        }

        // リソースファイル格納ディレクトリパス
        if (JMPLoader.UsePluginDirectory == true) {
            resFileLocationPath = Utility.stringsCombin(pluginsDirPath, Platform.getSeparator(), RES_DIR_NAME);
        }
        else {
            resFileLocationPath = Utility.stringsCombin(Platform.getCurrentPath(), RES_DIR_NAME);
        }

        // プラグインjms格納ディレクトリパス
        jmsDirPath = Utility.stringsCombin(pluginsDirPath, Platform.getSeparator(), JMS_DIR_NAME);

        // プラグインjar格納ディレクトリパス
        jarDirPath = Utility.stringsCombin(pluginsDirPath, Platform.getSeparator(), JAR_DIR_NAME);

        // zip格納ディレクトリパス
        zipDirPath = Utility.stringsCombin(Platform.getCurrentPath(), ZIP_DIR_NAME);

        // 出力ファイル格納ディレクトリパス
        outputPath = Utility.stringsCombin(Platform.getCurrentPath(), OUTPUT_DIR_NAME);

        // アクティベートファイルパス
        activateFileLocationPath = Utility.stringsCombin(Platform.getCurrentPath(), Platform.getSeparator(), "activate");
    }

    /**
     * データファイル格納ディレクトリパス
     *
     * @return パス
     */
    public String getDataFileLocationPath() {
        return dataFileLocationPath;
    }
    @Override
    public String getDataFileLocationPath(IPlugin plugin) {
        String path = getDataFileLocationPath();
        if (JMPLoader.UsePluginDirectory == true) {
            path += (Platform.getSeparator() + getPluginName(plugin));
        }
        return path;
    }

    /**
     * リソースファイル格納ディレクトリパス
     *
     * @return
     */
    public String getResFileLocationPath() {
        return resFileLocationPath;
    }
    @Override
    public String getResFileLocationPath(IPlugin plugin) {
        String path = getResFileLocationPath();
        if (JMPLoader.UsePluginDirectory == true) {
            path += (Platform.getSeparator() + getPluginName(plugin));
        }
        return path;
    }

    public String getPluginsDirPath() {
        return pluginsDirPath;
    }

    public String getJmsDirPath() {
        return jmsDirPath;
    }

    public String getJarDirPath() {
        return jarDirPath;
    }

    public String getZipDirPath() {
        return zipDirPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    /**
     * アクティベート状態ファイル
     *
     * @return
     */
    public String getActivateFileLocationPath() {
        return activateFileLocationPath;
    }

    /**
     * ルックアンドフィールの設定
     */
    private void setupLookAndFeel() {
        String lf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lf);
        }
        catch (Exception e) {
            System.out.println("lferror");

            // 念のためMetalを再設定
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }
            catch (Exception e2) {
            }
        }
    }

    /**
     * メインウィンドウ取得
     *
     * @return メインウィンドウ
     */
    public IJmpMainWindow getMainWindow() {
        return mainWindow;
    }

    /**
     * メインウィンドウ登録
     *
     * @param mainWindow
     *            メインウィンドウ
     * @return 結果
     */
    public boolean registerMainWindow(IJmpMainWindow mainWindow) {
        boolean ret = false;
        if (this.mainWindow == null) {
            // 未登録時のみ
            this.mainWindow = mainWindow;
            ret = true;
        }
        return ret;
    }

    @Override
    public String getPluginName(IPlugin plugin) {
        // プラグインマネージャーに問い合わせ
        return JMPCore.getPluginManager().getPluginName(plugin);
    }

    public void showErrorMessageDialogSync(String message) {
        IJmpMainWindow win = getMainWindow();
        Component parent = null;
        if (win instanceof Component) {
            parent = (Component) win;
        }
        else {
            parent = null;
        }

        JOptionPane.showMessageDialog(parent, message, JMPCore.getLanguageManager().getLanguageStr(LangID.Error), JOptionPane.ERROR_MESSAGE);
        SystemManager.TempResisterEx = null;
    }

    public void showErrorMessageDialog(String message) {
        if (SystemManager.TempResisterEx != null) {
            String stackTrace = function.Error.getMsg(SystemManager.TempResisterEx);
            showMessageDialog(Utility.stringsCombin(message, Platform.getNewLine(), Platform.getNewLine(), stackTrace),
                    JMPCore.getLanguageManager().getLanguageStr(LangID.Error), JOptionPane.ERROR_MESSAGE);
        }
        else {
            showMessageDialog(message, JMPCore.getLanguageManager().getLanguageStr(LangID.Error), JOptionPane.ERROR_MESSAGE);
        }
        SystemManager.TempResisterEx = null;
    }

    public void showInformationMessageDialog(String message) {
        showMessageDialog(message, JMPCore.getLanguageManager().getLanguageStr(LangID.Message), JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMessageDialog(String message, int option) {
        showMessageDialog(message, JMPCore.getLanguageManager().getLanguageStr(LangID.Message), option);
    }

    public void showMessageDialog(String message, String title, int option) {
        JMPCore.getTaskManager().getTaskOfSequence().queuing(new ICallbackFunction() {
            @Override
            public void callback() {
                IJmpMainWindow win = getMainWindow();
                Component parent = null;
                if (win instanceof Component) {
                    parent = (Component) win;
                }
                else {
                    parent = null;
                }
                JOptionPane.showMessageDialog(parent, message, title, option);
            }
        });
    }

    public void executeBatFile(String path) {
        try {
            if (Utility.isExsistFile(path) == true) {
                Utility.invokeProcess(path);
            }
        }
        catch (Exception e1) {
        }
    }

    public void executeConvert(String inPath, String outPath) throws IOException {
        ffmpegWrapper.convert(inPath, outPath);

        JMPCore.getDataManager().addConvertedFile(new File(outPath));
    }

    public void setFFmpegWrapperCallback(IProcessingCallback cb) {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            pfw.setCallback(cb);
        }
    }

    public void setFFmpegWrapperWaitFor(boolean waitFor) {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            pfw.setWaitFor(waitFor);
        }
    }

    public boolean isFFmpegWrapperWaitFor() {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            return pfw.isWaitFor();
        }
        return false;
    }

    public boolean isValidFFmpegWrapper() {
        return ffmpegWrapper.isValid();
    }

    public void setFFmpegWrapperPath(String path) {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            pfw.setPath(path);
        }
    }

    public String getFFmpegWrapperPath() {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            return pfw.getPath();
        }
        return "";
    }
}
