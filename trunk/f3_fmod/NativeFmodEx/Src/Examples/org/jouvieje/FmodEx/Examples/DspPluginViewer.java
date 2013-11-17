/*===============================================================================================
DSP Plugin Viewer Example
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example ....
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static java.lang.System.exit;
import static java.lang.System.out;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENUSER;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_MODORDER;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.LINUX;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.LINUX64;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.MAC;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.WIN32;
import static org.jouvieje.FmodEx.Defines.PLATFORMS.WIN64;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_FFT_WINDOW.FMOD_DSP_FFT_WINDOW_TRIANGLE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_RESAMPLER.FMOD_DSP_RESAMPLER_NOINTERP;
import static org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE.FMOD_OUTPUTTYPE_ASIO;
import static org.jouvieje.FmodEx.Enumerations.FMOD_PLUGINTYPE.FMOD_PLUGINTYPE_DSP;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_PLUGIN;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCM16;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newFloatBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newIntBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_SHORT;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.DSP;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_OUTPUTTYPE;
import org.jouvieje.FmodEx.Enumerations.FMOD_PLUGINTYPE;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Structures.FMOD_CREATESOUNDEXINFO;

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
public class DspPluginViewer extends DspPluginViewerFrame
{
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_OK)
		{
			String errstring = String.format("FMOD error! (%d)\n%s", result.asInt(), FmodEx.FMOD_ErrorString(result));
			JOptionPane.showMessageDialog(this, errstring, "FMOD error!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/*
	 * Globals
	 */
	System      gSystem             = new System();
	Sound       gSound              = new Sound();
	Channel     gChannel            = new Channel();
	Sound       gSoundRecord        = new Sound();
	Channel     gChannelRecord      = new Channel();
	Plugin      gPluginHead = new Plugin();
	
	public DspPluginViewer()
	{
		super();
		
		gPluginHead.next = gPluginHead;
		gPluginHead.prev = gPluginHead;
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				FMOD_RESULT result;
				
				getTimer().stop();
				
				/*
				 * Shutdown FMOD
				 */
				if(!gSound.isNull())
				{
					result = gSound.release();
					ErrorCheck(result);
				}
				if(!gSystem.isNull())
				{
					result = gSystem.close();
					ErrorCheck(result);
					
					result = gSystem.release();
					ErrorCheck(result);
				}
			}
		});
		
		this.getTimer().start();
		
//		initialize();
	}
	
	private boolean initialized = false;
	private Timer timer = null;
	protected Timer getTimer()
	{
		if(timer == null)
		{
			timer = new Timer(10, new ActionListener(){
				public void actionPerformed(ActionEvent e)
				{
					if(!initialized)
						if(initialize())
							initialized = true;
						else
							exit(0);
					
					updateGui();
				}
			});
		}
		return timer;
	}
	
	public boolean initialize()
	{
		FMOD_RESULT result;
		int version;
		
		/*
		 * Initialise FMOD
		 */
		
		result = FmodEx.Memory_Initialize(newByteBuffer(10 * 1024*1024), 10 * 1024*1024, null, null, null, 0);
		ErrorCheck(result);
		
		result = FmodEx.System_Create(gSystem);
		ErrorCheck(result);
		
		IntBuffer buffer = newIntBuffer(1);
		result = gSystem.getVersion(buffer);
		ErrorCheck(result);
		version = buffer.get(0);
		
		if(version < FMOD_VERSION)
		{
			JOptionPane.showMessageDialog(this, "INCORRECT DLL VERSION!!", "FMOD ERROR", JOptionPane.ERROR_MESSAGE);
		}
		
		result = gSystem.setPluginPath("Plugins");
		ErrorCheck(result);
		
		if(FmodEx.getPlatform() == WIN32)
		{
			/*
			 * Load up an extra plugin that is not normally used by FMOD.
			 */
			result = gSystem.loadPlugin("codec_raw.dll", null, null);
			ErrorCheck(result);
		}
		else if(FmodEx.getPlatform() == WIN64)
		{
			/*
			 * Load up an extra plugin that is not normally used by FMOD.
			 */
			result = gSystem.loadPlugin("codec_raw64.dll", null, null);
			ErrorCheck(result);
		}
		else if(FmodEx.getPlatform() == LINUX || FmodEx.getPlatform() == LINUX64)
		{
			/*
			 * Load up an extra plugin that is not normally used by FMOD.
			 */
			result = gSystem.loadPlugin("codec_raw.so", null, null);
			ErrorCheck(result);
		}
		else if(FmodEx.getPlatform() == MAC)
		{
			/*
			 * Load up an extra plugin that is not normally used by FMOD.
			 */
			result = gSystem.loadPlugin("codec_raw.dylib", null, null);
			ErrorCheck(result);
		}
			
		/*
		 * Display selection dialog
		 */
		DspPluginViewerDialog dialog = new DspPluginViewerDialog();
		dialog.setVisible(true);
		if(dialog.getOutputType() == null)
		{
			return false;
		}
		gSystem.setOutput(dialog.getOutputType()); 
		dialog = null;
		
	    /*
	     * Set the output rate to 44100 instead of the default 48000, because some winamp plugins wont work unless they are 44100.
	     */
		/*
		 * Jouvieje note:
		 *  I've modified this line because in my Linux system, it return the error 46
		 *  (Soundcard does not support the minimum feature needed for this soundsystem (16bit stereo output)).
		 */
//		result = gSystem.setSoftwareFormat(44100, FMOD_SOUND_FORMAT_PCM16, 0, 0, FMOD_DSP_RESAMPLER_LINEAR);
		result = gSystem.setSoftwareFormat(44100, FMOD_SOUND_FORMAT_PCM16, 0, 0, FMOD_DSP_RESAMPLER_NOINTERP);
		ErrorCheck(result);
		
	    /*
	     * Initialize FMOD.
	     */
		result = gSystem.init(32, FMOD_INIT_NORMAL, null);
		ErrorCheck(result);
		if(result != FMOD_OK)
		{
			return false;
		}
		
		/*
		 * Create window and graphics 
		 */
//		this.getTimer().start();
		this.setVisible(true);
		
		return true;
	}
	
	/*
	 * Structures
	 */
	class PluginParam
	{
		DspPluginViewerParamPanel paramPanel;
		Plugin  plugin;
		int     index;
		FloatBuffer min = newFloatBuffer(1);
		FloatBuffer max = newFloatBuffer(1);
		
		ByteBuffer paramname = newByteBuffer(32);
		ByteBuffer label = newByteBuffer(32);
		ByteBuffer valuestr = newByteBuffer(32);
		FloatBuffer value = newFloatBuffer(1);
	}
	
	class Plugin
	{
		//linked list node.
		Plugin next;
		Plugin prev;
		
		DSP dsp = new DSP();
		ByteBuffer name = newByteBuffer(32);
		IntBuffer version = newIntBuffer(1);
		IntBuffer numparams = newIntBuffer(1);
		Vector<PluginParam> params = new Vector<PluginParam>();
		boolean configactive = false;
		
		//Visual
		DspPluginViewerPluginPanel pluginPanel;
		Component configguihwnd;
		IntBuffer configwidth = newIntBuffer(1);
		IntBuffer configheight = newIntBuffer(1);
		
		void createPluginPanel()
		{
			pluginPanel = new DspPluginViewerPluginPanel(this){
				public void setActive(DspPluginViewer.Plugin plugin, boolean active)
				{
					if(active)
					{
						gSystem.addDSP(plugin.dsp, null);
					}
					else
					{
						plugin.dsp.remove();
					}
				}
				public void remove(DspPluginViewer.Plugin plugin)
				{
					RemovePlugin(plugin);
				}
				public void showConfig(DspPluginViewer.Plugin plugin)
				{
					if(!plugin.dsp.isNull())
					{
						if(plugin.configactive)
						{
							pluginPanel.removeConfigDialog(configguihwnd);
							DspPluginViewer.this.getPluginScrollPane().validate();
							
							FMOD_RESULT result = plugin.dsp.showConfigDialog(plugin.configguihwnd, false);
							ErrorCheck(result);
							
							plugin.configactive = false;
							plugin.configguihwnd = null;
						}
						else
						{
							plugin.configactive = true;
							
							configguihwnd = new Canvas();
							configguihwnd.setSize(new Dimension(configwidth.get(0), configheight.get(0)));
							configguihwnd.setMinimumSize(new Dimension(configwidth.get(0), configheight.get(0)));
							configguihwnd.setMaximumSize(new Dimension(configwidth.get(0), configheight.get(0)));
							
							pluginPanel.addConfigDialog(configguihwnd);
							DspPluginViewer.this.getPluginScrollPane().validate();
							
							FMOD_RESULT result = plugin.dsp.showConfigDialog(plugin.configguihwnd, true);
							ErrorCheck(result);
						}
					}
				}
			};
			
			for(int i = 0; i < params.size(); i++)
			{
				params.get(i).paramPanel = new DspPluginViewerParamPanel(params.get(i)) {
					protected void paramValueChange(DspPluginViewer.PluginParam pluginParam, float value)
					{
						pluginParam.plugin.dsp.getParameterInfo(pluginParam.index, null, pluginParam.label, null, 0, null, null);
						pluginParam.plugin.dsp.setParameter(pluginParam.index, value);
						pluginParam.plugin.dsp.getParameter(pluginParam.index, null, pluginParam.valuestr, 32);
					}
				};
				pluginPanel.addPluginParameter(params.get(i));
			}
		}
	}
	
	protected void addPlugins()
	{
		int choice = getPluginChooser().showOpenDialog(this);
		if(choice == JFileChooser.APPROVE_OPTION)
		{
			File[] files = getPluginChooser().getSelectedFiles();
			if(files == null)
				return;
			
			for(int i = 0; i < files.length; i++)
			{
				/*
				 * Put path in name
				 */
				String path = files[i].getPath();
				String fileName = files[i].getName();
				
				/*
				 * Open the file.
				 */
				FMOD_RESULT result = addPlugin(path, fileName);
				ErrorCheck(result);
				
				try {
					Thread.sleep(1);
				} catch(InterruptedException e){}
			}
		}
		return;
	}
	
	private IntBuffer indexBuffer = newIntBuffer(1);
	FMOD_RESULT addPlugin(String pluginPath, String pluginName)
	{
		FMOD_PLUGINTYPE[] plugintype = new FMOD_PLUGINTYPE[1];
		FMOD_RESULT result;
		
		result = gSystem.loadPlugin(pluginPath, plugintype, indexBuffer);
		if(result != FMOD_OK)
		{
			return result;
		}
		int index = indexBuffer.get(0);
		
		if(plugintype[0] != FMOD_PLUGINTYPE_DSP)
		{
			gSystem.unloadPlugin(plugintype[0], index);
			return FMOD_ERR_PLUGIN;
		}
		
		Plugin plugin = new Plugin();
		
		result = gSystem.createDSPByIndex(index, plugin.dsp);
		if(result != FMOD_OK)
		{
			plugin = null;
			return result;
		}
		
		/*
		 * Add to end of circular linked list.
		 */
		plugin.prev = gPluginHead.prev;
		gPluginHead.prev.next = plugin;
		gPluginHead.prev = plugin;
		plugin.next = gPluginHead;
		
		/*
		 * Plugin informations
		 */
		plugin.dsp.getInfo(plugin.name, plugin.version, null, plugin.configwidth, plugin.configheight);
		
		/*
		 * Parameters informations
		 */
		plugin.dsp.getNumParameters(plugin.numparams);
		for(int i = 0; i < plugin.numparams.get(0); i++)
		{
			PluginParam param = new PluginParam();
			param.plugin = plugin;
			param.index  = i;
			
			plugin.dsp.getParameterInfo(i, param.paramname, param.label, null, 0, param.min, param.max);
			plugin.dsp.getParameter(i, param.value, param.valuestr, 32);
			
			plugin.params.add(param);
		}
		
		/*
		 * Create the plugin panel
		 */
		plugin.createPluginPanel();
		
		/*
		 * Add the plugin panel to the plugin list
		 */
		this.addPluginInList(plugin);
		
		return FMOD_OK;
	}
	
	FMOD_RESULT RemovePlugin(Plugin plugin)
	{
		plugin.next.prev = plugin.prev;
		plugin.prev.next = plugin.next;
		
		//Ensure that the config dialog is removed
		if(plugin.configactive)
		{
			plugin.pluginPanel.removeConfigDialog(plugin.configguihwnd);
			DspPluginViewer.this.getPluginScrollPane().validate();
			
			plugin.dsp.showConfigDialog(plugin.configguihwnd, false);
			
			plugin.configactive = false;
			plugin.configguihwnd = null;
		}
		
		FMOD_RESULT result = plugin.dsp.release();
		ErrorCheck(result);
		
		removePlugin(plugin);
		
		plugin = null;
		
		return FMOD_OK;
	}
	
	protected void open()
	{
		/*
		 * Display the Open dialog box.
		 */
		int choice = getMusicChooser().showOpenDialog(this);
		
		if(choice == JFileChooser.APPROVE_OPTION)
		{
			File file = getMusicChooser().getSelectedFile();
			
			FMOD_RESULT result;
			
			if(!gSound.isNull())
			{
				if(!gChannel.isNull())
				{
					gChannel.stop();
					gChannel = new Channel();
					getPlayStopButton().setText("Play");
				}
				gSound.release();
			}
			result = gSystem.createStream(file.getPath(), FMOD_2D | FMOD_SOFTWARE, null, gSound);
			ErrorCheck(result);
			
			getFileName().setText(file.getPath());
		}
	}
	protected void playStop()
	{
		boolean isplaying = false;
		
		if(!gChannel.isNull())
		{
			ByteBuffer buffer = newByteBuffer(1);
			gChannel.isPlaying(buffer);
			isplaying = buffer.get(0) != 0;
		}
		
		if(isplaying || gSound.isNull())
		{
			if(!gChannel.isNull())
			{
				gChannel.stop();
				gChannel = new Channel();
			}
			getPlayStopButton().setText("Play");
		}
		else
		{
			gSystem.playSound(FMOD_CHANNEL_FREE, gSound, false, gChannel);
			getPlayStopButton().setText("Stop");
		}
	}
	protected void pause()
	{
		if(!gChannel.isNull())
		{
			ByteBuffer paused = newByteBuffer(1);
			
			gChannel.getPaused(paused);
			if(paused.get(0) != 0)
			{
				gChannel.setPaused(false);
				getPauseButton().setText("Pause");
			}
			else
			{
				gChannel.setPaused(true);
				getPauseButton().setText("UnPause");
			}
		}
	}
	protected void record()
	{
		if(gSoundRecord.isNull())
		{
			FMOD_RESULT result;
			FMOD_OUTPUTTYPE[] output = new FMOD_OUTPUTTYPE[1];
			
			int mode = FMOD_2D | FMOD_OPENUSER;
			mode |= FMOD_LOOP_NORMAL;
			mode |= FMOD_SOFTWARE;
			
			final int USERLENGTH = 44100 * 5;
			
			FMOD_CREATESOUNDEXINFO exinfo = FMOD_CREATESOUNDEXINFO.create();
			exinfo.setLength(USERLENGTH * 2 * SIZEOF_SHORT);
			exinfo.setNumChannels(2);
			exinfo.setDefaultFrequency(44100);
			exinfo.setFormat(FMOD_SOUND_FORMAT_PCM16);
			
			result = gSystem.createSound((String)null, mode, exinfo, gSoundRecord);
			ErrorCheck(result);
			
			result = gSystem.recordStart(gSoundRecord, true);
			ErrorCheck(result);
			
			gSystem.getOutput(output);
			if(output[0] != FMOD_OUTPUTTYPE_ASIO)
			{
				try {
					Thread.sleep(100);
				} catch(InterruptedException e){}
			}
			
			result = gSystem.playSound(FMOD_CHANNEL_FREE, gSoundRecord, false, gChannelRecord);
			ErrorCheck(result);
			
			getRecordButton().setText("Stop Recording");
		}
		else
		{
			gSystem.recordStop();
			
			gChannelRecord.stop();
			gChannelRecord = new Channel();
			gSoundRecord.release();
			gSoundRecord = new Sound();
			
			getRecordButton().setText("Record");
		}
	}
	
	protected void updateProgression(int position)
	{
		if(!gSound.isNull() && !gChannel.isNull())
		{
			FMOD_RESULT  result;
			IntBuffer length = newIntBuffer(1);
			
			result = gSound.getLength(length, FMOD_TIMEUNIT_MODORDER);
			if(result != FMOD_OK)
			{
				gSound.getLength(length, FMOD_TIMEUNIT_MS);
			}
			
			int currentPosition = (int)( position * length.get(0) / 1000.0f );
			
			result = gChannel.setPosition(currentPosition, FMOD_TIMEUNIT_MODORDER);
			if(result != FMOD_OK)
			{
				gChannel.setPosition(currentPosition, FMOD_TIMEUNIT_MS);
			}
		}
	}
	
	private ByteBuffer bufferGui = newByteBuffer(SIZEOF_INT);
	protected void updateGui()
	{
		gSystem.update();
		
		plotSpectrum(true);
		plotOscilliscope();
		
		if(!gChannel.isNull())
		{
			FMOD_RESULT result;
			int currTime, length;
			boolean playing;
			
			result = gChannel.getPosition(bufferGui.asIntBuffer(), FMOD_TIMEUNIT_MODORDER);
			if(result != FMOD_OK)
			{
				gChannel.getPosition(bufferGui.asIntBuffer(), FMOD_TIMEUNIT_MS);
			}
			currTime = bufferGui.getInt(0);
			
			result = gSound.getLength(bufferGui.asIntBuffer(), FMOD_TIMEUNIT_MODORDER);
			if(result != FMOD_OK)
			{
				gSound.getLength(bufferGui.asIntBuffer(), FMOD_TIMEUNIT_MS);
			}
			length = bufferGui.getInt(0);
			
			getProgressBar().setValue((int)( (float)currTime / (float)length * 1000.0f ));
			
			gChannel.isPlaying(bufferGui);
			playing = bufferGui.get(0) != 0;
			if(!playing)
			{
				gChannel = new Channel();
				getPlayStopButton().setText("Play");
			}
		}
	}
	
	private FloatBuffer[] spectrumBuffer = new FloatBuffer[8];
	private int spectrumBufferLength = 0;
	private IntBuffer numChannels = newIntBuffer(1);
	private int grey = 0x404040;
	private void plotSpectrum(boolean uselog)
	{
		FMOD_RESULT result = gSystem.getSoftwareFormat(null, null, numChannels, null, null, null);
		if(result != FMOD_OK)
		{
			return;
		}
		if(spectrumBufferLength < numChannels.get(0))
		{
			for(int i = spectrumBufferLength; i < numChannels.get(0); i++)
			{
				spectrumBuffer[i] = newFloatBuffer(512);
			}
			spectrumBufferLength = numChannels.get(0);
		}
		
		//Draw spectrum offscreen
		BufferedImage image = new BufferedImage(GRAPHICWINDOW_WIDTH, GRAPHICWINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		/*
		 * Draw a black square with grey lines through it.
		 */
		for(int x = 0; x < GRAPHICWINDOW_WIDTH; x++)
		{
	        for(int y = 0; y < 30; y++)
	        {
				image.setRGB(x, GRAPHICWINDOW_HEIGHT * y / 30, grey);
			}
		}
		for(int x = 0; x < GRAPHICWINDOW_WIDTH; x += 64)
		{
	        for(int y = 0; y < GRAPHICWINDOW_HEIGHT; y++)
	        {
				image.setRGB(x, y, grey);
	        }
		}
		
		float max = 0;
		for(int i = 0; i < numChannels.get(0); i++)
		{
			//returns an array of 512 floats
			result = gSystem.getSpectrum(spectrumBuffer[i], 512, i, FMOD_DSP_FFT_WINDOW_TRIANGLE);
			if(result != FMOD_OK)
			{
				return;
			}
			
			{    
				for(int j = 0; j < 512; j++)
				{
					if(spectrumBuffer[i].get(j) > max)
					{
						max = spectrumBuffer[i].get(j);
					}
				}        
			}
			
			if(max > 0.0001f)
			{
				/*
				 * Spectrum graphic is 256 entries wide, and the spectrum is 512 entries.
				 * The upper band of frequencies at 44khz is pretty boring (ie 11-22khz), so we are only
				 * going to display the first 256 frequencies, or (0-11khz)
				 */
				for(int x = 0; x < 512; x++)
				{
					float val, db;
					int y;
					
					val = spectrumBuffer[i].get(x);
					
					if(uselog)
					{
						/*
						 * 1.0   = 0db
						 * 0.5   = -6db
						 * 0.25  = -12db
						 * 0.125 = -24db
						 */
						db = 10.0f * (float)Math.log10(val) * 2.0f;
						
						val = db;
						if(val < -150)
						{
							val = -150;
						}
						
						val /= -150.0f;
						val = 1.0f - val;
						
						y = (int)(val * GRAPHICWINDOW_HEIGHT);
					}
					else
					{
						y = (int)(val / max * GRAPHICWINDOW_HEIGHT);
					}
					
					if(y >= GRAPHICWINDOW_HEIGHT)
					{
						y = GRAPHICWINDOW_HEIGHT - 1;
					}
					
					for(int j = 0; j < y; j++)
					{
						int r,g,b;
						
						r = (j << 1);
						g = 0xFF - (j << 1);
						b = 0x1F;
						
						image.setRGB(x * GRAPHICWINDOW_WIDTH / 512, GRAPHICWINDOW_HEIGHT-1-j, (r << 16) + (g << 8) + b);
					}
				}
			}
		}
		
		//Draw the spectrum on the screen
		getSpectrumPanel().getGraphics().drawImage(image, 0, 0, null);
	}
	
	private FloatBuffer oscBuffer = newFloatBuffer(GRAPHICWINDOW_WIDTH);
	private int oscColor = 0xffffaf;
	private void plotOscilliscope()
	{
		float xoff, step;
		
		FMOD_RESULT result = gSystem.getSoftwareFormat(null, null, numChannels, null, null, null);
		if(result != FMOD_OK)
		{
			return;
		}
		
		//Draw spectrum offscreen
		BufferedImage image = new BufferedImage(GRAPHICWINDOW_WIDTH, GRAPHICWINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		for(int channel = 0; channel < numChannels.get(0); channel++)
		{
			gSystem.getWaveData(oscBuffer, GRAPHICWINDOW_WIDTH, channel);
			
			/*
			 * xoff is the x position that is scaled lookup of the dsp block according to the graphical
			 * window size.
			 */
			xoff = 0;
			step = 1;
			
			for(int i = 0; i < GRAPHICWINDOW_WIDTH-1; i++)
			{
				int x, y, y2;
				
				x  = (int)xoff;
				y  = (int)((oscBuffer.get(x)           + 1.0f) / 2.0f * GRAPHICWINDOW_HEIGHT);
				y2 = (int)((oscBuffer.get(x+(int)step) + 1.0f) / 2.0f * GRAPHICWINDOW_HEIGHT);
				
				y  = y  < 0 ? 0 : y  >= GRAPHICWINDOW_HEIGHT ? GRAPHICWINDOW_HEIGHT-1 : y;
				y2 = y2 < 0 ? 0 : y2 >= GRAPHICWINDOW_HEIGHT ? GRAPHICWINDOW_HEIGHT-1 : y2;
				
				if(y > y2)
				{
					int tmp = y;
					y = y2;
					y2 = tmp;
				}
				
				for(int j = y; j <= y2; j++)
				{
					image.setRGB(i, j, oscColor);
				}
				
				xoff += step;
			}
		}
		
		//Draw the oscilloscope on the screen
		getOsciloscopePanel().getGraphics().drawImage(image, 0, 0, null);
	}
	
	public static void main(String[] args)
	{
		/*
		 * NativeFmodEx Init
		 */
		try
		{
			Init.loadLibraries(INIT_MODES.INIT_FMOD_EX);
		}
		catch(InitException e)
		{
			out.printf("NativeFmodEx error! %s\n", e.getMessage());
			exit(1);
		}
		
		/*
		 * Checking NativeFmodEx version
		 */
		if(NATIVEFMODEX_LIBRARY_VERSION != NATIVEFMODEX_JAR_VERSION)
		{
			out.printf("Error!  NativeFmodEx library version (%08x) is different to jar version (%08x)\n", NATIVEFMODEX_LIBRARY_VERSION, NATIVEFMODEX_JAR_VERSION);
			exit(0);
		}
		
		/*==================================================*/
		
		new DspPluginViewer();
		
//		DspPluginViewer pluginViewer = new DspPluginViewer();
//		if(!pluginViewer.initialize())
//		{
//			exit(0);
//		}
	}
}