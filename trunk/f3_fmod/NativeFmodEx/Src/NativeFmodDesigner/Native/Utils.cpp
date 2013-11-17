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

const char * NULL_EVENT =							"";
const char * NULL_EVENTSYSTEM =						"";
const char * NULL_EVENTPROJECT =					"";
const char * NULL_EVENTCATEGORY =					"";
const char * NULL_EVENTGROUP =						"";
const char * NULL_EVENTPARAMETER =					"";
const char * NULL_EVENTREVERB =						"";
const char * NULL_MUSICPROMPT =						"";
const char * NULL_MUSICSYSTEM =						"";
const char * NULL_FMOD_EVENT_SYSTEMINFO =			"";
const char * NULL_FMOD_EVENT_WAVEBANKINFO =			"";
const char * NULL_FMOD_EVENT_LOADINFO =				"";
const char * NULL_FMOD_EVENT_INFO =					"";
const char * FATAL_ERROR_MESSAGE =					"An exception occures but it can't be thrown !";

void ThrowException(JNIEnv *jenv, ExceptionType type, const char *message) {
	jclass exception;
	switch(type) {
//		case CallbackException:				exception = jenv->FindClass("org/jouvieje/FmodEx/Exceptions/CallbackException"); break;
		case NullPointerException:			exception = jenv->FindClass("java/lang/NullPointerException"); break;
		case InitException:					exception = jenv->FindClass("org/jouvieje/FmodEx/Exceptions/InitException"); break;
		case RuntimeException:				exception = jenv->FindClass("java/lang/RuntimeException"); break;
		case OutOfMemoryError:				exception = jenv->FindClass("java/lang/OutOfMemoryError"); break;
//		case IndexOutOfBoundsException:		exception = jenv->FindClass("java/lang/IndexOutOfBoundsException"); break;
//		case IllegalArgumentException:		exception = jenv->FindClass("java/lang/IllegalArgumentException"); break;
//		case ArithmeticException:			exception = jenv->FindClass("java/lang/ArithmeticException"); break;
//		case IOException:					exception = jenv->FindClass("java/io/IOException"); break;
//		case UnknownError:
		default:							exception = jenv->FindClass("java/lang/UnknownError");
	}

	if(exception) {
		jenv->ThrowNew(exception, message);
		jenv->DeleteLocalRef(exception);
	}
}

jclass stringClass = 0;
jclass getStringClass(JNIEnv *jenv) {
	if(!stringClass) {
		stringClass = (jclass)jenv->NewGlobalRef(jenv->FindClass("java/lang/String"));
	}
	return stringClass;
}
jmethodID getBytesId = 0;
jmethodID getGetBytesId(JNIEnv *jenv) {
	if(getBytesId == 0) {
		getBytesId = jenv->GetMethodID(getStringClass(jenv), "getBytes", "()[B");
	}
	return getBytesId;
}

char *getByteArrayElements(JNIEnv *jenv, jbyteArray array) {
	if(array)
	{
		const jsize length = jenv->GetArrayLength(array);
		const jbyte *chars = jenv->GetByteArrayElements(array, 0);
		char *copy = new char[length+1];		//Allocate memory

		for(int i = 0; i < length; i++) {
			copy[i] = (char)chars[i];
		}
		copy[length] = 0;		//End of the string
		jenv->ReleaseByteArrayElements(array, (jbyte *)chars, 0);

		return copy;
	}
	return 0;
}

void releaseByteArrayElements(JNIEnv *jenv, jbyteArray array, const char *chars) {
	if(chars) {
		delete [] chars;						//Deallocate memory
		chars = NULL;
	}
}

char *getStringElements(JNIEnv *jenv, jstring string) {
	if(string)
	{
		jbyteArray array = (jbyteArray)jenv->CallObjectMethod(string, getGetBytesId(jenv));
		return getByteArrayElements(jenv, array);
	}
	return 0;
}

void releaseStringElements(JNIEnv *jenv, jstring string, const char *chars) {
	if(chars) {
		delete [] chars;						//Deallocate memory
		chars = NULL;
	}
}
