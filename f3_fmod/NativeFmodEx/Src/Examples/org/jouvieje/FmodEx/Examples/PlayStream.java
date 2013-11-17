/*===============================================================================================
PlayStream Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows how to simply play a stream, such as an mp3 or wav.
The stream behaviour is achieved by specifying FMOD_CREATESTREAM in the call to 
System::createSound.
This makes FMOD decode the file in realtime as it plays, instead of loading it all at once.
This uses far less memory, in exchange for a small runtime cpu hit.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_HARDWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
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
public class PlayStream extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new PlayStream());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound  sound  = new Sound();

	public PlayStream()
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
		
		result = system.init(1, FMOD_INIT_NORMAL, null);
		ErrorCheck(result);
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/wave.mp3");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createStream(soundBuffer, FMOD_HARDWARE | FMOD_LOOP_NORMAL | FMOD_2D | FMOD_OPENMEMORY, exinfo, sound);
		ErrorCheck(result);
		exinfo.release();
		//soundBuffer must remain valid during playback (because of createStream),
		//use createSound if you don't want this behavior.
		
		printf("====================================================================\n");
		printf("PlayStream Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("====================================================================\n");
		printf("\n");
		printf("Press SPACE to pause, E to quit\n");
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
					channel.getPaused(buffer);
					boolean paused = buffer.get(0) != 0;
					channel.setPaused(!paused);
					break;
				case 'e':
				case 'E': exit = true; break;
			}
			
			system.update();
			
			if(!channel.isNull())
			{
				int ms = 0, lenMs = 0;
				boolean playing = false;
				boolean paused = false;
				
				result = channel.isPlaying(buffer);
				if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE))
					ErrorCheck(result);
				playing = buffer.get(0) != 0;
				
				result = channel.getPaused(buffer);
				if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE))
					ErrorCheck(result);
				paused = buffer.get(0) != 0;
				
				result = channel.getPosition(buffer.asIntBuffer(), FMOD_TIMEUNIT_MS);
				if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE))
					ErrorCheck(result);
				ms = buffer.getInt(0);
				
				result = sound.getLength(buffer.asIntBuffer(), FMOD_TIMEUNIT_MS);
				if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE))
					ErrorCheck(result);
				lenMs = buffer.getInt(0);
				
				printfr("Time %02d:%02d:%02d/%02d:%02d:%02d : %s",
						ms/1000/60, ms/1000%60, ms/10%100,
						lenMs/1000/60, lenMs/1000%60, lenMs/10%100,
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