package std;

import jlib.plugin.IPlugin;
import jmp.ConfigDatabaseWrapper;
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

    public static void exec(String args[], ConfigDatabaseWrapper config, IPlugin plugin) {
        boolean res = JMPLoader.invoke(config, plugin);
        System.exit(res ? 0 : 1);
    }

    public static void execSimple(String args[], IPlugin plugin) {
        JMPLoader.UsePluginDirectory = false;
        JMPLoader.UseConfigFile = false;
        JMPLoader.UseHistoryFile = false;
        JMPLoader.UseSkinFile = false;

        exec(args, null, plugin);
    }
}
