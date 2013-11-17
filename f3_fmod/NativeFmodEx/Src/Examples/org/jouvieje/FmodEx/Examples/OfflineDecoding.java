/*===============================================================================================
 Offline Decoding Example
 Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

 This example shows how decode a file to PCM, without playing it.
 It writes out the data as a raw data file.
 The System::createSound function uses FMOD_OPENONLY so that FMOD does not read any data 
 itself.
 If this is uses then it is up to the user to use Sound::readData to get the data out of 
 the file and into the destination buffer.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_ACCURATETIME;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_DEFAULT;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENONLY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_PCMBYTES;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import javax.swing.JPanel;

import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Examples.Utils.ConsoleGUI;
import org.jouvieje.FmodEx.Examples.Utils.FmodExExampleFrame;
import org.jouvieje.FmodEx.Examples.Utils.Medias;
import org.jouvieje.FmodEx.Exceptions.InitException;
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
public class OfflineDecoding extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new OfflineDecoding());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System      system = new System();
	private Sound       sound  = new Sound();

	
	public OfflineDecoding()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex OfflineDecoding example."; }
	
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
		
		ByteBuffer soundBuffer; FMOD_CREATESOUNDEXINFO exinfo;
		FMOD_RESULT result;
		int         version;
		
		final ByteBuffer buffer = newByteBuffer(SIZEOF_INT);
		final int CHUNKSIZE = 4096;

		
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
		
		result = system.init(1, FMOD_INIT_NORMAL, null);
		ErrorCheck(result);
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/wave.mp3");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_OPENONLY | FMOD_ACCURATETIME | FMOD_OPENMEMORY, exinfo, sound);
		ErrorCheck(result);
		exinfo.release();
		
		printf("===============================================================================\n");
		printf("Offline Decoding Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===============================================================================\n");
		printf("\n");
		printf("This program will open wave.mp3 and decode it into wave.raw using the\n");
		printf("Sound::readData function.\n");
		printf("\n");
		
		/*
		 * Decode the sound and write it to a .raw file.
		 */
		try
		{
			int length = 0, read;
			
			RandomAccessFile file = new RandomAccessFile("output.raw", "rw");
			FileChannel fileChannel = file.getChannel();
			
			result = sound.getLength(buffer.asIntBuffer(), FMOD_TIMEUNIT_PCMBYTES);
			ErrorCheck(result);
			length = buffer.getInt(0);
			
			ByteBuffer data = newByteBuffer(CHUNKSIZE);
			ByteBuffer view = data.duplicate();
			
			int bytesread = 0;
			do
			{
				result = sound.readData(data, CHUNKSIZE, buffer.asIntBuffer());
				read = buffer.getInt(0);
				
				view.limit(read);
				fileChannel.write(view);
				
				bytesread += read;
				view.rewind();
				
				printfr("writing %d bytes of %d to output.raw", bytesread, length);
			}
			while(result == FMOD_OK && read == CHUNKSIZE);
			
			/*
			 * Loop terminates when either 
			 * 1. the read function returns an error.  (ie FMOD_ERR_FILE_EOF etc).
			 * 2. the amount requested was different to the amount returned. (somehow got an EOF without the file error, maybe a non stream file format like mod/s3m/xm/it/midi).
			 * 
			 * If 'bytesread' is bigger than 'length' then it just means that FMOD miscalculated the size, 
			 * but this will not usually happen if FMOD_ACCURATETIME is used.  (this will give the correct length for VBR formats)
			 */
			
			printf("\n");
			
			file.close();
		}
		catch(FileNotFoundException e)
		{
            printfExit("Error!  Could not open output.raw output file.\n");
            return;
		}
		catch(IOException e)
		{
            printfExit("Error!  %s\n", e.getMessage());
            return;
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
			result = system.close();
			ErrorCheck(result);
			result = system.release();
			ErrorCheck(result);
		}
		
		printExit("Shutdown\n");
	}
}