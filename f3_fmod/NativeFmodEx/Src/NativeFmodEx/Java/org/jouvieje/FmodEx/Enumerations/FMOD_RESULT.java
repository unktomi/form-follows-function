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
 *     error codes.  Returned from every function.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 * 
 */
public class FMOD_RESULT implements Enumeration, Comparable
{
	/**  */
	public final static FMOD_RESULT FMOD_OK = new FMOD_RESULT("FMOD_OK", EnumerationJNI.get_FMOD_OK());
	/** Tried to call lock a second time before unlock was called. */
	public final static FMOD_RESULT FMOD_ERR_ALREADYLOCKED = new FMOD_RESULT("FMOD_ERR_ALREADYLOCKED", EnumerationJNI.get_FMOD_ERR_ALREADYLOCKED());
	/** Tried to call a function on a data type that does not allow this type of functionality (ie calling Sound::lock on a streaming sound). */
	public final static FMOD_RESULT FMOD_ERR_BADCOMMAND = new FMOD_RESULT("FMOD_ERR_BADCOMMAND", EnumerationJNI.get_FMOD_ERR_BADCOMMAND());
	/** Neither NTSCSI nor ASPI could be initialised. */
	public final static FMOD_RESULT FMOD_ERR_CDDA_DRIVERS = new FMOD_RESULT("FMOD_ERR_CDDA_DRIVERS", EnumerationJNI.get_FMOD_ERR_CDDA_DRIVERS());
	/** An error occurred while initialising the CDDA subsystem. */
	public final static FMOD_RESULT FMOD_ERR_CDDA_INIT = new FMOD_RESULT("FMOD_ERR_CDDA_INIT", EnumerationJNI.get_FMOD_ERR_CDDA_INIT());
	/** Couldn't find the specified device. */
	public final static FMOD_RESULT FMOD_ERR_CDDA_INVALID_DEVICE = new FMOD_RESULT("FMOD_ERR_CDDA_INVALID_DEVICE", EnumerationJNI.get_FMOD_ERR_CDDA_INVALID_DEVICE());
	/** No audio tracks on the specified disc. */
	public final static FMOD_RESULT FMOD_ERR_CDDA_NOAUDIO = new FMOD_RESULT("FMOD_ERR_CDDA_NOAUDIO", EnumerationJNI.get_FMOD_ERR_CDDA_NOAUDIO());
	/** No CD/DVD devices were found. */
	public final static FMOD_RESULT FMOD_ERR_CDDA_NODEVICES = new FMOD_RESULT("FMOD_ERR_CDDA_NODEVICES", EnumerationJNI.get_FMOD_ERR_CDDA_NODEVICES());
	/** No disc present in the specified drive. */
	public final static FMOD_RESULT FMOD_ERR_CDDA_NODISC = new FMOD_RESULT("FMOD_ERR_CDDA_NODISC", EnumerationJNI.get_FMOD_ERR_CDDA_NODISC());
	/** A CDDA read error occurred. */
	public final static FMOD_RESULT FMOD_ERR_CDDA_READ = new FMOD_RESULT("FMOD_ERR_CDDA_READ", EnumerationJNI.get_FMOD_ERR_CDDA_READ());
	/** Error trying to allocate a channel. */
	public final static FMOD_RESULT FMOD_ERR_CHANNEL_ALLOC = new FMOD_RESULT("FMOD_ERR_CHANNEL_ALLOC", EnumerationJNI.get_FMOD_ERR_CHANNEL_ALLOC());
	/** The specified channel has been reused to play another sound. */
	public final static FMOD_RESULT FMOD_ERR_CHANNEL_STOLEN = new FMOD_RESULT("FMOD_ERR_CHANNEL_STOLEN", EnumerationJNI.get_FMOD_ERR_CHANNEL_STOLEN());
	/** A Win32 COM related error occured. COM failed to initialize or a QueryInterface failed meaning a Windows codec or driver was not installed properly. */
	public final static FMOD_RESULT FMOD_ERR_COM = new FMOD_RESULT("FMOD_ERR_COM", EnumerationJNI.get_FMOD_ERR_COM());
	/** DMA Failure.  See debug output for more information. */
	public final static FMOD_RESULT FMOD_ERR_DMA = new FMOD_RESULT("FMOD_ERR_DMA", EnumerationJNI.get_FMOD_ERR_DMA());
	/** DSP connection error.  Connection possibly caused a cyclic dependancy. */
	public final static FMOD_RESULT FMOD_ERR_DSP_CONNECTION = new FMOD_RESULT("FMOD_ERR_DSP_CONNECTION", EnumerationJNI.get_FMOD_ERR_DSP_CONNECTION());
	/** DSP Format error.  A DSP unit may have attempted to connect to this network with the wrong format. */
	public final static FMOD_RESULT FMOD_ERR_DSP_FORMAT = new FMOD_RESULT("FMOD_ERR_DSP_FORMAT", EnumerationJNI.get_FMOD_ERR_DSP_FORMAT());
	/** DSP connection error.  Couldn't find the DSP unit specified. */
	public final static FMOD_RESULT FMOD_ERR_DSP_NOTFOUND = new FMOD_RESULT("FMOD_ERR_DSP_NOTFOUND", EnumerationJNI.get_FMOD_ERR_DSP_NOTFOUND());
	/** DSP error.  Cannot perform this operation while the network is in the middle of running.  This will most likely happen if a connection or disconnection is attempted in a DSP callback. */
	public final static FMOD_RESULT FMOD_ERR_DSP_RUNNING = new FMOD_RESULT("FMOD_ERR_DSP_RUNNING", EnumerationJNI.get_FMOD_ERR_DSP_RUNNING());
	/** DSP connection error.  The unit being connected to or disconnected should only have 1 input or output. */
	public final static FMOD_RESULT FMOD_ERR_DSP_TOOMANYCONNECTIONS = new FMOD_RESULT("FMOD_ERR_DSP_TOOMANYCONNECTIONS", EnumerationJNI.get_FMOD_ERR_DSP_TOOMANYCONNECTIONS());
	/** Error loading file. */
	public final static FMOD_RESULT FMOD_ERR_FILE_BAD = new FMOD_RESULT("FMOD_ERR_FILE_BAD", EnumerationJNI.get_FMOD_ERR_FILE_BAD());
	/** Couldn't perform seek operation.  This is a limitation of the medium (ie netstreams) or the file format. */
	public final static FMOD_RESULT FMOD_ERR_FILE_COULDNOTSEEK = new FMOD_RESULT("FMOD_ERR_FILE_COULDNOTSEEK", EnumerationJNI.get_FMOD_ERR_FILE_COULDNOTSEEK());
	/** Media was ejected while reading. */
	public final static FMOD_RESULT FMOD_ERR_FILE_DISKEJECTED = new FMOD_RESULT("FMOD_ERR_FILE_DISKEJECTED", EnumerationJNI.get_FMOD_ERR_FILE_DISKEJECTED());
	/** End of file unexpectedly reached while trying to read essential data (truncated data?). */
	public final static FMOD_RESULT FMOD_ERR_FILE_EOF = new FMOD_RESULT("FMOD_ERR_FILE_EOF", EnumerationJNI.get_FMOD_ERR_FILE_EOF());
	/** File not found. */
	public final static FMOD_RESULT FMOD_ERR_FILE_NOTFOUND = new FMOD_RESULT("FMOD_ERR_FILE_NOTFOUND", EnumerationJNI.get_FMOD_ERR_FILE_NOTFOUND());
	/** Unwanted file access occured. */
	public final static FMOD_RESULT FMOD_ERR_FILE_UNWANTED = new FMOD_RESULT("FMOD_ERR_FILE_UNWANTED", EnumerationJNI.get_FMOD_ERR_FILE_UNWANTED());
	/** Unsupported file or audio format. */
	public final static FMOD_RESULT FMOD_ERR_FORMAT = new FMOD_RESULT("FMOD_ERR_FORMAT", EnumerationJNI.get_FMOD_ERR_FORMAT());
	/** A HTTP error occurred. This is a catch-all for HTTP errors not listed elsewhere. */
	public final static FMOD_RESULT FMOD_ERR_HTTP = new FMOD_RESULT("FMOD_ERR_HTTP", EnumerationJNI.get_FMOD_ERR_HTTP());
	/** The specified resource requires authentication or is forbidden. */
	public final static FMOD_RESULT FMOD_ERR_HTTP_ACCESS = new FMOD_RESULT("FMOD_ERR_HTTP_ACCESS", EnumerationJNI.get_FMOD_ERR_HTTP_ACCESS());
	/** Proxy authentication is required to access the specified resource. */
	public final static FMOD_RESULT FMOD_ERR_HTTP_PROXY_AUTH = new FMOD_RESULT("FMOD_ERR_HTTP_PROXY_AUTH", EnumerationJNI.get_FMOD_ERR_HTTP_PROXY_AUTH());
	/** A HTTP server error occurred. */
	public final static FMOD_RESULT FMOD_ERR_HTTP_SERVER_ERROR = new FMOD_RESULT("FMOD_ERR_HTTP_SERVER_ERROR", EnumerationJNI.get_FMOD_ERR_HTTP_SERVER_ERROR());
	/** The HTTP request timed out. */
	public final static FMOD_RESULT FMOD_ERR_HTTP_TIMEOUT = new FMOD_RESULT("FMOD_ERR_HTTP_TIMEOUT", EnumerationJNI.get_FMOD_ERR_HTTP_TIMEOUT());
	/** FMOD was not initialized correctly to support this function. */
	public final static FMOD_RESULT FMOD_ERR_INITIALIZATION = new FMOD_RESULT("FMOD_ERR_INITIALIZATION", EnumerationJNI.get_FMOD_ERR_INITIALIZATION());
	/** Cannot call this command after System::init. */
	public final static FMOD_RESULT FMOD_ERR_INITIALIZED = new FMOD_RESULT("FMOD_ERR_INITIALIZED", EnumerationJNI.get_FMOD_ERR_INITIALIZED());
	/** An error occured that wasn't supposed to.  Contact support. */
	public final static FMOD_RESULT FMOD_ERR_INTERNAL = new FMOD_RESULT("FMOD_ERR_INTERNAL", EnumerationJNI.get_FMOD_ERR_INTERNAL());
	/** On Xbox 360, this memory address passed to FMOD must be physical, (ie allocated with XPhysicalAlloc.) */
	public final static FMOD_RESULT FMOD_ERR_INVALID_ADDRESS = new FMOD_RESULT("FMOD_ERR_INVALID_ADDRESS", EnumerationJNI.get_FMOD_ERR_INVALID_ADDRESS());
	/** Value passed in was a NaN, Inf or denormalized float. */
	public final static FMOD_RESULT FMOD_ERR_INVALID_FLOAT = new FMOD_RESULT("FMOD_ERR_INVALID_FLOAT", EnumerationJNI.get_FMOD_ERR_INVALID_FLOAT());
	/** An invalid object handle was used. */
	public final static FMOD_RESULT FMOD_ERR_INVALID_HANDLE = new FMOD_RESULT("FMOD_ERR_INVALID_HANDLE", EnumerationJNI.get_FMOD_ERR_INVALID_HANDLE());
	/** An invalid parameter was passed to this function. */
	public final static FMOD_RESULT FMOD_ERR_INVALID_PARAM = new FMOD_RESULT("FMOD_ERR_INVALID_PARAM", EnumerationJNI.get_FMOD_ERR_INVALID_PARAM());
	/** An invalid speaker was passed to this function based on the current speaker mode. */
	public final static FMOD_RESULT FMOD_ERR_INVALID_SPEAKER = new FMOD_RESULT("FMOD_ERR_INVALID_SPEAKER", EnumerationJNI.get_FMOD_ERR_INVALID_SPEAKER());
	/** The vectors passed in are not unit length, or perpendicular. */
	public final static FMOD_RESULT FMOD_ERR_INVALID_VECTOR = new FMOD_RESULT("FMOD_ERR_INVALID_VECTOR", EnumerationJNI.get_FMOD_ERR_INVALID_VECTOR());
	/** PS2 only.  fmodex.irx failed to initialize.  This is most likely because you forgot to load it. */
	public final static FMOD_RESULT FMOD_ERR_IRX = new FMOD_RESULT("FMOD_ERR_IRX", EnumerationJNI.get_FMOD_ERR_IRX());
	/** Reached maximum audible playback count for this sound's soundgroup. */
	public final static FMOD_RESULT FMOD_ERR_MAXAUDIBLE = new FMOD_RESULT("FMOD_ERR_MAXAUDIBLE", EnumerationJNI.get_FMOD_ERR_MAXAUDIBLE());
	/** Not enough memory or resources. */
	public final static FMOD_RESULT FMOD_ERR_MEMORY = new FMOD_RESULT("FMOD_ERR_MEMORY", EnumerationJNI.get_FMOD_ERR_MEMORY());
	/** Can't use FMOD_OPENMEMORY_POINT on non PCM source data, or non mp3/xma/adpcm data if FMOD_CREATECOMPRESSEDSAMPLE was used. */
	public final static FMOD_RESULT FMOD_ERR_MEMORY_CANTPOINT = new FMOD_RESULT("FMOD_ERR_MEMORY_CANTPOINT", EnumerationJNI.get_FMOD_ERR_MEMORY_CANTPOINT());
	/** PS2 only.  Not enough memory or resources on PlayStation 2 IOP ram. */
	public final static FMOD_RESULT FMOD_ERR_MEMORY_IOP = new FMOD_RESULT("FMOD_ERR_MEMORY_IOP", EnumerationJNI.get_FMOD_ERR_MEMORY_IOP());
	/** Not enough memory or resources on console sound ram. */
	public final static FMOD_RESULT FMOD_ERR_MEMORY_SRAM = new FMOD_RESULT("FMOD_ERR_MEMORY_SRAM", EnumerationJNI.get_FMOD_ERR_MEMORY_SRAM());
	/** Tried to call a command on a 3d sound when the command was meant for 2d sound. */
	public final static FMOD_RESULT FMOD_ERR_NEEDS2D = new FMOD_RESULT("FMOD_ERR_NEEDS2D", EnumerationJNI.get_FMOD_ERR_NEEDS2D());
	/** Tried to call a command on a 2d sound when the command was meant for 3d sound. */
	public final static FMOD_RESULT FMOD_ERR_NEEDS3D = new FMOD_RESULT("FMOD_ERR_NEEDS3D", EnumerationJNI.get_FMOD_ERR_NEEDS3D());
	/** Tried to use a feature that requires hardware support.  (ie trying to play a VAG compressed sound in software on PS2). */
	public final static FMOD_RESULT FMOD_ERR_NEEDSHARDWARE = new FMOD_RESULT("FMOD_ERR_NEEDSHARDWARE", EnumerationJNI.get_FMOD_ERR_NEEDSHARDWARE());
	/** Tried to use a feature that requires the software engine.  Software engine has either been turned off, or command was executed on a hardware channel which does not support this feature. */
	public final static FMOD_RESULT FMOD_ERR_NEEDSSOFTWARE = new FMOD_RESULT("FMOD_ERR_NEEDSSOFTWARE", EnumerationJNI.get_FMOD_ERR_NEEDSSOFTWARE());
	/** Couldn't connect to the specified host. */
	public final static FMOD_RESULT FMOD_ERR_NET_CONNECT = new FMOD_RESULT("FMOD_ERR_NET_CONNECT", EnumerationJNI.get_FMOD_ERR_NET_CONNECT());
	/** A socket error occurred.  This is a catch-all for socket-related errors not listed elsewhere. */
	public final static FMOD_RESULT FMOD_ERR_NET_SOCKET_ERROR = new FMOD_RESULT("FMOD_ERR_NET_SOCKET_ERROR", EnumerationJNI.get_FMOD_ERR_NET_SOCKET_ERROR());
	/** The specified URL couldn't be resolved. */
	public final static FMOD_RESULT FMOD_ERR_NET_URL = new FMOD_RESULT("FMOD_ERR_NET_URL", EnumerationJNI.get_FMOD_ERR_NET_URL());
	/** Operation on a non-blocking socket could not complete immediately. */
	public final static FMOD_RESULT FMOD_ERR_NET_WOULD_BLOCK = new FMOD_RESULT("FMOD_ERR_NET_WOULD_BLOCK", EnumerationJNI.get_FMOD_ERR_NET_WOULD_BLOCK());
	/** Operation could not be performed because specified sound is not ready. */
	public final static FMOD_RESULT FMOD_ERR_NOTREADY = new FMOD_RESULT("FMOD_ERR_NOTREADY", EnumerationJNI.get_FMOD_ERR_NOTREADY());
	/** Error initializing output device, but more specifically, the output device is already in use and cannot be reused. */
	public final static FMOD_RESULT FMOD_ERR_OUTPUT_ALLOCATED = new FMOD_RESULT("FMOD_ERR_OUTPUT_ALLOCATED", EnumerationJNI.get_FMOD_ERR_OUTPUT_ALLOCATED());
	/** Error creating hardware sound buffer. */
	public final static FMOD_RESULT FMOD_ERR_OUTPUT_CREATEBUFFER = new FMOD_RESULT("FMOD_ERR_OUTPUT_CREATEBUFFER", EnumerationJNI.get_FMOD_ERR_OUTPUT_CREATEBUFFER());
	/** A call to a standard soundcard driver failed, which could possibly mean a bug in the driver or resources were missing or exhausted. */
	public final static FMOD_RESULT FMOD_ERR_OUTPUT_DRIVERCALL = new FMOD_RESULT("FMOD_ERR_OUTPUT_DRIVERCALL", EnumerationJNI.get_FMOD_ERR_OUTPUT_DRIVERCALL());
	/** Error enumerating the available driver list. List may be inconsistent due to a recent device addition or removal. */
	public final static FMOD_RESULT FMOD_ERR_OUTPUT_ENUMERATION = new FMOD_RESULT("FMOD_ERR_OUTPUT_ENUMERATION", EnumerationJNI.get_FMOD_ERR_OUTPUT_ENUMERATION());
	/** Soundcard does not support the minimum features needed for this soundsystem (16bit stereo output). */
	public final static FMOD_RESULT FMOD_ERR_OUTPUT_FORMAT = new FMOD_RESULT("FMOD_ERR_OUTPUT_FORMAT", EnumerationJNI.get_FMOD_ERR_OUTPUT_FORMAT());
	/** Error initializing output device. */
	public final static FMOD_RESULT FMOD_ERR_OUTPUT_INIT = new FMOD_RESULT("FMOD_ERR_OUTPUT_INIT", EnumerationJNI.get_FMOD_ERR_OUTPUT_INIT());
	/** FMOD_HARDWARE was specified but the sound card does not have the resources nescessary to play it. */
	public final static FMOD_RESULT FMOD_ERR_OUTPUT_NOHARDWARE = new FMOD_RESULT("FMOD_ERR_OUTPUT_NOHARDWARE", EnumerationJNI.get_FMOD_ERR_OUTPUT_NOHARDWARE());
	/** Attempted to create a software sound but no software channels were specified in System::init. */
	public final static FMOD_RESULT FMOD_ERR_OUTPUT_NOSOFTWARE = new FMOD_RESULT("FMOD_ERR_OUTPUT_NOSOFTWARE", EnumerationJNI.get_FMOD_ERR_OUTPUT_NOSOFTWARE());
	/** Panning only works with mono or stereo sound sources. */
	public final static FMOD_RESULT FMOD_ERR_PAN = new FMOD_RESULT("FMOD_ERR_PAN", EnumerationJNI.get_FMOD_ERR_PAN());
	/** An unspecified error has been returned from a 3rd party plugin. */
	public final static FMOD_RESULT FMOD_ERR_PLUGIN = new FMOD_RESULT("FMOD_ERR_PLUGIN", EnumerationJNI.get_FMOD_ERR_PLUGIN());
	/** The number of allowed instances of a plugin has been exceeded. */
	public final static FMOD_RESULT FMOD_ERR_PLUGIN_INSTANCES = new FMOD_RESULT("FMOD_ERR_PLUGIN_INSTANCES", EnumerationJNI.get_FMOD_ERR_PLUGIN_INSTANCES());
	/** A requested output, dsp unit type or codec was not available. */
	public final static FMOD_RESULT FMOD_ERR_PLUGIN_MISSING = new FMOD_RESULT("FMOD_ERR_PLUGIN_MISSING", EnumerationJNI.get_FMOD_ERR_PLUGIN_MISSING());
	/** A resource that the plugin requires cannot be found. (ie the DLS file for MIDI playback) */
	public final static FMOD_RESULT FMOD_ERR_PLUGIN_RESOURCE = new FMOD_RESULT("FMOD_ERR_PLUGIN_RESOURCE", EnumerationJNI.get_FMOD_ERR_PLUGIN_RESOURCE());
	/** An error occured trying to initialize the recording device. */
	public final static FMOD_RESULT FMOD_ERR_RECORD = new FMOD_RESULT("FMOD_ERR_RECORD", EnumerationJNI.get_FMOD_ERR_RECORD());
	/** Specified Instance in FMOD_REVERB_PROPERTIES couldn't be set. Most likely because it is an invalid instance number, or another application has locked the EAX4 FX slot. */
	public final static FMOD_RESULT FMOD_ERR_REVERB_INSTANCE = new FMOD_RESULT("FMOD_ERR_REVERB_INSTANCE", EnumerationJNI.get_FMOD_ERR_REVERB_INSTANCE());
	/** This subsound is already being used by another sound, you cannot have more than one parent to a sound.  Null out the other parent's entry first. */
	public final static FMOD_RESULT FMOD_ERR_SUBSOUND_ALLOCATED = new FMOD_RESULT("FMOD_ERR_SUBSOUND_ALLOCATED", EnumerationJNI.get_FMOD_ERR_SUBSOUND_ALLOCATED());
	/** Shared subsounds cannot be replaced or moved from their parent stream, such as when the parent stream is an FSB file. */
	public final static FMOD_RESULT FMOD_ERR_SUBSOUND_CANTMOVE = new FMOD_RESULT("FMOD_ERR_SUBSOUND_CANTMOVE", EnumerationJNI.get_FMOD_ERR_SUBSOUND_CANTMOVE());
	/** The subsound's mode bits do not match with the parent sound's mode bits.  See documentation for function that it was called with. */
	public final static FMOD_RESULT FMOD_ERR_SUBSOUND_MODE = new FMOD_RESULT("FMOD_ERR_SUBSOUND_MODE", EnumerationJNI.get_FMOD_ERR_SUBSOUND_MODE());
	/** The error occured because the sound referenced contains subsounds.  The operation cannot be performed on a parent sound, or a parent sound was played without setting up a sentence first. */
	public final static FMOD_RESULT FMOD_ERR_SUBSOUNDS = new FMOD_RESULT("FMOD_ERR_SUBSOUNDS", EnumerationJNI.get_FMOD_ERR_SUBSOUNDS());
	/** The specified tag could not be found or there are no tags. */
	public final static FMOD_RESULT FMOD_ERR_TAGNOTFOUND = new FMOD_RESULT("FMOD_ERR_TAGNOTFOUND", EnumerationJNI.get_FMOD_ERR_TAGNOTFOUND());
	/** The sound created exceeds the allowable input channel count.  This can be increased using the maxinputchannels parameter in System::setSoftwareFormat. */
	public final static FMOD_RESULT FMOD_ERR_TOOMANYCHANNELS = new FMOD_RESULT("FMOD_ERR_TOOMANYCHANNELS", EnumerationJNI.get_FMOD_ERR_TOOMANYCHANNELS());
	/** Something in FMOD hasn't been implemented when it should be! contact support! */
	public final static FMOD_RESULT FMOD_ERR_UNIMPLEMENTED = new FMOD_RESULT("FMOD_ERR_UNIMPLEMENTED", EnumerationJNI.get_FMOD_ERR_UNIMPLEMENTED());
	/** This command failed because System::init or System::setDriver was not called. */
	public final static FMOD_RESULT FMOD_ERR_UNINITIALIZED = new FMOD_RESULT("FMOD_ERR_UNINITIALIZED", EnumerationJNI.get_FMOD_ERR_UNINITIALIZED());
	/** A command issued was not supported by this object.  Possibly a plugin without certain callbacks specified. */
	public final static FMOD_RESULT FMOD_ERR_UNSUPPORTED = new FMOD_RESULT("FMOD_ERR_UNSUPPORTED", EnumerationJNI.get_FMOD_ERR_UNSUPPORTED());
	/** An error caused by System::update occured. */
	public final static FMOD_RESULT FMOD_ERR_UPDATE = new FMOD_RESULT("FMOD_ERR_UPDATE", EnumerationJNI.get_FMOD_ERR_UPDATE());
	/** The version number of this file format is not supported. */
	public final static FMOD_RESULT FMOD_ERR_VERSION = new FMOD_RESULT("FMOD_ERR_VERSION", EnumerationJNI.get_FMOD_ERR_VERSION());
	/** An Event failed to be retrieved, most likely due to 'just fail' being specified as the max playbacks behavior. */
	public final static FMOD_RESULT FMOD_ERR_EVENT_FAILED = new FMOD_RESULT("FMOD_ERR_EVENT_FAILED", EnumerationJNI.get_FMOD_ERR_EVENT_FAILED());
	/** Can't execute this command on an EVENT_INFOONLY event. */
	public final static FMOD_RESULT FMOD_ERR_EVENT_INFOONLY = new FMOD_RESULT("FMOD_ERR_EVENT_INFOONLY", EnumerationJNI.get_FMOD_ERR_EVENT_INFOONLY());
	/** An error occured that wasn't supposed to.  See debug log for reason. */
	public final static FMOD_RESULT FMOD_ERR_EVENT_INTERNAL = new FMOD_RESULT("FMOD_ERR_EVENT_INTERNAL", EnumerationJNI.get_FMOD_ERR_EVENT_INTERNAL());
	/** Event failed because 'Max streams' was hit when FMOD_INIT_FAIL_ON_MAXSTREAMS was specified. */
	public final static FMOD_RESULT FMOD_ERR_EVENT_MAXSTREAMS = new FMOD_RESULT("FMOD_ERR_EVENT_MAXSTREAMS", EnumerationJNI.get_FMOD_ERR_EVENT_MAXSTREAMS());
	/** FSB mismatches the FEV it was compiled with or FEV was built for a different platform. */
	public final static FMOD_RESULT FMOD_ERR_EVENT_MISMATCH = new FMOD_RESULT("FMOD_ERR_EVENT_MISMATCH", EnumerationJNI.get_FMOD_ERR_EVENT_MISMATCH());
	/** A category with the same name already exists. */
	public final static FMOD_RESULT FMOD_ERR_EVENT_NAMECONFLICT = new FMOD_RESULT("FMOD_ERR_EVENT_NAMECONFLICT", EnumerationJNI.get_FMOD_ERR_EVENT_NAMECONFLICT());
	/** The requested event, event group, event category or event property could not be found. */
	public final static FMOD_RESULT FMOD_ERR_EVENT_NOTFOUND = new FMOD_RESULT("FMOD_ERR_EVENT_NOTFOUND", EnumerationJNI.get_FMOD_ERR_EVENT_NOTFOUND());
	/** Makes sure this enum is signed 32bit. */
	public final static FMOD_RESULT FMOD_RESULT_FORCEINT = new FMOD_RESULT("FMOD_RESULT_FORCEINT", 65536);

	private final static HashMap VALUES = new HashMap(2*89);
	static
	{
		VALUES.put(new Integer(FMOD_OK.asInt()), FMOD_OK);
		VALUES.put(new Integer(FMOD_ERR_ALREADYLOCKED.asInt()), FMOD_ERR_ALREADYLOCKED);
		VALUES.put(new Integer(FMOD_ERR_BADCOMMAND.asInt()), FMOD_ERR_BADCOMMAND);
		VALUES.put(new Integer(FMOD_ERR_CDDA_DRIVERS.asInt()), FMOD_ERR_CDDA_DRIVERS);
		VALUES.put(new Integer(FMOD_ERR_CDDA_INIT.asInt()), FMOD_ERR_CDDA_INIT);
		VALUES.put(new Integer(FMOD_ERR_CDDA_INVALID_DEVICE.asInt()), FMOD_ERR_CDDA_INVALID_DEVICE);
		VALUES.put(new Integer(FMOD_ERR_CDDA_NOAUDIO.asInt()), FMOD_ERR_CDDA_NOAUDIO);
		VALUES.put(new Integer(FMOD_ERR_CDDA_NODEVICES.asInt()), FMOD_ERR_CDDA_NODEVICES);
		VALUES.put(new Integer(FMOD_ERR_CDDA_NODISC.asInt()), FMOD_ERR_CDDA_NODISC);
		VALUES.put(new Integer(FMOD_ERR_CDDA_READ.asInt()), FMOD_ERR_CDDA_READ);
		VALUES.put(new Integer(FMOD_ERR_CHANNEL_ALLOC.asInt()), FMOD_ERR_CHANNEL_ALLOC);
		VALUES.put(new Integer(FMOD_ERR_CHANNEL_STOLEN.asInt()), FMOD_ERR_CHANNEL_STOLEN);
		VALUES.put(new Integer(FMOD_ERR_COM.asInt()), FMOD_ERR_COM);
		VALUES.put(new Integer(FMOD_ERR_DMA.asInt()), FMOD_ERR_DMA);
		VALUES.put(new Integer(FMOD_ERR_DSP_CONNECTION.asInt()), FMOD_ERR_DSP_CONNECTION);
		VALUES.put(new Integer(FMOD_ERR_DSP_FORMAT.asInt()), FMOD_ERR_DSP_FORMAT);
		VALUES.put(new Integer(FMOD_ERR_DSP_NOTFOUND.asInt()), FMOD_ERR_DSP_NOTFOUND);
		VALUES.put(new Integer(FMOD_ERR_DSP_RUNNING.asInt()), FMOD_ERR_DSP_RUNNING);
		VALUES.put(new Integer(FMOD_ERR_DSP_TOOMANYCONNECTIONS.asInt()), FMOD_ERR_DSP_TOOMANYCONNECTIONS);
		VALUES.put(new Integer(FMOD_ERR_FILE_BAD.asInt()), FMOD_ERR_FILE_BAD);
		VALUES.put(new Integer(FMOD_ERR_FILE_COULDNOTSEEK.asInt()), FMOD_ERR_FILE_COULDNOTSEEK);
		VALUES.put(new Integer(FMOD_ERR_FILE_DISKEJECTED.asInt()), FMOD_ERR_FILE_DISKEJECTED);
		VALUES.put(new Integer(FMOD_ERR_FILE_EOF.asInt()), FMOD_ERR_FILE_EOF);
		VALUES.put(new Integer(FMOD_ERR_FILE_NOTFOUND.asInt()), FMOD_ERR_FILE_NOTFOUND);
		VALUES.put(new Integer(FMOD_ERR_FILE_UNWANTED.asInt()), FMOD_ERR_FILE_UNWANTED);
		VALUES.put(new Integer(FMOD_ERR_FORMAT.asInt()), FMOD_ERR_FORMAT);
		VALUES.put(new Integer(FMOD_ERR_HTTP.asInt()), FMOD_ERR_HTTP);
		VALUES.put(new Integer(FMOD_ERR_HTTP_ACCESS.asInt()), FMOD_ERR_HTTP_ACCESS);
		VALUES.put(new Integer(FMOD_ERR_HTTP_PROXY_AUTH.asInt()), FMOD_ERR_HTTP_PROXY_AUTH);
		VALUES.put(new Integer(FMOD_ERR_HTTP_SERVER_ERROR.asInt()), FMOD_ERR_HTTP_SERVER_ERROR);
		VALUES.put(new Integer(FMOD_ERR_HTTP_TIMEOUT.asInt()), FMOD_ERR_HTTP_TIMEOUT);
		VALUES.put(new Integer(FMOD_ERR_INITIALIZATION.asInt()), FMOD_ERR_INITIALIZATION);
		VALUES.put(new Integer(FMOD_ERR_INITIALIZED.asInt()), FMOD_ERR_INITIALIZED);
		VALUES.put(new Integer(FMOD_ERR_INTERNAL.asInt()), FMOD_ERR_INTERNAL);
		VALUES.put(new Integer(FMOD_ERR_INVALID_ADDRESS.asInt()), FMOD_ERR_INVALID_ADDRESS);
		VALUES.put(new Integer(FMOD_ERR_INVALID_FLOAT.asInt()), FMOD_ERR_INVALID_FLOAT);
		VALUES.put(new Integer(FMOD_ERR_INVALID_HANDLE.asInt()), FMOD_ERR_INVALID_HANDLE);
		VALUES.put(new Integer(FMOD_ERR_INVALID_PARAM.asInt()), FMOD_ERR_INVALID_PARAM);
		VALUES.put(new Integer(FMOD_ERR_INVALID_SPEAKER.asInt()), FMOD_ERR_INVALID_SPEAKER);
		VALUES.put(new Integer(FMOD_ERR_INVALID_VECTOR.asInt()), FMOD_ERR_INVALID_VECTOR);
		VALUES.put(new Integer(FMOD_ERR_IRX.asInt()), FMOD_ERR_IRX);
		VALUES.put(new Integer(FMOD_ERR_MAXAUDIBLE.asInt()), FMOD_ERR_MAXAUDIBLE);
		VALUES.put(new Integer(FMOD_ERR_MEMORY.asInt()), FMOD_ERR_MEMORY);
		VALUES.put(new Integer(FMOD_ERR_MEMORY_CANTPOINT.asInt()), FMOD_ERR_MEMORY_CANTPOINT);
		VALUES.put(new Integer(FMOD_ERR_MEMORY_IOP.asInt()), FMOD_ERR_MEMORY_IOP);
		VALUES.put(new Integer(FMOD_ERR_MEMORY_SRAM.asInt()), FMOD_ERR_MEMORY_SRAM);
		VALUES.put(new Integer(FMOD_ERR_NEEDS2D.asInt()), FMOD_ERR_NEEDS2D);
		VALUES.put(new Integer(FMOD_ERR_NEEDS3D.asInt()), FMOD_ERR_NEEDS3D);
		VALUES.put(new Integer(FMOD_ERR_NEEDSHARDWARE.asInt()), FMOD_ERR_NEEDSHARDWARE);
		VALUES.put(new Integer(FMOD_ERR_NEEDSSOFTWARE.asInt()), FMOD_ERR_NEEDSSOFTWARE);
		VALUES.put(new Integer(FMOD_ERR_NET_CONNECT.asInt()), FMOD_ERR_NET_CONNECT);
		VALUES.put(new Integer(FMOD_ERR_NET_SOCKET_ERROR.asInt()), FMOD_ERR_NET_SOCKET_ERROR);
		VALUES.put(new Integer(FMOD_ERR_NET_URL.asInt()), FMOD_ERR_NET_URL);
		VALUES.put(new Integer(FMOD_ERR_NET_WOULD_BLOCK.asInt()), FMOD_ERR_NET_WOULD_BLOCK);
		VALUES.put(new Integer(FMOD_ERR_NOTREADY.asInt()), FMOD_ERR_NOTREADY);
		VALUES.put(new Integer(FMOD_ERR_OUTPUT_ALLOCATED.asInt()), FMOD_ERR_OUTPUT_ALLOCATED);
		VALUES.put(new Integer(FMOD_ERR_OUTPUT_CREATEBUFFER.asInt()), FMOD_ERR_OUTPUT_CREATEBUFFER);
		VALUES.put(new Integer(FMOD_ERR_OUTPUT_DRIVERCALL.asInt()), FMOD_ERR_OUTPUT_DRIVERCALL);
		VALUES.put(new Integer(FMOD_ERR_OUTPUT_ENUMERATION.asInt()), FMOD_ERR_OUTPUT_ENUMERATION);
		VALUES.put(new Integer(FMOD_ERR_OUTPUT_FORMAT.asInt()), FMOD_ERR_OUTPUT_FORMAT);
		VALUES.put(new Integer(FMOD_ERR_OUTPUT_INIT.asInt()), FMOD_ERR_OUTPUT_INIT);
		VALUES.put(new Integer(FMOD_ERR_OUTPUT_NOHARDWARE.asInt()), FMOD_ERR_OUTPUT_NOHARDWARE);
		VALUES.put(new Integer(FMOD_ERR_OUTPUT_NOSOFTWARE.asInt()), FMOD_ERR_OUTPUT_NOSOFTWARE);
		VALUES.put(new Integer(FMOD_ERR_PAN.asInt()), FMOD_ERR_PAN);
		VALUES.put(new Integer(FMOD_ERR_PLUGIN.asInt()), FMOD_ERR_PLUGIN);
		VALUES.put(new Integer(FMOD_ERR_PLUGIN_INSTANCES.asInt()), FMOD_ERR_PLUGIN_INSTANCES);
		VALUES.put(new Integer(FMOD_ERR_PLUGIN_MISSING.asInt()), FMOD_ERR_PLUGIN_MISSING);
		VALUES.put(new Integer(FMOD_ERR_PLUGIN_RESOURCE.asInt()), FMOD_ERR_PLUGIN_RESOURCE);
		VALUES.put(new Integer(FMOD_ERR_RECORD.asInt()), FMOD_ERR_RECORD);
		VALUES.put(new Integer(FMOD_ERR_REVERB_INSTANCE.asInt()), FMOD_ERR_REVERB_INSTANCE);
		VALUES.put(new Integer(FMOD_ERR_SUBSOUND_ALLOCATED.asInt()), FMOD_ERR_SUBSOUND_ALLOCATED);
		VALUES.put(new Integer(FMOD_ERR_SUBSOUND_CANTMOVE.asInt()), FMOD_ERR_SUBSOUND_CANTMOVE);
		VALUES.put(new Integer(FMOD_ERR_SUBSOUND_MODE.asInt()), FMOD_ERR_SUBSOUND_MODE);
		VALUES.put(new Integer(FMOD_ERR_SUBSOUNDS.asInt()), FMOD_ERR_SUBSOUNDS);
		VALUES.put(new Integer(FMOD_ERR_TAGNOTFOUND.asInt()), FMOD_ERR_TAGNOTFOUND);
		VALUES.put(new Integer(FMOD_ERR_TOOMANYCHANNELS.asInt()), FMOD_ERR_TOOMANYCHANNELS);
		VALUES.put(new Integer(FMOD_ERR_UNIMPLEMENTED.asInt()), FMOD_ERR_UNIMPLEMENTED);
		VALUES.put(new Integer(FMOD_ERR_UNINITIALIZED.asInt()), FMOD_ERR_UNINITIALIZED);
		VALUES.put(new Integer(FMOD_ERR_UNSUPPORTED.asInt()), FMOD_ERR_UNSUPPORTED);
		VALUES.put(new Integer(FMOD_ERR_UPDATE.asInt()), FMOD_ERR_UPDATE);
		VALUES.put(new Integer(FMOD_ERR_VERSION.asInt()), FMOD_ERR_VERSION);
		VALUES.put(new Integer(FMOD_ERR_EVENT_FAILED.asInt()), FMOD_ERR_EVENT_FAILED);
		VALUES.put(new Integer(FMOD_ERR_EVENT_INFOONLY.asInt()), FMOD_ERR_EVENT_INFOONLY);
		VALUES.put(new Integer(FMOD_ERR_EVENT_INTERNAL.asInt()), FMOD_ERR_EVENT_INTERNAL);
		VALUES.put(new Integer(FMOD_ERR_EVENT_MAXSTREAMS.asInt()), FMOD_ERR_EVENT_MAXSTREAMS);
		VALUES.put(new Integer(FMOD_ERR_EVENT_MISMATCH.asInt()), FMOD_ERR_EVENT_MISMATCH);
		VALUES.put(new Integer(FMOD_ERR_EVENT_NAMECONFLICT.asInt()), FMOD_ERR_EVENT_NAMECONFLICT);
		VALUES.put(new Integer(FMOD_ERR_EVENT_NOTFOUND.asInt()), FMOD_ERR_EVENT_NOTFOUND);
		VALUES.put(new Integer(FMOD_RESULT_FORCEINT.asInt()), FMOD_RESULT_FORCEINT);
	}

	private final String name;
	private final int nativeValue;
	private FMOD_RESULT(String name, int nativeValue)
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
		if(object instanceof FMOD_RESULT)
			return asInt() == ((FMOD_RESULT)object).asInt();
		return false;
	}
	public int compareTo(Object object)
	{
		return asInt() - ((FMOD_RESULT)object).asInt();
	}


	/**
	 * Retrieve a FMOD_RESULT enum field with his integer value
	 * @param nativeValue the integer value of the field to retrieve
	 * @return the FMOD_RESULT enum field that correspond to the integer value
	 */
	public static FMOD_RESULT get(int nativeValue)
	{
		return (FMOD_RESULT)VALUES.get(new Integer(nativeValue));
	}

	/**
	 * Retrieve a FMOD_RESULT enum field from a Pointer
	 * @param pointer a pointer holding an FMOD_RESULT enum field
	 * @return the FMOD_RESULT enum field that correspond to the enum field in the pointer
	 */
	public static FMOD_RESULT get(Pointer pointer)
	{
		return get(pointer.asInt());
	}

	/**
	 * @return an <code>Iterator</code> over the elements in this enumeration.<BR>
	 * Can be cast to <code>Iterator<FMOD_RESULT></code> in Java 1.5.
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