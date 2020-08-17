package jmp.player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import function.Platform;
import jlib.player.IPlayer;
import jmp.util.JmpUtil;

public abstract class Player implements IPlayer {
    public class Info {
        public static final String PLAYER_ABS_INFO_KEY_FILENAME = "File Name";

        private List<String> columns = null;
        private Map<String, String> data = null;
        public Info() {
            columns = new ArrayList<String>();
            data = new HashMap<String, String>();
            put(PLAYER_ABS_INFO_KEY_FILENAME, "");
        }

        public void put(String key, String value) {
            if (data.containsKey(key) == false) {
                columns.add(key);
            }
            data.put(key, value);
        }

        public String get(String key, String value) {
            if (data.containsKey(key) == true) {
                return data.get(key);
            }
            return "";
        }

        public int size() {
            return data.size();
        }

        public String getMessage() {
            String msg = "";
            for (String key : columns) {
                msg += String.format("%s : %s", key, data.get(key));
                msg += Platform.getNewLine();
            }
            return msg;
        }

        public void update() {
        }
    }

    private Info info = null;
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

    public Info getInfo() {
        return info;
    }

    protected void setInfo(Info info) {
        this.info = info;
    }
}
