package jmp.core;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiMessage;

import jlib.player.IPlayer;
import jmp.JMPFlags;
import jmp.task.CallbackPackage;
import jmp.task.ICallbackFunction;
import jmp.task.ITask;
import jmp.task.TaskOfBase;
import jmp.task.TaskOfMidiEvent;
import jmp.task.TaskOfSequence;
import jmp.task.TaskOfTimer;
import jmp.task.TaskOfUpdate;

public class TaskManager extends AbstractManager {

    private TaskOfUpdate taskOfUpdate;
    private TaskOfTimer taskOfTimer;
    private TaskOfSequence taskOfSequence;
    private TaskOfMidiEvent taskOfMidiEvent;
    private static List<TaskOfBase> tasks = new ArrayList<TaskOfBase>();

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

    public void queuing(Class<?> c, ICallbackFunction callbackFunction) {
        // 対応するタスクに対してキューイングを行う
        for (TaskOfBase task : tasks) {
            if (task.getClass() == c) {
                task.queuing(callbackFunction);
                break;
            }
        }
    }

    public void queuing(ICallbackFunction callbackFunction) {
        queuing(TaskOfSequence.class, callbackFunction);
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
        for (ITask task : tasks) {
            task.joinTask();
        }
    }

    public void addMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        taskOfMidiEvent.add(message, timeStamp, senderType);
    }

    public void addCallbackPackage(long cyclicTime, ICallbackFunction func) {
        CallbackPackage pkg = new CallbackPackage(cyclicTime, taskOfTimer.getSleepTime());
        pkg.addCallbackFunction(func);
        taskOfTimer.addCallbackPackage(pkg);
    }

    private void registerCommonCallbackPackage() {
        addCallbackPackage(500L, new ICallbackFunction() {
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
    }
}
