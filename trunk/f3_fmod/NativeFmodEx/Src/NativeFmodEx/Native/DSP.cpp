/**
 * 				NativeFmodEx Project
 *
 * Want to use FMOD Ex API (www.fmod.org) in the Java language ? NativeFmodEx is made for you.
 * Copyright © 2005-2008 Jérôme JOUVIE (Jouvieje)
 *
 * Created on 23 feb. 2005
 * @version file v1.4.4
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
#include "org_jouvieje_FmodEx_FmodExJNI.h"
#include "CallbackManager.h"

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1release(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->release();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getSystemObject(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsystem) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::System *system/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getSystemObject(&system);

	if(jsystem) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::System **)&newAddress = system;
		setPointerAddress(java_env, jsystem, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1addInput(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jtarget, jobject jconnection) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *target = 0;
	if(jtarget) {
		POINTER_TYPE targetTmp = (POINTER_TYPE)jtarget;
		target = *(FMOD::DSP **)&targetTmp;
	}
	FMOD::DSPConnection *connection/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->addInput(target, &connection);

	if(jconnection) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSPConnection **)&newAddress = connection;
		setPointerAddress(java_env, jconnection, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1disconnectFrom(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jtarget) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *target = 0;
	if(jtarget) {
		POINTER_TYPE targetTmp = (POINTER_TYPE)jtarget;
		target = *(FMOD::DSP **)&targetTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->disconnectFrom(target);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1disconnectAll(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jinputs, jboolean joutputs) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool inputs = (bool)(jinputs != 0);
	bool outputs = (bool)(joutputs != 0);

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->disconnectAll(inputs, outputs);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1remove(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->remove();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getNumInputs(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnuminputs, jlong jnuminputs_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numinputs = 0;
	if(jnuminputs) {
		numinputs = (int *)((char *)java_env->GetDirectBufferAddress(jnuminputs)+jnuminputs_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getNumInputs(numinputs);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getNumOutputs(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumoutputs, jlong jnumoutputs_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numoutputs = 0;
	if(jnumoutputs) {
		numoutputs = (int *)((char *)java_env->GetDirectBufferAddress(jnumoutputs)+jnumoutputs_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getNumOutputs(numoutputs);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getInput(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jinput, jobject jinputconnection) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::DSP *input/* = 0*/;
	FMOD::DSPConnection *inputconnection/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getInput(index, &input, &inputconnection);

	if(jinput) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = input;
		setPointerAddress(java_env, jinput, newAddress);
	}
	if(jinputconnection) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSPConnection **)&newAddress = inputconnection;
		setPointerAddress(java_env, jinputconnection, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getOutput(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject joutput, jobject joutputconnection) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::DSP *output/* = 0*/;
	FMOD::DSPConnection *outputconnection/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getOutput(index, &output, &outputconnection);

	if(joutput) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = output;
		setPointerAddress(java_env, joutput, newAddress);
	}
	if(joutputconnection) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSPConnection **)&newAddress = outputconnection;
		setPointerAddress(java_env, joutputconnection, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1setActive(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jactive) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool active = (bool)(jactive != 0);

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->setActive(active);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getActive(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jactive, jlong jactive_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *active = 0;
	if(jactive) {
		active = (bool *)((char *)java_env->GetDirectBufferAddress(jactive)+jactive_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getActive(active);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1setBypass(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jbypass) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool bypass = (bool)(jbypass != 0);

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->setBypass(bypass);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getBypass(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jbypass, jlong jbypass_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *bypass = 0;
	if(jbypass) {
		bypass = (bool *)((char *)java_env->GetDirectBufferAddress(jbypass)+jbypass_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getBypass(bypass);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1setSpeakerActive(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeaker, jboolean jactive) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKER speaker = (FMOD_SPEAKER)jspeaker;
	bool active = (bool)(jactive != 0);

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->setSpeakerActive(speaker, active);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getSpeakerActive(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeaker, jobject jactive, jlong jactive_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKER speaker = (FMOD_SPEAKER)jspeaker;
	bool *active = 0;
	if(jactive) {
		active = (bool *)((char *)java_env->GetDirectBufferAddress(jactive)+jactive_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getSpeakerActive(speaker, active);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1reset(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->reset();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1setParameter(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jfloat jvalue) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	float value = (float)jvalue;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->setParameter(index, value);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getParameter(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jvalue, jlong jvalue_, 	jobject jvaluestr, jlong jvaluestr_, jint jvaluestrlen) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	float *value = 0;
	if(jvalue) {
		value = (float *)((char *)java_env->GetDirectBufferAddress(jvalue)+jvalue_);
	}
	char *valuestr = 0;
	if(jvaluestr) {
		valuestr = (char *)java_env->GetDirectBufferAddress(jvaluestr)+jvaluestr_;
	}
	int valuestrlen = (int)jvaluestrlen;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getParameter(index, value, valuestr, valuestrlen);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getNumParameters(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumparams, jlong jnumparams_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numparams = 0;
	if(jnumparams) {
		numparams = (int *)((char *)java_env->GetDirectBufferAddress(jnumparams)+jnumparams_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getNumParameters(numparams);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getParameterInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, 	jobject jname, jlong jname_, 	jobject jlabel, jlong jlabel_, 	jobject jdescription, jlong jdescription_, jint jdescriptionlen, jobject jmin, jlong jmin_, jobject jmax, jlong jmax_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	char *label = 0;
	if(jlabel) {
		label = (char *)java_env->GetDirectBufferAddress(jlabel)+jlabel_;
	}
	char *description = 0;
	if(jdescription) {
		description = (char *)java_env->GetDirectBufferAddress(jdescription)+jdescription_;
	}
	int descriptionlen = (int)jdescriptionlen;
	float *min = 0;
	if(jmin) {
		min = (float *)((char *)java_env->GetDirectBufferAddress(jmin)+jmin_);
	}
	float *max = 0;
	if(jmax) {
		max = (float *)((char *)java_env->GetDirectBufferAddress(jmax)+jmax_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getParameterInfo(index, name, label, description, descriptionlen, min, max);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1showConfigDialog(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jhwnd, jlong hwndHwnd, jboolean jshow) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	jlong handle/* = 0*/;
	if(jhwnd) {
		handle = Java_org_jouvieje_FmodEx_FmodExJNI_getHwnd(java_env, jcls, jhwnd);
	}
	bool show = (bool)(jshow != 0);
	
#if (CURRENT_PLATFORM == NATIVE2JAVA_WIN_32) || (CURRENT_PLATFORM == NATIVE2JAVA_WIN_64)
#pragma message("Windows platform detected !")
	ConfigDialogThreadParams *params = new ConfigDialogThreadParams();
	params->pointer = pointer;
	params->handle = handle;
	params->hwndHwnd = hwndHwnd;
	params->show = show;
	params->isShown = false;
	
	_beginthread(configDialogThread, 0, (void *)params);
	
	while(params->isShown == false) {
		Sleep(1);
	}
	return (jint)(params->result);
#else
#pragma message("Linux/Mac platform detected !")
	FMOD_RESULT result_;
	if(handle) {
		result_ = (*(FMOD::DSP **)&pointer)->showConfigDialog(*(void **)&handle, show);
	}
	else {
		result_ = (*(FMOD::DSP **)&pointer)->showConfigDialog(*(void **)&hwndHwnd, show);
	}
	return (jint)result_;
#endif
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, 	jobject jname, jlong jname_, jobject jversion, jlong jversion_, jobject jchannels, jlong jchannels_, jobject jconfigwidth, jlong jconfigwidth_, jobject jconfigheight, jlong jconfigheight_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	unsigned int *version = 0;
	if(jversion) {
		version = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jversion)+jversion_);
	}
	int *channels = 0;
	if(jchannels) {
		channels = (int *)((char *)java_env->GetDirectBufferAddress(jchannels)+jchannels_);
	}
	int *configwidth = 0;
	if(jconfigwidth) {
		configwidth = (int *)((char *)java_env->GetDirectBufferAddress(jconfigwidth)+jconfigwidth_);
	}
	int *configheight = 0;
	if(jconfigheight) {
		configheight = (int *)((char *)java_env->GetDirectBufferAddress(jconfigheight)+jconfigheight_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getInfo(name, version, channels, configwidth, configheight);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getType(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jtypePointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_DSP_TYPE type;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getType(&type);

	if(jtypePointer) {
		int *typePointer = (int *)java_env->GetDirectBufferAddress(jtypePointer);
		typePointer[0] = type;
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1setDefaults(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jfrequency, jfloat jvolume, jfloat jpan, jint jpriority) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float frequency = (float)jfrequency;
	float volume = (float)jvolume;
	float pan = (float)jpan;
	int priority = (int)jpriority;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->setDefaults(frequency, volume, pan, priority);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getDefaults(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jfrequency, jlong jfrequency_, jobject jvolume, jlong jvolume_, jobject jpan, jlong jpan_, jobject jpriority, jlong jpriority_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *frequency = 0;
	if(jfrequency) {
		frequency = (float *)((char *)java_env->GetDirectBufferAddress(jfrequency)+jfrequency_);
	}
	float *volume = 0;
	if(jvolume) {
		volume = (float *)((char *)java_env->GetDirectBufferAddress(jvolume)+jvolume_);
	}
	float *pan = 0;
	if(jpan) {
		pan = (float *)((char *)java_env->GetDirectBufferAddress(jpan)+jpan_);
	}
	int *priority = 0;
	if(jpriority) {
		priority = (int *)((char *)java_env->GetDirectBufferAddress(jpriority)+jpriority_);
	}

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getDefaults(frequency, volume, pan, priority);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_DSP_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_DSP);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::DSP **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


