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
 * typedef int                       FMOD_BOOL;<BR>
 * typedef struct FMOD_SYSTEM        FMOD_SYSTEM;<BR>
 * typedef struct FMOD_SOUND         FMOD_SOUND;<BR>
 * typedef struct FMOD_CHANNEL       FMOD_CHANNEL;<BR>
 * typedef struct FMOD_CHANNELGROUP  FMOD_CHANNELGROUP;<BR>
 * typedef struct FMOD_SOUNDGROUP    FMOD_SOUNDGROUP;<BR>
 * typedef struct FMOD_REVERB        FMOD_REVERB;<BR>
 * typedef struct FMOD_DSP           FMOD_DSP;<BR>
 * typedef struct FMOD_DSPCONNECTION FMOD_DSPCONNECTION;<BR>
 * typedef struct FMOD_POLYGON		  FMOD_POLYGON;<BR>
 * typedef struct FMOD_GEOMETRY	  FMOD_GEOMETRY;
 */
public class FMOD_SYNCPOINT extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_SYNCPOINT</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_SYNCPOINT object.
	 */
	public static FMOD_SYNCPOINT createView(Pointer pointer)
	{
		return new FMOD_SYNCPOINT(Pointer.getPointer(pointer));
	}
	protected FMOD_SYNCPOINT(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_SYNCPOINT</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_SYNCPOINT obj = new FMOD_SYNCPOINT();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_SYNCPOINT</code>, use the static "constructor" :
	 * <pre><code>  FMOD_SYNCPOINT obj = FMOD_SYNCPOINT.create();</code></pre>
	 */
	public FMOD_SYNCPOINT()
	{
		super();
	}

	public void release()
	{
		pointer = 0;
	}

}