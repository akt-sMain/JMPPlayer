package jmp;

public class JMPPlayer {
    public static void main(String[] args) {
        int res = (JMPLoader.invoke(args) == true) ? 0 : 1;
        // メインスレッドで処理を行う場合はランタイムを終了する。ただしfalse起動失敗は常に終了する
        if (JMPLoader.MainThreadRunnable == true || res == 1) {
            System.exit(res);
        }
    }
}
