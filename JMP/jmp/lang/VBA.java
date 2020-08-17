package jmp.lang;

import jmp.lang.DefineLanguage.LangID;

/**
 * VBAで出力するファイル内に記載されている定義
 *
 * @author akkut
 *
 */
public class VBA {
    // 改行コード
    static final String NL = function.Platform.getNewLine();
}

/* HashMapのラッパー */
class LangMap extends java.util.HashMap<LangID, LanguageWords> {
};
