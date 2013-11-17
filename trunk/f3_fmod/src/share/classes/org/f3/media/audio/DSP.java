package org.f3.media.audio;
import java.util.List;

public interface DSP {

    public void addInput(DSP input);
    public void disconnectFrom(DSP dsp);
    public void disconnect();
    public void reset();

    public DSP[] getInputs();
    public DSP[] getOutputs();

    public void setActive(boolean value);
    public boolean isActive();

    public void setBypass(boolean value);
    public boolean isBypass();

    public interface Echo extends DSP {

        // default 0
        public int getMaxChannels();
        public void setMaxChannels(int value);

        // default 500
        public int getDelay();
        public void setDelay(int value);

        // default 0.5
        public float getDecayRatio();
        public void setDecayRatio(float value);

        //default 1
        public float getDryMix();
        public void setDryMix(float value);
        
        // default 1
        public float getWetMix();
        public void setWetMix(float value);
    }


    public interface Oscillator extends DSP {
        public static final int SINE = 0;
        public static final int SQUARE = 1;
        public static final int SAWUP = 2;
        public static final int SAWDOWN = 3;
        public static final int TRIANGLE = 4;
        public static final int NOISE = 5;

        // Waveform type
        public void setType(int type);
        public int getType();

        //Frequency of the sinewave in hz. 1.0 to 22000.0. Default = 220.0. 
        public void setRate(float value);
        public float getRate();
    }

    public interface Distortion extends DSP {
        // 0.0..1.0, default 0.5
        public void setLevel(float value);
        public float getLevel();
    }

    public interface Reverb extends DSP {
        // 0.0..1.0, default 0.5
        public float getDamp();
        public void setDamp(float value);

        // 0.0..1.0, default 0.33
        public float getWetMix();
        public void setWetMix(float value);

        // 0.0..1.0, default 1.0
        public float getDryMix();
        public void setDryMix(float value);

        // default false
        public void setFreeze(boolean value);
        public boolean getFreeze();
    }

    public interface SFXReverb extends DSP {

        // Dry level in mB
        // -10000.0..0.0, default 0.0
        public float getDryLevel();
        public void setDryLevel(float value);

        // Room effect level in mB
        // -10000.0..0.0, default 0.0
        public float getRoomEffectLevel();
        public void setRoomEffectLevel(float value);

        // Room effect High Frequency
        // Room effect level in mB
        // -10000.0..0.0, default 0.0
        public float getRoomEffectHighFrequencyLevel();
        public void setRoomEffectHighFrequencyLevel(float value);

        // 0.0..10.0, default: 10.0
        public float getRoomEffectRolloffFactor();
        public void setRoomEffectRolloffFactor(float value);

        // 0.1..20.0, default 1.0
        public float getDecayTime();
        public void setDecayTime(float value);

        // 0.1..2.0, default 0.5
        public float getDecayRatio();
        public void setDecayRatio(float value);

        // -10000.0..1000.0, default: -10000.0
        public float getReflectionsLevel();
        public void setReflectionsLevel(float value);

        // 0.0..0.3, default: 0.02
        public float getReflectionsDelay();
        public void setReflectionsDelay(float value);
        
        // Late reverberation level in mB
        // -10000.0..2000.0, default: 0.0
        public float getReverbLevel();
        public void setReverbLevel(float value);

        // Late reverberation delay in seconds
        // 0.0..0.1, default: 0.04
        public float getReverbDelay();
        public void setReverbDelay(float value);
        
        // diffusion (echo density) in percent
        // 0.0..100.0, default: 100.0
        public float getDiffusion();
        public void setDiffusion(float value);

        // modal density in percent
        // 0.0..100.0, default: 100.0
        public float getDensity();
        public void setDensity(float value);

        // Reference highg frequence in Hz
        // 20.0..20000.0, default: 5000.0
        public float getHighFrequencyReference();
        public void setHighFrequencyReference(float value);


        // Room effect Low Frequency
        // Room effect level in mB
        // -10000.0..0.0, default 0.0
        public float getRoomEffectLowFrequencyLevel();
        public void setRoomEffectLowFrequencyLevel(float value);

        // Reference low frequency in Hz
        // 20.0..1000.0, default: 250.0
        public float getLowFrequencyReference();
        public void setLowFrequencyReference(float value);

    }
    
}
