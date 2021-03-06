package jmp.task;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiMessage;

import jlib.midi.IMidiEventListener;
import jmp.JMPFlags;
import jmp.core.JMPCore;
import jmp.core.PluginManager;
import jmp.core.WindowManager;

public class TaskOfMidiEvent extends TaskOfBase {

    public class JmpMidiPacket {
        public MidiMessage message = null;
        public long timeStamp = 0;
        public short senderType = IMidiEventListener.SENDER_MIDI_OUT;

        public JmpMidiPacket(MidiMessage message, long timeStamp, short senderType) {
            this.message = message;
            this.timeStamp = timeStamp;
            this.senderType = senderType;
        }
    }

    private List<JmpMidiPacket> stack = null;

    public TaskOfMidiEvent() {
        super(20);
        stack = Collections.synchronizedList(new LinkedList<JmpMidiPacket>());
    }

    public void add(MidiMessage message, long timeStamp, short senderType) {

        JmpMidiPacket packet;
        if (JMPFlags.UseUnsynchronizedMidiPacket == true) {
            // 非同期にするため、MidiMessageをクローンする
            packet = new JmpMidiPacket((MidiMessage) message.clone(), timeStamp, senderType);
        }
        else {
            packet = new JmpMidiPacket(message, timeStamp, senderType);
        }

        synchronized (stack) {
            // プラグインに送信するパケットを発行
            stack.add(packet);
        }
    }

    @Override
    void begin() {
    }

    @Override
    void loop() {
        WindowManager wm = JMPCore.getWindowManager();
        PluginManager pm = JMPCore.getPluginManager();
        IMidiEventListener midiEventMonitor = (IMidiEventListener) wm.getWindow(WindowManager.WINDOW_NAME_MIDI_MONITOR);

        synchronized (stack) {
            // スタックされたパケットをプラグインに送信
            Iterator<JmpMidiPacket> i = stack.iterator();
            while (i.hasNext()) {
                JmpMidiPacket packet = i.next();

                pm.send(packet);
                if (midiEventMonitor != null) {
                    midiEventMonitor.catchMidiEvent(packet.message, packet.timeStamp, packet.senderType);
                }

                i.remove();
            }
        }
    }

    @Override
    void end() {
    }

    @Override
    protected void notifySleepTimeCalc(long sleepTime) {
        // System.out.println("" + sleepTime);
    }

}
