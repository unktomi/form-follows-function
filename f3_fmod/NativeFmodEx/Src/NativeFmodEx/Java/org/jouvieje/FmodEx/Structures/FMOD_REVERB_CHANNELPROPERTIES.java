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

package org.jouvieje.FmodEx.Structures;

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

/**
 * <BR>
 *     <BR>
 *     Structure defining the properties for a reverb source, related to a FMOD channel.<BR>
 * <BR>
 *     For more indepth descriptions of the reverb properties under win32, please see the EAX3<BR>
 *     documentation at http://developer.creative.com/ under the 'downloads' section.<BR>
 *     If they do not have the EAX3 documentation, then most information can be attained from<BR>
 *     the EAX2 documentation, as EAX3 only adds some more parameters and functionality on top of<BR>
 *     EAX2.<BR>
 * <BR>
 *     Note the default reverb properties are the same as the FMOD_PRESET_GENERIC preset.<BR>
 *     Note that integer values that typically range from -10,000 to 1000 are represented in<BR>
 *     decibels, and are of a logarithmic scale, not linear, wheras float values are typically linear.<BR>
 *     PORTABILITY: Each member has the platform it supports in braces ie (win32/Xbox).<BR>
 *     Some reverb parameters are only supported in win32 and some only on Xbox. If all parameters are set then<BR>
 *     the reverb should product a similar effect on either platform.<BR>
 * <BR>
 *     The numerical values listed below are the maximum, minimum and default values for each variable respectively.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     <b>SUPPORTED</b> next to each parameter means the platform the parameter can be set on.  Some platforms support all parameters and some don't.<BR>
 *     EAX   means hardware reverb on FMOD_OUTPUTTYPE_DSOUND on windows only (must use FMOD_HARDWARE), on soundcards that support EAX 1 to 4.<BR>
 *     EAX4  means hardware reverb on FMOD_OUTPUTTYPE_DSOUND on windows only (must use FMOD_HARDWARE), on soundcards that support EAX 4.<BR>
 *     I3DL2 means hardware reverb on FMOD_OUTPUTTYPE_DSOUND on windows only (must use FMOD_HARDWARE), on soundcards that support I3DL2 non EAX native reverb.<BR>
 *     GC    means Nintendo Gamecube hardware reverb (must use FMOD_HARDWARE).<BR>
 *     WII   means Nintendo Wii hardware reverb (must use FMOD_HARDWARE).<BR>
 *     Xbox1 means the original Xbox hardware reverb (must use FMOD_HARDWARE).<BR>
 *     PS2   means Playstation 2 hardware reverb (must use FMOD_HARDWARE).<BR>
 *     SFX   means FMOD SFX software reverb.  This works on any platform that uses FMOD_SOFTWARE for loading sounds.<BR>
 * <BR>
 * <BR>
 *     <b>'ConnectionPoint' Parameter.</b>  This parameter is for the FMOD software reverb only (known as SFX in the list above).<BR>
 *     By default the dsp network connection for a channel and its reverb is between the 'SFX Reverb' unit, and the channel's wavetable/resampler/dspcodec/oscillator unit (the unit below the channel DSP head).<BR>
 *     This parameter allows the user to connect the SFX reverb to somewhere else internally, for example the channel DSP head, or a related channelgroup.  The event system uses this so that it can have the output of an event going to the reverb, instead of just the output of the event's channels (thereby ignoring event effects/submixes etc).<BR>
 *     Do not use/leave null if you are unaware of DSP network connection issues.<BR>
 * <BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     Channel::setReverbProperties<BR>
 *     Channel::getReverbProperties<BR>
 *     FMOD_REVERB_CHANNELFLAGS<BR>
 * 
 */
public class FMOD_REVERB_CHANNELPROPERTIES extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_REVERB_CHANNELPROPERTIES</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_REVERB_CHANNELPROPERTIES object.
	 */
	public static FMOD_REVERB_CHANNELPROPERTIES createView(Pointer pointer)
	{
		return new FMOD_REVERB_CHANNELPROPERTIES(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_REVERB_CHANNELPROPERTIES</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_REVERB_CHANNELPROPERTIES obj = FMOD_REVERB_CHANNELPROPERTIES.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_REVERB_CHANNELPROPERTIES create()
	{
		return new FMOD_REVERB_CHANNELPROPERTIES(StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_new());
	}

	protected FMOD_REVERB_CHANNELPROPERTIES(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_REVERB_CHANNELPROPERTIES</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_REVERB_CHANNELPROPERTIES obj = new FMOD_REVERB_CHANNELPROPERTIES();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_REVERB_CHANNELPROPERTIES</code>, use the static "constructor" :
	 * <pre><code>  FMOD_REVERB_CHANNELPROPERTIES obj = FMOD_REVERB_CHANNELPROPERTIES.create();</code></pre>
	 * @see FMOD_REVERB_CHANNELPROPERTIES#create()
	 */
	public FMOD_REVERB_CHANNELPROPERTIES()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{

			StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [in/out] -10000, 1000,  0,       direct path level (at low and mid frequencies)              (SUPPORTED:EAX/I3DL2/Xbox1/SFX)
	 */
	public int getDirect()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_Direct(pointer);
		return javaResult;
	}
	public void setDirect(int direct)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_Direct(pointer, direct);
	}

	/**
	 * [in/out] -10000, 0,     0,       relative direct path level at high frequencies              (SUPPORTED:EAX/I3DL2/Xbox1)
	 */
	public int getDirectHF()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_DirectHF(pointer);
		return javaResult;
	}
	public void setDirectHF(int directHF)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_DirectHF(pointer, directHF);
	}

	/**
	 * [in/out] -10000, 1000,  0,       room effect level (at low and mid frequencies)              (SUPPORTED:EAX/I3DL2/Xbox1/GC/SFX)
	 */
	public int getRoom()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_Room(pointer);
		return javaResult;
	}
	public void setRoom(int room)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_Room(pointer, room);
	}

	/**
	 * [in/out] -10000, 0,     0,       relative room effect level at high frequencies              (SUPPORTED:EAX/I3DL2/Xbox1)
	 */
	public int getRoomHF()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_RoomHF(pointer);
		return javaResult;
	}
	public void setRoomHF(int roomHF)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_RoomHF(pointer, roomHF);
	}

	/**
	 * [in/out] -10000, 0,     0,       main obstruction control (attenuation at high frequencies)  (SUPPORTED:EAX/I3DL2/Xbox1)
	 */
	public int getObstruction()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_Obstruction(pointer);
		return javaResult;
	}
	public void setObstruction(int obstruction)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_Obstruction(pointer, obstruction);
	}

	/**
	 * [in/out] 0.0,    1.0,   0.0,     obstruction low-frequency level re. main control            (SUPPORTED:EAX/I3DL2/Xbox1)
	 */
	public float getObstructionLFRatio()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_ObstructionLFRatio(pointer);
		return javaResult;
	}
	public void setObstructionLFRatio(float obstructionLFRatio)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_ObstructionLFRatio(pointer, obstructionLFRatio);
	}

	/**
	 * [in/out] -10000, 0,     0,       main occlusion control (attenuation at high frequencies)    (SUPPORTED:EAX/I3DL2/Xbox1)
	 */
	public int getOcclusion()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_Occlusion(pointer);
		return javaResult;
	}
	public void setOcclusion(int occlusion)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_Occlusion(pointer, occlusion);
	}

	/**
	 * [in/out] 0.0,    1.0,   0.25,    occlusion low-frequency level re. main control              (SUPPORTED:EAX/I3DL2/Xbox1)
	 */
	public float getOcclusionLFRatio()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_OcclusionLFRatio(pointer);
		return javaResult;
	}
	public void setOcclusionLFRatio(float occlusionLFRatio)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_OcclusionLFRatio(pointer, occlusionLFRatio);
	}

	/**
	 * [in/out] 0.0,    10.0,  1.5,     relative occlusion control for room effect                  (SUPPORTED:EAX only)
	 */
	public float getOcclusionRoomRatio()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_OcclusionRoomRatio(pointer);
		return javaResult;
	}
	public void setOcclusionRoomRatio(float occlusionRoomRatio)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_OcclusionRoomRatio(pointer, occlusionRoomRatio);
	}

	/**
	 * [in/out] 0.0,    10.0,  1.0,     relative occlusion control for direct path                  (SUPPORTED:EAX only)
	 */
	public float getOcclusionDirectRatio()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_OcclusionDirectRatio(pointer);
		return javaResult;
	}
	public void setOcclusionDirectRatio(float occlusionDirectRatio)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_OcclusionDirectRatio(pointer, occlusionDirectRatio);
	}

	/**
	 * [in/out] -10000, 0,     0,       main exlusion control (attenuation at high frequencies)     (SUPPORTED:EAX only)
	 */
	public int getExclusion()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_Exclusion(pointer);
		return javaResult;
	}
	public void setExclusion(int exclusion)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_Exclusion(pointer, exclusion);
	}

	/**
	 * [in/out] 0.0,    1.0,   1.0,     exclusion low-frequency level re. main control              (SUPPORTED:EAX only)
	 */
	public float getExclusionLFRatio()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_ExclusionLFRatio(pointer);
		return javaResult;
	}
	public void setExclusionLFRatio(float exclusionLFRatio)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_ExclusionLFRatio(pointer, exclusionLFRatio);
	}

	/**
	 * [in/out] -10000, 0,     0,       outside sound cone level at high frequencies                (SUPPORTED:EAX only)
	 */
	public int getOutsideVolumeHF()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_OutsideVolumeHF(pointer);
		return javaResult;
	}
	public void setOutsideVolumeHF(int outsideVolumeHF)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_OutsideVolumeHF(pointer, outsideVolumeHF);
	}

	/**
	 * [in/out] 0.0,    10.0,  0.0,     like DS3D flDopplerFactor but per source                    (SUPPORTED:EAX only)
	 */
	public float getDopplerFactor()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_DopplerFactor(pointer);
		return javaResult;
	}
	public void setDopplerFactor(float dopplerFactor)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_DopplerFactor(pointer, dopplerFactor);
	}

	/**
	 * [in/out] 0.0,    10.0,  0.0,     like DS3D flRolloffFactor but per source                    (SUPPORTED:EAX only)
	 */
	public float getRolloffFactor()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_RolloffFactor(pointer);
		return javaResult;
	}
	public void setRolloffFactor(float rolloffFactor)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_RolloffFactor(pointer, rolloffFactor);
	}

	/**
	 * [in/out] 0.0,    10.0,  0.0,     like DS3D flRolloffFactor but for room effect               (SUPPORTED:EAX/I3DL2/Xbox1)
	 */
	public float getRoomRolloffFactor()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_RoomRolloffFactor(pointer);
		return javaResult;
	}
	public void setRoomRolloffFactor(float roomRolloffFactor)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_RoomRolloffFactor(pointer, roomRolloffFactor);
	}

	/**
	 * [in/out] 0.0,    10.0,  1.0,     multiplies AirAbsorptionHF member of FMOD_REVERB_PROPERTIES (SUPPORTED:EAX only)
	 */
	public float getAirAbsorptionFactor()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_AirAbsorptionFactor(pointer);
		return javaResult;
	}
	public void setAirAbsorptionFactor(float airAbsorptionFactor)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_AirAbsorptionFactor(pointer, airAbsorptionFactor);
	}

	/**
	 * [in/out] FMOD_REVERB_CHANNELFLAGS - modifies the behavior of properties                      (SUPPORTED:EAX only)
	 */
	public int getFlags()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_Flags(pointer);
		return javaResult;
	}
	public void setFlags(int flags)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_Flags(pointer, flags);
	}

	/**
	 * [in/out] See remarks.            DSP network location to connect reverb for this channel.    (SUPPORTED:SFX only).
	 */
	public DSP getConnectionPoint()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_get_ConnectionPoint(pointer);
		return javaResult == 0 ? null : DSP.createView(Pointer.newPointer(javaResult));
	}
	public void setConnectionPoint(DSP connectionPoint)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_CHANNELPROPERTIES_set_ConnectionPoint(pointer, Pointer.getPointer(connectionPoint));
	}

}