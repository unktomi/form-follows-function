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
 *     These output types are used with System::setOutput / System::getOutput, to choose which output method to use.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     To pass information to the driver when initializing fmod use the extradriverdata parameter in System::init for the following reasons.<BR>
 *     - FMOD_OUTPUTTYPE_WAVWRITER - extradriverdata is a pointer to a char * filename that the wav writer will output to.<BR>
 *     - FMOD_OUTPUTTYPE_WAVWRITER_NRT - extradriverdata is a pointer to a char * filename that the wav writer will output to.<BR>
 *     - FMOD_OUTPUTTYPE_DSOUND - extradriverdata is a pointer to a HWND so that FMOD can set the focus on the audio for a particular window.<BR>
 *     - FMOD_OUTPUTTYPE_PS2 - extradriverdata is a pointer to a FMOD_PS2_EXTRADRIVERDATA struct. This can be found in fmodps2.h.<BR>
 *     - FMOD_OUTPUTTYPE_PS3 - extradriverdata is a pointer to a FMOD_PS3_EXTRADRIVERDATA struct. This can be found in fmodps3.h.<BR>
 *     - FMOD_OUTPUTTYPE_GC - extradriverdata is a pointer to a FMOD_GC_INFO struct. This can be found in fmodgc.h.<BR>
 *     - FMOD_OUTPUTTYPE_WII - extradriverdata is a pointer to a FMOD_WII_INFO struct. This can be found in fmodwii.h.<BR>
 *     - FMOD_OUTPUTTYPE_ALSA - extradriverdata is a pointer to a FMOD_LINUX_EXTRADRIVERDATA struct. This can be found in fmodlinux.h.<BR>
 * <BR>
 *     Currently these are the only FMOD drivers that take extra information.  Other unknown plugins may have different requirements.<BR>
 * <BR>
 *     Note! If FMOD_OUTPUTTYPE_WAVWRITER_NRT or FMOD_OUTPUTTYPE_NOSOUND_NRT are used, and if the System::update function is being called<BR>
 *     very quickly (ie for a non realtime decode) it may be being called too quickly for the FMOD streamer thread to respond to.<BR>
 *     The result will be a skipping/stuttering output in the captured audio.<BR>
 * <BR>
 *     To remedy this, disable the FMOD Ex streamer thread, and use FMOD_INIT_STREAM_FROM_UPDATE to avoid skipping in the output stream,<BR>
 *     as it will lock the mixer and the streamer together in the same thread.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::setOutput<BR>
 *     System::getOutput<BR>
 *     System::setSoftwareFormat<BR>
 *     System::getSoftwareFormat<BR>
 *     System::init<BR>
 *     System::update<BR>
 *     FMOD_INITFLAGS<BR>
 * 
 */
public class FMOD_OUTPUTTYPE implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_AUTODETECT = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_AUTODETECT", EnumerationJNI.get_FMOD_OUTPUTTYPE_AUTODETECT());
	/** All                   - 3rd party plugin, unknown.  This is for use with System::getOutput only. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_UNKNOWN = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_UNKNOWN", EnumerationJNI.get_FMOD_OUTPUTTYPE_UNKNOWN());
	/** All                   - All calls in this mode succeed but make no sound. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_NOSOUND = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_NOSOUND", EnumerationJNI.get_FMOD_OUTPUTTYPE_NOSOUND());
	/** All                   - Writes output to fmodoutput.wav by default.  Use the 'extradriverdata' parameter in System::init, by simply passing the filename as a string, to set the wav filename. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_WAVWRITER = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_WAVWRITER", EnumerationJNI.get_FMOD_OUTPUTTYPE_WAVWRITER());
	/** All                   - Non-realtime version of FMOD_OUTPUTTYPE_NOSOUND.  User can drive mixer with System::update at whatever rate they want. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_NOSOUND_NRT = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_NOSOUND_NRT", EnumerationJNI.get_FMOD_OUTPUTTYPE_NOSOUND_NRT());
	/** All                   - Non-realtime version of FMOD_OUTPUTTYPE_WAVWRITER.  User can drive mixer with System::update at whatever rate they want. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_WAVWRITER_NRT = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_WAVWRITER_NRT", EnumerationJNI.get_FMOD_OUTPUTTYPE_WAVWRITER_NRT());
	/** Win32/Win64           - DirectSound output.  Use this to get hardware accelerated 3d audio and EAX Reverb support. (Default on Windows) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_DSOUND = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_DSOUND", EnumerationJNI.get_FMOD_OUTPUTTYPE_DSOUND());
	/** Win32/Win64           - Windows Multimedia output. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_WINMM = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_WINMM", EnumerationJNI.get_FMOD_OUTPUTTYPE_WINMM());
	/** Win32/Win64           - OpenAL 1.1 output. Use this for lower CPU overhead than FMOD_OUTPUTTYPE_DSOUND, and also Vista H/W support with Creative Labs cards. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_OPENAL = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_OPENAL", EnumerationJNI.get_FMOD_OUTPUTTYPE_OPENAL());
	/** Win32                 - Windows Audio Session API. (Default on Windows Vista) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_WASAPI = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_WASAPI", EnumerationJNI.get_FMOD_OUTPUTTYPE_WASAPI());
	/** Win32                 - Low latency ASIO driver. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_ASIO = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_ASIO", EnumerationJNI.get_FMOD_OUTPUTTYPE_ASIO());
	/** Linux/Linux64/Solaris - Open Sound System output. (Default on Linux/Linux64/Solaris) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_OSS = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_OSS", EnumerationJNI.get_FMOD_OUTPUTTYPE_OSS());
	/** Linux/Linux64         - Advanced Linux Sound Architecture output. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_ALSA = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_ALSA", EnumerationJNI.get_FMOD_OUTPUTTYPE_ALSA());
	/** Linux/Linux64         - Enlightment Sound Daemon output. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_ESD = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_ESD", EnumerationJNI.get_FMOD_OUTPUTTYPE_ESD());
	/** Mac                   - Macintosh SoundManager output. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_SOUNDMANAGER = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_SOUNDMANAGER", EnumerationJNI.get_FMOD_OUTPUTTYPE_SOUNDMANAGER());
	/** Mac                   - Macintosh CoreAudio output.  (Default on Mac) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_COREAUDIO = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_COREAUDIO", EnumerationJNI.get_FMOD_OUTPUTTYPE_COREAUDIO());
	/** Xbox                  - Native hardware output. (Default on Xbox) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_XBOX = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_XBOX", EnumerationJNI.get_FMOD_OUTPUTTYPE_XBOX());
	/** PS2                   - Native hardware output. (Default on PS2) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_PS2 = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_PS2", EnumerationJNI.get_FMOD_OUTPUTTYPE_PS2());
	/** PS3                   - Native hardware output. (Default on PS3) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_PS3 = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_PS3", EnumerationJNI.get_FMOD_OUTPUTTYPE_PS3());
	/** GameCube              - Native hardware output. (Default on GameCube) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_GC = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_GC", EnumerationJNI.get_FMOD_OUTPUTTYPE_GC());
	/** Xbox 360              - Native hardware output. (Default on Xbox 360) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_XBOX360 = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_XBOX360", EnumerationJNI.get_FMOD_OUTPUTTYPE_XBOX360());
	/** PSP                   - Native hardware output. (Default on PSP) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_PSP = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_PSP", EnumerationJNI.get_FMOD_OUTPUTTYPE_PSP());
	/** Wii			          - Native hardware output. (Default on Wii) */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_WII = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_WII", EnumerationJNI.get_FMOD_OUTPUTTYPE_WII());
	/** Maximum number of output types supported. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_MAX = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_MAX", EnumerationJNI.get_FMOD_OUTPUTTYPE_MAX());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_OUTPUTTYPE FMOD_OUTPUTTYPE_FORCEINT = new FMOD_OUTPUTTYPE("FMOD_OUTPUTTYPE_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*25);
	static
	{
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_AUTODETECT.asInt()), FMOD_OUTPUTTYPE_AUTODETECT);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_UNKNOWN.asInt()), FMOD_OUTPUTTYPE_UNKNOWN);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_NOSOUND.asInt()), FMOD_OUTPUTTYPE_NOSOUND);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_WAVWRITER.asInt()), FMOD_OUTPUTTYPE_WAVWRITER);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_NOSOUND_NRT.asInt()), FMOD_OUTPUTTYPE_NOSOUND_NRT);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_WAVWRITER_NRT.asInt()), FMOD_OUTPUTTYPE_WAVWRITER_NRT);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_DSOUND.asInt()), FMOD_OUTPUTTYPE_DSOUND);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_WINMM.asInt()), FMOD_OUTPUTTYPE_WINMM);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_OPENAL.asInt()), FMOD_OUTPUTTYPE_OPENAL);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_WASAPI.asInt()), FMOD_OUTPUTTYPE_WASAPI);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_ASIO.asInt()), FMOD_OUTPUTTYPE_ASIO);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_OSS.asInt()), FMOD_OUTPUTTYPE_OSS);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_ALSA.asInt()), FMOD_OUTPUTTYPE_ALSA);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_ESD.asInt()), FMOD_OUTPUTTYPE_ESD);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_SOUNDMANAGER.asInt()), FMOD_OUTPUTTYPE_SOUNDMANAGER);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_COREAUDIO.asInt()), FMOD_OUTPUTTYPE_COREAUDIO);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_XBOX.asInt()), FMOD_OUTPUTTYPE_XBOX);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_PS2.asInt()), FMOD_OUTPUTTYPE_PS2);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_PS3.asInt()), FMOD_OUTPUTTYPE_PS3);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_GC.asInt()), FMOD_OUTPUTTYPE_GC);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_XBOX360.asInt()), FMOD_OUTPUTTYPE_XBOX360);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_PSP.asInt()), FMOD_OUTPUTTYPE_PSP);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_WII.asInt()), FMOD_OUTPUTTYPE_WII);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_MAX.asInt()), FMOD_OUTPUTTYPE_MAX);
		VALUES.put(new Integer(FMOD_OUTPUTTYPE_FORCEINT.asInt()), FMOD_OUTPUTTYPE_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_OUTPUTTYPE(String name, int nativeValue)
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
		if(object instanceof FMOD_OUTPUTTYPE)
			return asInt() == ((FMOD_OUTPUTTYPE)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_OUTPUTTYPE)object).asInt();
	}


	/**
	 * Retrieve a FMOD_OUTPUTTYPE enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_OUTPUTTYPE enum field that correspond to the integer value
	 */
	public static FMOD_OUTPUTTYPE get(int nativeValue)
	{
		return (FMOD_OUTPUTTYPE)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_OUTPUTTYPE enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_OUTPUTTYPE enum field
	 * @return the FMOD_OUTPUTTYPE enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_OUTPUTTYPE get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_OUTPUTTYPE></code> in Java 1.5.
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