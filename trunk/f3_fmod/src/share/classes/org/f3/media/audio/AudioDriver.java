package org.f3.media.audio;

public interface AudioDriver {

    static final int CAPS_NONE  = 0x00000000;
    static final int CAPS_HARDWARE  = 0x00000001;
    static final int CAPS_HARDWARE_EMULATED  = 0x00000002;
    static final int CAPS_OUTPUT_MULTICHANNEL  = 0x00000004;
    static final int CAPS_OUTPUT_FORMAT_PCM8  = 0x00000008;
    static final int CAPS_OUTPUT_FORMAT_PCM16  = 0x00000010;
    static final int CAPS_OUTPUT_FORMAT_PCM24  = 0x00000020;
    static final int CAPS_OUTPUT_FORMAT_PCM32  = 0x00000040;
    static final int CAPS_OUTPUT_FORMAT_PCMFLOAT  = 0x00000080;
    static final int CAPS_REVERB_EAX2  = 0x00000100;
    static final int CAPS_REVERB_EAX3  = 0x00000200;
    static final int CAPS_REVERB_EAX4  = 0x00000400;
    static final int CAPS_REVERB_EAX5  = 0x00000800;
    static final int CAPS_REVERB_I3DL2  = 0x00001000;
    static final int CAPS_REVERB_LIMITED  = 0x00002000;


    public String getName();
    public int getMinimumFrequency();
    public int getMaximumFrequency();
    public int getCapabilities();

    // Get the audio system that outputs to this driver
    public AudioSystem createAudioSystem();

}
