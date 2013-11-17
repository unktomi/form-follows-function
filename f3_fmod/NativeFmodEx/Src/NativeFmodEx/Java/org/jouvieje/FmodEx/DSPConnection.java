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
 * 'DSPConnection' API
 */
public class DSPConnection extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>DSPConnection</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a DSPConnection object.
	 */
	public static DSPConnection createView(Pointer pointer)
	{
		return new DSPConnection(Pointer.getPointer(pointer));
	}
	private DSPConnection(long pointer)
	{
		super(pointer);
	}

	public DSPConnection()
	{
		super(0);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getInput(DSP input)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSPConnection_getInput(pointer, input);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getOutput(DSP output)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSPConnection_getOutput(pointer, output);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setMix(float volume)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSPConnection_setMix(pointer, volume);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMix(FloatBuffer volume)
	{
		if(pointer == 0) throw new NullPointerException();
		if(volume != null && !volume.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSPConnection_getMix(pointer, volume, BufferUtils.getPositionInBytes(volume));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setLevels(FMOD_SPEAKER speaker, FloatBuffer levels, int numlevels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(levels != null && !levels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSPConnection_setLevels(pointer, speaker.asInt(), levels, BufferUtils.getPositionInBytes(levels), numlevels);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getLevels(FMOD_SPEAKER speaker, FloatBuffer levels, int numlevels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(levels != null && !levels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSPConnection_getLevels(pointer, speaker.asInt(), levels, BufferUtils.getPositionInBytes(levels), numlevels);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSPConnection_setUserData(pointer, Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSPConnection_getUserData(pointer, userdata);
		return FMOD_RESULT.get(javaResult);
	}

}