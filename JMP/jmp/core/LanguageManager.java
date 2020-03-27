package jmp.core;

import function.Platform;
import function.Utility;
import jlib.core.ILanguageManager;
import jmp.lang.DefineLanguage.LangID;
import jmp.lang.LanguageTable;

/**
 *
 * 言語マネージャー
 *
 */
public class LanguageManager extends AbstractManager implements ILanguageManager {
    /** 言語テーブル名 */
    public static final String LANGUAGE_FILE_NAME = "language.csv";

    /**
     * コンストラクタ
     */
    LanguageManager(int pri) {
        super(pri, "language");
    }

    @Override
    protected boolean initFunc() {
        // 言語初期設定
        return super.initFunc();
    }

    public String getLangFilePath() {
        return Utility.pathCombin(Platform.getCurrentPath(false), LANGUAGE_FILE_NAME);
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
}
