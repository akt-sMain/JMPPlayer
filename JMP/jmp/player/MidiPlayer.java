package jmp.player;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import jlib.midi.IMidiEventListener;
import jlib.midi.IMidiFilter;
import jlib.midi.MidiByte;
import jmp.JMPFlags;
import jmp.core.DataManager;
import jmp.core.JMPCore;
import jmp.core.SystemManager;
import jmp.core.WindowManager;
import jmp.gui.SelectSynthsizerDialog;
import jmp.gui.SelectSynthsizerDialogListener;
import jmp.midi.JMPSequencer;
import jmp.midi.MIReceiver;
import jmp.midi.MITransmitter;
import jmp.midi.MOReceiver;

public class MidiPlayer extends Player {
    public class MidiInfo extends Player.Info {
        public static final String PLAYER_MIDI_INFO_KEY_BPM = "BPM";

        public MidiInfo() {
            super();
            put(PLAYER_MIDI_INFO_KEY_BPM, "");
        }

        @Override
        public void update() {
            super.update();
            put(PLAYER_MIDI_INFO_KEY_BPM, (sequencer != null) ? String.valueOf(sequencer.getTempoInBPM()) : "0.0");
        }
    }

    private List<IMidiFilter> filters = null;

    // MIDI Synth
    private JMPSequencer sequencer;
    private SelectSynthsizerDialog selectSynthsizerDialog = null;

    public SelectSynthsizerDialog getSelectSynthsizerDialog() {
        return selectSynthsizerDialog;
    };

    private Receiver currentReceiver = null;

    public Receiver getCurrentReciver() {
        return currentReceiver;
    };

    private Transmitter currentTransmitter = null;

    public Transmitter getCurrentTransmitter() {
        return currentTransmitter;
    };

    public MidiPlayer() {
        filters = new LinkedList<IMidiFilter>();
    }

    public void addFilter(IMidiFilter f) {
        filters.add(f);
    }

    public void removeFilter(IMidiFilter f) {
        filters.remove(f);
    }

    public boolean filter(MidiMessage message, short senderType) {
        for (IMidiFilter f : filters) {
            if (f.filter(message, senderType) == false) {
                return false;
            }
        }
        return true;
    }

    public static MidiDevice.Info[] getMidiDeviceInfo() {
        return getMidiDeviceInfo(true, true);
    }

    public static MidiDevice.Info[] getMidiDeviceInfo(boolean incTransmitter, boolean incReciever) {
        ArrayList<MidiDevice.Info> ret = new ArrayList<MidiDevice.Info>();

        MidiDevice.Info[] tmp = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < tmp.length; i++) {
            MidiDevice dev;
            try {
                dev = MidiSystem.getMidiDevice(tmp[i]);
            }
            catch (MidiUnavailableException e) {
                continue;
            }

            if (incTransmitter == false && dev.getMaxTransmitters() != 0) {
                // Transmitterは除外
                continue;
            }
            else if (incReciever == false && dev.getMaxReceivers() != 0) {
                // Recieverは除外
                continue;
            }
            ret.add(tmp[i]);
        }
        return (MidiDevice.Info[]) ret.toArray(new MidiDevice.Info[0]);
    }

    /**
     * MIDIデバイスの一覧を取得
     *
     * @return
     */
    public ArrayList<MidiDevice> getMidiDevices() {
        ArrayList<MidiDevice> devices = new ArrayList<MidiDevice>();

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        for (int i = 0; i < infos.length; i++) {
            MidiDevice.Info info = infos[i];
            MidiDevice device = null;

            try {
                device = MidiSystem.getMidiDevice(info);
                devices.add(device);
            }
            catch (MidiUnavailableException me) {
            }
            catch (Exception e) {
            }
        }

        return devices;
    }

    /**
     * MIDIデバイス一覧を取得
     *
     * @return
     */
    public ArrayList<MidiDevice> getDevices() {
        ArrayList<MidiDevice> devices = new ArrayList<MidiDevice>();

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < infos.length; i++) {
            MidiDevice.Info info = infos[i];
            MidiDevice device = null;
            try {
                device = MidiSystem.getMidiDevice(info);
                if (device.getMaxReceivers() != 0) {
                    devices.add(device);
                }
            }
            catch (SecurityException e) {
            }
            catch (MidiUnavailableException e) {
            }
        }
        return devices;
    }

    /**
     * レシーバー名を探索する
     *
     * @param recvName
     *            指定レシーバー
     * @return レシーバー
     */
    public static Receiver findReciver(String recvName) {
        Receiver receiver = null;

        try {
            MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
            for (MidiDevice.Info info : devices) {
                if (info.getName().startsWith(recvName) == false) {
                    // ネームチェック
                    continue;
                }

                MidiDevice dv = MidiSystem.getMidiDevice(info);
                if (dv.getMaxReceivers() > 0) {
                    dv.open();
                    receiver = dv.getReceiver();
                    break;
                }
            }
        }
        catch (MidiUnavailableException e) {
            receiver = null;
        }

        return receiver;
    }

    public JMPSequencer getSequencer() {
        return sequencer;
    }

    private String getEncText(byte[] data) {
        String charcode = JMPCore.getSystemManager().getCommonRegisterValue(SystemManager.COMMON_REGKEY_NO_LYRIC_CHARCODE);
        String str = "";
        try {
            str = new String(data, charcode);
        }
        catch (UnsupportedEncodingException e) {
        }
        return str;
    }

    @Override
    public boolean open() {
        boolean result = true;
        // Midiシーケンサー取得
        try {
            sequencer = new JMPSequencer(MidiSystem.getSequencer(false));
            if (sequencer.isOpen() == true) {
                sequencer.close();
            }

            sequencer.addMetaEventListener(new MetaEventListener() {

                @Override
                public void meta(MetaMessage meta) {
                    if (MidiByte.COPYRIGHT_NOTICE.type == meta.getType()) {
                        // System.out.println(getEncText(meta.getData()));
                    }
                    else if (MidiByte.TEXT.type == meta.getType()) {
                        // System.out.println(getEncText(meta.getData()));
                    }
                    else if (MidiByte.LYRICS.type == meta.getType()) {
                        JMPCore.getWindowManager().getMainWindow().setLyric(getEncText(meta.getData()));
                    }
                    else if (MidiByte.END_OF_TRACK.type == meta.getType()) {
                        JMPCore.getWindowManager().getMainWindow().setLyric("");
                    }

                    IMidiEventListener l = (IMidiEventListener) JMPCore.getWindowManager().getWindow(WindowManager.WINDOW_NAME_MIDI_MONITOR);
                    if (l != null) {
                        l.catchMidiEvent(meta, 0, IMidiEventListener.SENDER_MIDI_OUT);
                    }
                }
            });

            sequencer.open();
        }
        catch (MidiUnavailableException e) {
            result = false;
            return result;
        }

        // シンセサイザー選択ダイアログ表示
        selectSynthsizerDialog = new SelectSynthsizerDialog(true, true);
        selectSynthsizerDialog.setCommitListener(new SelectSynthsizerDialogListener() {

            @Override
            public void commitMidiOut(Receiver rec) throws MidiUnavailableException {
                updateMidiOut(rec);
            }

            @Override
            public void commitMidiIn(Transmitter trans) throws MidiUnavailableException {
                updateMidiIn(trans);
            }
        });

        if (JMPFlags.StartupAutoConectSynth == false) {
            selectSynthsizerDialog.start();
            if (selectSynthsizerDialog.isOkActionClose() == false) {
                return false;
            }

            // SelectSoundFontDIalog dialog = new SelectSoundFontDIalog();
            // dialog.start();
        }
        else {
            String outName = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIOUT);
            updateMidiOut(outName);

            String inName = JMPCore.getDataManager().getConfigParam(DataManager.CFG_KEY_MIDIIN);
            updateMidiIn(inName);
        }

        return result;
    }

    private static MOReceiver s_MOReceiver = null;

    public void updateMidiOut(String name) {
        Receiver inReciever = null;
        MidiDevice.Info recInfo = null;
        MidiDevice inDev = null;
        for (MidiDevice.Info info : getMidiDeviceInfo(false, true)) {
            if (info.getName().equals(name) == true) {
                recInfo = info;
                break;
            }
        }
        try {
            if (recInfo != null) {
                inDev = MidiSystem.getMidiDevice(recInfo);
                if (inDev.isOpen() == false) {
                    inDev.open();
                }
                inReciever = inDev.getReceiver();
            }
            else {
                inReciever = MidiSystem.getReceiver();
            }
            updateMidiOut(inReciever);
        }
        catch (MidiUnavailableException e) {
            if (inDev != null && inDev.isOpen() == true) {
                inDev.close();
            }
            JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIOUT, "");
        }
    }

    public void updateMidiOut(Receiver rec) throws MidiUnavailableException {
        if (s_MOReceiver != null) {
            s_MOReceiver.close();
        }
        if (s_MOReceiver == null) {
            s_MOReceiver = new MOReceiver(rec);
            getSequencer().getTransmitter().setReceiver(s_MOReceiver);
        }
        else {
            s_MOReceiver.changeAbsReceiver(rec);
        }
        currentReceiver = s_MOReceiver;

        if (s_MIReceiver != null) {
            s_MIReceiver.changeAbsReceiver(currentReceiver);
        }
    }

    private static MITransmitter s_MITransmitter = null;
    private static MIReceiver s_MIReceiver = null;

    public void updateMidiIn(String name) {
        MidiDevice outDev = null;
        Transmitter outTransmitter = null;
        MidiDevice.Info transInfo = null;
        for (MidiDevice.Info info : getMidiDeviceInfo(true, false)) {
            if (info.getName().equals(name) == true) {
                transInfo = info;
                break;
            }
        }
        try {
            if (transInfo != null) {
                outDev = MidiSystem.getMidiDevice(transInfo);
                if (outDev.isOpen() == false) {
                    outDev.open();
                }
                outTransmitter = outDev.getTransmitter();
            }
            else {
                outTransmitter = MidiSystem.getTransmitter();
            }
            updateMidiIn(outTransmitter);
        }
        catch (MidiUnavailableException e) {
            if (outDev != null && outDev.isOpen() == true) {
                outDev.close();
            }
            JMPCore.getDataManager().setConfigParam(DataManager.CFG_KEY_MIDIIN, "");
        }
    }

    public void updateMidiIn(Transmitter trans) throws MidiUnavailableException {
        if (s_MITransmitter != null) {
            s_MITransmitter.close();
        }

        if (s_MITransmitter == null) {
            s_MITransmitter = new MITransmitter(trans);
        }
        else {
            s_MITransmitter.changeAbsTransmitter(trans);
        }
        currentTransmitter = s_MITransmitter;

        if (s_MIReceiver == null) {
            s_MIReceiver = new MIReceiver(currentReceiver);
        }
        else {
            s_MIReceiver.changeAbsReceiver(currentReceiver);
        }
        currentTransmitter.setReceiver(s_MIReceiver);
    }

    @Override
    public boolean close() {
        if (sequencer != null) {
            if (sequencer.isRunning() == true || sequencer.isRecording() == true) {
                // 念のためシーケンサーを停止
                sequencer.stop();
            }

            // トランスミッター・レシーバーの接続を解除
            for (Receiver rec : sequencer.getReceivers()) {
                rec.close();
            }
            for (Transmitter trans : sequencer.getTransmitters()) {
                trans.close();
            }
            if (sequencer.isOpen() == true) {
                sequencer.close();
            }
            // sequencer = null;
        }
        return true;
    }

    @Override
    public void play() {
        if (isValid() == false) {
            return;
        }
        if (sequencer.getTickPosition() >= sequencer.getTickLength()) {
            // 最初から再生
            sequencer.setTickPosition(0);
        }
        sequencer.start();
    }

    @Override
    public void stop() {
        if (isValid() == false) {
            return;
        }
        sequencer.stop();
    }

    @Override
    public boolean isRunnable() {
        if (isValid() == false) {
            return false;
        }
        return sequencer.isRunning();
    }

    @Override
    public void setPosition(long pos) {
        if (isValid() == false) {
            return;
        }
        sequencer.setTickPosition(pos);
    }

    @Override
    public long getPosition() {
        if (isValid() == false) {
            return 0;
        }
        return sequencer.getTickPosition();
    }

    @Override
    public long getLength() {
        if (isValid() == false) {
            return 0;
        }
        return sequencer.getTickLength();
    }

    @Override
    public boolean isValid() {
        if (sequencer == null) {
            return false;
        }

        if (sequencer.getSequence() == null) {
            return false;
        }
        return true;
    }

    @Override
    public int getPositionSecond() {
        if (isValid() == false) {
            return 0;
        }
        int time = (int) (sequencer.getMicrosecondPosition() / 1000 / 1000);
        return time;
    }

    @Override
    public int getLengthSecond() {
        if (isValid() == false) {
            return 0;
        }
        int time = (int) (sequencer.getMicrosecondLength() / 1000 / 1000);
        return time;
    }

    @Override
    public void setVolume(float volume) {
        if (isValid() == false) {
            return;
        }
    }

    @Override
    public float getVolume() {
        if (isValid() == false) {
            return 0;
        }
        return 0;
    }

    @Override
    public boolean loadFile(File file) throws InvalidMidiDataException, IOException {
        if (sequencer == null) {
            return false;
        }
        loadMidiFile(file);

        Player.Info info = createInfo();
        info.put(Player.Info.PLAYER_ABS_INFO_KEY_FILENAME, file.getName());
        info.update();
        setInfo(info);

        return true;
    }

    public Player.Info createInfo() {
        Player.Info info = new MidiInfo();
        if (sequencer == null) {
            info.update();
            return info;
        }

        Sequence sequence = sequencer.getSequence();
        if (sequence == null) {
            info.update();
            return info;
        }

        String sDivisionType = "UNKNOWN";
        float divisionType = sequence.getDivisionType();
        if (divisionType == Sequence.PPQ) {
            sDivisionType = "PPQ";
        }
        else if (divisionType == Sequence.SMPTE_24) {
            sDivisionType = "SMPTE_24";
        }
        else if (divisionType == Sequence.SMPTE_25) {
            sDivisionType = "SMPTE_25";
        }
        else if (divisionType == Sequence.SMPTE_30) {
            sDivisionType = "SMPTE_30";
        }
        else if (divisionType == Sequence.SMPTE_30DROP) {
            sDivisionType = "SMPTE_30DROP";
        }
        info.put("Division Type", sDivisionType);
        info.put("Resolution", String.valueOf(sequence.getResolution()));
        info.put("Tick Length", String.valueOf(sequence.getTickLength()));
        info.put("Tracks", String.valueOf(sequence.getTracks().length));

        info.update();
        return info;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        if (sequencer == null) {
            return false;
        }
        outputMidiFile(file.getPath());
        return true;
    }

    /**
     * MIDIファイルを解析
     *
     * @param file
     *            Midiファイル
     * @return Sequence
     * @throws IOException
     * @throws InvalidMidiDataException
     */
    public Sequence readMidiFile(File file) throws InvalidMidiDataException, IOException {
        Sequence seq = null;
        seq = MidiSystem.getSequence(file);

        // JMPMidiReader mr = new JMPMidiReader(file);
        // try {
        // mr.read();
        // }
        // catch (Exception e) {
        // System.out.println(function.Error.getMsg(e));
        //
        // }

        return seq;
    }

    /**
     * MIDIファイルを解析
     *
     * @param path
     *            ファイルパス
     * @return Sequence
     * @throws IOException
     * @throws InvalidMidiDataException
     */
    public Sequence readMidiFile(String path) throws InvalidMidiDataException, IOException {
        File f = new File(path);
        if (f.exists() == false) {
            // ファイルが存在しない
            return null;
        }
        return readMidiFile(f);
    }

    /**
     * MIDIファイルをシーケンサーにロードする
     *
     * @param file
     *            MIDIファイル
     * @throws IOException
     * @throws InvalidMidiDataException
     * @throws Exception
     */
    public void loadMidiFile(File file) throws InvalidMidiDataException, IOException {
        Sequence seq = readMidiFile(file);
        loadMidiSequence(seq);
    }

    /**
     * MIDIファイルをシーケンサーにロードする
     *
     * @param path
     *            MIDIファイルパス
     * @throws IOException
     * @throws InvalidMidiDataException
     * @throws Exception
     */
    public void loadMidiFile(String path) throws InvalidMidiDataException, IOException {
        File f = new File(path);
        if (f.exists() == true) {
            loadMidiFile(f);
        }
        return;
    }

    public void loadMidiSequence(Sequence seq) throws InvalidMidiDataException {
        getSequencer().setSequence(seq);
    }

    /**
     * MIDIファイル出力
     *
     * @param path
     *            MIDIファイルパス
     * @return
     * @throws IOException
     * @throws InvalidMidiDataException
     */
    public void outputMidiFile(String path) throws InvalidMidiDataException, IOException {
        outputMidiFile(1, path);
    }

    /**
     * MIDIファイル出力
     *
     * @param format
     *            SMFフォーマット
     * @param path
     *            出力ファイルパス
     * @return
     * @throws InvalidMidiDataException
     * @throws IOException
     */
    public void outputMidiFile(int format, String path) throws InvalidMidiDataException, IOException {
        Sequence curSeq = getSequencer().getSequence();
        Sequence seq = new Sequence(curSeq.getDivisionType(), curSeq.getResolution(), curSeq.getTracks().length);
        for (int i = 0; i < curSeq.getTracks().length; i++) {
            Track srcTrack = curSeq.getTracks()[i];
            Track dstTrack = seq.getTracks()[i];
            for (int j = 0; j < srcTrack.size(); j++) {
                dstTrack.add(srcTrack.get(j));
            }
        }

        outputMidiFile(seq, format, path);
    }

    /**
     * MIDIファイル出力
     *
     * @param sequence
     *            シーケンス
     * @param path
     *            ファイルパス
     * @return
     * @throws IOException
     */
    public void outputMidiFile(Sequence sequence, String path) throws IOException {
        outputMidiFile(sequence, 1, path);
    }

    /**
     * MIDIファイル出力
     *
     * @param sequence
     *            シーケンス
     * @param format
     *            SMFフォーマット
     * @param path
     *            ファイルパス
     * @return
     * @throws IOException
     */
    public void outputMidiFile(Sequence sequence, int format, String path) throws IOException {
        File file = new File(path);
        MidiSystem.write(sequence, format, file);
        return;
    }
}
