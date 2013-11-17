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
 *     Parameter types for the FMOD_DSP_TYPE_ECHO filter.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Note.  Every time the delay is changed, the plugin re-allocates the echo buffer.  This means the echo will dissapear at that time while it refills its new buffer.<BR>
 *     Larger echo delays result in larger amounts of memory allocated.<BR>
 *     <BR>
 *     '<i>maxchannels</i>' also dictates the amount of memory allocated.  By default, the maxchannels value is 0.  If FMOD is set to stereo, the echo unit will allocate enough memory for 2 channels.  If it is 5.1, it will allocate enough memory for a 6 channel echo, etc.<BR>
 *     If the echo effect is only ever applied to the global mix (ie it was added with System::addDSP), then 0 is the value to set as it will be enough to handle all speaker modes.<BR>
 *     When the echo is added to a channel (ie Channel::addDSP) then the channel count that comes in could be anything from 1 to 8 possibly.  It is only in this case where you might want to increase the channel count above the output's channel count.<BR>
 *     If a channel echo is set to a lower number than the sound's channel count that is coming in, it will not echo the sound.<BR>
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
public class FMOD_DSP_ECHO implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_DSP_ECHO FMOD_DSP_ECHO_DELAY = new FMOD_DSP_ECHO("FMOD_DSP_ECHO_DELAY", EnumerationJNI.get_FMOD_DSP_ECHO_DELAY());
	/** Echo decay per delay.  0 to 1.  1.0 = No decay, 0.0 = total decay (ie simple 1 line delay).  Default = 0.5. */
	public final static FMOD_DSP_ECHO FMOD_DSP_ECHO_DECAYRATIO = new FMOD_DSP_ECHO("FMOD_DSP_ECHO_DECAYRATIO", EnumerationJNI.get_FMOD_DSP_ECHO_DECAYRATIO());
	/** Maximum channels supported.  0 to 16.  0 = same as fmod's default output polyphony, 1 = mono, 2 = stereo etc.  See remarks for more.  Default = 0.  It is suggested to leave at 0! */
	public final static FMOD_DSP_ECHO FMOD_DSP_ECHO_MAXCHANNELS = new FMOD_DSP_ECHO("FMOD_DSP_ECHO_MAXCHANNELS", EnumerationJNI.get_FMOD_DSP_ECHO_MAXCHANNELS());
	/** Volume of original signal to pass to output.  0.0 to 1.0. Default = 1.0. */
	public final static FMOD_DSP_ECHO FMOD_DSP_ECHO_DRYMIX = new FMOD_DSP_ECHO("FMOD_DSP_ECHO_DRYMIX", EnumerationJNI.get_FMOD_DSP_ECHO_DRYMIX());
	/** Volume of echo signal to pass to output.  0.0 to 1.0. Default = 1.0. */
	public final static FMOD_DSP_ECHO FMOD_DSP_ECHO_WETMIX = new FMOD_DSP_ECHO("FMOD_DSP_ECHO_WETMIX", EnumerationJNI.get_FMOD_DSP_ECHO_WETMIX());

	private final static HashMap VALUES = new HashMap(2*5);
	static
	{
		VALUES.put(new Integer(FMOD_DSP_ECHO_DELAY.asInt()), FMOD_DSP_ECHO_DELAY);
		VALUES.put(new Integer(FMOD_DSP_ECHO_DECAYRATIO.asInt()), FMOD_DSP_ECHO_DECAYRATIO);
		VALUES.put(new Integer(FMOD_DSP_ECHO_MAXCHANNELS.asInt()), FMOD_DSP_ECHO_MAXCHANNELS);
		VALUES.put(new Integer(FMOD_DSP_ECHO_DRYMIX.asInt()), FMOD_DSP_ECHO_DRYMIX);
		VALUES.put(new Integer(FMOD_DSP_ECHO_WETMIX.asInt()), FMOD_DSP_ECHO_WETMIX);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_DSP_ECHO(String name, int nativeValue)
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
		if(object instanceof FMOD_DSP_ECHO)
			return asInt() == ((FMOD_DSP_ECHO)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_DSP_ECHO)object).asInt();
	}


	/**
	 * Retrieve a FMOD_DSP_ECHO enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_DSP_ECHO enum field that correspond to the integer value
	 */
	public static FMOD_DSP_ECHO get(int nativeValue)
	{
		return (FMOD_DSP_ECHO)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_DSP_ECHO enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_DSP_ECHO enum field
	 * @return the FMOD_DSP_ECHO enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_DSP_ECHO get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_DSP_ECHO></code> in Java 1.5.
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