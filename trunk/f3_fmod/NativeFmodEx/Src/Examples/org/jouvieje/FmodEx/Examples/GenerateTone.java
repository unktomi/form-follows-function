/*===============================================================================================
GenerateTone Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows how simply play generated tones using FMOD::System::payDSP instead of 
manually connecting and disconnecting DSP units.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;


import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_REUSE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_OSCILLATOR.FMOD_DSP_OSCILLATOR_RATE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_OSCILLATOR.FMOD_DSP_OSCILLATOR_TYPE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_OSCILLATOR;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.nio.ByteBuffer;

import javax.swing.JPanel;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.DSP;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Examples.Utils.ConsoleGUI;
import org.jouvieje.FmodEx.Examples.Utils.FmodExExampleFrame;
import org.jouvieje.FmodEx.Exceptions.InitException;

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
public class GenerateTone extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new GenerateTone());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private DSP    dsp    = new DSP();
	
	public GenerateTone()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex GenerateTone example."; }
	
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
		FMOD_RESULT result;
		int version;
		
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
		
		/*
		 * Create an oscillator DSP units for the tone.
		 */
		result = system.createDSPByType(FMOD_DSP_TYPE_OSCILLATOR, dsp);
		ErrorCheck(result);
		result = dsp.setParameter(FMOD_DSP_OSCILLATOR_RATE.asInt(), 440.0f);       /* musical note 'A' */
		ErrorCheck(result);
		
		printf("======================================================================\n");
		printf("GenerateTone Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("======================================================================\n\n");
		printf("\n");
		printf("Press '1' to play a sine wave\n");
		printf("Press '2' to play a square wave\n");
		printf("Press '3' to play a saw wave\n");
		printf("Press '4' to play a triangle wave\n");
		printf("Press '5' to play a white noise\n");
		printf("Press 's' to stop channel\n");
		printf("\n");
		printf("Press 'v'/'V' to change channel volume\n");
		printf("Press 'f'/'F' to change channel frequency\n");
		printf("Press '['/']' to change channel pan\n");
		printf("Press 'E' to quit\n");
		printf("\n");
		
		/*
		 * Main loop
		 */
		boolean exit = false;
		do
		{
			switch(getKey())
			{
				case '1':
				{
					result = system.playDSP(FMOD_CHANNEL_REUSE, dsp, true, channel);
//					ErrorCheck(result);
					result = dsp.setParameter(FMOD_DSP_OSCILLATOR_TYPE.asInt(), 0);
					ErrorCheck(result);
					if(!channel.isNull())
					{
						channel.setVolume(0.5f);
						channel.setPaused(false);
					}
					break;
				}
				case '2':
//				{
					result = system.playDSP(FMOD_CHANNEL_REUSE, dsp, true, channel);
//					ErrorCheck(result);
					result = dsp.setParameter(FMOD_DSP_OSCILLATOR_TYPE.asInt(), 1);
					ErrorCheck(result);
					if(!channel.isNull())
					{
						channel.setVolume(0.125f);
						channel.setPaused(false);
					}
					break;
//				}
				case '3':
				{
					result = system.playDSP(FMOD_CHANNEL_REUSE, dsp, true, channel);
//					ErrorCheck(result);
					result = dsp.setParameter(FMOD_DSP_OSCILLATOR_TYPE.asInt(), 2);
					ErrorCheck(result);
					if(!channel.isNull())
					{
						channel.setVolume(0.125f);
						channel.setPaused(false);
					}
					break;
				}
				case '4':
				{
					result = system.playDSP(FMOD_CHANNEL_REUSE, dsp, true, channel);
//					ErrorCheck(result);
					result = dsp.setParameter(FMOD_DSP_OSCILLATOR_TYPE.asInt(), 4);
					ErrorCheck(result);
					if(!channel.isNull())
					{
						channel.setVolume(0.5f);
						channel.setPaused(false);
					}
					break;
				}
				case '5':
				{
					result = system.playDSP(FMOD_CHANNEL_REUSE, dsp, true, channel);
//					ErrorCheck(result);
					result = dsp.setParameter(FMOD_DSP_OSCILLATOR_TYPE.asInt(), 5);
					ErrorCheck(result);
					if(!channel.isNull())
					{
						channel.setVolume(0.25f);
						channel.setPaused(false);
					}
					break;
				}
				case 's':
				{
					if(!channel.isNull())
					{
						result = channel.stop();
						ErrorCheck(result);
					}
					break;
				}
				case 'v':
				{
					if(!channel.isNull())
					{
						channel.getVolume(buffer.asFloatBuffer());
						float volume = buffer.getFloat(0);
						
						volume -= 0.1f;
						channel.setVolume(volume);
					}
					break;
				}
				case 'V':
				{
					if(!channel.isNull())
					{
						channel.getVolume(buffer.asFloatBuffer());
						float volume = buffer.getFloat(0);
						
						volume += 0.1f;
						channel.setVolume(volume);
					}
					break;
				}
				case 'f':
				{
					if(!channel.isNull())
					{
						channel.getFrequency(buffer.asFloatBuffer());
						float frequency = buffer.getFloat(0);
						
						frequency -= 500.0f;
						channel.setFrequency(frequency);
					}
					break;
				}
				case 'F':
				{
					if(!channel.isNull())
					{
						channel.getFrequency(buffer.asFloatBuffer());
						float frequency = buffer.getFloat(0);
						
						frequency += 500.0f;
						channel.setFrequency(frequency);
					}
					break;
				}
				case '[':
				{
					channel.getPan(buffer.asFloatBuffer());
					float pan = buffer.getFloat(0);
					
					pan -= 0.1f;
					channel.setPan(pan);
					break;
				}
				case ']':
				{
					channel.getPan(buffer.asFloatBuffer());
					float pan = buffer.getFloat(0);
					
					pan += 0.1f;
					channel.setPan(pan);
					break;
				}
				case 'e':
				case 'E': exit = true;
			}
			
			system.update();
			
			{
				float frequency = 0, volume = 0, pan = 0;
				boolean playing = false;
				
				if(!channel.isNull())
				{
					channel.getFrequency(buffer.asFloatBuffer());
					frequency = buffer.getFloat(0);
					
					channel.getVolume(buffer.asFloatBuffer());
					volume = buffer.getFloat(0);
					
					channel.getPan(buffer.asFloatBuffer());
					pan = buffer.getFloat(0);
					
					channel.isPlaying(buffer);
					playing = buffer.get(0) != 0;
				}
				
				printfr("Channel %s : Frequency %.1f Volume %.1f Pan %.1f",
						playing ? "playing" : "stopped", frequency, volume, pan);
			}
			
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
		if(!dsp.isNull()) {
			result = dsp.release();
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