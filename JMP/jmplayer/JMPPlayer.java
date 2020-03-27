package jmplayer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import function.Platform;
import function.Platform.KindOfPlatform;
import function.Utility;
import jlib.gui.IJmpMainWindow;
import jlib.gui.IJmpWindow;
import jlib.plugin.IPlugin;
import jmp.JMPFlags;
import jmp.JMPLoader;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.LanguageManager;
import jmp.core.PluginManager;
import jmp.core.SoundManager;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.gui.JmpQuickLaunch;
import jmp.gui.MidiFileListDialog;
import jmp.gui.VersionInfoDialog;
import jmp.gui.ui.ControlButtonUI;
import jmp.gui.ui.IButtonMarkPaint;
import jmp.gui.ui.IJMPComponentUI;
import jmp.gui.ui.SequencerSliderUI;
import jmp.lang.DefineLanguage.LangID;
import jmp.player.Player;
import jmp.player.PlayerAccessor;
import jmp.task.CallbackPackage;
import jmp.task.ICallbackFunction;

/**
 * MIDIプレイヤーメインウィンドウクラス
 *
 * @author abs
 *
 */
public class JMPPlayer extends JFrame implements WindowListener, IJmpMainWindow, IJMPComponentUI {
    /**
     *
     * ドラッグ＆ドロップハンドラー
     *
     */
    public class DropFileHandler extends TransferHandler {
        /**
         * ドロップされたものを受け取るか判断 (アイテムのときだけ受け取る)
         */
        @Override
        public boolean canImport(TransferSupport support) {
            if (support.isDrop() == false) {
                // ドロップ操作でない場合は受け取らない
                return false;
            }

            if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) == false) {
                // ファイルでない場合は受け取らない
                return false;
            }

            return true;
        }

        /**
         * ドロップされたアイテムを受け取る
         */
        @Override
        public boolean importData(TransferSupport support) {
            // ドロップアイテム受理の確認
            if (canImport(support) == false) {
                return false;
            }

            // ドロップ処理
            Transferable t = support.getTransferable();
            try {
                // ドロップアイテム取得
                catchLoadItem(t.getTransferData(DataFlavor.javaFileListFlavor));
                return true;
            }
            catch (Exception e) {
                /* 受け取らない */
            }
            return false;
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

    private static String s_currentFileName = "";
    private static long s_tmpSliderTick = -1;

    private PlayerAccessor playerAccessor = PlayerAccessor.getInstance();

    private VersionInfoDialog versionInfoDialog = null;

    // コンポーネント
    private JMenu pluginMenu;
    private JLabel statusLabel;
    private JSlider slider;
    private JButton playButton;
    private MidiFileListDialog midiFileListDialog;
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
    private JCheckBoxMenuItem roopPlayCheckBoxMenuItem;
    private JMenuItem mntmMidiDeviceSetup;
    private JMenuItem zipGenerateMenuItem;
    private JMenuItem removeAllPluginMenuItem;
    private JLabel labelLengthTime;
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

    /**
     * コンストラクタ(WindowBuilderによる自動生成)
     *
     *
     * @throws HeadlessException
     */
    public JMPPlayer() throws HeadlessException {
        super();

        getContentPane().setBackground(getJmpBackColor());

        setResizable(false);
        setTitle(APP_TITLE);
        this.addWindowListener(this);
        this.setTransferHandler(new DropFileHandler());
        getContentPane().setLayout(null);
        setBounds(20, 20, 442, 199);

        Image jmpIcon = JMPCore.getResourceManager().getJmpImageIcon();
        if (jmpIcon != null) {
            setIconImage(jmpIcon);
        }

        // ウィンドウマネージャーに登録
        JMPCore.getWindowManager().register(WindowManager.WINDOW_NAME_MAIN, this);

        // Midiリストダイアログ
        midiFileListDialog = new MidiFileListDialog();

        prev2Button = new JButton("←←");
        prev2Button.setToolTipText("前の曲へ");
        prev2Button.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
        prev2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JMPCore.getSoundManager().isValidPlayList() == true) {
                    if (playerAccessor.getCurrent().getPosition() < 2000) {
                        JMPCore.getSoundManager().playPrev();
                        return;
                    }
                }

                if (playerAccessor.getCurrent().isValid() == false) {
                    return;
                }

                JMPCore.getSoundManager().initPlay();
            }
        });
        prev2Button.setBounds(19, 10, 64, 64);
        getContentPane().add(prev2Button);

        playButton = new JButton("再生");
        playButton.setToolTipText("再生/停止");
        playButton.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getSoundManager().togglePlayStop();
                repaint();
            }
        });
        playButton.setBounds(185, 10, 64, 64);
        getContentPane().add(playButton);

        next2Button = new JButton("→→");
        next2Button.setToolTipText("次の曲へ");
        next2Button.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
        next2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (JMPCore.getSoundManager().isValidPlayList() == true) {
                    JMPCore.getSoundManager().playNext();
                    return;
                }

                JMPCore.getSoundManager().endPosition();
            }
        });
        next2Button.setBounds(351, 10, 64, 64);
        getContentPane().add(next2Button);

        prevButton = new JButton("←");
        prevButton.setToolTipText("巻き戻し");
        prevButton.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
        prevButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JMPCore.getSoundManager().rewind();
            }
        });
        prevButton.setBounds(102, 10, 64, 64);
        getContentPane().add(prevButton);

        nextButton = new JButton("→");
        nextButton.setToolTipText("早送り");
        nextButton.setBackground(JMPCore.getResourceManager().getBtnBackgroundColor());
        nextButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                JMPCore.getSoundManager().fastForward();
            }
        });
        nextButton.setBounds(268, 10, 64, 64);
        getContentPane().add(nextButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(12, 118, 412, 13);
        getContentPane().add(statusLabel);

        slider = new JSlider();
        slider.setForeground(new Color(255, 255, 255));
        slider.setBackground(getJmpBackColor());
        slider.setValue(0);
        slider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                s_tmpSliderTick = slider.getValue();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                long value = s_tmpSliderTick;
                if (s_tmpSliderTick != -1) {
                    playerAccessor.getCurrent().setPosition(value);
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
        slider.setBounds(12, 95, 412, 13);
        getContentPane().add(slider);

        labelPositionTime = new JLabel("00:00");
        labelPositionTime.setForeground(Color.WHITE);
        labelPositionTime.setBounds(12, 80, 67, 16);
        getContentPane().add(labelPositionTime);

        labelLengthTime = new JLabel("00:00");
        labelLengthTime.setForeground(Color.WHITE);
        labelLengthTime.setHorizontalAlignment(SwingConstants.RIGHT);
        labelLengthTime.setBounds(351, 80, 73, 16);
        getContentPane().add(labelLengthTime);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        fileMenu = new JMenu("ファイル");
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
                String path = s_currentFileName;
                if (path.isEmpty() == false) {
                    File f = new File(path);
                    if (f.exists() == true && f.canRead() == true) {
                        loadFile(f.getPath());
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
        menuBar.add(windowMenu);

        alwayTopCheckBox = new JCheckBoxMenuItem("常に手前で表示");
        alwayTopCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setAlwaysOnTop(alwayTopCheckBox.isSelected());
            }
        });
        windowMenu.add(alwayTopCheckBox);

        playerMenu = new JMenu("プレイヤー");
        menuBar.add(playerMenu);

        roopPlayCheckBoxMenuItem = new JCheckBoxMenuItem("ループ再生");
        roopPlayCheckBoxMenuItem.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JMPCore.getDataManager().setLoopPlay(roopPlayCheckBoxMenuItem.isSelected());
            }
        });
        playerMenu.add(roopPlayCheckBoxMenuItem);

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
                midiFileListDialog.setVisible(true);
            }
        });

        pluginMenu = new JMenu("プラグイン");
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
                        String path = file.getPath();
                        if (file.isDirectory() == false) {
                            if (Utility.checkExtension(path, PluginManager.SETUP_FILE_EX) == true) {
                                // CoreAccessor.getPluginManager().readingSetupFile(path);
                                catchLoadItem(path);
                            }
                            else if (Utility.checkExtension(path, PluginManager.PLUGIN_ZIP_EX) == true) {
                                // CoreAccessor.getPluginManager().readingPluginZipPackage(path);
                                catchLoadItem(path);
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
        menuBar.add(configMenu);

        mntmMidiDeviceSetup = new JMenuItem("MIDIデバイス設定");
        mntmMidiDeviceSetup.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SoundManager.MidiPlayer.getSelectSynthsizerDialog().start();
            }
        });

        lblCommon = new JLabel("-- 共通設定 --");
        configMenu.add(lblCommon);

        menuItemLanguage = new JMenuItem("言語設定");
        menuItemLanguage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_LANGUAGE).showWindow();
            }
        });
        configMenu.add(menuItemLanguage);

        lblMidi = new JLabel("-- MIDI設定 --");
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

        mntmMidiMonitor = new JMenuItem("MIDIメッセージモニタ");
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
        configMenu.add(mntmMidiMonitor);

        mntmMidimessagesender = new JMenuItem("MIDIメッセージセンダー");
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
        configMenu.add(mntmMidimessagesender);

        lblDebugMenu = new JLabel("-- Developer menu --");
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

    /**
     * 設定初期化
     */
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

        if (Platform.getRunPlatform() != KindOfPlatform.WINDOWS) {
            mnExecuteBatFile.setVisible(false);
        }

        /* スタンドアロンモード用メニュー表示 */
        if (JMPCore.isEnableStandAlonePlugin() == true) {
            pluginMenu.setVisible(false);
        }

        setUI();
        alwayTopCheckBox.setSelected(false);
        autoPlayCheckBox.setSelected(dm.isAutoPlay());
        roopPlayCheckBoxMenuItem.setSelected(dm.isLoopPlay());
        chckbxmntmStartupmidisetup.setSelected(dm.isShowStartupDeviceSetup());
    }

    public static void main(String[] args) {
        int res = (JMPLoader.invoke(args) == true) ? 0 : 1;
        System.exit(res);
    }

    public static void registerCallbackPackage() {
        CallbackPackage commonCallbackPkg = new CallbackPackage((long) 500);
        commonCallbackPkg.addCallbackFunction(new ICallbackFunction() {
            @Override
            public void callback() {
                Player player = PlayerAccessor.getInstance().getCurrent();
                if (player != null) {
                    if (player.isRunnable() == true) {
                        // 再生バーのトグル
                        TimerColorToggleFlag = !(TimerColorToggleFlag);
                    }
                }
            }
        });
        commonCallbackPkg.addCallbackFunction(new ICallbackFunction() {
            @Override
            public void callback() {
                SoundManager sm = JMPCore.getSoundManager();
                DataManager dm = JMPCore.getDataManager();
                Player player = PlayerAccessor.getInstance().getCurrent();
                if (player == null) {
                    return;
                }

                long tickPos = player.getPosition();
                long tickLength = player.getLength();
                if (s_currentFileName.isEmpty() == true) {
                    return;
                }

                if (JMPFlags.NowLoadingFlag == false) {
                    if (tickPos >= tickLength) {
                        if (dm.isLoopPlay() == true) {
                            if (dm.isAutoPlay() == true) {
                                if (sm.isValidPlayList() == true) {
                                    if (sm.isValidPlayListNext() == true) {
                                        // 次の曲
                                        sm.playNext();
                                    }
                                    else {
                                        sm.playForList(0);
                                    }
                                }
                            }
                            else {
                                // ループ再生
                                sm.initPlay();
                            }
                        }
                        else if (dm.isAutoPlay() == true) {
                            if (sm.isValidPlayList() == true) {
                                if (sm.isValidPlayListNext() == true) {
                                    // 次の曲
                                    sm.playNext();
                                }
                            }
                        }
                    }
                }
            }
        });
        JMPCore.getTaskManager().getTaskOfTimer().addCallbackPackage(commonCallbackPkg);
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
        slider.setUI(new SequencerSliderUI());

        // 再生ボタンUI
        ControlButtonUI playUI = new ControlButtonUI();
        playUI.addMarkPainter(new IButtonMarkPaint() {
            @Override
            public void paintMark(Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g.create();
                if (playerAccessor.isValid() == true && playerAccessor.getCurrent().isRunnable() == true) {
                    // 停止ボタン
                    Image img = JMPCore.getResourceManager().getBtnStopIcon();
                    if (img != null) {
                        g.drawImage(img, 0, 0, null);
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
                        g.drawImage(img, 0, 0, null);
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
                    g.drawImage(img, 0, 0, null);
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
                    g.drawImage(img, 0, 0, null);
                    return;
                }

                x -= 2;
                Graphics2D g2d = (Graphics2D) g.create();
                Color color = Color.BLUE;
                for (int i = 0; i < 2; i++) {
                    int offsetW = width / 2;
                    int xPoints[] = { x + (offsetW * i), x + (offsetW * i) + width - offsetW,
                            x + (offsetW * i) + width - offsetW };
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
                    g.drawImage(img, 0, 0, null);
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
                    g.drawImage(img, 0, 0, null);
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
        if (playerAccessor.isValid() == true) {
            slider.setMaximum((int) playerAccessor.getCurrent().getLength());
            if (s_tmpSliderTick == -1) {
                slider.setValue((int) playerAccessor.getCurrent().getPosition());
            }

            labelPositionTime.setText(JMPCore.getSoundManager().getPositionTimeString());
            labelLengthTime.setText(JMPCore.getSoundManager().getLengthTimeString());
        }
        else {
            s_tmpSliderTick = -1;
            slider.setMaximum(100);
            slider.setValue(0);

            labelPositionTime.setText(JMPCore.getSoundManager().getPlayerTimeString(0));
            labelLengthTime.setText(JMPCore.getSoundManager().getPlayerTimeString(0));
        }

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

        if (forcedExit == true) {
            // プレイリストダイアログの破棄
            if (midiFileListDialog != null) {
                midiFileListDialog.setVisible(false);
                midiFileListDialog.dispose();
                midiFileListDialog = null;
            }

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

    /**
     * ロードアイテム受信
     *
     * @param item
     *            アイテム
     */
    public void catchLoadItem(Object item) {
        @SuppressWarnings("unchecked")
        List<File> files = (List<File>) item;

        // 一番先頭のファイルを取得
        if ((files != null) && (files.size() > 0)) {
            SystemManager system = JMPCore.getSystemManager();
            LanguageManager lm = JMPCore.getLanguageManager();

            String path = files.get(0).getPath();
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
                loadFile(path);
            }
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

        File dir = new File(Platform.getCurrentPath());
        filechooser.setCurrentDirectory(dir);
        int selected = filechooser.showOpenDialog(getParent());
        switch (selected) {
            case JFileChooser.APPROVE_OPTION:
                File file = filechooser.getSelectedFile();
                String path = file.getPath();
                if (file.isDirectory() == false) {
                    // ファイルロード
                    loadFile(path);
                }
                break;
            default:
                break;
        }
    }

    /**
     * ファイルロード処理
     *
     * @param path
     *            パス
     */
    public void loadFile(String path) {
        LanguageManager lm = JMPCore.getLanguageManager();

        if (JMPFlags.NowLoadingFlag == true) {
            setStatusText(lm.getLanguageStr(LangID.FILE_ERROR_1), false);
            return;
        }

        JMPFlags.NowLoadingFlag = true;
        setStatusText(lm.getLanguageStr(LangID.Now_loading), Color.LIGHT_GRAY);
        repaint();

        /* ファイルロード */
        // Sequenceタスクに委託
        JMPCore.getTaskManager().getTaskOfSequence().queuing(new ICallbackFunction() {
            @Override
            public void callback() {
                boolean status = true;
                String tmpFileName = s_currentFileName;
                String statusStr = "";

                File f = new File(path);
                String ex = Utility.getExtension(f);

                /* ロード前の検査 */
                if (status == true) {
                    if (playerAccessor.isSupportedExtension(ex) == false) {
                        status = false;
                        statusStr = lm.getLanguageStr(LangID.FILE_ERROR_2);
                    }
                    else if (playerAccessor.getCurrent().isRunnable() == true) {
                        status = false;
                        statusStr = lm.getLanguageStr(LangID.FILE_ERROR_3);
                    }
                }

                /* ロード処理 */
                if (status == true) {
                    String subErrorStr = "";
                    try {
                        if (f.canRead() == true && f.exists() == true) {
                            // ファイルロード実行
                            JMPCore.getSoundManager().loadFile(f);

                            // プラグインのファイルロード処理
                            try {
                                JMPCore.getPluginManager().loadFile(f);
                            }
                            catch (Exception e) {
                                subErrorStr = "(" + lm.getLanguageStr(LangID.Plugin_error) + ")";
                            }
                        }
                        else {
                            status = false;
                            statusStr = lm.getLanguageStr(LangID.FILE_ERROR_4) + subErrorStr;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();

                        status = false;
                        statusStr = lm.getLanguageStr(LangID.FILE_ERROR_5);
                    }
                }

                // 結果表示
                if (status == true) {

                    // 履歴に追加
                    if (JMPFlags.NoneHIstoryLoadFlag == false) {
                        JMPCore.getDataManager().addHistory(f.getPath());
                    }

                    // 自動再生
                    if (JMPFlags.LoadToPlayFlag == true) {
                        playerAccessor.getCurrent().play();
                        JMPFlags.LoadToPlayFlag = false;
                    }

                    // 新しいファイル名
                    s_currentFileName = path;

                    // メッセージ発行
                    statusStr = String.format("%s ...(" + lm.getLanguageStr(LangID.FILE_LOAD_SUCCESS) + ")", Utility.getFileNameAndExtension(s_currentFileName));
                }
                else {
                    // 前のファイル名に戻す
                    s_currentFileName = tmpFileName;
                }

                // メッセージ表示
                setStatusTextForFile();
                setStatusText(statusStr, status);

                // フラグ初期化
                JMPFlags.NoneHIstoryLoadFlag = false;
                JMPFlags.LoadToPlayFlag = false;

                // ロード中フラグ初期化
                JMPFlags.NowLoadingFlag = false;
            }
        });
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
        if (s_currentFileName.equals("") == false) {
            String name = Utility.getFileNameAndExtension(s_currentFileName);
            setStatusText(name, STATUS_COLOR_FILE);
        }
        else {
            setStatusText("", STATUS_COLOR_FILE);
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
        roopPlayCheckBoxMenuItem.setText(lm.getLanguageStr(LangID.Loop_playback));
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
    }
}
