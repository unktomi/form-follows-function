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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1newArray(JNIEnv *java_env, jclass jcls, jint length) {
	FMOD_DSP_PARAMETERDESC *array = new FMOD_DSP_PARAMETERDESC[(int)length];
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_DSP_PARAMETERDESC **)&jresult = array;
	return (jlong)jresult;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1SIZEOF(JNIEnv *java_env, jclass jcls) {
	return (jint)sizeof(FMOD_DSP_PARAMETERDESC);
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_DSP_PARAMETERDESC *result_ = new FMOD_DSP_PARAMETERDESC();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_DSP_PARAMETERDESC **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_DSP_PARAMETERDESC **)&pointer;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1get_1min(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_DSP_PARAMETERDESC **)&pointer)->min;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1set_1min(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jmin) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float min = (float)jmin;
	(*(FMOD_DSP_PARAMETERDESC **)&pointer)->min = min;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1get_1max(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_DSP_PARAMETERDESC **)&pointer)->max;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1set_1max(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jmax) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float max = (float)jmax;
	(*(FMOD_DSP_PARAMETERDESC **)&pointer)->max = max;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1get_1defaultval(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float result_ = (*(FMOD_DSP_PARAMETERDESC **)&pointer)->defaultval;
	return (jfloat)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1set_1defaultval(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jdefaultval) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float defaultval = (float)jdefaultval;
	(*(FMOD_DSP_PARAMETERDESC **)&pointer)->defaultval = defaultval;
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1get_1name(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *result_ = (char *)(*(FMOD_DSP_PARAMETERDESC **)&pointer)->name;
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1set_1name(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = 0;
	if(jname) {
		name = (char *)getByteArrayElements(java_env, jname);
		if(name) {
			strncpy((char *)((*(FMOD_DSP_PARAMETERDESC **)&pointer)->name), name, 16);
		}
		releaseByteArrayElements(java_env, jname, name);
	}
	else {
		(*(FMOD_DSP_PARAMETERDESC **)&pointer)->name[0] = 0;
	}
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1get_1label(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *result_ = (char *)(*(FMOD_DSP_PARAMETERDESC **)&pointer)->label;
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1set_1label(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jlabel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *label = 0;
	if(jlabel) {
		label = (char *)getByteArrayElements(java_env, jlabel);
		if(label) {
			strncpy((char *)((*(FMOD_DSP_PARAMETERDESC **)&pointer)->label), label, 16);
		}
		releaseByteArrayElements(java_env, jlabel, label);
	}
	else {
		(*(FMOD_DSP_PARAMETERDESC **)&pointer)->label[0] = 0;
	}
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1get_1description(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	const char *result_ = (const char *)((*(FMOD_DSP_PARAMETERDESC **)&pointer)->description);
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1PARAMETERDESC_1set_1description(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jdescription) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_PARAMETERDESC);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *description = 0;
	if(jdescription) {
		description = getByteArrayElements(java_env, jdescription);
		(*(FMOD_DSP_PARAMETERDESC **)&pointer)->description = description;
	}
	else {
		(*(FMOD_DSP_PARAMETERDESC **)&pointer)->description = (char *)0;
	}
}



