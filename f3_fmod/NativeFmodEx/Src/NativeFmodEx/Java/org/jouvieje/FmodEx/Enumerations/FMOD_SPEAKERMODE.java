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
 *     These are speaker types defined for use with the System::setSpeakerMode or System::getSpeakerMode command.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     These are important notes on speaker modes in regards to sounds created with FMOD_SOFTWARE.<BR>
 *     Note below the phrase 'sound channels' is used.  These are the subchannels inside a sound, they are not related and<BR>
 *     have nothing to do with the FMOD class "Channel".<BR>
 *     For example a mono sound has 1 sound channel, a stereo sound has 2 sound channels, and an AC3 or 6 channel wav file have 6 "sound channels".<BR>
 * <BR>
 *     FMOD_SPEAKERMODE_RAW<BR>
 *     ---------------------<BR>
 *     This mode is for output devices that are not specifically mono/stereo/quad/surround/5.1 or 7.1, but are multichannel.<BR>
 *     Use System::setSoftwareFormat to specify the number of speakers you want to address, otherwise it will default to 2 (stereo).<BR>
 *     Sound channels map to speakers sequentially, so a mono sound maps to output speaker 0, stereo sound maps to output speaker 0 & 1.<BR>
 *     The user assumes knowledge of the speaker order.  FMOD_SPEAKER enumerations may not apply, so raw channel indices should be used.<BR>
 *     Multichannel sounds map input channels to output channels 1:1.<BR>
 *     Channel::setPan and Channel::setSpeakerMix do not work.<BR>
 *     Speaker levels must be manually set with Channel::setSpeakerLevels.<BR>
 * <BR>
 *     FMOD_SPEAKERMODE_MONO<BR>
 *     ---------------------<BR>
 *     This mode is for a 1 speaker arrangement.<BR>
 *     Panning does not work in this speaker mode.<BR>
 *     Mono, stereo and multichannel sounds have each sound channel played on the one speaker unity.<BR>
 *     Mix behavior for multichannel sounds can be set with Channel::setSpeakerLevels.<BR>
 *     Channel::setSpeakerMix does not work.<BR>
 * <BR>
 *     FMOD_SPEAKERMODE_STEREO<BR>
 *     -----------------------<BR>
 *     This mode is for 2 speaker arrangements that have a left and right speaker.<BR>
 *     - Mono sounds default to an even distribution between left and right.  They can be panned with Channel::setPan.<BR>
 *     - Stereo sounds default to the middle, or full left in the left speaker and full right in the right speaker.<BR>
 *     - They can be cross faded with Channel::setPan.<BR>
 *     - Multichannel sounds have each sound channel played on each speaker at unity.<BR>
 *     - Mix behavior for multichannel sounds can be set with Channel::setSpeakerLevels.<BR>
 *     - Channel::setSpeakerMix works but only front left and right parameters are used, the rest are ignored.<BR>
 * <BR>
 *     FMOD_SPEAKERMODE_QUAD<BR>
 *     ------------------------<BR>
 *     This mode is for 4 speaker arrangements that have a front left, front right, rear left and a rear right speaker.<BR>
 *     - Mono sounds default to an even distribution between front left and front right.  They can be panned with Channel::setPan.<BR>
 *     - Stereo sounds default to the left sound channel played on the front left, and the right sound channel played on the front right.<BR>
 *     - They can be cross faded with Channel::setPan.<BR>
 *     - Multichannel sounds default to all of their sound channels being played on each speaker in order of input.<BR>
 *     - Mix behavior for multichannel sounds can be set with Channel::setSpeakerLevels.<BR>
 *     - Channel::setSpeakerMix works but side left, side right, center and lfe are ignored.<BR>
 * <BR>
 *     FMOD_SPEAKERMODE_SURROUND<BR>
 *     ------------------------<BR>
 *     This mode is for 5 speaker arrangements that have a left/right/center/rear left/rear right.<BR>
 *     - Mono sounds default to the center speaker.  They can be panned with Channel::setPan.<BR>
 *     - Stereo sounds default to the left sound channel played on the front left, and the right sound channel played on the front right.<BR>
 *     - They can be cross faded with Channel::setPan.<BR>
 *     - Multichannel sounds default to all of their sound channels being played on each speaker in order of input.<BR>
 *     - Mix behavior for multichannel sounds can be set with Channel::setSpeakerLevels.<BR>
 *     - Channel::setSpeakerMix works but side left / side right are ignored.<BR>
 * <BR>
 *     FMOD_SPEAKERMODE_5POINT1<BR>
 *     ------------------------<BR>
 *     This mode is for 5.1 speaker arrangements that have a left/right/center/rear left/rear right and a subwoofer speaker.<BR>
 *     - Mono sounds default to the center speaker.  They can be panned with Channel::setPan.<BR>
 *     - Stereo sounds default to the left sound channel played on the front left, and the right sound channel played on the front right.<BR>
 *     - They can be cross faded with Channel::setPan.<BR>
 *     - Multichannel sounds default to all of their sound channels being played on each speaker in order of input.<BR>
 *     - Mix behavior for multichannel sounds can be set with Channel::setSpeakerLevels.<BR>
 *     - Channel::setSpeakerMix works but side left / side right are ignored.<BR>
 * <BR>
 *     FMOD_SPEAKERMODE_7POINT1<BR>
 *     ------------------------<BR>
 *     This mode is for 7.1 speaker arrangements that have a left/right/center/rear left/rear right/side left/side right<BR>
 *     and a subwoofer speaker.<BR>
 *     - Mono sounds default to the center speaker.  They can be panned with Channel::setPan.<BR>
 *     - Stereo sounds default to the left sound channel played on the front left, and the right sound channel played on the front right.<BR>
 *     - They can be cross faded with Channel::setPan.<BR>
 *     - Multichannel sounds default to all of their sound channels being played on each speaker in order of input.<BR>
 *     - Mix behavior for multichannel sounds can be set with Channel::setSpeakerLevels.<BR>
 *     - Channel::setSpeakerMix works and every parameter is used to set the balance of a sound in any speaker.<BR>
 * <BR>
 *     FMOD_SPEAKERMODE_PROLOGIC<BR>
 *     ------------------------------------------------------<BR>
 *     This mode is for mono, stereo, 5.1 and 7.1 speaker arrangements, as it is backwards and forwards compatible with stereo,<BR>
 *     but to get a surround effect a Dolby Prologic or Prologic 2 hardware decoder / amplifier is needed.<BR>
 *     Pan behavior is the same as FMOD_SPEAKERMODE_5POINT1.<BR>
 * <BR>
 *     If this function is called the numoutputchannels setting in System::setSoftwareFormat is overwritten.<BR>
 * <BR>
 *     For 3D sounds, panning is determined at runtime by the 3D subsystem based on the speaker mode to determine which speaker the<BR>
 *     sound should be placed in.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::setSpeakerMode<BR>
 *     System::getSpeakerMode<BR>
 *     System::getDriverCaps<BR>
 *     System::setSoftwareFormat<BR>
 *     Channel::setSpeakerLevels<BR>
 * 
 */
public class FMOD_SPEAKERMODE implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_RAW = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_RAW", EnumerationJNI.get_FMOD_SPEAKERMODE_RAW());
	/** The speakers are monaural. */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_MONO = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_MONO", EnumerationJNI.get_FMOD_SPEAKERMODE_MONO());
	/** The speakers are stereo (DEFAULT). */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_STEREO = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_STEREO", EnumerationJNI.get_FMOD_SPEAKERMODE_STEREO());
	/** 4 speaker setup.  This includes front left, front right, rear left, rear right. */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_QUAD = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_QUAD", EnumerationJNI.get_FMOD_SPEAKERMODE_QUAD());
	/** 5 speaker setup.  This includes front left, front right, center, rear left, rear right. */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_SURROUND = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_SURROUND", EnumerationJNI.get_FMOD_SPEAKERMODE_SURROUND());
	/** 5.1 speaker setup.  This includes front left, front right, center, rear left, rear right and a subwoofer. */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_5POINT1 = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_5POINT1", EnumerationJNI.get_FMOD_SPEAKERMODE_5POINT1());
	/** 7.1 speaker setup.  This includes front left, front right, center, rear left, rear right, side left, side right and a subwoofer. */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_7POINT1 = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_7POINT1", EnumerationJNI.get_FMOD_SPEAKERMODE_7POINT1());
	/** Stereo output, but data is encoded in a way that is picked up by a Prologic/Prologic2 decoder and split into a 5.1 speaker setup. */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_PROLOGIC = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_PROLOGIC", EnumerationJNI.get_FMOD_SPEAKERMODE_PROLOGIC());
	/** Maximum number of speaker modes supported. */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_MAX = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_MAX", EnumerationJNI.get_FMOD_SPEAKERMODE_MAX());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_SPEAKERMODE FMOD_SPEAKERMODE_FORCEINT = new FMOD_SPEAKERMODE("FMOD_SPEAKERMODE_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*10);
	static
	{
		VALUES.put(new Integer(FMOD_SPEAKERMODE_RAW.asInt()), FMOD_SPEAKERMODE_RAW);
		VALUES.put(new Integer(FMOD_SPEAKERMODE_MONO.asInt()), FMOD_SPEAKERMODE_MONO);
		VALUES.put(new Integer(FMOD_SPEAKERMODE_STEREO.asInt()), FMOD_SPEAKERMODE_STEREO);
		VALUES.put(new Integer(FMOD_SPEAKERMODE_QUAD.asInt()), FMOD_SPEAKERMODE_QUAD);
		VALUES.put(new Integer(FMOD_SPEAKERMODE_SURROUND.asInt()), FMOD_SPEAKERMODE_SURROUND);
		VALUES.put(new Integer(FMOD_SPEAKERMODE_5POINT1.asInt()), FMOD_SPEAKERMODE_5POINT1);
		VALUES.put(new Integer(FMOD_SPEAKERMODE_7POINT1.asInt()), FMOD_SPEAKERMODE_7POINT1);
		VALUES.put(new Integer(FMOD_SPEAKERMODE_PROLOGIC.asInt()), FMOD_SPEAKERMODE_PROLOGIC);
		VALUES.put(new Integer(FMOD_SPEAKERMODE_MAX.asInt()), FMOD_SPEAKERMODE_MAX);
		VALUES.put(new Integer(FMOD_SPEAKERMODE_FORCEINT.asInt()), FMOD_SPEAKERMODE_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_SPEAKERMODE(String name, int nativeValue)
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
		if(object instanceof FMOD_SPEAKERMODE)
			return asInt() == ((FMOD_SPEAKERMODE)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_SPEAKERMODE)object).asInt();
	}


	/**
	 * Retrieve a FMOD_SPEAKERMODE enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_SPEAKERMODE enum field that correspond to the integer value
	 */
	public static FMOD_SPEAKERMODE get(int nativeValue)
	{
		return (FMOD_SPEAKERMODE)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_SPEAKERMODE enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_SPEAKERMODE enum field
	 * @return the FMOD_SPEAKERMODE enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_SPEAKERMODE get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_SPEAKERMODE></code> in Java 1.5.
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