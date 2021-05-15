package wrapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import process.IProcessingCallback;
import process.ProcessInvoker;

public class ProcessingYoutubeDLWrapper {
    /** FFmpegの実行パス */
    protected String path = "";

    protected String output = "";

    protected ProcessInvoker invoker = null;

    public ProcessingYoutubeDLWrapper() {
        this.invoker = new ProcessInvoker();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public void setOutput(String path) {
        this.output = path;
    }

    public String getOutput() {
        return this.output;
    }

    public boolean isWaitFor() {
        return invoker.isWaitFor();
    }

    public void setWaitFor(boolean waitFor) {
        invoker.setWaitFor(waitFor);
    }

    public void setCallback(IProcessingCallback callback) {
        invoker.setCallback(callback);
    }

    public boolean isValid() {
        File f = new File(path);
        return f.exists();
    }

    public void convert(String url, String audioFormat) throws IOException {
        ArrayList<String> cmd = new ArrayList<String>();
//        if (output.isEmpty() == false) {
//            cmd.add("-o");
//            cmd.add(output);
//        }
        cmd.add(url);
        cmd.add("-x");
        cmd.add("--audio-format");
        cmd.add(audioFormat);
        exec(cmd);
    }

    public void exec(String... cmd) throws IOException {
        exec(cmd);
    }

    public void exec(List<String> cmd) throws IOException {
        cmd.add(0, path);
        invoker.exec(cmd);
    }
}
