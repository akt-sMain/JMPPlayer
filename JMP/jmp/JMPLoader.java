package jmp;

import java.io.File;

import function.Platform;
import jlib.plugin.IPlugin;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.core.SystemManager;
import jmp.core.TaskManager;
import jmp.core.WindowManager;
import jmp.task.ICallbackFunction;
import jmp.util.JmpUtil;
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
    public static final String CMD_UN_LOAD_SKIN = "-nskin";

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
                CMD_UN_LOAD_SKIN, //
                "スキンを読み込まない"//
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
        String path = JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_JMS_DIR);
        File jmsDir = new File(path);
        int cnt = 0;
        if (jmsDir.exists() == true && jmsDir.isDirectory() == true) {
            for (File f : jmsDir.listFiles()) {
                if (JmpUtil.checkExtension(f, PluginManager.SETUP_FILE_EX) == true) {
                    String dsc = String.format(PLGLST_FORMAT, (cnt + 1), JmpUtil.getFileNameNotExtension(f));
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
        String path = JMPCore.getSystemManager().getSystemPath(SystemManager.PATH_JMS_DIR);
        if (jmsName.endsWith("." + PluginManager.SETUP_FILE_EX) == false) {
            jmsName += "." + PluginManager.SETUP_FILE_EX;
        }
        path += Platform.getSeparator() + jmsName;

        IPlugin stdPlugin = null;
        File jms = new File(path);
        if (jms.exists() == true) {
            if (JmpUtil.checkExtension(jms.getPath(), PluginManager.SETUP_FILE_EX) == true) {
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
     * コマンド引数の解析
     *
     * @param args
     * @return
     */
    private static InvokeArgs parseArgs(String[] args) {
        InvokeArgs res = new InvokeArgs();
        String jmsName = "";
        res.stdPlugin = null;
        res.doReturn = false;
        res.loadFile = null;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase(CMD_MKJMP) == true) {
                args[0] = MakeJmpLib.CMD_CONSOLE;
                MakeJmpLib.call(args);
                res.doReturn = true;
                return res;
            }

            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase(CMD_MANUAL) == true) {
                    setupConsoleApplication();
                    printManual();
                    res.doReturn = true;
                    return res;
                }
                else if (args[i].equalsIgnoreCase(CMD_STDPLG) == true) {
                    i++;
                    if (i >= args.length) {
                        break;
                    }

                    jmsName = args[i];
                }
                else if (args[i].equalsIgnoreCase(CMD_NONPLG) == true) {
                    JMPFlags.NonPluginLoadFlag = true;
                }
                else if (args[i].equalsIgnoreCase(CMD_PLGLST) == true) {
                    setupConsoleApplication();
                    printPlglst();
                    res.doReturn = true;
                    return res;
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
                else if (args[i].equalsIgnoreCase(CMD_UN_LOAD_SKIN) == true) {
                    UseSkinFile = false;
                }
                /* ※コマンドの判定を優先するため、このelseifは最後に挿入すること */
                else if (JmpUtil.isExsistFile(args[i]) == true) {
                    File f = new File(args[i]);
                    if (f.isFile() == true) {

                        res.loadFile = new File(args[i]);

                        // ロード設定
                        JMPFlags.StartupAutoConectSynth = true;
                        JMPFlags.LoadToPlayFlag = true;
                    }
                }
            }
        }

        if (JMPFlags.NonPluginLoadFlag == true) {
            // ※nonplgとstdplgコマンドは併用不可
            jmsName = "";
        }

        /* スタンドアロンプラグインの特殊処理 */
        if (jmsName.isEmpty() == false) {
            // システムパス設定
            JMPCore.getSystemManager().makeSystemPath();

            // プラグインを探索
            res.stdPlugin = getStdPlugin(jmsName);
        }
        return res;
    }

    /**
     * JMPPlayer起動設定
     *
     * @param args
     * @param config
     * @return
     */
    public static boolean invoke(String[] args, ConfigDatabaseWrapper config) {
        InvokeArgs rArgs = parseArgs(args);
        if (rArgs.doReturn == true) {
            return true;
        }
        return invokeImpl(config, rArgs.stdPlugin, rArgs.loadFile);
    }

    /**
     * JMPPlayer起動設定
     *
     * @param args
     * @return
     */
    public static boolean invoke(String[] args) {
        InvokeArgs rArgs = parseArgs(args);
        if (rArgs.doReturn == true) {
            return true;
        }
        return invokeImpl(null, rArgs.stdPlugin, rArgs.loadFile);
    }

    /**
     * スタンドアロンプラグイン起動設定
     *
     * @param args
     * @param standAlonePlugin
     *            スタンドアロンプラグイン
     * @return
     */
    public static boolean invoke(ConfigDatabaseWrapper config, IPlugin standAlonePlugin) {
        return invokeImpl(config, standAlonePlugin, null);
    }

    /**
     * 起動処理本体
     * @param config
     * @param standAlonePlugin
     * @return
     */
    private static boolean invokeImpl(ConfigDatabaseWrapper config, IPlugin standAlonePlugin, File loadFile) {

        // invokeメソッドからの起動はLibraryモードではない
        JMPFlags.LibraryMode = false;

        SystemManager.consoleOutSystemInfo();

        // ライブラリ初期化処理
        boolean result = initLibrary(config, standAlonePlugin);

        /* 起動準備 */
        if (result == true) {

            TaskManager taskManager = JMPCore.getTaskManager();

            // コマンド引数で指定されたファイルを開く
            if (loadFile != null) {
                if (loadFile.canRead() == true) {
                    taskManager.queuing(new ICallbackFunction() {
                        @Override
                        public void callback() {
                            JmpUtil.threadSleep(500);
                            JMPCore.getFileManager().loadFile(loadFile);
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

                JMPCore.getSystemManager().showSystemErrorMessage(ErrorDef.ERROR_ID_UNKNOWN_EXIT_APPLI);
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

        if (JMPCore.getWindowManager().isValidBuiltinSynthFrame() == true) {
            JMPCore.getWindowManager().closeBuiltinSynthFrame();
        }

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
    public static boolean initLibrary(ConfigDatabaseWrapper config) {
        return initLibrary(config, null);
    }

    /**
     * ライブラリ初期化
     *
     * @param config
     * @param plugin
     * @return
     */
    public static boolean initLibrary(ConfigDatabaseWrapper config, IPlugin plugin) {

        // Sequenceタスクが準備出来るまではNotifyを破棄する必要がある
        JMPFlags.EnableNotifyFlag = false;

        // システムパス設定
        JMPCore.getSystemManager().makeSystemPath();

        // 設定値を先行して登録する
        if (config == null) {
            JMPCore.getDataManager().setConfigDatabase(null);
        }
        else {
            JMPCore.getDataManager().setConfigDatabase(config.getConfigDatabase());
        }

        // 管理クラス初期化処理
        boolean result = JMPCore.initFunc();
        if (result == true) {
            // 全ての画面を最新版にする
            JMPCore.getWindowManager().updateBackColor();
            JMPCore.getWindowManager().updateDebugMenu();
            JMPCore.getWindowManager().updateLanguage();

            /* ライセンス確認 */
            if (JMPFlags.LibraryMode == false) {
                /* ライセンス確認 */
                if (JMPFlags.ActivateFlag == false) {
                    JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_LANGUAGE).showWindow();
                    //Notifyタスクがまだ有効ではないため、ここで言語更新する必要がある
                    JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_LICENSE).updateLanguage();
                    JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_LICENSE).showWindow();
                }

                // ダイアログが閉じられてから再度ActivateFlagを確認する
                if (JMPFlags.ActivateFlag == false) {
                    // アクティベートされなかった場合は終了する
                    result = false;
                }
            }
        }
        else {
            // 立ち上げ失敗
            JMPCore.getSystemManager().showSystemErrorMessage(ErrorDef.ERROR_ID_SYSTEM_FAIL_INIT_FUNC);
        }

        /* プレイヤーロード */
        if (result == true) {
            result = JMPCore.getSoundManager().openPlayer();
            if (result == false) {
                JMPCore.getSystemManager().showSystemErrorMessage(ErrorDef.ERROR_ID_UNKNOWN_FAIL_LOAD_PLAYER);
            }
        }

        /* 起動準備 */
        if (result == true) {

            if (JMPCore.getDataManager().isShowStartupDeviceSetup() == false) {
                // 自動接続フラグを立てる
                JMPFlags.StartupAutoConectSynth = true;
            }

            // サウンドデバイス設定の初期処理
            if (JMPCore.getSoundManager().startupDeviceSetup() == true) {
                /* サウンドデバイスが用意出来たら次の設定へ */

                // プラグイン準備
                JMPCore.getPluginManager().startupPluginInstance(plugin);

                // Window初期処理
                JMPCore.getWindowManager().startupWindow();

                // 起動構成
                if (JMPCore.isEnableStandAlonePlugin() == false) {
                    // JMPPlayer起動
                    if (JMPFlags.LibraryMode == false) {
                        JMPCore.getWindowManager().getMainWindow().showWindow();
                    }
                }
                else {
                    // スタンドアロンプラグイン起動
                    JMPCore.getStandAlonePluginWrapper().open();
                }

                // タスク開始
                TaskManager taskManager = JMPCore.getTaskManager();
                taskManager.taskStart();
            }
            else {
                result = false;
            }
        }

        // Notify有効化
        JMPFlags.EnableNotifyFlag = true;
        return result;
    }

    /**
     * ライブラリ終了
     *
     * @return
     */
    public static boolean exitLibrary() {

        // 生きているタスクが無いか確認、あったら終了させる
        TaskManager taskManager = JMPCore.getTaskManager();
        if (taskManager.isRunnable() == true) {
            exitFlag = false;
            exit();

            // タスクジョイン
            try {
                taskManager.join();
            }
            catch (InterruptedException e) {
            }
        }

        // Windowを閉じる
        JMPCore.getWindowManager().setVisibleAll(false);

        boolean result = JMPCore.endFunc();
        if (result == false && JMPCore.isFinishedInitialize() == true) {
            JMPCore.getSystemManager().showSystemErrorMessage(ErrorDef.ERROR_ID_SYSTEM_FAIL_END_FUNC);
        }
        return result;
    }

}
