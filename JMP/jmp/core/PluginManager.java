package jmp.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.swing.JOptionPane;

import function.Platform;
import function.Utility;
import jlib.plugin.IPlugin;
import jlib.plugin.ISupportExtensionConstraints;
import jmp.JMPFlags;
import jmp.plugin.JMPPluginLoader;
import jmp.plugin.JmsProperty;
import jmp.plugin.PluginObserver;
import jmp.task.TaskOfMidiEvent.JmpMidiPacket;
import lib.MakeJmpLib;

/**
 * プラグイン管理クラス
 *
 * @author abs
 *
 */
public class PluginManager extends AbstractManager {
    // ---------------------------------------------
    // 定数
    // ---------------------------------------------

    // ---------------------------------------------
    // セットアップ用の定義
    // ---------------------------------------------

    /** zipファイル拡張子 */
    public static final String PLUGIN_ZIP_EX = MakeJmpLib.PKG_ZIP_EX;

    /** setupファイル拡張子 */
    public static final String SETUP_FILE_EX = MakeJmpLib.PKG_SETUP_EX;

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

    /** プラグインオブザーバ */
    private PluginObserver observers = null;

    // ---------------------------------------------
    // メソッド群
    // ---------------------------------------------
    PluginManager(int pri) {
        super(pri, "plugin");
    }

    protected boolean initFunc() {
        super.initFunc();

        observers = new PluginObserver();

        return true;
    }

    protected boolean endFunc() {
        super.endFunc();

        // プラグイン終了処理
        exit();
        return true;
    }

    public void startupPluginInstance() {

        // プラグイン読み込み
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            // スタンドアロンプラグインを登録
            String name = JMPCore.StandAlonePlugin.getClass().getName().trim();
            if (addPlugin(name, JMPCore.StandAlonePlugin) == false) {
                // return false;
            }
        }
        else {
            // 起動時に削除予定のプラグインを削除する
            removePlugin();

            // プラグインディレクトリのロード
            readingPlugin();
        }

        // プラグイン初期化
        initialize();

        if (JMPFlags.NonPluginLoadFlag == false) {
            // jmzフォルダ内のインポート処理
            readingJmzDirectory();
        }

    }

    public void readingJmzDirectory() {
        File zipDir = new File(JMPCore.getSystemManager().getZipDirPath());
        if (zipDir.exists() == false) {
            zipDir.mkdirs();
        }
        for (File f : zipDir.listFiles()) {
            if (Utility.checkExtension(f.getPath(), PLUGIN_ZIP_EX) == false) {
                continue;
            }
            if (readingJmzPackage(f.getPath(), true) == true) {
                Utility.deleteFileDirectory(f);
            }
            Utility.threadSleep(100);
        }
    }

    public boolean readingJmzPackage(String path) {
        return readingJmzPackage(path, true);
    }

    public boolean readingJmzPackage(String path, boolean isImport) {
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
                readingJmsFile(jmsFile, isImport);
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
                JmsProperty jms = JmsProperty.getJmsProparty(f);
                String jar = "";
                if (jms.getJar() != null) {
                    jar = jms.getJar().getPath();
                }
                String data = "";
                if (jms.getData() != null) {
                    data = jms.getData().getPath();
                }
                String res = "";
                if (jms.getRes() != null) {
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

    public boolean readingJmsFile(String path) {
        return readingJmsFile(new File(path), true);
    }

    public boolean readingJmsFile(File file) {
        return readingJmsFile(file, true);
    }

    public boolean readingJmsFile(String path, boolean isImport) {
        return readingJmsFile(new File(path), isImport);
    }

    public boolean readingJmsFile(File file, boolean isImport) {
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
                        String dst = Utility.stringsCombin(JMPCore.getSystemManager().getJarDirPath(), Platform.getSeparator(), param);
                        Utility.copyFile(src, dst);

                        // Jar名を保持（jar名をパス名にする）
                        // ※Jar名とクラス名は一緒にすること
                        jarName = Utility.getFileNameNotExtension(dst);

                        // プラグインファイルを保持
                        pluginFile = new File(dst);
                    }
                    else if (key.equalsIgnoreCase(SETUP_KEYNAME_DATA) == true) {
                        if (param.equalsIgnoreCase("TRUE") == true) {
                            String src = Utility.stringsCombin(file.getParent(), Platform.getSeparator(), SETUP_KEYNAME_DATA);
                            String dst = Utility.stringsCombin(JMPCore.getSystemManager().getDataFileLocationPath(), Platform.getSeparator(), jarName);
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
                            String src = Utility.stringsCombin(file.getParent(), Platform.getSeparator(), SETUP_KEYNAME_RES);
                            String dst = Utility.stringsCombin(JMPCore.getSystemManager().getResFileLocationPath(), Platform.getSeparator(), jarName);
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
            String dst = Utility.stringsCombin(JMPCore.getSystemManager().getJmsDirPath(), Platform.getSeparator(), jmsName);
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
                        JMPCore.getWindowManager().getMainWindow().addPluginMenu(name, plugin);
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

        JmsProperty jms = JmsProperty.getJmsProparty(file);

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


            JmsProperty jms = JmsProperty.getJmsProparty(f);

            // プラグインをインポート
            importPlugin(jms.getJar());
        }
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
        JmsProperty jms = JmsProperty.getJmsProparty(jmsFile);

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
            JMPCore.getWindowManager().showMessageDialog("再起動時に削除します。", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private String importPlugin(File jarFile) {
        String ret = null;
        IPlugin p = JMPPluginLoader.load(jarFile);
        if (p != null) {
            String name = p.getClass().getName().trim();
            if (addPlugin(name, p, true) == true) {
                ret = name;
            }
        }
        return ret;
    }

    public boolean addPlugin(String name, IPlugin plugin) {
        return addPlugin(name, plugin, false);
    }

    public boolean addPlugin(String name, IPlugin plugin, boolean isOverwrite) {
        return observers.addPlugin(name, plugin, isOverwrite);
    }

    public String getPluginName(IPlugin plugin) {
        return observers.getPluginName(plugin);
    }

    public Set<String> getPluginsNameSet() {
        return observers.getPluginsNameSet();
    }

    public IPlugin getPlugin(String name) {
        return observers.getPlugin(name);
    }

    public void initialize() {
        observers.initialize();
    }

    public void exit() {
        observers.exit();
    }

    public void update() {
        observers.update();
    }

    public void startSequencer() {
        observers.startSequencer();
    }

    public void stopSequencer() {
        observers.stopSequencer();
    }

    public void updateTickPosition(long before, long after) {
        observers.updateTickPosition(before, after);
    }

    public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        // Midiイベント受信後の処理は別スレッドに委譲する
        JMPCore.getTaskManager().getTaskOfMidiEvent().add(message, timeStamp, senderType);
    }

    public void send(JmpMidiPacket packet) {
        observers.catchMidiEvent(packet.message, packet.timeStamp, packet.senderType);
    }

    void loadFile(File file) {
        for (IPlugin plugin : observers.getPlugins()) {
            boolean ret = false;
            if (plugin instanceof ISupportExtensionConstraints) {
                ISupportExtensionConstraints sec = (ISupportExtensionConstraints) plugin;
                String[] allowsEx = sec.allowedExtensionsArray();
                for (String ae : allowsEx) {
                    if (Utility.checkExtension(file, ae) == true) {
                        ret = true;
                        break;
                    }
                }
            }
            else {
                ret = true;
            }

            if (ret == true) {
                plugin.loadFile(file);
            }
        }
    }

    public void closeAllPlugins() {
        observers.close();
    }

    public void closeNonSupportPlugins(String ex) {
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            // スタンドアロンモードの時は無効
            return;
        }

        for (IPlugin plugin : observers.getPlugins()) {
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

    @Override
    protected void notifyUpdateCommonRegister(String key) {
        super.notifyUpdateCommonRegister(key);
        observers.notifyUpdateCommonRegister(key);
    }

    @Override
    protected void notifyUpdateConfig(String key) {
        super.notifyUpdateConfig(key);
        observers.notifyUpdateConfig(key);
    }
}
