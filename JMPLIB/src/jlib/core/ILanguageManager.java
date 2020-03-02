package jlib.core;

import jlib.lang.IUpdateLanguageListener;

public interface ILanguageManager {
    /**
     * リスナー登録
     *
     * @param listener
     *            リスナー
     */
    abstract void registerListener(IUpdateLanguageListener listener);
}
