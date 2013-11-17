/*=============================================================================
 Programmer Selected Sound Definition Example
 Copyright (c), Firelight Technologies Pty, Ltd 2007.

 Demonstrates how to use the "programmer selected sound definition" feature of
 the FMOD event system
=============================================================================*/

package org.jouvieje.FmodDesigner.Examples;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.swing.JPanel;

import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_INITFLAGS.FMOD_EVENT_INIT_NORMAL;
import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_MODE.FMOD_EVENT_DEFAULT;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_JAR_VERSION;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_LIBRARY_VERSION;
import static org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_CALLBACKTYPE.FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_SELECTINDEX;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

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
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Examples.Utils.ConsoleGUI;
import org.jouvieje.FmodEx.Examples.Utils.FmodExExampleFrame;
import org.jouvieje.FmodEx.Examples.Utils.Medias;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Misc.Pointer;
import org.jouvieje.FmodEx.Misc.PointerUtils;

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
public class ProgrammerSelected extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new ProgrammerSelected());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private EventSystem eventsystem = new EventSystem();
	
	private int g_sounddef_entry_index = 0;
	
	public ProgrammerSelected()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Designer ProgrammeSelected example."; }
	
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_RESULT.FMOD_OK)
		{
			printfExit("FMOD error! (%d) %s\n", result.asInt(), FmodEx.FMOD_ErrorString(result));
		}
	}
	
	private FMOD_EVENT_CALLBACK eventcallback = new FMOD_EVENT_CALLBACK(){
		public FMOD_RESULT FMOD_EVENT_CALLBACK(Event event, FMOD_EVENT_CALLBACKTYPE type, Pointer param1, Pointer param2, Pointer userdata)
		{
			if(type == FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_SELECTINDEX)
			{
				String name = PointerUtils.toString(param1);                    				// [in]  (char *) name of sound definition
				IntBuffer indexBuf = PointerUtils.createView(param2, SIZEOF_INT).asIntBuffer();	// [out] (int *) the sounddef entry index to use

				int index = indexBuf.get(0);
				index = g_sounddef_entry_index < index ? g_sounddef_entry_index : index - 1;
				indexBuf.put(0, index);

				printf("FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_SELECTINDEX '%s': %d\n",
						name, index);
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
		EventGroup  eventgroup  = new EventGroup();
		Event       event       = new Event();

		result = FmodDesigner.EventSystem_Create(eventsystem);
		ErrorCheck(result);
		result = eventsystem.init(64, FMOD_INIT_NORMAL, null, FMOD_EVENT_INIT_NORMAL);
		ErrorCheck(result);
//		result = eventsystem.setMediaPath("DesignerMedia");
//		ErrorCheck(result);
//		result = eventsystem.load("examples.fev", null, null);
//		ErrorCheck(result);
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

		printf("======================================================================\n");
		printf("Programmer Selected Sound Definition.\n");
		printf("Copyright (c) Firelight Technologies 2006-2007.\n");
		printf("----------------------------------------------------------------------\n");
		printf("Press '1' to start a 'Programmer Selected' event\n");
		printf("Press '>' or '.' to increase sound definition entry index\n");
		printf("Press '<' or ',' to decrease sound definition entry index\n");
		printf("Press 'E' to quit\n");
		printf("======================================================================\n");
		printf("Sound definition entry index = %d\n", g_sounddef_entry_index);

		boolean exit = false;
		do
		{
			switch(getKey())
			{
				case '1':
					result = eventgroup.getEvent("programmer_selected", FMOD_EVENT_DEFAULT, event);
					if (result == FMOD_OK)
					{
						result = event.setCallback(eventcallback, null);
						ErrorCheck(result);
						result = event.start();
						ErrorCheck(result);
					}
					break;
				case '>': case '.':
					++g_sounddef_entry_index;
					printf("Sound definition entry index = %d\n", g_sounddef_entry_index);
					break;
				case '<': case ',':
					--g_sounddef_entry_index;
					g_sounddef_entry_index = g_sounddef_entry_index < 0 ? 0 : g_sounddef_entry_index;
					printf("Sound definition entry index = %d\n", g_sounddef_entry_index);
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
			result = eventsystem.release();
			ErrorCheck(result);
		}
		
		printExit("Shutdown\n");
	}
}