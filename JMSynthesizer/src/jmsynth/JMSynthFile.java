package jmsynth;

import java.io.File;

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

import jmsynth.envelope.Envelope;
import jmsynth.modulate.Modulator;
import jmsynth.oscillator.OscillatorSet.WaveType;
import jmsynth.oscillator.WaveGenerater;

public class JMSynthFile {

    public static final String EXTENSION_CONFIG = "jst";

    public static final String WAVE_STR_SAW = "SAW";
    public static final String WAVE_STR_TRIANGLE = "TRIANGLE";
    public static final String WAVE_STR_SQUARE = "SQUARE";
    public static final String WAVE_STR_PULSE_25 = "PULSE_25";
    public static final String WAVE_STR_PULSE_12_5 = "PULSE_12_5";
    public static final String WAVE_STR_SINE = "SINE";
    public static final String WAVE_STR_LOW_SINE = "LOW_SINE";
    public static final String WAVE_STR_LONG_NOIS = "LONG_NOIS";
    public static final String WAVE_STR_SHORT_NOIS = "SHORT_NOIS";

    public static final String[] WAVE_STR_ITEMS = new String[] { //
            WAVE_STR_SINE, //
//            WAVE_STR_LOW_SINE, //
            WAVE_STR_SAW, //
            WAVE_STR_TRIANGLE, //
            WAVE_STR_SQUARE, //
            WAVE_STR_PULSE_25, //
            WAVE_STR_PULSE_12_5, //
            WAVE_STR_LONG_NOIS, //
            WAVE_STR_SHORT_NOIS //
    };//
    public static WaveType toWaveType(String sWave) {
        WaveType type = WaveType.SINE;
        if (sWave.equalsIgnoreCase(WAVE_STR_SAW) == true) {
            type = WaveType.SAW;
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_TRIANGLE) == true) {
            type = WaveType.TRIANGLE;
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_SQUARE) == true) {
            type = WaveType.SQUARE;
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_PULSE_25) == true) {
            type = WaveType.PULSE_25;
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_PULSE_12_5) == true) {
            type = WaveType.PULSE_12_5;
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_SINE) == true) {
            type = WaveType.SINE;
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_LOW_SINE) == true) {
            type = WaveType.LOW_SINE;
        }
        else if (sWave.equalsIgnoreCase(WAVE_STR_LONG_NOIS) == true) {
            type = WaveType.LONG_NOISE;
        }
        else {
            type = WaveType.SHORT_NOISE;
        }
        return type;
    }
    public static String toWaveStr(WaveType type) {
        String sWave = WAVE_STR_LONG_NOIS;
        switch (type) {
            case LONG_NOISE:
                sWave = WAVE_STR_LONG_NOIS;
                break;
            case SHORT_NOISE:
                sWave = WAVE_STR_SHORT_NOIS;
                break;
            case PULSE_25:
                sWave = WAVE_STR_PULSE_25;
                break;
            case PULSE_12_5:
                sWave = WAVE_STR_PULSE_12_5;
                break;
            case SAW:
                sWave = WAVE_STR_SAW;
                break;
            case SINE:
                sWave = WAVE_STR_SINE;
                break;
            case LOW_SINE:
                sWave = WAVE_STR_LOW_SINE;
                break;
            case SQUARE:
                sWave = WAVE_STR_SQUARE;
                break;
            case TRIANGLE:
                sWave = WAVE_STR_TRIANGLE;
                break;
            default:
                break;
        }
        return sWave;
    }
    // グラフィック用
    public static int toYCord(WaveType type, double f, int overallLeval, boolean isReverse) {
        int y = -1;
        switch (type) {
            case SAW:
                y = WaveGenerater.makeSawWave(f, overallLeval, isReverse);
                break;
            case SINE:
                y = WaveGenerater.makeSinWave(f, overallLeval, isReverse);
                break;
            case LOW_SINE:
                y = WaveGenerater.makeSinWaveForLowSampling(f, overallLeval, isReverse);
                break;
            case SQUARE:
                y = WaveGenerater.makeSquareWave(f, overallLeval, isReverse, true);
                break;
            case PULSE_25:
                y = WaveGenerater.makePulseWave(f, overallLeval, 0.25, isReverse, true);
                break;
            case PULSE_12_5:
                y = WaveGenerater.makePulseWave(f, overallLeval, 0.125, isReverse, true);
                break;
            case TRIANGLE:
                y = WaveGenerater.makeTriangleWave(f, overallLeval, isReverse, true);
                break;
            case LONG_NOISE:
            {
                double[] ft = {0.00, 0.05, 0.10, 0.15, 0.25, 0.35, 0.50, 0.55, 0.70, 0.85, 0.95, 1.00};
                double[] lt = {0.20, 0.30, 0.40, 1.00, 0.95, 0.85, 1.00, 0.50, 0.70, 0.40, 0.30, 0.20};
                int sign = 1;
                for (int i=0; i<ft.length; i++) {
                    if (f > ft[i]) {
                        y = (int)((double)overallLeval * lt[i]) * sign;
                    }
                    sign *= -1;
                }
            }
                break;
            case SHORT_NOISE:
            {
                double[] lt = {0.20, 1.00, 0.40};
                int ilt = 0;
                double cf = 0.0;
                int sign = 1;
                while (cf <= 1.00) {
                    if (f <= cf) {
                        y = (int)((double)overallLeval * lt[ilt]) * sign;
                        break;
                    }
                    else {
                        sign *= -1;
                        cf += 0.05;
                        ilt++;
                        if (ilt >= lt.length) {
                            ilt = 0;
                        }
                    }
                }
            }
                break;
            default:
                break;
        }
        return y;
    }

    private static final String S_TRUE = "TRUE";
    private static final String S_FALSE = "FALSE";

    public static final String XML_NODE_ROOT = "jmsynth";
    public static final String XML_NODE_INFO = "info";
    public static final String XML_NODE_INFO_NAME = "name";
    public static final String XML_NODE_INFO_VERSION = "version";
    public static final String XML_NODE_INFO_NUMOFCHANNEL = "num_of_channel";
    public static final String XML_NODE_SOUNDSOURCE = "sound_source";
    public static final String XML_NODE_CHANNEL = "channel";
    public static final String XML_ATTR_CHANNEL = "no";
    public static final String XML_NODE_WAVE = "wave";
    public static final String XML_ATTR_TYPE = "type";
    public static final String XML_ATTR_REVERSE = "reverse";
    public static final String XML_NODE_ENVELOPE = "envelope";
    public static final String XML_ATTR_A = "a";
    public static final String XML_ATTR_MAX_A = "ma";
    public static final String XML_ATTR_D = "d";
    public static final String XML_ATTR_MAX_D = "md";
    public static final String XML_ATTR_S = "s";
    public static final String XML_ATTR_R = "r";
    public static final String XML_ATTR_MAX_R = "mr";
    public static final String XML_NODE_MOD = "modulator";
    public static final String XML_ATTR_DEPTH = "depth";

    public static boolean saveSynthConfig(File file, JMSoftSynthesizer synth) {
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            return false;
        }
        Document document = documentBuilder.newDocument();

        Element configElement = document.createElement(XML_NODE_ROOT);
        document.appendChild(configElement);

        /*
         * info
         */
        Element infoElement = document.createElement(XML_NODE_INFO);
        configElement.appendChild(infoElement);
        apendInfoElement(document, infoElement, synth);

        /*
         * soundsouce
         */
        Element soundSourceElement = document.createElement(XML_NODE_SOUNDSOURCE);
        configElement.appendChild(soundSourceElement);
        apendSoundSourceElement(document, soundSourceElement, synth);


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
    private static void apendInfoElement(Document document, Element rootElement, JMSoftSynthesizer synth) {
        // シンセ名
        Element infoNameElement = document.createElement(XML_NODE_INFO_NAME);
        infoNameElement.setTextContent(String.valueOf(JMSoftSynthesizer.INFO_NAME));
        rootElement.appendChild(infoNameElement);
        // バージョン
        Element infoNameVersionElement = document.createElement(XML_NODE_INFO_VERSION);
        infoNameVersionElement.setTextContent(String.valueOf(JMSoftSynthesizer.INFO_VERSION));
        rootElement.appendChild(infoNameVersionElement);
        // チャンネル数
        Element infoNumOfChannelElement = document.createElement(XML_NODE_INFO_NUMOFCHANNEL);
        infoNumOfChannelElement.setTextContent(String.valueOf(synth.getNumberOfChannel()));
        rootElement.appendChild(infoNumOfChannelElement);
    }
    private static void apendSoundSourceElement(Document document, Element rootElement, JMSoftSynthesizer synth) {
        // channel
        for (int ch=0; ch<synth.getNumberOfChannel(); ch++) {
            Element channelElement = document.createElement(XML_NODE_CHANNEL);
            channelElement.setAttribute(XML_ATTR_CHANNEL, String.valueOf(ch));
            rootElement.appendChild(channelElement);

            apendSynthElement(document, channelElement, synth.getWaveType(ch), synth.isWaveReverse(ch), synth.getEnvelope(ch), synth.getModulator(ch));
        }
    }
    private static void apendSynthElement(Document document, Element rootElement, WaveType waveType, boolean waveReverse, Envelope env, Modulator mod) {
        // wave
        Element waveElement = document.createElement(XML_NODE_WAVE);
        waveElement.setAttribute(XML_ATTR_TYPE, String.valueOf(toWaveStr(waveType)));
        waveElement.setAttribute(XML_ATTR_REVERSE, waveReverse ? S_TRUE : S_FALSE);
        rootElement.appendChild(waveElement);

        // envelope
        Element envElement = document.createElement(XML_NODE_ENVELOPE);
        envElement.setAttribute(XML_ATTR_A, String.valueOf(env.getAttackTime()));
        envElement.setAttribute(XML_ATTR_MAX_A, String.valueOf(env.getMaxAttackMills()));
        envElement.setAttribute(XML_ATTR_D, String.valueOf(env.getDecayTime()));
        envElement.setAttribute(XML_ATTR_MAX_D, String.valueOf(env.getMaxDecayMills()));
        envElement.setAttribute(XML_ATTR_S, String.valueOf(env.getSustainLevel()));
        envElement.setAttribute(XML_ATTR_R, String.valueOf(env.getReleaseTime()));
        envElement.setAttribute(XML_ATTR_MAX_R, String.valueOf(env.getMaxReleaseMills()));
        rootElement.appendChild(envElement);

        // mod
        Element modElement = document.createElement(XML_NODE_MOD);
        modElement.setAttribute(XML_ATTR_DEPTH, String.valueOf(mod.getDepth()));
        rootElement.appendChild(modElement);
    }

    public static boolean loadSynthConfig(File file, JMSoftSynthesizer synth) {
        boolean ret = true;

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
                if (node.getNodeName().equals(XML_NODE_INFO)) {
                    readNodeInfo(node, synth);
                }
                else if (node.getNodeName().equals(XML_NODE_SOUNDSOURCE)) {
                    readNodeSoundSource(node, synth);
                }
            }
        }
        catch (Exception e) {
            ret = false;
        }
        return ret;

    }
    private static void readNodeInfo(Node node, JMSoftSynthesizer synth) {
        NodeList lst = node.getChildNodes();
        for (int i = 0; i < lst.getLength(); i++) {
            if (lst.item(i).getNodeName().equals(XML_NODE_INFO_NAME) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                System.out.println("info_name " + el.getTextContent());
            }
            else if (lst.item(i).getNodeName().equals(XML_NODE_INFO_VERSION) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                System.out.println("info_version " + el.getTextContent());
            }
            else if (lst.item(i).getNodeName().equals(XML_NODE_INFO_NUMOFCHANNEL) == true) {
                if (!(lst.item(i) instanceof Element)) {
                    continue;
                }

                Element el = (Element) lst.item(i);
                System.out.println("num_of_channel " + el.getTextContent());
            }
        }
    }
    private static void readNodeSoundSource(Node node, JMSoftSynthesizer synth) {
        NodeList lst = node.getChildNodes();
        for (int i = 0; i < lst.getLength(); i++) {
            if (lst.item(i).getNodeName().equals(XML_NODE_CHANNEL) == true) {
                Node chNode = lst.item(i);
                readNodeChannel(chNode, synth);
            }
        }
    }
    private static void readNodeChannel(Node node, JMSoftSynthesizer synth) {
        if (!(node instanceof Element)) {
            return;
        }

        int ch = -1;
        Element channelElement = (Element) node;
        if (channelElement.hasAttribute(XML_ATTR_CHANNEL) == true) {
            ch = Integer.valueOf(channelElement.getAttribute(XML_ATTR_CHANNEL));
        }

        if (ch < 0 || ch >= synth.getNumberOfChannel()) {
            return;
        }

        NodeList chLst = channelElement.getChildNodes();
        for (int i = 0; i < chLst.getLength(); i++) {
            Node chChild = chLst.item(i);
            if (!(chChild instanceof Element)) {
                continue;
            }

            if (chChild.getNodeName().equals(XML_NODE_WAVE)) {
                Element waveElement = (Element) chChild;
                if (waveElement.hasAttribute(XML_ATTR_TYPE) == true) {
                    synth.setOscillator(ch, toWaveType(waveElement.getAttribute(XML_ATTR_TYPE)));
                }
                if (waveElement.hasAttribute(XML_ATTR_REVERSE) == true) {
                    synth.setWaveReverse(ch, waveElement.getAttribute(XML_ATTR_REVERSE).equalsIgnoreCase(S_TRUE) ? true : false);
                }
            }
            else if (chChild.getNodeName().equals(XML_NODE_ENVELOPE)) {
                Element envElement = (Element) chChild;
                Envelope env = synth.getEnvelope(ch);
                if (envElement.hasAttribute(XML_ATTR_A) == true) {
                    env.setAttackTime(Double.valueOf(envElement.getAttribute(XML_ATTR_A)));
                }
                if (envElement.hasAttribute(XML_ATTR_MAX_A) == true) {
                    env.setMaxAttackMills(Integer.valueOf(envElement.getAttribute(XML_ATTR_MAX_A)));
                }
                if (envElement.hasAttribute(XML_ATTR_D) == true) {
                    env.setDecayTime(Double.valueOf(envElement.getAttribute(XML_ATTR_D)));
                }
                if (envElement.hasAttribute(XML_ATTR_MAX_D) == true) {
                    env.setMaxDecayMills(Integer.valueOf(envElement.getAttribute(XML_ATTR_MAX_D)));
                }
                if (envElement.hasAttribute(XML_ATTR_S) == true) {
                    env.setSustainLevel(Double.valueOf(envElement.getAttribute(XML_ATTR_S)));
                }
                if (envElement.hasAttribute(XML_ATTR_R) == true) {
                    env.setReleaseTime(Double.valueOf(envElement.getAttribute(XML_ATTR_R)));
                }
                if (envElement.hasAttribute(XML_ATTR_MAX_R) == true) {
                    env.setMaxReleaseMills(Integer.valueOf(envElement.getAttribute(XML_ATTR_MAX_R)));
                }
            }
            else if (chChild.getNodeName().equals(XML_NODE_MOD)) {
                Element modElement = (Element) chChild;
                Modulator mod = synth.getModulator(ch);
                if (modElement.hasAttribute(XML_ATTR_DEPTH) == true) {
                    mod.setDepth(Integer.valueOf(modElement.getAttribute(XML_ATTR_DEPTH)));
                }
            }
        }
    }
}
