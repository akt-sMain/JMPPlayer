package jlib;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jlib.mdx.MidiUtility;

public abstract class JMidiPlugin extends JMPlugin implements IMidiEventListener {

	public JMidiPlugin() {}

	/**
	 * MIDIイベントを受信するか判定する
	 *
	 * @return
	 */
	protected boolean isCatchMidiEvent() { return true; };

	/**
	 * ノートオンイベント受信
	 *
	 * @param channel チャンネル
	 * @param midiNumber MIDI番号
	 * @param velocity ベロシティ
	 * @param timeStamp タイムスタンプ
	 * @param senderType MIDIイベントの送信元種別(SENDER_MIDI_OUT, SENDER_MIDI_IN)
	 */
	protected abstract void noteOn(int channel, int midiNumber, int velocity, long timeStamp, short senderType);

	/**
	 * ノートオフイベント受信
	 *
	 * @param channel チャンネル
	 * @param midiNumber MIDI番号
	 * @param timeStamp タイムスタンプ
	 * @param senderType MIDIイベントの送信元種別(SENDER_MIDI_OUT, SENDER_MIDI_IN)
	 */
	protected abstract void noteOff(int channel, int midiNumber, long timeStamp, short senderType);

	/**
	 * プログラムチェンジ受信
	 *
	 * @param channel チャンネル
	 * @param programNumber プログラム番号
	 * @param timeStamp タイムスタンプ
	 * @param senderType MIDIイベントの送信元種別(SENDER_MIDI_OUT, SENDER_MIDI_IN)
	 */
	protected abstract void programChange(int channel, int programNumber, long timeStamp, short senderType);

	/**
	 * ピッチベンド受信
	 *
	 * @param channel チャンネル
	 * @param pbValue ピッチベンド値
	 * @param timeStamp タイムスタンプ
	 * @param senderType MIDIイベントの送信元種別(SENDER_MIDI_OUT, SENDER_MIDI_IN)
	 */
	protected abstract void pitchBend(int channel, int pbValue, long timeStamp, short senderType);

	@Override
	public void catchMidiEvent(MidiMessage message, long timeStamp, short senderType) {
		if (isCatchMidiEvent() == false) {
			return;
		}

		try {
		    if(message instanceof ShortMessage) {
		        ShortMessage sMes = (ShortMessage) message;
		        if (MidiUtility.isNoteOn(sMes) == true) {
		        	noteOn(sMes.getChannel(), sMes.getData1(), sMes.getData2(), timeStamp, senderType);
		        }
		        else if (MidiUtility.isNoteOff(sMes) == true) {
		        	noteOff(sMes.getChannel(), sMes.getData1(), timeStamp, senderType);
		        }
		        else if ((sMes.getCommand() == ShortMessage.PROGRAM_CHANGE)) {
		        	programChange(sMes.getChannel(), sMes.getData1(), timeStamp, senderType);
		        }
		        else if ((sMes.getCommand() == ShortMessage.PITCH_BEND)) {
		        	int pbValue = MidiUtility.convertPitchBendValue(sMes);
		        	pitchBend(sMes.getChannel(), pbValue, timeStamp, senderType);
		        }
		    }
		}
	    catch(Exception e) {
	    }
	}

}
