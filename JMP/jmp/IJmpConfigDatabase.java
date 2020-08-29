package jmp;

import function.Platform;
import function.Platform.KindOfPlatform;
import function.Utility;
import jmp.core.DataManager;

public interface IJmpConfigDatabase {

    abstract void setConfigParam(String key, String value);
    abstract String getConfigParam(String key);

    default boolean isAutoPlay() {
        String sValue = getConfigParam(DataManager.CFG_KEY_AUTOPLAY);
        return Utility.tryParseBoolean(sValue, false);
    }

    default void setAutoPlay(boolean isAutoPlay) {
        setConfigParam(DataManager.CFG_KEY_AUTOPLAY, isAutoPlay ? "TRUE" : "FALSE");
    }

    default boolean isLoopPlay() {
        String sValue = getConfigParam(DataManager.CFG_KEY_LOOPPLAY);
        return Utility.tryParseBoolean(sValue, false);
    }

    default void setLoopPlay(boolean isLoopPlay) {
        setConfigParam(DataManager.CFG_KEY_LOOPPLAY, isLoopPlay ? "TRUE" : "FALSE");
    }

    default boolean isShowStartupDeviceSetup() {
        String sValue = getConfigParam(DataManager.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP);
        return Utility.tryParseBoolean(sValue, false);
    }

    default void setShowStartupDeviceSetup(boolean isShow) {
        setConfigParam(DataManager.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, isShow ? "TRUE" : "FALSE");
    }

    default int getLanguage() {
        String sValue = getConfigParam(DataManager.CFG_KEY_LANGUAGE);
        return Utility.tryParseInt(sValue, 0);
    }

    default void setLanguage(int language) {
        setConfigParam(DataManager.CFG_KEY_LANGUAGE, String.valueOf(language));
    }

    default String getPlayListPath() {
        return getConfigParam(DataManager.CFG_KEY_PLAYLIST);
    }

    default void setPlayListPath(String filePath) {
        setConfigParam(DataManager.CFG_KEY_PLAYLIST, filePath);
    }

    default String getLoadedFile() {
        return getConfigParam(DataManager.CFG_KEY_LOADED_FILE);
    }

    default void setLoadedFile(String filePath) {
        setConfigParam(DataManager.CFG_KEY_LOADED_FILE, filePath);
    }

    default boolean isLyricView() {
        String sValue = getConfigParam(DataManager.CFG_KEY_LYRIC_VIEW);
        return Utility.tryParseBoolean(sValue, false);
    }

    default void setLyricView(boolean isLyricView) {
        setConfigParam(DataManager.CFG_KEY_LYRIC_VIEW, isLyricView ? "TRUE" : "FALSE");
    }

    default String getFFmpegPath() {
        return getConfigParam(DataManager.CFG_KEY_FFMPEG_PATH);
    }

    default void setFFmpegPath(String filePath) {
        setConfigParam(DataManager.CFG_KEY_FFMPEG_PATH, filePath);
    }

    default boolean isFFmpegLeaveOutputFile() {
        String sValue = getConfigParam(DataManager.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE);
        return Utility.tryParseBoolean(sValue, false);
    }

    default void setFFmpegLeaveOutputFile(boolean isLeave) {
        setConfigParam(DataManager.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, isLeave ? "TRUE" : "FALSE");
    }

    default boolean isUseFFmpegPlayer() {
        String sValue = getConfigParam(DataManager.CFG_KEY_USE_FFMPEG_PLAYER);
        return Utility.tryParseBoolean(sValue, false);
    }

    default void setUseFFmpegPlayer(boolean isUse) {
        setConfigParam(DataManager.CFG_KEY_USE_FFMPEG_PLAYER, isUse ? "TRUE" : "FALSE");
    }

    default boolean isFFmpegInstalled() {
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            return true;
        }
        String sValue = getConfigParam(DataManager.CFG_KEY_FFMPEG_INSTALLED);
        return Utility.tryParseBoolean(sValue, false);
    }

    default void setFFmpegInstalled(boolean isEnableEnvironmentVariable) {
        setConfigParam(DataManager.CFG_KEY_FFMPEG_INSTALLED, isEnableEnvironmentVariable? "TRUE" : "FALSE");
    }
}
