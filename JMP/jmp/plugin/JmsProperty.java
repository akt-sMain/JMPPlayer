package jmp.plugin;

import java.io.File;

/**
 * JMSファイルのプロパティクラス
 *
 * @author akkut
 *
 */
public class JmsProperty {
    private File data;
    private File res;
    private File jar;
    private boolean isDeleteRequest = false;

    public JmsProperty(File jar, File data, File res) {
        setRes(res);
        setData(data);
        setJar(jar);
        this.isDeleteRequest = false;
    }

    public File getData() {
        return data;
    }

    public void setData(File data) {
        this.data = data;
    }

    public File getJar() {
        return jar;
    }

    public void setJar(File jar) {
        this.jar = jar;
    }

    public boolean isDeleteRequest() {
        return isDeleteRequest;
    }

    public void setDeleteRequest(boolean isDeleteRequest) {
        this.isDeleteRequest = isDeleteRequest;
    }

    public File getRes() {
        return res;
    }

    public void setRes(File res) {
        this.res = res;
    }
}
