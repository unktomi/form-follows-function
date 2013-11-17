/**
 * 							LibLoader
 *
 * Projet created for loading NativeFmod & NativeFmodEx libraries.
 * Copyright © 2007 Jérôme JOUVIE (Jouvieje)
 *
 * Created on 25 mar. 2007
 * @version file v1.1
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
 * Project created to fix library loading, System.loadLibrary load library
 * with RTLD_LOCAL, RTLD_GLOCAL is needed for loading well NativeFmodEx under linux.
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

package org.jouvieje.libloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdesktop.applet.util.JNLPAppletLauncher;
import org.jdesktop.applet.util.JNLPAppletLauncher.LibraryLoader;

public class LibLoader
{
	/** Prints output debug in System.out */
	public static boolean DEBUG = false;
	/** Prints output debug in a Dialog box. Only if <code>DEBUG</code> is enabled. */
	public static boolean DEBUG_DIALOG = false;
	public static boolean ENABLE_JWS = true;

	protected static boolean librariesLoaded = false;
	public static boolean isLibrariesLoaded()
	{
		return librariesLoaded;
	}

	/**
	 * Loads LibLoader libraries.<BR>
	 * Should be called first.
	 * @throws UnsatisfiedLinkError if the loading fails
	 */
	public static void loadLibraries() throws UnsatisfiedLinkError
	{
		if(!isLibrariesLoaded())
		{
			try
			{
				loadLibrary("LibLoader");
				librariesLoaded = true;
			}
			catch(UnsatisfiedLinkError e)
			{
				try
				{
					loadLibrary("LibLoader64");
					librariesLoaded = true;
				}
				catch(UnsatisfiedLinkError e1)
				{
					if(DEBUG)
					{
						e.printStackTrace();
						e1.printStackTrace();						
					}
					throw new UnsatisfiedLinkError(e.getMessage()+" | "+e1.getMessage());
				}
			}
		}
	}

	public static void loadLibrary(final String libraryName) throws UnsatisfiedLinkError
	{
AccessController.doPrivileged(new PrivilegedAction(){ public Object run() {
		boolean loaded = false;
		
		//Use applet-launcher
		boolean useAppletLauncher = Boolean.valueOf(System.getProperty("sun.jnlp.applet.launcher")).booleanValue();
		if(useAppletLauncher)
		{
			try
			{
				Class appletLauncherClass = Class.forName("org.jdesktop.applet.util.JNLPAppletLauncher");
				JNLPAppletLauncher.DEBUG = DEBUG;
				Method loadLibraryMethod = appletLauncherClass.getDeclaredMethod(
						"loadLibrary",
							new Class[] { String.class, LibraryLoader.class });
				loadLibraryMethod.invoke(null, new Object[] {
						libraryName,
						new LibraryLoader() {
							public void loadLibrary(String fullLibraryName) throws UnsatisfiedLinkError
							{
								/*
								 * Use first System.load, then LibLoader.loadLibraryFromFullPath
								 */
								try {
									System.load(fullLibraryName);
								}
								catch(UnsatisfiedLinkError e) {
									long handle = 0;
									try {
										if (DEBUG) {
											System.err.println("    LibLoader loading : " + fullLibraryName + "");
										}
										handle = loadLibraryFromFullPath(fullLibraryName);
									} catch(Exception f) {
										handle = 0;
									} catch(Error f) {
										handle = 0;
									}
									if(handle == 0) {
										throw e;
									}
								}
							}
						}});
				loaded = true;
			}
			catch(ClassNotFoundException ex)
			{
				printlnDebug("loadLibrary(" + libraryName + ")");
				printlnDebug(ex.toString());
				printlnDebug("Attempting to use System.loadLibrary instead");
			}
			catch(Exception e)
			{
				Throwable t = e;
				if (t instanceof InvocationTargetException) {
					t = ((InvocationTargetException) t).getTargetException();
				}
				if (t instanceof Error)
					throw (Error) t;
				if (t instanceof RuntimeException) {
					throw (RuntimeException) t;
				}
				// Throw UnsatisfiedLinkError for best compatibility with System.loadLibrary()
				throw (UnsatisfiedLinkError) new UnsatisfiedLinkError().initCause(e);
			}
		}

		if (!loaded) {
			System.loadLibrary(libraryName);
		}
return null; }});
	}

	/**
	 * Loads a library with <code>RTLD_NOW | RTLD_GLOBAL</code> mode<BR>
	 * <I>(<code>RTLD_NOW | RTLD_GLOBAL</code> mean external symbol will be available for other libraries)</I>.
	 * @param lib library <B><U>FULL</U></B> file name or absolute/relative path (including <B><U>FULL</U></B> library name)
	 */
	public static long loadLibraryFromFullName(String libraryName, String fullName) throws UnsatisfiedLinkError
	{
		return loadLibraryFromFullName(libraryName, fullName, RTLD.RTLD_NOW | RTLD.RTLD_GLOBAL);
	}
	/**
	 * Loads a library with specified <code>RTLD</code> mode<BR>
	 * @param lib library <B>FULL</B> file name or full path
	 * @param mode an RTLD mode for the library loading
	 */
	public static long loadLibraryFromFullName(final String libraryName, final String fullName, final int mode) throws UnsatisfiedLinkError
	{
		if(!isLibrariesLoaded()) return 0;

		//Build path list
		ArrayList paths = new ArrayList();
		
		/*
		 * Copy the library localy and 
		 */
		if(ENABLE_JWS)
		{
			printlnDebug("Preparing library extraction of "+fullName);
			try
			{
				printlnDebug("Opening for reading: "+fullName);
				InputStream is = new LibLoader().getClass().getResourceAsStream("/"+fullName);
				if(is == null) {
					printlnDebug("Re-opening for reading: "+fullName);
					is = Class.class.getResourceAsStream("/"+fullName);
				}
				if(is != null)
				{
					printlnDebug("Library sucessfully opened.");
					//Create output directory
					String destDir = getProperty("java.io.tmpdir") + File.separator + "LibLoader" + File.separator;	//TODO Add version
					File dir = new File(destDir);
					printlnDebug("Creating destination directory at : "+destDir);
					dir.mkdirs();
					//Write library to output directory
					BufferedInputStream in = new BufferedInputStream(is);
					printlnDebug("Opening output for writting");
					BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(dir, fullName)));
					printlnDebug("Copying original library to output");
					byte[] readBuffer = new byte[8192];
					int bytesRead = 0;
					while((bytesRead = is.read(readBuffer)) > 0)
						out.write(readBuffer, 0, bytesRead);
					printlnDebug("Closing files");
					in.close();
					out.close();
					printlnDebug("Sucessfully extracted");
					paths.add(destDir);
				}
			}
			catch(FileNotFoundException e)
			{
				if(DEBUG)
					e.printStackTrace();
			}
			catch(IOException e)
			{
				if(DEBUG)
					e.printStackTrace();
			}
		}

		//Search in java.library.path
		String javaLibraryPath = getProperty("java.library.path");
		if(javaLibraryPath != null)
		{
			StringTokenizer tokens = new StringTokenizer(javaLibraryPath, File.pathSeparator);
			while(tokens.hasMoreTokens())
				paths.add(tokens.nextToken());
		}
		//Full path ?
		File file = new File(fullName);
		if(file.isAbsolute())
			paths.add(file.getAbsolutePath());
		
		//Load from known search path
		long handle = 0;
		for(Iterator i = paths.iterator(); i.hasNext();)
		{
			String path = (String)i.next();
			if(!path.endsWith(File.separator))
				path += File.separator;
			String fullPath = path+fullName;
			handle = LibLoaderJNI.dlopen(fullPath.getBytes(), mode);
			if(handle == 0)
			{
				printlnDebug("ERROR: "+LibLoaderJNI.dlerror());
			}
			else
			{
				printlnDebug(fullName+" successfully loaded from "+path+" [handle=0x"+Long.toHexString(handle)+"]");
				break;
			}
		}
		if(handle == 0 && libraryName != null)
		{
AccessController.doPrivileged(new PrivilegedAction(){ public Object run() {
			printlnDebug("Fail to load library.");
			String error = LibLoaderJNI.dlerror();
			printlnDebug("ERROR: "+error);

			boolean loaded = false;
			
			//Use applet-launcher
			boolean useAppletLauncher = Boolean.valueOf(getProperty("sun.jnlp.applet.launcher")).booleanValue();
			if(useAppletLauncher)
			{
				try
				{
					Class appletLauncherClass = Class.forName("org.jdesktop.applet.util.JNLPAppletLauncher");
					JNLPAppletLauncher.DEBUG = DEBUG;
					Method loadLibraryMethod = appletLauncherClass.getDeclaredMethod(
							"loadLibrary",
							new Class[] { String.class, LibraryLoader.class });
					loadLibraryMethod.invoke(null, new Object[] {
							libraryName,
							new LibraryLoader() {
								public void loadLibrary(String fullLibraryName) throws UnsatisfiedLinkError
								{
									/*
									 * Use first LibLoader.loadLibraryFromFullPath, then System.load
									 */
									long handle = 0;
									try {
										if (DEBUG) {
											System.err.println("    LibLoader loading : " + fullLibraryName + "");
										}
										handle = loadLibraryFromFullPath(fullLibraryName);
									} catch(Exception f) {
										handle = 0;
									} catch(Error f) {
										handle = 0;
									}
									if(handle == 0) {
										if (DEBUG) {
											System.err.println("    loading: " + fullLibraryName + "");
										}

										System.load(fullLibraryName);
									}
								}
							} });
					loaded = true;
				}
				catch(ClassNotFoundException ex)
				{
					printlnDebug("loadLibrary(" + libraryName + ")");
					printlnDebug(ex.toString());
					printlnDebug("Attempting to use System.loadLibrary instead");
				}
				catch(Exception e)
				{
					Throwable t = e;
					if(t instanceof InvocationTargetException) {
						t = ((InvocationTargetException) t).getTargetException();
					}
					if(t instanceof Error)
						throw (Error) t;
					if(t instanceof RuntimeException) {
						throw (RuntimeException) t;
					}
					//Throw UnsatisfiedLinkError for best compatibility with System.loadLibrary()
					throw (UnsatisfiedLinkError) new UnsatisfiedLinkError().initCause(e);
				}
			}
			
			if(!loaded)
				throw new UnsatisfiedLinkError(error);
return null; }});
		}

		LibLoaderJNI.dlerror();	/* Clear any error */
		return handle;
	}

	public static long loadLibraryFromFullPath(String fullPath) throws UnsatisfiedLinkError
	{
		return loadLibraryFromFullPath(fullPath, RTLD.RTLD_NOW | RTLD.RTLD_GLOBAL);
	}
	public static long loadLibraryFromFullPath(String fullPath, int mode) throws UnsatisfiedLinkError
	{
		if(!isLibrariesLoaded()) return 0;

		long handle = LibLoaderJNI.dlopen(fullPath.getBytes(), mode);
		if(handle == 0) {
			printlnDebug("ERROR: "+LibLoaderJNI.dlerror());
		} else {
			printlnDebug("Library successfully loaded from "+fullPath+" [handle=0x"+Long.toHexString(handle)+"]");
		}

		LibLoaderJNI.dlerror();	/* Clear any error */
		return handle;
	}

	private static Vector libraryPaths = null;
	private static String pathSeparator = null;
	private static String fileSeparator = null;
	public static boolean loadMacLibrary(String fullLibraryName)
	{
		if(libraryPaths == null || pathSeparator == null)
		{
			String libPaths = getProperty("java.library.path");
			pathSeparator = getProperty("path.separator");
			fileSeparator = getProperty("file.separator");
			libraryPaths = new Vector();

			int index;
			while((index = libPaths.indexOf(pathSeparator)) != -1)
			{
				String path = libPaths.substring(0, index);
				if(!path.endsWith(fileSeparator))
					path += fileSeparator;
				libraryPaths.add(path);

				libPaths = libPaths.substring(index+pathSeparator.length(), libPaths.length());
			}
			if(!libPaths.endsWith(fileSeparator))
				libPaths += fileSeparator;
			libraryPaths.add(libPaths);
		}

		for(int i = 0; i < libraryPaths.size(); i++)
		{
			final File file = new File((String)libraryPaths.get(i)+fullLibraryName);
			if(file.exists())
			{
				printlnDebug("LIB PATH="+file.getAbsolutePath());
				try
				{
AccessController.doPrivileged(new PrivilegedAction(){ public Object run() {
					System.load(file.getAbsolutePath());
return null; }});
				}
				catch(UnsatisfiedLinkError e)
				{
					printlnDebug(" FAIL="+e.getMessage());
					continue;
				}
				printlnDebug("LOADED");
				return true;
			}
		}
		return false;
	}
	
	private static String getProperty(final String prop)
	{
		return (String)AccessController.doPrivileged(new PrivilegedAction(){ public Object run() {
			return System.getProperty(prop);
		}});
	}

	public static void closeLibrary(long handle) throws UnsatisfiedLinkError
	{
		if(!isLibrariesLoaded() || handle == 0)
			return;

		int error = LibLoaderJNI.dlclose(handle);
		if(error != 0)
		{
			printlnDebug("Fail to close library.");
			String s = LibLoaderJNI.dlerror();
			printlnDebug("ERROR: "+s);
			throw new UnsatisfiedLinkError(s);
		}
		else
		{
			printlnDebug("Library sucessfully closed.");
		}
	}

	private static void printlnDebug(String s)
	{
		if(DEBUG) {
			System.out.println("[LibLoader] "+s);
			if(DEBUG_DIALOG)
				javax.swing.JOptionPane.showMessageDialog(null, "[LibLoader] "+s);
		}
	}
}