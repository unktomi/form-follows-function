/*===============================================================================================
geometry.exe main.cpp
Copyright (c), Firelight Technologies Pty, Ltd 2005.

Example to show occlusion
===============================================================================================*/

package org.jouvieje.FmodEx.Examples;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;
import static java.lang.System.out;
import static org.jouvieje.FmodEx.Defines.FMOD_CAPS.FMOD_CAPS_HARDWARE_EMULATED;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_3D_RIGHTHANDED;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_SOFTWARE_HRTF;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_SOFTWARE_OCCLUSION;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_3D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_RESAMPLER.FMOD_DSP_RESAMPLER_LINEAR;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_OUTPUT_CREATEBUFFER;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCMFLOAT;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SPEAKERMODE.FMOD_SPEAKERMODE_STEREO;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newByteBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newFloatBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newIntBuffer;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_FLOAT;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Geometry;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Enumerations.FMOD_SPEAKERMODE;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.Structures.FMOD_CREATESOUNDEXINFO;
import org.jouvieje.FmodEx.Structures.FMOD_VECTOR;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;

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
public class Geometry_ implements GLEventListener, WindowListener, KeyListener, MouseListener, MouseMotionListener
{
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_OK)
		{
			out.printf("FMOD error! (%d) %s\n", result.asInt(), FmodEx.FMOD_ErrorString(result));
			JOptionPane.showMessageDialog(null, "FMOD error! ("+result.asInt()+")"+FmodEx.FMOD_ErrorString(result));
			exit(1);
		}
	}
	
	public static void main(String[] args)
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
		
		/*==================================================*/
		
		//Initialization of the Frame
		JFrame frame = new JFrame("FMOD Geometry example.");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(500, 500);
		frame.setLocationRelativeTo(null);
		
		/**
		 * Creates the OpenGl scene using Jogl
		 */
		GLCanvas glScene = new GLCanvas();
		final Geometry_ glEvents = new Geometry_();
		glScene.addGLEventListener(glEvents);
		
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e)
			{
				glEvents.keyReleased(new KeyEvent((Component)e.getSource(), e.getID(),
						0, 0,
						KeyEvent.VK_ESCAPE, KeyEvent.CHAR_UNDEFINED));
			}
		});
		
		frame.getContentPane().add(glScene, BorderLayout.CENTER);
		
		frame.setVisible(true);
		glScene.requestFocus();
	}
	
	private Animator animator;
	
	//mouse control
	boolean doRotate = false;
	int xMouse = 0;
	int yMouse = 0;
	
	//listener orientation
	float xRotation = 0.0f;
	float yRotation = -90.0f;

	//listerer position
	float xListenerPos = 30.0f;
	float yListenerPos = 1.3f;
	float zListenerPos = 0.4f;

	//keyboard control
	boolean moveForward = false;
	boolean moveBackward = false;
	boolean moveLeft = false;
	boolean moveRight = false;
	boolean moveUp = false;
	boolean moveDown = false;

	float accumulatedTime = 0.0f;

	private int texture = 0;

	//sounds placement
	class Object3D
	{
		float xPos;
		float yPos;
		float zPos;
		float intensity;
		int sound;
		Channel channel;
		public Object3D(){}
		public Object3D(float xPos, float yPos, float zPos, float intensity, int sound, Channel channel)
		{
			this.xPos = xPos;
			this.yPos = yPos;
			this.zPos = zPos;
			this.intensity = intensity;
			this.sound = sound;
			this.channel = channel;
		}
	}
	Object3D[] objects = new Object3D[] {
			new Object3D( -11.0f, 1.0f,   0.0f, 1.0f, 0, new Channel()),
			new Object3D(  12.0f, 2.0f,   0.0f, 1.0f, 1, new Channel()),
			new Object3D(  45.0f, 1.0f,   0.0f, 1.0f, 3, new Channel()),
			new Object3D( -30.0f, 1.0f,  21.0f, 1.0f, 2, new Channel()),
			new Object3D( -30.0f, 1.0f, -21.0f, 1.0f, 3, new Channel()),
			new Object3D(  12.0f, 1.0f, -27.0f, 1.0f, 0, new Channel()),
			new Object3D(   4.0f, 1.0f,  16.0f, 1.0f, 0, new Channel())
	};

	//geometry structers for loading and drawing
	class Polygon
	{
		int numVertices;
		int indicesOffset;
		float directOcclusion;
		float reverbOcclusion;
		FMOD_VECTOR normal = FMOD_VECTOR.create();
	}

	class Mesh
	{
		int numVertices;
		FMOD_VECTOR[] vertices;
		float[][] texcoords;
		int numPolygons;
		Polygon[] polygons;
		int numIndices;
		int[] indices;
		Geometry geometry = new Geometry();
	}
	
	Mesh walls;
	Mesh rotatingMesh;
	Mesh[] doorList = new Mesh[4];
	
	//fmod sounds structures
	System	 fmodSystem = new System();
	Sound[]	 sounds = new Sound[]{new Sound(), new Sound(), new Sound(), new Sound()};
	Geometry geometry = new Geometry();
	
	public void displayChanged(GLAutoDrawable GLAutoDrawable, boolean modeChanged, boolean deviceChanged){}
	
	/**
	 * Reshape method
	 */
	public void reshape(GLAutoDrawable GLAutoDrawable, int x, int y, int width, int height)
	{
		final GL gl = GLAutoDrawable.getGL();
		final GLU glu = new GLU();
		
		if(height <= 0) height = 1;
		
		//set the viewport
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(
				60.0,								// fov
				(float)width / (float)height,		// aspect
				0.1,								// near
				500.0								// far
		);
		
		gl.glMatrixMode(GL.GL_MODELVIEW);							//select The Modelview Matrix
		gl.glLoadIdentity();										//set the ModalView matrix to identity
	}
	
	void initGeometry(String fileName, Mesh mesh)
	{
		FMOD_RESULT result;
		
		ByteBuffer file = loadFileIntoMemory(fileName);
		
		// read vertices
		mesh.numVertices = file.getInt();
		mesh.vertices = new FMOD_VECTOR[mesh.numVertices];
		mesh.texcoords = new float[mesh.numVertices][2];
		for(int i = 0; i < mesh.numVertices; i++)
		{
			mesh.vertices[i] = FMOD_VECTOR.create(file.getFloat(), file.getFloat(), file.getFloat());
		}
		for(int i = 0; i < mesh.numVertices; i++)
		{
			mesh.texcoords[i][0] = file.getFloat();
			mesh.texcoords[i][1] = file.getFloat();
		}
		
		mesh.numIndices = file.getInt();
		mesh.indices = new int[mesh.numIndices];
		for(int i = 0; i < mesh.numIndices; i++)
		{
			mesh.indices[i] = file.getInt();
		}
		
		mesh.numPolygons = file.getInt();
		mesh.polygons = new Polygon[mesh.numPolygons];
		
		// read polygons
		for(int poly = 0; poly < mesh.numPolygons; poly++)
		{
			mesh.polygons[poly] = new Polygon();
			Polygon polygon = mesh.polygons[poly];
			
			polygon.numVertices = file.getInt();
			polygon.indicesOffset = file.getInt();
			polygon.directOcclusion = file.getFloat();
			polygon.reverbOcclusion = file.getFloat();
			
			// calculate polygon normal
			float xN = 0.0f;
			float yN = 0.0f;
			float zN = 0.0f;
			// todo: return an error if a polygon has less then 3 vertices.
			for(int vertex = 0; vertex < polygon.numVertices - 2; vertex++)
			{
				int offset = polygon.indicesOffset;
				float xA = mesh.vertices[mesh.indices[offset + vertex + 1]].getX() - mesh.vertices[mesh.indices[offset]].getX();
				float yA = mesh.vertices[mesh.indices[offset + vertex + 1]].getY() - mesh.vertices[mesh.indices[offset]].getY();
				float zA = mesh.vertices[mesh.indices[offset + vertex + 1]].getZ() - mesh.vertices[mesh.indices[offset]].getZ();
				float xB = mesh.vertices[mesh.indices[offset + vertex + 2]].getX() - mesh.vertices[mesh.indices[offset]].getX();
				float yB = mesh.vertices[mesh.indices[offset + vertex + 2]].getY() - mesh.vertices[mesh.indices[offset]].getY();
				float zB = mesh.vertices[mesh.indices[offset + vertex + 2]].getZ() - mesh.vertices[mesh.indices[offset]].getZ();
				// cross product
				xN += yA * zB - zA * yB;
				yN += zA * xB - xA * zB;
				zN += xA * yB - yA * xB;
			}	
			float fMagnidued = (float)sqrt(xN * xN + yN * yN + zN * zN);
			if (fMagnidued > 0.0f) // a tollerance here might be called for
			{
				xN /= fMagnidued;
				yN /= fMagnidued;
				zN /= fMagnidued;
			}
			polygon.normal.setX(xN);
			polygon.normal.setY(yN);
			polygon.normal.setZ(zN);
		}
		
		result = fmodSystem.createGeometry(mesh.numPolygons, mesh.numIndices, mesh.geometry);
		ErrorCheck(result);
		
		/*
		 * Tell FMOD about the geometry
		 */
		IntBuffer polygonIndex = newIntBuffer(1);
		for(int poly = 0; poly < mesh.numPolygons; poly++)
		{
			Polygon polygon = mesh.polygons[poly];
			
			/*
			 * Note :
			 * Don't use FMOD_VECTOR[16], FMOD_VECTOR.create(16) will create
			 * a contiguous array which is usable by FMOD Ex.
			 */
			FMOD_VECTOR[] vertices = FMOD_VECTOR.create(16);
			for(int i = 0; i < polygon.numVertices; i++)
				vertices[i].setXYZ(mesh.vertices[mesh.indices[polygon.indicesOffset + i]]);
			
			result = mesh.geometry.addPolygon(
					polygon.directOcclusion, 
					polygon.reverbOcclusion, 
					false,						//single sided
					polygon.numVertices, 
					vertices,
					polygonIndex);
			ErrorCheck(result);
		}
	}
	
	void freeGeometry(Mesh mesh)
	{
		mesh.geometry.release();
		
		mesh.vertices = null;
		mesh.texcoords = null;
		mesh.polygons = null;
		mesh.indices = null;
	}
	
	void initObjects()
	{	
		FMOD_RESULT result;
		for(int i = 0; i < objects.length; i++)
		{
			//play object sounds
			FMOD_VECTOR pos = FMOD_VECTOR.create(objects[i].xPos, objects[i].yPos, objects[i].zPos);
			FMOD_VECTOR vel = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);
			
			result = fmodSystem.playSound(FMOD_CHANNEL_FREE, sounds[objects[i].sound], true, objects[i].channel);
			ErrorCheck(result);
			result = objects[i].channel.set3DAttributes(pos, vel);
			ErrorCheck(result);
			result = objects[i].channel.set3DSpread(40.0f);
			ErrorCheck(result);
			result = objects[i].channel.setPaused(false);
			ErrorCheck(result);
			
			pos.release();
			vel.release();
		}
	}
	
	/**
	 * The init method
	 */
	public void init(GLAutoDrawable GLAutoDrawable)
	{
		FMOD_RESULT	result;
		int version;
		FMOD_SPEAKERMODE[] speakermode = new FMOD_SPEAKERMODE[1];
		
		ByteBuffer buffer = newByteBuffer(256);
		
		out.printf("==================================================================\n");
		out.printf("Geometry example.  Copyright (c) Firelight Technologies 2004-2008.\n");
		out.printf("==================================================================\n\n");
		
		/*
		 * Create a System object and initialize.
		 */
		result = FmodEx.System_Create(fmodSystem);
		ErrorCheck(result);
		
		result = fmodSystem.getVersion(buffer.asIntBuffer());
		version = buffer.getInt(0);
		ErrorCheck(result);
		
		if(version < FMOD_VERSION)
		{
			out.printf("Error!  You are using an old version of FMOD %08x.  This program requires %08x\n", version, FMOD_VERSION);
			exit(-1);
		}
		
		result = fmodSystem.getDriverCaps(0, buffer.asIntBuffer(), null, null, speakermode);
		ErrorCheck(result);
		int caps = buffer.getInt(0);

	    result = fmodSystem.setSpeakerMode(speakermode[0]);       /* Set the user selected speaker mode. */
	    ErrorCheck(result);
	    
	    if((caps & FMOD_CAPS_HARDWARE_EMULATED) != 0)             /* The user has the 'Acceleration' slider set to off!  This is really bad for latency!. */
	    {                                                   /* You might want to warn the user about this. */
	        result = fmodSystem.setDSPBufferSize(1024, 10);
	        ErrorCheck(result);
	    }
	    
	    result = fmodSystem.getDriverInfo(0, buffer, buffer.capacity(), null);
	    ErrorCheck(result);
	    String name = BufferUtils.toString(buffer);

	    if(name.equals("SigmaTel"))   /* Sigmatel sound devices crackle for some reason if the format is PCM 16bit.  PCM floating point output seems to solve it. */
	    {
	        result = fmodSystem.setSoftwareFormat(48000, FMOD_SOUND_FORMAT_PCMFLOAT, 0, 0, FMOD_DSP_RESAMPLER_LINEAR);
	        ErrorCheck(result);
	    }
	    
//        FMOD_ADVANCEDSETTINGS settings = FMOD_ADVANCEDSETTINGS.create();
//        settings.setHRTFMinAngle(0);
//        settings.setHRTFMaxAngle(180);
//        settings.setHRTFFreq(4000);
//
//        result = fmodSystem.setAdvancedSettings(settings);
//        ErrorCheck(result);
	
		result = fmodSystem.init(100, FMOD_INIT_3D_RIGHTHANDED | FMOD_INIT_SOFTWARE_OCCLUSION | FMOD_INIT_SOFTWARE_HRTF, null);
	    if(result == FMOD_ERR_OUTPUT_CREATEBUFFER)         /* Ok, the speaker mode selected isn't supported by this soundcard.  Switch it back to stereo... */
	    {
	        result = fmodSystem.setSpeakerMode(FMOD_SPEAKERMODE_STEREO);
	        ErrorCheck(result);
	                
	        result = fmodSystem.init(100, FMOD_INIT_3D_RIGHTHANDED | FMOD_INIT_SOFTWARE_OCCLUSION | FMOD_INIT_SOFTWARE_HRTF, null);/* ... and re-init. */
	        ErrorCheck(result);
	    }
		
		// Load sounds
		ByteBuffer datas = loadFileIntoMemory("/Media/drumloop.wav");
		FMOD_CREATESOUNDEXINFO exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(datas.capacity());
		result = fmodSystem.createSound(datas, FMOD_SOFTWARE | FMOD_3D | FMOD_OPENMEMORY, exinfo, sounds[0]);
		ErrorCheck(result);											datas = null; exinfo.release();
		result = sounds[0].set3DMinMaxDistance(1.0f, 10000.0f);
		ErrorCheck(result);
		result = sounds[0].setMode(FMOD_LOOP_NORMAL);
		ErrorCheck(result);
		
		datas = loadFileIntoMemory("/Media/wave.mp3");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(datas.capacity());
		result = fmodSystem.createSound(datas, FMOD_SOFTWARE | FMOD_3D | FMOD_OPENMEMORY, exinfo, sounds[1]);
		ErrorCheck(result);											datas = null; exinfo.release();
		result = sounds[1].set3DMinMaxDistance(1.0f, 10000.0f);
		ErrorCheck(result);
		result = sounds[1].setMode(FMOD_LOOP_NORMAL);
		ErrorCheck(result);
		
		datas = loadFileIntoMemory("/Media/jaguar.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(datas.capacity());
		result = fmodSystem.createSound(datas, FMOD_SOFTWARE | FMOD_3D | FMOD_OPENMEMORY, exinfo, sounds[2]);
		ErrorCheck(result);											datas = null; exinfo.release();
		result = sounds[2].set3DMinMaxDistance(1.0f, 10000.0f);
		ErrorCheck(result);
		result = sounds[2].setMode(FMOD_LOOP_NORMAL);
		ErrorCheck(result);
		
		datas = loadFileIntoMemory("/Media/swish.wav");
		exinfo = FMOD_CREATESOUNDEXINFO.create();
		exinfo.setLength(datas.capacity());
		result = fmodSystem.createSound(datas, FMOD_SOFTWARE | FMOD_3D | FMOD_OPENMEMORY, exinfo, sounds[3]);
		ErrorCheck(result);											datas = null; exinfo.release();
		result = sounds[3].set3DMinMaxDistance(1.0f, 10000.0f);
		ErrorCheck(result);
		result = sounds[3].setMode(FMOD_LOOP_NORMAL);
		ErrorCheck(result);
		
		
		initObjects();
		
		result = fmodSystem.setGeometrySettings(200.0f);
		ErrorCheck(result);
		
		// load objects
		walls = new Mesh();
		rotatingMesh = new Mesh();
		doorList[0] = new Mesh();
		doorList[1] = new Mesh();
		doorList[2] = new Mesh();
		doorList[3] = new Mesh();
		initGeometry("/Media/walls.bin", walls);
		initGeometry("/Media/center.bin", rotatingMesh);
		initGeometry("/Media/door.bin", doorList[0]);
		initGeometry("/Media/door.bin", doorList[1]);
		initGeometry("/Media/door.bin", doorList[2]);
		initGeometry("/Media/door.bin", doorList[3]);
		
		// place doors in desired orientatins
		FMOD_VECTOR up      = FMOD_VECTOR.create(0.0f, 1.0f, 0.0f);
		FMOD_VECTOR forward = FMOD_VECTOR.create(1.0f, 0.0f, 0.0f);
		result = doorList[1].geometry.setRotation(forward, up);
		ErrorCheck(result);
		result = doorList[2].geometry.setRotation(forward, up);
		ErrorCheck(result);
		result = doorList[3].geometry.setRotation(forward, up);
		ErrorCheck(result);
		up.release();
		forward.release();
		
		GLAutoDrawable.addKeyListener(this);
		GLAutoDrawable.addMouseListener(this);
		GLAutoDrawable.addMouseMotionListener(this);
		
		final GL gl = GLAutoDrawable.getGL();
		
		texture = loadTexture(GLAutoDrawable, "/Media/texture.img");
		
		//setup lighting
		float[] lightDiffuse  = new float[]{1.0f, 1.0f, 1.0f, 1.0f}; 
		float[] lightPosition = new float[]{300.0f, 1000.0f, 400.0f, 0.0f};
		float[] lightAmbiant  = new float[]{1.25f, 1.25f, 1.25f, 1.0f};
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, lightDiffuse, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, lightPosition, 0);
		gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, lightAmbiant, 0);	
		gl.glLightModelf(GL.GL_LIGHT_MODEL_TWO_SIDE, 1.0f);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_LIGHTING);
		
		gl.glShadeModel(GL.GL_SMOOTH);							//Smooth color shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		gl.glClearDepth(1.0);									//Enable Clearing of the Depth buffer
		gl.glDepthFunc(GL.GL_LEQUAL);							//Type of Depth test
		gl.glEnable(GL.GL_DEPTH_TEST);							//Enable Depth Testing
		
		//Define the correction done to the perspective calculation (perspective looks a it better)
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		
		//Create the animator to call the display method in loop
		animator = new Animator(GLAutoDrawable);
		animator.start();
	}
	
	private ByteBuffer loadFileIntoMemory(String name)
	{
		try
		{
			BufferedInputStream bis = new BufferedInputStream(getClass().getResourceAsStream(name));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
			byte[] bytes = new byte[4*1024];
			int read;
			while((read = bis.read(bytes, 0, bytes.length)) != -1)
			{
				baos.write(bytes, 0, read);
			}
			bis.close();
			
			ByteBuffer buffer = newByteBuffer(baos.size());
			buffer.put(baos.toByteArray());
			buffer.rewind();
			return buffer;
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		exit(-1);
		return null;
	}
	
	private int loadTexture(GLAutoDrawable GLAutoDrawable, String filename)
	{
		GL gl = GLAutoDrawable.getGL();
		GLU glu = new GLU();
		
		int width  = 128;
		int height = 128;
		
		ByteBuffer data = loadFileIntoMemory(filename);
		
		IntBuffer texture = newIntBuffer(SIZEOF_INT);
		gl.glGenTextures(1, texture);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture.get(0));
		
		gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_NEAREST );
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR );
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
		
		glu.gluBuild2DMipmaps(
				GL.GL_TEXTURE_2D, 
				3, 
				width, 
				height,
				GL.GL_RGB, 
				GL.GL_UNSIGNED_BYTE,
				data);
		return texture.get(0);
	}
	
	/**
	 * Display event
	 */
	public void display(GLAutoDrawable GLAutoDrawable)
	{
		final GL gl = GLAutoDrawable.getGL();
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);	//Clear the buffers
		gl.glLoadIdentity();											//Reset the view
		
		gl.glRotatef(xRotation, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yRotation, 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(-xListenerPos, -yListenerPos, -zListenerPos);
		
		// clear
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0.4f, 0.6f, 1.0f, 0.0f);
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		
		// draw geometry
		drawGeometry(gl, walls);
		drawGeometry(gl, rotatingMesh);
		drawGeometry(gl, doorList[0]);
		drawGeometry(gl, doorList[1]);
		drawGeometry(gl, doorList[2]);
		drawGeometry(gl, doorList[3]);
		
		gl.glDisable(GL.GL_TEXTURE_2D);
		
		// draw sound objects
		for(int object = 0; object < objects.length; object++)
		{
			FloatBuffer directOcclusion = newFloatBuffer(SIZEOF_FLOAT);
			FloatBuffer reverbOcclusion = newFloatBuffer(SIZEOF_FLOAT);
			directOcclusion.put(0, 1.0f);
			reverbOcclusion.put(0, 1.0f);
			
			// set colour baced on direct occlusion
			objects[object].channel.get3DOcclusion(directOcclusion, reverbOcclusion);
			float intensity = 1.0f - directOcclusion.get(0);
			
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
			gl.glPushMatrix();
			gl.glTranslatef(objects[object].xPos, objects[object].yPos, objects[object].zPos);
			
			gl.glPushAttrib(GL.GL_LIGHTING_BIT);
			
			intensity *= 0.75f;
			float[] color = new float[]{intensity, intensity, 0.0f, 0.0f};
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, color, 0);
			intensity *= 0.5f;
			float[] ambient = new float[]{intensity, intensity, 0.0f, 0.0f};
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, ambient, 0);
			
			gl.glRotatef(accumulatedTime * 200.0f, 0.0f, 1.0f, 0.0f);
			new GLUT().glutSolidTorus(0.15f, 0.6f, 8, 16);
			gl.glPopAttrib();
			gl.glPopMatrix();
		}
		
		timerFunc();
	}
	
	void drawGeometry(GL gl, Mesh mesh)
	{
		FMOD_RESULT result;
		
		FMOD_VECTOR pos = FMOD_VECTOR.create();
		result = mesh.geometry.getPosition(pos);
		ErrorCheck(result);
		
		gl.glPushMatrix();
		//Create matrix and set gl transformation for geometry
		gl.glTranslatef(pos.getX(), pos.getY(), pos.getZ());
		pos.release();
		
		FMOD_VECTOR forward = FMOD_VECTOR.create();
		FMOD_VECTOR up = FMOD_VECTOR.create();
		result = mesh.geometry.getRotation(forward, up);
		ErrorCheck(result);
		float[] matrix = new float[]{
				up.getY() * forward.getZ() - up.getZ() * forward.getY(), up.getX(), forward.getX(), 0.0f,
				up.getZ() * forward.getX() - up.getX() * forward.getZ(), up.getY(), forward.getY(), 0.0f,
				up.getX() * forward.getY() - up.getY() * forward.getX(), up.getZ(), forward.getZ(), 0.0f,
				0.0f,													 0.0f,		0.0f,			1.0f};
		gl.glMultMatrixf(matrix, 0);
		forward.release();
		up.release();
		
		// draw all polygons in object
		gl.glEnable(GL.GL_LIGHTING);	
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_FILL);
		
		for(int poly = 0; poly < mesh.numPolygons; poly++)
		{
			Polygon polygon = mesh.polygons[poly];
			if(polygon.directOcclusion == 0.0f)
				continue; // don't draw because it is an open door way
			gl.glBegin(GL.GL_TRIANGLE_FAN);
			gl.glNormal3f(polygon.normal.getX(), polygon.normal.getY(), polygon.normal.getZ());
			
			for(int i = 0; i < polygon.numVertices; i++)
			{
				int index = mesh.indices[polygon.indicesOffset + i];
				gl.glTexCoord2f(mesh.texcoords[index][0], mesh.texcoords[index][1]);
				gl.glVertex3f(mesh.vertices[index].getX(), mesh.vertices[index].getY(), mesh.vertices[index].getZ());
			}
			gl.glEnd();
		}
		gl.glPopMatrix();
	}
	
	private long INTERFACE_UPDATETIME = 10;
	private float frames = 0;
	private long firstFrame = -1;
	void timerFunc()
	{
						/*Calculate the average elapsed time*/
		
		if(firstFrame == -1)
			firstFrame = currentTimeMillis();
		long currentTime = currentTimeMillis();
		frames++;
		if((currentTime-firstFrame) >= 500)
		{
			float fps = 1000*frames/(float)(currentTime-firstFrame);
			INTERFACE_UPDATETIME = (long)(1000.0f / fps);
			frames = 0;
			firstFrame = currentTime;
		}
		
						/* END */
		
		doGeometryMovement();
		
		doSoundMovement();
		doListenerMovement();
		
		FMOD_RESULT result = fmodSystem.update();
		ErrorCheck(result);
		
		accumulatedTime += INTERFACE_UPDATETIME / 1000.0f;
		
	    if(false)
	    {
	        FloatBuffer dsp = BufferUtils.newFloatBuffer(1);
	        FloatBuffer stream = BufferUtils.newFloatBuffer(1);
	        FloatBuffer update = BufferUtils.newFloatBuffer(1);
	        FloatBuffer total = BufferUtils.newFloatBuffer(1);
	        IntBuffer chansplaying = BufferUtils.newIntBuffer(1);

	        fmodSystem.getCPUUsage(dsp, stream, update, total);
	        fmodSystem.getChannelsPlaying(chansplaying);

	        out.printf("chans %d : cpu : dsp = %.02f stream = %.02f update = %.02f total = %.02f\n", chansplaying.get(0), dsp.get(0), stream.get(0), update.get(0), total.get(0));
	    }
	}
	
	private FMOD_VECTOR lastOffset = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);
	void doGeometryMovement()
	{
		FMOD_RESULT result;
		
		// example of moving individual polygon vertices
		int xGeometryWarpPos = -30;
		int zGeometryWarpPos = -21;
		int dx = (int)xListenerPos - xGeometryWarpPos;
		int dz = (int)zListenerPos - zGeometryWarpPos;
		if(dx * dx + dz * dz < 30.0f * 30.0f)
		{
			if(sin(accumulatedTime * 1.0f) > 0.0f)
			{
				FMOD_VECTOR offset = FMOD_VECTOR.create((float)sin(accumulatedTime * 2.0f), 0.0f, (float)cos(accumulatedTime * 2.0f));
				for (int poly = 0; poly < walls.numPolygons; poly++)
				{
					Polygon polygon = walls.polygons[poly];
					for(int i = 0; i < polygon.numVertices; i++)
					{
						FMOD_VECTOR vertex = walls.vertices[walls.indices[polygon.indicesOffset + i]];
						
						dx = (int)vertex.getX() - xGeometryWarpPos;
						dz = (int)vertex.getZ() - zGeometryWarpPos;
						if(dx * dx + dz * dz > 90.0f)
							continue;
						
						vertex.setX(vertex.getX()+offset.getX()-lastOffset.getX());
						vertex.setY(vertex.getY()+offset.getY()-lastOffset.getY());
						vertex.setZ(vertex.getZ()+offset.getZ()-lastOffset.getZ());
						
						result = walls.geometry.setPolygonVertex(poly, i, vertex);
						ErrorCheck(result);
					}	
				}
				lastOffset.release();
				lastOffset = offset;
			}
		}
		
		// example of rotation and a geometry object
		FMOD_VECTOR up = FMOD_VECTOR.create(0.0f, 1.0f, 0.0f);
		FMOD_VECTOR forward = FMOD_VECTOR.create((float)sin(accumulatedTime * 0.5f), 0.0f, (float)cos(accumulatedTime * 0.5f));
		result = rotatingMesh.geometry.setRotation(forward, up);
		up.release();
		forward.release();
		
		ErrorCheck(result);
		FMOD_VECTOR pos = FMOD_VECTOR.create();
		pos.setX(12.0f);
		pos.setY((float)sin(accumulatedTime) * 0.4f + 0.1f);
		pos.setZ(0.0f);
		result = rotatingMesh.geometry.setPosition(pos);
		ErrorCheck(result);
		
		
		// example of moving doors
		// door 1
		pos.setX(3.25f);
		pos.setY(((float)sin(accumulatedTime)) * 2.0f + 1.0f);
		if (pos.getY() < 0.0f)
			pos.setY(0);
		if (pos.getY() > 2.0f)
			pos.setY(2.0f);
		pos.setZ(11.5f);
		result = doorList[0].geometry.setPosition(pos);
		ErrorCheck(result);
		
		// door 2
		pos.setX(0.75f);
		pos.setY(((float)sin(accumulatedTime)) * 2.0f + 1.0f);
		if (pos.getY() < 0.0f)
			pos.setY(0);
		if (pos.getY() > 2.0f)
			pos.setY(2.0f);
		pos.setZ(14.75f);
		result = doorList[1].geometry.setPosition(pos);
		ErrorCheck(result);
		
		// door 3
		pos.setX(8.25f);
		pos.setY(((float)sin(accumulatedTime)) * 2.0f + 1.0f);
		if (pos.getY() < 0.0f)
			pos.setY(0);
		if (pos.getY() > 2.0f)
			pos.setY(2.0f);
		pos.setZ(14.75f);
		result = doorList[2].geometry.setPosition(pos);
		ErrorCheck(result);
		
		// door 4
		pos.setX(33.0f);
		pos.setY(((float)sin(accumulatedTime)) * 2.0f + 1.0f);
		if (pos.getY() < 0.0f)
			pos.setY(0);
		if (pos.getY() > 2.0f)
			pos.setY(2.0f);
		pos.setZ(-0.75f);
		result = doorList[3].geometry.setPosition(pos);
		ErrorCheck(result);
		
		pos.release();
	}
	
	void doSoundMovement()
	{	
		objects[0].zPos = 10.0f * (float)sin(accumulatedTime);
		updateObjectSoundPos(objects[0]);
		objects[5].zPos = -22 + 8.0f * (float)sin(accumulatedTime);
		updateObjectSoundPos(objects[5]);
	}
	void updateObjectSoundPos(Object3D object)
	{
		FMOD_RESULT result;
		FMOD_VECTOR pos = FMOD_VECTOR.create(object.xPos, object.yPos, object.zPos);
		FMOD_VECTOR oldPos = FMOD_VECTOR.create();
		object.channel.get3DAttributes(oldPos, null);
		
		FMOD_VECTOR vel = FMOD_VECTOR.create();
		vel.setX( (pos.getX() - oldPos.getX()) *  (1000 / (float)INTERFACE_UPDATETIME) );
		vel.setY( (pos.getY() - oldPos.getY()) *  (1000 / (float)INTERFACE_UPDATETIME) );
		vel.setZ( (pos.getZ() - oldPos.getZ()) *  (1000 / (float)INTERFACE_UPDATETIME) );
		result = object.channel.set3DAttributes(pos, vel);
		ErrorCheck(result);
		
		pos.release();
		oldPos.release();
	}
	
	final float MOVEMENT_SPEED = 0.1f;
	FMOD_VECTOR lastpos = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);
	FMOD_VECTOR lastVel = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);
	boolean bFirst = true;
	void doListenerMovement()
	{
		// Update user movement
		float forward = 0.0f;
		
		if (moveForward)
			forward += MOVEMENT_SPEED * INTERFACE_UPDATETIME / 17.0f;		//INTERFACE_UPDATETIME in FMOD Ex is 17
		if (moveBackward)
			forward -= MOVEMENT_SPEED * INTERFACE_UPDATETIME / 17.0f;
		
		float right = 0.0f;
		if (moveLeft)
			right -= MOVEMENT_SPEED * INTERFACE_UPDATETIME / 17.0f;
		if (moveRight)
			right += MOVEMENT_SPEED * INTERFACE_UPDATETIME / 17.0f;	
		
		float up = 0.0f;
	    if (moveUp)
	        up += MOVEMENT_SPEED * INTERFACE_UPDATETIME / 17.0f;
	    if (moveDown)
	        up -= MOVEMENT_SPEED * INTERFACE_UPDATETIME / 17.0f;
		
		float xRight = (float)cos(yRotation * (PI / 180.0f));
		float yRight = 0.0f;
		float zRight = (float)sin(yRotation * (PI / 180.0f));
		
		xListenerPos += xRight * right;
		yListenerPos += yRight * right;
		zListenerPos += zRight * right;
		
		float xForward = (float)(sin(yRotation * (PI / 180.0f)) * cos(xRotation  * (PI / 180.0f)));
		float yForward = -(float)sin(xRotation  * (PI / 180.0f));
		float zForward = -(float)(cos(yRotation * (PI / 180.0f)) * cos(xRotation  * (PI / 180.0f)));
		
		xListenerPos += xForward * forward;
		yListenerPos += yForward * forward;
		zListenerPos += zForward * forward;
		
		yListenerPos += up;
		
		if(yListenerPos < 1.0f)
		{
			yListenerPos = 1.0f;
		}
		
		// cross product
		float xUp = yRight * zForward - zRight * yForward;
		float yUp = zRight * xForward - xRight * zForward;
		float zUp = xRight * yForward - yRight * xForward;
		
		// Update listener
		{
			FMOD_VECTOR listenerVector = FMOD_VECTOR.create();
			listenerVector.setX(xListenerPos);
			listenerVector.setY(yListenerPos);
			listenerVector.setZ(zListenerPos);
			
			FMOD_VECTOR forwardVec = FMOD_VECTOR.create();
			FMOD_VECTOR upVec = FMOD_VECTOR.create();
			FMOD_VECTOR vel = FMOD_VECTOR.create();
			
			forwardVec.setX(xForward);
			forwardVec.setY(yForward);
			forwardVec.setZ(zForward);
			upVec.setX(xUp);
			upVec.setY(yUp);
			upVec.setZ(zUp);
			
			// ********* NOTE ******* READ NEXT COMMENT!!!!!
			// vel = how far we moved last FRAME (m/f), then time compensate it to SECONDS (m/s).
			vel.setX((listenerVector.getX() - lastpos.getX()) * (1000 / (float)INTERFACE_UPDATETIME));
			vel.setY((listenerVector.getY() - lastpos.getY()) * (1000 / (float)INTERFACE_UPDATETIME));
			vel.setZ((listenerVector.getZ() - lastpos.getZ()) * (1000 / (float)INTERFACE_UPDATETIME));
			if(bFirst)
			{
				bFirst = false;
				vel.setX(0);
				vel.setY(0);
				vel.setZ(0);
			}
			
			// store pos for next time
			lastpos.release();
			lastpos = listenerVector;
			lastVel.release();
			lastVel = vel;
			
			FMOD_RESULT result = fmodSystem.set3DListenerAttributes(0, listenerVector, vel, forwardVec, upVec);
			ErrorCheck(result);
			
			forwardVec.release();
			upVec.release();
			vel.release();
		}
	}
	
							/*Key events*/
	
	public void keyReleased(KeyEvent ke)
	{
		switch(ke.getKeyCode())
		{
			case KeyEvent.VK_ESCAPE:
				animator.stop();
				
				//TODO Skip glDeleteTextures for simplicity (GLContext is not current)
//				glDeleteTextures(1, texture);
				
				freeGeometry(walls);
				freeGeometry(rotatingMesh);
				freeGeometry(doorList[0]);
				freeGeometry(doorList[1]);
				freeGeometry(doorList[2]);
				freeGeometry(doorList[3]);
				
				sounds[0].release();
				sounds[1].release();
				sounds[2].release();
				sounds[3].release();
				
				fmodSystem.release();
				
				exit(0);
				break;
			case KeyEvent.VK_UP: moveForward = false; break;
			case KeyEvent.VK_DOWN: moveBackward = false; break;
			case KeyEvent.VK_LEFT: moveLeft = false; break;
			case KeyEvent.VK_RIGHT: moveRight = false; break;
			case KeyEvent.VK_PAGE_UP: moveUp = false; break;
			case KeyEvent.VK_PAGE_DOWN: moveDown = false; break;
		}
	}
	public void keyPressed(KeyEvent ke)
	{
		switch(ke.getKeyCode())
		{
			case KeyEvent.VK_UP: moveForward = true; break;
			case KeyEvent.VK_DOWN: moveBackward = true; break;
			case KeyEvent.VK_LEFT: moveLeft = true; break;
			case KeyEvent.VK_RIGHT: moveRight = true; break;
			case KeyEvent.VK_PAGE_UP: moveUp = true; break;
			case KeyEvent.VK_PAGE_DOWN: moveDown = true; break;
		}
	}
	public void keyTyped(KeyEvent ke){}
	
	/*Windows Listener*/
	
	public void windowClosing(WindowEvent e)
	{
		if(animator != null)
			animator.stop();
	}
	public void windowOpened(WindowEvent e){}
	public void windowClosed(WindowEvent e){}
	public void windowIconified(WindowEvent e)
	{
		if(animator != null)
			animator.stop();
	}
	public void windowDeiconified(WindowEvent e)
	{
		if(animator != null)
			animator.start();
	}
	public void windowActivated(WindowEvent e){}
	public void windowDeactivated(WindowEvent e){}
	
	/*Mouse Listener*/
	
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			doRotate = true;
			xMouse = e.getX();
			yMouse = e.getY();
		}
	}
	public void mouseReleased(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			doRotate = false;
		}
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	/*Mouse Motion Listener*/
	
	final float xExtent = 88.0f;
	public void mouseDragged(MouseEvent e)
	{
		int x = e.getX();
		int y = e.getY();
		
		int dx = x - xMouse;
		int dy = y - yMouse;
		
		// view rotation about y-axis
		yRotation += dx * 0.5f;
		if (yRotation > 180.0f)
			yRotation -= 360.0f;
		else
			if (yRotation < -180.0f)
				yRotation += 360.0f;
		
		// view rotation about x-axis
		xRotation += dy * 0.5f;
		if (xRotation > xExtent)
			xRotation = xExtent;
		else 
			if (xRotation < -xExtent)
				xRotation = -xExtent;
		
		xMouse = x;
		yMouse = y;
	}
	public void mouseMoved(MouseEvent e){}
}