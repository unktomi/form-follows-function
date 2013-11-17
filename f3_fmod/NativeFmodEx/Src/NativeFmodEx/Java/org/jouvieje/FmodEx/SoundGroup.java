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
 * 'SoundGroup' API
 */
public class SoundGroup extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>SoundGroup</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a SoundGroup object.
	 */
	public static SoundGroup createView(Pointer pointer)
	{
		return new SoundGroup(Pointer.getPointer(pointer));
	}
	private SoundGroup(long pointer)
	{
		super(pointer);
	}

	public SoundGroup()
	{
		super(0);
	}

	/**
	 * 
	 */
	public FMOD_RESULT release()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_release(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSystemObject(System system)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_getSystemObject(pointer, system);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setMaxAudible(int maxaudible)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_setMaxAudible(pointer, maxaudible);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMaxAudible(IntBuffer maxaudible)
	{
		if(pointer == 0) throw new NullPointerException();
		if(maxaudible != null && !maxaudible.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.SoundGroup_getMaxAudible(pointer, maxaudible, BufferUtils.getPositionInBytes(maxaudible));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setMaxAudibleBehavior(FMOD_SOUNDGROUP_BEHAVIOR behavior)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_setMaxAudibleBehavior(pointer, behavior.asInt());
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMaxAudibleBehavior(FMOD_SOUNDGROUP_BEHAVIOR[] behavior)
	{
		if(pointer == 0) throw new NullPointerException();
		IntBuffer behaviorPointer = BufferUtils.newIntBuffer(1);
		int javaResult = FmodExJNI.SoundGroup_getMaxAudibleBehavior(pointer, behaviorPointer);
		if(behavior != null) {
			behavior[0] = FMOD_SOUNDGROUP_BEHAVIOR.get(behaviorPointer.get(0));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setMuteFadeSpeed(float speed)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_setMuteFadeSpeed(pointer, speed);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMuteFadeSpeed(FloatBuffer speed)
	{
		if(pointer == 0) throw new NullPointerException();
		if(speed != null && !speed.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.SoundGroup_getMuteFadeSpeed(pointer, speed, BufferUtils.getPositionInBytes(speed));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setVolume(float volume)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_setVolume(pointer, volume);
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
		int javaResult = FmodExJNI.SoundGroup_getVolume(pointer, volume, BufferUtils.getPositionInBytes(volume));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT stop()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_stop(pointer);
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
		int javaResult = FmodExJNI.SoundGroup_getName(pointer, name, BufferUtils.getPositionInBytes(name), namelen);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumSounds(IntBuffer numsounds)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numsounds != null && !numsounds.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.SoundGroup_getNumSounds(pointer, numsounds, BufferUtils.getPositionInBytes(numsounds));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSound(int index, Sound sound)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_getSound(pointer, index, sound);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumPlaying(IntBuffer numplaying)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numplaying != null && !numplaying.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.SoundGroup_getNumPlaying(pointer, numplaying, BufferUtils.getPositionInBytes(numplaying));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_setUserData(pointer, Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.SoundGroup_getUserData(pointer, userdata);
		return FMOD_RESULT.get(javaResult);
	}

}