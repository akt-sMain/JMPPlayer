package jmp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.swing.JOptionPane;

import function.Error;
import function.Platform;
import function.Utility;
import jlib.IMidiEventListener;
import jlib.IPlayerListener;
import jlib.IPlugin;
import jlib.ISupportExtensionConstraints;
import jmp.task.TaskOfMidiEvent.JmpMidiPacket;
import lib.MakeJmpLib;

/**
 * プラグイン管理クラス
 *
 * @author abs
 *
 */
public class PluginManager {
    // ---------------------------------------------
    // 定数
    // ---------------------------------------------

    // ---------------------------------------------
    // セットアップ用の定義
    // ---------------------------------------------

    /** zipファイル拡張子 */
    public static final String PLUGIN_ZIP_EX = "zip";

    /** setupファイル拡張子 */
    public static final String SETUP_FILE_EX = "jms";

    /** setup プラグインキー名 */
    public static final String SETUP_KEYNAME_PLUGIN = "PLUGIN";

    /** setup データキー名 */
    public static final String SETUP_KEYNAME_DATA = "DATA";

    /** setup リソースキー名 */
    public static final String SETUP_KEYNAME_RES = "RES";

    /** リムーブタグ */
    public static final String SETUP_REMOVE_TAG = "~";

    /** スキップタグ */
    public static final String SETUP_SKIP_TAG = "_";

    // ---------------------------------------------
    // 変数
    // ---------------------------------------------

    /** プラグイン格納用コレクション */
    private HashMap<String, IPlugin> aPlugins = new HashMap<String, IPlugin>();

    // ---------------------------------------------
    // 内部クラス
    // ---------------------------------------------
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
        private boolean isDeleteRequest = false;

        public JmsProperty(File jar, File data, File res) {
            setRes(res);
            setData(data);
            setJar(jar);
            this.isDeleteRequest = false;
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
    }

    // ---------------------------------------------
    // メソッド群
    // ---------------------------------------------
    PluginManager() {
    }

    // private static PluginManager singleton = new PluginManager();
    //
    // public static PluginManager getInstance() {
    // return singleton;
    // }

    public boolean initFunc() {

        // プラグイン読み込み
        if (JMPCore.StandAlonePlugin != null) {
            // スタンドアロンプラグインを登録
            String name = JMPCore.StandAlonePlugin.getClass().getName().trim();
            if (addPlugin(name, JMPCore.StandAlonePlugin) == false) {
                // return false;
            }
        }
        else {

            // 起動時に削除予定のプラグインを削除する
            removePlugin();

            if (JMPFlags.NonPluginLoadFlag == false) {
                File zipDir = new File(JMPCore.getSystemManager().getZipDirPath());
                if (zipDir.exists() == false) {
                    zipDir.mkdirs();
                }
                for (File f : zipDir.listFiles()) {
                    if (Utility.checkExtension(f.getPath(), PLUGIN_ZIP_EX) == false) {
                        continue;
                    }
                    if (readingPluginZipPackage(f.getPath(), false) == true) {
                        Utility.deleteFileDirectory(f);
                    }
                    Utility.threadSleep(100);
                }
                readingPlugin();
            }
        }

        // プラグイン初期化
        initialize();
        return true;
    }

    public boolean endFunc() {

        // プラグイン終了処理
        exit();
        return true;
    }

    public boolean readingPluginZipPackage(String path) {
        return readingPluginZipPackage(path, true);
    }

    public boolean readingPluginZipPackage(String path, boolean isImport) {
        boolean ret = true;
        String tmpDirectoryPath = Utility.pathCombin(Platform.getCurrentPath(false),
                Utility.stringsCombin("_", Utility.getFileNameNotExtension(path), Utility.getCurrentTimeStr()));

        File tmpDir = new File(tmpDirectoryPath);
        if (tmpDir.exists() == false) {
            tmpDir.mkdir();
        }

        if (JMPFlags.DebugMode == true) {
            System.out.println(tmpDirectoryPath);
            System.out.println("exists " + (tmpDir.exists() ? "TRUE" : "FALSE"));
        }

        try {
            Utility.unZip(path, tmpDirectoryPath);

            File jmsFile = null;
            for (File f : tmpDir.listFiles()) {
                if (Utility.checkExtension(f, SETUP_FILE_EX) == true) {
                    jmsFile = f;
                    break;
                }
            }

            if (jmsFile != null) {
                readingSetupFile(jmsFile, isImport);
            }
        }
        catch (Exception e) {
            ret = false;
            SystemManager.TempResisterEx = e;
        }
        finally {
            Utility.deleteFileDirectory(tmpDir);
        }
        return ret;
    }

    public void generatePluginZipPackage(String dirPath) {
        File pluginDir = new File(JMPCore.getSystemManager().getJmsDirPath());
        for (File f : pluginDir.listFiles()) {
            if (Utility.checkExtension(f, SETUP_FILE_EX) == true) {
                JmsProperty jms = getJmsProparty(f);
                String jar = "";
                if (jms.getJar() != null) {
                    jar = jms.getJar().getPath();
                }
                String data = "";
                if (jms.getData() != null) {
                    // data =
                    // Utility.pathCombin(JMPCore.getSystemManager().getDataFileLocationPath(),
                    // Utility.getFileNameNotExtension(jms.getJar()));
                    data = jms.getData().getPath();
                }
                String res = "";
                if (jms.getRes() != null) {
                    // res =
                    // Utility.pathCombin(JMPCore.getSystemManager().getResFileLocationPath(),
                    // Utility.getFileNameNotExtension(jms.getJar()));
                    res = jms.getRes().getPath();
                }

                if (Utility.isExsistFile(data) == true) {
                    // DATA無
                    MakeJmpLib.exportPackageForBlankData(jar, res, Utility.getFileNameNotExtension(f), dirPath);
                }
                else {
                    MakeJmpLib.exportPackage(jar, data, res, Utility.getFileNameNotExtension(f), dirPath);
                }
            }
        }
    }

    public boolean readingSetupFile(String path) {
        return readingSetupFile(new File(path), true);
    }

    public boolean readingSetupFile(File file) {
        return readingSetupFile(file, true);
    }

    public boolean readingSetupFile(String path, boolean isImport) {
        return readingSetupFile(new File(path), isImport);
    }

    public boolean readingSetupFile(File file, boolean isImport) {
        boolean ret = true;

        // 削除オプション
        boolean isDelete = false;
        BufferedReader reader;
        String line = "";

        if (Utility.checkExtension(file.getPath(), SETUP_FILE_EX) == false) {
            return false;
        }

        try {
            String jarName = "";
            File pluginFile = null;
            FileInputStream fs = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fs, "UTF-8");
            reader = new BufferedReader(isr);

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
                    if (key.equalsIgnoreCase(SETUP_KEYNAME_PLUGIN) == true) {
                        String src = Utility.stringsCombin(file.getParent(), Platform.getSeparator(), param);
                        String dst = Utility.stringsCombin(JMPCore.getSystemManager().getJarDirPath(),
                                Platform.getSeparator(), param);
                        Utility.copyFile(src, dst);

                        // Jar名を保持（jar名をパス名にする）
                        // ※Jar名とクラス名は一緒にすること
                        jarName = Utility.getFileNameNotExtension(dst);

                        // プラグインファイルを保持
                        pluginFile = new File(dst);
                    }
                    else if (key.equalsIgnoreCase(SETUP_KEYNAME_DATA) == true) {
                        if (param.equalsIgnoreCase("TRUE") == true) {
                            String src = Utility.stringsCombin(file.getParent(), Platform.getSeparator(),
                                    SETUP_KEYNAME_DATA);
                            String dst = Utility.stringsCombin(JMPCore.getSystemManager().getDataFileLocationPath(),
                                    Platform.getSeparator(), jarName);
                            File df = new File(dst);
                            if (df.exists() == false) {
                                df.mkdir();
                            }

                            if (Utility.isExsistFile(src) == true) {
                                Utility.copyFile(src, dst);
                            }
                        }
                    }
                    else if (key.equalsIgnoreCase(SETUP_KEYNAME_RES) == true) {
                        if (param.equalsIgnoreCase("TRUE") == true) {
                            String src = Utility.stringsCombin(file.getParent(), Platform.getSeparator(),
                                    SETUP_KEYNAME_RES);
                            String dst = Utility.stringsCombin(JMPCore.getSystemManager().getResFileLocationPath(),
                                    Platform.getSeparator(), jarName);
                            File df = new File(dst);
                            if (df.exists() == false) {
                                df.mkdir();
                            }

                            if (Utility.isExsistFile(src) == true) {
                                Utility.copyFile(src, dst);
                            }
                        }
                    }
                }
            }

            // jmsをコピー
            String src = file.getPath();

            String jmsName = Utility.getFileNameNotExtension(file) + "." + SETUP_FILE_EX;
            String dst = Utility.stringsCombin(JMPCore.getSystemManager().getJmsDirPath(), Platform.getSeparator(),
                    jmsName);
            Utility.copyFile(src, dst);

            // 最後にプラグインを追加
            if (isImport == true) {
                if (pluginFile != null) {
                    // プラグインを抽出
                    String name = importPlugin(pluginFile);
                    if (name != null) {
                        // プラグインをメニューに追加
                        IPlugin plugin = getPlugin(name);
                        plugin.initialize();
                        JMPCore.getSystemManager().getMainWindow().addPluginMenu(name, plugin);
                    }
                }
            }

            if (isDelete == true) {
                File delFile = new File(file.getPath());
                delFile = delFile.getParentFile();
                for (File f : delFile.listFiles()) {
                    f.delete();
                }
                delFile.delete();
            }

            reader.close();
        }
        catch (Exception e) {
            ret = false;
        }
        finally {
            reader = null;
        }
        return ret;
    }

    public IPlugin readingPlugin(File file) {
        if (Utility.checkExtension(file.getPath(), SETUP_FILE_EX) == false) {
            return null;
        }
        String fileName = Utility.getFileNameNotExtension(file.getPath());
        if (fileName.startsWith(SETUP_SKIP_TAG) == true) {
            // "_"で始まるファイルはスキップ
            return null;
        }

        JmsProperty jms = getJmsProparty(file);

        // プラグインをインポート
        String name = importPlugin(jms.getJar());
        return getPlugin(name);
    }

    private void readingPlugin() {
        /* プラグインディレクトリの存在を確認 */
        File dir = new File(JMPCore.getSystemManager().getJmsDirPath());
        if (dir.exists() == false) {
            if (dir.mkdir() == false) {
                return;
            }
        }
        for (File f : dir.listFiles()) {

            if (Utility.checkExtension(f.getPath(), SETUP_FILE_EX) == false) {
                continue;
            }
            String fileName = Utility.getFileNameNotExtension(f.getPath());
            if (fileName.startsWith(SETUP_SKIP_TAG) == true) {
                // "_"で始まるファイルはスキップ
                continue;
            }

            JmsProperty jms = getJmsProparty(f);

            // プラグインをインポート
            importPlugin(jms.getJar());
        }
    }

    private JmsProperty getJmsProparty(File jmsFile) {
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
                    if (key.equalsIgnoreCase(SETUP_KEYNAME_PLUGIN) == true) {
                        String plgPath = Utility.stringsCombin(JMPCore.getSystemManager().getJarDirPath(),
                                Platform.getSeparator(), param);

                        // プラグインファイルを保持
                        pluginFile = new File(plgPath);
                    }
                    else if (key.equalsIgnoreCase(SETUP_KEYNAME_DATA) == true) {
                        if (param.equalsIgnoreCase("TRUE") == true) {
                            isData = true;
                        }
                    }
                    else if (key.equalsIgnoreCase(SETUP_KEYNAME_RES) == true) {
                        if (param.equalsIgnoreCase("TRUE") == true) {
                            isRes = true;
                        }
                    }
                }
            }

            if (isData == true) {
                String dataPath = Utility.stringsCombin(JMPCore.getSystemManager().getDataFileLocationPath(),
                        Platform.getSeparator(), Utility.getFileNameNotExtension(pluginFile));

                dataFile = new File(dataPath);
            }
            if (isRes == true) {
                String resPath = Utility.stringsCombin(JMPCore.getSystemManager().getResFileLocationPath(),
                        Platform.getSeparator(), Utility.getFileNameNotExtension(pluginFile));

                resFile = new File(resPath);
            }

            jms = new JmsProperty(pluginFile, dataFile, resFile);

            String fileName = Utility.getFileNameAndExtension(jmsFile);
            if (fileName.startsWith(SETUP_REMOVE_TAG) == true) {
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

    public void removePlugin() {
        /* プラグインディレクトリの存在を確認 */
        File dir = new File(JMPCore.getSystemManager().getJmsDirPath());
        if (dir.exists() == false) {
            return;
        }
        for (File f : dir.listFiles()) {

            if (Utility.checkExtension(f.getPath(), SETUP_FILE_EX) == false) {
                continue;
            }
            String fileName = Utility.getFileNameNotExtension(f.getPath());
            if (fileName.startsWith(SETUP_REMOVE_TAG) == true) {
                // 削除
                removePlugin(f);
            }
        }
    }

    public void removePlugin(File jmsFile) {
        JmsProperty jms = getJmsProparty(jmsFile);

        File jar = jms.getJar();
        if (jar != null) {
            Utility.deleteFileDirectory(jar);
        }

        // File data = jms.getData();
        // if (data != null) {
        // Utility.deleteFileDirectory(data);
        // }
        File res = jms.getRes();
        if (res != null) {
            if (res.exists() == true) {
                Utility.deleteFileDirectory(res);
            }
        }
        Utility.deleteFileDirectory(jmsFile);
    }

    public void reserveRemovePlugin(File jmsFile) {
        reserveRemovePlugin(jmsFile, true);
    }

    public void reserveRemovePlugin(File jmsFile, boolean isVisibleMsg) {
        String name = Utility.getFileNameAndExtension(jmsFile);
        Utility.renameFile(jmsFile, Utility.stringsCombin(SETUP_REMOVE_TAG, name));

        if (isVisibleMsg == true) {
            JMPCore.getSystemManager().showMessageDialog("再起動時に削除します。", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String importPlugin(File jarFile) {
        String ret = null;

        try {
            URL[] urls = { jarFile.toURI().toURL() };
            ClassLoader loader = URLClassLoader.newInstance(urls);

            // クラスをロード
            Class<?> c = loader.loadClass(Utility.getFileNameNotExtension(jarFile.getPath()));
            if (IPlugin.class.isAssignableFrom(c) == true) {
                boolean result = true;
                IPlugin p = null;
                try {
                    p = (IPlugin) c.newInstance();
                }
                catch (Exception ex) {
                    result = false;
                }

                if (result == true) {
                    String name = c.getName().trim();
                    if (addPlugin(name, p, true) == true) {
                        ret = name;
                    }
                }
            }
        }
        catch (Exception e) {
            Error.copyMsg(e);
        }
        return ret;
    }

    public boolean addPlugin(String name, IPlugin plugin) {
        return addPlugin(name, plugin, false);
    }

    public boolean addPlugin(String name, IPlugin plugin, boolean isOverwrite) {
        if (aPlugins.containsKey(name) == true) {
            if (isOverwrite == true) {
                IPlugin rem = aPlugins.remove(name);
                if (rem != null) {
                    rem.close();
                    rem.exit();
                }
            }
            else {
                return false;
            }
        }
        aPlugins.put(name, plugin);
        return true;
    }

    public String getPluginName(IPlugin plugin) {
        String name = "";
        for (String key : aPlugins.keySet()) {
            if (plugin == aPlugins.get(key)) {
                name = key;
            }
        }
        return name;
    }

    public Set<String> getPluginsNameSet() {
        return aPlugins.keySet();
    }

    public IPlugin getPlugin(String name) {
        if (aPlugins.containsKey(name) == false) {
            return null;
        }
        return aPlugins.get(name);
    }

    public Collection<IPlugin> getPlugins() {
        return aPlugins.values();
    }

    public void initialize() {
        for (IPlugin plugin : getPlugins()) {
            plugin.initialize();
        }
    }

    public void exit() {
        for (IPlugin plugin : getPlugins()) {
            plugin.exit();
        }
    }

    public void update() {
        for (IPlugin plugin : getPlugins()) {
            plugin.update();
        }
    }

    public void startSequencer() {
        for (IPlugin plugin : getPlugins()) {
            if (plugin instanceof IPlayerListener) {
                IPlayerListener pi = (IPlayerListener) plugin;
                pi.startSequencer();
            }
        }
    }

    public void stopSequencer() {
        for (IPlugin plugin : getPlugins()) {
            if (plugin instanceof IPlayerListener) {
                IPlayerListener pi = (IPlayerListener) plugin;
                pi.stopSequencer();
            }
        }
    }

    public void updateTickPosition(long before, long after) {
        for (IPlugin plugin : getPlugins()) {
            if (plugin instanceof IPlayerListener) {
                IPlayerListener pi = (IPlayerListener) plugin;
                pi.updateTickPosition(before, after);
            }
        }
    }

    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        // Midiイベント受信後の処理は別スレッドに委譲する
        TaskManager.getInstance().getTaskOfMidiEvent().add(message, timeStamp, senderType);
    }

    public void send(JmpMidiPacket packet) {
        for (IPlugin plugin : getPlugins()) {
            if (plugin instanceof IMidiEventListener) {
                IMidiEventListener mi = (IMidiEventListener) plugin;
                mi.catchMidiEvent(packet.message, packet.timeStamp, packet.senderType);
            }
        }
    }

    public void loadFile(File file) {
        for (IPlugin plugin : getPlugins()) {
            plugin.loadFile(file);
        }
    }

    public void closeAllPlugins() {
        for (IPlugin plugin : getPlugins()) {
            if (plugin.isOpen() == true) {
                plugin.close();
            }
        }
    }

    public void closeNonSupportPlugins(String ex) {
        if (JMPCore.StandAlonePlugin != null) {
            // スタンドアロンモードの時は無効
            return;
        }

        for (IPlugin plugin : getPlugins()) {
            if (plugin instanceof ISupportExtensionConstraints) {
                ISupportExtensionConstraints sec = (ISupportExtensionConstraints) plugin;
                String[] allowsEx = sec.allowedExtensionsArray();
                boolean ret = false;
                for (String ae : allowsEx) {
                    if (ae.equalsIgnoreCase(ex) == true) {
                        ret = true;
                        break;
                    }
                }

                if (ret == false) {
                    if (plugin.isOpen() == true) {
                        plugin.close();
                    }
                }
            }
        }
    }
}
