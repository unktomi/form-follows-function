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
 *     Structure describing a point in 3D space.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     FMOD uses a left handed co-ordinate system by default.<BR>
 *     To use a right handed co-ordinate system specify FMOD_INIT_3D_RIGHTHANDED from FMOD_INITFLAGS in System::init.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::set3DListenerAttributes<BR>
 *     System::get3DListenerAttributes<BR>
 *     Channel::set3DAttributes<BR>
 *     Channel::get3DAttributes<BR>
 *     Channel::set3DCustomRolloff<BR>
 *     Channel::get3DCustomRolloff<BR>
 *     Sound::set3DCustomRolloff<BR>
 *     Sound::get3DCustomRolloff<BR>
 *     Geometry::addPolygon<BR>
 *     Geometry::setPolygonVertex<BR>
 *     Geometry::getPolygonVertex<BR>
 *     Geometry::setRotation<BR>
 *     Geometry::getRotation<BR>
 *     Geometry::setPosition<BR>
 *     Geometry::getPosition<BR>
 *     Geometry::setScale<BR>
 *     Geometry::getScale<BR>
 *     FMOD_INITFLAGS<BR>
 * 
 */
public class FMOD_VECTOR extends Pointer
{
	/**
	 * Create a new <code>FMOD_VECTOR</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_VECTOR obj = FMOD_VECTOR.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 * @param x X co-ordinate in 3D space.
	 * @param y Y co-ordinate in 3D space.
	 * @param z Z co-ordinate in 3D space.
	 */
	public static FMOD_VECTOR create(float x, float y, float z)
	{
		return new FMOD_VECTOR(StructureJNI.FMOD_VECTOR_create(x, y, z));
	}

	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_VECTOR</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_VECTOR object.
	 */
	public static FMOD_VECTOR createView(Pointer pointer)
	{
		return new FMOD_VECTOR(Pointer.getPointer(pointer));
	}
	private static long SIZEOF_FMOD_VECTOR = -1;
	/**
	 * Create and initialize a new <code>FMOD_VECTOR[]</code>.<br>
	 * @param length length of the array returned.
	 */
	public static FMOD_VECTOR[] create(int length)
	{
		if(length <= 0)
			 return null;
		
		long first = StructureJNI.FMOD_VECTOR_newArray(length);
		
		if(SIZEOF_FMOD_VECTOR == -1)
			SIZEOF_FMOD_VECTOR = StructureJNI.FMOD_VECTOR_SIZEOF();
		
		FMOD_VECTOR[] array = new FMOD_VECTOR[length];
		for(int i = 0; i < length; i++)
			array[i] = new FMOD_VECTOR(first + i * SIZEOF_FMOD_VECTOR);
		
		return array;
	}

	/**
	 * Create a new <code>FMOD_VECTOR</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_VECTOR obj = FMOD_VECTOR.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_VECTOR create()
	{
		return new FMOD_VECTOR(StructureJNI.FMOD_VECTOR_new());
	}

	protected FMOD_VECTOR(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_VECTOR</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_VECTOR obj = new FMOD_VECTOR();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_VECTOR</code>, use the static "constructor" :
	 * <pre><code>  FMOD_VECTOR obj = FMOD_VECTOR.create();</code></pre>
	 * @see FMOD_VECTOR#create()
	 */
	public FMOD_VECTOR()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{

			StructureJNI.FMOD_VECTOR_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * X co-ordinate in 3D space.
	 */
	public float getX()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_VECTOR_get_x(pointer);
		return javaResult;
	}
	public void setX(float x)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_VECTOR_set_x(pointer, x);
	}

	/**
	 * Y co-ordinate in 3D space.
	 */
	public float getY()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_VECTOR_get_y(pointer);
		return javaResult;
	}
	public void setY(float y)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_VECTOR_set_y(pointer, y);
	}

	/**
	 * Z co-ordinate in 3D space.
	 */
	public float getZ()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_VECTOR_get_z(pointer);
		return javaResult;
	}
	public void setZ(float z)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_VECTOR_set_z(pointer, z);
	}

	/**
	 * X, Y & Z co-ordinate in 3D space.
	 */
	public void setXYZ(FMOD_VECTOR vector)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_VECTOR_set_xyz(pointer, Pointer.getPointer(vector));
	}
	
	/**
	 * X, Y & Z co-ordinate in 3D space.
	 */
	public void setXYZ(float x, float y, float z)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_VECTOR_set_xyz(pointer, x, y, z);
	}
}