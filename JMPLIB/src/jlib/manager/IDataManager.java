package jlib.manager;

public interface IDataManager extends IManager {

    /**
     * コンフィグ変更
     *
     * @param key
     *            キー名
     * @param value
     *            コンフィグ値
     */
    abstract void setConfigParam(String key, String value);

    /**
     * コンフィグ値取得
     *
     * @param key
     *            キー名
     * @return コンフィグ値
     */
    abstract String getConfigParam(String key);
}
