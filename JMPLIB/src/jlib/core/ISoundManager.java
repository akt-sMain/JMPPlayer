package jlib.core;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequencer;

import jlib.midi.IMidiToolkit;

public interface ISoundManager {
    /**
     * MIDIシーケンサーを取得
     *
     * @return MIDIシーケンサー
     */
    abstract Sequencer getSequencer();

    /**
     * 再生中か
     *
     * @return true=再生中, false=停止中
     */
    abstract boolean isPlay();

    /**
     * 再生
     */
    abstract void play();

    /**
     * 停止
     */
    abstract void stop();

    /**
     * 再生/停止のトグル
     */
    default void togglePlayStop() {
        if (isPlay() == true) {
            stop();
        }
        else {
            play();
        }
    };

    /**
     * 再生データのポジション設定
     *
     * @param pos
     *            ポジション
     */
    abstract void setPosition(long pos);

    /**
     * 再生データのポジション取得
     *
     * @return ポジション
     */
    abstract long getPosition();

    /**
     * 再生データのサイズ取得
     *
     * @return サイズ
     */
    abstract long getLength();

    /**
     * ポジションの秒数
     *
     * @return
     */
    abstract int getPositionSecond();

    /**
     * サイズの秒数
     *
     * @return
     */
    abstract int getLengthSecond();

    /**
     * 開始位置に設定
     */
    default void initPosition() {
        setPosition(0);
    }

    /**
     * 終了位置に設定
     */
    default void endPosition() {
        setPosition(getLength() - 1);
    }

    /**
     * 1タップの移動量
     *
     * @return
     */
    default long getAmount() {
        long amount = 0;
        long length = getLength();
        if (length > 0) {
            // ポジションの移動量
            amount = length / 100;
        }
        return amount;
    }

    /**
     * 巻き戻し
     */
    default void rewind() {
        long tick = getPosition();
        tick -= getAmount();
        if (tick < 0) {
            tick = 0;
        }
        setPosition(tick);
    }

    /**
     * 早送り
     */
    default void fastForward() {
        long tick = getPosition();
        tick += getAmount();
        if (tick > getLength()) {
            tick = getLength();
        }
        setPosition(tick);
    }

    /**
     * サポートする拡張子か判定する
     *
     * @param extension
     *            拡張子
     * @return
     */
    abstract boolean isSupportedExtension(String extension);

    /**
     * Midiツールキット取得
     *
     * @return
     */
    abstract IMidiToolkit getMidiToolkit();

    /**
     * Midiメッセージ送信
     *
     * @param msg
     * @param timeStamp
     * @return
     */
    abstract boolean sendMidiMessage(MidiMessage msg, long timeStamp);

    /**
     * ノートオンイベント送信
     *
     * @param channel
     *            チャンネル
     * @param midiNumber
     *            MIDI番号
     * @param velocity
     *            ベロシティ
     * @throws InvalidMidiDataException
     */
    default boolean sendNoteOn(int channel, int midiNumber, int velocity, long timeStamp) throws InvalidMidiDataException {
        MidiMessage sMes = getMidiToolkit().createNoteOnMessage(channel, midiNumber, velocity);
        return sendMidiMessage(sMes, timeStamp);
    }

    /**
     * ノートオフイベント送信
     *
     * @param channel
     *            チャンネル
     * @param midiNumber
     *            MIDI番号
     * @throws InvalidMidiDataException
     */
    default boolean sendNoteOff(int channel, int midiNumber, long timeStamp) throws InvalidMidiDataException {
        MidiMessage sMes = getMidiToolkit().createNoteOffMessage(channel, midiNumber, 0);
        return sendMidiMessage(sMes, timeStamp);
    }

    /**
     * プログラムチェンジ送信
     *
     * @param channel
     *            チャンネル
     * @param programNumber
     *            プログラム番号
     * @throws InvalidMidiDataException
     */
    default boolean sendProgramChange(int channel, int programNumber, long timeStamp) throws InvalidMidiDataException {
        MidiMessage sMes = getMidiToolkit().createProgramChangeMessage(channel, programNumber);
        return sendMidiMessage(sMes, timeStamp);
    }

}
