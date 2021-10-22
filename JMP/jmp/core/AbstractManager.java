package jmp.core;

import java.io.File;

import jlib.core.IManager;
import jmp.FileResult;

public abstract class AbstractManager implements IManager {

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

}
