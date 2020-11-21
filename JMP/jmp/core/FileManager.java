package jmp.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jlib.core.IFileManager;
import jmp.FileResult;
import jmp.IFileResultCallback;
import jmp.JMPFlags;
import jmp.lang.DefineLanguage.LangID;
import jmp.task.ICallbackFunction;
import jmp.util.JmpUtil;

public class FileManager extends AbstractManager implements IFileManager {

    private static final String SUCCESS_MSG_FOAMET_LOAD = "%s ...(%s)";

    private class LoadSetting {
        public boolean noneHistoryLoadFlag = false;
        public boolean loadToPlayFlag = false;

        public LoadSetting() {
        }
    }

    private List<IFileResultCallback> loadCallbacks = null;

    FileManager(int pri) {
        super(pri, "file");
    }

    @Override
    protected boolean initFunc() {
        loadCallbacks = new ArrayList<IFileResultCallback>();
        return super.initFunc();
    }

    public void addLoadResultCallback(IFileResultCallback loadResultCallback) {
        loadCallbacks.add(loadResultCallback);
    }

    @Override
    public void loadFileToPlay(File f) {
        JMPFlags.LoadToPlayFlag = true;
        loadFile(f);
    }

    @Override
    public void loadFile(File f) {
        SystemManager system = JMPCore.getSystemManager();
        LanguageManager lm = JMPCore.getLanguageManager();
        SoundManager sm = JMPCore.getSoundManager();
        PluginManager pm = JMPCore.getPluginManager();

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
            if (pm.isSupportExtension(f, JMPCore.StandAlonePlugin) == false) {
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

            // フラグバックアップ
            LoadSetting setting = new LoadSetting();
            setting.noneHistoryLoadFlag = JMPFlags.NoneHIstoryLoadFlag;
            setting.loadToPlayFlag = JMPFlags.LoadToPlayFlag;

            // Sequenceタスクに委託
            JMPCore.getTaskManager().queuing(new ICallbackFunction() {
                @Override
                public void callback() {

                    /* Coreのロード処理 */
                    FileResult endResult = loadFileImpl(f, setting);

                    // 終了判定の結果を通知
                    for (IFileResultCallback cb : loadCallbacks) {
                        cb.end(endResult);
                    }
                }
            });
        }

        // フラグ初期化
        JMPFlags.NoneHIstoryLoadFlag = false; // 履歴保存
        JMPFlags.LoadToPlayFlag = false; // ロード後再生
    }

    /** コアのファイルロード処理 */
    private FileResult loadFileImpl(File f, LoadSetting setting) {
        DataManager dm = JMPCore.getDataManager();
        SoundManager sm = JMPCore.getSoundManager();
        LanguageManager lm = JMPCore.getLanguageManager();
        PluginManager pm = JMPCore.getPluginManager();

        FileResult result = new FileResult();
        result.status = true;
        result.statusMsg = "";

        // 念のためロード中フラグを立てる
        JMPFlags.NowLoadingFlag = true;

        // ファイル名をバックアップ
        String tmpFileName = dm.getLoadedFile();

        /* ロード処理 */
        if (result.status == true) {
            String subErrorStr = "";
            try {
                // ファイルロード実行
                sm.loadFile(f);

                // プラグインのファイルロード処理
                try {
                    pm.loadFile(f);
                }
                catch (Exception e) {
                    subErrorStr = "(" + lm.getLanguageStr(LangID.Plugin_error) + ")";
                    result.status = false;
                    result.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_5) + subErrorStr;
                }
            }
            catch (Exception e) {
                result.status = false;
                result.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_5);
            }
        }

        /* 事後処理 */
        if (result.status == true) {

            // 履歴に追加
            if (setting.noneHistoryLoadFlag == false) {
                dm.addHistory(f.getPath());
            }

            // 自動再生
            if (setting.loadToPlayFlag == true) {
                sm.play();
            }

            // 新しいファイル名
            dm.setLoadedFile(f.getPath());

            // メッセージ発行
            String successFileName = JmpUtil.getFileNameAndExtension(dm.getLoadedFile());
            String successMsg = lm.getLanguageStr(LangID.FILE_LOAD_SUCCESS);
            result.statusMsg = String.format(SUCCESS_MSG_FOAMET_LOAD, successFileName, successMsg);

            if (sm.getCurrentPlayerInfo() != null) {
                sm.getCurrentPlayerInfo().update();

                JMPFlags.Log.cprintln(">> PlayerInfo");
                JMPFlags.Log.cprintln(sm.getCurrentPlayerInfo().getMessage());
            }
        }
        else {
            // 前のファイル名に戻す
            dm.setLoadedFile(tmpFileName);
        }

        // ロード中フラグ解除
        JMPFlags.NowLoadingFlag = false;
        return result;
    }

    @Override
    public void reload() {
        String path = JMPCore.getDataManager().getLoadedFile();
        loadFile(path);
    }
}
