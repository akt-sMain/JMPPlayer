package jmsynth.midi;

import jmsynth.oscillator.IOscillator;
import jmsynth.oscillator.OscillatorFactory;
import jmsynth.oscillator.OscillatorSet;

public class DefaultProgramChangeTable extends ProgramChangeTable {
    
    private static IOscillator createOsc(OscillatorFactory ofc, String name) {
        return ofc.createOscillator(name);
    }

    public DefaultProgramChangeTable() {
        super();
        
        OscillatorFactory ofc = new OscillatorFactory();

        // GS準拠

        /*
         * Piano
         */
        this.sets[PC_1] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_2] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_3] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_4] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_5] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_6] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_7] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_8] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
//        this.sets[PC_1] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
//        this.sets[PC_2] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
//        this.sets[PC_3] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
//        this.sets[PC_4] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
//        this.sets[PC_5] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
//        this.sets[PC_6] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
//        this.sets[PC_7] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
//        this.sets[PC_8] = new OscillatorSet(0.0, 1.0, 0.0, 0.0, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));

        /*
         * Chromatic Percussion
         */
        this.sets[PC_9] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_10] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_11] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_12] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_13] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_14] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_15] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_16] = new OscillatorSet(0.04, 0.15, 0.5, 0.2, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));

        /*
         * Organ
         */
        this.sets[PC_17] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_18] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_19] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_20] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_21] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_22] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_23] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_24] = new OscillatorSet(0.05, 0.0, 1.0, 0.05, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));

        /*
         * Guitar
         */
        this.sets[PC_25] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_26] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_27] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_28] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_29] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_30] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_31] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_32] = new OscillatorSet(0.05, 0.2, 0.65, 0.3, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));

        /*
         * Bass
         */
        this.sets[PC_33] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
        this.sets[PC_34] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
        this.sets[PC_35] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
        this.sets[PC_36] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
        this.sets[PC_37] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
        this.sets[PC_38] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
        this.sets[PC_39] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
        this.sets[PC_40] = new OscillatorSet(0.0, 0.67, 0.2, 0.2, 1000, 3000, 1000, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));

        /*
         * Strings
         */
        this.sets[PC_41] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_42] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_43] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_44] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_45] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_46] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_47] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_48] = new OscillatorSet(0.04, 0.0, 1.0, 0.1, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));

        /*
         * Ensemble
         */
        this.sets[PC_49] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_50] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_51] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_52] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_53] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_54] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_55] = new OscillatorSet(0.1, 0.0, 1.0, 0.25, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        this.sets[PC_56] = new OscillatorSet(0.0, 0.4, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));

        /*
         * Brass
         */
        this.sets[PC_57] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_58] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_59] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_60] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_61] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_62] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_63] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_64] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));

        /*
         * Reed
         */
        this.sets[PC_65] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_66] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_67] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_68] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_69] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_70] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_71] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_72] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));

        /*
         * Pipe
         */
        this.sets[PC_73] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE125));
        this.sets[PC_74] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE125));
        this.sets[PC_75] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE125));
        this.sets[PC_76] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE125));
        this.sets[PC_77] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE125));
        this.sets[PC_78] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE125));
        this.sets[PC_79] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE125));
        this.sets[PC_80] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE125));

        /*
         * Synth Lead
         */
        // Square Wave
        this.sets[PC_81] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SQUARE));
        // Saw Wave
        this.sets[PC_82] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        // Syn Calliope
        this.sets[PC_83] = new OscillatorSet(0.1, 0.0, 1.0, 0.1, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
        // Chiffer Lead
        this.sets[PC_84] = new OscillatorSet(0.25, 0.0, 1.0, 0.25, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_TRIANGLE));
        // Charang
        this.sets[PC_85] = new OscillatorSet(0.1, 0.0, 1.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        // Solo Vox
        this.sets[PC_86] = new OscillatorSet(0.1, 0.0, 1.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        // 7th Saw
        this.sets[PC_87] = new OscillatorSet(0.1, 0.0, 1.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SAW));
        // Bass & Lead
        this.sets[PC_88] = new OscillatorSet(0.0, 0.0, 1.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));

        /*
         * Pipe
         */
        // Fantasia
        this.sets[PC_89] = new OscillatorSet(0.0, 1.0, 0.25, 0.5, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_90] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_91] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_92] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_93] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_94] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_95] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));
        this.sets[PC_96] = new OscillatorSet(0.15, 0.75, 0.25, 0.5, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_SINE));

        /*
         * Synth Effects
         */
        this.sets[PC_97] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_98] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_99] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_100] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_101] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_102] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_103] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_104] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));

        /*
         * ethnic
         */
        this.sets[PC_105] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_106] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_107] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_108] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_109] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_110] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_111] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_112] = new OscillatorSet(createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));

        /*
         * Percussive
         */
        this.sets[PC_113] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_114] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_115] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_116] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_117] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_118] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_119] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));
        this.sets[PC_120] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_PULSE25));

        /*
         * Sound effects
         */
        this.sets[PC_121] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_122] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_123] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_124] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_125] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_126] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_127] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
        this.sets[PC_128] = new OscillatorSet(0.0, 0.25, 0.0, 0.0, createOsc(ofc, OscillatorFactory.OSCILLATOR_NAME_NOISE_L));
    }

}
