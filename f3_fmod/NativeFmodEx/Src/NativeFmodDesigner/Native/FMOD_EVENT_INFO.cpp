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
#include "org_jouvieje_FmodDesigner_Structures_StructureJNI.h"
#include "CallbackManager.h"

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_EVENT_INFO *result_ = new FMOD_EVENT_INFO();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_EVENT_INFO **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_EVENT_INFO **)&pointer;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1memoryused(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_INFO **)&pointer)->memoryused;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1positionms(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_INFO **)&pointer)->positionms;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1lengthms(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_INFO **)&pointer)->lengthms;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1channelsplaying(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_INFO **)&pointer)->channelsplaying;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1instancesactive(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_INFO **)&pointer)->instancesactive;
	return (jint)result_;
}

JNIEXPORT jobjectArray JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1wavebanknames(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char **result = (*(FMOD_EVENT_INFO **)&pointer)->wavebanknames;
	jint size = (jint)-1;
	jobjectArray jresult = java_env->NewObjectArray(size, getStringClass(java_env), 0);
	for(int i = 0; i < size; i++) {
		jobject resulti = java_env->NewStringUTF(result[i]);
		java_env->SetObjectArrayElement(jresult, (jsize)(jint)i, resulti);
	}
	return jresult;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1projectid(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_INFO **)&pointer)->projectid;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1systemid(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_INFO **)&pointer)->systemid;
	return (jint)result_;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1audibility(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_EVENT_INFO **)&pointer)->audibility;
	return (jfloat)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1numinstances(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_INFO **)&pointer)->numinstances;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1set_1numinstances(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jnuminstances) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int numinstances = (int)jnuminstances;
	(*(FMOD_EVENT_INFO **)&pointer)->numinstances = numinstances;
}

JNIEXPORT jobjectArray JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1get_1instances(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	//ThrowException(java_env, RuntimeException, "Not implemented, contact NativeFmodEx support !"); return 0;
	FMOD_EVENT **instances = (FMOD_EVENT **)((*(FMOD_EVENT_INFO **)&pointer)->instances);
	jobjectArray jinstances = 0;
	if(instances) {
		jsize jlength = (jsize)(jint)(-1);
		if(jlength > 0) {
			POINTER_TYPE instancesPointer/* = 0*/;
			*(FMOD_EVENT ***)&instancesPointer = instances;
			jinstances = createEventArray(java_env, instancesPointer, jlength);
		}
	}
	return jinstances;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1INFO_1set_1instances(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jinstances) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_INFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	//ThrowException(java_env, RuntimeException, "Not implemented, contact NativeFmodEx support !"); return;
	FMOD_EVENT **instances = 0;
	if(jinstances) {
		POINTER_TYPE instancesTmp = (POINTER_TYPE)jinstances;
		instances = *(FMOD_EVENT ***)&instancesTmp;
	}
	(*(FMOD_EVENT_INFO **)&pointer)->instances = (FMOD_EVENT **)instances;
}



