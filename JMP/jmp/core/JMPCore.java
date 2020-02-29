package jmp.core;

import jlib.JMPLIB;
import jlib.plugin.IPlugin;

public class JMPCore {

    /** アプリケーション名 */
    public static final String APPLICATION_NAME = "JamPlayer";

    /** アプリケーションバージョン */
    public static final String APPLICATION_VERSION = "0.01(β)";

    /** ライブラリバージョン */
    public static final String LIBRALY_VERSION = JMPLIB.BUILD_VERSION;

    /** スタンドアロンモードのプラグイン */
    public static IPlugin StandAlonePlugin = null;

    /** 初期化・終了実行順位 */
    private static final int MANAGER_PRIORITY_RESOUCE = 0;
    private static final int MANAGER_PRIORITY_LANG = AbstractManager.INVALID_PRIORITY; // 未実装
    private static final int MANAGER_PRIORITY_SYSTEM = 10;
    private static final int MANAGER_PRIORITY_WINDOW = 20;
    private static final int MANAGER_PRIORITY_DATA = 30;
    private static final int MANAGER_PRIORITY_SOUND = 40;
    private static final int MANAGER_PRIORITY_PLUGIN = 50;
    private static final int MANAGER_PRIORITY_TASK = 60;

    private static SystemManager systemManager = new SystemManager(MANAGER_PRIORITY_SYSTEM);
    private static DataManager dataManager = new DataManager(MANAGER_PRIORITY_DATA);
    private static SoundManager soundManager = new SoundManager(MANAGER_PRIORITY_SOUND);
    private static PluginManager pluginManager = new PluginManager(MANAGER_PRIORITY_PLUGIN);
    private static WindowManager windowManager = new WindowManager(MANAGER_PRIORITY_WINDOW);
    private static LanguageManager languageManager = new LanguageManager(MANAGER_PRIORITY_LANG);
    private static TaskManager taskManager = new TaskManager(MANAGER_PRIORITY_TASK);
    private static ResourceManager resourceManager = new ResourceManager(MANAGER_PRIORITY_RESOUCE);

    public static boolean initFunc() {
        boolean result = true;
        result = AbstractManager.init();
        return result;
    }

    public static boolean endFunc() {
        boolean result = true;
        result = AbstractManager.end();
        return result;
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
}
