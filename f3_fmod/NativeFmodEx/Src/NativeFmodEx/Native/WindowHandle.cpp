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

#include "WindowHandle.h"
#include "org_jouvieje_FmodEx_FmodExJNI.h"
#include "fmod.h"
#include "fmod.hpp"

#if (CURRENT_PLATFORM == NATIVE2JAVA_WIN_32) || (CURRENT_PLATFORM == NATIVE2JAVA_WIN_64)
#pragma message("Windows platform detected !")
	JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_getHwnd(JNIEnv *jenv, jclass jcls, jobject canvas) {
          return (jlong)0;
	}

	void configDialogThread(void *args) {
		ConfigDialogThreadParams *params = (ConfigDialogThreadParams *)args;
		params->isShown = false;

		FMOD_RESULT result_;
		if(params->handle) {
			result_ = (*(FMOD::DSP **)&(params->pointer))->showConfigDialog((HWND)(params->handle), params->show);
		}
		else {
			result_ = (*(FMOD::DSP **)&(params->pointer))->showConfigDialog((HWND)(params->hwndHwnd), params->show);
		}
		params->result = (jint)result_;
		params->isShown = true;

		if(params->show) {
			MSG msg;
			while(GetMessage(&msg, 0, 0, 0) > 0) {
				TranslateMessage(&msg);
				DispatchMessage(&msg);
			}
		}

		_endthread();
	}
#else
#pragma message("Linux/Mac platform detected !")
	JNIEXPORT jlong JNICALL Java_org_jouvieje_FmodEx_FmodExJNI_getHwnd(JNIEnv *jenv, jclass jcls, jobject canvas) {
		return 0;
	}
#endif
