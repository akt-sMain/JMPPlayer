package jmp.core;

import java.util.List;

import jlib.JMPLIB;
import jmp.CommonRegisterINI;
import jmp.JMPFlags;
import jmp.core.TaskManager.TaskID;
import jmp.plugin.PluginWrapper;
import jmp.task.ICallbackFunction;
import jmp.util.JmpUtil;

public class JMPCore {

    /** アプリケーション名 */
    public static final String APPLICATION_NAME = "JMPPlayer";

    /** アプリケーションバージョン */
    public static final String APPLICATION_VERSION = "0.02β";

    /** ライブラリバージョン */
    public static final String LIBRALY_VERSION = JMPLIB.BUILD_VERSION;

    /** スタンドアロンモードのプラグイン */
    // private static IPlugin StandAlonePlugin = null;
    private static PluginWrapper StandAlonePluginWrapper = null;
    
    public static List<CommonRegisterINI> cregStack = null;

    public static boolean initFunc() {
        boolean result = true;
        try {
            result = ManagerInstances.callInitFunc();
        }
        catch (Exception e) {
            try {
                String eMsg = function.Utility.getCurrentTimeStr() + function.Platform.getNewLine() + function.Error.getPrintStackTrace(e);
                JmpUtil.writeTextFile("errorlog_init.txt", eMsg);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            result = false;
        }
        return result;
    }

    public static boolean endFunc() {
        boolean result = true;
        try {
            result = ManagerInstances.callEndFunc();
        }
        catch (Exception e) {
            try {
                String eMsg = function.Utility.getCurrentTimeStr() + function.Platform.getNewLine() + function.Error.getPrintStackTrace(e);
                JmpUtil.writeTextFile("errorlog_end.txt", eMsg);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            result = false;
        }
        return result;
    }

    public static boolean isFinishedInitialize() {
        return ManagerInstances.isFinishedAllInitialize();
    }

    public static boolean isEnableStandAlonePlugin() {
        if (StandAlonePluginWrapper != null) {
            return true;
        }
        return false;
    }

    public static void setStandAlonePluginWrapper(PluginWrapper plg) {
        StandAlonePluginWrapper = plg;
    }

    public static PluginWrapper getStandAlonePluginWrapper() {
        return StandAlonePluginWrapper;
    }

    /** 通知メソッド作成 */
    private static void createNotifyFunc(ICallbackFunction func) {
        if (JMPFlags.EnableNotifyFlag == false) {
            // func.callback();
            return;
        }
        getTaskManager().queuing(TaskID.SEQUENCE, func);
    }

    public static void callNotifyUpdateConfig(String key) {
        createNotifyFunc(new ICallbackFunction() {
            @Override
            public void callback() {
                for (AbstractManager am : ManagerInstances.getManagersOfAsc()) {
                    if (am.isFinishedInitialize() == true) {
                        am.notifyUpdateConfig(key);
                    }
                }
            }
        });
    }

    public static void callNotifyUpdateCommonRegister(String key) {
        createNotifyFunc(new ICallbackFunction() {
            @Override
            public void callback() {
                for (AbstractManager am : ManagerInstances.getManagersOfAsc()) {
                    if (am.isFinishedInitialize() == true) {
                        am.notifyUpdateCommonRegister(key);
                    }
                }
            }

        });
    }
    
    public static void initializeAllSetting() {
        // 全てのWindowを閉じる
        getWindowManager().setVisibleAll(false);
        getPluginManager().closeAllPlugins();

        getDataManager().initializeConfigDatabase();
        getDataManager().clearHistory();
        getSoundManager().clearPlayList();
        getWindowManager().initializeLayout();

        // 言語更新
        getWindowManager().updateLanguage();

        // 設定ファイルのFFmpegパスを同期
        getSystemManager().setFFmpegWrapperPath(getDataManager().getFFmpegPath());
        getSystemManager().setFFmpegInstalled(getDataManager().isFFmpegInstalled());
    }

    public static SystemManager getSystemManager() {
        return ManagerInstances.SSystemManager;
    }

    public static DataManager getDataManager() {
        return ManagerInstances.SDataManager;
    }

    public static SoundManager getSoundManager() {
        return ManagerInstances.SSoundManager;
    }

    public static PluginManager getPluginManager() {
        return ManagerInstances.SPluginManager;
    }

    public static WindowManager getWindowManager() {
        return ManagerInstances.SWindowManager;
    }

    public static LanguageManager getLanguageManager() {
        return ManagerInstances.SLanguageManager;
    }

    public static TaskManager getTaskManager() {
        return ManagerInstances.STaskManager;
    }

    public static ResourceManager getResourceManager() {
        return ManagerInstances.SResourceManager;
    }

    public static FileManager getFileManager() {
        return ManagerInstances.SFileManager;
    }
}
