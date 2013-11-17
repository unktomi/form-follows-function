/*===============================================================================================
PlaySound Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows how to simply load and play multiple sounds.  This is about the simplest
use of FMOD.
This makes FMOD decode the into memory when it loads.  If the sounds are big and possibly take
up a lot of ram, then it would be better to use the FMOD_CREATESTREAM flag so that it is 
streamed in realtime as it plays.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_DEFAULT;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_HARDWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_OFF;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_CHANNEL_STOLEN;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_INVALID_HANDLE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.nio.ByteBuffer;

import javax.swing.JPanel;

import org.jouvieje.FmodEx.Channel;
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
public class PlaySound extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new PlaySound());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound  sound1 = new Sound();
	private Sound  sound2 = new Sound();
	private Sound  sound3 = new Sound();

	
	public PlaySound()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex *** example."; }
	
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
		Channel channel = new Channel();
		FMOD_RESULT result;
		int version;
		
		/*
		 * Buffer used to store all datas received from FMOD.
		 */
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
		
		result = system.init(32, FMOD_INIT_NORMAL, null);
		ErrorCheck(result);
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/drumloop.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_HARDWARE | FMOD_OPENMEMORY, exinfo, sound1);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		result = sound1.setMode(FMOD_LOOP_OFF);		/* drumloop.wav has embedded loop points which automatically makes looping turn on, */
		ErrorCheck(result);							/* so turn it off here.  We could have also just put FMOD_LOOP_OFF in the above CreateSound call. */
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/jaguar.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_HARDWARE | FMOD_OPENMEMORY, exinfo, sound2);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/swish.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_HARDWARE | FMOD_OPENMEMORY, exinfo, sound3);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		printf("===================================================================\n");
		printf("PlaySound Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===================================================================\n");
		printf("\n");
		printf("Press '1' to play a mono sound using hardware mixing\n");
		printf("Press '2' to play a mono sound using software mixing\n");
		printf("Press '3' to play a stereo sound using hardware mixing\n");
		printf("Press 'E' to quit\n");
		printf("\n");
		
		/*
		 * Main loop.
		 */
		boolean exit = false;
		do
		{
			switch(getKey())
			{
				case '1':
					result = system.playSound(FMOD_CHANNEL_FREE, sound1, false, channel);
					ErrorCheck(result);
					break;
				case '2':
					result = system.playSound(FMOD_CHANNEL_FREE, sound2, false, channel);
					ErrorCheck(result);
					break;
				case '3':
					result = system.playSound(FMOD_CHANNEL_FREE, sound3, false, channel);
					ErrorCheck(result);
					break;
				case 'e':
				case 'E': exit = true; break;
			}
			
			system.update();
			
			{
				int ms = 0, lenms = 0;
				boolean playing = false;
				boolean paused = false;
				int channelsplaying = 0;
				
				if(channel != null && !channel.isNull())
				{
					Sound currentsound = new Sound();
					
					result = channel.isPlaying(buffer);
					if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN))
						ErrorCheck(result);
					playing = buffer.get(0) != 0;
					
					result = channel.getPaused(buffer);
					if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN))
						ErrorCheck(result);
					paused = buffer.get(0) != 0;
					
					result = channel.getPosition(buffer.asIntBuffer(), FMOD_TIMEUNIT_MS);
					if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN))
						ErrorCheck(result);
					ms = buffer.getInt(0);
					
					channel.getCurrentSound(currentsound);
					if(!currentsound.isNull())
					{
						result = currentsound.getLength(buffer.asIntBuffer(), FMOD_TIMEUNIT_MS);
						if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN))
							ErrorCheck(result);
						lenms = buffer.getInt(0);
					}
				}
				
				system.getChannelsPlaying(buffer.asIntBuffer());
				channelsplaying = buffer.getInt(0);
				
				printfr("Time %02d:%02d:%02d/%02d:%02d:%02d : %s : Channels Playing %2d",
						ms/1000/60, ms/1000%60, ms/10%100, lenms/1000/60, lenms/1000%60, lenms/10%100,
						paused ? "Paused " : playing ? "Playing" : "Stopped", channelsplaying);
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
		if(!sound1.isNull()) {
			result = sound1.release();
			ErrorCheck(result);
		}
		if(!sound2.isNull()) {
			result = sound2.release();
			ErrorCheck(result);
		}
		if(!sound3.isNull()) {
			result = sound3.release();
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