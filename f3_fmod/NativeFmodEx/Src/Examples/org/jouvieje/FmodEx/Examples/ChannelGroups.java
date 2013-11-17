/*===============================================================================================
ChannelGroups Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows how to put channels into channel groups, so that you can affect a group
of channels at a time instead of just one channel at a time.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.nio.ByteBuffer;

import javax.swing.JPanel;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.ChannelGroup;
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
public class ChannelGroups extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new ChannelGroups());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	System		 system		 = new System();
	Sound[]		 sounds		 = new Sound[6];
	ChannelGroup groupA		 = new ChannelGroup();
	ChannelGroup groupB		 = new ChannelGroup();

	
	public ChannelGroups()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex ChannelGroups example."; }
	
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
		Channel[]	 channels	 = new Channel[6];
		ChannelGroup masterGroup = new ChannelGroup();
		FMOD_RESULT	 result;
		int			 version;
		
		ByteBuffer buffer = newByteBuffer(SIZEOF_INT * 1);
		
		for(int i = 0; i < sounds.length; i++)
			sounds[i] = new Sound();
		for(int i = 0; i < channels.length; i++)
			channels[i] = new Channel();
		
		/*
		 * Create a System object and initialize.
		 */
		result = FmodEx.System_Create(system);
		ErrorCheck(result);
		
		result = system.getVersion(buffer.asIntBuffer());
		version = buffer.getInt(0);
		ErrorCheck(result);
		
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
		result = system.createSound(soundBuffer, FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[0]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/jaguar.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[1]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/swish.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[2]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/c.ogg");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[3]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/d.ogg");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[4]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/e.ogg");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[5]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		result = system.createChannelGroup("Group A", groupA);
		ErrorCheck(result);
		
		result = system.createChannelGroup("Group B", groupB);
		ErrorCheck(result);

	    result = system.getMasterChannelGroup(masterGroup);
	    ErrorCheck(result);
	    
		printf("===================================================================\n");
		printf("ChannelGroups Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===================================================================\n");
		printf("\n");
		printf("Group A : drumloop.wav, jaguar.wav, swish.wav\n");
		printf("Group B : c.ogg, d.ogg, e.ogg\n");
		printf("\n");
		printf("Press 'A' to mute/unmute group A\n");
		printf("Press 'B' to mute/unmute group B\n");
	    printf("Press 'C' to mute/unmute group A and B (master group)\n");
		printf("Press 'E' to quit\n");
		printf("\n");
		
	    /*
	     * Instead of being independent, set the group A and B to be children of the master group.
	     */
	    result = masterGroup.addGroup(groupA);
	    ErrorCheck(result);
	    
	    result = masterGroup.addGroup(groupB);
	    ErrorCheck(result);
		
		/*
		 * Start all the sounds!
		 */
		for(int i = 0; i < 6; i++)
		{
			result = system.playSound(FMOD_CHANNEL_FREE, sounds[i], true, channels[i]);
			ErrorCheck(result);
			if(i < 3)
			{
				result = channels[i].setChannelGroup(groupA);
			}
			else
			{
				result = channels[i].setChannelGroup(groupB);
			}
			ErrorCheck(result);
			result = channels[i].setPaused(false);
			ErrorCheck(result);
		}
		
		/*
		 * Change the volume of each group, just because we can!  (And makes it less noise).
		 */
		result = groupA.setVolume(0.5f);
		ErrorCheck(result);
		result = groupB.setVolume(0.5f);
		ErrorCheck(result);
		
		/*
		 * Main loop.
		 */
		boolean muteA = true;
		boolean muteB = true;
		boolean muteMaster = true;
		
		boolean exit = false;
		do
		{
			switch (getKey())
			{
				case 'a': 
				case 'A': 
					groupA.setMute(muteA);
					
					muteA = !muteA;
					break;
				case 'b': 
				case 'B': 
					groupB.setMute(muteB);
					
					muteB = !muteB;
					break;
                case 'c':
                case 'C':
                    masterGroup.setMute(muteMaster);

                    muteMaster = !muteMaster;
                    break;
				case 'e':
				case 'E': exit = true; break;
			}
			
			system.update();
			
			{
				system.getChannelsPlaying(buffer.asIntBuffer());
				int channelsplaying = buffer.getInt(0);
				
				printfr("Channels Playing %2d", channelsplaying);
			}
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e){}
		}
		while(!exit && !deinit);
		
		printf("\n");
		
		/*
		 * A little fade  (over 2 seconds)
		 */
		printf("Goodbye!\n");
		{
	        float pitch = 1.0f;
	        float vol = 1.0f;
			
			for(int i = 0; i < 200; i++)
			{
	            masterGroup.setPitch(pitch);
	            masterGroup.setVolume(vol);

	            vol   -= (1.0f / 200.0f);
	            pitch -= (0.5f / 200.0f);
				
				try {
					Thread.sleep(10);
				} catch(InterruptedException e){}
			}
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
		for(int i = 0; i < 6; i++)
		{
			if(!sounds[i].isNull()) {
				result = sounds[i].release();
				ErrorCheck(result);
			}
		}
		
		if(!groupA.isNull()) {
			result = groupA.release();
			ErrorCheck(result);
		}
		if(!groupB.isNull()) {
			result = groupB.release();
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