package jmp.lang;

import jmp.lang.DefineLanguage.LangID;

public class LanguageTable {

    /**
     * 言語タイトル取得
     *
     * @param index
     * @return
     */
    public static String getTitle(int langIndex) {
        return getLanguageStr(DefineLanguage.titles[langIndex], langIndex);
    }

    /**
     * 言語タイトルからインデックスを取得
     *
     * @param title
     * @return
     */
    public static int getIndex(LangID titleID) {
        for (int i = 0; i < DefineLanguage.titles.length; i++) {
            if (DefineLanguage.titles[i] == titleID) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 文字列取得
     *
     * @param id
     * @param langIndex
     * @return
     */
    public static String getLanguageStr(LangID id, int langIndex) {
        String ret = LanguageWords.BLANK_TEXT;
        if (DefineLanguage.langMap.containsKey(id) == true) {
            LanguageWords words = DefineLanguage.langMap.get(id);
            if (0 > langIndex || langIndex >= words.getSize()) {
                // 存在しない翻訳が指定された場合は、英語を参照する
                langIndex = DefineLanguage.INDEX_LANG_ENGLISH;
            }
            ret = words.getWord(langIndex);
        }
        return ret;
    }

    /**
     * readme本文を取得
     * @param langIndex
     * @return
     */
    public static String getReadmeContent(int langIndex) {
        String ret = "";
        switch (langIndex) {
            case DefineLanguage.INDEX_LANG_JAPANESE:
                ret = DefineReadme.README_JA;
                break;
            default:
                ret = DefineReadme.README_EN;
                break;
        }
        return ret;
    }
}
