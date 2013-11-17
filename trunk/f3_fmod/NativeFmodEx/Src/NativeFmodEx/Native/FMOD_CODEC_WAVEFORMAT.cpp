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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_CODEC_WAVEFORMAT *result_ = new FMOD_CODEC_WAVEFORMAT();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_CODEC_WAVEFORMAT **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_CODEC_WAVEFORMAT **)&pointer;
}

JNIEXPORT jobject JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1name(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *result_ = (char *)(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->name;
	jobject jresult = 0;
	if(result_) {
		jresult = java_env->NewDirectByteBuffer((char *)result_, 256);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1name(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = 0;
	if(jname) {
		name = (char *)getByteArrayElements(java_env, jname);
		if(name) {
			strncpy((char *)((*(FMOD_CODEC_WAVEFORMAT **)&pointer)->name), name, 256);
		}
		releaseByteArrayElements(java_env, jname, name);
	}
	else {
		(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->name[0] = 0;
	}
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1format(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SOUND_FORMAT result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->format;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1format(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jformat) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int format = (int)jformat;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->format = (FMOD_SOUND_FORMAT)format;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1channels(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->channels;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1channels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jchannels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int channels = (int)jchannels;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->channels = channels;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1frequency(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->frequency;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1frequency(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jfrequency) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int frequency = (int)jfrequency;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->frequency = frequency;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1lengthbytes(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->lengthbytes;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1lengthbytes(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jlengthbytes) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int lengthbytes = (int)jlengthbytes;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->lengthbytes = lengthbytes;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1lengthpcm(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->lengthpcm;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1lengthpcm(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jlengthpcm) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int lengthpcm = (int)jlengthpcm;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->lengthpcm = lengthpcm;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1blockalign(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->blockalign;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1blockalign(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jblockalign) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int blockalign = (int)jblockalign;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->blockalign = blockalign;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1loopstart(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->loopstart;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1loopstart(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jloopstart) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int loopstart = (int)jloopstart;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->loopstart = loopstart;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1loopend(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->loopend;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1loopend(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jloopend) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int loopend = (int)jloopend;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->loopend = loopend;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1mode(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_MODE result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->mode;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1mode(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmode) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int mode = (int)jmode;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->mode = (FMOD_MODE)mode;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1get_1channelmask(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_WAVEFORMAT **)&pointer)->channelmask;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1WAVEFORMAT_1set_1channelmask(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jchannelmask) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_WAVEFORMAT);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int channelmask = (int)jchannelmask;
	(*(FMOD_CODEC_WAVEFORMAT **)&pointer)->channelmask = channelmask;
}



