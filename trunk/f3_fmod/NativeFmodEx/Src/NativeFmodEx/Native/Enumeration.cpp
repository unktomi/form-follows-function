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
#include "org_jouvieje_FmodEx_Enumerations_EnumerationJNI.h"
#include "CallbackManager.h"

				/* FMOD_DSP_TYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1UNKNOWN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_UNKNOWN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1MIXER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_MIXER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1OSCILLATOR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_OSCILLATOR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1LOWPASS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_LOWPASS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1ITLOWPASS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_ITLOWPASS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1HIGHPASS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_HIGHPASS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1ECHO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_ECHO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1FLANGE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_FLANGE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1DISTORTION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_DISTORTION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1NORMALIZE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_NORMALIZE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1PARAMEQ(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_PARAMEQ;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1PITCHSHIFT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_PITCHSHIFT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1CHORUS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_CHORUS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1REVERB(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_REVERB;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1VSTPLUGIN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_VSTPLUGIN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1WINAMPPLUGIN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_WINAMPPLUGIN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1ITECHO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_ITECHO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1COMPRESSOR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_COMPRESSOR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1SFXREVERB(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_SFXREVERB;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1LOWPASS_1SIMPLE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_LOWPASS_SIMPLE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1TYPE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_TYPE_FORCEINT;
}

				/* FMOD_DSP_OSCILLATOR */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1OSCILLATOR_1TYPE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_OSCILLATOR_TYPE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1OSCILLATOR_1RATE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_OSCILLATOR_RATE;
}

				/* FMOD_DSP_LOWPASS */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1LOWPASS_1CUTOFF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_LOWPASS_CUTOFF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1LOWPASS_1RESONANCE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_LOWPASS_RESONANCE;
}

				/* FMOD_DSP_ITLOWPASS */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ITLOWPASS_1CUTOFF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ITLOWPASS_CUTOFF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ITLOWPASS_1RESONANCE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ITLOWPASS_RESONANCE;
}

				/* FMOD_DSP_HIGHPASS */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1HIGHPASS_1CUTOFF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_HIGHPASS_CUTOFF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1HIGHPASS_1RESONANCE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_HIGHPASS_RESONANCE;
}

				/* FMOD_DSP_ECHO */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ECHO_1DELAY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ECHO_DELAY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ECHO_1DECAYRATIO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ECHO_DECAYRATIO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ECHO_1MAXCHANNELS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ECHO_MAXCHANNELS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ECHO_1DRYMIX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ECHO_DRYMIX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ECHO_1WETMIX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ECHO_WETMIX;
}

				/* FMOD_DSP_FLANGE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FLANGE_1DRYMIX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FLANGE_DRYMIX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FLANGE_1WETMIX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FLANGE_WETMIX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FLANGE_1DEPTH(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FLANGE_DEPTH;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FLANGE_1RATE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FLANGE_RATE;
}

				/* FMOD_DSP_DISTORTION */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1DISTORTION_1LEVEL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_DISTORTION_LEVEL;
}

				/* FMOD_DSP_NORMALIZE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1NORMALIZE_1FADETIME(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_NORMALIZE_FADETIME;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1NORMALIZE_1THRESHHOLD(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_NORMALIZE_THRESHHOLD;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1NORMALIZE_1MAXAMP(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_NORMALIZE_MAXAMP;
}

				/* FMOD_DSP_PARAMEQ */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1PARAMEQ_1CENTER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_PARAMEQ_CENTER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1PARAMEQ_1BANDWIDTH(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_PARAMEQ_BANDWIDTH;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1PARAMEQ_1GAIN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_PARAMEQ_GAIN;
}

				/* FMOD_DSP_PITCHSHIFT */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1PITCHSHIFT_1PITCH(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_PITCHSHIFT_PITCH;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1PITCHSHIFT_1FFTSIZE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_PITCHSHIFT_FFTSIZE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1PITCHSHIFT_1OVERLAP(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_PITCHSHIFT_OVERLAP;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1PITCHSHIFT_1MAXCHANNELS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_PITCHSHIFT_MAXCHANNELS;
}

				/* FMOD_DSP_CHORUS */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1CHORUS_1DRYMIX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_CHORUS_DRYMIX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1CHORUS_1WETMIX1(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_CHORUS_WETMIX1;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1CHORUS_1WETMIX2(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_CHORUS_WETMIX2;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1CHORUS_1WETMIX3(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_CHORUS_WETMIX3;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1CHORUS_1DELAY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_CHORUS_DELAY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1CHORUS_1RATE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_CHORUS_RATE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1CHORUS_1DEPTH(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_CHORUS_DEPTH;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1CHORUS_1FEEDBACK(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_CHORUS_FEEDBACK;
}

				/* FMOD_DSP_REVERB */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1REVERB_1ROOMSIZE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_REVERB_ROOMSIZE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1REVERB_1DAMP(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_REVERB_DAMP;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1REVERB_1WETMIX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_REVERB_WETMIX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1REVERB_1DRYMIX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_REVERB_DRYMIX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1REVERB_1WIDTH(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_REVERB_WIDTH;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1REVERB_1MODE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_REVERB_MODE;
}

				/* FMOD_DSP_ITECHO */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ITECHO_1WETDRYMIX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ITECHO_WETDRYMIX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ITECHO_1FEEDBACK(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ITECHO_FEEDBACK;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ITECHO_1LEFTDELAY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ITECHO_LEFTDELAY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ITECHO_1RIGHTDELAY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ITECHO_RIGHTDELAY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1ITECHO_1PANDELAY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_ITECHO_PANDELAY;
}

				/* FMOD_DSP_COMPRESSOR */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1COMPRESSOR_1THRESHOLD(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_COMPRESSOR_THRESHOLD;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1COMPRESSOR_1ATTACK(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_COMPRESSOR_ATTACK;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1COMPRESSOR_1RELEASE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_COMPRESSOR_RELEASE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1COMPRESSOR_1GAINMAKEUP(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_COMPRESSOR_GAINMAKEUP;
}

				/* FMOD_DSP_SFXREVERB */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1DRYLEVEL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_DRYLEVEL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1ROOM(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_ROOM;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1ROOMHF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_ROOMHF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1ROOMROLLOFFFACTOR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_ROOMROLLOFFFACTOR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1DECAYTIME(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_DECAYTIME;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1DECAYHFRATIO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_DECAYHFRATIO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1REFLECTIONSLEVEL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_REFLECTIONSLEVEL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1REFLECTIONSDELAY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_REFLECTIONSDELAY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1REVERBLEVEL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_REVERBLEVEL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1REVERBDELAY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_REVERBDELAY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1DIFFUSION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_DIFFUSION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1DENSITY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_DENSITY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1HFREFERENCE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_HFREFERENCE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1ROOMLF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_ROOMLF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1SFXREVERB_1LFREFERENCE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_SFXREVERB_LFREFERENCE;
}

				/* FMOD_DSP_LOWPASS_SIMPLE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1LOWPASS_1SIMPLE_1CUTOFF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_LOWPASS_SIMPLE_CUTOFF;
}

				/* FMOD_RESULT */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OK(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OK;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1ALREADYLOCKED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_ALREADYLOCKED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1BADCOMMAND(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_BADCOMMAND;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1CDDA_1DRIVERS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_CDDA_DRIVERS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1CDDA_1INIT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_CDDA_INIT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1CDDA_1INVALID_1DEVICE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_CDDA_INVALID_DEVICE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1CDDA_1NOAUDIO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_CDDA_NOAUDIO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1CDDA_1NODEVICES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_CDDA_NODEVICES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1CDDA_1NODISC(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_CDDA_NODISC;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1CDDA_1READ(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_CDDA_READ;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1CHANNEL_1ALLOC(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_CHANNEL_ALLOC;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1CHANNEL_1STOLEN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_CHANNEL_STOLEN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1COM(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_COM;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1DMA(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_DMA;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1DSP_1CONNECTION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_DSP_CONNECTION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1DSP_1FORMAT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_DSP_FORMAT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1DSP_1NOTFOUND(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_DSP_NOTFOUND;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1DSP_1RUNNING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_DSP_RUNNING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1DSP_1TOOMANYCONNECTIONS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_DSP_TOOMANYCONNECTIONS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1FILE_1BAD(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_FILE_BAD;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1FILE_1COULDNOTSEEK(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_FILE_COULDNOTSEEK;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1FILE_1DISKEJECTED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_FILE_DISKEJECTED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1FILE_1EOF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_FILE_EOF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1FILE_1NOTFOUND(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_FILE_NOTFOUND;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1FILE_1UNWANTED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_FILE_UNWANTED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1FORMAT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_FORMAT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1HTTP(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_HTTP;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1HTTP_1ACCESS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_HTTP_ACCESS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1HTTP_1PROXY_1AUTH(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_HTTP_PROXY_AUTH;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1HTTP_1SERVER_1ERROR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_HTTP_SERVER_ERROR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1HTTP_1TIMEOUT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_HTTP_TIMEOUT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1INITIALIZATION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_INITIALIZATION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1INITIALIZED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_INITIALIZED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1INTERNAL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_INTERNAL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1INVALID_1ADDRESS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_INVALID_ADDRESS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1INVALID_1FLOAT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_INVALID_FLOAT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1INVALID_1HANDLE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_INVALID_HANDLE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1INVALID_1PARAM(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_INVALID_PARAM;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1INVALID_1SPEAKER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_INVALID_SPEAKER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1INVALID_1VECTOR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_INVALID_VECTOR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1IRX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_IRX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1MAXAUDIBLE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_MAXAUDIBLE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1MEMORY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_MEMORY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1MEMORY_1CANTPOINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_MEMORY_CANTPOINT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1MEMORY_1IOP(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_MEMORY_IOP;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1MEMORY_1SRAM(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_MEMORY_SRAM;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1NEEDS2D(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_NEEDS2D;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1NEEDS3D(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_NEEDS3D;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1NEEDSHARDWARE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_NEEDSHARDWARE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1NEEDSSOFTWARE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_NEEDSSOFTWARE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1NET_1CONNECT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_NET_CONNECT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1NET_1SOCKET_1ERROR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_NET_SOCKET_ERROR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1NET_1URL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_NET_URL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1NET_1WOULD_1BLOCK(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_NET_WOULD_BLOCK;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1NOTREADY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_NOTREADY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1OUTPUT_1ALLOCATED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_OUTPUT_ALLOCATED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1OUTPUT_1CREATEBUFFER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_OUTPUT_CREATEBUFFER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1OUTPUT_1DRIVERCALL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_OUTPUT_DRIVERCALL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1OUTPUT_1ENUMERATION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_OUTPUT_ENUMERATION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1OUTPUT_1FORMAT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_OUTPUT_FORMAT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1OUTPUT_1INIT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_OUTPUT_INIT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1OUTPUT_1NOHARDWARE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_OUTPUT_NOHARDWARE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1OUTPUT_1NOSOFTWARE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_OUTPUT_NOSOFTWARE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1PAN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_PAN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1PLUGIN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_PLUGIN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1PLUGIN_1INSTANCES(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_PLUGIN_INSTANCES;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1PLUGIN_1MISSING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_PLUGIN_MISSING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1PLUGIN_1RESOURCE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_PLUGIN_RESOURCE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1RECORD(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_RECORD;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1REVERB_1INSTANCE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_REVERB_INSTANCE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1SUBSOUND_1ALLOCATED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_SUBSOUND_ALLOCATED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1SUBSOUND_1CANTMOVE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_SUBSOUND_CANTMOVE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1SUBSOUND_1MODE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_SUBSOUND_MODE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1SUBSOUNDS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_SUBSOUNDS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1TAGNOTFOUND(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_TAGNOTFOUND;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1TOOMANYCHANNELS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_TOOMANYCHANNELS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1UNIMPLEMENTED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_UNIMPLEMENTED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1UNINITIALIZED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_UNINITIALIZED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1UNSUPPORTED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_UNSUPPORTED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1UPDATE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_UPDATE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1VERSION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_VERSION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1EVENT_1FAILED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_EVENT_FAILED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1EVENT_1INFOONLY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_EVENT_INFOONLY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1EVENT_1INTERNAL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_EVENT_INTERNAL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1EVENT_1MAXSTREAMS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_EVENT_MAXSTREAMS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1EVENT_1MISMATCH(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_EVENT_MISMATCH;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1EVENT_1NAMECONFLICT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_EVENT_NAMECONFLICT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1ERR_1EVENT_1NOTFOUND(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_ERR_EVENT_NOTFOUND;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1RESULT_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_RESULT_FORCEINT;
}

				/* FMOD_OUTPUTTYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1AUTODETECT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_AUTODETECT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1UNKNOWN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_UNKNOWN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1NOSOUND(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_NOSOUND;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1WAVWRITER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_WAVWRITER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1NOSOUND_1NRT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_NOSOUND_NRT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1WAVWRITER_1NRT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_WAVWRITER_NRT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1DSOUND(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_DSOUND;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1WINMM(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_WINMM;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1OPENAL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_OPENAL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1WASAPI(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_WASAPI;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1ASIO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_ASIO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1OSS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_OSS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1ALSA(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_ALSA;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1ESD(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_ESD;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1SOUNDMANAGER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_SOUNDMANAGER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1COREAUDIO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_COREAUDIO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1XBOX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_XBOX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1PS2(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_PS2;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1PS3(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_PS3;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1GC(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_GC;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1XBOX360(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_XBOX360;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1PSP(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_PSP;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1WII(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_WII;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OUTPUTTYPE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OUTPUTTYPE_FORCEINT;
}

				/* FMOD_SPEAKERMODE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_1RAW(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_RAW;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_1MONO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_MONO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_1STEREO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_STEREO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_1QUAD(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_QUAD;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_1SURROUND(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_SURROUND;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_15POINT1(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_5POINT1;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_17POINT1(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_7POINT1;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_1PROLOGIC(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_PROLOGIC;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMODE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMODE_FORCEINT;
}

				/* FMOD_SPEAKER */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1FRONT_1LEFT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_FRONT_LEFT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1FRONT_1RIGHT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_FRONT_RIGHT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1FRONT_1CENTER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_FRONT_CENTER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1LOW_1FREQUENCY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_LOW_FREQUENCY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1BACK_1LEFT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_BACK_LEFT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1BACK_1RIGHT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_BACK_RIGHT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1SIDE_1LEFT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_SIDE_LEFT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1SIDE_1RIGHT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_SIDE_RIGHT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1MONO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_MONO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1NULL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_NULL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1SBL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_SBL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1SBR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_SBR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKER_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKER_FORCEINT;
}

				/* FMOD_PLUGINTYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1PLUGINTYPE_1OUTPUT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_PLUGINTYPE_OUTPUT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1PLUGINTYPE_1CODEC(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_PLUGINTYPE_CODEC;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1PLUGINTYPE_1DSP(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_PLUGINTYPE_DSP;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1PLUGINTYPE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_PLUGINTYPE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1PLUGINTYPE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_PLUGINTYPE_FORCEINT;
}

				/* FMOD_SOUND_TYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1UNKNOWN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_UNKNOWN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1AAC(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_AAC;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1AIFF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_AIFF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1ASF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_ASF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1AT3(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_AT3;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1CDDA(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_CDDA;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1DLS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_DLS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1FLAC(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_FLAC;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1FSB(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_FSB;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1GCADPCM(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_GCADPCM;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1IT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_IT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1MIDI(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_MIDI;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1MOD(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_MOD;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1MPEG(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_MPEG;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1OGGVORBIS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_OGGVORBIS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1PLAYLIST(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_PLAYLIST;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1RAW(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_RAW;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1S3M(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_S3M;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1SF2(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_SF2;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1USER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_USER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1WAV(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_WAV;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1XM(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_XM;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1XMA(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_XMA;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1VAG(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_VAG;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1TYPE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_TYPE_FORCEINT;
}

				/* FMOD_SOUND_FORMAT */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1NONE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_NONE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1PCM8(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_PCM8;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1PCM16(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_PCM16;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1PCM24(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_PCM24;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1PCM32(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_PCM32;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1PCMFLOAT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_PCMFLOAT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1GCADPCM(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_GCADPCM;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1IMAADPCM(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_IMAADPCM;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1VAG(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_VAG;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1XMA(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_XMA;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1MPEG(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_MPEG;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUND_1FORMAT_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUND_FORMAT_FORCEINT;
}

				/* FMOD_OPENSTATE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OPENSTATE_1READY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OPENSTATE_READY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OPENSTATE_1LOADING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OPENSTATE_LOADING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OPENSTATE_1ERROR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OPENSTATE_ERROR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OPENSTATE_1CONNECTING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OPENSTATE_CONNECTING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OPENSTATE_1BUFFERING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OPENSTATE_BUFFERING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OPENSTATE_1SEEKING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OPENSTATE_SEEKING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OPENSTATE_1STREAMING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OPENSTATE_STREAMING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OPENSTATE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OPENSTATE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1OPENSTATE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_OPENSTATE_FORCEINT;
}

				/* FMOD_SOUNDGROUP_BEHAVIOR */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUNDGROUP_1BEHAVIOR_1FAIL(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUNDGROUP_BEHAVIOR_FAIL;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUNDGROUP_1BEHAVIOR_1MUTE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUNDGROUP_BEHAVIOR_MUTE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUNDGROUP_1BEHAVIOR_1STEALLOWEST(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUNDGROUP_BEHAVIOR_STEALLOWEST;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUNDGROUP_1BEHAVIOR_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUNDGROUP_BEHAVIOR_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SOUNDGROUP_1BEHAVIOR_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SOUNDGROUP_BEHAVIOR_FORCEINT;
}

				/* FMOD_CHANNEL_CALLBACKTYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1CHANNEL_1CALLBACKTYPE_1END(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_CHANNEL_CALLBACKTYPE_END;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1CHANNEL_1CALLBACKTYPE_1VIRTUALVOICE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_CHANNEL_CALLBACKTYPE_VIRTUALVOICE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1CHANNEL_1CALLBACKTYPE_1SYNCPOINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_CHANNEL_CALLBACKTYPE_SYNCPOINT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1CHANNEL_1CALLBACKTYPE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_CHANNEL_CALLBACKTYPE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1CHANNEL_1CALLBACKTYPE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_CHANNEL_CALLBACKTYPE_FORCEINT;
}

				/* FMOD_SYSTEM_CALLBACKTYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SYSTEM_1CALLBACKTYPE_1DEVICELISTCHANGED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SYSTEM_CALLBACKTYPE_DEVICELISTCHANGED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SYSTEM_1CALLBACKTYPE_1MEMORYALLOCATIONFAILED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SYSTEM_CALLBACKTYPE_MEMORYALLOCATIONFAILED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SYSTEM_1CALLBACKTYPE_1THREADCREATED(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SYSTEM_CALLBACKTYPE_THREADCREATED;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SYSTEM_1CALLBACKTYPE_1BADDSPCONNECTION(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SYSTEM_CALLBACKTYPE_BADDSPCONNECTION;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SYSTEM_1CALLBACKTYPE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SYSTEM_CALLBACKTYPE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SYSTEM_1CALLBACKTYPE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SYSTEM_CALLBACKTYPE_FORCEINT;
}

				/* FMOD_DSP_FFT_WINDOW */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FFT_1WINDOW_1RECT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FFT_WINDOW_RECT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FFT_1WINDOW_1TRIANGLE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FFT_WINDOW_TRIANGLE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FFT_1WINDOW_1HAMMING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FFT_WINDOW_HAMMING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FFT_1WINDOW_1HANNING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FFT_WINDOW_HANNING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FFT_1WINDOW_1BLACKMAN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FFT_WINDOW_BLACKMAN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FFT_1WINDOW_1BLACKMANHARRIS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FFT_WINDOW_BLACKMANHARRIS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FFT_1WINDOW_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FFT_WINDOW_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1FFT_1WINDOW_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_FFT_WINDOW_FORCEINT;
}

				/* FMOD_DSP_RESAMPLER */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1RESAMPLER_1NOINTERP(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_RESAMPLER_NOINTERP;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1RESAMPLER_1LINEAR(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_RESAMPLER_LINEAR;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1RESAMPLER_1CUBIC(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_RESAMPLER_CUBIC;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1RESAMPLER_1SPLINE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_RESAMPLER_SPLINE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1RESAMPLER_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_RESAMPLER_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DSP_1RESAMPLER_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DSP_RESAMPLER_FORCEINT;
}

				/* FMOD_TAGTYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1UNKNOWN(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_UNKNOWN;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1ID3V1(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_ID3V1;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1ID3V2(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_ID3V2;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1VORBISCOMMENT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_VORBISCOMMENT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1SHOUTCAST(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_SHOUTCAST;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1ICECAST(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_ICECAST;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1ASF(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_ASF;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1MIDI(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_MIDI;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1PLAYLIST(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_PLAYLIST;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1FMOD(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_FMOD;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1USER(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_USER;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGTYPE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGTYPE_FORCEINT;
}

				/* FMOD_TAGDATATYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1BINARY(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_BINARY;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1INT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_INT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1FLOAT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_FLOAT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1STRING(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_STRING;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1STRING_1UTF16(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_STRING_UTF16;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1STRING_1UTF16BE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_STRING_UTF16BE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1STRING_1UTF8(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_STRING_UTF8;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1CDTOC(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_CDTOC;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1TAGDATATYPE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_TAGDATATYPE_FORCEINT;
}

				/* FMOD_DELAYTYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DELAYTYPE_1END_1MS(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DELAYTYPE_END_MS;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DELAYTYPE_1DSPCLOCK_1START(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DELAYTYPE_DSPCLOCK_START;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DELAYTYPE_1DSPCLOCK_1END(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DELAYTYPE_DSPCLOCK_END;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DELAYTYPE_1MAX(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DELAYTYPE_MAX;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1DELAYTYPE_1FORCEINT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_DELAYTYPE_FORCEINT;
}

				/* FMOD_SPEAKERMAPTYPE */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMAPTYPE_1DEFAULT(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMAPTYPE_DEFAULT;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMAPTYPE_1ALLMONO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMAPTYPE_ALLMONO;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1SPEAKERMAPTYPE_1ALLSTEREO(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_SPEAKERMAPTYPE_ALLSTEREO;
}

				/* FMOD_CHANNELINDEX */

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1CHANNEL_1FREE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_CHANNEL_FREE;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Enumerations_EnumerationJNI_get_1FMOD_1CHANNEL_1REUSE(JNIEnv *java_env, jclass jcls) {
	return (jint)FMOD_CHANNEL_REUSE;
}


