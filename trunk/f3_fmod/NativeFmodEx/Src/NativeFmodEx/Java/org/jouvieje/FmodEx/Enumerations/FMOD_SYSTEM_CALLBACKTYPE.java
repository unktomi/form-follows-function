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

package org.jouvieje.FmodEx.Enumerations;

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
import java.util.HashMap;

/**
 * <BR>
 *     <BR>
 *     These callback types are used with System::setCallback.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Each callback has commanddata parameters passed as void* unique to the type of callback.<BR>
 *     See reference to FMOD_SYSTEM_CALLBACK to determine what they might mean for each type of callback.<BR>
 * <BR>
 *     <b>Note!</b> Using FMOD_SYSTEM_CALLBACKTYPE_DEVICELISTCHANGED (on Mac only) requires the application to be running an event loop which will allow external changes to device list to be detected by FMOD.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::setCallback<BR>
 *     FMOD_SYSTEM_CALLBACK<BR>
 *     System::update<BR>
 *     DSP::addInput<BR>
 * 
 */
public class FMOD_SYSTEM_CALLBACKTYPE implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_SYSTEM_CALLBACKTYPE FMOD_SYSTEM_CALLBACKTYPE_DEVICELISTCHANGED = new FMOD_SYSTEM_CALLBACKTYPE("FMOD_SYSTEM_CALLBACKTYPE_DEVICELISTCHANGED", EnumerationJNI.get_FMOD_SYSTEM_CALLBACKTYPE_DEVICELISTCHANGED());
	/** Called directly when a memory allocation fails somewhere in FMOD. */
	public final static FMOD_SYSTEM_CALLBACKTYPE FMOD_SYSTEM_CALLBACKTYPE_MEMORYALLOCATIONFAILED = new FMOD_SYSTEM_CALLBACKTYPE("FMOD_SYSTEM_CALLBACKTYPE_MEMORYALLOCATIONFAILED", EnumerationJNI.get_FMOD_SYSTEM_CALLBACKTYPE_MEMORYALLOCATIONFAILED());
	/** Called directly when a thread is created. */
	public final static FMOD_SYSTEM_CALLBACKTYPE FMOD_SYSTEM_CALLBACKTYPE_THREADCREATED = new FMOD_SYSTEM_CALLBACKTYPE("FMOD_SYSTEM_CALLBACKTYPE_THREADCREATED", EnumerationJNI.get_FMOD_SYSTEM_CALLBACKTYPE_THREADCREATED());
	/** Called when a bad connection was made with DSP::addInput. Usually called from mixer thread because that is where the connections are made. */
	public final static FMOD_SYSTEM_CALLBACKTYPE FMOD_SYSTEM_CALLBACKTYPE_BADDSPCONNECTION = new FMOD_SYSTEM_CALLBACKTYPE("FMOD_SYSTEM_CALLBACKTYPE_BADDSPCONNECTION", EnumerationJNI.get_FMOD_SYSTEM_CALLBACKTYPE_BADDSPCONNECTION());
	/** Maximum number of callback types supported. */
	public final static FMOD_SYSTEM_CALLBACKTYPE FMOD_SYSTEM_CALLBACKTYPE_MAX = new FMOD_SYSTEM_CALLBACKTYPE("FMOD_SYSTEM_CALLBACKTYPE_MAX", EnumerationJNI.get_FMOD_SYSTEM_CALLBACKTYPE_MAX());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_SYSTEM_CALLBACKTYPE FMOD_SYSTEM_CALLBACKTYPE_FORCEINT = new FMOD_SYSTEM_CALLBACKTYPE("FMOD_SYSTEM_CALLBACKTYPE_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*6);
	static
	{
		VALUES.put(new Integer(FMOD_SYSTEM_CALLBACKTYPE_DEVICELISTCHANGED.asInt()), FMOD_SYSTEM_CALLBACKTYPE_DEVICELISTCHANGED);
		VALUES.put(new Integer(FMOD_SYSTEM_CALLBACKTYPE_MEMORYALLOCATIONFAILED.asInt()), FMOD_SYSTEM_CALLBACKTYPE_MEMORYALLOCATIONFAILED);
		VALUES.put(new Integer(FMOD_SYSTEM_CALLBACKTYPE_THREADCREATED.asInt()), FMOD_SYSTEM_CALLBACKTYPE_THREADCREATED);
		VALUES.put(new Integer(FMOD_SYSTEM_CALLBACKTYPE_BADDSPCONNECTION.asInt()), FMOD_SYSTEM_CALLBACKTYPE_BADDSPCONNECTION);
		VALUES.put(new Integer(FMOD_SYSTEM_CALLBACKTYPE_MAX.asInt()), FMOD_SYSTEM_CALLBACKTYPE_MAX);
		VALUES.put(new Integer(FMOD_SYSTEM_CALLBACKTYPE_FORCEINT.asInt()), FMOD_SYSTEM_CALLBACKTYPE_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_SYSTEM_CALLBACKTYPE(String name, int nativeValue)
	{
		this.name = name;
		this.nativeValue = nativeValue;
	}

	public int asInt()
	{
		return nativeValue;
	}
	public String toString()
	{
		return name;
	}
	public boolean equals(Object object)
	{
		if(object instanceof FMOD_SYSTEM_CALLBACKTYPE)
			return asInt() == ((FMOD_SYSTEM_CALLBACKTYPE)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_SYSTEM_CALLBACKTYPE)object).asInt();
	}


	/**
	 * Retrieve a FMOD_SYSTEM_CALLBACKTYPE enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_SYSTEM_CALLBACKTYPE enum field that correspond to the integer value
	 */
	public static FMOD_SYSTEM_CALLBACKTYPE get(int nativeValue)
	{
		return (FMOD_SYSTEM_CALLBACKTYPE)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_SYSTEM_CALLBACKTYPE enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_SYSTEM_CALLBACKTYPE enum field
	 * @return the FMOD_SYSTEM_CALLBACKTYPE enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_SYSTEM_CALLBACKTYPE get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_SYSTEM_CALLBACKTYPE></code> in Java 1.5.
	 */
	public static java.util.Iterator iterator()
	{
		return new java.util.Iterator(){
			private java.util.Iterator i = VALUES.values().iterator();	//Wrapper of the HashMap iterator
			public boolean hasNext() { return i.hasNext(); }
			public Object next() { return i.next(); }
			public void remove() { throw new UnsupportedOperationException(); }
		};
	}
}