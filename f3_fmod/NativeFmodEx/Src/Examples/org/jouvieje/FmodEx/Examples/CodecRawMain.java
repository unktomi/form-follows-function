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
 * I've written this example for using CodecRaw codec plugin.
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
public class CodecRawMain extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new CodecRawMain());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound sound   = new Sound();
	
	public CodecRawMain()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex CodecRaw example."; }
	
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
		
		Channel channel = new Channel();
		FMOD_RESULT result;
		int version;
		
		ByteBuffer buffer = newByteBuffer(SIZEOF_INT);
		
		printf("===================================================================\n");
		printf("CODEC_RAW.DLL  Copyright (c), Firelight Technologies Pty, Ltd 2005.\n");
		printf("===================================================================\n");
		printf("Usage:    CodecRaw <filename>.raw\n");
		printf("Examples: CodecRaw output.raw\n");
		printf("(output.raw can be generate with 'OfflineDecoding' examples)\n\n");
		resetInput();
		setInput("output.raw");
		while(!keyHit()) {
			Thread.yield();
		}
		String path = getInput();
		
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
		
		result = system.createCodec(CodecRaw.FMODGetCodecDescription());	//Platform INDependant way [Preferred - Pack your plugins in jar instead of library]
		ErrorCheck(result);
//		result = system.loadPlugin("lib/codec_raw.dll", null, null);		//Platform Dependant way
//		ErrorCheck(result);
		
		result = system.createSound(path, FMOD_DEFAULT | FMOD_SOFTWARE | FMOD_LOOP_NORMAL, null, sound);
		ErrorCheck(result);
		
		result = system.playSound(FMOD_CHANNEL_FREE, sound, false, channel);
		ErrorCheck(result);
		
		printf("Press 'ENTER' to quit\n");
		printf("\n");
		
		/*
		 * Main loop.
		 */
		resetInput();
		do
		{
			system.update();
			
			try {
				Thread.sleep(10);
			} catch(InterruptedException e1){}
		}
		while(!keyHit() && !deinit);

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