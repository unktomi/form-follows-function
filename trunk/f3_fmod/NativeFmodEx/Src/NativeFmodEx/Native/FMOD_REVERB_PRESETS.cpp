/**
 * 							NativeFmodEx Project
 *
 * Do you want to use FMOD Ex API (www.fmod.org) with the Java language ? I've created NativeFmodEx for you.
 * Copyright © 2005-2007 Jérôme JOUVIE (Jouvieje)
 *
 * Created on 23 feb. 2005
 * @version file v1.0.0
 * @author Jérôme JOUVIE (Jouvieje)
 *
 *
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * My web sites :
 * 		http://jerome.jouvie.free.fr/
 * 		http://topresult.tomato.co.uk/~jerome/
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

#include "fmod.h"
#include "org_jouvieje_FmodEx_Structures_StructureJNI.h"

FMOD_REVERB_PROPERTIES *copyPreset(FMOD_REVERB_PROPERTIES preset) {
	FMOD_REVERB_PROPERTIES *prop = new FMOD_REVERB_PROPERTIES();

	prop->Instance = preset.Instance;
	prop->Environment = preset.Environment;
	prop->EnvSize = preset.EnvSize;
	prop->EnvDiffusion = preset.EnvDiffusion;
	prop->Room = preset.Room;
	prop->RoomHF = preset.RoomHF;
	prop->RoomLF = preset.RoomLF;
	prop->DecayTime = preset.DecayTime;
	prop->DecayHFRatio = preset.DecayHFRatio;
	prop->DecayLFRatio = preset.DecayLFRatio;
	prop->Reflections = preset.Reflections;
	prop->ReflectionsDelay = preset.ReflectionsDelay;
	prop->ReflectionsPan[0] = preset.ReflectionsPan[0];
	prop->ReflectionsPan[1] = preset.ReflectionsPan[1];
	prop->ReflectionsPan[2] = preset.ReflectionsPan[2];
	prop->Reverb = preset.Reverb;
	prop->ReverbDelay = preset.ReverbDelay;
	prop->ReverbPan[0] = preset.ReverbPan[0];
	prop->ReverbPan[1] = preset.ReverbPan[1];
	prop->ReverbPan[2] = preset.ReverbPan[2];
	prop->EchoTime = preset.EchoTime;
	prop->EchoDepth = preset.EchoDepth;
	prop->ModulationTime = preset.ModulationTime;
	prop->ModulationDepth = preset.ModulationDepth;
	prop->AirAbsorptionHF = preset.AirAbsorptionHF;
	prop->HFReference = preset.HFReference;
	prop->LFReference = preset.LFReference;
	prop->RoomRolloffFactor = preset.RoomRolloffFactor;
	prop->Diffusion = preset.Diffusion;
	prop->Density = preset.Density;
	prop->Flags = preset.Flags;

	return prop;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1OFF(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_OFF;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1GENERIC(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_GENERIC;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PADDEDCELL(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PADDEDCELL;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1ROOM(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_ROOM;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1BATHROOM(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_BATHROOM;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1LIVINGROOM(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_LIVINGROOM;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1STONEROOM(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_STONEROOM;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1AUDITORIUM(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_AUDITORIUM;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1CONCERTHALL(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_CONCERTHALL;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1CAVE(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_CAVE;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1ARENA(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_ARENA;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1HANGAR(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_HANGAR;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1CARPETTEDHALLWAY(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_CARPETTEDHALLWAY;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1HALLWAY(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_HALLWAY;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1STONECORRIDOR(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_STONECORRIDOR;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1ALLEY(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_ALLEY;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1FOREST(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_FOREST;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1CITY(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_CITY;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1MOUNTAINS(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_MOUNTAINS;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1QUARRY(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_QUARRY;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PLAIN(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PLAIN;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PARKINGLOT(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PARKINGLOT;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1SEWERPIPE(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_SEWERPIPE;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1UNDERWATER(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_UNDERWATER;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1DRUGGED(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_DRUGGED;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1DIZZY(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_DIZZY;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PSYCHOTIC(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PSYCHOTIC;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PS2_1ROOM(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PS2_ROOM;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PS2_1STUDIO_1A(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PS2_STUDIO_A;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PS2_1STUDIO_1B(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PS2_STUDIO_B;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PS2_1STUDIO_1C(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PS2_STUDIO_C;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PS2_1HALL(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PS2_HALL;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PS2_1SPACE(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PS2_SPACE;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PS2_1ECHO(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PS2_ECHO;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PS2_1DELAY(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PS2_DELAY;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Structures_StructureJNI_get_1FMOD_1PRESET_1PS2_1PIPE(JNIEnv *jenv, jclass jcls) {
	FMOD_REVERB_PROPERTIES preset = FMOD_PRESET_PS2_PIPE;
	FMOD_REVERB_PROPERTIES *prop = copyPreset(preset);

	long jresult = 0;
	*(FMOD_REVERB_PROPERTIES **)&jresult = prop;
	return (jlong)jresult;
}
