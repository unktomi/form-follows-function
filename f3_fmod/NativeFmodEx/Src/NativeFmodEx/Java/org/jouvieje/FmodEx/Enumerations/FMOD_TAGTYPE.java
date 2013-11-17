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
 *     List of tag types that could be stored within a sound.  These include id3 tags, metadata from netstreams and vorbis/asf data.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     Sound::getTag<BR>
 * 
 */
public class FMOD_TAGTYPE implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_UNKNOWN = new FMOD_TAGTYPE("FMOD_TAGTYPE_UNKNOWN", 0);
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_ID3V1 = new FMOD_TAGTYPE("FMOD_TAGTYPE_ID3V1", EnumerationJNI.get_FMOD_TAGTYPE_ID3V1());
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_ID3V2 = new FMOD_TAGTYPE("FMOD_TAGTYPE_ID3V2", EnumerationJNI.get_FMOD_TAGTYPE_ID3V2());
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_VORBISCOMMENT = new FMOD_TAGTYPE("FMOD_TAGTYPE_VORBISCOMMENT", EnumerationJNI.get_FMOD_TAGTYPE_VORBISCOMMENT());
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_SHOUTCAST = new FMOD_TAGTYPE("FMOD_TAGTYPE_SHOUTCAST", EnumerationJNI.get_FMOD_TAGTYPE_SHOUTCAST());
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_ICECAST = new FMOD_TAGTYPE("FMOD_TAGTYPE_ICECAST", EnumerationJNI.get_FMOD_TAGTYPE_ICECAST());
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_ASF = new FMOD_TAGTYPE("FMOD_TAGTYPE_ASF", EnumerationJNI.get_FMOD_TAGTYPE_ASF());
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_MIDI = new FMOD_TAGTYPE("FMOD_TAGTYPE_MIDI", EnumerationJNI.get_FMOD_TAGTYPE_MIDI());
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_PLAYLIST = new FMOD_TAGTYPE("FMOD_TAGTYPE_PLAYLIST", EnumerationJNI.get_FMOD_TAGTYPE_PLAYLIST());
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_FMOD = new FMOD_TAGTYPE("FMOD_TAGTYPE_FMOD", EnumerationJNI.get_FMOD_TAGTYPE_FMOD());
	/**  */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_USER = new FMOD_TAGTYPE("FMOD_TAGTYPE_USER", EnumerationJNI.get_FMOD_TAGTYPE_USER());
	/** Maximum number of tag types supported. */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_MAX = new FMOD_TAGTYPE("FMOD_TAGTYPE_MAX", EnumerationJNI.get_FMOD_TAGTYPE_MAX());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_TAGTYPE FMOD_TAGTYPE_FORCEINT = new FMOD_TAGTYPE("FMOD_TAGTYPE_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*13);
	static
	{
		VALUES.put(new Integer(FMOD_TAGTYPE_UNKNOWN.asInt()), FMOD_TAGTYPE_UNKNOWN);
		VALUES.put(new Integer(FMOD_TAGTYPE_ID3V1.asInt()), FMOD_TAGTYPE_ID3V1);
		VALUES.put(new Integer(FMOD_TAGTYPE_ID3V2.asInt()), FMOD_TAGTYPE_ID3V2);
		VALUES.put(new Integer(FMOD_TAGTYPE_VORBISCOMMENT.asInt()), FMOD_TAGTYPE_VORBISCOMMENT);
		VALUES.put(new Integer(FMOD_TAGTYPE_SHOUTCAST.asInt()), FMOD_TAGTYPE_SHOUTCAST);
		VALUES.put(new Integer(FMOD_TAGTYPE_ICECAST.asInt()), FMOD_TAGTYPE_ICECAST);
		VALUES.put(new Integer(FMOD_TAGTYPE_ASF.asInt()), FMOD_TAGTYPE_ASF);
		VALUES.put(new Integer(FMOD_TAGTYPE_MIDI.asInt()), FMOD_TAGTYPE_MIDI);
		VALUES.put(new Integer(FMOD_TAGTYPE_PLAYLIST.asInt()), FMOD_TAGTYPE_PLAYLIST);
		VALUES.put(new Integer(FMOD_TAGTYPE_FMOD.asInt()), FMOD_TAGTYPE_FMOD);
		VALUES.put(new Integer(FMOD_TAGTYPE_USER.asInt()), FMOD_TAGTYPE_USER);
		VALUES.put(new Integer(FMOD_TAGTYPE_MAX.asInt()), FMOD_TAGTYPE_MAX);
		VALUES.put(new Integer(FMOD_TAGTYPE_FORCEINT.asInt()), FMOD_TAGTYPE_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_TAGTYPE(String name, int nativeValue)
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
		if(object instanceof FMOD_TAGTYPE)
			return asInt() == ((FMOD_TAGTYPE)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_TAGTYPE)object).asInt();
	}


	/**
	 * Retrieve a FMOD_TAGTYPE enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_TAGTYPE enum field that correspond to the integer value
	 */
	public static FMOD_TAGTYPE get(int nativeValue)
	{
		return (FMOD_TAGTYPE)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_TAGTYPE enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_TAGTYPE enum field
	 * @return the FMOD_TAGTYPE enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_TAGTYPE get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_TAGTYPE></code> in Java 1.5.
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