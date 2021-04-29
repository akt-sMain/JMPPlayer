package jmp.core;

import jlib.JMPLIB;
import jlib.plugin.IPlugin;
import jmp.core.TaskManager.TaskID;
import jmp.task.ICallbackFunction;

public class JMPCore {

    /** アプリケーション名 */
    public static final String APPLICATION_NAME = "JMPPlayer";

    /** アプリケーションバージョン */
    public static final String APPLICATION_VERSION = "0.02β";

    /** ライブラリバージョン */
    public static final String LIBRALY_VERSION = JMPLIB.BUILD_VERSION;

    /** スタンドアロンモードのプラグイン */
    private static IPlugin StandAlonePlugin = null;

    /** 初期化・終了実行順位 */
    private static final int MANAGER_PRIORITY_RESOUCE = 0;
    private static final int MANAGER_PRIORITY_LANG = 10;
    private static final int MANAGER_PRIORITY_SYSTEM = 20;
    private static final int MANAGER_PRIORITY_TASK = 30;
    private static final int MANAGER_PRIORITY_DATA = 40;
    private static final int MANAGER_PRIORITY_FILE = 50;
    private static final int MANAGER_PRIORITY_WINDOW = 60;
    private static final int MANAGER_PRIORITY_SOUND = 70;
    private static final int MANAGER_PRIORITY_PLUGIN = 80;

    private static SystemManager systemManager = new SystemManager(MANAGER_PRIORITY_SYSTEM);
    private static DataManager dataManager = new DataManager(MANAGER_PRIORITY_DATA);
    private static SoundManager soundManager = new SoundManager(MANAGER_PRIORITY_SOUND);
    private static PluginManager pluginManager = new PluginManager(MANAGER_PRIORITY_PLUGIN);
    private static WindowManager windowManager = new WindowManager(MANAGER_PRIORITY_WINDOW);
    private static LanguageManager languageManager = new LanguageManager(MANAGER_PRIORITY_LANG);
    private static TaskManager taskManager = new TaskManager(MANAGER_PRIORITY_TASK);
    private static ResourceManager resourceManager = new ResourceManager(MANAGER_PRIORITY_RESOUCE);
    private static FileManager fileManager = new FileManager(MANAGER_PRIORITY_FILE);

    public static boolean initFunc() {
        boolean result = true;
        result = AbstractManager.callInitFunc();
        return result;
    }

    public static boolean endFunc() {
        boolean result = true;
        result = AbstractManager.callEndFunc();
        return result;
    }

    public static boolean isFinishedInitialize() {
        return AbstractManager.isFinishedAllInitialize();
    }

    public static boolean isEnableStandAlonePlugin() {
        if (StandAlonePlugin != null) {
            return true;
        }
        return false;
    }

    public static void setStandAlonePlugin(IPlugin plg) {
        StandAlonePlugin = plg;
    }

    /** 通知メソッド作成 */
    private static void createNotifyFunc(ICallbackFunction func) {
        if (AbstractManager.isFinishedAllInitialize() == false) {
            return;
        }
        getTaskManager().queuing(TaskID.SEQUENCE, func);
    }

    public static void callNotifyUpdateConfig(String key) {
        createNotifyFunc(new ICallbackFunction() {
            @Override
            public void callback() {
                for (AbstractManager am : AbstractManager.asc) {
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
                for (AbstractManager am : AbstractManager.asc) {
                    if (am.isFinishedInitialize() == true) {
                        am.notifyUpdateCommonRegister(key);
                    }
                }
            }

        });
    }

    public static IPlugin getStandAlonePlugin() {
        return StandAlonePlugin;
    }

    public static SystemManager getSystemManager() {
        return systemManager;
    }

    public static DataManager getDataManager() {
        return dataManager;
    }

    public static SoundManager getSoundManager() {
        return soundManager;
    }

    public static PluginManager getPluginManager() {
        return pluginManager;
    }

    public static WindowManager getWindowManager() {
        return windowManager;
    }

    public static LanguageManager getLanguageManager() {
        return languageManager;
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public static ResourceManager getResourceManager() {
        return resourceManager;
    }

    public static FileManager getFileManager() {
        return fileManager;
    }
}
