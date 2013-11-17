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
 *   <BR>
 *        <BR>
 *     Parameter types for the FMOD_DSP_TYPE_REVERB filter.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Based on freeverb by Jezar at Dreampoint - http://www.dreampoint.co.uk.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR>      <BR>
 *     DSP::setParameter<BR>
 *     DSP::getParameter<BR>
 *     FMOD_DSP_TYPE<BR>
 * 
 */
public class FMOD_DSP_REVERB implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_DSP_REVERB FMOD_DSP_REVERB_ROOMSIZE = new FMOD_DSP_REVERB("FMOD_DSP_REVERB_ROOMSIZE", EnumerationJNI.get_FMOD_DSP_REVERB_ROOMSIZE());
	/** Damp.     0.0 to 1.0.  Default = 0.5 */
	public final static FMOD_DSP_REVERB FMOD_DSP_REVERB_DAMP = new FMOD_DSP_REVERB("FMOD_DSP_REVERB_DAMP", EnumerationJNI.get_FMOD_DSP_REVERB_DAMP());
	/** Wet mix.  0.0 to 1.0.  Default = 0.33 */
	public final static FMOD_DSP_REVERB FMOD_DSP_REVERB_WETMIX = new FMOD_DSP_REVERB("FMOD_DSP_REVERB_WETMIX", EnumerationJNI.get_FMOD_DSP_REVERB_WETMIX());
	/** Dry mix.  0.0 to 1.0.  Default = 0.66 */
	public final static FMOD_DSP_REVERB FMOD_DSP_REVERB_DRYMIX = new FMOD_DSP_REVERB("FMOD_DSP_REVERB_DRYMIX", EnumerationJNI.get_FMOD_DSP_REVERB_DRYMIX());
	/** Stereo width. 0.0 to 1.0.  Default = 1.0 */
	public final static FMOD_DSP_REVERB FMOD_DSP_REVERB_WIDTH = new FMOD_DSP_REVERB("FMOD_DSP_REVERB_WIDTH", EnumerationJNI.get_FMOD_DSP_REVERB_WIDTH());
	/** Mode.     0 (normal), 1 (freeze).  Default = 0 */
	public final static FMOD_DSP_REVERB FMOD_DSP_REVERB_MODE = new FMOD_DSP_REVERB("FMOD_DSP_REVERB_MODE", EnumerationJNI.get_FMOD_DSP_REVERB_MODE());

	private final static HashMap VALUES = new HashMap(2*6);
	static
	{
		VALUES.put(new Integer(FMOD_DSP_REVERB_ROOMSIZE.asInt()), FMOD_DSP_REVERB_ROOMSIZE);
		VALUES.put(new Integer(FMOD_DSP_REVERB_DAMP.asInt()), FMOD_DSP_REVERB_DAMP);
		VALUES.put(new Integer(FMOD_DSP_REVERB_WETMIX.asInt()), FMOD_DSP_REVERB_WETMIX);
		VALUES.put(new Integer(FMOD_DSP_REVERB_DRYMIX.asInt()), FMOD_DSP_REVERB_DRYMIX);
		VALUES.put(new Integer(FMOD_DSP_REVERB_WIDTH.asInt()), FMOD_DSP_REVERB_WIDTH);
		VALUES.put(new Integer(FMOD_DSP_REVERB_MODE.asInt()), FMOD_DSP_REVERB_MODE);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_DSP_REVERB(String name, int nativeValue)
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
		if(object instanceof FMOD_DSP_REVERB)
			return asInt() == ((FMOD_DSP_REVERB)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_DSP_REVERB)object).asInt();
	}


	/**
	 * Retrieve a FMOD_DSP_REVERB enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_DSP_REVERB enum field that correspond to the integer value
	 */
	public static FMOD_DSP_REVERB get(int nativeValue)
	{
		return (FMOD_DSP_REVERB)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_DSP_REVERB enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_DSP_REVERB enum field
	 * @return the FMOD_DSP_REVERB enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_DSP_REVERB get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_DSP_REVERB></code> in Java 1.5.
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