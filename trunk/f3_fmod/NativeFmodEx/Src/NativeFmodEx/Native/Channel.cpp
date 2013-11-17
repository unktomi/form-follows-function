/**
 * 				NativeFmodEx Project
 *
 * Want to use FMOD Ex API (www.fmod.org) in the Java language ? NativeFmodEx is made for you.
 * Copyright © 2005-2008 JÈrÙme JOUVIE (Jouvieje)
 *
 * Created on 23 feb. 2005
 * @version file v1.4.3
 * @author JÈrÙme JOUVIE (Jouvieje)
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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getSystemObject(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsystem) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::System *system/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getSystemObject(&system);

	if(jsystem) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::System **)&newAddress = system;
		setPointerAddress(java_env, jsystem, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1stop(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->stop();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jpaused) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool paused = (bool)(jpaused != 0);

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpaused, jlong jpaused_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *paused = 0;
	if(jpaused) {
		paused = (bool *)((char *)java_env->GetDirectBufferAddress(jpaused)+jpaused_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float volume = (float)jvolume;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvolume, jlong jvolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *volume = 0;
	if(jvolume) {
		volume = (float *)((char *)java_env->GetDirectBufferAddress(jvolume)+jvolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setFrequency(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jfrequency) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float frequency = (float)jfrequency;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setFrequency(frequency);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getFrequency(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jfrequency, jlong jfrequency_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *frequency = 0;
	if(jfrequency) {
		frequency = (float *)((char *)java_env->GetDirectBufferAddress(jfrequency)+jfrequency_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getFrequency(frequency);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setPan(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jpan) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float pan = (float)jpan;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setPan(pan);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getPan(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpan, jlong jpan_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *pan = 0;
	if(jpan) {
		pan = (float *)((char *)java_env->GetDirectBufferAddress(jpan)+jpan_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getPan(pan);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setDelay(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jdelaytype, jint jdelayhi, jint jdelaylo) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_DELAYTYPE delaytype = (FMOD_DELAYTYPE)jdelaytype;
	int delayhi = (int)jdelayhi;
	int delaylo = (int)jdelaylo;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setDelay(delaytype, delayhi, delaylo);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getDelay(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jdelaytype, jobject jdelayhi, jlong jdelayhi_, jobject jdelaylo, jlong jdelaylo_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_DELAYTYPE delaytype = (FMOD_DELAYTYPE)jdelaytype;
	unsigned int *delayhi = 0;
	if(jdelayhi) {
		delayhi = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jdelayhi)+jdelayhi_);
	}
	unsigned int *delaylo = 0;
	if(jdelaylo) {
		delaylo = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jdelaylo)+jdelaylo_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getDelay(delaytype, delayhi, delaylo);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setSpeakerMix(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jfrontleft, jfloat jfrontright, jfloat jcenter, jfloat jlfe, jfloat jbackleft, jfloat jbackright, jfloat jsideleft, jfloat jsideright) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
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

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setSpeakerMix(frontleft, frontright, center, lfe, backleft, backright, sideleft, sideright);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getSpeakerMix(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jfrontleft, jlong jfrontleft_, jobject jfrontright, jlong jfrontright_, jobject jcenter, jlong jcenter_, jobject jlfe, jlong jlfe_, jobject jbackleft, jlong jbackleft_, jobject jbackright, jlong jbackright_, jobject jsideleft, jlong jsideleft_, jobject jsideright, jlong jsideright_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *frontleft = 0;
	if(jfrontleft) {
		frontleft = (float *)((char *)java_env->GetDirectBufferAddress(jfrontleft)+jfrontleft_);
	}
	float *frontright = 0;
	if(jfrontright) {
		frontright = (float *)((char *)java_env->GetDirectBufferAddress(jfrontright)+jfrontright_);
	}
	float *center = 0;
	if(jcenter) {
		center = (float *)((char *)java_env->GetDirectBufferAddress(jcenter)+jcenter_);
	}
	float *lfe = 0;
	if(jlfe) {
		lfe = (float *)((char *)java_env->GetDirectBufferAddress(jlfe)+jlfe_);
	}
	float *backleft = 0;
	if(jbackleft) {
		backleft = (float *)((char *)java_env->GetDirectBufferAddress(jbackleft)+jbackleft_);
	}
	float *backright = 0;
	if(jbackright) {
		backright = (float *)((char *)java_env->GetDirectBufferAddress(jbackright)+jbackright_);
	}
	float *sideleft = 0;
	if(jsideleft) {
		sideleft = (float *)((char *)java_env->GetDirectBufferAddress(jsideleft)+jsideleft_);
	}
	float *sideright = 0;
	if(jsideright) {
		sideright = (float *)((char *)java_env->GetDirectBufferAddress(jsideright)+jsideright_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getSpeakerMix(frontleft, frontright, center, lfe, backleft, backright, sideleft, sideright);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setSpeakerLevels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeaker, jobject jlevels, jlong jlevels_, jint jnumlevels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKER speaker = (FMOD_SPEAKER)jspeaker;
	float *levels = 0;
	if(jlevels) {
		levels = (float *)((char *)java_env->GetDirectBufferAddress(jlevels)+jlevels_);
	}
	int numlevels = (int)jnumlevels;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setSpeakerLevels(speaker, levels, numlevels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getSpeakerLevels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeaker, jobject jlevels, jlong jlevels_, jint jnumlevels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKER speaker = (FMOD_SPEAKER)jspeaker;
	float *levels = 0;
	if(jlevels) {
		levels = (float *)((char *)java_env->GetDirectBufferAddress(jlevels)+jlevels_);
	}
	int numlevels = (int)jnumlevels;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getSpeakerLevels(speaker, levels, numlevels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setInputChannelMix(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jlevels, jlong jlevels_, jint jnumlevels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *levels = 0;
	if(jlevels) {
		levels = (float *)((char *)java_env->GetDirectBufferAddress(jlevels)+jlevels_);
	}
	int numlevels = (int)jnumlevels;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setInputChannelMix(levels, numlevels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getInputChannelMix(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jlevels, jlong jlevels_, jint jnumlevels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *levels = 0;
	if(jlevels) {
		levels = (float *)((char *)java_env->GetDirectBufferAddress(jlevels)+jlevels_);
	}
	int numlevels = (int)jnumlevels;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getInputChannelMix(levels, numlevels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jmute) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool mute = (bool)(jmute != 0);

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmute, jlong jmute_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *mute = 0;
	if(jmute) {
		mute = (bool *)((char *)java_env->GetDirectBufferAddress(jmute)+jmute_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setPriority(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jpriority) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int priority = (int)jpriority;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setPriority(priority);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getPriority(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpriority, jlong jpriority_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *priority = 0;
	if(jpriority) {
		priority = (int *)((char *)java_env->GetDirectBufferAddress(jpriority)+jpriority_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getPriority(priority);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setPosition(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jposition, jint jpostype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int position = (int)jposition;
	FMOD_TIMEUNIT postype = (FMOD_TIMEUNIT)jpostype;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setPosition(position, postype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getPosition(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jposition, jlong jposition_, jint jpostype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *position = 0;
	if(jposition) {
		position = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jposition)+jposition_);
	}
	FMOD_TIMEUNIT postype = (FMOD_TIMEUNIT)jpostype;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getPosition(position, postype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setReverbProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_CHANNELPROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_CHANNELPROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setReverbProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getReverbProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_CHANNELPROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_CHANNELPROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getReverbProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setChannelGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jchannelgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::ChannelGroup *channelgroup = 0;
	if(jchannelgroup) {
		POINTER_TYPE channelgroupTmp = (POINTER_TYPE)jchannelgroup;
		channelgroup = *(FMOD::ChannelGroup **)&channelgroupTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setChannelGroup(channelgroup);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getChannelGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jchannelgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::ChannelGroup *channelgroup/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getChannelGroup(&channelgroup);

	if(jchannelgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::ChannelGroup **)&newAddress = channelgroup;
		setPointerAddress(java_env, jchannelgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setCallback(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jtype, jboolean jcallback, jint jcommand) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	//FMOD_CHANNEL_CALLBACKTYPE type = (FMOD_CHANNEL_CALLBACKTYPE)jtype;
	//int command = (int)jcommand;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setCallback(jcallback == 0 ? NULL : FMOD_CHANNEL_CALLBACK_BRIDGE);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1set3DAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jpos, jlong jvel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
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

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->set3DAttributes(pos, vel);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1get3DAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jpos, jlong jvel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
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

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->get3DAttributes(pos, vel);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1set3DMinMaxDistance(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jmindistance, jfloat jmaxdistance) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float mindistance = (float)jmindistance;
	float maxdistance = (float)jmaxdistance;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->set3DMinMaxDistance(mindistance, maxdistance);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1get3DMinMaxDistance(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmindistance, jlong jmindistance_, jobject jmaxdistance, jlong jmaxdistance_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *mindistance = 0;
	if(jmindistance) {
		mindistance = (float *)((char *)java_env->GetDirectBufferAddress(jmindistance)+jmindistance_);
	}
	float *maxdistance = 0;
	if(jmaxdistance) {
		maxdistance = (float *)((char *)java_env->GetDirectBufferAddress(jmaxdistance)+jmaxdistance_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->get3DMinMaxDistance(mindistance, maxdistance);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1set3DConeSettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jinsideconeangle, jfloat joutsideconeangle, jfloat joutsidevolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float insideconeangle = (float)jinsideconeangle;
	float outsideconeangle = (float)joutsideconeangle;
	float outsidevolume = (float)joutsidevolume;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->set3DConeSettings(insideconeangle, outsideconeangle, outsidevolume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1get3DConeSettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jinsideconeangle, jlong jinsideconeangle_, jobject joutsideconeangle, jlong joutsideconeangle_, jobject joutsidevolume, jlong joutsidevolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *insideconeangle = 0;
	if(jinsideconeangle) {
		insideconeangle = (float *)((char *)java_env->GetDirectBufferAddress(jinsideconeangle)+jinsideconeangle_);
	}
	float *outsideconeangle = 0;
	if(joutsideconeangle) {
		outsideconeangle = (float *)((char *)java_env->GetDirectBufferAddress(joutsideconeangle)+joutsideconeangle_);
	}
	float *outsidevolume = 0;
	if(joutsidevolume) {
		outsidevolume = (float *)((char *)java_env->GetDirectBufferAddress(joutsidevolume)+joutsidevolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->get3DConeSettings(insideconeangle, outsideconeangle, outsidevolume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1set3DConeOrientation(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jorientation) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *orientation = 0;
	if(jorientation) {
		POINTER_TYPE orientationTmp = (POINTER_TYPE)jorientation;
		orientation = *(FMOD_VECTOR **)&orientationTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->set3DConeOrientation(orientation);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1get3DConeOrientation(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jorientation) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *orientation = 0;
	if(jorientation) {
		POINTER_TYPE orientationTmp = (POINTER_TYPE)jorientation;
		orientation = *(FMOD_VECTOR **)&orientationTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->get3DConeOrientation(orientation);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1set3DCustomRolloff(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jpoints, jint jnumpoints) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *points = 0;
	if(jpoints) {
		POINTER_TYPE pointsTmp = (POINTER_TYPE)jpoints;
		points = *(FMOD_VECTOR **)&pointsTmp;
	}
	int numpoints = (int)jnumpoints;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->set3DCustomRolloff(points, numpoints);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1get3DCustomRolloff(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpoints, jobject jnumpoints, jlong jnumpoints_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *points/* = 0*/;
	int *numpoints = 0;
	if(jnumpoints) {
		numpoints = (int *)((char *)java_env->GetDirectBufferAddress(jnumpoints)+jnumpoints_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->get3DCustomRolloff(&points, numpoints);

	if(jpoints) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD_VECTOR **)&newAddress = points;
		setPointerAddress(java_env, jpoints, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1set3DOcclusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jdirectocclusion, jfloat jreverbocclusion) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float directocclusion = (float)jdirectocclusion;
	float reverbocclusion = (float)jreverbocclusion;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->set3DOcclusion(directocclusion, reverbocclusion);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1get3DOcclusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdirectocclusion, jlong jdirectocclusion_, jobject jreverbocclusion, jlong jreverbocclusion_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
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

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->get3DOcclusion(directocclusion, reverbocclusion);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1set3DSpread(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jangle) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float angle = (float)jangle;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->set3DSpread(angle);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1get3DSpread(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jangle, jlong jangle_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *angle = 0;
	if(jangle) {
		angle = (float *)((char *)java_env->GetDirectBufferAddress(jangle)+jangle_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->get3DSpread(angle);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1set3DPanLevel(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jlevel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float level = (float)jlevel;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->set3DPanLevel(level);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1get3DPanLevel(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jlevel, jlong jlevel_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *level = 0;
	if(jlevel) {
		level = (float *)((char *)java_env->GetDirectBufferAddress(jlevel)+jlevel_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->get3DPanLevel(level);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1set3DDopplerLevel(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jlevel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float level = (float)jlevel;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->set3DDopplerLevel(level);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1get3DDopplerLevel(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jlevel, jlong jlevel_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *level = 0;
	if(jlevel) {
		level = (float *)((char *)java_env->GetDirectBufferAddress(jlevel)+jlevel_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->get3DDopplerLevel(level);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getDSPHead(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdsp) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *dsp/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getDSPHead(&dsp);

	if(jdsp) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = dsp;
		setPointerAddress(java_env, jdsp, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1addDSP(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jdsp, jobject jconnection) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *dsp = 0;
	if(jdsp) {
		POINTER_TYPE dspTmp = (POINTER_TYPE)jdsp;
		dsp = *(FMOD::DSP **)&dspTmp;
	}
	FMOD::DSPConnection *connection/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->addDSP(dsp, &connection);

	if(jconnection) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSPConnection **)&newAddress = connection;
		setPointerAddress(java_env, jconnection, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1isPlaying(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jisplaying, jlong jisplaying_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *isplaying = 0;
	if(jisplaying) {
		isplaying = (bool *)((char *)java_env->GetDirectBufferAddress(jisplaying)+jisplaying_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->isPlaying(isplaying);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1isVirtual(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jisvirtual, jlong jisvirtual_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *isvirtual = 0;
	if(jisvirtual) {
		isvirtual = (bool *)((char *)java_env->GetDirectBufferAddress(jisvirtual)+jisvirtual_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->isVirtual(isvirtual);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getAudibility(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jaudibility, jlong jaudibility_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *audibility = 0;
	if(jaudibility) {
		audibility = (float *)((char *)java_env->GetDirectBufferAddress(jaudibility)+jaudibility_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getAudibility(audibility);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getCurrentSound(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsound) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::Sound *sound/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getCurrentSound(&sound);

	if(jsound) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Sound **)&newAddress = sound;
		setPointerAddress(java_env, jsound, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getSpectrum(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jspectrumarray, jlong jspectrumarray_, jint jnumvalues, jint jchanneloffset, jint jwindowtype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
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

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getSpectrum(spectrumarray, numvalues, channeloffset, windowtype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getWaveData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jwavearray, jlong jwavearray_, jint jnumvalues, jint jchanneloffset) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *wavearray = 0;
	if(jwavearray) {
		wavearray = (float *)((char *)java_env->GetDirectBufferAddress(jwavearray)+jwavearray_);
	}
	int numvalues = (int)jnumvalues;
	int channeloffset = (int)jchanneloffset;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getWaveData(wavearray, numvalues, channeloffset);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jindex, jlong jindex_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *index = 0;
	if(jindex) {
		index = (int *)((char *)java_env->GetDirectBufferAddress(jindex)+jindex_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getIndex(index);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setMode(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmode) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_MODE mode = (FMOD_MODE)jmode;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setMode(mode);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getMode(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmode, jlong jmode_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_MODE *mode = 0;
	if(jmode) {
		mode = (FMOD_MODE *)(int *)((char *)java_env->GetDirectBufferAddress(jmode)+jmode_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getMode(mode);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setLoopCount(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jloopcount) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int loopcount = (int)jloopcount;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setLoopCount(loopcount);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getLoopCount(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jloopcount, jlong jloopcount_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *loopcount = 0;
	if(jloopcount) {
		loopcount = (int *)((char *)java_env->GetDirectBufferAddress(jloopcount)+jloopcount_);
	}

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getLoopCount(loopcount);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setLoopPoints(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jloopstart, jint jloopstarttype, jint jloopend, jint jloopendtype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int loopstart = (int)jloopstart;
	FMOD_TIMEUNIT loopstarttype = (FMOD_TIMEUNIT)jloopstarttype;
	int loopend = (int)jloopend;
	FMOD_TIMEUNIT loopendtype = (FMOD_TIMEUNIT)jloopendtype;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setLoopPoints(loopstart, loopstarttype, loopend, loopendtype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getLoopPoints(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jloopstart, jlong jloopstart_, jint jloopstarttype, jobject jloopend, jlong jloopend_, jint jloopendtype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *loopstart = 0;
	if(jloopstart) {
		loopstart = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jloopstart)+jloopstart_);
	}
	FMOD_TIMEUNIT loopstarttype = (FMOD_TIMEUNIT)jloopstarttype;
	unsigned int *loopend = 0;
	if(jloopend) {
		loopend = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jloopend)+jloopend_);
	}
	FMOD_TIMEUNIT loopendtype = (FMOD_TIMEUNIT)jloopendtype;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getLoopPoints(loopstart, loopstarttype, loopend, loopendtype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Channel_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_CHANNEL);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Channel **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


