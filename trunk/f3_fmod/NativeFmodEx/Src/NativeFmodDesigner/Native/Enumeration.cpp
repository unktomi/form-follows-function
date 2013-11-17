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
#include "fmod_event.h"
#include "fmod_event_net.h"
#include "fmod_event.hpp"
#include "org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI.h"
#include "CallbackManager.h"

				/* FMOD_EVENT_PROPERTY */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1NAME(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_NAME;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1VOLUME(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_VOLUME;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1VOLUMERANDOMIZATION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_VOLUMERANDOMIZATION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1PITCH(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_PITCH;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1PITCH_1OCTAVES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_PITCH_OCTAVES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1PITCH_1SEMITONES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_PITCH_SEMITONES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1PITCH_1TONES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_PITCH_TONES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1PITCHRANDOMIZATION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_PITCHRANDOMIZATION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1PITCHRANDOMIZATION_1OCTAVES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_PITCHRANDOMIZATION_OCTAVES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1PITCHRANDOMIZATION_1SEMITONES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_PITCHRANDOMIZATION_SEMITONES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1PITCHRANDOMIZATION_1TONES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_PITCHRANDOMIZATION_TONES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1PRIORITY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_PRIORITY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1MAX_1PLAYBACKS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_MAX_PLAYBACKS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1MAX_1PLAYBACKS_1BEHAVIOR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_MAX_PLAYBACKS_BEHAVIOR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1MODE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_MODE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1ROLLOFF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_ROLLOFF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1MINDISTANCE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_MINDISTANCE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1MAXDISTANCE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_MAXDISTANCE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1POSITION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_POSITION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1CONEINSIDEANGLE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_CONEINSIDEANGLE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1CONEOUTSIDEANGLE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_CONEOUTSIDEANGLE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1CONEOUTSIDEVOLUME(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_CONEOUTSIDEVOLUME;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1DOPPLERSCALE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_DOPPLERSCALE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1SPEAKERSPREAD(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_SPEAKERSPREAD;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1PANLEVEL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_PANLEVEL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPEAKER_1L(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPEAKER_L;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPEAKER_1C(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPEAKER_C;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPEAKER_1R(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPEAKER_R;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPEAKER_1LS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPEAKER_LS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPEAKER_1RS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPEAKER_RS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPEAKER_1LR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPEAKER_LR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPEAKER_1RR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPEAKER_RR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPEAKER_1LFE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPEAKER_LFE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1REVERBWETLEVEL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_REVERBWETLEVEL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1ONESHOT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_ONESHOT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1FADEIN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_FADEIN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1FADEOUT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_FADEOUT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1REVERBDRYLEVEL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_REVERBDRYLEVEL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1TIMEOFFSET(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_TIMEOFFSET;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPAWNINTENSITY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPAWNINTENSITY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1SPAWNINTENSITY_1RANDOMIZATION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_SPAWNINTENSITY_RANDOMIZATION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1WII_1CONTROLLERSPEAKER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_WII_CONTROLLERSPEAKER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_13D_1POSRANDOMIZATION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_3D_POSRANDOMIZATION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENTPROPERTY_1USER_1BASE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENTPROPERTY_USER_BASE;
}

				/* FMOD_EVENT_PITCHUNITS */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1PITCHUNITS_1RAW(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_PITCHUNITS_RAW;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1PITCHUNITS_1OCTAVES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_PITCHUNITS_OCTAVES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1PITCHUNITS_1SEMITONES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_PITCHUNITS_SEMITONES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1PITCHUNITS_1TONES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_PITCHUNITS_TONES;
}

				/* FMOD_EVENT_RESOURCE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1RESOURCE_1STREAMS_1AND_1SAMPLES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_RESOURCE_STREAMS_AND_SAMPLES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1RESOURCE_1STREAMS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_RESOURCE_STREAMS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1RESOURCE_1SAMPLES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_RESOURCE_SAMPLES;
}

				/* FMOD_EVENT_CALLBACKTYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1SYNCPOINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_SYNCPOINT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1SOUNDDEF_1START(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_START;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1SOUNDDEF_1END(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_END;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1STOLEN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_STOLEN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1EVENTFINISHED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_EVENTFINISHED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1NET_1MODIFIED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_NET_MODIFIED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1SOUNDDEF_1CREATE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_CREATE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1SOUNDDEF_1RELEASE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_RELEASE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1SOUNDDEF_1INFO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_INFO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1EVENTSTARTED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_EVENTSTARTED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1EVENT_1CALLBACKTYPE_1SOUNDDEF_1SELECTINDEX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_EVENT_CALLBACKTYPE_SOUNDDEF_SELECTINDEX;
}

				/* FMOD_MUSIC_CALLBACKTYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1MUSIC_1CALLBACKTYPE_1SEGMENT_1START(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_MUSIC_CALLBACKTYPE_SEGMENT_START;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1MUSIC_1CALLBACKTYPE_1SEGMENT_1END(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_MUSIC_CALLBACKTYPE_SEGMENT_END;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodDesigner_Enumerations_EnumerationJNI_get_1FMOD_1MUSIC_1CALLBACKTYPE_1RESET(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_MUSIC_CALLBACKTYPE_RESET;
}


