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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_CREATESOUNDEXINFO *result_ = new FMOD_CREATESOUNDEXINFO();
	result_->cbsize = sizeof(FMOD_CREATESOUNDEXINFO);
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_CREATESOUNDEXINFO **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_CREATESOUNDEXINFO **)&pointer;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1cbsize(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->cbsize;
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1length(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->length;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1length(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jlength) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int length = (int)jlength;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->length = length;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1fileoffset(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->fileoffset;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1fileoffset(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jfileoffset) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int fileoffset = (int)jfileoffset;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->fileoffset = fileoffset;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1numchannels(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->numchannels;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1numchannels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jnumchannels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int numchannels = (int)jnumchannels;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->numchannels = numchannels;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1defaultfrequency(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->defaultfrequency;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1defaultfrequency(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jdefaultfrequency) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int defaultfrequency = (int)jdefaultfrequency;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->defaultfrequency = defaultfrequency;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1format(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SOUND_FORMAT result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->format;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1format(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jformat) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int format = (int)jformat;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->format = (FMOD_SOUND_FORMAT)format;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1decodebuffersize(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->decodebuffersize;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1decodebuffersize(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jdecodebuffersize) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int decodebuffersize = (int)jdecodebuffersize;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->decodebuffersize = decodebuffersize;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1initialsubsound(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->initialsubsound;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1initialsubsound(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jinitialsubsound) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int initialsubsound = (int)jinitialsubsound;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->initialsubsound = initialsubsound;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1numsubsounds(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->numsubsounds;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1numsubsounds(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jnumsubsounds) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int numsubsounds = (int)jnumsubsounds;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->numsubsounds = numsubsounds;
}

JNIEXPORT jobject JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1inclusionlist(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *result_ = (int *)((*(FMOD_CREATESOUNDEXINFO **)&pointer)->inclusionlist);
	jobject jresult = 0;
	if(result_) {
		jresult = java_env->NewDirectByteBuffer((int *)result_, (*(FMOD_CREATESOUNDEXINFO **)&pointer)->inclusionlistnum*sizeof(int));
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1inclusionlist(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jinclusionlist, jlong jinclusionlist_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *inclusionlist = 0;
	if(jinclusionlist) {
		inclusionlist = (int *)((char *)java_env->GetDirectBufferAddress(java_env->NewGlobalRef(jinclusionlist))+jinclusionlist_);
	}
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->inclusionlist = inclusionlist;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1inclusionlistnum(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->inclusionlistnum;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1inclusionlistnum(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jinclusionlistnum) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int inclusionlistnum = (int)jinclusionlistnum;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->inclusionlistnum = inclusionlistnum;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1pcmreadcallback(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jpcmreadcallback) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->pcmreadcallback = ((jpcmreadcallback == 0) ? NULL : FMOD_SOUND_PCMREADCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1pcmsetposcallback(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jpcmsetposcallback) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->pcmsetposcallback = ((jpcmsetposcallback == 0) ? NULL : FMOD_SOUND_PCMSETPOSCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1nonblockcallback(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jnonblockcallback) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->nonblockcallback = ((jnonblockcallback == 0) ? NULL : FMOD_SOUND_NONBLOCKCALLBACK_BRIDGE);
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1dlsname(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	const char *result_ = (const char *)((*(FMOD_CREATESOUNDEXINFO **)&pointer)->dlsname);
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1dlsname(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jdlsname) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *dlsname = 0;
	if(jdlsname) {
		dlsname = getByteArrayElements(java_env, jdlsname);
		(*(FMOD_CREATESOUNDEXINFO **)&pointer)->dlsname = dlsname;
	}
	else {
		(*(FMOD_CREATESOUNDEXINFO **)&pointer)->dlsname = (char *)0;
	}
}

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1encryptionkey(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	const char *result_ = (const char *)((*(FMOD_CREATESOUNDEXINFO **)&pointer)->encryptionkey);
	jstring jresult = 0;
	if(result_) {
		jresult = java_env->NewStringUTF(result_);
	}
	return jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1encryptionkey(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jencryptionkey) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *encryptionkey = 0;
	if(jencryptionkey) {
		encryptionkey = getByteArrayElements(java_env, jencryptionkey);
		(*(FMOD_CREATESOUNDEXINFO **)&pointer)->encryptionkey = encryptionkey;
	}
	else {
		(*(FMOD_CREATESOUNDEXINFO **)&pointer)->encryptionkey = (char *)0;
	}
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1maxpolyphony(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->maxpolyphony;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1maxpolyphony(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmaxpolyphony) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int maxpolyphony = (int)jmaxpolyphony;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->maxpolyphony = maxpolyphony;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1userdata(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(void **)&jresult = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->userdata;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1userdata(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->userdata = *(void **)&userdataTmp;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1suggestedsoundtype(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SOUND_TYPE result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->suggestedsoundtype;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1suggestedsoundtype(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jsuggestedsoundtype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int suggestedsoundtype = (int)jsuggestedsoundtype;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->suggestedsoundtype = (FMOD_SOUND_TYPE)suggestedsoundtype;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1useropen(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean juseropen) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->useropen = ((juseropen == 0) ? NULL : FMOD_FILE_OPENCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1userclose(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean juserclose) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->userclose = ((juserclose == 0) ? NULL : FMOD_FILE_CLOSECALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1userread(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean juserread) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->userread = ((juserread == 0) ? NULL : FMOD_FILE_READCALLBACK_BRIDGE);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1userseek(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean juserseek) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->userseek = ((juserseek == 0) ? NULL : FMOD_FILE_SEEKCALLBACK_BRIDGE);
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1speakermap(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKERMAPTYPE result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->speakermap;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1speakermap(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeakermap) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int speakermap = (int)jspeakermap;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->speakermap = (FMOD_SPEAKERMAPTYPE)speakermap;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1initialsoundgroup(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_SOUNDGROUP **)&jresult = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->initialsoundgroup;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1initialsoundgroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jinitialsoundgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE initialsoundgroupTmp = (POINTER_TYPE)jinitialsoundgroup;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->initialsoundgroup = *(FMOD_SOUNDGROUP **)&initialsoundgroupTmp;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1initialseekposition(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->initialseekposition;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1initialseekposition(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jinitialseekposition) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int initialseekposition = (int)jinitialseekposition;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->initialseekposition = initialseekposition;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1get_1initialseekpostype(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_TIMEUNIT result_ = (*(FMOD_CREATESOUNDEXINFO **)&pointer)->initialseekpostype;
	return (jint)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1CREATESOUNDEXINFO_1set_1initialseekpostype(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jinitialseekpostype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_CREATESOUNDEXINFO);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int initialseekpostype = (int)jinitialseekpostype;
	(*(FMOD_CREATESOUNDEXINFO **)&pointer)->initialseekpostype = (FMOD_TIMEUNIT)initialseekpostype;
}



