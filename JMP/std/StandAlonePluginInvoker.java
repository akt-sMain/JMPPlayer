package std;

import jlib.IPlugin;
import jmp.ConfigDatabase;
import jmp.JMPLoader;

/**
 * プラグインのスタンドアロン実行
 *
 * @author akkut
 *
 */
public class StandAlonePluginInvoker {
    public static void exec(String args[], IPlugin plugin) {
	exec(args, null, plugin);
    }

    public static void exec(String args[], ConfigDatabase config, IPlugin plugin) {
	boolean res = JMPLoader.invoke(config, plugin);
	System.exit(res ? 0 : 1);
    }
}
