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
 *     DSP plugin structure that is passed into each callback.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Members marked with [in] mean the variable can be written to.  The user can set the value.<BR>
 *     Members marked with [out] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     FMOD_DSP_DESCRIPTION<BR>
 * 
 */
public class FMOD_DSP_STATE extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_DSP_STATE</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_DSP_STATE object.
	 */
	public static FMOD_DSP_STATE createView(Pointer pointer)
	{
		return new FMOD_DSP_STATE(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_DSP_STATE</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_DSP_STATE obj = FMOD_DSP_STATE.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_DSP_STATE create()
	{
		return new FMOD_DSP_STATE(StructureJNI.FMOD_DSP_STATE_new());
	}

	protected FMOD_DSP_STATE(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_DSP_STATE</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_DSP_STATE obj = new FMOD_DSP_STATE();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_DSP_STATE</code>, use the static "constructor" :
	 * <pre><code>  FMOD_DSP_STATE obj = FMOD_DSP_STATE.create();</code></pre>
	 * @see FMOD_DSP_STATE#create()
	 */
	public FMOD_DSP_STATE()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{

			StructureJNI.FMOD_DSP_STATE_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [out] Handle to the DSP hand the user created.  Not to be modified.  C++ users cast to FMOD::DSP to use.
	 */
	public DSP getInstance()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_DSP_STATE_get_instance(pointer);
		return javaResult == 0 ? null : DSP.createView(Pointer.newPointer(javaResult));
	}

	/**
	 * [in] Plugin writer created data the output author wants to attach to this object.
	 */
	public Pointer getPluginData()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_DSP_STATE_get_plugindata(pointer);
		return javaResult == 0 ? null : Pointer.newPointer(javaResult);
	}
	public void setPluginData(Pointer pluginData)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_STATE_set_plugindata(pointer, Pointer.getPointer(pluginData));
	}

	/**
	 * Specifies which speakers the DSP effect is active on
	 */
	public short getSpeakerMask()
	{
		if(pointer == 0) throw new NullPointerException();
		short javaResult = StructureJNI.FMOD_DSP_STATE_get_speakermask(pointer);
		return javaResult;
	}
	public void setSpeakerMask(short speakerMask)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_STATE_set_speakermask(pointer, speakerMask);
	}

}