/**
 * 				NativeFmodEx Project
 *
 * Want to use FMOD Ex API (www.fmod.org) in the Java language ? NativeFmodEx is made for you.
 * Copyright © 2005-2008 Jérôme JOUVIE (Jouvieje)
 *
 * Created on 23 feb. 2005
 * @version file v1.4.3
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
#include "fmod.h"
#include "fmod.hpp"
#include "fmod_codec.h"
#include "fmod_dsp.h"
#include "fmod_output.h"
#include "org_jouvieje_FmodEx_Structures_StructureJNI.h"
#include "CallbackManager.h"

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_GUID *result_ = new FMOD_GUID();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_GUID **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_GUID **)&pointer;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1get_1Data1(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_GUID);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_GUID **)&pointer)->Data1;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1set_1Data1(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jData1) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_GUID);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Data1 = (int)jData1;
	(*(FMOD_GUID **)&pointer)->Data1 = Data1;
}

JNIEXPORT jshort JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1get_1Data2(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_GUID);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	short result_ = (*(FMOD_GUID **)&pointer)->Data2;
	return (jshort)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1set_1Data2(JNIEnv *java_env, jclass jcls, jlong jpointer, jshort jData2) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_GUID);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	short Data2 = (short)jData2;
	(*(FMOD_GUID **)&pointer)->Data2 = Data2;
}

JNIEXPORT jshort JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1get_1Data3(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_GUID);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	short result_ = (*(FMOD_GUID **)&pointer)->Data3;
	return (jshort)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1set_1Data3(JNIEnv *java_env, jclass jcls, jlong jpointer, jshort jData3) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_GUID);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	short Data3 = (short)jData3;
	(*(FMOD_GUID **)&pointer)->Data3 = Data3;
}

JNIEXPORT jobject JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1get_1Data4(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_GUID);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *result_ = (char *)(*(FMOD_GUID **)&pointer)->Data4;
	jobject jresult = 0;
	if(result_) {
		jresult = java_env->NewDirectByteBuffer((char *)result_, 8);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1GUID_1set_1Data4(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jData4) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_GUID);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *Data4 = 0;
	if(jData4) {
		Data4 = (char *)getByteArrayElements(java_env, jData4);
		if(Data4) {
			strncpy((char *)((*(FMOD_GUID **)&pointer)->Data4), Data4, 8);
		}
		releaseByteArrayElements(java_env, jData4, Data4);
	}
	else {
		(*(FMOD_GUID **)&pointer)->Data4[0] = 0;
	}
}



