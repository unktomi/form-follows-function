/*===============================================================================================
SimpleEvent Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2007.

Demonstrates basic usage of FMOD's data-driven event library (fmod_event.dll)
===============================================================================================*/

package org.jouvieje.FmodDesigner.Examples;

import java.nio.ByteBuffer;

import javax.swing.JPanel;

import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_INITFLAGS.FMOD_EVENT_INIT_NORMAL;
import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_MODE.FMOD_EVENT_DEFAULT;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.FMOD_EVENT_VERSION;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_JAR_VERSION;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_FLOAT;

import org.jouvieje.FileSystem.JARFileSystem;
import org.jouvieje.FmodDesigner.Event;
import org.jouvieje.FmodDesigner.EventCategory;
import org.jouvieje.FmodDesigner.EventGroup;
import org.jouvieje.FmodDesigner.EventParameter;
import org.jouvieje.FmodDesigner.EventSystem;
import org.jouvieje.FmodDesigner.FmodDesigner;
import org.jouvieje.FmodDesigner.InitFmodDesigner;
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
public class SimpleEvent extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new SimpleEvent());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private EventSystem eventsystem = new EventSystem();
	private EventGroup  eventgroup  = new EventGroup();

	private float UPDATE_INTERVAL = 100.0f;
	
	public SimpleEvent()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Designer SimpleEvent example."; }
	
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
		 * NativeFmodEx/NativeFmodDesigner Init
		 */
		try
		{
			Init.loadLibraries(INIT_MODES.INIT_FMOD_EX/*_MINIMUM*/);
			InitFmodDesigner.loadLibraries(INIT_MODES.INIT_FMOD_DESIGNER/*_MINIMUM*/);
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
		
		EventCategory   mastercategory = new EventCategory();
		Event			car = new Event();
		EventParameter	rpm = new EventParameter();
		EventParameter	load = new EventParameter();
		FMOD_RESULT		result;
		int				version;
		float			val, rangemin, rangemax, updatespeed;
		
		ByteBuffer buffer = newByteBuffer(SIZEOF_FLOAT);
		ByteBuffer buffer2 = newByteBuffer(SIZEOF_FLOAT);
		
		printf("======================================================================\n");
		printf("Simple Event Example.  Copyright (c) Firelight Technologies 2004-2007.\n");
		printf("==============================-------=================================\n");
		printf("This example plays an event created with the FMOD Designer sound \n");
		printf("designer tool.  It simply plays an event, retrieves the parameters\n");
		printf("and allows the user to adjust them.\n");
		printf("======================================================================\n\n");
		
		result = FmodDesigner.EventSystem_Create(eventsystem);
		ErrorCheck(result);
		
		result = eventsystem.getVersion(buffer.asIntBuffer());
		version = buffer.getInt(0);
		ErrorCheck(result);
		if(version < FMOD_EVENT_VERSION)
		{
			printfExit("Error!  You are using an old version of FMOD EVENT %08x.  This program requires %08x\n", version, FMOD_EVENT_VERSION);
			return;
		}
		
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
		
		result = eventsystem.getGroup("examples/examples/car", false, eventgroup);
		ErrorCheck(result);
		result = eventgroup.getEvent("car", FMOD_EVENT_DEFAULT, car);
		ErrorCheck(result);
		
	    result = eventsystem.getCategory("master", mastercategory);
	    ErrorCheck(result);
	    
		result = car.getParameter("load", load);
		ErrorCheck(result);
		result = load.getRange(buffer.asFloatBuffer(), buffer2.asFloatBuffer());
		ErrorCheck(result);
		rangemin = buffer.getFloat(0);
		rangemax = buffer2.getFloat(0);
		result = load.setValue(rangemax);
		ErrorCheck(result);
		
		result = car.getParameterByIndex(0, rpm);
		ErrorCheck(result);
		result = rpm.getRange(buffer.asFloatBuffer(), buffer2.asFloatBuffer());
		ErrorCheck(result);
		rangemin = buffer.getFloat(0);
		rangemax = buffer2.getFloat(0);
		result = rpm.setValue(1000.0f);
		ErrorCheck(result);
		
		result = car.start();
		ErrorCheck(result);
		
		printf("======================================================================\n");
		printf("Press '<'     to decrease RPM\n");
		printf("Press '>'     to increase RPM\n");
		printf("Press 'SPACE' to pause / unpause master event category.\n");
		printf("Press 'E'     to quit\n");
		printf("======================================================================\n");
		
		updatespeed = (rangemax - rangemin) / UPDATE_INTERVAL;
		rpm.getValue(buffer.asFloatBuffer());
		val = buffer.getFloat(0);
		
		boolean exit = false;
		do
		{
			char c = getKey();
			if(c == 'e' || c == 'E')
			{
				exit = true;
			}
			else if(c == '<')
			{
				val -= updatespeed;
				if(val < rangemin)
				{
					val = rangemin;
				}
				
				result = rpm.setValue(val);
				ErrorCheck(result);
			}
			else if(c == '>')
			{
				val += updatespeed;
				if(val > rangemax)
				{
					val = rangemax;
				}
				
				result = rpm.setValue(val);
				ErrorCheck(result);
			}
			else if(c == ' ')
			{
                mastercategory.getPaused(buffer);
                boolean paused = buffer.get(0) != 0;
                mastercategory.setPaused(!paused);
			}
			
			eventsystem.update();
			
			try {
				Thread.sleep(15);
			} catch(InterruptedException e){}
			
			printfr("Car RPM = %.4f", val);
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
		if(!eventgroup.isNull()) {
			result = eventgroup.freeEventData(null, true);
			ErrorCheck(result);
		}
		if(!eventsystem.isNull()) {
		    System system = new System();
		    eventsystem.getSystemObject(system);
		    system.setFileSystem(null, null, null, null, -1);
			
			result = eventsystem.release();
			ErrorCheck(result);
		}
		
		printExit("Shutdown\n");
	}
}