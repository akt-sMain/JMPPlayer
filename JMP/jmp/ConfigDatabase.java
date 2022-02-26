package jmp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jmp.core.JMPCore;
import jmp.util.JmpUtil;

public class ConfigDatabase {

    private static final String KER_SEPARATOR = "<->";

    // 設定データベース
    private Map<String, String> database = null;

    private String[] keyset = null;
    
    private String appName = "None";
    private String version = "None";

    public ConfigDatabase(String[] keys) {
        setup(keys);
    }

    public ConfigDatabase(Set<String> keys) {
        setup(keySetToArray(keys));
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    private void setup(String[] keys) {
        database = new HashMap<String, String>();
        database.clear();

        keyset = keys;

        for (String key : keyset) {
            database.put(key, "");
        }
    }

    public static String[] keySetToArray(Set<String> keys) {
        String[] aKey = new String[keys.size()];
        int i = 0;
        Iterator<String> ite = keys.iterator();
        while (ite.hasNext()) {
            String s = ite.next();
            aKey[i] = s;
            i++;
        }
        return aKey;
    }

    public static ConfigDatabase create(String path) {
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        File file = new File(path);
        if (file.exists() == false) {
            return null;
        }

        try {
            List<String> textContents = JmpUtil.readTextFile(file);

            for (String line : textContents) {
                String[] sLine = line.split(KER_SEPARATOR);
                if (sLine.length >= 1) {
                    String key = sLine[0].trim();
                    String value = (sLine.length >= 2) ? sLine[1] : "";
                    keys.add(key);
                    values.add(value);
                }
            }
        }
        catch (Exception e) {
            return null;
        }

        String[] aKeys = new String[keys.size()];
        for (int i = 0; i < aKeys.length; i++) {
            aKeys[i] = keys.get(i);
        }

        ConfigDatabase db = new ConfigDatabase(aKeys);
        for (int i = 0; i < aKeys.length; i++) {
            db.setConfigParam(aKeys[i], values.get(i));
        }
        return db;
    }

    public String[] getKeySet() {
        return keyset;
    }

    public void setConfigParam(String key, String value) {
        if (database.containsKey(key) == true) {
            database.put(key, value);
        }
    }

    public String getConfigParam(String key) {
        String ret = "";
        if (database.containsKey(key) == true) {
            ret = database.get(key);
        }
        return ret;
    }

    public boolean output(String path) {
        //return outputTxt(path);
        return outputXml(path);
    }
    
    public boolean reading(String path) {
        setAppName("Unknown");
        setVersion("Unknown");
        //return readingTxt(path);
        return readingXml(path);
    }
    
    protected boolean outputTxt(String path) {
        boolean ret = true;

        try {
            List<String> textContents = new LinkedList<String>();

            for (String key : database.keySet()) {
                String value = getConfigParam(key);
                textContents.add(key + KER_SEPARATOR + value);
            }
            JmpUtil.writeTextFile(path, textContents);
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }

    protected boolean readingTxt(String path) {
        boolean ret = true;
        File file = new File(path);
        if (file.exists() == false) {
            return false;
        }

        try {
            List<String> textContents = JmpUtil.readTextFile(file);

            for (String line : textContents) {
                String[] sLine = line.split(KER_SEPARATOR);
                if (sLine.length >= 1) {
                    String key = sLine[0].trim();
                    for (String ckey : getKeySet()) {
                        if (key.equals(ckey) == true) {
                            String value = (sLine.length >= 2) ? sLine[1] : "";
                            database.put(key, value);
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }
    
    private static String JMP_XML_ROOT = "jmpp";
    private static String JMP_XML_INFO = "info";
    private static String JMP_XML_NAME = "name";
    private static String JMP_XML_VERSION = "version";
    private static String JMP_XML_DATA = "data";
    private static String JMP_XML_OBJ = "param";
    private static String JMP_XML_KEY = "key";
    
    protected boolean readingXml(String path) {
        boolean ret = true;

        File file = new  File(path);
        if (file.exists() == false) {
            return false;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            Element root = document.getDocumentElement();
            NodeList rootChildrens = root.getChildNodes();

            for (int i = 0; i < rootChildrens.getLength(); i++) {
                Node node = rootChildrens.item(i);
                if (node.getNodeName().equals(JMP_XML_INFO)) {
                    readNodeInfo(node);
                }
                else if (node.getNodeName().equals(JMP_XML_DATA)) {
                    readNodeData(node);
                }
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;
    }
    
    private void readNodeInfo(Node node) {
        NodeList lst = node.getChildNodes();
        for (int i = 0; i < lst.getLength(); i++) {
            if (lst.item(i).getNodeName().equals(JMP_XML_NAME) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                setAppName(el.getTextContent());
            }
            else if (lst.item(i).getNodeName().equals(JMP_XML_VERSION) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                setVersion(el.getTextContent());
            }
        }
    }
    
    private void readNodeData(Node node) {
        NodeList lst = node.getChildNodes();
        for (int i = 0; i < lst.getLength(); i++) {
            if (lst.item(i).getNodeName().equals(JMP_XML_OBJ) == true) {
                Node objNode = lst.item(i);
                Element ele = (Element) objNode;
                
                String key = "";
                String value = "";
                if (ele.hasAttribute(JMP_XML_KEY) == true) {
                    key = ele.getAttribute(JMP_XML_KEY);
                    value = ele.getTextContent();
                }
                for (String ckey : getKeySet()) {
                    if (key.equals(ckey) == true) {
                        database.put(key, value);
                        break;
                    }
                }
            }
        }
    }
    
    protected boolean outputXml(String path) {
        File file = new File(path);
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            return false;
        }
        Document document = documentBuilder.newDocument();

        Element rootElement = document.createElement(JMP_XML_ROOT);
        document.appendChild(rootElement);

        /*
         * info
         */
        Element infoElement = document.createElement(JMP_XML_INFO);
        rootElement.appendChild(infoElement);
        // AppName
        Element infoNameElement = document.createElement(JMP_XML_NAME);
        infoNameElement.setTextContent(JMPCore.APPLICATION_NAME);
        infoElement.appendChild(infoNameElement);
        // バージョン
        Element infoNameVersionElement = document.createElement(JMP_XML_VERSION);
        infoNameVersionElement.setTextContent(String.valueOf(JMPCore.APPLICATION_VERSION));
        infoElement.appendChild(infoNameVersionElement);

        /*
         * data
         */
        Element dataElement = document.createElement(JMP_XML_DATA);
        rootElement.appendChild(dataElement);
        apendDataElement(document, dataElement);

        // Transformerインスタンスの生成
        Transformer transformer = null;
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformer = transformerFactory.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            return false;
        }

        // Transformerの設定
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // 改行指定
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty("encoding", "UTF-8"); // エンコーディング

        // XMLファイルの作成
        try {
            transformer.transform(new DOMSource(document), new StreamResult(file));
        }
        catch (TransformerException e) {
            return false;
        }
        return true;
    }

    private void apendDataElement(Document document, Element rootElement) {
        for (String key : database.keySet()) {
            String value = getConfigParam(key);
            
            Element objElement = document.createElement(JMP_XML_OBJ);
            objElement.setAttribute(JMP_XML_KEY, key);
            objElement.setTextContent(value);
            rootElement.appendChild(objElement);
        }
    }

}
