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

class FmodExJNI
{
	static
	{
		if(!Init.isLibrariesLoaded())
		{
			throw new RuntimeException("Libraries not loaded ! Use Init.loadLibraries() before using NativeFmodEx.");
		}
	}

	protected final static native String FMOD_ErrorString(int errCode);


	protected final static native int FmodEx_Memory_Initialize(ByteBuffer poolmem, long poolmem_, int poollen, boolean useralloc, boolean userrealloc, boolean userfree, int memtypeflags);
	protected final static native int FmodEx_Memory_GetStats(IntBuffer currentalloced, long currentalloced_, IntBuffer maxalloced, long maxalloced_);
	protected final static native int FmodEx_Debug_SetLevel(int level);
	protected final static native int FmodEx_Debug_GetLevel(IntBuffer level, long level_);
	protected final static native int FmodEx_File_SetDiskBusy(int busy);
	protected final static native int FmodEx_File_GetDiskBusy(IntBuffer busy, long busy_);
	protected final static native int FmodEx_System_Create(System system);
	protected final static native int System_release(long pointer);
	protected final static native int System_setOutput(long pointer, int output);
	protected final static native int System_getOutput(long pointer, IntBuffer outputPointer);
	protected final static native int System_getNumDrivers(long pointer, IntBuffer numdrivers, long numdrivers_);
	protected final static native int System_getDriverInfo(long pointer, int id, ByteBuffer name, long name_, int namelen, long guid);
	protected final static native int System_getDriverCaps(long pointer, int id, IntBuffer caps, long caps_, IntBuffer minfrequency, long minfrequency_, IntBuffer maxfrequency, long maxfrequency_, IntBuffer controlpanelspeakermodePointer);
	protected final static native int System_setDriver(long pointer, int driver);
	protected final static native int System_getDriver(long pointer, IntBuffer driver, long driver_);
	protected final static native int System_setHardwareChannels(long pointer, int min2d, int max2d, int min3d, int max3d);
	protected final static native int System_setSoftwareChannels(long pointer, int numsoftwarechannels);
	protected final static native int System_getSoftwareChannels(long pointer, IntBuffer numsoftwarechannels, long numsoftwarechannels_);
	protected final static native int System_setSoftwareFormat(long pointer, int samplerate, int format, int numoutputchannels, int maxinputchannels, int resamplemethod);
	protected final static native int System_getSoftwareFormat(long pointer, IntBuffer samplerate, long samplerate_, IntBuffer formatPointer, IntBuffer numoutputchannels, long numoutputchannels_, IntBuffer maxinputchannels, long maxinputchannels_, IntBuffer resamplemethodPointer, IntBuffer bits, long bits_);
	protected final static native int System_setDSPBufferSize(long pointer, int bufferlength, int numbuffers);
	protected final static native int System_getDSPBufferSize(long pointer, IntBuffer bufferlength, long bufferlength_, IntBuffer numbuffers, long numbuffers_);
	protected final static native int System_setFileSystem(long pointer, boolean useropen, boolean userclose, boolean userread, boolean userseek, int blockalign);
	protected final static native int System_attachFileSystem(long pointer, boolean useropen, boolean userclose, boolean userread, boolean userseek);
	protected final static native int System_setAdvancedSettings(long pointer, long settings);
	protected final static native int System_getAdvancedSettings(long pointer, long settings);
	protected final static native int System_setSpeakerMode(long pointer, int speakermode);
	protected final static native int System_getSpeakerMode(long pointer, IntBuffer speakermodePointer);
	protected final static native int System_setCallback(long pointer, boolean callback);
	protected final static native int System_setPluginPath(long pointer, byte[] path);
	protected final static native int System_loadPlugin(long pointer, byte[] filename, IntBuffer plugintypePointer, IntBuffer index, long index_);
	protected final static native int System_getNumPlugins(long pointer, int plugintype, IntBuffer numplugins, long numplugins_);
	protected final static native int System_getPluginInfo(long pointer, int plugintype, int index, ByteBuffer name, long name_, int namelen, IntBuffer version, long version_);
	protected final static native int System_unloadPlugin(long pointer, int plugintype, int index);
	protected final static native int System_setOutputByPlugin(long pointer, int index);
	protected final static native int System_getOutputByPlugin(long pointer, IntBuffer index, long index_);
	protected final static native int System_createCodec(long pointer, long description);
	protected final static native int System_init(long pointer, int maxchannels, int flags, long extradriverdata);
	protected final static native int System_close(long pointer);
	protected final static native int System_update(long pointer);
	protected final static native int System_set3DSettings(long pointer, float dopplerscale, float distancefactor, float rolloffscale);
	protected final static native int System_get3DSettings(long pointer, FloatBuffer dopplerscale, long dopplerscale_, FloatBuffer distancefactor, long distancefactor_, FloatBuffer rolloffscale, long rolloffscale_);
	protected final static native int System_set3DNumListeners(long pointer, int numlisteners);
	protected final static native int System_get3DNumListeners(long pointer, IntBuffer numlisteners, long numlisteners_);
	protected final static native int System_set3DListenerAttributes(long pointer, int listener, long pos, long vel, long forward, long up);
	protected final static native int System_get3DListenerAttributes(long pointer, int listener, long pos, long vel, long forward, long up);
	protected final static native int System_set3DRolloffCallback(long pointer, boolean callback);
	protected final static native int System_set3DSpeakerPosition(long pointer, int speaker, float x, float y, boolean active);
	protected final static native int System_get3DSpeakerPosition(long pointer, int speaker, FloatBuffer x, long x_, FloatBuffer y, long y_, ByteBuffer active, long active_);
	protected final static native int System_setStreamBufferSize(long pointer, int filebuffersize, int filebuffersizetype);
	protected final static native int System_getStreamBufferSize(long pointer, IntBuffer filebuffersize, long filebuffersize_, IntBuffer filebuffersizetype, long filebuffersizetype_);
	protected final static native int System_getVersion(long pointer, IntBuffer version, long version_);
	protected final static native int System_getOutputHandle(long pointer, Pointer handle);
	protected final static native int System_getChannelsPlaying(long pointer, IntBuffer channels, long channels_);
	protected final static native int System_getHardwareChannels(long pointer, IntBuffer num2d, long num2d_, IntBuffer num3d, long num3d_, IntBuffer total, long total_);
	protected final static native int System_getCPUUsage(long pointer, FloatBuffer dsp, long dsp_, FloatBuffer stream, long stream_, FloatBuffer update, long update_, FloatBuffer total, long total_);
	protected final static native int System_getSoundRAM(long pointer, IntBuffer currentalloced, long currentalloced_, IntBuffer maxalloced, long maxalloced_, IntBuffer total, long total_);
	protected final static native int System_getNumCDROMDrives(long pointer, IntBuffer numdrives, long numdrives_);
	protected final static native int System_getCDROMDriveName(long pointer, int drive, ByteBuffer drivename, long drivename_, int drivenamelen, ByteBuffer scsiname, long scsiname_, int scsinamelen, ByteBuffer devicename, long devicename_, int devicenamelen);
	protected final static native int System_getSpectrum(long pointer, FloatBuffer spectrumarray, long spectrumarray_, int numvalues, int channeloffset, int windowtype);
	protected final static native int System_getWaveData(long pointer, FloatBuffer wavearray, long wavearray_, int numvalues, int channeloffset);
	protected final static native int System_createSound(long pointer, byte[] name_or_data, int mode, long exinfo, Sound sound);
	protected final static native int System_createSound(long pointer, ByteBuffer name_or_data, long name_or_data_, int mode, long exinfo, Sound sound);
	protected final static native int System_createStream(long pointer, byte[] name_or_data, int mode, long exinfo, Sound sound);
	protected final static native int System_createStream(long pointer, ByteBuffer name_or_data, long name_or_data_, int mode, long exinfo, Sound sound);
	protected final static native int System_createDSP(long pointer, long description, DSP dsp);
	protected final static native int System_createDSPByType(long pointer, int type, DSP dsp);
	protected final static native int System_createDSPByIndex(long pointer, int index, DSP dsp);
	protected final static native int System_createChannelGroup(long pointer, byte[] name, ChannelGroup channelgroup);
	protected final static native int System_createSoundGroup(long pointer, byte[] name, SoundGroup soundgroup);
	protected final static native int System_createReverb(long pointer, Reverb reverb);
	protected final static native int System_playSound(long pointer, int channelid, long sound, boolean paused, Channel channel);
	protected final static native int System_playDSP(long pointer, int channelid, long dsp, boolean paused, Channel channel);
	protected final static native int System_getChannel(long pointer, int channelid, Channel channel);
	protected final static native int System_getMasterChannelGroup(long pointer, ChannelGroup channelgroup);
	protected final static native int System_getMasterSoundGroup(long pointer, SoundGroup soundgroup);
	protected final static native int System_setReverbProperties(long pointer, long prop);
	protected final static native int System_getReverbProperties(long pointer, long prop);
	protected final static native int System_setReverbAmbientProperties(long pointer, long prop);
	protected final static native int System_getReverbAmbientProperties(long pointer, long prop);
	protected final static native int System_getDSPHead(long pointer, DSP dsp);
	protected final static native int System_addDSP(long pointer, long dsp, DSPConnection connection);
	protected final static native int System_lockDSP(long pointer);
	protected final static native int System_unlockDSP(long pointer);
	protected final static native int System_getDSPClock(long pointer, IntBuffer hi, long hi_, IntBuffer lo, long lo_);
	protected final static native int System_setRecordDriver(long pointer, int driver);
	protected final static native int System_getRecordDriver(long pointer, IntBuffer driver, long driver_);
	protected final static native int System_getRecordNumDrivers(long pointer, IntBuffer numdrivers, long numdrivers_);
	protected final static native int System_getRecordDriverInfo(long pointer, int id, ByteBuffer name, long name_, int namelen, long guid);
	protected final static native int System_getRecordDriverCaps(long pointer, int id, IntBuffer caps, long caps_, IntBuffer minfrequency, long minfrequency_, IntBuffer maxfrequency, long maxfrequency_);
	protected final static native int System_getRecordPosition(long pointer, IntBuffer position, long position_);
	protected final static native int System_recordStart(long pointer, long sound, boolean loop);
	protected final static native int System_recordStop(long pointer);
	protected final static native int System_isRecording(long pointer, ByteBuffer recording, long recording_);
	protected final static native int System_createGeometry(long pointer, int maxpolygons, int maxvertices, Geometry geometry);
	protected final static native int System_setGeometrySettings(long pointer, float maxworldsize);
	protected final static native int System_getGeometrySettings(long pointer, FloatBuffer maxworldsize, long maxworldsize_);
	protected final static native int System_loadGeometry(long pointer, ByteBuffer data, long data_, int datasize, Geometry geometry);
	protected final static native int System_setNetworkProxy(long pointer, byte[] proxy);
	protected final static native int System_getNetworkProxy(long pointer, ByteBuffer proxy, long proxy_, int proxylen);
	protected final static native int System_setNetworkTimeout(long pointer, int timeout);
	protected final static native int System_getNetworkTimeout(long pointer, IntBuffer timeout, long timeout_);
	protected final static native int System_setUserData(long pointer, long userdata);
	protected final static native int System_getUserData(long pointer, Pointer userdata);
	protected final static native int Sound_release(long pointer);
	protected final static native int Sound_getSystemObject(long pointer, System system);
	protected final static native int Sound_lock(long pointer, int offset, int length, ByteBuffer[] ptr1, ByteBuffer[] ptr2, IntBuffer len1, long len1_, IntBuffer len2, long len2_);
	protected final static native int Sound_unlock(long pointer, ByteBuffer ptr1, long ptr1_, ByteBuffer ptr2, long ptr2_, int len1, int len2);
	protected final static native int Sound_setDefaults(long pointer, float frequency, float volume, float pan, int priority);
	protected final static native int Sound_getDefaults(long pointer, FloatBuffer frequency, long frequency_, FloatBuffer volume, long volume_, FloatBuffer pan, long pan_, IntBuffer priority, long priority_);
	protected final static native int Sound_setVariations(long pointer, float frequencyvar, float volumevar, float panvar);
	protected final static native int Sound_getVariations(long pointer, FloatBuffer frequencyvar, long frequencyvar_, FloatBuffer volumevar, long volumevar_, FloatBuffer panvar, long panvar_);
	protected final static native int Sound_set3DMinMaxDistance(long pointer, float min, float max);
	protected final static native int Sound_get3DMinMaxDistance(long pointer, FloatBuffer min, long min_, FloatBuffer max, long max_);
	protected final static native int Sound_set3DConeSettings(long pointer, float insideconeangle, float outsideconeangle, float outsidevolume);
	protected final static native int Sound_get3DConeSettings(long pointer, FloatBuffer insideconeangle, long insideconeangle_, FloatBuffer outsideconeangle, long outsideconeangle_, FloatBuffer outsidevolume, long outsidevolume_);
	protected final static native int Sound_set3DCustomRolloff(long pointer, long points, int numpoints);
	protected final static native int Sound_get3DCustomRolloff(long pointer, FMOD_VECTOR points, IntBuffer numpoints, long numpoints_);
	protected final static native int Sound_setSubSound(long pointer, int index, long subsound);
	protected final static native int Sound_getSubSound(long pointer, int index, Sound subsound);
	protected final static native int Sound_setSubSoundSentence(long pointer, IntBuffer subsoundlist, long subsoundlist_, int numsubsounds);
	protected final static native int Sound_getName(long pointer, ByteBuffer name, long name_, int namelen);
	protected final static native int Sound_getLength(long pointer, IntBuffer length, long length_, int lengthtype);
	protected final static native int Sound_getFormat(long pointer, IntBuffer typePointer, IntBuffer formatPointer, IntBuffer channels, long channels_, IntBuffer bits, long bits_);
	protected final static native int Sound_getNumSubSounds(long pointer, IntBuffer numsubsounds, long numsubsounds_);
	protected final static native int Sound_getNumTags(long pointer, IntBuffer numtags, long numtags_, IntBuffer numtagsupdated, long numtagsupdated_);
	protected final static native int Sound_getTag(long pointer, byte[] name, int index, long tag);
	protected final static native int Sound_getOpenState(long pointer, IntBuffer openstatePointer, IntBuffer percentbuffered, long percentbuffered_, ByteBuffer starving, long starving_);
	protected final static native int Sound_readData(long pointer, ByteBuffer buffer, long buffer_, int lenbytes, IntBuffer read, long read_);
	protected final static native int Sound_seekData(long pointer, int pcm);
	protected final static native int Sound_setSoundGroup(long pointer, long soundgroup);
	protected final static native int Sound_getSoundGroup(long pointer, SoundGroup soundgroup);
	protected final static native int Sound_getNumSyncPoints(long pointer, IntBuffer numsyncpoints, long numsyncpoints_);
	protected final static native int Sound_getSyncPoint(long pointer, int index, FMOD_SYNCPOINT point);
	protected final static native int Sound_getSyncPointInfo(long pointer, long point, ByteBuffer name, long name_, int namelen, IntBuffer offset, long offset_, int offsettype);
	protected final static native int Sound_addSyncPoint(long pointer, int offset, int offsettype, byte[] name, FMOD_SYNCPOINT point);
	protected final static native int Sound_deleteSyncPoint(long pointer, long point);
	protected final static native int Sound_setMode(long pointer, int mode);
	protected final static native int Sound_getMode(long pointer, IntBuffer mode, long mode_);
	protected final static native int Sound_setLoopCount(long pointer, int loopcount);
	protected final static native int Sound_getLoopCount(long pointer, IntBuffer loopcount, long loopcount_);
	protected final static native int Sound_setLoopPoints(long pointer, int loopstart, int loopstarttype, int loopend, int loopendtype);
	protected final static native int Sound_getLoopPoints(long pointer, IntBuffer loopstart, long loopstart_, int loopstarttype, IntBuffer loopend, long loopend_, int loopendtype);
	protected final static native int Sound_getMusicNumChannels(long pointer, IntBuffer numchannels, long numchannels_);
	protected final static native int Sound_setMusicChannelVolume(long pointer, int channel, float volume);
	protected final static native int Sound_getMusicChannelVolume(long pointer, int channel, FloatBuffer volume, long volume_);
	protected final static native int Sound_setUserData(long pointer, long userdata);
	protected final static native int Sound_getUserData(long pointer, Pointer userdata);
	protected final static native int Channel_getSystemObject(long pointer, System system);
	protected final static native int Channel_stop(long pointer);
	protected final static native int Channel_setPaused(long pointer, boolean paused);
	protected final static native int Channel_getPaused(long pointer, ByteBuffer paused, long paused_);
	protected final static native int Channel_setVolume(long pointer, float volume);
	protected final static native int Channel_getVolume(long pointer, FloatBuffer volume, long volume_);
	protected final static native int Channel_setFrequency(long pointer, float frequency);
	protected final static native int Channel_getFrequency(long pointer, FloatBuffer frequency, long frequency_);
	protected final static native int Channel_setPan(long pointer, float pan);
	protected final static native int Channel_getPan(long pointer, FloatBuffer pan, long pan_);
	protected final static native int Channel_setDelay(long pointer, int delaytype, int delayhi, int delaylo);
	protected final static native int Channel_getDelay(long pointer, int delaytype, IntBuffer delayhi, long delayhi_, IntBuffer delaylo, long delaylo_);
	protected final static native int Channel_setSpeakerMix(long pointer, float frontleft, float frontright, float center, float lfe, float backleft, float backright, float sideleft, float sideright);
	protected final static native int Channel_getSpeakerMix(long pointer, FloatBuffer frontleft, long frontleft_, FloatBuffer frontright, long frontright_, FloatBuffer center, long center_, FloatBuffer lfe, long lfe_, FloatBuffer backleft, long backleft_, FloatBuffer backright, long backright_, FloatBuffer sideleft, long sideleft_, FloatBuffer sideright, long sideright_);
	protected final static native int Channel_setSpeakerLevels(long pointer, int speaker, FloatBuffer levels, long levels_, int numlevels);
	protected final static native int Channel_getSpeakerLevels(long pointer, int speaker, FloatBuffer levels, long levels_, int numlevels);
	protected final static native int Channel_setInputChannelMix(long pointer, FloatBuffer levels, long levels_, int numlevels);
	protected final static native int Channel_getInputChannelMix(long pointer, FloatBuffer levels, long levels_, int numlevels);
	protected final static native int Channel_setMute(long pointer, boolean mute);
	protected final static native int Channel_getMute(long pointer, ByteBuffer mute, long mute_);
	protected final static native int Channel_setPriority(long pointer, int priority);
	protected final static native int Channel_getPriority(long pointer, IntBuffer priority, long priority_);
	protected final static native int Channel_setPosition(long pointer, int position, int postype);
	protected final static native int Channel_getPosition(long pointer, IntBuffer position, long position_, int postype);
	protected final static native int Channel_setReverbProperties(long pointer, long prop);
	protected final static native int Channel_getReverbProperties(long pointer, long prop);
	protected final static native int Channel_setChannelGroup(long pointer, long channelgroup);
	protected final static native int Channel_getChannelGroup(long pointer, ChannelGroup channelgroup);
	protected final static native int Channel_setCallback(long pointer, int type, boolean callback, int command);
	protected final static native int Channel_set3DAttributes(long pointer, long pos, long vel);
	protected final static native int Channel_get3DAttributes(long pointer, long pos, long vel);
	protected final static native int Channel_set3DMinMaxDistance(long pointer, float mindistance, float maxdistance);
	protected final static native int Channel_get3DMinMaxDistance(long pointer, FloatBuffer mindistance, long mindistance_, FloatBuffer maxdistance, long maxdistance_);
	protected final static native int Channel_set3DConeSettings(long pointer, float insideconeangle, float outsideconeangle, float outsidevolume);
	protected final static native int Channel_get3DConeSettings(long pointer, FloatBuffer insideconeangle, long insideconeangle_, FloatBuffer outsideconeangle, long outsideconeangle_, FloatBuffer outsidevolume, long outsidevolume_);
	protected final static native int Channel_set3DConeOrientation(long pointer, long orientation);
	protected final static native int Channel_get3DConeOrientation(long pointer, long orientation);
	protected final static native int Channel_set3DCustomRolloff(long pointer, long points, int numpoints);
	protected final static native int Channel_get3DCustomRolloff(long pointer, FMOD_VECTOR points, IntBuffer numpoints, long numpoints_);
	protected final static native int Channel_set3DOcclusion(long pointer, float directocclusion, float reverbocclusion);
	protected final static native int Channel_get3DOcclusion(long pointer, FloatBuffer directocclusion, long directocclusion_, FloatBuffer reverbocclusion, long reverbocclusion_);
	protected final static native int Channel_set3DSpread(long pointer, float angle);
	protected final static native int Channel_get3DSpread(long pointer, FloatBuffer angle, long angle_);
	protected final static native int Channel_set3DPanLevel(long pointer, float level);
	protected final static native int Channel_get3DPanLevel(long pointer, FloatBuffer level, long level_);
	protected final static native int Channel_set3DDopplerLevel(long pointer, float level);
	protected final static native int Channel_get3DDopplerLevel(long pointer, FloatBuffer level, long level_);
	protected final static native int Channel_getDSPHead(long pointer, DSP dsp);
	protected final static native int Channel_addDSP(long pointer, long dsp, DSPConnection connection);
	protected final static native int Channel_isPlaying(long pointer, ByteBuffer isplaying, long isplaying_);
	protected final static native int Channel_isVirtual(long pointer, ByteBuffer isvirtual, long isvirtual_);
	protected final static native int Channel_getAudibility(long pointer, FloatBuffer audibility, long audibility_);
	protected final static native int Channel_getCurrentSound(long pointer, Sound sound);
	protected final static native int Channel_getSpectrum(long pointer, FloatBuffer spectrumarray, long spectrumarray_, int numvalues, int channeloffset, int windowtype);
	protected final static native int Channel_getWaveData(long pointer, FloatBuffer wavearray, long wavearray_, int numvalues, int channeloffset);
	protected final static native int Channel_getIndex(long pointer, IntBuffer index, long index_);
	protected final static native int Channel_setMode(long pointer, int mode);
	protected final static native int Channel_getMode(long pointer, IntBuffer mode, long mode_);
	protected final static native int Channel_setLoopCount(long pointer, int loopcount);
	protected final static native int Channel_getLoopCount(long pointer, IntBuffer loopcount, long loopcount_);
	protected final static native int Channel_setLoopPoints(long pointer, int loopstart, int loopstarttype, int loopend, int loopendtype);
	protected final static native int Channel_getLoopPoints(long pointer, IntBuffer loopstart, long loopstart_, int loopstarttype, IntBuffer loopend, long loopend_, int loopendtype);
	protected final static native int Channel_setUserData(long pointer, long userdata);
	protected final static native int Channel_getUserData(long pointer, Pointer userdata);
	protected final static native int ChannelGroup_release(long pointer);
	protected final static native int ChannelGroup_getSystemObject(long pointer, System system);
	protected final static native int ChannelGroup_setVolume(long pointer, float volume);
	protected final static native int ChannelGroup_getVolume(long pointer, FloatBuffer volume, long volume_);
	protected final static native int ChannelGroup_setPitch(long pointer, float pitch);
	protected final static native int ChannelGroup_getPitch(long pointer, FloatBuffer pitch, long pitch_);
	protected final static native int ChannelGroup_set3DOcclusion(long pointer, float directocclusion, float reverbocclusion);
	protected final static native int ChannelGroup_get3DOcclusion(long pointer, FloatBuffer directocclusion, long directocclusion_, FloatBuffer reverbocclusion, long reverbocclusion_);
	protected final static native int ChannelGroup_setPaused(long pointer, boolean paused);
	protected final static native int ChannelGroup_getPaused(long pointer, ByteBuffer paused, long paused_);
	protected final static native int ChannelGroup_setMute(long pointer, boolean mute);
	protected final static native int ChannelGroup_getMute(long pointer, ByteBuffer mute, long mute_);
	protected final static native int ChannelGroup_stop(long pointer);
	protected final static native int ChannelGroup_overrideVolume(long pointer, float volume);
	protected final static native int ChannelGroup_overrideFrequency(long pointer, float frequency);
	protected final static native int ChannelGroup_overridePan(long pointer, float pan);
	protected final static native int ChannelGroup_overrideReverbProperties(long pointer, long prop);
	protected final static native int ChannelGroup_override3DAttributes(long pointer, long pos, long vel);
	protected final static native int ChannelGroup_overrideSpeakerMix(long pointer, float frontleft, float frontright, float center, float lfe, float backleft, float backright, float sideleft, float sideright);
	protected final static native int ChannelGroup_addGroup(long pointer, long group);
	protected final static native int ChannelGroup_getNumGroups(long pointer, IntBuffer numgroups, long numgroups_);
	protected final static native int ChannelGroup_getGroup(long pointer, int index, ChannelGroup group);
	protected final static native int ChannelGroup_getParentGroup(long pointer, ChannelGroup group);
	protected final static native int ChannelGroup_getDSPHead(long pointer, DSP dsp);
	protected final static native int ChannelGroup_addDSP(long pointer, long dsp, DSPConnection connection);
	protected final static native int ChannelGroup_getName(long pointer, ByteBuffer name, long name_, int namelen);
	protected final static native int ChannelGroup_getNumChannels(long pointer, IntBuffer numchannels, long numchannels_);
	protected final static native int ChannelGroup_getChannel(long pointer, int index, Channel channel);
	protected final static native int ChannelGroup_getSpectrum(long pointer, FloatBuffer spectrumarray, long spectrumarray_, int numvalues, int channeloffset, int windowtype);
	protected final static native int ChannelGroup_getWaveData(long pointer, FloatBuffer wavearray, long wavearray_, int numvalues, int channeloffset);
	protected final static native int ChannelGroup_setUserData(long pointer, long userdata);
	protected final static native int ChannelGroup_getUserData(long pointer, Pointer userdata);
	protected final static native int SoundGroup_release(long pointer);
	protected final static native int SoundGroup_getSystemObject(long pointer, System system);
	protected final static native int SoundGroup_setMaxAudible(long pointer, int maxaudible);
	protected final static native int SoundGroup_getMaxAudible(long pointer, IntBuffer maxaudible, long maxaudible_);
	protected final static native int SoundGroup_setMaxAudibleBehavior(long pointer, int behavior);
	protected final static native int SoundGroup_getMaxAudibleBehavior(long pointer, IntBuffer behaviorPointer);
	protected final static native int SoundGroup_setMuteFadeSpeed(long pointer, float speed);
	protected final static native int SoundGroup_getMuteFadeSpeed(long pointer, FloatBuffer speed, long speed_);
	protected final static native int SoundGroup_setVolume(long pointer, float volume);
	protected final static native int SoundGroup_getVolume(long pointer, FloatBuffer volume, long volume_);
	protected final static native int SoundGroup_stop(long pointer);
	protected final static native int SoundGroup_getName(long pointer, ByteBuffer name, long name_, int namelen);
	protected final static native int SoundGroup_getNumSounds(long pointer, IntBuffer numsounds, long numsounds_);
	protected final static native int SoundGroup_getSound(long pointer, int index, Sound sound);
	protected final static native int SoundGroup_getNumPlaying(long pointer, IntBuffer numplaying, long numplaying_);
	protected final static native int SoundGroup_setUserData(long pointer, long userdata);
	protected final static native int SoundGroup_getUserData(long pointer, Pointer userdata);
	protected final static native int DSP_release(long pointer);
	protected final static native int DSP_getSystemObject(long pointer, System system);
	protected final static native int DSP_addInput(long pointer, long target, DSPConnection connection);
	protected final static native int DSP_disconnectFrom(long pointer, long target);
	protected final static native int DSP_disconnectAll(long pointer, boolean inputs, boolean outputs);
	protected final static native int DSP_remove(long pointer);
	protected final static native int DSP_getNumInputs(long pointer, IntBuffer numinputs, long numinputs_);
	protected final static native int DSP_getNumOutputs(long pointer, IntBuffer numoutputs, long numoutputs_);
	protected final static native int DSP_getInput(long pointer, int index, DSP input, DSPConnection inputconnection);
	protected final static native int DSP_getOutput(long pointer, int index, DSP output, DSPConnection outputconnection);
	protected final static native int DSP_setActive(long pointer, boolean active);
	protected final static native int DSP_getActive(long pointer, ByteBuffer active, long active_);
	protected final static native int DSP_setBypass(long pointer, boolean bypass);
	protected final static native int DSP_getBypass(long pointer, ByteBuffer bypass, long bypass_);
	protected final static native int DSP_setSpeakerActive(long pointer, int speaker, boolean active);
	protected final static native int DSP_getSpeakerActive(long pointer, int speaker, ByteBuffer active, long active_);
	protected final static native int DSP_reset(long pointer);
	protected final static native int DSP_setParameter(long pointer, int index, float value);
	protected final static native int DSP_getParameter(long pointer, int index, FloatBuffer value, long value_, ByteBuffer valuestr, long valuestr_, int valuestrlen);
	protected final static native int DSP_getNumParameters(long pointer, IntBuffer numparams, long numparams_);
	protected final static native int DSP_getParameterInfo(long pointer, int index, ByteBuffer name, long name_, ByteBuffer label, long label_, ByteBuffer description, long description_, int descriptionlen, FloatBuffer min, long min_, FloatBuffer max, long max_);

	protected final static native int DSP_getInfo(long pointer, ByteBuffer name, long name_, IntBuffer version, long version_, IntBuffer channels, long channels_, IntBuffer configwidth, long configwidth_, IntBuffer configheight, long configheight_);
	protected final static native int DSP_getType(long pointer, IntBuffer typePointer);
	protected final static native int DSP_setDefaults(long pointer, float frequency, float volume, float pan, int priority);
	protected final static native int DSP_getDefaults(long pointer, FloatBuffer frequency, long frequency_, FloatBuffer volume, long volume_, FloatBuffer pan, long pan_, IntBuffer priority, long priority_);
	protected final static native int DSP_setUserData(long pointer, long userdata);
	protected final static native int DSP_getUserData(long pointer, Pointer userdata);
	protected final static native int DSPConnection_getInput(long pointer, DSP input);
	protected final static native int DSPConnection_getOutput(long pointer, DSP output);
	protected final static native int DSPConnection_setMix(long pointer, float volume);
	protected final static native int DSPConnection_getMix(long pointer, FloatBuffer volume, long volume_);
	protected final static native int DSPConnection_setLevels(long pointer, int speaker, FloatBuffer levels, long levels_, int numlevels);
	protected final static native int DSPConnection_getLevels(long pointer, int speaker, FloatBuffer levels, long levels_, int numlevels);
	protected final static native int DSPConnection_setUserData(long pointer, long userdata);
	protected final static native int DSPConnection_getUserData(long pointer, Pointer userdata);
	protected final static native int Geometry_release(long pointer);
	protected final static native int Geometry_addPolygon(long pointer, float directocclusion, float reverbocclusion, boolean doublesided, int numvertices, long vertices, IntBuffer polygonindex, long polygonindex_);
	protected final static native int Geometry_getNumPolygons(long pointer, IntBuffer numpolygons, long numpolygons_);
	protected final static native int Geometry_getMaxPolygons(long pointer, IntBuffer maxpolygons, long maxpolygons_, IntBuffer maxvertices, long maxvertices_);
	protected final static native int Geometry_getPolygonNumVertices(long pointer, int index, IntBuffer numvertices, long numvertices_);
	protected final static native int Geometry_setPolygonVertex(long pointer, int index, int vertexindex, long vertex);
	protected final static native int Geometry_getPolygonVertex(long pointer, int index, int vertexindex, long vertex);
	protected final static native int Geometry_setPolygonAttributes(long pointer, int index, float directocclusion, float reverbocclusion, boolean doublesided);
	protected final static native int Geometry_getPolygonAttributes(long pointer, int index, FloatBuffer directocclusion, long directocclusion_, FloatBuffer reverbocclusion, long reverbocclusion_, ByteBuffer doublesided, long doublesided_);
	protected final static native int Geometry_setActive(long pointer, boolean active);
	protected final static native int Geometry_getActive(long pointer, ByteBuffer active, long active_);
	protected final static native int Geometry_setRotation(long pointer, long forward, long up);
	protected final static native int Geometry_getRotation(long pointer, long forward, long up);
	protected final static native int Geometry_setPosition(long pointer, long position);
	protected final static native int Geometry_getPosition(long pointer, long position);
	protected final static native int Geometry_setScale(long pointer, long scale);
	protected final static native int Geometry_getScale(long pointer, long scale);
	protected final static native int Geometry_save(long pointer, ByteBuffer data, long data_, IntBuffer datasize, long datasize_);
	protected final static native int Geometry_setUserData(long pointer, long userdata);
	protected final static native int Geometry_getUserData(long pointer, Pointer userdata);
	protected final static native int Reverb_release(long pointer);
	protected final static native int Reverb_set3DAttributes(long pointer, long position, float mindistance, float maxdistance);
	protected final static native int Reverb_get3DAttributes(long pointer, long position, FloatBuffer mindistance, long mindistance_, FloatBuffer maxdistance, long maxdistance_);
	protected final static native int Reverb_setProperties(long pointer, long properties);
	protected final static native int Reverb_getProperties(long pointer, long properties);
	protected final static native int Reverb_setActive(long pointer, boolean active);
	protected final static native int Reverb_getActive(long pointer, ByteBuffer active, long active_);
	protected final static native int Reverb_setUserData(long pointer, long userdata);
	protected final static native int Reverb_getUserData(long pointer, Pointer userdata);
}