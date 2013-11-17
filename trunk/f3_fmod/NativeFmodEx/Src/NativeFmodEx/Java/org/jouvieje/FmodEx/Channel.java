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
 * 'Channel' API.
 */
public class Channel extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>Channel</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a Channel object.
	 */
	public static Channel createView(Pointer pointer)
	{
		return new Channel(Pointer.getPointer(pointer));
	}
	private Channel(long pointer)
	{
		super(pointer);
	}

	public Channel()
	{
		super(0);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSystemObject(System system)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_getSystemObject(pointer, system);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT stop()
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addOwner(0, pointer);
		int javaResult = FmodExJNI.Channel_stop(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setPaused(boolean paused)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setPaused(pointer, paused);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getPaused(ByteBuffer paused)
	{
		if(pointer == 0) throw new NullPointerException();
		if(paused != null && !paused.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getPaused(pointer, paused, BufferUtils.getPositionInBytes(paused));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setVolume(float volume)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setVolume(pointer, volume);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getVolume(FloatBuffer volume)
	{
		if(pointer == 0) throw new NullPointerException();
		if(volume != null && !volume.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getVolume(pointer, volume, BufferUtils.getPositionInBytes(volume));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setFrequency(float frequency)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setFrequency(pointer, frequency);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getFrequency(FloatBuffer frequency)
	{
		if(pointer == 0) throw new NullPointerException();
		if(frequency != null && !frequency.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getFrequency(pointer, frequency, BufferUtils.getPositionInBytes(frequency));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setPan(float pan)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setPan(pointer, pan);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getPan(FloatBuffer pan)
	{
		if(pointer == 0) throw new NullPointerException();
		if(pan != null && !pan.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getPan(pointer, pan, BufferUtils.getPositionInBytes(pan));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setDelay(FMOD_DELAYTYPE delaytype, int delayhi, int delaylo)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setDelay(pointer, delaytype.asInt(), delayhi, delaylo);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDelay(FMOD_DELAYTYPE delaytype, IntBuffer delayhi, IntBuffer delaylo)
	{
		if(pointer == 0) throw new NullPointerException();
		if(delayhi != null && !delayhi.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(delaylo != null && !delaylo.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getDelay(pointer, delaytype.asInt(), delayhi, BufferUtils.getPositionInBytes(delayhi), delaylo, BufferUtils.getPositionInBytes(delaylo));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setSpeakerMix(float frontleft, float frontright, float center, float lfe, float backleft, float backright, float sideleft, float sideright)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setSpeakerMix(pointer, frontleft, frontright, center, lfe, backleft, backright, sideleft, sideright);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSpeakerMix(FloatBuffer frontleft, FloatBuffer frontright, FloatBuffer center, FloatBuffer lfe, FloatBuffer backleft, FloatBuffer backright, FloatBuffer sideleft, FloatBuffer sideright)
	{
		if(pointer == 0) throw new NullPointerException();
		if(frontleft != null && !frontleft.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(frontright != null && !frontright.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(center != null && !center.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(lfe != null && !lfe.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(backleft != null && !backleft.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(backright != null && !backright.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(sideleft != null && !sideleft.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(sideright != null && !sideright.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getSpeakerMix(pointer, frontleft, BufferUtils.getPositionInBytes(frontleft), frontright, BufferUtils.getPositionInBytes(frontright), center, BufferUtils.getPositionInBytes(center), lfe, BufferUtils.getPositionInBytes(lfe), backleft, BufferUtils.getPositionInBytes(backleft), backright, BufferUtils.getPositionInBytes(backright), sideleft, BufferUtils.getPositionInBytes(sideleft), sideright, BufferUtils.getPositionInBytes(sideright));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setSpeakerLevels(FMOD_SPEAKER speaker, FloatBuffer levels, int numlevels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(levels != null && !levels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_setSpeakerLevels(pointer, speaker.asInt(), levels, BufferUtils.getPositionInBytes(levels), numlevels);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSpeakerLevels(FMOD_SPEAKER speaker, FloatBuffer levels, int numlevels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(levels != null && !levels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getSpeakerLevels(pointer, speaker.asInt(), levels, BufferUtils.getPositionInBytes(levels), numlevels);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setInputChannelMix(FloatBuffer levels, int numlevels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(levels != null && !levels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_setInputChannelMix(pointer, levels, BufferUtils.getPositionInBytes(levels), numlevels);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getInputChannelMix(FloatBuffer levels, int numlevels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(levels != null && !levels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getInputChannelMix(pointer, levels, BufferUtils.getPositionInBytes(levels), numlevels);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setMute(boolean mute)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setMute(pointer, mute);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMute(ByteBuffer mute)
	{
		if(pointer == 0) throw new NullPointerException();
		if(mute != null && !mute.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getMute(pointer, mute, BufferUtils.getPositionInBytes(mute));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setPriority(int priority)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setPriority(pointer, priority);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getPriority(IntBuffer priority)
	{
		if(pointer == 0) throw new NullPointerException();
		if(priority != null && !priority.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getPriority(pointer, priority, BufferUtils.getPositionInBytes(priority));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setPosition(int position, int postype)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setPosition(pointer, position, postype);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getPosition(IntBuffer position, int postype)
	{
		if(pointer == 0) throw new NullPointerException();
		if(position != null && !position.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getPosition(pointer, position, BufferUtils.getPositionInBytes(position), postype);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setReverbProperties(FMOD_REVERB_CHANNELPROPERTIES prop)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setReverbProperties(pointer, Pointer.getPointer(prop));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getReverbProperties(FMOD_REVERB_CHANNELPROPERTIES prop)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_getReverbProperties(pointer, Pointer.getPointer(prop));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setChannelGroup(ChannelGroup channelgroup)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setChannelGroup(pointer, Pointer.getPointer(channelgroup));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getChannelGroup(ChannelGroup channelgroup)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_getChannelGroup(pointer, channelgroup);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setCallback(FMOD_CHANNEL_CALLBACKTYPE type, FMOD_CHANNEL_CALLBACK callback, int command)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addOwner(callback == null ? 0 : pointer, pointer);
		CallbackManager.addCallback(18, callback, pointer);
		int javaResult = FmodExJNI.Channel_setCallback(pointer, type.asInt(), callback == null ? false : true, command);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DAttributes(FMOD_VECTOR pos, FMOD_VECTOR vel)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_set3DAttributes(pointer, Pointer.getPointer(pos), Pointer.getPointer(vel));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DAttributes(FMOD_VECTOR pos, FMOD_VECTOR vel)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_get3DAttributes(pointer, Pointer.getPointer(pos), Pointer.getPointer(vel));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DMinMaxDistance(float mindistance, float maxdistance)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_set3DMinMaxDistance(pointer, mindistance, maxdistance);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DMinMaxDistance(FloatBuffer mindistance, FloatBuffer maxdistance)
	{
		if(pointer == 0) throw new NullPointerException();
		if(mindistance != null && !mindistance.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(maxdistance != null && !maxdistance.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_get3DMinMaxDistance(pointer, mindistance, BufferUtils.getPositionInBytes(mindistance), maxdistance, BufferUtils.getPositionInBytes(maxdistance));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DConeSettings(float insideconeangle, float outsideconeangle, float outsidevolume)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_set3DConeSettings(pointer, insideconeangle, outsideconeangle, outsidevolume);
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
		int javaResult = FmodExJNI.Channel_get3DConeSettings(pointer, insideconeangle, BufferUtils.getPositionInBytes(insideconeangle), outsideconeangle, BufferUtils.getPositionInBytes(outsideconeangle), outsidevolume, BufferUtils.getPositionInBytes(outsidevolume));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DConeOrientation(FMOD_VECTOR orientation)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_set3DConeOrientation(pointer, Pointer.getPointer(orientation));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DConeOrientation(FMOD_VECTOR orientation)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_get3DConeOrientation(pointer, Pointer.getPointer(orientation));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DCustomRolloff(FMOD_VECTOR points, int numpoints)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_set3DCustomRolloff(pointer, Pointer.getPointer(points), numpoints);
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
		int javaResult = FmodExJNI.Channel_get3DCustomRolloff(pointer, points, numpoints, BufferUtils.getPositionInBytes(numpoints));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DOcclusion(float directocclusion, float reverbocclusion)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_set3DOcclusion(pointer, directocclusion, reverbocclusion);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DOcclusion(FloatBuffer directocclusion, FloatBuffer reverbocclusion)
	{
		if(pointer == 0) throw new NullPointerException();
		if(directocclusion != null && !directocclusion.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(reverbocclusion != null && !reverbocclusion.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_get3DOcclusion(pointer, directocclusion, BufferUtils.getPositionInBytes(directocclusion), reverbocclusion, BufferUtils.getPositionInBytes(reverbocclusion));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DSpread(float angle)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_set3DSpread(pointer, angle);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DSpread(FloatBuffer angle)
	{
		if(pointer == 0) throw new NullPointerException();
		if(angle != null && !angle.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_get3DSpread(pointer, angle, BufferUtils.getPositionInBytes(angle));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DPanLevel(float level)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_set3DPanLevel(pointer, level);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DPanLevel(FloatBuffer level)
	{
		if(pointer == 0) throw new NullPointerException();
		if(level != null && !level.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_get3DPanLevel(pointer, level, BufferUtils.getPositionInBytes(level));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DDopplerLevel(float level)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_set3DDopplerLevel(pointer, level);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DDopplerLevel(FloatBuffer level)
	{
		if(pointer == 0) throw new NullPointerException();
		if(level != null && !level.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_get3DDopplerLevel(pointer, level, BufferUtils.getPositionInBytes(level));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDSPHead(DSP dsp)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_getDSPHead(pointer, dsp);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT addDSP(DSP dsp, DSPConnection connection)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_addDSP(pointer, Pointer.getPointer(dsp), connection);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT isPlaying(ByteBuffer isplaying)
	{
		if(pointer == 0) throw new NullPointerException();
		if(isplaying != null && !isplaying.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_isPlaying(pointer, isplaying, BufferUtils.getPositionInBytes(isplaying));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT isVirtual(ByteBuffer isvirtual)
	{
		if(pointer == 0) throw new NullPointerException();
		if(isvirtual != null && !isvirtual.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_isVirtual(pointer, isvirtual, BufferUtils.getPositionInBytes(isvirtual));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getAudibility(FloatBuffer audibility)
	{
		if(pointer == 0) throw new NullPointerException();
		if(audibility != null && !audibility.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getAudibility(pointer, audibility, BufferUtils.getPositionInBytes(audibility));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getCurrentSound(Sound sound)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_getCurrentSound(pointer, sound);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSpectrum(FloatBuffer spectrumarray, int numvalues, int channeloffset, FMOD_DSP_FFT_WINDOW windowtype)
	{
		if(pointer == 0) throw new NullPointerException();
		if(spectrumarray != null && !spectrumarray.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getSpectrum(pointer, spectrumarray, BufferUtils.getPositionInBytes(spectrumarray), numvalues, channeloffset, windowtype.asInt());
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getWaveData(FloatBuffer wavearray, int numvalues, int channeloffset)
	{
		if(pointer == 0) throw new NullPointerException();
		if(wavearray != null && !wavearray.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getWaveData(pointer, wavearray, BufferUtils.getPositionInBytes(wavearray), numvalues, channeloffset);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getIndex(IntBuffer index)
	{
		if(pointer == 0) throw new NullPointerException();
		if(index != null && !index.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Channel_getIndex(pointer, index, BufferUtils.getPositionInBytes(index));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setMode(int mode)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setMode(pointer, mode);
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
		int javaResult = FmodExJNI.Channel_getMode(pointer, mode, BufferUtils.getPositionInBytes(mode));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setLoopCount(int loopcount)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setLoopCount(pointer, loopcount);
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
		int javaResult = FmodExJNI.Channel_getLoopCount(pointer, loopcount, BufferUtils.getPositionInBytes(loopcount));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setLoopPoints(int loopstart, int loopstarttype, int loopend, int loopendtype)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setLoopPoints(pointer, loopstart, loopstarttype, loopend, loopendtype);
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
		int javaResult = FmodExJNI.Channel_getLoopPoints(pointer, loopstart, BufferUtils.getPositionInBytes(loopstart), loopstarttype, loopend, BufferUtils.getPositionInBytes(loopend), loopendtype);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_setUserData(pointer, Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Channel_getUserData(pointer, userdata);
		return FMOD_RESULT.get(javaResult);
	}

}