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

package org.jouvieje.FmodEx;

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
 * 'DSP' API
 */
public class DSP extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>DSP</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a DSP object.
	 */
	public static DSP createView(Pointer pointer)
	{
		return new DSP(Pointer.getPointer(pointer));
	}
	private DSP(long pointer)
	{
		super(pointer);
	}

	public DSP()
	{
		super(0);
	}

	/**
	 * 
	 */
	public FMOD_RESULT release()
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addOwner(0, pointer);
		int javaResult = FmodExJNI.DSP_release(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSystemObject(System system)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_getSystemObject(pointer, system);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT addInput(DSP target, DSPConnection connection)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_addInput(pointer, Pointer.getPointer(target), connection);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT disconnectFrom(DSP target)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_disconnectFrom(pointer, Pointer.getPointer(target));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT disconnectAll(boolean inputs, boolean outputs)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_disconnectAll(pointer, inputs, outputs);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT remove()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_remove(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumInputs(IntBuffer numinputs)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numinputs != null && !numinputs.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getNumInputs(pointer, numinputs, BufferUtils.getPositionInBytes(numinputs));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumOutputs(IntBuffer numoutputs)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numoutputs != null && !numoutputs.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getNumOutputs(pointer, numoutputs, BufferUtils.getPositionInBytes(numoutputs));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getInput(int index, DSP input, DSPConnection inputconnection)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_getInput(pointer, index, input, inputconnection);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getOutput(int index, DSP output, DSPConnection outputconnection)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_getOutput(pointer, index, output, outputconnection);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setActive(boolean active)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_setActive(pointer, active);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getActive(ByteBuffer active)
	{
		if(pointer == 0) throw new NullPointerException();
		if(active != null && !active.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getActive(pointer, active, BufferUtils.getPositionInBytes(active));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setBypass(boolean bypass)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_setBypass(pointer, bypass);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getBypass(ByteBuffer bypass)
	{
		if(pointer == 0) throw new NullPointerException();
		if(bypass != null && !bypass.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getBypass(pointer, bypass, BufferUtils.getPositionInBytes(bypass));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setSpeakerActive(FMOD_SPEAKER speaker, boolean active)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_setSpeakerActive(pointer, speaker.asInt(), active);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSpeakerActive(FMOD_SPEAKER speaker, ByteBuffer active)
	{
		if(pointer == 0) throw new NullPointerException();
		if(active != null && !active.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getSpeakerActive(pointer, speaker.asInt(), active, BufferUtils.getPositionInBytes(active));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT reset()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_reset(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setParameter(int index, float value)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_setParameter(pointer, index, value);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getParameter(int index, FloatBuffer value, ByteBuffer valuestr, int valuestrlen)
	{
		if(pointer == 0) throw new NullPointerException();
		if(value != null && !value.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(valuestr != null && !valuestr.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getParameter(pointer, index, value, BufferUtils.getPositionInBytes(value), valuestr, BufferUtils.getPositionInBytes(valuestr), valuestrlen);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumParameters(IntBuffer numparams)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numparams != null && !numparams.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getNumParameters(pointer, numparams, BufferUtils.getPositionInBytes(numparams));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getParameterInfo(int index, ByteBuffer name, ByteBuffer label, ByteBuffer description, int descriptionlen, FloatBuffer min, FloatBuffer max)
	{
		if(pointer == 0) throw new NullPointerException();
		if(name != null && !name.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(label != null && !label.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(description != null && !description.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(min != null && !min.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(max != null && !max.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getParameterInfo(pointer, index, name, BufferUtils.getPositionInBytes(name), label, BufferUtils.getPositionInBytes(label), description, BufferUtils.getPositionInBytes(description), descriptionlen, min, BufferUtils.getPositionInBytes(min), max, BufferUtils.getPositionInBytes(max));
		return FMOD_RESULT.get(javaResult);
	}

	public FMOD_RESULT getInfo(ByteBuffer name, IntBuffer version, IntBuffer channels, IntBuffer configwidth, IntBuffer configheight)
	{
		if(pointer == 0) throw new NullPointerException();
		if(name != null && !name.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(version != null && !version.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(channels != null && !channels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(configwidth != null && !configwidth.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(configheight != null && !configheight.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getInfo(pointer, name, BufferUtils.getPositionInBytes(name), version, BufferUtils.getPositionInBytes(version), channels, BufferUtils.getPositionInBytes(channels), configwidth, BufferUtils.getPositionInBytes(configwidth), configheight, BufferUtils.getPositionInBytes(configheight));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getType(FMOD_DSP_TYPE[] type)
	{
		if(pointer == 0) throw new NullPointerException();
		IntBuffer typePointer = BufferUtils.newIntBuffer(1);
		int javaResult = FmodExJNI.DSP_getType(pointer, typePointer);
		if(type != null) {
			type[0] = FMOD_DSP_TYPE.get(typePointer.get(0));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setDefaults(float frequency, float volume, float pan, int priority)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_setDefaults(pointer, frequency, volume, pan, priority);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDefaults(FloatBuffer frequency, FloatBuffer volume, FloatBuffer pan, IntBuffer priority)
	{
		if(pointer == 0) throw new NullPointerException();
		if(frequency != null && !frequency.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(volume != null && !volume.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(pan != null && !pan.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(priority != null && !priority.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.DSP_getDefaults(pointer, frequency, BufferUtils.getPositionInBytes(frequency), volume, BufferUtils.getPositionInBytes(volume), pan, BufferUtils.getPositionInBytes(pan), priority, BufferUtils.getPositionInBytes(priority));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_setUserData(pointer, Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.DSP_getUserData(pointer, userdata);
		return FMOD_RESULT.get(javaResult);
	}

}