/*===============================================================================================
Pitch detection example.
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example combines recording with spectrum analysis to determine the pitch of the sound 
being recorded.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENUSER;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_REUSE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_FFT_WINDOW.FMOD_DSP_FFT_WINDOW_TRIANGLE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_RESAMPLER.FMOD_DSP_RESAMPLER_LINEAR;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_ALSA;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_ASIO;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_COREAUDIO;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_DSOUND;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_ESD;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_NOSOUND;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_OSS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_SOUNDMANAGER;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_WINMM;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCM16;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newFloatBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_SHORT;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.swing.JPanel;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Defines.PLATFORMS;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Examples.Utils.ConsoleGUI;
import org.jouvieje.FmodEx.Examples.Utils.FmodExExampleFrame;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.Structures.FMOD_CREATESOUNDEXINFO;

/**
 * I've ported the C++ FMOD Ex example to NativeFmodEx.
 * 
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * Site :
 * 		http://jerome.jouvie.free.fr/
 */
public class PitchDetection extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new PitchDetection());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound  sound  = new Sound();

	private final static String[] note = new String[] {
		"C 0", "C#0", "D 0", "D#0", "E 0", "F 0", "F#0", "G 0", "G#0", "A 0", "A#0", "B 0",  
		"C 1", "C#1", "D 1", "D#1", "E 1", "F 1", "F#1", "G 1", "G#1", "A 1", "A#1", "B 1",  
		"C 2", "C#2", "D 2", "D#2", "E 2", "F 2", "F#2", "G 2", "G#2", "A 2", "A#2", "B 2",  
		"C 3", "C#3", "D 3", "D#3", "E 3", "F 3", "F#3", "G 3", "G#3", "A 3", "A#3", "B 3",  
		"C 4", "C#4", "D 4", "D#4", "E 4", "F 4", "F#4", "G 4", "G#4", "A 4", "A#4", "B 4",  
		"C 5", "C#5", "D 5", "D#5", "E 5", "F 5", "F#5", "G 5", "G#5", "A 5", "A#5", "B 5",  
		"C 6", "C#6", "D 6", "D#6", "E 6", "F 6", "F#6", "G 6", "G#6", "A 6", "A#6", "B 6",  
		"C 7", "C#7", "D 7", "D#7", "E 7", "F 7", "F#7", "G 7", "G#7", "A 7", "A#7", "B 7",  
		"C 8", "C#8", "D 8", "D#8", "E 8", "F 8", "F#8", "G 8", "G#8", "A 8", "A#8", "B 8",  
		"C 9", "C#9", "D 9", "D#9", "E 9", "F 9", "F#9", "G 9", "G#9", "A 9", "A#9", "B 9"
	};
	
	private final static float[] notefreq = new float[] {
		16.35f,   17.32f,   18.35f,   19.45f,    20.60f,    21.83f,    23.12f,    24.50f,    25.96f,    27.50f,    29.14f,    30.87f, 
		32.70f,   34.65f,   36.71f,   38.89f,    41.20f,    43.65f,    46.25f,    49.00f,    51.91f,    55.00f,    58.27f,    61.74f, 
		65.41f,   69.30f,   73.42f,   77.78f,    82.41f,    87.31f,    92.50f,    98.00f,   103.83f,   110.00f,   116.54f,   123.47f, 
		130.81f,  138.59f,  146.83f,  155.56f,   164.81f,   174.61f,   185.00f,   196.00f,   207.65f,   220.00f,   233.08f,   246.94f, 
		261.63f,  277.18f,  293.66f,  311.13f,   329.63f,   349.23f,   369.99f,   392.00f,   415.30f,   440.00f,   466.16f,   493.88f, 
		523.25f,  554.37f,  587.33f,  622.25f,   659.26f,   698.46f,   739.99f,   783.99f,   830.61f,   880.00f,   932.33f,   987.77f, 
		1046.50f, 1108.73f, 1174.66f, 1244.51f,  1318.51f,  1396.91f,  1479.98f,  1567.98f,  1661.22f,  1760.00f,  1864.66f,  1975.53f, 
		2093.00f, 2217.46f, 2349.32f, 2489.02f,  2637.02f,  2793.83f,  2959.96f,  3135.96f,  3322.44f,  3520.00f,  3729.31f,  3951.07f, 
		4186.01f, 4434.92f, 4698.64f, 4978.03f,  5274.04f,  5587.65f,  5919.91f,  6271.92f,  6644.87f,  7040.00f,  7458.62f,  7902.13f, 
		8372.01f, 8869.84f, 9397.27f, 9956.06f, 10548.08f, 11175.30f, 11839.82f, 12543.85f, 13289.75f, 14080.00f, 14917.24f, 15804.26f
	};
	
	private final int OUTPUTRATE = 48000;
	private final int SPECTRUMSIZE = 8192;
	private final float SPECTRUMRANGE = OUTPUTRATE / 2.0f;		/* 0 to nyquist */
	private final float BINSIZE = SPECTRUMRANGE / (float)SPECTRUMSIZE;
	
	private FloatBuffer spectrum = newFloatBuffer(SPECTRUMSIZE);
	
	public PitchDetection()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex PitchDetection example."; }
	
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_RESULT.FMOD_OK)
		{
			printfExit("FMOD error! (%d) %s\n", result.asInt(), FmodEx.FMOD_ErrorString(result));
		}
	}
	
	public void initFmod()
	{
		/*
		 * NativeFmodEx Init
		 */
		try
		{
			Init.loadLibraries(INIT_MODES.INIT_FMOD_EX/*_MINIMUM*/);
		}
		catch(InitException e)
		{
			printfExit("NativeFmodEx error! %s\n", e.getMessage());
			return;
		}
		
		/*
		 * Checking NativeFmodEx version
		 */
		if(NATIVEFMODEX_LIBRARY_VERSION != NATIVEFMODEX_JAR_VERSION)
		{
			printfExit("Error!  NativeFmodEx library version (%08x) is different to jar version (%08x)\n", NATIVEFMODEX_LIBRARY_VERSION, NATIVEFMODEX_JAR_VERSION);
			return;
		}
		
		/*==================================================*/
	    
		init = true;
	}

	public void run()
	{
		if(!init) return;
		
		Channel channel = new Channel();
		FMOD_RESULT result;
		int numdrivers, bin;
		int version;    
		
		ByteBuffer buffer = newByteBuffer(256);
		
		/*
		 * Create a System object and initialize.
		 */
		result = FmodEx.System_Create(system);
		ErrorCheck(result);
		
		result = system.getVersion(buffer.asIntBuffer());
		ErrorCheck(result);
		version = buffer.getInt(0);
		
		if(version < FMOD_VERSION)
		{
			printfExit("Error!  You are using an old version of FMOD %08x.  This program requires %08x\n", version, FMOD_VERSION);
			return;
		}
		
		/* 
		 * System initialization
		 */
		printf("---------------------------------------------------------\n");    
		printf("Select OUTPUT type\n");    
		printf("---------------------------------------------------------\n");
		if(FmodEx.getPlatform() == PLATFORMS.WIN32 || FmodEx.getPlatform() == PLATFORMS.WIN64)
		{
			printf("1 :  DirectSound\n");
			printf("2 :  Windows Multimedia WaveOut\n");
			printf("3 :  ASIO\n");
		}
		else if(FmodEx.getPlatform() == PLATFORMS.LINUX || FmodEx.getPlatform() == PLATFORMS.LINUX64)
		{
			printf("1 :  OSS  - Open Sound System\n");
			printf("2 :  ALSA - Advanced Linux Sound Architecture\n");
			printf("3 :  ESD  - Enlightenment Sound Daemon\n");
		}
		else if(FmodEx.getPlatform() == PLATFORMS.MAC)
		{
			printf("1 :  Macintosh SoundManager\n");
			printf("2 :  Macintosh CoreAudio\n");
			printf("3 :  No Sound\n");
		}
		printf("---------------------------------------------------------\n");
		printf("Press a corresponding number or E to quit\n");
		
		int output = -1;
		while(output < '1' || output > '3')
		{
			output = getKey();
			Thread.yield();
		}
		
		if(FmodEx.getPlatform() == PLATFORMS.WIN32 || FmodEx.getPlatform() == PLATFORMS.WIN64)
		{
			switch(output)
			{
				case '1': result = system.setOutput(FMOD_OUTPUTTYPE_DSOUND); break;
				case '2': result = system.setOutput(FMOD_OUTPUTTYPE_WINMM); break;
				case '3': result = system.setOutput(FMOD_OUTPUTTYPE_ASIO); break;
			}  
		}
		else if(FmodEx.getPlatform() == PLATFORMS.LINUX || FmodEx.getPlatform() == PLATFORMS.LINUX64)
		{
			switch(output)
			{
				case '1': result = system.setOutput(FMOD_OUTPUTTYPE_OSS); break;
				case '2': result = system.setOutput(FMOD_OUTPUTTYPE_ALSA); break;
				case '3': result = system.setOutput(FMOD_OUTPUTTYPE_ESD); break;
			}  
		}
		else if(FmodEx.getPlatform() == PLATFORMS.MAC)
		{
			switch(output)
			{
				case '1': result = system.setOutput(FMOD_OUTPUTTYPE_SOUNDMANAGER); break;
				case '2': result = system.setOutput(FMOD_OUTPUTTYPE_COREAUDIO); break;
				case '3': result = system.setOutput(FMOD_OUTPUTTYPE_NOSOUND); break;
			}  
		}
		ErrorCheck(result);
		
		/*
		 * Enumerate playback devices
		 */
		
		result = system.getNumDrivers(buffer.asIntBuffer());
		ErrorCheck(result);
		numdrivers = buffer.getInt(0);
		
		printf("---------------------------------------------------------\n");    
		printf("Choose a PLAYBACK driver\n");
		printf("---------------------------------------------------------\n");    
		for(int i = 0; i < numdrivers; i++)
		{
			result = system.getDriverInfo(i, buffer, buffer.capacity(), null);
			ErrorCheck(result);
			String name = BufferUtils.toString(buffer);
			
			printf("%d : %s\n", i + 1, name);
		}
		printf("---------------------------------------------------------\n");
		printf("Press a corresponding number or E to quit\n");
		
		int driver = -1;
		while(driver < 0 || driver >= numdrivers)
		{
			try {
				driver = Integer.parseInt(""+getKey())-1;
			} catch(NumberFormatException e) {
				driver = -1;
			}
			Thread.yield();
		}
		
		result = system.setDriver(driver);
		ErrorCheck(result);
		
		/*
		 * Enumerate record devices
		 */
		
		result = system.getRecordNumDrivers(buffer.asIntBuffer());
		ErrorCheck(result);
		numdrivers = buffer.getInt(0);
		
		printf("---------------------------------------------------------\n");    
		printf("Choose a RECORD driver\n");
		printf("---------------------------------------------------------\n");    
		for(int i = 0; i < numdrivers; i++)
		{
			result = system.getRecordDriverInfo(i, buffer, buffer.capacity(), null);
			ErrorCheck(result);
			String name = BufferUtils.toString(buffer);
			
			printf("%d : %s\n", i+1, name);
		}
		printf("---------------------------------------------------------\n");
		printf("Press a corresponding number or E to quit\n\n");
		
		int recordDriver = -1;
		while(recordDriver < 0 || recordDriver >= numdrivers)
		{
			try {
				recordDriver = Integer.parseInt(""+getKey())-1;
			} catch(NumberFormatException e) {
				recordDriver = -1;
			}
			Thread.yield();
		}
		
		result = system.setRecordDriver(recordDriver);
		ErrorCheck(result);
		
		result = system.setSoftwareFormat(OUTPUTRATE, FMOD_SOUND_FORMAT_PCM16, 1, 0, FMOD_DSP_RESAMPLER_LINEAR);
		ErrorCheck(result);
		
		result = system.init(32, FMOD_INIT_NORMAL, null);
		ErrorCheck(result);
		
		/*
		 * Create a sound to record to.
		 */
		FMOD_CREATESOUNDEXINFO exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setNumChannels(1);
		exinfo.setFormat(FMOD_SOUND_FORMAT_PCM16);
		exinfo.setDefaultFrequency(OUTPUTRATE);
		exinfo.setLength(exinfo.getDefaultFrequency() * SIZEOF_SHORT * exinfo.getNumChannels() * 5);
		
		result = system.createSound((String)null, FMOD_2D | FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENUSER, exinfo, sound);
		ErrorCheck(result);
		
		exinfo.release();
		
		/*
		 * Start the interface
		 */
		printf("=========================================================================\n");
		printf("Pitch detection example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("=========================================================================\n");
		printf("\n");
		printf("Record something through the selected recording device and FMOD will\n");
		printf("Determine the pitch.  Sustain the tone for at least a second to get an\n");
		printf("accurate reading.\n");
		printf("Press 'E' to quit\n");
		printf("\n");
		
		result = system.recordStart(sound, true);
		ErrorCheck(result);
		
		try {
			Thread.sleep(200);      /* Give it some time to record something */
		} catch(InterruptedException e1){}
		
		result = system.playSound(FMOD_CHANNEL_REUSE, sound, false, channel);
		ErrorCheck(result);
		
		/* Dont hear what is being recorded otherwise it will feedback.  Spectrum analysis is done before volume scaling in the DSP chain */
		result = channel.setVolume(0);
		ErrorCheck(result);
		
		bin = 0;
		
		/*
		 * Main loop.
		 */
		resetInput();
		do
		{
			float dominanthz = 0;
			float max;
			int   dominantnote = 0;
			
			result = channel.getSpectrum(spectrum, SPECTRUMSIZE, 0, FMOD_DSP_FFT_WINDOW_TRIANGLE);
			ErrorCheck(result);
			
			max = 0;
			
			for(int i = 0; i < SPECTRUMSIZE; i++)
			{
				if(spectrum.get(i) > 0.01f && spectrum.get(i) > max)
				{
					max = spectrum.get(i);
					bin = i;
				}
			}
			
			dominanthz = bin * BINSIZE;       /* dominant frequency min */
			
			dominantnote = 0;
			for(int i = 0; i < 120; i++)
			{
				if(dominanthz >= notefreq[i] && dominanthz < notefreq[i + 1])
				{
					/* which is it closer to.  This note or the next note */
					if(Math.abs(dominanthz - notefreq[i]) < Math.abs(dominanthz - notefreq[i+1]))
						dominantnote = i;
					else
						dominantnote = i + 1;
					break;
				}
			}
			
			printfr("Detected rate : %7.1f . %7.1f hz.  Detected musical note. %-3s (%7.1f hz)",
					dominanthz, (bin + 0.99f) * BINSIZE, note[dominantnote], notefreq[dominantnote]);
			
			system.update();
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e2){}
		}
		while(!keyHit() && !deinit);

		stop();
	}
	
	public void stop()
	{
		if(!init || deinit) return;
		deinit = true;
		
		print("\n");
		
		/*
		 * Shut down
		 */
		FMOD_RESULT result;
		if(!sound.isNull()) {
			result = sound.release();
			ErrorCheck(result);
		}
		
		if(!system.isNull()) {
//			result = system.close();
//			ErrorCheck(result);
			result = system.release();
			ErrorCheck(result);
		}
		
		printExit("Shutdown\n");
	}
}