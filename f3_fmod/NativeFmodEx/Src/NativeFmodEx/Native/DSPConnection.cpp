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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSPConnection_1getInput(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jinput) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSPCONNECTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *input/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::DSPConnection **)&pointer)->getInput(&input);

	if(jinput) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = input;
		setPointerAddress(java_env, jinput, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSPConnection_1getOutput(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject joutput) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSPCONNECTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *output/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::DSPConnection **)&pointer)->getOutput(&output);

	if(joutput) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = output;
		setPointerAddress(java_env, joutput, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSPConnection_1setMix(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSPCONNECTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float volume = (float)jvolume;

	FMOD_RESULT result_ = (*(FMOD::DSPConnection **)&pointer)->setMix(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSPConnection_1getMix(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvolume, jlong jvolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSPCONNECTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *volume = 0;
	if(jvolume) {
		volume = (float *)((char *)java_env->GetDirectBufferAddress(jvolume)+jvolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSPConnection **)&pointer)->getMix(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSPConnection_1setLevels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeaker, jobject jlevels, jlong jlevels_, jint jnumlevels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSPCONNECTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKER speaker = (FMOD_SPEAKER)jspeaker;
	float *levels = 0;
	if(jlevels) {
		levels = (float *)((char *)java_env->GetDirectBufferAddress(jlevels)+jlevels_);
	}
	int numlevels = (int)jnumlevels;

	FMOD_RESULT result_ = (*(FMOD::DSPConnection **)&pointer)->setLevels(speaker, levels, numlevels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSPConnection_1getLevels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeaker, jobject jlevels, jlong jlevels_, jint jnumlevels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSPCONNECTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKER speaker = (FMOD_SPEAKER)jspeaker;
	float *levels = 0;
	if(jlevels) {
		levels = (float *)((char *)java_env->GetDirectBufferAddress(jlevels)+jlevels_);
	}
	int numlevels = (int)jnumlevels;

	FMOD_RESULT result_ = (*(FMOD::DSPConnection **)&pointer)->getLevels(speaker, levels, numlevels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSPConnection_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSPCONNECTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::DSPConnection **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSPConnection_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSPCONNECTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::DSPConnection **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


