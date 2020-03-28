package jmp.player;

import java.io.File;

import javax.sound.midi.Sequence;

import jmp.convert.musicxml.MusicXMLReader;
import jmp.core.SoundManager;

public class MusicXmlPlayer extends Player {

    private MidiPlayer midiPlayer = SoundManager.MidiPlayer;

    public MusicXmlPlayer() {
    }

    @Override
    public void play() {
        midiPlayer.play();
    }

    @Override
    public void stop() {
        midiPlayer.stop();
    }

    @Override
    public boolean isRunnable() {
        return midiPlayer.isRunnable();
    }

    @Override
    public void setPosition(long pos) {
        midiPlayer.setPosition(pos);
    }

    @Override
    public long getPosition() {
        return midiPlayer.getPosition();
    }

    @Override
    public long getLength() {
        return midiPlayer.getLength();
    }

    @Override
    public boolean isValid() {
        return midiPlayer.isValid();
    }

    @Override
    public int getPositionSecond() {
        return midiPlayer.getPositionSecond();
    }

    @Override
    public int getLengthSecond() {
        return midiPlayer.getLengthSecond();
    }

    @Override
    public void setVolume(float volume) {
        midiPlayer.setVolume(volume);
    }

    @Override
    public float getVolume() {
        return midiPlayer.getVolume();
    }

    @Override
    public boolean loadFile(File file) throws Exception {
        MusicXMLReader reader = new MusicXMLReader(file);
        reader.load();

        boolean result = false;
        if (reader.isLoadResult() == true) {
            Sequence seq = reader.convertToMidi();
            midiPlayer.loadMidiSequence(seq);
            result = true;
        }
        return result;
    }

    @Override
    public boolean saveFile(File file) throws Exception {
        return midiPlayer.saveFile(file);
    }

}
