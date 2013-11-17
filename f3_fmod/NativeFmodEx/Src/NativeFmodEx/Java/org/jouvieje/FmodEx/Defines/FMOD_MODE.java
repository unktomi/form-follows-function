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

package org.jouvieje.FmodEx.Defines;

/**
 * <BR>
 *     Sound description bitfields, bitwise OR them together for loading and describing sounds.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     By default a sound will open as a static sound that is decompressed fully into memory to PCM. (ie equivalent of FMOD_CREATESAMPLE)<BR>
 *     To have a sound stream instead, use FMOD_CREATESTREAM, or use the wrapper function System::createStream.<BR>
 *     Some opening modes (ie FMOD_OPENUSER, FMOD_OPENMEMORY, FMOD_OPENMEMORY_POINT, FMOD_OPENRAW) will need extra information.<BR>
 *     This can be provided using the FMOD_CREATESOUNDEXINFO structure.<BR>
 * <BR>
 *     On Playstation 2, non VAG formats will default to FMOD_SOFTWARE if FMOD_HARDWARE is not specified.<BR>
 *     This is due to PS2 hardware not supporting PCM data.<BR>
 * <BR>
 *     Specifying FMOD_OPENMEMORY_POINT will POINT to your memory rather allocating its own sound buffers and duplicating it internally.<BR>
 *     <b><u>This means you cannot free the memory while FMOD is using it, until after Sound::release is called.</b></u><BR>
 *     With FMOD_OPENMEMORY_POINT, for PCM formats, only WAV, FSB, and RAW are supported.  For compressed formats, only those formats supported by FMOD_CREATECOMPRESSEDSAMPLE are supported.<BR>
 *     With FMOD_OPENMEMORY_POINT and FMOD_OPENRAW or PCM, if using them together, note that you must pad the data on each side by 16 bytes.  This is so fmod can modify the ends of the data for looping/interpolation/mixing purposes.  If a wav file, you will need to insert silence, and then reset loop points to stop the playback from playing that silence.<BR>
 * <BR>
 *     <b>Xbox 360 memory</b> On Xbox 360 Specifying FMOD_OPENMEMORY_POINT to a virtual memory address will cause FMOD_ERR_INVALID_ADDRESS<BR>
 *     to be returned.  Use physical memory only for this functionality.<BR>
 * <BR>
 *     FMOD_LOWMEM is used on a sound if you want to minimize the memory overhead, by having FMOD not allocate memory for certain<BR>
 *     features that are not likely to be used in a game environment.  These are :<BR>
 *     1. Sound::getName functionality is removed.  256 bytes per sound is saved.<BR>
 *     2. For a stream, a default sentence is not created, 4 bytes per subsound.  On a 2000 subsound FSB this can save 8kb for example.<BR>
 *        Sound::setSubSoundSentence can simply be used to set up a sentence as normal, System::playSound just wont play through the<BR>
 *        whole set of subsounds by default any more.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::createSound<BR>
 *     System::createStream<BR>
 *     Sound::setMode<BR>
 *     Sound::getMode<BR>
 *     Channel::setMode<BR>
 *     Channel::getMode<BR>
 *     Sound::set3DCustomRolloff<BR>
 *     Channel::set3DCustomRolloff<BR>
 *     Sound::getOpenState<BR>
 * 
 */
public interface FMOD_MODE
{
	/** FMOD_DEFAULT is a default sound type.  Equivalent to all the defaults listed below.  FMOD_LOOP_OFF, FMOD_2D, FMOD_HARDWARE. */
	public static final int FMOD_DEFAULT = 0x00000000;
	/** For non looping sounds. (DEFAULT).  Overrides FMOD_LOOP_NORMAL / FMOD_LOOP_BIDI. */
	public static final int FMOD_LOOP_OFF = 0x00000001;
	/** For forward looping sounds. */
	public static final int FMOD_LOOP_NORMAL = 0x00000002;
	/** For bidirectional looping sounds. (only works on software mixed static sounds). */
	public static final int FMOD_LOOP_BIDI = 0x00000004;
	/** Ignores any 3d processing. (DEFAULT). */
	public static final int FMOD_2D = 0x00000008;
	/** Makes the sound positionable in 3D.  Overrides FMOD_2D. */
	public static final int FMOD_3D = 0x00000010;
	/** Attempts to make sounds use hardware acceleration. (DEFAULT). */
	public static final int FMOD_HARDWARE = 0x00000020;
	/** Makes the sound be mixed by the FMOD CPU based software mixer.  Overrides FMOD_HARDWARE.  Use this for FFT, DSP, compressed sample support, 2D multi-speaker support and other software related features. */
	public static final int FMOD_SOFTWARE = 0x00000040;
	/** Decompress at runtime, streaming from the source provided (ie from disk).  Overrides FMOD_CREATESAMPLE and FMOD_CREATECOMPRESSEDSAMPLE.  Note a stream can only be played once at a time due to a stream only having 1 stream buffer and file handle.  Open multiple streams to have them play concurrently. */
	public static final int FMOD_CREATESTREAM = 0x00000080;
	/** Decompress at loadtime, decompressing or decoding whole file into memory as the target sample format (ie PCM).  Fastest for FMOD_SOFTWARE based playback and most flexible. */
	public static final int FMOD_CREATESAMPLE = 0x00000100;
	/** Load MP2, MP3, IMAADPCM or XMA into memory and leave it compressed.  During playback the FMOD software mixer will decode it in realtime as a 'compressed sample'.  Can only be used in combination with FMOD_SOFTWARE.  Overrides FMOD_CREATESAMPLE.  If the sound data is not ADPCM, MPEG or XMA it will behave as if it was created with FMOD_CREATESAMPLE and decode the sound into PCM. */
	public static final int FMOD_CREATECOMPRESSEDSAMPLE = 0x00000200;
	/** Opens a user created static sample or stream. Use FMOD_CREATESOUNDEXINFO to specify format and/or read callbacks.  If a user created 'sample' is created with no read callback, the sample will be empty.  Use Sound::lock and Sound::unlock to place sound data into the sound if this is the case. */
	public static final int FMOD_OPENUSER = 0x00000400;
	/** "name_or_data" will be interpreted as a pointer to memory instead of filename for creating sounds.  Use FMOD_CREATESOUNDEXINFO to specify length.  FMOD duplicates the memory into its own buffers.  Can be freed after open. */
	public static final int FMOD_OPENMEMORY = 0x00000800;
	/** "name_or_data" will be interpreted as a pointer to memory instead of filename for creating sounds.  Use FMOD_CREATESOUNDEXINFO to specify length.  This differs to FMOD_OPENMEMORY in that it uses the memory as is, without duplicating the memory into its own buffers.  FMOD_SOFTWARE only.  Doesn't work with FMOD_HARDWARE, as sound hardware cannot access main ram on a lot of platforms.  Cannot be freed after open, only after Sound::release.   Will not work if the data is compressed and FMOD_CREATECOMPRESSEDSAMPLE is not used. */
	public static final int FMOD_OPENMEMORY_POINT = 0x10000000;
	/** Will ignore file format and treat as raw pcm.  Use FMOD_CREATESOUNDEXINFO to specify format.  Requires at least defaultfrequency, numchannels and format to be specified before it will open.  Must be little endian data. */
	public static final int FMOD_OPENRAW = 0x00001000;
	/** Just open the file, dont prebuffer or read.  Good for fast opens for info, or when sound::readData is to be used. */
	public static final int FMOD_OPENONLY = 0x00002000;
	/** For System::createSound - for accurate Sound::getLength/Channel::setPosition on VBR MP3, and MOD/S3M/XM/IT/MIDI files.  Scans file first, so takes longer to open. FMOD_OPENONLY does not affect this. */
	public static final int FMOD_ACCURATETIME = 0x00004000;
	/** For corrupted / bad MP3 files.  This will search all the way through the file until it hits a valid MPEG header.  Normally only searches for 4k. */
	public static final int FMOD_MPEGSEARCH = 0x00008000;
	/** For opening sounds and getting streamed subsounds (seeking) asyncronously.  Use Sound::getOpenState to poll the state of the sound as it opens or retrieves the subsound in the background. */
	public static final int FMOD_NONBLOCKING = 0x00010000;
	/** Unique sound, can only be played one at a time */
	public static final int FMOD_UNIQUE = 0x00020000;
	/** Make the sound's position, velocity and orientation relative to the listener. */
	public static final int FMOD_3D_HEADRELATIVE = 0x00040000;
	/** Make the sound's position, velocity and orientation absolute (relative to the world). (DEFAULT) */
	public static final int FMOD_3D_WORLDRELATIVE = 0x00080000;
	/** This sound will follow the standard logarithmic rolloff model where mindistance = full volume, maxdistance = where sound stops attenuating, and rolloff is fixed according to the global rolloff factor.  (DEFAULT) */
	public static final int FMOD_3D_LOGROLLOFF = 0x00100000;
	/** This sound will follow a linear rolloff model where mindistance = full volume, maxdistance = silence.  Rolloffscale is ignored. */
	public static final int FMOD_3D_LINEARROLLOFF = 0x00200000;
	/** This sound will follow a rolloff model defined by Sound::set3DCustomRolloff / Channel::set3DCustomRolloff. */
	public static final int FMOD_3D_CUSTOMROLLOFF = 0x04000000;
	/** Is not affect by geometry occlusion.  If not specified in Sound::setMode, or Channel::setMode, the flag is cleared and it is affected by geometry again. */
	public static final int FMOD_3D_IGNOREGEOMETRY = 0x40000000;
	/** For CDDA sounds only - use ASPI instead of NTSCSI to access the specified CD/DVD device. */
	public static final int FMOD_CDDA_FORCEASPI = 0x00400000;
	/** For CDDA sounds only - perform jitter correction. Jitter correction helps produce a more accurate CDDA stream at the cost of more CPU time. */
	public static final int FMOD_CDDA_JITTERCORRECT = 0x00800000;
	/** Filename is double-byte unicode. */
	public static final int FMOD_UNICODE = 0x01000000;
	/** Skips id3v2/asf/etc tag checks when opening a sound, to reduce seek/read overhead when opening files (helps with CD performance). */
	public static final int FMOD_IGNORETAGS = 0x02000000;
	/** Removes some features from samples to give a lower memory overhead, like Sound::getName.  See remarks. */
	public static final int FMOD_LOWMEM = 0x08000000;
	/** Load sound into the secondary RAM of supported platform. On PS3, sounds will be loaded into RSX/VRAM. */
	public static final int FMOD_LOADSECONDARYRAM = 0x20000000;
	/** For sounds that start virtual (due to being quiet or low importance), instead of swapping back to audible, and playing at the correct offset according to time, this flag makes the sound play from the start. */
	public static final int FMOD_VIRTUAL_PLAYFROMSTART = 0x80000000;
}