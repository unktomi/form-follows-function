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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1newArray(JNIEnv *java_env, jclass jcls, jint length) {
	FMOD::Event **array = new FMOD::Event *[(int)length];
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD::Event ***)&jresult = array;
	return (jlong)jresult;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1SIZEOF(JNIEnv *java_env, jclass jcls) {
	return (jint)sizeof(FMOD::Event *);
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1start(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->start();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1stop(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jimmediate) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool immediate = (bool)(jimmediate != 0);

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->stop(immediate);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jindex, jlong jindex_, jobject jname, jlong jinfo) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *index = 0;
	if(jindex) {
		index = (int *)((char *)java_env->GetDirectBufferAddress(jindex)+jindex_);
	}
	char *name/* = 0*/;
	FMOD_EVENT_INFO *info = 0;
	if(jinfo) {
		POINTER_TYPE infoTmp = (POINTER_TYPE)jinfo;
		info = *(FMOD_EVENT_INFO **)&infoTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getInfo(index, &name, info);

	if(jname) {
		POINTER_TYPE newAddress/* = 0*/;
		*(char **)&newAddress = name;
		setPointerAddress(java_env, jname, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getState(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jstate, jlong jstate_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_EVENT_STATE *state = 0;
	if(jstate) {
		state = (FMOD_EVENT_STATE *)(int *)((char *)java_env->GetDirectBufferAddress(jstate)+jstate_);
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getState(state);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getParentGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::EventGroup *group/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getParentGroup(&group);

	if(jgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventGroup **)&newAddress = group;
		setPointerAddress(java_env, jgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getChannelGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jchannelgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::ChannelGroup *channelgroup/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getChannelGroup(&channelgroup);

	if(jchannelgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::ChannelGroup **)&newAddress = channelgroup;
		setPointerAddress(java_env, jchannelgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1setCallback(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jcallback, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->setCallback(jcallback == 0 ? NULL : FMOD_EVENT_CALLBACK_BRIDGE, userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getParameter(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jobject jparameter) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD::EventParameter *parameter/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getParameter(name, &parameter);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jparameter) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventParameter **)&newAddress = parameter;
		setPointerAddress(java_env, jparameter, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getParameterByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jparameter) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::EventParameter *parameter/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getParameterByIndex(index, &parameter);

	if(jparameter) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventParameter **)&newAddress = parameter;
		setPointerAddress(java_env, jparameter, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getNumParameters(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumparameters, jlong jnumparameters_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numparameters = 0;
	if(jnumparameters) {
		numparameters = (int *)((char *)java_env->GetDirectBufferAddress(jnumparameters)+jnumparameters_);
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getNumParameters(numparameters);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getProperty(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jpropertyname, jlong jvalue, jboolean jthis_instance) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *propertyname = getByteArrayElements(java_env, jpropertyname);
	POINTER_TYPE valueTmp = (POINTER_TYPE)jvalue;
	void *value = *(void **)&valueTmp;
	bool this_instance = (bool)(jthis_instance != 0);

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getProperty(propertyname, value, this_instance);

	releaseByteArrayElements(java_env, jpropertyname, (const char *)propertyname);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getPropertyByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jpropertyindex, jlong jvalue, jboolean jthis_instance) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int propertyindex = (int)jpropertyindex;
	POINTER_TYPE valueTmp = (POINTER_TYPE)jvalue;
	void *value = *(void **)&valueTmp;
	bool this_instance = (bool)(jthis_instance != 0);

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getPropertyByIndex(propertyindex, value, this_instance);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1setProperty(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jpropertyname, jlong jvalue, jboolean jthis_instance) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *propertyname = getByteArrayElements(java_env, jpropertyname);
	POINTER_TYPE valueTmp = (POINTER_TYPE)jvalue;
	void *value = *(void **)&valueTmp;
	bool this_instance = (bool)(jthis_instance != 0);

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->setProperty(propertyname, value, this_instance);

	releaseByteArrayElements(java_env, jpropertyname, (const char *)propertyname);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1setPropertyByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jpropertyindex, jlong jvalue, jboolean jthis_instance) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int propertyindex = (int)jpropertyindex;
	POINTER_TYPE valueTmp = (POINTER_TYPE)jvalue;
	void *value = *(void **)&valueTmp;
	bool this_instance = (bool)(jthis_instance != 0);

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->setPropertyByIndex(propertyindex, value, this_instance);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getNumProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumproperties, jlong jnumproperties_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numproperties = 0;
	if(jnumproperties) {
		numproperties = (int *)((char *)java_env->GetDirectBufferAddress(jnumproperties)+jnumproperties_);
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getNumProperties(numproperties);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getCategory(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jcategory) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::EventCategory *category/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getCategory(&category);

	if(jcategory) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventCategory **)&newAddress = category;
		setPointerAddress(java_env, jcategory, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1setVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float volume = (float)jvolume;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->setVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvolume, jlong jvolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *volume = 0;
	if(jvolume) {
		volume = (float *)((char *)java_env->GetDirectBufferAddress(jvolume)+jvolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getVolume(volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1setPitch(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jpitch, jint junits) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float pitch = (float)jpitch;
	FMOD_EVENT_PITCHUNITS units = (FMOD_EVENT_PITCHUNITS)junits;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->setPitch(pitch, units);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getPitch(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpitch, jlong jpitch_, jint junits) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *pitch = 0;
	if(jpitch) {
		pitch = (float *)((char *)java_env->GetDirectBufferAddress(jpitch)+jpitch_);
	}
	FMOD_EVENT_PITCHUNITS units = (FMOD_EVENT_PITCHUNITS)junits;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getPitch(pitch, units);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1setPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jpaused) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool paused = (bool)(jpaused != 0);

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->setPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getPaused(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpaused, jlong jpaused_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *paused = 0;
	if(jpaused) {
		paused = (bool *)((char *)java_env->GetDirectBufferAddress(jpaused)+jpaused_);
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getPaused(paused);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1setMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jmute) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool mute = (bool)(jmute != 0);

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->setMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getMute(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmute, jlong jmute_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *mute = 0;
	if(jmute) {
		mute = (bool *)((char *)java_env->GetDirectBufferAddress(jmute)+jmute_);
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getMute(mute);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1set3DAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jposition, jlong jvelocity, jlong jorientation) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *position = 0;
	if(jposition) {
		POINTER_TYPE positionTmp = (POINTER_TYPE)jposition;
		position = *(FMOD_VECTOR **)&positionTmp;
	}
	FMOD_VECTOR *velocity = 0;
	if(jvelocity) {
		POINTER_TYPE velocityTmp = (POINTER_TYPE)jvelocity;
		velocity = *(FMOD_VECTOR **)&velocityTmp;
	}
	FMOD_VECTOR *orientation = 0;
	if(jorientation) {
		POINTER_TYPE orientationTmp = (POINTER_TYPE)jorientation;
		orientation = *(FMOD_VECTOR **)&orientationTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->set3DAttributes(position, velocity, orientation);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1get3DAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jposition, jlong jvelocity, jlong jorientation) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *position = 0;
	if(jposition) {
		POINTER_TYPE positionTmp = (POINTER_TYPE)jposition;
		position = *(FMOD_VECTOR **)&positionTmp;
	}
	FMOD_VECTOR *velocity = 0;
	if(jvelocity) {
		POINTER_TYPE velocityTmp = (POINTER_TYPE)jvelocity;
		velocity = *(FMOD_VECTOR **)&velocityTmp;
	}
	FMOD_VECTOR *orientation = 0;
	if(jorientation) {
		POINTER_TYPE orientationTmp = (POINTER_TYPE)jorientation;
		orientation = *(FMOD_VECTOR **)&orientationTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->get3DAttributes(position, velocity, orientation);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1set3DOcclusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jdirectocclusion, jfloat jreverbocclusion) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float directocclusion = (float)jdirectocclusion;
	float reverbocclusion = (float)jreverbocclusion;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->set3DOcclusion(directocclusion, reverbocclusion);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1get3DOcclusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdirectocclusion, jlong jdirectocclusion_, jobject jreverbocclusion, jlong jreverbocclusion_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
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

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->get3DOcclusion(directocclusion, reverbocclusion);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1setReverbProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_CHANNELPROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_CHANNELPROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->setReverbProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getReverbProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_CHANNELPROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_CHANNELPROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getReverbProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_Event_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Event **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


