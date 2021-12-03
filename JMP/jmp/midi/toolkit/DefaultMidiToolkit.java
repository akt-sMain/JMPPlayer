package jmp.midi.toolkit;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;

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
    public MidiDevice getMidiDevice(Info info) throws MidiUnavailableException {
        return MidiSystem.getMidiDevice(info);
    }

    @Override
    public MidiDevice getMidiDevice(String name) throws MidiUnavailableException {
        MidiDevice dev = null;
        MidiDevice.Info recInfo = null;
        for (MidiDevice.Info info : getMidiDeviceInfo(true, true)) {
            if (info.getName().equals(name) == true) {
                recInfo = info;
                break;
            }
        }
        if (recInfo != null) {
            dev = MidiSystem.getMidiDevice(recInfo);
        }
        else {
            throw new MidiUnavailableException();
        }
        return dev;
    }

    @Override
    public MidiMessage createByteMidiMessage(byte[] data) {
        return new MidiByteMessage(data);
    }

    @Override
    public MidiMessage createSysexBeginMessage(byte[] data, int length) throws InvalidMidiDataException {
        return new SysexMessage(MidiByte.Status.System.SystemCommon.Fst.SYSEX_BEGIN, data, length);
    }

    @Override
    public MidiMessage createSysexEndMessage(byte[] data, int length) throws InvalidMidiDataException {
        return new SysexMessage(MidiByte.Status.System.SystemCommon.Fst.SYSEX_END, data, length);
    }

    @Override
    public MidiMessage createShortMessage(int command, int channel, int data1, int data2) throws InvalidMidiDataException {
        return new ShortMessage(command, channel, data1, data2);
    }

    @Override
    public MidiMessage createMetaMessage(int type, byte[] data, int length) throws InvalidMidiDataException {
        return new MetaMessage(type, data, length);
    }

    @Override
    public MidiEvent createMidiEvent(MidiMessage mes, long position) throws InvalidMidiDataException {
        return new MidiEvent(mes, position);
    }
}
