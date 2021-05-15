package jmp;

import function.Platform;
import function.Platform.KindOfPlatform;
import jmp.core.DataManager;
import jmp.util.JmpUtil;

public interface IJmpConfigDatabase {

    //
    // abstract
    //
    abstract void setConfigParam(String key, String value);

    abstract String getConfigParam(String key);

    //
    // getter setter
    //
    default boolean isAutoPlay() {
        return JmpUtil.toBoolean(getConfigParam(DataManager.CFG_KEY_AUTOPLAY));
    }

    default void setAutoPlay(boolean isAutoPlay) {
        setConfigParam(DataManager.CFG_KEY_AUTOPLAY, isAutoPlay ? "TRUE" : "FALSE");
    }

    default boolean isLoopPlay() {
        return JmpUtil.toBoolean(getConfigParam(DataManager.CFG_KEY_LOOPPLAY));
    }

    default void setLoopPlay(boolean isLoopPlay) {
        setConfigParam(DataManager.CFG_KEY_LOOPPLAY, isLoopPlay ? "TRUE" : "FALSE");
    }

    default boolean isShowStartupDeviceSetup() {
        return JmpUtil.toBoolean(getConfigParam(DataManager.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP));
    }

    default void setShowStartupDeviceSetup(boolean isShow) {
        setConfigParam(DataManager.CFG_KEY_SHOW_STARTUP_DEVICE_SETUP, isShow ? "TRUE" : "FALSE");
    }

    default int getLanguage() {
        return JmpUtil.toInt(getConfigParam(DataManager.CFG_KEY_LANGUAGE));
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
        return JmpUtil.toBoolean(getConfigParam(DataManager.CFG_KEY_LYRIC_VIEW));
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
        return JmpUtil.toBoolean(getConfigParam(DataManager.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE));
    }

    default void setFFmpegLeaveOutputFile(boolean isLeave) {
        setConfigParam(DataManager.CFG_KEY_FFMPEG_LEAVE_OUTPUT_FILE, isLeave ? "TRUE" : "FALSE");
    }

    default boolean isUseFFmpegPlayer() {
        return JmpUtil.toBoolean(getConfigParam(DataManager.CFG_KEY_USE_FFMPEG_PLAYER));
    }

    default void setUseFFmpegPlayer(boolean isUse) {
        setConfigParam(DataManager.CFG_KEY_USE_FFMPEG_PLAYER, isUse ? "TRUE" : "FALSE");
    }

    default boolean isFFmpegInstalled() {
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            return true;
        }
        return JmpUtil.toBoolean(getConfigParam(DataManager.CFG_KEY_FFMPEG_INSTALLED));
    }

    default void setFFmpegInstalled(boolean isEnableEnvironmentVariable) {
        setConfigParam(DataManager.CFG_KEY_FFMPEG_INSTALLED, isEnableEnvironmentVariable ? "TRUE" : "FALSE");
    }

    default boolean isSendMidiSystemSetup() {
        return JmpUtil.toBoolean(getConfigParam(DataManager.CFG_KEY_SEND_MIDI_SYSTEMSETUP));
    }

    default void setSendMidiSystemSetup(boolean isSendMidiSystemSetup) {
        setConfigParam(DataManager.CFG_KEY_SEND_MIDI_SYSTEMSETUP, isSendMidiSystemSetup ? "TRUE" : "FALSE");
    }

    default String getYoutubeDlPath() {
        return getConfigParam(DataManager.CFG_KEY_YOUTUBEDL_PATH);
    }

    default void setYoutubeDlPath(String filePath) {
        setConfigParam(DataManager.CFG_KEY_YOUTUBEDL_PATH, filePath);
    }
}
