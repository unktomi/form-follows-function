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
 *     These flags are used with SoundGroup::setMaxAudibleBehavior to determine what happens when more sounds<BR>
 *     are played than are specified with SoundGroup::setMaxAudible.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     When using FMOD_SOUNDGROUP_BEHAVIOR_MUTE, SoundGroup::setMuteFadeSpeed can be used to stop a sudden transition.<BR>
 *     Instead, the time specified will be used to cross fade between the sounds that go silent and the ones that become audible.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     SoundGroup::setMaxAudibleBehavior<BR>
 *     SoundGroup::getMaxAudibleBehavior<BR>
 *     SoundGroup::setMaxAudible<BR>
 *     SoundGroup::getMaxAudible<BR>
 *     SoundGroup::setMuteFadeSpeed<BR>
 *     SoundGroup::getMuteFadeSpeed<BR>
 * 
 */
public class FMOD_SOUNDGROUP_BEHAVIOR implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_SOUNDGROUP_BEHAVIOR FMOD_SOUNDGROUP_BEHAVIOR_FAIL = new FMOD_SOUNDGROUP_BEHAVIOR("FMOD_SOUNDGROUP_BEHAVIOR_FAIL", EnumerationJNI.get_FMOD_SOUNDGROUP_BEHAVIOR_FAIL());
	/** Any sound played that puts the sound count over the SoundGroup::setMaxAudible setting, will be silent, then if another sound in the group stops the sound that was silent before becomes audible again. */
	public final static FMOD_SOUNDGROUP_BEHAVIOR FMOD_SOUNDGROUP_BEHAVIOR_MUTE = new FMOD_SOUNDGROUP_BEHAVIOR("FMOD_SOUNDGROUP_BEHAVIOR_MUTE", EnumerationJNI.get_FMOD_SOUNDGROUP_BEHAVIOR_MUTE());
	/** Any sound played that puts the sound count over the SoundGroup::setMaxAudible setting, will steal the quietest / least important sound playing in the group. */
	public final static FMOD_SOUNDGROUP_BEHAVIOR FMOD_SOUNDGROUP_BEHAVIOR_STEALLOWEST = new FMOD_SOUNDGROUP_BEHAVIOR("FMOD_SOUNDGROUP_BEHAVIOR_STEALLOWEST", EnumerationJNI.get_FMOD_SOUNDGROUP_BEHAVIOR_STEALLOWEST());
	/** Maximum number of open state types. */
	public final static FMOD_SOUNDGROUP_BEHAVIOR FMOD_SOUNDGROUP_BEHAVIOR_MAX = new FMOD_SOUNDGROUP_BEHAVIOR("FMOD_SOUNDGROUP_BEHAVIOR_MAX", EnumerationJNI.get_FMOD_SOUNDGROUP_BEHAVIOR_MAX());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_SOUNDGROUP_BEHAVIOR FMOD_SOUNDGROUP_BEHAVIOR_FORCEINT = new FMOD_SOUNDGROUP_BEHAVIOR("FMOD_SOUNDGROUP_BEHAVIOR_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*5);
	static
	{
		VALUES.put(new Integer(FMOD_SOUNDGROUP_BEHAVIOR_FAIL.asInt()), FMOD_SOUNDGROUP_BEHAVIOR_FAIL);
		VALUES.put(new Integer(FMOD_SOUNDGROUP_BEHAVIOR_MUTE.asInt()), FMOD_SOUNDGROUP_BEHAVIOR_MUTE);
		VALUES.put(new Integer(FMOD_SOUNDGROUP_BEHAVIOR_STEALLOWEST.asInt()), FMOD_SOUNDGROUP_BEHAVIOR_STEALLOWEST);
		VALUES.put(new Integer(FMOD_SOUNDGROUP_BEHAVIOR_MAX.asInt()), FMOD_SOUNDGROUP_BEHAVIOR_MAX);
		VALUES.put(new Integer(FMOD_SOUNDGROUP_BEHAVIOR_FORCEINT.asInt()), FMOD_SOUNDGROUP_BEHAVIOR_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_SOUNDGROUP_BEHAVIOR(String name, int nativeValue)
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
		if(object instanceof FMOD_SOUNDGROUP_BEHAVIOR)
			return asInt() == ((FMOD_SOUNDGROUP_BEHAVIOR)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_SOUNDGROUP_BEHAVIOR)object).asInt();
	}


	/**
	 * Retrieve a FMOD_SOUNDGROUP_BEHAVIOR enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_SOUNDGROUP_BEHAVIOR enum field that correspond to the integer value
	 */
	public static FMOD_SOUNDGROUP_BEHAVIOR get(int nativeValue)
	{
		return (FMOD_SOUNDGROUP_BEHAVIOR)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_SOUNDGROUP_BEHAVIOR enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_SOUNDGROUP_BEHAVIOR enum field
	 * @return the FMOD_SOUNDGROUP_BEHAVIOR enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_SOUNDGROUP_BEHAVIOR get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_SOUNDGROUP_BEHAVIOR></code> in Java 1.5.
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