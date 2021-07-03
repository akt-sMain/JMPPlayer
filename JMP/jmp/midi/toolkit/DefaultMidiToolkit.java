package jmp.midi.toolkit;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import jlib.midi.IMidiToolkit;
import jlib.midi.MidiByte;
import jmp.midi.MidiByteMessage;

public class DefaultMidiToolkit implements IMidiToolkit {
    DefaultMidiToolkit() {
    };

    @Override
    public List<MidiDevice> getMidiDevices() {
        ArrayList<MidiDevice> devices = new ArrayList<MidiDevice>();

        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

        for (int i = 0; i < infos.length; i++) {
            MidiDevice.Info info = infos[i];
            MidiDevice device = null;

            try {
                device = MidiSystem.getMidiDevice(info);
                devices.add(device);
            }
            catch (MidiUnavailableException me) {
            }
            catch (Exception e) {
            }
        }

        return devices;
    }

    @Override
    public MidiDevice.Info[] getMidiDeviceInfo(boolean incTransmitter, boolean incReciever) {
        ArrayList<MidiDevice.Info> ret = new ArrayList<MidiDevice.Info>();

        MidiDevice.Info[] tmp = MidiSystem.getMidiDeviceInfo();
        for (int i = 0; i < tmp.length; i++) {
            MidiDevice dev;
            try {
                dev = MidiSystem.getMidiDevice(tmp[i]);
            }
            catch (MidiUnavailableException e) {
                continue;
            }

            if (incTransmitter == false && dev.getMaxTransmitters() != 0) {
                // Transmitterは除外
                continue;
            }
            else if (incReciever == false && dev.getMaxReceivers() != 0) {
                // Recieverは除外
                continue;
            }
            ret.add(tmp[i]);
        }
        return (MidiDevice.Info[]) ret.toArray(new MidiDevice.Info[0]);
    }

    @Override
    public Receiver findReciver(String recvName) {
        Receiver receiver = null;

        try {
            MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
            for (MidiDevice.Info info : devices) {
                if (info.getName().startsWith(recvName) == false) {
                    // ネームチェック
                    continue;
                }

                MidiDevice dv = MidiSystem.getMidiDevice(info);
                if (dv.getMaxReceivers() > 0) {
                    dv.open();
                    receiver = dv.getReceiver();
                    break;
                }
            }
        }
        catch (MidiUnavailableException e) {
            receiver = null;
        }

        return receiver;
    }

    @Override
    public boolean isNoteOn(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        int data2 = MidiByte.getData2(mes.getMessage(), mes.getLength());
        if ((command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON) && (data2 > 0)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isNoteOff(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        int data2 = MidiByte.getData2(mes.getMessage(), mes.getLength());
        if ((command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_OFF) || (command == MidiByte.Status.Channel.ChannelVoice.Fst.NOTE_ON && data2 <= 0)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isProgramChange(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        if (command == MidiByte.Status.Channel.ChannelVoice.Fst.PROGRAM_CHANGE) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isPitchBend(MidiMessage mes) {
        if (mes.getLength() < 3) {
            return false;
        }

        int command = MidiByte.getCommand(mes.getMessage(), mes.getLength());
        if (command == MidiByte.Status.Channel.ChannelVoice.Fst.PITCH_BEND) {
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
        MetaMessage meta = new MetaMessage(MidiByte.SET_TEMPO.type, data, data.length);
        return meta;
    }

    @Override
    public MidiEvent createTempoEvent(long position, float bpm) throws InvalidMidiDataException {
        MidiMessage mes = createTempoMessage(bpm);
        MidiEvent event = new MidiEvent(mes, position);
        return event;
    }

}
