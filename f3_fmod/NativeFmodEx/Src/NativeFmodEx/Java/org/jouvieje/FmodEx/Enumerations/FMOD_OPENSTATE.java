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
 *     These values describe what state a sound is in after FMOD_NONBLOCKING has been used to open it.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     Sound::getOpenState<BR>
 *     FMOD_MODE<BR>
 * 
 */
public class FMOD_OPENSTATE implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_OPENSTATE FMOD_OPENSTATE_READY = new FMOD_OPENSTATE("FMOD_OPENSTATE_READY", 0);
	/** Initial load in progress. */
	public final static FMOD_OPENSTATE FMOD_OPENSTATE_LOADING = new FMOD_OPENSTATE("FMOD_OPENSTATE_LOADING", EnumerationJNI.get_FMOD_OPENSTATE_LOADING());
	/** Failed to open - file not found, out of memory etc.  See return value of Sound::getOpenState for what happened. */
	public final static FMOD_OPENSTATE FMOD_OPENSTATE_ERROR = new FMOD_OPENSTATE("FMOD_OPENSTATE_ERROR", EnumerationJNI.get_FMOD_OPENSTATE_ERROR());
	/** Connecting to remote host (internet sounds only). */
	public final static FMOD_OPENSTATE FMOD_OPENSTATE_CONNECTING = new FMOD_OPENSTATE("FMOD_OPENSTATE_CONNECTING", EnumerationJNI.get_FMOD_OPENSTATE_CONNECTING());
	/** Buffering data. */
	public final static FMOD_OPENSTATE FMOD_OPENSTATE_BUFFERING = new FMOD_OPENSTATE("FMOD_OPENSTATE_BUFFERING", EnumerationJNI.get_FMOD_OPENSTATE_BUFFERING());
	/** Seeking to subsound and re-flushing stream buffer. */
	public final static FMOD_OPENSTATE FMOD_OPENSTATE_SEEKING = new FMOD_OPENSTATE("FMOD_OPENSTATE_SEEKING", EnumerationJNI.get_FMOD_OPENSTATE_SEEKING());
	/** Ready and playing, but not possible to release at this time without stalling the main thread. */
	public final static FMOD_OPENSTATE FMOD_OPENSTATE_STREAMING = new FMOD_OPENSTATE("FMOD_OPENSTATE_STREAMING", EnumerationJNI.get_FMOD_OPENSTATE_STREAMING());
	/** Maximum number of open state types. */
	public final static FMOD_OPENSTATE FMOD_OPENSTATE_MAX = new FMOD_OPENSTATE("FMOD_OPENSTATE_MAX", EnumerationJNI.get_FMOD_OPENSTATE_MAX());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_OPENSTATE FMOD_OPENSTATE_FORCEINT = new FMOD_OPENSTATE("FMOD_OPENSTATE_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*9);
	static
	{
		VALUES.put(new Integer(FMOD_OPENSTATE_READY.asInt()), FMOD_OPENSTATE_READY);
		VALUES.put(new Integer(FMOD_OPENSTATE_LOADING.asInt()), FMOD_OPENSTATE_LOADING);
		VALUES.put(new Integer(FMOD_OPENSTATE_ERROR.asInt()), FMOD_OPENSTATE_ERROR);
		VALUES.put(new Integer(FMOD_OPENSTATE_CONNECTING.asInt()), FMOD_OPENSTATE_CONNECTING);
		VALUES.put(new Integer(FMOD_OPENSTATE_BUFFERING.asInt()), FMOD_OPENSTATE_BUFFERING);
		VALUES.put(new Integer(FMOD_OPENSTATE_SEEKING.asInt()), FMOD_OPENSTATE_SEEKING);
		VALUES.put(new Integer(FMOD_OPENSTATE_STREAMING.asInt()), FMOD_OPENSTATE_STREAMING);
		VALUES.put(new Integer(FMOD_OPENSTATE_MAX.asInt()), FMOD_OPENSTATE_MAX);
		VALUES.put(new Integer(FMOD_OPENSTATE_FORCEINT.asInt()), FMOD_OPENSTATE_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_OPENSTATE(String name, int nativeValue)
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
		if(object instanceof FMOD_OPENSTATE)
			return asInt() == ((FMOD_OPENSTATE)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_OPENSTATE)object).asInt();
	}


	/**
	 * Retrieve a FMOD_OPENSTATE enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_OPENSTATE enum field that correspond to the integer value
	 */
	public static FMOD_OPENSTATE get(int nativeValue)
	{
		return (FMOD_OPENSTATE)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_OPENSTATE enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_OPENSTATE enum field
	 * @return the FMOD_OPENSTATE enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_OPENSTATE get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_OPENSTATE></code> in Java 1.5.
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