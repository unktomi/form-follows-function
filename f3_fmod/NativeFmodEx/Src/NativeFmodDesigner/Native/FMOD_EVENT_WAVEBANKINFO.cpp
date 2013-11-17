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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_EVENT_WAVEBANKINFO *result_ = new FMOD_EVENT_WAVEBANKINFO();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_EVENT_WAVEBANKINFO **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_EVENT_WAVEBANKINFO **)&pointer;
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1get_1name(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_WAVEBANKINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *result_ = (char *)((*(FMOD_EVENT_WAVEBANKINFO **)&pointer)->name);
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1get_1streamrefcnt(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_WAVEBANKINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_WAVEBANKINFO **)&pointer)->streamrefcnt;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1get_1samplerefcnt(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_WAVEBANKINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_WAVEBANKINFO **)&pointer)->samplerefcnt;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1get_1numstreams(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_WAVEBANKINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_WAVEBANKINFO **)&pointer)->numstreams;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1get_1maxstreams(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_WAVEBANKINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_WAVEBANKINFO **)&pointer)->maxstreams;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1get_1streamsinuse(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_WAVEBANKINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_WAVEBANKINFO **)&pointer)->streamsinuse;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1get_1streammemory(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_WAVEBANKINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_WAVEBANKINFO **)&pointer)->streammemory;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1WAVEBANKINFO_1get_1samplememory(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_WAVEBANKINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_WAVEBANKINFO **)&pointer)->samplememory;
	return (jint)result_;
}



