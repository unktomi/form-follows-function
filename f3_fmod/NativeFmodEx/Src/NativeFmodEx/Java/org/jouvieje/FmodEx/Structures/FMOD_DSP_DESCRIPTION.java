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
 *     When creating a DSP unit, declare one of these and provide the relevant callbacks and name for FMOD to use when it creates and uses a DSP unit of this type.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Members marked with [in] mean the variable can be written to.  The user can set the value.<BR>
 *     Members marked with [out] mean the variable is modified by FMOD and is for reading purposes only.  Do not change this value.<BR>
 *     <BR>
 *     There are 2 different ways to change a parameter in this architecture.<BR>
 *     One is to use DSP::setParameter / DSP::getParameter.  This is platform independant and is dynamic, so new unknown plugins can have their parameters enumerated and used.<BR>
 *     The other is to use DSP::showConfigDialog.  This is platform specific and requires a GUI, and will display a dialog box to configure the plugin.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR>    <BR>
 *     System::createDSP<BR>
 *     FMOD_DSP_STATE<BR>
 * 
 */
public class FMOD_DSP_DESCRIPTION extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>FMOD_DSP_DESCRIPTION</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a FMOD_DSP_DESCRIPTION object.
	 */
	public static FMOD_DSP_DESCRIPTION createView(Pointer pointer)
	{
		return new FMOD_DSP_DESCRIPTION(Pointer.getPointer(pointer));
	}
	/**
	 * Create a new <code>FMOD_DSP_DESCRIPTION</code>.<br>
	 * The call <code>isNull()</code> on the object created will return false.<br>
	 * <pre><code>  FMOD_DSP_DESCRIPTION obj = FMOD_DSP_DESCRIPTION.create();
	 *  (obj == null) <=> obj.isNull() <=> false
	 * </code></pre>
	 */
	public static FMOD_DSP_DESCRIPTION create()
	{
		return new FMOD_DSP_DESCRIPTION(StructureJNI.FMOD_DSP_DESCRIPTION_new());
	}

	protected FMOD_DSP_DESCRIPTION(long pointer)
	{
		super(pointer);
	}

	/**
	 * Create an object that holds a null <code>FMOD_DSP_DESCRIPTION</code>.<br>
	 * The call <code>isNull()</code> on the object created will returns true.<br>
	 * <pre><code>  FMOD_DSP_DESCRIPTION obj = new FMOD_DSP_DESCRIPTION();
	 *  (obj == null) <=> false
	 *  obj.isNull() <=> true
	 * </code></pre>
	 * To creates a new <code>FMOD_DSP_DESCRIPTION</code>, use the static "constructor" :
	 * <pre><code>  FMOD_DSP_DESCRIPTION obj = FMOD_DSP_DESCRIPTION.create();</code></pre>
	 * @see FMOD_DSP_DESCRIPTION#create()
	 */
	public FMOD_DSP_DESCRIPTION()
	{
		super();
	}

	public void release()
	{
		if(pointer != 0)
		{
			CallbackManager.addCallback(9, null, pointer);
			CallbackManager.addCallback(10, null, pointer);
			CallbackManager.addCallback(11, null, pointer);
			CallbackManager.addCallback(12, null, pointer);
			CallbackManager.addCallback(13, null, pointer);
			CallbackManager.addCallback(14, null, pointer);
			CallbackManager.addCallback(15, null, pointer);
			CallbackManager.addCallback(16, null, pointer);
			CallbackManager.addOwner(0, pointer);
			StructureJNI.FMOD_DSP_DESCRIPTION_delete(pointer);
		}
		pointer = 0;
	}

	/**
	 * [in] Name of the unit to be displayed in the network.
	 */
	public String getName()
	{
		if(pointer == 0) throw new NullPointerException();
		String javaResult = StructureJNI.FMOD_DSP_DESCRIPTION_get_name(pointer);
		return javaResult;
	}
	public void setName(String name)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_DESCRIPTION_set_name(pointer, name == null ? null : name.getBytes());
	}

	/**
	 * [in] Plugin writer's version number.
	 */
	public int getVersion()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_DSP_DESCRIPTION_get_version(pointer);
		return javaResult;
	}
	public void setVersion(int version)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_DESCRIPTION_set_version(pointer, version);
	}

	/**
	 * [in] Number of channels.  Use 0 to process whatever number of channels is currently in the network.  >0 would be mostly used if the unit is a unit that only generates sound.
	 */
	public int getChannels()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_DSP_DESCRIPTION_get_channels(pointer);
		return javaResult;
	}
	public void setChannels(int channels)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_DESCRIPTION_set_channels(pointer, channels);
	}

	/**
	 * [in] Create callback.  This is called when DSP unit is created.  Can be null.
	 */
	public FMOD_DSP_CREATECALLBACK getCreate()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_DSP_CREATECALLBACK)CallbackManager.getCallback(9, pointer, false);
	}
	public void setCreate(FMOD_DSP_CREATECALLBACK create)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(9, create, pointer);
		StructureJNI.FMOD_DSP_DESCRIPTION_set_create(pointer, create != null);
	}

	/**
	 * [in] Release callback.  This is called just before the unit is freed so the user can do any cleanup needed for the unit.  Can be null.
	 */
	public FMOD_DSP_RELEASECALLBACK getRelease()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_DSP_RELEASECALLBACK)CallbackManager.getCallback(10, pointer, false);
	}
	public void setRelease(FMOD_DSP_RELEASECALLBACK release)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(10, release, pointer);
		StructureJNI.FMOD_DSP_DESCRIPTION_set_release(pointer, release != null);
	}

	/**
	 * [in] Reset callback.  This is called by the user to reset any history buffers that may need resetting for a filter, when it is to be used or re-used for the first time to its initial clean state.  Use to avoid clicks or artifacts.
	 */
	public FMOD_DSP_RESETCALLBACK getReset()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_DSP_RESETCALLBACK)CallbackManager.getCallback(11, pointer, false);
	}
	public void setReset(FMOD_DSP_RESETCALLBACK reset)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(11, reset, pointer);
		StructureJNI.FMOD_DSP_DESCRIPTION_set_reset(pointer, reset != null);
	}

	/**
	 * [in] Read callback.  Processing is done here.  Can be null.
	 */
	public FMOD_DSP_READCALLBACK getRead()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_DSP_READCALLBACK)CallbackManager.getCallback(12, pointer, false);
	}
	public void setRead(FMOD_DSP_READCALLBACK read)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(12, read, pointer);
		StructureJNI.FMOD_DSP_DESCRIPTION_set_read(pointer, read != null);
	}

	/**
	 * [in] Set position callback.  This is called if the unit wants to update its position info but not process data, or reset a cursor position internally if it is reading data from a certain source.  Can be null.
	 */
	public FMOD_DSP_SETPOSITIONCALLBACK getSetPosition()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_DSP_SETPOSITIONCALLBACK)CallbackManager.getCallback(13, pointer, false);
	}
	public void setSetPosition(FMOD_DSP_SETPOSITIONCALLBACK setPosition)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(13, setPosition, pointer);
		StructureJNI.FMOD_DSP_DESCRIPTION_set_setposition(pointer, setPosition != null);
	}

	/**
	 * [in] Number of parameters used in this filter.  The user finds this with DSP::getNumParameters
	 */
	public int getNumParameters()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_DSP_DESCRIPTION_get_numparameters(pointer);
		return javaResult;
	}
	public void setNumParameters(int numParameters)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_DESCRIPTION_set_numparameters(pointer, numParameters);
	}

	/**
	 * [in] Variable number of parameter structures.
	 */
	public FMOD_DSP_PARAMETERDESC[] getParamDesc()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_DSP_DESCRIPTION_get_paramdesc(pointer);
		int paramdescLength = -1;
		if(paramdescLength <= 0 || javaResult == 0) return null;
		FMOD_DSP_PARAMETERDESC[] paramdescArray = new FMOD_DSP_PARAMETERDESC[paramdescLength];
		int SIZEOF_FMOD_DSP_PARAMETERDESC = StructureJNI.FMOD_DSP_PARAMETERDESC_SIZEOF();
		for(int i = 0; i < paramdescArray.length; i++) {
			paramdescArray[i] = new FMOD_DSP_PARAMETERDESC(javaResult + i * SIZEOF_FMOD_DSP_PARAMETERDESC);
		}
		return paramdescArray;
	}
	public void setParamDesc(FMOD_DSP_PARAMETERDESC[] paramDesc)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_DESCRIPTION_set_paramdesc(pointer, Pointer.getPointer(paramDesc[0]));
	}

	/**
	 * [in] This is called when the user calls DSP::setParameter.  Can be null.
	 */
	public FMOD_DSP_SETPARAMCALLBACK getSetParameter()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_DSP_SETPARAMCALLBACK)CallbackManager.getCallback(14, pointer, false);
	}
	public void setSetParameter(FMOD_DSP_SETPARAMCALLBACK setParameter)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(14, setParameter, pointer);
		StructureJNI.FMOD_DSP_DESCRIPTION_set_setparameter(pointer, setParameter != null);
	}

	/**
	 * [in] This is called when the user calls DSP::getParameter.  Can be null.
	 */
	public FMOD_DSP_GETPARAMCALLBACK getGetParameter()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_DSP_GETPARAMCALLBACK)CallbackManager.getCallback(15, pointer, false);
	}
	public void setGetParameter(FMOD_DSP_GETPARAMCALLBACK getParameter)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(15, getParameter, pointer);
		StructureJNI.FMOD_DSP_DESCRIPTION_set_getparameter(pointer, getParameter != null);
	}

	/**
	 * [in] This is called when the user calls DSP::showConfigDialog.  Can be used to display a dialog to configure the filter.  Can be null.
	 */
	public FMOD_DSP_DIALOGCALLBACK getConfig()
	{
		if(pointer == 0) throw new NullPointerException();
		return (FMOD_DSP_DIALOGCALLBACK)CallbackManager.getCallback(16, pointer, false);
	}
	public void setConfig(FMOD_DSP_DIALOGCALLBACK config)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(16, config, pointer);
		StructureJNI.FMOD_DSP_DESCRIPTION_set_config(pointer, config != null);
	}

	/**
	 * [in] Width of config dialog graphic if there is one.  0 otherwise.
	 */
	public int getConfigWidth()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_DSP_DESCRIPTION_get_configwidth(pointer);
		return javaResult;
	}
	public void setConfigWidth(int configWidth)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_DESCRIPTION_set_configwidth(pointer, configWidth);
	}

	/**
	 * [in] Height of config dialog graphic if there is one.  0 otherwise.
	 */
	public int getConfigHeight()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = StructureJNI.FMOD_DSP_DESCRIPTION_get_configheight(pointer);
		return javaResult;
	}
	public void setConfigHeight(int configHeight)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_DESCRIPTION_set_configheight(pointer, configHeight);
	}

	/**
	 * [in] Optional. Specify 0 to ignore. This is user data to be attached to the DSP unit during creation.  Access via DSP::getUserData.
	 */
	public Pointer getUserData()
	{
		if(pointer == 0) throw new NullPointerException();
		long javaResult = StructureJNI.FMOD_DSP_DESCRIPTION_get_userdata(pointer);
		return javaResult == 0 ? null : Pointer.newPointer(javaResult);
	}
	public void setUserData(Pointer userData)
	{
		if(pointer == 0) throw new NullPointerException();
		StructureJNI.FMOD_DSP_DESCRIPTION_set_userdata(pointer, Pointer.getPointer(userData));
	}

}