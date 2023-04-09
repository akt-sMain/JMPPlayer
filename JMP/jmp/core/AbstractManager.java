package jmp.core;

import java.io.File;

import jlib.core.IManager;
import jmp.core.ManagerInstances.TypeOfKey;
import jmp.file.FileResult;
import jmp.file.IJmpConfigDatabase;
import jmp.task.TaskOfNotify.NotifyID;
import jmp.util.JmpUtil;

public abstract class AbstractManager implements IManager, IJmpConfigDatabase {

    protected String name = "";
    private boolean initializeFlag = false;

    AbstractManager(String name) {
        this.name = name;

        // Coreに登録
        ManagerInstances.register(this);
    }

    public String getName() {
        return name;
    }

    protected boolean initFunc() {
        initializeFlag = true;
        return true;
    }

    protected boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        return true;
    }

    // Config更新通知
    protected void notifyUpdateConfig(String key) {
    }

    // cReg更新通知
    protected void notifyUpdateCommonRegister(String key) {
    }

    public boolean isFinishedInitialize() {
        return initializeFlag;
    }

    protected void loadFileForCore(File file, FileResult result) {
    }
    
    @Override
    public void setConfigParam(String key, String value) {
        if (key.equals(ManagerInstances.CFG_KEY_INITIALIZE) == false) {
            if (ManagerInstances.CFG_INIT_TABLE.containsKey(key) == false) {
                return;
            }

            String newValue = new String(value);
            TypeOfKey type = ManagerInstances.CFG_INIT_TABLE.get(key).type;
            if (type == TypeOfKey.BOOL) {
                // BOOLの表記を統一
                boolean b = JmpUtil.toBoolean(newValue, false);
                if (b == true) {
                    newValue = new String(IJmpConfigDatabase.IJ_TRUE);
                }
                else {
                    newValue = new String(IJmpConfigDatabase.IJ_FALSE);
                }
            }
            ManagerInstances.setConfigParam(key, newValue);
        }
        // 設定変更通知
        ManagerInstances.sendNotify(NotifyID.UPDATE_CONFIG, key);
    }
    
    @Override
    public String getConfigParam(String key) {
        return ManagerInstances.getConfigParam(key);
    }

}
