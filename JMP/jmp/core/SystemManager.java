package jmp.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.UIManager;

import function.Platform;
import function.Utility;
import jlib.core.ISystemManager;
import jlib.plugin.IPlugin;
import jmp.CommonRegister;
import jmp.JMPFlags;
import jmp.JMPLoader;
import wffmpeg.FFmpegWrapper;
import wrapper.IProcessingCallback;
import wrapper.ProcessingFFmpegWrapper;

/**
 * システム管理クラス
 *
 * @author abs
 *
 */
public class SystemManager extends AbstractManager implements ISystemManager {

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
    public static final String ZIP_DIR_NAME = "jmz";

    /** Outputディレクトリ名 */
    public static final String OUTPUT_DIR_NAME = "output";

    /** 共通レジスタ */
    private CommonRegister cReg = null;

    /** FFmpeg wrapper インスタンス */
    private FFmpegWrapper ffmpegWrapper = null;

    // システムパス変数
    private String dataFileLocationPath = "";
    private String resFileLocationPath = "";
    private String pluginsDirPath = "";
    private String jmsDirPath = "";
    private String jarDirPath = "";
    private String zipDirPath = "";
    private String outputPath = "";
    private String activateFileLocationPath = "";

    SystemManager(int pri) {
        super(pri, "system");
    }

    protected boolean initFunc() {
        boolean result = true;
        TempResisterEx = null;

        // 共通レジスタのインスタンス生成
        cReg = new CommonRegister();
        cReg.init();
        cReg.load();

        // ResourceとcRegの同期
        setCommonRegisterValue(CommonRegister.COMMON_REGKEY_PLAYER_BACK_COLOR,
                Utility.convertHtmlColorToCode(JMPCore.getResourceManager().getAppBackgroundColor()));

        // ルックアンドフィールの設定
        setupLookAndFeel();

        // FFmpegWrapperインスタンス生成
        ffmpegWrapper = new ProcessingFFmpegWrapper();
        ffmpegWrapper.setOverwrite(true);

        if (JMPLoader.UsePluginDirectory == true) {
            File pluginsDir = new File(getPluginsDirPath());
            if (pluginsDir.exists() == false) {
                // プラグインフォルダ作成
                pluginsDir.mkdirs();
            }
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
        if (JMPLoader.UsePluginDirectory == true) {
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
            File outDir = new File(getOutputPath());
            if (outDir.exists() == false) {
                // outputフォルダ作成
                outDir.mkdirs();
            }
        }

        // アクティベート処理
        executeActivate();

        if (initializeFlag == false) {
            initializeFlag = true;
        }
        return result;
    }

    protected boolean endFunc() {
        /* アクティベート処理 */
        boolean activateOutFlag = false;
        if (JMPFlags.ActivateFlag == true) {
            activateOutFlag = true;
        }
        if (JMPFlags.DebugMode == true || JMPCore.isEnableStandAlonePlugin() == true || JMPFlags.LibraryMode == true) {
            // デバッグ・スタンドアロン実行は発行しない
            activateOutFlag = false;
        }
        // ライセンス発行
        if (activateOutFlag == true) {
            if (Utility.isExsistFile(getActivateFileLocationPath()) == false) {
                try {
                    String text = "ライセンス認証のためのファイルです。" + Platform.getNewLine() + "このファイルを削除してもソフトウェアの動作には影響ありません。" + Platform.getNewLine()
                            + Platform.getNewLine() + "File for license authentication." + Platform.getNewLine()
                            + "Deleting this file does not affect the operation of the software.";

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

    @Override
    public boolean isEnableStandAlonePlugin() {
        return JMPCore.isEnableStandAlonePlugin();
    }

    public void initializeAllSetting() {
        // 全てのWindowを閉じる
        JMPCore.getWindowManager().setVisibleAll(false);
        JMPCore.getPluginManager().closeAllPlugins();

        JMPCore.getDataManager().initializeConfigDatabase();
        // JMPCore.getDataManager().clearHistory();
        // JMPCore.getSoundManager().clearPlayList();
        JMPCore.getWindowManager().initializeLayout();

        // 言語更新
        JMPCore.getWindowManager().updateLanguage();

        // 設定ファイルのFFmpegパスを同期
        setFFmpegWrapperPath(JMPCore.getDataManager().getFFmpegPath());
    }

    public void executeActivate() {
        /* アクティベート状況の確認 */
        if (Utility.isExsistFile(getActivateFileLocationPath()) == true) {
            JMPFlags.ActivateFlag = true;
        }
        else {
            JMPFlags.ActivateFlag = false;
        }

        /* スタンドアロンモード or デバッグモード or ライブラリモードの際はアクティベートする */
        if (JMPFlags.DebugMode == true || JMPCore.isEnableStandAlonePlugin() == true || JMPFlags.LibraryMode == true) {
            JMPFlags.ActivateFlag = true;
        }
    }

    public boolean setCommonRegisterValue(String key, String value) {
        if (cReg == null) {
            return false;
        }
        if (JMPFlags.DebugMode == false) {
            // デバッグモード時のみ設定可能
            return false;
        }

        boolean ret = cReg.setValue(key, value);
        if (ret == true) {
            // 全てのマネージャーに通知
            AbstractManager.callNotifyUpdateCommonRegister(key);
        }
        return ret;
    }

    public String getCommonRegisterValue(String key) {
        if (cReg == null) {
            return "";
        }
        return cReg.getValue(key);
    }

    public String[] getCommonKeySet() {
        return cReg.getKeySet();
    }

    public void makeSystemPath() {

        // プラグイン格納ディレクトリパス
        pluginsDirPath = Utility.stringsCombin(Platform.getCurrentPath(), PLUGINS_DIR_NAME);

        // データファイル格納ディレクトリパス
        if (JMPLoader.UsePluginDirectory == true) {
            dataFileLocationPath = Utility.stringsCombin(pluginsDirPath, Platform.getSeparator(), DATA_DIR_NAME);
        }
        else {
            dataFileLocationPath = Utility.stringsCombin(Platform.getCurrentPath(), DATA_DIR_NAME);
        }

        // リソースファイル格納ディレクトリパス
        if (JMPLoader.UsePluginDirectory == true) {
            resFileLocationPath = Utility.stringsCombin(pluginsDirPath, Platform.getSeparator(), RES_DIR_NAME);
        }
        else {
            resFileLocationPath = Utility.stringsCombin(Platform.getCurrentPath(), RES_DIR_NAME);
        }

        // プラグインjms格納ディレクトリパス
        jmsDirPath = Utility.stringsCombin(pluginsDirPath, Platform.getSeparator(), JMS_DIR_NAME);

        // プラグインjar格納ディレクトリパス
        jarDirPath = Utility.stringsCombin(pluginsDirPath, Platform.getSeparator(), JAR_DIR_NAME);

        // zip格納ディレクトリパス
        zipDirPath = Utility.stringsCombin(Platform.getCurrentPath(), ZIP_DIR_NAME);

        // 出力ファイル格納ディレクトリパス
        outputPath = Utility.stringsCombin(Platform.getCurrentPath(), OUTPUT_DIR_NAME);

        // アクティベートファイルパス
        activateFileLocationPath = Utility.stringsCombin(Platform.getCurrentPath(), Platform.getSeparator(), "activate");
    }

    /**
     * データファイル格納ディレクトリパス
     *
     * @return パス
     */
    public String getDataFileLocationPath() {
        return dataFileLocationPath;
    }

    @Override
    public String getDataFileLocationPath(IPlugin plugin) {
        String path = getDataFileLocationPath();
        if (JMPLoader.UsePluginDirectory == true) {
            path += (Platform.getSeparator() + getPluginName(plugin));
        }
        return path;
    }

    /**
     * リソースファイル格納ディレクトリパス
     *
     * @return
     */
    public String getResFileLocationPath() {
        return resFileLocationPath;
    }

    @Override
    public String getResFileLocationPath(IPlugin plugin) {
        String path = getResFileLocationPath();
        if (JMPLoader.UsePluginDirectory == true) {
            path += (Platform.getSeparator() + getPluginName(plugin));
        }
        return path;
    }

    public String getPluginsDirPath() {
        return pluginsDirPath;
    }

    public String getJmsDirPath() {
        return jmsDirPath;
    }

    public String getJarDirPath() {
        return jarDirPath;
    }

    public String getZipDirPath() {
        return zipDirPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    /**
     * アクティベート状態ファイル
     *
     * @return
     */
    public String getActivateFileLocationPath() {
        return activateFileLocationPath;
    }

    /**
     * ルックアンドフィールの設定
     */
    private void setupLookAndFeel() {
        String lf = UIManager.getSystemLookAndFeelClassName();
        // String lf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
        // String lf = "javax.swing.plaf.metal.MetalLookAndFeel";
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

    @Override
    public String getPluginName(IPlugin plugin) {
        // プラグインマネージャーに問い合わせ
        return JMPCore.getPluginManager().getPluginName(plugin);
    }

    public void executeBatFile(String path) {
        try {
            if (Utility.isExsistFile(path) == true) {
                Utility.invokeProcess(path);
            }
        }
        catch (Exception e1) {
        }
    }

    public void executeConvert(String inPath, String outPath) throws IOException {
        ffmpegWrapper.convert(inPath, outPath);

        JMPCore.getDataManager().addConvertedFile(new File(outPath));
    }

    public void setFFmpegWrapperCallback(IProcessingCallback cb) {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            pfw.setCallback(cb);
        }
    }

    public void setFFmpegWrapperWaitFor(boolean waitFor) {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            pfw.setWaitFor(waitFor);
        }
    }

    public boolean isFFmpegWrapperWaitFor() {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            return pfw.isWaitFor();
        }
        return false;
    }

    public boolean isValidFFmpegWrapper() {
        return ffmpegWrapper.isValid();
    }

    public void setFFmpegWrapperPath(String path) {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            pfw.setPath(path);
        }
    }

    public String getFFmpegWrapperPath() {
        if (ffmpegWrapper instanceof ProcessingFFmpegWrapper) {
            ProcessingFFmpegWrapper pfw = (ProcessingFFmpegWrapper) ffmpegWrapper;
            return pfw.getPath();
        }
        return "";
    }
}
