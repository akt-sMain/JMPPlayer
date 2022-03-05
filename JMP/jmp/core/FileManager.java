package jmp.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import jlib.core.IFileManager;
import jmp.JMPFlags;
import jmp.file.FileResult;
import jmp.file.IFileResultCallback;
import jmp.gui.ui.FileListTableModel;
import jmp.lang.DefineLanguage.LangID;
import jmp.plugin.PluginWrapper;
import jmp.task.ICallbackFunction;
import jmp.task.TaskOfNotify.NotifyID;
import jmp.util.JmpUtil;

public class FileManager extends AbstractManager implements IFileManager {
    
    public enum AutoPlayMode {
        DIRECTORY,
        PLAY_LIST
    }
    
    private AutoPlayMode autoPlayMode = AutoPlayMode.DIRECTORY;

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
        JMPFlags.NoneHistoryLoadFlag = false; // 履歴保存
    }

    public void addLoadResultCallback(IFileResultCallback loadResultCallback) {
        loadCallbacks.add(loadResultCallback);
    }
    
    void callFileResultBegin(FileResult beginResult) {
        for (IFileResultCallback cb : loadCallbacks) {
            cb.begin(beginResult);
        }
    }
    void callFileResultEnd(FileResult endResult) {
        for (IFileResultCallback cb : loadCallbacks) {
            cb.end(endResult);
        }
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
        JMPCore.getTaskManager().sendNotifyMessage(NotifyID.FILE_RESULT_BEGIN, beginResult);

        if (beginResult.status == true) {
            // ロード中フラグ
            JMPFlags.NowLoadingFlag = true;

            // 実処理はSequenceタスクに委譲する
            ICallbackFunction func = FileCallbackCreator.getInstance().createLoadCallback(f, JMPFlags.NoneHistoryLoadFlag, toPlay);
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

    public AutoPlayMode getAutoPlayMode() {
        return autoPlayMode;
    }

    public void setAutoPlayMode(AutoPlayMode autoPlayMode) {
        this.autoPlayMode = autoPlayMode;
    }
}
