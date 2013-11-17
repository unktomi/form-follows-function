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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1getInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jindex, jlong jindex_, jobject jname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *index = 0;
	if(jindex) {
		index = (int *)((char *)java_env->GetDirectBufferAddress(jindex)+jindex_);
	}
	char *name/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->getInfo(index, &name);

	if(jname) {
		POINTER_TYPE newAddress/* = 0*/;
		*(char **)&newAddress = name;
		setPointerAddress(java_env, jname, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1getRange(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jrangemin, jlong jrangemin_, jobject jrangemax, jlong jrangemax_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *rangemin = 0;
	if(jrangemin) {
		rangemin = (float *)((char *)java_env->GetDirectBufferAddress(jrangemin)+jrangemin_);
	}
	float *rangemax = 0;
	if(jrangemax) {
		rangemax = (float *)((char *)java_env->GetDirectBufferAddress(jrangemax)+jrangemax_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->getRange(rangemin, rangemax);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1setValue(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvalue) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float value = (float)jvalue;

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->setValue(value);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1getValue(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvalue, jlong jvalue_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *value = 0;
	if(jvalue) {
		value = (float *)((char *)java_env->GetDirectBufferAddress(jvalue)+jvalue_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->getValue(value);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1setVelocity(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvalue) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float value = (float)jvalue;

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->setVelocity(value);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1getVelocity(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvalue, jlong jvalue_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *value = 0;
	if(jvalue) {
		value = (float *)((char *)java_env->GetDirectBufferAddress(jvalue)+jvalue_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->getVelocity(value);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1setSeekSpeed(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvalue) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float value = (float)jvalue;

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->setSeekSpeed(value);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1getSeekSpeed(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jvalue, jlong jvalue_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *value = 0;
	if(jvalue) {
		value = (float *)((char *)java_env->GetDirectBufferAddress(jvalue)+jvalue_);
	}

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->getSeekSpeed(value);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_EventParameter_1keyOff(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_EVENTPARAMETER);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::EventParameter **)&pointer)->keyOff();

	return (jint)result_;
}


