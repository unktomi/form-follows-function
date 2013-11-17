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
import org.jouvieje.FmodEx.Defines.FMOD_REVERB_PRESETS;

/**
 * <BR>
 *     <BR>
 *     Structure defining a reverb environment.<BR>
 * <BR>
 *     For more indepth descriptions of the reverb properties under win32, please see the EAX2 and EAX3<BR>
 *     documentation at http://developer.creative.com/ under the 'downloads' section.<BR>
 *     If they do not have the EAX3 documentation, then most information can be attained from<BR>
 *     the EAX2 documentation, as EAX3 only adds some more parameters and functionality on top of<BR>
 *     EAX2.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Note the default reverb properties are the same as the FMOD_PRESET_GENERIC preset.<BR>
 *     Note that integer values that typically range from -10,000 to 1000 are represented in<BR>
 *     decibels, and are of a logarithmic scale, not linear, wheras float values are always linear.<BR>
 * <BR>
 *     The numerical values listed below are the maximum, minimum and default values for each variable respectively.<BR>
 * <BR>
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
 *     Members marked with [in] mean the user sets the value before passing it to the function.<BR>
 *     Members marked with [out] mean FMOD sets the value to be used after the function exits.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::setReverbProperties<BR>
 *     System::getReverbProperties<BR>
 *     FMOD_REVERB_PRESETS<BR>
 *     FMOD_REVERB_FLAGS<BR>
 * 
 */
public class FMOD_REVERB_PROPERTIES extends Pointer
{
	/**
	 * Create an <code>FMOD_REVERB_PROPERTIES</code> using a preset <code>FMOD_REVERB_PRESETS</code>.<br>
	 * @param preset a preset of the interface <code>FMOD_REVERB_PRESETS</code>.
	 * @see org.jouvieje.FmodEx.Defines.FMOD_REVERB_PRESETS
	 */
	public static FMOD_REVERB_PROPERTIES create(int preset)
	{
		switch(preset)
		{
			case FMOD_REVERB_PRESETS.FMOD_PRESET_OFF: 				return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_OFF());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_GENERIC: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_GENERIC());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PADDEDCELL: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PADDEDCELL());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_ROOM: 				return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_ROOM());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_BATHROOM: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_BATHROOM());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_LIVINGROOM: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_LIVINGROOM());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_STONEROOM: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_STONEROOM());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_AUDITORIUM: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_AUDITORIUM());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_CONCERTHALL: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_CONCERTHALL());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_CAVE: 				return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_CAVE());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_ARENA: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_ARENA());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_HANGAR: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_HANGAR());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_CARPETTEDHALLWAY: 	return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_CARPETTEDHALLWAY());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_HALLWAY: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_HALLWAY());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_STONECORRIDOR: 	return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_STONECORRIDOR());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_ALLEY: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_ALLEY());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_FOREST: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_FOREST());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_CITY: 				return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_CITY());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_MOUNTAINS: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_MOUNTAINS());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_QUARRY: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_QUARRY());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PLAIN: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PLAIN());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PARKINGLOT: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PARKINGLOT());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_SEWERPIPE: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_SEWERPIPE());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_UNDERWATER: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_UNDERWATER());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_DRUGGED: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_DRUGGED());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_DIZZY: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_DIZZY());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PSYCHOTIC: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PSYCHOTIC());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PS2_ROOM: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PS2_ROOM());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PS2_STUDIO_A: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PS2_STUDIO_A());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PS2_STUDIO_B: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PS2_STUDIO_B());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PS2_STUDIO_C: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PS2_STUDIO_C());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PS2_HALL: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PS2_HALL());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PS2_SPACE: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PS2_SPACE());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PS2_ECHO: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PS2_ECHO());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PS2_DELAY: 		return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PS2_DELAY());
			case FMOD_REVERB_PRESETS.FMOD_PRESET_PS2_PIPE: 			return new FMOD_REVERB_PROPERTIES(StructureJNI.get_FMOD_PRESET_PS2_PIPE());
			default : return create();
		}
	}
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_REVERB_PROPERTIES</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_REVERB_PROPERTIES object.
	 */
	public static FMOD_REVERB_PROPERTIES createView(Pointer pointer)
	{
		return new FMOD_REVERB_PROPERTIES(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_REVERB_PROPERTIES</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_REVERB_PROPERTIES obj = FMOD_REVERB_PROPERTIES.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_REVERB_PROPERTIES create()
	{
		return new FMOD_REVERB_PROPERTIES(StructureJNI.FMOD_REVERB_PROPERTIES_new());
	}

	protected FMOD_REVERB_PROPERTIES(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_REVERB_PROPERTIES</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_REVERB_PROPERTIES obj = new FMOD_REVERB_PROPERTIES();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_REVERB_PROPERTIES</code>, use the static "constructor" :
	 * <pre><code>  FMOD_REVERB_PROPERTIES obj = FMOD_REVERB_PROPERTIES.create();</code></pre>
	 * @see FMOD_REVERB_PROPERTIES#create()
	 */
	public FMOD_REVERB_PROPERTIES()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{

			StructureJNI.FMOD_REVERB_PROPERTIES_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [in]     0     , 3     , 0      , Environment Instance. Simultaneous HW reverbs are possible on some platforms. (SUPPORTED:EAX4/SFX(3 instances)/GC and Wii (2 instances))
	 */
	public int getInstance()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_Instance(pointer);
		return javaResult;
	}
	public void setInstance(int instance)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_Instance(pointer, instance);
	}

	/**
	 * [in/out] -1    , 25    , -1     , sets all listener properties.  -1 = OFF.                                      (SUPPORTED:EAX/PS2)
	 */
	public int getEnvironment()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_Environment(pointer);
		return javaResult;
	}
	public void setEnvironment(int environment)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_Environment(pointer, environment);
	}

	/**
	 * [in/out] 1.0   , 100.0 , 7.5    , environment size in meters                                                    (SUPPORTED:EAX)
	 */
	public float getEnvSize()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_EnvSize(pointer);
		return javaResult;
	}
	public void setEnvSize(float envSize)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_EnvSize(pointer, envSize);
	}

	/**
	 * [in/out] 0.0   , 1.0   , 1.0    , environment diffusion                                                         (SUPPORTED:EAX/Xbox1/GC)
	 */
	public float getEnvDiffusion()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_EnvDiffusion(pointer);
		return javaResult;
	}
	public void setEnvDiffusion(float envDiffusion)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_EnvDiffusion(pointer, envDiffusion);
	}

	/**
	 * [in/out] -10000, 0     , -1000  , room effect level (at mid frequencies)                                        (SUPPORTED:EAX/Xbox1/GC/I3DL2/SFX)
	 */
	public int getRoom()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_Room(pointer);
		return javaResult;
	}
	public void setRoom(int room)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_Room(pointer, room);
	}

	/**
	 * [in/out] -10000, 0     , -100   , relative room effect level at high frequencies                                (SUPPORTED:EAX/Xbox1/I3DL2/SFX)
	 */
	public int getRoomHF()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_RoomHF(pointer);
		return javaResult;
	}
	public void setRoomHF(int roomHF)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_RoomHF(pointer, roomHF);
	}

	/**
	 * [in/out] -10000, 0     , 0      , relative room effect level at low frequencies                                 (SUPPORTED:EAX/SFX)
	 */
	public int getRoomLF()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_RoomLF(pointer);
		return javaResult;
	}
	public void setRoomLF(int roomLF)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_RoomLF(pointer, roomLF);
	}

	/**
	 * [in/out] 0.1   , 20.0  , 1.49   , reverberation decay time at mid frequencies                                   (SUPPORTED:EAX/Xbox1/GC/I3DL2/SFX)
	 */
	public float getDecayTime()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_DecayTime(pointer);
		return javaResult;
	}
	public void setDecayTime(float decayTime)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_DecayTime(pointer, decayTime);
	}

	/**
	 * [in/out] 0.1   , 2.0   , 0.83   , high-frequency to mid-frequency decay time ratio                              (SUPPORTED:EAX/Xbox1/I3DL2/SFX)
	 */
	public float getDecayHFRatio()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_DecayHFRatio(pointer);
		return javaResult;
	}
	public void setDecayHFRatio(float decayHFRatio)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_DecayHFRatio(pointer, decayHFRatio);
	}

	/**
	 * [in/out] 0.1   , 2.0   , 1.0    , low-frequency to mid-frequency decay time ratio                               (SUPPORTED:EAX)
	 */
	public float getDecayLFRatio()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_DecayLFRatio(pointer);
		return javaResult;
	}
	public void setDecayLFRatio(float decayLFRatio)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_DecayLFRatio(pointer, decayLFRatio);
	}

	/**
	 * [in/out] -10000, 1000  , -2602  , early reflections level relative to room effect                               (SUPPORTED:EAX/Xbox1/GC/I3DL2/SFX)
	 */
	public int getReflections()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_Reflections(pointer);
		return javaResult;
	}
	public void setReflections(int reflections)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_Reflections(pointer, reflections);
	}

	/**
	 * [in/out] 0.0   , 0.3   , 0.007  , initial reflection delay time                                                 (SUPPORTED:EAX/Xbox1/I3DL2/SFX)
	 */
	public float getReflectionsDelay()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_ReflectionsDelay(pointer);
		return javaResult;
	}
	public void setReflectionsDelay(float reflectionsDelay)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_ReflectionsDelay(pointer, reflectionsDelay);
	}

	/**
	 * [in/out]       ,       , [0,0,0], early reflections panning vector                                              (SUPPORTED:EAX)
	 */
	public FloatBuffer getReflectionsPan()
	{
		if(pointer == 0) throw new NullPointerException();
		ByteBuffer javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_ReflectionsPan(pointer);
		if(javaResult != null) {
			javaResult.order(ByteOrder.nativeOrder());
		}
		return javaResult.asFloatBuffer();
	}
	public void setReflectionsPan(FloatBuffer reflectionsPan)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_ReflectionsPan(pointer, reflectionsPan, BufferUtils.getPositionInBytes(reflectionsPan));
	}

	/**
	 * [in/out] -10000, 2000  , 200    , late reverberation level relative to room effect                              (SUPPORTED:EAX/Xbox1/I3DL2/SFX)
	 */
	public int getReverb()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_Reverb(pointer);
		return javaResult;
	}
	public void setReverb(int reverb)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_Reverb(pointer, reverb);
	}

	/**
	 * [in/out] 0.0   , 0.1   , 0.011  , late reverberation delay time relative to initial reflection                  (SUPPORTED:EAX/Xbox1/GC/I3DL2/SFX)
	 */
	public float getReverbDelay()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_ReverbDelay(pointer);
		return javaResult;
	}
	public void setReverbDelay(float reverbDelay)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_ReverbDelay(pointer, reverbDelay);
	}

	/**
	 * [in/out]       ,       , [0,0,0], late reverberation panning vector                                             (SUPPORTED:EAX)
	 */
	public FloatBuffer getReverbPan()
	{
		if(pointer == 0) throw new NullPointerException();
		ByteBuffer javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_ReverbPan(pointer);
		if(javaResult != null) {
			javaResult.order(ByteOrder.nativeOrder());
		}
		return javaResult.asFloatBuffer();
	}
	public void setReverbPan(FloatBuffer reverbPan)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_ReverbPan(pointer, reverbPan, BufferUtils.getPositionInBytes(reverbPan));
	}

	/**
	 * [in/out] .075  , 0.25  , 0.25   , echo time                                                                     (SUPPORTED:EAX/PS2(FMOD_PRESET_PS2_ECHO/FMOD_PRESET_PS2_DELAY only)
	 */
	public float getEchoTime()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_EchoTime(pointer);
		return javaResult;
	}
	public void setEchoTime(float echoTime)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_EchoTime(pointer, echoTime);
	}

	/**
	 * [in/out] 0.0   , 1.0   , 0.0    , echo depth                                                                    (SUPPORTED:EAX/PS2(FMOD_PRESET_PS2_ECHO only)
	 */
	public float getEchoDepth()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_EchoDepth(pointer);
		return javaResult;
	}
	public void setEchoDepth(float echoDepth)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_EchoDepth(pointer, echoDepth);
	}

	/**
	 * [in/out] 0.04  , 4.0   , 0.25   , modulation time                                                               (SUPPORTED:EAX)
	 */
	public float getModulationTime()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_ModulationTime(pointer);
		return javaResult;
	}
	public void setModulationTime(float modulationTime)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_ModulationTime(pointer, modulationTime);
	}

	/**
	 * [in/out] 0.0   , 1.0   , 0.0    , modulation depth                                                              (SUPPORTED:EAX/GC)
	 */
	public float getModulationDepth()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_ModulationDepth(pointer);
		return javaResult;
	}
	public void setModulationDepth(float modulationDepth)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_ModulationDepth(pointer, modulationDepth);
	}

	/**
	 * [in/out] -100  , 0.0   , -5.0   , change in level per meter at high frequencies                                 (SUPPORTED:EAX)
	 */
	public float getAirAbsorptionHF()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_AirAbsorptionHF(pointer);
		return javaResult;
	}
	public void setAirAbsorptionHF(float airAbsorptionHF)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_AirAbsorptionHF(pointer, airAbsorptionHF);
	}

	/**
	 * [in/out] 1000.0, 20000 , 5000.0 , reference high frequency (hz)                                                 (SUPPORTED:EAX/Xbox1/I3DL2/SFX)
	 */
	public float getHFReference()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_HFReference(pointer);
		return javaResult;
	}
	public void setHFReference(float HFReference)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_HFReference(pointer, HFReference);
	}

	/**
	 * [in/out] 20.0  , 1000.0, 250.0  , reference low frequency (hz)                                                  (SUPPORTED:EAX/SFX)
	 */
	public float getLFReference()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_LFReference(pointer);
		return javaResult;
	}
	public void setLFReference(float LFReference)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_LFReference(pointer, LFReference);
	}

	/**
	 * [in/out] 0.0   , 10.0  , 0.0    , like rolloffscale in System::set3DSettings but for reverb room size effect    (SUPPORTED:EAX/Xbox1/I3DL2/SFX)
	 */
	public float getRoomRolloffFactor()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_RoomRolloffFactor(pointer);
		return javaResult;
	}
	public void setRoomRolloffFactor(float roomRolloffFactor)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_RoomRolloffFactor(pointer, roomRolloffFactor);
	}

	/**
	 * [in/out] 0.0   , 100.0 , 100.0  , Value that controls the echo density in the late reverberation decay.         (SUPPORTED:I3DL2/Xbox1/SFX)
	 */
	public float getDiffusion()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_Diffusion(pointer);
		return javaResult;
	}
	public void setDiffusion(float diffusion)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_Diffusion(pointer, diffusion);
	}

	/**
	 * [in/out] 0.0   , 100.0 , 100.0  , Value that controls the modal density in the late reverberation decay         (SUPPORTED:I3DL2/Xbox1/SFX)
	 */
	public float getDensity()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_Density(pointer);
		return javaResult;
	}
	public void setDensity(float density)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_Density(pointer, density);
	}

	/**
	 * [in/out] FMOD_REVERB_FLAGS - modifies the behavior of above properties                                          (SUPPORTED:EAX/PS2/GC/WII)
	 */
	public int getFlags()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_REVERB_PROPERTIES_get_Flags(pointer);
		return javaResult;
	}
	public void setFlags(int flags)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_REVERB_PROPERTIES_set_Flags(pointer, flags);
	}

}