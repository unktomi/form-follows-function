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
 *     These callback types are used with Channel::setCallback.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Each callback has commanddata parameters passed as int unique to the type of callback.<BR>
 *     See reference to FMOD_CHANNEL_CALLBACK to determine what they might mean for each type of callback.<BR>
 * <BR>
 *     <b>Note!</b>  Currently the user must call System::update for these callbacks to trigger!<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     Channel::setCallback<BR>
 *     FMOD_CHANNEL_CALLBACK<BR>
 *     System::update<BR>
 * 
 */
public class FMOD_CHANNEL_CALLBACKTYPE implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_CHANNEL_CALLBACKTYPE FMOD_CHANNEL_CALLBACKTYPE_END = new FMOD_CHANNEL_CALLBACKTYPE("FMOD_CHANNEL_CALLBACKTYPE_END", EnumerationJNI.get_FMOD_CHANNEL_CALLBACKTYPE_END());
	/** Called when a voice is swapped out or swapped in. */
	public final static FMOD_CHANNEL_CALLBACKTYPE FMOD_CHANNEL_CALLBACKTYPE_VIRTUALVOICE = new FMOD_CHANNEL_CALLBACKTYPE("FMOD_CHANNEL_CALLBACKTYPE_VIRTUALVOICE", EnumerationJNI.get_FMOD_CHANNEL_CALLBACKTYPE_VIRTUALVOICE());
	/** Called when a syncpoint is encountered.  Can be from wav file markers. */
	public final static FMOD_CHANNEL_CALLBACKTYPE FMOD_CHANNEL_CALLBACKTYPE_SYNCPOINT = new FMOD_CHANNEL_CALLBACKTYPE("FMOD_CHANNEL_CALLBACKTYPE_SYNCPOINT", EnumerationJNI.get_FMOD_CHANNEL_CALLBACKTYPE_SYNCPOINT());
	/** Maximum number of callback types supported. */
	public final static FMOD_CHANNEL_CALLBACKTYPE FMOD_CHANNEL_CALLBACKTYPE_MAX = new FMOD_CHANNEL_CALLBACKTYPE("FMOD_CHANNEL_CALLBACKTYPE_MAX", EnumerationJNI.get_FMOD_CHANNEL_CALLBACKTYPE_MAX());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_CHANNEL_CALLBACKTYPE FMOD_CHANNEL_CALLBACKTYPE_FORCEINT = new FMOD_CHANNEL_CALLBACKTYPE("FMOD_CHANNEL_CALLBACKTYPE_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*5);
	static
	{
		VALUES.put(new Integer(FMOD_CHANNEL_CALLBACKTYPE_END.asInt()), FMOD_CHANNEL_CALLBACKTYPE_END);
		VALUES.put(new Integer(FMOD_CHANNEL_CALLBACKTYPE_VIRTUALVOICE.asInt()), FMOD_CHANNEL_CALLBACKTYPE_VIRTUALVOICE);
		VALUES.put(new Integer(FMOD_CHANNEL_CALLBACKTYPE_SYNCPOINT.asInt()), FMOD_CHANNEL_CALLBACKTYPE_SYNCPOINT);
		VALUES.put(new Integer(FMOD_CHANNEL_CALLBACKTYPE_MAX.asInt()), FMOD_CHANNEL_CALLBACKTYPE_MAX);
		VALUES.put(new Integer(FMOD_CHANNEL_CALLBACKTYPE_FORCEINT.asInt()), FMOD_CHANNEL_CALLBACKTYPE_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_CHANNEL_CALLBACKTYPE(String name, int nativeValue)
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
		if(object instanceof FMOD_CHANNEL_CALLBACKTYPE)
			return asInt() == ((FMOD_CHANNEL_CALLBACKTYPE)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_CHANNEL_CALLBACKTYPE)object).asInt();
	}


	/**
	 * Retrieve a FMOD_CHANNEL_CALLBACKTYPE enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_CHANNEL_CALLBACKTYPE enum field that correspond to the integer value
	 */
	public static FMOD_CHANNEL_CALLBACKTYPE get(int nativeValue)
	{
		return (FMOD_CHANNEL_CALLBACKTYPE)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_CHANNEL_CALLBACKTYPE enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_CHANNEL_CALLBACKTYPE enum field
	 * @return the FMOD_CHANNEL_CALLBACKTYPE enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_CHANNEL_CALLBACKTYPE get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_CHANNEL_CALLBACKTYPE></code> in Java 1.5.
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