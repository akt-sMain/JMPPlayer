package jmp.task;

import jmp.task.TaskOfNotify.NotifyID;

public class NotifyPacket {

    private NotifyID id;
    private Object[] data = null;

    public NotifyPacket(NotifyID id, Object... d) {
        this.id = id;
        this.data = d;
    }

    public NotifyID getId() {
        return id;
    }

    public Object getData() {
        return getData(0);
    }

    public Object getData(int index) {
        if (this.data == null) {
            return null;
        }
        if (index >= this.data.length) {
            return null;
        }
        return this.data[index];
    }

}
