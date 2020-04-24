package jmp.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import function.Utility;
import jlib.core.IFileManager;
import jmp.FileResult;
import jmp.IFileResultCallback;
import jmp.JMPFlags;
import jmp.lang.DefineLanguage.LangID;
import jmp.task.ICallbackFunction;

public class FileManager extends AbstractManager implements IFileManager {

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
    public void loadFile(File f) {
        LanguageManager lm = JMPCore.getLanguageManager();
        SoundManager sm = JMPCore.getSoundManager();

        String ex = Utility.getExtension(f);

        /* 事前の判定 */
        FileResult beginResult = new FileResult();
        beginResult.status = true;
        beginResult.statusMsg = lm.getLanguageStr(LangID.Now_loading);
        if (JMPFlags.NowLoadingFlag == true) {
            // ロード中
            beginResult.status = false;
            beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_1);
        }
        else if (sm.isSupportedExtensionAccessor(ex) == false) {
            // サポート外
            beginResult.status = false;
            beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_2);
        }
        else if (sm.isPlay() == true) {
            // 再生中
            beginResult.status = false;
            beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_3);
        }
        else if (f.canRead() == false || f.exists() == false) {
            // アクセス不可
            beginResult.status = false;
            beginResult.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_4);
        }

        // 事前判定の結果を通知
        for (IFileResultCallback cb : loadCallbacks) {
            cb.begin(beginResult);
        }
        if (beginResult.status == false) {
            // 失敗
            return;
        }

        // ロード中フラグ
        JMPFlags.NowLoadingFlag = true;

        // Sequenceタスクに委託
        JMPCore.getTaskManager().getTaskOfSequence().queuing(new ICallbackFunction() {
            @Override
            public void callback() {

                /* Coreのロード処理 */
                FileResult endResult = loadFileImpl(f);

                // 終了判定の結果を通知
                for (IFileResultCallback cb : loadCallbacks) {
                    cb.end(endResult);
                }
            }
        });
    }

    /** コアのファイルロード処理 */
    private FileResult loadFileImpl(File f) {
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
                // e.printStackTrace();
                result.status = false;
                result.statusMsg = lm.getLanguageStr(LangID.FILE_ERROR_5);
            }
        }

        /* 事後処理 */
        if (result.status == true) {

            // 履歴に追加
            if (JMPFlags.NoneHIstoryLoadFlag == false) {
                dm.addHistory(f.getPath());
            }

            // 自動再生
            if (JMPFlags.LoadToPlayFlag == true) {
                sm.play();
                JMPFlags.LoadToPlayFlag = false;
            }

            // 新しいファイル名
            dm.setLoadedFile(f.getPath());

            // メッセージ発行
            String successFileName = Utility.getFileNameAndExtension(dm.getLoadedFile());
            String successMsg = lm.getLanguageStr(LangID.FILE_LOAD_SUCCESS);
            result.statusMsg = String.format("%s ...(%s)", successFileName, successMsg);
        }
        else {
            // 前のファイル名に戻す
            dm.setLoadedFile(tmpFileName);
        }

        // フラグ初期化
        JMPFlags.NoneHIstoryLoadFlag = false; // 履歴保存
        JMPFlags.LoadToPlayFlag = false; // ロード後再生
        JMPFlags.NowLoadingFlag = false; // ロード中
        return result;
    }

}
