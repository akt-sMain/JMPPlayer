package jlib.midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;

public interface IMidiToolkit {

    /**
     * NoteOnイベントか判定
     *
     * @param sMes
     * @return
     */
    abstract boolean isNoteOn(MidiMessage mes);

    /**
     * NoteOffイベントか判定
     *
     * @param sMes
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
     * @param sMes
     * @return
     */
    abstract boolean isPitchBend(MidiMessage mes);

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
