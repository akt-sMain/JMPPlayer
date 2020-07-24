package jmp.lang;

public class DefineLanguage {
    public static final int INDEX_LANG_ENGLISH = 0;
    public static final int INDEX_LANG_JAPANESE = 1;
    public static final int INDEX_LANG_CHINESE = 2;
    public static final int NUMBER_OF_INDEX_LANG = 3;
    static final LangID[] titles = new LangID[] { LangID.English, LangID.Japanese, LangID.Chinese, };

    public static enum LangID {
        Language,
        Japanese,
        English,
        Korean,
        Chinese,
        File,
        Save,
        Open,
        Reload,
        Version,
        Exit,
        Window,
        Allways_on_top,
        Player,
        Loop_playback,
        Continuous_playback,
        Playlist,
        History,
        Plugin,
        Add_plugin,
        Remove_plugin,
        Close_all_plugin,
        Setting,
        MIDI_settings,
        MIDI_device_settings,
        Open_MIDI_device_settings_on_startup,
        PC_reset,
        MIDI_message_monitor,
        MIDI_message_sender,
        Now_loading,
        Plugin_error,
        FILE_ERROR_1,
        FILE_ERROR_2,
        FILE_ERROR_3,
        FILE_ERROR_4,
        FILE_ERROR_5,
        FILE_ERROR_6,
        FILE_LOAD_SUCCESS,
        PLUGIN_LOAD_SUCCESS,
        PLUGIN_LOAD_ERROR,
        Clear,
        Playback,
        Stop,
        Fast_forward,
        Rewind,
        Next,
        Previous,
        Close,
        Add,
        Remove,
        Open_with_Explorer,
        MIDI_Data_byte,
        Send,
        Invalid_byte_data,
        License,
        Above_conditions,
        Accept,
        Reject,
        Copied_to_clipboard,
        Original_copy,
        Common_settings,
        Apply,
        Error,
        Message,
        Layout_initialization,
        Tool,
        FFmpeg_converter,
        FFmpeg_path,
        Output_directory,
        Input_file,
        Play_after_convert,
        Convert,
        A_is_invalid,
        Conversion_failed,
        Conversion_completed,
        Now_converting,
        Initialize_setting,
        Leave_output_file,
        Use_FFmpeg_player,
        Automatically_assign_MIDI_channel,
        Automatically_assign_Program_change_number,
        Confirm_jms_with_different_versions,
        Confirm_jmz_with_different_versions,
    }

    static java.util.HashMap<LangID, LanguageWords> langMap = new java.util.HashMap<LangID, LanguageWords>() {
        {
            put(LangID.Language, new LanguageWords("Language", "言語", "语言"));
            put(LangID.Japanese, new LanguageWords("日本語", "日本語", "日本语"));
            put(LangID.English, new LanguageWords("English", "English", "英语"));
            put(LangID.Korean, new LanguageWords("한국어", "한국어", "한국어"));
            put(LangID.Chinese, new LanguageWords("中文", "中文", "中文"));
            put(LangID.File, new LanguageWords("File", "ファイル", "文件"));
            put(LangID.Save, new LanguageWords("Save", "保存", "保存"));
            put(LangID.Open, new LanguageWords("Open", "開く", "打开"));
            put(LangID.Reload, new LanguageWords("Reload", "リロード", "重装"));
            put(LangID.Version, new LanguageWords("Version", "バージョン情報", "版"));
            put(LangID.Exit, new LanguageWords("Exit", "終了", "出口"));
            put(LangID.Window, new LanguageWords("Window", "ウィンドウ", "窗口"));
            put(LangID.Allways_on_top, new LanguageWords("Allways on top", "常に手前に表示", "总在最前面"));
            put(LangID.Player, new LanguageWords("Player", "プレイヤー", "播放器"));
            put(LangID.Loop_playback, new LanguageWords("Loop playback", "ループ再生", "循环播放"));
            put(LangID.Continuous_playback, new LanguageWords("Continuous playback", "連続再生", "连续播放"));
            put(LangID.Playlist, new LanguageWords("Playlist", "プレイリスト", "播放清单"));
            put(LangID.History, new LanguageWords("History", "履歴", "历史"));
            put(LangID.Plugin, new LanguageWords("Plugin", "プラグイン", "插入"));
            put(LangID.Add_plugin, new LanguageWords("Add plugin", "プラグインを追加", "添加插件"));
            put(LangID.Remove_plugin, new LanguageWords("Remove plugin", "プラグインを削除", "删除插件"));
            put(LangID.Close_all_plugin, new LanguageWords("Close all plugin", "すべて閉じる", "关闭所有插件"));
            put(LangID.Setting, new LanguageWords("Setting", "設定", "设置"));
            put(LangID.MIDI_settings, new LanguageWords("MIDI Settings", "MIDI設定", "MIDI设定"));
            put(LangID.MIDI_device_settings, new LanguageWords("MIDI Device settings", "MIDIデバイス設定", "MIDI设备设置"));
            put(LangID.Open_MIDI_device_settings_on_startup, new LanguageWords("Open MIDI device settings on startup", "起動時にMIDIデバイス設定を開く", "启动时打开MIDI设备设置"));
            put(LangID.PC_reset, new LanguageWords("Program change reset", "プログラムチェンジリセット", "程序变更重置"));
            put(LangID.MIDI_message_monitor, new LanguageWords("MIDI Message monitor", "MIDIメッセージモニター", "MIDI信息监视器"));
            put(LangID.MIDI_message_sender, new LanguageWords("MIDI Message sender", "MIDIメッセージ送信", "MIDI信息发送者"));
            put(LangID.Now_loading, new LanguageWords("Now loading", "ロード中", "现在加载"));
            put(LangID.Plugin_error, new LanguageWords("Plugin error", "プラグインエラー", "插件错误"));
            put(LangID.FILE_ERROR_1, new LanguageWords("※Other files cannot be loaded while loading a file.", "※ファイルロード中に他のファイルをロードできません。", "※加载文件时无法加载其他文件。"));
            put(LangID.FILE_ERROR_2, new LanguageWords("※File format not supported.", "※サポート外のファイル形式です", "※不支持文件格式。"));
            put(LangID.FILE_ERROR_3, new LanguageWords("※Files cannot be loaded during playback.", "※再生中はファイルロードできません。", "※播放过程中无法加载文件。"));
            put(LangID.FILE_ERROR_4, new LanguageWords("※The file cannot be opened.", "※ファイルを開くことができません。", "※文件无法打开。"));
            put(LangID.FILE_ERROR_5, new LanguageWords("※Failed to load the file.", "※ファイルのロードに失敗しました。", "※无法加载文件。"));
            put(LangID.FILE_ERROR_6, new LanguageWords("※There is no data to play.", "※再生するデータがありません。", "※没有数据可玩。"));
            put(LangID.FILE_LOAD_SUCCESS, new LanguageWords("File loading successful.", "ファイルロード成功", "文件加载成功。"));
            put(LangID.PLUGIN_LOAD_SUCCESS, new LanguageWords("Plugin loading successful.", "プラグインロード成功", "插件加载成功。"));
            put(LangID.PLUGIN_LOAD_ERROR, new LanguageWords("Plugin loading failed.", "プラグインロード失敗", "插件加载失败。"));
            put(LangID.Clear, new LanguageWords("Clear", "クリア", "明确"));
            put(LangID.Playback, new LanguageWords("Playback", "再生", "回放"));
            put(LangID.Stop, new LanguageWords("Stop", "停止", "停止"));
            put(LangID.Fast_forward, new LanguageWords("Fast forward", "早送り", "快进"));
            put(LangID.Rewind, new LanguageWords("Rewind", "巻き戻し", "倒带"));
            put(LangID.Next, new LanguageWords("Next", "次へ", "下一个"));
            put(LangID.Previous, new LanguageWords("Previous", "前へ", "以前"));
            put(LangID.Close, new LanguageWords("Close", "閉じる", "关"));
            put(LangID.Add, new LanguageWords("Add", "追加", "加"));
            put(LangID.Remove, new LanguageWords("Remove", "削除", "去掉"));
            put(LangID.Open_with_Explorer, new LanguageWords("Open with Explorer", "エクスプローラで開く", "用资源管理器打开"));
            put(LangID.MIDI_Data_byte, new LanguageWords("MIDI Data byte（Hex）", "MIDIデータバイト（Hex）", "MIDI数据字节（十六进制）"));
            put(LangID.Send, new LanguageWords("Send", "送信", "发送"));
            put(LangID.Invalid_byte_data, new LanguageWords("Invalid byte data", "無効なバイトデータ", "无效的字节数据"));
            put(LangID.License, new LanguageWords("License", "ライセンス", "执照"));
            put(LangID.Above_conditions, new LanguageWords("Above conditions：", "上記の条件に：", "以上条件："));
            put(LangID.Accept, new LanguageWords("Accept", "同意する", "接受"));
            put(LangID.Reject, new LanguageWords("Reject", "同意しない", "拒绝"));
            put(LangID.Copied_to_clipboard, new LanguageWords("Copied to clipboard.", "クリップボードにコピーしました。", "复制到剪贴板。"));
            put(LangID.Original_copy, new LanguageWords("Original copy", "原文のコピー", "最初版"));
            put(LangID.Common_settings, new LanguageWords("Common settings", "共通設定", "常用设定"));
            put(LangID.Apply, new LanguageWords("Apply", "適用", "应用"));
            put(LangID.Error, new LanguageWords("Error", "エラー", "错误"));
            put(LangID.Message, new LanguageWords("Message", "メッセージ", "信息"));
            put(LangID.Layout_initialization, new LanguageWords("Layout initialization", "レイアウト初期化", "布局初始化"));
            put(LangID.Tool, new LanguageWords("Tool", "ツール", "工具"));
            put(LangID.FFmpeg_converter, new LanguageWords("FFmpeg converter", "FFmpegコンバータ", "FFmpeg转换器"));
            put(LangID.FFmpeg_path, new LanguageWords("FFmpeg path(.exe)", "FFmpeg実行ファイル(.exe)", "FFmpeg路径（.exe）"));
            put(LangID.Output_directory, new LanguageWords("Output directory", "出力フォルダ", "输出目录"));
            put(LangID.Input_file, new LanguageWords("Input file", "入力ファイル", "输入文件"));
            put(LangID.Play_after_convert, new LanguageWords("Play after convert", "変換後に再生する", "转换后播放"));
            put(LangID.Convert, new LanguageWords("Convert", "変換", "兑换"));
            put(LangID.A_is_invalid, new LanguageWords("[NAME] is invalid.", "[NAME]が無効です。", "[NAME]无效。"));
            put(LangID.Conversion_failed, new LanguageWords("Conversion failed.", "変換失敗。", "转换失败。"));
            put(LangID.Conversion_completed, new LanguageWords("Conversion completed.", "変換完了。", "转换完成。"));
            put(LangID.Now_converting, new LanguageWords("Now converting.", "変換中。", "现在转换。"));
            put(LangID.Initialize_setting, new LanguageWords("Initialize setting", "初期設定に戻す", "初始化设定"));
            put(LangID.Leave_output_file, new LanguageWords("Leave output file", "出力ファイルを残す", "离开输出文件"));
            put(LangID.Use_FFmpeg_player, new LanguageWords("Use FFmpeg player", "FFmpegプレイヤー使用", "使用FFmpeg播放器"));
            put(LangID.Automatically_assign_MIDI_channel, new LanguageWords("Automatically assign MIDI channel", "「MIDIチャンネル」を自動的に割り当てる", "自动分配MIDI通道"));
            put(LangID.Automatically_assign_Program_change_number,
                    new LanguageWords("Automatically assign Program change number", "「プログラムチェンジ」を自動的に割り当てる", "自动分配程序更改号"));
            put(LangID.Confirm_jms_with_different_versions, new LanguageWords("There are plugins with different versions. Do you want to load it?",
                    "バージョンが異なるプラグインが存在します。ロードしますか？", "有不同版本的插件。是否要加载？"));
            put(LangID.Confirm_jmz_with_different_versions,
                    new LanguageWords("The version of the plugin you are trying to add is different. Do you want to add it?",
                            "追加しようとしているプラグインのバージョンが異なります。追加しますか？", "您尝试添加的插件版本不同。 您要添加吗？"));
        }
    };
}
