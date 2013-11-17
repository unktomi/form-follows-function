/**
 * 				NativeFmodEx Project
 *
 * Want to use FMOD Ex API (www.fmod.org) in the Java language ? NativeFmodEx is made for you.
 * Copyright © 2005-2008 Jérôme JOUVIE (Jouvieje)
 *
 * Created on 23 feb. 2005
 * @version file v1.4.3
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

class EnumerationJNI
{
	static
	{
		if(!Init.isLibrariesLoaded())
		{
			throw new RuntimeException("Libraries not loaded ! Use Init.loadLibraries() before using NativeFmodEx.");
		}
	}

				/* FMOD_DSP_TYPE */

	protected final static native int get_FMOD_DSP_TYPE_UNKNOWN();
	protected final static native int get_FMOD_DSP_TYPE_MIXER();
	protected final static native int get_FMOD_DSP_TYPE_OSCILLATOR();
	protected final static native int get_FMOD_DSP_TYPE_LOWPASS();
	protected final static native int get_FMOD_DSP_TYPE_ITLOWPASS();
	protected final static native int get_FMOD_DSP_TYPE_HIGHPASS();
	protected final static native int get_FMOD_DSP_TYPE_ECHO();
	protected final static native int get_FMOD_DSP_TYPE_FLANGE();
	protected final static native int get_FMOD_DSP_TYPE_DISTORTION();
	protected final static native int get_FMOD_DSP_TYPE_NORMALIZE();
	protected final static native int get_FMOD_DSP_TYPE_PARAMEQ();
	protected final static native int get_FMOD_DSP_TYPE_PITCHSHIFT();
	protected final static native int get_FMOD_DSP_TYPE_CHORUS();
	protected final static native int get_FMOD_DSP_TYPE_REVERB();
	protected final static native int get_FMOD_DSP_TYPE_VSTPLUGIN();
	protected final static native int get_FMOD_DSP_TYPE_WINAMPPLUGIN();
	protected final static native int get_FMOD_DSP_TYPE_ITECHO();
	protected final static native int get_FMOD_DSP_TYPE_COMPRESSOR();
	protected final static native int get_FMOD_DSP_TYPE_SFXREVERB();
	protected final static native int get_FMOD_DSP_TYPE_LOWPASS_SIMPLE();

				/* FMOD_DSP_OSCILLATOR */

	protected final static native int get_FMOD_DSP_OSCILLATOR_TYPE();
	protected final static native int get_FMOD_DSP_OSCILLATOR_RATE();

				/* FMOD_DSP_LOWPASS */

	protected final static native int get_FMOD_DSP_LOWPASS_CUTOFF();
	protected final static native int get_FMOD_DSP_LOWPASS_RESONANCE();

				/* FMOD_DSP_ITLOWPASS */

	protected final static native int get_FMOD_DSP_ITLOWPASS_CUTOFF();
	protected final static native int get_FMOD_DSP_ITLOWPASS_RESONANCE();

				/* FMOD_DSP_HIGHPASS */

	protected final static native int get_FMOD_DSP_HIGHPASS_CUTOFF();
	protected final static native int get_FMOD_DSP_HIGHPASS_RESONANCE();

				/* FMOD_DSP_ECHO */

	protected final static native int get_FMOD_DSP_ECHO_DELAY();
	protected final static native int get_FMOD_DSP_ECHO_DECAYRATIO();
	protected final static native int get_FMOD_DSP_ECHO_MAXCHANNELS();
	protected final static native int get_FMOD_DSP_ECHO_DRYMIX();
	protected final static native int get_FMOD_DSP_ECHO_WETMIX();

				/* FMOD_DSP_FLANGE */

	protected final static native int get_FMOD_DSP_FLANGE_DRYMIX();
	protected final static native int get_FMOD_DSP_FLANGE_WETMIX();
	protected final static native int get_FMOD_DSP_FLANGE_DEPTH();
	protected final static native int get_FMOD_DSP_FLANGE_RATE();

				/* FMOD_DSP_DISTORTION */

	protected final static native int get_FMOD_DSP_DISTORTION_LEVEL();

				/* FMOD_DSP_NORMALIZE */

	protected final static native int get_FMOD_DSP_NORMALIZE_FADETIME();
	protected final static native int get_FMOD_DSP_NORMALIZE_THRESHHOLD();
	protected final static native int get_FMOD_DSP_NORMALIZE_MAXAMP();

				/* FMOD_DSP_PARAMEQ */

	protected final static native int get_FMOD_DSP_PARAMEQ_CENTER();
	protected final static native int get_FMOD_DSP_PARAMEQ_BANDWIDTH();
	protected final static native int get_FMOD_DSP_PARAMEQ_GAIN();

				/* FMOD_DSP_PITCHSHIFT */

	protected final static native int get_FMOD_DSP_PITCHSHIFT_PITCH();
	protected final static native int get_FMOD_DSP_PITCHSHIFT_FFTSIZE();
	protected final static native int get_FMOD_DSP_PITCHSHIFT_OVERLAP();
	protected final static native int get_FMOD_DSP_PITCHSHIFT_MAXCHANNELS();

				/* FMOD_DSP_CHORUS */

	protected final static native int get_FMOD_DSP_CHORUS_DRYMIX();
	protected final static native int get_FMOD_DSP_CHORUS_WETMIX1();
	protected final static native int get_FMOD_DSP_CHORUS_WETMIX2();
	protected final static native int get_FMOD_DSP_CHORUS_WETMIX3();
	protected final static native int get_FMOD_DSP_CHORUS_DELAY();
	protected final static native int get_FMOD_DSP_CHORUS_RATE();
	protected final static native int get_FMOD_DSP_CHORUS_DEPTH();
	protected final static native int get_FMOD_DSP_CHORUS_FEEDBACK();

				/* FMOD_DSP_REVERB */

	protected final static native int get_FMOD_DSP_REVERB_ROOMSIZE();
	protected final static native int get_FMOD_DSP_REVERB_DAMP();
	protected final static native int get_FMOD_DSP_REVERB_WETMIX();
	protected final static native int get_FMOD_DSP_REVERB_DRYMIX();
	protected final static native int get_FMOD_DSP_REVERB_WIDTH();
	protected final static native int get_FMOD_DSP_REVERB_MODE();

				/* FMOD_DSP_ITECHO */

	protected final static native int get_FMOD_DSP_ITECHO_WETDRYMIX();
	protected final static native int get_FMOD_DSP_ITECHO_FEEDBACK();
	protected final static native int get_FMOD_DSP_ITECHO_LEFTDELAY();
	protected final static native int get_FMOD_DSP_ITECHO_RIGHTDELAY();
	protected final static native int get_FMOD_DSP_ITECHO_PANDELAY();

				/* FMOD_DSP_COMPRESSOR */

	protected final static native int get_FMOD_DSP_COMPRESSOR_THRESHOLD();
	protected final static native int get_FMOD_DSP_COMPRESSOR_ATTACK();
	protected final static native int get_FMOD_DSP_COMPRESSOR_RELEASE();
	protected final static native int get_FMOD_DSP_COMPRESSOR_GAINMAKEUP();

				/* FMOD_DSP_SFXREVERB */

	protected final static native int get_FMOD_DSP_SFXREVERB_DRYLEVEL();
	protected final static native int get_FMOD_DSP_SFXREVERB_ROOM();
	protected final static native int get_FMOD_DSP_SFXREVERB_ROOMHF();
	protected final static native int get_FMOD_DSP_SFXREVERB_ROOMROLLOFFFACTOR();
	protected final static native int get_FMOD_DSP_SFXREVERB_DECAYTIME();
	protected final static native int get_FMOD_DSP_SFXREVERB_DECAYHFRATIO();
	protected final static native int get_FMOD_DSP_SFXREVERB_REFLECTIONSLEVEL();
	protected final static native int get_FMOD_DSP_SFXREVERB_REFLECTIONSDELAY();
	protected final static native int get_FMOD_DSP_SFXREVERB_REVERBLEVEL();
	protected final static native int get_FMOD_DSP_SFXREVERB_REVERBDELAY();
	protected final static native int get_FMOD_DSP_SFXREVERB_DIFFUSION();
	protected final static native int get_FMOD_DSP_SFXREVERB_DENSITY();
	protected final static native int get_FMOD_DSP_SFXREVERB_HFREFERENCE();
	protected final static native int get_FMOD_DSP_SFXREVERB_ROOMLF();
	protected final static native int get_FMOD_DSP_SFXREVERB_LFREFERENCE();

				/* FMOD_DSP_LOWPASS_SIMPLE */

	protected final static native int get_FMOD_DSP_LOWPASS_SIMPLE_CUTOFF();

				/* FMOD_RESULT */

	protected final static native int get_FMOD_OK();
	protected final static native int get_FMOD_ERR_ALREADYLOCKED();
	protected final static native int get_FMOD_ERR_BADCOMMAND();
	protected final static native int get_FMOD_ERR_CDDA_DRIVERS();
	protected final static native int get_FMOD_ERR_CDDA_INIT();
	protected final static native int get_FMOD_ERR_CDDA_INVALID_DEVICE();
	protected final static native int get_FMOD_ERR_CDDA_NOAUDIO();
	protected final static native int get_FMOD_ERR_CDDA_NODEVICES();
	protected final static native int get_FMOD_ERR_CDDA_NODISC();
	protected final static native int get_FMOD_ERR_CDDA_READ();
	protected final static native int get_FMOD_ERR_CHANNEL_ALLOC();
	protected final static native int get_FMOD_ERR_CHANNEL_STOLEN();
	protected final static native int get_FMOD_ERR_COM();
	protected final static native int get_FMOD_ERR_DMA();
	protected final static native int get_FMOD_ERR_DSP_CONNECTION();
	protected final static native int get_FMOD_ERR_DSP_FORMAT();
	protected final static native int get_FMOD_ERR_DSP_NOTFOUND();
	protected final static native int get_FMOD_ERR_DSP_RUNNING();
	protected final static native int get_FMOD_ERR_DSP_TOOMANYCONNECTIONS();
	protected final static native int get_FMOD_ERR_FILE_BAD();
	protected final static native int get_FMOD_ERR_FILE_COULDNOTSEEK();
	protected final static native int get_FMOD_ERR_FILE_DISKEJECTED();
	protected final static native int get_FMOD_ERR_FILE_EOF();
	protected final static native int get_FMOD_ERR_FILE_NOTFOUND();
	protected final static native int get_FMOD_ERR_FILE_UNWANTED();
	protected final static native int get_FMOD_ERR_FORMAT();
	protected final static native int get_FMOD_ERR_HTTP();
	protected final static native int get_FMOD_ERR_HTTP_ACCESS();
	protected final static native int get_FMOD_ERR_HTTP_PROXY_AUTH();
	protected final static native int get_FMOD_ERR_HTTP_SERVER_ERROR();
	protected final static native int get_FMOD_ERR_HTTP_TIMEOUT();
	protected final static native int get_FMOD_ERR_INITIALIZATION();
	protected final static native int get_FMOD_ERR_INITIALIZED();
	protected final static native int get_FMOD_ERR_INTERNAL();
	protected final static native int get_FMOD_ERR_INVALID_ADDRESS();
	protected final static native int get_FMOD_ERR_INVALID_FLOAT();
	protected final static native int get_FMOD_ERR_INVALID_HANDLE();
	protected final static native int get_FMOD_ERR_INVALID_PARAM();
	protected final static native int get_FMOD_ERR_INVALID_SPEAKER();
	protected final static native int get_FMOD_ERR_INVALID_VECTOR();
	protected final static native int get_FMOD_ERR_IRX();
	protected final static native int get_FMOD_ERR_MAXAUDIBLE();
	protected final static native int get_FMOD_ERR_MEMORY();
	protected final static native int get_FMOD_ERR_MEMORY_CANTPOINT();
	protected final static native int get_FMOD_ERR_MEMORY_IOP();
	protected final static native int get_FMOD_ERR_MEMORY_SRAM();
	protected final static native int get_FMOD_ERR_NEEDS2D();
	protected final static native int get_FMOD_ERR_NEEDS3D();
	protected final static native int get_FMOD_ERR_NEEDSHARDWARE();
	protected final static native int get_FMOD_ERR_NEEDSSOFTWARE();
	protected final static native int get_FMOD_ERR_NET_CONNECT();
	protected final static native int get_FMOD_ERR_NET_SOCKET_ERROR();
	protected final static native int get_FMOD_ERR_NET_URL();
	protected final static native int get_FMOD_ERR_NET_WOULD_BLOCK();
	protected final static native int get_FMOD_ERR_NOTREADY();
	protected final static native int get_FMOD_ERR_OUTPUT_ALLOCATED();
	protected final static native int get_FMOD_ERR_OUTPUT_CREATEBUFFER();
	protected final static native int get_FMOD_ERR_OUTPUT_DRIVERCALL();
	protected final static native int get_FMOD_ERR_OUTPUT_ENUMERATION();
	protected final static native int get_FMOD_ERR_OUTPUT_FORMAT();
	protected final static native int get_FMOD_ERR_OUTPUT_INIT();
	protected final static native int get_FMOD_ERR_OUTPUT_NOHARDWARE();
	protected final static native int get_FMOD_ERR_OUTPUT_NOSOFTWARE();
	protected final static native int get_FMOD_ERR_PAN();
	protected final static native int get_FMOD_ERR_PLUGIN();
	protected final static native int get_FMOD_ERR_PLUGIN_INSTANCES();
	protected final static native int get_FMOD_ERR_PLUGIN_MISSING();
	protected final static native int get_FMOD_ERR_PLUGIN_RESOURCE();
	protected final static native int get_FMOD_ERR_RECORD();
	protected final static native int get_FMOD_ERR_REVERB_INSTANCE();
	protected final static native int get_FMOD_ERR_SUBSOUND_ALLOCATED();
	protected final static native int get_FMOD_ERR_SUBSOUND_CANTMOVE();
	protected final static native int get_FMOD_ERR_SUBSOUND_MODE();
	protected final static native int get_FMOD_ERR_SUBSOUNDS();
	protected final static native int get_FMOD_ERR_TAGNOTFOUND();
	protected final static native int get_FMOD_ERR_TOOMANYCHANNELS();
	protected final static native int get_FMOD_ERR_UNIMPLEMENTED();
	protected final static native int get_FMOD_ERR_UNINITIALIZED();
	protected final static native int get_FMOD_ERR_UNSUPPORTED();
	protected final static native int get_FMOD_ERR_UPDATE();
	protected final static native int get_FMOD_ERR_VERSION();
	protected final static native int get_FMOD_ERR_EVENT_FAILED();
	protected final static native int get_FMOD_ERR_EVENT_INFOONLY();
	protected final static native int get_FMOD_ERR_EVENT_INTERNAL();
	protected final static native int get_FMOD_ERR_EVENT_MAXSTREAMS();
	protected final static native int get_FMOD_ERR_EVENT_MISMATCH();
	protected final static native int get_FMOD_ERR_EVENT_NAMECONFLICT();
	protected final static native int get_FMOD_ERR_EVENT_NOTFOUND();

				/* FMOD_OUTPUTTYPE */

	protected final static native int get_FMOD_OUTPUTTYPE_AUTODETECT();
	protected final static native int get_FMOD_OUTPUTTYPE_UNKNOWN();
	protected final static native int get_FMOD_OUTPUTTYPE_NOSOUND();
	protected final static native int get_FMOD_OUTPUTTYPE_WAVWRITER();
	protected final static native int get_FMOD_OUTPUTTYPE_NOSOUND_NRT();
	protected final static native int get_FMOD_OUTPUTTYPE_WAVWRITER_NRT();
	protected final static native int get_FMOD_OUTPUTTYPE_DSOUND();
	protected final static native int get_FMOD_OUTPUTTYPE_WINMM();
	protected final static native int get_FMOD_OUTPUTTYPE_OPENAL();
	protected final static native int get_FMOD_OUTPUTTYPE_WASAPI();
	protected final static native int get_FMOD_OUTPUTTYPE_ASIO();
	protected final static native int get_FMOD_OUTPUTTYPE_OSS();
	protected final static native int get_FMOD_OUTPUTTYPE_ALSA();
	protected final static native int get_FMOD_OUTPUTTYPE_ESD();
	protected final static native int get_FMOD_OUTPUTTYPE_SOUNDMANAGER();
	protected final static native int get_FMOD_OUTPUTTYPE_COREAUDIO();
	protected final static native int get_FMOD_OUTPUTTYPE_XBOX();
	protected final static native int get_FMOD_OUTPUTTYPE_PS2();
	protected final static native int get_FMOD_OUTPUTTYPE_PS3();
	protected final static native int get_FMOD_OUTPUTTYPE_GC();
	protected final static native int get_FMOD_OUTPUTTYPE_XBOX360();
	protected final static native int get_FMOD_OUTPUTTYPE_PSP();
	protected final static native int get_FMOD_OUTPUTTYPE_WII();
	protected final static native int get_FMOD_OUTPUTTYPE_MAX();

				/* FMOD_SPEAKERMODE */

	protected final static native int get_FMOD_SPEAKERMODE_RAW();
	protected final static native int get_FMOD_SPEAKERMODE_MONO();
	protected final static native int get_FMOD_SPEAKERMODE_STEREO();
	protected final static native int get_FMOD_SPEAKERMODE_QUAD();
	protected final static native int get_FMOD_SPEAKERMODE_SURROUND();
	protected final static native int get_FMOD_SPEAKERMODE_5POINT1();
	protected final static native int get_FMOD_SPEAKERMODE_7POINT1();
	protected final static native int get_FMOD_SPEAKERMODE_PROLOGIC();
	protected final static native int get_FMOD_SPEAKERMODE_MAX();

				/* FMOD_SPEAKER */

	protected final static native int get_FMOD_SPEAKER_FRONT_LEFT();
	protected final static native int get_FMOD_SPEAKER_FRONT_RIGHT();
	protected final static native int get_FMOD_SPEAKER_FRONT_CENTER();
	protected final static native int get_FMOD_SPEAKER_LOW_FREQUENCY();
	protected final static native int get_FMOD_SPEAKER_BACK_LEFT();
	protected final static native int get_FMOD_SPEAKER_BACK_RIGHT();
	protected final static native int get_FMOD_SPEAKER_SIDE_LEFT();
	protected final static native int get_FMOD_SPEAKER_SIDE_RIGHT();
	protected final static native int get_FMOD_SPEAKER_MAX();
	protected final static native int get_FMOD_SPEAKER_MONO();
	protected final static native int get_FMOD_SPEAKER_NULL();
	protected final static native int get_FMOD_SPEAKER_SBL();
	protected final static native int get_FMOD_SPEAKER_SBR();

				/* FMOD_PLUGINTYPE */

	protected final static native int get_FMOD_PLUGINTYPE_OUTPUT();
	protected final static native int get_FMOD_PLUGINTYPE_CODEC();
	protected final static native int get_FMOD_PLUGINTYPE_DSP();
	protected final static native int get_FMOD_PLUGINTYPE_MAX();

				/* FMOD_SOUND_TYPE */

	protected final static native int get_FMOD_SOUND_TYPE_UNKNOWN();
	protected final static native int get_FMOD_SOUND_TYPE_AAC();
	protected final static native int get_FMOD_SOUND_TYPE_AIFF();
	protected final static native int get_FMOD_SOUND_TYPE_ASF();
	protected final static native int get_FMOD_SOUND_TYPE_AT3();
	protected final static native int get_FMOD_SOUND_TYPE_CDDA();
	protected final static native int get_FMOD_SOUND_TYPE_DLS();
	protected final static native int get_FMOD_SOUND_TYPE_FLAC();
	protected final static native int get_FMOD_SOUND_TYPE_FSB();
	protected final static native int get_FMOD_SOUND_TYPE_GCADPCM();
	protected final static native int get_FMOD_SOUND_TYPE_IT();
	protected final static native int get_FMOD_SOUND_TYPE_MIDI();
	protected final static native int get_FMOD_SOUND_TYPE_MOD();
	protected final static native int get_FMOD_SOUND_TYPE_MPEG();
	protected final static native int get_FMOD_SOUND_TYPE_OGGVORBIS();
	protected final static native int get_FMOD_SOUND_TYPE_PLAYLIST();
	protected final static native int get_FMOD_SOUND_TYPE_RAW();
	protected final static native int get_FMOD_SOUND_TYPE_S3M();
	protected final static native int get_FMOD_SOUND_TYPE_SF2();
	protected final static native int get_FMOD_SOUND_TYPE_USER();
	protected final static native int get_FMOD_SOUND_TYPE_WAV();
	protected final static native int get_FMOD_SOUND_TYPE_XM();
	protected final static native int get_FMOD_SOUND_TYPE_XMA();
	protected final static native int get_FMOD_SOUND_TYPE_VAG();
	protected final static native int get_FMOD_SOUND_TYPE_MAX();

				/* FMOD_SOUND_FORMAT */

	protected final static native int get_FMOD_SOUND_FORMAT_NONE();
	protected final static native int get_FMOD_SOUND_FORMAT_PCM8();
	protected final static native int get_FMOD_SOUND_FORMAT_PCM16();
	protected final static native int get_FMOD_SOUND_FORMAT_PCM24();
	protected final static native int get_FMOD_SOUND_FORMAT_PCM32();
	protected final static native int get_FMOD_SOUND_FORMAT_PCMFLOAT();
	protected final static native int get_FMOD_SOUND_FORMAT_GCADPCM();
	protected final static native int get_FMOD_SOUND_FORMAT_IMAADPCM();
	protected final static native int get_FMOD_SOUND_FORMAT_VAG();
	protected final static native int get_FMOD_SOUND_FORMAT_XMA();
	protected final static native int get_FMOD_SOUND_FORMAT_MPEG();
	protected final static native int get_FMOD_SOUND_FORMAT_MAX();

				/* FMOD_OPENSTATE */

	protected final static native int get_FMOD_OPENSTATE_LOADING();
	protected final static native int get_FMOD_OPENSTATE_ERROR();
	protected final static native int get_FMOD_OPENSTATE_CONNECTING();
	protected final static native int get_FMOD_OPENSTATE_BUFFERING();
	protected final static native int get_FMOD_OPENSTATE_SEEKING();
	protected final static native int get_FMOD_OPENSTATE_STREAMING();
	protected final static native int get_FMOD_OPENSTATE_MAX();

				/* FMOD_SOUNDGROUP_BEHAVIOR */

	protected final static native int get_FMOD_SOUNDGROUP_BEHAVIOR_FAIL();
	protected final static native int get_FMOD_SOUNDGROUP_BEHAVIOR_MUTE();
	protected final static native int get_FMOD_SOUNDGROUP_BEHAVIOR_STEALLOWEST();
	protected final static native int get_FMOD_SOUNDGROUP_BEHAVIOR_MAX();

				/* FMOD_CHANNEL_CALLBACKTYPE */

	protected final static native int get_FMOD_CHANNEL_CALLBACKTYPE_END();
	protected final static native int get_FMOD_CHANNEL_CALLBACKTYPE_VIRTUALVOICE();
	protected final static native int get_FMOD_CHANNEL_CALLBACKTYPE_SYNCPOINT();
	protected final static native int get_FMOD_CHANNEL_CALLBACKTYPE_MAX();

				/* FMOD_SYSTEM_CALLBACKTYPE */

	protected final static native int get_FMOD_SYSTEM_CALLBACKTYPE_DEVICELISTCHANGED();
	protected final static native int get_FMOD_SYSTEM_CALLBACKTYPE_MEMORYALLOCATIONFAILED();
	protected final static native int get_FMOD_SYSTEM_CALLBACKTYPE_THREADCREATED();
	protected final static native int get_FMOD_SYSTEM_CALLBACKTYPE_BADDSPCONNECTION();
	protected final static native int get_FMOD_SYSTEM_CALLBACKTYPE_MAX();

				/* FMOD_DSP_FFT_WINDOW */

	protected final static native int get_FMOD_DSP_FFT_WINDOW_RECT();
	protected final static native int get_FMOD_DSP_FFT_WINDOW_TRIANGLE();
	protected final static native int get_FMOD_DSP_FFT_WINDOW_HAMMING();
	protected final static native int get_FMOD_DSP_FFT_WINDOW_HANNING();
	protected final static native int get_FMOD_DSP_FFT_WINDOW_BLACKMAN();
	protected final static native int get_FMOD_DSP_FFT_WINDOW_BLACKMANHARRIS();
	protected final static native int get_FMOD_DSP_FFT_WINDOW_MAX();

				/* FMOD_DSP_RESAMPLER */

	protected final static native int get_FMOD_DSP_RESAMPLER_NOINTERP();
	protected final static native int get_FMOD_DSP_RESAMPLER_LINEAR();
	protected final static native int get_FMOD_DSP_RESAMPLER_CUBIC();
	protected final static native int get_FMOD_DSP_RESAMPLER_SPLINE();
	protected final static native int get_FMOD_DSP_RESAMPLER_MAX();

				/* FMOD_TAGTYPE */

	protected final static native int get_FMOD_TAGTYPE_ID3V1();
	protected final static native int get_FMOD_TAGTYPE_ID3V2();
	protected final static native int get_FMOD_TAGTYPE_VORBISCOMMENT();
	protected final static native int get_FMOD_TAGTYPE_SHOUTCAST();
	protected final static native int get_FMOD_TAGTYPE_ICECAST();
	protected final static native int get_FMOD_TAGTYPE_ASF();
	protected final static native int get_FMOD_TAGTYPE_MIDI();
	protected final static native int get_FMOD_TAGTYPE_PLAYLIST();
	protected final static native int get_FMOD_TAGTYPE_FMOD();
	protected final static native int get_FMOD_TAGTYPE_USER();
	protected final static native int get_FMOD_TAGTYPE_MAX();

				/* FMOD_TAGDATATYPE */

	protected final static native int get_FMOD_TAGDATATYPE_INT();
	protected final static native int get_FMOD_TAGDATATYPE_FLOAT();
	protected final static native int get_FMOD_TAGDATATYPE_STRING();
	protected final static native int get_FMOD_TAGDATATYPE_STRING_UTF16();
	protected final static native int get_FMOD_TAGDATATYPE_STRING_UTF16BE();
	protected final static native int get_FMOD_TAGDATATYPE_STRING_UTF8();
	protected final static native int get_FMOD_TAGDATATYPE_CDTOC();
	protected final static native int get_FMOD_TAGDATATYPE_MAX();

				/* FMOD_DELAYTYPE */

	protected final static native int get_FMOD_DELAYTYPE_END_MS();
	protected final static native int get_FMOD_DELAYTYPE_DSPCLOCK_START();
	protected final static native int get_FMOD_DELAYTYPE_DSPCLOCK_END();
	protected final static native int get_FMOD_DELAYTYPE_MAX();

				/* FMOD_SPEAKERMAPTYPE */

	protected final static native int get_FMOD_SPEAKERMAPTYPE_DEFAULT();
	protected final static native int get_FMOD_SPEAKERMAPTYPE_ALLMONO();
	protected final static native int get_FMOD_SPEAKERMAPTYPE_ALLSTEREO();

				/* FMOD_CHANNELINDEX */


}