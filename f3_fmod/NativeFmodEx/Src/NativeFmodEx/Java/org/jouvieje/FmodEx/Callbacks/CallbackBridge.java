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

package org.jouvieje.FmodEx.Callbacks;

import org.jouvieje.FmodEx.*;
import org.jouvieje.FmodEx.Exceptions.*;
import org.jouvieje.FmodEx.Callbacks.*;
import org.jouvieje.FmodEx.*;
import org.jouvieje.FmodEx.Defines.*;
import org.jouvieje.FmodEx.Enumerations.*;
import org.jouvieje.FmodEx.Structures.*;
import java.nio.*;
import org.jouvieje.FmodEx.Misc.*;
import org.jouvieje.FmodEx.System;

class CallbackBridge extends Pointer
{
	protected final static int NB_CALLBACKS = 30;

	public static int FMOD_CODEC_OPENCALLBACK_BRIDGE(long codec_state, int usermode, long userexinfo)
	{
		FMOD_CODEC_OPENCALLBACK callback = (FMOD_CODEC_OPENCALLBACK)CallbackManager.getCallback(0, codec_state, true);
		FMOD_RESULT javaResult = callback.FMOD_CODEC_OPENCALLBACK(codec_state == 0 ? null : FMOD_CODEC_STATE.createView(Pointer.newPointer(codec_state)), usermode, userexinfo == 0 ? null : FMOD_CREATESOUNDEXINFO.createView(Pointer.newPointer(userexinfo)));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_CODEC_CLOSECALLBACK_BRIDGE(long codec_state)
	{
		FMOD_CODEC_CLOSECALLBACK callback = (FMOD_CODEC_CLOSECALLBACK)CallbackManager.getCallback(1, codec_state, false);
		FMOD_RESULT javaResult = callback.FMOD_CODEC_CLOSECALLBACK(codec_state == 0 ? null : FMOD_CODEC_STATE.createView(Pointer.newPointer(codec_state)));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_CODEC_READCALLBACK_BRIDGE(long codec_state, ByteBuffer buffer, int sizebytes, ByteBuffer bytesread)
	{
		FMOD_CODEC_READCALLBACK callback = (FMOD_CODEC_READCALLBACK)CallbackManager.getCallback(2, codec_state, false);
		if(buffer != null) {
			buffer.order(ByteOrder.nativeOrder());
		}
		if(bytesread != null) {
			bytesread.order(ByteOrder.nativeOrder());
		}
		FMOD_RESULT javaResult = callback.FMOD_CODEC_READCALLBACK(codec_state == 0 ? null : FMOD_CODEC_STATE.createView(Pointer.newPointer(codec_state)), buffer, sizebytes, bytesread == null ? null : bytesread.asIntBuffer());
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_CODEC_GETLENGTHCALLBACK_BRIDGE(long codec_state, ByteBuffer length, int lengthtype)
	{
		FMOD_CODEC_GETLENGTHCALLBACK callback = (FMOD_CODEC_GETLENGTHCALLBACK)CallbackManager.getCallback(3, codec_state, false);
		if(length != null) {
			length.order(ByteOrder.nativeOrder());
		}
		FMOD_RESULT javaResult = callback.FMOD_CODEC_GETLENGTHCALLBACK(codec_state == 0 ? null : FMOD_CODEC_STATE.createView(Pointer.newPointer(codec_state)), length == null ? null : length.asIntBuffer(), lengthtype);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_CODEC_SETPOSITIONCALLBACK_BRIDGE(long codec_state, int subsound, int position, int postype)
	{
		FMOD_CODEC_SETPOSITIONCALLBACK callback = (FMOD_CODEC_SETPOSITIONCALLBACK)CallbackManager.getCallback(4, codec_state, false);
		FMOD_RESULT javaResult = callback.FMOD_CODEC_SETPOSITIONCALLBACK(codec_state == 0 ? null : FMOD_CODEC_STATE.createView(Pointer.newPointer(codec_state)), subsound, position, postype);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_CODEC_GETPOSITIONCALLBACK_BRIDGE(long codec_state, ByteBuffer position, int postype)
	{
		FMOD_CODEC_GETPOSITIONCALLBACK callback = (FMOD_CODEC_GETPOSITIONCALLBACK)CallbackManager.getCallback(5, codec_state, false);
		if(position != null) {
			position.order(ByteOrder.nativeOrder());
		}
		FMOD_RESULT javaResult = callback.FMOD_CODEC_GETPOSITIONCALLBACK(codec_state == 0 ? null : FMOD_CODEC_STATE.createView(Pointer.newPointer(codec_state)), position == null ? null : position.asIntBuffer(), postype);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_CODEC_SOUNDCREATECALLBACK_BRIDGE(long codec_state, int subsound, long sound)
	{
		FMOD_CODEC_SOUNDCREATECALLBACK callback = (FMOD_CODEC_SOUNDCREATECALLBACK)CallbackManager.getCallback(6, codec_state, false);
		FMOD_RESULT javaResult = callback.FMOD_CODEC_SOUNDCREATECALLBACK(codec_state == 0 ? null : FMOD_CODEC_STATE.createView(Pointer.newPointer(codec_state)), subsound, sound == 0 ? null : Sound.createView(Pointer.newPointer(sound)));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_CODEC_METADATACALLBACK_BRIDGE(long codec_state, int tagtype, ByteBuffer name, long data, int datalen, int datatype, int unique)
	{
		FMOD_CODEC_METADATACALLBACK callback = (FMOD_CODEC_METADATACALLBACK)CallbackManager.getCallback(7, codec_state, false);
		if(name != null) {
			name.order(ByteOrder.nativeOrder());
		}
		FMOD_RESULT javaResult = callback.FMOD_CODEC_METADATACALLBACK(codec_state == 0 ? null : FMOD_CODEC_STATE.createView(Pointer.newPointer(codec_state)), FMOD_TAGTYPE.get(tagtype), name, data == 0 ? null : Pointer.newPointer(data), datalen, FMOD_TAGDATATYPE.get(datatype), unique);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_CODEC_GETWAVEFORMAT_BRIDGE(long codec_state, int index, long waveformat)
	{
		FMOD_CODEC_GETWAVEFORMAT callback = (FMOD_CODEC_GETWAVEFORMAT)CallbackManager.getCallback(8, codec_state, false);
		FMOD_RESULT javaResult = callback.FMOD_CODEC_GETWAVEFORMAT(codec_state == 0 ? null : FMOD_CODEC_STATE.createView(Pointer.newPointer(codec_state)), index, waveformat == 0 ? null : FMOD_CODEC_WAVEFORMAT.createView(Pointer.newPointer(waveformat)));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_DSP_CREATECALLBACK_BRIDGE(long dsp_state)
	{
		FMOD_DSP_STATE callbackbridge_dsp_state = (dsp_state == 0) ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state));
		DSP callbackbridge_dsp = (callbackbridge_dsp_state == null) ? null : callbackbridge_dsp_state.getInstance();

		FMOD_DSP_CREATECALLBACK callback = (FMOD_DSP_CREATECALLBACK)CallbackManager.getCallback(9, Pointer.getPointer(callbackbridge_dsp), true);
		FMOD_RESULT javaResult = callback.FMOD_DSP_CREATECALLBACK(dsp_state == 0 ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state)));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_DSP_RELEASECALLBACK_BRIDGE(long dsp_state)
	{
		FMOD_DSP_STATE callbackbridge_dsp_state = (dsp_state == 0) ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state));
		DSP callbackbridge_dsp = (callbackbridge_dsp_state == null) ? null : callbackbridge_dsp_state.getInstance();

		FMOD_DSP_RELEASECALLBACK callback = (FMOD_DSP_RELEASECALLBACK)CallbackManager.getCallback(10, Pointer.getPointer(callbackbridge_dsp), false);
		FMOD_RESULT javaResult = callback.FMOD_DSP_RELEASECALLBACK(dsp_state == 0 ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state)));
		CallbackManager.addOwner(0, dsp_state);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_DSP_RESETCALLBACK_BRIDGE(long dsp_state)
	{
		FMOD_DSP_STATE callbackbridge_dsp_state = (dsp_state == 0) ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state));
		DSP callbackbridge_dsp = (callbackbridge_dsp_state == null) ? null : callbackbridge_dsp_state.getInstance();

		FMOD_DSP_RESETCALLBACK callback = (FMOD_DSP_RESETCALLBACK)CallbackManager.getCallback(11, Pointer.getPointer(callbackbridge_dsp), false);
		FMOD_RESULT javaResult = callback.FMOD_DSP_RESETCALLBACK(dsp_state == 0 ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state)));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_DSP_READCALLBACK_BRIDGE(long dsp_state, ByteBuffer inbuffer, ByteBuffer outbuffer, int length, int inchannels, int outchannels)
	{
		FMOD_DSP_STATE callbackbridge_dsp_state = (dsp_state == 0) ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state));
		DSP callbackbridge_dsp = (callbackbridge_dsp_state == null) ? null : callbackbridge_dsp_state.getInstance();

		FMOD_DSP_READCALLBACK callback = (FMOD_DSP_READCALLBACK)CallbackManager.getCallback(12, Pointer.getPointer(callbackbridge_dsp), false);
		if(inbuffer != null) {
			inbuffer.order(ByteOrder.nativeOrder());
		}
		if(outbuffer != null) {
			outbuffer.order(ByteOrder.nativeOrder());
		}
		FMOD_RESULT javaResult = callback.FMOD_DSP_READCALLBACK(dsp_state == 0 ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state)), inbuffer == null ? null : inbuffer.asFloatBuffer(), outbuffer == null ? null : outbuffer.asFloatBuffer(), length, inchannels, outchannels);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_DSP_SETPOSITIONCALLBACK_BRIDGE(long dsp_state, int pos)
	{
		FMOD_DSP_STATE callbackbridge_dsp_state = (dsp_state == 0) ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state));
		DSP callbackbridge_dsp = (callbackbridge_dsp_state == null) ? null : callbackbridge_dsp_state.getInstance();

		FMOD_DSP_SETPOSITIONCALLBACK callback = (FMOD_DSP_SETPOSITIONCALLBACK)CallbackManager.getCallback(13, Pointer.getPointer(callbackbridge_dsp), false);
		FMOD_RESULT javaResult = callback.FMOD_DSP_SETPOSITIONCALLBACK(dsp_state == 0 ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state)), pos);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_DSP_SETPARAMCALLBACK_BRIDGE(long dsp_state, int index, float value)
	{
		FMOD_DSP_STATE callbackbridge_dsp_state = (dsp_state == 0) ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state));
		DSP callbackbridge_dsp = (callbackbridge_dsp_state == null) ? null : callbackbridge_dsp_state.getInstance();

		FMOD_DSP_SETPARAMCALLBACK callback = (FMOD_DSP_SETPARAMCALLBACK)CallbackManager.getCallback(14, Pointer.getPointer(callbackbridge_dsp), false);
		FMOD_RESULT javaResult = callback.FMOD_DSP_SETPARAMCALLBACK(dsp_state == 0 ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state)), index, value);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_DSP_GETPARAMCALLBACK_BRIDGE(long dsp_state, int index, ByteBuffer value, ByteBuffer valuestr)
	{
		FMOD_DSP_STATE callbackbridge_dsp_state = (dsp_state == 0) ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state));
		DSP callbackbridge_dsp = (callbackbridge_dsp_state == null) ? null : callbackbridge_dsp_state.getInstance();

		FMOD_DSP_GETPARAMCALLBACK callback = (FMOD_DSP_GETPARAMCALLBACK)CallbackManager.getCallback(15, Pointer.getPointer(callbackbridge_dsp), false);
		if(value != null) {
			value.order(ByteOrder.nativeOrder());
		}
		if(valuestr != null) {
			valuestr.order(ByteOrder.nativeOrder());
		}
		FMOD_RESULT javaResult = callback.FMOD_DSP_GETPARAMCALLBACK(dsp_state == 0 ? null : FMOD_DSP_STATE.createView(Pointer.newPointer(dsp_state)), index, value == null ? null : value.asFloatBuffer(), valuestr);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_DSP_DIALOGCALLBACK_BRIDGE(long dsp_state, long hwnd, int show)
	{
            return 0;
	}
	public static int FMOD_SYSTEM_CALLBACK_BRIDGE(long system, int type, long commanddata1, long commanddata2)
	{
		FMOD_SYSTEM_CALLBACK callback = (FMOD_SYSTEM_CALLBACK)CallbackManager.getCallback(17, system, false);
		FMOD_RESULT javaResult = callback.FMOD_SYSTEM_CALLBACK(system == 0 ? null : System.createView(Pointer.newPointer(system)), FMOD_SYSTEM_CALLBACKTYPE.get(type), commanddata1 == 0 ? null : Pointer.newPointer(commanddata1), commanddata2 == 0 ? null : Pointer.newPointer(commanddata2));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_CHANNEL_CALLBACK_BRIDGE(long channel, int type, int command, int commanddata1, int commanddata2)
	{
		FMOD_CHANNEL_CALLBACK callback = (FMOD_CHANNEL_CALLBACK)CallbackManager.getCallback(18, channel, false);
		FMOD_RESULT javaResult = callback.FMOD_CHANNEL_CALLBACK(channel == 0 ? null : Channel.createView(Pointer.newPointer(channel)), FMOD_CHANNEL_CALLBACKTYPE.get(type), command, commanddata1, commanddata2);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_SOUND_NONBLOCKCALLBACK_BRIDGE(long sound, int result)
	{
		FMOD_SOUND_NONBLOCKCALLBACK callback = (FMOD_SOUND_NONBLOCKCALLBACK)CallbackManager.getCallback(19, sound, false);
		FMOD_RESULT javaResult = callback.FMOD_SOUND_NONBLOCKCALLBACK(sound == 0 ? null : Sound.createView(Pointer.newPointer(sound)), FMOD_RESULT.get(result));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_SOUND_PCMREADCALLBACK_BRIDGE(long sound, ByteBuffer data, int datalen)
	{
		FMOD_SOUND_PCMREADCALLBACK callback = (FMOD_SOUND_PCMREADCALLBACK)CallbackManager.getCallback(20, sound, false);
		if(data != null) {
			data.order(ByteOrder.nativeOrder());
		}
		FMOD_RESULT javaResult = callback.FMOD_SOUND_PCMREADCALLBACK(sound == 0 ? null : Sound.createView(Pointer.newPointer(sound)), data, datalen);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_SOUND_PCMSETPOSCALLBACK_BRIDGE(long sound, int subsound, int position, int postype)
	{
		FMOD_SOUND_PCMSETPOSCALLBACK callback = (FMOD_SOUND_PCMSETPOSCALLBACK)CallbackManager.getCallback(21, sound, false);
		FMOD_RESULT javaResult = callback.FMOD_SOUND_PCMSETPOSCALLBACK(sound == 0 ? null : Sound.createView(Pointer.newPointer(sound)), subsound, position, postype);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_FILE_OPENCALLBACK_BRIDGE(String name, int unicode, ByteBuffer filesize, Pointer handle, Pointer userdata)
	{
		FMOD_FILE_OPENCALLBACK callback = (FMOD_FILE_OPENCALLBACK)CallbackManager.getCallback(22);
		if(filesize != null) {
			filesize.order(ByteOrder.nativeOrder());
		}
		FMOD_RESULT javaResult = callback.FMOD_FILE_OPENCALLBACK(name, unicode, filesize == null ? null : filesize.asIntBuffer(), handle, userdata);
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_FILE_CLOSECALLBACK_BRIDGE(long handle, long userdata)
	{
		FMOD_FILE_CLOSECALLBACK callback = (FMOD_FILE_CLOSECALLBACK)CallbackManager.getCallback(23);
		FMOD_RESULT javaResult = callback.FMOD_FILE_CLOSECALLBACK(handle == 0 ? null : Pointer.newPointer(handle), userdata == 0 ? null : Pointer.newPointer(userdata));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_FILE_READCALLBACK_BRIDGE(long handle, ByteBuffer buffer, int sizebytes, ByteBuffer bytesread, long userdata)
	{
		FMOD_FILE_READCALLBACK callback = (FMOD_FILE_READCALLBACK)CallbackManager.getCallback(24);
		if(buffer != null) {
			buffer.order(ByteOrder.nativeOrder());
		}
		if(bytesread != null) {
			bytesread.order(ByteOrder.nativeOrder());
		}
		FMOD_RESULT javaResult = callback.FMOD_FILE_READCALLBACK(handle == 0 ? null : Pointer.newPointer(handle), buffer, sizebytes, bytesread == null ? null : bytesread.asIntBuffer(), userdata == 0 ? null : Pointer.newPointer(userdata));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static int FMOD_FILE_SEEKCALLBACK_BRIDGE(long handle, int pos, long userdata)
	{
		FMOD_FILE_SEEKCALLBACK callback = (FMOD_FILE_SEEKCALLBACK)CallbackManager.getCallback(25);
		FMOD_RESULT javaResult = callback.FMOD_FILE_SEEKCALLBACK(handle == 0 ? null : Pointer.newPointer(handle), pos, userdata == 0 ? null : Pointer.newPointer(userdata));
		return javaResult == null ? FMOD_RESULT.FMOD_RESULT_FORCEINT.asInt() : javaResult.asInt();
	}
	public static ByteBuffer FMOD_MEMORY_ALLOCCALLBACK_BRIDGE(int size, int type)
	{
		FMOD_MEMORY_ALLOCCALLBACK callback = (FMOD_MEMORY_ALLOCCALLBACK)CallbackManager.getCallback(26);
		ByteBuffer javaResult = callback.FMOD_MEMORY_ALLOCCALLBACK(size, type);
		return javaResult;
	}
	public static ByteBuffer FMOD_MEMORY_REALLOCCALLBACK_BRIDGE(ByteBuffer ptr, int size, int type)
	{
		FMOD_MEMORY_REALLOCCALLBACK callback = (FMOD_MEMORY_REALLOCCALLBACK)CallbackManager.getCallback(27);
		if(ptr != null) {
			ptr.order(ByteOrder.nativeOrder());
		}
		ByteBuffer javaResult = callback.FMOD_MEMORY_REALLOCCALLBACK(ptr, size, type);
		return javaResult;
	}
	public static void FMOD_MEMORY_FREECALLBACK_BRIDGE(ByteBuffer ptr, int type)
	{
		FMOD_MEMORY_FREECALLBACK callback = (FMOD_MEMORY_FREECALLBACK)CallbackManager.getCallback(28);
		if(ptr != null) {
			ptr.order(ByteOrder.nativeOrder());
		}
		callback.FMOD_MEMORY_FREECALLBACK(ptr, type);
	}
	public static float FMOD_3D_ROLLOFFCALLBACK_BRIDGE(long channel, float distance)
	{
		System system = new System();
		if(channel != 0) Channel.createView(Pointer.newPointer(channel)).getSystemObject(system);
		
		FMOD_3D_ROLLOFFCALLBACK callback = (FMOD_3D_ROLLOFFCALLBACK)CallbackManager.getCallback(29, Pointer.getPointer(system), false);
		float javaResult = callback.FMOD_3D_ROLLOFFCALLBACK(channel == 0 ? null : Channel.createView(Pointer.newPointer(channel)), distance);
		return javaResult;
	}

	protected static String getCallbackName(int type)
	{
		switch(type)
		{
			case 0: return "FMOD_CODEC_OPENCALLBACK";
			case 1: return "FMOD_CODEC_CLOSECALLBACK";
			case 2: return "FMOD_CODEC_READCALLBACK";
			case 3: return "FMOD_CODEC_GETLENGTHCALLBACK";
			case 4: return "FMOD_CODEC_SETPOSITIONCALLBACK";
			case 5: return "FMOD_CODEC_GETPOSITIONCALLBACK";
			case 6: return "FMOD_CODEC_SOUNDCREATECALLBACK";
			case 7: return "FMOD_CODEC_METADATACALLBACK";
			case 8: return "FMOD_CODEC_GETWAVEFORMAT";
			case 9: return "FMOD_DSP_CREATECALLBACK";
			case 10: return "FMOD_DSP_RELEASECALLBACK";
			case 11: return "FMOD_DSP_RESETCALLBACK";
			case 12: return "FMOD_DSP_READCALLBACK";
			case 13: return "FMOD_DSP_SETPOSITIONCALLBACK";
			case 14: return "FMOD_DSP_SETPARAMCALLBACK";
			case 15: return "FMOD_DSP_GETPARAMCALLBACK";
			case 16: return "FMOD_DSP_DIALOGCALLBACK";
			case 17: return "FMOD_SYSTEM_CALLBACK";
			case 18: return "FMOD_CHANNEL_CALLBACK";
			case 19: return "FMOD_SOUND_NONBLOCKCALLBACK";
			case 20: return "FMOD_SOUND_PCMREADCALLBACK";
			case 21: return "FMOD_SOUND_PCMSETPOSCALLBACK";
			case 22: return "FMOD_FILE_OPENCALLBACK";
			case 23: return "FMOD_FILE_CLOSECALLBACK";
			case 24: return "FMOD_FILE_READCALLBACK";
			case 25: return "FMOD_FILE_SEEKCALLBACK";
			case 26: return "FMOD_MEMORY_ALLOCCALLBACK";
			case 27: return "FMOD_MEMORY_REALLOCCALLBACK";
			case 28: return "FMOD_MEMORY_FREECALLBACK";
			case 29: return "FMOD_3D_ROLLOFFCALLBACK";
			default: return "UNKNOW_"+type;
		}
	}
}