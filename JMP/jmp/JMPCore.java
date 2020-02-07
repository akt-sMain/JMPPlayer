package jmp;

import jlib.IPlugin;
import jlib.JMPLIB;

public class JMPCore {

    /** アプリケーション名 */
    public static final String APPLICATION_NAME = "JamPlayer";

    /** アプリケーションバージョン */
    public static final String APPLICATION_VERSION = "0.01(β)";

    /** ライブラリバージョン */
    public static final String LIBRALY_VERSION = JMPLIB.BUILD_VERSION;

    /** スタンドアロンモードのプラグイン */
    public static IPlugin StandAlonePlugin = null;

    private static SystemManager systemManager = new SystemManager();
    private static DataManager dataManager = new DataManager();
    private static SoundManager soundManager = new SoundManager();
    private static PluginManager pluginManager = new PluginManager();
    private static WindowManager windowManager = new WindowManager();

    public static void consolePrint(String msg) {
	System.out.print(msg);
    }
    public static void consolePrintln(String msg) {
	System.out.println(msg);
    }

    public static boolean initFunc() {
	boolean result = true;
	consolePrintln("## CORE initializing ##");

	// system
	consolePrint(">> system init... ");
	if (result == true) {
	    result = systemManager.initFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	consolePrint(">> window init... ");
	if (result == true) {
	    result = windowManager.initFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	// data
	consolePrint(">> data init... ");
	if (result == true) {
	    result = dataManager.initFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	// sound
	consolePrint(">> sound init... ");
	if (result == true) {
	    result = soundManager.initFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	// plugin
	consolePrint(">> plugin init... ");
	if (result == true) {
	    result = pluginManager.initFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	consolePrintln(">> finished");
	consolePrintln("");
	return result;
    }

    public static boolean endFunc() {
	boolean result = true;

	consolePrintln("");
	consolePrintln("## CORE exiting ##");

	consolePrint(">> plugin exit... ");
	if (result == true) {
	    result = pluginManager.endFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	consolePrint(">> sound exit... ");
	if (result == true) {
	    result = soundManager.endFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	consolePrint(">> data exit... ");
	if (result == true) {
	    result = dataManager.endFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	consolePrint(">> window exit... ");
	if (result == true) {
	    result = windowManager.endFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	consolePrint(">> system exit... ");
	if (result == true) {
	    result = systemManager.endFunc();
	}
	consolePrintln(result == true ? "success" : "fail");

	consolePrintln(">> finished");

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

}
