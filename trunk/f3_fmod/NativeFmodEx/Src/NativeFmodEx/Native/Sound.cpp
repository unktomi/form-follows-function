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
#include "org_jouvieje_FmodEx_FmodExJNI.h"
#include "CallbackManager.h"

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1release(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->release();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getSystemObject(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsystem) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::System *system/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getSystemObject(&system);

	if(jsystem) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::System **)&newAddress = system;
		setPointerAddress(java_env, jsystem, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1lock(JNIEnv *java_env, jclass jcls, jlong jpointer, jint joffset, jint jlength, jobjectArray jptr1, jobjectArray jptr2, jobject jlen1, jlong jlen1_, jobject jlen2, jlong jlen2_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int offset = (int)joffset;
	int length = (int)jlength;
	void *ptr1/* = 0*/;
	void *ptr2/* = 0*/;
	unsigned int *len1 = 0;
	if(jlen1) {
		len1 = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jlen1)+jlen1_);
	}
	unsigned int *len2 = 0;
	if(jlen2) {
		len2 = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jlen2)+jlen2_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->lock(offset, length, &ptr1, &ptr2, len1, len2);

	if(ptr1 && jptr1 && java_env->GetArrayLength(jptr1) >= 1) {
		java_env->SetObjectArrayElement(jptr1, 0, java_env->NewDirectByteBuffer((void *)ptr1, *len1));
	}
	if(ptr2 && jptr2 && java_env->GetArrayLength(jptr2) >= 1) {
		java_env->SetObjectArrayElement(jptr2, 0, java_env->NewDirectByteBuffer((void *)ptr2, *len2));
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1unlock(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jptr1, jlong jptr1_, jobject jptr2, jlong jptr2_, jint jlen1, jint jlen2) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *ptr1 = 0;
	if(jptr1) {
		ptr1 = (void *)((char *)java_env->GetDirectBufferAddress(jptr1)+jptr1_);
	}
	void *ptr2 = 0;
	if(jptr2) {
		ptr2 = (void *)((char *)java_env->GetDirectBufferAddress(jptr2)+jptr2_);
	}
	int len1 = (int)jlen1;
	int len2 = (int)jlen2;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->unlock(ptr1, ptr2, len1, len2);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setDefaults(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jfrequency, jfloat jvolume, jfloat jpan, jint jpriority) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float frequency = (float)jfrequency;
	float volume = (float)jvolume;
	float pan = (float)jpan;
	int priority = (int)jpriority;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setDefaults(frequency, volume, pan, priority);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getDefaults(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jfrequency, jlong jfrequency_, jobject jvolume, jlong jvolume_, jobject jpan, jlong jpan_, jobject jpriority, jlong jpriority_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
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

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getDefaults(frequency, volume, pan, priority);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setVariations(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jfrequencyvar, jfloat jvolumevar, jfloat jpanvar) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float frequencyvar = (float)jfrequencyvar;
	float volumevar = (float)jvolumevar;
	float panvar = (float)jpanvar;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setVariations(frequencyvar, volumevar, panvar);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getVariations(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jfrequencyvar, jlong jfrequencyvar_, jobject jvolumevar, jlong jvolumevar_, jobject jpanvar, jlong jpanvar_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *frequencyvar = 0;
	if(jfrequencyvar) {
		frequencyvar = (float *)((char *)java_env->GetDirectBufferAddress(jfrequencyvar)+jfrequencyvar_);
	}
	float *volumevar = 0;
	if(jvolumevar) {
		volumevar = (float *)((char *)java_env->GetDirectBufferAddress(jvolumevar)+jvolumevar_);
	}
	float *panvar = 0;
	if(jpanvar) {
		panvar = (float *)((char *)java_env->GetDirectBufferAddress(jpanvar)+jpanvar_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getVariations(frequencyvar, volumevar, panvar);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1set3DMinMaxDistance(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jmin, jfloat jmax) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float min = (float)jmin;
	float max = (float)jmax;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->set3DMinMaxDistance(min, max);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1get3DMinMaxDistance(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmin, jlong jmin_, jobject jmax, jlong jmax_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *min = 0;
	if(jmin) {
		min = (float *)((char *)java_env->GetDirectBufferAddress(jmin)+jmin_);
	}
	float *max = 0;
	if(jmax) {
		max = (float *)((char *)java_env->GetDirectBufferAddress(jmax)+jmax_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->get3DMinMaxDistance(min, max);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1set3DConeSettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jinsideconeangle, jfloat joutsideconeangle, jfloat joutsidevolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float insideconeangle = (float)jinsideconeangle;
	float outsideconeangle = (float)joutsideconeangle;
	float outsidevolume = (float)joutsidevolume;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->set3DConeSettings(insideconeangle, outsideconeangle, outsidevolume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1get3DConeSettings(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jinsideconeangle, jlong jinsideconeangle_, jobject joutsideconeangle, jlong joutsideconeangle_, jobject joutsidevolume, jlong joutsidevolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float *insideconeangle = 0;
	if(jinsideconeangle) {
		insideconeangle = (float *)((char *)java_env->GetDirectBufferAddress(jinsideconeangle)+jinsideconeangle_);
	}
	float *outsideconeangle = 0;
	if(joutsideconeangle) {
		outsideconeangle = (float *)((char *)java_env->GetDirectBufferAddress(joutsideconeangle)+joutsideconeangle_);
	}
	float *outsidevolume = 0;
	if(joutsidevolume) {
		outsidevolume = (float *)((char *)java_env->GetDirectBufferAddress(joutsidevolume)+joutsidevolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->get3DConeSettings(insideconeangle, outsideconeangle, outsidevolume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1set3DCustomRolloff(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jpoints, jint jnumpoints) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *points = 0;
	if(jpoints) {
		POINTER_TYPE pointsTmp = (POINTER_TYPE)jpoints;
		points = *(FMOD_VECTOR **)&pointsTmp;
	}
	int numpoints = (int)jnumpoints;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->set3DCustomRolloff(points, numpoints);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1get3DCustomRolloff(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jpoints, jobject jnumpoints, jlong jnumpoints_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *points/* = 0*/;
	int *numpoints = 0;
	if(jnumpoints) {
		numpoints = (int *)((char *)java_env->GetDirectBufferAddress(jnumpoints)+jnumpoints_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->get3DCustomRolloff(&points, numpoints);

	if(jpoints) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD_VECTOR **)&newAddress = points;
		setPointerAddress(java_env, jpoints, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setSubSound(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jlong jsubsound) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::Sound *subsound = 0;
	if(jsubsound) {
		POINTER_TYPE subsoundTmp = (POINTER_TYPE)jsubsound;
		subsound = *(FMOD::Sound **)&subsoundTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setSubSound(index, subsound);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getSubSound(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jsubsound) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD::Sound *subsound/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getSubSound(index, &subsound);

	if(jsubsound) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::Sound **)&newAddress = subsound;
		setPointerAddress(java_env, jsubsound, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setSubSoundSentence(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsubsoundlist, jlong jsubsoundlist_, jint jnumsubsounds) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *subsoundlist = 0;
	if(jsubsoundlist) {
		subsoundlist = (int *)((char *)java_env->GetDirectBufferAddress(jsubsoundlist)+jsubsoundlist_);
	}
	int numsubsounds = (int)jnumsubsounds;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setSubSoundSentence(subsoundlist, numsubsounds);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getName(JNIEnv *java_env, jclass jcls, jlong jpointer, 	jobject jname, jlong jname_, jint jnamelen) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	int namelen = (int)jnamelen;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getName(name, namelen);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getLength(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jlength, jlong jlength_, jint jlengthtype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *length = 0;
	if(jlength) {
		length = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jlength)+jlength_);
	}
	FMOD_TIMEUNIT lengthtype = (FMOD_TIMEUNIT)jlengthtype;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getLength(length, lengthtype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getFormat(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jtypePointer, jobject jformatPointer, jobject jchannels, jlong jchannels_, jobject jbits, jlong jbits_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SOUND_TYPE type;
	FMOD_SOUND_FORMAT format;
	int *channels = 0;
	if(jchannels) {
		channels = (int *)((char *)java_env->GetDirectBufferAddress(jchannels)+jchannels_);
	}
	int *bits = 0;
	if(jbits) {
		bits = (int *)((char *)java_env->GetDirectBufferAddress(jbits)+jbits_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getFormat(&type, &format, channels, bits);

	if(jtypePointer) {
		int *typePointer = (int *)java_env->GetDirectBufferAddress(jtypePointer);
		typePointer[0] = type;
	}
	if(jformatPointer) {
		int *formatPointer = (int *)java_env->GetDirectBufferAddress(jformatPointer);
		formatPointer[0] = format;
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getNumSubSounds(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumsubsounds, jlong jnumsubsounds_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numsubsounds = 0;
	if(jnumsubsounds) {
		numsubsounds = (int *)((char *)java_env->GetDirectBufferAddress(jnumsubsounds)+jnumsubsounds_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getNumSubSounds(numsubsounds);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getNumTags(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumtags, jlong jnumtags_, jobject jnumtagsupdated, jlong jnumtagsupdated_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numtags = 0;
	if(jnumtags) {
		numtags = (int *)((char *)java_env->GetDirectBufferAddress(jnumtags)+jnumtags_);
	}
	int *numtagsupdated = 0;
	if(jnumtagsupdated) {
		numtagsupdated = (int *)((char *)java_env->GetDirectBufferAddress(jnumtagsupdated)+jnumtagsupdated_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getNumTags(numtags, numtagsupdated);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getTag(JNIEnv *java_env, jclass jcls, jlong jpointer, jbyteArray jname, jint jindex, jlong jtag) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	char *name = getByteArrayElements(java_env, jname);
	int index = (int)jindex;
	FMOD_TAG *tag = 0;
	if(jtag) {
		POINTER_TYPE tagTmp = (POINTER_TYPE)jtag;
		tag = *(FMOD_TAG **)&tagTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getTag(name, index, tag);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getOpenState(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jopenstatePointer, jobject jpercentbuffered, jlong jpercentbuffered_, jobject jstarving, jlong jstarving_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_OPENSTATE openstate;
	unsigned int *percentbuffered = 0;
	if(jpercentbuffered) {
		percentbuffered = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jpercentbuffered)+jpercentbuffered_);
	}
	bool *starving = 0;
	if(jstarving) {
		starving = (bool *)((char *)java_env->GetDirectBufferAddress(jstarving)+jstarving_);
	}

	FMOD_RESULT result_ = (FMOD_RESULT)0; //(*(FMOD::Sound **)&pointer)->getOpenState(&openstate, percentbuffered, starving);

	if(jopenstatePointer) {
		int *openstatePointer = (int *)java_env->GetDirectBufferAddress(jopenstatePointer);
		openstatePointer[0] = openstate;
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1readData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jbuffer, jlong jbuffer_, jint jlenbytes, jobject jread, jlong jread_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *buffer = 0;
	if(jbuffer) {
		buffer = (void *)((char *)java_env->GetDirectBufferAddress(jbuffer)+jbuffer_);
	}
	int lenbytes = (int)jlenbytes;
	unsigned int *read = 0;
	if(jread) {
		read = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jread)+jread_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->readData(buffer, lenbytes, read);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1seekData(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jpcm) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int pcm = (int)jpcm;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->seekData(pcm);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setSoundGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jsoundgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::SoundGroup *soundgroup = 0;
	if(jsoundgroup) {
		POINTER_TYPE soundgroupTmp = (POINTER_TYPE)jsoundgroup;
		soundgroup = *(FMOD::SoundGroup **)&soundgroupTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setSoundGroup(soundgroup);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getSoundGroup(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jsoundgroup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD::SoundGroup *soundgroup/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getSoundGroup(&soundgroup);

	if(jsoundgroup) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD::SoundGroup **)&newAddress = soundgroup;
		setPointerAddress(java_env, jsoundgroup, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getNumSyncPoints(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumsyncpoints, jlong jnumsyncpoints_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numsyncpoints = 0;
	if(jnumsyncpoints) {
		numsyncpoints = (int *)((char *)java_env->GetDirectBufferAddress(jnumsyncpoints)+jnumsyncpoints_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getNumSyncPoints(numsyncpoints);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getSyncPoint(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jpoint) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	FMOD_SYNCPOINT *point/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getSyncPoint(index, &point);

	if(jpoint) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD_SYNCPOINT **)&newAddress = point;
		setPointerAddress(java_env, jpoint, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getSyncPointInfo(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jpoint, 	jobject jname, jlong jname_, jint jnamelen, jobject joffset, jlong joffset_, jint joffsettype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SYNCPOINT *point = 0;
	if(jpoint) {
		POINTER_TYPE pointTmp = (POINTER_TYPE)jpoint;
		point = *(FMOD_SYNCPOINT **)&pointTmp;
	}
	char *name = 0;
	if(jname) {
		name = (char *)java_env->GetDirectBufferAddress(jname)+jname_;
	}
	int namelen = (int)jnamelen;
	unsigned int *offset = 0;
	if(joffset) {
		offset = (unsigned int *)((char *)java_env->GetDirectBufferAddress(joffset)+joffset_);
	}
	FMOD_TIMEUNIT offsettype = (FMOD_TIMEUNIT)joffsettype;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getSyncPointInfo(point, name, namelen, offset, offsettype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1addSyncPoint(JNIEnv *java_env, jclass jcls, jlong jpointer, jint joffset, jint joffsettype, jbyteArray jname, jobject jpoint) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int offset = (int)joffset;
	FMOD_TIMEUNIT offsettype = (FMOD_TIMEUNIT)joffsettype;
	char *name = getByteArrayElements(java_env, jname);
	FMOD_SYNCPOINT *point/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->addSyncPoint(offset, offsettype, name, &point);

	releaseByteArrayElements(java_env, jname, (const char *)name);
	if(jpoint) {
		POINTER_TYPE newAddress/* = 0*/;
		*(FMOD_SYNCPOINT **)&newAddress = point;
		setPointerAddress(java_env, jpoint, newAddress);
	}
	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1deleteSyncPoint(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jpoint) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_SYNCPOINT *point = 0;
	if(jpoint) {
		POINTER_TYPE pointTmp = (POINTER_TYPE)jpoint;
		point = *(FMOD_SYNCPOINT **)&pointTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->deleteSyncPoint(point);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setMode(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jmode) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_MODE mode = (FMOD_MODE)jmode;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setMode(mode);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getMode(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmode, jlong jmode_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_MODE *mode = 0;
	if(jmode) {
		mode = (FMOD_MODE *)(int *)((char *)java_env->GetDirectBufferAddress(jmode)+jmode_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getMode(mode);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setLoopCount(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jloopcount) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int loopcount = (int)jloopcount;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setLoopCount(loopcount);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getLoopCount(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jloopcount, jlong jloopcount_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *loopcount = 0;
	if(jloopcount) {
		loopcount = (int *)((char *)java_env->GetDirectBufferAddress(jloopcount)+jloopcount_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getLoopCount(loopcount);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setLoopPoints(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jloopstart, jint jloopstarttype, jint jloopend, jint jloopendtype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int loopstart = (int)jloopstart;
	FMOD_TIMEUNIT loopstarttype = (FMOD_TIMEUNIT)jloopstarttype;
	int loopend = (int)jloopend;
	FMOD_TIMEUNIT loopendtype = (FMOD_TIMEUNIT)jloopendtype;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setLoopPoints(loopstart, loopstarttype, loopend, loopendtype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getLoopPoints(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jloopstart, jlong jloopstart_, jint jloopstarttype, jobject jloopend, jlong jloopend_, jint jloopendtype) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	unsigned int *loopstart = 0;
	if(jloopstart) {
		loopstart = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jloopstart)+jloopstart_);
	}
	FMOD_TIMEUNIT loopstarttype = (FMOD_TIMEUNIT)jloopstarttype;
	unsigned int *loopend = 0;
	if(jloopend) {
		loopend = (unsigned int *)((char *)java_env->GetDirectBufferAddress(jloopend)+jloopend_);
	}
	FMOD_TIMEUNIT loopendtype = (FMOD_TIMEUNIT)jloopendtype;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getLoopPoints(loopstart, loopstarttype, loopend, loopendtype);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getMusicNumChannels(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumchannels, jlong jnumchannels_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numchannels = 0;
	if(jnumchannels) {
		numchannels = (int *)((char *)java_env->GetDirectBufferAddress(jnumchannels)+jnumchannels_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getMusicNumChannels(numchannels);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setMusicChannelVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jchannel, jfloat jvolume) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int channel = (int)jchannel;
	float volume = (float)jvolume;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setMusicChannelVolume(channel, volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getMusicChannelVolume(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jchannel, jobject jvolume, jlong jvolume_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int channel = (int)jchannel;
	float *volume = 0;
	if(jvolume) {
		volume = (float *)((char *)java_env->GetDirectBufferAddress(jvolume)+jvolume_);
	}

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getMusicChannelVolume(channel, volume);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Sound_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_SOUND);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Sound **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


