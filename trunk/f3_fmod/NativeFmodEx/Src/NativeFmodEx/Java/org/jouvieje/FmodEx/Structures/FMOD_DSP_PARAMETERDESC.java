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
 *     Structure to define a parameter for a DSP unit.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Members marked with [in] mean the variable can be written to.  The user can set the value.<BR>
 *     Members marked with [out] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<BR>
 *     <BR>
 *     The step parameter tells the gui or application that the parameter has a certain granularity.<BR>
 *     For example in the example of cutoff frequency with a range from 100.0 to 22050.0 you might only want the selection to be in 10hz increments.  For this you would simply use 10.0 as the step value.<BR>
 *     For a boolean, you can use min = 0.0, max = 1.0, step = 1.0.  This way the only possible values are 0.0 and 1.0.<BR>
 *     Some applications may detect min = 0.0, max = 1.0, step = 1.0 and replace a graphical slider bar with a checkbox instead.<BR>
 *     A step value of 1.0 would simulate integer values only.<BR>
 *     A step value of 0.0 would mean the full floating point range is accessable.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR>    <BR>
 *     System::createDSP<BR>
 *     DSP::setParameter<BR>
 * 
 */
public class FMOD_DSP_PARAMETERDESC extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_DSP_PARAMETERDESC</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_DSP_PARAMETERDESC object.
	 */
	public static FMOD_DSP_PARAMETERDESC createView(Pointer pointer)
	{
		return new FMOD_DSP_PARAMETERDESC(Pointer.getPointer(pointer));
	}
	private static long SIZEOF_FMOD_DSP_PARAMETERDESC = -1;
	/**
	 * Create and initialize a new <code>FMOD_DSP_PARAMETERDESC[]</code>.<br>
	 * @param length length of the array returned.
	 */
	public static FMOD_DSP_PARAMETERDESC[] create(int length)
	{
		if(length <= 0)
			 return null;
		
		long first = StructureJNI.FMOD_DSP_PARAMETERDESC_newArray(length);
		
		if(SIZEOF_FMOD_DSP_PARAMETERDESC == -1)
			SIZEOF_FMOD_DSP_PARAMETERDESC = StructureJNI.FMOD_DSP_PARAMETERDESC_SIZEOF();
		
		FMOD_DSP_PARAMETERDESC[] array = new FMOD_DSP_PARAMETERDESC[length];
		for(int i = 0; i < length; i++)
			array[i] = new FMOD_DSP_PARAMETERDESC(first + i * SIZEOF_FMOD_DSP_PARAMETERDESC);
		
		return array;
	}

	/**
	 * Create a new <code>FMOD_DSP_PARAMETERDESC</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_DSP_PARAMETERDESC obj = FMOD_DSP_PARAMETERDESC.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_DSP_PARAMETERDESC create()
	{
		return new FMOD_DSP_PARAMETERDESC(StructureJNI.FMOD_DSP_PARAMETERDESC_new());
	}

	protected FMOD_DSP_PARAMETERDESC(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_DSP_PARAMETERDESC</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_DSP_PARAMETERDESC obj = new FMOD_DSP_PARAMETERDESC();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_DSP_PARAMETERDESC</code>, use the static "constructor" :
	 * <pre><code>  FMOD_DSP_PARAMETERDESC obj = FMOD_DSP_PARAMETERDESC.create();</code></pre>
	 * @see FMOD_DSP_PARAMETERDESC#create()
	 */
	public FMOD_DSP_PARAMETERDESC()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{

			StructureJNI.FMOD_DSP_PARAMETERDESC_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [in] Minimum value of the parameter (ie 100.0).
	 */
	public float getMin()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_DSP_PARAMETERDESC_get_min(pointer);
		return javaResult;
	}
	public void setMin(float min)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_PARAMETERDESC_set_min(pointer, min);
	}

	/**
	 * [in] Maximum value of the parameter (ie 22050.0).
	 */
	public float getMax()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_DSP_PARAMETERDESC_get_max(pointer);
		return javaResult;
	}
	public void setMax(float max)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_PARAMETERDESC_set_max(pointer, max);
	}

	/**
	 * [in] Default value of parameter.
	 */
	public float getDefaultVal()
	{
		if(pointer == 0) throw new NullPointerException();
		float javaResult = StructureJNI.FMOD_DSP_PARAMETERDESC_get_defaultval(pointer);
		return javaResult;
	}
	public void setDefaultVal(float defaultVal)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_PARAMETERDESC_set_defaultval(pointer, defaultVal);
	}

	/**
	 * [in] Name of the parameter to be displayed (ie "Cutoff frequency").
	 */
	public String getName()
	{
		if(pointer == 0) throw new NullPointerException();
		String javaResult = StructureJNI.FMOD_DSP_PARAMETERDESC_get_name(pointer);
		return javaResult;
	}
	public void setName(String name)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_PARAMETERDESC_set_name(pointer, name == null ? null : name.getBytes());
	}

	/**
	 * [in] Short string to be put next to value to denote the unit type (ie "hz").
	 */
	public String getLabel()
	{
		if(pointer == 0) throw new NullPointerException();
		String javaResult = StructureJNI.FMOD_DSP_PARAMETERDESC_get_label(pointer);
		return javaResult;
	}
	public void setLabel(String label)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_PARAMETERDESC_set_label(pointer, label == null ? null : label.getBytes());
	}

	/**
	 * [in] Description of the parameter to be displayed as a help item / tooltip for this parameter.
	 */
	public String getDescription()
	{
		if(pointer == 0) throw new NullPointerException();
		String javaResult = StructureJNI.FMOD_DSP_PARAMETERDESC_get_description(pointer);
		return javaResult;
	}
	public void setDescription(String description)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_PARAMETERDESC_set_description(pointer, description == null ? null : description.getBytes());
	}

}