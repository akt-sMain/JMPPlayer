package jmp.core;

import java.util.List;

import function.Platform;
import function.Utility;
import jlib.IUpdateLanguageListener;
import jlib.manager.ILanguageManager;
import jmp.LanguageTable;

/**
 *
 * 言語マネージャー
 *
 */
public class LanguageManager extends AbstractManager implements ILanguageManager {
    /** 言語テーブル名 */
    public static final String LANGUAGE_FILE_NAME = "language.lang";

    private LanguageTable languageTable = null;

    private int currentLanguage = 0;

    private List<IUpdateLanguageListener> register = null;

    /**
     * コンストラクタ
     */
    LanguageManager(int pri) {
        super(pri, "language");
    }

    @Override
    public void registerListener(IUpdateLanguageListener listener) {
        if (register.contains(listener) == false) {
            register.add(listener);
        }
    }

    public String getLangFilePath() {
        return Utility.pathCombin(Platform.getCurrentPath(false), LANGUAGE_FILE_NAME);
    }

    /**
     * 言語テーブル読み込み
     *
     * @return 可否
     */
    public boolean readingLanguageFile() {
        boolean ret = true; // 可否
        try {
            languageTable.reading(getLangFilePath());
        }
        catch (Exception e) {
            ret = false;
        }

        return ret;
    }

    public List<String> getTitleHeader() {
        return languageTable.getTitleHeader();
    }

    /**
     * 言語タイトル取得
     *
     * @param index
     * @return
     */
    public String getTitle(int index) {
        return languageTable.getTitle(index);
    }

    /**
     * 言語タイトルからインデックスを取得
     *
     * @param title
     * @return
     */
    public int getIndex(String title) {
        return languageTable.getIndex(title);
    }

    /**
     * 文字列取得
     *
     * @param id
     *            文字列ID
     * @return 文字列
     */
    public String getLanguageStr(String id) {
        int langIndex = getCurrentLanguage();
        return getLanguageStr(id, langIndex);
    }

    /**
     * 文字列取得
     *
     * @param id
     * @param langIndex
     * @return
     */
    public String getLanguageStr(String id, int langIndex) {
        // ID表示モードの場合はIDを返す
        // if (CoreAccessor.getSystemManager().isVisibleLangID() == true) {
        // return id;
        // }
        return languageTable.getLanguageStr(id, langIndex);
    }

    /**
     * 現在の言語名
     *
     * @return
     */
    public String getCurLanguageName() {
        return getTitle(getCurrentLanguage());
    }

    public void updateLanguage() {
        for (IUpdateLanguageListener l : register) {
            l.updateLanguage();
        }
    }

    public int getCurrentLanguage() {
        return currentLanguage;
    }

    public void setCurrentLanguage(int currentLanguage) {
        this.currentLanguage = currentLanguage;
    }
}
