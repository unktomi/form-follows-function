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
 * 'Reverb' API
 */
public class Reverb extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>Reverb</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a Reverb object.
	 */
	public static Reverb createView(Pointer pointer)
	{
		return new Reverb(Pointer.getPointer(pointer));
	}
	private Reverb(long pointer)
	{
		super(pointer);
	}

	public Reverb()
	{
		super(0);
	}

	/**
	 * 
	 */
	public FMOD_RESULT release()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Reverb_release(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DAttributes(FMOD_VECTOR position, float mindistance, float maxdistance)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Reverb_set3DAttributes(pointer, Pointer.getPointer(position), mindistance, maxdistance);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DAttributes(FMOD_VECTOR position, FloatBuffer mindistance, FloatBuffer maxdistance)
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
		int javaResult = FmodExJNI.Reverb_get3DAttributes(pointer, Pointer.getPointer(position), mindistance, BufferUtils.getPositionInBytes(mindistance), maxdistance, BufferUtils.getPositionInBytes(maxdistance));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setProperties(FMOD_REVERB_PROPERTIES properties)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Reverb_setProperties(pointer, Pointer.getPointer(properties));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getProperties(FMOD_REVERB_PROPERTIES properties)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Reverb_getProperties(pointer, Pointer.getPointer(properties));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setActive(boolean active)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Reverb_setActive(pointer, active);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getActive(ByteBuffer active)
	{
		if(pointer == 0) throw new NullPointerException();
		if(active != null && !active.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.Reverb_getActive(pointer, active, BufferUtils.getPositionInBytes(active));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Reverb_setUserData(pointer, Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.Reverb_getUserData(pointer, userdata);
		return FMOD_RESULT.get(javaResult);
	}

}