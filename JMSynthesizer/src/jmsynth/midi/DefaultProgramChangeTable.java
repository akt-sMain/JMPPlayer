package jmsynth.midi;

import jmsynth.oscillator.OscillatorSet;
import jmsynth.oscillator.OscillatorSet.WaveType;

public class DefaultProgramChangeTable extends ProgramChangeTable {

    public DefaultProgramChangeTable() {
        super();

        // GS準拠
        int pc;

        /*
         * Piano
         */
        pc = 0;
        // Piano 1
        this.sets[pc] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, WaveType.SINE);
        pc++;
        // Piano 2
        this.sets[pc] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, WaveType.SINE);
        pc++;
        // Piano 3
        this.sets[pc] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, WaveType.SINE);
        pc++;
        // Piano 4
        this.sets[pc] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, WaveType.SINE);
        pc++;
        // E.Piano 1
        this.sets[pc] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, WaveType.SINE);
        pc++;
        // E.Piano 2
        this.sets[pc] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, WaveType.SINE);
        pc++;
        // Harpsichord
        this.sets[pc] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, WaveType.SINE);
        pc++;
        // Clav.
        this.sets[pc] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, WaveType.SINE);
        pc++;

        /*
         * Chromatic Percussion
         */
        pc = 8;
        this.sets[pc] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, WaveType.SINE);
        pc++;

        /*
         * Organ
         */
        pc = 16;
        this.sets[pc] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, WaveType.SINE);
        pc++;

        /*
         * Guitar
         */
        pc = 24;
        this.sets[pc] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, WaveType.SAW);
        pc++;

        /*
         * Bass
         */
        pc = 32;
        this.sets[pc] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, WaveType.TRIANGLE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, WaveType.TRIANGLE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, WaveType.TRIANGLE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, WaveType.TRIANGLE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, WaveType.TRIANGLE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, WaveType.TRIANGLE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, WaveType.TRIANGLE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, WaveType.TRIANGLE);
        pc++;

        /*
         * Strings
         */
        pc = 40;
        this.sets[pc] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, WaveType.SAW);
        pc++;

        /*
         * Ensemble
         */
        pc = 48;
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, WaveType.SAW);
        pc++;
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, WaveType.SAW);
        pc++;

        /*
         * Brass
         */
        pc = 56;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;

        /*
         * Reed
         */
        pc = 64;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;

        /*
         * Pipe
         */
        pc = 72;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;

        /*
         * Synth Lead
         */
        pc = 80;
        // Square Wave
        this.sets[pc] = new OscillatorSet(WaveType.SQUARE);
        pc++;
        // Saw Wave
        this.sets[pc] = new OscillatorSet(WaveType.SAW);
        pc++;
        // Syn Calliope
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.1, WaveType.TRIANGLE);
        pc++;
        // Chiffer Lead
        this.sets[pc] = new OscillatorSet(0.25, 0.0, 1.0, 0.25, WaveType.TRIANGLE);
        pc++;
        // Charang
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.0, WaveType.PULSE);
        pc++;
        // Solo Vox
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.0, WaveType.SAW);
        pc++;
        // 7th Saw
        this.sets[pc] = new OscillatorSet(0.1, 0.0, 1.0, 0.0, WaveType.SAW);
        pc++;
        // Bass & Lead
        this.sets[pc] = new OscillatorSet(0.0, 0.0, 1.0, 0.0, WaveType.SAW);
        pc++;

        /*
         * Pipe
         */
        pc = 88;
        // Fantasia
        this.sets[pc] = new OscillatorSet(0.0, 1.0, 0.25, 0.5, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, WaveType.SINE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, WaveType.SINE);
        pc++;

        /*
         * Synth Effects
         */
        pc = 96;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;

        /*
         * ethnic
         */
        pc = 104;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(WaveType.PULSE);
        pc++;

        /*
         * Percussive
         */
        pc = 112;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.PULSE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.PULSE);
        pc++;

        /*
         * Sound effects
         */
        pc = 120;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
        this.sets[pc] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, WaveType.NOISE);
        pc++;
    }

}
