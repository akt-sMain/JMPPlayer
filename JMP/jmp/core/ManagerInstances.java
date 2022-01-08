package jmp.core;

import java.util.ArrayList;
import java.util.List;

import jlib.core.JMPCoreManagerRegister;
import jmp.JMPFlags;

public class ManagerInstances {

    static SystemManager SSystemManager = new SystemManager();
    static DataManager SDataManager = new DataManager();
    static SoundManager SSoundManager = new SoundManager();
    static PluginManager SPluginManager = new PluginManager();
    static WindowManager SWindowManager = new WindowManager();
    static LanguageManager SLanguageManager = new LanguageManager();
    static TaskManager STaskManager = new TaskManager();
    static ResourceManager SResourceManager = new ResourceManager();
    static FileManager SFileManager = new FileManager();

    /** 初期化・終了実行順位 */
    private static AbstractManager[] aManager = { SResourceManager, SSystemManager, SLanguageManager, STaskManager, SDataManager, SFileManager, SWindowManager,
            SSoundManager, SPluginManager, };

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

        remakeAsc();
        remakeDesc();
        JMPFlags.Log.cprintln("###");
        JMPFlags.Log.cprintln("## CORE initializing");
        JMPFlags.Log.cprintln("##");
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

    static void remakeAsc() {
        if (asc == null) {
            asc = new ArrayList<AbstractManager>();
        }
        copyArray2ManagerList(asc, true);
    }

    static void remakeDesc() {
        if (desc == null) {
            desc = new ArrayList<AbstractManager>();
        }
        copyArray2ManagerList(desc, false);
    }

    private static void copyArray2ManagerList(List<AbstractManager> lst, boolean isAsc) {

        int startP, counter;
        if (isAsc == true) {
            startP = 0;
            counter = 1;
        }
        else {
            startP = aManager.length - 1;
            counter = -1;
        }

        // リストを空にする
        lst.clear();

        // マネージャーリストをコピー
        int i = startP;
        while (true) {
            if (i < 0 || i >= aManager.length) {
                break;
            }
            lst.add(aManager[i]);
            i += counter;
        }
    }

    public static List<AbstractManager> getManagersOfAsc() {
        if (asc == null) {
            remakeAsc();
        }
        return asc;
    }

    public static List<AbstractManager> getManagersOfDesc() {
        if (desc == null) {
            remakeDesc();
        }
        return desc;
    }
}
