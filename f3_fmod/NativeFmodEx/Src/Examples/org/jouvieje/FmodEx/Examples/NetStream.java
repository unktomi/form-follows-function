/*===============================================================================================
NetStream Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows how to play streaming audio from the internet
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_CREATESTREAM;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_HARDWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_MPEGSEARCH;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_NONBLOCKING;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_RAWBYTES;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OPENSTATE.FMOD_OPENSTATE_BUFFERING;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OPENSTATE.FMOD_OPENSTATE_CONNECTING;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OPENSTATE.FMOD_OPENSTATE_READY;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_INVALID_HANDLE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGDATATYPE.FMOD_TAGDATATYPE_STRING;
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
import org.jouvieje.FmodEx.Enumerations.FMOD_OPENSTATE;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Examples.Utils.ConsoleGUI;
import org.jouvieje.FmodEx.Examples.Utils.FmodExExampleFrame;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Structures.FMOD_TAG;

/**
 * I've ported the C++ FMOD Ex example to NativeFmodEx.
 * It plays a music from the net. So, you need an internet connexion to run this sample.
 * I've put a mp3 file (from Fmod API) in my site if you want to test this example :
 * 		http://jerome.jouvie.free.fr/downloads/NativeFmod/jules.mp3
 * or
 * 		http://topresult.tomato.co.uk/~jerome/downloads/NativeFmod/jules.mp3
 * 
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * Site :
 * 		http://jerome.jouvie.free.fr/
 */
public class NetStream extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new NetStream());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound  sound  = new Sound();
	
	public NetStream()
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
		
		Channel channel = new Channel();
		FMOD_RESULT      result;
		long version;
		
		ByteBuffer buffer = newByteBuffer(SIZEOF_INT);
		
		printf("===================================================================\n");
		printf("NetStream Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===================================================================\n\n");
		printf("Usage:   netstream <url>\n");
		printf("Example: netstream http://www.fmod.org/stream.mp3\n");
		printf("Example: netstream http://jerome.jouvie.free.fr/downloads/NativeFmodEx/jules.mp3\n\n");
		resetInput();
		setInput("http://www.fmod.org/stream.mp3");
		while(!keyHit()) {
			Thread.yield();
		}
		String url = getInput();
		
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
		
		/*
		 * Bump up the file buffer size a little bit for netstreams (to account for lag).
		 */
		result = system.setStreamBufferSize(64*1024, FMOD_TIMEUNIT_RAWBYTES);
		ErrorCheck(result);
		
		printf("Buffering...\n\n");
		
		result = system.createSound(url, FMOD_HARDWARE | FMOD_2D | FMOD_CREATESTREAM | FMOD_MPEGSEARCH | FMOD_NONBLOCKING, null, sound);
		ErrorCheck(result);
		
		printf("Press space to pause, E to quit\n");
		printf("\n");
		
		/*
		 * Main loop
		 */
		boolean exit = false;
		do
		{
			int ms = 0, percent = 0;
			boolean playing = false;
			boolean paused = false;
			boolean starving = false;
			FMOD_OPENSTATE openstate;
			
			if(channel.isNull())
			{
				result = system.playSound(FMOD_CHANNEL_FREE, sound, false, channel);
			}
			
			switch(getKey())
			{
				case ' ':
					if(!channel.isNull())
					{
						channel.getPaused(buffer);
						boolean pause = buffer.get(0) != 0;
						channel.setPaused(!pause);
					}
				case 'e':
				case 'E': exit = true; break;
			}
			
			system.update();
			
			for(;;)
			{
				FMOD_TAG tag = FMOD_TAG.create();
				if(sound.getTag(null, -1, tag) != FMOD_OK)
				{
					break;
				}
				if(tag.getDataType() == FMOD_TAGDATATYPE_STRING)
				{
					printf("%s = %s (%d bytes)       \n",
							tag.getName(), tag.getData().asString(), tag.getDataLen());
				}
				tag.release();
			}
			
			FMOD_OPENSTATE[] openstateArray = new FMOD_OPENSTATE[1];
			ByteBuffer starvingBuffer = newByteBuffer(1);
			result = sound.getOpenState(openstateArray, buffer.asIntBuffer(), starvingBuffer);
			ErrorCheck(result);
			openstate = openstateArray[0];
			percent = buffer.getInt(0);
			starving = starvingBuffer.get(0) != 0;
			
			if(!channel.isNull())
			{
				result = channel.getPaused(buffer);
				if(result == FMOD_ERR_INVALID_HANDLE) {	//Added to shutdown nicely
					break;
				}
				ErrorCheck(result);
				paused = buffer.get(0) != 0;
				result = channel.isPlaying(buffer);
				ErrorCheck(result);
				playing = buffer.get(0) != 0;
				result = channel.getPosition(buffer.asIntBuffer(), FMOD_TIMEUNIT_MS);
				ErrorCheck(result);
				ms = buffer.getInt(0);
				result = channel.setMute(starving);
				ErrorCheck(result);
			}
			
			printfr("Time %02d:%02d:%02d : %s : (%3d%%) %s",
					ms/1000/60, ms/1000%60, ms/10%100,
					openstate == FMOD_OPENSTATE_BUFFERING ? "Buffering..." : openstate == FMOD_OPENSTATE_CONNECTING ? "Connecting..." : paused ? "Paused       " : playing ? "Playing      " : "Stopped      ", percent, starving ? "STARVING" : "        ");
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e1){}
		}
		while(!exit && !deinit);
		
		printf("\n");
		
	    printf("Shutting down.\n");
	    
	    if(!channel.isNull())
	    {
	    	result = channel.stop();
	    	ErrorCheck(result);
	    }
		
	    /*
	     * If we pressed escape before it is ready, wait for it to finish opening before we release it.
	     */
		do
		{
			FMOD_OPENSTATE[] openstate = new FMOD_OPENSTATE[1];
			result = sound.getOpenState(openstate, null, null);
			ErrorCheck(result);
			
			if(openstate[0] == FMOD_OPENSTATE_READY) {
				break;
			}
			
			printfr("Waiting for sound to finish opening before trying to release it....");
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e1){}
		}
		while(true);

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