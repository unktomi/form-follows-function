/*===============================================================================================
 Custom DSP Example
 Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

 This example shows how to add a user created DSP callback to process audio data.
 A read callback is generated at runtime, and can be added anywhere in the DSP network.

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
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
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
import org.jouvieje.FmodEx.Callbacks.FMOD_DSP_READCALLBACK;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Examples.Utils.ConsoleGUI;
import org.jouvieje.FmodEx.Examples.Utils.FmodExExampleFrame;
import org.jouvieje.FmodEx.Examples.Utils.Medias;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.Structures.FMOD_CREATESOUNDEXINFO;
import org.jouvieje.FmodEx.Structures.FMOD_DSP_DESCRIPTION;
import org.jouvieje.FmodEx.Structures.FMOD_DSP_STATE;

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
public class DspCustom extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new DspCustom());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	public DspCustom()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex DspCustom example."; }
	
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_RESULT.FMOD_OK)
		{
			printfExit("FMOD error! (%d) %s\n", result.asInt(), FmodEx.FMOD_ErrorString(result));
		}
	}
	

	private boolean active = false;
	
	private System system = new System();
	private Sound  sound  = new Sound();
	private DSP    mydsp  = new DSP();
	
	private FMOD_DSP_READCALLBACK myDSPCallback = new FMOD_DSP_READCALLBACK(){
		ByteBuffer nameBuffer = newByteBuffer(256);
		public FMOD_RESULT FMOD_DSP_READCALLBACK(FMOD_DSP_STATE dsp_state, FloatBuffer inbuffer, FloatBuffer outbuffer,
				int length, int inchannels, int outchannels)
		{
			DSP thisdsp = dsp_state.getInstance(); 
			
			/*
			 * This redundant call just shows using the instance parameter of FMOD_DSP_STATE and using it to 
			 * call a DSP information function. 
			 */
			thisdsp.getInfo(nameBuffer, null, null, null, null);
			String name = BufferUtils.toString(nameBuffer);
			
			/*
			 * This loop assumes inchannels = outchannels, which it will be if the DSP is created with '0' 
			 * as the number of channels in FMOD_DSP_DESCRIPTION.  
			 * Specifying an actual channel count will mean you have to take care of any number of channels coming in,
			 * but outputting the number of channels specified.  Generally it is best to keep the channel 
			 * count at 0 for maximum compatibility.
			 */
			for(int sample = 0; sample < length; sample++) 
			{
				/*
				 * Feel free to unroll this.
				 */
				for(int channel = 0; channel < inchannels; channel++)
				{
					/*
					 * This DSP filter just halves the volume! 
					 * Input is modified, and sent to output.
					 */
					/*
					 * Jouvieje note:
					 *  this is valide only if inchannels == outchannels
					 */
					outbuffer.put(inbuffer.get() * 0.2f);
					//Otherwise use :
//					outbuffer.put((sample * outchannels) + channel, inbuffer.get() * 0.2f);
				}
			}
			inbuffer.rewind();
			outbuffer.rewind();
			
			return FMOD_OK; 
		}
	};
	
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
		Channel      channel = new Channel();
		FMOD_RESULT  result;
		int          version;
		
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
		printf("Custom DSP example. Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("===============================================================================\n");
		printf("Press 'f' to activate, deactivate user filter\n");
		printf("Press 'e' to quit\n");
		printf("\n");
		
		result = system.playSound(FMOD_CHANNEL_FREE, sound, false, channel);
		ErrorCheck(result);
		
		/*
		 * Create the DSP effects.
		 */  
		{ 
			FMOD_DSP_DESCRIPTION dspdesc = FMOD_DSP_DESCRIPTION.create(); 
			
			dspdesc.setName("My first DSP unit");
			dspdesc.setChannels(0);						// 0 = whatever comes in, else specify. 
			dspdesc.setRead(myDSPCallback); 
			
			result = system.createDSP(dspdesc, mydsp); 
			ErrorCheck(result); 
		} 
		
		/*
		 * Inactive by default.
		 */
		mydsp.setBypass(true);
		
		result = system.addDSP(mydsp, null); 
		
		/*
		 * Main loop.
		 */
		boolean exit = false;
		do
		{
			switch(getKey())
			{
				case 'f': 
				case 'F': 
					mydsp.setBypass(active);
					active = !active;
					break;
				case 'e': 
				case 'E': exit = true; break;
			}
			
			system.update();
			
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
		if(!mydsp.isNull()) {
			result = mydsp.release();
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