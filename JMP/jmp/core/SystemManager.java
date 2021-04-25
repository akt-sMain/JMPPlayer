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
import jmp.lang.DefineLanguage;
import jmp.midi.toolkit.MidiToolkitManager;
import jmp.util.JmpUtil;
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

    /** sysファイル */
    public static final String COMMON_SYS_FILENAME = "syscommon";

    /** skinフォルダ名 */
    public static final String SKIN_FOLDER_NAME = "skin";

    /** デフォルトMidiToolkit名 */
    public static final String USE_MIDI_TOOLKIT_CLASSNAME = MidiToolkitManager.DEFAULT_MIDI_TOOLKIT_NAME;

    /** デフォルトUtilToolkit名 */
    public static final String USE_UTIL_TOOLKIT_CLASSNAME = UtilityToolkitManager.DEFAULT_UTIL_TOOLKIT_NAME;

    /** デフォルトプレイヤーカラー */
    public static final Color DEFAULT_PLAYER_BACK_COLOR = Color.DARK_GRAY;

    public static final String COMMON_REGKEY_CH_COLOR_FORMAT = "ch_color_%d";

    /** 共通レジスタ */
    private CommonRegister cReg = null;
    private String[] cRegKeys = null;

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
    private String syscommonPath = "";
    private String skinPath = "";

    public static void consoleOutSystemInfo() {
        JMPFlags.Log.cprintln("Date : " + Utility.getCurrentTimeStr());
        JMPFlags.Log.cprintln("Java : " + Platform.getJavaVersion());
        JMPFlags.Log.cprintln("OS   : " + Platform.getOSName());
        JMPFlags.Log.cprintln("SLaF : " + UIManager.getSystemLookAndFeelClassName());
        JMPFlags.Log.cprintln("CLaF : " + UIManager.getCrossPlatformLookAndFeelClassName());
        JMPFlags.Log.cprintln();
    }

    SystemManager(int pri) {
        super(pri, "system");
    }

    protected boolean initFunc() {
        TempResisterEx = null;

        utilToolkit = UtilityToolkitManager.getInstance().getUtilityToolkit(UtilityToolkitManager.DEFAULT_UTIL_TOOLKIT_NAME);

        // FFmpegWrapperインスタンス生成
        ffmpegWrapper = new ProcessingFFmpegWrapper();
        ffmpegWrapper.setOverwrite(true);

        // 共通レジスタのキー名登録
        cRegKeys = new String[NUMBER_OF_COMMON_REGKEY];
        cRegKeys[COMMON_REGKEY_NO_EXTENSION_MIDI] = "extension_midi";
        cRegKeys[COMMON_REGKEY_NO_EXTENSION_WAV] = "extension_wav";
        cRegKeys[COMMON_REGKEY_NO_EXTENSION_MUSICXML] = "extension_musicxml";
        cRegKeys[COMMON_REGKEY_NO_EXTENSION_MUSIC] = "extension_music";
        cRegKeys[COMMON_REGKEY_NO_EXTENSION_MML] = "extension_mml";
        cRegKeys[COMMON_REGKEY_NO_USE_MIDI_TOOLKIT] = "use_midi_toolkit";
        cRegKeys[COMMON_REGKEY_NO_USE_UTIL_TOOLKIT] = "use_util_toolkit";
        cRegKeys[COMMON_REGKEY_NO_PLAYER_BACK_COLOR] = "player_back_color";
        cRegKeys[COMMON_REGKEY_NO_DEBUGMODE] = "debug_mode";
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT] = "ch_color_format";
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_1] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 1);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_2] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 2);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_3] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 3);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_4] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 4);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_5] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 5);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_6] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 6);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_7] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 7);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_8] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 8);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_9] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 9);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_10] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 10);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_11] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 11);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_12] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 12);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_13] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 13);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_14] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 14);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_15] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 15);
        cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_16] = String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 16);
        cRegKeys[COMMON_REGKEY_NO_FFMPEG_OUTPUT] = "ffmpeg_output";
        cRegKeys[COMMON_REGKEY_NO_FFMPEG_WIN] = "ffmpeg_env_win";
        cRegKeys[COMMON_REGKEY_NO_FFMPEG_MAC] = "ffmpeg_env_mac";
        cRegKeys[COMMON_REGKEY_NO_FFMPEG_OTHER] = "ffmpeg_env_other";
        cRegKeys[COMMON_REGKEY_NO_LYRIC_CHARCODE] = "lyric_charcode";

        // 共通レジスタのインスタンス生成・パラメータ登録
        cReg = new CommonRegister();
        cReg.add(cRegKeys[COMMON_REGKEY_NO_EXTENSION_MIDI], JmpUtil.genExtensions2Str("mid", "midi"));
        cReg.add(cRegKeys[COMMON_REGKEY_NO_EXTENSION_WAV], JmpUtil.genExtensions2Str("wav"));
        cReg.add(cRegKeys[COMMON_REGKEY_NO_EXTENSION_MUSICXML], JmpUtil.genExtensions2Str("xml", "musicxml", "mxl"));
        cReg.add(cRegKeys[COMMON_REGKEY_NO_EXTENSION_MUSIC], JmpUtil.genExtensions2Str("mp3", "mp4", "aac", "m4a"));
        cReg.add(cRegKeys[COMMON_REGKEY_NO_EXTENSION_MML], JmpUtil.genExtensions2Str("mml"));
        cReg.add(cRegKeys[COMMON_REGKEY_NO_USE_MIDI_TOOLKIT], USE_MIDI_TOOLKIT_CLASSNAME);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_USE_UTIL_TOOLKIT], USE_UTIL_TOOLKIT_CLASSNAME);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_PLAYER_BACK_COLOR], Utility.convertHtmlColorToCode(DEFAULT_PLAYER_BACK_COLOR));
        cReg.add(cRegKeys[COMMON_REGKEY_NO_DEBUGMODE], JMPFlags.DebugMode ? "TRUE" : "FALSE");
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT], COMMON_REGKEY_CH_COLOR_FORMAT);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_1], "#8ec21f", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_2], "#3dc21f", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_3], "#1fc253", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_4], "#1fc2a4", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_5], "#1f8ec2", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_6], "#1f3dc2", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_7], "#531fc2", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_8], "#a41fc2", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_9], "#ffc0cb", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_10], "#c21f3d", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_11], "#c2531f", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_12], "#c2a41f", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_13], "#3d00c2", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_14], "#ffff29", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_15], "#bbff29", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_16], "#f98608", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_FFMPEG_OUTPUT], "output");
        cReg.add(cRegKeys[COMMON_REGKEY_NO_FFMPEG_WIN], "ffmpeg", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_FFMPEG_MAC], "/Usr/local/bin/ffmpeg", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_FFMPEG_OTHER], "/usr/local/bin/ffmpeg", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_LYRIC_CHARCODE], "SJIS", true);

        // syscommon読み込み
        cReg.read(getSyscommonPath());

        // OSごとのFFmpeg設定
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            String ffmpegCommand = "ffmpeg";
            switch (Platform.getRunPlatform()) {
                case WINDOWS:
                    ffmpegCommand = getCommonRegisterValue(COMMON_REGKEY_NO_FFMPEG_WIN);
                    break;
                case MAC:
                    ffmpegCommand = getCommonRegisterValue(COMMON_REGKEY_NO_FFMPEG_MAC);
                    break;
                case LINUX:
                case SUN_OS:
                case OTHER:
                default:
                    ffmpegCommand = getCommonRegisterValue(COMMON_REGKEY_NO_FFMPEG_OTHER);
                    break;
            }
            ((ProcessingFFmpegWrapper) ffmpegWrapper).setFFmpegCommand(ffmpegCommand);
        }

        updateUtilToolkit();

        // ResourceとcRegの同期
        setCommonRegisterValue(COMMON_REGKEY_NO_PLAYER_BACK_COLOR, Utility.convertHtmlColorToCode(JMPCore.getResourceManager().getAppBackgroundColor()));

        // ルックアンドフィールの設定
        setupLookAndFeel();

        if (JMPLoader.UsePluginDirectory == true) {
            File pluginsDir = new File(getPluginsDirPath());
            if (pluginsDir.exists() == false) {
                // プラグインフォルダ作成
                if(!pluginsDir.mkdir()) {
                    return false;
                }
            }
        }
        File dataDir = new File(getDataFileLocationPath());
        if (dataDir.exists() == false) {
            // データフォルダ作成
            if (!dataDir.mkdir()) {
                return false;
            }
        }
        File resDir = new File(getResFileLocationPath());
        if (resDir.exists() == false) {
            // resフォルダ作成
            if (!resDir.mkdir()) {

            }
        }
        if (JMPLoader.UseHistoryFile == true || JMPLoader.UseConfigFile == true) {
            File saveDir = new File(getSavePath());
            if (saveDir.exists() == false) {
                // saveフォルダ作成
                if (!saveDir.mkdir()) {
                    return false;
                }
            }
        }
        if (JMPLoader.UsePluginDirectory == true) {
            File jarDir = new File(getJarDirPath());
            if (jarDir.exists() == false) {
                // Jarフォルダ作成
                if (!jarDir.mkdir()) {
                    return false;
                }
            }
            File jmsDir = new File(getJmsDirPath());
            if (jmsDir.exists() == false) {
                // jmsフォルダ作成
                if (!jmsDir.mkdir()) {
                    return false;
                }
            }
            File outDir = new File(getOutputPath());
            if (outDir.exists() == false) {
                // outputフォルダ作成
                if (!outDir.mkdir()) {
                    return false;
                }
            }
            File zipDir = new File(getZipDirPath());
            if (zipDir.exists() == false) {
                if (!zipDir.mkdir()) {
                    return false;
                }
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

        // syscommon dump
        if (Utility.isExsistFile(getSavePath()) == true) {
            cReg.write(getSyscommonPath());
        }
        return true;
    }

    public void updateUtilToolkit() {
        // 使用するツールキットを更新
        SystemManager system = JMPCore.getSystemManager();
        String toolkitName = system.getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_USE_UTIL_TOOLKIT);
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
        JMPCore.getDataManager().clearHistory();
        JMPCore.getSoundManager().clearPlayList();
        JMPCore.getWindowManager().initializeLayout();

        // 言語更新
        JMPCore.getWindowManager().updateLanguage();

        // 設定ファイルのFFmpegパスを同期
        setFFmpegWrapperPath(JMPCore.getDataManager().getFFmpegPath());
        setFFmpegInstalled(JMPCore.getDataManager().isFFmpegInstalled());
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

        if (key.equalsIgnoreCase(getCommonRegisterKeyName(COMMON_REGKEY_NO_DEBUGMODE)) == false) {
            // デバッグモードの切り替えのみ許可
            if (JMPFlags.DebugMode == false) {
                // デバッグモード時のみ設定可能
                return false;
            }
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

        String currentPath = Platform.getCurrentPath(false);

        // プラグイン格納ディレクトリパス
        pluginsDirPath = Utility.pathCombin(currentPath, PLUGINS_DIR_NAME);

        // データファイル格納ディレクトリパス
        if (JMPLoader.UsePluginDirectory == true) {
            dataFileLocationPath = Utility.pathCombin(pluginsDirPath, DATA_DIR_NAME);
        }
        else {
            dataFileLocationPath = Utility.pathCombin(currentPath, DATA_DIR_NAME);
        }

        // リソースファイル格納ディレクトリパス
        if (JMPLoader.UsePluginDirectory == true) {
            resFileLocationPath = Utility.pathCombin(pluginsDirPath, RES_DIR_NAME);
        }
        else {
            resFileLocationPath = Utility.pathCombin(currentPath, RES_DIR_NAME);
        }

        // プラグインjms格納ディレクトリパス
        jmsDirPath = Utility.pathCombin(pluginsDirPath, JMS_DIR_NAME);

        // プラグインjar格納ディレクトリパス
        jarDirPath = Utility.pathCombin(pluginsDirPath, JAR_DIR_NAME);

        // zip格納ディレクトリパス
        zipDirPath = Utility.pathCombin(currentPath, ZIP_DIR_NAME);

        // 出力ファイル格納ディレクトリパス
        outputPath = Utility.pathCombin(currentPath, OUTPUT_DIR_NAME);

        // セーブデータ格納ディレクトリパス
        savePath = Utility.pathCombin(currentPath, SAVE_DIR_NAME);

        // アクティベートファイルパス
        activateFileLocationPath = Utility.pathCombin(savePath, "activate");

        // syscommon
        syscommonPath = Utility.pathCombin(savePath, COMMON_SYS_FILENAME);

        // skin
        skinPath = Utility.pathCombin(currentPath, SKIN_FOLDER_NAME);

        JMPFlags.Log.cprintln("###");
        JMPFlags.Log.cprintln("## Directory list");
        JMPFlags.Log.cprintln("##");
        JMPFlags.Log.cprintln(pluginsDirPath);
        JMPFlags.Log.cprintln(dataFileLocationPath);
        JMPFlags.Log.cprintln(resFileLocationPath);
        JMPFlags.Log.cprintln(jmsDirPath);
        JMPFlags.Log.cprintln(jarDirPath);
        JMPFlags.Log.cprintln(zipDirPath);
        JMPFlags.Log.cprintln(outputPath);
        JMPFlags.Log.cprintln(savePath);
        JMPFlags.Log.cprintln(activateFileLocationPath);
        JMPFlags.Log.cprintln(syscommonPath);
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

    public String getSyscommonPath() {
        return syscommonPath;
    }

    /**
     * ルックアンドフィールの設定
     */
    private void setupLookAndFeel() {
        final String systemlf = UIManager.getSystemLookAndFeelClassName();
        final String crosslf = UIManager.getCrossPlatformLookAndFeelClassName();
        // String lf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        // String lf = "javax.swing.plaf.metal.MetalLookAndFeel";
        try {
            UIManager.setLookAndFeel(systemlf);
        }
        catch (Exception e) {
            System.out.println("lferror");

            // 念のためMetalを再設定
            try {
                UIManager.setLookAndFeel(crosslf);
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
        if (key.equals(getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_USE_UTIL_TOOLKIT)) == true) {
            updateUtilToolkit();
        }
        if (key.equals(getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_DEBUGMODE)) == true) {
            JMPFlags.DebugMode = JmpUtil.toBoolean(getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_DEBUGMODE));
        }
    }

    @Override
    protected void notifyUpdateConfig(String key) {
        super.notifyUpdateConfig(key);

        DataManager dm = JMPCore.getDataManager();
        if (JmpUtil.checkConfigKey(key, DataManager.CFG_KEY_FFMPEG_PATH) == true) {
            setFFmpegWrapperPath(dm.getFFmpegPath());
        }
        if (JmpUtil.checkConfigKey(key, DataManager.CFG_KEY_FFMPEG_INSTALLED) == true) {
            setFFmpegInstalled(dm.isFFmpegInstalled());
        }
    }

    public boolean isFFmpegInstalled() {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            return pfw.isFFmpegInstalled();
        }
        return false;
    }

    public void setFFmpegInstalled(boolean isEnableEnvironmentVariable) {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            pfw.setFFmpegInstalled(isEnableEnvironmentVariable);
        }
    }

    public String getFFmpegCommand() {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            return pfw.getFFmpegCommand();
        }
        return "";
    }

    @Override
    public String getCommonRegisterKeyName(int keyNo) {
        if (NUMBER_OF_COMMON_REGKEY < keyNo || keyNo < 0) {
            return "";
        }
        if (cRegKeys == null) {
            return "";
        }
        return cRegKeys[keyNo];
    }

    public String getSkinPath() {
        return skinPath;
    }

    @Override
    public String getCurrentLanguageCode() {
        LanguageManager lm = JMPCore.getLanguageManager();
        DataManager dm = JMPCore.getDataManager();
        return lm.getLanguageCode(dm.getLanguage());
    }

    @Override
    public String[] getSupportedLanguageCode() {
        LanguageManager lm = JMPCore.getLanguageManager();
        String[] array = new String[DefineLanguage.NUMBER_OF_INDEX_LANG];
        for (int i = 0; i < array.length; i++) {
            array[i] = new String(lm.getLanguageCode(i));
        }
        return array;
    }
}
