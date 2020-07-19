package jmp.midi.toolkit;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jlib.midi.IMidiToolkit;
import jlib.midi.MidiByte;
import jmp.midi.MidiByteMessage;

public class DefaultMidiToolkit implements IMidiToolkit {
    DefaultMidiToolkit() {
    };

    @Override
    public boolean isNoteOn(MidiMessage mes) {
        ShortMessage sMes = null;
        if (mes instanceof ShortMessage) {
            sMes = (ShortMessage) mes;
        }
        else {
            return false;
        }

        if ((sMes.getCommand() == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON) && (sMes.getData2() > 0)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isNoteOff(MidiMessage mes) {
        ShortMessage sMes = null;
        if (mes instanceof ShortMessage) {
            sMes = (ShortMessage) mes;
        }
        else {
            return false;
        }

        if ((sMes.getCommand() == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF)
                || (sMes.getCommand() == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON && sMes.getData2() <= 0)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isProgramChange(MidiMessage mes) {
        ShortMessage sMes = null;
        if (mes instanceof ShortMessage) {
            sMes = (ShortMessage) mes;
        }
        else {
            return false;
        }

        if (sMes.getCommand() == MidiByte.Status.Channel.ChannelVoice.Fst.PROGRAM_CHANGE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isPitchBend(MidiMessage mes) {
        ShortMessage sMes = null;
        if (mes instanceof ShortMessage) {
            sMes = (ShortMessage) mes;
        }
        else {
            return false;
        }

        if (sMes.getCommand() == MidiByte.Status.Channel.ChannelVoice.Fst.PITCH_BEND) {
            return true;
        }
        return false;
    }

    @Override
    public MidiMessage createByteMidiMessage(byte[] data) {
        return new MidiByteMessage(data);
    }

    @Override
    public MidiMessage createProgramChangeMessage(int channel, int programNumber) throws InvalidMidiDataException {
        ShortMessage sMes = new ShortMessage();
        sMes.setMessage(MidiByte.Status.Channel.ChannelVoice.Fst.PROGRAM_CHANGE, channel, programNumber, 0);
        return sMes;
    }

    @Override
    public MidiEvent createProgramChangeEvent(long position, int channel, int programNumber) throws InvalidMidiDataException {
        MidiMessage mes = createProgramChangeMessage(channel, programNumber);
        MidiEvent event = new MidiEvent(mes, position);
        return event;
    }

    @Override
    public MidiMessage createNoteOnMessage(int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        ShortMessage sMes = new ShortMessage();
        sMes.setMessage(MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON, channel, midiNumber, velocity);
        return sMes;
    }

    @Override
    public MidiEvent createNoteOnEvent(long position, int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        MidiMessage mes = createNoteOnMessage(channel, midiNumber, velocity);
        MidiEvent event = new MidiEvent(mes, position);
        return event;
    }

    @Override
    public MidiMessage createNoteOffMessage(int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        ShortMessage sMes = new ShortMessage();
        sMes.setMessage(MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF, channel, midiNumber, velocity);
        return sMes;
    }

    @Override
    public MidiEvent createNoteOffEvent(long position, int channel, int midiNumber, int velocity) throws InvalidMidiDataException {
        MidiMessage mes = createNoteOffMessage(channel, midiNumber, velocity);
        MidiEvent event = new MidiEvent(mes, position);
        return event;
    }

    @Override
    public MidiMessage createPitchBendMessage(int channel, int bend) throws InvalidMidiDataException {
        ShortMessage sMes = new ShortMessage();
        int msb = bend >> 7;
        int lsb = bend & 0x00ef;
        sMes.setMessage(MidiByte.Status.Channel.ChannelVoice.Fst.PITCH_BEND, channel, lsb, msb);
        return sMes;
    }

    @Override
    public MidiEvent createPitchBendEvent(long position, int channel, int bend) throws InvalidMidiDataException {
        MidiMessage mes = createPitchBendMessage(channel, bend);
        MidiEvent event = new MidiEvent(mes, position);
        return event;
    }

    @Override
    public MidiMessage createTempoMessage(float bpm) throws InvalidMidiDataException {
        long mpq = Math.round(60000000f / bpm);
        byte[] data = new byte[3];
        data[0] = new Long(mpq / 0x10000).byteValue();
        data[1] = new Long((mpq / 0x100) % 0x100).byteValue();
        data[2] = new Long(mpq % 0x100).byteValue();
        MetaMessage meta = new MetaMessage(0x51, data, data.length);
        return meta;
    }

    @Override
    public MidiEvent createTempoEvent(long position, float bpm) throws InvalidMidiDataException {
        MidiMessage mes = createTempoMessage(bpm);
        MidiEvent event = new MidiEvent(mes, position);
        return event;
    }

}
