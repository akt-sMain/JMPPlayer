package jmp;

import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import function.Platform;
import function.Utility;
import jlib.IJmpMainWindow;
import jlib.IPlugin;
import jlib.manager.ISystemManager;
import jlib.manager.JMPCoreAccessor;
import jmp.gui.LicenseReaderDialog;
import jmp.task.ICallbackFunction;

/**
 * システム管理クラス
 *
 * @author abs
 *
 */
public class SystemManager implements ISystemManager {

    public class CommonRegister {
        public static final String COMMON_REGKEY_PLAYER_BACK_COLOR = "player_back_color";

        public static final String COMMON_REGKEY_CH_COLOR_FORMAT = "ch_color_%d";

        public static final String COMMON_REGKEY_EXTENSION_MIDI = "extension_midi";
        public static final String COMMON_REGKEY_EXTENSION_WAV = "extension_wav";

        private HashMap<String, String> map;

        public CommonRegister() {
            map = new HashMap<String, String>();
        }

        public void load() {
        }

        public void init() {
            add(COMMON_REGKEY_PLAYER_BACK_COLOR, Utility.convertHtmlColorToCode(Color.DARK_GRAY));
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 1), "#8ec21f");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 2), "#3dc21f");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 3), "#1fc253");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 4), "#1fc2a4");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 5), "#1f8ec2");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 6), "#1f3dc2");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 7), "#531fc2");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 8), "#a41fc2");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 9), "#ffc0cb");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 10), "#c21f3d");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 11), "#c2531f");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 12), "#c2a41f");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 13), "#3d00c2");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 14), "#ffff29");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 15), "#bbff29");
            add(String.format(COMMON_REGKEY_CH_COLOR_FORMAT, 16), "#f98608");

            add(COMMON_REGKEY_EXTENSION_MIDI, genExtensionsStr(DataManager.ExtentionForMIDI));
            add(COMMON_REGKEY_EXTENSION_WAV, genExtensionsStr(DataManager.ExtentionForWAV));
        }

        private String genExtensionsStr(String... ex) {
            String ret = "";
            for (int i = 0; i < ex.length; i++) {
                if (i > 0) {
                    ret += ",";
                }
                ret += ex[i];
            }
            return ret;
        }

        public void add(String key, String value) {
            map.put(key, value);
        }

        public boolean setValue(String key, String value) {
            if (map.containsKey(key) == true) {
                map.put(key, value);
                return true;
            }
            return false;
        }

        public String getValue(String key) {
            if (map.containsKey(key) == true) {
                return map.get(key);
            }
            return "";
        }
    }

    /** 例外メッセージのテンポラリ */
    public static Exception TempResisterEx = null;

    /** データディレクトリ名 */
    public static final String PLUGINS_DIR_NAME = "plugins";

    /** データディレクトリ名 */
    public static final String DATA_DIR_NAME = "data";

    /** リソースディレクトリ名 */
    public static final String RES_DIR_NAME = "res";

    /** JMSディレクトリ名 */
    public static final String JMS_DIR_NAME = "jms";

    /** JARディレクトリ名 */
    public static final String JAR_DIR_NAME = "jar";

    /** ZIPパッケージディレクトリ名 */
    public static final String ZIP_DIR_NAME = "zip";

    // 初期化フラグ
    private boolean initializeFlag = false;

    /** メインウィンドウ */
    public IJmpMainWindow mainWindow = null;

    /** 共通レジスタ */
    private CommonRegister cReg = null;

    /** ライセンス表示ダイアログ */
    private LicenseReaderDialog licenseDialog = null;

    SystemManager() {
        // アクセッサに登録
        JMPCoreAccessor.register(this);
    }

    public boolean initFunc() {
        boolean result = true;
        TempResisterEx = null;

        // 共通レジスタのインスタンス生成
        cReg = new CommonRegister();
        cReg.init();
        cReg.load();

        // ルックアンドフィールの設定
        setupLookAndFeel();

        File pluginsDir = new File(getPluginsDirPath());
        if (pluginsDir.exists() == false) {
            // プラグインフォルダ作成
            pluginsDir.mkdirs();
        }
        File dataDir = new File(getDataFileLocationPath());
        if (dataDir.exists() == false) {
            // データフォルダ作成
            dataDir.mkdirs();
        }
        File resDir = new File(getResFileLocationPath());
        if (resDir.exists() == false) {
            // resフォルダ作成
            resDir.mkdirs();
        }
        File jarDir = new File(getJarDirPath());
        if (jarDir.exists() == false) {
            // Jarフォルダ作成
            jarDir.mkdirs();
        }
        File jmsDir = new File(getJmsDirPath());
        if (jmsDir.exists() == false) {
            // jmsフォルダ作成
            jmsDir.mkdirs();
        }

        /* ライセンス確認ダイアログのインスタンス生成 */
        licenseDialog = new LicenseReaderDialog();

        /* アクティベート状況の確認 */
        if (Utility.isExsistFile(getActivateFileLocationPath()) == true) {
            JMPFlags.ActivateFlag = true;
        }
        else {
            JMPFlags.ActivateFlag = false;
        }

        /* スタンドアロンモード or デバッグモードの際はアクティベートする */
        if (JMPFlags.DebugMode == true || JMPCore.StandAlonePlugin != null) {
            JMPFlags.ActivateFlag = true;
        }

        /* ライセンス確認 */
        if (JMPFlags.ActivateFlag == false) {
            getLicenseDialog().start();
        }

        if (JMPFlags.ActivateFlag == false) {
            // アクティベートされなかった場合は終了する
            result = false;
        }

        if (initializeFlag == false) {
            initializeFlag = true;
        }
        return result;
    }

    public boolean endFunc() {
        /* アクティベート処理 */
        boolean activateOutFlag = false;
        if (JMPFlags.ActivateFlag == true) {
            activateOutFlag = true;
        }
        if (JMPFlags.DebugMode == true || JMPCore.StandAlonePlugin != null) {
            // デバッグ・スタンドアロン実行は発行しない
            activateOutFlag = false;
        }
        // ライセンス発行
        if (activateOutFlag == true) {
            if (Utility.isExsistFile(getActivateFileLocationPath()) == false) {
                try {
                    String text = "ライセンス認証のためのファイルです。" + Platform.getNewLine() + "このファイルを削除してもソフトウェアの動作には影響ありません。";
                    Utility.outputTextFile(getActivateFileLocationPath(), text);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public LicenseReaderDialog getLicenseDialog() {
        return licenseDialog;
    };

    public boolean setCommonRegisterValue(String key, String value) {
        if (cReg == null) {
            return false;
        }
        return cReg.setValue(key, value);
    }

    public String getCommonRegisterValue(String key) {
        if (cReg == null) {
            return "";
        }
        return cReg.getValue(key);
    }

    /**
     * データファイル格納ディレクトリパス
     *
     * @return パス
     */
    public String getDataFileLocationPath() {
        String path = "";
        path = Utility.stringsCombin(getPluginsDirPath(), Platform.getSeparator(), DATA_DIR_NAME);
        return path;
    }

    /**
     * リソースファイル格納ディレクトリパス
     *
     * @return
     */
    public String getResFileLocationPath() {
        String path = "";
        path = Utility.stringsCombin(getPluginsDirPath(), Platform.getSeparator(), RES_DIR_NAME);
        return path;
    }

    public String getPluginsDirPath() {
        return Utility.stringsCombin(Platform.getCurrentPath(), PLUGINS_DIR_NAME);
    }

    public String getJmsDirPath() {
        return Utility.stringsCombin(getPluginsDirPath(), Platform.getSeparator(), JMS_DIR_NAME);
    }

    public String getJarDirPath() {
        return Utility.stringsCombin(getPluginsDirPath(), Platform.getSeparator(), JAR_DIR_NAME);
    }

    public String getZipDirPath() {
        return Utility.stringsCombin(Platform.getCurrentPath(), ZIP_DIR_NAME);
    }

    /**
     * アクティベート状態ファイル
     *
     * @return
     */
    public String getActivateFileLocationPath() {
        String path = "";
        path = Utility.stringsCombin(Platform.getCurrentPath(), Platform.getSeparator(), "activate");
        return path;
    }

    /**
     * ルックアンドフィールの設定
     */
    private void setupLookAndFeel() {
        String lf = UIManager.getSystemLookAndFeelClassName();
        try {
            UIManager.setLookAndFeel(lf);
        }
        catch (Exception e) {
            System.out.println("lferror");

            // 念のためMetalを再設定
            try {
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            }
            catch (Exception e2) {
            }
        }
    }

    /**
     * メインウィンドウ取得
     *
     * @return メインウィンドウ
     */
    public IJmpMainWindow getMainWindow() {
        return mainWindow;
    }

    /**
     * メインウィンドウ登録
     *
     * @param mainWindow
     *            メインウィンドウ
     * @return 結果
     */
    public boolean registerMainWindow(IJmpMainWindow mainWindow) {
        boolean ret = false;
        if (this.mainWindow == null) {
            // 未登録時のみ
            this.mainWindow = mainWindow;
            ret = true;
        }
        return ret;
    }

    @Override
    public String getPluginName(IPlugin plugin) {
        // プラグインマネージャーに問い合わせ
        return JMPCore.getPluginManager().getPluginName(plugin);
    }

    public void showErrorMessageDialog(String message) {
        if (SystemManager.TempResisterEx != null) {
            String stackTrace = function.Error.getMsg(SystemManager.TempResisterEx);
            showMessageDialog(Utility.stringsCombin(message, Platform.getNewLine(), Platform.getNewLine(), stackTrace),
                    "エラー", JOptionPane.ERROR_MESSAGE);
        }
        else {
            showMessageDialog(message, "エラー", JOptionPane.ERROR_MESSAGE);
        }
        SystemManager.TempResisterEx = null;
    }

    public void showInformationMessageDialog(String message) {
        showMessageDialog(message, "メッセージ", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showMessageDialog(String message, int option) {
        showMessageDialog(message, "メッセージ", option);
    }

    public void showMessageDialog(String message, String title, int option) {
        TaskManager.getInstance().getTaskOfSequence().queuing(new ICallbackFunction() {
            @Override
            public void callback() {
                IJmpMainWindow win = getMainWindow();
                Component parent = null;
                if (win instanceof Component) {
                    parent = (Component) win;
                }
                else {
                    parent = null;
                }
                JOptionPane.showMessageDialog(parent, message, title, option);
            }
        });
    }
}
