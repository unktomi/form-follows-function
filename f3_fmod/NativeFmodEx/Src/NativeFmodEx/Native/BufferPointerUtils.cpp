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

#include "Utils.h"
#include "JavaObject.h"
#include "org_jouvieje_FmodEx_Misc_PointerUtilsJNI.h"
#include "org_jouvieje_FmodEx_Misc_BufferUtilsJNI.h"


									/*Buffer*/

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Misc_BufferUtilsJNI_getBufferAddress(JNIEnv *jenv, jclass jcls, jobject jarg1, jint jarg1_) {
	void *arg1 = 0;
	if(jarg1)
		arg1 = (char *)jenv->GetDirectBufferAddress(jarg1)+jarg1_;

	long jresult/* = 0*/;
	*(void **)&jresult = arg1;
	return (jlong)jresult;
}

JNIEXPORT jobject JNICALL Java_org_jouvieje_FmodEx_Misc_BufferUtilsJNI_newDirectByteBuffer(JNIEnv *jenv, jclass jcls, jlong jarg1, jlong jarg2) {
	if(!jarg1) {
		ThrowException(jenv, NullPointerException, NULL_BUFFER);
		return 0;
	}
	void * arg1 = *(void **)&jarg1;
	return jenv->NewDirectByteBuffer(arg1, jarg2);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Misc_BufferUtilsJNI_writeNullTerminal(JNIEnv *jenv, jclass jcls, jobject jarg1, jint jarg1_) {
	if(!jarg1) {
		ThrowException(jenv, NullPointerException, NULL_BUFFER);
		return;
	}
	char *arg1 = (char *)jenv->GetDirectBufferAddress(jarg1)+jarg1_;
	if(arg1)
		strcpy(arg1, "");
}

									/*PointerUtils*/

JNIEXPORT jstring JNICALL Java_org_jouvieje_FmodEx_Misc_PointerUtilsJNI_Pointer_1toString(JNIEnv *jenv, jclass jcls, jlong jarg1) {
	if(!jarg1) {
		ThrowException(jenv, NullPointerException, NULL_POINTER);
		return 0;
	}
	char *arg1 = *(char **)&jarg1;
	jstring jresult = jenv->NewStringUTF(arg1);
	return jresult;
}

JNIEXPORT jint JNICALL Java_org_jouvieje_FmodEx_Misc_PointerUtilsJNI_asInt(JNIEnv *jenv, jclass jcls, jlong jarg1) {
	if(!jarg1) {
		ThrowException(jenv, NullPointerException, NULL_POINTER);
		return 0;
	}
	int jreturn = *(int *)&jarg1;
	return (jint)jreturn;
}

JNIEXPORT jfloat JNICALL Java_org_jouvieje_FmodEx_Misc_PointerUtilsJNI_asFloat(JNIEnv *jenv, jclass jcls, jlong jarg1) {
	if(!jarg1) {
		ThrowException(jenv, NullPointerException, NULL_POINTER);
		return 0;
	}
	float jreturn = *(float *)&jarg1;
	return (jfloat)jreturn;
}

JNIEXPORT jdouble JNICALL Java_org_jouvieje_FmodEx_Misc_PointerUtilsJNI_asDouble(JNIEnv *jenv, jclass jcls, jlong jarg1) {
	if(!jarg1) {
		ThrowException(jenv, NullPointerException, NULL_POINTER);
		return 0;
	}
	double jreturn = *(double *)&jarg1;
	return (jdouble)jreturn;
}

									/*ObjectPointer*/

class ObjectPointer : public JavaObject {
public:
	ObjectPointer(JNIEnv *jenv, jobject obj) : JavaObject(jenv){
		object = 0;
		setObject(jenv, obj);
	}
	~ObjectPointer(){
		JNIEnv *jenv = acquire_jenv();
		deleteObject(jenv);
	}

	void setObject(JNIEnv *jenv, jobject obj) {
		deleteObject(jenv);
		object = jenv->NewGlobalRef(obj);
	}
	jobject getObject() {
		return object;
	}

private:
	jobject object;
	void deleteObject(JNIEnv *jenv) {
		if(object) {
			jenv->DeleteGlobalRef(object);
			object = 0;
		}
	}
};

JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_Misc_PointerUtilsJNI_new_1ObjectPointer(JNIEnv *jenv, jclass jcls, jobject jarg1) {
	ObjectPointer *result = new ObjectPointer(jenv, jarg1);

    long jresult = 0 ;
    *(ObjectPointer **)&jresult = result;
    return (jlong)jresult;
}

JNIEXPORT jobject JNICALL Java_org_jouvieje_FmodEx_Misc_PointerUtilsJNI_get_1ObjectPointer(JNIEnv *jenv, jclass jcls, jlong jarg1) {
    ObjectPointer *arg1 = *(ObjectPointer **)&jarg1;
    if (!arg1) {
        ThrowException(jenv, NullPointerException, NULL_OBJECT_POINTER);
        return 0;
    }
	return arg1->getObject();
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Misc_PointerUtilsJNI_set_1ObjectPointer(JNIEnv *jenv, jclass jcls, jlong jarg1, jobject jarg2) {
    ObjectPointer *arg1 = *(ObjectPointer **)&jarg1;
    if (!arg1) {
        ThrowException(jenv, NullPointerException, NULL_OBJECT_POINTER);
        return ;
    }
	arg1->setObject(jenv, jarg2);
}

JNIEXPORT void JNICALL Java_org_jouvieje_FmodEx_Misc_PointerUtilsJNI_delete_1ObjectPointer(JNIEnv *jenv, jclass jcls, jlong jarg1) {
	delete *(ObjectPointer **)&jarg1;
}
