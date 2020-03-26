package jmp.convert.musicxml;

import function.Utility;

public class MusicXMLDirection extends MusicXMLElement {

    private String tempo = "120.0";

    public MusicXMLDirection() {

    }

    public String getTempo() {
        return tempo;
    }

    public float getTempoDouble() {
        return Utility.tryParseFloat(getTempo(), 120.0f);
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }
}
