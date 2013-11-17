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
#include "malloc.h"
#include "CallbackManager.h"

jclass byteBufferClass = 0;
jclass getByteBufferClass(JNIEnv *java_env) {
	if(!byteBufferClass) {
		byteBufferClass = (jclass)java_env->NewGlobalRef(java_env->FindClass("java/nio/ByteBuffer"));
	}
	return byteBufferClass;
}

jclass caller = 0;
void connectCaller(JNIEnv *java_env) {
	caller = (jclass)java_env->NewGlobalRef(java_env->FindClass("org/jouvieje/FmodDesigner/Callbacks/CallbackBridge"));
	if(java_env->ExceptionCheck()) {
		java_env->ExceptionClear();
		caller = 0;
		ThrowException(java_env, InitException, "Connection to CallbackBridge fails.");
	}
}

jmethodID callbackId[2];
void connectCallbacks(JNIEnv *java_env) {
	static struct {
		const char *name;
		const char *signature;
	}callbacks[2] = {
		{"FMOD_EVENT_CALLBACK_BRIDGE", "(JIJLorg/jouvieje/FmodEx/Misc/Pointer;J)I"},
		{"FMOD_MUSIC_CALLBACK_BRIDGE", "(IJJJ)I"}
	};

	for(int i = 0; i < 2; i++) {
		callbackId[i] = java_env->GetStaticMethodID(caller, callbacks[i].name, callbacks[i].signature);
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionClear();
			ThrowException(java_env, InitException, "Connection to a Callback fails.");
			return;
		}
	}
}

JavaVM *jvm;
void attachJavaVM(JNIEnv *java_env) {
	java_env->GetJavaVM(&jvm);
	connectCaller(java_env);
	if(caller) {
		connectCallbacks(java_env);
	}
}
bool acquire_jenv(JNIEnv **java_env) {
	if(jvm->GetEnv((void **)java_env, JNI_VERSION_1_4) != JNI_OK) {
		jvm->AttachCurrentThread((void **)java_env, 0);
		return true;
	}
	return false;
}
void leave_jenv(bool attached) {
	if(attached) {
		jvm->DetachCurrentThread();
	}
}

FMOD_RESULT F_CALLBACK FMOD_EVENT_CALLBACK_BRIDGE(FMOD_EVENT * event, FMOD_EVENT_CALLBACKTYPE type, void * param1, void * param2, void * userdata) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jevent/* = 0*/;
	*(FMOD_EVENT **)&jevent = (FMOD_EVENT *)event;
	POINTER_TYPE jparam1/* = 0*/;
	*(void **)&jparam1 = param1;
	POINTER_TYPE param2NewPointerAddress/* = 0*/;
	*(void **)&param2NewPointerAddress = param2;
	jobject jparam2 = newPointer(java_env, param2NewPointerAddress);
	POINTER_TYPE juserdata/* = 0*/;
	*(void **)&juserdata = userdata;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[0], (jlong)jevent, (jint)type, (jlong)jparam1, (jobject)jparam2, (jlong)juserdata);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	if(jparam2 && (type == FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_CREATE)) {
		POINTER_TYPE jparam2Address = getPointerAddress(java_env, jparam2);
		if(jparam2Address) {
			*(FMOD::Sound **)param2 = *(FMOD::Sound **)&jparam2Address;
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_MUSIC_CALLBACK_BRIDGE(FMOD_MUSIC_CALLBACKTYPE type, void * param1, void * param2, void * userdata) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jparam1/* = 0*/;
	*(void **)&jparam1 = param1;
	POINTER_TYPE jparam2/* = 0*/;
	*(void **)&jparam2 = param2;
	POINTER_TYPE juserdata/* = 0*/;
	*(void **)&juserdata = userdata;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[1], (jint)type, (jlong)jparam1, (jlong)jparam2, (jlong)juserdata);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}


