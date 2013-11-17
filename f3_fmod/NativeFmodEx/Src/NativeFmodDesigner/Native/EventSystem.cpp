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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1init(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmaxchannels, jint jflags, jlong jextradriverdata, jint jeventflags) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int maxchannels = (int)jmaxchannels;
	FMOD_INITFLAGS flags = (FMOD_INITFLAGS)jflags;
	POINTER_TYPE extradriverdataTmp = (POINTER_TYPE)jextradriverdata;
	void *extradriverdata = *(void **)&extradriverdataTmp;
	FMOD_EVENT_INITFLAGS eventflags = (FMOD_EVENT_INITFLAGS)jeventflags;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->init(maxchannels, flags, extradriverdata, eventflags);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1release(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->release();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1update(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->update();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1setMediaPath(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jpath) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *path = getByteArrayElements(java_env, jpath);

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->setMediaPath(path);

	releaseByteArrayElements(java_env, jpath, (const char *)path);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1setPluginPath(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jpath) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *path = getByteArrayElements(java_env, jpath);

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->setPluginPath(path);

	releaseByteArrayElements(java_env, jpath, (const char *)path);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getVersion(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jversion, jlong jversion_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *version = 0;
	if(jversion) {
		version = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jversion)+jversion_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getVersion(version);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jinfo) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_EVENT_SYSTEMINFO *info = 0;
	if(jinfo) {
		POINTER_TYPE infoTmp = (POINTER_TYPE)jinfo;
		info = *(FMOD_EVENT_SYSTEMINFO **)&infoTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getInfo(info);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getSystemObject(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsystem) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::System *system/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getSystemObject(&system);

	if(jsystem) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::System **)&newAddress = system;
		setPointerAddress(java_env, jsystem, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getMusicSystem(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmusicsystem) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::MusicSystem *musicsystem/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getMusicSystem(&musicsystem);

	if(jmusicsystem) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::MusicSystem **)&newAddress = musicsystem;
		setPointerAddress(java_env, jmusicsystem, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1load__J_3BJLorg_jouvieje_FmodDesigner_EventProject_2(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname_or_data, jlong jloadinfo, jobject jproject) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name_or_data = getByteArrayElements(java_env, jname_or_data);
	FMOD_EVENT_LOADINFO *loadinfo = 0;
	if(jloadinfo) {
		POINTER_TYPE loadinfoTmp = (POINTER_TYPE)jloadinfo;
		loadinfo = *(FMOD_EVENT_LOADINFO **)&loadinfoTmp;
	}
	FMOD::EventProject *project/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->load(name_or_data, loadinfo, &project);

	releaseByteArrayElements(java_env, jname_or_data, (const char *)name_or_data);
	if(jproject) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventProject **)&newAddress = project;
		setPointerAddress(java_env, jproject, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1load__JLjava_nio_ByteBuffer_2JJLorg_jouvieje_FmodDesigner_EventProject_2(JNIEnv *java_env, jclass jcls, jlong jpointer, 	jobject jname_or_data, jlong jname_or_data_, jlong jloadinfo, jobject jproject) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name_or_data = 0;
	if(jname_or_data) {
		name_or_data = (char *)java_env->GetDirectBufferAddress(jname_or_data)+jname_or_data_;
	}
	FMOD_EVENT_LOADINFO *loadinfo = 0;
	if(jloadinfo) {
		POINTER_TYPE loadinfoTmp = (POINTER_TYPE)jloadinfo;
		loadinfo = *(FMOD_EVENT_LOADINFO **)&loadinfoTmp;
	}
	FMOD::EventProject *project/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->load(name_or_data, loadinfo, &project);

	if(jproject) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventProject **)&newAddress = project;
		setPointerAddress(java_env, jproject, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1unload(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->unload();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getProject(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jobject jproject) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD::EventProject *project/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getProject(name, &project);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jproject) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventProject **)&newAddress = project;
		setPointerAddress(java_env, jproject, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getProjectByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jproject) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::EventProject *project/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getProjectByIndex(index, &project);

	if(jproject) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventProject **)&newAddress = project;
		setPointerAddress(java_env, jproject, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getNumProjects(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumprojects, jlong jnumprojects_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numprojects = 0;
	if(jnumprojects) {
		numprojects = (int *)((char *)java_env->GetDirectBufferAddress(jnumprojects)+jnumprojects_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getNumProjects(numprojects);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getCategory(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jobject jcategory) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD::EventCategory *category/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getCategory(name, &category);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jcategory) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventCategory **)&newAddress = category;
		setPointerAddress(java_env, jcategory, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getCategoryByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jcategory) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::EventCategory *category/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getCategoryByIndex(index, &category);

	if(jcategory) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventCategory **)&newAddress = category;
		setPointerAddress(java_env, jcategory, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getNumCategories(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumcategories, jlong jnumcategories_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numcategories = 0;
	if(jnumcategories) {
		numcategories = (int *)((char *)java_env->GetDirectBufferAddress(jnumcategories)+jnumcategories_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getNumCategories(numcategories);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jboolean jcacheevents, jobject jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	bool cacheevents = (bool)(jcacheevents != 0);
	FMOD::EventGroup *group/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getGroup(name, cacheevents, &group);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventGroup **)&newAddress = group;
		setPointerAddress(java_env, jgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getEvent(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jint jmode, jobject jevent) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD_EVENT_MODE mode = (FMOD_EVENT_MODE)jmode;
	FMOD::Event *event/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getEvent(name, mode, &event);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jevent) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Event **)&newAddress = event;
		setPointerAddress(java_env, jevent, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getEventBySystemID(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jsystemid, jint jmode, jobject jevent) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int systemid = (int)jsystemid;
	FMOD_EVENT_MODE mode = (FMOD_EVENT_MODE)jmode;
	FMOD::Event *event/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getEventBySystemID(systemid, mode, &event);

	if(jevent) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Event **)&newAddress = event;
		setPointerAddress(java_env, jevent, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getNumEvents(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumevents, jlong jnumevents_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numevents = 0;
	if(jnumevents) {
		numevents = (int *)((char *)java_env->GetDirectBufferAddress(jnumevents)+jnumevents_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getNumEvents(numevents);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1setReverbProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->setReverbProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getReverbProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getReverbProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getReverbPreset(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jlong jprop, jobject jindex, jlong jindex_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}
	int *index = 0;
	if(jindex) {
		index = (int *)((char *)java_env->GetDirectBufferAddress(jindex)+jindex_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getReverbPreset(name, prop, index);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getReverbPresetByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jlong jprop, jobject jname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}
	char *name/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getReverbPresetByIndex(index, prop, &name);

	if(jname) {
		POINTER_TYPE newAddress/* = 0*/;
		*(char **)&newAddress = name;
		setPointerAddress(java_env, jname, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getNumReverbPresets(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumpresets, jlong jnumpresets_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numpresets = 0;
	if(jnumpresets) {
		numpresets = (int *)((char *)java_env->GetDirectBufferAddress(jnumpresets)+jnumpresets_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getNumReverbPresets(numpresets);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1createReverb(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jreverb) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::EventReverb *reverb/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->createReverb(&reverb);

	if(jreverb) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventReverb **)&newAddress = reverb;
		setPointerAddress(java_env, jreverb, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1setReverbAmbientProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->setReverbAmbientProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getReverbAmbientProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getReverbAmbientProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1set3DNumListeners(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jnumlisteners) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int numlisteners = (int)jnumlisteners;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->set3DNumListeners(numlisteners);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1get3DNumListeners(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumlisteners, jlong jnumlisteners_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numlisteners = 0;
	if(jnumlisteners) {
		numlisteners = (int *)((char *)java_env->GetDirectBufferAddress(jnumlisteners)+jnumlisteners_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->get3DNumListeners(numlisteners);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1set3DListenerAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jlistener, jlong jpos, jlong jvel, jlong jforward, jlong jup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int listener = (int)jlistener;
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
	FMOD_VECTOR *forward = 0;
	if(jforward) {
		POINTER_TYPE forwardTmp = (POINTER_TYPE)jforward;
		forward = *(FMOD_VECTOR **)&forwardTmp;
	}
	FMOD_VECTOR *up = 0;
	if(jup) {
		POINTER_TYPE upTmp = (POINTER_TYPE)jup;
		up = *(FMOD_VECTOR **)&upTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->set3DListenerAttributes(listener, pos, vel, forward, up);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1get3DListenerAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jlistener, jlong jpos, jlong jvel, jlong jforward, jlong jup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int listener = (int)jlistener;
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
	FMOD_VECTOR *forward = 0;
	if(jforward) {
		POINTER_TYPE forwardTmp = (POINTER_TYPE)jforward;
		forward = *(FMOD_VECTOR **)&forwardTmp;
	}
	FMOD_VECTOR *up = 0;
	if(jup) {
		POINTER_TYPE upTmp = (POINTER_TYPE)jup;
		up = *(FMOD_VECTOR **)&upTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->get3DListenerAttributes(listener, pos, vel, forward, up);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1registerMemoryFSB(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jfilename, jlong jfsbdata, jint jfsbdatalen, jboolean jloadintorsx) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *filename = getByteArrayElements(java_env, jfilename);
	POINTER_TYPE fsbdataTmp = (POINTER_TYPE)jfsbdata;
	void *fsbdata = *(void **)&fsbdataTmp;
	int fsbdatalen = (int)jfsbdatalen;
	bool loadintorsx = (bool)(jloadintorsx != 0);

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->registerMemoryFSB(filename, fsbdata, fsbdatalen, loadintorsx);

	releaseByteArrayElements(java_env, jfilename, (const char *)filename);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventSystem_1unregisterMemoryFSB(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jfilename) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTSYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *filename = getByteArrayElements(java_env, jfilename);

	FMOD_RESULT result_ = (*(FMOD::EventSystem **)&pointer)->unregisterMemoryFSB(filename);

	releaseByteArrayElements(java_env, jfilename, (const char *)filename);
	return (jint)result_;
}


