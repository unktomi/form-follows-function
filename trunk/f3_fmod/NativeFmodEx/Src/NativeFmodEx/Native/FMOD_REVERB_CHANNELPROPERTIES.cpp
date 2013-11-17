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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_REVERB_CHANNELPROPERTIES *result_ = new FMOD_REVERB_CHANNELPROPERTIES();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_REVERB_CHANNELPROPERTIES **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_REVERB_CHANNELPROPERTIES **)&pointer;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1Direct(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Direct;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1Direct(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jDirect) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Direct = (int)jDirect;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Direct = Direct;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1DirectHF(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->DirectHF;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1DirectHF(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jDirectHF) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int DirectHF = (int)jDirectHF;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->DirectHF = DirectHF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1Room(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Room;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1Room(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jRoom) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Room = (int)jRoom;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Room = Room;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1RoomHF(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->RoomHF;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1RoomHF(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jRoomHF) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int RoomHF = (int)jRoomHF;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->RoomHF = RoomHF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1Obstruction(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Obstruction;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1Obstruction(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jObstruction) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Obstruction = (int)jObstruction;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Obstruction = Obstruction;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1ObstructionLFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->ObstructionLFRatio;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1ObstructionLFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jObstructionLFRatio) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float ObstructionLFRatio = (float)jObstructionLFRatio;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->ObstructionLFRatio = ObstructionLFRatio;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1Occlusion(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Occlusion;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1Occlusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jOcclusion) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Occlusion = (int)jOcclusion;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Occlusion = Occlusion;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1OcclusionLFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->OcclusionLFRatio;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1OcclusionLFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jOcclusionLFRatio) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float OcclusionLFRatio = (float)jOcclusionLFRatio;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->OcclusionLFRatio = OcclusionLFRatio;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1OcclusionRoomRatio(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->OcclusionRoomRatio;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1OcclusionRoomRatio(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jOcclusionRoomRatio) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float OcclusionRoomRatio = (float)jOcclusionRoomRatio;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->OcclusionRoomRatio = OcclusionRoomRatio;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1OcclusionDirectRatio(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->OcclusionDirectRatio;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1OcclusionDirectRatio(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jOcclusionDirectRatio) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float OcclusionDirectRatio = (float)jOcclusionDirectRatio;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->OcclusionDirectRatio = OcclusionDirectRatio;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1Exclusion(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Exclusion;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1Exclusion(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jExclusion) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Exclusion = (int)jExclusion;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Exclusion = Exclusion;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1ExclusionLFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->ExclusionLFRatio;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1ExclusionLFRatio(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jExclusionLFRatio) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float ExclusionLFRatio = (float)jExclusionLFRatio;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->ExclusionLFRatio = ExclusionLFRatio;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1OutsideVolumeHF(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->OutsideVolumeHF;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1OutsideVolumeHF(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jOutsideVolumeHF) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int OutsideVolumeHF = (int)jOutsideVolumeHF;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->OutsideVolumeHF = OutsideVolumeHF;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1DopplerFactor(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->DopplerFactor;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1DopplerFactor(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jDopplerFactor) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float DopplerFactor = (float)jDopplerFactor;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->DopplerFactor = DopplerFactor;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1RolloffFactor(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->RolloffFactor;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1RolloffFactor(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jRolloffFactor) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float RolloffFactor = (float)jRolloffFactor;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->RolloffFactor = RolloffFactor;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1RoomRolloffFactor(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->RoomRolloffFactor;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1RoomRolloffFactor(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jRoomRolloffFactor) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float RoomRolloffFactor = (float)jRoomRolloffFactor;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->RoomRolloffFactor = RoomRolloffFactor;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1AirAbsorptionFactor(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->AirAbsorptionFactor;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1AirAbsorptionFactor(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jAirAbsorptionFactor) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float AirAbsorptionFactor = (float)jAirAbsorptionFactor;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->AirAbsorptionFactor = AirAbsorptionFactor;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1Flags(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Flags;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1Flags(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jFlags) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int Flags = (int)jFlags;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->Flags = Flags;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1get_1ConnectionPoint(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_DSP **)&jresult = (*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->ConnectionPoint;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1REVERB_1CHANNELPROPERTIES_1set_1ConnectionPoint(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jConnectionPoint) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_REVERB_CHANNELPROPERTIES);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE ConnectionPointTmp = (POINTER_TYPE)jConnectionPoint;
	(*(FMOD_REVERB_CHANNELPROPERTIES **)&pointer)->ConnectionPoint = *(FMOD_DSP **)&ConnectionPointTmp;
}



