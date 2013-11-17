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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1release(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->release();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getSystemObject(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsystem) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::System *system/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getSystemObject(&system);

	if(jsystem) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::System **)&newAddress = system;
		setPointerAddress(java_env, jsystem, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1setMaxAudible(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmaxaudible) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int maxaudible = (int)jmaxaudible;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->setMaxAudible(maxaudible);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getMaxAudible(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmaxaudible, jlong jmaxaudible_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *maxaudible = 0;
	if(jmaxaudible) {
		maxaudible = (int *)((char *)java_env->GetDirectBufferAddress(jmaxaudible)+jmaxaudible_);
	}

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getMaxAudible(maxaudible);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1setMaxAudibleBehavior(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jbehavior) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SOUNDGROUP_BEHAVIOR behavior = (FMOD_SOUNDGROUP_BEHAVIOR)jbehavior;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->setMaxAudibleBehavior(behavior);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getMaxAudibleBehavior(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jbehaviorPointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SOUNDGROUP_BEHAVIOR behavior;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getMaxAudibleBehavior(&behavior);

	if(jbehaviorPointer) {
		int *behaviorPointer = (int *)java_env->GetDirectBufferAddress(jbehaviorPointer);
		behaviorPointer[0] = behavior;
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1setMuteFadeSpeed(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jspeed) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float speed = (float)jspeed;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->setMuteFadeSpeed(speed);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getMuteFadeSpeed(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jspeed, jlong jspeed_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *speed = 0;
	if(jspeed) {
		speed = (float *)((char *)java_env->GetDirectBufferAddress(jspeed)+jspeed_);
	}

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getMuteFadeSpeed(speed);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1setVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float volume = (float)jvolume;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->setVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvolume, jlong jvolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *volume = 0;
	if(jvolume) {
		volume = (float *)((char *)java_env->GetDirectBufferAddress(jvolume)+jvolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1stop(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->stop();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getName(JNIEnv *java_env, jclass jcls, jlong jpointer, 	jobject jname, jlong jname_, jint jnamelen) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	int namelen = (int)jnamelen;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getName(name, namelen);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getNumSounds(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumsounds, jlong jnumsounds_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numsounds = 0;
	if(jnumsounds) {
		numsounds = (int *)((char *)java_env->GetDirectBufferAddress(jnumsounds)+jnumsounds_);
	}

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getNumSounds(numsounds);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getSound(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jsound) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::Sound *sound/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getSound(index, &sound);

	if(jsound) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Sound **)&newAddress = sound;
		setPointerAddress(java_env, jsound, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getNumPlaying(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumplaying, jlong jnumplaying_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numplaying = 0;
	if(jnumplaying) {
		numplaying = (int *)((char *)java_env->GetDirectBufferAddress(jnumplaying)+jnumplaying_);
	}

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getNumPlaying(numplaying);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_SoundGroup_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUNDGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::SoundGroup **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


