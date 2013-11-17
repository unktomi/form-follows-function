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
 *     Settings for advanced features like configuring memory and cpu usage for the FMOD_CREATECOMPRESSEDSAMPLE feature.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     maxMPEGcodecs / maxADPCMcodecs / maxXMAcodecs will determine the maximum cpu usage of playing realtime samples.  Use this to lower potential excess cpu usage and also control memory usage.<BR>
 * <BR>
 *     maxPCMcodecs is for use with PS3 only. It will determine the maximum number of PCM voices that can be played at once. This includes streams of any format and all sounds created<BR>
 *     *without* the FMOD_CREATECOMPRESSEDSAMPLE flag.<BR>
 * <BR>
 *     Memory will be allocated for codecs 'up front' (during System::init) if these values are specified as non zero.  If any are zero, it allocates memory for the codec whenever a file of the type in question is loaded.  So if maxMPEGcodecs is 0 for example, it will allocate memory for the mpeg codecs the first time an mp3 is loaded or an mp3 based .FSB file is loaded.<BR>
 * <BR>
 *     Due to inefficient encoding techniques on certain .wav based ADPCM files, FMOD can can need an extra 29720 bytes per codec.  This means for lowest memory consumption.  Use FSB as it uses an optimal/small ADPCM block size.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::setAdvancedSettings<BR>
 *     System::getAdvancedSettings<BR>
 *     System::init<BR>
 *     FMOD_MODE<BR>
 * 
 */
public class FMOD_ADVANCEDSETTINGS extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_ADVANCEDSETTINGS</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_ADVANCEDSETTINGS object.
	 */
	public static FMOD_ADVANCEDSETTINGS createView(Pointer pointer)
	{
		return new FMOD_ADVANCEDSETTINGS(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_ADVANCEDSETTINGS</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_ADVANCEDSETTINGS obj = FMOD_ADVANCEDSETTINGS.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_ADVANCEDSETTINGS create()
	{
		return new FMOD_ADVANCEDSETTINGS(StructureJNI.FMOD_ADVANCEDSETTINGS_new());
	}

	protected FMOD_ADVANCEDSETTINGS(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_ADVANCEDSETTINGS</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_ADVANCEDSETTINGS obj = new FMOD_ADVANCEDSETTINGS();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_ADVANCEDSETTINGS</code>, use the static "constructor" :
	 * <pre><code>  FMOD_ADVANCEDSETTINGS obj = FMOD_ADVANCEDSETTINGS.create();</code></pre>
	 * @see FMOD_ADVANCEDSETTINGS#create()
	 */
	public FMOD_ADVANCEDSETTINGS()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{

			StructureJNI.FMOD_ADVANCEDSETTINGS_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [in]     Size of this structure.  Use sizeof(FMOD_ADVANCEDSETTINGS)  NOTE: This must be set before calling System::getAdvancedSettings!
	 */
	public int getCbSize()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_cbsize(pointer);
		return javaResult;
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with FMOD_CREATECOMPRESSEDSAMPLE only.  Mpeg  codecs consume 29,424 bytes per instance and this number will determine how many mpeg channels can be played simultaneously.  Default = 16.
	 */
	public int getMaxMpegCodecs()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_maxMPEGcodecs(pointer);
		return javaResult;
	}
	public void setMaxMpegCodecs(int maxMpegCodecs)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_maxMPEGcodecs(pointer, maxMpegCodecs);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with FMOD_CREATECOMPRESSEDSAMPLE only.  ADPCM codecs consume 2,136 bytes per instance (based on FSB encoded ADPCM block size - see remarks) and this number will determine how many ADPCM channels can be played simultaneously.  Default = 32.
	 */
	public int getMaxAdpcmCodecs()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_maxADPCMcodecs(pointer);
		return javaResult;
	}
	public void setMaxAdpcmCodecs(int maxAdpcmCodecs)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_maxADPCMcodecs(pointer, maxAdpcmCodecs);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with FMOD_CREATECOMPRESSEDSAMPLE only.  XMA   codecs consume 20,512 bytes per instance and this number will determine how many XMA channels can be played simultaneously.  Default = 32.
	 */
	public int getMaxXmaCodecs()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_maxXMAcodecs(pointer);
		return javaResult;
	}
	public void setMaxXmaCodecs(int maxXmaCodecs)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_maxXMAcodecs(pointer, maxXmaCodecs);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with PS3 only.                          PCM   codecs consume 12,672 bytes per instance and this number will determine how many streams and PCM voices can be played simultaneously. Default = 16
	 */
	public int getMaxPCMCodecs()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_maxPCMcodecs(pointer);
		return javaResult;
	}
	public void setMaxPCMCodecs(int maxPCMCodecs)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_maxPCMcodecs(pointer, maxPCMCodecs);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. Number of channels available on the ASIO device.
	 */
	public int getASIONumChannels()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_ASIONumChannels(pointer);
		return javaResult;
	}
	public void setASIONumChannels(int ASIONumChannels)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_ASIONumChannels(pointer, ASIONumChannels);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. Pointer to an array of strings (number of entries defined by ASIONumChannels) with ASIO channel names.
	 */
	public String[] getASIOChannelList()
	{
		if(pointer == 0) throw new NullPointerException();
		String[] javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_ASIOChannelList(pointer);
		return javaResult;
	}
	public void setASIOChannelList(String[] ASIOChannelList)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_ASIOChannelList(pointer, ASIOChannelList);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. Pointer to a list of speakers that the ASIO channels map to.  This can be called after System::init to remap ASIO output.
	 */
	public FMOD_SPEAKER[] getASIOSpeakerList()
	{
		if(pointer == 0) throw new NullPointerException();
		ByteBuffer javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_ASIOSpeakerList(pointer);
		if(javaResult != null) {
			javaResult.order(ByteOrder.nativeOrder());
		}
		if(javaResult == null) return null;
		IntBuffer ASIOSpeakerListIntBuffer = javaResult.asIntBuffer();
		if(ASIOSpeakerListIntBuffer.capacity() <= 0) return null;
		FMOD_SPEAKER[] ASIOSpeakerList = new FMOD_SPEAKER[ASIOSpeakerListIntBuffer.capacity()];
		for(int i = 0; i < ASIOSpeakerList.length; i++) {
			ASIOSpeakerList[i] = FMOD_SPEAKER.get(ASIOSpeakerListIntBuffer.get());
		}
		return ASIOSpeakerList;
	}
	public void setASIOSpeakerList(FMOD_SPEAKER[] ASIOSpeakerList)
	{
		if(pointer == 0) throw new NullPointerException();
		ByteBuffer ASIOSpeakerListBuffer = BufferUtils.newByteBuffer(ASIOSpeakerList.length * BufferUtils.SIZEOF_INT);
		for(int i = 0; i < ASIOSpeakerList.length; i++) {
			ASIOSpeakerListBuffer.putInt(i, ASIOSpeakerList[i].asInt());
		}
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_ASIOSpeakerList(pointer, ASIOSpeakerListBuffer, BufferUtils.getPositionInBytes(ASIOSpeakerListBuffer));
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. The max number of 3d reverb DSP's in the system.
	 */
	public int getMax3DReverbDSPs()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_max3DReverbDSPs(pointer);
		return javaResult;
	}
	public void setMax3DReverbDSPs(int max3DReverbDSPs)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_max3DReverbDSPs(pointer, max3DReverbDSPs);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with FMOD_INIT_SOFTWARE_HRTF.  The angle range (0-360) of a 3D sound in relation to the listener, at which the HRTF function begins to have an effect. 0 = in front of the listener. 180 = from 90 degrees to the left of the listener to 90 degrees to the right. 360 = behind the listener. Default = 180.0.
	 */
	public float getHRTFMinAngle()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_HRTFMinAngle(pointer);
		return javaResult;
	}
	public void setHRTFMinAngle(float HRTFMinAngle)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_HRTFMinAngle(pointer, HRTFMinAngle);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with FMOD_INIT_SOFTWARE_HRTF.  The angle range (0-360) of a 3D sound in relation to the listener, at which the HRTF function has maximum effect. 0 = front of the listener. 180 = from 90 degrees to the left of the listener to 90 degrees to the right. 360 = behind the listener. Default = 360.0.
	 */
	public float getHRTFMaxAngle()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_HRTFMaxAngle(pointer);
		return javaResult;
	}
	public void setHRTFMaxAngle(float HRTFMaxAngle)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_HRTFMaxAngle(pointer, HRTFMaxAngle);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with FMOD_INIT_SOFTWARE_HRTF.  The cutoff frequency of the HRTF's lowpass filter function when at maximum effect. (i.e. at HRTFMaxAngle).  Default = 4000.0.
	 */
	public float getHRTFFreq()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_HRTFFreq(pointer);
		return javaResult;
	}
	public void setHRTFFreq(float HRTFFreq)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_HRTFFreq(pointer, HRTFFreq);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with FMOD_INIT_VOL0_BECOMES_VIRTUAL.  If this flag is used, and the volume is 0.0, then the sound will become virtual.  Use this value to raise the threshold to a different point where a sound goes virtual.
	 */
	public float getVol0VirtualVol()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_vol0virtualvol(pointer);
		return javaResult;
	}
	public void setVol0VirtualVol(float vol0VirtualVol)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_vol0virtualvol(pointer, vol0VirtualVol);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with FMOD Event system only.  Specifies the number of slots available for simultaneous non blocking loads.  Default = 32.
	 */
	public int getEventQueueSize()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_eventqueuesize(pointer);
		return javaResult;
	}
	public void setEventQueueSize(int eventQueueSize)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_eventqueuesize(pointer, eventQueueSize);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For streams. This determines the default size of the double buffer (in milliseconds) that a stream uses.  Default = 400ms
	 */
	public int getDefaultDecodeBufferSize()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_defaultDecodeBufferSize(pointer);
		return javaResult;
	}
	public void setDefaultDecodeBufferSize(int defaultDecodeBufferSize)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_defaultDecodeBufferSize(pointer, defaultDecodeBufferSize);
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. Gives fmod's logging system a path/filename.  Normally the log is placed in the same directory as the executable and called fmod.log. When using System::getAdvancedSettings, provide at least 256 bytes of memory to copy into.
	 */
	public String getDebugLogFilename()
	{
		if(pointer == 0) throw new NullPointerException();
		String javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_debugLogFilename(pointer);
		return javaResult;
	}
	public void setDebugLogFilename(String debugLogFilename)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_debugLogFilename(pointer, debugLogFilename == null ? null : debugLogFilename.getBytes());
	}

	/**
	 * [in/out] Optional. Specify 0 to ignore. For use with FMOD_INIT_ENABLE_PROFILE.  Specify the port to listen on for connections by the profiler application.
	 */
	public short getProfilePort()
	{
		if(pointer == 0) throw new NullPointerException();
		short javaResult = StructureJNI.FMOD_ADVANCEDSETTINGS_get_profileport(pointer);
		return javaResult;
	}
	public void setProfilePort(short profilePort)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_ADVANCEDSETTINGS_set_profileport(pointer, profilePort);
	}

}