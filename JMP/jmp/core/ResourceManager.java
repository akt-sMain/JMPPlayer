package jmp.core;

import java.awt.Image;

import jlib.manager.IManager;
import jmp.Resource;

public class ResourceManager extends AbstractManager implements IManager{

    private Resource rsrc = null;

    ResourceManager(int pri) {
        super(pri, "resource");
    }

    @Override
    public boolean initFunc() {
        if (initializeFlag == false) {
            initializeFlag = true;
        }

        // リソース生成
        rsrc = new Resource();
        return true;
    }

    @Override
    public boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        return true;
    }

    public Image getJmpImageIcon() {
        return rsrc.jmpImageIcon;
    }

    public Image getFileOtherIcon() {
        return rsrc.fileOtherIcon;
    }

    public Image getFileMidiIcon() {
        return rsrc.fileMidiIcon;
    }

    public Image getFileWavIcon() {
        return rsrc.fileWavIcon;
    }

    public Image getFileFolderIcon() {
        return rsrc.fileFolderIcon;
    }

    public Image getBtnPlayIcon() {
        return rsrc.btnPlayIcon;
    }

    public Image getBtnStopIcon() {
        return rsrc.btnStopIcon;
    }

    public Image getBtnNextIcon() {
        return rsrc.btnNextIcon;
    }

    public Image getBtnNext2Icon() {
        return rsrc.btnNext2Icon;
    }

    public Image getBtnPrevIcon() {
        return rsrc.btnPrevIcon;
    }

    public Image getBtnPrev2Icon() {
        return rsrc.btnPrev2Icon;
    }
}
