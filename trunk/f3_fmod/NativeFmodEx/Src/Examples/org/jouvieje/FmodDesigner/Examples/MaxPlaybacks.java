/*=============================================================================
 Max Playbacks Example
 Copyright (c), Firelight Technologies Pty, Ltd 2004-2007.

 Demonstrates basic usage of event max playbacks behaviour.
=============================================================================*/

package org.jouvieje.FmodDesigner.Examples;

import java.nio.ByteBuffer;

import javax.swing.JPanel;

import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_INITFLAGS.FMOD_EVENT_INIT_NORMAL;
import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_MODE.FMOD_EVENT_DEFAULT;
import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_MODE.FMOD_EVENT_INFOONLY;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_JAR_VERSION;
import static org.jouvieje.FmodDesigner.Defines.VERSIONS.NATIVEFMODDESIGNER_LIBRARY_VERSION;
import static org.jouvieje.FmodDesigner.Enumerations.FMOD_EVENT_PITCHUNITS.FMOD_EVENT_PITCHUNITS_RAW;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;

import org.jouvieje.FileSystem.JARFileSystem;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodDesigner.Event;
import org.jouvieje.FmodDesigner.EventGroup;
import org.jouvieje.FmodDesigner.EventSystem;
import org.jouvieje.FmodDesigner.FmodDesigner;
import org.jouvieje.FmodDesigner.InitFmodDesigner;
import org.jouvieje.FmodDesigner.Structures.FMOD_EVENT_LOADINFO;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Examples.Utils.ConsoleGUI;
import org.jouvieje.FmodEx.Examples.Utils.FmodExExampleFrame;
import org.jouvieje.FmodEx.Examples.Utils.Medias;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Structures.FMOD_VECTOR;

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
public class MaxPlaybacks extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new MaxPlaybacks());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private EventSystem eventsystem = new EventSystem();

	private float g_distance = 2.0f;
	private float g_pitch    = 0.0f;
    
	public MaxPlaybacks()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Designer MaxPlaybacks example."; }
	
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_RESULT.FMOD_OK)
		{
			printfExit("FMOD error! (%d) %s\n", result.asInt(), FmodEx.FMOD_ErrorString(result));
		}
	}
	
	private void setupEvent(Event event)
	{
	    FMOD_VECTOR pos = FMOD_VECTOR.create(0, 0, g_distance);
	    FMOD_RESULT result;
	    
	    result = event.set3DAttributes(pos, null, null);
	    ErrorCheck(result);
	    pos.release();
	    result = event.setPitch(g_pitch, FMOD_EVENT_PITCHUNITS_RAW);
	    ErrorCheck(result);
	    result = event.start();
	    ErrorCheck(result);
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
		
		FMOD_RESULT result;
	    EventGroup  eventgroup  = new EventGroup();
	    Event       event       = new Event();

	    result = FmodDesigner.EventSystem_Create(eventsystem);
	    ErrorCheck(result);
	    result = eventsystem.init(64, FMOD_INIT_NORMAL, null, FMOD_EVENT_INIT_NORMAL);
	    ErrorCheck(result);
//	    result = eventsystem.setMediaPath("DesignerMedia");
//	    ErrorCheck(result);
//	    result = eventsystem.load("examples.fev", info, null);
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
	    
	    result = eventsystem.getGroup("examples/max_playbacks", FMOD_EVENT_DEFAULT != 0, eventgroup);
	    ErrorCheck(result);

	    printf("======================================================================\n");
	    printf("Max Playbacks Example.  Copyright (c) Firelight Technologies 2004-2007.\n");
	    printf("----------------------------------------------------------------------\n");
	    printf("Press '1' to play an event with 'Steal oldest' behaviour\n");
	    printf("Press '2' to play an event with 'Steal newest' behaviour\n");
	    printf("Press '3' to play an event with 'Steal quietest' behaviour\n");
	    printf("Press '4' to play an event with 'Just fail' behaviour\n");
	    printf("Press '5' to play an event with 'Just fail if quietest' behaviour\n");
	    printf("Press 's' or 'S' to stop all events\n");
	    printf("Press '+' or '=' to increase event pitch\n");
	    printf("Press '-' or '_' to decrease event pitch\n");
	    printf("Press '>' or '.' to increase event distance\n");
	    printf("Press '<' or ',' to decrease event distance\n");
	    printf("Press 'E' to quit\n");
	    printf("======================================================================\n");
		
		boolean exit = false;
	    do
	    {
            switch(getKey())
            {
	            case '1':
	                result = eventgroup.getEvent("steal_oldest", FMOD_EVENT_DEFAULT, event);
	                if (result == FMOD_OK)
	                {
	                    printf("getEvent(\"steal_oldest\") succeeded\n");
	                    setupEvent(event);
	                }
	                else
	                {
	                    printf("getEvent(\"steal_oldest\") failed (%s)\n", result.toString());
	                }
	                break;
	            case '2':
	                result = eventgroup.getEvent("steal_newest", FMOD_EVENT_DEFAULT, event);
	                if (result == FMOD_OK)
	                {
	                    printf("getEvent(\"steal_newest\") succeeded\n");
	                    setupEvent(event);
	                }
	                else
	                {
	                    printf("getEvent(\"steal_newest\") failed (%s)\n", result.toString());
	                }
	                break;
	            case '3':
	                result = eventgroup.getEvent("steal_quietest", FMOD_EVENT_DEFAULT, event);
	                if (result == FMOD_OK)
	                {
	                    printf("getEvent(\"steal_quietest\") succeeded\n");
	                    setupEvent(event);
	                }
	                else
	                {
	                    printf("getEvent(\"steal_quietest\") failed (%s)\n", result.toString());
	                }
	                break;
	            case '4':
	                result = eventgroup.getEvent("just_fail", FMOD_EVENT_DEFAULT, event);
	                if (result == FMOD_OK)
	                {
	                    printf("getEvent(\"just_fail\") succeeded\n");
	                    setupEvent(event);
	                }
	                else
	                {
	                    printf("getEvent(\"just_fail\") failed (%s)\n", result.toString());
	                }
	                break;
	            case '5':
	                // get the info-only event to set up for volume calculation
	                result = eventgroup.getEvent("just_fail_if_quietest", FMOD_EVENT_INFOONLY, event);
	                if (result == FMOD_OK)
	                {
	                    // set the position on the info-only event
	                    FMOD_VECTOR pos = FMOD_VECTOR.create(0, 0, g_distance);
	                    result = event.set3DAttributes(pos, null, null);
	                    ErrorCheck(result);
	                    pos.release();

	                    // attempt to get a real event instance
	                    result = eventgroup.getEvent("just_fail_if_quietest", FMOD_EVENT_DEFAULT, event);
	                    if (result == FMOD_OK)
	                    {
	                        printf("getEvent(\"just_fail_if_quietest\") succeeded\n");
	                        // we don't need to set the position, as it is copied from the info-only event
	                        result = event.setPitch(g_pitch, FMOD_EVENT_PITCHUNITS_RAW);
	                        ErrorCheck(result);
	                        result = event.start();
	                        ErrorCheck(result);
	                    }
	                    else
	                    {
	                        printf("getEvent(\"just_fail_if_quietest\") failed (%s)\n", result.toString());
	                    }
	                }
	                break;
	            case 's':
	            case 'S':
	                // slightly hacky, but the simplest way to stop all events in the group
	                result = eventgroup.freeEventData(null, true);
	                ErrorCheck(result);
	                break;
	            case '+':
	            case '=':
	                g_pitch += 0.1f;
	                break;
	            case '-':
	            case '_':
	                g_pitch -= 0.1f;
	                break;
	            case '>':
	            case '.':
	                g_distance += 0.1f;
	                break;
	            case '<':
	            case ',':
	                g_distance -= 0.1f;
	                g_distance = (g_distance < 0.0f) ? 0.0f : g_distance;
	                break;
	            case 'e':
	            case 'E': exit = true; break;
            }
            
            printfr("Distance = %.1f, Pitch = %.1f", g_distance, g_pitch);

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