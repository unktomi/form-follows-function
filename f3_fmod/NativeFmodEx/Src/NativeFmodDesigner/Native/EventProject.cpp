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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1release(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->release();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1getInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jindex, jlong jindex_, jobject jname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *index = 0;
	if(jindex) {
		index = (int *)((char *)java_env->GetDirectBufferAddress(jindex)+jindex_);
	}
	char *name/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->getInfo(index, &name);

	if(jname) {
		POINTER_TYPE newAddress/* = 0*/;
		*(char **)&newAddress = name;
		setPointerAddress(java_env, jname, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1getGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jboolean jcacheevents, jobject jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	bool cacheevents = (bool)(jcacheevents != 0);
	FMOD::EventGroup *group/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->getGroup(name, cacheevents, &group);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventGroup **)&newAddress = group;
		setPointerAddress(java_env, jgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1getGroupByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jboolean jcacheevents, jobject jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	bool cacheevents = (bool)(jcacheevents != 0);
	FMOD::EventGroup *group/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->getGroupByIndex(index, cacheevents, &group);

	if(jgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventGroup **)&newAddress = group;
		setPointerAddress(java_env, jgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1getNumGroups(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumgroups, jlong jnumgroups_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numgroups = 0;
	if(jnumgroups) {
		numgroups = (int *)((char *)java_env->GetDirectBufferAddress(jnumgroups)+jnumgroups_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->getNumGroups(numgroups);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1getEvent(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jint jmode, jobject jevent) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD_EVENT_MODE mode = (FMOD_EVENT_MODE)jmode;
	FMOD::Event *event/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->getEvent(name, mode, &event);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jevent) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Event **)&newAddress = event;
		setPointerAddress(java_env, jevent, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1getEventByProjectID(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jprojectid, jint jmode, jobject jevent) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int projectid = (int)jprojectid;
	FMOD_EVENT_MODE mode = (FMOD_EVENT_MODE)jmode;
	FMOD::Event *event/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->getEventByProjectID(projectid, mode, &event);

	if(jevent) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Event **)&newAddress = event;
		setPointerAddress(java_env, jevent, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1getNumEvents(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumevents, jlong jnumevents_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numevents = 0;
	if(jnumevents) {
		numevents = (int *)((char *)java_env->GetDirectBufferAddress(jnumevents)+jnumevents_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->getNumEvents(numevents);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventProject_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPROJECT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventProject **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


