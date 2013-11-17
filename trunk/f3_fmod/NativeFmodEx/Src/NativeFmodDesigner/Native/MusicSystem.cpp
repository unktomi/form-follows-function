/**
 * 				NativeFmodEx Project
 *
 * Want to use FMOD Ex API (www.fmod.org) in the Java language ? NativeFmodEx is made for you.
 * Copyright © 2005-2008 Jérôme JOUVIE (Jouvieje)
 *
 * Created on 23 feb. 2005
 * @version file v1.4.4
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
#include "fmod_event.h"
#include "fmod_event_net.h"
#include "fmod_event.hpp"
#include "org_jouvieje_FmodDesigner_FmodDesignerJNI.h"
#include "CallbackManager.h"

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1reset(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->reset();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1setVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float volume = (float)jvolume;

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->setVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1getVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvolume, jlong jvolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *volume = 0;
	if(jvolume) {
		volume = (float *)((char *)java_env->GetDirectBufferAddress(jvolume)+jvolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->getVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1setPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jpaused) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool paused = (bool)(jpaused != 0);

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->setPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1getPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpaused, jlong jpaused_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *paused = 0;
	if(jpaused) {
		paused = (bool *)((char *)java_env->GetDirectBufferAddress(jpaused)+jpaused_);
	}

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->getPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1setMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jmute) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool mute = (bool)(jmute != 0);

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->setMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1getMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmute, jlong jmute_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *mute = 0;
	if(jmute) {
		mute = (bool *)((char *)java_env->GetDirectBufferAddress(jmute)+jmute_);
	}

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->getMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1promptCue(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jid) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_MUSIC_CUE_ID id = (FMOD_MUSIC_CUE_ID)jid;

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->promptCue(id);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1prepareCue(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jid, jobject jprompt) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_MUSIC_CUE_ID id = (FMOD_MUSIC_CUE_ID)jid;
	FMOD::MusicPrompt *prompt/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->prepareCue(id, &prompt);

	if(jprompt) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::MusicPrompt **)&newAddress = prompt;
		setPointerAddress(java_env, jprompt, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1getParameter(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jid, jobject jparameter, jlong jparameter_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_MUSIC_PARAM_ID id = (FMOD_MUSIC_PARAM_ID)jid;
	float *parameter = 0;
	if(jparameter) {
		parameter = (float *)((char *)java_env->GetDirectBufferAddress(jparameter)+jparameter_);
	}

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->getParameter(id, parameter);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1setParameter(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jid, jfloat jparameter) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_MUSIC_PARAM_ID id = (FMOD_MUSIC_PARAM_ID)jid;
	float parameter = (float)jparameter;

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->setParameter(id, parameter);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1loadSoundData(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jresource, jint jmode) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_EVENT_RESOURCE resource = (FMOD_EVENT_RESOURCE)jresource;
	FMOD_EVENT_MODE mode = (FMOD_EVENT_MODE)jmode;

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->loadSoundData(resource, mode);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1freeSoundData(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jwaituntilready) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool waituntilready = (bool)(jwaituntilready != 0);

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->freeSoundData(waituntilready);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_MusicSystem_1setCallback(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jcallback, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_MUSICSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::MusicSystem **)&pointer)->setCallback(jcallback == 0 ? NULL : FMOD_MUSIC_CALLBACK_BRIDGE, userdata);

	return (jint)result_;
}


