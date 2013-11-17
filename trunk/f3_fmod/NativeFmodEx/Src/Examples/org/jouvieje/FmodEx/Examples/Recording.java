/*===============================================================================================
 Record example
 Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

 This example shows how to record a sound, then write it to a wav file.
 It then shows how to play a sound while it is being recorded to.  Because it is recording, the
 sound playback has to be delayed a little bit so that the playback doesn't play part of the
 buffer that is still being written to.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_OFF;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENUSER;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_PCM;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_PCMBYTES;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.LINUX;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.LINUX64;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.MAC;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.WIN32;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.WIN64;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_REUSE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_ALSA;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_ASIO;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_COREAUDIO;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_DSOUND;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_ESD;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_NOSOUND;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_OSS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_SOUNDMANAGER;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_WASAPI;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_WINMM;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCM16;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_SHORT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import javax.swing.JPanel;

import org.jouvieje.FileFormat.WavFormat.DataChunk;
import org.jouvieje.FileFormat.WavFormat.FmtChunk;
import org.jouvieje.FileFormat.WavFormat.RiffChunk;
import org.jouvieje.FileFormat.WavFormat.WavHeader;
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
public class Recording extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new Recording());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system  = new System();
	private Sound sound	   = new Sound();
	
	private boolean looping = false;
	
	public Recording()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex Recording example."; }
	
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_RESULT.FMOD_OK)
		{
			printfExit("FMOD error! (%d) %s\n", result.asInt(), FmodEx.FMOD_ErrorString(result));
		}
	}
	
	/*
	 * Writes out the contents of a record buffer to a file.
	 */
	private static void saveToWav(Sound sound)
	{
		if(sound == null || sound.isNull())
			return;
		
		RandomAccessFile file;
		int channels, bits;
		float rate;
		int lenbytes;
		ByteBuffer[] ptr1 = new ByteBuffer[1];
		ByteBuffer[] ptr2 = new ByteBuffer[1];
		int len1, len2;
		
		ByteBuffer buffer1 = newByteBuffer(SIZEOF_INT);
		ByteBuffer buffer2 = newByteBuffer(SIZEOF_INT);
		
		sound.getFormat(null, null, buffer1.asIntBuffer(), buffer2.asIntBuffer());
		channels = buffer1.getInt(0);
	 	bits = buffer2.getInt(0);
		sound.getDefaults(buffer1.asFloatBuffer(), null, null, null);
		rate = buffer1.getFloat(0);
		sound.getLength(buffer1.asIntBuffer(), FMOD_TIMEUNIT_PCMBYTES);
		lenbytes = buffer1.getInt(0);
		
		/*
		 * WAV Structures
		 */
		WavHeader wavHeader = new WavHeader(
				new RiffChunk(new byte[]{'R','I','F','F'},
						FmtChunk.SIZEOF_FMT_CHUNK + RiffChunk.SIZEOF_RIFF_CHUNK+lenbytes),
				new byte[]{'W','A','V','E'});
		FmtChunk fmtChunk = new FmtChunk(
				new RiffChunk(new byte[]{'f','m','t',' '},
						FmtChunk.SIZEOF_FMT_CHUNK - RiffChunk.SIZEOF_RIFF_CHUNK),
				(short)1, (short)channels,
				(int)rate, (int)rate*channels*bits/8,
				(short)(1*channels*bits/8), (short)bits);
		DataChunk dataChunk = new DataChunk(new RiffChunk(new byte[]{'d','a','t','a'}, lenbytes));
		
		try
		{
			file = new RandomAccessFile(new File("record.wav"), "rw");
			
			/*
			 * Write out the WAV header.
			 */
			WavHeader.writeWavHeader(file, wavHeader);
			FmtChunk.writeFmtChunk(file, fmtChunk);
			DataChunk.writeDataChunk(file, dataChunk);
			
			/*
			 * Lock the sound to get access to the raw data.
			 */
			sound.lock(0, lenbytes, ptr1, ptr2, buffer1.asIntBuffer(), buffer2.asIntBuffer());
			len1 = buffer1.getInt(0);
			len2 = buffer2.getInt(0);
			
			/*
			 * Write it to disk.
			 */
			file.getChannel().write(ptr1[0]);
			ptr1[0].rewind();
			
			/*
			 * Unlock the sound to allow FMOD to use it again.
			 */
			sound.unlock(ptr1[0], ptr2[0], len1, len2);
			
			file.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
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
		int version, numdrivers;
		
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
		if(FmodEx.getPlatform() == WIN32 || FmodEx.getPlatform() == WIN64)
		{
			printf("1 : DirectSound\n");
			printf("2 : Windows Multimedia WaveOut\n");
			printf("3 : ASIO\n");
			printf("4 : WASAPI (Vista only)\n");

		}
		else if(FmodEx.getPlatform() == LINUX || FmodEx.getPlatform() == LINUX64)
		{
			printf("1 : OSS  - Open Sound System\n");
			printf("2 : ALSA - Advanced Linux Sound Architecture\n");
			printf("3 : ESD  - Enlightenment Sound Daemon\n");
		}
		else if(FmodEx.getPlatform() == MAC)
		{
			printf("1 : Macintosh SoundManager\n");
			printf("2 : Macintosh CoreAudio\n");
			printf("3 : No Sound\n");
		}
		else
		{
			printfExit("Unsupported platform !");
			return;
		}
		printf("---------------------------------------------------------\n");
		printf("Press a corresponding number or E to quit\n");
		
		int output = -1;
		while(output < '1' || output > '5')
		{
			output = getKey();
			Thread.yield();
		}
		
		if(FmodEx.getPlatform() == WIN32 || FmodEx.getPlatform() == WIN64)
		{
			switch(output)
			{
				case '1': result = system.setOutput(FMOD_OUTPUTTYPE_DSOUND); break;
				case '2': result = system.setOutput(FMOD_OUTPUTTYPE_WINMM);  break;
				case '3': result = system.setOutput(FMOD_OUTPUTTYPE_ASIO);   break;
				case '4': result = system.setOutput(FMOD_OUTPUTTYPE_WASAPI); break;
				default : printExit("Invalid output"); return;
			}  
		}
		else if(FmodEx.getPlatform() == LINUX || FmodEx.getPlatform() == LINUX64)
		{
			switch(output)
			{
				case '1': result = system.setOutput(FMOD_OUTPUTTYPE_OSS);  break;
				case '2': result = system.setOutput(FMOD_OUTPUTTYPE_ALSA); break;
				case '3': result = system.setOutput(FMOD_OUTPUTTYPE_ESD);  break;
				default : printExit("Invalid output"); return;
			}  
		}
		else if(FmodEx.getPlatform() == PLATFORMS.MAC)
		{
			switch(output)
			{
				case '1': result = system.setOutput(FMOD_OUTPUTTYPE_SOUNDMANAGER); break;
				case '2': result = system.setOutput(FMOD_OUTPUTTYPE_COREAUDIO); break;
				case '3': result = system.setOutput(FMOD_OUTPUTTYPE_NOSOUND); break;
				default : printExit("Invalid output"); return;
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
			
			printf("%d : %s\n", i+1, name);
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
			
			printf("%d : %s\n", i + 1, name);
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
		
		result = system.setRecordDriver(driver);
		ErrorCheck(result);
		
		
		result = system.init(32, FMOD_INIT_NORMAL, null);
		ErrorCheck(result);
		
		FMOD_CREATESOUNDEXINFO exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setNumChannels(1);
		exinfo.setFormat(FMOD_SOUND_FORMAT_PCM16);
		exinfo.setDefaultFrequency(44100);
		exinfo.setLength(exinfo.getDefaultFrequency() * SIZEOF_SHORT * exinfo.getNumChannels() * 5);
		
		result = system.createSound((String)null, FMOD_2D | FMOD_SOFTWARE | FMOD_OPENUSER, exinfo, sound);
		ErrorCheck(result);
		
		printf("===================================================================\n");
		printf("Recording example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===================================================================\n");
		printf("\n");
		printf("Press 'r' to record a 5 second segment of audio and write it to a wav file.\n");
		printf("Press 'p' to play the 5 second segment of audio.\n");
		printf("Press 'l' to turn looping on/off.\n");
		printf("Press 's' to stop recording and playback.\n");
		printf("Press 'w' to save the 5 second segment to a wav file.\n");
		printf("Press 'E' to quit\n");
		printf("\n");
		
		/*
		 * Main loop.
		 */
		boolean exit = false;
		do
		{
			boolean recording  = false;
			boolean playing    = false;
			int recordpos = 0;
			int playpos = 0;
			
			switch(getKey())
			{
				case 'r':
				case 'R':
					result = system.recordStart(sound, looping);
					ErrorCheck(result);
					break;
				case 'p':
				case 'P':
					if(looping)
					{
						sound.setMode(FMOD_LOOP_NORMAL);
					}
					else
					{
						sound.setMode(FMOD_LOOP_OFF);
					}
					ErrorCheck(result);
					
					result = system.playSound(FMOD_CHANNEL_REUSE, sound, false, channel);
					ErrorCheck(result);
					break;
				case 'l':
				case 'L':
					looping = !looping;
					break;
				case 's':
				case 'S':
					result = system.recordStop();
					if(channel != null && !channel.isNull())
					{
						channel.stop();
					}
					break;
				case 'w':
				case 'W':
					printfr("Writing to record.wav ...");
					
					saveToWav(sound);
					try {
						Thread.sleep(500);
					} catch(InterruptedException e1){}
					break;
				case 'e':
				case 'E': exit = true; break;
			}
			
//			sound.getLength(buffer.asIntBuffer(), FMOD_TIMEUNIT_PCM);
//			ErrorCheck(result);
//			length = buffer.getInt(0);
			
			system.isRecording(buffer);
			ErrorCheck(result);
			recording = buffer.get(0) != 0;
			
			system.getRecordPosition(buffer.asIntBuffer());
			ErrorCheck(result);
			recordpos = buffer.getInt(0);
			
			if(channel != null && !channel.isNull())
			{
				channel.isPlaying(buffer);
				ErrorCheck(result);
				playing = buffer.get(0) != 0;
				
				channel.getPosition(buffer.asIntBuffer(), FMOD_TIMEUNIT_PCM);
				ErrorCheck(result);
				playpos = buffer.getInt(0);
			}
			
			printfr("State: %-19s. Record pos = %6d : Play pos = %6d : Loop %-3s",
					recording ? playing ? "Recording / playing" : "Recording" : playing ? "Playing" : "Idle",
					recordpos, playpos, looping ? "On" : "Off");
			
			system.update();
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e2){}
		}
		while(!exit && !deinit);

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
		
		if(!sound.isNull()) {
			result = system.release();
			ErrorCheck(result);
		}
		
		printExit("Shutdown\n");
	}
}