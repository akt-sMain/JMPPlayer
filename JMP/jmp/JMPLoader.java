package jmp;

import java.io.File;

import javax.sound.midi.Sequencer;

import function.Platform;
import function.Utility;
import jlib.plugin.IPlugin;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.core.TaskManager;
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

        // スタンドアロンプラグイン設定
        JMPCore.StandAlonePlugin = standAlonePlugin;

        // 設定値を先行して登録する
        JMPCore.getDataManager().setConfigDatabase(config);

        // 管理クラス初期化処理
        boolean result = JMPCore.initFunc();

        /* 起動準備 */
        if (result == true) {

            // シンセ情報表示
            if (JMPFlags.DebugMode == true) {
                Sequencer seq = JMPCore.getSoundManager().getSequencer();
                System.out.println("sequencer : " + (seq == null ? "NONE" : seq.getDeviceInfo().getName()));
            }

            // メインウィンドウ登録
            JMPPlayer win = new JMPPlayer();
            win.initializeSetting();
            JMPCore.getSystemManager().registerMainWindow(win);

            // 起動構成
            if (JMPCore.StandAlonePlugin == null) {
                // JMPPlayer起動
                win.setVisible(true);
            }
            else {
                // スタンドアロンプラグイン起動
                win.setVisible(false);
                JMPCore.StandAlonePlugin.open();
            }

            // タスク開始
            TaskManager taskManager = JMPCore.getTaskManager();
            taskManager.taskStart();

            // アプリ全般のコールバック関数を登録
            JMPPlayer.registerCallbackPackage();

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

            // Windowを閉じる
            JMPCore.getWindowManager().setVisibleAll(false);

            if (win != null) {
                // Windowの破棄
                win.dispose();
                win = null;
            }
        }

        boolean endResult = JMPCore.endFunc();
        if (result == false) {
            endResult = result;
        }
        return endResult;
    }

    public static void exit() {
        PluginManager pm = JMPCore.getPluginManager();
        DataManager dm = JMPCore.getDataManager();

        // 念のためシーケンサーを停止
        PlayerAccessor.getInstance().getCurrent().stop();

        // 終了前に全てのプラグインを閉じる
        for (IPlugin p : pm.getPlugins()) {
            p.close();
        }

        dm.getHistoryDialog().setVisible(false);

        // タスクの終了
        JMPCore.getTaskManager().taskExit();
    }

}
