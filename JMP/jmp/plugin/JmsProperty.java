package jmp.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import function.Platform;
import function.Utility;
import jmp.core.JMPCore;
import jmp.core.PluginManager;

/**
 * JMSファイルのプロパティクラス
 *
 * @author akkut
 *
 */
public class JmsProperty {
    private File data;
    private File res;
    private File jar;
    private String version = "";
    private boolean isDeleteRequest = false;

    public JmsProperty(File jar, File data, File res, String version) {
        setRes(res);
        setData(data);
        setJar(jar);
        setVersion(version);
        this.isDeleteRequest = false;
    }

    public static JmsProperty getJmsProparty(File jmsFile) {
        JmsProperty jms = null;

        BufferedReader reader;
        String line = "";
        try {
            File pluginFile = null;
            File dataFile = null;
            File resFile = null;

            FileInputStream fs = new FileInputStream(jmsFile);
            InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
            reader = new BufferedReader(isr);

            boolean isData = false;
            boolean isRes = false;
            String version = "";

            while ((line = reader.readLine()) != null) {
                /* コメントを除外 */
                String[] comment = line.split("#", 0);
                if (comment.length > 0) {
                    line = comment[0];
                }

                String[] sLine = line.split("=", 0);
                if (sLine.length >= 2) {
                    String key = sLine[0].trim();
                    String param = sLine[1].trim();
                    if (key.equalsIgnoreCase(PluginManager.SETUP_KEYNAME_PLUGIN) == true) {
                        String plgPath = Utility.stringsCombin(JMPCore.getSystemManager().getJarDirPath(), Platform.getSeparator(), param);

                        // プラグインファイルを保持
                        pluginFile = new File(plgPath);
                    }
                    else if (key.equalsIgnoreCase(PluginManager.SETUP_KEYNAME_DATA) == true) {
                        if (param.equalsIgnoreCase("TRUE") == true) {
                            isData = true;
                        }
                    }
                    else if (key.equalsIgnoreCase(PluginManager.SETUP_KEYNAME_RES) == true) {
                        if (param.equalsIgnoreCase("TRUE") == true) {
                            isRes = true;
                        }
                    }
                    else if (key.equalsIgnoreCase(PluginManager.SETUP_KEYNAME_VERSION) == true) {
                        version = param;
                    }
                }
            }

            if (isData == true) {
                String dataPath = Utility.stringsCombin(JMPCore.getSystemManager().getDataFileLocationPath(), Platform.getSeparator(),
                        Utility.getFileNameNotExtension(pluginFile));

                dataFile = new File(dataPath);
            }
            if (isRes == true) {
                String resPath = Utility.stringsCombin(JMPCore.getSystemManager().getResFileLocationPath(), Platform.getSeparator(),
                        Utility.getFileNameNotExtension(pluginFile));

                resFile = new File(resPath);
            }

            jms = new JmsProperty(pluginFile, dataFile, resFile, version);

            String fileName = Utility.getFileNameAndExtension(jmsFile);
            if (fileName.startsWith(PluginManager.SETUP_REMOVE_TAG) == true) {
                jms.setDeleteRequest(true);
            }
            reader.close();
        }
        catch (Exception e) {
            jms = null;
        }
        finally {
            reader = null;
        }
        return jms;
    }

    public File getData() {
        return data;
    }

    public void setData(File data) {
        this.data = data;
    }

    public File getJar() {
        return jar;
    }

    public void setJar(File jar) {
        this.jar = jar;
    }

    public boolean isDeleteRequest() {
        return isDeleteRequest;
    }

    public void setDeleteRequest(boolean isDeleteRequest) {
        this.isDeleteRequest = isDeleteRequest;
    }

    public File getRes() {
        return res;
    }

    public void setRes(File res) {
        this.res = res;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
