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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_DSP_DESCRIPTION *result_ = new FMOD_DSP_DESCRIPTION();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_DSP_DESCRIPTION **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_DSP_DESCRIPTION **)&pointer;
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1get_1name(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *result_ = (char *)(*(FMOD_DSP_DESCRIPTION **)&pointer)->name;
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1name(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = 0;
	if(jname) {
		name = (char *)getByteArrayElements(java_env, jname);
		if(name) {
			strncpy((char *)((*(FMOD_DSP_DESCRIPTION **)&pointer)->name), name, 32);
		}
		releaseByteArrayElements(java_env, jname, name);
	}
	else {
		(*(FMOD_DSP_DESCRIPTION **)&pointer)->name[0] = 0;
	}
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1get_1version(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_DSP_DESCRIPTION **)&pointer)->version;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1version(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jversion) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int version = (int)jversion;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->version = version;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1get_1channels(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_DSP_DESCRIPTION **)&pointer)->channels;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1channels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jchannels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int channels = (int)jchannels;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->channels = channels;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1create(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jcreate) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->create = ((jcreate == 0) ? NULL : FMOD_DSP_CREATECALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1release(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jrelease) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->release = ((jrelease == 0) ? NULL : FMOD_DSP_RELEASECALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1reset(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jreset) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->reset = ((jreset == 0) ? NULL : FMOD_DSP_RESETCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1read(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jread) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->read = ((jread == 0) ? NULL : FMOD_DSP_READCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1setposition(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jsetposition) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->setposition = ((jsetposition == 0) ? NULL : FMOD_DSP_SETPOSITIONCALLBACK_BRIDGE);
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1get_1numparameters(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_DSP_DESCRIPTION **)&pointer)->numparameters;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1numparameters(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jnumparameters) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int numparameters = (int)jnumparameters;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->numparameters = numparameters;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1get_1paramdesc(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_DSP_PARAMETERDESC **)&jresult = (*(FMOD_DSP_DESCRIPTION **)&pointer)->paramdesc;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1paramdesc(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jparamdesc) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE paramdescTmp = (POINTER_TYPE)jparamdesc;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->paramdesc = *(FMOD_DSP_PARAMETERDESC **)&paramdescTmp;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1setparameter(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jsetparameter) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->setparameter = ((jsetparameter == 0) ? NULL : FMOD_DSP_SETPARAMCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1getparameter(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jgetparameter) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->getparameter = ((jgetparameter == 0) ? NULL : FMOD_DSP_GETPARAMCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1config(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jconfig) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->config = ((jconfig == 0) ? NULL : FMOD_DSP_DIALOGCALLBACK_BRIDGE);
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1get_1configwidth(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_DSP_DESCRIPTION **)&pointer)->configwidth;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1configwidth(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jconfigwidth) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int configwidth = (int)jconfigwidth;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->configwidth = configwidth;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1get_1configheight(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_DSP_DESCRIPTION **)&pointer)->configheight;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1configheight(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jconfigheight) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int configheight = (int)jconfigheight;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->configheight = configheight;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1get_1userdata(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(void **)&jresult = (*(FMOD_DSP_DESCRIPTION **)&pointer)->userdata;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1DESCRIPTION_1set_1userdata(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_DESCRIPTION);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	(*(FMOD_DSP_DESCRIPTION **)&pointer)->userdata = *(void **)&userdataTmp;
}



