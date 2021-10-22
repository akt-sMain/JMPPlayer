package jmp.core;

import java.util.ArrayList;
import java.util.List;

import jlib.core.JMPCoreManagerRegister;
import jmp.JMPFlags;

public class ManagerInstances {

    static SystemManager systemManager = new SystemManager();
    static DataManager dataManager = new DataManager();
    static SoundManager soundManager = new SoundManager();
    static PluginManager pluginManager = new PluginManager();
    static WindowManager windowManager = new WindowManager();
    static LanguageManager languageManager = new LanguageManager();
    static TaskManager taskManager = new TaskManager();
    static ResourceManager resourceManager = new ResourceManager();
    static FileManager fileManager = new FileManager();

    /** 初期化・終了実行順位 */
    private static AbstractManager[] aManager = {
            resourceManager,
            languageManager,
            systemManager,
            taskManager,
            dataManager,
            fileManager,
            windowManager,
            soundManager,
            pluginManager,
    };

    private static List<AbstractManager> asc = null;
    private static List<AbstractManager> desc = null;

    static void register(AbstractManager m) {
        // ライブラリに登録
        JMPCoreManagerRegister.register(m);
    }

    static boolean isFinishedAllInitialize() {
        for (AbstractManager am : getManagersOfAsc()) {
            if (am.isFinishedInitialize() == false) {
                return false;
            }
        }
        return true;
    }

    static boolean callInitFunc() {
        boolean result = true;
        JMPFlags.Log.cprintln("###");
        JMPFlags.Log.cprintln("## CORE initializing");
        JMPFlags.Log.cprintln("##");
        remakeOrder();
        for (AbstractManager am : getManagersOfAsc()) {
            JMPFlags.Log.cprint(">> " + am.getName() + " init... ");
            if (result == true) {
                result = am.initFunc();
            }
            JMPFlags.Log.cprintln(result == true ? "success" : "fail");
        }
        JMPFlags.Log.cprintln("## finished");
        JMPFlags.Log.cprintln("");
        return result;
    }

    static boolean callEndFunc() {
        boolean result = true;
        JMPFlags.Log.cprintln("###");
        JMPFlags.Log.cprintln("## CORE exiting");
        JMPFlags.Log.cprintln("##");
        for (AbstractManager am : getManagersOfDesc()) {
            JMPFlags.Log.cprint(">> " + am.getName() + " exit... ");
            if (result == true) {
                result = am.endFunc();
            }
            JMPFlags.Log.cprintln(result == true ? "success" : "fail");
        }
        JMPFlags.Log.cprintln("## finished");
        return result;
    }

    static void remakeOrder() {
        asc = makeCloneManagerList(true);
        desc = makeCloneManagerList(false);
    }

    private static List<AbstractManager> makeCloneManagerList(boolean order) {

        int startP = order ? 0 : aManager.length-1;
        int counter = order ? 1 : -1;

        int i = startP;

        // マネージャーリストをコピー
        List<AbstractManager> cloneMng = new ArrayList<AbstractManager>();
        while (true) {
            if (i < 0 || i >= aManager.length) {
                break;
            }
            cloneMng.add(aManager[i]);
            i += counter;
        }
        return cloneMng;
    }

    public static List<AbstractManager> getManagersOfAsc() {
        if (asc == null) {
            remakeOrder();
        }
        return asc;
    }
    public static List<AbstractManager> getManagersOfDesc() {
        if (desc == null) {
            remakeOrder();
        }
        return desc;
    }
}
