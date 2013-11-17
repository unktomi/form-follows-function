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

public class FmodEx extends Pointer
{
	private FmodEx(){}

	/**
	 * 
	 */
	public static FMOD_RESULT Memory_Initialize(ByteBuffer poolmem, int poollen, FMOD_MEMORY_ALLOCCALLBACK useralloc, FMOD_MEMORY_REALLOCCALLBACK userrealloc, FMOD_MEMORY_FREECALLBACK userfree, int memtypeflags)
	{
		if(poolmem != null && !poolmem.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(useralloc != null && userrealloc != null && userfree != null)
		{
			CallbackManager.addCallback(26, useralloc, 0);
			CallbackManager.addCallback(27, userrealloc, 0);
			CallbackManager.addCallback(28, userfree, 0);
		}
		int javaResult = FmodExJNI.FmodEx_Memory_Initialize(poolmem, BufferUtils.getPositionInBytes(poolmem), poollen, useralloc == null ? false : true, userrealloc == null ? false : true, userfree == null ? false : true, memtypeflags);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public static FMOD_RESULT Memory_GetStats(IntBuffer currentalloced, IntBuffer maxalloced)
	{
		if(currentalloced != null && !currentalloced.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(maxalloced != null && !maxalloced.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.FmodEx_Memory_GetStats(currentalloced, BufferUtils.getPositionInBytes(currentalloced), maxalloced, BufferUtils.getPositionInBytes(maxalloced));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public static FMOD_RESULT Debug_SetLevel(int level)
	{
		int javaResult = FmodExJNI.FmodEx_Debug_SetLevel(level);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public static FMOD_RESULT Debug_GetLevel(IntBuffer level)
	{
		if(level != null && !level.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.FmodEx_Debug_GetLevel(level, BufferUtils.getPositionInBytes(level));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public static FMOD_RESULT File_SetDiskBusy(int busy)
	{
		int javaResult = FmodExJNI.FmodEx_File_SetDiskBusy(busy);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public static FMOD_RESULT File_GetDiskBusy(IntBuffer busy)
	{
		if(busy != null && !busy.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.FmodEx_File_GetDiskBusy(busy, BufferUtils.getPositionInBytes(busy));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public static FMOD_RESULT System_Create(System system)
	{
		int javaResult = FmodExJNI.FmodEx_System_Create(system);
		return FMOD_RESULT.get(javaResult);
	}


						/*fmod_errors.h*/

	public static String FMOD_ErrorString(FMOD_RESULT errCode)
	{
		return FmodExJNI.FMOD_ErrorString(errCode.asInt());
	}


	/**
	 * A little thing that I've added to know in which platform the program is running.<br>
	 * Compare the return value to those of the <code>PLATFORMS</code> interface.
	 * @return the platform in which the application is running.
	 * @see org.jouvieje.FmodEx.Defines.PLATFORMS
	 */
	public final static int getPlatform()
	{
		return Init.get_PLATFORM();
	}
}