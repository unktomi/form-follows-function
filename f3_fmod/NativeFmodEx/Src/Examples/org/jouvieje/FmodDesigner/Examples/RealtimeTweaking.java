/*===============================================================================================
 realtime_tweaking Example
 Copyright (c), Firelight Technologies Pty, Ltd 2004-2007.

 Demonstrates basic usage of FMOD's network data-driven event library (fmod_event_net.dll)
===============================================================================================*/

package org.jouvieje.FmodDesigner.Examples;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.swing.JPanel;

import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_INITFLAGS.FMOD_EVENT_INIT_NORMAL;
import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_MODE.FMOD_EVENT_DEFAULT;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.FMOD_EVENT_VERSION;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_JAR_VERSION;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_LIBRARY_VERSION;
import static org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_CALLBACKTYPE.FMOD_EVENT_CALLBACKTYPE_NET_MODIFIED;
import static org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_PROPERTY.FMOD_EVENTPROPERTY_PITCH;
import static org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_PROPERTY.FMOD_EVENTPROPERTY_VOLUME;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Misc.BufferUtils.createView;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newIntBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import org.jouvieje.FileSystem.JARFileSystem;
import org.jouvieje.FmodDesigner.Event;
import org.jouvieje.FmodDesigner.EventGroup;
import org.jouvieje.FmodDesigner.EventSystem;
import org.jouvieje.FmodDesigner.FmodDesigner;
import org.jouvieje.FmodDesigner.InitFmodDesigner;
import org.jouvieje.FmodDesigner.Callbacks.FMOD_EVENT_CALLBACK;
import org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_CALLBACKTYPE;
import org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_PROPERTY;
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
public class RealtimeTweaking extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new RealtimeTweaking());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private EventSystem eventsystem = new EventSystem();

	private final static int NUM_EVENTS = 3;
	
	public RealtimeTweaking()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Designer RealtimeTweaking example."; }
	
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
		    if(type == FMOD_EVENT_CALLBACKTYPE_NET_MODIFIED)
		    {
		    	/*
		    	 * From FMOD Ex doc :
		    	 * When the event callback is called. 'param1' and 'param2' mean different things depending on the type of callback.
		    	 * Here the contents of param1 and param2 are listed.
		    	 * The parameters are void *, but should be cast to the listed C type to get the correct value.
		    	 *  - FMOD_EVENT_CALLBACKTYPE_NET_MODIFIED
		    	 *     param1 = (EVENT_PROPERTY) which property was modified.
		    	 *     param2 = (float) the new property value. 
		    	 */
		    	FMOD_EVENT_PROPERTY eventProperty = FMOD_EVENT_PROPERTY.get(param1);
		    	float t = param2 == null ? 0.0f : param2.asFloat();
		    	printf("p %s %f (%d)\n",
//		    			event,
		    			eventProperty == FMOD_EVENTPROPERTY_VOLUME ? "volume" : eventProperty == FMOD_EVENTPROPERTY_PITCH ? "pitch" : "???",
		    			t,
		    			PointerUtils.createView(userdata, SIZEOF_INT).getInt(0));
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
		
		EventGroup  eventgroup  = new EventGroup();
		Event[]     event       = new Event[NUM_EVENTS];
		FMOD_RESULT result;
		IntBuffer userdata = newIntBuffer(1).put(0, 0);
		
		printf("======================================================================\n");
		printf("Realtime Tweaking. Copyright (c) Firelight Technologies 2004-2007.\n");
		printf("======================================================================\n");
		printf("This example shows how to initialize the FMOD Net Event System so that\n");
		printf("FMOD Designer can connect to your game and tweak events as they're\n");
		printf("playing.\n");
		printf("Start some events then connect to this app using the Audition menu\n");
		printf("in FMOD Designer. You can use 127.0.0.1 for the IP address if you\n");
		printf("don't want to use two machines. Load examples.fdp and change the \n");
		printf("volume of the playing events using the volume slider in the event\n");
		printf("property sheet\n");
		printf("======================================================================\n\n");
		
		result = FmodDesigner.EventSystem_Create(eventsystem);
		ErrorCheck(result);
		
		IntBuffer version = newIntBuffer(1);
		result = eventsystem.getVersion(version);
		ErrorCheck(result);
		if(version.get(0) < FMOD_EVENT_VERSION)
		{
			printfExit("Error!  You are using an old version of FMOD EVENT %08x.  This program requires %08x\n", version.get(0), FMOD_EVENT_VERSION);
			return;
		}
		
		result = FmodDesigner.NetEventSystem_Init(eventsystem);
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
		result = eventsystem.getGroup("examples/examples/filters", false, eventgroup);
		ErrorCheck(result);
		
		for(int i = 0; i < NUM_EVENTS; i++)
		{
			event[i] = new Event();
		}
		
		printf("======================================================================\n");
		printf("Press 1 - 3  to start/stop events\n");
		printf("Press e      to quit\n");
		printf("======================================================================\n");
		
		boolean exit = false;
		do
		{
			char c = getKey();
			if(c == 'e' || c == 'E')
			{
				exit = true;
			}
			else if((c >= '1') && (c <= '3'))
			{
				Pointer name = new Pointer();
				
				int i = c - '1';
				
				if(!event[i].isNull())
				{
					result = event[i].getInfo(null, name, null);
					ErrorCheck(result);
					result = event[i].stop(false);
					ErrorCheck(result);
					event[i] = new Event();;
					printf("Stopping '%s'\n", name.asString());
				}
				else
				{
					result = eventgroup.getEventByIndex(i, FMOD_EVENT_DEFAULT, event[i]);
					if(result == FMOD_OK)
					{
						result = event[i].getInfo(null, name, null);
						ErrorCheck(result);
						result = event[i].setCallback(eventcallback, createView(userdata));
						ErrorCheck(result);
						result = event[i].start();
						ErrorCheck(result);
						printf("Starting '%s'\n", name.asString());
					}
				}
			}
			
			result = eventsystem.update();
			ErrorCheck(result);
			result = FmodDesigner.NetEventSystem_Update();
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
		    
			result = eventsystem.release();
			ErrorCheck(result);
		}
		result = FmodDesigner.NetEventSystem_Shutdown();
		ErrorCheck(result);
		
		printExit("Shutdown\n");
	}
}