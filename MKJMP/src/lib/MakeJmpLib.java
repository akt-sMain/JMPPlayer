package lib;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import function.Platform;
import function.Utility;

public class MakeJmpLib {

    public static final String PKG_SETUP_EX = "jms";

    public static final String PKG_ZIP_EX = "jmz";

    /* コマンド文字列 */
    public static final String CMD_CONSOLE = "-cmd";
    public static final String CMD_NAME = "-name";
    public static final String CMD_JAR = "-jar";
    public static final String CMD_DATA = "-data";
    public static final String CMD_RES = "-res";
    public static final String CMD_OUT = "-out";
    // 隠しコマンド
    public static final String _CMD_WIN = "--win"; // windowを閉じたときにランタイムを終了しない(別のjavaソースからコールする用)
    public static final String _CMD_EXP = "--exp"; // export後にExplolerを表示する

    public static void call(String... args) {
        try {
            boolean isConsole = false;
            String name = "";
            String jar = "";
            String data = "";
            String res = "";
            String out = "";
            boolean appExitFlag = true;
            boolean showExplolerFlag = false;

            for (int i = 0; i < args.length; i++) {
                String str = args[i];
                if (str.equalsIgnoreCase(CMD_CONSOLE)) {
                    isConsole = true;
                }
                else if (str.equalsIgnoreCase(CMD_NAME)) {
                    i++;
                    if (i < args.length && args[i].startsWith("-") == false) {
                        name = args[i];
                    }
                }
                else if (str.equalsIgnoreCase(CMD_JAR)) {
                    i++;
                    if (i < args.length && args[i].startsWith("-") == false) {
                        jar = args[i];
                    }
                }
                else if (str.equalsIgnoreCase(CMD_DATA)) {
                    i++;
                    if (i < args.length && args[i].startsWith("-") == false) {
                        data = args[i];
                    }
                }
                else if (str.equalsIgnoreCase(CMD_RES)) {
                    i++;
                    if (i < args.length && args[i].startsWith("-") == false) {
                        res = args[i];
                    }
                }
                else if (str.equalsIgnoreCase(CMD_OUT)) {
                    i++;
                    if (i < args.length && args[i].startsWith("-") == false) {
                        out = args[i];
                    }
                }
                else if (str.equalsIgnoreCase(_CMD_WIN)) {
                    appExitFlag = false;
                }
                else if (str.equalsIgnoreCase(_CMD_EXP)) {
                    showExplolerFlag = true;
                }
            }

            if (isConsole == false) {
                MakeJmpPackege frame = new MakeJmpPackege(jar, data, res, out, appExitFlag, showExplolerFlag);
                frame.setVisible(true);
            }
            else {
                if (name.isEmpty() == true) {
                    name = Utility.getFileNameNotExtension(jar);
                }

                /*
                 * dataがEmptyの場合は、data==FALSEになる
                 */
                MakeJmpLib.exportPackage(jar, data, res, name, out);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exportPackage(MakeJmpConfig config) {
        if (config.isAddData() == true) {
            MakeJmpLib.exportPackageForBlankData(config.getJar(), config.getRes(), config.getPluginName(), config.getOutput());
        }
        else {
            MakeJmpLib.exportPackage(config.getJar(), config.getData(), config.getRes(), config.getPluginName(), config.getOutput());
        }
    }

    public static void exportPackageForBlankData(String jar, String res, String pluginName, String exportDir) {
        exportPackage(jar, true, "", res, pluginName, exportDir);
    }

    public static void exportPackage(String jar, String data, String res, String pluginName, String exportDir) {
        exportPackage(jar, false, data, res, pluginName, exportDir);
    }

    private static void exportPackage(String jar, boolean isAddBlankData, String data, String res, String pluginName, String exportDir) {
        String jmsContents = "";
        if (jar.isEmpty() != true) {
            String jarPath = jar;
            if (Utility.isExsistFile(jarPath) == true) {
                jmsContents += Utility.stringsCombin("PLUGIN = ", Utility.getFileNameAndExtension(jarPath));
            }
        }

        boolean isData = (data.isEmpty() == true) ? false : true;
        if (isData == false) {
            if (isAddBlankData == true) {
                isData = true;
            }
        }
        else {
            isAddBlankData = false;
        }

        boolean isRes = (res.isEmpty() == true) ? false : true;

        jmsContents += Platform.getNewLine();
        jmsContents += Utility.stringsCombin("DATA = " + (isData ? "TRUE" : "FALSE"));

        jmsContents += Platform.getNewLine();
        jmsContents += Utility.stringsCombin("RES = " + (isRes ? "TRUE" : "FALSE"));

        try {
            String plgDirName = pluginName;
            File plgDir = new File(plgDirName);
            if (plgDir.exists() == false) {
                plgDir.mkdir();
            }

            String jmsPath = Utility.pathCombin(plgDirName, pluginName + "." + PKG_SETUP_EX);
            Utility.outputTextFile(jmsPath, jmsContents);
            Utility.copyFile(jar, Utility.pathCombin(plgDirName, pluginName + ".jar"));
            if (isAddBlankData == false) {
                Utility.copyFile(data, Utility.pathCombin(plgDirName, "DATA"));
            }
            Utility.copyFile(res, Utility.pathCombin(plgDirName, "RES"));

            String outDir = exportDir;
            File od = new File(outDir);
            if (!od.isDirectory() || !od.exists()) {
                outDir = "";
            }

            String zipPath = Utility.pathCombin(outDir, (plgDirName + "." + PKG_ZIP_EX));
            try {
                Utility.toZip(plgDirName, zipPath);
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            finally {
                Utility.deleteFileDirectory(plgDirName);
            }

        }
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
    }

}
