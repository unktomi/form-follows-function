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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_CODEC_STATE *result_ = new FMOD_CODEC_STATE();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_CODEC_STATE **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_CODEC_STATE **)&pointer;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1get_1numsubsounds(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_STATE **)&pointer)->numsubsounds;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1set_1numsubsounds(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jnumsubsounds) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int numsubsounds = (int)jnumsubsounds;
	(*(FMOD_CODEC_STATE **)&pointer)->numsubsounds = numsubsounds;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1get_1waveformat(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_CODEC_WAVEFORMAT **)&jresult = (*(FMOD_CODEC_STATE **)&pointer)->waveformat;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1set_1waveformat(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jwaveformat) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE waveformatTmp = (POINTER_TYPE)jwaveformat;
	(*(FMOD_CODEC_STATE **)&pointer)->waveformat = *(FMOD_CODEC_WAVEFORMAT **)&waveformatTmp;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1get_1plugindata(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(void **)&jresult = (*(FMOD_CODEC_STATE **)&pointer)->plugindata;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1set_1plugindata(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jplugindata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE plugindataTmp = (POINTER_TYPE)jplugindata;
	(*(FMOD_CODEC_STATE **)&pointer)->plugindata = *(void **)&plugindataTmp;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1get_1filehandle(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(void **)&jresult = (*(FMOD_CODEC_STATE **)&pointer)->filehandle;
	return (jlong)jresult;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1get_1filesize(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CODEC_STATE **)&pointer)->filesize;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1invoke_1fileread(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jhandle, jobject jbuffer, jlong jbuffer_, jint jsizebytes, jobject jbytesread, jlong jbytesread_, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE handleTmp = (POINTER_TYPE)jhandle;
	void *handle = *(void **)&handleTmp;
	void *buffer = 0;
	if(jbuffer) {
		buffer = (void *)((char *)java_env->GetDirectBufferAddress(jbuffer)+jbuffer_);
	}
	int sizebytes = (int)jsizebytes;
	unsigned int *bytesread = 0;
	if(jbytesread) {
		bytesread = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jbytesread)+jbytesread_);
	}
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD_CODEC_STATE **)&pointer)->fileread(handle, buffer, sizebytes, bytesread, userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1invoke_1fileseek(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jhandle, jint jpos, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE handleTmp = (POINTER_TYPE)jhandle;
	void *handle = *(void **)&handleTmp;
	int pos = (int)jpos;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD_CODEC_STATE **)&pointer)->fileseek(handle, pos, userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CODEC_1STATE_1invoke_1metadata(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jcodec_state, jint jtagtype, 	jobject jname, jlong jname_, jlong jdata, jint jdatalen, jint jdatatype, jint junique) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CODEC_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_CODEC_STATE *codec_state = 0;
	if(jcodec_state) {
		POINTER_TYPE codec_stateTmp = (POINTER_TYPE)jcodec_state;
		codec_state = *(FMOD_CODEC_STATE **)&codec_stateTmp;
	}
	FMOD_TAGTYPE tagtype = (FMOD_TAGTYPE)jtagtype;
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	POINTER_TYPE dataTmp = (POINTER_TYPE)jdata;
	void *data = *(void **)&dataTmp;
	int datalen = (int)jdatalen;
	FMOD_TAGDATATYPE datatype = (FMOD_TAGDATATYPE)jdatatype;
	int unique = (int)junique;

	FMOD_RESULT result_ = (*(FMOD_CODEC_STATE **)&pointer)->metadata(codec_state, tagtype, name, data, datalen, datatype, unique);

	return (jint)result_;
}



