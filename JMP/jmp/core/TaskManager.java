package jmp.core;

import java.util.ArrayList;
import java.util.List;

import jlib.core.IManager;
import jlib.player.IPlayer;
import jmp.JMPFlags;
import jmp.task.CallbackPackage;
import jmp.task.ICallbackFunction;
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
        super.initFunc();

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

        // アプリケーション共通コールバック関数の登録
        registerCommonCallbackPackage();
        return true;
    }

    @Override
    protected boolean endFunc() {
        super.endFunc();
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
            task.startTask();
        }
    }

    public void taskExit() {
        for (ITask task : tasks) {
            task.exitTask();
        }
    }

    public void join() throws InterruptedException {
        // Threadインスタンスのjoin処理
        for (ITask task : tasks) {
            task.joinTask();
        }
    }

    private void registerCommonCallbackPackage() {
        CallbackPackage commonCallbackPkg = new CallbackPackage((long) 500);

        /* ループ・繰り返し再生の判定 */
        commonCallbackPkg.addCallbackFunction(new ICallbackFunction() {
            @Override
            public void callback() {
                SoundManager sm = JMPCore.getSoundManager();
                DataManager dm = JMPCore.getDataManager();
                IPlayer player = sm.getCurrentPlayer();
                if (player == null) {
                    return;
                }

                long tickPos = player.getPosition();
                long tickLength = player.getLength();
                if (dm.getLoadedFile().isEmpty() == true) {
                    return;
                }

                if (JMPFlags.NowLoadingFlag == false) {
                    if (tickPos >= tickLength) {
                        sm.playNextForList();
                    }
                }
            }
        });
        getTaskOfTimer().addCallbackPackage(commonCallbackPkg);
    }
}
