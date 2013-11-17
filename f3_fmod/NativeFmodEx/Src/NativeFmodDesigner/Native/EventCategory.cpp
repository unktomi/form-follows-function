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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jindex, jlong jindex_, jobject jname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *index = 0;
	if(jindex) {
		index = (int *)((char *)java_env->GetDirectBufferAddress(jindex)+jindex_);
	}
	char *name/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getInfo(index, &name);

	if(jname) {
		POINTER_TYPE newAddress/* = 0*/;
		*(char **)&newAddress = name;
		setPointerAddress(java_env, jname, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getCategory(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jobject jcategory) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD::EventCategory *category/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getCategory(name, &category);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jcategory) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventCategory **)&newAddress = category;
		setPointerAddress(java_env, jcategory, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getCategoryByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jcategory) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::EventCategory *category/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getCategoryByIndex(index, &category);

	if(jcategory) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventCategory **)&newAddress = category;
		setPointerAddress(java_env, jcategory, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getNumCategories(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumcategories, jlong jnumcategories_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numcategories = 0;
	if(jnumcategories) {
		numcategories = (int *)((char *)java_env->GetDirectBufferAddress(jnumcategories)+jnumcategories_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getNumCategories(numcategories);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getEventByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jint jmode, jobject jevent) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD_EVENT_MODE mode = (FMOD_EVENT_MODE)jmode;
	FMOD::Event *event/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getEventByIndex(index, mode, &event);

	if(jevent) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Event **)&newAddress = event;
		setPointerAddress(java_env, jevent, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getNumEvents(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumevents, jlong jnumevents_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numevents = 0;
	if(jnumevents) {
		numevents = (int *)((char *)java_env->GetDirectBufferAddress(jnumevents)+jnumevents_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getNumEvents(numevents);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1stopAllEvents(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->stopAllEvents();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1setVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float volume = (float)jvolume;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->setVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvolume, jlong jvolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *volume = 0;
	if(jvolume) {
		volume = (float *)((char *)java_env->GetDirectBufferAddress(jvolume)+jvolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1setPitch(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jpitch, jint junits) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float pitch = (float)jpitch;
	FMOD_EVENT_PITCHUNITS units = (FMOD_EVENT_PITCHUNITS)junits;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->setPitch(pitch, units);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getPitch(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpitch, jlong jpitch_, jint junits) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *pitch = 0;
	if(jpitch) {
		pitch = (float *)((char *)java_env->GetDirectBufferAddress(jpitch)+jpitch_);
	}
	FMOD_EVENT_PITCHUNITS units = (FMOD_EVENT_PITCHUNITS)junits;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getPitch(pitch, units);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1setPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jpaused) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool paused = (bool)(jpaused != 0);

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->setPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpaused, jlong jpaused_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *paused = 0;
	if(jpaused) {
		paused = (bool *)((char *)java_env->GetDirectBufferAddress(jpaused)+jpaused_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1setMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jmute) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool mute = (bool)(jmute != 0);

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->setMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmute, jlong jmute_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *mute = 0;
	if(jmute) {
		mute = (bool *)((char *)java_env->GetDirectBufferAddress(jmute)+jmute_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getChannelGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jchannelgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::ChannelGroup *channelgroup/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getChannelGroup(&channelgroup);

	if(jchannelgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::ChannelGroup **)&newAddress = channelgroup;
		setPointerAddress(java_env, jchannelgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventCategory_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTCATEGORY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventCategory **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


