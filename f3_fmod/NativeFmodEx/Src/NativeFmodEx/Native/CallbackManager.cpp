/**
 * 				NativeFmodEx Project
 *
 * Want to use FMOD Ex API (www.fmod.org) in the Java language ? NativeFmodEx is made for you.
 * Copyright © 2005-2008 JÈrÙme JOUVIE (Jouvieje)
 *
 * Created on 23 feb. 2005
 * @version file v1.4.3
 * @author JÈrÙme JOUVIE (Jouvieje)
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
#if (CURRENT_PLATFORM != NATIVE2JAVA_MAC)
#include "malloc.h"
#else
#include <malloc/malloc.h>
#endif
#include "CallbackManager.h"

jclass byteBufferClass = 0;
jclass getByteBufferClass(JNIEnv *java_env) {
	if(!byteBufferClass) {
		byteBufferClass = (jclass)java_env->NewGlobalRef(java_env->FindClass("java/nio/ByteBuffer"));
	}
	return byteBufferClass;
}

jclass caller = 0;
void connectCaller(JNIEnv *java_env) {
	caller = (jclass)java_env->NewGlobalRef(java_env->FindClass("org/jouvieje/FmodEx/Callbacks/CallbackBridge"));
	if(java_env->ExceptionCheck()) {
		java_env->ExceptionClear();
		caller = 0;
		ThrowException(java_env, InitException, "Connection to CallbackBridge fails.");
	}
}

jmethodID callbackId[30];
void connectCallbacks(JNIEnv *java_env) {
	static struct {
		const char *name;
		const char *signature;
	}callbacks[30] = {
		{"FMOD_CODEC_OPENCALLBACK_BRIDGE", "(JIJ)I"},
		{"FMOD_CODEC_CLOSECALLBACK_BRIDGE", "(J)I"},
		{"FMOD_CODEC_READCALLBACK_BRIDGE", "(JLjava/nio/ByteBuffer;ILjava/nio/ByteBuffer;)I"},
		{"FMOD_CODEC_GETLENGTHCALLBACK_BRIDGE", "(JLjava/nio/ByteBuffer;I)I"},
		{"FMOD_CODEC_SETPOSITIONCALLBACK_BRIDGE", "(JIII)I"},
		{"FMOD_CODEC_GETPOSITIONCALLBACK_BRIDGE", "(JLjava/nio/ByteBuffer;I)I"},
		{"FMOD_CODEC_SOUNDCREATECALLBACK_BRIDGE", "(JIJ)I"},
		{"FMOD_CODEC_METADATACALLBACK_BRIDGE", "(JILjava/nio/ByteBuffer;JIII)I"},
		{"FMOD_CODEC_GETWAVEFORMAT_BRIDGE", "(JIJ)I"},
		{"FMOD_DSP_CREATECALLBACK_BRIDGE", "(J)I"},
		{"FMOD_DSP_RELEASECALLBACK_BRIDGE", "(J)I"},
		{"FMOD_DSP_RESETCALLBACK_BRIDGE", "(J)I"},
		{"FMOD_DSP_READCALLBACK_BRIDGE", "(JLjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;III)I"},
		{"FMOD_DSP_SETPOSITIONCALLBACK_BRIDGE", "(JI)I"},
		{"FMOD_DSP_SETPARAMCALLBACK_BRIDGE", "(JIF)I"},
		{"FMOD_DSP_GETPARAMCALLBACK_BRIDGE", "(JILjava/nio/ByteBuffer;Ljava/nio/ByteBuffer;)I"},
		{"FMOD_DSP_DIALOGCALLBACK_BRIDGE", "(JJI)I"},
		{"FMOD_SYSTEM_CALLBACK_BRIDGE", "(JIJJ)I"},
		{"FMOD_CHANNEL_CALLBACK_BRIDGE", "(JIIII)I"},
		{"FMOD_SOUND_NONBLOCKCALLBACK_BRIDGE", "(JI)I"},
		{"FMOD_SOUND_PCMREADCALLBACK_BRIDGE", "(JLjava/nio/ByteBuffer;I)I"},
		{"FMOD_SOUND_PCMSETPOSCALLBACK_BRIDGE", "(JIII)I"},
		{"FMOD_FILE_OPENCALLBACK_BRIDGE", "(Ljava/lang/String;ILjava/nio/ByteBuffer;Lorg/jouvieje/FmodEx/Misc/Pointer;Lorg/jouvieje/FmodEx/Misc/Pointer;)I"},
		{"FMOD_FILE_CLOSECALLBACK_BRIDGE", "(JJ)I"},
		{"FMOD_FILE_READCALLBACK_BRIDGE", "(JLjava/nio/ByteBuffer;ILjava/nio/ByteBuffer;J)I"},
		{"FMOD_FILE_SEEKCALLBACK_BRIDGE", "(JIJ)I"},
		{"FMOD_MEMORY_ALLOCCALLBACK_BRIDGE", "(II)Ljava/nio/ByteBuffer;"},
		{"FMOD_MEMORY_REALLOCCALLBACK_BRIDGE", "(Ljava/nio/ByteBuffer;II)Ljava/nio/ByteBuffer;"},
		{"FMOD_MEMORY_FREECALLBACK_BRIDGE", "(Ljava/nio/ByteBuffer;I)V"},
		{"FMOD_3D_ROLLOFFCALLBACK_BRIDGE", "(JF)F"}
	};

	for(int i = 0; i < 30; i++) {
		callbackId[i] = java_env->GetStaticMethodID(caller, callbacks[i].name, callbacks[i].signature);
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionClear();
			ThrowException(java_env, InitException, "Connection to a Callback fails.");
			return;
		}
	}
}

JavaVM *jvm;
void attachJavaVM(JNIEnv *java_env) {
	java_env->GetJavaVM(&jvm);
	connectCaller(java_env);
	if(caller) {
		connectCallbacks(java_env);
	}
}
bool acquire_jenv(JNIEnv **java_env) {
	if(jvm->GetEnv((void **)java_env, JNI_VERSION_1_4) != JNI_OK) {
		jvm->AttachCurrentThread((void **)java_env, 0);
		return true;
	}
	return false;
}
void leave_jenv(bool attached) {
	if(attached) {
		jvm->DetachCurrentThread();
	}
}

FMOD_RESULT F_CALLBACK FMOD_CODEC_OPENCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, FMOD_MODE usermode, FMOD_CREATESOUNDEXINFO * userexinfo) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jcodec_state/* = 0*/;
	*(FMOD_CODEC_STATE **)&jcodec_state = (FMOD_CODEC_STATE *)codec_state;
	POINTER_TYPE juserexinfo/* = 0*/;
	*(FMOD_CREATESOUNDEXINFO **)&juserexinfo = (FMOD_CREATESOUNDEXINFO *)userexinfo;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[0], (jlong)jcodec_state, (jint)usermode, (jlong)juserexinfo);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_CODEC_CLOSECALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jcodec_state/* = 0*/;
	*(FMOD_CODEC_STATE **)&jcodec_state = (FMOD_CODEC_STATE *)codec_state;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[1], (jlong)jcodec_state);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_CODEC_READCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, void * buffer, unsigned int sizebytes, unsigned int * bytesread) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jcodec_state/* = 0*/;
	*(FMOD_CODEC_STATE **)&jcodec_state = (FMOD_CODEC_STATE *)codec_state;
	jobject jbuffer = 0;
	if(buffer) {
		jbuffer = java_env->NewDirectByteBuffer((void *)buffer, sizebytes);
	}
	jobject jbytesread = 0;
	if(bytesread) {
		jbytesread = java_env->NewDirectByteBuffer((int *)bytesread, 4);
	}
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[2], (jlong)jcodec_state, jbuffer, (jint)sizebytes, jbytesread);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_CODEC_GETLENGTHCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, unsigned int * length, FMOD_TIMEUNIT lengthtype) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jcodec_state/* = 0*/;
	*(FMOD_CODEC_STATE **)&jcodec_state = (FMOD_CODEC_STATE *)codec_state;
	jobject jlength = 0;
	if(length) {
		jlength = java_env->NewDirectByteBuffer((int *)length, 4);
	}
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[3], (jlong)jcodec_state, jlength, (jint)lengthtype);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_CODEC_SETPOSITIONCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, int subsound, unsigned int position, FMOD_TIMEUNIT postype) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jcodec_state/* = 0*/;
	*(FMOD_CODEC_STATE **)&jcodec_state = (FMOD_CODEC_STATE *)codec_state;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[4], (jlong)jcodec_state, (jint)subsound, (jint)position, (jint)postype);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_CODEC_GETPOSITIONCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, unsigned int * position, FMOD_TIMEUNIT postype) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jcodec_state/* = 0*/;
	*(FMOD_CODEC_STATE **)&jcodec_state = (FMOD_CODEC_STATE *)codec_state;
	jobject jposition = 0;
	if(position) {
		jposition = java_env->NewDirectByteBuffer((int *)position, 4);
	}
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[5], (jlong)jcodec_state, jposition, (jint)postype);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_CODEC_SOUNDCREATECALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, int subsound, FMOD_SOUND * sound) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jcodec_state/* = 0*/;
	*(FMOD_CODEC_STATE **)&jcodec_state = (FMOD_CODEC_STATE *)codec_state;
	POINTER_TYPE jsound/* = 0*/;
	*(FMOD_SOUND **)&jsound = (FMOD_SOUND *)sound;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[6], (jlong)jcodec_state, (jint)subsound, (jlong)jsound);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_CODEC_METADATACALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, FMOD_TAGTYPE tagtype, char * name, void * data, unsigned int datalen, FMOD_TAGDATATYPE datatype, int unique) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jcodec_state/* = 0*/;
	*(FMOD_CODEC_STATE **)&jcodec_state = (FMOD_CODEC_STATE *)codec_state;
	jobject jname = 0;
	if(name) {
		jname = java_env->NewDirectByteBuffer((char *)name, strlen((const char *)name));
	}
	POINTER_TYPE jdata/* = 0*/;
	*(void **)&jdata = data;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[7], (jlong)jcodec_state, (jint)tagtype, jname, (jlong)jdata, (jint)datalen, (jint)datatype, (jint)unique);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_CODEC_GETWAVEFORMAT_BRIDGE(FMOD_CODEC_STATE * codec_state, int index, FMOD_CODEC_WAVEFORMAT * waveformat) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jcodec_state/* = 0*/;
	*(FMOD_CODEC_STATE **)&jcodec_state = (FMOD_CODEC_STATE *)codec_state;
	POINTER_TYPE jwaveformat/* = 0*/;
	*(FMOD_CODEC_WAVEFORMAT **)&jwaveformat = (FMOD_CODEC_WAVEFORMAT *)waveformat;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[8], (jlong)jcodec_state, (jint)index, (jlong)jwaveformat);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_DSP_CREATECALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jdsp_state/* = 0*/;
	*(FMOD_DSP_STATE **)&jdsp_state = (FMOD_DSP_STATE *)dsp_state;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[9], (jlong)jdsp_state);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_DSP_RELEASECALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jdsp_state/* = 0*/;
	*(FMOD_DSP_STATE **)&jdsp_state = (FMOD_DSP_STATE *)dsp_state;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[10], (jlong)jdsp_state);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_DSP_RESETCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jdsp_state/* = 0*/;
	*(FMOD_DSP_STATE **)&jdsp_state = (FMOD_DSP_STATE *)dsp_state;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[11], (jlong)jdsp_state);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_DSP_READCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, float * inbuffer, float * outbuffer, unsigned int length, int inchannels, int outchannels) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jdsp_state/* = 0*/;
	*(FMOD_DSP_STATE **)&jdsp_state = (FMOD_DSP_STATE *)dsp_state;
	jobject jinbuffer = 0;
	if(inbuffer) {
		jinbuffer = java_env->NewDirectByteBuffer((float *)inbuffer, length*inchannels*4);
	}
	jobject joutbuffer = 0;
	if(outbuffer) {
		joutbuffer = java_env->NewDirectByteBuffer((float *)outbuffer, length*outchannels*4);
	}
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[12], (jlong)jdsp_state, jinbuffer, joutbuffer, (jint)length, (jint)inchannels, (jint)outchannels);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_DSP_SETPOSITIONCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, unsigned int pos) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jdsp_state/* = 0*/;
	*(FMOD_DSP_STATE **)&jdsp_state = (FMOD_DSP_STATE *)dsp_state;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[13], (jlong)jdsp_state, (jint)pos);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_DSP_SETPARAMCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, int index, float value) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jdsp_state/* = 0*/;
	*(FMOD_DSP_STATE **)&jdsp_state = (FMOD_DSP_STATE *)dsp_state;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[14], (jlong)jdsp_state, (jint)index, (jfloat)value);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_DSP_GETPARAMCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, int index, float * value, char * valuestr) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jdsp_state/* = 0*/;
	*(FMOD_DSP_STATE **)&jdsp_state = (FMOD_DSP_STATE *)dsp_state;
	jobject jvalue = 0;
	if(value) {
		jvalue = java_env->NewDirectByteBuffer((float *)value, 4);
	}
	jobject jvaluestr = 0;
	if(valuestr) {
		jvaluestr = java_env->NewDirectByteBuffer((char *)valuestr, 16);
	}
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[15], (jlong)jdsp_state, (jint)index, jvalue, jvaluestr);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_DSP_DIALOGCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, void * hwnd, int show) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jdsp_state/* = 0*/;
	*(FMOD_DSP_STATE **)&jdsp_state = (FMOD_DSP_STATE *)dsp_state;
	POINTER_TYPE jhwnd/* = 0*/;
	*(void **)&jhwnd = hwnd;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[16], (jlong)jdsp_state, (jlong)jhwnd, (jint)show);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_SYSTEM_CALLBACK_BRIDGE(FMOD_SYSTEM * system, FMOD_SYSTEM_CALLBACKTYPE type, void * commanddata1, void * commanddata2) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jsystem/* = 0*/;
	*(FMOD_SYSTEM **)&jsystem = (FMOD_SYSTEM *)system;
	POINTER_TYPE jcommanddata1/* = 0*/;
	*(void **)&jcommanddata1 = commanddata1;
	POINTER_TYPE jcommanddata2/* = 0*/;
	*(void **)&jcommanddata2 = commanddata2;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[17], (jlong)jsystem, (jint)type, (jlong)jcommanddata1, (jlong)jcommanddata2);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_CHANNEL_CALLBACK_BRIDGE(FMOD_CHANNEL * channel, FMOD_CHANNEL_CALLBACKTYPE type, void * commanddata1, void * commanddata2) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jchannel/* = 0*/;
	*(FMOD_CHANNEL **)&jchannel = (FMOD_CHANNEL *)channel;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[18], (jlong)jchannel, (jint)type, (jint)0, (jint)(POINTER_TYPE)commanddata1, (jint)(POINTER_TYPE)commanddata2);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_SOUND_NONBLOCKCALLBACK_BRIDGE(FMOD_SOUND * sound, FMOD_RESULT result) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jsound/* = 0*/;
	*(FMOD_SOUND **)&jsound = (FMOD_SOUND *)sound;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[19], (jlong)jsound, (jint)result);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_SOUND_PCMREADCALLBACK_BRIDGE(FMOD_SOUND * sound, void * data, unsigned int datalen) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jsound/* = 0*/;
	*(FMOD_SOUND **)&jsound = (FMOD_SOUND *)sound;
	jobject jdata = 0;
	if(data) {
		jdata = java_env->NewDirectByteBuffer((void *)data, datalen);
	}
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[20], (jlong)jsound, jdata, (jint)datalen);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_SOUND_PCMSETPOSCALLBACK_BRIDGE(FMOD_SOUND * sound, int subsound, unsigned int position, FMOD_TIMEUNIT postype) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jsound/* = 0*/;
	*(FMOD_SOUND **)&jsound = (FMOD_SOUND *)sound;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[21], (jlong)jsound, (jint)subsound, (jint)position, (jint)postype);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_FILE_OPENCALLBACK_BRIDGE(const char * name, int unicode, unsigned int * filesize, void ** handle, void ** userdata) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	jstring jname = 0;
	if(name) {
		jname = java_env->NewStringUTF(name);
	}
	jobject jfilesize = 0;
	if(filesize) {
		jfilesize = java_env->NewDirectByteBuffer((int *)filesize, 4);
	}
	jobject jhandle = newPointer(java_env);
	jobject juserdata = newPointer(java_env);
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[22], jname, (jint)unicode, jfilesize, jhandle, juserdata);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	if(jhandle) {
		POINTER_TYPE jhandleAddress = getPointerAddress(java_env, jhandle);
		if(jhandleAddress) {
			*handle = *(void **)&jhandleAddress;
		}
	}
	if(juserdata) {
		POINTER_TYPE juserdataAddress = getPointerAddress(java_env, juserdata);
		if(juserdataAddress) {
			*userdata = *(void **)&juserdataAddress;
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_FILE_CLOSECALLBACK_BRIDGE(void * handle, void * userdata) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jhandle/* = 0*/;
	*(void **)&jhandle = handle;
	POINTER_TYPE juserdata/* = 0*/;
	*(void **)&juserdata = userdata;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[23], (jlong)jhandle, (jlong)juserdata);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_FILE_READCALLBACK_BRIDGE(void * handle, void * buffer, unsigned int sizebytes, unsigned int * bytesread, void * userdata) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jhandle/* = 0*/;
	*(void **)&jhandle = handle;
	jobject jbuffer = 0;
	if(buffer) {
		jbuffer = java_env->NewDirectByteBuffer((void *)buffer, sizebytes);
	}
	jobject jbytesread = 0;
	if(bytesread) {
		jbytesread = java_env->NewDirectByteBuffer((int *)bytesread, 4);
	}
	POINTER_TYPE juserdata/* = 0*/;
	*(void **)&juserdata = userdata;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[24], (jlong)jhandle, jbuffer, (jint)sizebytes, jbytesread, (jlong)juserdata);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

FMOD_RESULT F_CALLBACK FMOD_FILE_SEEKCALLBACK_BRIDGE(void * handle, unsigned int pos, void * userdata) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jhandle/* = 0*/;
	*(void **)&jhandle = handle;
	POINTER_TYPE juserdata/* = 0*/;
	*(void **)&juserdata = userdata;
	jint result_ = java_env->CallStaticIntMethod(caller, callbackId[25], (jlong)jhandle, (jint)pos, (jlong)juserdata);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (FMOD_RESULT)result_;
}

void * F_CALLBACK FMOD_MEMORY_ALLOCCALLBACK_BRIDGE(unsigned int size, FMOD_MEMORY_TYPE type) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	jobject result_ = java_env->CallStaticObjectMethod(caller, callbackId[26], (jint)size, (jint)type);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	if(result_) {
		jobject globalRef = java_env->NewGlobalRef(result_);
		if(globalRef) {
			return java_env->GetDirectBufferAddress(globalRef);
		}
		else {
			ThrowException(java_env, OutOfMemoryError, "");
		}
	}
	leave_jenv(attached);
	return 0;
}

void * F_CALLBACK FMOD_MEMORY_REALLOCCALLBACK_BRIDGE(void * ptr, unsigned int size, FMOD_MEMORY_TYPE type) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	jobject jptr = 0;
	if(ptr) {
#if (CURRENT_PLATFORM == NATIVE2JAVA_WIN_32) || (CURRENT_PLATFORM == NATIVE2JAVA_WIN_64)
		jptr = java_env->NewDirectByteBuffer((void *)ptr, _msize(ptr));
#elif (CURRENT_PLATFORM == NATIVE2JAVA_LINUX) || (CURRENT_PLATFORM == NATIVE2JAVA_LINUX_64)
		jptr = java_env->NewDirectByteBuffer((void *)ptr, malloc_usable_size(ptr));
#elif (CURRENT_PLATFORM == NATIVE2JAVA_MAC) || (CURRENT_PLATFORM == NATIVE2JAVA_MAC)
		jptr = java_env->NewDirectByteBuffer((void *)ptr, malloc_size(ptr));
#else 
        #error no platform specified for malloc_size
#endif
	}
	jobject result_ = java_env->CallStaticObjectMethod(caller, callbackId[27], jptr, (jint)size, (jint)type);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	if(result_) {
		jobject globalRef = java_env->NewGlobalRef(result_);
		if(globalRef) {
			return java_env->GetDirectBufferAddress(globalRef);
		}
		else {
			ThrowException(java_env, OutOfMemoryError, "");
		}
	}
	leave_jenv(attached);
	return 0;
}

void F_CALLBACK FMOD_MEMORY_FREECALLBACK_BRIDGE(void * ptr, FMOD_MEMORY_TYPE type) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	jobject jptr = 0;
	if(ptr) {
#if (CURRENT_PLATFORM == NATIVE2JAVA_WIN_32) || (CURRENT_PLATFORM == NATIVE2JAVA_WIN_64)
		jptr = java_env->NewDirectByteBuffer((void *)ptr, _msize(ptr));
#elif (CURRENT_PLATFORM == NATIVE2JAVA_LINUX) || (CURRENT_PLATFORM == NATIVE2JAVA_LINUX_64)
		jptr = java_env->NewDirectByteBuffer((void *)ptr, malloc_usable_size(ptr));
#elif (CURRENT_PLATFORM == NATIVE2JAVA_MAC) || (CURRENT_PLATFORM == NATIVE2JAVA_MAC)
		jptr = java_env->NewDirectByteBuffer((void *)ptr, malloc_size(ptr));
#else 
        #error no platform specified for malloc_size
#endif
	}
	java_env->CallStaticVoidMethod(caller, callbackId[28], jptr, (jint)type);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
}

float F_CALLBACK FMOD_3D_ROLLOFFCALLBACK_BRIDGE(FMOD_CHANNEL * channel, float distance) {
	JNIEnv *java_env/* = 0*/;
	bool attached = acquire_jenv(&java_env);
	POINTER_TYPE jchannel/* = 0*/;
	*(FMOD_CHANNEL **)&jchannel = (FMOD_CHANNEL *)channel;
	jfloat result_ = java_env->CallStaticFloatMethod(caller, callbackId[29], (jlong)jchannel, (jfloat)distance);
	if(java_env->ExceptionCheck()) {
		java_env->Throw(java_env->ExceptionOccurred());
		if(java_env->ExceptionCheck()) {
			java_env->ExceptionDescribe();
			java_env->FatalError(FATAL_ERROR_MESSAGE);
		}
	}
	leave_jenv(attached);
	return (float)result_;
}


