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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_FmodDesigner_1EventSystem_1Create(JNIEnv *java_env, jclass jcls, jobject jeventsystem) {
	FMOD::EventSystem *eventsystem/* = 0*/;

	FMOD_RESULT result_ = FMOD::EventSystem_Create(&eventsystem);

	if(jeventsystem) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::EventSystem **)&newAddress = eventsystem;
		setPointerAddress(java_env, jeventsystem, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_FmodDesigner_1NetEventSystem_1Init(JNIEnv *java_env, jclass jcls, jlong jeventsystem, jshort jport) {
	FMOD::EventSystem *eventsystem = 0;
	if(jeventsystem) {
		POINTER_TYPE eventsystemTmp = (POINTER_TYPE)jeventsystem;
		eventsystem = *(FMOD::EventSystem **)&eventsystemTmp;
	}
	short port = (short)jport;

	FMOD_RESULT result_ = FMOD::NetEventSystem_Init(eventsystem, port);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_FmodDesigner_1NetEventSystem_1Update(JNIEnv *java_env, jclass jcls) {

	FMOD_RESULT result_ = FMOD::NetEventSystem_Update();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_FmodDesigner_1NetEventSystem_1Shutdown(JNIEnv *java_env, jclass jcls) {

	FMOD_RESULT result_ = FMOD::NetEventSystem_Shutdown();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_FmodDesignerJNI_FmodDesigner_1NetEventSystem_1GetVersion(JNIEnv *java_env, jclass jcls, jobject jversion, jlong jversion_) {
	unsigned int *version = 0;
	if(jversion) {
		version = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jversion)+jversion_);
	}

	FMOD_RESULT result_ = FMOD::NetEventSystem_GetVersion(version);

	return (jint)result_;
}


