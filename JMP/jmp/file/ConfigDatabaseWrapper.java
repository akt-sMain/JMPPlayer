package jmp.file;

import jmp.core.DataManager;

public class ConfigDatabaseWrapper implements IJmpConfigDatabase {

    private ConfigDatabase database = null;

    public ConfigDatabaseWrapper() {
        database = new ConfigDatabase(DataManager.CFG_INIT_TABLE.keySet());
        initialize();
    }

    public ConfigDatabaseWrapper(ConfigDatabase db) {
        database = db;
    }

    public void initialize() {
        for (String key : DataManager.CFG_INIT_TABLE.keySet()) {
            String init = "";
            if (DataManager.CFG_INIT_TABLE.containsKey(key) == true) {
                JmpConfigValueType valueType = DataManager.CFG_INIT_TABLE.get(key);
                init = new String(valueType.value);
            }
            database.setConfigParam(key, init);
        }
    }

    public ConfigDatabase getConfigDatabase() {
        return database;
    }

    @Override
    public void setConfigParam(String key, String value) {
        database.setConfigParam(key, value);
    }

    @Override
    public String getConfigParam(String key) {
        return database.getConfigParam(key);
    }

}
