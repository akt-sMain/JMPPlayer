package jmp.task;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.sound.midi.MidiMessage;

import function.Utility;
import jlib.IMidiEventListener;
import jmp.JMPCore;
import jmp.PluginManager;

public class TaskOfMidiEvent extends Thread implements ITask {

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

    // ! 一定周期時間（ms）
    public static long CyclicTime = 20;

    private boolean isRunnable = true;

    private List<JmpMidiPacket> stack = null;

    public TaskOfMidiEvent() {
        stack = Collections.synchronizedList(new LinkedList<JmpMidiPacket>());
    }

    @Override
    public void run() {
        PluginManager pm = JMPCore.getPluginManager();
        while (isRunnable) {
            synchronized (stack) {
                // スタックされたパケットをプラグインに送信
                Iterator<JmpMidiPacket> i = stack.iterator();
                while (i.hasNext()) {
                    JmpMidiPacket packet = i.next();
                    pm.send(packet);
                    i.remove();
                }
            }
            Utility.threadSleep(CyclicTime);
        }
    }

    public void add(MidiMessage message, long timeStamp, short senderType) {
        synchronized (stack) {
            // プラグインに送信するパケットを発行
            stack.add(new JmpMidiPacket(message, timeStamp, senderType));
        }
    }

    @Override
    public void exit() {
        isRunnable = false;
    }

}
