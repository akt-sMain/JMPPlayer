package jmp.convert.musicxml;

import java.util.ArrayList;
import java.util.List;

public class MusicXML {
    private List<MusicXMLPart> parts = new ArrayList<MusicXMLPart>();

    public MusicXML() {
    }

    public void addPart(MusicXMLPart part) {
        parts.add(part);
    }

    public MusicXMLPart getPart(int index) {
        return parts.get(index);
    }

    public List<MusicXMLPart> getParts() {
        return parts;
    }

    public int partSize() {
        return parts.size();
    }
}
