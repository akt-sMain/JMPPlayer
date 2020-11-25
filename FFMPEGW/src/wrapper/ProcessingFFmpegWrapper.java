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
    protected String path = "";

    /** waitあり */
    protected boolean waitFor = false;

    /** 環境変数定義有効 */
    protected boolean isFFmpegInstalled = false;

    /** コマンド */
    protected String ffmpegCommand = "ffmpeg";

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
            notifyBegin();

            try {
                proc.waitFor();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            int result = proc.exitValue();

            pPrintln("## EXIT(" + result + ") ##");
            notifyEnd(result);

            isRunnable = false;
        }

        private void notifyBegin() {
            if (callback != null) {
                callback.begin();
            }
        }

        private void notifyEnd(int result) {
            if (callback != null) {
                callback.end(result);
            }
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
        super();

        this.path = path;
    }

    public ProcessingFFmpegWrapper() {
        super();
    }

    @Override
    protected void init() {
        super.init();
        this.path = "";
        this.waitFor = false;
        this.callback = null;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isWaitFor() {
        return waitFor;
    }

    public void setWaitFor(boolean waitFor) {
        this.waitFor = waitFor;
    }

    public void setCallback(IProcessingCallback callback) {
        this.callback = callback;
    }

    @Override
    public boolean isValid() {
        if (isFFmpegInstalled() == true) {
            return true;
        }
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

        if (isFFmpegInstalled() == true) {
            boolean isExistsCurrent = false;
            File exeFile = new File("ffmpeg.exe");
            if (path.isEmpty() == false && exeFile.exists() == true) {
                isExistsCurrent = true;
            }

            if (isExistsCurrent == true) {
                // カレントに有効なffmpegがあるときはそちらを優先的に使用する
                cmd.add(0, exeFile.getPath());
            }
            else {
                cmd.add(0, getFFmpegCommand());
            }
        }
        else {
            cmd.add(0, path);
        }

        for (String c : cmd) {
            System.out.print(c + " ");
        }
        System.out.println();

        ProcessBuilder pb = new ProcessBuilder();
        pb.inheritIO();

        pb.command(cmd);
        Process p = pb.start();

        LogingRunnable lr = new LogingRunnable(p);
        ExecutorService execTh = Executors.newSingleThreadExecutor();
        execTh.submit(lr);

        if (isWaitFor() == true) {
            while (lr.isRunnable() == true) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    break;
                }

                if (isWaitFor() == false) {
                    // Wait設定解除時に抜ける
                    break;
                }
            }
        }

    }

    public boolean isFFmpegInstalled() {
        return isFFmpegInstalled;
    }

    public void setFFmpegInstalled(boolean isFFmpegInstalled) {
        this.isFFmpegInstalled = isFFmpegInstalled;
    }

    public String getFFmpegCommand() {
        return ffmpegCommand;
    }

    public void setFFmpegCommand(String ffmpegCommand) {
        this.ffmpegCommand = ffmpegCommand;
    }
}
