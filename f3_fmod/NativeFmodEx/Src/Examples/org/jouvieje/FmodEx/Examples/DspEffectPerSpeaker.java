/*===============================================================================================
 DSP Effect per speaker Example
 Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

 This example shows how to manipulate a DSP network and as an example, creates 2 dsp effects,
 and splits a single sound into 2 audio paths, which it then filters seperately.
 To only have each audio path come out of one speaker each, DSPConnection::setLevels is used just
 before the 2 branches merge back together again.

 For more speakers:
 1. Use System::setSpeakerMode or System::setOutputFormat.
 2. Create more effects, currently 2 for stereo (lowpass and chorus), create one per speaker.
 3. Under the 'Now connect the 2 effects to channeldsp head.' section, connect the extra effects
    by duplicating the code more times.
 4. Filter each effect to each speaker by calling DSP::setInputLevels.  Expand the existing code
    by extending the level arrays from 2 to the number of speakers you require, and change the
    numlevels parameter in DSP::setInputLevels from 2 to the correct number accordingly.

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
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_CHORUS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_LOWPASS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SPEAKER.FMOD_SPEAKER_FRONT_LEFT;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SPEAKER.FMOD_SPEAKER_FRONT_RIGHT;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newFloatBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.swing.JPanel;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.DSP;
import org.jouvieje.FmodEx.DSPConnection;
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
public class DspEffectPerSpeaker extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new DspEffectPerSpeaker());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private boolean lowpass = false;
	private boolean chorus = false;
	
	private System system     = new System();
	private Sound  sound      = new Sound();
	private DSP    dsplowpass = new DSP();
	private DSP    dspchorus  = new DSP();

	
	public DspEffectPerSpeaker()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex DspEffectPerSpeaker example."; }
	
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
		DSPConnection dsplowpassconnection = new DSPConnection();
		DSPConnection dspchorusconnection  = new DSPConnection();
		Channel  channel         = new Channel();
		DSP      dsphead         = new DSP();
		DSP      dspchannelmixer = new DSP();
		FMOD_RESULT result;
		int version;
		float pan = 0;
		
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
		result = system.createSound(soundBuffer, FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sound);
		ErrorCheck(result);
		exinfo.release();
		
		printf("===============================================================================\n");
		printf("DSP effect per speaker example. Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===============================================================================\n");
		printf("Press 'L' to toggle lowpass on/off on left speaker only\n");
		printf("Press 'R' to toggle chorus on/off on right speaker only\n");
		printf("Press '[' to pan sound left\n");
		printf("Press ']' to pan sound right\n");
		printf("Press 'E' to quit\n");
		printf("\n");
		
		result = system.playSound(FMOD_CHANNEL_FREE, sound, false, channel);
		ErrorCheck(result);
		
		/*
		 * Create the DSP effects.
		 */  
		result = system.createDSPByType(FMOD_DSP_TYPE_LOWPASS, dsplowpass);
		ErrorCheck(result);
		
		result = system.createDSPByType(FMOD_DSP_TYPE_CHORUS, dspchorus);
		ErrorCheck(result);
		
		/*
		 * Connect up the DSP network
		 */
		
		/*
		 * When a sound is played, a subnetwork is set up in the DSP network which looks like this.
		 * Wavetable is the drumloop sound, and it feeds its data from right to left.
		 * 
		 * [DSPHEAD]<------------[DSPCHANNELMIXER]
		 */  
		result = system.getDSPHead(dsphead);
		ErrorCheck(result);
		
		result = dsphead.getInput(0, dspchannelmixer, null);
		ErrorCheck(result);
		
		/*
		 * Now disconnect channeldsp head from wavetable to look like this.
		 * 
		 * [DSPHEAD]           [DSPCHANNELMIXER]
		 */
		result = dsphead.disconnectFrom(dspchannelmixer);
		ErrorCheck(result);
		
	    /*
	     * Now connect the 2 effects to channeldsp head.  
	     * Store the 2 connections this makes so we can set their speakerlevels later.
	     * 
	     *          [DSPLOWPASS]
	     *         /x           
	     * [DSPHEAD]           [DSPCHANNELMIXER]
	     *         \y           
	     *          [DSPCHORUS]
	     */
		result = dsphead.addInput(dsplowpass, dsplowpassconnection);	//x = dsplowpassconnection
		ErrorCheck(result);
		result = dsphead.addInput(dspchorus, dspchorusconnection);		//y = dspchorusconnection
		ErrorCheck(result);
		
	    /*
	     * Now connect the wavetable to the 2 effects
	     * 
	     *          [DSPLOWPASS]
	     *         /x          \ 
	     * [DSPHEAD]           [DSPCHANNELMIXER]
	     *         \y          /
	     *          [DSPCHORUS]
	     */
		result = dsplowpass.addInput(dspchannelmixer, null);	//Null for connection - we dont care about it.
		ErrorCheck(result);
		result = dspchorus.addInput(dspchannelmixer, null);		//Null for connection - we dont care about it.
		ErrorCheck(result);
		
	    /*
	     * Now the drumloop will be twice as loud, because it is being split into 2, then recombined at the end.
	     * What we really want is to only feed the dspchannelmixer->dsplowpass through the left speaker, and 
	     * dspchannelmixer->dspchorus to the right speaker.
	     * We can do that simply by setting the pan, or speaker levels of the connections.
	     *          [DSPLOWPASS]
	     *         /x=1,0      \ 
	     * [DSPHEAD]           [DSPCHANNELMIXER]
	     *         \y=0,1      /
	     *          [DSPCHORUS]
	     */
		{
			FloatBuffer leftinputon  = newFloatBuffer(2);
			leftinputon.put(0, 1.0f);
			leftinputon.put(1, 0.0f);
			FloatBuffer rightinputon = newFloatBuffer(2);
			rightinputon.put(0, 0.0f);
			rightinputon.put(1, 1.0f);
			FloatBuffer inputsoff    = newFloatBuffer(2);
			inputsoff.put(0, 0.0f);
			inputsoff.put(1, 0.0f);
			
			result = dsplowpassconnection.setLevels(FMOD_SPEAKER_FRONT_LEFT, leftinputon, 2);
			ErrorCheck(result);
			result = dsplowpassconnection.setLevels(FMOD_SPEAKER_FRONT_RIGHT, inputsoff, 2);
			ErrorCheck(result);
			
			result = dspchorusconnection.setLevels(FMOD_SPEAKER_FRONT_LEFT, inputsoff, 2);
			ErrorCheck(result);
			result = dspchorusconnection.setLevels(FMOD_SPEAKER_FRONT_RIGHT, rightinputon, 2);
			ErrorCheck(result);
		}
		
		result = dsplowpass.setBypass(true);
		result = dspchorus.setBypass(true);
		
		result = dsplowpass.setActive(true);
		result = dspchorus.setActive(true);
		
		/*
		 * Main loop.
		 */
		boolean exit = false;
		do
		{
			switch(getKey())
			{
				case 'l': 
				case 'L': 
				{
					dsplowpass.setBypass(lowpass);
					
					lowpass = !lowpass;
					break;
				}
				case 'r': 
				case 'R': 
				{
					dspchorus.setBypass(chorus);
					
					chorus = !chorus;
					break;
				}
				case '[':
				{
					channel.getPan(buffer.asFloatBuffer());
					pan = buffer.getFloat(0);
					
					pan -= 0.1f;
					if (pan < -1)
					{
						pan = -1;
					}
					channel.setPan(pan);
					break;
				}
				case ']':
				{
					channel.getPan(buffer.asFloatBuffer());
					pan = buffer.getFloat(0);
					
					pan += 0.1f;
					if (pan > 1)
					{
						pan = 1;
					}
					channel.setPan(pan);
					break;
				}
				case 'e': 
				case 'E': exit = true; break; 
			}
			
			system.update();
			
			{
				system.getChannelsPlaying(buffer.asIntBuffer());
				int channelsplaying = buffer.getInt(0);
				
				printfr("Channels Playing %2d : Pan = %.02f", channelsplaying, pan);
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
		
		if(!dsplowpass.isNull()) {
			result = dsplowpass.release();
			ErrorCheck(result);
		}
		if(!dspchorus.isNull()) {
			result = dspchorus.release();
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