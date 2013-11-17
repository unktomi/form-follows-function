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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jindex, jlong jindex_, jobject jname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *index = 0;
	if(jindex) {
		index = (int *)((char *)java_env->GetDirectBufferAddress(jindex)+jindex_);
	}
	char *name/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getInfo(index, &name);

	if(jname) {
		POINTER_TYPE newAddress/* = 0*/;
		*(char **)&newAddress = name;
		setPointerAddress(java_env, jname, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1loadEventData(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jresource, jint jmode) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_EVENT_RESOURCE resource = (FMOD_EVENT_RESOURCE)jresource;
	FMOD_EVENT_MODE mode = (FMOD_EVENT_MODE)jmode;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->loadEventData(resource, mode);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1freeEventData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jevent, jboolean jwaituntilready) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::Event *event = 0;
	if(jevent) {
		POINTER_TYPE eventTmp = (POINTER_TYPE)jevent;
		event = *(FMOD::Event **)&eventTmp;
	}
	bool waituntilready = (bool)(jwaituntilready != 0);

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->freeEventData(event, waituntilready);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jboolean jcacheevents, jobject jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	bool cacheevents = (bool)(jcacheevents != 0);
	FMOD::EventGroup *group/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getGroup(name, cacheevents, &group);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventGroup **)&newAddress = group;
		setPointerAddress(java_env, jgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getGroupByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jboolean jcacheevents, jobject jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	bool cacheevents = (bool)(jcacheevents != 0);
	FMOD::EventGroup *group/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getGroupByIndex(index, cacheevents, &group);

	if(jgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventGroup **)&newAddress = group;
		setPointerAddress(java_env, jgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getParentGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::EventGroup *group/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getParentGroup(&group);

	if(jgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventGroup **)&newAddress = group;
		setPointerAddress(java_env, jgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getParentProject(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jproject) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::EventProject *project/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getParentProject(&project);

	if(jproject) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventProject **)&newAddress = project;
		setPointerAddress(java_env, jproject, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getNumGroups(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumgroups, jlong jnumgroups_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numgroups = 0;
	if(jnumgroups) {
		numgroups = (int *)((char *)java_env->GetDirectBufferAddress(jnumgroups)+jnumgroups_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getNumGroups(numgroups);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getEvent(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jint jmode, jobject jevent) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD_EVENT_MODE mode = (FMOD_EVENT_MODE)jmode;
	FMOD::Event *event/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getEvent(name, mode, &event);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jevent) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Event **)&newAddress = event;
		setPointerAddress(java_env, jevent, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getEventByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jint jmode, jobject jevent) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD_EVENT_MODE mode = (FMOD_EVENT_MODE)jmode;
	FMOD::Event *event/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getEventByIndex(index, mode, &event);

	if(jevent) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Event **)&newAddress = event;
		setPointerAddress(java_env, jevent, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getNumEvents(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumevents, jlong jnumevents_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numevents = 0;
	if(jnumevents) {
		numevents = (int *)((char *)java_env->GetDirectBufferAddress(jnumevents)+jnumevents_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getNumEvents(numevents);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getProperty(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jpropertyname, jlong jvalue) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *propertyname = getByteArrayElements(java_env, jpropertyname);
	POINTER_TYPE valueTmp = (POINTER_TYPE)jvalue;
	void *value = *(void **)&valueTmp;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getProperty(propertyname, value);

	releaseByteArrayElements(java_env, jpropertyname, (const char *)propertyname);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getPropertyByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jpropertyindex, jlong jvalue) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int propertyindex = (int)jpropertyindex;
	POINTER_TYPE valueTmp = (POINTER_TYPE)jvalue;
	void *value = *(void **)&valueTmp;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getPropertyByIndex(propertyindex, value);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getNumProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumproperties, jlong jnumproperties_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numproperties = 0;
	if(jnumproperties) {
		numproperties = (int *)((char *)java_env->GetDirectBufferAddress(jnumproperties)+jnumproperties_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getNumProperties(numproperties);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getState(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jstate, jlong jstate_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_EVENT_STATE *state = 0;
	if(jstate) {
		state = (FMOD_EVENT_STATE *)(int *)((char *)java_env->GetDirectBufferAddress(jstate)+jstate_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getState(state);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventGroup_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTGROUP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventGroup **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


