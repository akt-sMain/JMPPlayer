package jmsynth;

import jmsynth.midi.MidiInterface;

public class JMSynthEngine {

    /**
     * JMSynthインスタンスを取得
     *
     * @param isOpen true:open
     * @return
     */
    public static JMSoftSynthesizer getSoftSynthesizer(boolean isOpen) {
        JMSoftSynthesizer synth = new JMSoftSynthesizer();
        if (isOpen == true) {
            synth.openDevice();
        }
        return synth;
    }

    /**
     * JMSynthインスタンスを取得
     *
     * @return
     */
    public static JMSoftSynthesizer getSoftSynthesizer() {
        return getSoftSynthesizer(true);
    }

    /**
     * MIDIインターフェースでラップしたJMSynthインスタンスを取得
     *
     * @param isOpen true:open
     * @return
     */
    public static MidiInterface getMidiInterface(boolean isOpen) {
        JMSoftSynthesizer synth = getSoftSynthesizer(isOpen);
        return new MidiInterface(synth);
    }

    /**
     * MIDIインターフェースでラップしたJMSynthインスタンスを取得
     *
     * @return
     */
    public static MidiInterface getMidiInterface() {
        return getMidiInterface(true);
    }

}
