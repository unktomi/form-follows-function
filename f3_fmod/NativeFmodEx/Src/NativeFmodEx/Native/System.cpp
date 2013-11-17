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
#include "org_jouvieje_FmodEx_FmodExJNI.h"
#include "CallbackManager.h"

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1release(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->release();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setOutput(JNIEnv *java_env, jclass jcls, jlong jpointer, jint joutput) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_OUTPUTTYPE output = (FMOD_OUTPUTTYPE)joutput;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setOutput(output);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getOutput(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject joutputPointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_OUTPUTTYPE output;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getOutput(&output);

	if(joutputPointer) {
		int *outputPointer = (int *)java_env->GetDirectBufferAddress(joutputPointer);
		outputPointer[0] = output;
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getNumDrivers(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumdrivers, jlong jnumdrivers_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numdrivers = 0;
	if(jnumdrivers) {
		numdrivers = (int *)((char *)java_env->GetDirectBufferAddress(jnumdrivers)+jnumdrivers_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getNumDrivers(numdrivers);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getDriverInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jid, 	jobject jname, jlong jname_, jint jnamelen, jlong jguid) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int id = (int)jid;
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	int namelen = (int)jnamelen;
	FMOD_GUID *guid = 0;
	if(jguid) {
		POINTER_TYPE guidTmp = (POINTER_TYPE)jguid;
		guid = *(FMOD_GUID **)&guidTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getDriverInfo(id, name, namelen, guid);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getDriverCaps(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jid, jobject jcaps, jlong jcaps_, jobject jminfrequency, jlong jminfrequency_, jobject jmaxfrequency, jlong jmaxfrequency_, jobject jcontrolpanelspeakermodePointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int id = (int)jid;
	FMOD_CAPS *caps = 0;
	if(jcaps) {
		caps = (FMOD_CAPS *)(int *)((char *)java_env->GetDirectBufferAddress(jcaps)+jcaps_);
	}
	int *minfrequency = 0;
	if(jminfrequency) {
		minfrequency = (int *)((char *)java_env->GetDirectBufferAddress(jminfrequency)+jminfrequency_);
	}
	int *maxfrequency = 0;
	if(jmaxfrequency) {
		maxfrequency = (int *)((char *)java_env->GetDirectBufferAddress(jmaxfrequency)+jmaxfrequency_);
	}
	FMOD_SPEAKERMODE controlpanelspeakermode;

	FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->getDriverCaps(id, caps, minfrequency, maxfrequency, &controlpanelspeakermode);

	if(jcontrolpanelspeakermodePointer) {
		int *controlpanelspeakermodePointer = (int *)java_env->GetDirectBufferAddress(jcontrolpanelspeakermodePointer);
		controlpanelspeakermodePointer[0] = controlpanelspeakermode;
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setDriver(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jdriver) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int driver = (int)jdriver;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setDriver(driver);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getDriver(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdriver, jlong jdriver_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *driver = 0;
	if(jdriver) {
		driver = (int *)((char *)java_env->GetDirectBufferAddress(jdriver)+jdriver_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getDriver(driver);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setHardwareChannels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmin2d, jint jmax2d, jint jmin3d, jint jmax3d) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int min2d = (int)jmin2d;
	int max2d = (int)jmax2d;
	int min3d = (int)jmin3d;
	int max3d = (int)jmax3d;
        FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->setHardwareChannels(min2d, max2d, min3d, max3d);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setSoftwareChannels(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jnumsoftwarechannels) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int numsoftwarechannels = (int)jnumsoftwarechannels;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setSoftwareChannels(numsoftwarechannels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getSoftwareChannels(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumsoftwarechannels, jlong jnumsoftwarechannels_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numsoftwarechannels = 0;
	if(jnumsoftwarechannels) {
		numsoftwarechannels = (int *)((char *)java_env->GetDirectBufferAddress(jnumsoftwarechannels)+jnumsoftwarechannels_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getSoftwareChannels(numsoftwarechannels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setSoftwareFormat(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jsamplerate, jint jformat, jint jnumoutputchannels, jint jmaxinputchannels, jint jresamplemethod) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int samplerate = (int)jsamplerate;
	FMOD_SOUND_FORMAT format = (FMOD_SOUND_FORMAT)jformat;
	int numoutputchannels = (int)jnumoutputchannels;
	int maxinputchannels = (int)jmaxinputchannels;
	FMOD_DSP_RESAMPLER resamplemethod = (FMOD_DSP_RESAMPLER)jresamplemethod;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setSoftwareFormat(samplerate, format, numoutputchannels, maxinputchannels, resamplemethod);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getSoftwareFormat(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsamplerate, jlong jsamplerate_, jobject jformatPointer, jobject jnumoutputchannels, jlong jnumoutputchannels_, jobject jmaxinputchannels, jlong jmaxinputchannels_, jobject jresamplemethodPointer, jobject jbits, jlong jbits_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *samplerate = 0;
	if(jsamplerate) {
		samplerate = (int *)((char *)java_env->GetDirectBufferAddress(jsamplerate)+jsamplerate_);
	}
	FMOD_SOUND_FORMAT format;
	int *numoutputchannels = 0;
	if(jnumoutputchannels) {
		numoutputchannels = (int *)((char *)java_env->GetDirectBufferAddress(jnumoutputchannels)+jnumoutputchannels_);
	}
	int *maxinputchannels = 0;
	if(jmaxinputchannels) {
		maxinputchannels = (int *)((char *)java_env->GetDirectBufferAddress(jmaxinputchannels)+jmaxinputchannels_);
	}
	FMOD_DSP_RESAMPLER resamplemethod;
	int *bits = 0;
	if(jbits) {
		bits = (int *)((char *)java_env->GetDirectBufferAddress(jbits)+jbits_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getSoftwareFormat(samplerate, &format, numoutputchannels, maxinputchannels, &resamplemethod, bits);

	if(jformatPointer) {
		int *formatPointer = (int *)java_env->GetDirectBufferAddress(jformatPointer);
		formatPointer[0] = format;
	}
	if(jresamplemethodPointer) {
		int *resamplemethodPointer = (int *)java_env->GetDirectBufferAddress(jresamplemethodPointer);
		resamplemethodPointer[0] = resamplemethod;
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setDSPBufferSize(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jbufferlength, jint jnumbuffers) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int bufferlength = (int)jbufferlength;
	int numbuffers = (int)jnumbuffers;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setDSPBufferSize(bufferlength, numbuffers);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getDSPBufferSize(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jbufferlength, jlong jbufferlength_, jobject jnumbuffers, jlong jnumbuffers_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *bufferlength = 0;
	if(jbufferlength) {
		bufferlength = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jbufferlength)+jbufferlength_);
	}
	int *numbuffers = 0;
	if(jnumbuffers) {
		numbuffers = (int *)((char *)java_env->GetDirectBufferAddress(jnumbuffers)+jnumbuffers_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getDSPBufferSize(bufferlength, numbuffers);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setFileSystem(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean juseropen, jboolean juserclose, jboolean juserread, jboolean juserseek, jint jblockalign) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int blockalign = (int)jblockalign;

        FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->setFileSystem(juseropen == 0 ? NULL : FMOD_FILE_OPENCALLBACK_BRIDGE, juserclose == 0 ? NULL : FMOD_FILE_CLOSECALLBACK_BRIDGE, juserread == 0 ? NULL : FMOD_FILE_READCALLBACK_BRIDGE, juserseek == 0 ? NULL : FMOD_FILE_SEEKCALLBACK_BRIDGE, blockalign);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1attachFileSystem(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean juseropen, jboolean juserclose, jboolean juserread, jboolean juserseek) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->attachFileSystem(juseropen == 0 ? NULL : FMOD_FILE_OPENCALLBACK_BRIDGE, juserclose == 0 ? NULL : FMOD_FILE_CLOSECALLBACK_BRIDGE, juserread == 0 ? NULL : FMOD_FILE_READCALLBACK_BRIDGE, juserseek == 0 ? NULL : FMOD_FILE_SEEKCALLBACK_BRIDGE);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setAdvancedSettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jsettings) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_ADVANCEDSETTINGS *settings = 0;
	if(jsettings) {
		POINTER_TYPE settingsTmp = (POINTER_TYPE)jsettings;
		settings = *(FMOD_ADVANCEDSETTINGS **)&settingsTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setAdvancedSettings(settings);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getAdvancedSettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jsettings) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_ADVANCEDSETTINGS *settings = 0;
	if(jsettings) {
		POINTER_TYPE settingsTmp = (POINTER_TYPE)jsettings;
		settings = *(FMOD_ADVANCEDSETTINGS **)&settingsTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getAdvancedSettings(settings);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setSpeakerMode(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeakermode) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKERMODE speakermode = (FMOD_SPEAKERMODE)jspeakermode;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setSpeakerMode(speakermode);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getSpeakerMode(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jspeakermodePointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKERMODE speakermode;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getSpeakerMode(&speakermode);

	if(jspeakermodePointer) {
		int *speakermodePointer = (int *)java_env->GetDirectBufferAddress(jspeakermodePointer);
		speakermodePointer[0] = speakermode;
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setCallback(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jcallback) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setCallback(jcallback == 0 ? NULL : FMOD_SYSTEM_CALLBACK_BRIDGE);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setPluginPath(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jpath) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *path = getByteArrayElements(java_env, jpath);

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setPluginPath(path);

	releaseByteArrayElements(java_env, jpath, (const char *)path);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1loadPlugin(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jfilename, jobject jplugintypePointer, jobject jindex, jlong jindex_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *filename = getByteArrayElements(java_env, jfilename);
	FMOD_PLUGINTYPE plugintype;
	int *index = 0;
	if(jindex) {
		index = (int *)((char *)java_env->GetDirectBufferAddress(jindex)+jindex_);
	}

    unsigned int handle = 0;
    FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getPluginHandle(FMOD_PLUGINTYPE_CODEC, *index, &handle);
    result_ = (*(FMOD::System **)&pointer)->loadPlugin(filename, &handle, 0);
    
    unsigned int version = 0;
    result_ = (*(FMOD::System **)&pointer)->getPluginInfo(handle, &plugintype, NULL, 0, &version);
    
	releaseByteArrayElements(java_env, jfilename, (const char *)filename);
	if(jplugintypePointer) {
		int *plugintypePointer = (int *)java_env->GetDirectBufferAddress(jplugintypePointer);
		plugintypePointer[0] = plugintype;
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getNumPlugins(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jplugintype, jobject jnumplugins, jlong jnumplugins_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_PLUGINTYPE plugintype = (FMOD_PLUGINTYPE)jplugintype;
	int *numplugins = 0;
	if(jnumplugins) {
		numplugins = (int *)((char *)java_env->GetDirectBufferAddress(jnumplugins)+jnumplugins_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getNumPlugins(plugintype, numplugins);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getPluginInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jplugintype, jint jindex, 	jobject jname, jlong jname_, jint jnamelen, jobject jversion, jlong jversion_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_PLUGINTYPE plugintype = (FMOD_PLUGINTYPE)jplugintype;
	int index = (int)jindex;
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	int namelen = (int)jnamelen;
	unsigned int *version = 0;
	if(jversion) {
		version = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jversion)+jversion_);
	}

    unsigned int handle = 0;
    FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getPluginHandle(FMOD_PLUGINTYPE_CODEC, index, &handle);
    result_ = (*(FMOD::System **)&pointer)->getPluginInfo(handle, &plugintype, name, namelen, version);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1unloadPlugin(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jplugintype, jint jindex) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	//FMOD_PLUGINTYPE plugintype = (FMOD_PLUGINTYPE)jplugintype;
	int index = (int)jindex;

    unsigned int handle = 0;
    FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getPluginHandle(FMOD_PLUGINTYPE_CODEC, index, &handle);
    result_ = (*(FMOD::System **)&pointer)->unloadPlugin(handle);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setOutputByPlugin(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setOutputByPlugin(index);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getOutputByPlugin(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jindex, jlong jindex_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *index = 0;
	if(jindex) {
		index = (int *)((char *)java_env->GetDirectBufferAddress(jindex)+jindex_);
	}

    unsigned int handle = 0;
    FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getPluginHandle(FMOD_PLUGINTYPE_CODEC, *index, &handle);
    result_ = (*(FMOD::System **)&pointer)->getOutputByPlugin(&handle);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createCodec(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jdescription) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_CODEC_DESCRIPTION *description = 0;
	if(jdescription) {
		POINTER_TYPE descriptionTmp = (POINTER_TYPE)jdescription;
		description = *(FMOD_CODEC_DESCRIPTION **)&descriptionTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createCodec(description);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1init(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmaxchannels, jint jflags, jlong jextradriverdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int maxchannels = (int)jmaxchannels;
	FMOD_INITFLAGS flags = (FMOD_INITFLAGS)jflags;
	POINTER_TYPE extradriverdataTmp = (POINTER_TYPE)jextradriverdata;
	void *extradriverdata = *(void **)&extradriverdataTmp;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->init(maxchannels, flags, extradriverdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1close(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->close();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1update(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->update();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1set3DSettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jdopplerscale, jfloat jdistancefactor, jfloat jrolloffscale) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float dopplerscale = (float)jdopplerscale;
	float distancefactor = (float)jdistancefactor;
	float rolloffscale = (float)jrolloffscale;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->set3DSettings(dopplerscale, distancefactor, rolloffscale);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1get3DSettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdopplerscale, jlong jdopplerscale_, jobject jdistancefactor, jlong jdistancefactor_, jobject jrolloffscale, jlong jrolloffscale_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *dopplerscale = 0;
	if(jdopplerscale) {
		dopplerscale = (float *)((char *)java_env->GetDirectBufferAddress(jdopplerscale)+jdopplerscale_);
	}
	float *distancefactor = 0;
	if(jdistancefactor) {
		distancefactor = (float *)((char *)java_env->GetDirectBufferAddress(jdistancefactor)+jdistancefactor_);
	}
	float *rolloffscale = 0;
	if(jrolloffscale) {
		rolloffscale = (float *)((char *)java_env->GetDirectBufferAddress(jrolloffscale)+jrolloffscale_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->get3DSettings(dopplerscale, distancefactor, rolloffscale);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1set3DNumListeners(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jnumlisteners) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int numlisteners = (int)jnumlisteners;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->set3DNumListeners(numlisteners);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1get3DNumListeners(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumlisteners, jlong jnumlisteners_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numlisteners = 0;
	if(jnumlisteners) {
		numlisteners = (int *)((char *)java_env->GetDirectBufferAddress(jnumlisteners)+jnumlisteners_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->get3DNumListeners(numlisteners);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1set3DListenerAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jlistener, jlong jpos, jlong jvel, jlong jforward, jlong jup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int listener = (int)jlistener;
	FMOD_VECTOR *pos = 0;
	if(jpos) {
		POINTER_TYPE posTmp = (POINTER_TYPE)jpos;
		pos = *(FMOD_VECTOR **)&posTmp;
	}
	FMOD_VECTOR *vel = 0;
	if(jvel) {
		POINTER_TYPE velTmp = (POINTER_TYPE)jvel;
		vel = *(FMOD_VECTOR **)&velTmp;
	}
	FMOD_VECTOR *forward = 0;
	if(jforward) {
		POINTER_TYPE forwardTmp = (POINTER_TYPE)jforward;
		forward = *(FMOD_VECTOR **)&forwardTmp;
	}
	FMOD_VECTOR *up = 0;
	if(jup) {
		POINTER_TYPE upTmp = (POINTER_TYPE)jup;
		up = *(FMOD_VECTOR **)&upTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->set3DListenerAttributes(listener, pos, vel, forward, up);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1get3DListenerAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jlistener, jlong jpos, jlong jvel, jlong jforward, jlong jup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int listener = (int)jlistener;
	FMOD_VECTOR *pos = 0;
	if(jpos) {
		POINTER_TYPE posTmp = (POINTER_TYPE)jpos;
		pos = *(FMOD_VECTOR **)&posTmp;
	}
	FMOD_VECTOR *vel = 0;
	if(jvel) {
		POINTER_TYPE velTmp = (POINTER_TYPE)jvel;
		vel = *(FMOD_VECTOR **)&velTmp;
	}
	FMOD_VECTOR *forward = 0;
	if(jforward) {
		POINTER_TYPE forwardTmp = (POINTER_TYPE)jforward;
		forward = *(FMOD_VECTOR **)&forwardTmp;
	}
	FMOD_VECTOR *up = 0;
	if(jup) {
		POINTER_TYPE upTmp = (POINTER_TYPE)jup;
		up = *(FMOD_VECTOR **)&upTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->get3DListenerAttributes(listener, pos, vel, forward, up);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1set3DRolloffCallback(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jcallback) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->set3DRolloffCallback(jcallback == 0 ? NULL : FMOD_3D_ROLLOFFCALLBACK_BRIDGE);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1set3DSpeakerPosition(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeaker, jfloat jx, jfloat jy, jboolean jactive) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKER speaker = (FMOD_SPEAKER)jspeaker;
	float x = (float)jx;
	float y = (float)jy;
	bool active = (bool)(jactive != 0);

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->set3DSpeakerPosition(speaker, x, y, active);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1get3DSpeakerPosition(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jspeaker, jobject jx, jlong jx_, jobject jy, jlong jy_, jobject jactive, jlong jactive_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SPEAKER speaker = (FMOD_SPEAKER)jspeaker;
	float *x = 0;
	if(jx) {
		x = (float *)((char *)java_env->GetDirectBufferAddress(jx)+jx_);
	}
	float *y = 0;
	if(jy) {
		y = (float *)((char *)java_env->GetDirectBufferAddress(jy)+jy_);
	}
	bool *active = 0;
	if(jactive) {
		active = (bool *)((char *)java_env->GetDirectBufferAddress(jactive)+jactive_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->get3DSpeakerPosition(speaker, x, y, active);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setStreamBufferSize(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jfilebuffersize, jint jfilebuffersizetype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int filebuffersize = (int)jfilebuffersize;
	FMOD_TIMEUNIT filebuffersizetype = (FMOD_TIMEUNIT)jfilebuffersizetype;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setStreamBufferSize(filebuffersize, filebuffersizetype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getStreamBufferSize(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jfilebuffersize, jlong jfilebuffersize_, jobject jfilebuffersizetype, jlong jfilebuffersizetype_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *filebuffersize = 0;
	if(jfilebuffersize) {
		filebuffersize = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jfilebuffersize)+jfilebuffersize_);
	}
	FMOD_TIMEUNIT *filebuffersizetype = 0;
	if(jfilebuffersizetype) {
		filebuffersizetype = (FMOD_TIMEUNIT *)(int *)((char *)java_env->GetDirectBufferAddress(jfilebuffersizetype)+jfilebuffersizetype_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getStreamBufferSize(filebuffersize, filebuffersizetype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getVersion(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jversion, jlong jversion_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *version = 0;
	if(jversion) {
		version = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jversion)+jversion_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getVersion(version);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getOutputHandle(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jhandle) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *handle/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getOutputHandle(&handle);

	if(jhandle) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = handle;
		setPointerAddress(java_env, jhandle, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getChannelsPlaying(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jchannels, jlong jchannels_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *channels = 0;
	if(jchannels) {
		channels = (int *)((char *)java_env->GetDirectBufferAddress(jchannels)+jchannels_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getChannelsPlaying(channels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getHardwareChannels(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnum2d, jlong jnum2d_, jobject jnum3d, jlong jnum3d_, jobject jtotal, jlong jtotal_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *num2d = 0;
	if(jnum2d) {
		num2d = (int *)((char *)java_env->GetDirectBufferAddress(jnum2d)+jnum2d_);
	}
	int *num3d = 0;
	if(jnum3d) {
		num3d = (int *)((char *)java_env->GetDirectBufferAddress(jnum3d)+jnum3d_);
	}
	int *total = 0;
	if(jtotal) {
		total = (int *)((char *)java_env->GetDirectBufferAddress(jtotal)+jtotal_);
	}

        FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->getHardwareChannels(num2d, num3d, total);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getCPUUsage(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdsp, jlong jdsp_, jobject jstream, jlong jstream_, jobject jupdate, jlong jupdate_, jobject jtotal, jlong jtotal_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *dsp = 0;
	if(jdsp) {
		dsp = (float *)((char *)java_env->GetDirectBufferAddress(jdsp)+jdsp_);
	}
	float *stream = 0;
	if(jstream) {
		stream = (float *)((char *)java_env->GetDirectBufferAddress(jstream)+jstream_);
	}
	float *update = 0;
	if(jupdate) {
		update = (float *)((char *)java_env->GetDirectBufferAddress(jupdate)+jupdate_);
	}
	float *total = 0;
	if(jtotal) {
		total = (float *)((char *)java_env->GetDirectBufferAddress(jtotal)+jtotal_);
	}

	FMOD_RESULT result_ = (FMOD_RESULT)0;// (*(FMOD::System **)&pointer)->getCPUUsage(dsp, stream, update, total);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getSoundRAM(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jcurrentalloced, jlong jcurrentalloced_, jobject jmaxalloced, jlong jmaxalloced_, jobject jtotal, jlong jtotal_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *currentalloced = 0;
	if(jcurrentalloced) {
		currentalloced = (int *)((char *)java_env->GetDirectBufferAddress(jcurrentalloced)+jcurrentalloced_);
	}
	int *maxalloced = 0;
	if(jmaxalloced) {
		maxalloced = (int *)((char *)java_env->GetDirectBufferAddress(jmaxalloced)+jmaxalloced_);
	}
	int *total = 0;
	if(jtotal) {
		total = (int *)((char *)java_env->GetDirectBufferAddress(jtotal)+jtotal_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getSoundRAM(currentalloced, maxalloced, total);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getNumCDROMDrives(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumdrives, jlong jnumdrives_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numdrives = 0;
	if(jnumdrives) {
		numdrives = (int *)((char *)java_env->GetDirectBufferAddress(jnumdrives)+jnumdrives_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getNumCDROMDrives(numdrives);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getCDROMDriveName(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jdrive, 	jobject jdrivename, jlong jdrivename_, jint jdrivenamelen, 	jobject jscsiname, jlong jscsiname_, jint jscsinamelen, 	jobject jdevicename, jlong jdevicename_, jint jdevicenamelen) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int drive = (int)jdrive;
	char *drivename = 0;
	if(jdrivename) {
		drivename = (char *)java_env->GetDirectBufferAddress(jdrivename)+jdrivename_;
	}
	int drivenamelen = (int)jdrivenamelen;
	char *scsiname = 0;
	if(jscsiname) {
		scsiname = (char *)java_env->GetDirectBufferAddress(jscsiname)+jscsiname_;
	}
	int scsinamelen = (int)jscsinamelen;
	char *devicename = 0;
	if(jdevicename) {
		devicename = (char *)java_env->GetDirectBufferAddress(jdevicename)+jdevicename_;
	}
	int devicenamelen = (int)jdevicenamelen;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getCDROMDriveName(drive, drivename, drivenamelen, scsiname, scsinamelen, devicename, devicenamelen);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getSpectrum(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jspectrumarray, jlong jspectrumarray_, jint jnumvalues, jint jchanneloffset, jint jwindowtype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *spectrumarray = 0;
	if(jspectrumarray) {
		spectrumarray = (float *)((char *)java_env->GetDirectBufferAddress(jspectrumarray)+jspectrumarray_);
	}
	int numvalues = (int)jnumvalues;
	int channeloffset = (int)jchanneloffset;
	FMOD_DSP_FFT_WINDOW windowtype = (FMOD_DSP_FFT_WINDOW)jwindowtype;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getSpectrum(spectrumarray, numvalues, channeloffset, windowtype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getWaveData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jwavearray, jlong jwavearray_, jint jnumvalues, jint jchanneloffset) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *wavearray = 0;
	if(jwavearray) {
		wavearray = (float *)((char *)java_env->GetDirectBufferAddress(jwavearray)+jwavearray_);
	}
	int numvalues = (int)jnumvalues;
	int channeloffset = (int)jchanneloffset;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getWaveData(wavearray, numvalues, channeloffset);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createSound__J_3BIJLorg_jouvieje_FmodEx_Sound_2(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname_or_data, jint jmode, jlong jexinfo, jobject jsound) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name_or_data = getByteArrayElements(java_env, jname_or_data);
	FMOD_MODE mode = (FMOD_MODE)jmode;
	FMOD_CREATESOUNDEXINFO *exinfo = 0;
	if(jexinfo) {
		POINTER_TYPE exinfoTmp = (POINTER_TYPE)jexinfo;
		exinfo = *(FMOD_CREATESOUNDEXINFO **)&exinfoTmp;
	}
	FMOD::Sound *sound/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createSound(name_or_data, mode, exinfo, &sound);

	releaseByteArrayElements(java_env, jname_or_data, (const char *)name_or_data);
	if(jsound) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Sound **)&newAddress = sound;
		setPointerAddress(java_env, jsound, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createSound__JLjava_nio_ByteBuffer_2JIJLorg_jouvieje_FmodEx_Sound_2(JNIEnv *java_env, jclass jcls, jlong jpointer, 	jobject jname_or_data, jlong jname_or_data_, jint jmode, jlong jexinfo, jobject jsound) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name_or_data = 0;
	if(jname_or_data) {
		name_or_data = (char *)java_env->GetDirectBufferAddress(jname_or_data)+jname_or_data_;
	}
	FMOD_MODE mode = (FMOD_MODE)jmode;
	FMOD_CREATESOUNDEXINFO *exinfo = 0;
	if(jexinfo) {
		POINTER_TYPE exinfoTmp = (POINTER_TYPE)jexinfo;
		exinfo = *(FMOD_CREATESOUNDEXINFO **)&exinfoTmp;
	}
	FMOD::Sound *sound/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createSound(name_or_data, mode, exinfo, &sound);

	if(jsound) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Sound **)&newAddress = sound;
		setPointerAddress(java_env, jsound, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createStream__J_3BIJLorg_jouvieje_FmodEx_Sound_2(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname_or_data, jint jmode, jlong jexinfo, jobject jsound) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name_or_data = getByteArrayElements(java_env, jname_or_data);
	FMOD_MODE mode = (FMOD_MODE)jmode;
	FMOD_CREATESOUNDEXINFO *exinfo = 0;
	if(jexinfo) {
		POINTER_TYPE exinfoTmp = (POINTER_TYPE)jexinfo;
		exinfo = *(FMOD_CREATESOUNDEXINFO **)&exinfoTmp;
	}
	FMOD::Sound *sound/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createStream(name_or_data, mode, exinfo, &sound);

	releaseByteArrayElements(java_env, jname_or_data, (const char *)name_or_data);
	if(jsound) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Sound **)&newAddress = sound;
		setPointerAddress(java_env, jsound, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createStream__JLjava_nio_ByteBuffer_2JIJLorg_jouvieje_FmodEx_Sound_2(JNIEnv *java_env, jclass jcls, jlong jpointer, 	jobject jname_or_data, jlong jname_or_data_, jint jmode, jlong jexinfo, jobject jsound) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name_or_data = 0;
	if(jname_or_data) {
		name_or_data = (char *)java_env->GetDirectBufferAddress(jname_or_data)+jname_or_data_;
	}
	FMOD_MODE mode = (FMOD_MODE)jmode;
	FMOD_CREATESOUNDEXINFO *exinfo = 0;
	if(jexinfo) {
		POINTER_TYPE exinfoTmp = (POINTER_TYPE)jexinfo;
		exinfo = *(FMOD_CREATESOUNDEXINFO **)&exinfoTmp;
	}
	FMOD::Sound *sound/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createStream(name_or_data, mode, exinfo, &sound);

	if(jsound) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Sound **)&newAddress = sound;
		setPointerAddress(java_env, jsound, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createDSP(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jdescription, jobject jdsp) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_DSP_DESCRIPTION *description = 0;
	if(jdescription) {
		POINTER_TYPE descriptionTmp = (POINTER_TYPE)jdescription;
		description = *(FMOD_DSP_DESCRIPTION **)&descriptionTmp;
	}
	FMOD::DSP *dsp/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createDSP(description, &dsp);

	if(jdsp) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = dsp;
		setPointerAddress(java_env, jdsp, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createDSPByType(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jtype, jobject jdsp) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_DSP_TYPE type = (FMOD_DSP_TYPE)jtype;
	FMOD::DSP *dsp/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createDSPByType(type, &dsp);

	if(jdsp) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = dsp;
		setPointerAddress(java_env, jdsp, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createDSPByIndex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jdsp) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::DSP *dsp/* = 0*/;
    
    unsigned int handle = 0;
    FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getPluginHandle(FMOD_PLUGINTYPE_CODEC, index, &handle);
    result_ = (*(FMOD::System **)&pointer)->createDSPByPlugin(handle, &dsp);
	if(jdsp) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = dsp;
		setPointerAddress(java_env, jdsp, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createChannelGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jobject jchannelgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD::ChannelGroup *channelgroup/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createChannelGroup(name, &channelgroup);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jchannelgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::ChannelGroup **)&newAddress = channelgroup;
		setPointerAddress(java_env, jchannelgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createSoundGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jobject jsoundgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	FMOD::SoundGroup *soundgroup/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createSoundGroup(name, &soundgroup);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jsoundgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::SoundGroup **)&newAddress = soundgroup;
		setPointerAddress(java_env, jsoundgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createReverb(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jreverb) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::Reverb *reverb/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createReverb(&reverb);

	if(jreverb) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Reverb **)&newAddress = reverb;
		setPointerAddress(java_env, jreverb, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1playSound(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jchannelid, jlong jsound, jboolean jpaused, jobject jchannel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_CHANNELINDEX channelid = (FMOD_CHANNELINDEX)jchannelid;
	FMOD::Sound *sound = 0;
	if(jsound) {
		POINTER_TYPE soundTmp = (POINTER_TYPE)jsound;
		sound = *(FMOD::Sound **)&soundTmp;
	}
	bool paused = (bool)(jpaused != 0);
	FMOD::Channel *channel/* = 0*/;
	if(jchannel) {
		POINTER_TYPE jchannelAddress = getPointerAddress(java_env, jchannel);
		if(jchannelAddress) {
			channel = *(FMOD::Channel **)&jchannelAddress;
		}
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->playSound(channelid, sound, paused, &channel);

	if(jchannel) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Channel **)&newAddress = channel;
		setPointerAddress(java_env, jchannel, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1playDSP(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jchannelid, jlong jdsp, jboolean jpaused, jobject jchannel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_CHANNELINDEX channelid = (FMOD_CHANNELINDEX)jchannelid;
	FMOD::DSP *dsp = 0;
	if(jdsp) {
		POINTER_TYPE dspTmp = (POINTER_TYPE)jdsp;
		dsp = *(FMOD::DSP **)&dspTmp;
	}
	bool paused = (bool)(jpaused != 0);
	FMOD::Channel *channel/* = 0*/;
	if(jchannel) {
		POINTER_TYPE jchannelAddress = getPointerAddress(java_env, jchannel);
		if(jchannelAddress) {
			channel = *(FMOD::Channel **)&jchannelAddress;
		}
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->playDSP(channelid, dsp, paused, &channel);

	if(jchannel) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Channel **)&newAddress = channel;
		setPointerAddress(java_env, jchannel, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getChannel(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jchannelid, jobject jchannel) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int channelid = (int)jchannelid;
	FMOD::Channel *channel/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getChannel(channelid, &channel);

	if(jchannel) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Channel **)&newAddress = channel;
		setPointerAddress(java_env, jchannel, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getMasterChannelGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jchannelgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::ChannelGroup *channelgroup/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getMasterChannelGroup(&channelgroup);

	if(jchannelgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::ChannelGroup **)&newAddress = channelgroup;
		setPointerAddress(java_env, jchannelgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getMasterSoundGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsoundgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::SoundGroup *soundgroup/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getMasterSoundGroup(&soundgroup);

	if(jsoundgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::SoundGroup **)&newAddress = soundgroup;
		setPointerAddress(java_env, jsoundgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setReverbProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setReverbProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getReverbProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getReverbProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setReverbAmbientProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setReverbAmbientProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getReverbAmbientProperties(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jprop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_REVERB_PROPERTIES *prop = 0;
	if(jprop) {
		POINTER_TYPE propTmp = (POINTER_TYPE)jprop;
		prop = *(FMOD_REVERB_PROPERTIES **)&propTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getReverbAmbientProperties(prop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getDSPHead(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdsp) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *dsp/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getDSPHead(&dsp);

	if(jdsp) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSP **)&newAddress = dsp;
		setPointerAddress(java_env, jdsp, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1addDSP(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jdsp, jobject jconnection) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::DSP *dsp = 0;
	if(jdsp) {
		POINTER_TYPE dspTmp = (POINTER_TYPE)jdsp;
		dsp = *(FMOD::DSP **)&dspTmp;
	}
	FMOD::DSPConnection *connection/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->addDSP(dsp, &connection);

	if(jconnection) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::DSPConnection **)&newAddress = connection;
		setPointerAddress(java_env, jconnection, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1lockDSP(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->lockDSP();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1unlockDSP(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->unlockDSP();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getDSPClock(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jhi, jlong jhi_, jobject jlo, jlong jlo_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *hi = 0;
	if(jhi) {
		hi = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jhi)+jhi_);
	}
	unsigned int *lo = 0;
	if(jlo) {
		lo = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jlo)+jlo_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getDSPClock(hi, lo);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setRecordDriver(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jdriver) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int driver = (int)jdriver;

	FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->setRecordDriver(driver);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getRecordDriver(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdriver, jlong jdriver_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *driver = 0;
	if(jdriver) {
		driver = (int *)((char *)java_env->GetDirectBufferAddress(jdriver)+jdriver_);
	}

	FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->getRecordDriver(driver);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getRecordNumDrivers(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumdrivers, jlong jnumdrivers_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numdrivers = 0;
	if(jnumdrivers) {
		numdrivers = (int *)((char *)java_env->GetDirectBufferAddress(jnumdrivers)+jnumdrivers_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getRecordNumDrivers(numdrivers);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getRecordDriverInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jid, 	jobject jname, jlong jname_, jint jnamelen, jlong jguid) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int id = (int)jid;
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	int namelen = (int)jnamelen;
	FMOD_GUID *guid = 0;
	if(jguid) {
		POINTER_TYPE guidTmp = (POINTER_TYPE)jguid;
		guid = *(FMOD_GUID **)&guidTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getRecordDriverInfo(id, name, namelen, guid);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getRecordDriverCaps(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jid, jobject jcaps, jlong jcaps_, jobject jminfrequency, jlong jminfrequency_, jobject jmaxfrequency, jlong jmaxfrequency_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int id = (int)jid;
	FMOD_CAPS *caps = 0;
	if(jcaps) {
		caps = (FMOD_CAPS *)(int *)((char *)java_env->GetDirectBufferAddress(jcaps)+jcaps_);
	}
	int *minfrequency = 0;
	if(jminfrequency) {
		minfrequency = (int *)((char *)java_env->GetDirectBufferAddress(jminfrequency)+jminfrequency_);
	}
	int *maxfrequency = 0;
	if(jmaxfrequency) {
		maxfrequency = (int *)((char *)java_env->GetDirectBufferAddress(jmaxfrequency)+jmaxfrequency_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getRecordDriverCaps(id, caps, minfrequency, maxfrequency);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getRecordPosition(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jposition, jlong jposition_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *position = 0;
	if(jposition) {
		position = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jposition)+jposition_);
	}

	FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->getRecordPosition(position);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1recordStart(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jsound, jboolean jloop) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::Sound *sound = 0;
	if(jsound) {
		POINTER_TYPE soundTmp = (POINTER_TYPE)jsound;
		sound = *(FMOD::Sound **)&soundTmp;
	}
	bool loop = (bool)(jloop != 0);

	FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->recordStart(sound, loop);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1recordStop(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->recordStop();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1isRecording(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jrecording, jlong jrecording_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *recording = 0;
	if(jrecording) {
		recording = (bool *)((char *)java_env->GetDirectBufferAddress(jrecording)+jrecording_);
	}

	FMOD_RESULT result_ = (FMOD_RESULT)0;//(*(FMOD::System **)&pointer)->isRecording(recording);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1createGeometry(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmaxpolygons, jint jmaxvertices, jobject jgeometry) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int maxpolygons = (int)jmaxpolygons;
	int maxvertices = (int)jmaxvertices;
	FMOD::Geometry *geometry/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->createGeometry(maxpolygons, maxvertices, &geometry);

	if(jgeometry) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Geometry **)&newAddress = geometry;
		setPointerAddress(java_env, jgeometry, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setGeometrySettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jmaxworldsize) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float maxworldsize = (float)jmaxworldsize;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setGeometrySettings(maxworldsize);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getGeometrySettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmaxworldsize, jlong jmaxworldsize_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *maxworldsize = 0;
	if(jmaxworldsize) {
		maxworldsize = (float *)((char *)java_env->GetDirectBufferAddress(jmaxworldsize)+jmaxworldsize_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getGeometrySettings(maxworldsize);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1loadGeometry(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdata, jlong jdata_, jint jdatasize, jobject jgeometry) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *data = 0;
	if(jdata) {
		data = (void *)((char *)java_env->GetDirectBufferAddress(jdata)+jdata_);
	}
	int datasize = (int)jdatasize;
	FMOD::Geometry *geometry/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->loadGeometry(data, datasize, &geometry);

	if(jgeometry) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Geometry **)&newAddress = geometry;
		setPointerAddress(java_env, jgeometry, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setNetworkProxy(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jproxy) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *proxy = getByteArrayElements(java_env, jproxy);

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setNetworkProxy(proxy);

	releaseByteArrayElements(java_env, jproxy, (const char *)proxy);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getNetworkProxy(JNIEnv *java_env, jclass jcls, jlong jpointer, 	jobject jproxy, jlong jproxy_, jint jproxylen) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *proxy = 0;
	if(jproxy) {
		proxy = (char *)java_env->GetDirectBufferAddress(jproxy)+jproxy_;
	}
	int proxylen = (int)jproxylen;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getNetworkProxy(proxy, proxylen);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setNetworkTimeout(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jtimeout) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int timeout = (int)jtimeout;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setNetworkTimeout(timeout);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getNetworkTimeout(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jtimeout, jlong jtimeout_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *timeout = 0;
	if(jtimeout) {
		timeout = (int *)((char *)java_env->GetDirectBufferAddress(jtimeout)+jtimeout_);
	}

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getNetworkTimeout(timeout);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_System_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SYSTEM);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::System **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


