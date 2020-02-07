package jmp.midi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import jlib.manager.PlayerAccessor;
import jmp.JMPCore;
import jmp.SoundManager;

public class JMPSequencer implements Sequencer {

    private Sequencer abstractSequenser = null;

    public JMPSequencer(Sequencer abstractSequenser) {
	this.abstractSequenser = abstractSequenser;
    }

    @Override
    public Info getDeviceInfo() {
	return abstractSequenser.getDeviceInfo();
    }

    @Override
    public void open() throws MidiUnavailableException {
	abstractSequenser.open();
    }

    @Override
    public void close() {
	abstractSequenser.close();
    }

    @Override
    public boolean isOpen() {
	return abstractSequenser.isOpen();
    }

    @Override
    public int getMaxReceivers() {
	return abstractSequenser.getMaxReceivers();
    }

    @Override
    public int getMaxTransmitters() {
	return abstractSequenser.getMaxTransmitters();
    }

    @Override
    public Receiver getReceiver() throws MidiUnavailableException {
	return abstractSequenser.getReceiver();
    }

    @Override
    public List<Receiver> getReceivers() {
	return abstractSequenser.getReceivers();
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
	return abstractSequenser.getTransmitter();
    }

    @Override
    public List<Transmitter> getTransmitters() {
	return abstractSequenser.getTransmitters();
    }

    @Override
    public void setSequence(Sequence sequence) throws InvalidMidiDataException {
	abstractSequenser.setSequence(sequence);
    }

    @Override
    public void setSequence(InputStream stream) throws IOException, InvalidMidiDataException {
	abstractSequenser.setSequence(stream);
    }

    @Override
    public Sequence getSequence() {
	return abstractSequenser.getSequence();
    }

    @Override
    public void start() {
	if (getSequence() == null) {
	    // 念のためNULLチェック
	    return;
	}

	// MidiPlayerに変更する
	if (PlayerAccessor.getInstance().getCurrent() != SoundManager.MidiPlayer) {
	    PlayerAccessor.getInstance().getCurrent().stop();
	    PlayerAccessor.getInstance().change(SoundManager.MidiPlayer);
	}

	abstractSequenser.start();

	JMPCore.getPluginManager().startSequencer();
    }

    @Override
    public void stop() {
	abstractSequenser.stop();

	JMPCore.getPluginManager().stopSequencer();
    }

    @Override
    public boolean isRunning() {
	return abstractSequenser.isRunning();
    }

    @Override
    public void startRecording() {
	abstractSequenser.startRecording();
    }

    @Override
    public void stopRecording() {
	abstractSequenser.stopRecording();
    }

    @Override
    public boolean isRecording() {
	return abstractSequenser.isRecording();
    }

    @Override
    public void recordEnable(Track track, int channel) {
	abstractSequenser.recordEnable(track, channel);
    }

    @Override
    public void recordDisable(Track track) {
	abstractSequenser.recordDisable(track);
    }

    @Override
    public float getTempoInBPM() {
	return abstractSequenser.getTempoInBPM();
    }

    @Override
    public void setTempoInBPM(float bpm) {
	abstractSequenser.setTempoInBPM(bpm);
    }

    @Override
    public float getTempoInMPQ() {
	return abstractSequenser.getTempoInMPQ();
    }

    @Override
    public void setTempoInMPQ(float mpq) {
	abstractSequenser.setTempoInMPQ(mpq);
    }

    @Override
    public void setTempoFactor(float factor) {
	abstractSequenser.setTempoFactor(factor);
    }

    @Override
    public float getTempoFactor() {
	return abstractSequenser.getTempoFactor();
    }

    @Override
    public long getTickLength() {
	return abstractSequenser.getTickLength();
    }

    @Override
    public long getTickPosition() {
	return abstractSequenser.getTickPosition();
    }

    @Override
    public void setTickPosition(long tick) {
	long before = abstractSequenser.getTickPosition();

	abstractSequenser.setTickPosition(tick);

	JMPCore.getPluginManager().updateTickPosition(before, abstractSequenser.getTickPosition());
    }

    @Override
    public long getMicrosecondLength() {
	return abstractSequenser.getMicrosecondLength();
    }

    @Override
    public long getMicrosecondPosition() {
	return abstractSequenser.getMicrosecondPosition();
    }

    @Override
    public void setMicrosecondPosition(long microseconds) {
	abstractSequenser.setMicrosecondPosition(microseconds);
    }

    @Override
    public void setMasterSyncMode(SyncMode sync) {
	abstractSequenser.setMasterSyncMode(sync);
    }

    @Override
    public SyncMode getMasterSyncMode() {
	return abstractSequenser.getMasterSyncMode();
    }

    @Override
    public SyncMode[] getMasterSyncModes() {
	return abstractSequenser.getMasterSyncModes();
    }

    @Override
    public void setSlaveSyncMode(SyncMode sync) {
	abstractSequenser.setSlaveSyncMode(sync);
    }

    @Override
    public SyncMode getSlaveSyncMode() {
	return abstractSequenser.getSlaveSyncMode();
    }

    @Override
    public SyncMode[] getSlaveSyncModes() {
	return abstractSequenser.getSlaveSyncModes();
    }

    @Override
    public void setTrackMute(int track, boolean mute) {
	abstractSequenser.setTrackMute(track, mute);
    }

    @Override
    public boolean getTrackMute(int track) {
	return abstractSequenser.getTrackMute(track);
    }

    @Override
    public void setTrackSolo(int track, boolean solo) {
	abstractSequenser.setTrackSolo(track, solo);
    }

    @Override
    public boolean getTrackSolo(int track) {
	return abstractSequenser.getTrackSolo(track);
    }

    @Override
    public boolean addMetaEventListener(MetaEventListener listener) {
	return abstractSequenser.addMetaEventListener(listener);
    }

    @Override
    public void removeMetaEventListener(MetaEventListener listener) {
	abstractSequenser.removeMetaEventListener(listener);
    }

    @Override
    public int[] addControllerEventListener(ControllerEventListener listener, int[] controllers) {
	return abstractSequenser.addControllerEventListener(listener, controllers);
    }

    @Override
    public int[] removeControllerEventListener(ControllerEventListener listener, int[] controllers) {
	return abstractSequenser.removeControllerEventListener(listener, controllers);
    }

    @Override
    public void setLoopStartPoint(long tick) {
	abstractSequenser.setLoopStartPoint(tick);
    }

    @Override
    public long getLoopStartPoint() {
	return abstractSequenser.getLoopStartPoint();
    }

    @Override
    public void setLoopEndPoint(long tick) {
	abstractSequenser.setLoopEndPoint(tick);
    }

    @Override
    public long getLoopEndPoint() {
	return abstractSequenser.getLoopEndPoint();
    }

    @Override
    public void setLoopCount(int count) {
	abstractSequenser.setLoopCount(count);
    }

    @Override
    public int getLoopCount() {
	return abstractSequenser.getLoopCount();
    }

}
