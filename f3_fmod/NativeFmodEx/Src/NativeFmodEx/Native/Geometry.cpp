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

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1release(JNIEnv *java_env, jclass jcls, jlong jpointer) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->release();

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1addPolygon(JNIEnv *java_env, jclass jcls, jlong jpointer, jfloat jdirectocclusion, jfloat jreverbocclusion, jboolean jdoublesided, jint jnumvertices, jlong jvertices, jobject jpolygonindex, jlong jpolygonindex_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	float directocclusion = (float)jdirectocclusion;
	float reverbocclusion = (float)jreverbocclusion;
	bool doublesided = (bool)(jdoublesided != 0);
	int numvertices = (int)jnumvertices;
	POINTER_TYPE verticesTmp = (POINTER_TYPE)jvertices;
	FMOD_VECTOR *vertices = *(FMOD_VECTOR **)&verticesTmp;
	int *polygonindex = 0;
	if(jpolygonindex) {
		polygonindex = (int *)((char *)java_env->GetDirectBufferAddress(jpolygonindex)+jpolygonindex_);
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->addPolygon(directocclusion, reverbocclusion, doublesided, numvertices, vertices, polygonindex);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getNumPolygons(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jnumpolygons, jlong jnumpolygons_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *numpolygons = 0;
	if(jnumpolygons) {
		numpolygons = (int *)((char *)java_env->GetDirectBufferAddress(jnumpolygons)+jnumpolygons_);
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getNumPolygons(numpolygons);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getMaxPolygons(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jmaxpolygons, jlong jmaxpolygons_, jobject jmaxvertices, jlong jmaxvertices_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int *maxpolygons = 0;
	if(jmaxpolygons) {
		maxpolygons = (int *)((char *)java_env->GetDirectBufferAddress(jmaxpolygons)+jmaxpolygons_);
	}
	int *maxvertices = 0;
	if(jmaxvertices) {
		maxvertices = (int *)((char *)java_env->GetDirectBufferAddress(jmaxvertices)+jmaxvertices_);
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getMaxPolygons(maxpolygons, maxvertices);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getPolygonNumVertices(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jnumvertices, jlong jnumvertices_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	int *numvertices = 0;
	if(jnumvertices) {
		numvertices = (int *)((char *)java_env->GetDirectBufferAddress(jnumvertices)+jnumvertices_);
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getPolygonNumVertices(index, numvertices);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1setPolygonVertex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jint jvertexindex, jlong jvertex) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	int vertexindex = (int)jvertexindex;
	FMOD_VECTOR *vertex = 0;
	if(jvertex) {
		POINTER_TYPE vertexTmp = (POINTER_TYPE)jvertex;
		vertex = *(FMOD_VECTOR **)&vertexTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->setPolygonVertex(index, vertexindex, vertex);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getPolygonVertex(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jint jvertexindex, jlong jvertex) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	int vertexindex = (int)jvertexindex;
	FMOD_VECTOR *vertex = 0;
	if(jvertex) {
		POINTER_TYPE vertexTmp = (POINTER_TYPE)jvertex;
		vertex = *(FMOD_VECTOR **)&vertexTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getPolygonVertex(index, vertexindex, vertex);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1setPolygonAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jfloat jdirectocclusion, jfloat jreverbocclusion, jboolean jdoublesided) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	float directocclusion = (float)jdirectocclusion;
	float reverbocclusion = (float)jreverbocclusion;
	bool doublesided = (bool)(jdoublesided != 0);

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->setPolygonAttributes(index, directocclusion, reverbocclusion, doublesided);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getPolygonAttributes(JNIEnv *java_env, jclass jcls, jlong jpointer, jint jindex, jobject jdirectocclusion, jlong jdirectocclusion_, jobject jreverbocclusion, jlong jreverbocclusion_, jobject jdoublesided, jlong jdoublesided_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	int index = (int)jindex;
	float *directocclusion = 0;
	if(jdirectocclusion) {
		directocclusion = (float *)((char *)java_env->GetDirectBufferAddress(jdirectocclusion)+jdirectocclusion_);
	}
	float *reverbocclusion = 0;
	if(jreverbocclusion) {
		reverbocclusion = (float *)((char *)java_env->GetDirectBufferAddress(jreverbocclusion)+jreverbocclusion_);
	}
	bool *doublesided = 0;
	if(jdoublesided) {
		doublesided = (bool *)((char *)java_env->GetDirectBufferAddress(jdoublesided)+jdoublesided_);
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getPolygonAttributes(index, directocclusion, reverbocclusion, doublesided);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1setActive(JNIEnv *java_env, jclass jcls, jlong jpointer, jboolean jactive) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool active = (bool)(jactive != 0);

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->setActive(active);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getActive(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jactive, jlong jactive_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	bool *active = 0;
	if(jactive) {
		active = (bool *)((char *)java_env->GetDirectBufferAddress(jactive)+jactive_);
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getActive(active);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1setRotation(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jforward, jlong jup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
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

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->setRotation(forward, up);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getRotation(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jforward, jlong jup) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
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

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getRotation(forward, up);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1setPosition(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jposition) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *position = 0;
	if(jposition) {
		POINTER_TYPE positionTmp = (POINTER_TYPE)jposition;
		position = *(FMOD_VECTOR **)&positionTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->setPosition(position);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getPosition(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jposition) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *position = 0;
	if(jposition) {
		POINTER_TYPE positionTmp = (POINTER_TYPE)jposition;
		position = *(FMOD_VECTOR **)&positionTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getPosition(position);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1setScale(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jscale) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *scale = 0;
	if(jscale) {
		POINTER_TYPE scaleTmp = (POINTER_TYPE)jscale;
		scale = *(FMOD_VECTOR **)&scaleTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->setScale(scale);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getScale(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong jscale) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	FMOD_VECTOR *scale = 0;
	if(jscale) {
		POINTER_TYPE scaleTmp = (POINTER_TYPE)jscale;
		scale = *(FMOD_VECTOR **)&scaleTmp;
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getScale(scale);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1save(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject jdata, jlong jdata_, jobject jdatasize, jlong jdatasize_) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *data = 0;
	if(jdata) {
		data = (void *)((char *)java_env->GetDirectBufferAddress(jdata)+jdata_);
	}
	int *datasize = 0;
	if(jdatasize) {
		datasize = (int *)((char *)java_env->GetDirectBufferAddress(jdatasize)+jdatasize_);
	}

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->save(data, datasize);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1setUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jlong juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	POINTER_TYPE userdataTmp = (POINTER_TYPE)juserdata;
	void *userdata = *(void **)&userdataTmp;

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->setUserData(userdata);

	return (jint)result_;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_Geometry_1getUserData(JNIEnv *java_env, jclass jcls, jlong jpointer, jobject juserdata) {
	if(!jpointer) {
		ThrowException(java_env, NullPointerException, NULL_GEOMETRY);
		return 0;
	}
	POINTER_TYPE pointer = (POINTER_TYPE)jpointer;
	void *userdata/* = 0*/;

	FMOD_RESULT result_ = (*(FMOD::Geometry **)&pointer)->getUserData(&userdata);

	if(juserdata) {
		POINTER_TYPE newAddress/* = 0*/;
		*(void **)&newAddress = userdata;
		setPointerAddress(java_env, juserdata, newAddress);
	}
	return (jint)result_;
}


