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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_ADVANCEDSETTINGS *result_ = new FMOD_ADVANCEDSETTINGS();
	result_->cbsize = sizeof(FMOD_ADVANCEDSETTINGS);
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_ADVANCEDSETTINGS **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_ADVANCEDSETTINGS **)&pointer;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1cbsize(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->cbsize;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1maxMPEGcodecs(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->maxMPEGcodecs;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1maxMPEGcodecs(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmaxMPEGcodecs) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int maxMPEGcodecs = (int)jmaxMPEGcodecs;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->maxMPEGcodecs = maxMPEGcodecs;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1maxADPCMcodecs(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->maxADPCMcodecs;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1maxADPCMcodecs(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmaxADPCMcodecs) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int maxADPCMcodecs = (int)jmaxADPCMcodecs;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->maxADPCMcodecs = maxADPCMcodecs;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1maxXMAcodecs(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->maxXMAcodecs;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1maxXMAcodecs(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmaxXMAcodecs) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int maxXMAcodecs = (int)jmaxXMAcodecs;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->maxXMAcodecs = maxXMAcodecs;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1maxPCMcodecs(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->maxPCMcodecs;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1maxPCMcodecs(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmaxPCMcodecs) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int maxPCMcodecs = (int)jmaxPCMcodecs;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->maxPCMcodecs = maxPCMcodecs;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1ASIONumChannels(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->ASIONumChannels;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1ASIONumChannels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jASIONumChannels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int ASIONumChannels = (int)jASIONumChannels;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->ASIONumChannels = ASIONumChannels;
}

JNIEXPORT jobjectArray JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1ASIOChannelList(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char **result = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->ASIOChannelList;
	jint size = (jint)(*(FMOD_ADVANCEDSETTINGS **)&pointer)->ASIONumChannels;
	jobjectArray jresult = java_env->NewObjectArray(size, getStringClass(java_env), 0);
	for(int i = 0; i < size; i++) {
		jobject resulti = java_env->NewStringUTF(result[i]);
		java_env->SetObjectArrayElement(jresult, (jsize)(jint)i, resulti);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1ASIOChannelList(JNIEnv *java_env, jclass jcls, jlong jpointer, jobjectArray jASIOChannelList) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
//ThrowException(java_env, RuntimeException, "Not implemented, contact NativeFmodEx support !");
	int size = (int)(jint)java_env->GetArrayLength(jASIOChannelList);
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->ASIONumChannels = size;
	
	char **cresult = new char *[size];
	for(int i = 0; i < size; i++) {
		jstring jstringi = (jstring)java_env->GetObjectArrayElement(jASIOChannelList, (jsize)(jint)i);
		char* cstringi = getStringElements(java_env, jstringi);
		cresult[i] = cstringi;
	}
	
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->ASIOChannelList = cresult;
}

JNIEXPORT jobject JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1ASIOSpeakerList(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	//POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	ThrowException(java_env, RuntimeException, "Not implemented, contact NativeFmodEx support"); return 0;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1ASIOSpeakerList(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jASIOSpeakerList, jlong jASIOSpeakerList_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	//ThrowException(java_env, RuntimeException, "Not implemented, contact NativeFmodEx support");
	FMOD_SPEAKER *ASIOSpeakerList = 0;
	if(jASIOSpeakerList) {
		ASIOSpeakerList = (FMOD_SPEAKER *)((char *)java_env->GetDirectBufferAddress(java_env->NewGlobalRef(jASIOSpeakerList))+jASIOSpeakerList_);
	}
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->ASIOSpeakerList = ASIOSpeakerList;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1max3DReverbDSPs(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->max3DReverbDSPs;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1max3DReverbDSPs(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmax3DReverbDSPs) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int max3DReverbDSPs = (int)jmax3DReverbDSPs;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->max3DReverbDSPs = max3DReverbDSPs;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1HRTFMinAngle(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->HRTFMinAngle;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1HRTFMinAngle(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jHRTFMinAngle) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float HRTFMinAngle = (float)jHRTFMinAngle;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->HRTFMinAngle = HRTFMinAngle;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1HRTFMaxAngle(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->HRTFMaxAngle;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1HRTFMaxAngle(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jHRTFMaxAngle) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float HRTFMaxAngle = (float)jHRTFMaxAngle;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->HRTFMaxAngle = HRTFMaxAngle;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1HRTFFreq(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->HRTFFreq;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1HRTFFreq(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jHRTFFreq) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float HRTFFreq = (float)jHRTFFreq;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->HRTFFreq = HRTFFreq;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1vol0virtualvol(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->vol0virtualvol;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1vol0virtualvol(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jvol0virtualvol) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float vol0virtualvol = (float)jvol0virtualvol;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->vol0virtualvol = vol0virtualvol;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1eventqueuesize(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->eventqueuesize;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1eventqueuesize(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jeventqueuesize) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int eventqueuesize = (int)jeventqueuesize;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->eventqueuesize = eventqueuesize;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1defaultDecodeBufferSize(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->defaultDecodeBufferSize;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1defaultDecodeBufferSize(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jdefaultDecodeBufferSize) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int defaultDecodeBufferSize = (int)jdefaultDecodeBufferSize;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->defaultDecodeBufferSize = defaultDecodeBufferSize;
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1debugLogFilename(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *result_ = (char *)((*(FMOD_ADVANCEDSETTINGS **)&pointer)->debugLogFilename);
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1debugLogFilename(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jdebugLogFilename) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *debugLogFilename = 0;
	if(jdebugLogFilename) {
		debugLogFilename = getByteArrayElements(java_env, jdebugLogFilename);
		(*(FMOD_ADVANCEDSETTINGS **)&pointer)->debugLogFilename = debugLogFilename;
	}
	else {
		(*(FMOD_ADVANCEDSETTINGS **)&pointer)->debugLogFilename = (char *)0;
	}
}

JNIEXPORT jshort JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1get_1profileport(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	short result_ = (*(FMOD_ADVANCEDSETTINGS **)&pointer)->profileport;
	return (jshort)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1ADVANCEDSETTINGS_1set_1profileport(JNIEnv *java_env, jclass jcls, jlong jpointer, jshort jprofileport) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_ADVANCEDSETTINGS);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	short profileport = (short)jprofileport;
	(*(FMOD_ADVANCEDSETTINGS **)&pointer)->profileport = profileport;
}



