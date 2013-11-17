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
 *     Structure describing a piece of tag data.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Members marked with [in] mean the user sets the value before passing it to the function.<BR>
 *     Members marked with [out] mean FMOD sets the value to be used after the function exits.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     Sound::getTag<BR>
 *     FMOD_TAGTYPE<BR>
 *     FMOD_TAGDATATYPE<BR>
 * 
 */
public class FMOD_TAG extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_TAG</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_TAG object.
	 */
	public static FMOD_TAG createView(Pointer pointer)
	{
		return new FMOD_TAG(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_TAG</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_TAG obj = FMOD_TAG.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_TAG create()
	{
		return new FMOD_TAG(StructureJNI.FMOD_TAG_new());
	}

	protected FMOD_TAG(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_TAG</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_TAG obj = new FMOD_TAG();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_TAG</code>, use the static "constructor" :
	 * <pre><code>  FMOD_TAG obj = FMOD_TAG.create();</code></pre>
	 * @see FMOD_TAG#create()
	 */
	public FMOD_TAG()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{

			StructureJNI.FMOD_TAG_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [out] The type of this tag.
	 */
	public FMOD_TAGTYPE getType()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_TAG_get_type(pointer);
		return FMOD_TAGTYPE.get(javaResult);
	}

	/**
	 * [out] The type of data that this tag contains
	 */
	public FMOD_TAGDATATYPE getDataType()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_TAG_get_datatype(pointer);
		return FMOD_TAGDATATYPE.get(javaResult);
	}

	/**
	 * [out] The name of this tag i.e. "TITLE", "ARTIST" etc.
	 */
	public String getName()
	{
		if(pointer == 0) throw new NullPointerException();
		String javaResult = StructureJNI.FMOD_TAG_get_name(pointer);
		return javaResult;
	}

	/**
	 * [out] Pointer to the tag data - its format is determined by the datatype member
	 */
	public Pointer getData()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_TAG_get_data(pointer);
		return javaResult == 0 ? null : Pointer.newPointer(javaResult);
	}

	/**
	 * [out] Length of the data contained in this tag
	 */
	public int getDataLen()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_TAG_get_datalen(pointer);
		return javaResult;
	}

	/**
	 * [out] True if this tag has been updated since last being accessed with Sound::getTag
	 */
	public boolean getUpdated()
	{
		if(pointer == 0) throw new NullPointerException();
		boolean javaResult = StructureJNI.FMOD_TAG_get_updated(pointer);
		return javaResult;
	}

}