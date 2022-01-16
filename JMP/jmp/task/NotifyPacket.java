package jmp.task;

import java.util.ArrayList;
import java.util.List;

import jmp.task.TaskOfNotify.NotifyID;

public class NotifyPacket {

    private NotifyID id;
    private List<Object> datas;

    public NotifyPacket(NotifyID id, Object... d) {
        this.id = id;

        this.datas = new ArrayList<Object>();
        for (Object o : d) {
            this.datas.add(o);
        }
    }

    public NotifyID getId() {
        return id;
    }

    public Object getData() {
        return getData(0);
    }

    public Object getData(int index) {
        if (index >= datas.size()) {
            return null;
        }
        return datas.get(index);
    }

}
