package jlib.midi;

import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;

public interface IMidiToolkit {

    /**
     * MIDIデバイスの一覧を取得
     *
     * @return
     */
    abstract List<MidiDevice> getMidiDevices();

    /**
     * MIDIデバイス情報一覧取得
     *
     * @param incTransmitter
     * @param incReciever
     * @return
     */
    abstract MidiDevice.Info[] getMidiDeviceInfo(boolean incTransmitter, boolean incReciever);

    /**
     * レシーバー名を探索する
     *
     * @param recvName
     *            指定レシーバー
     * @return レシーバー
     */
    abstract Receiver findReciver(String recvName);

    /**
     * NoteOnイベントか判定
     *
     * @param mes
     * @return
     */
    abstract boolean isNoteOn(MidiMessage mes);

    /**
     * NoteOffイベントか判定
     *
     * @param mes
     * @return
     */
    abstract boolean isNoteOff(MidiMessage mes);

    /**
     * ProgramChangeイベントか判定
     *
     * @param mes
     * @return
     */
    abstract boolean isProgramChange(MidiMessage mes);

    /**
     * PitchBendイベントか判定
     *
     * @param mes
     * @return
     */
    abstract boolean isPitchBend(MidiMessage mes);

    /**
     * ByteメッセージからMIDIメッセージオブジェクト生成
     *
     * @param data
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createByteMidiMessage(byte[] data);

    /**
     * プログラムチェンジMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @param programNumber
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createProgramChangeMessage(int channel, int programNumber) throws InvalidMidiDataException;

    /**
     * プログラムチェンジMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @param programNumber
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiEvent createProgramChangeEvent(long position, int channel, int programNumber) throws InvalidMidiDataException;

    /**
     * ノートオンMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @param midiNumber
     * @param velocity
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createNoteOnMessage(int channel, int midiNumber, int velocity) throws InvalidMidiDataException;

    /**
     * ノートオンMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @param midiNumber
     * @param velocity
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiEvent createNoteOnEvent(long position, int channel, int midiNumber, int velocity) throws InvalidMidiDataException;

    /**
     * ノートオフMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @param midiNumber
     * @param velocity
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createNoteOffMessage(int channel, int midiNumber, int velocity) throws InvalidMidiDataException;

    /**
     * ノートオフMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @param midiNumber
     * @param velocity
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiEvent createNoteOffEvent(long position, int channel, int midiNumber, int velocity) throws InvalidMidiDataException;

    /**
     * ピッチベンドMIDIメッセージオブジェクト生成
     *
     * @param channel
     * @param bend
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createPitchBendMessage(int channel, int bend) throws InvalidMidiDataException;

    /**
     * ピッチベンドMIDIイベントオブジェクト生成
     *
     * @param position
     * @param channel
     * @param bend
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiEvent createPitchBendEvent(long position, int channel, int bend) throws InvalidMidiDataException;

    /**
     * テンポ変更MIDIメッセージオブジェクト生成
     *
     * @param bpm
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiMessage createTempoMessage(float bpm) throws InvalidMidiDataException;

    /**
     * テンポ変更MIDIイベントオブジェクト生成
     *
     * @param position
     * @param bpm
     * @return
     * @throws InvalidMidiDataException
     */
    abstract MidiEvent createTempoEvent(long position, float bpm) throws InvalidMidiDataException;
}
