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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_EVENT_SYSTEMINFO *result_ = new FMOD_EVENT_SYSTEMINFO();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_EVENT_SYSTEMINFO **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_EVENT_SYSTEMINFO **)&pointer;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1get_1numevents(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_SYSTEMINFO **)&pointer)->numevents;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1get_1eventmemory(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_SYSTEMINFO **)&pointer)->eventmemory;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1get_1numinstances(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_SYSTEMINFO **)&pointer)->numinstances;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1get_1instancememory(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_SYSTEMINFO **)&pointer)->instancememory;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1get_1dspmemory(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_SYSTEMINFO **)&pointer)->dspmemory;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1get_1numwavebanks(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_EVENT_SYSTEMINFO **)&pointer)->numwavebanks;
	return (jint)result_;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1get_1wavebankinfo(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_EVENT_WAVEBANKINFO **)&jresult = (*(FMOD_EVENT_SYSTEMINFO **)&pointer)->wavebankinfo;
	return (jlong)jresult;
}

JNIEXPORT jobject JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1get_1numplayingevents(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *result_ = (int *)(*(FMOD_EVENT_SYSTEMINFO **)&pointer)->numplayingevents;
	jobject jresult = 0;
	if(result_) {
		jresult = java_env->NewDirectByteBuffer((int *)result_, (jlong)(4*sizeof(int)));
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1set_1numplayingevents(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumplayingevents, jlong jnumplayingevents_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numplayingevents = 0;
	if(jnumplayingevents) {
		numplayingevents = (int *)((char *)java_env->GetDirectBufferAddress(jnumplayingevents)+jnumplayingevents_);
	}
	int *temp = (int *)(*(FMOD_EVENT_SYSTEMINFO **)&pointer)->numplayingevents;
	for(int i = 0; i < 4; i++) {
		temp[i] = *((int *) numplayingevents + i);
	}
}

JNIEXPORT jobjectArray JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1get_1playingevents(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	//ThrowException(java_env, RuntimeException, "Not implemented, contact NativeFmodEx support !"); return 0;
	FMOD_EVENT **playingevents = (FMOD_EVENT **)((*(FMOD_EVENT_SYSTEMINFO **)&pointer)->playingevents);
	jobjectArray jplayingevents = 0;
	if(playingevents) {
		jsize jlength = (jsize)(jint)((*(FMOD_EVENT_SYSTEMINFO **)&pointer)->numplayingevents);
		if(jlength > 0) {
			POINTER_TYPE playingeventsPointer/* = 0*/;
			*(FMOD_EVENT ***)&playingeventsPointer = playingevents;
			jplayingevents = createEventArray(java_env, playingeventsPointer, jlength);
		}
	}
	return jplayingevents;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodDesigner_Structures_StructureJNI_FMOD_1EVENT_1SYSTEMINFO_1set_1playingevents(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jplayingevents) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_EVENT_SYSTEMINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	//ThrowException(java_env, RuntimeException, "Not implemented, contact NativeFmodEx support !"); return;
	FMOD_EVENT **playingevents = 0;
	if(jplayingevents) {
		POINTER_TYPE playingeventsTmp = (POINTER_TYPE)jplayingevents;
		playingevents = *(FMOD_EVENT ***)&playingeventsTmp;
	}
	(*(FMOD_EVENT_SYSTEMINFO **)&pointer)->playingevents = (FMOD_EVENT **)playingevents;
}



