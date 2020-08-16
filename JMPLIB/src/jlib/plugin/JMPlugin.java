package jlib.plugin;

import jlib.core.JMPCoreAccessor;

public abstract class JMPlugin implements IPlugin {

    public JMPlugin() {
    }

    /**
     * プラグイン名を取得する
     *
     * @return プラグイン名
     */
    public String getPluginName() {
        return JMPCoreAccessor.getSystemManager().getPluginName(this);
    }
}
