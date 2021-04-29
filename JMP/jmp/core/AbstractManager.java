package jmp.core;

import java.util.ArrayList;
import java.util.List;

import jlib.core.IManager;
import jlib.core.JMPCoreAccessor;
import jmp.JMPFlags;

public abstract class AbstractManager implements IManager {

    public static final int MAX_PRIORITY = 9999;
    public static final int INVALID_PRIORITY = -1;

    private static List<AbstractManager> managers = null;
    static List<AbstractManager> asc = null;
    static List<AbstractManager> desc = null;
    protected int priority = 0;
    protected String name = "";
    private boolean initializeFlag = false;

    AbstractManager(int pri, String name) {
        this.priority = pri;
        this.name = name;

        if (managers == null) {
            managers = new ArrayList<AbstractManager>();
        }
        managers.add(this);

        // アクセッサに登録
        JMPCoreAccessor.register(this);
    }

    static boolean isFinishedAllInitialize() {
        for (AbstractManager am : managers) {
            if (am.getPriority() <= INVALID_PRIORITY) {
                // INVALID_PRIORITY は除去
                continue;
            }

            if (am.isFinishedInitialize() == false) {
                return false;
            }
        }
        return true;
    }

    static boolean callInitFunc() {
        boolean result = true;
        asc = getCloneManagerList(true);
        desc = getCloneManagerList(false);
        JMPFlags.Log.cprintln("###");
        JMPFlags.Log.cprintln("## CORE initializing");
        JMPFlags.Log.cprintln("##");
        for (AbstractManager am : asc) {
            if (am.getPriority() <= INVALID_PRIORITY) {
                // INVALID_PRIORITY は除去
                continue;
            }

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
        for (AbstractManager am : desc) {
            if (am.getPriority() <= INVALID_PRIORITY) {
                // INVALID_PRIORITY は除去
                continue;
            }

            JMPFlags.Log.cprint(">> " + am.getName() + " exit... ");
            if (result == true) {
                result = am.endFunc();
            }
            JMPFlags.Log.cprintln(result == true ? "success" : "fail");
        }
        JMPFlags.Log.cprintln("## finished");
        return result;
    }

    private static List<AbstractManager> getCloneManagerList(boolean order) {
        // マネージャーリストをコピー
        List<AbstractManager> cloneMng = new ArrayList<AbstractManager>();
        for (AbstractManager am : managers) {
            cloneMng.add(am);
        }

        int curPriority = INVALID_PRIORITY;
        List<AbstractManager> tmp = new ArrayList<AbstractManager>();

        while (true) {
            int limit = (order == true) ? MAX_PRIORITY : INVALID_PRIORITY;
            AbstractManager addManager = null;
            for (AbstractManager am : cloneMng) {
                curPriority = am.getPriority();

                if (order == true) {
                    if (am.getPriority() <= limit) {
                        addManager = am;
                        limit = curPriority;
                    }
                }
                else {
                    if (am.getPriority() >= limit) {
                        addManager = am;
                        limit = curPriority;
                    }
                }
            }
            tmp.add(addManager);
            cloneMng.remove(addManager);

            if (managers.size() <= tmp.size()) {
                break;
            }
        }
        return tmp;
    }

    public int getPriority() {
        return priority;
    }

    public String getName() {
        return name;
    }

    protected boolean initFunc() {
        initializeFlag = true;
        return true;
    }

    protected boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        return true;
    }

    // Config更新通知
    protected void notifyUpdateConfig(String key) {
    }

    // cReg更新通知
    protected void notifyUpdateCommonRegister(String key) {
    }

    public boolean isFinishedInitialize() {
        return initializeFlag;
    }

}
