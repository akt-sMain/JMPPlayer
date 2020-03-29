package jmp.core;

import jmp.lang.DefineLanguage.LangID;
import jmp.lang.LanguageTable;

/**
 *
 * 言語マネージャー
 *
 */
public class LanguageManager extends AbstractManager {

    /**
     * コンストラクタ
     */
    LanguageManager(int pri) {
        super(pri, "language");
    }

    /**
     * 言語タイトル取得
     *
     * @param index
     * @return
     */
    public String getTitle(int index) {
        return LanguageTable.getTitle(index);
    }

    /**
     * 言語タイトルからインデックスを取得
     *
     * @param title
     * @return
     */
    public int getIndex(LangID title) {
        return LanguageTable.getIndex(title);
    }

    /**
     * 文字列取得
     *
     * @param id
     *            文字列ID
     * @return 文字列
     */
    public String getLanguageStr(LangID id) {
        int langIndex = JMPCore.getDataManager().getLanguage();
        return getLanguageStr(id, langIndex);
    }

    /**
     * 文字列取得
     *
     * @param id
     * @param langIndex
     * @return
     */
    public String getLanguageStr(LangID id, int langIndex) {
        return LanguageTable.getLanguageStr(id, langIndex);
    }

    /**
     * 現在の言語名
     *
     * @return
     */
    public String getCurLanguageName() {
        return getTitle(JMPCore.getDataManager().getLanguage());
    }

    public String getReadmeContent() {
        int langIndex = JMPCore.getDataManager().getLanguage();
        return LanguageTable.getReadmeContent(langIndex);
    }
}
