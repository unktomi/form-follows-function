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
 *     These are plugin types defined for use with the System::getNumPlugins,<BR>
 *     System::getPluginInfo and System::unloadPlugin functions.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::getNumPlugins<BR>
 *     System::getPluginInfo<BR>
 *     System::unloadPlugin<BR>
 * 
 */
public class FMOD_PLUGINTYPE implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_PLUGINTYPE FMOD_PLUGINTYPE_OUTPUT = new FMOD_PLUGINTYPE("FMOD_PLUGINTYPE_OUTPUT", EnumerationJNI.get_FMOD_PLUGINTYPE_OUTPUT());
	/** The plugin type is a file format codec.  FMOD will use these codecs to load file formats for playback. */
	public final static FMOD_PLUGINTYPE FMOD_PLUGINTYPE_CODEC = new FMOD_PLUGINTYPE("FMOD_PLUGINTYPE_CODEC", EnumerationJNI.get_FMOD_PLUGINTYPE_CODEC());
	/** The plugin type is a DSP unit.  FMOD will use these plugins as part of its DSP network to apply effects to output or generate sound in realtime. */
	public final static FMOD_PLUGINTYPE FMOD_PLUGINTYPE_DSP = new FMOD_PLUGINTYPE("FMOD_PLUGINTYPE_DSP", EnumerationJNI.get_FMOD_PLUGINTYPE_DSP());
	/** Maximum number of plugin types supported. */
	public final static FMOD_PLUGINTYPE FMOD_PLUGINTYPE_MAX = new FMOD_PLUGINTYPE("FMOD_PLUGINTYPE_MAX", EnumerationJNI.get_FMOD_PLUGINTYPE_MAX());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_PLUGINTYPE FMOD_PLUGINTYPE_FORCEINT = new FMOD_PLUGINTYPE("FMOD_PLUGINTYPE_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*5);
	static
	{
		VALUES.put(new Integer(FMOD_PLUGINTYPE_OUTPUT.asInt()), FMOD_PLUGINTYPE_OUTPUT);
		VALUES.put(new Integer(FMOD_PLUGINTYPE_CODEC.asInt()), FMOD_PLUGINTYPE_CODEC);
		VALUES.put(new Integer(FMOD_PLUGINTYPE_DSP.asInt()), FMOD_PLUGINTYPE_DSP);
		VALUES.put(new Integer(FMOD_PLUGINTYPE_MAX.asInt()), FMOD_PLUGINTYPE_MAX);
		VALUES.put(new Integer(FMOD_PLUGINTYPE_FORCEINT.asInt()), FMOD_PLUGINTYPE_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_PLUGINTYPE(String name, int nativeValue)
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
		if(object instanceof FMOD_PLUGINTYPE)
			return asInt() == ((FMOD_PLUGINTYPE)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_PLUGINTYPE)object).asInt();
	}


	/**
	 * Retrieve a FMOD_PLUGINTYPE enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_PLUGINTYPE enum field that correspond to the integer value
	 */
	public static FMOD_PLUGINTYPE get(int nativeValue)
	{
		return (FMOD_PLUGINTYPE)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_PLUGINTYPE enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_PLUGINTYPE enum field
	 * @return the FMOD_PLUGINTYPE enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_PLUGINTYPE get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_PLUGINTYPE></code> in Java 1.5.
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