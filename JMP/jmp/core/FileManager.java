package jmp.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jlib.core.IFileManager;
import jmp.FileResult;
import jmp.IFileResultCallback;
import jmp.JMPFlags;
import jmp.gui.ui.FileListTableModel;
import jmp.lang.DefineLanguage.LangID;
import jmp.plugin.PluginWrapper;
import jmp.task.ICallbackFunction;
import jmp.util.JmpUtil;

public class FileManager extends AbstractManager implements IFileManager {

    private static final String SUCCESS_MSG_FOAMET_LOAD = "%s ...(%s)";

    /**
     * ファイルロードの実処理
     * 
     * @author akkut
     *
     */
    private class LoadCallbackFunc implements ICallbackFunction {

        private File file;
        private boolean noneHistoryFlag = false;
        private boolean loadToPlayFlag = false;

        public LoadCallbackFunc(File f, boolean noneHistoryFlag, boolean toPlay) {
            this.file = f;
            this.noneHistoryFlag = noneHistoryFlag;
            this.loadToPlayFlag = toPlay;
        }

        @Override
        public void callback() {

            /* Coreのロード処理 */
            DataManager dm = JMPCore.getDataManager();
            SoundManager sm = JMPCore.getSoundManager();
            LanguageManager lm = JMPCore.getLanguageManager();

            // 念のためロード中フラグを立てる
            JMPFlags.NowLoadingFlag = true;

            // ファイル名をバックアップ
            String tmpFileName = dm.getLoadedFile();

            dm.clearCachedFiles(file);

            /* ロード処理 */
            FileResult result = new FileResult();
            result.status = true;
            result.statusMsg = "";
            for (AbstractManager am : ManagerInstances.getManagersOfAsc()) {
                am.loadFileForCore(file, result);
                if (result.status == false) {
                    break;
                }
            }

            /* 事後処理 */
            if (result.status == true) {

                // 履歴に追加
                if (this.noneHistoryFlag == false) {
                    dm.addHistory(file.getPath());
                }

                // 自動再生
                if (this.loadToPlayFlag == true) {
                    sm.play();
                }

                // 新しいファイル名
                dm.setLoadedFile(file.getPath());

                // メッセージ発行
                String successFileName = JmpUtil.getFileNameAndExtension(dm.getLoadedFile());
                String successMsg = lm.getLanguageStr(LangID.FILE_LOAD_SUCCESS);
                result.statusMsg = String.format(SUCCESS_MSG_FOAMET_LOAD, successFileName, successMsg);

                if (sm.getCurrentPlayerInfo() != null) {
                    sm.getCurrentPlayerInfo().update();

                    JMPFlags.Log.cprintln(">> PlayerInfo", true);
                    JMPFlags.Log.cprintln(sm.getCurrentPlayerInfo().getMessage(), true);
                }
            }
            else {
                // 前のファイル名に戻す
                dm.setLoadedFile(tmpFileName);

                // ファイル読み込み失敗時、連続再生を停止する
                JMPFlags.NextPlayFlag = false;
            }

            // ロード中フラグ解除
            JMPFlags.NowLoadingFlag = false;

            // 終了判定の結果を通知
            for (IFileResultCallback cb : loadCallbacks) {
                cb.end(result);
            }
        }

    }

    private List<IFileResultCallback> loadCallbacks = null;

    /* ファイルリスト */
    private static final String[] columnNames = new String[] { "", "Name" };
    private DefaultTableModel fileListModel;
    private JTable fileList;

    public DefaultTableModel getFileListModel() {
        return fileListModel;
    }

    public JTable getFileList() {
        return fileList;
    }

    FileManager() {
        super("file");
    }

    @Override
    protected boolean initFunc() {
        loadCallbacks = new ArrayList<IFileResultCallback>();
        fileListModel = new FileListTableModel(columnNames, 0);
        fileList = new JTable(fileListModel);
        return super.initFunc();
    }

    /**
     * 指定ディレクトリにあるファイルを列挙する
     *
     * @param dir
     *            ディレクトリ
     * @return ファイルリスト(key:ファイル名, value:Fileオブジェクト)
     */
    public Map<String, File> getFileMap(File dir) {
        HashMap<String, File> result = new HashMap<String, File>();
        if (dir.isDirectory() == false) {
            return result;
        }

        for (File file : dir.listFiles()) {
            if (file == null) {
                continue;
            }

            if (file.exists() == false) {
                // 除外ケース
                continue;
            }
            result.put(file.getName(), file);
        }
        return result;
    }

    private void initializeFlag() {
        // フラグ初期化
        JMPFlags.NoneHIstoryLoadFlag = false; // 履歴保存
    }

    public void addLoadResultCallback(IFileResultCallback loadResultCallback) {
        loadCallbacks.add(loadResultCallback);
    }

    @Override
    public void loadFileToPlay(File f) {
        loadFileImpl(f, true);
    }

    @Override
    public void loadFile(File f) {
        loadFileImpl(f, false);
    }

    private void loadFileImpl(File f, boolean toPlay) {
        SystemManager system = JMPCore.getSystemManager();
        LanguageManager lm = JMPCore.getLanguageManager();
        SoundManager sm = JMPCore.getSoundManager();

        String ex = JmpUtil.getExtension(f);
        System.out.println(ex);

        /* 事前の判定 */
        FileResult beginResult = new FileResult();
        beginResult.status = true;
        beginResult.statusMsg = lm.getLanguageStr(LangID.Now_loading);
        if (JMPFlags.NowLoadingFlag == true) {
            // ロード中
            beginResult.status = false;
            beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_1);
        }
        else if (sm.isPlay() == true) {
            // 再生中
            beginResult.status = false;
            beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_3);
        }
        else if (f.getPath().isEmpty() == true || f.canRead() == false || f.exists() == false) {
            // アクセス不可
            beginResult.status = false;
            beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_4);
        }
        else if (sm.isSupportedExtensionAccessor(ex) == false) {
            // サポート外
            beginResult.status = false;
            beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_2);
        }
        else if (system.isEnableStandAlonePlugin() == true) {
            // サポート外(スタンドアロンモード時)
            PluginWrapper pw = JMPCore.getStandAlonePluginWrapper();
            if (pw.isSupportExtension(f) == false) {
                beginResult.status = false;
                beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_2);
            }
        }

        // 事前判定の結果を通知
        for (IFileResultCallback cb : loadCallbacks) {
            cb.begin(beginResult);
        }

        if (beginResult.status == true) {
            // ロード中フラグ
            JMPFlags.NowLoadingFlag = true;

            // Sequenceタスクに委託
            ICallbackFunction func = new LoadCallbackFunc(f, JMPFlags.NoneHIstoryLoadFlag, toPlay);
            JMPCore.getTaskManager().queuing(func);
        }

        // フラグ初期化
        initializeFlag();
    }

    @Override
    public void reload() {
        String path = JMPCore.getDataManager().getLoadedFile();
        loadFile(path);
    }
}
