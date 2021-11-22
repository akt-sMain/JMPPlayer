package jmp.lang;

import jmp.lang.DefineLanguage.LangID;

/**
 * VBAで出力するファイル内に記載されている定義
 *
 * @author akkut
 *
 */
public class VBA {
    // 出力ソース文字コード
    public static final String SRC_CHARSET = "UTF-16";

    // 改行コード
    static final String NL = function.Platform.getNewLine();
}

/* HashMapのラッパー */
class LangMap extends java.util.HashMap<LangID, LanguageWords> {
};
