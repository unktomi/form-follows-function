/*===============================================================================================
Effects Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows how to apply some of the built in software effects to sounds. 
This example filters the global mix.  All software sounds played here would be filtered in the 
same way.
To filter per channel, and not have other channels affected, simply replace system->addDSP with
channel->addDSP.
Note in this example you don't have to add and remove units each time, you could simply add them 
all at the start then use DSP::setActive to toggle them on and off.
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
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_DISTORTION.FMOD_DSP_DISTORTION_LEVEL;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_ECHO.FMOD_DSP_ECHO_DELAY;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_PARAMEQ.FMOD_DSP_PARAMEQ_CENTER;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_PARAMEQ.FMOD_DSP_PARAMEQ_GAIN;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_CHORUS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_DISTORTION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_ECHO;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_FLANGE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_HIGHPASS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_LOWPASS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_PARAMEQ;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_CHANNEL_STOLEN;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_INVALID_HANDLE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.nio.ByteBuffer;

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
public class Effects extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new Effects());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound  sound  = new Sound();

	
	public Effects()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex Effects example."; }
	
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
		Channel	channel       = new Channel();
		DSP		dsplowpass    = new DSP();
		DSP		dsphighpass   = new DSP();
		DSP		dspecho       = new DSP();
		DSP		dspflange     = new DSP();
		DSP		dspdistortion = new DSP();
		DSP		dspchorus     = new DSP();
		DSP		dspparameq    = new DSP();
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
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/drumloop.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_SOFTWARE | FMOD_LOOP_NORMAL | FMOD_OPENMEMORY, exinfo, sound);
		ErrorCheck(result);
		exinfo.release();
		
		printf("===================================================================\n");
		printf("Effects Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===================================================================\n");
		printf("\n");
		printf("Press SPACE to paused/unpause sound.\n");
		printf("Press 1 to toggle dsplowpass effect.\n");
		printf("Press 2 to toggle dsphighpass effect.\n");
		printf("Press 3 to toggle dspecho effect.\n");
		printf("Press 4 to toggle dspflange effect.\n");
		printf("Press 5 to toggle dspdistortion effect.\n");
		printf("Press 6 to toggle dspchorus effect.\n");
		printf("Press 7 to toggle dspparameq effect.\n");
		printf("Press e to quit\n");
		printf("\n");
		
		result = system.playSound(FMOD_CHANNEL_FREE, sound, false, channel);
		ErrorCheck(result);
		
		/*
		 * Create some effects to play with.
		 */
		result = system.createDSPByType(FMOD_DSP_TYPE_LOWPASS, dsplowpass);
		ErrorCheck(result);
		result = system.createDSPByType(FMOD_DSP_TYPE_HIGHPASS, dsphighpass);
		ErrorCheck(result);
		result = system.createDSPByType(FMOD_DSP_TYPE_ECHO, dspecho);
		ErrorCheck(result);
		result = system.createDSPByType(FMOD_DSP_TYPE_FLANGE, dspflange);
		ErrorCheck(result);
		result = system.createDSPByType(FMOD_DSP_TYPE_DISTORTION, dspdistortion);
		ErrorCheck(result);
		result = system.createDSPByType(FMOD_DSP_TYPE_CHORUS, dspchorus);
		ErrorCheck(result);
		result = system.createDSPByType(FMOD_DSP_TYPE_PARAMEQ, dspparameq);
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
					ErrorCheck(result);
					boolean paused = buffer.get(0) != 0;
					
					result = channel.setPaused(!paused);
					ErrorCheck(result);
					break;
				}
				case '1':
				{
					result = dsplowpass.getActive(buffer);
					ErrorCheck(result);
					boolean active = buffer.get(0) != 0;
					
					if(active)
					{
						result = dsplowpass.remove();
						ErrorCheck(result);
					}
					else
					{
						result = system.addDSP(dsplowpass, null);
						ErrorCheck(result);
					}
					break;
				}
				case '2':
				{
					result = dsphighpass.getActive(buffer);
					ErrorCheck(result);
					boolean active = buffer.get(0) != 0;
					
					if(active)
					{
						result = dsphighpass.remove();
						ErrorCheck(result);
					}
					else
					{
						result = system.addDSP(dsphighpass, null);
						ErrorCheck(result);
					}
					break;
				}
				case '3':
				{
					result = dspecho.getActive(buffer);
					ErrorCheck(result);
					boolean active = buffer.get(0) != 0;
					
					
					if(active)
					{
						result = dspecho.remove();
						ErrorCheck(result);
					}
					else
					{
						result = system.addDSP(dspecho, null);
						ErrorCheck(result);
						
						result = dspecho.setParameter(FMOD_DSP_ECHO_DELAY.asInt(), 50.0f);
						ErrorCheck(result);
					}
					break;
				}
				case '4':
				{
					result = dspflange.getActive(buffer);
					ErrorCheck(result);
					boolean active = buffer.get(0) != 0;
					
					if(active)
					{
						result = dspflange.remove();
						ErrorCheck(result);
					}
					else
					{
						result = system.addDSP(dspflange, null);
						ErrorCheck(result);
					}
					break;
				}
				case '5':
				{
					result = dspdistortion.getActive(buffer);
					ErrorCheck(result);
					boolean active = buffer.get(0) != 0;
					
					if(active)
					{
						result = dspdistortion.remove();
						ErrorCheck(result);
					}
					else
					{
						result = system.addDSP(dspdistortion, null);
						ErrorCheck(result);
						
						result = dspdistortion.setParameter(FMOD_DSP_DISTORTION_LEVEL.asInt(), 0.8f);
						ErrorCheck(result);
					}
					break;
				}
				case '6':
				{
					result = dspchorus.getActive(buffer);
					ErrorCheck(result);
					boolean active = buffer.get(0) != 0;
					
					if(active)
					{
						result = dspchorus.remove();
						ErrorCheck(result);
					}
					else
					{
						result = system.addDSP(dspchorus, null);
						ErrorCheck(result);
					}
					break;
				}
				case '7':
				{
					result = dspparameq.getActive(buffer);
					ErrorCheck(result);
					boolean active = buffer.get(0) != 0;
					
					if(active)
					{
						result = dspparameq.remove();
						ErrorCheck(result);
					}
					else
					{
						result = system.addDSP(dspparameq, null);
						ErrorCheck(result);
						
						result = dspparameq.setParameter(FMOD_DSP_PARAMEQ_CENTER.asInt(), 5000.0f);
						ErrorCheck(result);
						result = dspparameq.setParameter(FMOD_DSP_PARAMEQ_GAIN.asInt(), 0.0f);
						ErrorCheck(result);
					}
					break;
				}
				case 'e':
				case 'E': exit = true; break;
			}
			
			system.update();
			
			{
				boolean paused = false;
				
				dsplowpass.getActive(buffer);
				 boolean dsplowpass_active = buffer.get(0) != 0;
				dsphighpass.getActive(buffer);
				 boolean dsphighpass_active = buffer.get(0) != 0;
				dspecho.getActive(buffer);
				 boolean dspecho_active = buffer.get(0) != 0;
				dspflange.getActive(buffer);
				 boolean dspflange_active = buffer.get(0) != 0;
				dspdistortion.getActive(buffer);
				 boolean dspdistortion_active = buffer.get(0) != 0;
				dspchorus.getActive(buffer);
				 boolean dspchorus_active = buffer.get(0) != 0;
				dspparameq.getActive(buffer);
				 boolean dspparameq_active = buffer.get(0) != 0;
				
				if(channel != null && !channel.isNull())
				{
					result = channel.getPaused(buffer);
					if((result != FMOD_OK) && (result != FMOD_ERR_INVALID_HANDLE) && (result != FMOD_ERR_CHANNEL_STOLEN))
						ErrorCheck(result);
					paused = buffer.get(0) != 0;
				}
				
				printfr("%s : lowpass[%c] highpass[%c] echo[%c] flange[%c] dist[%c] chorus[%c] parameq[%c]", 
						paused ? "Paused " : "Playing",
						dsplowpass_active	 ? 'x' : ' ',
						dsphighpass_active	 ? 'x' : ' ',
						dspecho_active		 ? 'x' : ' ',
						dspflange_active	 ? 'x' : ' ',
						dspdistortion_active ? 'x' : ' ',
						dspchorus_active	 ? 'x' : ' ',
						dspparameq_active	 ? 'x' : ' ');
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