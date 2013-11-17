/*===============================================================================================
Copyright (c), Firelight Technologies Pty, Ltd 2004-2008.

This example shows the virtual channels of FMOD. The listener and sounce sources can be moved
around by clicking and dragging. Sound sources will change colour depending on whether they
are virtual or not. Red means they are real, blue means they are virtual channels.
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static java.lang.System.exit;
import static java.lang.System.out;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_3D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newIntBuffer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.swing.JOptionPane;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Structures.FMOD_VECTOR;

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
public class VirtualVoices extends VirtualVoicesFrame
{
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_OK)
		{
			String errstring = String.format("FMOD error! (%d)\n%s", result.asInt(), FmodEx.FMOD_ErrorString(result));
			JOptionPane.showMessageDialog(this, errstring, "FMOD error!", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	static
	{
		/*
		 * NativeFmodEx Init
		 */
		try
		{
			Init.loadLibraries(INIT_MODES.INIT_FMOD_EX/*_MINIMUM*/);
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
	}
	
	private int NUMCHANNELS     = 50;
	private int NUMREALCHANNELS = 10;
	private int SOURCESIZE      = 5;
	private int LISTENERSIZE    = 15;
	private int SELECTTHRESHOLD = 5;
	
	private boolean       gSelected       = false;
	private SoundSource   gSelectedSource = null;
	private SoundSource[] gSoundSource    = new SoundSource[NUMCHANNELS];
	private Listener      gListener;
	
	private Random random;
	
	private System gSystem = new System();
	private Sound  gSound  =  new Sound();
	
	public class SoundSource
	{
		Channel mChannel = new Channel();
		FMOD_VECTOR mPos, mVel;
		Color mBrushBlue = new Color(0, 0, 255);
		Color mBrushRed = new Color(255, 0, 0);
		ByteBuffer buffer = newByteBuffer(1);
		
		SoundSource(float posx, float posy)
		{
			FMOD_RESULT result;
			
			mPos = FMOD_VECTOR.create(posx, posy, 0.0f);
			mVel = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);
			
			result = gSystem.playSound(FMOD_CHANNEL_FREE, gSound, true, mChannel);
			ErrorCheck(result);
			
			setPosition(posx, posy);
			
			result = mChannel.setFrequency(22050.0f + random.nextInt(88200));
			ErrorCheck(result);
			
			result = mChannel.setPaused(false);
			ErrorCheck(result);
		}
		
		protected void draw(Graphics g)
		{
			FMOD_RESULT result = mChannel.isVirtual(buffer);
			ErrorCheck(result);
			boolean isvirtual = buffer.get(0) != 0;
			
			if(isvirtual)
			{
				g.setColor(mBrushBlue);
			}
			else
			{
				g.setColor(mBrushRed);
			}
			
			g.fillOval((int)mPos.getX()-SOURCESIZE, (int)mPos.getY()-SOURCESIZE, 2*SOURCESIZE, 2*SOURCESIZE);
		}
		
		void setPosition(float posx, float posy)
		{
			mPos.setX(posx);
			mPos.setY(posy);
			
			FMOD_RESULT result = mChannel.set3DAttributes(mPos, mVel);
			ErrorCheck(result);
		}
		
		FMOD_VECTOR GetPosition()
		{
			return mPos;
		}
		
		boolean isSelected(float posx, float posy)
		{
			if(posx > mPos.getX() - SOURCESIZE - SELECTTHRESHOLD &&
					posx < mPos.getX() + SOURCESIZE + SELECTTHRESHOLD &&
					posy > mPos.getY() - SOURCESIZE - SELECTTHRESHOLD &&
					posy < mPos.getY() + SOURCESIZE + SELECTTHRESHOLD)
			{
				return true;
			}
			
			return false;
		}
	};
	
	public class Listener
	{
		FMOD_VECTOR mListenerPos;
		FMOD_VECTOR mUp, mVel, mForward;
		Color mBrush = new Color(0, 0, 0);
		
		Listener(float posx, float posy)
		{
			mUp      = FMOD_VECTOR.create(0,  0, 1);
			mForward = FMOD_VECTOR.create(0, -1, 0);
			mVel     = FMOD_VECTOR.create(0,  0, 0);
			
			mListenerPos = FMOD_VECTOR.create();
			setPosition(posx, posy);
		}
		
		protected void draw(Graphics g)
		{
			g.setColor(mBrush);
			
			//head
			g.fillOval((int)(mListenerPos.getX() - LISTENERSIZE),
                    (int)(mListenerPos.getY() - LISTENERSIZE),
					2 * LISTENERSIZE,
					2 * LISTENERSIZE);
			
			//nose
			g.fillOval((int)(mListenerPos.getX() - (LISTENERSIZE / 3)),
					(int)(mListenerPos.getY() - (LISTENERSIZE / 3) - LISTENERSIZE),
					2 * LISTENERSIZE / 3,
					2 * LISTENERSIZE / 3);
			
			//left ear
			g.fillOval((int)(mListenerPos.getX() - (LISTENERSIZE / 3) - LISTENERSIZE),
					(int)(mListenerPos.getY() - (LISTENERSIZE / 3)),
					2 * LISTENERSIZE / 3,
					2 * LISTENERSIZE / 3);
			
			//right ear
			g.fillOval((int)(mListenerPos.getX() - (LISTENERSIZE / 3) + LISTENERSIZE),
					(int)(mListenerPos.getY() - (LISTENERSIZE / 3)),
					2 * LISTENERSIZE / 3,
					2 * LISTENERSIZE / 3);
		}
		
		void setPosition(float posx, float posy)
		{
			mListenerPos.setX(posx);
			mListenerPos.setY(posy);
			
			FMOD_RESULT result = gSystem.set3DListenerAttributes(0, mListenerPos, mVel, mForward, mUp);
			ErrorCheck(result);
		}
		
		FMOD_VECTOR getPosition()
		{
			return mListenerPos;
		}
		
		boolean isSelected(float posx, float posy)
		{
			if(posx > mListenerPos.getX() - LISTENERSIZE - SELECTTHRESHOLD &&
					posx < mListenerPos.getX() + LISTENERSIZE + SELECTTHRESHOLD &&
					posy > mListenerPos.getY() - LISTENERSIZE - SELECTTHRESHOLD &&
					posy < mListenerPos.getY() + LISTENERSIZE + SELECTTHRESHOLD)
			{
				return true;
			}
			
			return false;
		}
	};
	
	public VirtualVoices()
	{
		super();
		
		this.addKeyListener(new KeyAdapter(){
			public void keyReleased(KeyEvent e)
			{
				switch(e.getKeyCode())
				{
					case KeyEvent.VK_ESCAPE: close(); exit(-1); break;
				}
			}
		});
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e)
			{
				close();
			}
		});
	}
	private void close()
	{
		FMOD_RESULT result;
		
		getTimer().stop();
		try {
			Thread.sleep(200);
		} catch(InterruptedException e1){}
		
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
	
	public boolean initialize()
	{
		FMOD_RESULT  result;
		int          version;
		IntBuffer buffer = newIntBuffer(1);
		
		/*
		 * Create a System object and initialize.
		 */
		result = FmodEx.System_Create(gSystem);
		ErrorCheck(result);
		
		/*
		 * Initialise
		 */
		result = gSystem.getVersion(buffer);
		ErrorCheck(result);
		version = buffer.get(0);
		
		if(version < FMOD_VERSION)
		{
			JOptionPane.showMessageDialog(this, "INCORRECT DLL VERSION!!", "FMOD ERROR", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
	    result = gSystem.setSoftwareChannels(NUMREALCHANNELS);
	    ErrorCheck(result);

	    result = gSystem.init(NUMCHANNELS, FMOD_INIT_NORMAL, null);
	    ErrorCheck(result);
		
		result = gSystem.createSound("Media/drumloop.wav", FMOD_SOFTWARE | FMOD_3D | FMOD_LOOP_NORMAL, null, gSound);
		ErrorCheck(result);
		result = gSound.set3DMinMaxDistance(4.0f, 10000.0f);
		ErrorCheck(result);
		
		random = new Random(java.lang.System.currentTimeMillis());
		
		/*
		 * Create a listener in the middle of the window
		 */
		gListener = new Listener(WIDTH/2, HEIGHT/2);
		
		/*
		 * Initialise all of the sound sources
		 */
		for(int count = 0; count < NUMCHANNELS; count++)
		{
			int x = random.nextInt(WIDTH);
			int y = random.nextInt(HEIGHT);
			
			gSoundSource[count] = new SoundSource(x, y);
		}
		
		this.setVisible(true);
		this.getTimer().start();
		return true;
	}
	
	public void ontimer()
	{
		getJContentPane().repaint();
		gSystem.update();
	}
	
	private IntBuffer channels = newIntBuffer(1);
	public void draw(Graphics g)
	{
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		/*
		 * Draw the sound sources
		 */
		for(int i = 0; i < NUMCHANNELS; i++)
		{
			gSoundSource[i].draw(g);
		}
		
		/*
		 * Draw the listener
		 */
		gListener.draw(g);
		
		/*
		 * Print some information
		 */
		FMOD_RESULT result = gSystem.getChannelsPlaying(channels);
		ErrorCheck(result);
		
		g.drawString("Channels Playing: "+channels.get(0), 5, 15);
		g.drawString("Real Channels:    "+NUMREALCHANNELS+" (RED)", 5, 32);
		g.drawString("Virtual Channels: "+(NUMCHANNELS - NUMREALCHANNELS)+" (BLUE)", 5, 49);
		
		g.drawString("Drag the listener or a source with the mouse ...", 5, getJContentPane().getHeight()-10);
	}
	
	public void mousePressed(MouseEvent e)
	{
		gSelectedSource = null;
		
		/*
		 * Check if listener was selected
		 */
		if(gListener.isSelected(e.getX(), e.getY()))
		{
			gSelected = true;
			return;
		}
		/*
		 * Check if a soundsource was selected
		 */
		for(int i = 0; i < NUMCHANNELS; i++)
		{
			if(gSoundSource[i].isSelected(e.getX(), e.getY()))
			{
				gSelectedSource = gSoundSource[i];
				gSelected = true;
				return;
			}
		}
	}
	public void mouseReleased(MouseEvent e)
	{
		gSelected  = false;
	}
	public void mouseDragged(MouseEvent e)
	{
		if(gSelected)
		{
			if(gSelectedSource != null)
			{
				gSelectedSource.setPosition(e.getX(), e.getY());
			}
			else
			{
				gListener.setPosition(e.getX(), e.getY());
			}
		}
	}
	
	public static void main(String[] args)
	{
		VirtualVoices pluginViewer = new VirtualVoices();
		if(!pluginViewer.initialize())
		{
			exit(0);
		}
	}
}