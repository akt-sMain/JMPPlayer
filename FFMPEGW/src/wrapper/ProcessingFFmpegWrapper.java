package wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import wffmpeg.FFmpegWrapper;

/**
 * OSのプロセスからFFmpegを使用するためのラッパークラス
 *
 * @author akkut
 *
 */
public class ProcessingFFmpegWrapper extends FFmpegWrapper {
    /** FFmpegの実行パス */
    private String path = "";

    private IProcessingCallback callback = null;

    public class LogingRunnable implements Runnable {
        private boolean isRunnable = true;
        private Process proc;

        public LogingRunnable(Process p) {
            proc = p;
            isRunnable = true;
        }

        @Override
        public void run() {
            pPrintln("## Converting... ##");
            callback.begin();
            try {
                proc.waitFor();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            int result = proc.exitValue();
            pPrintln("## EXIT(" + proc.exitValue() + ") ##");
            isRunnable = false;
            callback.end(result);
        }

        private void pPrintln(String str) {
            System.out.println(str);
        }

        public boolean isRunnable() {
            return isRunnable;
        }

    }

    /**
     * コンストラクタ
     *
     * @param path
     *            FFmpegの実行パス
     */
    public ProcessingFFmpegWrapper(String path) {
        this.path = path;
    }

    public ProcessingFFmpegWrapper() {
        this.path = "";
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    @Override
    public boolean isValid() {
        File f = new File(path);
        return f.exists();
    }

    @Override
    public void convert(String inPath, String outPath) throws IOException {
        ArrayList<String> cmd = new ArrayList<String>();
        if (isOverwrite() == true) {
            // 上書き有無
            cmd.add("-y");
        }
        cmd.add("-i");
        cmd.add(inPath);
        cmd.add(outPath);
        exec(cmd);
    }

    /**
     * コマンド実行
     *
     * @param cmd
     *            コマンド
     * @throws IOException
     */
    public void exec(String... cmd) throws IOException {
        exec(cmd);
    }

    /**
     * コマンド実行
     *
     * @param cmd
     *            コマンド
     * @throws IOException
     * @throws InterruptedException
     */
    public void exec(List<String> cmd) throws IOException {
        cmd.add(0, path);

        ProcessBuilder pb = new ProcessBuilder();
        pb.inheritIO();

        pb.command(cmd);
        Process p = pb.start();

        LogingRunnable lr = new LogingRunnable(p);
        ExecutorService execTh = Executors.newSingleThreadExecutor();
        execTh.submit(lr);

//        while(lr.isRunnable() == true) {
//            try {
//                Thread.sleep(1000);
//            }
//            catch (InterruptedException e) {
//            }
//        }

    }

    public void setCallback(IProcessingCallback callback) {
        this.callback = callback;
    }
}
