package jmp.convert.musicxml;

import function.Utility;

public class MusicXMLBackup extends MusicXMLElement {

    /* <durasion> */
    private String duration = "";

    public MusicXMLBackup() {
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getDurationInt() {
        return Utility.tryParseInt(getDuration(), 0);
    }

}
