package jmp;

import java.util.ArrayList;
import java.util.List;

import jmp.task.ITask;
import jmp.task.TaskOfMidiEvent;
import jmp.task.TaskOfSequence;
import jmp.task.TaskOfTimer;
import jmp.task.TaskOfUpdate;

public class TaskManager {

    private TaskOfUpdate taskOfUpdate;
    private TaskOfTimer taskOfTimer;
    private TaskOfSequence taskOfSequence;
    private TaskOfMidiEvent taskOfMidiEvent;
    private static List<ITask> tasks = new ArrayList<ITask>();

    private static TaskManager singleton = new TaskManager();

    private TaskManager() {
    }

    public static TaskManager getInstance() {
        return singleton;
    }

    public boolean initFunc() {
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

        /* Threadインスタンスのstart処理 */
        for (ITask task : tasks) {
            if (task instanceof Thread) {
                Thread th = (Thread) task;
                th.start();
            }
        }
        return true;
    }

    public boolean endFunc() {
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
