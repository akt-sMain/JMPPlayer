package jmp.lang;

public class LanguageWords {

    /** Blankテキスト */
    public static final String BLANK_TEXT = "#BLANK#";

    private String[] words = null;

    public LanguageWords(String... strings) {
        words = strings;
    }

    public String[] getWords() {
        return words;
    }

    public int getSize() {
        return words.length;
    }

    public String getWord(int index) {
        if (index < 0 || words.length <= index) {
            return BLANK_TEXT;
        }
        return words[index];
    }
}
