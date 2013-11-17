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
#ifndef Callback_Manager_H_
#define Callback_Manager_H_

#include "Utils.h"
#include "Pointer.h"
#include "fmod.h"
#include "fmod.hpp"
#include "fmod_codec.h"
#include "fmod_dsp.h"
#include "fmod_output.h"
#include "CallbackManager.h"

extern void attachJavaVM(JNIEnv *java_env);

FMOD_RESULT F_CALLBACK FMOD_CODEC_OPENCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, FMOD_MODE usermode, FMOD_CREATESOUNDEXINFO * userexinfo);
FMOD_RESULT F_CALLBACK FMOD_CODEC_CLOSECALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state);
FMOD_RESULT F_CALLBACK FMOD_CODEC_READCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, void * buffer, unsigned int sizebytes, unsigned int * bytesread);
FMOD_RESULT F_CALLBACK FMOD_CODEC_GETLENGTHCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, unsigned int * length, FMOD_TIMEUNIT lengthtype);
FMOD_RESULT F_CALLBACK FMOD_CODEC_SETPOSITIONCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, int subsound, unsigned int position, FMOD_TIMEUNIT postype);
FMOD_RESULT F_CALLBACK FMOD_CODEC_GETPOSITIONCALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, unsigned int * position, FMOD_TIMEUNIT postype);
FMOD_RESULT F_CALLBACK FMOD_CODEC_SOUNDCREATECALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, int subsound, FMOD_SOUND * sound);
FMOD_RESULT F_CALLBACK FMOD_CODEC_METADATACALLBACK_BRIDGE(FMOD_CODEC_STATE * codec_state, FMOD_TAGTYPE tagtype, char * name, void * data, unsigned int datalen, FMOD_TAGDATATYPE datatype, int unique);
FMOD_RESULT F_CALLBACK FMOD_CODEC_GETWAVEFORMAT_BRIDGE(FMOD_CODEC_STATE * codec_state, int index, FMOD_CODEC_WAVEFORMAT * waveformat);
FMOD_RESULT F_CALLBACK FMOD_DSP_CREATECALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state);
FMOD_RESULT F_CALLBACK FMOD_DSP_RELEASECALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state);
FMOD_RESULT F_CALLBACK FMOD_DSP_RESETCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state);
FMOD_RESULT F_CALLBACK FMOD_DSP_READCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, float * inbuffer, float * outbuffer, unsigned int length, int inchannels, int outchannels);
FMOD_RESULT F_CALLBACK FMOD_DSP_SETPOSITIONCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, unsigned int pos);
FMOD_RESULT F_CALLBACK FMOD_DSP_SETPARAMCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, int index, float value);
FMOD_RESULT F_CALLBACK FMOD_DSP_GETPARAMCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, int index, float * value, char * valuestr);
FMOD_RESULT F_CALLBACK FMOD_DSP_DIALOGCALLBACK_BRIDGE(FMOD_DSP_STATE * dsp_state, void * hwnd, int show);
FMOD_RESULT F_CALLBACK FMOD_SYSTEM_CALLBACK_BRIDGE(FMOD_SYSTEM * system, FMOD_SYSTEM_CALLBACKTYPE type, void * commanddata1, void * commanddata2);
FMOD_RESULT F_CALLBACK FMOD_CHANNEL_CALLBACK_BRIDGE(FMOD_CHANNEL * channel, FMOD_CHANNEL_CALLBACKTYPE type, void * commanddata1, void * commanddata2);
FMOD_RESULT F_CALLBACK FMOD_SOUND_NONBLOCKCALLBACK_BRIDGE(FMOD_SOUND * sound, FMOD_RESULT result);
FMOD_RESULT F_CALLBACK FMOD_SOUND_PCMREADCALLBACK_BRIDGE(FMOD_SOUND * sound, void * data, unsigned int datalen);
FMOD_RESULT F_CALLBACK FMOD_SOUND_PCMSETPOSCALLBACK_BRIDGE(FMOD_SOUND * sound, int subsound, unsigned int position, FMOD_TIMEUNIT postype);
FMOD_RESULT F_CALLBACK FMOD_FILE_OPENCALLBACK_BRIDGE(const char * name, int unicode, unsigned int * filesize, void ** handle, void ** userdata);
FMOD_RESULT F_CALLBACK FMOD_FILE_CLOSECALLBACK_BRIDGE(void * handle, void * userdata);
FMOD_RESULT F_CALLBACK FMOD_FILE_READCALLBACK_BRIDGE(void * handle, void * buffer, unsigned int sizebytes, unsigned int * bytesread, void * userdata);
FMOD_RESULT F_CALLBACK FMOD_FILE_SEEKCALLBACK_BRIDGE(void * handle, unsigned int pos, void * userdata);
void * F_CALLBACK FMOD_MEMORY_ALLOCCALLBACK_BRIDGE(unsigned int size, FMOD_MEMORY_TYPE type);
void * F_CALLBACK FMOD_MEMORY_REALLOCCALLBACK_BRIDGE(void * ptr, unsigned int size, FMOD_MEMORY_TYPE type);
void F_CALLBACK FMOD_MEMORY_FREECALLBACK_BRIDGE(void * ptr, FMOD_MEMORY_TYPE type);
float F_CALLBACK FMOD_3D_ROLLOFFCALLBACK_BRIDGE(FMOD_CHANNEL * channel, float distance);

#endif

