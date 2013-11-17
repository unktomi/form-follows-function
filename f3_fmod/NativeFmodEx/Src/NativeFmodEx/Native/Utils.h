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

#ifndef UTILS_H_
#define UTILS_H_

#include "Includes.h"
#include "WindowHandle.h"
#include <iostream>

typedef enum {
//	CallbackException,
	NullPointerException,
	InitException,
	RuntimeException,
	OutOfMemoryError
//	IndexOutOfBoundsException,
//	IllegalArgumentException,
//	ArithmeticException,
//	IOException,
//	UnknownError
} ExceptionType;

extern const char * NULL_FMOD_ADVANCEDSETTINGS;
extern const char * NULL_CHANNEL;
extern const char * NULL_CHANNELGROUP;
extern const char * NULL_DSP;
extern const char * NULL_DSPCONNECTION;
extern const char * NULL_GEOMETRY;
extern const char * NULL_REVERB;
extern const char * NULL_SOUND;
extern const char * NULL_SOUNDGROUP;
extern const char * NULL_SYSTEM;
extern const char * NULL_FMOD_CDTOC;
extern const char * NULL_FMOD_CREATESOUNDEXINFO;
extern const char * NULL_FMOD_DSP_DESCRIPTION;
extern const char * NULL_FMOD_DSP_PARAMETERDESC;
extern const char * NULL_FMOD_DSP_STATE;
extern const char * NULL_FMOD_REVERB_CHANNELPROPERTIES;
extern const char * NULL_FMOD_REVERB_PROPERTIES;
extern const char * NULL_FMOD_TAG;
extern const char * NULL_FMOD_VECTOR;
extern const char * NULL_FMOD_GUID;
extern const char * NULL_OBJECT_POINTER;
extern const char * NULL_BUFFER;
extern const char * NULL_POINTER;
extern const char * NULL_FMOD_CODEC_WAVEFORMAT;
extern const char * NULL_FMOD_CODEC_STATE;
extern const char * NULL_FMOD_CODEC_DESCRIPTION;

extern const char * FATAL_ERROR_MESSAGE;

/**
 * Exception
 */
extern void ThrowException(JNIEnv *jenv, ExceptionType type, const char *message);

/**
 * String manipulation
 */
extern jclass getStringClass(JNIEnv *jenv);
extern char *getByteArrayElements(JNIEnv *jenv, jbyteArray s);
extern void releaseByteArrayElements(JNIEnv *jenv, jbyteArray jarg1, const char *chars);
extern char *getStringElements(JNIEnv *jenv, jstring string);	/* It is better to use getByteArrayElements */
extern void releaseStingElements(JNIEnv *jenv, jstring string, const char *chars);

#endif
