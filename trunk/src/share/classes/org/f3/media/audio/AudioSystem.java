package org.f3.media.audio;
import java.net.URL;
import java.io.InputStream;

public interface AudioSystem {
    public long getClock();
    public int getOutputRate();
    public Channel createChannel();
    public ChannelGroup createChannelGroup(String name);
    public Stream createStream(URL url);
    public Stream createStreamPlaylist(int frequency,
                                       int channels,
                                       int format,
                                       int size);
    public Sample createSample(URL url);
    public Sample createSample(InputStream is);
    public Sample createSamplePlaylist(int frequency,
                                       int channels,
                                       int format,
                                       int size);
    public Signal createSignal(SignalGenerator generator,
                               int bufferSize,
                               int length,
                               int channels,
                               int frequency,
                               int format);

    public ChannelGroup getRootChannelGroup();
    public void update();

    // User-defined
    public DSP createDSP(Processor processor);

    // Builtins:

    //This unit does nothing but take inputs and mix them together 
    //then feed the result to the soundcard unit.
    public DSP createMixer();
    
    //This unit generates sine/square/saw/triangle or noise tones.
    public DSP.Oscillator createOscillator();
    
    //This unit filters sound using a high quality, resonant lowpass filter 
    // algorithm but consumes more CPU time.
    public DSP createLowpass();
    
    //This unit filters sound using a resonant lowpass filter algorithm that 
    //is used in Impulse Tracker, but with limited cutoff range (0 to 8060hz).
    public DSP createITLowpass();

    //This unit filters sound using a resonant highpass filter algorithm.
    public DSP createHighpass();

    //This unit produces an echo on the sound and fades out at the desired 
    //rate.
    public DSP.Echo createEcho();

    //This unit produces a flange effect on the sound.
    public DSP createFlange();

    //This unit distorts the sound.
    public DSP.Distortion createDistortion();

    //This unit normalizes or amplifies the sound to a certain level.
    public DSP createNormalize();

    //This unit attenuates or amplifies a selected frequency range.
    public DSP createParamEQ();

    //This unit bends the pitch of a sound without changing the speed of 
    // playback.
    public DSP createPitchShift();

    //This unit produces a chorus effect on the sound.
    public DSP createChorus();

    //This unit produces a reverb effect on the sound.
    public DSP.Reverb createReverb();

    //This unit produces an echo on the sound and fades out at the desired 
    //rate as is used in Impulse Tracker.
    public DSP createITEcho();

    //This unit implements dynamic compression (linked multichannel, wideband)
    public DSP createCompressor();

    //This unit implements SFX reverb
    public DSP.SFXReverb createSFXReverb();

    //This unit filters sound using a simple lowpass with no resonance, 
    //but has flexible cutoff and is fast. 
    public DSP createLowpassSimple();

    // 3D Listener

    public Listener getListener();

}
