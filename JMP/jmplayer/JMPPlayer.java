package jmplayer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Platform;
import function.Platform.KindOfPlatform;
import function.Utility;
import jlib.gui.IJmpMainWindow;
import jlib.gui.IJmpWindow;
import jlib.player.IPlayer;
import jlib.plugin.IPlugin;
import jmp.FileResult;
import jmp.IFileResultCallback;
import jmp.JMPFlags;
import jmp.JMPLoader;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.PluginManager;
import jmp.core.SoundManager;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.gui.JmpPlayerLaunch;
import jmp.gui.JmpQuickLaunch;
import jmp.gui.VersionInfoDialog;
import jmp.gui.ui.ControlButtonUI;
import jmp.gui.ui.DropFileCallbackHandler;
import jmp.gui.ui.IButtonMarkPaint;
import jmp.gui.ui.IDropFileCallback;
import jmp.gui.ui.IJMPComponentUI;
import jmp.gui.ui.SequencerSliderUI;
import jmp.lang.DefineLanguage.LangID;
import jmp.task.CallbackPackage;
import jmp.task.ICallbackFunction;

/**
 * MIDIプレイヤーメインウィンドウクラス
 *
 * @author abs
 *
 */
public class JMPPlayer extends JFrame implements WindowListener, IJmpMainWindow, IJMPComponentUI, IDropFileCallback {

    private class JmpMenuListener implements MenuListener {

        @Override
        public void menuSelected(MenuEvent e) {
            updateMenuState();
        }

        @Override
        public void menuDeselected(MenuEvent e) {
        }

        @Override
        public void menuCanceled(MenuEvent e) {
        }

    }

    // ! アプリケーション名
    public static final String APP_TITLE = JMPCore.APPLICATION_NAME;
    // ! タイマーによるカラートグル制御
    public static boolean TimerColorToggleFlag = false;
    // ! ステータスカラー(File)
    public static final Color STATUS_COLOR_FILE = Color.CYAN;
    // ! ステータスカラー(Pass)
    public static final Color STATUS_COLOR_PASS = Color.GREEN;
    // ! ステータスカラー(Fail)
    public static final Color STATUS_COLOR_FAIL = Color.RED;
    // ! コントロールボタン背景色
    // public static final Color CONTROL_BTN_BACKGROUND =
    // Utility.convertCodeToHtmlColor("#888888");

    // ! カレントタイムフォーマット
    public static final String CURRENT_TIME_FORMAT = "%s／%s ";

    private static long s_tmpSliderTick = -1;

    private VersionInfoDialog versionInfoDialog = null;

    // コンポーネント
    private JMenu pluginMenu;
    private JLabel statusLabel;
    private JSlider slider;
    private JButton playButton;
    private JButton prev2Button;
    private JButton prevButton;
    private JButton nextButton;
    private JButton next2Button;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JCheckBoxMenuItem alwayTopCheckBox;
    private JCheckBoxMenuItem autoPlayCheckBox;
    private JMenu playerMenu;
    private JMenuItem removePluginMenuItem;
    private JCheckBoxMenuItem loopPlayCheckBoxMenuItem;
    private JMenuItem mntmMidiDeviceSetup;
    private JMenuItem zipGenerateMenuItem;
    private JMenuItem removeAllPluginMenuItem;
    private JLabel labelPositionTime;
    private JCheckBoxMenuItem chckbxmntmStartupmidisetup;
    private JLabel lblDebugMenu;
    private JMenuItem allClosePluginMenuItem;
    private JMenu mnExecuteBatFile;
    private JMenuItem mntmMidiMonitor;
    private JMenuItem addPluginMenuItem;
    private JMenuItem mntmMidimessagesender;
    private JMenuItem openItem;
    private JMenuItem mntmReload;
    private JMenuItem mntmVersion;
    private JMenuItem endItem;
    private JMenu windowMenu;
    private JMenuItem playlistItem;
    private JMenuItem menuItemHistory;
    private JLabel lblMidi;
    private JMenuItem mntmPcreset;
    private JMenu configMenu;
    private JLabel lblCommon;
    private JMenuItem menuItemLanguage;
    private JPanel panel_1;
    private JPanel panel_2;
    private JPanel panel_3;
    private JPanel panel_4;
    private JPanel panel_5;
    private JPanel panel_6;
    private JMenuItem mntmInitLayout;
    private JMenuItem mntmFFmpegConverter;
    private JMenu mnTool;
    private JMenuItem mntmInitializeConfig;

    /**
     * コンストラクタ(WindowBuilderによる自動生成)
     *
     *
     * @throws HeadlessException
     */
    public JMPPlayer() throws HeadlessException {
        super();

        getContentPane().setBackground(getJmpBackColor());
        setTitle(APP_TITLE);
        this.addWindowListener(this);
        this.setTransferHandler(new DropFileCallbackHandler(this));
        setBounds(WindowManager.DEFAULT_PLAYER_WINDOW_SIZE);

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        // ウィンドウマネージャーに登録
        JMPCore.getWindowManager().register(WindowManager.WINDOW_NAME_MAIN, this);

        // Midiリストダイアログ
        getContentPane().setLayout(new BorderLayout(0, 0));

        panel_2 = new JPanel();
        panel_2.setBackground(Color.BLACK);
        getContentPane().add(panel_2);
        panel_2.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel_2.add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        panel_1 = new JPanel();
        panel_1.setBackground(getJmpBackColor());
        panel.add(panel_1, BorderLayout.CENTER);
        panel_1.setLayout(new GridLayout(0, 5, 15, 0));

        prev2Button = new JButton("←←");
        panel_1.add(prev2Button);
        prev2Button.setToolTipText("前の曲へ");
        prev2Button.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());

        prevButton = new JButton("←");
        panel_1.add(prevButton);
        prevButton.setToolTipText("巻き戻し");
        prevButton.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());

        playButton = new JButton("再生");
        panel_1.add(playButton);
        playButton.setToolTipText("再生/停止");
        playButton.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());

        nextButton = new JButton("→");
        panel_1.add(nextButton);
        nextButton.setToolTipText("早送り");
        nextButton.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());

        next2Button = new JButton("→→");
        panel_1.add(next2Button);
        next2Button.setToolTipText("次の曲へ");
        next2Button.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());

        panel_3 = new JPanel();
        panel_3.setBounds(36, 237, 495, 46);
        panel.add(panel_3, BorderLayout.SOUTH);
        panel_3.setLayout(new BorderLayout(0, 0));

        slider = new JSlider();
        panel_3.add(slider, BorderLayout.CENTER);
        slider.setForeground(new Color(255, 255, 255));
        slider.setBackground(getJmpBackColor());
        slider.setValue(0);

        panel_4 = new JPanel();
        panel_4.setBackground(getJmpBackColor());
        panel_3.add(panel_4, BorderLayout.SOUTH);
        panel_4.setLayout(new GridLayout(0, 2, 0, 0));

        panel_6 = new JmpPlayerLaunch();
        panel_6.setBackground(getJmpBackColor());
        panel_4.add(panel_6);

        labelPositionTime = new JLabel(String.format(CURRENT_TIME_FORMAT, "00:00", "00:00"));
        labelPositionTime.setHorizontalAlignment(SwingConstants.RIGHT);
        labelPositionTime.setFont(new Font("Dialog", Font.BOLD, 18));
        panel_4.add(labelPositionTime);
        labelPositionTime.setForeground(Color.WHITE);

        panel_5 = new JPanel();
        panel_5.setBorder(new LineBorder(Color.WHITE, 2));
        panel_5.setBackground(Color.BLACK);
        panel_2.add(panel_5, BorderLayout.SOUTH);
        panel_5.setLayout(new BorderLayout(0, 0));

        statusLabel = new JLabel(" ");
        panel_5.add(statusLabel);
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                s_tmpSliderTick = slider.getValue();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                long value = s_tmpSliderTick;
                if (s_tmpSliderTick != -1) {
                    JMPCore.getSoundManager().getCurrentPlayer().setPosition(value);
                }
                s_tmpSliderTick = -1;
            }
        });
        slider.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                s_tmpSliderTick = slider.getValue();
            }
        });
        next2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().playNextForList();
            }
        });
        nextButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                JMPCore.getSoundManager().fastForward();
            }
        });
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().togglePlayStop();
                repaint();
            }
        });
        prevButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JMPCore.getSoundManager().rewind();
            }
        });
        prev2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().playPrevForList();
            }
        });

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        fileMenu = new JMenu("ファイル");
        fileMenu.addMenuListener(new JmpMenuListener());
        menuBar.add(fileMenu);

        openItem = new JMenuItem("開く");
        openItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileOpenFunc();
            }
        });
        fileMenu.add(openItem);

        endItem = new JMenuItem("終了");
        endItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit(true);
            }
        });

        mntmReload = new JMenuItem("リロード");
        mntmReload.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path = JMPCore.getDataManager().getLoadedFile();
                if (path.isEmpty() == false) {
                    File f = new File(path);
                    if (f.exists() == true && f.canRead() == true) {
                        JMPCore.getFileManager().loadFile(path);
                    }
                }
            }
        });
        fileMenu.add(mntmReload);

        JSeparator separator = new JSeparator();
        fileMenu.add(separator);

        mntmVersion = new JMenuItem("バージョン情報");
        mntmVersion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                versionInfoDialog.start();
            }
        });
        fileMenu.add(mntmVersion);
        fileMenu.add(endItem);

        windowMenu = new JMenu("ウィンドウ");
        windowMenu.addMenuListener(new JmpMenuListener());
        menuBar.add(windowMenu);

        alwayTopCheckBox = new JCheckBoxMenuItem("常に手前で表示");
        alwayTopCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setAlwaysOnTop(alwayTopCheckBox.isSelected());
            }
        });
        windowMenu.add(alwayTopCheckBox);

        mntmInitLayout = new JMenuItem("レイアウト初期化");
        mntmInitLayout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().initializeLayout();
                JMPCore.getWindowManager().setVisibleAll(false);
            }
        });
        windowMenu.add(mntmInitLayout);

        playerMenu = new JMenu("プレイヤー");
        playerMenu.addMenuListener(new JmpMenuListener());
        menuBar.add(playerMenu);

        loopPlayCheckBoxMenuItem = new JCheckBoxMenuItem("ループ再生");
        loopPlayCheckBoxMenuItem.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JMPCore.getDataManager().setLoopPlay(loopPlayCheckBoxMenuItem.isSelected());
            }
        });
        playerMenu.add(loopPlayCheckBoxMenuItem);

        autoPlayCheckBox = new JCheckBoxMenuItem("連続再生");
        playerMenu.add(autoPlayCheckBox);
        autoPlayCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JMPCore.getDataManager().setAutoPlay(autoPlayCheckBox.isSelected());
            }
        });

        playlistItem = new JMenuItem("プレイリスト");
        playerMenu.add(playlistItem);

        menuItemHistory = new JMenuItem("履歴");
        menuItemHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getDataManager().getHistoryDialog().open();
            }
        });
        playerMenu.add(menuItemHistory);
        playlistItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IJmpWindow win = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_FILE_LIST);
                if (win.isWindowVisible() == true) {
                    win.hideWindow();
                }
                else {
                    win.showWindow();
                }
            }
        });

        pluginMenu = new JMenu("プラグイン");
        pluginMenu.addMenuListener(new JmpMenuListener());

        mnTool = new JMenu("Tool");
        mnTool.addMenuListener(new JmpMenuListener());
        menuBar.add(mnTool);

        mntmFFmpegConverter = new JMenuItem("FFmpeg converter");
        mnTool.add(mntmFFmpegConverter);
        mntmFFmpegConverter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_FFMPEG).showWindow();
            }
        });

        mntmMidiMonitor = new JMenuItem("MIDIメッセージモニタ");
        mnTool.add(mntmMidiMonitor);

        mntmMidimessagesender = new JMenuItem("MIDIメッセージセンダー");
        mnTool.add(mntmMidimessagesender);
        mntmMidimessagesender.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IJmpWindow win = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_SENDER);
                if (win.isWindowVisible() == true) {
                    win.hideWindow();
                }
                else {
                    win.showWindow();
                }
            }
        });
        mntmMidiMonitor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IJmpWindow win = JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_MONITOR);
                if (win.isWindowVisible() == true) {
                    win.hideWindow();
                }
                else {
                    win.showWindow();
                }
            }
        });
        menuBar.add(pluginMenu);

        addPluginMenuItem = new JMenuItem("プラグイン追加");
        addPluginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                // FileNameExtensionFilter filter = new
                // FileNameExtensionFilter("PLUGIN SETUP Files(*.JMS)",
                // PluginManager.SETUP_FILE_EX);
                filechooser.setFileFilter(createFileFilter("PLUGIN Files", PluginManager.PLUGIN_ZIP_EX));
                File dir = new File(Platform.getCurrentPath());
                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        if (file.isDirectory() == false) {
                            if (Utility.checkExtension(file, PluginManager.SETUP_FILE_EX) == true) {
                                catchDropFile(file);
                            }
                            else if (Utility.checkExtension(file, PluginManager.PLUGIN_ZIP_EX) == true) {
                                catchDropFile(file);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        pluginMenu.add(addPluginMenuItem);

        removePluginMenuItem = new JMenuItem("プラグイン削除");
        removePluginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // ファイルフィルター
                JFileChooser filechooser = new JFileChooser();
                filechooser.setFileFilter(createFileFilter("PLUGIN SETUP Files", PluginManager.SETUP_FILE_EX));

                File dir = new File(JMPCore.getSystemManager().getJmsDirPath());
                filechooser.setCurrentDirectory(dir);
                int selected = filechooser.showOpenDialog(getParent());
                switch (selected) {
                    case JFileChooser.APPROVE_OPTION:
                        File file = filechooser.getSelectedFile();
                        String path = file.getPath();
                        if (file.isDirectory() == false) {
                            if (Utility.checkExtension(path, PluginManager.SETUP_FILE_EX) == true) {
                                File jmsFile = new File(path);
                                JMPCore.getPluginManager().reserveRemovePlugin(jmsFile);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        pluginMenu.add(removePluginMenuItem);

        allClosePluginMenuItem = new JMenuItem("すべて閉じる");
        allClosePluginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getPluginManager().closeAllPlugins();
            }
        });
        pluginMenu.add(allClosePluginMenuItem);
        pluginMenu.addSeparator();

        configMenu = new JMenu("設定");
        configMenu.addMenuListener(new JmpMenuListener());
        menuBar.add(configMenu);

        mntmMidiDeviceSetup = new JMenuItem("MIDIデバイス設定");
        mntmMidiDeviceSetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoundManager.MidiPlayer.getSelectSynthsizerDialog().start();
            }
        });

        lblCommon = new JLabel("-- 共通設定 --");
        lblCommon.setForeground(Color.DARK_GRAY);
        lblCommon.setFont(new Font("Dialog", Font.BOLD, 12));
        configMenu.add(lblCommon);

        menuItemLanguage = new JMenuItem("言語設定");
        menuItemLanguage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_LANGUAGE).showWindow();
            }
        });
        configMenu.add(menuItemLanguage);

        mntmInitializeConfig = new JMenuItem("InitializeConfig");
        mntmInitializeConfig.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSystemManager().initializeAllSetting();
            }
        });
        configMenu.add(mntmInitializeConfig);

        lblMidi = new JLabel("-- MIDI設定 --");
        lblMidi.setForeground(Color.DARK_GRAY);
        lblMidi.setFont(new Font("Dialog", Font.BOLD, 12));
        lblMidi.setHorizontalAlignment(SwingConstants.LEFT);
        configMenu.add(lblMidi);
        configMenu.add(mntmMidiDeviceSetup);

        chckbxmntmStartupmidisetup = new JCheckBoxMenuItem("起動時にMIDIデバイス設定を開く");
        chckbxmntmStartupmidisetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getDataManager().setShowStartupDeviceSetup(chckbxmntmStartupmidisetup.isSelected());
            }
        });
        configMenu.add(chckbxmntmStartupmidisetup);

        mntmPcreset = new JMenuItem("PCリセット");
        mntmPcreset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().resetMidiEvent();
            }
        });
        configMenu.add(mntmPcreset);

        lblDebugMenu = new JLabel("-- Developer menu --");
        lblDebugMenu.setForeground(Color.DARK_GRAY);
        lblDebugMenu.setFont(new Font("Dialog", Font.BOLD, 12));
        lblDebugMenu.setHorizontalAlignment(SwingConstants.LEFT);
        configMenu.add(lblDebugMenu);

        zipGenerateMenuItem = new JMenuItem("generateZIP");
        configMenu.add(zipGenerateMenuItem);

        removeAllPluginMenuItem = new JMenuItem("removeAll");
        configMenu.add(removeAllPluginMenuItem);

        mnExecuteBatFile = new JMenu("Execute BAT file");
        mnExecuteBatFile.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                mnExecuteBatFile.removeAll();

                File current = new File(Platform.getCurrentPath(false));
                for (File f : current.listFiles()) {
                    if (Utility.checkExtension(f, "bat") == true) {
                        JMenuItem item = new JMenuItem(Utility.getFileNameAndExtension(f));
                        item.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                JMPCore.getSystemManager().executeBatFile(f.getAbsolutePath());
                            }
                        });
                        mnExecuteBatFile.add(item);
                    }
                }
            }
        });
        mnExecuteBatFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        configMenu.add(mnExecuteBatFile);
        removeAllPluginMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File dir = new File(JMPCore.getSystemManager().getJmsDirPath());
                for (File f : dir.listFiles()) {

                    if (Utility.checkExtension(f.getPath(), PluginManager.SETUP_FILE_EX) == false) {
                        continue;
                    }
                    JMPCore.getPluginManager().reserveRemovePlugin(f, false);
                }
            }
        });
        zipGenerateMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String path = Utility.pathCombin(Platform.getCurrentPath(false), "_zip");
                File dir = new File(path);
                if (dir.exists() == false) {
                    dir.mkdir();
                }
                if (dir.exists() == true) {
                    JMPCore.getPluginManager().generatePluginZipPackage(dir.getPath());
                }
                else {
                    System.out.println("Not make zip dir.");
                }
            }
        });

        versionInfoDialog = new VersionInfoDialog();
    }

    @Override
    public void initializeLayout() {
        IJmpMainWindow.super.initializeLayout();

        updateMenuState();
        setAlwaysOnTop(false);
        setSize(WindowManager.DEFAULT_PLAYER_WINDOW_SIZE.width, WindowManager.DEFAULT_PLAYER_WINDOW_SIZE.height);
    }

    @Override
    public void initializeSetting() {
        DataManager dm = JMPCore.getDataManager();

        // プラグイン追加
        for (String name : JMPCore.getPluginManager().getPluginsNameSet()) {
            IPlugin plugin = JMPCore.getPluginManager().getPlugin(name);
            if (plugin == null) {
                continue;
            }

            addPluginMenu(name, plugin);
        }

        if (JMPFlags.NonPluginLoadFlag == true) {
            pluginMenu.setEnabled(false);
        }

        /* 開発者用用メニュー表示 */
        if (JMPFlags.DebugMode == false) {
            lblDebugMenu.setVisible(false);
            zipGenerateMenuItem.setVisible(false);
            removeAllPluginMenuItem.setVisible(false);
            mnExecuteBatFile.setVisible(false);
        }

        /* Windows用の処理 */
        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            mnExecuteBatFile.setVisible(false);
            mntmFFmpegConverter.setVisible(false);
        }

        /* スタンドアロンモード用メニュー表示 */
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            pluginMenu.setVisible(false);
        }

        setUI();
        alwayTopCheckBox.setSelected(false);
        autoPlayCheckBox.setSelected(dm.isAutoPlay());
        loopPlayCheckBoxMenuItem.setSelected(dm.isLoopPlay());
        chckbxmntmStartupmidisetup.setSelected(dm.isShowStartupDeviceSetup());

        // シーケンスバーのトグル用コールバック関数を登録
        CallbackPackage callbackPkg = new CallbackPackage((long) 500);
        callbackPkg.addCallbackFunction(new ICallbackFunction() {
            @Override
            public void callback() {
                IPlayer player = JMPCore.getSoundManager().getCurrentPlayer();
                if (player != null) {
                    if (player.isRunnable() == true) {
                        // 再生バーのトグル
                        TimerColorToggleFlag = !(TimerColorToggleFlag);
                    }
                }
            }
        });
        JMPCore.getTaskManager().getTaskOfTimer().addCallbackPackage(callbackPkg);

        // ロード後のコールバックを登録
        JMPCore.getFileManager().addLoadResultCallback(new IFileResultCallback() {

            @Override
            public void end(FileResult result) {
                setStatusTextForFile();
                setStatusText(result.statusMsg, result.status);
            }

            @Override
            public void begin(FileResult result) {
                if (result.status == false) {
                    setStatusText(result.statusMsg, result.status);
                }
                else {
                    setStatusText(result.statusMsg, Color.LIGHT_GRAY);
                }
            }
        });
    }

    // private Color getCtrlBorderColor() {
    // Color ret = Color.DARK_GRAY;
    // return ret;
    // }
    // private Color getCtrlBorderColor(Color color) {
    // return getCtrlBorderColor(color, 120);
    // }
    private Color getCtrlBorderColor(Color color) {
        Color newColor = Utility.convertHighLightColor(color, 120);
        return newColor;
    }

    private int getCtrlBorderBold() {
        return 1;
    }

    /**
     * カスタムUI設定<br>
     * （コンストラクタでコールするとデザイナーがエラーになるため、mainでコールする。）
     */
    private void setUI() {
        slider.setUI(new SequencerSliderUI(slider));

        // 再生ボタンUI
        ControlButtonUI playUI = new ControlButtonUI();
        playUI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                if (JMPCore.getSoundManager().getCurrentPlayer().isValid() == true && JMPCore.getSoundManager().getCurrentPlayer().isRunnable() == true) {
                    // 停止ボタン
                    Image img = JMPCore.getResourceManager().getBtnStopIcon();
                    if (img != null) {
                        int imgX = (playButton.getWidth() - img.getWidth(null)) / 2;
                        int imgY = (playButton.getHeight() - img.getHeight(null)) / 2;
                        imgX = (imgX < 0) ? 0 : imgX;
                        imgY = (imgY < 0) ? 0 : imgY;
                        g.drawImage(img, imgX, imgY, null);
                        return;
                    }

                    int offsetW = (width / 2) - 4;
                    x += 2;

                    Color color = Color.RED;
                    g2d.setColor(color);
                    g2d.fillRect(x, y, offsetW, height);
                    g2d.setColor(getCtrlBorderColor(color));
                    for (int i = 0; i < getCtrlBorderBold(); i++) {
                        g2d.drawRect(x + i, y + i, offsetW - (i * 2), height - (i * 2));
                    }

                    g2d.setColor(color);
                    g2d.fillRect(x + offsetW + 4, y, offsetW, height);
                    g2d.setColor(getCtrlBorderColor(color));
                    for (int i = 0; i < getCtrlBorderBold(); i++) {
                        g2d.drawRect(x + offsetW + 4 + i, y + i, offsetW - (i * 2), height - (i * 2));
                    }
                }
                else {
                    // 再生ボタン
                    Image img = JMPCore.getResourceManager().getBtnPlayIcon();
                    if (img != null) {
                        int imgX = (playButton.getWidth() - img.getWidth(null)) / 2;
                        int imgY = (playButton.getHeight() - img.getHeight(null)) / 2;
                        imgX = (imgX < 0) ? 0 : imgX;
                        imgY = (imgY < 0) ? 0 : imgY;
                        g.drawImage(img, imgX, imgY, null);
                        return;
                    }

                    Color color = Utility.convertCodeToHtmlColor("#00ee00");
                    g2d.setColor(color);

                    int xPoints[] = { x, x, x + width };
                    int yPoints[] = { y, y + height, y + (height / 2) };
                    g2d.fillPolygon(xPoints, yPoints, 3);
                    g2d.setColor(getCtrlBorderColor(color));
                    for (int i = 0; i < getCtrlBorderBold(); i++) {
                        int[] xp, yp;
                        xp = Arrays.copyOf(xPoints, xPoints.length);
                        yp = Arrays.copyOf(yPoints, yPoints.length);
                        xp[0] += i;
                        xp[1] += i;
                        xp[2] -= i;
                        yp[0] += i;
                        yp[1] -= i;
                        g2d.drawPolygon(xp, yp, 3);
                    }
                }
            }
        });
        playButton.setUI(playUI);

        // 早送りUI
        ControlButtonUI nextUI = new ControlButtonUI();
        nextUI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Image img = JMPCore.getResourceManager().getBtnNextIcon();
                if (img != null) {
                    int imgX = (playButton.getWidth() - img.getWidth(null)) / 2;
                    int imgY = (playButton.getHeight() - img.getHeight(null)) / 2;
                    imgX = (imgX < 0) ? 0 : imgX;
                    imgY = (imgY < 0) ? 0 : imgY;
                    g.drawImage(img, imgX, imgY, null);
                    return;
                }

                Graphics2D g2d = (Graphics2D) g.create();
                Color color = Color.BLUE;
                for (int i = 0; i < 2; i++) {
                    int offsetW = width / 2;
                    int xPoints[] = { x + (offsetW * i), x + (offsetW * i), x + (offsetW * i) + width - offsetW };
                    int yPoints[] = { y, y + height, y + (height / 2) };
                    g2d.setColor(color);
                    g2d.fillPolygon(xPoints, yPoints, 3);
                    g2d.setColor(getCtrlBorderColor(color));
                    for (int j = 0; j < getCtrlBorderBold(); j++) {
                        int[] xp, yp;
                        xp = Arrays.copyOf(xPoints, xPoints.length);
                        yp = Arrays.copyOf(yPoints, yPoints.length);
                        xp[0] += j;
                        xp[1] += j;
                        xp[2] -= j;
                        yp[0] += j;
                        yp[1] -= j;
                        g2d.drawPolygon(xp, yp, 3);
                    }
                }
            }
        });
        nextButton.setUI(nextUI);

        // 巻き戻しUI
        ControlButtonUI prevUI = new ControlButtonUI();
        prevUI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Image img = JMPCore.getResourceManager().getBtnPrevIcon();
                if (img != null) {
                    int imgX = (playButton.getWidth() - img.getWidth(null)) / 2;
                    int imgY = (playButton.getHeight() - img.getHeight(null)) / 2;
                    imgX = (imgX < 0) ? 0 : imgX;
                    imgY = (imgY < 0) ? 0 : imgY;
                    g.drawImage(img, imgX, imgY, null);
                    return;
                }

                x -= 2;
                Graphics2D g2d = (Graphics2D) g.create();
                Color color = Color.BLUE;
                for (int i = 0; i < 2; i++) {
                    int offsetW = width / 2;
                    int xPoints[] = { x + (offsetW * i), x + (offsetW * i) + width - offsetW, x + (offsetW * i) + width - offsetW };
                    int yPoints[] = { y + (height / 2), y, y + height, };
                    g2d.setColor(color);
                    g2d.fillPolygon(xPoints, yPoints, 3);
                    g2d.setColor(getCtrlBorderColor(color));
                    for (int j = 0; j < getCtrlBorderBold(); j++) {
                        int[] xp, yp;
                        xp = Arrays.copyOf(xPoints, xPoints.length);
                        yp = Arrays.copyOf(yPoints, yPoints.length);
                        xp[0] += j;
                        xp[1] -= j;
                        xp[2] -= j;
                        yp[1] += j;
                        yp[2] -= j;
                        g2d.drawPolygon(xp, yp, 3);
                    }
                }
            }
        });
        prevButton.setUI(prevUI);

        // 次へUI
        ControlButtonUI next2UI = new ControlButtonUI();
        next2UI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Image img = JMPCore.getResourceManager().getBtnNext2Icon();
                if (img != null) {
                    int imgX = (playButton.getWidth() - img.getWidth(null)) / 2;
                    int imgY = (playButton.getHeight() - img.getHeight(null)) / 2;
                    imgX = (imgX < 0) ? 0 : imgX;
                    imgY = (imgY < 0) ? 0 : imgY;
                    g.drawImage(img, imgX, imgY, null);
                    return;
                }

                Graphics2D g2d = (Graphics2D) g.create();
                int xPoints[] = { x, x, (int) (x + (width * 0.7)) };
                int yPoints[] = { y, y + height, y + (height / 2) };
                Color color = Color.BLUE;
                g2d.setColor(color);
                g2d.fillPolygon(xPoints, yPoints, 3);
                g2d.setColor(getCtrlBorderColor(color));
                for (int j = 0; j < getCtrlBorderBold(); j++) {
                    int[] xp, yp;
                    xp = Arrays.copyOf(xPoints, xPoints.length);
                    yp = Arrays.copyOf(yPoints, yPoints.length);
                    xp[0] += j;
                    xp[1] += j;
                    xp[2] -= j;
                    yp[0] += j;
                    yp[1] -= j;
                    g2d.drawPolygon(xp, yp, 3);
                }

                g2d.setColor(color);
                g2d.fillRect(x + (int) (width * 0.7), y, (int) (width * 0.3), height);
                g2d.setColor(getCtrlBorderColor(color));
                for (int j = 0; j < getCtrlBorderBold(); j++) {
                    g2d.drawRect(x + (int) (width * 0.7) + j, y + j, (int) (width * 0.3) - (j * 2), height - (j * 2));
                }
            }
        });
        next2Button.setUI(next2UI);

        // 前へUI
        ControlButtonUI prev2UI = new ControlButtonUI();
        prev2UI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Image img = JMPCore.getResourceManager().getBtnPrev2Icon();
                if (img != null) {
                    int imgX = (playButton.getWidth() - img.getWidth(null)) / 2;
                    int imgY = (playButton.getHeight() - img.getHeight(null)) / 2;
                    imgX = (imgX < 0) ? 0 : imgX;
                    imgY = (imgY < 0) ? 0 : imgY;
                    g.drawImage(img, imgX, imgY, null);
                    return;
                }

                Graphics2D g2d = (Graphics2D) g.create();

                Color color = Color.BLUE;
                g2d.setColor(color);
                g2d.fillRect(x, y, (int) (width * 0.3), height);
                g2d.setColor(getCtrlBorderColor(color));
                for (int i = 0; i < getCtrlBorderBold(); i++) {
                    g2d.drawRect(x + i, y + i, (int) (width * 0.3) - (i * 2), height - (i * 2));
                }

                int xPoints[] = { x + (int) (width * 0.3), x + width, x + width };
                int yPoints[] = { y + (height / 2), y, y + height };
                g2d.setColor(color);
                g2d.fillPolygon(xPoints, yPoints, 3);
                g2d.setColor(getCtrlBorderColor(color));
                for (int i = 0; i < getCtrlBorderBold(); i++) {
                    int[] xp, yp;
                    xp = Arrays.copyOf(xPoints, xPoints.length);
                    yp = Arrays.copyOf(yPoints, yPoints.length);
                    xp[0] += i;
                    xp[1] -= i;
                    xp[2] -= i;
                    yp[1] += i;
                    yp[2] -= i;
                    g2d.drawPolygon(xp, yp, 3);
                }
            }
        });
        prev2Button.setUI(prev2UI);

    }

    @Override
    public void update() {
        String posStr = "00:00";
        String lengthStr = "00:00";
        if (JMPCore.getSoundManager().getCurrentPlayer().isValid() == true) {
            slider.setMaximum((int) JMPCore.getSoundManager().getCurrentPlayer().getLength());
            if (s_tmpSliderTick == -1) {
                slider.setValue((int) JMPCore.getSoundManager().getCurrentPlayer().getPosition());
            }

            posStr = JMPCore.getSoundManager().getPositionTimeString();
            lengthStr = JMPCore.getSoundManager().getLengthTimeString();
        }
        else {
            s_tmpSliderTick = -1;
            slider.setMaximum(100);
            slider.setValue(0);

            posStr = JMPCore.getSoundManager().getPlayerTimeString(0);
            lengthStr = JMPCore.getSoundManager().getPlayerTimeString(0);
        }
        labelPositionTime.setText(String.format(CURRENT_TIME_FORMAT, posStr, lengthStr));

        // slider.setToolTipText(String.valueOf(slider.getValue()));

        if (pluginMenu.isPopupMenuVisible() == true) {
            updatePluginEnable();
        }

        // 再描画
        repaint();

        for (JmpQuickLaunch launch : JmpQuickLaunch.Accessor) {
            launch.repaint();
        }
    }

    /**
     * アプリケーション終了
     */
    public void exit() {
        exit(JMPCore.isEnableStandAlonePlugin() == false);
    }

    public void exit(boolean forcedExit) {
        setVisible(false);

        JMPCore.getSoundManager().stop();

        if (forcedExit == true) {
            // JMPリソースの終了処理
            JMPLoader.exit();
        }
    }

    /**
     * プラグイン追加
     *
     * @param plugin
     *            プラグイン
     */
    public void addPluginMenu(String name, IPlugin plugin) {
        JMPCore.getWindowManager().addPluginMenuItem(name, plugin);
        pluginMenu.removeAll();

        pluginMenu.add(addPluginMenuItem);
        pluginMenu.add(removePluginMenuItem);
        pluginMenu.add(allClosePluginMenuItem);
        pluginMenu.addSeparator();
        for (JMenuItem item : JMPCore.getWindowManager().getPluginMenuItems()) {
            pluginMenu.add(item);
        }
    }

    private void updatePluginEnable() {
        for (int i = 0; i < pluginMenu.getItemCount(); i++) {
            JMenuItem item = pluginMenu.getItem(i);
            if (item != null) {
                String name = item.getText();
                IPlugin plg = JMPCore.getPluginManager().getPlugin(name);
                if (plg != null) {
                    item.setEnabled(plg.isEnable());
                }
            }
        }
    }

    private void updateMenuState() {
        // 設定値とメニューの同期
        DataManager dm = JMPCore.getDataManager();
        chckbxmntmStartupmidisetup.setSelected(dm.isShowStartupDeviceSetup());
        loopPlayCheckBoxMenuItem.setSelected(dm.isLoopPlay());
        autoPlayCheckBox.setSelected(dm.isAutoPlay());
        alwayTopCheckBox.setSelected(isAlwaysOnTop());

        if (JMPCore.getSoundManager().getSequencer() != null) {
            // Sequencerが実行中の場合は設定不可
            mntmMidiDeviceSetup.setEnabled(!JMPCore.getSoundManager().getSequencer().isRunning());
        }
    }

    private FileNameExtensionFilter createFileFilter(String exName, String... ex) {
        String exs = "";
        for (int i = 0; i < ex.length; i++) {
            if (i > 0) {
                exs += ", ";
            }
            exs += String.format("*.%s", ex[i]);
        }

        String description = String.format("%s (%s)", exName, exs);
        return new FileNameExtensionFilter(description, ex);
    }

    /**
     * ファイルオープン処理
     */
    public void fileOpenFunc() {
        // ファイルフィルター
        JFileChooser filechooser = new JFileChooser();
        filechooser.addChoosableFileFilter(createFileFilter("MIDI Files", DataManager.ExtentionForMIDI));
        filechooser.addChoosableFileFilter(createFileFilter("WAV Files", DataManager.ExtentionForWAV));
        filechooser.addChoosableFileFilter(createFileFilter("MusicXML Files", DataManager.ExtentionForMusicXML));

        File dir = new File(JMPCore.getDataManager().getPlayListPath());
        filechooser.setCurrentDirectory(dir);
        int selected = filechooser.showOpenDialog(getParent());
        switch (selected) {
            case JFileChooser.APPROVE_OPTION:
                File file = filechooser.getSelectedFile();
                String path = file.getPath();
                if (file.isDirectory() == false) {
                    // ファイルロード
                    JMPCore.getFileManager().loadFile(path);
                }
                break;
            default:
                break;
        }
    }

    /**
     * ステータスメッセージ設定
     *
     * @param text
     *            メッセージ
     */
    public void setStatusText(String text) {
        setStatusText(text, true);
    }

    public void setStatusText(String text, boolean isPassColor) {
        if (isPassColor == true) {
            setStatusText(text, STATUS_COLOR_PASS);
        }
        else {
            setStatusText(text, STATUS_COLOR_FAIL);
        }
        CallbackPackage pkg = new CallbackPackage((long) 3000);
        pkg.addCallbackFunction(new ICallbackFunction() {

            @Override
            public void callback() {
                setStatusTextForFile();
                repaint();
            }

            @Override
            public boolean isDeleteConditions(int count) {
                return true;
            }
        });
        JMPCore.getTaskManager().getTaskOfTimer().addCallbackPackage(pkg);
    }

    /**
     * ステータスメッセージ設定
     *
     * @param text
     *            メッセージ
     * @param color
     *            テキストカラー
     */
    public void setStatusText(String text, Color color) {
        if (text.isEmpty() == true) {
            // ブランクの禁止
            text = " ";
        }

        statusLabel.setForeground(color);
        statusLabel.setText(text);
        statusLabel.repaint();
    }

    /**
     * ファイル用ステータスメッセージ設定
     *
     * @param text
     *            メッセージ
     * @param color
     *            テキストカラー
     */
    public void setStatusTextForFile() {
        DataManager dm = JMPCore.getDataManager();
        if (dm.getLoadedFile().equals("") == false) {
            String name = Utility.getFileNameAndExtension(dm.getLoadedFile());
            setStatusText(name, STATUS_COLOR_FILE);
        }
        else {
            setStatusText(" ", STATUS_COLOR_FILE);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        setVisible(false);
        exit();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }

    @Override
    public void showWindow() {
        setVisible(true);
    }

    @Override
    public void hideWindow() {
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            setVisible(false);
        }
    }

    @Override
    public boolean isWindowVisible() {
        return isVisible();
    }

    @Override
    public void updateLanguage() {
        LanguageManager lm = JMPCore.getLanguageManager();

        playButton.setToolTipText(lm.getLanguageStr(LangID.Playback) + "/" + lm.getLanguageStr(LangID.Stop));
        prevButton.setToolTipText(lm.getLanguageStr(LangID.Rewind));
        nextButton.setToolTipText(lm.getLanguageStr(LangID.Fast_forward));
        prev2Button.setToolTipText(lm.getLanguageStr(LangID.Previous));
        next2Button.setToolTipText(lm.getLanguageStr(LangID.Next));

        fileMenu.setText(lm.getLanguageStr(LangID.File));
        openItem.setText(lm.getLanguageStr(LangID.Open));
        mntmReload.setText(lm.getLanguageStr(LangID.Reload));
        mntmVersion.setText(lm.getLanguageStr(LangID.Version));
        endItem.setText(lm.getLanguageStr(LangID.Exit));

        windowMenu.setText(lm.getLanguageStr(LangID.Window));
        alwayTopCheckBox.setText(lm.getLanguageStr(LangID.Allways_on_top));

        playerMenu.setText(lm.getLanguageStr(LangID.Player));
        loopPlayCheckBoxMenuItem.setText(lm.getLanguageStr(LangID.Loop_playback));
        autoPlayCheckBox.setText(lm.getLanguageStr(LangID.Continuous_playback));
        playlistItem.setText(lm.getLanguageStr(LangID.Playlist));
        menuItemHistory.setText(lm.getLanguageStr(LangID.History));

        pluginMenu.setText(lm.getLanguageStr(LangID.Plugin));
        addPluginMenuItem.setText(lm.getLanguageStr(LangID.Add_plugin));
        removePluginMenuItem.setText(lm.getLanguageStr(LangID.Remove_plugin));
        allClosePluginMenuItem.setText(lm.getLanguageStr(LangID.Close_all_plugin));

        configMenu.setText(lm.getLanguageStr(LangID.Setting));
        lblMidi.setText("-- " + lm.getLanguageStr(LangID.MIDI_settings) + " --");
        mntmMidiDeviceSetup.setText(lm.getLanguageStr(LangID.MIDI_device_settings));
        chckbxmntmStartupmidisetup.setText(lm.getLanguageStr(LangID.Open_MIDI_device_settings_on_startup));
        mntmPcreset.setText(lm.getLanguageStr(LangID.PC_reset));
        mntmMidiMonitor.setText(lm.getLanguageStr(LangID.MIDI_message_monitor));
        mntmMidimessagesender.setText(lm.getLanguageStr(LangID.MIDI_message_sender));
        lblCommon.setText("-- " + lm.getLanguageStr(LangID.Common_settings) + " --");
        menuItemLanguage.setText(lm.getLanguageStr(LangID.Language));
        mntmInitLayout.setText(lm.getLanguageStr(LangID.Layout_initialization));

        mnTool.setText(lm.getLanguageStr(LangID.Tool));
        mntmFFmpegConverter.setText(lm.getLanguageStr(LangID.FFmpeg_converter));
        mntmInitializeConfig.setText(lm.getLanguageStr(LangID.Initialize_setting));
    }

    @Override
    public void catchDropFile(File file) {
        SystemManager system = JMPCore.getSystemManager();
        LanguageManager lm = JMPCore.getLanguageManager();

        String path = file.getPath();
        if (Utility.checkExtension(path, PluginManager.PLUGIN_ZIP_EX) == true) {
            // プラグインロード
            if (JMPCore.getPluginManager().readingPluginZipPackage(path, true) == true) {
                system.showInformationMessageDialog(lm.getLanguageStr(LangID.PLUGIN_LOAD_SUCCESS));
            }
            else {
                system.showErrorMessageDialog(lm.getLanguageStr(LangID.PLUGIN_LOAD_ERROR));
            }
        }
        else if (Utility.checkExtension(path, PluginManager.SETUP_FILE_EX) == true) {
            if (JMPCore.getPluginManager().readingSetupFile(path) == true) {
                system.showInformationMessageDialog(lm.getLanguageStr(LangID.PLUGIN_LOAD_SUCCESS));
            }
            else {
                system.showErrorMessageDialog(lm.getLanguageStr(LangID.PLUGIN_LOAD_ERROR));
            }
        }
        else {
            JMPCore.getFileManager().loadFile(path);
        }
    }
}
