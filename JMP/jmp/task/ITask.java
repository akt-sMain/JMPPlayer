package jmp.task;

public interface ITask {
    /**
     * タスク開始状態遷移
     */
    abstract void startTask();

    /**
     * タスク終了状態遷移
     */
    abstract void exitTask();

    /**
     * タスク待機状態遷移
     */
    abstract void joinTask();
}
