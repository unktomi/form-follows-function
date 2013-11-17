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
 *     Structure describing a globally unique identifier.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::getDriverInfo<BR>
 * 
 */
public class FMOD_GUID extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_GUID</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_GUID object.
	 */
	public static FMOD_GUID createView(Pointer pointer)
	{
		return new FMOD_GUID(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_GUID</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_GUID obj = FMOD_GUID.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_GUID create()
	{
		return new FMOD_GUID(StructureJNI.FMOD_GUID_new());
	}

	protected FMOD_GUID(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_GUID</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_GUID obj = new FMOD_GUID();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_GUID</code>, use the static "constructor" :
	 * <pre><code>  FMOD_GUID obj = FMOD_GUID.create();</code></pre>
	 * @see FMOD_GUID#create()
	 */
	public FMOD_GUID()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{

			StructureJNI.FMOD_GUID_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * Specifies the first 8 hexadecimal digits of the GUID
	 */
	public int getData1()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_GUID_get_Data1(pointer);
		return javaResult;
	}
	public void setData1(int data1)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_GUID_set_Data1(pointer, data1);
	}

	/**
	 * Specifies the first group of 4 hexadecimal digits.
	 */
	public short getData2()
	{
		if(pointer == 0) throw new NullPointerException();
		short javaResult = StructureJNI.FMOD_GUID_get_Data2(pointer);
		return javaResult;
	}
	public void setData2(short data2)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_GUID_set_Data2(pointer, data2);
	}

	/**
	 * Specifies the second group of 4 hexadecimal digits.
	 */
	public short getData3()
	{
		if(pointer == 0) throw new NullPointerException();
		short javaResult = StructureJNI.FMOD_GUID_get_Data3(pointer);
		return javaResult;
	}
	public void setData3(short data3)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_GUID_set_Data3(pointer, data3);
	}

	/**
	 * Array of 8 bytes. The first 2 bytes contain the third group of 4 hexadecimal digits. The remaining 6 bytes contain the final 12 hexadecimal digits.
	 */
	public CharBuffer getData4()
	{
		if(pointer == 0) throw new NullPointerException();
		ByteBuffer javaResult = StructureJNI.FMOD_GUID_get_Data4(pointer);
		if(javaResult != null) {
			javaResult.order(ByteOrder.nativeOrder());
		}
		return javaResult.asCharBuffer();
	}
	public void setData4(String data4)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_GUID_set_Data4(pointer, data4 == null ? null : data4.getBytes());
	}

}