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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_REVERB_PROPERTIES *result_ = new FMOD_REVERB_PROPERTIES();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_REVERB_PROPERTIES **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_REVERB_PROPERTIES **)&pointer;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1Instance(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->Instance;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1Instance(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jInstance) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Instance = (int)jInstance;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->Instance = Instance;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1Environment(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->Environment;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1Environment(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jEnvironment) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Environment = (int)jEnvironment;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->Environment = Environment;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1EnvSize(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->EnvSize;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1EnvSize(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jEnvSize) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float EnvSize = (float)jEnvSize;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->EnvSize = EnvSize;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1EnvDiffusion(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->EnvDiffusion;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1EnvDiffusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jEnvDiffusion) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float EnvDiffusion = (float)jEnvDiffusion;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->EnvDiffusion = EnvDiffusion;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1Room(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->Room;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1Room(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jRoom) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Room = (int)jRoom;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->Room = Room;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1RoomHF(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->RoomHF;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1RoomHF(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jRoomHF) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int RoomHF = (int)jRoomHF;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->RoomHF = RoomHF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1RoomLF(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->RoomLF;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1RoomLF(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jRoomLF) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int RoomLF = (int)jRoomLF;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->RoomLF = RoomLF;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1DecayTime(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->DecayTime;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1DecayTime(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jDecayTime) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float DecayTime = (float)jDecayTime;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->DecayTime = DecayTime;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1DecayHFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->DecayHFRatio;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1DecayHFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jDecayHFRatio) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float DecayHFRatio = (float)jDecayHFRatio;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->DecayHFRatio = DecayHFRatio;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1DecayLFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->DecayLFRatio;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1DecayLFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jDecayLFRatio) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float DecayLFRatio = (float)jDecayLFRatio;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->DecayLFRatio = DecayLFRatio;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1Reflections(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->Reflections;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1Reflections(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jReflections) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Reflections = (int)jReflections;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->Reflections = Reflections;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1ReflectionsDelay(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->ReflectionsDelay;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1ReflectionsDelay(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jReflectionsDelay) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float ReflectionsDelay = (float)jReflectionsDelay;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->ReflectionsDelay = ReflectionsDelay;
}

JNIEXPORT jobject JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1ReflectionsPan(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *result_ = (float *)(*(FMOD_REVERB_PROPERTIES **)&pointer)->ReflectionsPan;
	jobject jresult = 0;
	if(result_) {
		jresult = java_env->NewDirectByteBuffer((float *)result_, (jlong)(3*sizeof(float)));
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1ReflectionsPan(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jReflectionsPan, jlong jReflectionsPan_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *ReflectionsPan = 0;
	if(jReflectionsPan) {
		ReflectionsPan = (float *)((char *)java_env->GetDirectBufferAddress(jReflectionsPan)+jReflectionsPan_);
	}
	float *temp = (float *)(*(FMOD_REVERB_PROPERTIES **)&pointer)->ReflectionsPan;
	for(int i = 0; i < 3; i++) {
		temp[i] = *((float *) ReflectionsPan + i);
	}
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1Reverb(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->Reverb;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1Reverb(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jReverb) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Reverb = (int)jReverb;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->Reverb = Reverb;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1ReverbDelay(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->ReverbDelay;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1ReverbDelay(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jReverbDelay) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float ReverbDelay = (float)jReverbDelay;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->ReverbDelay = ReverbDelay;
}

JNIEXPORT jobject JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1ReverbPan(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *result_ = (float *)(*(FMOD_REVERB_PROPERTIES **)&pointer)->ReverbPan;
	jobject jresult = 0;
	if(result_) {
		jresult = java_env->NewDirectByteBuffer((float *)result_, (jlong)(3*sizeof(float)));
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1ReverbPan(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jReverbPan, jlong jReverbPan_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *ReverbPan = 0;
	if(jReverbPan) {
		ReverbPan = (float *)((char *)java_env->GetDirectBufferAddress(jReverbPan)+jReverbPan_);
	}
	float *temp = (float *)(*(FMOD_REVERB_PROPERTIES **)&pointer)->ReverbPan;
	for(int i = 0; i < 3; i++) {
		temp[i] = *((float *) ReverbPan + i);
	}
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1EchoTime(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->EchoTime;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1EchoTime(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jEchoTime) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float EchoTime = (float)jEchoTime;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->EchoTime = EchoTime;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1EchoDepth(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->EchoDepth;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1EchoDepth(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jEchoDepth) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float EchoDepth = (float)jEchoDepth;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->EchoDepth = EchoDepth;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1ModulationTime(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->ModulationTime;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1ModulationTime(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jModulationTime) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float ModulationTime = (float)jModulationTime;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->ModulationTime = ModulationTime;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1ModulationDepth(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->ModulationDepth;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1ModulationDepth(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jModulationDepth) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float ModulationDepth = (float)jModulationDepth;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->ModulationDepth = ModulationDepth;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1AirAbsorptionHF(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->AirAbsorptionHF;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1AirAbsorptionHF(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jAirAbsorptionHF) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float AirAbsorptionHF = (float)jAirAbsorptionHF;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->AirAbsorptionHF = AirAbsorptionHF;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1HFReference(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->HFReference;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1HFReference(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jHFReference) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float HFReference = (float)jHFReference;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->HFReference = HFReference;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1LFReference(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->LFReference;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1LFReference(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jLFReference) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float LFReference = (float)jLFReference;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->LFReference = LFReference;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1RoomRolloffFactor(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->RoomRolloffFactor;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1RoomRolloffFactor(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jRoomRolloffFactor) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float RoomRolloffFactor = (float)jRoomRolloffFactor;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->RoomRolloffFactor = RoomRolloffFactor;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1Diffusion(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->Diffusion;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1Diffusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jDiffusion) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float Diffusion = (float)jDiffusion;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->Diffusion = Diffusion;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1Density(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->Density;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1Density(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jDensity) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float Density = (float)jDensity;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->Density = Density;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1get_1Flags(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_PROPERTIES **)&pointer)->Flags;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1PROPERTIES_1set_1Flags(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jFlags) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_PROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Flags = (int)jFlags;
	(*(FMOD_REVERB_PROPERTIES **)&pointer)->Flags = Flags;
}



