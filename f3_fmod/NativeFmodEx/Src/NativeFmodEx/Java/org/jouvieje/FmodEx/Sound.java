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

package org.jouvieje.FmodEx;

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
 * 'Sound' API
 */
public class Sound extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>Sound</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a Sound object.
	 */
	public static Sound createView(Pointer pointer)
	{
		return new Sound(Pointer.getPointer(pointer));
	}
	private Sound(long pointer)
	{
		super(pointer);
	}

	public Sound()
	{
		super(0);
	}

	/**
	 * 
	 */
	public FMOD_RESULT release()
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addOwner(0, pointer);
		int javaResult = FmodExJNI.Sound_release(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSystemObject(System system)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_getSystemObject(pointer, system);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT lock(int offset, int length, ByteBuffer[] ptr1, ByteBuffer[] ptr2, IntBuffer len1, IntBuffer len2)
	{
		if(pointer == 0) throw new NullPointerException();
		if(len1 != null && !len1.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(len2 != null && !len2.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_lock(pointer, offset, length, ptr1, ptr2, len1, BufferUtils.getPositionInBytes(len1), len2, BufferUtils.getPositionInBytes(len2));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT unlock(ByteBuffer ptr1, ByteBuffer ptr2, int len1, int len2)
	{
		if(pointer == 0) throw new NullPointerException();
		if(ptr1 != null && !ptr1.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(ptr2 != null && !ptr2.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_unlock(pointer, ptr1, BufferUtils.getPositionInBytes(ptr1), ptr2, BufferUtils.getPositionInBytes(ptr2), len1, len2);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setDefaults(float frequency, float volume, float pan, int priority)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_setDefaults(pointer, frequency, volume, pan, priority);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDefaults(FloatBuffer frequency, FloatBuffer volume, FloatBuffer pan, IntBuffer priority)
	{
		if(pointer == 0) throw new NullPointerException();
		if(frequency != null && !frequency.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(volume != null && !volume.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(pan != null && !pan.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(priority != null && !priority.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getDefaults(pointer, frequency, BufferUtils.getPositionInBytes(frequency), volume, BufferUtils.getPositionInBytes(volume), pan, BufferUtils.getPositionInBytes(pan), priority, BufferUtils.getPositionInBytes(priority));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setVariations(float frequencyvar, float volumevar, float panvar)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_setVariations(pointer, frequencyvar, volumevar, panvar);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getVariations(FloatBuffer frequencyvar, FloatBuffer volumevar, FloatBuffer panvar)
	{
		if(pointer == 0) throw new NullPointerException();
		if(frequencyvar != null && !frequencyvar.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(volumevar != null && !volumevar.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(panvar != null && !panvar.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getVariations(pointer, frequencyvar, BufferUtils.getPositionInBytes(frequencyvar), volumevar, BufferUtils.getPositionInBytes(volumevar), panvar, BufferUtils.getPositionInBytes(panvar));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DMinMaxDistance(float min, float max)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_set3DMinMaxDistance(pointer, min, max);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DMinMaxDistance(FloatBuffer min, FloatBuffer max)
	{
		if(pointer == 0) throw new NullPointerException();
		if(min != null && !min.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(max != null && !max.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_get3DMinMaxDistance(pointer, min, BufferUtils.getPositionInBytes(min), max, BufferUtils.getPositionInBytes(max));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DConeSettings(float insideconeangle, float outsideconeangle, float outsidevolume)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_set3DConeSettings(pointer, insideconeangle, outsideconeangle, outsidevolume);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DConeSettings(FloatBuffer insideconeangle, FloatBuffer outsideconeangle, FloatBuffer outsidevolume)
	{
		if(pointer == 0) throw new NullPointerException();
		if(insideconeangle != null && !insideconeangle.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(outsideconeangle != null && !outsideconeangle.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(outsidevolume != null && !outsidevolume.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_get3DConeSettings(pointer, insideconeangle, BufferUtils.getPositionInBytes(insideconeangle), outsideconeangle, BufferUtils.getPositionInBytes(outsideconeangle), outsidevolume, BufferUtils.getPositionInBytes(outsidevolume));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DCustomRolloff(FMOD_VECTOR points, int numpoints)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_set3DCustomRolloff(pointer, Pointer.getPointer(points), numpoints);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DCustomRolloff(FMOD_VECTOR points, IntBuffer numpoints)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numpoints != null && !numpoints.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_get3DCustomRolloff(pointer, points, numpoints, BufferUtils.getPositionInBytes(numpoints));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setSubSound(int index, Sound subsound)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_setSubSound(pointer, index, Pointer.getPointer(subsound));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSubSound(int index, Sound subsound)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_getSubSound(pointer, index, subsound);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setSubSoundSentence(IntBuffer subsoundlist, int numsubsounds)
	{
		if(pointer == 0) throw new NullPointerException();
		if(subsoundlist != null && !subsoundlist.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_setSubSoundSentence(pointer, subsoundlist, BufferUtils.getPositionInBytes(subsoundlist), numsubsounds);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getName(ByteBuffer name, int namelen)
	{
		if(pointer == 0) throw new NullPointerException();
		if(name != null && !name.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getName(pointer, name, BufferUtils.getPositionInBytes(name), namelen);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getLength(IntBuffer length, int lengthtype)
	{
		if(pointer == 0) throw new NullPointerException();
		if(length != null && !length.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getLength(pointer, length, BufferUtils.getPositionInBytes(length), lengthtype);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getFormat(FMOD_SOUND_TYPE[] type, FMOD_SOUND_FORMAT[] format, IntBuffer channels, IntBuffer bits)
	{
		if(pointer == 0) throw new NullPointerException();
		IntBuffer typePointer = BufferUtils.newIntBuffer(1);
		IntBuffer formatPointer = BufferUtils.newIntBuffer(1);
		if(channels != null && !channels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(bits != null && !bits.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getFormat(pointer, typePointer, formatPointer, channels, BufferUtils.getPositionInBytes(channels), bits, BufferUtils.getPositionInBytes(bits));
		if(type != null) {
			type[0] = FMOD_SOUND_TYPE.get(typePointer.get(0));
		}
		if(format != null) {
			format[0] = FMOD_SOUND_FORMAT.get(formatPointer.get(0));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumSubSounds(IntBuffer numsubsounds)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numsubsounds != null && !numsubsounds.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getNumSubSounds(pointer, numsubsounds, BufferUtils.getPositionInBytes(numsubsounds));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumTags(IntBuffer numtags, IntBuffer numtagsupdated)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numtags != null && !numtags.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(numtagsupdated != null && !numtagsupdated.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getNumTags(pointer, numtags, BufferUtils.getPositionInBytes(numtags), numtagsupdated, BufferUtils.getPositionInBytes(numtagsupdated));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getTag(String name, int index, FMOD_TAG tag)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_getTag(pointer, name == null ? null : name.getBytes(), index, Pointer.getPointer(tag));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getOpenState(FMOD_OPENSTATE[] openstate, IntBuffer percentbuffered, ByteBuffer starving)
	{
		if(pointer == 0) throw new NullPointerException();
		IntBuffer openstatePointer = BufferUtils.newIntBuffer(1);
		if(percentbuffered != null && !percentbuffered.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(starving != null && !starving.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getOpenState(pointer, openstatePointer, percentbuffered, BufferUtils.getPositionInBytes(percentbuffered), starving, BufferUtils.getPositionInBytes(starving));
		if(openstate != null) {
			openstate[0] = FMOD_OPENSTATE.get(openstatePointer.get(0));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT readData(ByteBuffer buffer, int lenbytes, IntBuffer read)
	{
		if(pointer == 0) throw new NullPointerException();
		if(buffer != null && !buffer.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(read != null && !read.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_readData(pointer, buffer, BufferUtils.getPositionInBytes(buffer), lenbytes, read, BufferUtils.getPositionInBytes(read));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT seekData(int pcm)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_seekData(pointer, pcm);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setSoundGroup(SoundGroup soundgroup)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_setSoundGroup(pointer, Pointer.getPointer(soundgroup));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSoundGroup(SoundGroup soundgroup)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_getSoundGroup(pointer, soundgroup);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumSyncPoints(IntBuffer numsyncpoints)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numsyncpoints != null && !numsyncpoints.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getNumSyncPoints(pointer, numsyncpoints, BufferUtils.getPositionInBytes(numsyncpoints));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSyncPoint(int index, FMOD_SYNCPOINT point)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_getSyncPoint(pointer, index, point);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSyncPointInfo(FMOD_SYNCPOINT point, ByteBuffer name, int namelen, IntBuffer offset, int offsettype)
	{
		if(pointer == 0) throw new NullPointerException();
		if(name != null && !name.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(offset != null && !offset.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getSyncPointInfo(pointer, Pointer.getPointer(point), name, BufferUtils.getPositionInBytes(name), namelen, offset, BufferUtils.getPositionInBytes(offset), offsettype);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT addSyncPoint(int offset, int offsettype, String name, FMOD_SYNCPOINT point)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_addSyncPoint(pointer, offset, offsettype, name == null ? null : name.getBytes(), point);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT deleteSyncPoint(FMOD_SYNCPOINT point)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_deleteSyncPoint(pointer, Pointer.getPointer(point));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setMode(int mode)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_setMode(pointer, mode);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMode(IntBuffer mode)
	{
		if(pointer == 0) throw new NullPointerException();
		if(mode != null && !mode.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getMode(pointer, mode, BufferUtils.getPositionInBytes(mode));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setLoopCount(int loopcount)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_setLoopCount(pointer, loopcount);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getLoopCount(IntBuffer loopcount)
	{
		if(pointer == 0) throw new NullPointerException();
		if(loopcount != null && !loopcount.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getLoopCount(pointer, loopcount, BufferUtils.getPositionInBytes(loopcount));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setLoopPoints(int loopstart, int loopstarttype, int loopend, int loopendtype)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_setLoopPoints(pointer, loopstart, loopstarttype, loopend, loopendtype);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getLoopPoints(IntBuffer loopstart, int loopstarttype, IntBuffer loopend, int loopendtype)
	{
		if(pointer == 0) throw new NullPointerException();
		if(loopstart != null && !loopstart.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(loopend != null && !loopend.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getLoopPoints(pointer, loopstart, BufferUtils.getPositionInBytes(loopstart), loopstarttype, loopend, BufferUtils.getPositionInBytes(loopend), loopendtype);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMusicNumChannels(IntBuffer numchannels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numchannels != null && !numchannels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getMusicNumChannels(pointer, numchannels, BufferUtils.getPositionInBytes(numchannels));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setMusicChannelVolume(int channel, float volume)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_setMusicChannelVolume(pointer, channel, volume);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMusicChannelVolume(int channel, FloatBuffer volume)
	{
		if(pointer == 0) throw new NullPointerException();
		if(volume != null && !volume.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Sound_getMusicChannelVolume(pointer, channel, volume, BufferUtils.getPositionInBytes(volume));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_setUserData(pointer, Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Sound_getUserData(pointer, userdata);
		return FMOD_RESULT.get(javaResult);
	}

}