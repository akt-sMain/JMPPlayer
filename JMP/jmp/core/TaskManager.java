package jmp.core;

import java.util.ArrayList;
import java.util.List;

import jlib.core.IManager;
import jmp.task.ITask;
import jmp.task.TaskOfMidiEvent;
import jmp.task.TaskOfSequence;
import jmp.task.TaskOfTimer;
import jmp.task.TaskOfUpdate;

public class TaskManager extends AbstractManager implements IManager {

    private TaskOfUpdate taskOfUpdate;
    private TaskOfTimer taskOfTimer;
    private TaskOfSequence taskOfSequence;
    private TaskOfMidiEvent taskOfMidiEvent;
    private static List<ITask> tasks = new ArrayList<ITask>();

    TaskManager(int pri) {
        super(pri, "task");
    }

    @Override
    protected boolean initFunc() {
        if (initializeFlag == false) {
            initializeFlag = true;
        }
        // 更新タスク登録
        taskOfUpdate = new TaskOfUpdate();
        tasks.add(taskOfUpdate);

        // タイマータスク登録
        taskOfTimer = new TaskOfTimer();
        tasks.add(taskOfTimer);

        // シーケンスタスク登録
        taskOfSequence = new TaskOfSequence();
        tasks.add(taskOfSequence);

        // MIDIイベントタスク登録
        taskOfMidiEvent = new TaskOfMidiEvent();
        tasks.add(taskOfMidiEvent);
        return true;
    }

    @Override
    protected boolean endFunc() {
        if (initializeFlag == false) {
            return false;
        }
        return true;
    }

    public TaskOfUpdate getTaskOfUpdate() {
        return taskOfUpdate;
    }

    public TaskOfTimer getTaskOfTimer() {
        return taskOfTimer;
    }

    public TaskOfSequence getTaskOfSequence() {
        return taskOfSequence;
    }

    public TaskOfMidiEvent getTaskOfMidiEvent() {
        return taskOfMidiEvent;
    }

    public void taskStart() {
        /* Threadインスタンスのstart処理 */
        for (ITask task : tasks) {
            if (task instanceof Thread) {
                Thread th = (Thread) task;
                th.start();
            }
        }
    }

    public void taskExit() {
        for (ITask t : tasks) {
            t.exit();
        }
    }

    public void join() throws InterruptedException {
        // Threadインスタンスのjoin処理
        for (ITask task : tasks) {
            if (task instanceof Thread) {
                Thread th = (Thread) task;
                th.join();
            }
        }
    }
}
