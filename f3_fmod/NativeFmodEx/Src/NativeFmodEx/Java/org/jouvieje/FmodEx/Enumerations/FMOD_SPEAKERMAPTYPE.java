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
 *     When creating a multichannel sound, FMOD will pan them to their default speaker locations, for example a 6 channel sound will default to one channel per 5.1 output speaker.<BR>
 *     Another example is a stereo sound.  It will default to left = front left, right = front right.<BR>
 * <BR>
 *     This is for sounds that are not 'default'.  For example you might have a sound that is 6 channels but actually made up of 3 stereo pairs, that should all be located in front left, front right only.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     For full flexibility of speaker assignments, use Channel::setSpeakerLevels.  This functionality is cheaper, uses less memory and easier to use.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     FMOD_CREATESOUNDEXINFO<BR>
 *     Channel::setSpeakerLevels<BR>
 * 
 */
public class FMOD_SPEAKERMAPTYPE implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_SPEAKERMAPTYPE FMOD_SPEAKERMAPTYPE_DEFAULT = new FMOD_SPEAKERMAPTYPE("FMOD_SPEAKERMAPTYPE_DEFAULT", EnumerationJNI.get_FMOD_SPEAKERMAPTYPE_DEFAULT());
	/** This means the sound is made up of all mono sounds.  All voices will be panned to the front center by default in this case. */
	public final static FMOD_SPEAKERMAPTYPE FMOD_SPEAKERMAPTYPE_ALLMONO = new FMOD_SPEAKERMAPTYPE("FMOD_SPEAKERMAPTYPE_ALLMONO", EnumerationJNI.get_FMOD_SPEAKERMAPTYPE_ALLMONO());
	/** This means the sound is made up of all stereo sounds.  All voices will be panned to front left and front right alternating every second channel. */
	public final static FMOD_SPEAKERMAPTYPE FMOD_SPEAKERMAPTYPE_ALLSTEREO = new FMOD_SPEAKERMAPTYPE("FMOD_SPEAKERMAPTYPE_ALLSTEREO", EnumerationJNI.get_FMOD_SPEAKERMAPTYPE_ALLSTEREO());

	private final static HashMap VALUES = new HashMap(2*3);
	static
	{
		VALUES.put(new Integer(FMOD_SPEAKERMAPTYPE_DEFAULT.asInt()), FMOD_SPEAKERMAPTYPE_DEFAULT);
		VALUES.put(new Integer(FMOD_SPEAKERMAPTYPE_ALLMONO.asInt()), FMOD_SPEAKERMAPTYPE_ALLMONO);
		VALUES.put(new Integer(FMOD_SPEAKERMAPTYPE_ALLSTEREO.asInt()), FMOD_SPEAKERMAPTYPE_ALLSTEREO);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_SPEAKERMAPTYPE(String name, int nativeValue)
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
		if(object instanceof FMOD_SPEAKERMAPTYPE)
			return asInt() == ((FMOD_SPEAKERMAPTYPE)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_SPEAKERMAPTYPE)object).asInt();
	}


	/**
	 * Retrieve a FMOD_SPEAKERMAPTYPE enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_SPEAKERMAPTYPE enum field that correspond to the integer value
	 */
	public static FMOD_SPEAKERMAPTYPE get(int nativeValue)
	{
		return (FMOD_SPEAKERMAPTYPE)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_SPEAKERMAPTYPE enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_SPEAKERMAPTYPE enum field
	 * @return the FMOD_SPEAKERMAPTYPE enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_SPEAKERMAPTYPE get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_SPEAKERMAPTYPE></code> in Java 1.5.
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