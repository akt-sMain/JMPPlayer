package jmp.midi;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import jlib.IMidiEventListener;
import jlib.IMidiFilter;
import jmp.core.JMPCore;
import jmp.player.Player;
import jmp.player.PlayerAccessor;

/**
 * プラグインのみにMIDIメッセージを転送する
 *
 * @author akkut
 *
 */
public class NullReceiver implements Receiver {

    public NullReceiver() {
    }

    @Override
    public void send(MidiMessage message, long timeStamp) {
        int transpose = JMPCore.getDataManager().getTranspose();
        if (transpose != 0) {
            Player p = PlayerAccessor.getInstance().getCurrent();
            if (p instanceof IMidiFilter) {
                if (message instanceof ShortMessage) {
                    IMidiFilter filter = (IMidiFilter) p;
                    ShortMessage sMes = (ShortMessage) message;
                    filter.transpose(sMes, transpose);
                }
            }
        }

        JMPCore.getPluginManager().catchMidiEvent(message, timeStamp, IMidiEventListener.SENDER_MIDI_OUT);
    }

    @Override
    public void close() {
    }

}
