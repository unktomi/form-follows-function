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
#include "Utils.h"
#include "Pointer.h"
#include "fmod.h"
#include "fmod.hpp"
#include "fmod_codec.h"
#include "fmod_dsp.h"
#include "fmod_output.h"
#include "org_jouvieje_FmodEx_FmodExJNI.h"
#include "CallbackManager.h"

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1release(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->release();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getSystemObject(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsystem) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::System *system/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getSystemObject(&system);

	if(jsystem) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::System **)&newAddress = system;
		setPointerAddress(java_env, jsystem, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1setVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float volume = (float)jvolume;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->setVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvolume, jlong jvolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *volume = 0;
	if(jvolume) {
		volume = (float *)((char *)java_env->GetDirectBufferAddress(jvolume)+jvolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1setPitch(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jpitch) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float pitch = (float)jpitch;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->setPitch(pitch);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getPitch(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpitch, jlong jpitch_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *pitch = 0;
	if(jpitch) {
		pitch = (float *)((char *)java_env->GetDirectBufferAddress(jpitch)+jpitch_);
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getPitch(pitch);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1set3DOcclusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jdirectocclusion, jfloat jreverbocclusion) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float directocclusion = (float)jdirectocclusion;
	float reverbocclusion = (float)jreverbocclusion;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->set3DOcclusion(directocclusion, reverbocclusion);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1get3DOcclusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdirectocclusion, jlong jdirectocclusion_, jobject jreverbocclusion, jlong jreverbocclusion_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *directocclusion = 0;
	if(jdirectocclusion) {
		directocclusion = (float *)((char *)java_env->GetDirectBufferAddress(jdirectocclusion)+jdirectocclusion_);
	}
	float *reverbocclusion = 0;
	if(jreverbocclusion) {
		reverbocclusion = (float *)((char *)java_env->GetDirectBufferAddress(jreverbocclusion)+jreverbocclusion_);
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->get3DOcclusion(directocclusion, reverbocclusion);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1setPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jpaused) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool paused = (bool)(jpaused != 0);

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->setPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpaused, jlong jpaused_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *paused = 0;
	if(jpaused) {
		paused = (bool *)((char *)java_env->GetDirectBufferAddress(jpaused)+jpaused_);
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1setMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jmute) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool mute = (bool)(jmute != 0);

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->setMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmute, jlong jmute_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *mute = 0;
	if(jmute) {
		mute = (bool *)((char *)java_env->GetDirectBufferAddress(jmute)+jmute_);
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1stop(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->stop();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1overrideVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float volume = (float)jvolume;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->overrideVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1overrideFrequency(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jfrequency) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float frequency = (float)jfrequency;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->overrideFrequency(frequency);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1overridePan(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jpan) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float pan = (float)jpan;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->overridePan(pan);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1overrideReverbProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_CHANNELPROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_CHANNELPROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->overrideReverbProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1override3DAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jpos, jlong jvel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *pos = 0;
	if(jpos) {
		POINTER_TYPE posTmp = (POINTER_TYPE)jpos;
		pos = *(FMOD_VECTOR **)&posTmp;
	}
	FMOD_VECTOR *vel = 0;
	if(jvel) {
		POINTER_TYPE velTmp = (POINTER_TYPE)jvel;
		vel = *(FMOD_VECTOR **)&velTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->override3DAttributes(pos, vel);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1overrideSpeakerMix(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jfrontleft, jfloat jfrontright, jfloat jcenter, jfloat jlfe, jfloat jbackleft, jfloat jbackright, jfloat jsideleft, jfloat jsideright) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float frontleft = (float)jfrontleft;
	float frontright = (float)jfrontright;
	float center = (float)jcenter;
	float lfe = (float)jlfe;
	float backleft = (float)jbackleft;
	float backright = (float)jbackright;
	float sideleft = (float)jsideleft;
	float sideright = (float)jsideright;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->overrideSpeakerMix(frontleft, frontright, center, lfe, backleft, backright, sideleft, sideright);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1addGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::ChannelGroup *group = 0;
	if(jgroup) {
		POINTER_TYPE groupTmp = (POINTER_TYPE)jgroup;
		group = *(FMOD::ChannelGroup **)&groupTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->addGroup(group);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getNumGroups(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumgroups, jlong jnumgroups_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numgroups = 0;
	if(jnumgroups) {
		numgroups = (int *)((char *)java_env->GetDirectBufferAddress(jnumgroups)+jnumgroups_);
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getNumGroups(numgroups);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::ChannelGroup *group/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getGroup(index, &group);

	if(jgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::ChannelGroup **)&newAddress = group;
		setPointerAddress(java_env, jgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getParentGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::ChannelGroup *group/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getParentGroup(&group);

	if(jgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::ChannelGroup **)&newAddress = group;
		setPointerAddress(java_env, jgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getDSPHead(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdsp) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *dsp/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getDSPHead(&dsp);

	if(jdsp) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = dsp;
		setPointerAddress(java_env, jdsp, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1addDSP(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jdsp, jobject jconnection) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *dsp = 0;
	if(jdsp) {
		POINTER_TYPE dspTmp = (POINTER_TYPE)jdsp;
		dsp = *(FMOD::DSP **)&dspTmp;
	}
	FMOD::DSPConnection *connection/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->addDSP(dsp, &connection);

	if(jconnection) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSPConnection **)&newAddress = connection;
		setPointerAddress(java_env, jconnection, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getName(JNIEnv *java_env, jclass jcls, jlong jpointer, 	jobject jname, jlong jname_, jint jnamelen) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	int namelen = (int)jnamelen;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getName(name, namelen);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getNumChannels(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumchannels, jlong jnumchannels_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numchannels = 0;
	if(jnumchannels) {
		numchannels = (int *)((char *)java_env->GetDirectBufferAddress(jnumchannels)+jnumchannels_);
	}

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getNumChannels(numchannels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getChannel(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jchannel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::Channel *channel/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getChannel(index, &channel);

	if(jchannel) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Channel **)&newAddress = channel;
		setPointerAddress(java_env, jchannel, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getSpectrum(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jspectrumarray, jlong jspectrumarray_, jint jnumvalues, jint jchanneloffset, jint jwindowtype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *spectrumarray = 0;
	if(jspectrumarray) {
		spectrumarray = (float *)((char *)java_env->GetDirectBufferAddress(jspectrumarray)+jspectrumarray_);
	}
	int numvalues = (int)jnumvalues;
	int channeloffset = (int)jchanneloffset;
	FMOD_DSP_FFT_WINDOW windowtype = (FMOD_DSP_FFT_WINDOW)jwindowtype;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getSpectrum(spectrumarray, numvalues, channeloffset, windowtype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getWaveData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jwavearray, jlong jwavearray_, jint jnumvalues, jint jchanneloffset) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *wavearray = 0;
	if(jwavearray) {
		wavearray = (float *)((char *)java_env->GetDirectBufferAddress(jwavearray)+jwavearray_);
	}
	int numvalues = (int)jnumvalues;
	int channeloffset = (int)jchanneloffset;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getWaveData(wavearray, numvalues, channeloffset);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_ChannelGroup_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNELGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::ChannelGroup **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


