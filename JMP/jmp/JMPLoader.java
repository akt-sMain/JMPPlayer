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
import jmp.player.PlayerAccessor;
import jmp.task.ICallbackFunction;
import jmp.task.TaskOfSequence;
import jmplayer.JMPPlayer;
import lib.MakeJmpLib;

/**
 * JMPリソースのロードを行うクラス
 *
 * @author abs
 *
 */
public class JMPLoader {

    /** 起動時にロードするファイルパスを保持する */
    private static String RequestFile = null;

    private static boolean exitFlag = false;

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
            File jms = null;
            if (args[0].equalsIgnoreCase("-mkjmp") == true) {
                MakeJmpLib.call(args);
                return true;
            }

            for (int i = 0; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-man") == true) {
                    System.out.println("-jms [*.jms]");
                    System.out.println("	プラグインのスタンドアロン起動モード");
                    System.out.println("-nonplg");
                    System.out.println("        プラグインをロードせず起動");
                    System.out.println("-unsync");
                    System.out.println("        非同期化オプション");
                    System.out.println("-mkjmp");
                    System.out.println("        プラグインパッケージ作成ライブラリを呼び出す。");
                    System.out.println("        ※コマンドの先頭に記述すること");
                    return true;
                }
                else if (args[i].equalsIgnoreCase("-jms") == true) {
                    i++;
                    if (i >= args.length) {
                        break;
                    }

                    String path = JMPCore.getSystemManager().getJmsDirPath();
                    path += Platform.getSeparator() + args[i];

                    jms = new File(path);
                    if (jms.exists() == true) {
                        if (Utility.checkExtension(jms.getPath(), PluginManager.SETUP_FILE_EX) == true) {
                            stdPlugin = JMPCore.getPluginManager().readingPlugin(jms);
                        }
                    }
                }
                else if (args[i].equalsIgnoreCase("-nonplg") == true) {
                    JMPFlags.NonPluginLoadFlag = true;
                }
                else if (args[i].equalsIgnoreCase("-debug") == true) {
                    JMPFlags.DebugMode = true;
                }
                else if (args[i].equalsIgnoreCase("-unsync") == true) {
                    JMPFlags.UseUnsynchronizedMidiPacket = true;
                }
                /* ※コマンドの判定を優先するため、このelseifは最後に挿入すること */
                else if (Utility.isExsistFile(args[i]) == true) {
                    JMPFlags.RequestFileLoadFlag = true;

                    RequestFile = args[i];

                    // ロード設定
                    JMPFlags.StartupAutoConectSynth = true;
                    JMPFlags.LoadToPlayFlag = true;
                }
            }
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
                            try {
                                TaskOfSequence.sleep(1000);
                            }
                            catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            JMPCore.getSystemManager().getMainWindow().loadFile(f);
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
                JMPCore.getSystemManager().showErrorMessageDialogSync(taskErrorMsg);
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

        JMPCore.getWindowManager().setVisibleAll(false);

        // 念のためシーケンサーを停止
        PlayerAccessor.getInstance().stopAllPlayer();

        // 終了前に全てのプラグインを閉じる
        for (IPlugin p : JMPCore.getPluginManager().getPlugins()) {
            p.close();
        }

        // タスクの終了
        JMPCore.getTaskManager().taskExit();

        exitFlag = true;
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
            try {
                if (PlayerAccessor.getInstance().open() == false) {
                    result = false;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                result = false;
            }
        }

        /* 起動準備 */
        if (result == true) {

            // プラグイン準備
            JMPCore.getPluginManager().startupPluginInstance();

            // メインウィンドウ登録
            IJmpMainWindow win = JMPCore.getSystemManager().getMainWindow();
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

            // アプリ全般のコールバック関数を登録
            JMPPlayer.registerCallbackPackage();
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
