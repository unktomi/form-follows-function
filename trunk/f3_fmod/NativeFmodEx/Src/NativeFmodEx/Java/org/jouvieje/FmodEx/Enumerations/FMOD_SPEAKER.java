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
 *     These are speaker types defined for use with the Channel::setSpeakerLevels command.<BR>
 *     It can also be used for speaker placement in the System::set3DSpeakerPosition command.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     If you are using FMOD_SPEAKERMODE_RAW and speaker assignments are meaningless, just cast a raw integer value to this type.<BR>
 *     For example (FMOD_SPEAKER)7 would use the 7th speaker (also the same as FMOD_SPEAKER_SIDE_RIGHT).<BR>
 *     Values higher than this can be used if an output system has more than 8 speaker types / output channels.  15 is the current maximum.<BR>
 * <BR>
 *     NOTE: On Playstation 3 in 7.1, the extra 2 speakers are not side left/side right, they are 'surround back left'/'surround back right' which<BR>
 *     locate the speakers behind the listener instead of to the sides like on PC.  FMOD_SPEAKER_SBL/FMOD_SPEAKER_SBR are provided to make it<BR>
 *     clearer what speaker is being addressed on that platform.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     FMOD_SPEAKERMODE<BR>
 *     Channel::setSpeakerLevels<BR>
 *     Channel::getSpeakerLevels<BR>
 *     System::set3DSpeakerPosition<BR>
 *     System::get3DSpeakerPosition<BR>
 * 
 */
public class FMOD_SPEAKER implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_SPEAKER FMOD_SPEAKER_FRONT_LEFT = new FMOD_SPEAKER("FMOD_SPEAKER_FRONT_LEFT", EnumerationJNI.get_FMOD_SPEAKER_FRONT_LEFT());
	/**  */
	public final static FMOD_SPEAKER FMOD_SPEAKER_FRONT_RIGHT = new FMOD_SPEAKER("FMOD_SPEAKER_FRONT_RIGHT", EnumerationJNI.get_FMOD_SPEAKER_FRONT_RIGHT());
	/**  */
	public final static FMOD_SPEAKER FMOD_SPEAKER_FRONT_CENTER = new FMOD_SPEAKER("FMOD_SPEAKER_FRONT_CENTER", EnumerationJNI.get_FMOD_SPEAKER_FRONT_CENTER());
	/**  */
	public final static FMOD_SPEAKER FMOD_SPEAKER_LOW_FREQUENCY = new FMOD_SPEAKER("FMOD_SPEAKER_LOW_FREQUENCY", EnumerationJNI.get_FMOD_SPEAKER_LOW_FREQUENCY());
	/**  */
	public final static FMOD_SPEAKER FMOD_SPEAKER_BACK_LEFT = new FMOD_SPEAKER("FMOD_SPEAKER_BACK_LEFT", EnumerationJNI.get_FMOD_SPEAKER_BACK_LEFT());
	/**  */
	public final static FMOD_SPEAKER FMOD_SPEAKER_BACK_RIGHT = new FMOD_SPEAKER("FMOD_SPEAKER_BACK_RIGHT", EnumerationJNI.get_FMOD_SPEAKER_BACK_RIGHT());
	/**  */
	public final static FMOD_SPEAKER FMOD_SPEAKER_SIDE_LEFT = new FMOD_SPEAKER("FMOD_SPEAKER_SIDE_LEFT", EnumerationJNI.get_FMOD_SPEAKER_SIDE_LEFT());
	/**  */
	public final static FMOD_SPEAKER FMOD_SPEAKER_SIDE_RIGHT = new FMOD_SPEAKER("FMOD_SPEAKER_SIDE_RIGHT", EnumerationJNI.get_FMOD_SPEAKER_SIDE_RIGHT());
	/** Maximum number of speaker types supported. */
	public final static FMOD_SPEAKER FMOD_SPEAKER_MAX = new FMOD_SPEAKER("FMOD_SPEAKER_MAX", EnumerationJNI.get_FMOD_SPEAKER_MAX());
	/** For use with FMOD_SPEAKERMODE_MONO and Channel::SetSpeakerLevels.  Mapped to same value as FMOD_SPEAKER_FRONT_LEFT. */
	public final static FMOD_SPEAKER FMOD_SPEAKER_MONO = new FMOD_SPEAKER("FMOD_SPEAKER_MONO", EnumerationJNI.get_FMOD_SPEAKER_MONO());
	/** A non speaker.  Use this to send. */
	public final static FMOD_SPEAKER FMOD_SPEAKER_NULL = new FMOD_SPEAKER("FMOD_SPEAKER_NULL", EnumerationJNI.get_FMOD_SPEAKER_NULL());
	/** For use with FMOD_SPEAKERMODE_7POINT1 on PS3 where the extra speakers are surround back inside of side speakers. */
	public final static FMOD_SPEAKER FMOD_SPEAKER_SBL = new FMOD_SPEAKER("FMOD_SPEAKER_SBL", EnumerationJNI.get_FMOD_SPEAKER_SBL());
	/** For use with FMOD_SPEAKERMODE_7POINT1 on PS3 where the extra speakers are surround back inside of side speakers. */
	public final static FMOD_SPEAKER FMOD_SPEAKER_SBR = new FMOD_SPEAKER("FMOD_SPEAKER_SBR", EnumerationJNI.get_FMOD_SPEAKER_SBR());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_SPEAKER FMOD_SPEAKER_FORCEINT = new FMOD_SPEAKER("FMOD_SPEAKER_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*14);
	static
	{
		VALUES.put(new Integer(FMOD_SPEAKER_FRONT_LEFT.asInt()), FMOD_SPEAKER_FRONT_LEFT);
		VALUES.put(new Integer(FMOD_SPEAKER_FRONT_RIGHT.asInt()), FMOD_SPEAKER_FRONT_RIGHT);
		VALUES.put(new Integer(FMOD_SPEAKER_FRONT_CENTER.asInt()), FMOD_SPEAKER_FRONT_CENTER);
		VALUES.put(new Integer(FMOD_SPEAKER_LOW_FREQUENCY.asInt()), FMOD_SPEAKER_LOW_FREQUENCY);
		VALUES.put(new Integer(FMOD_SPEAKER_BACK_LEFT.asInt()), FMOD_SPEAKER_BACK_LEFT);
		VALUES.put(new Integer(FMOD_SPEAKER_BACK_RIGHT.asInt()), FMOD_SPEAKER_BACK_RIGHT);
		VALUES.put(new Integer(FMOD_SPEAKER_SIDE_LEFT.asInt()), FMOD_SPEAKER_SIDE_LEFT);
		VALUES.put(new Integer(FMOD_SPEAKER_SIDE_RIGHT.asInt()), FMOD_SPEAKER_SIDE_RIGHT);
		VALUES.put(new Integer(FMOD_SPEAKER_MAX.asInt()), FMOD_SPEAKER_MAX);
		VALUES.put(new Integer(FMOD_SPEAKER_MONO.asInt()), FMOD_SPEAKER_MONO);
		VALUES.put(new Integer(FMOD_SPEAKER_NULL.asInt()), FMOD_SPEAKER_NULL);
		VALUES.put(new Integer(FMOD_SPEAKER_SBL.asInt()), FMOD_SPEAKER_SBL);
		VALUES.put(new Integer(FMOD_SPEAKER_SBR.asInt()), FMOD_SPEAKER_SBR);
		VALUES.put(new Integer(FMOD_SPEAKER_FORCEINT.asInt()), FMOD_SPEAKER_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_SPEAKER(String name, int nativeValue)
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
		if(object instanceof FMOD_SPEAKER)
			return asInt() == ((FMOD_SPEAKER)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_SPEAKER)object).asInt();
	}


	/**
	 * Retrieve a FMOD_SPEAKER enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_SPEAKER enum field that correspond to the integer value
	 */
	public static FMOD_SPEAKER get(int nativeValue)
	{
		return (FMOD_SPEAKER)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_SPEAKER enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_SPEAKER enum field
	 * @return the FMOD_SPEAKER enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_SPEAKER get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_SPEAKER></code> in Java 1.5.
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

	/**
	 * Retrieve a FMOD_SPEAKER enum field with a speaker ID.<BR>
	 * @param speakerID ID of a speaker.
	 * @return the FMOD_SPEAKER enum field that correspond to the speaker ID.
	 */
	public static FMOD_SPEAKER create(int speakerID)
	{
		FMOD_SPEAKER speaker = get(speakerID);
		if(speaker == null)
		{
			speaker = new FMOD_SPEAKER("FMOD_SPEAKER_CUSTOM", speakerID);
			VALUES.put(new Integer(speaker.asInt()), speaker);
		}
		return speaker;
	}
}