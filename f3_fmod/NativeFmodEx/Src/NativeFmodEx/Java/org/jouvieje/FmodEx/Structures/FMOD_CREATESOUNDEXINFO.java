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
 *     Use this structure with System::createSound when more control is needed over loading.<BR>
 *     The possible reasons to use this with System::createSound are:<BR>
 *     - Loading a file from memory.<BR>
 *     - Loading a file from within another larger (possibly wad/pak) file, by giving the loader an offset and length.<BR>
 *     - To create a user created / non file based sound.<BR>
 *     - To specify a starting subsound to seek to within a multi-sample sounds (ie FSB/DLS/SF2) when created as a stream.<BR>
 *     - To specify which subsounds to load for multi-sample sounds (ie FSB/DLS/SF2) so that memory is saved and only a subset is actually loaded/read from disk.<BR>
 *     - To specify 'piggyback' read and seek callbacks for capture of sound data as fmod reads and decodes it.  Useful for ripping decoded PCM data from sounds as they are loaded / played.<BR>
 *     - To specify a MIDI DLS/SF2 sample set file to load when opening a MIDI file.<BR>
 *     See below on what members to fill for each of the above types of sound you want to create.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     This structure is optional!  Specify 0 or NULL in System::createSound if you don't need it!<BR>
 * <BR>
 *     Members marked with [in] mean the user sets the value before passing it to the function.<BR>
 *     Members marked with [out] mean FMOD sets the value to be used after the function exits.<BR>
 * <BR>
 *     <u>Loading a file from memory.</u><BR>
 *     - Create the sound using the FMOD_OPENMEMORY flag.<BR>
 *     - Mandatory.  Specify 'length' for the size of the memory block in bytes.<BR>
 *     - Other flags are optional.<BR>
 * <BR>
 * <BR>
 *     <u>Loading a file from within another larger (possibly wad/pak) file, by giving the loader an offset and length.</u><BR>
 *     - Mandatory.  Specify 'fileoffset' and 'length'.<BR>
 *     - Other flags are optional.<BR>
 * <BR>
 * <BR>
 *     <u>To create a user created / non file based sound.</u><BR>
 *     - Create the sound using the FMOD_OPENUSER flag.<BR>
 *     - Mandatory.  Specify 'defaultfrequency, 'numchannels' and 'format'.<BR>
 *     - Other flags are optional.<BR>
 * <BR>
 * <BR>
 *     <u>To specify a starting subsound to seek to and flush with, within a multi-sample stream (ie FSB/DLS/SF2).</u><BR>
 * <BR>
 *     - Mandatory.  Specify 'initialsubsound'.<BR>
 * <BR>
 * <BR>
 *     <u>To specify which subsounds to load for multi-sample sounds (ie FSB/DLS/SF2) so that memory is saved and only a subset is actually loaded/read from disk.</u><BR>
 * <BR>
 *     - Mandatory.  Specify 'inclusionlist' and 'inclusionlistnum'.<BR>
 * <BR>
 * <BR>
 *     <u>To specify 'piggyback' read and seek callbacks for capture of sound data as fmod reads and decodes it.  Useful for ripping decoded PCM data from sounds as they are loaded / played.</u><BR>
 * <BR>
 *     - Mandatory.  Specify 'pcmreadcallback' and 'pcmseekcallback'.<BR>
 * <BR>
 * <BR>
 *     <u>To specify a MIDI DLS/SF2 sample set file to load when opening a MIDI file.</u><BR>
 * <BR>
 *     - Mandatory.  Specify 'dlsname'.<BR>
 * <BR>
 * <BR>
 *     Setting the 'decodebuffersize' is for cpu intensive codecs that may be causing stuttering, not file intensive codecs (ie those from CD or netstreams) which are normally altered with System::setStreamBufferSize.  As an example of cpu intensive codecs, an mp3 file will take more cpu to decode than a PCM wav file.<BR>
 *     If you have a stuttering effect, then it is using more cpu than the decode buffer playback rate can keep up with.  Increasing the decode buffersize will most likely solve this problem.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::createSound<BR>
 *     System::setStreamBufferSize<BR>
 *     FMOD_MODE<BR>
 *     FMOD_SOUND_FORMAT<BR>
 *     FMOD_SOUND_TYPE<BR>
 *     FMOD_SPEAKERMAPTYPE<BR>
 * 
 */
public class FMOD_CREATESOUNDEXINFO extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_CREATESOUNDEXINFO</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_CREATESOUNDEXINFO object.
	 */
	public static FMOD_CREATESOUNDEXINFO createView(Pointer pointer)
	{
		return new FMOD_CREATESOUNDEXINFO(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_CREATESOUNDEXINFO</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_CREATESOUNDEXINFO obj = FMOD_CREATESOUNDEXINFO.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_CREATESOUNDEXINFO create()
	{
		return new FMOD_CREATESOUNDEXINFO(StructureJNI.FMOD_CREATESOUNDEXINFO_new());
	}

	protected FMOD_CREATESOUNDEXINFO(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_CREATESOUNDEXINFO</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_CREATESOUNDEXINFO obj = new FMOD_CREATESOUNDEXINFO();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_CREATESOUNDEXINFO</code>, use the static "constructor" :
	 * <pre><code>  FMOD_CREATESOUNDEXINFO obj = FMOD_CREATESOUNDEXINFO.create();</code></pre>
	 * @see FMOD_CREATESOUNDEXINFO#create()
	 */
	public FMOD_CREATESOUNDEXINFO()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{
			CallbackManager.addCallback(20, null, pointer);
			CallbackManager.addCallback(21, null, pointer);
			CallbackManager.addCallback(19, null, pointer);
			CallbackManager.addCallback(22, null, pointer);
			CallbackManager.addCallback(23, null, pointer);
			CallbackManager.addCallback(24, null, pointer);
			CallbackManager.addCallback(25, null, pointer);
			CallbackManager.addOwner(0, pointer);
			StructureJNI.FMOD_CREATESOUNDEXINFO_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [in] Size of this structure.  This is used so the structure can be expanded in the future and still work on older versions of FMOD Ex.
	 */
	public int getCbSize()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_cbsize(pointer);
		return javaResult;
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Size in bytes of file to load, or sound to create (in this case only if FMOD_OPENUSER is used).  Required if loading from memory.  If 0 is specified, then it will use the size of the file (unless loading from memory then an error will be returned).
	 */
	public int getLength()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_length(pointer);
		return javaResult;
	}
	public void setLength(int length)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_length(pointer, length);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Offset from start of the file to start loading from.  This is useful for loading files from inside big data files.
	 */
	public int getFileOffset()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_fileoffset(pointer);
		return javaResult;
	}
	public void setFileOffset(int fileOffset)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_fileoffset(pointer, fileOffset);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Number of channels in a sound mandatory if FMOD_OPENUSER or FMOD_OPENRAW is used.
	 */
	public int getNumChannels()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_numchannels(pointer);
		return javaResult;
	}
	public void setNumChannels(int numChannels)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_numchannels(pointer, numChannels);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Default frequency of sound in a sound mandatory if FMOD_OPENUSER or FMOD_OPENRAW is used.  Other formats use the frequency determined by the file format.
	 */
	public int getDefaultFrequency()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_defaultfrequency(pointer);
		return javaResult;
	}
	public void setDefaultFrequency(int defaultFrequency)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_defaultfrequency(pointer, defaultFrequency);
	}

	/**
	 * [in] Optional. Specify 0 or FMOD_SOUND_FORMAT_NONE to ignore. Format of the sound mandatory if FMOD_OPENUSER or FMOD_OPENRAW is used.  Other formats use the format determined by the file format.
	 */
	public FMOD_SOUND_FORMAT getFormat()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_format(pointer);
		return FMOD_SOUND_FORMAT.get(javaResult);
	}
	public void setFormat(FMOD_SOUND_FORMAT format)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_format(pointer, format.asInt());
	}

	/**
	 * [in] Optional. Specify 0 to ignore. For streams.  This determines the size of the double buffer (in PCM samples) that a stream uses.  Use this for user created streams if you want to determine the size of the callback buffer passed to you.  Specify 0 to use FMOD's default size which is currently equivalent to 400ms of the sound format created/loaded.
	 */
	public int getDecodeBufferSize()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_decodebuffersize(pointer);
		return javaResult;
	}
	public void setDecodeBufferSize(int decodeBufferSize)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_decodebuffersize(pointer, decodeBufferSize);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. In a multi-sample file format such as .FSB/.DLS/.SF2, specify the initial subsound to seek to, only if FMOD_CREATESTREAM is used.
	 */
	public int getInitialSubsound()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_initialsubsound(pointer);
		return javaResult;
	}
	public void setInitialSubsound(int initialSubsound)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_initialsubsound(pointer, initialSubsound);
	}

	/**
	 * [in] Optional. Specify 0 to ignore or have no subsounds.  In a user created multi-sample sound, specify the number of subsounds within the sound that are accessable with Sound::getSubSound.
	 */
	public int getNumSubsounds()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_numsubsounds(pointer);
		return javaResult;
	}
	public void setNumSubsounds(int numSubsounds)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_numsubsounds(pointer, numSubsounds);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. In a multi-sample format such as .FSB/.DLS/.SF2 it may be desirable to specify only a subset of sounds to be loaded out of the whole file.  This is an array of subsound indices to load into memory when created.
	 */
	public IntBuffer getInclusionList()
	{
		if(pointer == 0) throw new NullPointerException();
		ByteBuffer javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_inclusionlist(pointer);
		if(javaResult != null) {
			javaResult.order(ByteOrder.nativeOrder());
		}
		return javaResult.asIntBuffer();
	}
	public void setInclusionList(IntBuffer inclusionList)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_inclusionlist(pointer, inclusionList, BufferUtils.getPositionInBytes(inclusionList));
	}

	/**
	 * [in] Optional. Specify 0 to ignore. This is the number of integers contained within the inclusionlist array.
	 */
	public int getInclusionListNum()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_inclusionlistnum(pointer);
		return javaResult;
	}
	public void setInclusionListNum(int inclusionListNum)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_inclusionlistnum(pointer, inclusionListNum);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Callback to 'piggyback' on FMOD's read functions and accept or even write PCM data while FMOD is opening the sound.  Used for user sounds created with FMOD_OPENUSER or for capturing decoded data as FMOD reads it.
	 */
	public FMOD_SOUND_PCMREADCALLBACK getPcmReadCallback()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_SOUND_PCMREADCALLBACK)CallbackManager.getCallback(20, pointer, false);
	}
	public void setPcmReadCallback(FMOD_SOUND_PCMREADCALLBACK pcmReadCallback)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(20, pcmReadCallback, pointer);
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_pcmreadcallback(pointer, pcmReadCallback != null);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Callback for when the user calls a seeking function such as Channel::setTime or Channel::setPosition within a multi-sample sound, and for when it is opened.
	 */
	public FMOD_SOUND_PCMSETPOSCALLBACK getPcmSetPosCallback()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_SOUND_PCMSETPOSCALLBACK)CallbackManager.getCallback(21, pointer, false);
	}
	public void setPcmSetPosCallback(FMOD_SOUND_PCMSETPOSCALLBACK pcmSetPosCallback)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(21, pcmSetPosCallback, pointer);
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_pcmsetposcallback(pointer, pcmSetPosCallback != null);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Callback for successful completion, or error while loading a sound that used the FMOD_NONBLOCKING flag.
	 */
	public FMOD_SOUND_NONBLOCKCALLBACK getNonBlockCallback()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_SOUND_NONBLOCKCALLBACK)CallbackManager.getCallback(19, pointer, false);
	}
	public void setNonBlockCallback(FMOD_SOUND_NONBLOCKCALLBACK nonBlockCallback)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(19, nonBlockCallback, pointer);
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_nonblockcallback(pointer, nonBlockCallback != null);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Filename for a DLS or SF2 sample set when loading a MIDI file. If not specified, on Windows it will attempt to open /windows/system32/drivers/gm.dls or /windows/system32/drivers/etc/gm.dls, on Mac it will attempt to load /System/Library/Components/CoreAudio.component/Contents/Resources/gs_instruments.dls, otherwise the MIDI will fail to open. Current DLS support is for level 1 of the specification.
	 */
	public String getDlsName()
	{
		if(pointer == 0) throw new NullPointerException();
		String javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_dlsname(pointer);
		return javaResult;
	}
	public void setDlsName(String dlsName)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_dlsname(pointer, dlsName == null ? null : dlsName.getBytes());
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Key for encrypted FSB file.  Without this key an encrypted FSB file will not load.
	 */
	public String getEncryptionKey()
	{
		if(pointer == 0) throw new NullPointerException();
		String javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_encryptionkey(pointer);
		return javaResult;
	}
	public void setEncryptionKey(String encryptionKey)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_encryptionkey(pointer, encryptionKey == null ? null : encryptionKey.getBytes());
	}

	/**
	 * [in] Optional. Specify 0 to ignore. For sequenced formats with dynamic channel allocation such as .MID and .IT, this specifies the maximum voice count allowed while playing.  .IT defaults to 64.  .MID defaults to 32.
	 */
	public int getMaxPolyphony()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_maxpolyphony(pointer);
		return javaResult;
	}
	public void setMaxPolyphony(int maxPolyphony)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_maxpolyphony(pointer, maxPolyphony);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. This is user data to be attached to the sound during creation.  Access via Sound::getUserData.
	 */
	public Pointer getUserData()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_userdata(pointer);
		return javaResult == 0 ? null : Pointer.newPointer(javaResult);
	}
	public void setUserData(Pointer userData)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_userdata(pointer, Pointer.getPointer(userData));
	}

	/**
	 * [in] Optional. Specify 0 or FMOD_SOUND_TYPE_UNKNOWN to ignore.  Instead of scanning all codec types, use this to speed up loading by making it jump straight to this codec.
	 */
	public FMOD_SOUND_TYPE getSuggestedSoundType()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_suggestedsoundtype(pointer);
		return FMOD_SOUND_TYPE.get(javaResult);
	}
	public void setSuggestedSoundType(FMOD_SOUND_TYPE suggestedSoundType)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_suggestedsoundtype(pointer, suggestedSoundType.asInt());
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Callback for opening this file.
	 */
	public FMOD_FILE_OPENCALLBACK getUserOpen()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_FILE_OPENCALLBACK)CallbackManager.getCallback(22, pointer, false);
	}
	public void setUserOpen(FMOD_FILE_OPENCALLBACK userOpen)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(22, userOpen, pointer);
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_useropen(pointer, userOpen != null);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Callback for closing this file.
	 */
	public FMOD_FILE_CLOSECALLBACK getUserClose()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_FILE_CLOSECALLBACK)CallbackManager.getCallback(23, pointer, false);
	}
	public void setUserClose(FMOD_FILE_CLOSECALLBACK userClose)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(23, userClose, pointer);
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_userclose(pointer, userClose != null);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Callback for reading from this file.
	 */
	public FMOD_FILE_READCALLBACK getUserRead()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_FILE_READCALLBACK)CallbackManager.getCallback(24, pointer, false);
	}
	public void setUserRead(FMOD_FILE_READCALLBACK userRead)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(24, userRead, pointer);
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_userread(pointer, userRead != null);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Callback for seeking within this file.
	 */
	public FMOD_FILE_SEEKCALLBACK getUserSeek()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_FILE_SEEKCALLBACK)CallbackManager.getCallback(25, pointer, false);
	}
	public void setUserSeek(FMOD_FILE_SEEKCALLBACK userSeek)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(25, userSeek, pointer);
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_userseek(pointer, userSeek != null);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Use this to differ the way fmod maps multichannel sounds to speakers.  See FMOD_SPEAKERMAPTYPE for more.
	 */
	public FMOD_SPEAKERMAPTYPE getSpeakerMap()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_speakermap(pointer);
		return FMOD_SPEAKERMAPTYPE.get(javaResult);
	}
	public void setSpeakerMap(FMOD_SPEAKERMAPTYPE speakerMap)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_speakermap(pointer, speakerMap.asInt());
	}

	/**
	 * [in] Optional. Specify 0 to ignore. Specify a sound group if required, to put sound in as it is created.
	 */
	public SoundGroup getInitialSoundGroup()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_initialsoundgroup(pointer);
		return javaResult == 0 ? null : SoundGroup.createView(Pointer.newPointer(javaResult));
	}
	public void setInitialSoundGroup(SoundGroup initialSoundGroup)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_initialsoundgroup(pointer, Pointer.getPointer(initialSoundGroup));
	}

	/**
	 * [in] Optional. Specify 0 to ignore. For streams. Specify an initial position to seek the stream to.
	 */
	public int getInitialSeekPosition()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_initialseekposition(pointer);
		return javaResult;
	}
	public void setInitialSeekPosition(int initialSeekPosition)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_initialseekposition(pointer, initialSeekPosition);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. For streams. Specify the time unit for the position set in initialseekposition.
	 */
	public int getInitialSeekPosType()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_CREATESOUNDEXINFO_get_initialseekpostype(pointer);
		return javaResult;
	}
	public void setInitialSeekPosType(int initialSeekPosType)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_CREATESOUNDEXINFO_set_initialseekpostype(pointer, initialSeekPosType);
	}

}