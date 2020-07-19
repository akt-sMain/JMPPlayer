package jmp.player;

import java.io.File;

import jlib.player.IPlayer;
import jmp.JmpUtil;

public abstract class Player implements IPlayer {
    private String[] supportExtentions = null;

    public boolean open() {
        return true;
    }

    public boolean close() {
        return true;
    }

    /**
     * ファイルロード
     *
     * @param file
     *            ファイル
     * @return
     */
    public abstract boolean loadFile(File file) throws Exception;

    /**
     * ファイルセーブ
     *
     * @param file
     *            ファイル
     * @return
     * @throws Exception
     */
    public abstract boolean saveFile(File file) throws Exception;

    public boolean isAllSupported() {
        if (supportExtentions != null) {
            for (String s : supportExtentions) {
                if (s.equalsIgnoreCase("*") == true) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isSupportedExtension(String extension) {
        if (supportExtentions != null) {
            for (String s : supportExtentions) {
                if (s.equalsIgnoreCase(extension) == true) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String[] getSupportExtentions() {
        return supportExtentions;
    }

    /**
     * サポートする拡張子を設定
     *
     * @param supportExtentions
     *            サポートする拡張子
     */
    public void setSupportExtentions(String... supportExtentions) {
        this.supportExtentions = supportExtentions;
    }

    /**
     * サポートする拡張子を設定(文字列動的変換)
     *
     * @param supportExtentionsStr
     *            サポートする拡張子
     */
    public void setSupportExtentionsString(String supportExtentionsStr) {
        setSupportExtentions(JmpUtil.genStr2Extensions(supportExtentionsStr));
    }
}
