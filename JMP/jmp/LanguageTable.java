package jmp;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import function.Utility;

public class LanguageTable {
    /** Blankテキスト */
    public static final String BLANK_TEXT = "#BLANK#";

    private ArrayList<String> header = null;

    private Map<String, String[]> langTable = null;

    public LanguageTable() {
        header = new ArrayList<String>();
        langTable = new HashMap<String, String[]>();
    }

    public boolean reading(List<String> contents) throws Exception {
        boolean ret = true; // 可否
        if (ret == true) {
            try {
                boolean runFlag = false; // 実行状態フラグ
                for (String line : contents) {
                    String[] sLine = line.split(",");
                    if (runFlag == true) {

                        if (sLine.length <= 0) {
                            continue;
                        }

                        // 言語キーを保持
                        String key = sLine[0];

                        // 各言語を読み込み
                        LinkedList<String> buf = new LinkedList<String>();
                        for (int i = 1; i < header.size() + 1; i++) {
                            if (i >= sLine.length) {
                                buf.add(BLANK_TEXT);
                            }
                            else {
                                buf.add(sLine[i]);
                            }
                        }

                        // System.out.print(key + " : ");
                        // for (String aa : buf) {
                        // System.out.print(aa + " ");
                        // }
                        // System.out.println();

                        // 言語テーブルにputする
                        langTable.put(key, (String[]) buf.toArray(new String[0]));
                        continue;
                    }

                    if ((runFlag == false) && (sLine[0].equals("id"))) {
                        // 開始行を識別
                        runFlag = true;

                        // ヘッダーを記録
                        header.clear();
                        for (int i = 1; i < sLine.length; i++) {
                            header.add(sLine[i]);
                        }
                    }
                }
            }
            catch (Exception e) {
                // eLog = e;
                ret = false;
            }
        }
        return ret;
    }

    /**
     * 言語テーブル読み込み
     *
     * @return 可否
     */
    public boolean reading(String path) throws Exception {
        List<String> contents = null;
        try {
            contents = Utility.getTextFileContents(path);
        }
        catch (Exception e) {
            return false;
        }
        return reading(contents);
    }

    /**
     * 言語テーブル読み込み
     *
     * @return 可否
     */
    public boolean reading(InputStreamReader stream) throws Exception {
        List<String> contents = null;
        try {
            contents = Utility.getTextFileContents(stream);
        }
        catch (Exception e) {
            return false;
        }
        return reading(contents);
    }

    public List<String> getTitleHeader() {
        return header;
    }

    /**
     * 言語タイトル取得
     *
     * @param index
     * @return
     */
    public String getTitle(int index) {
        return getTitleHeader().get(index);
    }

    /**
     * 言語タイトルからインデックスを取得
     *
     * @param title
     * @return
     */
    public int getIndex(String title) {
        int index = 0;
        for (int i = 0; i < header.size(); i++) {
            String t = header.get(i);
            if (t.equalsIgnoreCase(title) == true) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 文字列取得
     *
     * @param id
     * @param langIndex
     * @return
     */
    public String getLanguageStr(String id, int langIndex) {
        String ret = BLANK_TEXT;
        if (langTable.containsKey(id) == true) {
            String[] table = langTable.get(id);
            if (table.length <= langIndex) {
                langIndex = 0;
            }
            ret = table[langIndex];
        }
        return ret;
    }
}
