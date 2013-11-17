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

class StructureJNI
{
	static
	{
		if(!Init.isLibrariesLoaded())
		{
			throw new RuntimeException("Libraries not loaded ! Use Init.loadLibraries() before using NativeFmodEx.");
		}
	}

						/* FMOD_CODEC_DESCRIPTION */

	protected final static native long FMOD_CODEC_DESCRIPTION_new();
	protected final static native void FMOD_CODEC_DESCRIPTION_delete(long pointer);
	protected final static native String FMOD_CODEC_DESCRIPTION_get_name(long pointer);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_name(long pointer, byte[] name);
	protected final static native int FMOD_CODEC_DESCRIPTION_get_version(long pointer);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_version(long pointer, int version);
	protected final static native int FMOD_CODEC_DESCRIPTION_get_defaultasstream(long pointer);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_defaultasstream(long pointer, int defaultasstream);
	protected final static native int FMOD_CODEC_DESCRIPTION_get_timeunits(long pointer);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_timeunits(long pointer, int timeunits);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_open(long pointer, boolean open);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_close(long pointer, boolean close);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_read(long pointer, boolean read);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_getlength(long pointer, boolean getlength);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_setposition(long pointer, boolean setposition);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_getposition(long pointer, boolean getposition);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_soundcreate(long pointer, boolean soundcreate);
	protected final static native void FMOD_CODEC_DESCRIPTION_set_getwaveformat(long pointer, boolean getwaveformat);

						/* FMOD_CODEC_WAVEFORMAT */

	protected final static native long FMOD_CODEC_WAVEFORMAT_new();
	protected final static native void FMOD_CODEC_WAVEFORMAT_delete(long pointer);
	protected final static native ByteBuffer FMOD_CODEC_WAVEFORMAT_get_name(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_name(long pointer, byte[] name);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_format(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_format(long pointer, int format);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_channels(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_channels(long pointer, int channels);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_frequency(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_frequency(long pointer, int frequency);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_lengthbytes(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_lengthbytes(long pointer, int lengthbytes);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_lengthpcm(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_lengthpcm(long pointer, int lengthpcm);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_blockalign(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_blockalign(long pointer, int blockalign);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_loopstart(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_loopstart(long pointer, int loopstart);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_loopend(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_loopend(long pointer, int loopend);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_mode(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_mode(long pointer, int mode);
	protected final static native int FMOD_CODEC_WAVEFORMAT_get_channelmask(long pointer);
	protected final static native void FMOD_CODEC_WAVEFORMAT_set_channelmask(long pointer, int channelmask);

						/* FMOD_CODEC_STATE */

	protected final static native long FMOD_CODEC_STATE_new();
	protected final static native void FMOD_CODEC_STATE_delete(long pointer);
	protected final static native int FMOD_CODEC_STATE_get_numsubsounds(long pointer);
	protected final static native void FMOD_CODEC_STATE_set_numsubsounds(long pointer, int numsubsounds);
	protected final static native long FMOD_CODEC_STATE_get_waveformat(long pointer);
	protected final static native void FMOD_CODEC_STATE_set_waveformat(long pointer, long waveformat);
	protected final static native long FMOD_CODEC_STATE_get_plugindata(long pointer);
	protected final static native void FMOD_CODEC_STATE_set_plugindata(long pointer, long plugindata);
	protected final static native long FMOD_CODEC_STATE_get_filehandle(long pointer);
	protected final static native int FMOD_CODEC_STATE_get_filesize(long pointer);
	protected final static native int FMOD_CODEC_STATE_invoke_fileread(long pointer, long handle, ByteBuffer buffer, long buffer_, int sizebytes, IntBuffer bytesread, long bytesread_, long userdata);
	protected final static native int FMOD_CODEC_STATE_invoke_fileseek(long pointer, long handle, int pos, long userdata);
	protected final static native int FMOD_CODEC_STATE_invoke_metadata(long pointer, long codec_state, int tagtype, ByteBuffer name, long name_, long data, int datalen, int datatype, int unique);

						/* FMOD_DSP_PARAMETERDESC */

	protected final static native int FMOD_DSP_PARAMETERDESC_SIZEOF();
	protected final static native long FMOD_DSP_PARAMETERDESC_newArray(int length);
	protected final static native long FMOD_DSP_PARAMETERDESC_new();
	protected final static native void FMOD_DSP_PARAMETERDESC_delete(long pointer);
	protected final static native float FMOD_DSP_PARAMETERDESC_get_min(long pointer);
	protected final static native void FMOD_DSP_PARAMETERDESC_set_min(long pointer, float min);
	protected final static native float FMOD_DSP_PARAMETERDESC_get_max(long pointer);
	protected final static native void FMOD_DSP_PARAMETERDESC_set_max(long pointer, float max);
	protected final static native float FMOD_DSP_PARAMETERDESC_get_defaultval(long pointer);
	protected final static native void FMOD_DSP_PARAMETERDESC_set_defaultval(long pointer, float defaultval);
	protected final static native String FMOD_DSP_PARAMETERDESC_get_name(long pointer);
	protected final static native void FMOD_DSP_PARAMETERDESC_set_name(long pointer, byte[] name);
	protected final static native String FMOD_DSP_PARAMETERDESC_get_label(long pointer);
	protected final static native void FMOD_DSP_PARAMETERDESC_set_label(long pointer, byte[] label);
	protected final static native String FMOD_DSP_PARAMETERDESC_get_description(long pointer);
	protected final static native void FMOD_DSP_PARAMETERDESC_set_description(long pointer, byte[] description);

						/* FMOD_DSP_DESCRIPTION */

	protected final static native long FMOD_DSP_DESCRIPTION_new();
	protected final static native void FMOD_DSP_DESCRIPTION_delete(long pointer);
	protected final static native String FMOD_DSP_DESCRIPTION_get_name(long pointer);
	protected final static native void FMOD_DSP_DESCRIPTION_set_name(long pointer, byte[] name);
	protected final static native int FMOD_DSP_DESCRIPTION_get_version(long pointer);
	protected final static native void FMOD_DSP_DESCRIPTION_set_version(long pointer, int version);
	protected final static native int FMOD_DSP_DESCRIPTION_get_channels(long pointer);
	protected final static native void FMOD_DSP_DESCRIPTION_set_channels(long pointer, int channels);
	protected final static native void FMOD_DSP_DESCRIPTION_set_create(long pointer, boolean create);
	protected final static native void FMOD_DSP_DESCRIPTION_set_release(long pointer, boolean release);
	protected final static native void FMOD_DSP_DESCRIPTION_set_reset(long pointer, boolean reset);
	protected final static native void FMOD_DSP_DESCRIPTION_set_read(long pointer, boolean read);
	protected final static native void FMOD_DSP_DESCRIPTION_set_setposition(long pointer, boolean setposition);
	protected final static native int FMOD_DSP_DESCRIPTION_get_numparameters(long pointer);
	protected final static native void FMOD_DSP_DESCRIPTION_set_numparameters(long pointer, int numparameters);
	protected final static native long FMOD_DSP_DESCRIPTION_get_paramdesc(long pointer);
	protected final static native void FMOD_DSP_DESCRIPTION_set_paramdesc(long pointer, long paramdesc);
	protected final static native void FMOD_DSP_DESCRIPTION_set_setparameter(long pointer, boolean setparameter);
	protected final static native void FMOD_DSP_DESCRIPTION_set_getparameter(long pointer, boolean getparameter);
	protected final static native void FMOD_DSP_DESCRIPTION_set_config(long pointer, boolean config);
	protected final static native int FMOD_DSP_DESCRIPTION_get_configwidth(long pointer);
	protected final static native void FMOD_DSP_DESCRIPTION_set_configwidth(long pointer, int configwidth);
	protected final static native int FMOD_DSP_DESCRIPTION_get_configheight(long pointer);
	protected final static native void FMOD_DSP_DESCRIPTION_set_configheight(long pointer, int configheight);
	protected final static native long FMOD_DSP_DESCRIPTION_get_userdata(long pointer);
	protected final static native void FMOD_DSP_DESCRIPTION_set_userdata(long pointer, long userdata);

						/* FMOD_DSP_STATE */

	protected final static native long FMOD_DSP_STATE_new();
	protected final static native void FMOD_DSP_STATE_delete(long pointer);
	protected final static native long FMOD_DSP_STATE_get_instance(long pointer);
	protected final static native long FMOD_DSP_STATE_get_plugindata(long pointer);
	protected final static native void FMOD_DSP_STATE_set_plugindata(long pointer, long plugindata);
	protected final static native short FMOD_DSP_STATE_get_speakermask(long pointer);
	protected final static native void FMOD_DSP_STATE_set_speakermask(long pointer, short speakermask);

						/* FMOD_SYNCPOINT */


						/* FMOD_VECTOR */

	protected final static native int FMOD_VECTOR_SIZEOF();
	protected final static native long FMOD_VECTOR_newArray(int length);
	protected final static native long FMOD_VECTOR_new();
	protected final static native void FMOD_VECTOR_delete(long pointer);
	protected final static native float FMOD_VECTOR_get_x(long pointer);
	protected final static native void FMOD_VECTOR_set_x(long pointer, float x);
	protected final static native float FMOD_VECTOR_get_y(long pointer);
	protected final static native void FMOD_VECTOR_set_y(long pointer, float y);
	protected final static native float FMOD_VECTOR_get_z(long pointer);
	protected final static native void FMOD_VECTOR_set_z(long pointer, float z);

	protected final static native long FMOD_VECTOR_create(float x, float y, float z);
	protected final static native void FMOD_VECTOR_set_xyz(long pointer, long vector);
	protected final static native void FMOD_VECTOR_set_xyz(long pointer, float x, float y, float z);

						/* FMOD_GUID */

	protected final static native long FMOD_GUID_new();
	protected final static native void FMOD_GUID_delete(long pointer);
	protected final static native int FMOD_GUID_get_Data1(long pointer);
	protected final static native void FMOD_GUID_set_Data1(long pointer, int Data1);
	protected final static native short FMOD_GUID_get_Data2(long pointer);
	protected final static native void FMOD_GUID_set_Data2(long pointer, short Data2);
	protected final static native short FMOD_GUID_get_Data3(long pointer);
	protected final static native void FMOD_GUID_set_Data3(long pointer, short Data3);
	protected final static native ByteBuffer FMOD_GUID_get_Data4(long pointer);
	protected final static native void FMOD_GUID_set_Data4(long pointer, byte[] Data4);

						/* FMOD_TAG */

	protected final static native long FMOD_TAG_new();
	protected final static native void FMOD_TAG_delete(long pointer);
	protected final static native int FMOD_TAG_get_type(long pointer);
	protected final static native int FMOD_TAG_get_datatype(long pointer);
	protected final static native String FMOD_TAG_get_name(long pointer);
	protected final static native long FMOD_TAG_get_data(long pointer);
	protected final static native int FMOD_TAG_get_datalen(long pointer);
	protected final static native boolean FMOD_TAG_get_updated(long pointer);

						/* FMOD_CDTOC */

	protected final static native long FMOD_CDTOC_new();
	protected final static native void FMOD_CDTOC_delete(long pointer);
	protected final static native int FMOD_CDTOC_get_numtracks(long pointer);
	protected final static native ByteBuffer FMOD_CDTOC_get_min(long pointer);
	protected final static native ByteBuffer FMOD_CDTOC_get_sec(long pointer);
	protected final static native ByteBuffer FMOD_CDTOC_get_frame(long pointer);

						/* FMOD_CREATESOUNDEXINFO */

	protected final static native long FMOD_CREATESOUNDEXINFO_new();
	protected final static native void FMOD_CREATESOUNDEXINFO_delete(long pointer);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_cbsize(long pointer);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_length(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_length(long pointer, int length);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_fileoffset(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_fileoffset(long pointer, int fileoffset);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_numchannels(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_numchannels(long pointer, int numchannels);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_defaultfrequency(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_defaultfrequency(long pointer, int defaultfrequency);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_format(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_format(long pointer, int format);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_decodebuffersize(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_decodebuffersize(long pointer, int decodebuffersize);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_initialsubsound(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_initialsubsound(long pointer, int initialsubsound);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_numsubsounds(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_numsubsounds(long pointer, int numsubsounds);
	protected final static native ByteBuffer FMOD_CREATESOUNDEXINFO_get_inclusionlist(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_inclusionlist(long pointer, IntBuffer inclusionlist, long inclusionlist_);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_inclusionlistnum(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_inclusionlistnum(long pointer, int inclusionlistnum);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_pcmreadcallback(long pointer, boolean pcmreadcallback);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_pcmsetposcallback(long pointer, boolean pcmsetposcallback);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_nonblockcallback(long pointer, boolean nonblockcallback);
	protected final static native String FMOD_CREATESOUNDEXINFO_get_dlsname(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_dlsname(long pointer, byte[] dlsname);
	protected final static native String FMOD_CREATESOUNDEXINFO_get_encryptionkey(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_encryptionkey(long pointer, byte[] encryptionkey);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_maxpolyphony(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_maxpolyphony(long pointer, int maxpolyphony);
	protected final static native long FMOD_CREATESOUNDEXINFO_get_userdata(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_userdata(long pointer, long userdata);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_suggestedsoundtype(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_suggestedsoundtype(long pointer, int suggestedsoundtype);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_useropen(long pointer, boolean useropen);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_userclose(long pointer, boolean userclose);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_userread(long pointer, boolean userread);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_userseek(long pointer, boolean userseek);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_speakermap(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_speakermap(long pointer, int speakermap);
	protected final static native long FMOD_CREATESOUNDEXINFO_get_initialsoundgroup(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_initialsoundgroup(long pointer, long initialsoundgroup);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_initialseekposition(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_initialseekposition(long pointer, int initialseekposition);
	protected final static native int FMOD_CREATESOUNDEXINFO_get_initialseekpostype(long pointer);
	protected final static native void FMOD_CREATESOUNDEXINFO_set_initialseekpostype(long pointer, int initialseekpostype);

						/* FMOD_REVERB_PROPERTIES */

	protected final static native long FMOD_REVERB_PROPERTIES_new();
	protected final static native void FMOD_REVERB_PROPERTIES_delete(long pointer);
	protected final static native int FMOD_REVERB_PROPERTIES_get_Instance(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_Instance(long pointer, int Instance);
	protected final static native int FMOD_REVERB_PROPERTIES_get_Environment(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_Environment(long pointer, int Environment);
	protected final static native float FMOD_REVERB_PROPERTIES_get_EnvSize(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_EnvSize(long pointer, float EnvSize);
	protected final static native float FMOD_REVERB_PROPERTIES_get_EnvDiffusion(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_EnvDiffusion(long pointer, float EnvDiffusion);
	protected final static native int FMOD_REVERB_PROPERTIES_get_Room(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_Room(long pointer, int Room);
	protected final static native int FMOD_REVERB_PROPERTIES_get_RoomHF(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_RoomHF(long pointer, int RoomHF);
	protected final static native int FMOD_REVERB_PROPERTIES_get_RoomLF(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_RoomLF(long pointer, int RoomLF);
	protected final static native float FMOD_REVERB_PROPERTIES_get_DecayTime(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_DecayTime(long pointer, float DecayTime);
	protected final static native float FMOD_REVERB_PROPERTIES_get_DecayHFRatio(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_DecayHFRatio(long pointer, float DecayHFRatio);
	protected final static native float FMOD_REVERB_PROPERTIES_get_DecayLFRatio(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_DecayLFRatio(long pointer, float DecayLFRatio);
	protected final static native int FMOD_REVERB_PROPERTIES_get_Reflections(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_Reflections(long pointer, int Reflections);
	protected final static native float FMOD_REVERB_PROPERTIES_get_ReflectionsDelay(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_ReflectionsDelay(long pointer, float ReflectionsDelay);
	protected final static native ByteBuffer FMOD_REVERB_PROPERTIES_get_ReflectionsPan(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_ReflectionsPan(long pointer, FloatBuffer ReflectionsPan, long ReflectionsPan_);
	protected final static native int FMOD_REVERB_PROPERTIES_get_Reverb(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_Reverb(long pointer, int Reverb);
	protected final static native float FMOD_REVERB_PROPERTIES_get_ReverbDelay(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_ReverbDelay(long pointer, float ReverbDelay);
	protected final static native ByteBuffer FMOD_REVERB_PROPERTIES_get_ReverbPan(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_ReverbPan(long pointer, FloatBuffer ReverbPan, long ReverbPan_);
	protected final static native float FMOD_REVERB_PROPERTIES_get_EchoTime(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_EchoTime(long pointer, float EchoTime);
	protected final static native float FMOD_REVERB_PROPERTIES_get_EchoDepth(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_EchoDepth(long pointer, float EchoDepth);
	protected final static native float FMOD_REVERB_PROPERTIES_get_ModulationTime(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_ModulationTime(long pointer, float ModulationTime);
	protected final static native float FMOD_REVERB_PROPERTIES_get_ModulationDepth(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_ModulationDepth(long pointer, float ModulationDepth);
	protected final static native float FMOD_REVERB_PROPERTIES_get_AirAbsorptionHF(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_AirAbsorptionHF(long pointer, float AirAbsorptionHF);
	protected final static native float FMOD_REVERB_PROPERTIES_get_HFReference(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_HFReference(long pointer, float HFReference);
	protected final static native float FMOD_REVERB_PROPERTIES_get_LFReference(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_LFReference(long pointer, float LFReference);
	protected final static native float FMOD_REVERB_PROPERTIES_get_RoomRolloffFactor(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_RoomRolloffFactor(long pointer, float RoomRolloffFactor);
	protected final static native float FMOD_REVERB_PROPERTIES_get_Diffusion(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_Diffusion(long pointer, float Diffusion);
	protected final static native float FMOD_REVERB_PROPERTIES_get_Density(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_Density(long pointer, float Density);
	protected final static native int FMOD_REVERB_PROPERTIES_get_Flags(long pointer);
	protected final static native void FMOD_REVERB_PROPERTIES_set_Flags(long pointer, int Flags);

	protected final static native long get_FMOD_PRESET_OFF();
	protected final static native long get_FMOD_PRESET_GENERIC();
	protected final static native long get_FMOD_PRESET_PADDEDCELL();
	protected final static native long get_FMOD_PRESET_ROOM();
	protected final static native long get_FMOD_PRESET_BATHROOM();
	protected final static native long get_FMOD_PRESET_LIVINGROOM();
	protected final static native long get_FMOD_PRESET_STONEROOM();
	protected final static native long get_FMOD_PRESET_AUDITORIUM();
	protected final static native long get_FMOD_PRESET_CONCERTHALL();
	protected final static native long get_FMOD_PRESET_CAVE();
	protected final static native long get_FMOD_PRESET_ARENA();
	protected final static native long get_FMOD_PRESET_HANGAR();
	protected final static native long get_FMOD_PRESET_CARPETTEDHALLWAY();
	protected final static native long get_FMOD_PRESET_HALLWAY();
	protected final static native long get_FMOD_PRESET_STONECORRIDOR();
	protected final static native long get_FMOD_PRESET_ALLEY();
	protected final static native long get_FMOD_PRESET_FOREST();
	protected final static native long get_FMOD_PRESET_CITY();
	protected final static native long get_FMOD_PRESET_MOUNTAINS();
	protected final static native long get_FMOD_PRESET_QUARRY();
	protected final static native long get_FMOD_PRESET_PLAIN();
	protected final static native long get_FMOD_PRESET_PARKINGLOT();
	protected final static native long get_FMOD_PRESET_SEWERPIPE();
	protected final static native long get_FMOD_PRESET_UNDERWATER();
	protected final static native long get_FMOD_PRESET_DRUGGED();
	protected final static native long get_FMOD_PRESET_DIZZY();
	protected final static native long get_FMOD_PRESET_PSYCHOTIC();
	protected final static native long get_FMOD_PRESET_PS2_ROOM();
	protected final static native long get_FMOD_PRESET_PS2_STUDIO_A();
	protected final static native long get_FMOD_PRESET_PS2_STUDIO_B();
	protected final static native long get_FMOD_PRESET_PS2_STUDIO_C();
	protected final static native long get_FMOD_PRESET_PS2_HALL();
	protected final static native long get_FMOD_PRESET_PS2_SPACE();
	protected final static native long get_FMOD_PRESET_PS2_ECHO();
	protected final static native long get_FMOD_PRESET_PS2_DELAY();
	protected final static native long get_FMOD_PRESET_PS2_PIPE();

						/* FMOD_REVERB_CHANNELPROPERTIES */

	protected final static native long FMOD_REVERB_CHANNELPROPERTIES_new();
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_delete(long pointer);
	protected final static native int FMOD_REVERB_CHANNELPROPERTIES_get_Direct(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_Direct(long pointer, int Direct);
	protected final static native int FMOD_REVERB_CHANNELPROPERTIES_get_DirectHF(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_DirectHF(long pointer, int DirectHF);
	protected final static native int FMOD_REVERB_CHANNELPROPERTIES_get_Room(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_Room(long pointer, int Room);
	protected final static native int FMOD_REVERB_CHANNELPROPERTIES_get_RoomHF(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_RoomHF(long pointer, int RoomHF);
	protected final static native int FMOD_REVERB_CHANNELPROPERTIES_get_Obstruction(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_Obstruction(long pointer, int Obstruction);
	protected final static native float FMOD_REVERB_CHANNELPROPERTIES_get_ObstructionLFRatio(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_ObstructionLFRatio(long pointer, float ObstructionLFRatio);
	protected final static native int FMOD_REVERB_CHANNELPROPERTIES_get_Occlusion(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_Occlusion(long pointer, int Occlusion);
	protected final static native float FMOD_REVERB_CHANNELPROPERTIES_get_OcclusionLFRatio(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_OcclusionLFRatio(long pointer, float OcclusionLFRatio);
	protected final static native float FMOD_REVERB_CHANNELPROPERTIES_get_OcclusionRoomRatio(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_OcclusionRoomRatio(long pointer, float OcclusionRoomRatio);
	protected final static native float FMOD_REVERB_CHANNELPROPERTIES_get_OcclusionDirectRatio(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_OcclusionDirectRatio(long pointer, float OcclusionDirectRatio);
	protected final static native int FMOD_REVERB_CHANNELPROPERTIES_get_Exclusion(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_Exclusion(long pointer, int Exclusion);
	protected final static native float FMOD_REVERB_CHANNELPROPERTIES_get_ExclusionLFRatio(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_ExclusionLFRatio(long pointer, float ExclusionLFRatio);
	protected final static native int FMOD_REVERB_CHANNELPROPERTIES_get_OutsideVolumeHF(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_OutsideVolumeHF(long pointer, int OutsideVolumeHF);
	protected final static native float FMOD_REVERB_CHANNELPROPERTIES_get_DopplerFactor(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_DopplerFactor(long pointer, float DopplerFactor);
	protected final static native float FMOD_REVERB_CHANNELPROPERTIES_get_RolloffFactor(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_RolloffFactor(long pointer, float RolloffFactor);
	protected final static native float FMOD_REVERB_CHANNELPROPERTIES_get_RoomRolloffFactor(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_RoomRolloffFactor(long pointer, float RoomRolloffFactor);
	protected final static native float FMOD_REVERB_CHANNELPROPERTIES_get_AirAbsorptionFactor(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_AirAbsorptionFactor(long pointer, float AirAbsorptionFactor);
	protected final static native int FMOD_REVERB_CHANNELPROPERTIES_get_Flags(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_Flags(long pointer, int Flags);
	protected final static native long FMOD_REVERB_CHANNELPROPERTIES_get_ConnectionPoint(long pointer);
	protected final static native void FMOD_REVERB_CHANNELPROPERTIES_set_ConnectionPoint(long pointer, long ConnectionPoint);

						/* FMOD_ADVANCEDSETTINGS */

	protected final static native long FMOD_ADVANCEDSETTINGS_new();
	protected final static native void FMOD_ADVANCEDSETTINGS_delete(long pointer);
	protected final static native int FMOD_ADVANCEDSETTINGS_get_cbsize(long pointer);
	protected final static native int FMOD_ADVANCEDSETTINGS_get_maxMPEGcodecs(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_maxMPEGcodecs(long pointer, int maxMPEGcodecs);
	protected final static native int FMOD_ADVANCEDSETTINGS_get_maxADPCMcodecs(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_maxADPCMcodecs(long pointer, int maxADPCMcodecs);
	protected final static native int FMOD_ADVANCEDSETTINGS_get_maxXMAcodecs(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_maxXMAcodecs(long pointer, int maxXMAcodecs);
	protected final static native int FMOD_ADVANCEDSETTINGS_get_maxPCMcodecs(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_maxPCMcodecs(long pointer, int maxPCMcodecs);
	protected final static native int FMOD_ADVANCEDSETTINGS_get_ASIONumChannels(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_ASIONumChannels(long pointer, int ASIONumChannels);
	protected final static native String[] FMOD_ADVANCEDSETTINGS_get_ASIOChannelList(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_ASIOChannelList(long pointer, String[] ASIOChannelList);
	protected final static native ByteBuffer FMOD_ADVANCEDSETTINGS_get_ASIOSpeakerList(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_ASIOSpeakerList(long pointer, ByteBuffer ASIOSpeakerList, long ASIOSpeakerList_);
	protected final static native int FMOD_ADVANCEDSETTINGS_get_max3DReverbDSPs(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_max3DReverbDSPs(long pointer, int max3DReverbDSPs);
	protected final static native float FMOD_ADVANCEDSETTINGS_get_HRTFMinAngle(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_HRTFMinAngle(long pointer, float HRTFMinAngle);
	protected final static native float FMOD_ADVANCEDSETTINGS_get_HRTFMaxAngle(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_HRTFMaxAngle(long pointer, float HRTFMaxAngle);
	protected final static native float FMOD_ADVANCEDSETTINGS_get_HRTFFreq(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_HRTFFreq(long pointer, float HRTFFreq);
	protected final static native float FMOD_ADVANCEDSETTINGS_get_vol0virtualvol(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_vol0virtualvol(long pointer, float vol0virtualvol);
	protected final static native int FMOD_ADVANCEDSETTINGS_get_eventqueuesize(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_eventqueuesize(long pointer, int eventqueuesize);
	protected final static native int FMOD_ADVANCEDSETTINGS_get_defaultDecodeBufferSize(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_defaultDecodeBufferSize(long pointer, int defaultDecodeBufferSize);
	protected final static native String FMOD_ADVANCEDSETTINGS_get_debugLogFilename(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_debugLogFilename(long pointer, byte[] debugLogFilename);
	protected final static native short FMOD_ADVANCEDSETTINGS_get_profileport(long pointer);
	protected final static native void FMOD_ADVANCEDSETTINGS_set_profileport(long pointer, short profileport);

}