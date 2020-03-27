package jmp.convert.musicxml;

import java.io.File;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import function.Utility;
import jmp.convert.IJMPDocumentReader;

public class MusicXMLReader implements IJMPDocumentReader {

    private File file = null;
    private MusicXML musicXML = null;

    public MusicXMLReader(File file) {
        this.file = file;
        musicXML = new MusicXML();
    }

    public void load() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); // DTDエラー対処
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);

        Element root = document.getDocumentElement();
        readRootNodeList(root);

        // printResult();
    }

    public void load(File file) throws SAXException, IOException, ParserConfigurationException {
        this.file = file;
        load();
    }

    public MusicXML getMusicXML() {
        return musicXML;
    }

    private void readRootNodeList(Element root) {
        NodeList scorePartwiseNodeList = root.getChildNodes();
        for (int i = 0; i < scorePartwiseNodeList.getLength(); i++) {
            Node node = scorePartwiseNodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (element.getNodeName().equals("part") == true) {
                    MusicXMLPart mxmlPart = readPartNodeList(element);
                    musicXML.addPart(mxmlPart);
                }
                else if (element.getNodeName().equals("part-list") == true) {
                    MusicXMLPartList mxmlPartList = readPartListNodeList(element);
                    musicXML.setPartList(mxmlPartList);
                }
            }
        }
    }

    private MusicXMLPartList readPartListNodeList(Element element) {
        MusicXMLPartList mxmlPartList = new MusicXMLPartList();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("score-part") == true) {
                    MusicXMLScorePart musicXMLScorePart = readScoreListNodeList(childElement);
                    mxmlPartList.addScorePart(musicXMLScorePart);
                }
            }
        }
        return mxmlPartList;
    }

    private MusicXMLScorePart readScoreListNodeList(Element element) {
        String id = element.getAttribute("id");
        MusicXMLScorePart musicXMLScorePart = new MusicXMLScorePart(id);
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("midi-device") == true) {
                    musicXMLScorePart.setPort(childElement.getAttribute("port"));
                }
                else if (childElement.getNodeName().equals("midi-instrument") == true) {
                    NodeList nodeList2 = childElement.getChildNodes();
                    for (int i2 = 0; i2 < nodeList2.getLength(); i2++) {
                        Node node2 = nodeList2.item(i2);
                        if (node2.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement2 = (Element) node2;
                            if (childElement2.getNodeName().equals("midi-channel") == true) {
                                musicXMLScorePart.setChannel(childElement2.getTextContent());
                            }
                            else if (childElement2.getNodeName().equals("midi-program") == true) {
                                musicXMLScorePart.setProgram(childElement2.getTextContent());
                            }
                            else if (childElement2.getNodeName().equals("volume") == true) {
                                musicXMLScorePart.setVolume(childElement2.getTextContent());
                            }
                            else if (childElement2.getNodeName().equals("pan") == true) {
                                musicXMLScorePart.setPan(childElement2.getTextContent());
                            }
                        }
                    }
                }
            }
        }
        return musicXMLScorePart;
    }

    private MusicXMLPart readPartNodeList(Element element) {
        MusicXMLPart mxmlPart = new MusicXMLPart(element.getAttribute("id"));
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("measure") == true) {
                    MusicXMLMeasure mxmlMeasure = readMeasureNodeList(childElement);
                    mxmlPart.addMeasure(mxmlMeasure);
                }
            }
        }
        return mxmlPart;
    }

    private MusicXMLMeasure readMeasureNodeList(Element element) {
        int number = Utility.tryParseInt(element.getAttribute("number"), -1);
        MusicXMLMeasure mxmlMeasure = new MusicXMLMeasure(number);
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("attributes") == true) {
                    mxmlMeasure.addElement(readAttributesNodeList(childElement));
                }
                else if (childElement.getNodeName().equals("direction") == true) {
                    mxmlMeasure.addElement(readDirectionNodeList(childElement));
                }
                else if (childElement.getNodeName().equals("note") == true) {
                    mxmlMeasure.addElement(readNoteNodeList(childElement));
                }
            }
        }
        return mxmlMeasure;
    }

    private MusicXMLDirection readDirectionNodeList(Element element) {
        MusicXMLDirection mxmlDirection = new MusicXMLDirection();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("sound") == true) {
                    // sound element
                    mxmlDirection.setTempo(childElement.getAttribute("tempo"));
                }
            }
        }
        return mxmlDirection;
    }

    private MusicXMLAttributes readAttributesNodeList(Element element) {
        MusicXMLAttributes mxmlAttributes = new MusicXMLAttributes();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("divisions") == true) {
                    // divisions element
                    mxmlAttributes.setDivisions(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("key") == true) {
                    // key element
                    NodeList keyNodeList = childElement.getChildNodes();
                    for (int j = 0; j < keyNodeList.getLength(); j++) {
                        Node keyNode = keyNodeList.item(j);
                        if (keyNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element keyElement = (Element) keyNode;
                            if (keyElement.getNodeName().equals("fifths") == true) {
                                mxmlAttributes.setFifths(keyElement.getTextContent());
                            }
                            if (keyElement.getNodeName().equals("mode") == true) {
                                mxmlAttributes.setMode(keyElement.getTextContent());
                            }
                        }
                    }
                }
                else if (childElement.getNodeName().equals("time") == true) {
                    // time element
                    NodeList timeNodeList = childElement.getChildNodes();
                    for (int j = 0; j < timeNodeList.getLength(); j++) {
                        Node timeNode = timeNodeList.item(j);
                        if (timeNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element timeElement = (Element) timeNode;
                            if (timeElement.getNodeName().equals("beats") == true) {
                                mxmlAttributes.setBeats(timeElement.getTextContent());
                            }
                            else if (timeElement.getNodeName().equals("beats-type") == true) {
                                mxmlAttributes.setBeatsType(timeElement.getTextContent());
                            }
                        }
                    }
                }
                else if (childElement.getNodeName().equals("staves") == true) {
                    // staves element
                    mxmlAttributes.setStaves(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("clef") == true) {
                    // clef element
                }
            }
        }
        return mxmlAttributes;
    }

    private MusicXMLNote readNoteNodeList(Element element) {
        MusicXMLNote mxmlNote = new MusicXMLNote();
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) node;
                if (childElement.getNodeName().equals("rest") == true) {
                    // rest element
                    mxmlNote.setRest(true);
                }
                else if (childElement.getNodeName().equals("chord") == true) {
                    // rest element
                    mxmlNote.setChord(true);
                }
                else if (childElement.getNodeName().equals("pitch") == true) {
                    // pitch element
                    NodeList pitchNodeList = childElement.getChildNodes();
                    for (int j = 0; j < pitchNodeList.getLength(); j++) {
                        Node pitchNode = pitchNodeList.item(j);
                        if (pitchNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element pitchElement = (Element) pitchNode;
                            if (pitchElement.getNodeName().equals("step") == true) {
                                mxmlNote.setStep(pitchElement.getTextContent());
                            }
                            else if (pitchElement.getNodeName().equals("octave") == true) {
                                mxmlNote.setOctave(pitchElement.getTextContent());
                            }
                            else if (pitchElement.getNodeName().equals("alter") == true) {
                                mxmlNote.setAlter(pitchElement.getTextContent());
                            }
                        }
                    }
                }
                else if (childElement.getNodeName().equals("duration") == true) {
                    // duration element
                    mxmlNote.setDuration(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("voice") == true) {
                    // voice element
                    mxmlNote.setVoice(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("type") == true) {
                    // type element
                    mxmlNote.setType(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("accidental") == true) {
                    // accidental element
                    mxmlNote.setAccidental(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("stem") == true) {
                    // stem element
                    mxmlNote.setStem(childElement.getTextContent());
                }
                else if (childElement.getNodeName().equals("staff") == true) {
                    // staff element
                    mxmlNote.setStaff(childElement.getTextContent());
                }
            }
        }

        return mxmlNote;
    }

    public Sequence convertToMidi() throws InvalidMidiDataException {
        final int BaseDuration = 480;
        final int FixedVelocity = 80;

        Sequence sequence = new Sequence(Sequence.PPQ, BaseDuration);

        int partSize = musicXML.partSize();
        for (int i = 0; i < partSize; i++) {
            MusicXMLPart part = musicXML.getPart(i);
            sequence.createTrack();

            int channel = 0;
            int program = 0;
            MusicXMLScorePart scorePart = musicXML.getPartList().getScorePart(part.getId());
            if (scorePart != null) {
                channel = Utility.tryParseInt(scorePart.getChannel(), 0) - 1;
                program = Utility.tryParseInt(scorePart.getProgram(), 0);

                // 不定値チェック
                channel = (channel < 0) ? 0 : (channel > 15) ? 15 : channel;
                program = (program < 0) ? 0 : program;
            }

            Track track = sequence.getTracks()[i];

            int divisionValue = BaseDuration;
            long position = 100;

            // プログラムチェンジイベント作成
            track.add(createProgramChangeEvent(position, channel, program));
            position += 10;

            position = 480;

            long pastDuration = 0;
            for (MusicXMLMeasure meas : part.getMeasures()) {
                for (MusicXMLElement element : meas.getElements()) {
                    if (element instanceof MusicXMLAttributes) {
                        MusicXMLAttributes mxAttr = (MusicXMLAttributes) element;
                        if (mxAttr.getDivisions().isEmpty() == false) {
                            divisionValue = BaseDuration / mxAttr.getDivisionsInt();
                        }
                    }
                    if (element instanceof MusicXMLDirection) {
                        /* テンポイベント作成 */
                        MusicXMLDirection mxDire = (MusicXMLDirection) element;
                        track.add(createTempoEvent(position, mxDire.getTempoDouble()));
                    }
                    else if (element instanceof MusicXMLNote) {
                        MusicXMLNote mxNote = (MusicXMLNote) element;
                        if (mxNote.isChord() == false) {
                            position += pastDuration;
                        }

                        long duration = divisionValue * mxNote.getDurationInt();
                        if (mxNote.isRest() == false) {
                            /* NoteON, NoteOFFイベント作成 */
                            int midiNumber = convertToMidiNumber(mxNote.getStep(), mxNote.getAlterInt(), mxNote.getOctaveInt());
                            track.add(createNoteOnEvent(position, channel, midiNumber, FixedVelocity));
                            track.add(createNoteOffEvent(position + duration, channel, midiNumber, FixedVelocity));
                        }
                        else {
                            /* 休符はDuration加算だけ行う */
                        }
                        pastDuration = duration;
                    }
                }
            }
        }
        return sequence;
    }

    private int convertToMidiNumber(String step, int alter, int octave) {
        int baseOctave = 4;
        int midiNumber = convertToMidiNumber(step, alter);
        midiNumber += (12 * (octave - baseOctave));
        if (midiNumber < 0) {
            midiNumber = 0;
        }
        if (midiNumber > 127) {
            midiNumber = 127;
        }
        return midiNumber;
    }

    private int convertToMidiNumber(String step, int alter) {
        int midiNumber = 0;
        if (step.equalsIgnoreCase("C") == true) {
            midiNumber = 60;
        }
        else if (step.equalsIgnoreCase("D") == true) {
            midiNumber = 62;
        }
        else if (step.equalsIgnoreCase("E") == true) {
            midiNumber = 64;
        }
        else if (step.equalsIgnoreCase("F") == true) {
            midiNumber = 65;
        }
        else if (step.equalsIgnoreCase("G") == true) {
            midiNumber = 67;
        }
        else if (step.equalsIgnoreCase("A") == true) {
            midiNumber = 69;
        }
        else if (step.equalsIgnoreCase("B") == true) {
            midiNumber = 71;
        }
        else {
            midiNumber = 60;
        }
        return midiNumber + alter;
    }

    protected MidiEvent createProgramChangeEvent(long position, int channel, int programNumber) throws InvalidMidiDataException {
        ShortMessage sMes = new ShortMessage();
        sMes.setMessage(ShortMessage.PROGRAM_CHANGE, channel, programNumber, 0);
        MidiEvent event = new MidiEvent(sMes, position);
        return event;
    }

    protected MidiEvent createNoteOnEvent(long position, int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        ShortMessage sMes = new ShortMessage();
        sMes.setMessage(ShortMessage.NOTE_ON, channel, midiNumber, velocity);
        MidiEvent event = new MidiEvent(sMes, position);
        return event;
    }

    protected MidiEvent createNoteOffEvent(long position, int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        ShortMessage sMes = new ShortMessage();
        sMes.setMessage(ShortMessage.NOTE_OFF, channel, midiNumber, velocity);
        MidiEvent event = new MidiEvent(sMes, position);
        return event;
    }

    protected MidiEvent createTempoEvent(long position, float bpm) throws InvalidMidiDataException {
        long mpq = Math.round(60000000f / bpm);
        byte[] data = new byte[3];
        data[0] = new Long(mpq / 0x10000).byteValue();
        data[1] = new Long((mpq / 0x100) % 0x100).byteValue();
        data[2] = new Long(mpq % 0x100).byteValue();
        MetaMessage meta = new MetaMessage(0x51, data, data.length);
        MidiEvent event = new MidiEvent(meta, position);
        return event;
    }

    @SuppressWarnings("unused")
    private void printResult() {
        MusicXMLPartList partList = musicXML.getPartList();
        for (MusicXMLScorePart sp : partList.getScorePart()) {
            System.out.println("---- PART_ID(" + sp.getPartID() + ") ----");
            System.out.println(" :Port = " + sp.getPort());
            System.out.println(" :Channel = " + sp.getChannel());
            System.out.println(" :Program = " + sp.getProgram());
            System.out.println(" :Volume = " + sp.getVolume());
            System.out.println(" :Pan = " + sp.getPan());
        }

        for (int i = 0; i < musicXML.partSize(); i++) {
            MusicXMLPart part = musicXML.getPart(i);
            for (MusicXMLMeasure meas : part.getMeasures()) {
                System.out.println("---- MEASURE(" + meas.getNumber() + ") ----");
                for (MusicXMLElement element : meas.getElements()) {
                    if (element instanceof MusicXMLAttributes) {
                        MusicXMLAttributes attr = (MusicXMLAttributes) element;
                        System.out.println(" ● Attributes");
                        System.out.println(" :Divisions = " + attr.getDivisions());
                        System.out.println(" :Fifths = " + attr.getFifths());
                        System.out.println(" :Mode = " + attr.getMode());
                        System.out.println(" :Staves = " + attr.getStaves());
                        System.out.println(" :Beats = " + attr.getBeats());
                        System.out.println(" :BeatsType = " + attr.getBeatsType());
                    }
                    else if (element instanceof MusicXMLDirection) {
                        MusicXMLDirection dir = (MusicXMLDirection) element;
                        System.out.println(" ● Direction");
                        System.out.println(" :Tempo = " + dir.getTempo());
                    }
                    else if (element instanceof MusicXMLNote) {
                        MusicXMLNote note = (MusicXMLNote) element;
                        System.out.println(" ● Note");
                        System.out.println(" :Duration = " + note.getDuration());
                        if (note.isRest() == false) {
                            System.out.println(" :Step = " + note.getStep());
                            System.out.println(" :Octave = " + note.getOctave());
                        }
                        else {
                            System.out.println(" :Rest");
                        }
                        System.out.println(" :Accidental = " + note.getAccidental());
                        System.out.println(" :Type = " + note.getType());
                        System.out.println(" :Stem = " + note.getStem());
                        System.out.println(" :Staff = " + note.getStaff());
                        System.out.println(" :Voice = " + note.getVoice());
                    }
                }
            }
        }
    }
}
