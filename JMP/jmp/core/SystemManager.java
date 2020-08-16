package jmp.core;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.UIManager;

import function.Platform;
import function.Utility;
import jlib.core.ISystemManager;
import jlib.plugin.IPlugin;
import jlib.util.IUtilityToolkit;
import jmp.CommonRegister;
import jmp.JMPFlags;
import jmp.JMPLoader;
import jmp.midi.toolkit.MidiToolkitManager;
import jmp.util.JmpUtil;
import jmp.util.toolkit.DefaultUtilityToolkit;
import jmp.util.toolkit.UtilityToolkitManager;
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
    public static final String ZIP_DIR_NAME = "jmz";

    /** Outputディレクトリ名 */
    public static final String OUTPUT_DIR_NAME = "output";

    /** saveディレクトリ名 */
    public static final String SAVE_DIR_NAME = "save";

    /** デフォルトMidiToolkit名 */
    public static final String USE_MIDI_TOOLKIT_CLASSNAME = MidiToolkitManager.DEFAULT_MIDI_TOOLKIT_NAME;

    /** デフォルトUtilToolkit名 */
    public static final String USE_UTIL_TOOLKIT_CLASSNAME = UtilityToolkitManager.DEFAULT_UTIL_TOOLKIT_NAME;

    /** デフォルトプレイヤーカラー */
    public static final Color DEFAULT_PLAYER_BACK_COLOR = Color.DARK_GRAY;

    public static final String COMMON_REGKEY_CH_COLOR_FORMAT = "ch_color_%d";
    public static final String COMMON_REGKEY_PLAYER_BACK_COLOR = "player_back_color";
    public static final String COMMON_REGKEY_EXTENSION_MIDI = "extension_midi";
    public static final String COMMON_REGKEY_EXTENSION_WAV = "extension_wav";
    public static final String COMMON_REGKEY_EXTENSION_MUSICXML = "extension_musicxml";
    public static final String COMMON_REGKEY_USE_MIDI_TOOLKIT = "use_midi_toolkit";
    public static final String COMMON_REGKEY_USE_UTIL_TOOLKIT = "use_util_toolkit";

    /** デバッグモード */
    public static final String COMMON_REGKEY_DEBUGMODE = "debug_mode";

    /** 共通レジスタ */
    private CommonRegister cReg = null;

    /** FFmpeg wrapper インスタンス */
    private FFmpegWrapper ffmpegWrapper = null;

    /** Utilityツールキット */
    private IUtilityToolkit utilToolkit = null;

    // システムパス変数
    private String dataFileLocationPath = "";
    private String resFileLocationPath = "";
    private String pluginsDirPath = "";
    private String jmsDirPath = "";
    private String jarDirPath = "";
    private String zipDirPath = "";
    private String outputPath = "";
    private String savePath = "";
    private String activateFileLocationPath = "";

    SystemManager(int pri) {
        super(pri, "system");
    }

    protected boolean initFunc() {
        TempResisterEx = null;

        utilToolkit = new DefaultUtilityToolkit();

        // 共通レジスタのインスタンス生成
        cReg = new CommonRegister();
        cReg.add(COMMON_REGKEY_EXTENSION_MIDI, JmpUtil.genExtensions2Str("mid", "midi"));
        cReg.add(COMMON_REGKEY_EXTENSION_WAV, JmpUtil.genExtensions2Str("wav"));
        cReg.add(COMMON_REGKEY_EXTENSION_MUSICXML, JmpUtil.genExtensions2Str("xml", "musicxml", "mxl"));
        cReg.add(COMMON_REGKEY_USE_MIDI_TOOLKIT, USE_MIDI_TOOLKIT_CLASSNAME);
        cReg.add(COMMON_REGKEY_USE_UTIL_TOOLKIT, USE_UTIL_TOOLKIT_CLASSNAME);
        cReg.add(COMMON_REGKEY_PLAYER_BACK_COLOR, Utility.convertHtmlColorToCode(DEFAULT_PLAYER_BACK_COLOR));
        cReg.add(COMMON_REGKEY_DEBUGMODE, JMPFlags.DebugMode ? "TRUE" : "FALSE");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 1), "#8ec21f");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 2), "#3dc21f");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 3), "#1fc253");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 4), "#1fc2a4");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 5), "#1f8ec2");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 6), "#1f3dc2");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 7), "#531fc2");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 8), "#a41fc2");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 9), "#ffc0cb");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 10), "#c21f3d");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 11), "#c2531f");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 12), "#c2a41f");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 13), "#3d00c2");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 14), "#ffff29");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 15), "#bbff29");
        cReg.add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 16), "#f98608");

        // ResourceとcRegの同期
        setCommonRegisterValue(COMMON_REGKEY_PLAYER_BACK_COLOR, Utility.convertHtmlColorToCode(JMPCore.getResourceManager().getAppBackgroundColor()));

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
        if (JMPLoader.UseHistoryFile == true || JMPLoader.UseConfigFile == true) {
            File saveDir = new File(getSavePath());
            if (saveDir.exists() == false) {
                // saveフォルダ作成
                saveDir.mkdirs();
            }
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
            File zipDir = new File(getZipDirPath());
            if (zipDir.exists() == false) {
                zipDir.mkdirs();
            }
        }

        // アクティベート処理
        preActivate();

        return super.initFunc();
    }

    protected boolean endFunc() {
        super.endFunc();

        /* アクティベート処理 */
        postActivate();
        return true;
    }

    public void updateUtilToolkit() {
        // 使用するツールキットを更新
        SystemManager system = JMPCore.getSystemManager();
        String toolkitName = system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_USE_UTIL_TOOLKIT);
        utilToolkit = UtilityToolkitManager.getInstance().getUtilityToolkit(toolkitName);
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

    /** アクティベート前処理 */
    private void preActivate() {
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

    /** アクティベート後処理 */
    private void postActivate() {
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
                    String text = "ライセンス認証のためのファイルです。" + Platform.getNewLine()//
                            + "このファイルを削除してもソフトウェアの動作には影響ありません。" + Platform.getNewLine()//
                            + Platform.getNewLine()//
                            + "File for license authentication." + Platform.getNewLine()//
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
    }

    public boolean setCommonRegisterValue(String key, String value) {
        if (cReg == null) {
            return false;
        }
        if (JMPFlags.DebugMode == false) {
            // デバッグモード時のみ設定可能
            return false;
        }

        if (key.equalsIgnoreCase(COMMON_REGKEY_DEBUGMODE) == true) {
            // Readonryのキー
            return true;
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

    private boolean makeSystemPathFlag = false;
    public void makeSystemPath() {
        if (makeSystemPathFlag == true) {
            // 多重呼び出し禁止
            return;
        }
        makeSystemPathFlag = true;

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

        // セーブデータ格納ディレクトリパス
        savePath = Utility.stringsCombin(Platform.getCurrentPath(), SAVE_DIR_NAME);

        // アクティベートファイルパス
        activateFileLocationPath = Utility.stringsCombin(savePath, Platform.getSeparator(), "activate");

        JMPFlags.Log.cprintln("###");
        JMPFlags.Log.cprintln("## Directory list");
        JMPFlags.Log.cprintln("##");
        JMPFlags.Log.cprintln(pluginsDirPath);
        JMPFlags.Log.cprintln(pluginsDirPath);
        JMPFlags.Log.cprintln(dataFileLocationPath);
        JMPFlags.Log.cprintln(resFileLocationPath);
        JMPFlags.Log.cprintln(jmsDirPath);
        JMPFlags.Log.cprintln(jarDirPath);
        JMPFlags.Log.cprintln(zipDirPath);
        JMPFlags.Log.cprintln(outputPath);
        JMPFlags.Log.cprintln(savePath);
        JMPFlags.Log.cprintln(activateFileLocationPath);
        JMPFlags.Log.cprintln("##");
        JMPFlags.Log.cprintln();
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

    public String getSavePath() {
        return savePath;
    }

    /**
     * ルックアンドフィールの設定
     */
    private void setupLookAndFeel() {
        String lf = UIManager.getSystemLookAndFeelClassName();
        // String lf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        // String lf = "javax.swing.plaf.metal.MetalLookAndFeel";
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

    @Override
    public String getPluginName(IPlugin plugin) {
        // プラグインマネージャーに問い合わせ
        return JMPCore.getPluginManager().getPluginName(plugin);
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

    @Override
    public IUtilityToolkit getUtilityToolkit() {
        return utilToolkit;
    }

    @Override
    protected void notifyUpdateCommonRegister(String key) {
        super.notifyUpdateCommonRegister(key);
        if (key.equals(SystemManager.COMMON_REGKEY_USE_UTIL_TOOLKIT) == true) {
            updateUtilToolkit();
        }
    }

    @Override
    protected void notifyUpdateConfig(String key) {
        super.notifyUpdateConfig(key);

        DataManager dm = JMPCore.getDataManager();
        if (JmpUtil.checkConfigKey(key, DataManager.CFG_KEY_FFMPEG_PATH) == true) {
            String val = dm.getConfigParam(DataManager.CFG_KEY_FFMPEG_PATH);
            setFFmpegWrapperPath(val);
        }
    }
}
