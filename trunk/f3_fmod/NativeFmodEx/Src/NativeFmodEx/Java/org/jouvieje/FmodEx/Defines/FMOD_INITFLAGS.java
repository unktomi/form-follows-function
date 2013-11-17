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
 *     Initialization flags.  Use them with System::init in the flags parameter to change various behavior.<BR>
 * <BR>
 *     <BR><U><B>Remarks</B></U><BR><BR>
 *     Use System::setAdvancedSettings to adjust settings for some of the features that are enabled by these flags.<BR>
 * <BR>
 *     <BR><U><B>Platforms Supported</B></U><BR><BR>
 *     Win32, Win64, Linux, Linux64, Macintosh, Xbox, Xbox360, PlayStation 2, GameCube, PlayStation Portable, PlayStation 3, Wii, Solaris<BR>
 * <BR>
 *     <BR><U><B>See Also</B></U><BR><BR>
 *     System::init<BR>
 *     System::update<BR>
 *     System::setAdvancedSettings<BR>
 *     Channel::set3DOcclusion<BR>
 * 
 */
public interface FMOD_INITFLAGS
{
	/** All platforms - Initialize normally */
	public static final int FMOD_INIT_NORMAL = 0x00000000;
	/** All platforms - No stream thread is created internally.  Streams are driven from System::update.  Mainly used with non-realtime outputs. */
	public static final int FMOD_INIT_STREAM_FROM_UPDATE = 0x00000001;
	/** All platforms - FMOD will treat +X as right, +Y as up and +Z as backwards (towards you). */
	public static final int FMOD_INIT_3D_RIGHTHANDED = 0x00000002;
	/** All platforms - Disable software mixer to save memory.  Anything created with FMOD_SOFTWARE will fail and DSP will not work. */
	public static final int FMOD_INIT_SOFTWARE_DISABLE = 0x00000004;
	/** All platforms - All FMOD_SOFTWARE with FMOD_3D based voices will add a software lowpass filter effect into the DSP chain which is automatically used when Channel::set3DOcclusion is used or the geometry API. */
	public static final int FMOD_INIT_SOFTWARE_OCCLUSION = 0x00000008;
	/** All platforms - All FMOD_SOFTWARE with FMOD_3D based voices will add a software lowpass filter effect into the DSP chain which causes sounds to sound duller when the sound goes behind the listener.  Use System::setAdvancedSettings to adjust cutoff frequency. */
	public static final int FMOD_INIT_SOFTWARE_HRTF = 0x00000010;
	/** All platforms - SFX reverb is run using 22/24khz delay buffers, halving the memory required. */
	public static final int FMOD_INIT_SOFTWARE_REVERB_LOWMEM = 0x00000040;
	/** All platforms - Enable TCP/IP based host which allows FMOD Designer or FMOD Profiler to connect to it, and view memory, CPU and the DSP network graph in real-time. */
	public static final int FMOD_INIT_ENABLE_PROFILE = 0x00000020;
	/** All platforms - Any sounds that are 0 volume will go virtual and not be processed except for having their positions updated virtually.  Use System::setAdvancedSettings to adjust what volume besides zero to switch to virtual at. */
	public static final int FMOD_INIT_VOL0_BECOMES_VIRTUAL = 0x00000080;
	/** Win32 Vista only - for WASAPI output - Enable exclusive access to hardware, lower latency at the expense of excluding other applications from accessing the audio hardware. */
	public static final int FMOD_INIT_WASAPI_EXCLUSIVE = 0x00000100;
	/** Win32 only - for DirectSound output - FMOD_HARDWARE | FMOD_3D buffers use simple stereo panning/doppler/attenuation when 3D hardware acceleration is not present. */
	public static final int FMOD_INIT_DSOUND_HRTFNONE = 0x00000200;
	/** Win32 only - for DirectSound output - FMOD_HARDWARE | FMOD_3D buffers use a slightly higher quality algorithm when 3D hardware acceleration is not present. */
	public static final int FMOD_INIT_DSOUND_HRTFLIGHT = 0x00000400;
	/** Win32 only - for DirectSound output - FMOD_HARDWARE | FMOD_3D buffers use full quality 3D playback when 3d hardware acceleration is not present. */
	public static final int FMOD_INIT_DSOUND_HRTFFULL = 0x00000800;
	/** PS2 only - Disable reverb on CORE 0 to regain 256k SRAM. */
	public static final int FMOD_INIT_PS2_DISABLECORE0REVERB = 0x00010000;
	/** PS2 only - Disable reverb on CORE 1 to regain 256k SRAM. */
	public static final int FMOD_INIT_PS2_DISABLECORE1REVERB = 0x00020000;
	/** PS2 only - Disable FMOD's usage of the scratchpad. */
	public static final int FMOD_INIT_PS2_DONTUSESCRATCHPAD = 0x00040000;
	/** PS2 only - Changes FMOD from using SPU DMA channel 0 for software mixing, and 1 for sound data upload/file streaming, to 1 and 0 respectively. */
	public static final int FMOD_INIT_PS2_SWAPDMACHANNELS = 0x00080000;
	/** PS3 only - Prefer DTS over Dolby Digital if both are supported. Note: 8 and 6 channel LPCM is always preferred over both DTS and Dolby Digital. */
	public static final int FMOD_INIT_PS3_PREFERDTS = 0x00800000;
	/** PS3 only - Force PS3 system output mode to 2 channel LPCM. */
	public static final int FMOD_INIT_PS3_FORCE2CHLPCM = 0x01000000;
	/** Xbox only - By default DirectSound attenuates all sound by 6db to avoid clipping/distortion.  CAUTION.  If you use this flag you are responsible for the final mix to make sure clipping / distortion doesn't happen. */
	public static final int FMOD_INIT_XBOX_REMOVEHEADROOM = 0x00100000;
	/** Xbox 360 only - The "music" channelgroup which by default pauses when custom 360 dashboard music is played, can be changed to mute (therefore continues playing) instead of pausing, by using this flag. */
	public static final int FMOD_INIT_360_MUSICMUTENOTPAUSE = 0x00200000;
	/** Win32/Wii/PS3/Xbox/Xbox 360 - FMOD Mixer thread is woken up to do a mix when System::update is called rather than waking periodically on its own timer. */
	public static final int FMOD_INIT_SYNCMIXERWITHUPDATE = 0x00400000;
}