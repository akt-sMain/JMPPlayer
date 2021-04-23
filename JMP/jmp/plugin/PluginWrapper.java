package jmp.plugin;

import jlib.plugin.IPlugin;

public class PluginWrapper {
    public static enum PluginState {
        CONNECTED,
        DISCONNECTED,
        INVALID;
    }

    public static PluginState toPluginState(String str) {
        if (str.equalsIgnoreCase("INVALID") == true) {
            return PluginState.INVALID;
        }
        else if (str.equalsIgnoreCase("DISCONNECTED") == true) {
            return PluginState.DISCONNECTED;
        }
        else {
            return PluginState.CONNECTED;
        }
    }
    public static String toString(PluginState pStat) {
        switch (pStat) {
            case DISCONNECTED:
                return "DISCONNECTED";
            case INVALID:
                return "INVALID";
            case CONNECTED:
            default:
                return "CONNECTED";
        }
    }

    private IPlugin plugin = null;
    private PluginState state = PluginState.CONNECTED;

    public PluginWrapper(IPlugin plg) {
        this.plugin = plg;
        this.state = PluginState.CONNECTED;
    }

    public void setPlugin(IPlugin plg) {
        plugin = plg;
    }
    public IPlugin getPlugin() {
        return plugin;
    }

    public PluginState getState() {
        return state;
    }

    public void setState(PluginState state) {
        this.state = state;
    }
}
