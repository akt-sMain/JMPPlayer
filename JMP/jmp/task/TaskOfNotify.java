package jmp.task;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import jmp.core.JMPCore;

public class TaskOfNotify extends TaskOfBase {
    
    /** Notify ID */
    public static enum NotifyID {
        UPDATE_CONFIG, UPDATE_SYSCOMMON, 
    }
    
    private List<NotifyPacket> stack = null;

    public TaskOfNotify() {
        super(100);
        stack = Collections.synchronizedList(new LinkedList<NotifyPacket>());
    }
    
    public void add(NotifyPacket p) {
        synchronized (stack) {
            // Notifyパケットを発行
            stack.add(p);
        }
    }

    @Override
    void begin() {
    }

    @Override
    void loop() {
        synchronized (stack) {
            // スタックされたパケットを送信
            Iterator<NotifyPacket> i = stack.iterator();
            while (i.hasNext()) {
                NotifyPacket packet = i.next();
                JMPCore.parseNotifyPacket(packet);
                i.remove();
            }
        }
    }

    @Override
    void end() {
    }
}
