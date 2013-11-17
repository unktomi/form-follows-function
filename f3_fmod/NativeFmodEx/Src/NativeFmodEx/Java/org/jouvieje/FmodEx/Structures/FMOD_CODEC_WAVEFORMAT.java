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
 *     Set these values marked 'in' to tell fmod what sort of sound to create.<BR>
 *     The format, channels and frequency tell FMOD what sort of hardware buffer to create when you initialize your code.  So if you wrote an MP3 codec that decoded to stereo 16bit integer PCM, you would specify FMOD_SOUND_FORMAT_PCM16, and channels would be equal to 2.<BR>
 *     Members marked as 'out' are set by fmod.  Do not modify these.  Simply specify 0 for these values when declaring the structure, FMOD will fill in the values for you after creation with the correct function pointers.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Members marked with [in] mean the variable can be written to.  The user can set the value.<BR>
 *     Members marked with [out] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<BR>
 *     <BR>
 *     An FMOD file might be from disk, memory or network, however the file may be opened by the user.<BR>
 *     <BR>
 *     'numsubsounds' should be 0 if the file is a normal single sound stream or sound.  Examples of this would be .WAV, .WMA, .MP3, .AIFF.<BR>
 *     'numsubsounds' should be 1+ if the file is a container format, and does not contain wav data itself.  Examples of these types would be CDDA (multiple CD tracks), FSB (contains multiple sounds), MIDI/MOD/S3M/XM/IT (contain instruments).<BR>
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
public class FMOD_CODEC_WAVEFORMAT extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_CODEC_WAVEFORMAT</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_CODEC_WAVEFORMAT object.
	 */
	public static FMOD_CODEC_WAVEFORMAT createView(Pointer pointer)
	{
		return new FMOD_CODEC_WAVEFORMAT(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_CODEC_WAVEFORMAT</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_CODEC_WAVEFORMAT obj = FMOD_CODEC_WAVEFORMAT.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_CODEC_WAVEFORMAT create()
	{
		return new FMOD_CODEC_WAVEFORMAT(StructureJNI.FMOD_CODEC_WAVEFORMAT_new());
	}

	protected FMOD_CODEC_WAVEFORMAT(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_CODEC_WAVEFORMAT</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_CODEC_WAVEFORMAT obj = new FMOD_CODEC_WAVEFORMAT();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_CODEC_WAVEFORMAT</code>, use the static "constructor" :
	 * <pre><code>  FMOD_CODEC_WAVEFORMAT obj = FMOD_CODEC_WAVEFORMAT.create();</code></pre>
	 * @see FMOD_CODEC_WAVEFORMAT#create()
	 */
	public FMOD_CODEC_WAVEFORMAT()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{

			StructureJNI.FMOD_CODEC_WAVEFORMAT_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [in] Name of sound.
	 */
	public CharBuffer getName()
	{
		if(pointer == 0) throw new NullPointerException();
		ByteBuffer javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_name(pointer);
		if(javaResult != null) {
			javaResult.order(ByteOrder.nativeOrder());
		}
		return javaResult.asCharBuffer();
	}
	public void setName(String name)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_name(pointer, name == null ? null : name.getBytes());
	}

	/**
	 * [in] Format for (decompressed) codec output, ie FMOD_SOUND_FORMAT_PCM8, FMOD_SOUND_FORMAT_PCM16.
	 */
	public FMOD_SOUND_FORMAT getFormat()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_format(pointer);
		return FMOD_SOUND_FORMAT.get(javaResult);
	}
	public void setFormat(FMOD_SOUND_FORMAT format)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_format(pointer, format.asInt());
	}

	/**
	 * [in] Number of channels used by codec, ie mono = 1, stereo = 2.
	 */
	public int getChannels()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_channels(pointer);
		return javaResult;
	}
	public void setChannels(int channels)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_channels(pointer, channels);
	}

	/**
	 * [in] Default frequency in hz of the codec, ie 44100.
	 */
	public int getFrequency()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_frequency(pointer);
		return javaResult;
	}
	public void setFrequency(int frequency)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_frequency(pointer, frequency);
	}

	/**
	 * [in] Length in bytes of the source data.
	 */
	public int getLengthBytes()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_lengthbytes(pointer);
		return javaResult;
	}
	public void setLengthBytes(int lengthBytes)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_lengthbytes(pointer, lengthBytes);
	}

	/**
	 * [in] Length in decompressed, PCM samples of the file, ie length in seconds * frequency.  Used for Sound::getLength and for memory allocation of static decompressed sample data.
	 */
	public int getLengthPCM()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_lengthpcm(pointer);
		return javaResult;
	}
	public void setLengthPCM(int lengthPCM)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_lengthpcm(pointer, lengthPCM);
	}

	/**
	 * [in] Blockalign in decompressed, PCM samples of the optimal decode chunk size for this format.  The codec read callback will be called in multiples of this value.
	 */
	public int getBlockAlign()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_blockalign(pointer);
		return javaResult;
	}
	public void setBlockAlign(int blockAlign)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_blockalign(pointer, blockAlign);
	}

	/**
	 * [in] Loopstart in decompressed, PCM samples of file.
	 */
	public int getLoopStart()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_loopstart(pointer);
		return javaResult;
	}
	public void setLoopStart(int loopStart)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_loopstart(pointer, loopStart);
	}

	/**
	 * [in] Loopend in decompressed, PCM samples of file.
	 */
	public int getLoopEnd()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_loopend(pointer);
		return javaResult;
	}
	public void setLoopEnd(int loopEnd)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_loopend(pointer, loopEnd);
	}

	/**
	 * [in] Mode to determine whether the sound should by default load as looping, non looping, 2d or 3d.
	 */
	public int getMode()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_mode(pointer);
		return javaResult;
	}
	public void setMode(int mode)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_mode(pointer, mode);
	}

	/**
	 * [in] Microsoft speaker channel mask, as defined for WAVEFORMATEXTENSIBLE and is found in ksmedia.h.  Leave at 0 to play in natural speaker order.
	 */
	public int getChannelMask()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CODEC_WAVEFORMAT_get_channelmask(pointer);
		return javaResult;
	}
	public void setChannelMask(int channelMask)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CODEC_WAVEFORMAT_set_channelmask(pointer, channelMask);
	}

}