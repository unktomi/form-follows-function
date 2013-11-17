/*===============================================================================================
 File Callbacks Example
 Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

 This example is a modified version of the playstream example.
 It shows the correct way declare and handle fmod file callbacks with System::setFileSystem.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_DEFAULT;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_HARDWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_FILE_BAD;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_FILE_EOF;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_FILE_NOTFOUND;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_INVALID_HANDLE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_INVALID_PARAM;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.swing.JPanel;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Callbacks.FMOD_FILE_CLOSECALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_FILE_OPENCALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_FILE_READCALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_FILE_SEEKCALLBACK;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Examples.Utils.ConsoleGUI;
import org.jouvieje.FmodEx.Examples.Utils.FmodExExampleFrame;
import org.jouvieje.FmodEx.Examples.Utils.Medias;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Misc.ObjectPointer;
import org.jouvieje.FmodEx.Misc.Pointer;
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
public class FileCallbacks extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new FileCallbacks());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound  sound  = new Sound();
	
	public FileCallbacks()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex FileCallbacks example."; }	
	
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_RESULT.FMOD_OK)
		{
			printfExit("FMOD error! (%d) %s\n", result.asInt(), FmodEx.FMOD_ErrorString(result));
		}
	}
	
	/*
	 * TIPS:
	 * 
	 * 1. use F_CALLBACK.  Do NOT force cast your own function to fmod's callback type.
	 * 2. return FMOD_ERR_FILE_NOTFOUND in open as required.
	 * 3. return number of bytes read in read callback.  Do not get the size and count 
	 *    around the wrong way in fread for example, this would return 1 instead of the number of bytes read.
	 * 
	 * QUESTIONS:
	 * 1. Why does fmod seek to the end and read?  Because it is looking for ID3V1 tags.  
	 *    Use FMOD_IGNORETAGS in System::createSound / System::createStream if you don't like this behaviour.
	 */
	
	private FMOD_FILE_OPENCALLBACK myopen = new FMOD_FILE_OPENCALLBACK(){
		public FMOD_RESULT FMOD_FILE_OPENCALLBACK(String name, int unicode, IntBuffer filesize, Pointer handle, Pointer userdata)
		{
			if(name != null)
			{
				try
				{
					RandomAccessFile file = new RandomAccessFile(new File(name), "r");
					
					filesize.put(0, (int)file.length());
					
					handle.shareMemory(ObjectPointer.create(file));
					//userdata = 0x12345678;		//Don't need this ...
				}
				catch(FileNotFoundException e)
				{
					return FMOD_ERR_FILE_NOTFOUND;
				}
				catch(IOException e)
				{
					return FMOD_ERR_FILE_BAD;
				}
			}
			
			return FMOD_OK;
		}
	};

	private FMOD_FILE_CLOSECALLBACK myclose = new FMOD_FILE_CLOSECALLBACK(){
		public FMOD_RESULT FMOD_FILE_CLOSECALLBACK(Pointer handle, Pointer userdata)
		{
			if(handle.isNull())
			{
				return FMOD_ERR_INVALID_PARAM;
			}
			
			try
			{
				RandomAccessFile file = (RandomAccessFile)ObjectPointer.createView(handle).getObject();
				file.close();
				
				ObjectPointer.createView(handle).release();
			}
			catch(IOException e)
			{
				return FMOD_ERR_INVALID_PARAM;
			}
			
			return FMOD_OK;
		}
	};
	
	private FMOD_FILE_READCALLBACK myread = new FMOD_FILE_READCALLBACK(){
		public FMOD_RESULT FMOD_FILE_READCALLBACK(Pointer handle, ByteBuffer buffer, int sizebytes, IntBuffer bytesread, Pointer userdata)
		{
			if(handle.isNull())
			{
				return FMOD_ERR_INVALID_PARAM;
			}
			
			if(bytesread != null)
			{
				try
				{
					RandomAccessFile file = (RandomAccessFile)ObjectPointer.createView(handle).getObject();
					
					bytesread.put(0, file.getChannel().read(buffer));
					buffer.rewind();
				}
				catch(IOException e)
				{
					return FMOD_ERR_FILE_BAD;
				}
				
				if(bytesread.get(0) < sizebytes)
				{
					return FMOD_ERR_FILE_EOF;
				}
			}
			
			return FMOD_OK;
		}
	};
	
	private FMOD_FILE_SEEKCALLBACK myseek = new FMOD_FILE_SEEKCALLBACK(){
		public FMOD_RESULT FMOD_FILE_SEEKCALLBACK(Pointer handle, int pos, Pointer userdata)
		{
			if(handle.isNull())
			{
				return FMOD_ERR_INVALID_PARAM;
			}
			
			try
			{
				RandomAccessFile file = (RandomAccessFile)ObjectPointer.createView(handle).getObject();
				
				file.seek(pos);
			}
			catch(IOException e)
			{
				return FMOD_ERR_FILE_BAD;
			}
			
			return FMOD_OK;
		}
	};
	
	public void initFmod()
	{
		/*
		 * NativeFmodEx Init
		 */
		try
		{
			Init.loadLibraries(INIT_MODES.INIT_FMOD_EX);
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
		Channel     channel = new Channel();
		FMOD_RESULT result;
		int         version;
		
		ByteBuffer buffer = newByteBuffer(SIZEOF_INT);
		
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
		
		result = system.setFileSystem(myopen, myclose, myread, myseek, 2048);
		ErrorCheck(result);
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/wave.mp3");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createStream(soundBuffer, FMOD_HARDWARE | FMOD_LOOP_NORMAL | FMOD_2D | FMOD_OPENMEMORY, exinfo, sound);
		ErrorCheck(result);
		exinfo.release();
		//soundBuffer must remain valid during playback (because of createStream),
		//use createSound if you don't want this behavior.
		
		printf("========================================================================\n");
		printf("File Callbacks Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("========================================================================\n");
		printf("\n");
		printf("Press space to pause, E to quit\n");
		printf("\n");
		
		/*
		 * Play the sound.
		 */
		
		result = system.playSound(FMOD_CHANNEL_FREE, sound, false, channel);
		ErrorCheck(result);
		
		/*
		 * Main loop.
		 */
		boolean exit = false;
		do
		{
			switch(getKey())
			{
				case ' ':
				{
					channel.getPaused(buffer);
					boolean paused = buffer.get(0) != 0;
					channel.setPaused(!paused);
					break;
				}
				case 'e':
				case 'E': exit = true; break;
			}
			
			system.update();
			
			if(!channel.isNull())
			{
				int ms;
				int lenms;
				boolean playing;
				boolean paused;
				
				channel.isPlaying(buffer);
				if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE))
				{
					ErrorCheck(result);
				}
				playing = buffer.get(0) != 0;
				
				result = channel.getPaused(buffer);
				if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE))
				{
					ErrorCheck(result);
				}
				paused = buffer.get(0) != 0;
				
				result = channel.getPosition(buffer.asIntBuffer(), FMOD_TIMEUNIT_MS);
				if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE))
				{
					ErrorCheck(result);
				}
				ms = buffer.getInt(0);
				
				result = sound.getLength(buffer.asIntBuffer(), FMOD_TIMEUNIT_MS);
				if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE))
				{
					ErrorCheck(result);
				}
				lenms = buffer.getInt(0);
				
				printfr("Time %02d:%02d:%02d/%02d:%02d:%02d : %s",
						ms/1000/60, ms/1000%60, ms/10%100, lenms/1000/60, lenms/1000%60, lenms/10%100,
						paused ? "Paused " : playing ? "Playing" : "Stopped");
			}
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e){}
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
		if(!system.isNull()) {
			result = system.close();
			ErrorCheck(result);
			result = system.release();
			ErrorCheck(result);
		}
		
		printExit("Shutdown\n");
	}
}