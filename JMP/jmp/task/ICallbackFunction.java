package jmp.task;

public interface ICallbackFunction {
    abstract void callback();

    default boolean isDeleteConditions(int count) {
	return false;
    }
}
