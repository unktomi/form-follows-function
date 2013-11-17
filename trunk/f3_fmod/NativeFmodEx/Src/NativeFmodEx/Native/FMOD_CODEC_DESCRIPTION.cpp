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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_CODEC_DESCRIPTION *result_ = new FMOD_CODEC_DESCRIPTION();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_CODEC_DESCRIPTION **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_CODEC_DESCRIPTION **)&pointer;
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1get_1name(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	const char *result_ = (const char *)((*(FMOD_CODEC_DESCRIPTION **)&pointer)->name);
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1name(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = 0;
	if(jname) {
		name = getByteArrayElements(java_env, jname);
		(*(FMOD_CODEC_DESCRIPTION **)&pointer)->name = name;
	}
	else {
		(*(FMOD_CODEC_DESCRIPTION **)&pointer)->name = (char *)0;
	}
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1get_1version(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_DESCRIPTION **)&pointer)->version;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1version(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jversion) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int version = (int)jversion;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->version = version;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1get_1defaultasstream(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_DESCRIPTION **)&pointer)->defaultasstream;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1defaultasstream(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jdefaultasstream) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int defaultasstream = (int)jdefaultasstream;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->defaultasstream = defaultasstream;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1get_1timeunits(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_TIMEUNIT result_ = (*(FMOD_CODEC_DESCRIPTION **)&pointer)->timeunits;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1timeunits(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jtimeunits) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int timeunits = (int)jtimeunits;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->timeunits = (FMOD_TIMEUNIT)timeunits;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1open(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jopen) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->open = ((jopen == 0) ? NULL : FMOD_CODEC_OPENCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1close(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jclose) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->close = ((jclose == 0) ? NULL : FMOD_CODEC_CLOSECALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1read(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jread) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->read = ((jread == 0) ? NULL : FMOD_CODEC_READCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1getlength(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jgetlength) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->getlength = ((jgetlength == 0) ? NULL : FMOD_CODEC_GETLENGTHCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1setposition(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jsetposition) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->setposition = ((jsetposition == 0) ? NULL : FMOD_CODEC_SETPOSITIONCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1getposition(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jgetposition) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->getposition = ((jgetposition == 0) ? NULL : FMOD_CODEC_GETPOSITIONCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1soundcreate(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jsoundcreate) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->soundcreate = ((jsoundcreate == 0) ? NULL : FMOD_CODEC_SOUNDCREATECALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1DESCRIPTION_1set_1getwaveformat(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jgetwaveformat) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CODEC_DESCRIPTION **)&pointer)->getwaveformat = ((jgetwaveformat == 0) ? NULL : FMOD_CODEC_GETWAVEFORMAT_BRIDGE);
}



