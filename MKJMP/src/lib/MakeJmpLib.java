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

    public static void exportPackageForBlankData(String jar, String res, String pluginName, String exportDir) {
        exportPackage(jar, true, "", res, pluginName, exportDir);
    }

    public static void exportPackage(String jar, String data, String res, String pluginName, String exportDir) {
        exportPackage(jar, false, data, res, pluginName, exportDir);
    }

    private static void exportPackage(String jar, boolean isAddBlankData, String data, String res, String pluginName,
            String exportDir) {
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
