package jmp.plugin;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import function.Utility;
import jlib.IPlugin;

public class JMPPluginLoader {

    public static IPlugin load(File jarFile){
        IPlugin plugin = null;
        try {
            URL[] urls = { jarFile.toURI().toURL() };
            ClassLoader loader = URLClassLoader.newInstance(urls);

            // クラスをロード
            Class<?> c = loader.loadClass(Utility.getFileNameNotExtension(jarFile.getPath()));
            if (IPlugin.class.isAssignableFrom(c) == true) {
                try {
                    plugin = (IPlugin) c.newInstance();
                }
                catch (Exception ex) {
                }
            }
        }
        catch (Exception e) {
            plugin = null;
        }
        return plugin;
    }

}
