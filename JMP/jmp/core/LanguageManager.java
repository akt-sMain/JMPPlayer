package jmp.core;

import java.util.HashMap;

import function.Platform;
import jmp.lang.DefineLanguage;
import jmp.lang.DefineLanguage.LangID;
import jmp.lang.FontRsrc;
import jmp.lang.LanguageTable;

/**
 *
 * 言語マネージャー
 *
 */
public class LanguageManager extends AbstractManager {

    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_UTF16 = "UTF-16";
    public static final String CHARSET_SJIS = "SJIS";
    public static final String CHARSET_EUCKR = "EUC_KR";//韓国語
    
    /** デフォルト文字コード */
    public static final String CHARSET_DEFAULT = CHARSET_UTF16;
    
    /** 正式版に追加しない言語設定 */
    private static int[] DisableLanguageList = {
    };

    /**
     * 変換する文字コードリスト(該当しないものはデフォルト文字コードにする) <br>
     *  ※ Javaの仕様上常にUTF-16が良い？
     */
    private static HashMap<Integer, String> chasets = new HashMap<Integer, String>() {
        {
            put(DefineLanguage.INDEX_LANG_ENGLISH, CHARSET_DEFAULT);
            put(DefineLanguage.INDEX_LANG_JAPANESE, CHARSET_DEFAULT);
            put(DefineLanguage.INDEX_LANG_CHINESE, CHARSET_DEFAULT);
            put(DefineLanguage.INDEX_LANG_KOREAN, CHARSET_DEFAULT);
        }
    };
    
    private static FontRsrc DEFAULT_FONT_RSRC = new FontRsrc(WindowManager.DEFAULT_FONT);
    
    /** Windows用フォントセット */
    private static HashMap<Integer, FontRsrc> SFontsForWindows = new HashMap<Integer, FontRsrc>() {
        {
            put(DefineLanguage.INDEX_LANG_ENGLISH, new FontRsrc("Open Sans"));
            put(DefineLanguage.INDEX_LANG_JAPANESE, new FontRsrc("Meiryo"));
            put(DefineLanguage.INDEX_LANG_CHINESE, new FontRsrc("Microsoft YaHei"));
            put(DefineLanguage.INDEX_LANG_KOREAN, new FontRsrc("Malgun Gothic"));
        }
    };
    /** Mac用フォントセット */
    private static HashMap<Integer, FontRsrc> SFontsForMac = new HashMap<Integer, FontRsrc>() {
        {
            put(DefineLanguage.INDEX_LANG_ENGLISH, new FontRsrc("Helvetica Neue"));
            put(DefineLanguage.INDEX_LANG_JAPANESE, new FontRsrc("Hiragino Sans"));
            put(DefineLanguage.INDEX_LANG_CHINESE, new FontRsrc("PingFang SC"));
            put(DefineLanguage.INDEX_LANG_KOREAN, new FontRsrc("Gulim"));
        }
    };
    /** 動作保障外OS用フォントセット */
    private static HashMap<Integer, FontRsrc> SFontsForOther = new HashMap<Integer, FontRsrc>() {
        {
            put(DefineLanguage.INDEX_LANG_ENGLISH, DEFAULT_FONT_RSRC);
            put(DefineLanguage.INDEX_LANG_JAPANESE, DEFAULT_FONT_RSRC);
            put(DefineLanguage.INDEX_LANG_CHINESE, DEFAULT_FONT_RSRC);
            put(DefineLanguage.INDEX_LANG_KOREAN, DEFAULT_FONT_RSRC);
        }
    };
    
    private HashMap<Integer, FontRsrc> fonts = null;

    /**
     * コンストラクタ
     */
    LanguageManager() {
        super("language");
    }
    
    @Override
    protected boolean initFunc() {
    	super.initFunc();
    	
    	/* OSごとのフォントファクシミリを切り替え */
        switch (Platform.getRunPlatform()) {
	        case WINDOWS:
	        	fonts = SFontsForWindows;
	            break;
	        case MAC:
	        	fonts = SFontsForMac;
	            break;
	        case LINUX:
	        case SUN_OS:
	        case OTHER:
	        default:
	        	fonts = SFontsForOther;
	            break;
	    }
    	return true;
    }
    
    /**
     * フォント名取得
     * 
     * @param lang
     * @param type
     * @return
     */
    public String getFontName(int lang, int type) {
    	FontRsrc f;
    	if (fonts.containsKey(lang) == true) {
    		f = fonts.get(lang);
    	}
    	else {
    		f = DEFAULT_FONT_RSRC;
    	}
    	return f.getName(type);
    }
    /**
     * フォント名取得
     * 
     * @param lang
     * @return
     */
    public String getFontName(int lang) {
    	return getFontName(lang, 0);
    }

    /**
     * 対応している言語か
     *
     * @param index
     * @return
     */
    public boolean isValidLanguageIndex(int index) {
        for (int j = 0; j < LanguageManager.DisableLanguageList.length; j++) {
            if (LanguageManager.DisableLanguageList[j] == index) {
                return false;
            }
        }
        return true;
    }

    private String getCode(int index) {
        String code = CHARSET_DEFAULT;
        if (chasets.containsKey(index) == true) {
            code = chasets.get(index);
        }
        return code;
    }


    /**
     * 言語タイトル取得
     *
     * @param index
     * @return
     */
    public String getTitle(int index, int lang) {
        return LanguageTable.getTitle(index, lang, getCode(index));
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
        return LanguageTable.getLanguageStr(id, langIndex, getCode(langIndex));
    }

    /**
     * 現在の言語名
     *
     * @return
     */
    public String getCurLanguageName() {
        return getTitle(JMPCore.getDataManager().getLanguage(), DefineLanguage.INDEX_LANG_ENGLISH);
    }

    public String getReadmeContent() {
        int langIndex = JMPCore.getDataManager().getLanguage();
        return LanguageTable.getReadmeContent(langIndex);
    }

    /**
     * 言語コード取得
     *
     * @param langIndex
     * @return
     */
    public String getLanguageCode(int langIndex) {
        return LanguageTable.getLangCode(langIndex);
    }

    /**
     * 言語コードインデックス取得
     *
     * @param code
     * @return
     */
    public int getLanguageCodeIndex(String code) {
        return LanguageTable.getLangCodeIndex(code);
    }
}
