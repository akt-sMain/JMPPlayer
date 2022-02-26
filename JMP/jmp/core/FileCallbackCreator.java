package jmp.core;

import java.io.File;

import jmp.FileResult;
import jmp.JMPFlags;
import jmp.lang.DefineLanguage.LangID;
import jmp.task.ICallbackFunction;
import jmp.task.TaskOfNotify.NotifyID;
import jmp.util.JmpUtil;

public class FileCallbackCreator {
    
    private static FileCallbackCreator instance = new FileCallbackCreator();
    private FileCallbackCreator() {
    }
    
    public static FileCallbackCreator getInstance() {
        return instance;
    }
    
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
            JMPCore.getTaskManager().sendNotifyMessage(NotifyID.FILE_RESULT_END, result);
        }
    }
    
    public ICallbackFunction createLoadCallback(File f, boolean noneHistoryFlag, boolean toPlay) {
        return new LoadCallbackFunc(f, JMPFlags.NoneHistoryLoadFlag, toPlay);
    }
}
