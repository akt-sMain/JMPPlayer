package jmp;

import java.io.File;

import function.Platform;
import function.Utility;
import jlib.gui.IJmpMainWindow;
import jlib.plugin.IPlugin;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.core.TaskManager;
import jmp.core.WindowManager;
import jmp.task.ICallbackFunction;
import lib.MakeJmpLib;

/**
 * JMPリソースのロードを行うクラス
 *
 * @author abs
 *
 */
public class JMPLoader {

    /** プラグインフォルダを使用するか */
    public static boolean UsePluginDirectory = true;
    public static boolean UseConfigFile = true;
    public static boolean UseHistoryFile = true;
    public static boolean UseSkinFile = true;

    /** 起動時にロードするファイルパスを保持する */
    private static String RequestFile = null;

    private static boolean exitFlag = false;

    /* コマンド文字列 */
    public static final String CMD_MANUAL = "-man";
    public static final String CMD_DEBUG = "-debug";
    public static final String CMD_STDPLG = "-stdplg";
    public static final String CMD_NONPLG = "-nonplg";
    public static final String CMD_PLGLST = "-plglst";
    public static final String CMD_UNSYNC = "-unsync";
    public static final String CMD_SYSOUT = "-c";
    public static final String CMD_MKJMP = "-mkjmp";

    /* フォーマット */
    private static final String PLGLST_FORMAT = "<%d> %s";
    private static final String MANUAL_FORMAT_CMD = " %s";
    private static final String MANUAL_FORMAT_DSC = "     %s";

    private static void printManual() {
        printManualLine(//
                CMD_STDPLG + " [プラグイン名]", //
                "プラグインのスタンドアロン起動モード"//
        );//
        printManualLine(//
                CMD_NONPLG, //
                "プラグインをロードせず起動"//
        );//
        printManualLine(//
                CMD_PLGLST, //
                "プラグイン一覧"//
        );//
        printManualLine(//
                CMD_UNSYNC, //
                "非同期化オプション"//
        );//
        printManualLine(//
                CMD_SYSOUT, //
                "コンソール出力有効化"//
        );//
        printManualLine(//
                CMD_MKJMP, //
                "プラグインパッケージ作成ライブラリを呼び出す。", //
                "※コマンドの先頭に記述すること"//
        );//
    }

    private static void printManualLine(String cmd, String... description) {
        System.out.println(String.format(MANUAL_FORMAT_CMD, cmd));
        for (String d : description) {
            System.out.println(String.format(MANUAL_FORMAT_DSC, d));
        }
        System.out.println();
    }

    private static void printPlglst() {
        String path = JMPCore.getSystemManager().getJmsDirPath();
        File jmsDir = new File(path);
        int cnt = 0;
        if (jmsDir.isDirectory() == true) {
            for (File f : jmsDir.listFiles()) {
                if (Utility.checkExtension(f, PluginManager.SETUP_FILE_EX) == true) {
                    String dsc = String.format(PLGLST_FORMAT, (cnt + 1), Utility.getFileNameNotExtension(f));
                    System.out.println(dsc);
                    cnt++;
                }
            }
        }
        if (cnt <= 0) {
            System.out.println("<!> プラグインが存在しません。");
        }
    }

    private static IPlugin getStdPlugin(String jmsName) {
        String path = JMPCore.getSystemManager().getJmsDirPath();
        if (jmsName.endsWith("." + PluginManager.SETUP_FILE_EX) == false) {
            jmsName += "." + PluginManager.SETUP_FILE_EX;
        }
        path += Platform.getSeparator() + jmsName;

        IPlugin stdPlugin = null;
        File jms = new File(path);
        if (jms.exists() == true) {
            if (Utility.checkExtension(jms.getPath(), PluginManager.SETUP_FILE_EX) == true) {
                stdPlugin = JMPCore.getPluginManager().readingPlugin(jms);
            }
        }
        if (stdPlugin == null) {
            System.out.println("<!> 指定したプラグインが存在しません。");
        }
        return stdPlugin;
    }

    // コンソール出力だけで完結する方式の起動方法に必要なセットアップ処理を行う
    private static void setupConsoleApplication() {
        // システムパス設定
        JMPCore.getSystemManager().makeSystemPath();
    }

    /**
     * JMPPlayer起動設定
     *
     * @param args
     * @param config
     * @return
     */
    public static boolean invoke(String[] args, ConfigDatabase config) {
        IPlugin stdPlugin = null;
        if (args.length > 0) {
            // システムパス設定
            JMPCore.getSystemManager().makeSystemPath();

            if (args[0].equalsIgnoreCase(CMD_MKJMP) == true) {
                args[0] = MakeJmpLib.CMD_CONSOLE;
                MakeJmpLib.call(args);
                return true;
            }

            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase(CMD_MANUAL) == true) {
                    setupConsoleApplication();
                    printManual();
                    return true;
                }
                else if (args[i].equalsIgnoreCase(CMD_STDPLG) == true) {
                    i++;
                    if (i >= args.length) {
                        break;
                    }

                    String jmsName = args[i];
                    stdPlugin = getStdPlugin(jmsName);
                }
                else if (args[i].equalsIgnoreCase(CMD_NONPLG) == true) {
                    JMPFlags.NonPluginLoadFlag = true;
                }
                else if (args[i].equalsIgnoreCase(CMD_PLGLST) == true) {
                    printPlglst();
                    return true;
                }
                else if (args[i].equalsIgnoreCase(CMD_DEBUG) == true) {
                    JMPFlags.DebugMode = true;
                }
                else if (args[i].equalsIgnoreCase(CMD_UNSYNC) == true) {
                    JMPFlags.UseUnsynchronizedMidiPacket = true;
                }
                else if (args[i].equalsIgnoreCase(CMD_SYSOUT) == true) {
                    JMPFlags.CoreConsoleOut = true;
                }
                /* ※コマンドの判定を優先するため、このelseifは最後に挿入すること */
                else if (Utility.isExsistFile(args[i]) == true) {
                    File f = new File(args[i]);
                    if (f.isFile() == true) {

                        RequestFile = args[i];

                        // ロード設定
                        JMPFlags.RequestFileLoadFlag = true;
                        JMPFlags.StartupAutoConectSynth = true;
                        JMPFlags.LoadToPlayFlag = true;
                    }
                }
            }
        }

        if (JMPFlags.NonPluginLoadFlag == true) {
            // ※nonplgとstdplgコマンドは併用不可
            stdPlugin = null;
        }
        return invoke(config, stdPlugin);
    }

    /**
     * JMPPlayer起動設定
     *
     * @param args
     * @return
     */
    public static boolean invoke(String[] args) {
        return invoke(args, null);
    }

    /**
     * スタンドアロンプラグイン起動設定
     *
     * @param args
     * @param standAlonePlugin
     *            スタンドアロンプラグイン
     * @return
     */
    public static boolean invoke(ConfigDatabase config, IPlugin standAlonePlugin) {

        JMPFlags.LibraryMode = false;

        // ライブラリ初期化処理
        boolean result = initLibrary(config, standAlonePlugin);

        /* 起動準備 */
        if (result == true) {

            TaskManager taskManager = JMPCore.getTaskManager();

            // コマンド引数で指定されたファイルを開く
            if (JMPFlags.RequestFileLoadFlag == true) {
                File f = new File(RequestFile);
                if (f.canRead() == true) {
                    taskManager.getTaskOfSequence().queuing(new ICallbackFunction() {
                        @Override
                        public void callback() {
                            Utility.threadSleep(1000);
                            JMPCore.getFileManager().loadFile(f);
                        }
                    });
                }
                else {
                    JMPFlags.LoadToPlayFlag = false;
                }
            }

            // タスクジョイン
            try {
                taskManager.join();
            }
            catch (InterruptedException e) {
                // 強制終了
                exit();

                String taskErrorMsg = "予期せぬエラーが発生しました。(Task error.)" + Platform.getNewLine() + "アプリケーションを強制終了します。";
                JMPCore.getWindowManager().showErrorMessageDialogSync(taskErrorMsg);
            }
        }

        // ライブラリ終了処理
        boolean endResult = exitLibrary();
        if (result == false) {
            endResult = result;
        }
        return endResult;
    }

    /**
     * 終了
     */
    public static void exit() {
        if (exitFlag == true) {
            return;
        }
        exitFlag = true;

        JMPCore.getWindowManager().setVisibleAll(false);

        // 終了前に全てのプラグインを閉じる
        JMPCore.getPluginManager().closeAllPlugins();

        // タスクの終了
        JMPCore.getTaskManager().taskExit();
    }

    /**
     * ライブラリ初期化
     *
     * @param config
     * @return
     */
    public static boolean initLibrary(ConfigDatabase config) {
        return initLibrary(config, null);
    }

    /**
     * ライブラリ初期化
     *
     * @param config
     * @param plugin
     * @return
     */
    public static boolean initLibrary(ConfigDatabase config, IPlugin plugin) {

        // スタンドアロンプラグイン設定
        JMPCore.StandAlonePlugin = plugin;

        // システムパス設定
        JMPCore.getSystemManager().makeSystemPath();

        // 設定値を先行して登録する
        JMPCore.getDataManager().setConfigDatabase(config);

        // 管理クラス初期化処理
        boolean result = JMPCore.initFunc();

        if (JMPFlags.LibraryMode == false) {
            if (result == true) {
                /* ライセンス確認 */
                if (JMPFlags.ActivateFlag == false) {
                    JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_LANGUAGE).showWindow();
                    JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_LICENSE).showWindow();
                }

                // ダイアログが閉じられてから再度ActivateFlagを確認する
                if (JMPFlags.ActivateFlag == false) {
                    // アクティベートされなかった場合は終了する
                    result = false;
                }
            }
        }
        if (result == true) {
            /* プレイヤーロード */
            result = JMPCore.getSoundManager().openPlayer();
        }

        /* 起動準備 */
        if (result == true) {

            // プラグイン準備
            JMPCore.getPluginManager().startupPluginInstance();

            // メインウィンドウ登録
            IJmpMainWindow win = JMPCore.getWindowManager().getMainWindow();
            win.initializeSetting();
            win.hideWindow();

            // 言語更新
            JMPCore.getWindowManager().updateLanguage();

            // 起動構成
            if (JMPCore.isEnableStandAlonePlugin() == false) {
                // JMPPlayer起動
                if (JMPFlags.LibraryMode == false) {
                    win.showWindow();
                }
            }
            else {
                // スタンドアロンプラグイン起動
                JMPCore.StandAlonePlugin.open();
            }

            // タスク開始
            TaskManager taskManager = JMPCore.getTaskManager();
            taskManager.taskStart();
        }
        return result;
    }

    /**
     * ライブラリ終了
     *
     * @return
     */
    public static boolean exitLibrary() {
        exit();

        // タスクジョイン
        try {
            TaskManager taskManager = JMPCore.getTaskManager();
            taskManager.join();
        }
        catch (InterruptedException e) {
        }

        // Windowを閉じる
        JMPCore.getWindowManager().setVisibleAll(false);

        boolean result = JMPCore.endFunc();
        return result;
    }

}
