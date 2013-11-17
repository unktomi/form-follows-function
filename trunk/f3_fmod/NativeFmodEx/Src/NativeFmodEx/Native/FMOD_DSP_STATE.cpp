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

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1STATE_1new(JNIEnv *java_env, jclass jcls) {
	FMOD_DSP_STATE *result_ = new FMOD_DSP_STATE();
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_DSP_STATE **)&jresult = result_;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1STATE_1delete(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	delete *(FMOD_DSP_STATE **)&pointer;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1STATE_1get_1instance(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(FMOD_DSP **)&jresult = (*(FMOD_DSP_STATE **)&pointer)->instance;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1STATE_1get_1plugindata(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE jresult/* = 0*/;
	*(void **)&jresult = (*(FMOD_DSP_STATE **)&pointer)->plugindata;
	return (jlong)jresult;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1STATE_1set_1plugindata(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jplugindata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_STATE);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE plugindataTmp = (POINTER_TYPE)jplugindata;
	(*(FMOD_DSP_STATE **)&pointer)->plugindata = *(void **)&plugindataTmp;
}

JNIEXPORT jshort JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1STATE_1get_1speakermask(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_STATE);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	short result_ = (*(FMOD_DSP_STATE **)&pointer)->speakermask;
	return (jshort)result_;
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_FMOD_1DSP_1STATE_1set_1speakermask(JNIEnv *java_env, jclass jcls, jlong jpointer, jshort jspeakermask) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_FMOD_DSP_STATE);
		return;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	short speakermask = (short)jspeakermask;
	(*(FMOD_DSP_STATE **)&pointer)->speakermask = speakermask;
}



