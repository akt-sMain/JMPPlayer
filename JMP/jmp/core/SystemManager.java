package jmp.core;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import function.Platform;
import function.Utility;
import jlib.core.ISystemManager;
import jlib.gui.IJmpMainWindow;
import jlib.plugin.IPlugin;
import jlib.util.IUtilityToolkit;
import jmp.CommonRegister;
import jmp.ErrorDef;
import jmp.JMPFlags;
import jmp.JMPLoader;
import jmp.gui.BuiltinSynthSetupDialog;
import jmp.gui.DebugLogConsole;
import jmp.lang.DefineLanguage;
import jmp.midi.toolkit.MidiToolkitManager;
import jmp.util.JmpUtil;
import jmp.util.toolkit.UtilityToolkitManager;
import jmsynth.JMSoftSynthesizer;
import jmsynth.JMSynthEngine;
import jmsynth.JMSynthFile;
import jmsynth.midi.MidiInterface;
import process.IConsoleOutCallback;
import process.IProcessingCallback;
import wffmpeg.FFmpegWrapper;
import wrapper.ProcessingFFmpegWrapper;
import wrapper.ProcessingYoutubeDLWrapper;

/**
 * システム管理クラス
 *
 * @author abs
 *
 */
public class SystemManager extends AbstractManager implements ISystemManager {

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

    /** JMSynthライブラリ名 */
    public static final String JMSYNTH_LIB_NAME = JMSoftSynthesizer.INFO_NAME;
    public static final String JMSYNTH_CONFIG_EX = JMSynthFile.EXTENSION_CONFIG;

    /** 共通レジスタ */
    private CommonRegister cReg = null;
    private String[] cRegKeys = null;

    /** FFmpeg wrapper インスタンス */
    private FFmpegWrapper ffmpegWrapper = null;

    /** youtube-dl wrapper インスタンス */
    private ProcessingYoutubeDLWrapper youtubeDlWrapper = null;

    /** Utilityツールキット */
    private IUtilityToolkit utilToolkit = null;

    // システムパス変数
    private String[] aPath = null;

    private DebugLogConsole console = null;
    public void showConsole() {
        if (console != null) {
            console.setVisible(true);
        }
    }
    public void showConsoleForClear() {
        if (console != null) {
            console.clearText();
            console.setVisible(true);
        }
    }
    public void closeConsole() {
        if (console != null) {
            console.setVisible(false);
        }
    }
    public void consoleOutln(String s) {
        if (console != null) {
            console.println(s);
        }
    }
    public void consoleOut(String s) {
        if (console != null) {
            console.print(s);
        }
    }

    public void showSystemErrorMessage(int errorID) {
        String errorMsg = ErrorDef.getTotalMsg(errorID);

        Component parent = null;
        if (JMPCore.getWindowManager().isFinishedInitialize() == true) {
            IJmpMainWindow win = JMPCore.getWindowManager().getMainWindow();
            if (win != null) {
                if (win.isWindowVisible() == true) {
                    if (win instanceof Component) {
                        parent = (Component) win;
                    }
                    else {
                        parent = null;
                    }
                }
            }
        }
        JOptionPane.showMessageDialog(parent, errorMsg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void consoleOutSystemInfo() {
        JMPFlags.Log.cprintln("Date : " + Utility.getCurrentTimeStr());
        JMPFlags.Log.cprintln("Java : " + Platform.getJavaVersion());
        JMPFlags.Log.cprintln("OS   : " + Platform.getOSName());
        JMPFlags.Log.cprintln("SLaF : " + UIManager.getSystemLookAndFeelClassName());
        JMPFlags.Log.cprintln("CLaF : " + UIManager.getCrossPlatformLookAndFeelClassName());
        JMPFlags.Log.cprintln();
    }

    SystemManager() {
        super("system");
    }

    protected boolean initFunc() {

        utilToolkit = UtilityToolkitManager.getInstance().getUtilityToolkit(UtilityToolkitManager.DEFAULT_UTIL_TOOLKIT_NAME);

        // FFmpegWrapperインスタンス生成
        ffmpegWrapper = new ProcessingFFmpegWrapper();
        ffmpegWrapper.setOverwrite(true);

        youtubeDlWrapper = new ProcessingYoutubeDLWrapper();

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
        cRegKeys[COMMON_REGKEY_NO_AUTOPLAY_FUNC] = "autoplay_func";

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
        cReg.add(cRegKeys[COMMON_REGKEY_NO_DEBUGMODE], "FALSE", true);
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
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_1], "#00ff00", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_2], "#008000", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_3], "#808000", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_4], "#ffff00", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_5], "#000080", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_6], "#0000ff", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_7], "#008080", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_8], "#00ffff", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_9], "#ff0000", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_10], "#800000", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_11], "#800080", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_12], "#ff00ff", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_13], "#ffb6c1", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_14], "#ffa07a", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_15], "#20b2aa", true);
//        cReg.add(cRegKeys[COMMON_REGKEY_NO_CH_COLOR_FORMAT_16], "#deb887", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_FFMPEG_OUTPUT], "output");
        cReg.add(cRegKeys[COMMON_REGKEY_NO_FFMPEG_WIN], "ffmpeg", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_FFMPEG_MAC], "/Usr/local/bin/ffmpeg", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_FFMPEG_OTHER], "/usr/local/bin/ffmpeg", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_LYRIC_CHARCODE], "SJIS", true);
        cReg.add(cRegKeys[COMMON_REGKEY_NO_AUTOPLAY_FUNC], "DIR", true);

        // syscommon読み込み
        cReg.read(aPath[PATH_SYSCOMMON_FILE]);

        // デバッグ設定の復元
        if (getCommonRegisterValue(COMMON_REGKEY_NO_DEBUGMODE).equalsIgnoreCase("TRUE")) {
            JMPFlags.DebugMode = true;
        }

        if (getCommonRegisterValue(COMMON_REGKEY_NO_AUTOPLAY_FUNC).equalsIgnoreCase("PLT")) {
            JMPFlags.PlayListExtention = true;
        }
        else {
            JMPFlags.PlayListExtention = false;
        }

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
        setCommonRegisterValueAdmin(COMMON_REGKEY_NO_PLAYER_BACK_COLOR, Utility.convertHtmlColorToCode(JMPCore.getResourceManager().getAppBackgroundColor()));

        // ルックアンドフィールの設定
        setupLookAndFeel();

        // コンソール画面(デバッグ画面)
        if (JMPFlags.DebugMode == true) {
            console = new DebugLogConsole();
            if (JMPFlags.InvokeToConsole == true) {
                showConsole();
            }
            IConsoleOutCallback cOutCb = new IConsoleOutCallback() {

                @Override
                public void println(String s) {
                    JMPFlags.Log.cprintln(s);
                }

                @Override
                public void print(String s) {
                    JMPFlags.Log.cprint(s);
                }
            };
            youtubeDlWrapper.setConsoleOut(cOutCb);
            ((ProcessingFFmpegWrapper)ffmpegWrapper).setConsoleOut(cOutCb);
        }

        if (JMPLoader.UsePluginDirectory == true) {
            File pluginsDir = new File(aPath[PATH_PLUGINS_DIR]);
            if (pluginsDir.exists() == false) {
                // プラグインフォルダ作成
                if (!pluginsDir.mkdir()) {
                    return false;
                }
            }
        }
        File dataDir = new File(aPath[PATH_DATA_DIR]);
        if (dataDir.exists() == false) {
            // データフォルダ作成
            if (!dataDir.mkdir()) {
                return false;
            }
        }
        File resDir = new File(aPath[PATH_RES_DIR]);
        if (resDir.exists() == false) {
            // resフォルダ作成
            if (!resDir.mkdir()) {

            }
        }
        if (JMPLoader.UseHistoryFile == true || JMPLoader.UseConfigFile == true) {
            File saveDir = new File(aPath[PATH_SAVE_DIR]);
            if (saveDir.exists() == false) {
                // saveフォルダ作成
                if (!saveDir.mkdir()) {
                    return false;
                }
            }
        }
        if (JMPLoader.UsePluginDirectory == true) {
            File jarDir = new File(aPath[PATH_JAR_DIR]);
            if (jarDir.exists() == false) {
                // Jarフォルダ作成
                if (!jarDir.mkdir()) {
                    return false;
                }
            }
            File jmsDir = new File(aPath[PATH_JMS_DIR]);
            if (jmsDir.exists() == false) {
                // jmsフォルダ作成
                if (!jmsDir.mkdir()) {
                    return false;
                }
            }
            File outDir = new File(aPath[PATH_OUTPUT_DIR]);
            if (outDir.exists() == false) {
                // outputフォルダ作成
                if (!outDir.mkdir()) {
                    return false;
                }
            }
            File zipDir = new File(aPath[PATH_ZIP_DIR]);
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

        closeConsole();

        /* アクティベート処理 */
        postActivate();

        // syscommon dump
        if (Utility.isExsistFile(getSystemPath(SystemManager.PATH_SAVE_DIR)) == true) {
            cReg.write(getSystemPath(PATH_SYSCOMMON_FILE));
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
        if (Utility.isExsistFile(getSystemPath(PATH_ACTIVATE_FILE)) == true) {
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
            if (Utility.isExsistFile(getSystemPath(PATH_ACTIVATE_FILE)) == false) {
                try {
                    String text = "ライセンス認証のためのファイルです。" + Platform.getNewLine()//
                            + "このファイルを削除してもソフトウェアの動作には影響ありません。" + Platform.getNewLine()//
                            + Platform.getNewLine()//
                            + "File for license authentication." + Platform.getNewLine()//
                            + "Deleting this file does not affect the operation of the software.";

                    Utility.outputTextFile(getSystemPath(PATH_ACTIVATE_FILE), text);
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

    public boolean setCommonRegisterValueAdmin(int keyNo, String value) {
        return setCommonRegisterValueAdmin(getCommonRegisterKeyName(keyNo), value);
    }
    public boolean setCommonRegisterValueAdmin(String key, String value) {
        if (cReg == null) {
            return false;
        }

        boolean ret = cReg.setValue(key, value);
        if (ret == true) {
            // 全てのマネージャーに通知
            JMPCore.callNotifyUpdateCommonRegister(key);
        }
        return ret;
    }

    public boolean setCommonRegisterValue(String key, String value) {
        if (key.equalsIgnoreCase(getCommonRegisterKeyName(COMMON_REGKEY_NO_DEBUGMODE)) == false) {
            // デバッグモードの切り替えのみ許可
            if (JMPFlags.DebugMode == false) {
                // デバッグモード時のみ設定可能
                return false;
            }
        }
        return setCommonRegisterValueAdmin(key, value);
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

        aPath = new String[NUM_OF_PATH];

        String currentPath = Platform.getCurrentPath(false);

        // プラグイン格納ディレクトリパス
        aPath[PATH_PLUGINS_DIR] = Utility.pathCombin(currentPath, PLUGINS_DIR_NAME);

        // データファイル格納ディレクトリパス
        if (JMPLoader.UsePluginDirectory == true) {
            aPath[PATH_DATA_DIR] = Utility.pathCombin(aPath[PATH_PLUGINS_DIR], DATA_DIR_NAME);
        }
        else {
            aPath[PATH_DATA_DIR] = Utility.pathCombin(currentPath, DATA_DIR_NAME);
        }

        // リソースファイル格納ディレクトリパス
        if (JMPLoader.UsePluginDirectory == true) {
            aPath[PATH_RES_DIR] = Utility.pathCombin(aPath[PATH_PLUGINS_DIR], RES_DIR_NAME);
        }
        else {
            aPath[PATH_RES_DIR] = Utility.pathCombin(currentPath, RES_DIR_NAME);
        }

        // プラグインjms格納ディレクトリパス
        aPath[PATH_JMS_DIR] = Utility.pathCombin(aPath[PATH_PLUGINS_DIR], JMS_DIR_NAME);

        // プラグインjar格納ディレクトリパス
        aPath[PATH_JAR_DIR] = Utility.pathCombin(aPath[PATH_PLUGINS_DIR], JAR_DIR_NAME);

        // zip格納ディレクトリパス
        aPath[PATH_ZIP_DIR] = Utility.pathCombin(currentPath, ZIP_DIR_NAME);

        // 出力ファイル格納ディレクトリパス
        aPath[PATH_OUTPUT_DIR] = Utility.pathCombin(currentPath, OUTPUT_DIR_NAME);

        // セーブデータ格納ディレクトリパス
        aPath[PATH_SAVE_DIR] = Utility.pathCombin(currentPath, SAVE_DIR_NAME);

        // アクティベートファイルパス
        aPath[PATH_ACTIVATE_FILE] = Utility.pathCombin(aPath[PATH_SAVE_DIR], "activate");

        // syscommon
        aPath[PATH_SYSCOMMON_FILE] = Utility.pathCombin(aPath[PATH_SAVE_DIR], COMMON_SYS_FILENAME);

        // skin
        aPath[PATH_SKIN_DIR] = Utility.pathCombin(currentPath, SKIN_FOLDER_NAME);

        JMPFlags.Log.cprintln("###");
        JMPFlags.Log.cprintln("## Directory list");
        JMPFlags.Log.cprintln("##");
        for (int i=0; i<NUM_OF_PATH; i++) {
            JMPFlags.Log.cprintln("[" + i + "]" + aPath[i]);
        }
        JMPFlags.Log.cprintln("##");
        JMPFlags.Log.cprintln();
    }

    public String getSystemPath(int id) {
        if (0 <= id && id < NUM_OF_PATH) {
            return new String(aPath[id]);
        }
        else {
            return "";
        }
    }

    @Override
    public String getSystemPath(int id, IPlugin plugin) {
        String path = getSystemPath(id);
        switch (id) {
            case PATH_DATA_DIR:
            case PATH_RES_DIR:
                // プラグイン名の付与
                if (JMPLoader.UsePluginDirectory == true) {
                    path += (Platform.getSeparator() + getPluginName(plugin));
                }
                break;
            default:
                break;
        }
        return path;
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
        boolean deleteWav = true;
        if (JMPCore.getDataManager().isFFmpegLeaveOutputFile() == true) {
            deleteWav = false;
        }
        executeConvert(inPath, outPath, deleteWav);
    }

    public void executeConvert(String inPath, String outPath, boolean deleteWav) throws IOException {
        ffmpegWrapper.convert(inPath, outPath);

        if (deleteWav == true) {
            JMPCore.getDataManager().addConvertedFile(new File(outPath));
        }
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
        if (key.equals(getCommonRegisterKeyName(SystemManager.COMMON_REGKEY_NO_AUTOPLAY_FUNC)) == true) {
            if (getCommonRegisterValue(COMMON_REGKEY_NO_AUTOPLAY_FUNC).equalsIgnoreCase("PLT")) {
                JMPFlags.PlayListExtention = true;
            }
            else {
                JMPFlags.PlayListExtention = false;
            }
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
        if (JmpUtil.checkConfigKey(key, DataManager.CFG_KEY_YOUTUBEDL_PATH) == true) {
            setYoutubeDlWrapperPath(dm.getYoutubeDlPath());
        }
        if (JmpUtil.checkConfigKey(key, DataManager.CFG_KEY_YOUTUBEDL_INSTALLED) == true) {
            setYoutubeDlInstalled(dm.isYoutubeDlInstalled());
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

    // 新しいJMSynthインスタンスを取得する
    MidiInterface miface;
    public MidiInterface newBuiltinSynthInstance() {
        miface = JMSynthEngine.getMidiInterface(true);

        // Window登録
        BuiltinSynthSetupDialog wvf = new BuiltinSynthSetupDialog(miface);
        Color[] ct = new Color[16];
        for (int i = 0; i < 16; i++) {
            String key = String.format(SystemManager.COMMON_REGKEY_CH_COLOR_FORMAT, i + 1);
            ct[i] = getUtilityToolkit().convertCodeToHtmlColor(getCommonRegisterValue(key));
        }
        wvf.setWaveColorTable(ct);
        JMPCore.getWindowManager().registerBuiltinSynthFrame(wvf);
        return miface;
    }

    public void loadJMSynthConfig(File file) {
        if (JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIOUT).equals(JMSYNTH_LIB_NAME) == true) {
            if (miface != null) {
                JMSynthFile.loadSynthConfig(file, (JMSoftSynthesizer)miface.getSynthController());
            }
        }
    }

    public void setYoutubeDlCallback(IProcessingCallback cb) {
        youtubeDlWrapper.setCallback(cb);
    }

    public void executeYoutubeDownload(String url, File outputDir, String extension, boolean isAudioOnly) throws IOException {
        youtubeDlWrapper.setAudioOnly(isAudioOnly);
        youtubeDlWrapper.setOutput(outputDir.getPath());
        youtubeDlWrapper.convert(url, extension);
    }

    public void setYoutubeDlWrapperPath(String path) {
        youtubeDlWrapper.setPath(path);
    }

    public String getYoutubeDlWrapperPath() {
        return youtubeDlWrapper.getPath();
    }

    public boolean isValidYoutubeDlWrapper() {
        return youtubeDlWrapper.isValid();
    }

    public boolean isYoutubeDlInstalled() {
        return youtubeDlWrapper.isYoutubeDlInstalled();
    }

    public void setYoutubeDlInstalled(boolean isEnableEnvironmentVariable) {
        youtubeDlWrapper.setYoutubeDlInstalled(isEnableEnvironmentVariable);
    }
}
