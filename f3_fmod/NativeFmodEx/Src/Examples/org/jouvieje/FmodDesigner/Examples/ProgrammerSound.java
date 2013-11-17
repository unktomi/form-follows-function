/*===============================================================================================
 programmer_sound Example
 Copyright (c), Firelight Technologies Pty, Ltd 2006-2007.

 Demonstrates how to use the "programmer sound" feature of the FMOD event system
===============================================================================================*/

package org.jouvieje.FmodDesigner.Examples;

import java.nio.ByteBuffer;

import javax.swing.JPanel;

import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_INITFLAGS.FMOD_EVENT_INIT_NORMAL;
import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_MODE.FMOD_EVENT_DEFAULT;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_JAR_VERSION;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_LIBRARY_VERSION;
import static org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_CALLBACKTYPE.FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_CREATE;
import static org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_CALLBACKTYPE.FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_RELEASE;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;

import org.jouvieje.FileSystem.JARFileSystem;
import org.jouvieje.FmodDesigner.Event;
import org.jouvieje.FmodDesigner.EventGroup;
import org.jouvieje.FmodDesigner.EventSystem;
import org.jouvieje.FmodDesigner.FmodDesigner;
import org.jouvieje.FmodDesigner.InitFmodDesigner;
import org.jouvieje.FmodDesigner.Callbacks.FMOD_EVENT_CALLBACK;
import org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_CALLBACKTYPE;
import org.jouvieje.FmodDesigner.Structures.FMOD_EVENT_LOADINFO;
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
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.Misc.Pointer;
import org.jouvieje.FmodEx.Structures.FMOD_CREATESOUNDEXINFO;

/**
 * I've ported the C++ FMOD Designer example to NativeFmodEx.
 * 
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * Site :
 * 		http://jerome.jouvie.free.fr/
 */
public class ProgrammerSound extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new ProgrammerSound());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
    private EventSystem eventsystem = new EventSystem();
    
	private int g_subsound_index = 0;
	
	private Sound  fsb;
	private System sys;
	
	public ProgrammerSound()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Designer ProgrammerSound example."; }
	
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_RESULT.FMOD_OK)
		{
			printfExit("FMOD error! (%d) %s\n", result.asInt(), FmodEx.FMOD_ErrorString(result));
		}
	}
	
	private FMOD_EVENT_CALLBACK eventcallback = new FMOD_EVENT_CALLBACK(){
		private ByteBuffer buffer = BufferUtils.newByteBuffer(128);
		public FMOD_RESULT FMOD_EVENT_CALLBACK(Event event, FMOD_EVENT_CALLBACKTYPE type, Pointer param1, Pointer param2, Pointer userdata)
		{
			FMOD_RESULT result;

			if(type == FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_CREATE)
			{
				//Read [in]
//				String name     = PointerUtils.toString(param1);							// [in]  (char *) name of sound definition
//				int entryindex  = PointerUtils.createView(param2, SIZEOF_INT).getInt(0);	// [in]  (int) index of sound definition entry
//	            Sound s = Sound.createView(param2);											// [out] (FMOD::Sound **) a valid lower level API FMOD Sound handle
				
				Sound s = new Sound();
				result = fsb.getSubSound(g_subsound_index, s);
				ErrorCheck(result);
				
				result = s.getName(buffer, buffer.capacity());
				ErrorCheck(result);
				String sound_name = BufferUtils.toString(buffer);
				
				//Write [out]
				param2.shareMemory(s);																	// [out] (FMOD::Sound **) a valid lower level API FMOD Sound handle

				printf("FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_CREATE '%s' (%d)\n",
						sound_name, g_subsound_index);
			}
			else if(type == FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_RELEASE)
			{
				Sound s = Sound.createView(param2);		// [in]  (FMOD::Sound *) the FMOD sound handle that was previously created in FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_CREATE

	            result = s.getName(buffer, buffer.capacity());
				ErrorCheck(result);
				String sound_name = BufferUtils.toString(buffer);
				
	            printf("FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_RELEASE '%s'\n", sound_name);
			}
			return FMOD_OK;
		}
	};
	
	public void initFmod()
	{
		/*
		 * NativeFmodEx/NativeFmodDesigner Init
		 */
		try
		{
			Init.loadLibraries(INIT_MODES.INIT_FMOD_EX/*_MINIMUM*/);
			InitFmodDesigner.loadLibraries(INIT_MODES.INIT_FMOD_DESIGNER);
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
		/*
		 * Checking NativeFmodDesigner version
		 */
		if(NATIVEFMODDESIGNER_LIBRARY_VERSION != NATIVEFMODDESIGNER_JAR_VERSION)
		{
			printfExit("Error!  NativeFmodDesigner library version (%08x) is different to jar version (%08x)\n", NATIVEFMODDESIGNER_LIBRARY_VERSION, NATIVEFMODDESIGNER_JAR_VERSION);
			return;
		}
		
		/*==================================================*/
	    
		init = true;
	}

	public void run()
	{
		if(!init) return;
		
		FMOD_RESULT result;
	    EventGroup  eventgroup = new EventGroup();
	    Event       event = new Event();
	    sys = new System();
	    fsb = new Sound();

	    print("======================================================================\n");
	    print("Programmer Sound. Copyright (c) Firelight Technologies 2006-2007.\n");
	    print("======================================================================\n");

	    result = FmodDesigner.EventSystem_Create(eventsystem);
	    ErrorCheck(result);
	    result = eventsystem.init(64, FMOD_INIT_NORMAL, null, FMOD_EVENT_INIT_NORMAL);
	    ErrorCheck(result);
//	    result = eventsystem.setMediaPath("DesignerMedia");
//	    ErrorCheck(result);
//	    result = eventsystem.load("examples.fev", null, null);
//	    ErrorCheck(result);
	    {
	    	//Load fev in memory
		    ByteBuffer fevBuffer = Medias.loadMediaIntoMemory("/DesignerMedia/examples.fev");
		    FMOD_EVENT_LOADINFO info = FMOD_EVENT_LOADINFO.create();
		    info.setLoadFromMemoryLength(fevBuffer.capacity());
		    result = eventsystem.load(fevBuffer, info, null);
		    ErrorCheck(result);
		    info.release();
		}
	    {
	    	//Attach a JAR file system
		    System system = new System();
		    eventsystem.getSystemObject(system);
		    JARFileSystem fileSystem = new JARFileSystem("/DesignerMedia/");
		    result = system.setFileSystem(fileSystem.jarOpen, fileSystem.jarClose, fileSystem.jarRead, fileSystem.jarSeek, -1);
		    ErrorCheck(result);
	    }
	    result = eventsystem.getGroup("examples/examples", FMOD_EVENT_DEFAULT != 0, eventgroup);
	    ErrorCheck(result);

	    result = eventsystem.getSystemObject(sys);
	    ErrorCheck(result);
		result = sys.createSound("other.fsb", FMOD_SOFTWARE | FMOD_2D, null, fsb);
		ErrorCheck(result);

	    print("======================================================================\n");
	    print("Press '1' to start a 'Programmer Sound' event\n");
	    print("Press '>' or '.' to increase subsound index\n");
	    print("Press '<' or ',' to decrease subsound index\n");
	    print("Press 'E' to quit\n");
	    print("======================================================================\n");
		
	    boolean exit = false;
	    do
	    {
	    	switch(getKey())
	    	{
	            case '1':
	                result = eventgroup.getEvent("programmer_sound", FMOD_EVENT_DEFAULT, event);
	                if(result == FMOD_OK)
	                {
	                    result = event.setCallback(eventcallback, null);
	                    ErrorCheck(result);
	                    result = event.start();
	                    ErrorCheck(result);
	                }
	                break;
	            case '>':
	            case '.':
	                ++g_subsound_index;
	                g_subsound_index = g_subsound_index > 3 ? 3 : g_subsound_index;
	                printf("Subsound index = %d\n", g_subsound_index);
	                break;
	            case '<':
	            case ',':
	                --g_subsound_index;
	                g_subsound_index = g_subsound_index < 0 ? 0 : g_subsound_index;
	                printf("Subsound index = %d\n", g_subsound_index);
	                break;
	            case 'e':
	            case 'E': exit = true; break;
	    	}

	        result = eventsystem.update();
	        ErrorCheck(result);

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
		
		FMOD_RESULT result;
		if(!eventsystem.isNull()) {
		    System system = new System();
		    eventsystem.getSystemObject(system);
		    system.setFileSystem(null, null, null, null, -1);
		    
		    result = eventsystem.unload();
		    ErrorCheck(result);
		}
		if(!fsb.isNull()) {
		    result = fsb.release();
		    ErrorCheck(result);
		}
		if(!eventsystem.isNull()) {
		    result = eventsystem.release();
		    ErrorCheck(result);
		}
		
		printExit("Shutdown\n");
	}
}