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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_EVENT_LOADINFO *result_ = new FMOD_EVENT_LOADINFO();
	result_->size = sizeof(FMOD_EVENT_LOADINFO);
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_EVENT_LOADINFO **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_EVENT_LOADINFO **)&pointer;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1get_1size(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_LOADINFO **)&pointer)->size;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1set_1size(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jsize) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int size = (int)jsize;
	(*(FMOD_EVENT_LOADINFO **)&pointer)->size = size;
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1get_1encryptionkey(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *result_ = (char *)((*(FMOD_EVENT_LOADINFO **)&pointer)->encryptionkey);
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1set_1encryptionkey(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jencryptionkey) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *encryptionkey = 0;
	if(jencryptionkey) {
		encryptionkey = getByteArrayElements(java_env, jencryptionkey);
		(*(FMOD_EVENT_LOADINFO **)&pointer)->encryptionkey = encryptionkey;
	}
	else {
		(*(FMOD_EVENT_LOADINFO **)&pointer)->encryptionkey = (char *)0;
	}
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1get_1sounddefentrylimit(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_EVENT_LOADINFO **)&pointer)->sounddefentrylimit;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1set_1sounddefentrylimit(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jsounddefentrylimit) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float sounddefentrylimit = (float)jsounddefentrylimit;
	(*(FMOD_EVENT_LOADINFO **)&pointer)->sounddefentrylimit = sounddefentrylimit;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1get_1loadfrommemory_1length(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_LOADINFO **)&pointer)->loadfrommemory_length;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1set_1loadfrommemory_1length(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jloadfrommemory_length) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int loadfrommemory_length = (int)jloadfrommemory_length;
	(*(FMOD_EVENT_LOADINFO **)&pointer)->loadfrommemory_length = loadfrommemory_length;
}

JNIEXPORT jboolean JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1get_1override_1category_1vals(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool result_ = (*(FMOD_EVENT_LOADINFO **)&pointer)->override_category_vals;
	return (jboolean)(result_ != 0);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1LOADINFO_1set_1override_1category_1vals(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean joverride_category_vals) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_LOADINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool override_category_vals = (bool)(joverride_category_vals != 0);
	(*(FMOD_EVENT_LOADINFO **)&pointer)->override_category_vals = override_category_vals;
}



