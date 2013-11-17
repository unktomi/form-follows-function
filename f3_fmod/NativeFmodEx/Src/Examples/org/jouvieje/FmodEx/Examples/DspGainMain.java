package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_DEFAULT;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newFloatBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.swing.JPanel;

import org.jouvieje.FmodEx.Channel;
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
 * I've written this example for using DspGain dsp plugin.
 * 
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * My web sites :
 * 		http://topresult.tomato.co.uk/~jerome/
 * 		http://jerome.jouvie.free.fr/
 */
public class DspGainMain extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new DspGainMain());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound  sound  = new Sound();
	
	public DspGainMain()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex DspGain example."; }
	
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
		Channel channel = new Channel();
		FMOD_RESULT result;
		int version;
		
		ByteBuffer buffer = newByteBuffer(SIZEOF_INT);
		
		/*
		 * Global Settings
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
		
		/**
		 * Jouvieje comment :
		 *  Creates our DSP Unit
		 */
		DSP myDsp = new DSP();
		result = system.createDSP(DspGain.FMODGetDSPDescription(), myDsp);
		ErrorCheck(result);
		
		/**
		 * Jouvieje comment :
		 *  Get dspGain min and max
		 */
		FloatBuffer gainMin = newFloatBuffer(1);
		FloatBuffer gainMax = newFloatBuffer(1);
		myDsp.getParameterInfo(0, null, null, null, 0, gainMin, gainMax);
		
		/**
		 * Jouvieje comment :
		 *  Set the wanted DSP Gain
		 */
		float dspGain = 0.5f;
		myDsp.setParameter(0, dspGain);
		
		if(true)
		{
			/**
			 * Jouvieje comment :
			 *  Insert DSP unit at the head of the DSP chain.
			 */
			result = system.addDSP(myDsp, null);
			ErrorCheck(result);
			
			/**
			 * Jouvieje comment :
			 *  FMOD_SOFTWARE to use DSP
			 */
			soundBuffer = Medias.loadMediaIntoMemory("/Media/wave.mp3");
			exinfo = FMOD_CREATESOUNDEXINFO.create();
			exinfo.setLength(soundBuffer.capacity());
			result = system.createSound(soundBuffer, FMOD_DEFAULT | FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sound);
			ErrorCheck(result);
			exinfo.release();
			
			result = system.playSound(FMOD_CHANNEL_FREE, sound, false, channel);
			ErrorCheck(result);
		}
		else
		{
			soundBuffer = Medias.loadMediaIntoMemory("/Media/wave.mp3");
			exinfo = FMOD_CREATESOUNDEXINFO.create();
			exinfo.setLength(soundBuffer.capacity());
			result = system.createSound(soundBuffer, FMOD_DEFAULT | FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sound);
			ErrorCheck(result);
			exinfo.release();
			
			result = system.playSound(FMOD_CHANNEL_FREE, sound, true, channel);
			ErrorCheck(result);
			
			result = myDsp.setActive(true);
			ErrorCheck(result);
			
			result = channel.addDSP(myDsp, null);
			ErrorCheck(result);
			
			result = channel.setPaused(false);
			ErrorCheck(result);
		}
		
		printf("===================================================================\n");
		printf("DSP_GAIN.DLL.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===================================================================\n");
		printf("Press '+' to increase DSP Gain\n");
		printf("Press '-' to decrease DSP Gain\n");
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
				case '+':
					dspGain += 0.1f;
					if(dspGain > gainMax.get(0))
						dspGain = gainMax.get(0);
					myDsp.setParameter(0, dspGain);
					break;
				case '-':
					dspGain -= 0.1f;
					if(dspGain < gainMin.get(0))
						dspGain = gainMin.get(0);
					myDsp.setParameter(0, dspGain);
					break;
				case 'e':
				case 'E': exit = true; break;
			}
			
			system.update();
			
			printfr("DSP Gain = %1.1f", dspGain);
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e1){}
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