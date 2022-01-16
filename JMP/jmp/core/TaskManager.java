package jmp.core;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiMessage;

import jlib.player.IPlayer;
import jmp.JMPFlags;
import jmp.task.CallbackPackage;
import jmp.task.ICallbackFunction;
import jmp.task.ITask;
import jmp.task.NotifyPacket;
import jmp.task.TaskOfBase;
import jmp.task.TaskOfMidiEvent;
import jmp.task.TaskOfNotify;
import jmp.task.TaskOfNotify.NotifyID;
import jmp.task.TaskOfSequence;
import jmp.task.TaskOfTimer;
import jmp.task.TaskOfUpdate;

public class TaskManager extends AbstractManager {

    /** タスクID */
    public static enum TaskID {
        UPDATE, TIMER, SEQUENCE, MIDI, NOTIFY,
    }

    /** タスクデータベース */
    private Map<TaskID, TaskOfBase> taskMap = null;

    TaskManager() {
        super("task");
    }

    @Override
    protected boolean initFunc() {
        super.initFunc();

        taskMap = new HashMap<TaskID, TaskOfBase>();

        // 更新タスク登録
        taskMap.put(TaskID.UPDATE, new TaskOfUpdate());

        // タイマータスク登録
        taskMap.put(TaskID.TIMER, new TaskOfTimer());

        // シーケンスタスク登録
        taskMap.put(TaskID.SEQUENCE, new TaskOfSequence());

        // MIDIイベントタスク登録
        taskMap.put(TaskID.MIDI, new TaskOfMidiEvent());
        
        // Notifyタスク登録
        taskMap.put(TaskID.NOTIFY, new TaskOfNotify());

        // アプリケーション共通コールバック関数の登録
        registerCommonCallbackPackage();
        return true;
    }

    @Override
    protected boolean endFunc() {
        super.endFunc();
        return true;
    }

    public void waitTask(TaskID id, long mills) {
        taskMap.get(id).waitTask(mills);
    }

    public void queuing(TaskID id, ICallbackFunction callbackFunction) {
        // 対応するタスクに対してキューイングを行う
        taskMap.get(id).queuing(callbackFunction);
    }

    public void queuing(ICallbackFunction callbackFunction) {
        queuing(TaskID.SEQUENCE, callbackFunction);
    }

    public void taskStart() {
        /* Threadインスタンスのstart処理 */
        for (ITask task : taskMap.values()) {
            task.startTask();
        }
    }

    public void taskExit() {
        for (ITask task : taskMap.values()) {
            task.exitTask();
        }
    }

    public void join() throws InterruptedException {
        for (ITask task : taskMap.values()) {
            task.joinTask();
        }
    }

    public boolean isRunnable() {
        boolean r = false;
        for (ITask task : taskMap.values()) {
            if (task.isRunnable() == true) {
                r = true;
                break;
            }
        }
        return r;
    }

    public void addMidiEvent(MidiMessage message, long timeStamp, short senderType) {
        TaskOfMidiEvent task = (TaskOfMidiEvent) taskMap.get(TaskID.MIDI);
        task.add(message, timeStamp, senderType);
    }

    public void addCallbackPackage(long cyclicTime, ICallbackFunction func) {
        TaskOfTimer task = (TaskOfTimer) taskMap.get(TaskID.TIMER);
        CallbackPackage pkg = new CallbackPackage(cyclicTime, task.getSleepTime());
        pkg.addCallbackFunction(func);
        task.addCallbackPackage(pkg);
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

                if (JMPFlags.NextPlayFlag == false) {
                    return;
                }

                if (JMPFlags.NowLoadingFlag == false) {
                    if (tickPos >= tickLength) {
                        sm.playNextForList();
                    }
                }
            }
        });
        // シーケンスバーのトグル用コールバック関数を登録
        addCallbackPackage((long) 500, new ICallbackFunction() {

            @Override
            public void callback() {
                if (JMPCore.getSoundManager().isPlay() == true) {
                    // 再生バーのトグル
                    JMPFlags.PlayingTimerToggleFlag = !(JMPFlags.PlayingTimerToggleFlag);
                }
            }
        });
    }
    
    public void sendNotifyMessage(NotifyID id, Object... data) {
        if (taskMap == null) {
            /* taskMapが生成前は何もしない */
            return;
        }
        if (taskMap.containsKey(TaskID.NOTIFY) == true) {
            TaskOfNotify task = (TaskOfNotify) taskMap.get(TaskID.NOTIFY);
            task.add(new NotifyPacket(id, data));
        }
    }
}
