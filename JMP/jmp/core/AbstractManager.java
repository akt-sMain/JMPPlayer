package jmp.core;

import java.util.ArrayList;
import java.util.List;

import jlib.manager.IManager;
import jlib.manager.JMPCoreAccessor;

public abstract class AbstractManager implements IManager {

    public static final int MAX_PRIORITY = 9999;
    public static final int INVALID_PRIORITY = -1;

    private static List<AbstractManager> managers = new ArrayList<AbstractManager>();
    protected int priority = 0;
    protected String name = "";
    protected boolean initializeFlag = false;

    AbstractManager(int pri, String name) {
        this.priority = pri;
        this.name = name;
        managers.add(this);

        // アクセッサに登録
        JMPCoreAccessor.register(this);
    }

    public static void consolePrint(String msg) {
        System.out.print(msg);
    }

    public static void consolePrintln(String msg) {
        System.out.println(msg);
    }

    public static boolean init() {
        boolean result = true;
        consolePrintln("## CORE initializing ##");
        for (AbstractManager am : getCloneManagerList(true)) {
            if (am.getPriority() <= INVALID_PRIORITY) {
                // INVALID_PRIORITY は除去
                continue;
            }

            consolePrint(">> " + am.getName() + " init... ");
            if (result == true) {
                result = am.initFunc();
            }
            consolePrintln(result == true ? "success" : "fail");
        }
        consolePrintln(">> finished");
        consolePrintln("");
        return result;
    }

    public static boolean end() {
        boolean result = true;
        consolePrintln("");
        consolePrintln("## CORE exiting ##");
        for (AbstractManager am : getCloneManagerList(false)) {
            if (am.getPriority() <= INVALID_PRIORITY) {
                // INVALID_PRIORITY は除去
                continue;
            }

            consolePrint(">> " + am.getName() + " exit... ");
            if (result == true) {
                result = am.endFunc();
            }
            consolePrintln(result == true ? "success" : "fail");
        }
        consolePrintln(">> finished");
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

        if (order = true) {
            // 優先度が高い順にソート
            while (true) {
                int min = MAX_PRIORITY;
                AbstractManager addManager = null;
                for (AbstractManager am : cloneMng) {
                    curPriority = am.getPriority();
                    if (am.getPriority() <= min) {
                        addManager = am;
                        min = curPriority;
                    }
                }
                tmp.add(addManager);
                cloneMng.remove(addManager);

                if (managers.size() <= tmp.size()) {
                    break;
                }
            }
        }
        else {
            // 優先度が低い順にソート
            while (true) {
                int max = INVALID_PRIORITY;
                AbstractManager addManager = null;
                for (AbstractManager am : cloneMng) {
                    curPriority = am.getPriority();
                    if (am.getPriority() >= max) {
                        addManager = am;
                        max = curPriority;
                    }
                }
                tmp.add(addManager);
                cloneMng.remove(addManager);

                if (managers.size() <= tmp.size()) {
                    break;
                }
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

    public boolean initFunc() {
        if (initializeFlag == false) {
            initializeFlag = true;
        }
        return true;
    }

    public boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        return true;
    }

}
