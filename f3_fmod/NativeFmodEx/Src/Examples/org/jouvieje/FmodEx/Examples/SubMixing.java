/*===============================================================================================
Submixing Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows how to put channels into channel groups, so that you can affect a group
of channels at a time instead of just one channel at a time.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_FLANGE.FMOD_DSP_FLANGE_RATE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_LOWPASS.FMOD_DSP_LOWPASS_CUTOFF;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_FLANGE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_LOWPASS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_REVERB;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.nio.ByteBuffer;

import javax.swing.JPanel;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.ChannelGroup;
import org.jouvieje.FmodEx.DSP;
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
public class SubMixing extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new SubMixing());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System       system      = new System();
	private Sound[]      sounds      = new Sound[5];
	private DSP          dspreverb   = new DSP();
	private DSP          dspflange   = new DSP();
	private DSP          dsplowpass  = new DSP();
	private ChannelGroup groupA      = new ChannelGroup();
	private ChannelGroup groupB      = new ChannelGroup();
	
	public SubMixing()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex SubMixing example."; }
	
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
		Channel[]	 channels	 = new Channel[5];
		ChannelGroup masterGroup = new ChannelGroup();
		FMOD_RESULT  result;
		int          version;
		
		ByteBuffer buffer = newByteBuffer(SIZEOF_INT);
		
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
		result = system.createSound(soundBuffer, FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[0]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/jaguar.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[1]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/c.ogg");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[2]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/d.ogg");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[3]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/e.ogg");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sounds[4]);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		result = system.createChannelGroup("Group A", groupA);
		ErrorCheck(result);
		
		result = system.createChannelGroup("Group B", groupB);
		ErrorCheck(result);
		
	    result = system.getMasterChannelGroup(masterGroup);
	    ErrorCheck(result);

	    result = masterGroup.addGroup(groupA);
	    ErrorCheck(result);

	    result = masterGroup.addGroup(groupB);
	    ErrorCheck(result);
		
		printf("======================================================================\n");
		printf("Sub-mixing example.  Copyright (c) Firelight Technologies 2004-2008.  \n");
		printf("======================================================================\n");
		printf("                                                       (drumloop.wav) \n");
		printf("                                                      /               \n");
		printf("                                              (groupA)                \n");
		printf("                                     (reverb)/        \\              \n");
		printf("                                    /                  (jaguar.wav)   \n");
	    printf("(soundcard)--(lowpass)--(mastergroup)                                 \n");
		printf("                                    \\                  (c.ogg)       \n");
		printf("                                     (flange)         /               \n");
		printf("                                             \\(groupB)--(d.ogg)      \n");
		printf("                                                      \\              \n");
		printf("                                                       (e.ogg)        \n");
		printf("Press 'A' to mute/unmute group A\n");
		printf("Press 'B' to mute/unmute group B\n");
		printf("\n");
		printf("Press 'R' to place reverb on group A\n");
		printf("Press 'F' to place flange on group B\n");
	    printf("Press 'L' to place lowpass on master group (everything)\n");
		printf("Press 'E' to quit\n");
		printf("\n");
		
		/*
		 * Start all the sounds!
		 */
		
		for(int i = 0; i < 5; i++)
		{
			result = system.playSound(FMOD_CHANNEL_FREE, sounds[i], true, channels[i]);
			ErrorCheck(result);
			if(i < 2)
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
		 * Create the DSP effects we want to apply to our submixes.
		 */  
		result = system.createDSPByType(FMOD_DSP_TYPE_REVERB, dspreverb);
		ErrorCheck(result);

		result = system.createDSPByType(FMOD_DSP_TYPE_FLANGE, dspflange);
		ErrorCheck(result);
		result = dspflange.setParameter(FMOD_DSP_FLANGE_RATE.asInt(), 1.0f);
		ErrorCheck(result);

		result = system.createDSPByType(FMOD_DSP_TYPE_LOWPASS, dsplowpass);
		ErrorCheck(result);
		result = dsplowpass.setParameter(FMOD_DSP_LOWPASS_CUTOFF.asInt(), 500.0f);
		ErrorCheck(result);
		
		boolean muteA = true;
		boolean muteB = true;
		boolean reverbA = true;
		boolean flangeB = true;
		boolean lowpass = true;
		
		/*
		 * Main loop.
		 */
		boolean exit = false;
		do
		{
			switch(getKey())
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
				case 'r':
				case 'R':
					
					if(reverbA)
					{
						groupA.addDSP(dspreverb, null);
					}
					else
					{
						dspreverb.remove();
					}
					
					reverbA = !reverbA;
					break;
				case 'f':
				case 'F':
					if(flangeB)
					{
						groupB.addDSP(dspflange, null);
					}
					else
					{
						dspflange.remove();
					}
					
					flangeB = !flangeB;
					break;
				case 'l':
				case 'L':
					if(lowpass)
					{
						masterGroup.addDSP(dsplowpass, null);
					}
					else
					{
						dsplowpass.remove();
					}
					
					lowpass = !lowpass;
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
		for(int i = 0; i < 5; i++)
		{
			if(!sounds[i].isNull()) {
				result = sounds[i].release();
				ErrorCheck(result);
			}
		}
		
		if(!dspreverb.isNull()) {
			result = dspreverb.release();
			ErrorCheck(result);
		}
		if(!dspflange.isNull()) {
			result = dspflange.release();
			ErrorCheck(result);
		}
		if(!dsplowpass.isNull()) {
			result = dsplowpass.release();
			ErrorCheck(result);
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