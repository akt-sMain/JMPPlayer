package jmp;

import java.io.File;

import jlib.plugin.IPlugin;
import jmp.core.JMPCore;
import jmp.core.SystemManager;
import jmp.core.TaskManager;
import jmp.file.ConfigDatabaseWrapper;
import jmp.task.ICallbackFunction;
import jmp.util.JmpUtil;

class InvokeTask implements Runnable {

    private boolean invokeResult = false;
    private ConfigDatabaseWrapper config;
    private IPlugin standAlonePlugin;
    private File loadFile;
    
    public InvokeTask(ConfigDatabaseWrapper config, IPlugin standAlonePlugin, File loadFile) {
        this.config = config;
        this.standAlonePlugin = standAlonePlugin;
        this.loadFile = loadFile;
    }

    @Override
    public void run() {
        // invokeメソッドからの起動はLibraryモードではない
        JMPFlags.LibraryMode = false;

        SystemManager.consoleOutSystemInfo();

        // ライブラリ初期化処理
        boolean result = JMPLoader.initLibrary(config, standAlonePlugin);

        /* 起動準備 */
        if (result == true) {

            TaskManager taskManager = JMPCore.getTaskManager();

            // コマンド引数で指定されたファイルを開く
            if (loadFile != null) {
                if (loadFile.canRead() == true) {
                    taskManager.queuing(new ICallbackFunction() {
                        @Override
                        public void callback() {
                            JmpUtil.threadSleep(500);
                            JMPCore.getFileManager().loadFileToPlay(loadFile);
                        }
                    });
                }
            }

            // タスクジョイン
            try {
                taskManager.join();
            }
            catch (InterruptedException e) {
                JMPCore.getSystemManager().showSystemErrorMessage(ErrorDef.ERROR_ID_UNKNOWN_EXIT_APPLI);
            }
        }

        // ライブラリ終了処理
        boolean endResult = JMPLoader.exitLibrary();
        if (result == false) {
            endResult = result;
        }
        invokeResult = endResult;
    }

    public boolean getResult() {
        return invokeResult;
    }
}
