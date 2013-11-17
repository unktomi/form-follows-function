/*===============================================================================================
 Record to disk example
 Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

 This example shows how to do a streaming record to disk.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENUSER;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_PCM;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
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
import static org.jouvieje.FmodEx.Misc.BufferUtils.newIntBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_SHORT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JPanel;

import org.jouvieje.FileFormat.WavFormat.DataChunk;
import org.jouvieje.FileFormat.WavFormat.FmtChunk;
import org.jouvieje.FileFormat.WavFormat.RiffChunk;
import org.jouvieje.FileFormat.WavFormat.WavHeader;
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
public class RecordToDisk extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new RecordToDisk());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound  sound  = new Sound();
	
	public RecordToDisk()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex RecordToDisk example."; }
	
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
	private static void WriteWavHeader(RandomAccessFile file, Sound sound, int length)
	{
		IntBuffer   bits     = newIntBuffer(1);
		IntBuffer   channels = newIntBuffer(1);
		FloatBuffer rate     = newFloatBuffer(1);
		
		if(sound.isNull())
		{
			return;
		}
		
		try
		{
			//Seek to beginning of the file.
			file.seek(0);
			
			sound.getFormat(null, null, channels, bits);
			sound.getDefaults(rate, null, null, null);
			
			/*
			 * WAV Structures
			 */
			WavHeader wavHeader = new WavHeader(
					new RiffChunk(new byte[]{'R','I','F','F'}, FmtChunk.SIZEOF_FMT_CHUNK + RiffChunk.SIZEOF_RIFF_CHUNK+length),
					new byte[]{'W','A','V','E'});
			FmtChunk fmtChunk = new FmtChunk(
					new RiffChunk(new byte[]{'f','m','t',' '}, FmtChunk.SIZEOF_FMT_CHUNK - RiffChunk.SIZEOF_RIFF_CHUNK),
					(short)1, (short)channels.get(0),
					(int)rate.get(0), (int)rate.get(0)*channels.get(0)*bits.get(0)/8,
					(short)(1*channels.get(0)*bits.get(0)/8), (short)bits.get(0));
			DataChunk dataChunk = new DataChunk(new RiffChunk(new byte[]{'d','a','t','a'}, length));
			
			/*
			 * Write out the WAV header.
			 */
			WavHeader.writeWavHeader(file, wavHeader);
			FmtChunk.writeFmtChunk(file, fmtChunk);
			DataChunk.writeDataChunk(file, dataChunk);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return;
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
		
		FMOD_CREATESOUNDEXINFO exinfo = FMOD_CREATESOUNDEXINFO.create();
		FMOD_RESULT     result;
		int version, numdrivers;
		int datalength = 0, soundlength;
		
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
		
		
		printf("\n");
		
		result = system.setRecordDriver(driver);
		ErrorCheck(result);
		
		result = system.init(32, FMOD_INIT_NORMAL, null);
		ErrorCheck(result);
		
		exinfo.setNumChannels(1);
		exinfo.setFormat(FMOD_SOUND_FORMAT_PCM16);
		exinfo.setDefaultFrequency(44100);
		exinfo.setLength(exinfo.getDefaultFrequency() * SIZEOF_SHORT * exinfo.getNumChannels() * 2);
		
		result = system.createSound((String)null, FMOD_2D | FMOD_SOFTWARE | FMOD_OPENUSER, exinfo, sound);
		ErrorCheck(result);
		
		printf("========================================================================\n");
		printf("Record to disk example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("========================================================================\n");
		printf("\n");
		printf("Press Enter to start recording to record.wav\n");
		printf("\n");
		
		resetInput();
		while(!keyHit()) {
			Thread.yield();
		}
		
		result = system.recordStart(sound, true);
		ErrorCheck(result);
		
		printf("Press Enter to quit\n");
		printf("\n");
		
		RandomAccessFile file = null;
		try
		{
			file = new RandomAccessFile(new File("record.wav"), "rw");
		}
		catch(FileNotFoundException e)
		{
			printfExit("ERROR : could not open record.wav for writing.\n");
			return;
		}
		
		/*
		 * Write out the wav header.  As we don't know the length yet it will be 0.
		 */
		WriteWavHeader(file, sound, datalength);
		
		result = sound.getLength(buffer.asIntBuffer(), FMOD_TIMEUNIT_PCM);
		ErrorCheck(result);
		soundlength = buffer.getInt(0);
		
		/*
		 * Main loop.
		 */
		int lastrecordpos = 0;
		resetInput();
		do
		{
			system.getRecordPosition(buffer.asIntBuffer());
			ErrorCheck(result);
			int recordpos = buffer.getInt(0);
			
			if(recordpos != lastrecordpos)        
			{
				ByteBuffer[] ptr1 = new ByteBuffer[1];
				ByteBuffer[] ptr2 = new ByteBuffer[1];
				IntBuffer len1 = newIntBuffer(1);
				IntBuffer len2 = newIntBuffer(1);
				int blocklength;
				
				blocklength = recordpos - lastrecordpos;
				if(blocklength < 0)
				{
					blocklength += soundlength;
				}
				
				/*
				 * Lock the sound to get access to the raw data.
				 */
				sound.lock(lastrecordpos * exinfo.getNumChannels() * 2, blocklength * exinfo.getNumChannels() * 2, ptr1, ptr2, len1, len2);   /* * exinfo.numchannels * 2 = stereo 16bit.  1 sample = 4 bytes. */
				
				try
				{
					/*
					 * Write it to disk.
					 */
					if(ptr1[0] != null && len1.get(0) > 0)
					{
						datalength += file.getChannel().write(ptr1[0]);
						ptr1[0].rewind();
					}
					if(ptr2[0] != null && len2.get(0) > 0)
					{
						datalength += file.getChannel().write(ptr2[0]);
						ptr2[0].rewind();
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				
				/*
				 * Unlock the sound to allow FMOD to use it again.
				 */
				sound.unlock(ptr1[0], ptr2[0], len1.get(0), len2.get(0));
			}
			
			lastrecordpos = recordpos;
			
			printfr("%-23s. Record buffer pos = %6d : Record time = %02d:%02d",
					(((int)java.lang.System.currentTimeMillis() / 500) % 2) == 0 ? "Recording to record.wav" : "",
					recordpos, datalength/exinfo.getDefaultFrequency()/exinfo.getNumChannels()/2/60, (datalength/exinfo.getDefaultFrequency()/exinfo.getNumChannels()/2)%60);
			
			system.update();
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e){}
		}
		while(!keyHit() && !deinit);
		
		printf("\n");
		
		/*
		 * Write back the wav header now that we know its length.
		 */
		WriteWavHeader(file, sound, datalength);
		
		try
		{
			file.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

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
			result = system.release();
			ErrorCheck(result);
		}
		
		printExit("Shutdown\n");
	}
}