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
 *        <BR>
 *     These definitions can be used for creating FMOD defined special effects or DSP units.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     To get them to be active, first create the unit, then add it somewhere into the DSP network, either at the front of the network near the soundcard unit to affect the global output (by using System::getDSPHead), or on a single channel (using Channel::getDSPHead).<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::createDSPByType<BR>
 * 
 */
public class FMOD_DSP_TYPE implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_UNKNOWN = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_UNKNOWN", EnumerationJNI.get_FMOD_DSP_TYPE_UNKNOWN());
	/** This unit does nothing but take inputs and mix them together then feed the result to the soundcard unit. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_MIXER = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_MIXER", EnumerationJNI.get_FMOD_DSP_TYPE_MIXER());
	/** This unit generates sine/square/saw/triangle or noise tones. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_OSCILLATOR = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_OSCILLATOR", EnumerationJNI.get_FMOD_DSP_TYPE_OSCILLATOR());
	/** This unit filters sound using a high quality, resonant lowpass filter algorithm but consumes more CPU time. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_LOWPASS = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_LOWPASS", EnumerationJNI.get_FMOD_DSP_TYPE_LOWPASS());
	/** This unit filters sound using a resonant lowpass filter algorithm that is used in Impulse Tracker, but with limited cutoff range (0 to 8060hz). */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_ITLOWPASS = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_ITLOWPASS", EnumerationJNI.get_FMOD_DSP_TYPE_ITLOWPASS());
	/** This unit filters sound using a resonant highpass filter algorithm. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_HIGHPASS = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_HIGHPASS", EnumerationJNI.get_FMOD_DSP_TYPE_HIGHPASS());
	/** This unit produces an echo on the sound and fades out at the desired rate. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_ECHO = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_ECHO", EnumerationJNI.get_FMOD_DSP_TYPE_ECHO());
	/** This unit produces a flange effect on the sound. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_FLANGE = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_FLANGE", EnumerationJNI.get_FMOD_DSP_TYPE_FLANGE());
	/** This unit distorts the sound. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_DISTORTION = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_DISTORTION", EnumerationJNI.get_FMOD_DSP_TYPE_DISTORTION());
	/** This unit normalizes or amplifies the sound to a certain level. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_NORMALIZE = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_NORMALIZE", EnumerationJNI.get_FMOD_DSP_TYPE_NORMALIZE());
	/** This unit attenuates or amplifies a selected frequency range. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_PARAMEQ = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_PARAMEQ", EnumerationJNI.get_FMOD_DSP_TYPE_PARAMEQ());
	/** This unit bends the pitch of a sound without changing the speed of playback. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_PITCHSHIFT = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_PITCHSHIFT", EnumerationJNI.get_FMOD_DSP_TYPE_PITCHSHIFT());
	/** This unit produces a chorus effect on the sound. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_CHORUS = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_CHORUS", EnumerationJNI.get_FMOD_DSP_TYPE_CHORUS());
	/** This unit produces a reverb effect on the sound. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_REVERB = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_REVERB", EnumerationJNI.get_FMOD_DSP_TYPE_REVERB());
	/** This unit allows the use of Steinberg VST plugins */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_VSTPLUGIN = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_VSTPLUGIN", EnumerationJNI.get_FMOD_DSP_TYPE_VSTPLUGIN());
	/** This unit allows the use of Nullsoft Winamp plugins */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_WINAMPPLUGIN = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_WINAMPPLUGIN", EnumerationJNI.get_FMOD_DSP_TYPE_WINAMPPLUGIN());
	/** This unit produces an echo on the sound and fades out at the desired rate as is used in Impulse Tracker. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_ITECHO = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_ITECHO", EnumerationJNI.get_FMOD_DSP_TYPE_ITECHO());
	/** This unit implements dynamic compression (linked multichannel, wideband) */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_COMPRESSOR = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_COMPRESSOR", EnumerationJNI.get_FMOD_DSP_TYPE_COMPRESSOR());
	/** This unit implements SFX reverb */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_SFXREVERB = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_SFXREVERB", EnumerationJNI.get_FMOD_DSP_TYPE_SFXREVERB());
	/** This unit filters sound using a simple lowpass with no resonance, but has flexible cutoff and is fast. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_LOWPASS_SIMPLE = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_LOWPASS_SIMPLE", EnumerationJNI.get_FMOD_DSP_TYPE_LOWPASS_SIMPLE());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_DSP_TYPE FMOD_DSP_TYPE_FORCEINT = new FMOD_DSP_TYPE("FMOD_DSP_TYPE_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*21);
	static
	{
		VALUES.put(new Integer(FMOD_DSP_TYPE_UNKNOWN.asInt()), FMOD_DSP_TYPE_UNKNOWN);
		VALUES.put(new Integer(FMOD_DSP_TYPE_MIXER.asInt()), FMOD_DSP_TYPE_MIXER);
		VALUES.put(new Integer(FMOD_DSP_TYPE_OSCILLATOR.asInt()), FMOD_DSP_TYPE_OSCILLATOR);
		VALUES.put(new Integer(FMOD_DSP_TYPE_LOWPASS.asInt()), FMOD_DSP_TYPE_LOWPASS);
		VALUES.put(new Integer(FMOD_DSP_TYPE_ITLOWPASS.asInt()), FMOD_DSP_TYPE_ITLOWPASS);
		VALUES.put(new Integer(FMOD_DSP_TYPE_HIGHPASS.asInt()), FMOD_DSP_TYPE_HIGHPASS);
		VALUES.put(new Integer(FMOD_DSP_TYPE_ECHO.asInt()), FMOD_DSP_TYPE_ECHO);
		VALUES.put(new Integer(FMOD_DSP_TYPE_FLANGE.asInt()), FMOD_DSP_TYPE_FLANGE);
		VALUES.put(new Integer(FMOD_DSP_TYPE_DISTORTION.asInt()), FMOD_DSP_TYPE_DISTORTION);
		VALUES.put(new Integer(FMOD_DSP_TYPE_NORMALIZE.asInt()), FMOD_DSP_TYPE_NORMALIZE);
		VALUES.put(new Integer(FMOD_DSP_TYPE_PARAMEQ.asInt()), FMOD_DSP_TYPE_PARAMEQ);
		VALUES.put(new Integer(FMOD_DSP_TYPE_PITCHSHIFT.asInt()), FMOD_DSP_TYPE_PITCHSHIFT);
		VALUES.put(new Integer(FMOD_DSP_TYPE_CHORUS.asInt()), FMOD_DSP_TYPE_CHORUS);
		VALUES.put(new Integer(FMOD_DSP_TYPE_REVERB.asInt()), FMOD_DSP_TYPE_REVERB);
		VALUES.put(new Integer(FMOD_DSP_TYPE_VSTPLUGIN.asInt()), FMOD_DSP_TYPE_VSTPLUGIN);
		VALUES.put(new Integer(FMOD_DSP_TYPE_WINAMPPLUGIN.asInt()), FMOD_DSP_TYPE_WINAMPPLUGIN);
		VALUES.put(new Integer(FMOD_DSP_TYPE_ITECHO.asInt()), FMOD_DSP_TYPE_ITECHO);
		VALUES.put(new Integer(FMOD_DSP_TYPE_COMPRESSOR.asInt()), FMOD_DSP_TYPE_COMPRESSOR);
		VALUES.put(new Integer(FMOD_DSP_TYPE_SFXREVERB.asInt()), FMOD_DSP_TYPE_SFXREVERB);
		VALUES.put(new Integer(FMOD_DSP_TYPE_LOWPASS_SIMPLE.asInt()), FMOD_DSP_TYPE_LOWPASS_SIMPLE);
		VALUES.put(new Integer(FMOD_DSP_TYPE_FORCEINT.asInt()), FMOD_DSP_TYPE_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_DSP_TYPE(String name, int nativeValue)
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
		if(object instanceof FMOD_DSP_TYPE)
			return asInt() == ((FMOD_DSP_TYPE)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_DSP_TYPE)object).asInt();
	}


	/**
	 * Retrieve a FMOD_DSP_TYPE enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_DSP_TYPE enum field that correspond to the integer value
	 */
	public static FMOD_DSP_TYPE get(int nativeValue)
	{
		return (FMOD_DSP_TYPE)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_DSP_TYPE enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_DSP_TYPE enum field
	 * @return the FMOD_DSP_TYPE enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_DSP_TYPE get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_DSP_TYPE></code> in Java 1.5.
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