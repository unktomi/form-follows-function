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
 *     Codec plugin structure that is passed into each callback.<BR>
 *     <BR>
 *     Set these numsubsounds and waveformat members when called in FMOD_CODEC_OPENCALLBACK to tell fmod what sort of sound to create.<BR>
 *     <BR>
 *     The format, channels and frequency tell FMOD what sort of hardware buffer to create when you initialize your code.  So if you wrote an MP3 codec that decoded to stereo 16bit integer PCM, you would specify FMOD_SOUND_FORMAT_PCM16, and channels would be equal to 2.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Members marked with [in] mean the variable can be written to.  The user can set the value.<BR>
 *     Members marked with [out] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<BR>
 *     <BR>
 *     An FMOD file might be from disk, memory or internet, however the file may be opened by the user.<BR>
 *     <BR>
 *     'numsubsounds' should be 0 if the file is a normal single sound stream or sound.  Examples of this would be .WAV, .WMA, .MP3, .AIFF.<BR>
 *     'numsubsounds' should be 1+ if the file is a container format, and does not contain wav data itself.  Examples of these types would be CDDA (multiple CD tracks), FSB (contains multiple sounds), DLS (contain instruments).<BR>
 *     The arrays of format, channel, frequency, length and blockalign should point to arrays of information based on how many subsounds are in the format.  If the number of subsounds is 0 then it should point to 1 of each attribute, the same as if the number of subsounds was 1.  If subsounds was 100 for example, each pointer should point to an array of 100 of each attribute.<BR>
 *     When a sound has 1 or more subsounds, you must play the individual sounds specified by first obtaining the subsound with Sound::getSubSound.<BR>
 *     <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     FMOD_SOUND_FORMAT<BR>
 *     FMOD_FILE_READCALLBACK      <BR>
 *     FMOD_FILE_SEEKCALLBACK      <BR>
 *     FMOD_CODEC_METADATACALLBACK<BR>
 *     Sound::getSubSound<BR>
 *     Sound::getNumSubSounds<BR>
 * 
 */
public class FMOD_CODEC_STATE extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_CODEC_STATE</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_CODEC_STATE object.
	 */
	public static FMOD_CODEC_STATE createView(Pointer pointer)
	{
		return new FMOD_CODEC_STATE(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_CODEC_STATE</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_CODEC_STATE obj = FMOD_CODEC_STATE.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_CODEC_STATE create()
	{
		return new FMOD_CODEC_STATE(StructureJNI.FMOD_CODEC_STATE_new());
	}

	protected FMOD_CODEC_STATE(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_CODEC_STATE</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_CODEC_STATE obj = new FMOD_CODEC_STATE();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_CODEC_STATE</code>, use the static "constructor" :
	 * <pre><code>  FMOD_CODEC_STATE obj = FMOD_CODEC_STATE.create();</code></pre>
	 * @see FMOD_CODEC_STATE#create()
	 */
	public FMOD_CODEC_STATE()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{
			CallbackManager.addCallback(24, null, pointer);
			CallbackManager.addCallback(25, null, pointer);
			CallbackManager.addCallback(7, null, pointer);
			CallbackManager.addOwner(0, pointer);
			StructureJNI.FMOD_CODEC_STATE_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [in] Number of 'subsounds' in this sound.  Anything other than 0 makes it a 'container' format (ie CDDA/DLS/FSB etc which contain 1 or more su bsounds).  For most normal, single sound codec such as WAV/AIFF/MP3, this should be 0 as they are not a container for subsounds, they are the sound by itself.
	 */
	public int getNumSubsounds()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_STATE_get_numsubsounds(pointer);
		return javaResult;
	}
	public void setNumSubsounds(int numSubsounds)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_STATE_set_numsubsounds(pointer, numSubsounds);
	}

	/**
	 * [in] Pointer to an array of format structures containing information about each sample.  Can be 0 or NULL if FMOD_CODEC_GETWAVEFORMAT callback is preferred.  The number of entries here must equal the number of subsounds defined in the subsound parameter. If numsubsounds = 0 then there should be 1 instance of this structure.
	 */
	public FMOD_CODEC_WAVEFORMAT getWaveFormat()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_CODEC_STATE_get_waveformat(pointer);
		return javaResult == 0 ? null : FMOD_CODEC_WAVEFORMAT.createView(Pointer.newPointer(javaResult));
	}
	public void setWaveFormat(FMOD_CODEC_WAVEFORMAT waveFormat)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_STATE_set_waveformat(pointer, Pointer.getPointer(waveFormat));
	}

	/**
	 * [in] Plugin writer created data the codec author wants to attach to this object.
	 */
	public Pointer getPluginData()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_CODEC_STATE_get_plugindata(pointer);
		return javaResult == 0 ? null : Pointer.newPointer(javaResult);
	}
	public void setPluginData(Pointer pluginData)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_STATE_set_plugindata(pointer, Pointer.getPointer(pluginData));
	}

	/**
	 * [out] This will return an internal FMOD file handle to use with the callbacks provided.
	 */
	public Pointer getFileHandle()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_CODEC_STATE_get_filehandle(pointer);
		return javaResult == 0 ? null : Pointer.newPointer(javaResult);
	}

	/**
	 * [out] This will contain the size of the file in bytes.
	 */
	public int getFileSize()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_STATE_get_filesize(pointer);
		return javaResult;
	}

	/**
	 * [out] This will return a callable FMOD file function to use from codec.
	 */
	public FMOD_FILE_READCALLBACK getFileRead()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_FILE_READCALLBACK)CallbackManager.getCallback(24, pointer, false);
	}
	/**
	 * Invoke an internal FMOD callback.<BR>
	 * Don't use this for user callbacks (callback created from Java).<BR>
	 * <BR>
	 * For an example of its use, look at CodecRaw esxample.
	 */
	public FMOD_RESULT invokeFileRead(Pointer handle, ByteBuffer buffer, int sizebytes, IntBuffer bytesread, Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();

		if(buffer != null && !buffer.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(bytesread != null && !bytesread.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = StructureJNI.FMOD_CODEC_STATE_invoke_fileread(pointer, Pointer.getPointer(handle), buffer, BufferUtils.getPositionInBytes(buffer), sizebytes, bytesread, BufferUtils.getPositionInBytes(bytesread), Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * [out] This will return a callable FMOD file function to use from codec.
	 */
	public FMOD_FILE_SEEKCALLBACK getFileSeek()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_FILE_SEEKCALLBACK)CallbackManager.getCallback(25, pointer, false);
	}
	/**
	 * Invoke an internal FMOD callback.<BR>
	 * Don't use this for user callbacks (callback created from Java).<BR>
	 * <BR>
	 * For an example of its use, look at CodecRaw esxample.
	 */
	public FMOD_RESULT invokeFileSeek(Pointer handle, int pos, Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();

		int javaResult = StructureJNI.FMOD_CODEC_STATE_invoke_fileseek(pointer, Pointer.getPointer(handle), pos, Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * [out] This will return a callable FMOD metadata function to use from codec.
	 */
	public FMOD_CODEC_METADATACALLBACK getMetadata()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_CODEC_METADATACALLBACK)CallbackManager.getCallback(7, pointer, false);
	}
	/**
	 * Invoke an internal FMOD callback.<BR>
	 * Don't use this for user callbacks (callback created from Java).<BR>
	 * <BR>
	 * For an example of its use, look at CodecRaw esxample.
	 */
	public FMOD_RESULT invokeMetadata(FMOD_CODEC_STATE codec_state, FMOD_TAGTYPE tagtype, ByteBuffer name, Pointer data, int datalen, FMOD_TAGDATATYPE datatype, int unique)
	{
		if(pointer == 0) throw new NullPointerException();

		if(name != null && !name.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = StructureJNI.FMOD_CODEC_STATE_invoke_metadata(pointer, Pointer.getPointer(codec_state), tagtype.asInt(), name, BufferUtils.getPositionInBytes(name), Pointer.getPointer(data), datalen, datatype.asInt(), unique);
		return FMOD_RESULT.get(javaResult);
	}

}