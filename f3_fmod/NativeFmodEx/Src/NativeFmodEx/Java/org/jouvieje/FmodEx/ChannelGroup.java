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
 * 'ChannelGroup' API
 */
public class ChannelGroup extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>ChannelGroup</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a ChannelGroup object.
	 */
	public static ChannelGroup createView(Pointer pointer)
	{
		return new ChannelGroup(Pointer.getPointer(pointer));
	}
	private ChannelGroup(long pointer)
	{
		super(pointer);
	}

	public ChannelGroup()
	{
		super(0);
	}

	/**
	 * 
	 */
	public FMOD_RESULT release()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_release(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSystemObject(System system)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_getSystemObject(pointer, system);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setVolume(float volume)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_setVolume(pointer, volume);
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
		int javaResult = FmodExJNI.ChannelGroup_getVolume(pointer, volume, BufferUtils.getPositionInBytes(volume));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setPitch(float pitch)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_setPitch(pointer, pitch);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getPitch(FloatBuffer pitch)
	{
		if(pointer == 0) throw new NullPointerException();
		if(pitch != null && !pitch.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.ChannelGroup_getPitch(pointer, pitch, BufferUtils.getPositionInBytes(pitch));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DOcclusion(float directocclusion, float reverbocclusion)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_set3DOcclusion(pointer, directocclusion, reverbocclusion);
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
		int javaResult = FmodExJNI.ChannelGroup_get3DOcclusion(pointer, directocclusion, BufferUtils.getPositionInBytes(directocclusion), reverbocclusion, BufferUtils.getPositionInBytes(reverbocclusion));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setPaused(boolean paused)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_setPaused(pointer, paused);
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
		int javaResult = FmodExJNI.ChannelGroup_getPaused(pointer, paused, BufferUtils.getPositionInBytes(paused));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setMute(boolean mute)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_setMute(pointer, mute);
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
		int javaResult = FmodExJNI.ChannelGroup_getMute(pointer, mute, BufferUtils.getPositionInBytes(mute));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT stop()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_stop(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT overrideVolume(float volume)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_overrideVolume(pointer, volume);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT overrideFrequency(float frequency)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_overrideFrequency(pointer, frequency);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT overridePan(float pan)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_overridePan(pointer, pan);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT overrideReverbProperties(FMOD_REVERB_CHANNELPROPERTIES prop)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_overrideReverbProperties(pointer, Pointer.getPointer(prop));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT override3DAttributes(FMOD_VECTOR pos, FMOD_VECTOR vel)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_override3DAttributes(pointer, Pointer.getPointer(pos), Pointer.getPointer(vel));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT overrideSpeakerMix(float frontleft, float frontright, float center, float lfe, float backleft, float backright, float sideleft, float sideright)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_overrideSpeakerMix(pointer, frontleft, frontright, center, lfe, backleft, backright, sideleft, sideright);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT addGroup(ChannelGroup group)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_addGroup(pointer, Pointer.getPointer(group));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumGroups(IntBuffer numgroups)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numgroups != null && !numgroups.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.ChannelGroup_getNumGroups(pointer, numgroups, BufferUtils.getPositionInBytes(numgroups));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getGroup(int index, ChannelGroup group)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_getGroup(pointer, index, group);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getParentGroup(ChannelGroup group)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_getParentGroup(pointer, group);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDSPHead(DSP dsp)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_getDSPHead(pointer, dsp);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT addDSP(DSP dsp, DSPConnection connection)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_addDSP(pointer, Pointer.getPointer(dsp), connection);
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
		int javaResult = FmodExJNI.ChannelGroup_getName(pointer, name, BufferUtils.getPositionInBytes(name), namelen);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumChannels(IntBuffer numchannels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numchannels != null && !numchannels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.ChannelGroup_getNumChannels(pointer, numchannels, BufferUtils.getPositionInBytes(numchannels));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getChannel(int index, Channel channel)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_getChannel(pointer, index, channel);
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
		int javaResult = FmodExJNI.ChannelGroup_getSpectrum(pointer, spectrumarray, BufferUtils.getPositionInBytes(spectrumarray), numvalues, channeloffset, windowtype.asInt());
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
		int javaResult = FmodExJNI.ChannelGroup_getWaveData(pointer, wavearray, BufferUtils.getPositionInBytes(wavearray), numvalues, channeloffset);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_setUserData(pointer, Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.ChannelGroup_getUserData(pointer, userdata);
		return FMOD_RESULT.get(javaResult);
	}

}