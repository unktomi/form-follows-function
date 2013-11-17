/*===============================================================================================
ReadTags Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows how to read tags from sound files
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_CREATESTREAM;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENONLY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGDATATYPE.FMOD_TAGDATATYPE_STRING;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGTYPE.FMOD_TAGTYPE_ASF;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGTYPE.FMOD_TAGTYPE_FMOD;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGTYPE.FMOD_TAGTYPE_ICECAST;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGTYPE.FMOD_TAGTYPE_ID3V1;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGTYPE.FMOD_TAGTYPE_ID3V2;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGTYPE.FMOD_TAGTYPE_SHOUTCAST;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGTYPE.FMOD_TAGTYPE_UNKNOWN;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGTYPE.FMOD_TAGTYPE_USER;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGTYPE.FMOD_TAGTYPE_VORBISCOMMENT;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.nio.ByteBuffer;

import javax.swing.JPanel;

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
import org.jouvieje.FmodEx.Structures.FMOD_TAG;

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
public class ReadTags extends ConsoleGUI
{
	public static void main(String[] args)
	{
		new FmodExExampleFrame(new ReadTags());
	}
	
	private boolean init   = false;
	private boolean deinit = false;
	
	private System system = new System();
	private Sound  sound  = new Sound();
	
	public ReadTags()
	{
		super();
		initFmod();
		initialize();
	}
	
	public JPanel getPanel() { return this; }
	public String getTitle() { return "FMOD Ex ReadTags example."; }
	
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
		FMOD_RESULT result;
		FMOD_TAG tag = FMOD_TAG.create();
		int numtags;
		int version;
		
		/*
		 * Buffer used to store all datas received from FMOD.
		 */
		ByteBuffer buffer = newByteBuffer(SIZEOF_INT);
		
		printf("==================================================================\n");
		printf("ReadTags Example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		printf("==================================================================\n\n");
		printf("Usage:   ReadTags <url>\n");
		printf("Example: ReadTags C:/My Documents/My Musics/Some Music.mp3\n");
		resetInput();
		setInput("C:/My Documents/My Musics/Some Music.mp3");
		while(!keyHit()) {
			Thread.yield();
		}
		String url = getInput();
		
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
		
		result = system.init(100, FMOD_INIT_NORMAL, null);
		ErrorCheck(result);
		
		/*
		 * Open the specified file. Use FMOD_CREATESTREAM and FMOD_OPENONLY so it opens quickly
		 */
		result = system.createSound(url, FMOD_SOFTWARE | FMOD_2D | FMOD_CREATESTREAM | FMOD_OPENONLY, null, sound);
		ErrorCheck(result);
		
		/*
		 * Read and display all tags associated with this file
		 */
		for(;;)
		{
			/*
			 * An index of -1 means "get the first tag that's new or updated".
			 * If no tags are new or updated then getTag will return FMOD_ERR_TAGNOTFOUND.
			 * This is the first time we've read any tags so they'll all be new but after we've read them, 
			 * they won't be new any more.
			 */
			if(sound.getTag(null, -1, tag) != FMOD_OK)
			{
				break;
			}
			
			if(tag.getDataType() == FMOD_TAGDATATYPE_STRING)
			{
				printf("%s = %s (%d bytes)\n", tag.getName(), tag.getData().asString(), tag.getDataLen());
			}
			else
			{
				printf("%s = <binary> (%d bytes)\n", tag.getName(), tag.getDataLen());
			}
		}
		printf("\n");
		
		
		/*
		 * Read all the tags regardless of whether they're updated or not. Also show the tag type.
		 */
		
		result = sound.getNumTags(buffer.asIntBuffer(), null);
		ErrorCheck(result);
		numtags = buffer.getInt(0);
		
		
		for(int i = 0; i < numtags; i++)
		{
			result = sound.getTag(null, i, tag);
			ErrorCheck(result);
			
			//Yeah, if/elseif is a little boring but now we can't use switch/case
			if(tag.getType() == FMOD_TAGTYPE_UNKNOWN)
			{
				printf("FMOD_TAGTYPE_UNKNOWN  ");
			}
			else if(tag.getType() == FMOD_TAGTYPE_ID3V1)
			{
				printf("FMOD_TAGTYPE_ID3V1  ");
			}
			else if(tag.getType() == FMOD_TAGTYPE_ID3V2)
			{
				printf("FMOD_TAGTYPE_ID3V2  ");
			}
			else if(tag.getType() == FMOD_TAGTYPE_VORBISCOMMENT)
			{
				printf("FMOD_TAGTYPE_VORBISCOMMENT  ");
			}
			else if(tag.getType() == FMOD_TAGTYPE_SHOUTCAST)
			{
				printf("FMOD_TAGTYPE_SHOUTCAST  ");
			}
			else if(tag.getType() == FMOD_TAGTYPE_ICECAST)
			{
				printf("FMOD_TAGTYPE_ICECAST  ");
			}
			else if(tag.getType() == FMOD_TAGTYPE_ASF)
			{
				printf("FMOD_TAGTYPE_ASF  ");
			}
			else if(tag.getType() == FMOD_TAGTYPE_FMOD)
			{
				printf("FMOD_TAGTYPE_FMOD  ");
			}
			else if(tag.getType() == FMOD_TAGTYPE_USER)
			{
				printf("FMOD_TAGTYPE_USER  ");
			}
			
			if(tag.getDataType() == FMOD_TAGDATATYPE_STRING)
			{
				printf("%s = %s (%d bytes)\n", tag.getName(), tag.getData().asString(), tag.getDataLen());
			}
			else
			{
				printf("%s = ??? (%d bytes)\n", tag.getName(), tag.getDataLen());
			}
		}
		printf("\n");
		
		/*
		 * Find a specific tag by name. Specify an index > 0 to get access to multiple tags of the same name.
		 */
		result = sound.getTag("ARTIST", 0, tag);
		ErrorCheck(result);
		printf("%s = %s (%d bytes)\n", tag.getName(), tag.getData().asString(), tag.getDataLen());
		printf("\n");

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