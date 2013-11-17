/**
 * 				NativeFmodEx Project
 *
 * Want to use FMOD Ex API (www.fmod.org) in the Java language ? NativeFmodEx is made for you.
 * Copyright © 2005-2008 Jérôme JOUVIE (Jouvieje)
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

package org.jouvieje.FmodEx;

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

/**
 * 'System' API
 */
public class System extends Pointer
{
	/**
	 * Create a view of the <code>Pointer</code> object as a <code>System</code> object.<br>
	 * This view is valid only if the memory holded by the <code>Pointer</code> holds a System object.
	 */
	public static System createView(Pointer pointer)
	{
		return new System(Pointer.getPointer(pointer));
	}
	private System(long pointer)
	{
		super(pointer);
	}

	public System()
	{
		super(0);
	}

	/**
	 * 
	 */
	public FMOD_RESULT release()
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(22, null, 0);
		CallbackManager.addCallback(23, null, 0);
		CallbackManager.addCallback(24, null, 0);
		CallbackManager.addCallback(25, null, 0);
		CallbackManager.addOwner(0, pointer);
		int javaResult = FmodExJNI.System_release(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setOutput(FMOD_OUTPUTTYPE output)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setOutput(pointer, output.asInt());
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getOutput(FMOD_OUTPUTTYPE[] output)
	{
		if(pointer == 0) throw new NullPointerException();
		IntBuffer outputPointer = BufferUtils.newIntBuffer(1);
		int javaResult = FmodExJNI.System_getOutput(pointer, outputPointer);
		if(output != null) {
			output[0] = FMOD_OUTPUTTYPE.get(outputPointer.get(0));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumDrivers(IntBuffer numdrivers)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numdrivers != null && !numdrivers.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getNumDrivers(pointer, numdrivers, BufferUtils.getPositionInBytes(numdrivers));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDriverInfo(int id, ByteBuffer name, int namelen, FMOD_GUID guid)
	{
		if(pointer == 0) throw new NullPointerException();
		if(name != null && !name.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getDriverInfo(pointer, id, name, BufferUtils.getPositionInBytes(name), namelen, Pointer.getPointer(guid));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDriverCaps(int id, IntBuffer caps, IntBuffer minfrequency, IntBuffer maxfrequency, FMOD_SPEAKERMODE[] controlpanelspeakermode)
	{
		if(pointer == 0) throw new NullPointerException();
		if(caps != null && !caps.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(minfrequency != null && !minfrequency.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(maxfrequency != null && !maxfrequency.isDirect())
		{
			throw new NonDirectBufferException();
		}
		IntBuffer controlpanelspeakermodePointer = BufferUtils.newIntBuffer(1);
		int javaResult = FmodExJNI.System_getDriverCaps(pointer, id, caps, BufferUtils.getPositionInBytes(caps), minfrequency, BufferUtils.getPositionInBytes(minfrequency), maxfrequency, BufferUtils.getPositionInBytes(maxfrequency), controlpanelspeakermodePointer);
		if(controlpanelspeakermode != null) {
			controlpanelspeakermode[0] = FMOD_SPEAKERMODE.get(controlpanelspeakermodePointer.get(0));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setDriver(int driver)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setDriver(pointer, driver);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDriver(IntBuffer driver)
	{
		if(pointer == 0) throw new NullPointerException();
		if(driver != null && !driver.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getDriver(pointer, driver, BufferUtils.getPositionInBytes(driver));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setHardwareChannels(int min2d, int max2d, int min3d, int max3d)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setHardwareChannels(pointer, min2d, max2d, min3d, max3d);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setSoftwareChannels(int numsoftwarechannels)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setSoftwareChannels(pointer, numsoftwarechannels);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSoftwareChannels(IntBuffer numsoftwarechannels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numsoftwarechannels != null && !numsoftwarechannels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getSoftwareChannels(pointer, numsoftwarechannels, BufferUtils.getPositionInBytes(numsoftwarechannels));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setSoftwareFormat(int samplerate, FMOD_SOUND_FORMAT format, int numoutputchannels, int maxinputchannels, FMOD_DSP_RESAMPLER resamplemethod)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setSoftwareFormat(pointer, samplerate, format.asInt(), numoutputchannels, maxinputchannels, resamplemethod.asInt());
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSoftwareFormat(IntBuffer samplerate, FMOD_SOUND_FORMAT[] format, IntBuffer numoutputchannels, IntBuffer maxinputchannels, FMOD_DSP_RESAMPLER[] resamplemethod, IntBuffer bits)
	{
		if(pointer == 0) throw new NullPointerException();
		if(samplerate != null && !samplerate.isDirect())
		{
			throw new NonDirectBufferException();
		}
		IntBuffer formatPointer = BufferUtils.newIntBuffer(1);
		if(numoutputchannels != null && !numoutputchannels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(maxinputchannels != null && !maxinputchannels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		IntBuffer resamplemethodPointer = BufferUtils.newIntBuffer(1);
		if(bits != null && !bits.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getSoftwareFormat(pointer, samplerate, BufferUtils.getPositionInBytes(samplerate), formatPointer, numoutputchannels, BufferUtils.getPositionInBytes(numoutputchannels), maxinputchannels, BufferUtils.getPositionInBytes(maxinputchannels), resamplemethodPointer, bits, BufferUtils.getPositionInBytes(bits));
		if(format != null) {
			format[0] = FMOD_SOUND_FORMAT.get(formatPointer.get(0));
		}
		if(resamplemethod != null) {
			resamplemethod[0] = FMOD_DSP_RESAMPLER.get(resamplemethodPointer.get(0));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setDSPBufferSize(int bufferlength, int numbuffers)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setDSPBufferSize(pointer, bufferlength, numbuffers);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDSPBufferSize(IntBuffer bufferlength, IntBuffer numbuffers)
	{
		if(pointer == 0) throw new NullPointerException();
		if(bufferlength != null && !bufferlength.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(numbuffers != null && !numbuffers.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getDSPBufferSize(pointer, bufferlength, BufferUtils.getPositionInBytes(bufferlength), numbuffers, BufferUtils.getPositionInBytes(numbuffers));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setFileSystem(FMOD_FILE_OPENCALLBACK useropen, FMOD_FILE_CLOSECALLBACK userclose, FMOD_FILE_READCALLBACK userread, FMOD_FILE_SEEKCALLBACK userseek, int blockalign)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(22, useropen, 0);
		CallbackManager.addCallback(23, userclose, 0);
		CallbackManager.addCallback(24, userread, 0);
		CallbackManager.addCallback(25, userseek, 0);
		int javaResult = FmodExJNI.System_setFileSystem(pointer, useropen == null ? false : true, userclose == null ? false : true, userread == null ? false : true, userseek == null ? false : true, blockalign);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT attachFileSystem(FMOD_FILE_OPENCALLBACK useropen, FMOD_FILE_CLOSECALLBACK userclose, FMOD_FILE_READCALLBACK userread, FMOD_FILE_SEEKCALLBACK userseek)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(22, useropen, 0);
		CallbackManager.addCallback(23, userclose, 0);
		CallbackManager.addCallback(24, userread, 0);
		CallbackManager.addCallback(25, userseek, 0);
		int javaResult = FmodExJNI.System_attachFileSystem(pointer, useropen == null ? false : true, userclose == null ? false : true, userread == null ? false : true, userseek == null ? false : true);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setAdvancedSettings(FMOD_ADVANCEDSETTINGS settings)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setAdvancedSettings(pointer, Pointer.getPointer(settings));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getAdvancedSettings(FMOD_ADVANCEDSETTINGS settings)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_getAdvancedSettings(pointer, Pointer.getPointer(settings));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setSpeakerMode(FMOD_SPEAKERMODE speakermode)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setSpeakerMode(pointer, speakermode.asInt());
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSpeakerMode(FMOD_SPEAKERMODE[] speakermode)
	{
		if(pointer == 0) throw new NullPointerException();
		IntBuffer speakermodePointer = BufferUtils.newIntBuffer(1);
		int javaResult = FmodExJNI.System_getSpeakerMode(pointer, speakermodePointer);
		if(speakermode != null) {
			speakermode[0] = FMOD_SPEAKERMODE.get(speakermodePointer.get(0));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setCallback(FMOD_SYSTEM_CALLBACK callback)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(17, callback, pointer);
		CallbackManager.addOwner(callback == null ? 0 : pointer, pointer);
		int javaResult = FmodExJNI.System_setCallback(pointer, callback == null ? false : true);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setPluginPath(String path)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setPluginPath(pointer, path == null ? null : path.getBytes());
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT loadPlugin(String filename, FMOD_PLUGINTYPE[] plugintype, IntBuffer index)
	{
		if(pointer == 0) throw new NullPointerException();
		IntBuffer plugintypePointer = BufferUtils.newIntBuffer(1);
		if(index != null && !index.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_loadPlugin(pointer, filename == null ? null : filename.getBytes(), plugintypePointer, index, BufferUtils.getPositionInBytes(index));
		if(plugintype != null) {
			plugintype[0] = FMOD_PLUGINTYPE.get(plugintypePointer.get(0));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumPlugins(FMOD_PLUGINTYPE plugintype, IntBuffer numplugins)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numplugins != null && !numplugins.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getNumPlugins(pointer, plugintype.asInt(), numplugins, BufferUtils.getPositionInBytes(numplugins));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getPluginInfo(FMOD_PLUGINTYPE plugintype, int index, ByteBuffer name, int namelen, IntBuffer version)
	{
		if(pointer == 0) throw new NullPointerException();
		if(name != null && !name.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(version != null && !version.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getPluginInfo(pointer, plugintype.asInt(), index, name, BufferUtils.getPositionInBytes(name), namelen, version, BufferUtils.getPositionInBytes(version));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT unloadPlugin(FMOD_PLUGINTYPE plugintype, int index)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_unloadPlugin(pointer, plugintype.asInt(), index);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setOutputByPlugin(int index)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setOutputByPlugin(pointer, index);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getOutputByPlugin(IntBuffer index)
	{
		if(pointer == 0) throw new NullPointerException();
		if(index != null && !index.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getOutputByPlugin(pointer, index, BufferUtils.getPositionInBytes(index));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createCodec(FMOD_CODEC_DESCRIPTION description)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createCodec(pointer, Pointer.getPointer(description));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT init(int maxchannels, int flags, Pointer extradriverdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_init(pointer, maxchannels, flags, Pointer.getPointer(extradriverdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT close()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_close(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT update()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_update(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DSettings(float dopplerscale, float distancefactor, float rolloffscale)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_set3DSettings(pointer, dopplerscale, distancefactor, rolloffscale);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DSettings(FloatBuffer dopplerscale, FloatBuffer distancefactor, FloatBuffer rolloffscale)
	{
		if(pointer == 0) throw new NullPointerException();
		if(dopplerscale != null && !dopplerscale.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(distancefactor != null && !distancefactor.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(rolloffscale != null && !rolloffscale.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_get3DSettings(pointer, dopplerscale, BufferUtils.getPositionInBytes(dopplerscale), distancefactor, BufferUtils.getPositionInBytes(distancefactor), rolloffscale, BufferUtils.getPositionInBytes(rolloffscale));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DNumListeners(int numlisteners)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_set3DNumListeners(pointer, numlisteners);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DNumListeners(IntBuffer numlisteners)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numlisteners != null && !numlisteners.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_get3DNumListeners(pointer, numlisteners, BufferUtils.getPositionInBytes(numlisteners));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DListenerAttributes(int listener, FMOD_VECTOR pos, FMOD_VECTOR vel, FMOD_VECTOR forward, FMOD_VECTOR up)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_set3DListenerAttributes(pointer, listener, Pointer.getPointer(pos), Pointer.getPointer(vel), Pointer.getPointer(forward), Pointer.getPointer(up));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DListenerAttributes(int listener, FMOD_VECTOR pos, FMOD_VECTOR vel, FMOD_VECTOR forward, FMOD_VECTOR up)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_get3DListenerAttributes(pointer, listener, Pointer.getPointer(pos), Pointer.getPointer(vel), Pointer.getPointer(forward), Pointer.getPointer(up));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DRolloffCallback(FMOD_3D_ROLLOFFCALLBACK callback)
	{
		if(pointer == 0) throw new NullPointerException();
		CallbackManager.addCallback(29, callback, pointer);
		CallbackManager.addOwner(callback == null ? 0 : pointer, pointer);
		int javaResult = FmodExJNI.System_set3DRolloffCallback(pointer, callback == null ? false : true);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT set3DSpeakerPosition(FMOD_SPEAKER speaker, float x, float y, boolean active)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_set3DSpeakerPosition(pointer, speaker.asInt(), x, y, active);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT get3DSpeakerPosition(FMOD_SPEAKER speaker, FloatBuffer x, FloatBuffer y, ByteBuffer active)
	{
		if(pointer == 0) throw new NullPointerException();
		if(x != null && !x.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(y != null && !y.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(active != null && !active.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_get3DSpeakerPosition(pointer, speaker.asInt(), x, BufferUtils.getPositionInBytes(x), y, BufferUtils.getPositionInBytes(y), active, BufferUtils.getPositionInBytes(active));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setStreamBufferSize(int filebuffersize, int filebuffersizetype)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setStreamBufferSize(pointer, filebuffersize, filebuffersizetype);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getStreamBufferSize(IntBuffer filebuffersize, IntBuffer filebuffersizetype)
	{
		if(pointer == 0) throw new NullPointerException();
		if(filebuffersize != null && !filebuffersize.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(filebuffersizetype != null && !filebuffersizetype.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getStreamBufferSize(pointer, filebuffersize, BufferUtils.getPositionInBytes(filebuffersize), filebuffersizetype, BufferUtils.getPositionInBytes(filebuffersizetype));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getVersion(IntBuffer version)
	{
		if(pointer == 0) throw new NullPointerException();
		if(version != null && !version.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getVersion(pointer, version, BufferUtils.getPositionInBytes(version));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getOutputHandle(Pointer handle)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_getOutputHandle(pointer, handle);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getChannelsPlaying(IntBuffer channels)
	{
		if(pointer == 0) throw new NullPointerException();
		if(channels != null && !channels.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getChannelsPlaying(pointer, channels, BufferUtils.getPositionInBytes(channels));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getHardwareChannels(IntBuffer num2d, IntBuffer num3d, IntBuffer total)
	{
		if(pointer == 0) throw new NullPointerException();
		if(num2d != null && !num2d.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(num3d != null && !num3d.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(total != null && !total.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getHardwareChannels(pointer, num2d, BufferUtils.getPositionInBytes(num2d), num3d, BufferUtils.getPositionInBytes(num3d), total, BufferUtils.getPositionInBytes(total));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getCPUUsage(FloatBuffer dsp, FloatBuffer stream, FloatBuffer update, FloatBuffer total)
	{
		if(pointer == 0) throw new NullPointerException();
		if(dsp != null && !dsp.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(stream != null && !stream.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(update != null && !update.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(total != null && !total.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getCPUUsage(pointer, dsp, BufferUtils.getPositionInBytes(dsp), stream, BufferUtils.getPositionInBytes(stream), update, BufferUtils.getPositionInBytes(update), total, BufferUtils.getPositionInBytes(total));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSoundRAM(IntBuffer currentalloced, IntBuffer maxalloced, IntBuffer total)
	{
		if(pointer == 0) throw new NullPointerException();
		if(currentalloced != null && !currentalloced.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(maxalloced != null && !maxalloced.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(total != null && !total.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getSoundRAM(pointer, currentalloced, BufferUtils.getPositionInBytes(currentalloced), maxalloced, BufferUtils.getPositionInBytes(maxalloced), total, BufferUtils.getPositionInBytes(total));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNumCDROMDrives(IntBuffer numdrives)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numdrives != null && !numdrives.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getNumCDROMDrives(pointer, numdrives, BufferUtils.getPositionInBytes(numdrives));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getCDROMDriveName(int drive, ByteBuffer drivename, int drivenamelen, ByteBuffer scsiname, int scsinamelen, ByteBuffer devicename, int devicenamelen)
	{
		if(pointer == 0) throw new NullPointerException();
		if(drivename != null && !drivename.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(scsiname != null && !scsiname.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(devicename != null && !devicename.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getCDROMDriveName(pointer, drive, drivename, BufferUtils.getPositionInBytes(drivename), drivenamelen, scsiname, BufferUtils.getPositionInBytes(scsiname), scsinamelen, devicename, BufferUtils.getPositionInBytes(devicename), devicenamelen);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getSpectrum(FloatBuffer spectrumarray, int numvalues, int channeloffset, FMOD_DSP_FFT_WINDOW windowtype)
	{
		if(pointer == 0) throw new NullPointerException();
		if(spectrumarray != null && !spectrumarray.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getSpectrum(pointer, spectrumarray, BufferUtils.getPositionInBytes(spectrumarray), numvalues, channeloffset, windowtype.asInt());
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getWaveData(FloatBuffer wavearray, int numvalues, int channeloffset)
	{
		if(pointer == 0) throw new NullPointerException();
		if(wavearray != null && !wavearray.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getWaveData(pointer, wavearray, BufferUtils.getPositionInBytes(wavearray), numvalues, channeloffset);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createSound(String name_or_data, int mode, FMOD_CREATESOUNDEXINFO exinfo, Sound sound)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createSound(pointer, name_or_data == null ? null : name_or_data.getBytes(), mode, Pointer.getPointer(exinfo), sound);
		if(sound != null && !sound.isNull() && exinfo != null && !exinfo.isNull()) {
			CallbackManager.addOwner(Pointer.getPointer(exinfo), Pointer.getPointer(sound));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createSound(ByteBuffer name_or_data, int mode, FMOD_CREATESOUNDEXINFO exinfo, Sound sound)
	{
		if(pointer == 0) throw new NullPointerException();
		if(name_or_data != null && !name_or_data.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_createSound(pointer, name_or_data, BufferUtils.getPositionInBytes(name_or_data), mode, Pointer.getPointer(exinfo), sound);
		if(sound != null && !sound.isNull() && exinfo != null && !exinfo.isNull()) {
			CallbackManager.addOwner(Pointer.getPointer(exinfo), Pointer.getPointer(sound));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createStream(String name_or_data, int mode, FMOD_CREATESOUNDEXINFO exinfo, Sound sound)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createStream(pointer, name_or_data == null ? null : name_or_data.getBytes(), mode, Pointer.getPointer(exinfo), sound);
		if(sound != null && !sound.isNull() && exinfo != null && !exinfo.isNull()) {
			CallbackManager.addOwner(Pointer.getPointer(exinfo), Pointer.getPointer(sound));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createStream(ByteBuffer name_or_data, int mode, FMOD_CREATESOUNDEXINFO exinfo, Sound sound)
	{
		if(pointer == 0) throw new NullPointerException();
		if(name_or_data != null && !name_or_data.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_createStream(pointer, name_or_data, BufferUtils.getPositionInBytes(name_or_data), mode, Pointer.getPointer(exinfo), sound);
		if(sound != null && !sound.isNull() && exinfo != null && !exinfo.isNull()) {
			CallbackManager.addOwner(Pointer.getPointer(exinfo), Pointer.getPointer(sound));
		}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createDSP(FMOD_DSP_DESCRIPTION description, DSP dsp)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createDSP(pointer, Pointer.getPointer(description), dsp);
		if(description != null && !description.isNull() && dsp != null && !dsp.isNull()) {
			CallbackManager.addOwner(Pointer.getPointer(description), Pointer.getPointer(dsp));
	}
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createDSPByType(FMOD_DSP_TYPE type, DSP dsp)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createDSPByType(pointer, type.asInt(), dsp);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createDSPByIndex(int index, DSP dsp)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createDSPByIndex(pointer, index, dsp);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createChannelGroup(String name, ChannelGroup channelgroup)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createChannelGroup(pointer, name == null ? null : name.getBytes(), channelgroup);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createSoundGroup(String name, SoundGroup soundgroup)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createSoundGroup(pointer, name == null ? null : name.getBytes(), soundgroup);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createReverb(Reverb reverb)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createReverb(pointer, reverb);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT playSound(FMOD_CHANNELINDEX channelid, Sound sound, boolean paused, Channel channel)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_playSound(pointer, channelid.asInt(), Pointer.getPointer(sound), paused, channel);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT playDSP(FMOD_CHANNELINDEX channelid, DSP dsp, boolean paused, Channel channel)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_playDSP(pointer, channelid.asInt(), Pointer.getPointer(dsp), paused, channel);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getChannel(int channelid, Channel channel)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_getChannel(pointer, channelid, channel);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMasterChannelGroup(ChannelGroup channelgroup)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_getMasterChannelGroup(pointer, channelgroup);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getMasterSoundGroup(SoundGroup soundgroup)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_getMasterSoundGroup(pointer, soundgroup);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setReverbProperties(FMOD_REVERB_PROPERTIES prop)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setReverbProperties(pointer, Pointer.getPointer(prop));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getReverbProperties(FMOD_REVERB_PROPERTIES prop)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_getReverbProperties(pointer, Pointer.getPointer(prop));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setReverbAmbientProperties(FMOD_REVERB_PROPERTIES prop)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setReverbAmbientProperties(pointer, Pointer.getPointer(prop));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getReverbAmbientProperties(FMOD_REVERB_PROPERTIES prop)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_getReverbAmbientProperties(pointer, Pointer.getPointer(prop));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDSPHead(DSP dsp)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_getDSPHead(pointer, dsp);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT addDSP(DSP dsp, DSPConnection connection)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_addDSP(pointer, Pointer.getPointer(dsp), connection);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT lockDSP()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_lockDSP(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT unlockDSP()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_unlockDSP(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getDSPClock(IntBuffer hi, IntBuffer lo)
	{
		if(pointer == 0) throw new NullPointerException();
		if(hi != null && !hi.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(lo != null && !lo.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getDSPClock(pointer, hi, BufferUtils.getPositionInBytes(hi), lo, BufferUtils.getPositionInBytes(lo));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setRecordDriver(int driver)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setRecordDriver(pointer, driver);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getRecordDriver(IntBuffer driver)
	{
		if(pointer == 0) throw new NullPointerException();
		if(driver != null && !driver.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getRecordDriver(pointer, driver, BufferUtils.getPositionInBytes(driver));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getRecordNumDrivers(IntBuffer numdrivers)
	{
		if(pointer == 0) throw new NullPointerException();
		if(numdrivers != null && !numdrivers.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getRecordNumDrivers(pointer, numdrivers, BufferUtils.getPositionInBytes(numdrivers));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getRecordDriverInfo(int id, ByteBuffer name, int namelen, FMOD_GUID guid)
	{
		if(pointer == 0) throw new NullPointerException();
		if(name != null && !name.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getRecordDriverInfo(pointer, id, name, BufferUtils.getPositionInBytes(name), namelen, Pointer.getPointer(guid));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getRecordDriverCaps(int id, IntBuffer caps, IntBuffer minfrequency, IntBuffer maxfrequency)
	{
		if(pointer == 0) throw new NullPointerException();
		if(caps != null && !caps.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(minfrequency != null && !minfrequency.isDirect())
		{
			throw new NonDirectBufferException();
		}
		if(maxfrequency != null && !maxfrequency.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getRecordDriverCaps(pointer, id, caps, BufferUtils.getPositionInBytes(caps), minfrequency, BufferUtils.getPositionInBytes(minfrequency), maxfrequency, BufferUtils.getPositionInBytes(maxfrequency));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getRecordPosition(IntBuffer position)
	{
		if(pointer == 0) throw new NullPointerException();
		if(position != null && !position.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getRecordPosition(pointer, position, BufferUtils.getPositionInBytes(position));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT recordStart(Sound sound, boolean loop)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_recordStart(pointer, Pointer.getPointer(sound), loop);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT recordStop()
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_recordStop(pointer);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT isRecording(ByteBuffer recording)
	{
		if(pointer == 0) throw new NullPointerException();
		if(recording != null && !recording.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_isRecording(pointer, recording, BufferUtils.getPositionInBytes(recording));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT createGeometry(int maxpolygons, int maxvertices, Geometry geometry)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_createGeometry(pointer, maxpolygons, maxvertices, geometry);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setGeometrySettings(float maxworldsize)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setGeometrySettings(pointer, maxworldsize);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getGeometrySettings(FloatBuffer maxworldsize)
	{
		if(pointer == 0) throw new NullPointerException();
		if(maxworldsize != null && !maxworldsize.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getGeometrySettings(pointer, maxworldsize, BufferUtils.getPositionInBytes(maxworldsize));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT loadGeometry(ByteBuffer data, int datasize, Geometry geometry)
	{
		if(pointer == 0) throw new NullPointerException();
		if(data != null && !data.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_loadGeometry(pointer, data, BufferUtils.getPositionInBytes(data), datasize, geometry);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setNetworkProxy(String proxy)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setNetworkProxy(pointer, proxy == null ? null : proxy.getBytes());
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNetworkProxy(ByteBuffer proxy, int proxylen)
	{
		if(pointer == 0) throw new NullPointerException();
		if(proxy != null && !proxy.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getNetworkProxy(pointer, proxy, BufferUtils.getPositionInBytes(proxy), proxylen);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setNetworkTimeout(int timeout)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setNetworkTimeout(pointer, timeout);
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getNetworkTimeout(IntBuffer timeout)
	{
		if(pointer == 0) throw new NullPointerException();
		if(timeout != null && !timeout.isDirect())
		{
			throw new NonDirectBufferException();
		}
		int javaResult = FmodExJNI.System_getNetworkTimeout(pointer, timeout, BufferUtils.getPositionInBytes(timeout));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT setUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_setUserData(pointer, Pointer.getPointer(userdata));
		return FMOD_RESULT.get(javaResult);
	}

	/**
	 * 
	 */
	public FMOD_RESULT getUserData(Pointer userdata)
	{
		if(pointer == 0) throw new NullPointerException();
		int javaResult = FmodExJNI.System_getUserData(pointer, userdata);
		return FMOD_RESULT.get(javaResult);
	}

}