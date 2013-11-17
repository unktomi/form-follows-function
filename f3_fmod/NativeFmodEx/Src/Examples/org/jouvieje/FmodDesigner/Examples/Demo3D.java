/*===============================================================================================
 geometry.exe main.cpp
 Copyright (c), Firelight Technologies Pty, Ltd 2005-2007.

 Example to show occlusion
===============================================================================================*/

package org.jouvieje.FmodDesigner.Examples;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.exit;
import static java.lang.System.out;
import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_INITFLAGS.FMOD_EVENT_INIT_NORMAL;
import static org.jouvieje.FmodDesigner.Defines.FMOD_EVENT_MODE.FMOD_EVENT_DEFAULT;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_3D_RIGHTHANDED;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_SOFTWARE_HRTF;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_SOFTWARE_OCCLUSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_LOWPASS.FMOD_DSP_LOWPASS_CUTOFF;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_LOWPASS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Misc.BufferUtils.newIntBuffer;
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
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Stack;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.jouvieje.FileSystem.JARFileSystem;
import org.jouvieje.FmodDesigner.Event;
import org.jouvieje.FmodDesigner.EventGroup;
import org.jouvieje.FmodDesigner.EventParameter;
import org.jouvieje.FmodDesigner.EventProject;
import org.jouvieje.FmodDesigner.EventSystem;
import org.jouvieje.FmodDesigner.FmodDesigner;
import org.jouvieje.FmodDesigner.InitFmodDesigner;
import org.jouvieje.FmodEx.DSP;
import org.jouvieje.FmodEx.FmodEx;
import org.jouvieje.FmodEx.Geometry;
import org.jouvieje.FmodEx.Init;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Enumerations.FMOD_SPEAKERMODE;
import org.jouvieje.FmodEx.Examples.Utils.Medias;
import org.jouvieje.FmodEx.Exceptions.InitException;
import org.jouvieje.FmodEx.Misc.BufferUtils;
import org.jouvieje.FmodEx.Structures.FMOD_REVERB_PROPERTIES;
import org.jouvieje.FmodEx.Structures.FMOD_VECTOR;

import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;

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
public class Demo3D implements GLEventListener, KeyListener, MouseListener, MouseMotionListener
{
	private void ErrorCheck(FMOD_RESULT result)
	{
		if(result != FMOD_OK)
		{
			out.printf("FMOD error! (%d) %s\n", result.asInt(), FmodEx.FMOD_ErrorString(result));
			JOptionPane.showMessageDialog(null, "FMOD error! ("+result.asInt()+")"+FmodEx.FMOD_ErrorString(result));
			exit(-1);
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
			InitFmodDesigner.loadLibraries(INIT_MODES.INIT_FMOD_DESIGNER/*_MINIMUM*/);
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
		JFrame frame = new JFrame("FMOD Ex 3D Demo");
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		
		/**
		 * Creates the OpenGl scene using Jogl
		 */
		GLCanvas glScene = new GLCanvas();
		final Demo3D glEvents = new Demo3D();
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
	private GLUT glut = new GLUT();
	
	
	private final static boolean SHOW_GUI_DEBUG_TEXT = true;

	private boolean fullscreen = false;

	//window size
	static int width = 500;
	static int height = 500;

	//mouse control
	private int xMouse = 0;
	private int yMouse = 0;

	//listener orientation
	private float xRotation = 0.0f;
	private float yRotation = 90.0f;

	//listerer position
	private float xListenerPos = -4.0f;
	private float yListenerPos = 1.3f;
	private float zListenerPos = 0.0f;

	//keyboard control
	private boolean moveForward    = false;
	private boolean moveBackward   = false;
	private boolean moveLeft       = false;
	private boolean moveRight      = false;
	private boolean moveUp         = false;
	private boolean moveDown       = false;
	private boolean moveFast       = false;
	private boolean ambientVolUp   = false;
	private boolean ambientVolDown = false;
	private boolean masterVolUp    = false;
	private boolean masterVolDown  = false;

	private float accumulatedTime = 0.0f;

	//textures
	private int texture;
	private Texture[] skyboxTexture = new Texture[6];

	//sounds placement
	class Object3D
	{
		float xPos;
		float yPos;
		float zPos;
		float intensity;
		int sound;
		Event event;
		public Object3D(){}
		public Object3D(float xPos, float yPos, float zPos, float intensity, int sound, Event event)
		{
			this.xPos = xPos;
			this.yPos = yPos;
			this.zPos = zPos;
			this.intensity = intensity;
			this.sound = sound;
			this.event = event;
		}
	}
	Object3D[] objects = new Object3D[] {
			new Object3D(-11.0f, 1.0f,   0.0f, 1.0f, 0, new Event()),
			new Object3D( 12.0f, 2.0f,   0.0f, 1.0f, 1, new Event()),
			new Object3D( 45.0f, 1.0f,   0.0f, 1.0f, 3, new Event()),
			new Object3D(-30.0f, 1.0f,  21.0f, 1.0f, 2, new Event()),
			new Object3D(-30.0f, 1.0f, -21.0f, 1.0f, 3, new Event()),
			new Object3D( 12.0f, 1.0f, -27.0f, 1.0f, 0, new Event()),
			new Object3D(  4.0f, 1.0f,  16.0f, 1.0f, 0, new Event()),
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

	Mesh walls = new Mesh();
	Mesh rotatingMesh = new Mesh();
	Mesh[] doorList = new Mesh[]{new Mesh(), new Mesh(), new Mesh(), new Mesh()};

	//fmod sounds structures
	EventSystem    fmodEventSystem  = new EventSystem();
	EventProject   fmodEventProject = new EventProject();
	EventGroup     fmodEventGroup   = new EventGroup();
	EventParameter fmodEventParameter = new EventParameter();
	System         fmodSystem       = new System();
	Geometry	   geometry         = new Geometry();
	DSP            global_lowpass   = new DSP();

	float ambientVolume = 0.2f;
	float masterVolume;

	int rendermode = GL.GL_FILL;
	/*
	 * Global stack of strings to render
	 */
	Stack<String> debugText = new Stack<String>();
	Stack<String> statusText = new Stack<String>();
	
	boolean showdebug = false;
	boolean showhelp = false;
	
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
	
	void initGeometry(String fileName, Mesh mesh, boolean alter)
	{
		FMOD_RESULT result;
		
		ByteBuffer file = Medias.loadMediaIntoMemory(fileName);
		
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
		
		result = fmodEventGroup.getEvent("drumloop", FMOD_EVENT_DEFAULT, objects[0].event);
		ErrorCheck(result);
		
		result = fmodEventGroup.getEvent("drumloop2", FMOD_EVENT_DEFAULT, objects[1].event);
		ErrorCheck(result);
		
		result = fmodEventGroup.getEvent("drumloop3", FMOD_EVENT_DEFAULT, objects[2].event);
		ErrorCheck(result);
		
		result = fmodEventGroup.getEvent("drumloop4", FMOD_EVENT_DEFAULT, objects[3].event);
		ErrorCheck(result);
		
		result = fmodEventGroup.getEvent("drumloop5", FMOD_EVENT_DEFAULT, objects[4].event);
		ErrorCheck(result);
		
		result = fmodEventGroup.getEvent("drumloop6", FMOD_EVENT_DEFAULT, objects[5].event);
		ErrorCheck(result);
		
		result = fmodEventGroup.getEvent("drumloop7", FMOD_EVENT_DEFAULT, objects[6].event);
		ErrorCheck(result);
		
		for(int i = 0; i < objects.length; i++)
		{
			FMOD_VECTOR pos = FMOD_VECTOR.create(objects[i].xPos, objects[i].yPos, objects[i].zPos);
			FMOD_VECTOR vel = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);

			if(!objects[i].event.isNull())
			{
				result = objects[i].event.set3DAttributes(pos, vel, null);
				ErrorCheck(result);
				result = objects[i].event.start();
				ErrorCheck(result);
			}
		}
	}
	
	public void init(GLAutoDrawable GLAutoDrawable)
	{
		FMOD_RESULT      result;
		FMOD_SPEAKERMODE[] speakermode = new FMOD_SPEAKERMODE[1];

		out.printf("==================================================================\n");
		out.printf("3D example.  Copyright (c) Firelight Technologies 2004-2007.\n");
		out.printf("==================================================================\n\n");

		result = FmodDesigner.EventSystem_Create(fmodEventSystem);
		ErrorCheck(result);
		result = FmodDesigner.NetEventSystem_Init(fmodEventSystem);
		ErrorCheck(result);
		result = fmodEventSystem.getSystemObject(fmodSystem);
		ErrorCheck(result);
		result = fmodSystem.getDriverCaps(0, null, null, null, speakermode);
		ErrorCheck(result);
		result = fmodSystem.setSpeakerMode(speakermode[0]);
		ErrorCheck(result);
		result = fmodEventSystem.init(64, FMOD_INIT_3D_RIGHTHANDED | FMOD_INIT_SOFTWARE_OCCLUSION | FMOD_INIT_SOFTWARE_HRTF, null, FMOD_EVENT_INIT_NORMAL);
		ErrorCheck(result);
		result = fmodEventSystem.setMediaPath("DesignerMedia/");
		ErrorCheck(result);
		result = fmodEventSystem.load("examples.fev", null, fmodEventProject);
		ErrorCheck(result);
		result = fmodEventProject.getGroup("examples/3d", true, fmodEventGroup);
		ErrorCheck(result);

	    /*
		 * Create a programmer created lowpass filter to apply to everything.
		 */
		result = fmodSystem.createDSPByType(FMOD_DSP_TYPE_LOWPASS, global_lowpass);
		ErrorCheck(result);

		result = global_lowpass.setParameter(FMOD_DSP_LOWPASS_CUTOFF.asInt(), 1000);
		ErrorCheck(result);

		result = global_lowpass.setBypass(true);   // turn it off to start with.
		ErrorCheck(result);

		result = fmodSystem.addDSP(global_lowpass, null);
		ErrorCheck(result);
		
		initObjects();

		result = fmodSystem.setGeometrySettings(200.0f);
		ErrorCheck(result);

		out.printf("Loading geometry...");

		// load objects
		initGeometry("/DesignerMedia/walls.bin", walls, true);
		initGeometry("/DesignerMedia/center.bin", rotatingMesh, false);
		initGeometry("/DesignerMedia/door.bin", doorList[0], false);
		initGeometry("/DesignerMedia/door.bin", doorList[1], false);
		initGeometry("/DesignerMedia/door.bin", doorList[2], false);
		initGeometry("/DesignerMedia/door.bin", doorList[3], false);

		out.printf("done.\n");

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
		
		/*
		 * Load textures
		 */
		
		texture = loadTexture(GLAutoDrawable, "/DesignerMedia/texture.img");
		out.printf("Loading textures...");
		try
		{
			skyboxTexture[0] = loadTexturePNG(GLAutoDrawable, "/DesignerMedia/skybox/bluesky/front.png");
			skyboxTexture[1] = loadTexturePNG(GLAutoDrawable, "/DesignerMedia/skybox/bluesky/right.png");
			skyboxTexture[2] = loadTexturePNG(GLAutoDrawable, "/DesignerMedia/skybox/bluesky/back.png");
			skyboxTexture[3] = loadTexturePNG(GLAutoDrawable, "/DesignerMedia/skybox/bluesky/left.png");
			skyboxTexture[4] = loadTexturePNG(GLAutoDrawable, "/DesignerMedia/skybox/bluesky/top.png");
			skyboxTexture[5] = loadTexturePNG(GLAutoDrawable, "/DesignerMedia/skybox/bluesky/bottom.png");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			exit(-1);
		}
		out.printf("done.\n");

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
		
		//setup fog(water)
		float[] fogColor = {0.0f, 0.1f, 0.9f, 1.0f};
		
		gl.glFogi(GL.GL_FOG_MODE, GL.GL_EXP);		//Fog Mode
		gl.glFogfv(GL.GL_FOG_COLOR, fogColor, 0);		//Set Fog Color
		gl.glFogf(GL.GL_FOG_DENSITY, 0.15f);		//How Dense Will The Fog Be
		gl.glHint(GL.GL_FOG_HINT, GL.GL_DONT_CARE);	//Fog Hint Value
		gl.glFogf(GL.GL_FOG_START, 0.0f);			//Fog Start Depth
		gl.glFogf(GL.GL_FOG_END, 1.0f);				//Fog End Depth
		
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
	
	private int loadTexture(GLAutoDrawable GLAutoDrawable, String filename)
	{
		GL gl = GLAutoDrawable.getGL();
		GLU glu = new GLU();
		
		int width  = 128;
		int height = 128;
		
		ByteBuffer data = Medias.loadMediaIntoMemory(filename);
		
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
	
	private Texture loadTexturePNG(GLAutoDrawable GLAutoDrawable, String filename) throws IOException
	{
		return TextureIO.newTexture(getClass().getResourceAsStream(filename), true, "png");
		
//		BufferedImage buffer = ImageIO.read(getClass().getResourceAsStream(filename));
//		int width = buffer.getWidth();
//		int height = buffer.getHeight();
//		
//		byte[] pixels = new byte[width*height*3];
//		for(int y = 0; y < height; y++)
//		{
//			for(int x = 0; x < width; x++)
//			{
//				int i = y*width+x;
//				
//				int color = buffer.getRGB(x, y);
//				pixels[3*i  ] = (byte) ((color >> 16) & 0xFF);		//r
//				pixels[3*i+1] = (byte) ((color >>  8) & 0xFF);		//g
//				pixels[3*i+2] = (byte) ( color        & 0xFF);		//b
//			}
//		}
//		
//		IntBuffer texture = newIntBuffer(SIZEOF_INT);
//		gl.glGenTextures(1, texture);
//		gl.glBindTexture(GL.GL_TEXTURE_2D, texture.get(0));
//		
//		gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_MODULATE);
//		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_NEAREST );
//		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR );
//		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
//		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
//		
//		glu.gluBuild2DMipmaps(
//				GL.GL_TEXTURE_2D, 
//				3, 
//				width, 
//				height,
//				GL.GL_RGB, 
//				GL.GL_UNSIGNED_BYTE,
//				ByteBuffer.wrap(pixels));
//		return texture.get(0);
	}	
	
	
	/**
	 * Display event
	 */
	public void display(GLAutoDrawable GLAutoDrawable)
	{
		final GL gl = GLAutoDrawable.getGL();
		final GLU glu = new GLU();
		
		// Show listener position
		{
			String s = String.format("Listener Pos: (%.2f, %.2f, %.2f)", xListenerPos, yListenerPos, zListenerPos);
			if(SHOW_GUI_DEBUG_TEXT)
				debugText.push(s);
		}
		// Show cpu usage position
		{
			FloatBuffer x = BufferUtils.newFloatBuffer(1);
			FloatBuffer y = BufferUtils.newFloatBuffer(1);
			FloatBuffer z = BufferUtils.newFloatBuffer(1);
			FloatBuffer w = BufferUtils.newFloatBuffer(1);
			fmodSystem.getCPUUsage(x, y, z, w);
			
			String s = String.format("CPU Usage : (%.2f, %.2f, %.2f %.2f)", x.get(0), y.get(0), z.get(0), w.get(0));
			if(SHOW_GUI_DEBUG_TEXT)
				debugText.push(s);
		}
		
		/*
		 * 3D RENDERING
		 */
		
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(
			60.0,								// fov
			(float)width / (float)height,		// aspect
			0.1,								// near
			500.0								// far
			);
		
		gl.glRotatef(xRotation, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yRotation, 0.0f, 1.0f, 0.0f);
		gl.glTranslatef(-xListenerPos, -yListenerPos, -zListenerPos);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		//clear
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(0.4f, 0.6f, 1.0f, 0.0f);
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		
		//draw geometry
		drawGeometry(gl, walls);
		drawGeometry(gl, rotatingMesh);
		drawGeometry(gl, doorList[0]);
		drawGeometry(gl, doorList[1]);
		drawGeometry(gl, doorList[2]);
		drawGeometry(gl, doorList[3]);
		
		//draw skybox
		drawSkyBox(gl);
		
		gl.glDisable(GL.GL_TEXTURE_2D);

		//draw sound objects
		for(int object = 0; object < objects.length; object++)
		{
			float intensity = 1.0f;
			
			gl.glPolygonMode(GL.GL_FRONT_AND_BACK, rendermode);
			gl.glPushMatrix();
			gl.glTranslatef(objects[object].xPos, objects[object].yPos, objects[object].zPos);
			
			String s = String.format("Sound object (%d): %.2f, %.2f, %.2f", object, objects[object].xPos, objects[object].yPos, objects[object].zPos);
			if(SHOW_GUI_DEBUG_TEXT)
				debugText.push(s);
			
			gl.glPushAttrib(GL.GL_LIGHTING_BIT);
			
			intensity *= 0.75f;
			float[] color = {intensity, intensity, 0.0f, 0.0f};
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, color, 0);
			intensity *= 0.5f;
			float[] ambient = {intensity, intensity, 0.0f, 0.0f};
			gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, ambient, 0);
			
			
			gl.glRotatef(accumulatedTime * 200.0f, 0.0f, 1.0f, 0.0f);
			{
				float rad = (accumulatedTime * 200.0f);

				rad *= 3.14159f;
				rad /= 180.0f;

				FMOD_VECTOR soundorientation = FMOD_VECTOR.create();
				soundorientation.setX((float)sin(rad));
				soundorientation.setY(0);
				soundorientation.setZ((float)cos(rad));

				objects[object].event.set3DAttributes(null, null, soundorientation);
			}

			glut.glutSolidTorus(0.15f, 0.6f, 8, 16);
			gl.glPopAttrib();
			gl.glPopMatrix();
		}

		/*
		 * Draw blue transparent blue quads to entry to water room
		 */
		drawWaterRoom(gl);

		/*
		 * Do water effects if we are in the water room
		 */  
		inWater(gl);


		/*
		 * 2D RENDERING
		 */
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluOrtho2D(0.0f, width, 0.0f, height);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_NORMALIZE);
		gl.glDisable(GL.GL_DEPTH_TEST);

		/*
		 * Render text
		 */
		renderUiText(gl);


		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_LIGHT0);
		gl.glEnable(GL.GL_NORMALIZE);
		gl.glShadeModel(GL.GL_SMOOTH);
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, rendermode);
		

		timerFunc(gl);
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
				up.getY() * forward.getZ() - up.getZ() * forward.getY(), up.getX(),	forward.getX(),	0.0f,
				up.getZ() * forward.getX() - up.getX() * forward.getZ(), up.getY(),	forward.getY(),	0.0f,
				up.getX() * forward.getY() - up.getY() * forward.getX(), up.getZ(),	forward.getZ(),	0.0f,
				0.0f,													 0.0f,		0.0f,			1.0f};
		gl.glMultMatrixf(matrix, 0);
		forward.release();
		up.release();
		
		// draw all polygons in object
		gl.glEnable(GL.GL_LIGHTING);	
		gl.glPolygonMode(GL.GL_FRONT_AND_BACK, rendermode);
		
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
	
	void drawWaterRoom(GL gl)
	{
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
		gl.glEnable(GL.GL_BLEND);

		gl.glPushMatrix();
		gl.glColor4f(0.0f,0.1f,0.8f,1.0f);
		gl.glDisable(GL.GL_LIGHTING);

		gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(-14.72f, 4.0f, -10.85f);
			gl.glVertex3f(-7.68f, 4.0f, -10.85f);
			gl.glVertex3f(-7.68f, 4.0f, -3.85f);
			gl.glVertex3f(-14.72f, 4.0f, -3.85f);
		gl.glEnd();

		//Door
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(-7.6f, 0.0f, -6.3f);
			gl.glVertex3f(-7.6f, 0.0f, -8.35f);
			gl.glVertex3f(-7.6f, 2.0f, -8.35f);
			gl.glVertex3f(-7.6f, 2.0f, -6.3f);
		gl.glEnd();

		//Door
		gl.glBegin(GL.GL_QUADS);
			gl.glVertex3f(-12.25f, 0.0f, -3.75f);
			gl.glVertex3f(-10.17f, 0.0f, -3.75f);
			gl.glVertex3f(-10.17f, 2.0f, -3.75f);
			gl.glVertex3f(-12.25f, 2.0f, -3.75f);
		gl.glEnd();

		gl.glEnable(GL.GL_LIGHTING);
		gl.glPopMatrix();

		gl.glDisable(GL.GL_BLEND);
	}

	private boolean inWater = false;
	private void inWater(GL gl)
	{
		FMOD_RESULT result;

		if(xListenerPos > -14.75f && xListenerPos < -7.6f
				&& zListenerPos > -10.85f && zListenerPos < -3.75f
				&& yListenerPos < 5.0f)
		{
			/*
			 * Use opengl fog to make it look like we are in water
			 */
			if(!inWater)
			{
				gl.glEnable(GL.GL_FOG);

				FMOD_REVERB_PROPERTIES reverbprops = FMOD_REVERB_PROPERTIES.create();
				result = fmodEventSystem.getReverbPreset("waterroom", reverbprops, null);
				ErrorCheck(result);
				fmodEventSystem.setReverbProperties(reverbprops);
				ErrorCheck(result);
				reverbprops.release();
				
				result = global_lowpass.setBypass(false);
				ErrorCheck(result);

				inWater = true;
			}
		}
		else
		{
			if(inWater)
			{
				/*
				 * Disable fog (water)
				 */
				gl.glDisable(GL.GL_FOG);
				
				FMOD_REVERB_PROPERTIES reverbprops = FMOD_REVERB_PROPERTIES.create();
				result = fmodEventSystem.getReverbPreset("scarycave", reverbprops, null);
				ErrorCheck(result);
				fmodEventSystem.setReverbProperties(reverbprops);
				ErrorCheck(result);
				reverbprops.release();

				if(!global_lowpass.isNull())
				{
					result = global_lowpass.setBypass(true);
					ErrorCheck(result);
				}

				inWater = false;
			}
		}
	}

	void drawSkyBox(GL gl)
	{
		gl.glPushMatrix();
		gl.glTranslatef(xListenerPos, 0.0f, yListenerPos);
		gl.glDisable(GL.GL_LIGHTING);
		/*
		 * Walls
		 */
//		gl.glBindTexture(GL.GL_TEXTURE_2D, skyboxTexture[0]);
		skyboxTexture[0].bind();
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-150.0f, -150.0f, -150.0f);
			gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-150.0f, 150.0f, -150.0f);
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(150.0f, 150.0f, -150.0f);
			gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(150.0f, -150.0f, -150.0f);
		gl.glEnd();

//		gl.glBindTexture(GL.GL_TEXTURE_2D, skyboxTexture[1]);
		skyboxTexture[1].bind();
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(150.0f, -150.0f, -150.0f);
			gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(150.0f, 150.0f,-150.0f);
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(150.0f, 150.0f, 150.0f);
			gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(150.0f, -150.0f, 150.0f);
		gl.glEnd();

//		gl.glBindTexture(GL.GL_TEXTURE_2D, skyboxTexture[2]);
		skyboxTexture[2].bind();
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-150.0f, -150.0f, 150.0f);
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-150.0f, 150.0f, 150.0f);
			gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(150.0f, 150.0f, 150.0f);
			gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(150.0f, -150.0f, 150.0f);
		gl.glEnd();

//		gl.glBindTexture(GL.GL_TEXTURE_2D, skyboxTexture[3]);
		skyboxTexture[3].bind();
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-150.0f, -150.0f, -150.0f);
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-150.0f, 150.0f, -150.0f);
			gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-150.0f, 150.0f, 150.0f);
			gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-150.0f, -150.0f, 150.0f);
		gl.glEnd();

		/*
		 * Top
		 */
//		gl.glBindTexture(GL.GL_TEXTURE_2D, skyboxTexture[4]);
		skyboxTexture[4].bind();
		gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(-150.0f, 150.0f, -150.0f);
			gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(150.0f, 150.0f, -150.0f);
			gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(150.0f, 150.0f, 150.0f);
			gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-150.0f, 150.0f, 150.0f);
		gl.glEnd();

		/*
		 * Bottom
		 */
//		 gl.glBindTexture(GL.GL_TEXTURE_2D, skyboxTexture[5]);
		skyboxTexture[5].bind();
		 gl.glBegin(GL.GL_QUADS);
			 gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-150.0f, -150.0f, -150.0f);
			 gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(150.0f, -150.0f, -150.0f);
			 gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f(150.0f, -150.0f, 150.0f);
			 gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f(-150.0f, -150.0f, 150.0f);
		 gl.glEnd();

		 gl.glEnable(GL.GL_LIGHTING);
		 gl.glPopMatrix();
	}
	
	void updateObjectSoundPos(Object3D object)
	{
		if(!object.event.isNull())
		{
			FMOD_VECTOR pos = FMOD_VECTOR.create(object.xPos, object.yPos, object.zPos);
			FMOD_VECTOR oldPos = FMOD_VECTOR.create();
			object.event.get3DAttributes(oldPos, null, null);

			FMOD_VECTOR vel = FMOD_VECTOR.create();
			vel.setX((pos.getX() - oldPos.getX()) *  (1000 / (float)INTERFACE_UPDATETIME));
			vel.setY((pos.getY() - oldPos.getY()) *  (1000 / (float)INTERFACE_UPDATETIME));
			vel.setZ((pos.getZ() - oldPos.getZ()) *  (1000 / (float)INTERFACE_UPDATETIME));
			object.event.set3DAttributes(pos, vel, null);
		}
	}
	
	void outputText(GL gl, int x, int y, String text)
	{
		gl.glRasterPos2f(x, y);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_18, text);
	}
	void renderText(GL gl, int x, int y, Stack<String> stack)
	{
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		for(int count = 0; count < stack.size(); count++)
		{
			outputText(gl, x, y, stack.peek());
			stack.pop();

			y -= 17;
		}
	}
	void renderUiText(GL gl)
	{
		/*
		 * Render help text
		 */
		if(showhelp)
		{
			int x = 10;
			int y = height - 20;

			gl.glColor3f(1.0f, 1.0f, 1.0f);
			outputText(gl, x, y,     "F1   - Toggle help");
			outputText(gl, x, y-=18, "F2   - Toggle fullscreen");
			outputText(gl, x, y-=18, "F3   - Toggle wireframe rendering");
			outputText(gl, x, y-=18, "F11  - Toggle debug info");
			outputText(gl, x, y-=18, "--");
			outputText(gl, x, y-=18, "up (arrow) - Move forward");
			outputText(gl, x, y-=18, "down       - Move backward");
			outputText(gl, x, y-=18, "left       - Move left");
			outputText(gl, x, y-=18, "right      - Move right");
			outputText(gl, x, y-=18, "page up    - Move up");
			outputText(gl, x, y-=18, "page down  - Move down");
			outputText(gl, x, y-=18, "Mouse (hold left button) - look direction");
			outputText(gl, x, y-=18, "--");
//			outputText(gl, x, y-=18, "V/v   - Master volume up/down");
			outputText(gl, x, y-=18, "Z/z   - Ambient sound volume up/down");
		}
		else
		{
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			outputText(gl, 10, height - 20, "F1 - Help");
		}

		/*
	        Render debug text
		 */
		if(SHOW_GUI_DEBUG_TEXT)
		{
			if(showdebug)
			{
				renderText(gl, width - (width/2), height - 20, debugText);
			}
			else
			{
				/*
				 * Otherwise just pop everything off the stack
				 */
				for(int count = 0; count < debugText.size(); count++)
				{
					debugText.pop();
				}
			}

			/*
			 * Render status text
			 */
			renderText(gl, 10, 20, statusText);
		}
	}



	private FMOD_VECTOR lastOffset = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);
	void doGeometryMovement(GL gl)
	{
		FMOD_RESULT result;
		boolean[] doorMoving = new boolean[]{true, true, true, true};

		// example of moving individual polygon vertices
		//TODO float or int ?
		float xGeometryWarpPos = -30.0f;
		float zGeometryWarpPos = -21.0f;
		float dx = xListenerPos - xGeometryWarpPos;
		float dz = zListenerPos - zGeometryWarpPos;
		if(dx * dx + dz * dz < 30.0f * 30.0f)
		{
			if(sin(accumulatedTime * 1.0f) > 0.0f)
			{
				FMOD_VECTOR offset = FMOD_VECTOR.create((float)sin(accumulatedTime * 2.0f), 0.0f, (float)cos(accumulatedTime * 2.0f));
				for(int poly = 0; poly < walls.numPolygons; poly++)
				{
					Polygon polygon = walls.polygons[poly];
					for(int i = 0; i < polygon.numVertices; i++)
					{
						FMOD_VECTOR vertex = walls.vertices[walls.indices[polygon.indicesOffset + i]];

						dx = vertex.getX() - xGeometryWarpPos;
						dz = vertex.getZ() - zGeometryWarpPos;
						if(dx * dx + dz * dz > 90.0f)
							continue;
						vertex.setX(vertex.getX() - lastOffset.getX());
						vertex.setY(vertex.getY() - lastOffset.getY());
						vertex.setZ(vertex.getZ() - lastOffset.getZ());

						vertex.setX(vertex.getX() + offset.getX());
						vertex.setY(vertex.getY() + offset.getY());
						vertex.setZ(vertex.getZ() + offset.getZ());
						
						result = walls.geometry.setPolygonVertex(poly, i, vertex);
						ErrorCheck(result);
					}	
				}
				lastOffset = offset;
			}
		}

		// example of rotation and a geometry object
		FMOD_VECTOR up = FMOD_VECTOR.create(0.0f, 1.0f, 0.0f);
		FMOD_VECTOR forward = FMOD_VECTOR.create((float)sin(accumulatedTime * 0.5f), 0.0f, (float)cos(accumulatedTime * 0.5f));
		result = rotatingMesh.geometry.setRotation(forward, up);
		ErrorCheck(result);
		FMOD_VECTOR pos = FMOD_VECTOR.create();
		pos.setX(12.0f);
		pos.setY((float)sin(accumulatedTime) * 0.4f + 0.1f);
		pos.setZ(0.0f);
		result = rotatingMesh.geometry.setPosition(pos);
		ErrorCheck(result);
		drawGeometry(gl, rotatingMesh);


		// example of moving doors
		// door 1
		pos.setX(3.25f);
		pos.setY(((float)sin(accumulatedTime)) * 2.0f + 1.0f);
		if(pos.getY() < 0.0f)
		{
			pos.setY(0);
			doorMoving[0] = false;
		}
		if(pos.getY() > 2.0f)
		{
			pos.setY(2.0f);
			doorMoving[0] = false;
		}
		pos.setZ(11.5f);


		result = doorList[0].geometry.setPosition(pos);
		ErrorCheck(result);


		// door 2
		pos.setX(0.75f);
		pos.setY(((float)sin(accumulatedTime)) * 2.0f + 1.0f);
		if(pos.getY() < 0.0f)
		{
			pos.setY(0);
			doorMoving[1] = false;
		}
		if(pos.getY() > 2.0f)
		{
			pos.setY(2.0f);
			doorMoving[1] = false;
		}
		pos.setZ(14.75f);


		result = doorList[1].geometry.setPosition(pos);
		ErrorCheck(result);


		// door 3
		pos.setX(8.25f);
		pos.setY(((float)sin(accumulatedTime)) * 2.0f + 1.0f);
		if(pos.getY() < 0.0f)
		{
			pos.setY(0);
			doorMoving[2] = false;
		}
		if(pos.getY() > 2.0f)
		{
			pos.setY(2.0f);
			doorMoving[2] = false;
		}
		pos.setZ(14.75f);


		result = doorList[2].geometry.setPosition(pos);
		ErrorCheck(result);


		// door 4
		pos.setX(33.0f);
		pos.setY(((float)sin(accumulatedTime)) * 2.0f + 1.0f);
		if(pos.getY() < 0.0f)
		{
			pos.setY(0);
			doorMoving[3] = false;
		}
		if(pos.getY() > 2.0f)
		{
			pos.setY(2.0f);
			doorMoving[3] = false;
		}
		pos.setZ(-0.75f);


		result = doorList[3].geometry.setPosition(pos);
		ErrorCheck(result);
	}

	void doSoundMovement()
	{	
		objects[0].zPos = 10.0f * (float)sin(accumulatedTime*0.3);
		updateObjectSoundPos(objects[0]);
		objects[5].zPos = -22.0f + 8.0f * (float)sin(accumulatedTime);
		updateObjectSoundPos(objects[5]);
	}

	private FMOD_VECTOR lastpos = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);
	private boolean bFirst = true;
	private FMOD_VECTOR lastVel = FMOD_VECTOR.create(0.0f, 0.0f, 0.0f);
	void doListenerMovement()
	{
		// Update user movement
		final float MOVEMENT_SPEED = 0.1f;

		float forwardF = 0.0f;
		if(moveForward)
			forwardF += (MOVEMENT_SPEED * (moveFast ? 2.0f : 1.0f));
		if(moveBackward)
			forwardF -= (MOVEMENT_SPEED * (moveFast ? 2.0f : 1.0f));

		float rightF = 0.0f;
		if(moveLeft)
			rightF -= (MOVEMENT_SPEED * (moveFast ? 2.0f : 1.0f));
		if(moveRight)
			rightF += (MOVEMENT_SPEED * (moveFast ? 2.0f : 1.0f));	

		float upF = 0.0f;
		if(moveUp)
			upF += (MOVEMENT_SPEED * (moveFast ? 2.0f : 1.0f));
		if(moveDown)
			upF -= (MOVEMENT_SPEED * (moveFast ? 2.0f : 1.0f));

		float xRight = (float)cos(yRotation * (PI / 180.0f));
		float yRight = 0.0f;
		float zRight = (float)sin(yRotation * (PI / 180.0f));

		xListenerPos += xRight * rightF;
		yListenerPos += yRight * rightF;
		zListenerPos += zRight * rightF;

		float xForward =  (float)sin(yRotation * (PI / 180.0f)) * (float)cos(xRotation  * (PI / 180.0f));
		float yForward = -(float)sin(xRotation * (PI / 180.0f));
		float zForward = -(float)cos(yRotation * (PI / 180.0f)) * (float)cos(xRotation  * (PI / 180.0f));

		xListenerPos += xForward * forwardF;
		yListenerPos += yForward * forwardF;
		zListenerPos += zForward * forwardF;

		yListenerPos += upF;

		if(yListenerPos < 1.0f)
			yListenerPos = 1.0f;

		// cross product
		float xUp = yRight * zForward - zRight * yForward;
		float yUp = zRight * xForward - xRight * zForward;
		float zUp = xRight * yForward - yRight * xForward;

		// Update listener
		{
			FMOD_VECTOR listenerVector = FMOD_VECTOR.create(xListenerPos, yListenerPos, zListenerPos);

			FMOD_VECTOR forward = FMOD_VECTOR.create();
			FMOD_VECTOR up = FMOD_VECTOR.create();
			FMOD_VECTOR vel = FMOD_VECTOR.create();

			forward.setX(xForward);
			forward.setY(yForward);
			forward.setZ(zForward);
			up.setX(xUp);
			up.setY(yUp);
			up.setZ(zUp);

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
			lastpos = listenerVector;
			lastVel = vel;

			FMOD_RESULT result = fmodSystem.set3DListenerAttributes(0, listenerVector, vel, forward, up);
			ErrorCheck(result);
		}
	}

	void doUpdateVolume()
	{
		if(ambientVolUp)
		{
			ambientVolume += 0.025;
			if(ambientVolume > 1.0f)
				ambientVolume = 1.0f;

			String volumestring = String.format("Ambient Volume: %.3f", ambientVolume);

			if(SHOW_GUI_DEBUG_TEXT)
				statusText.push(volumestring);
		}
		else if(ambientVolDown)
		{
			ambientVolume -= 0.025;
			if(ambientVolume < 0.0f)
				ambientVolume = 0.0f;

			String volumestring = String.format("Ambient Volume: %.3f", ambientVolume);

			if(SHOW_GUI_DEBUG_TEXT)
				statusText.push(volumestring);
		}
	}
	
	private long INTERFACE_UPDATETIME = 17;
	private float frames = 0;
	private long firstFrame = -1;
	void timerFunc(GL gl)
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
		
		FMOD_RESULT result;

		doGeometryMovement(gl);

		doSoundMovement();
		doListenerMovement();
		doUpdateVolume();

		result = FmodDesigner.NetEventSystem_Update();
		ErrorCheck(result);
		result = fmodEventSystem.update();
		ErrorCheck(result);

		accumulatedTime += INTERFACE_UPDATETIME / 1000.0f;
	}
	
	
	
    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e)
    {
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_UP:
				moveForward = true;
				break;
			case KeyEvent.VK_DOWN:
				moveBackward = true;
				break;
			case KeyEvent.VK_LEFT:
				moveLeft = true;
				break;
			case KeyEvent.VK_RIGHT:
				moveRight = true;
				break;
			case KeyEvent.VK_PAGE_UP:
				moveUp = true;
				break;
			case KeyEvent.VK_PAGE_DOWN:
				moveDown = true;
				break;
			case KeyEvent.VK_Z:
				if(e.isShiftDown())
					ambientVolUp = true;
				else
					ambientVolDown = true;
				break;
			case KeyEvent.VK_V:
				if(e.isShiftDown())
					masterVolUp = true;
				else
					masterVolDown = true;
				break;
			case KeyEvent.VK_P:
				FloatBuffer value = BufferUtils.newFloatBuffer(1);
				fmodEventParameter.getValue(value);
				if(e.isShiftDown())
					fmodEventParameter.setValue(value.get(0) + 20.0f);
				else
					fmodEventParameter.setValue(value.get(0) - 20.0f);
				break;
			case KeyEvent.VK_F:
				moveFast = true;
				break;
		}
	}
    public void keyReleased(KeyEvent e)
    {
		switch(e.getKeyCode())
		{
			case KeyEvent.VK_UP:
				moveForward = false;
				break;
			case KeyEvent.VK_DOWN:
				moveBackward = false;
				break;
			case KeyEvent.VK_LEFT:
				moveLeft = false;
				break;
			case KeyEvent.VK_RIGHT:
				moveRight = false;
				break;
			case KeyEvent.VK_PAGE_UP:
				moveUp = false;
				break;
			case KeyEvent.VK_PAGE_DOWN:
				moveDown = false;
				break;
			case KeyEvent.VK_Z:
				if(e.isShiftDown())
					ambientVolUp = false;
				else
					ambientVolDown = false;
				break;
			case KeyEvent.VK_V:
				if(e.isShiftDown())
					masterVolUp = false;
				else
					masterVolDown = false;
				break;
			case KeyEvent.VK_F:
				moveFast = false;
				break;
				
			case KeyEvent.VK_ESCAPE:
				animator.stop();
				
				//TODO Skip glDeleteTextures for simplicity (GLContext is not current)
//				gl.glDeleteTextures(1, texture); 

				freeGeometry(walls);
				freeGeometry(rotatingMesh);
				freeGeometry(doorList[0]);
				freeGeometry(doorList[1]);
				freeGeometry(doorList[2]);
				freeGeometry(doorList[3]);

				fmodEventSystem.release();
				FmodDesigner.NetEventSystem_Shutdown();
				
				exit(0);
				break;
			
			case KeyEvent.VK_F1:
				showhelp = !showhelp;
				break;
			case KeyEvent.VK_F2:
				//FIXME fullscreen
				if(fullscreen)
				{
//					glutPositionWindow(20, 40);
//					glutReshapeWindow(500, 500);
					fullscreen = false;
				}
				else
				{       
//					glutFullScreen();
					fullscreen = true;
				}
				break;
			case KeyEvent.VK_F3:
				rendermode = (rendermode == GL.GL_LINE) ? GL.GL_FILL : GL.GL_LINE;
				break;
			case KeyEvent.VK_F11:
				showdebug = !showdebug;
				break;
			case KeyEvent.VK_F8:
				rotatingMesh.geometry.setActive(false);
				walls.geometry.setActive(false);
				break;
			case KeyEvent.VK_F9:
				rotatingMesh.geometry.setActive(true);
				walls.geometry.setActive(true);
				break;
		}
	}
    
	
	/*Mouse Listener*/
	
	public void mouseClicked(MouseEvent e){}
	public void mousePressed(MouseEvent e)
	{
		if(e.getButton() == MouseEvent.BUTTON1)
		{
			xMouse = e.getX();
			yMouse = e.getY();
		}
	}
	public void mouseReleased(MouseEvent e){}
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