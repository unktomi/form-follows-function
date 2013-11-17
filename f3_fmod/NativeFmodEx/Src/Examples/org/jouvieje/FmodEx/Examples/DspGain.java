/*===============================================================================================
DSP_GAIN.DLL
Copyright (c), Firelight Technologies Pty, Ltd 2005.

===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_MEMORY;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.jouvieje.FmodEx.Callbacks.FMOD_DSP_CREATECALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_DSP_GETPARAMCALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_DSP_READCALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_DSP_RELEASECALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_DSP_RESETCALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_DSP_SETPARAMCALLBACK;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.Misc.ObjectPointer;
import org.jouvieje.FmodEx.Structures.FMOD_DSP_DESCRIPTION;
import org.jouvieje.FmodEx.Structures.FMOD_DSP_PARAMETERDESC;
import org.jouvieje.FmodEx.Structures.FMOD_DSP_STATE;

/**
 * I've ported the C++ FMOD Ex example to NativeFmodEx.
 * 
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * Site :
 * 		http://jerome.jouvie.free.fr/
 */
public class DspGain
{
	/*
	 * FMODGetDSPDescription is mandantory for every fmod plugin.  This is the symbol the registerplugin function searches for.
	 * Must be declared with F_API to make it export as stdcall.
	 * MUST BE EXTERN'ED AS C!  C++ functions will be mangled incorrectly and not load in fmod.
	 */
	public static FMOD_DSP_DESCRIPTION FMODGetDSPDescription()
	{
		if(dspparam == null)
		{
			dspparam = new FMOD_DSP_PARAMETERDESC[1];
			dspparam[0] = FMOD_DSP_PARAMETERDESC.create();
			dspparam[0].setMin(0.0f);
			dspparam[0].setMax(1.0f);
			dspparam[0].setDefaultVal(1.0f);
			dspparam[0].setName("Level");
			dspparam[0].setLabel("%");
			dspparam[0].setDescription("Gain level");
		}
		
		if(dspgaindesc == null)
		{
			dspgaindesc = FMOD_DSP_DESCRIPTION.create();
			dspgaindesc.setName("FMOD gain example");	//name
			dspgaindesc.setVersion(0x00010000);			//version 0xAAAABBBB   A = major, B = minor.
			dspgaindesc.setChannels(0);					// 0 = we can filter whatever you throw at us.  To be most user friendly, always write a filter like this.
			dspgaindesc.setCreate(dspcreate);
			dspgaindesc.setRelease(dsprelease);
			dspgaindesc.setReset(dspreset);	
			dspgaindesc.setRead(dspread);
			dspgaindesc.setSetPosition(null);			//This is for if you want to allow the plugin to seek, which doesnt really make sense in a gain filter so we'll just leave it out.
			dspgaindesc.setNumParameters(1);			//1 parameter.  "level"
			dspgaindesc.setParamDesc(dspparam);			//pointer to the parameter list definition.
			dspgaindesc.setSetParameter(dspsetparam);
			dspgaindesc.setGetParameter(dspgetparam);
			dspgaindesc.setConfig(null);				//This is for if you want to pop up a dialog box to configure the plugin.  Not doing that here.
			dspgaindesc.setConfigWidth(0);				//This is for if you want to pop up a dialog box to configure the plugin.  Not doing that here.
			dspgaindesc.setConfigHeight(0);				//This is for if you want to pop up a dialog box to configure the plugin.  Not doing that here.
		}
		
		return dspgaindesc;
	}
	
	static class dspgain_state
	{
	    float gain;
	}
	
	/*
	 * DSP Parameter list.
	 */
	private static FMOD_DSP_PARAMETERDESC[] dspparam = null;
	
	private static FMOD_DSP_DESCRIPTION dspgaindesc = null;
	
	private static FMOD_DSP_CREATECALLBACK dspcreate = new FMOD_DSP_CREATECALLBACK(){
		public FMOD_RESULT FMOD_DSP_CREATECALLBACK(FMOD_DSP_STATE dsp)
		{
			/*
			 * If we were allocating memory for buffers etc, it would be done in this function.
			 */
			dspgain_state state = new dspgain_state();
			if(state == null)
				return FMOD_ERR_MEMORY;
			
			state.gain = dspparam[0].getDefaultVal();
			
			dsp.setPluginData(ObjectPointer.create(state));
			
			return FMOD_OK;
		}
	};
	
	private static FMOD_DSP_RELEASECALLBACK dsprelease = new FMOD_DSP_RELEASECALLBACK(){
		public FMOD_RESULT FMOD_DSP_RELEASECALLBACK(FMOD_DSP_STATE dsp)
		{
			ObjectPointer op = ObjectPointer.createView(dsp.getPluginData());
			
			/*
			 * If memory was allocated in create, it would be freed in this function.
			 */
			op.release();
			
			return FMOD_OK;
		}
	};
	
	private static FMOD_DSP_RESETCALLBACK dspreset = new FMOD_DSP_RESETCALLBACK(){
		public FMOD_RESULT FMOD_DSP_RESETCALLBACK(FMOD_DSP_STATE dsp)
		{
			dspgain_state state = (dspgain_state)ObjectPointer.createView(dsp.getPluginData()).getObject();
			
			/*
			 * This isnt really needed here.  It is used to reset a filter back to it's default state.
			 */
			
			state.gain = dspparam[0].getDefaultVal();
			
			return FMOD_OK;
		}
	};
	
	/*
	 * This callback does the work.  Modify data from inbuffer and send it to outbuffer.
	 */
	private static FMOD_DSP_READCALLBACK dspread = new FMOD_DSP_READCALLBACK(){
		public FMOD_RESULT FMOD_DSP_READCALLBACK(FMOD_DSP_STATE dsp, FloatBuffer inbuffer, FloatBuffer outbuffer, int length,
				int inchannels, int outchannels)
		{
			dspgain_state state = (dspgain_state)ObjectPointer.createView(dsp.getPluginData()).getObject();
			int channels = inchannels;		//outchannels and inchannels will always be the same because this is a flexible filter.
			
			for(int i = 0; i < length; i++)
			{
				for(int j = 0; j < channels; j++)
				{
					outbuffer.put(i*channels+j, inbuffer.get(i*channels+j) * state.gain);
				}
			}
			
			return FMOD_OK;
		}
	};
	
	/*
	 * This callback is for when the user sets a parameter.  It is automatically clamped between 0 and 1.
	 */
	private static FMOD_DSP_SETPARAMCALLBACK dspsetparam = new FMOD_DSP_SETPARAMCALLBACK(){
		public FMOD_RESULT FMOD_DSP_SETPARAMCALLBACK(FMOD_DSP_STATE dsp, int index, float value)
		{
			dspgain_state state = (dspgain_state)ObjectPointer.createView(dsp.getPluginData()).getObject();
			
			switch (index)
			{
				case 0:
				{
					state.gain = value;
					break;
				}
			}
			return FMOD_OK;
		}
	};
	
	/*
	 * This callback is for when the user gets a parameter.  The label for our only parameter is percent,
	 * so when the string is requested print it out as 0 to 100.
	 */
	private static FMOD_DSP_GETPARAMCALLBACK dspgetparam = new FMOD_DSP_GETPARAMCALLBACK(){
		public FMOD_RESULT FMOD_DSP_GETPARAMCALLBACK(FMOD_DSP_STATE dsp, int index, FloatBuffer value, ByteBuffer valuestr)
		{
			dspgain_state state = (dspgain_state)ObjectPointer.createView(dsp.getPluginData()).getObject();
			
			switch(index)
			{
				case 0:
				{
					value.put(state.gain);
					
					String s = String.format("%.02f", state.gain * 100.0f);		//our units are '%', so print it out as 0 to 100.
					BufferUtils.putString(valuestr, s);
					BufferUtils.putNullTerminal(valuestr);
				}
			}
			
			return FMOD_OK;
		}
	};
}