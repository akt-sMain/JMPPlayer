package wrapper;

public interface IProcessingCallback {
    abstract void begin();

    abstract void end(int result);
}
