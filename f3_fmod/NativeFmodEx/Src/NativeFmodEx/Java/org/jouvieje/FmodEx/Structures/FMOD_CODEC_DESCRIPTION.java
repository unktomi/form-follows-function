/**
 * 				NativeFmodEx Project
 *
 * Want to use FMOD Ex API (www.fmod.org) in the Java language ? NativeFmodEx is made for you.
 * Copyright © 2005-2008 Jérôme JOUVIE (Jouvieje)
 *
 * Created on 23 feb. 2005
 * @version file v1.0.0
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * My web sites :
 * 		http://jerome.jouvie.free.fr/
 * 
 * 
 * INTRODUCTION
 * FMOD Ex is an API (Application Programming Interface) that allow you to use music
 * and creating sound effects with a lot of sort of musics.
 * FMOD is at :
 * 		http://www.fmod.org/
 * The reason of this project is that FMOD Ex can't be used direcly with Java, so I've created
 * this project to do this.
 * 
 * 
 * GNU LESSER GENERAL PUBLIC LICENSE
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA 
 */

package org.jouvieje.FmodEx.Structures;

import org.jouvieje.FmodEx.*;
import org.jouvieje.FmodEx.Exceptions.*;
import org.jouvieje.FmodEx.Callbacks.*;
import org.jouvieje.FmodEx.*;
import org.jouvieje.FmodEx.Defines.*;
import org.jouvieje.FmodEx.Enumerations.*;
import org.jouvieje.FmodEx.Structures.*;
import java.nio.*;
import org.jouvieje.FmodEx.Misc.*;
import org.jouvieje.FmodEx.System;

/**
 * <BR>
 *     <BR>
 *     When creating a codec, declare one of these and provide the relevant callbacks and name for FMOD to use when it opens and reads a file.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Members marked with [in] mean the variable can be written to.  The user can set the value.<BR>
 *     Members marked with [out] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     FMOD_CODEC_STATE<BR>
 * 
 */
public class FMOD_CODEC_DESCRIPTION extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_CODEC_DESCRIPTION</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_CODEC_DESCRIPTION object.
	 */
	public static FMOD_CODEC_DESCRIPTION createView(Pointer pointer)
	{
		return new FMOD_CODEC_DESCRIPTION(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_CODEC_DESCRIPTION</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_CODEC_DESCRIPTION obj = FMOD_CODEC_DESCRIPTION.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_CODEC_DESCRIPTION create()
	{
		return new FMOD_CODEC_DESCRIPTION(StructureJNI.FMOD_CODEC_DESCRIPTION_new());
	}

	protected FMOD_CODEC_DESCRIPTION(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_CODEC_DESCRIPTION</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_CODEC_DESCRIPTION obj = new FMOD_CODEC_DESCRIPTION();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_CODEC_DESCRIPTION</code>, use the static "constructor" :
	 * <pre><code>  FMOD_CODEC_DESCRIPTION obj = FMOD_CODEC_DESCRIPTION.create();</code></pre>
	 * @see FMOD_CODEC_DESCRIPTION#create()
	 */
	public FMOD_CODEC_DESCRIPTION()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{
			CallbackManager.addCallback(0, null, pointer);
			CallbackManager.addCallback(1, null, pointer);
			CallbackManager.addCallback(2, null, pointer);
			CallbackManager.addCallback(3, null, pointer);
			CallbackManager.addCallback(4, null, pointer);
			CallbackManager.addCallback(5, null, pointer);
			CallbackManager.addCallback(6, null, pointer);
			CallbackManager.addCallback(8, null, pointer);
			CallbackManager.addOwner(0, pointer);
			StructureJNI.FMOD_CODEC_DESCRIPTION_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [in] Name of the codec.
	 */
	public String getName()
	{
		if(pointer == 0) throw new NullPointerException();
		String javaResult = StructureJNI.FMOD_CODEC_DESCRIPTION_get_name(pointer);
		return javaResult;
	}
	public void setName(String name)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_name(pointer, name == null ? null : name.getBytes());
	}

	/**
	 * [in] Plugin writer's version number.
	 */
	public int getVersion()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_DESCRIPTION_get_version(pointer);
		return javaResult;
	}
	public void setVersion(int version)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_version(pointer, version);
	}

	/**
	 * [in] Tells FMOD to open the file as a stream when calling System::createSound, and not a static sample.  Should normally be 0 (FALSE), because generally the user wants to decode the file into memory when using System::createSound.   Mainly used for formats that decode for a very long time, or could use large amounts of memory when decoded.  Usually sequenced formats such as mod/s3m/xm/it/midi fall into this category.   It is mainly to stop users that don't know what they're doing from getting FMOD_ERR_MEMORY returned from createSound when they should have in fact called System::createStream or used FMOD_CREATESTREAM in System::createSound.
	 */
	public int getDefaultAsStream()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_DESCRIPTION_get_defaultasstream(pointer);
		return javaResult;
	}
	public void setDefaultAsStream(int defaultAsStream)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_defaultasstream(pointer, defaultAsStream);
	}

	/**
	 * [in] When setposition codec is called, only these time formats will be passed to the codec. Use bitwise OR to accumulate different types.
	 */
	public int getTimeUnits()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_DESCRIPTION_get_timeunits(pointer);
		return javaResult;
	}
	public void setTimeUnits(int timeUnits)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_timeunits(pointer, timeUnits);
	}

	/**
	 * [in] Open callback for the codec for when FMOD tries to open a sound using this codec.
	 */
	public FMOD_CODEC_OPENCALLBACK getOpen()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_CODEC_OPENCALLBACK)CallbackManager.getCallback(0, pointer, false);
	}
	public void setOpen(FMOD_CODEC_OPENCALLBACK open)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(0, open, pointer);
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_open(pointer, open != null);
	}

	/**
	 * [in] Close callback for the codec for when FMOD tries to close a sound using this codec.
	 */
	public FMOD_CODEC_CLOSECALLBACK getClose()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_CODEC_CLOSECALLBACK)CallbackManager.getCallback(1, pointer, false);
	}
	public void setClose(FMOD_CODEC_CLOSECALLBACK close)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(1, close, pointer);
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_close(pointer, close != null);
	}

	/**
	 * [in] Read callback for the codec for when FMOD tries to read some data from the file to the destination format (specified in the open callback).
	 */
	public FMOD_CODEC_READCALLBACK getRead()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_CODEC_READCALLBACK)CallbackManager.getCallback(2, pointer, false);
	}
	public void setRead(FMOD_CODEC_READCALLBACK read)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(2, read, pointer);
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_read(pointer, read != null);
	}

	/**
	 * [in] Callback to return the length of the song in whatever format required when Sound::getLength is called.
	 */
	public FMOD_CODEC_GETLENGTHCALLBACK getGetLength()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_CODEC_GETLENGTHCALLBACK)CallbackManager.getCallback(3, pointer, false);
	}
	public void setGetLength(FMOD_CODEC_GETLENGTHCALLBACK getLength)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(3, getLength, pointer);
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_getlength(pointer, getLength != null);
	}

	/**
	 * [in] Seek callback for the codec for when FMOD tries to seek within the file with Channel::setPosition.
	 */
	public FMOD_CODEC_SETPOSITIONCALLBACK getSetPosition()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_CODEC_SETPOSITIONCALLBACK)CallbackManager.getCallback(4, pointer, false);
	}
	public void setSetPosition(FMOD_CODEC_SETPOSITIONCALLBACK setPosition)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(4, setPosition, pointer);
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_setposition(pointer, setPosition != null);
	}

	/**
	 * [in] Tell callback for the codec for when FMOD tries to get the current position within the with Channel::getPosition.
	 */
	public FMOD_CODEC_GETPOSITIONCALLBACK getGetPosition()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_CODEC_GETPOSITIONCALLBACK)CallbackManager.getCallback(5, pointer, false);
	}
	public void setGetPosition(FMOD_CODEC_GETPOSITIONCALLBACK getPosition)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(5, getPosition, pointer);
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_getposition(pointer, getPosition != null);
	}

	/**
	 * [in] Sound creation callback for the codec when FMOD finishes creating the sound.  (So the codec can set more parameters for the related created sound, ie loop points/mode or 3D attributes etc).
	 */
	public FMOD_CODEC_SOUNDCREATECALLBACK getSoundCreate()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_CODEC_SOUNDCREATECALLBACK)CallbackManager.getCallback(6, pointer, false);
	}
	public void setSoundCreate(FMOD_CODEC_SOUNDCREATECALLBACK soundCreate)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(6, soundCreate, pointer);
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_soundcreate(pointer, soundCreate != null);
	}

	/**
	 * [in] Callback to tell FMOD about the waveformat of a particular subsound.  This is to save memory, rather than saving 1000 FMOD_CODEC_WAVEFORMAT structures in the codec, the codec might have a more optimal way of storing this information.
	 */
	public FMOD_CODEC_GETWAVEFORMAT getGetWaveFormat()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_CODEC_GETWAVEFORMAT)CallbackManager.getCallback(8, pointer, false);
	}
	public void setGetWaveFormat(FMOD_CODEC_GETWAVEFORMAT getWaveFormat)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(8, getWaveFormat, pointer);
		StructureJNI.FMOD_CODEC_DESCRIPTION_set_getwaveformat(pointer, getWaveFormat != null);
	}

}