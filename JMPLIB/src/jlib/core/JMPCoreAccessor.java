package jlib.core;

/**
 * 各マネージャークラスへのアクセス制御をおこなうクラス
 *
 * @author abs
 *
 */
public class JMPCoreAccessor {
    private static ISystemManager systemManager = null;
    private static ISoundManager soundManager = null;
    private static IDataManager dataManager = null;
    private static IWindowManager windowManager = null;
    private static IFileManager fileManager = null;

    /**
     * マネージャー登録
     *
     * @param manager
     */
    public static void register(IManager manager) {
        if (manager instanceof ISystemManager) {
            systemManager = (ISystemManager) manager;
        }
        else if (manager instanceof ISoundManager) {
            soundManager = (ISoundManager) manager;
        }
        else if (manager instanceof IDataManager) {
            dataManager = (IDataManager) manager;
        }
        else if (manager instanceof IWindowManager) {
            windowManager = (IWindowManager) manager;
        }
        else if (manager instanceof IFileManager) {
            fileManager = (IFileManager) manager;
        }
    }

    /**
     * SystemManager取得
     *
     * @return
     */
    public static ISystemManager getSystemManager() {
        return systemManager;
    }

    /**
     * SoundManager取得
     *
     * @return
     */
    public static ISoundManager getSoundManager() {
        return soundManager;
    }

    /**
     * DataManager取得
     *
     * @return
     */
    public static IDataManager getDataManager() {
        return dataManager;
    }

    /**
     * WindowManager取得
     *
     * @return
     */
    public static IWindowManager getWindowManager() {
        return windowManager;
    }

    /**
     * FileManager取得
     *
     * @return
     */
    public static IFileManager getFileManager() {
        return fileManager;
    }
}
