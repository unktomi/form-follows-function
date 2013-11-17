/*===============================================================================================
3d Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows how to basic 3d positioning
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static java.lang.Math.sin;
import static org.jouvieje.FmodEx.Defines.FMOD_CAPS.FMOD_CAPS_HARDWARE_EMULATED;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_3D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_RESAMPLER.FMOD_DSP_RESAMPLER_LINEAR;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_OUTPUT_CREATEBUFFER;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCMFLOAT;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SPEAKERMODE.FMOD_SPEAKERMODE_STEREO;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newIntBuffer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.print.attribute.standard.Media;
import javax.swing.JPanel;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Enumerations.FMOD_SPEAKERMODE;
import org.jouvieje.FmodEx.Examples.Utils.ConsoleGUI;
import org.jouvieje.FmodEx.Examples.Utils.FmodExExampleFrame;
import org.jouvieje.FmodEx.Examples.Utils.Medias;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.Structures.FMOD_CREATESOUNDEXINFO;
import org.jouvieje.FmodEx.Structures.FMOD_VECTOR;

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
public class _3D extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new _3D());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private final static int INTERFACE_UPDATETIME = 50;        // 50ms update for interface
	private final static float DISTANCEFACTOR = 1.0f;          // Units per meter.  I.e feet would = 3.28.  centimeters would = 100.

	private System system = new System();
	private Sound sound1 = new Sound();
	private Sound sound2 = new Sound();
	private Sound sound3 = new Sound();
	
	public _3D()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex 3D example."; }
	
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
		Channel channel1 = new Channel();
		Channel channel2 = new Channel();
		Channel channel3 = new Channel();
		FMOD_RESULT result;
		FMOD_SPEAKERMODE[] speakermode = new FMOD_SPEAKERMODE[1];
		
		FMOD_VECTOR listenerpos  = FMOD_VECTOR.create(0.0f, 0.0f, -1.0f * DISTANCEFACTOR);
		FMOD_VECTOR lastpos = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);
		boolean listenerflag = true;
		float t = 0;
		
		ByteBuffer buffer = newByteBuffer(256);
		
		printf("===============================================================\n");
		printf("3d Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===============================================================\n\n");
		printf("This example plays 2 3D sounds in hardware.  Optionally you can\n");
		printf("play a 2D hardware sound as well.\n");
		printf("===============================================================\n\n");
		
		/*
		 * Create a System object and initialize.
		 */
		result = FmodEx.System_Create(system);
		ErrorCheck(result);
		
		result = system.getVersion(buffer.asIntBuffer());
		ErrorCheck(result);
		int version = buffer.getInt(0);
		
		if(version < FMOD_VERSION)
		{
			printf("Error!  You are using an old version of FMOD %08x.  This program requires %08x\n", version, FMOD_VERSION);
			return;
		}

		result = system.getDriverCaps(0, buffer.asIntBuffer(), null, null, speakermode);
		ErrorCheck(result);
		int caps = buffer.getInt(0);

		result = system.setSpeakerMode(speakermode[0]);       /* Set the user selected speaker mode. */
		ErrorCheck(result);
	    
	    if((caps & FMOD_CAPS_HARDWARE_EMULATED) != 0)             /* The user has the 'Acceleration' slider set to off!  This is really bad for latency!. */
	    {                                                   /* You might want to warn the user about this. */
	        result = system.setDSPBufferSize(1024, 10);
	        ErrorCheck(result);
	    }
	    
	    result = system.getDriverInfo(0, buffer, buffer.capacity(), null);
	    ErrorCheck(result);
	    String name = BufferUtils.toString(buffer);

	    if(name.equals("SigmaTel"))   /* Sigmatel sound devices crackle for some reason if the format is PCM 16bit.  PCM floating point output seems to solve it. */
	    {
	        result = system.setSoftwareFormat(48000, FMOD_SOUND_FORMAT_PCMFLOAT, 0, 0, FMOD_DSP_RESAMPLER_LINEAR);
	        ErrorCheck(result);
	    }
		
		result = system.init(100, FMOD_INIT_NORMAL, null);
	    if(result == FMOD_ERR_OUTPUT_CREATEBUFFER)         /* Ok, the speaker mode selected isn't supported by this soundcard.  Switch it back to stereo... */
	    {
	        result = system.setSpeakerMode(FMOD_SPEAKERMODE_STEREO);
	        ErrorCheck(result);
	                
	        result = system.init(100, FMOD_INIT_NORMAL, null);/* ... and re-init. */
	        ErrorCheck(result);
	    }
	    
	    /*
	     * Set the distance units. (meters/feet etc).
	     */
		result = system.set3DSettings(1.0f, DISTANCEFACTOR, 1.0f);
		ErrorCheck(result);
		
		/*
		 * Load some sounds
		 */
		soundBuffer = Medias.loadMediaIntoMemory("/Media/drumloop.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_3D | FMOD_OPENMEMORY, exinfo, sound1);
		ErrorCheck(result);
	    result = sound1.set3DMinMaxDistance(0.5f * DISTANCEFACTOR, 5000.0f * DISTANCEFACTOR);
		ErrorCheck(result);
		result = sound1.setMode(FMOD_LOOP_NORMAL);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/jaguar.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_3D | FMOD_OPENMEMORY, exinfo, sound2);
		ErrorCheck(result);
		result = sound2.set3DMinMaxDistance(0.5f * DISTANCEFACTOR, 5000.0f * DISTANCEFACTOR);
		ErrorCheck(result);
		result = sound2.setMode(FMOD_LOOP_NORMAL);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		soundBuffer = Medias.loadMediaIntoMemory("/Media/swish.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(soundBuffer.capacity());
		result = system.createSound(soundBuffer, FMOD_SOFTWARE | FMOD_2D | FMOD_OPENMEMORY, exinfo, sound3);
		ErrorCheck(result);
		soundBuffer = null; exinfo.release();
		
		/*
		 * Play sounds at certain positions
		 */
		{
			FMOD_VECTOR pos = FMOD_VECTOR.create(-10.0f * DISTANCEFACTOR, 0.0f, 0.0f);
			FMOD_VECTOR vel = FMOD_VECTOR.create(0.0f,  0.0f, 0.0f);
			
			result = system.playSound(FMOD_CHANNEL_FREE, sound1, true, channel1);
			ErrorCheck(result);
			result = channel1.set3DAttributes(pos, vel);
			ErrorCheck(result);
			result = channel1.setPaused(false);
			ErrorCheck(result);
		}
		
		{
			FMOD_VECTOR pos = FMOD_VECTOR.create(15.0f * DISTANCEFACTOR, 0.0f, 0.0f);
			FMOD_VECTOR vel = FMOD_VECTOR.create(0.0f,  0.0f,  0.0f);
			
			result = system.playSound(FMOD_CHANNEL_FREE, sound2, true, channel2);
			ErrorCheck(result);
			result = channel2.set3DAttributes(pos, vel);
			ErrorCheck(result);
			result = channel2.setPaused(false);
			ErrorCheck(result);
		}
		
		/*
		 * Display help
		 */
		{
			int num3d = 0, num2d = 0;
			
			IntBuffer iBuffer = newIntBuffer(1);
			result = system.getHardwareChannels(buffer.asIntBuffer(), iBuffer, null);
			ErrorCheck(result);
			num3d = buffer.getInt(0);
			num2d = iBuffer.get(0);
			
			printf("Hardware 2D channels : %d\n", num2d);
			printf("Hardware 3D channels : %d\n", num3d);
		}
		
		print("=========================================================================\n");
		print("Press 1        Pause/Unpause 16bit 3D sound at any time\n");
		print("      2        Pause/Unpause 8bit 3D sound at any time\n");
		print("      3        Play 16bit STEREO 2D sound at any time\n");
		print("      <        Move listener left (in still mode)\n");
		print("      >        Move listener right (in still mode)\n");
		print("      SPACE    Stop/Start listener automatic movement\n");
		print("      E        Quit\n");
		print("=========================================================================\n");
		
		/*
		 * Main loop
		 */
		FMOD_VECTOR forward  = FMOD_VECTOR.create(0.0f, 0.0f, 1.0f);
		FMOD_VECTOR up       = FMOD_VECTOR.create(0.0f, 1.0f, 0.0f);
		FMOD_VECTOR vel      = FMOD_VECTOR.create();
		
		boolean exit = false;
		do
		{
			switch(getKey())
			{
				case '1':
					channel1.getPaused(buffer);
					boolean paused = buffer.get(0) != 0;
					channel1.setPaused(!paused);
					break;
				case '2':
					channel2.getPaused(buffer);
					paused = buffer.get(0) != 0;
					channel2.setPaused(!paused);
					break;
				case '3':
					result = system.playSound(FMOD_CHANNEL_FREE, sound3, false, channel3);
					ErrorCheck(result);
					break;
				case ' ':
					listenerflag = !listenerflag;
					break;
				case '<':
					if(!listenerflag)
					{
						listenerpos.setX(listenerpos.getX() - 1.0f * DISTANCEFACTOR);
						if(listenerpos.getX() < -35 * DISTANCEFACTOR)
							listenerpos.setX(-35 * DISTANCEFACTOR);
					}
					break;
				case '>':
					if(!listenerflag)
					{
						listenerpos.setX(listenerpos.getX() + 1.0f * DISTANCEFACTOR);
						if(listenerpos.getX() > 36 * DISTANCEFACTOR)
							listenerpos.setX(36 * DISTANCEFACTOR);
					}
					break;
				case 'e':
				case 'E': exit = true; break;
			}
				
			// ==========================================================================================
			// UPDATE THE LISTENER
			// ==========================================================================================
			{
				if(listenerflag)
				{
					listenerpos.setX((float)sin(t * 0.05f) * 33.0f * DISTANCEFACTOR); // left right pingpong
				}
				
				// ********* NOTE ******* READ NEXT COMMENT!!!!!
				// vel = how far we moved last FRAME (m/f), then time compensate it to SECONDS (m/s).
				vel.setX( (listenerpos.getX()-lastpos.getX()) * (1000/INTERFACE_UPDATETIME) );
				vel.setY( (listenerpos.getY()-lastpos.getY()) * (1000/INTERFACE_UPDATETIME) );
				vel.setZ( (listenerpos.getZ()-lastpos.getZ()) * (1000/INTERFACE_UPDATETIME) );
				
				// store pos for next time
				lastpos.setXYZ(listenerpos);
				
				result = system.set3DListenerAttributes(0, listenerpos, vel, forward, up);
				ErrorCheck(result);
				
				t += 30 / (float)INTERFACE_UPDATETIME;    // t is just a time value .. it increments in 30m/s steps in this example
				
				// print out a small visual display
				{
//	                FloatBuffer aud1 = BufferUtils.newFloatBuffer(1);
//	                FloatBuffer aud2 = BufferUtils.newFloatBuffer(1);
//	                channel1.getAudibility(aud1);
//	                channel2.getAudibility(aud2);
	                
	                StringBuilder sb = new StringBuilder();
//	                sb.append(String.format("|.......................<1>......................<2>....................|%.1f %.1f", aud1.get(0), aud2.get(0)));
	                sb.append("|.......................<1>......................<2>....................|");
	                sb.setCharAt((int)(listenerpos.getX() / DISTANCEFACTOR) + 35, 'L');
	                
	                printr(sb.toString());
				}
			}
			
			system.update();
			
			try {
				Thread.sleep(INTERFACE_UPDATETIME - 1);
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