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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1create(JNIEnv *java_env, jclass jcls, jfloat x, jfloat y, jfloat z) {
	FMOD_VECTOR *result_ = new FMOD_VECTOR();
	result_->x = (float)x;
	result_->y = (float)y;
	result_->z = (float)z;
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_VECTOR **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1newArray(JNIEnv *java_env, jclass jcls, jint length) {
	FMOD_VECTOR *array = new FMOD_VECTOR[(int)length];
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_VECTOR **)&jresult = array;
	return (jlong)jresult;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1SIZEOF(JNIEnv *java_env, jclass jcls) {
	return (jint)sizeof(FMOD_VECTOR);
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_VECTOR *result_ = new FMOD_VECTOR();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_VECTOR **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_VECTOR **)&pointer;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1get_1x(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_VECTOR);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_VECTOR **)&pointer)->x;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1set_1x(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jx) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_VECTOR);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float x = (float)jx;
	(*(FMOD_VECTOR **)&pointer)->x = x;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1get_1y(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_VECTOR);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_VECTOR **)&pointer)->y;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1set_1y(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jy) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_VECTOR);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float y = (float)jy;
	(*(FMOD_VECTOR **)&pointer)->y = y;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1get_1z(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_VECTOR);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_VECTOR **)&pointer)->z;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1set_1z(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jz) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_VECTOR);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float z = (float)jz;
	(*(FMOD_VECTOR **)&pointer)->z = z;
}


JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1set_1xyz__JJ(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jvector) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_VECTOR);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	if(jvector) {
		POINTER_TYPE vector = (POINTER_TYPE)jvector;
		FMOD_VECTOR *pointer_ = *(FMOD_VECTOR **)&pointer;
		FMOD_VECTOR *vector_ = *(FMOD_VECTOR **)&vector;
		
		pointer_->x = vector_->x;
		pointer_->y = vector_->y;
		pointer_->z = vector_->z;
	}
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1VECTOR_1set_1xyz__JFFF(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat x, jfloat y, jfloat z) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_VECTOR);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *pointer_ = *(FMOD_VECTOR **)&pointer;
	pointer_->x = (float)x;
	pointer_->y = (float)y;
	pointer_->z = (float)z;
}


