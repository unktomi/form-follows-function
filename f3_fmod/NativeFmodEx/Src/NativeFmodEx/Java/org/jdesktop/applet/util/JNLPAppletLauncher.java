/*
 * $RCSfile: JNLPAppletLauncher.java,v $
 *
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 * $Revision: 1.25 $
 * $Date: 2007/10/18 22:48:10 $
 * $State: Exp $
 */

package org.jdesktop.applet.util;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * The JNLPAppletLauncher is a general purpose JNLP-based applet
 * launcher class for deploying applets that use extension libraries
 * containing native code. It allows applets to use extensions like
 * Java 3D, JOGL, and JOAL very easily, with just a few additional
 * parameters to the <code>&lt;applet&gt;</code> tag, on Java SE
 * versions as far back as 1.4.2.
 *
 * <p>
 *
 * Like Java Web Start, the JNLPAppletLauncher uses an extension's
 * .jnlp file to locate the native resources for a given extension.
 * The applet developer only needs to specify the platform-independent
 * .jar files containing the .class files for the extension. The
 * platform-specific "nativelib" .jar files are downloaded
 * automatically from the same server that hosts the extension's Java
 * Web Start binaries.
 *
 * <p>
 *
 * Extensions that support JNLPAppletLauncher include Java 3D, JOGL,
 * and JOAL. More can be added without needing to modify the
 * JNLPAppletLauncher. See the section below on <a
 * href="#MODIFYING">modifying extensions to work with the
 * JNLPAppletLauncher</a>.
 *
 * <h2> How to Deploy Applets Using the JNLPAppletLauncher </h2>
 * <p>
 *
 * The <code>applet-launcher.jar</code> file containing the
 * JNLPAppletLauncher class must be signed with the same certificate
 * as the extension's native resources, for example "sun microsystems,
 * inc.". The user will receive a security dialog and will be prompted
 * to accept the certificate for the JLNPAppletLauncher.  The applet
 * being deployed may be either signed or unsigned; if it is unsigned,
 * it runs inside the security sandbox, and if it is signed, the user
 * receives a security dialog to accept the certificate for the applet
 * (in addition to the applet-launcher jar, if it is signed by a
 * different entity).
 *
 * <p>
 *
 * The steps for deploying such applets are straightforward. First,
 * the <code>archive</code> parameter to the applet tag must contain
 * <code>applet-laucher.jar</code>, the extension .jar files, and any
 * jar files associated with your applet. See the section on <a
 * href="#ORGANIZING">organizing jar files</a> for more details.
 *
 * <p>
 *
 * Second, the name of your applet's main class and a textual
 * description must be specified via the applet tag parameters
 * <code>subapplet.classname</code> and
 * <code>subapplet.displayname</code>.
 *
 * <p>
 *
 * Finally, the URLs for the extension .jnlp files being used must be
 * specified as parameters. The <code>jnlpNumExtensions</code>
 * parameter indicates the number of JNLP files that are referenced,
 * and for <code>n</code> such files, their URLs are passed in as
 * parameters <code>jnlpExtension1</code> ...
 * <code>jnlpExtension[n]</code>.
 *
 * <h2><a name="ORGANIZING">Organizing Jar Files</a></h2>
 *
 * <p>
 *
 * Traditionally, applets are specified with a codebase and an archive
 * parameter, the latter which is a list of jar files relative to that
 * codebase. The codebase is optional and defaults to the directory on
 * the web server containing the HTML document which contains the
 * applet tag. See the documentation for the <a
 * href="http://java.sun.com/j2se/1.4.2/docs/guide/misc/applet.html">applet
 * tag</a>.
 *
 * <p>
 *
 * It is not well documented, but at least in the Sun JRE at least as
 * far back as Java SE 1.4.2, it is possible to use absolute URLs in
 * the applet tag's archive parameter. This functionality works on all
 * major operating systems: Windows, Mac OS X, Linux, and Solaris.
 * This means that you can pull code resources from multiple web
 * servers, not just one, in similar fashion to Java Web Start and its
 * extension mechanism. (The security implications are that each
 * unsigned piece of code downloaded from a separate server receives
 * sandboxed permissions to connect back to that server; if there are
 * multiple pieces of unsigned code on the stack during execution of
 * the program then the permissions will be the intersection of all of
 * those on the stack, implying that no programmatic network
 * connections back to the web server(s) will be allowed. See the <a
 * href="http://java.sun.com/sfaq/">Applet Security FAQ</a> for more
 * details.)
 *
 * <p>
 *
 * This capability means that your applets can refer directly to
 * extensions like Java 3D and JOGL hosted on Sun's web servers
 * without having to duplicate their jar files on your web server.
 *
 * <p>
 *
 * To use this capability effectively with the JNLPAppletLauncher, you
 * need to pull in at least three primary pieces of code: the applet
 * launcher itself, your applet's code, and the Java code for the
 * extension, or extensions, your applet depends on. (Remember that
 * the JNLPAppletLauncher's primary function is to automatically
 * download the native code associated with these extensions, and not
 * the Java code for these extensions.)
 *
 * <p>
 *
 * You might choose to specify the codebase of your applet to point to
 * your web server's directory containing the jar files of your
 * applet, and specify absolute URLs to the
 * <code>applet-launcher.jar</code> and the extension jar files. Or
 * you might decide to point the codebase to the server which hosts
 * the applet launcher and specify all of the other resources,
 * including your applet, with absolute URLs. Or you might decide to
 * use all absolute URLs in your archive tag with no codebase. The
 * techniques are basically equivalent. We recommend either pointing
 * the codebase to the directory containing your applet's jars, using
 * relative URLs for your applet, and using absolute URLs for all
 * other resources; or using all absolute URLs.
 *
 * <p>
 *
 * Alternatively, you can re-host the jar files and/or JNLP files and
 * nativelib jars for the extensions you use on your own web
 * server. This has the advantage that your applet will connect to
 * fewer web servers upon startup, but has the disadvantages of
 * requiring additional maintenance on your part and not automatically
 * receiving updates to the extensions when they are published.
 *
 * <p>
 *
 * <b>Note</b> that if you are planning to call into your applet from
 * JavaScript that there are some <a
 * href="#SCRIPTING">scripting-related caveats</a> that you need to be
 * aware of.
 *
 * <p>
 *
 * The <code>jnlpExtension</code> parameters passed to the
 * JNLPAppletLauncher must be specified with absolute URLs.
 *
 * <p>
 *
 * The <a href="#EXAMPLES">examples</a> show how to use the
 * JNLPAppletLauncher in a few different scenarios.
 *
 * <h2>The codebase_lookup parameter</h2>
 *
 * <p>
 *
 * This applet parameter was not well documented until <a
 * href="http://java.sun.com/javase/6/docs/technotes/guides/plugin/developer_guide/special_attributes.html#codebase">recently</a>,
 * but it disables certain legacy behavior of the Java Plug-In. Before
 * the introduction of jar files, applets used to host their class
 * files and resources as flat files on the web server. Once jar files
 * were introduced, it was possible to improve the efficiency of
 * resource loading for applets, but (apparently) not without breaking
 * compatibility. An applet can specify the parameter
 *
 * <pre>
 * &lt;param name="codebase_lookup" value="false"&gt;
 * </pre>
 *
 * <p>
 *
 * to improve efficiency of its loading if it does not rely on
 * fetching flat files from the web server off the codebase.
 * We recommend setting this parameter.
 *
 * <h2>Applets using the OpenGL(r) 3D API</h2>
 *
 * <p>
 *
 * Applets using the OpenGL 3D graphics API, for example through JOGL
 * or Java 3D, may encounter robustness issues on the Windows platform
 * because Sun's Java 2D implementation on Windows uses Microsoft's
 * DirectDraw API. DirectDraw and OpenGL are incompatible at the
 * driver level.
 *
 * <p>
 *
 * As a workaround for this problem, the JNLPAppletLauncher supports
 * disabling the use of DirectDraw. Currently this can only be done on
 * a global basis, for all applets, but doing so is unlikely to slow
 * down other non-3D applets significantly.
 *
 * <p>
 *
 * Specifying the applet parameter
 *
 * <pre>
 * &lt;param name="noddraw.check" value="true"&gt;
 * </pre>
 *
 * <p>
 *
 * will cause the applet launcher, when run on Windows, to check to
 * see whether DirectDraw is enabled and, if so, will prompt the user
 * with a dialog box asking to disable it. A browser restart is
 * required if the setting is changed.
 *
 * <p>
 *
 * If the dialog box is undesirable in a given situation, you can
 * force the noddraw check to always disable DirectDraw with the two
 * applet parameters:
 *
 * <pre>
 * &lt;param name="noddraw.check" value="true"&gt;
 * &lt;param name="noddraw.check.silent" value="true"&gt;
 * </pre>
 *
 * <p>
 *
 * In this case it will not be obvious to the end user that a browser
 * restart might be required for best robustness, but you could
 * potentially document the need to try restarting the browser in case
 * of instability.
 *
 * <h2><a name="SCRIPTING">Scripting Support</a></h2>
 *
 * <p>
 *
 * The JNLPAppletLauncher supports interaction with the sub-applet via
 * the <code>getSubApplet()</code> method. Calling this method from
 * JavaScript will return the subordinate applet with which you can
 * then interact via JavaScript.
 *
 * <p>
 *
 * There are currently some scripting-related caveats associated with
 * <a href="#ORGANIZING">pulling jar files from multiple locations</a>
 * for a particular applet. In particular, it appears that the
 * LiveConnect security model on Mac OS X in the Safari browser
 * prohibits JavaScript from one domain from communicating with Java
 * code (such as an applet) downloaded from another domain. This is
 * correct according to older versions of the LiveConnect
 * specification, although some more recent implementations of
 * LiveConnect allow this, restricting the privileges of such calls in
 * other ways.
 *
 * <p>
 *
 * The workaround for this problem seems to be to host the
 * <code>applet-launcher.jar</code> on your web site if you need to
 * talk to your applet from JavaScript. Your applet's jars will likely
 * also need to be hosted from the same web server. If you talk to
 * extension APIs in your <code>archive</code> tag directly from
 * JavaScript, you may find it necessary to host those jars on your
 * web server as well.
 *
 * <p>
 *
 * From a practical standpoint, most applet developers using
 * JavaScript with the JNLPAppletLauncher will only need to re-host at
 * most <code>applet-launcher.jar</code> on their web site.
 *
 * <h2><a name="EXAMPLES">Examples</a></h2>
 *
 * <p>
 *
 * An applet using JOGL as an extension. Note that this example does
 * not specify a codebase, instead specifying all of its archive tag
 * elements with absolute URLs (split here for readability; in a real
 * applet tag they must be all on one line). Note also the use of the
 * <code>noddraw.check</code> parameter to disable the use of
 * DirectDraw since using JOGL implies the use of OpenGL.
 *
 * <pre>
 * &lt;applet code="org.jdesktop.applet.util.JNLPAppletLauncher"
 *      width=600
 *      height=400
 *      archive="http://download.java.net/media/applet-launcher/applet-launcher.jar,
 *               http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl.jar,
 *               http://download.java.net/media/gluegen/webstart/gluegen-rt.jar,
 *               http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl-demos.jar"&gt;
 *   &lt;param name="codebase_lookup" value="false"&gt;
 *   &lt;param name="subapplet.classname" value="demos.applets.GearsApplet"&gt;
 *   &lt;param name="subapplet.displayname" value="JOGL Gears Applet"&gt;
 *   &lt;param name="noddraw.check" value="true"&gt;
 *   &lt;param name="progressbar" value="true"&gt;
 *   &lt;param name="jnlpNumExtensions" value="1"&gt;
 *   &lt;param name="jnlpExtension1"
 *          value="http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl.jnlp"&gt;
 * &lt;/applet&gt;
 * </pre>
 *
 * <p>
 *
 * An applet using both JOGL and JOAL as extensions. Note again that
 * all code resources are specified with absolute URLs. In this
 * example the unsigned applet pulls in code from both
 * <code>jogl-demos.jar</code> and <code>joal-demos.jar</code>. Note
 * again the use of the <code>noddraw.check</code> parameter.
 *
 * <pre>
 * &lt;applet code="org.jdesktop.applet.util.JNLPAppletLauncher"
 *      width=600
 *      height=400
 *      archive="http://download.java.net/media/applet-launcher/applet-launcher.jar,
 *               http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl.jar,
 *               http://download.java.net/media/gluegen/webstart/gluegen-rt.jar,
 *               http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl-demos.jar,
 *               http://download.java.net/media/joal/webstart/joal.jar,
 *               http://download.java.net/media/joal/webstart/joal-demos.jar"&gt;
 *   &lt;param name="codebase_lookup" value="false"&gt;
 *   &lt;param name="subapplet.classname" VALUE="demos.applets.GearsJOALApplet"&gt;
 *   &lt;param name="subapplet.displayname" VALUE="JOGL / JOAL Gears Applet"&gt;
 *   &lt;param name="noddraw.check" value="true"&gt;
 *   &lt;param name="progressbar" value="true"&gt;
 *   &lt;param name="jnlpNumExtensions" value="2"&gt;
 *   &lt;param name="jnlpExtension1"
 *          value="http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl.jnlp"&gt;
 *   &lt;param name="jnlpExtension2"
 *          value="http://download.java.net/media/joal/webstart/joal.jnlp"&gt;
 * &lt;/applet&gt;
 * </pre>
 *
 * <p>
 * An applet using Java 3D as an extension:
 *
 * <pre>
 * &lt;applet code="org.jdesktop.applet.util.JNLPAppletLauncher"
 *      width=400
 *      height=300
 *      archive="myapplet.jar,
 *               http://download.java.net/media/applet-launcher/applet-launcher.jar,
 *               http://download.java.net/media/java3d/webstart/release/j3d/latest/j3dcore.jar,
 *               http://download.java.net/media/java3d/webstart/release/j3d/latest/j3dutils.jar,
 *               http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl.jar,
 *               http://download.java.net/media/gluegen/webstart/gluegen-rt.jar,
 *               http://download.java.net/media/java3d/webstart/release/vecmath/latest/vecmath.jar"&gt;
 *   &lt;param name="codebase_lookup" value="false"&gt;
 *   &lt;param name="subapplet.classname" value="mypkg.MyApplet"&gt;
 *   &lt;param name="subapplet.displayname" value="My Java 3D Applet"&gt;
 *   &lt;param name="jnlpNumExtensions" value="1"&gt;
 *   &lt;param name="jnlpExtension1" value="http://download.java.net/media/java3d/webstart/release/java3d-latest.jnlp"&gt;
 *   &lt;param name="progressbar" value="true"&gt;
 *   &lt;param name="noddraw.check" value="true"&gt;
 * &lt;/applet&gt;
 * </pre>
 *
 * <p>
 * Note that the JOGL jar files are also included in this example. This is
 * necessary in order to run on Mac OS X, for which Java 3D always uses
 * JOGL to render.
 *
 * <h2> Locations of Standard Extensions </h2>
 *
 * <p>
 *
 * This section describes how to set up the <code>archive</code> and
 * <code>jnlpExtension</code> parameters for a few standard
 * extensions.
 *
 * <h4>JNLPAppletLauncher</h4>
 *
 * <p>
 *
 * The master jar file for the JNLPAppletLauncher is located at the following URL:
 * <pre>
 * http://download.java.net/media/applet-launcher/applet-launcher.jar
 * </pre>
 *
 * <p>
 *
 * This jar needs to be added to your archive parameter.
 *
 * <h4>Java 3D</h4>
 *
 * <p>
 *
 * Java 3D 1.5.1 and later supports the
 * JNLPAppletLauncher. You will need to add the following URLs to your
 * archive parameter:
 *
 * <pre>
 * http://download.java.net/media/java3d/webstart/release/j3d/latest/j3dcore.jar
 * http://download.java.net/media/java3d/webstart/release/j3d/latest/j3dutils.jar
 * http://download.java.net/media/java3d/webstart/release/vecmath/latest/vecmath.jar
 * </pre>
 *
 * Then add the following to one of your <code>jnlpExtension</code> parameters:
 *
 * <pre>
 * http://download.java.net/media/java3d/webstart/release/java3d-latest.jnlp
 * </pre>
 *
 * If you want to deploy your applet on Mac OS X, you will also need to
 * include JOGL by adding the following URLs to your archive parameter:
 *
 * <pre>
 * http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl.jar
 * http://download.java.net/media/gluegen/webstart/gluegen-rt.jar
 * </pre>
 *
 * Note that this will only work if Java 3D is not installed into the JRE as an
 * extension. Since Apple ships their JRE with Java 3D pre-installed, the
 * end-user must uninstall Java 3D in order for Java 3D applets to work on Mac.
 * <p>
 *
 * Note that the Java 3D .jnlp extension will automatically pull in the
 * native code associated with JOGL and the GlueGen runtime, so you don't have
 * to separately refer to those .jnlp files.
 *
 * <h4>JOGL</h4>
 *
 * <p>
 *
 * JOGL 1.1.1-rc3 and later support the JNLPAppletLauncher. You will
 * need to add the following URL to your archive parameter:
 *
 * <pre>
 * http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl.jar
 * </pre>
 *
 * <p>
 *
 * Because JOGL depends on the GlueGen runtime, you will also need to
 * add the following URL to your archive parameter:
 *
 * <pre>
 * http://download.java.net/media/gluegen/webstart/gluegen-rt.jar
 * </pre>
 *
 * <p>
 *
 * Finally, add the following to one of your
 * <code>jnlpExtension</code> parameters:
 *
 * <pre>
 * http://download.java.net/media/jogl/builds/archive/jsr-231-webstart-current/jogl.jnlp
 * </pre>
 *
 * <p>
 *
 * Note that the jogl.jnlp extension will automatically pull in the
 * native code associated with the GlueGen runtime, so you don't have
 * to separately refer to the gluegen-rt.jnlp file.
 *
 * <h4>JOAL</h4>
 *
 * <p>
 *
 * JOAL 1.1.1 and later support the JNLPAppletLauncher. You will need
 * to add the following URL to your archive parameter:
 *
 * <pre>
 * http://download.java.net/media/joal/webstart/joal.jar
 * </pre>
 *
 * <p>
 *
 * Because JOAL, like JOGL, depends on the GlueGen runtime, you will
 * also need to add the following URL to your archive parameter:
 *
 * <pre>
 * http://download.java.net/media/gluegen/webstart/gluegen-rt.jar
 * </pre>
 *
 * <p>
 *
 * (If you are using both JOGL and JOAL, you only need to refer to
 * gluegen-rt.jar once in your archive parameter.)
 *
 * <p>
 *
 * Finally, add the following to one of your
 * <code>jnlpExtension</code> parameters:
 *
 * <pre>
 * http://download.java.net/media/joal/webstart/joal.jnlp
 * </pre>
 *
 * <p>
 *
 * Note that the joal.jnlp extension will automatically pull in the
 * native code associated with the GlueGen runtime, so you don't have
 * to separately refer to the gluegen-rt.jnlp file.
 *
 * <h2><a name="MODIFYING">Modifying Your Extension To Work With The JNLPAppletLauncher</a></h2>
 *
 * <p>
 *
 * If you are the author of an extension like JOGL which requires some
 * native code, with only a simple code change you can make your
 * extension work with the JNLPAppletLauncher. Simply add the
 * following method somewhere in your code:
 *
 * <pre>
 *  private static void loadLibraryInternal(String libraryName) {
 *      String sunAppletLauncher = System.getProperty("sun.jnlp.applet.launcher");
 *      boolean usingJNLPAppletLauncher =
 *          Boolean.valueOf(sunAppletLauncher).booleanValue();
 *
 *      boolean loaded = false;
 *      if (usingJNLPAppletLauncher) {
 *          try {
 *              Class jnlpAppletLauncherClass =
 *                  Class.forName("org.jdesktop.applet.util.JNLPAppletLauncher");
 *              Method jnlpLoadLibraryMethod =
 *                  jnlpAppletLauncherClass.getDeclaredMethod("loadLibrary",
 *                                                            new Class[] { String.class });
 *              jnlpLoadLibraryMethod.invoke(null, new Object[] { libraryName });
 *              loaded = true;
 *          } catch (ClassNotFoundException ex) {
 *              System.err.println("loadLibrary(" + libName + ")");
 *              System.err.println(ex);
 *              System.err.println("Attempting to use System.loadLibrary instead");
 *          } catch (Exception e) {
 *              Throwable t = e;
 *              if (t instanceof InvocationTargetException) {
 *                  t = ((InvocationTargetException) t).getTargetException();
 *              }
 *              if (t instanceof Error)
 *                  throw (Error) t;
 *              if (t instanceof RuntimeException) {
 *                  throw (RuntimeException) t;
 *              }
 *              // Throw UnsatisfiedLinkError for best compatibility with System.loadLibrary()
 *              throw (UnsatisfiedLinkError) new UnsatisfiedLinkError().initCause(e);
 *          }
 *      }
 *
 *      if (!loaded) {
 *          System.loadLibrary(libraryName);
 *      }
 *  }
 * </pre>
 *
 * <p>
 *
 * and wherever you would call <code>System.loadLibrary()</code> (from
 * within an <code>AccessController.doPrivileged()</code> block) to
 * load your extension's native code, call the above
 * <code>loadLibraryInternal</code> method instead.
 *
 * <p>
 *
 * Note again that because the <code>applet-launcher.jar</code> and
 * the nativelib jars for all extensions must currently be signed with
 * the same certificate, this implies that you must resign both the
 * applet launcher as well as any other extensions your applet relies
 * on (unless yours is a Sun-standard extension and can be signed with
 * Sun's code signing certificate).
 *
 * <h2>Acknowledgments</h2>
 *
 * <p>
 *
 * The JNLPAppletLauncher was developed by Kevin Rushforth, Kenneth
 * Russell, and Chien Yang. It is based on the earlier
 * JOGLAppletLauncher developed by Lilian Chamontin.
 */
/*
 * 20-01-2008: Changes in VERBOSE/DEBUG fields.
 * 08-01-2008: Changes in loadLibrary.
 */
public class JNLPAppletLauncher extends Applet {

	/*
	 * 20-01-2008:
	 * private final -> public
	 * true -> false
	 */
    public static boolean VERBOSE = false;
    public static boolean DEBUG = false;

    // Indicated that the applet was successfully initialized
    private boolean isInitOk = false;

    // True the first time start is called, false afterwards
    private boolean firstStart = true;

    // Indicates that the applet was started successfully
    private boolean appletStarted = false;

    // The applet we have to start
    private Applet subApplet;

    // Class name of applet to load (required)
    private String subAppletClassName; // from applet PARAM subapplet.classname

    // String representing the name of the applet (optional)
    private String subAppletDisplayName; // from applet PARAM subapplet.displayname

    // URL to an image that we will display while installing (optional)
    private URL subAppletImageURL; // from applet PARAM subapplet.image

    // Panel that will hold the splash-screen image and progress bar while loading
    private JPanel loaderPanel;

    // Helpers for updating deployment.properties with -Dsun.java2d.noddraw=true
    private static final String JRE_PREFIX = "deployment.javapi.jre.";
    private static final String NODDRAW_PROP = "-Dsun.java2d.noddraw=true";
    private static final String DONT_ASK = ".dont_ask";

    // Optional progress bar
    private JProgressBar progressBar = null;

    /*
     * The following variables are defined per-applet, but we can assert that
     * they will not differ for each applet that is loaded by the same
     * ClassLoader. This means we can just cache the values from the first
     * applet. We will check the values for subsequent applets and throw an
     * exception if there are any differences.
     */

    // Flag indicating that this is the first applet
    private static boolean firstApplet = true;

    // List of extension JNLP files. This is saved for the first applet
    // and verified for each subsequent applet.
    private static List/*<URL>*/ jnlpExtensions = null;

    // Code base and archive tag for all applets that use the same ClassLoader
    private static URL codeBase;
    private static String archive = null;

    // Persistent cache directory for storing native libraries and time stamps.
    // The directory is of the form:
    //
    //     ${user.home}/.jnlp-applet/cache/<HOSTNAME>/<DIGEST-OF-CODEBASE-ARCHIVE>
    //
    private static File cacheDir;

    // Set of jar files specified in the JNLP files.
    // Currently unused.
    private static Set/*<URL>*/ jarFiles;

    // Set of native jar files to be loaded. We need to download these
    // native jars, verify the signatures, verify the security certificates,
    // and extract the native libraries from each jar.
    private static Set/*<URL>*/ nativeJars;

    // Native library prefix (e.g., "lib") and suffix (e.g. ".dll" or ".so")
    private static String nativePrefix;
    private static String nativeSuffix;

    // A HashMap of native libraries that can be loaded with System.load()
    // The key is the string name of the library as passed into the loadLibrary
    // call; it is the file name without the directory or the platform-dependent
    // library prefix and suffix. The value is the absolute path name to the
    // unpacked library file in nativeTmpDir.
    private static Map/*<String, String>*/ nativeLibMap;

    /*
     * The following variables are per-ClassLoader static globals.
     */

    // Flag indicating that we got a fatal error in the static initializer.
    // If this happens we will not attempt to start any applets.
    private static boolean staticInitError = false;

    // Base temp directory used by JNLPAppletLauncher. This is set to:
    //
    // ${java.io.tmpdir}/jnlp-applet
    //
    private static File tmpBaseDir;

    // String representing the name of the temp root directory relative to the
    // tmpBaseDir. Its value is "jlnNNNNN", which is the unique filename created
    // by File.createTempFile() without the ".tmp" extension.
    //
    private static String tmpRootPropValue;

    // Root temp directory for this JVM instance. Used to store the individual,
    // per-ClassLoader directories that will be used to load native code. The
    // directory name is:
    //
    // <tmpBaseDir>/<tmpRootPropValue>
    //
    // Old temp directories are cleaned up the next time a JVM is launched that
    // uses JNLPAppletLauncher.
    //
    private static File tmpRootDir;

    // Temporary directory for loading native libraries for this instance of
    // the class loader. The directory name is:
    //
    // <tmpRootDir>/jlnMMMMM
    //
    // where jlnMMMMM is the unique filename created by File.createTempFile()
    // without the ".tmp" extension.
    //
    private static File nativeTmpDir;

    /*
     * IMPLEMENTATION NOTES
     *
     * Assumptions:
     *
     * A. Multiple applets can be launched from the same class loader, and thus
     *    share the same set of statics and same set of native library symbols.
     *    This can only happen if the codebase and set of jar files as specified
     *    in the archive tag are identical. Applets launched from different code
     *    bases or whose set of jar files are different will always get a
     *    different ClassLoader. If this assumption breaks, too many other
     *    things wouldn't work properly, so we can be assured that it will hold.
     *    However, we cannot assume that the converse is true; it is possible
     *    that two applets with the same codebase and archive tag will be loaded
     *    from a different ClassLoader.
     *
     * B. Given the above, this means that we must store the native libraries,
     *    and keep track of which ones have already been loaded statically, that
     *    is, per-ClassLoader rather than per-Applet. This is a good thing,
     *    because it turns out to be difficult (at best) to find the instance of
     *    the Applet at loadLibrary time.
     *
     * Our solution is as follows:
     *
     *    Use the same criteria for determining the cache dir that JPI
     *    uses to determine the class loader to use. More precisely, we will
     *    create a directory based on the codebase and complete set of jar files
     *    specified by the archive tag. To support the case where each applet is
     *    in a unique class loader, we will copy the native libraries into a
     *    unique-per-ClassLoader temp directory and do the System.load() from
     *    there. For a robust solution, we need to lock the cache directory
     *    during validation, since multiple threads, or even multiple processes,
     *    can access it concurrently.
     *
     * TODO: We need a way to clear the cache.
     *
     * We also considered, but rejected, the following solutions:
     *
     * 1. Use a temporary directory for native jars, download, verify, unpack,
     *    and loadLibrary in this temp dir. No persistent cache.
     *
     * 2. Cache the native libraries in a directory based on the codebase and
     *    the extension jars (i.e., the subset of the jars in the archive tag
     *    that also appear in one of the extension JNLP files). Copy the native
     *    libraries into a unique-per-ClassLoader temp directory and load from
     *    there. Note that this has the potential problem of violating the
     *    assertion that two different applets that share the same ClassLoader
     *    must map to the same cache directory.
     *
     * 3. Use the exact criteria for determining the cache dir that JPI
     *    uses to determine which class loader to use (as in our proposed
     *    solution above), unpack the native jars into the cache directory and
     *    load from there. This obviates the need for locking, but it will break
     *    if the JPI ever isolates each applet into its own ClassLoader.
     */

    /**
     * Constructs an instance of the JNLPAppletLauncher class. This is called by
     * Java Plug-in, and should not be called directly by an application or
     * applet.
     */
    public JNLPAppletLauncher() {
    }

    /* @Override */
    public void init() {
        if (VERBOSE) {
            System.err.println();
            System.err.println("===========================================================================");
        }
        if (DEBUG) {
            System.err.println("Applet.init");
        }

        if (staticInitError) {
            return;
        }

        subAppletClassName = getParameter("subapplet.classname");
        if (subAppletClassName == null) {
            displayError("Init failed : Missing subapplet.classname parameter");
            return;
        }

        subAppletDisplayName = getParameter("subapplet.displayname");
        if (subAppletDisplayName == null) {
            subAppletDisplayName = "Applet";
        }

        subAppletImageURL = null;
        try {
            String subAppletImageStr = getParameter("subapplet.image");
            if (subAppletImageStr != null && subAppletImageStr.length() > 0) {
                subAppletImageURL = new URL(subAppletImageStr);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            // Continue with a null subAppletImageURL
        }

        if (DEBUG) {
            System.err.println("subapplet.classname = " + subAppletClassName);
            System.err.println("subapplet.displayname = " + subAppletDisplayName);
            if (subAppletImageURL != null) {
                System.err.println("subapplet.image = " + subAppletImageURL.toExternalForm());
            }
        }

        initLoaderLayout();

        isInitOk = true;
    }

    /* @Override */
    public void start() {
        if (DEBUG) {
            System.err.println("Applet.start");
        }

        if (isInitOk) {
            if (firstStart) { // first time
                firstStart = false;

                Thread startupThread = new Thread() {
                    public void run() {
                        initAndStartApplet();
                    }
                };
                startupThread.setName("AppletLauncher-Startup");
                startupThread.setPriority(Thread.NORM_PRIORITY - 1);
                startupThread.start();
            } else if (appletStarted) {
                checkNoDDrawAndUpdateDeploymentProperties();

                // We have to start again the applet (start can be called multiple times,
                // e.g once per tabbed browsing
                subApplet.start();
            }
        }

    }

    /* @Override */
    public void stop(){
        if (subApplet != null) {
            subApplet.stop();
        }
    }

    /* @Override */
    public void destroy(){
        if (subApplet != null) {
            subApplet.destroy();
        }
    }

    /** Helper method to make it easier to call methods on the
        sub-applet from JavaScript. */
    public Applet getSubApplet() {
        return subApplet;
    }

    /**
     * This method is called by the static initializer to create / initialize
     * the temp root directory that will hold the temp directories for this
     * instance of the JVM. This is done as follows:
     *
     *     1. Synchronize on a global lock. Note that for this purpose we will
     *        use System.out in the absence of a true global lock facility.
     *        We are careful not to hold this lock too long.
     *
     *     2. Check for the existence of the "jnlp.applet.launcher.tmproot"
     *        system property.
     *
     *         a. If set, then some other thread in a different ClassLoader has
     *            already created the tmprootdir, so we just need to
     *            use it. The remaining steps are skipped.
     *
     *         b. If not set, then we are the first thread in this JVM to run,
     *            and we need to create the the tmprootdir.
     *
     *     3. Create the tmprootdir, along with the appropriate locks.
     *        Note that we perform the operations in the following order,
     *        prior to creating tmprootdir itself, to work around the fact that
     *        the file creation and file lock steps are not atomic, and we need
     *        to ensure that a newly-created tmprootdir isn't reaped by a
     *        concurrently running JVM.
     *
     *            create jlnNNNN.tmp using File.createTempFile()
     *            lock jlnNNNN.tmp
     *            create jlnNNNN.lck while holding the lock on the .tmp file
     *            lock jlnNNNN.lck
     *
     *        Since the Reaper thread will enumerate the list of *.lck files
     *        before starting, we can guarantee that if there exists a *.lck file
     *        for an active process, then the corresponding *.tmp file is locked
     *        by that active process. This guarantee lets us avoid reaping an
     *        active process' files.
     *
     *     4. Set the "jnlp.applet.launcher.tmproot" system property.
     *
     *     5. Add a shutdown hook to cleanup jlnNNNN.lck and jlnNNNN.tmp. We
     *        don't actually expect that this shutdown hook will ever be called,
     *        but the act of doing this, ensures that the locks never get
     *        garbage-collected, which is necessary for correct behavior when
     *        the first ClassLoader is later unloaded, while subsequent Applets
     *        are still running.
     *
     *     6. Start the Reaper thread to cleanup old installations.
     */
    private static void initTmpRoot() throws IOException {
        if (VERBOSE) {
            System.err.println("---------------------------------------------------");
        }

        synchronized (System.out) {

            // Get the name of the tmpbase directory.
            String tmpBaseName = System.getProperty("java.io.tmpdir") +
                    File.separator + "jnlp-applet";
            tmpBaseDir = new File(tmpBaseName);

            // Get the value of the tmproot system property
            final String tmpRootPropName = "jnlp.applet.launcher.tmproot";

            tmpRootPropValue = System.getProperty(tmpRootPropName);

            if (tmpRootPropValue == null) {
                // Create the tmpbase directory if it doesn't already exist
                tmpBaseDir.mkdir();
                if (!tmpBaseDir.isDirectory()) {
                    throw new IOException("Cannot create directory " + tmpBaseDir);
                }

                // Create ${tmpbase}/jlnNNNN.tmp then lock the file
                File tmpFile = File.createTempFile("jln", ".tmp", tmpBaseDir);
                if (VERBOSE) {
                    System.err.println("tmpFile = " + tmpFile.getAbsolutePath());
                }
                final FileOutputStream tmpOut = new FileOutputStream(tmpFile);
                final FileChannel tmpChannel = tmpOut.getChannel();
                final FileLock tmpLock = tmpChannel.lock();

                // Strip off the ".tmp" to get the name of the tmprootdir
                String tmpFileName = tmpFile.getAbsolutePath();
                String tmpRootName = tmpFileName.substring(0, tmpFileName.lastIndexOf(".tmp"));

                // create ${tmpbase}/jlnNNNN.lck then lock the file
                String lckFileName = tmpRootName + ".lck";
                File lckFile = new File(lckFileName);
                if (VERBOSE) {
                    System.err.println("lckFile = " + lckFile.getAbsolutePath());
                }
                lckFile.createNewFile();
                final FileOutputStream lckOut = new FileOutputStream(lckFile);
                final FileChannel lckChannel = lckOut.getChannel();
                final FileLock lckLock = lckChannel.lock();

                // Create tmprootdir
                tmpRootDir = new File(tmpRootName);
                if (DEBUG) {
                    System.err.println("tmpRootDir = " + tmpRootDir.getAbsolutePath());
                }
                if (!tmpRootDir.mkdir()) {
                    throw new IOException("Cannot create " + tmpRootDir);
                }

                // Add shutdown hook to cleanup the OutputStream, FileChannel,
                // and FileLock for the jlnNNNN.lck and jlnNNNN.lck files.
                // We do this so that the locks never get garbage-collected.
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    /* @Override */
                    public void run() {
                        // NOTE: we don't really expect that this code will ever
                        // be called. If it does, we will close the output
                        // stream, which will in turn close the channel.
                        // We will then release the lock.
                        try {
                            tmpOut.close();
                            tmpLock.release();
                            lckOut.close();
                            lckLock.release();
                        } catch (IOException ex) {
                            // Do nothing
                        }
                    }
                });

                // Set the system property...
                tmpRootPropValue = tmpRootName.substring(tmpRootName.lastIndexOf(File.separator) + 1);
                System.setProperty(tmpRootPropName, tmpRootPropValue);
                if (VERBOSE) {
                    System.err.println("Setting " + tmpRootPropName + "=" + tmpRootPropValue);
                }

                // Start a new Reaper thread to do stuff...
                Thread reaperThread = new Thread() {
                    /* @Override */
                    public void run() {
                        deleteOldTempDirs();
                    }
                };
                reaperThread.setName("AppletLauncher-Reaper");
                reaperThread.start();
            } else {
                // Make sure that the property is not set to an illegal value
                if (tmpRootPropValue.indexOf('/') >= 0 ||
                        tmpRootPropValue.indexOf(File.separatorChar) >= 0) {
                    throw new IOException("Illegal value of: " + tmpRootPropName);
                }

                // Set tmpRootDir = ${tmpbase}/${jnlp.applet.launcher.tmproot}
                if (VERBOSE) {
                    System.err.println("Using existing value of: " +
                            tmpRootPropName + "=" + tmpRootPropValue);
                }
                tmpRootDir = new File(tmpBaseDir, tmpRootPropValue);
                if (DEBUG) {
                    System.err.println("tmpRootDir = " + tmpRootDir.getAbsolutePath());
                }
                if (!tmpRootDir.isDirectory()) {
                    throw new IOException("Cannot access " + tmpRootDir);
                }
            }
        }
    }

    /**
     * Called by the Reaper thread to delete old temp directories
     * Only one of these threads will run per JVM invocation.
     */
    private static void deleteOldTempDirs() {
        if (VERBOSE) {
            System.err.println("*** Reaper: deleteOldTempDirs in " +
                    tmpBaseDir.getAbsolutePath());
        }

        // enumerate list of jnl*.lck files, ignore our own jlnNNNN file
        final String ourLockFile = tmpRootPropValue + ".lck";
        FilenameFilter lckFilter = new FilenameFilter() {
            /* @Override */
            public boolean accept(File dir, String name) {
                return name.endsWith(".lck") && !name.equals(ourLockFile);
            }
        };

        // For each file <file>.lck in the list we will first try to lock
        // <file>.tmp if that succeeds then we will try to lock <file>.lck
        // (which should always succeed unless there is a problem). If we can
        // get the lock on both files, then it must be an old installation, and
        // we will delete it.
        String[] fileNames = tmpBaseDir.list(lckFilter);
        if (fileNames != null) {
            for (int i = 0; i < fileNames.length; i++) {
                String lckFileName = fileNames[i];
                String tmpDirName = lckFileName.substring(0, lckFileName.lastIndexOf(".lck"));
                String tmpFileName = tmpDirName + ".tmp";

                File lckFile = new File(tmpBaseDir, lckFileName);
                File tmpFile = new File(tmpBaseDir, tmpFileName);
                File tmpDir = new File(tmpBaseDir, tmpDirName);

                if (lckFile.exists() && tmpFile.exists() && tmpDir.isDirectory()) {
                    FileOutputStream tmpOut = null;
                    FileChannel tmpChannel = null;
                    FileLock tmpLock = null;

                    try {
                        tmpOut = new FileOutputStream(tmpFile);
                        tmpChannel = tmpOut.getChannel();
                        tmpLock = tmpChannel.tryLock();
                    } catch (Exception ex) {
                        // Ignore exceptions
                        if (DEBUG) {
                            ex.printStackTrace();
                        }
                    }

                    if (tmpLock != null) {
                        FileOutputStream lckOut = null;
                        FileChannel lckChannel = null;
                        FileLock lckLock = null;

                        try {
                            lckOut = new FileOutputStream(lckFile);
                            lckChannel = lckOut.getChannel();
                            lckLock = lckChannel.tryLock();
                        } catch (Exception ex) {
                            if (DEBUG) {
                                ex.printStackTrace();
                            }
                        }

                        if (lckLock != null) {
                            // Recursively remove the old tmpDir and all of
                            // its contents
                            removeAll(tmpDir);

                            // Close the streams and delete the .lck and .tmp
                            // files. Note that there is a slight race condition
                            // in that another process could open a stream at
                            // the same time we are trying to delete it, which will
                            // prevent deletion, but we won't worry about it, since
                            // the worst that will happen is we might have an
                            // occasional 0-byte .lck or .tmp file left around
                            try {
                                lckOut.close();
                            } catch (IOException ex) {
                            }
                            lckFile.delete();
                            try {
                                tmpOut.close();
                            } catch (IOException ex) {
                            }
                            tmpFile.delete();
                        } else {
                            try {
                                // Close the file and channel for the *.lck file
                                if (lckOut != null) {
                                    lckOut.close();
                                }
                                // Close the file/channel and release the lock
                                // on the *.tmp file
                                tmpOut.close();
                                tmpLock.release();
                            } catch (IOException ex) {
                                if (DEBUG) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                } else {
                    if (VERBOSE) {
                        System.err.println("    Skipping: " + tmpDir.getAbsolutePath());
                    }
                }
            }
        }
    }

    /**
     * Remove the specified file or directory. If "path" is a directory, then
     * recursively remove all entries, then remove the directory itself.
     */
    private static void removeAll(File path) {
        if (VERBOSE) {
            System.err.println("removeAll(" + path + ")");
        }

        if (path.isDirectory()) {
            // Recursively remove all files/directories in this directory
            File[] list = path.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    removeAll(list[i]);
                }
            }
        }

        path.delete();
    }


    /**
     * This method is executed from outside the Event Dispatch Thread. It
     * initializes, downloads, and unpacks the required native libraries into
     * the cache, and then starts the applet on the EDT.
     */
    private void initAndStartApplet() {
        // Parse the extension JNLP files and download the native resources
        try {
            initResources();
        } catch (Exception ex) {
            ex.printStackTrace();
            displayError(toErrorString(ex));
            return;
        }

        // Indicate that we are starting the applet
        displayMessage("Starting applet " + subAppletDisplayName);
        setProgress(0);

        // Now schedule the starting of the subApplet on the EDT
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // start the subapplet
                startSubApplet();
            }
        });

    }

    /**
     * Initializes, downloads, and extracts the native resources needed by this
     * applet.
     */
    private void initResources() throws IOException {
        synchronized (JNLPAppletLauncher.class) {
            if (firstApplet) {
                // Save codeBase and archive parameter for assertion checking
                codeBase = getCodeBase();
//                assert(codeBase != null);
                archive = getParameter("archive");
                if (archive == null || archive.length() == 0) {
                    throw new IllegalArgumentException("Missing archive parameter");
                }

                // Initialize the collections of resources
                jarFiles = new HashSet/*<URL>*/();
                nativeJars = new HashSet/*<URL>*/();
                nativeLibMap = new HashMap/*<String, String>*/();
            } else {
                // The following must hold for applets in the same ClassLoader
//                assert(getCodeBase().equals(codeBase));
//                assert(getParameter("archive").equals(archive));
            }

            int jnlpNumExt = -1;
            String numParamString = getParameter("jnlpNumExtensions");
            if (numParamString != null) {
                try {
                    jnlpNumExt = Integer.parseInt(numParamString);
                } catch (NumberFormatException ex) {
                }

                if (jnlpNumExt <= 0) {
                    throw new IllegalArgumentException("Missing or invalid jnlpNumExtensions parameter");
                }
            }

            List/*<URL>*/ urls = new ArrayList/*<URL>*/();
            for (int i = 1; i <= jnlpNumExt; i++) {
                String paramName = "jnlpExtension" + i;
                String urlString = getParameter(paramName);
                if (urlString == null || urlString.length() == 0) {
                    throw new IllegalArgumentException("Missing " + paramName + " parameter");
                }
                URL url = new URL(urlString);
                urls.add(url);
            }

            // If this is the first time, process the list of extensions and
            // save the results. Otherwise, verify that the list of extensions
            // is the same as the first applet.
            if (firstApplet) {
                jnlpExtensions = urls;
                parseJNLPExtensions(urls);

                if (VERBOSE) {
                    System.err.println();
                    System.err.println("All files successfully parsed");
                    printResources();
                }

                if (nativeJars.size() > 0) {
                    // Create the cache directory if not already created.
                    // Create the temporary directory that will hold a copy of
                    // the extracted native libraries, then copy each native
                    // library into the temp dir so we can call System.load().
                    createCacheDir();
                    createTmpDir();

                    // Download and validate the set of native jars, if the
                    // cache is out of date. Then extract the native DLLs,
                    // creating a list of native libraries to be loaded.
                    for (Iterator iter = nativeJars.iterator(); iter.hasNext(); ) {
                        URL url = (URL) iter.next();
                        processNativeJar(url);
                    }
                }

                // Set a system property that libraries can use to know when to call
                // JNLPAppletLauncher.loadLibrary instead of System.loadLibrary
                System.setProperty("sun.jnlp.applet.launcher", "true");

            } else {
                // Verify that the list of jnlpExtensions is the same as the
                // first applet
                if (!jnlpExtensions.equals(urls)) {
                    throw new IllegalArgumentException(
                            "jnlpExtension parameters do not match previously loaded applet");
                }
            }

            firstApplet = false;
        }
    }

    /**
     * Detemine the cache directory location based on the codebase and archive
     * tag. Create the cache directory if not already created.
     */
    private void createCacheDir() throws IOException {
        StringBuffer cacheBaseName = new StringBuffer();
        cacheBaseName.append(System.getProperty("user.home")).append(File.separator).
                append(".jnlp-applet").append(File.separator).
                append("cache");
        File cacheBaseDir = new File(cacheBaseName.toString());
        if (VERBOSE) {
            System.err.println("cacheBaseDir = " + cacheBaseDir.getAbsolutePath());
        }

        cacheDir = new File(cacheBaseDir, getCacheDirName());
        if (VERBOSE) {
            System.err.println("cacheDir = " + cacheDir.getAbsolutePath());
        }

        // Create cache directory and load native library
        if (!cacheDir.isDirectory()) {
            if (!cacheDir.mkdirs()) {
                throw new IOException("Cannot create directory " + cacheDir);
            }
        }

//        assert(cacheBaseDir.isDirectory());
    }

    /**
     * Returns a directory name of the form: hostname/hash(codebase,archive)
     */
    private String getCacheDirName() {
        final String codeBasePath = getCodeBase().toExternalForm();

        // Extract the host name; replace characters in the set ".:\[]" with "_"
        int hostIdx1 = -1;
        int hostIdx2 = -1;
        String hostNameDir = "UNKNOWN";
        hostIdx1 = codeBasePath.indexOf("://");
        if (hostIdx1 >= 0) {
            hostIdx1 += 3; // skip the "://"
            // Verify that the character immediately following the "://"
            // exists and is not a "/"
            if (hostIdx1 < codeBasePath.length() &&
                    codeBasePath.charAt(hostIdx1) != '/') {
                hostIdx2 = codeBasePath.indexOf('/', hostIdx1);
                if (hostIdx2 > hostIdx1) {
                    hostNameDir = codeBasePath.substring(hostIdx1, hostIdx2).
                            replace('.', '_').
                            replace(':', '_').
                            replace('\\', '_').
                            replace('[', '_').
                            replace(']', '_');
                }
            }
        }

        // Now concatenate the codebase and the list of jar files in the archive
        // Separate them by an "out-of-band" character which cannot appear in
        // either the codeBasePath or archive list.
        StringBuffer key = new StringBuffer();
        key.append(codeBasePath).
                append("\n").
                append(getParameter("archive"));
        if (VERBOSE) {
            System.err.println("key = " + key);
        }

        StringBuffer result = new StringBuffer();
        result.append(hostNameDir).
                append(File.separator).
                append(sha1Hash(key.toString()));
        if (VERBOSE) {
            System.err.println("result = " + result);
        }

        return result.toString();
    }

    /**
     * Produces a 40-byte SHA-1 hash of the input string.
     */
    private static String sha1Hash(String str) {
        MessageDigest sha1 = null;
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        byte[] digest = sha1.digest(str.getBytes());
        if (digest == null || digest.length == 0) {
            throw new RuntimeException("Error reading message digest");
        }
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < digest.length; i++) {
            int val = (int)digest[i] & 0xFF;
            if (val < 0x10) {
                res.append("0");
            }
            res.append(Integer.toHexString(val));
        }
        return res.toString();
    }

    /**
     * Create the temp directory in tmpRootDir. To do this, we create a temp
     * file with a ".tmp" extension, and then create a directory of the
     * same name but without the ".tmp". The temp file, directory, and all
     * files in the directory will be reaped the next time this is started.
     * We avoid deleteOnExit, because it doesn't work reliably.
     */
    private void createTmpDir() throws IOException {

        if (VERBOSE) {
            System.err.println("---------------------------------------------------");
        }

        File tmpFile = File.createTempFile("jln", ".tmp", tmpRootDir);
        String tmpFileName = tmpFile.getAbsolutePath();
        String tmpDirName = tmpFileName.substring(0, tmpFileName.lastIndexOf(".tmp"));
        nativeTmpDir = new File(tmpDirName);
        if (VERBOSE) {
            System.err.println("tmpFile = " + tmpFile.getAbsolutePath() +
                    "  tmpDir = " + nativeTmpDir.getAbsolutePath());
        }
        if (!nativeTmpDir.mkdir()) {
            throw new IOException("Cannot create " + nativeTmpDir);
        }
    }

    /**
     * Download, cache, verify, and unpack the specified native jar file.
     * Before downloading, check the cached time stamp for the jar file
     * against the server. If the time stamp is valid and matches that of the
     * server, then we will use the locally cached files. This method assumes
     * that cacheDir and nativeTmpDir both exist.
     *
     * An IOException is thrown if the files cannot loaded for some reason.
     */
    private void processNativeJar(URL url) throws IOException {
//        assert(cacheDir.isDirectory());
//        assert(nativeTmpDir.isDirectory());

        // 6618105: Map '\' to '/' prior to stripping off the path
        String urlString = url.toExternalForm().replace('\\', '/');
        String nativeFileName = urlString.substring(urlString.lastIndexOf("/") + 1);
        File nativeFile = new File(cacheDir, nativeFileName);
        // Make sure the file is not "." or ".."
        if (nativeFile.isDirectory()) {
            throw new IOException(nativeFile + " is a directory");
        }

        String tmpStr = nativeFileName;
        int idx = nativeFileName.lastIndexOf(".");
        if (idx > 0) {
            tmpStr = nativeFileName.substring(0, idx);
        }
        String indexFileName = tmpStr + ".idx";
        File indexFile = new File(cacheDir, indexFileName);

        if (VERBOSE) {
            System.err.println("nativeFile = " + nativeFile);
            System.err.println("indexFile = " + indexFile);
        }

        displayMessage("Loading: " + nativeFileName);
        setProgress(0);

        URLConnection conn = url.openConnection();
        conn.connect();

        Map/*<String,List<String>>*/ headerFields = conn.getHeaderFields();
        if (VERBOSE) {
            for (Iterator iter = headerFields.entrySet().iterator(); iter.hasNext(); ) {
                Entry/*<String,List<String>>*/ e = (Entry) iter.next();
                for (Iterator iter2 = ((List/*<String>*/) e.getValue()).iterator(); iter2.hasNext(); ) {
                    String s = (String) iter2.next();
                    if (e.getKey() != null) {
                        System.err.print(e.getKey() + ": ");
                    }
                    System.err.print(s + " ");
                }
                System.err.println();
            }
            System.err.println();
        }

        // Validate the cache, download the jar if needed
        // TODO: rather than synchronizing on System.out during cache validation,
        // we should use a System property as a lock token (protected by
        // System.out) so we don't hold a synchronized lock on System.out during
        // a potentially long download operation.
        synchronized (System.out) {
            validateCache(conn, nativeFile, indexFile);
        }

        // Unpack the jar file
        displayMessage("Unpacking: " + nativeFileName);
        setProgress(0);

        // Enumerate the jar file looking for native libraries
        JarFile jarFile = new JarFile(nativeFile);
        Set/*<String>*/ nativeLibNames = getNativeLibNames(jarFile);

        // Validate certificates; throws exception upon validation error
        validateCertificates(jarFile, nativeLibNames);

        // Extract native libraries from the jar file
        extractNativeLibs(jarFile, nativeLibNames);

        if (VERBOSE) {
            System.err.println();
        }
    }

    // Validate the cached file. If the cached file is out of date or otherwise
    // invalid, download the file and store the new time stamp.
    // This method must be called with a global lock being held such that
    // no other thread -- even in another class loader -- can executed this
    // method concurrently.
    private void validateCache(URLConnection conn,
            File nativeFile,
            File indexFile) throws IOException {

        // Lock the cache directory
        final String lckFileName = "cache.lck";
        File lckFile = new File(cacheDir, lckFileName);
        lckFile.createNewFile();
        final FileOutputStream lckOut = new FileOutputStream(lckFile);
        final FileChannel lckChannel = lckOut.getChannel();
        final FileLock lckLock = lckChannel.lock();

        try {
            // Check to see whether the cached jar file exists and is valid
            boolean valid = false;
            long cachedTimeStamp = readTimeStamp(indexFile);
            long urlTimeStamp = conn.getLastModified();

            if (nativeFile.exists() &&
                    urlTimeStamp > 0 &&
                    urlTimeStamp == readTimeStamp(indexFile)) {

                valid = true;
            }

            // Validate the cache, download the jar if needed
            if (!valid) {
                if (VERBOSE) {
                    System.err.println("processNativeJar: downloading " + nativeFile.getAbsolutePath());
                }
                indexFile.delete();
                nativeFile.delete();

                // Copy from URL to File
                int len = conn.getContentLength();
                if (VERBOSE) {
                    System.err.println("Content length = " + len + " bytes");
                }

                int totalNumBytes = copyURLToFile(conn, nativeFile);
                if (DEBUG) {
                    System.err.println("processNativeJar: " + conn.getURL().toString() +
                            " --> " + nativeFile.getAbsolutePath() + " : " +
                            totalNumBytes + " bytes written");
                }

                // Write timestamp to index file.
                writeTimeStamp(indexFile, urlTimeStamp);

            } else {
                if (DEBUG) {
                    System.err.println("processNativeJar: using previously cached: " +
                            nativeFile.getAbsolutePath());
                }
            }
        } finally {
            // Unlock the cache directory
            lckLock.release();
        }
    }

    private long readTimeStamp(File indexFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(indexFile));
            try {
                String str = reader.readLine();
                return Long.parseLong(str);
            } finally {
                reader.close();
            }
        } catch (Exception ex) {
        }
        return -1;
    }

    private void writeTimeStamp(File indexFile, long timestamp) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(indexFile));
            try {
                writer.write("" + timestamp + "\n");
                writer.flush();
            } finally {
                writer.close();
            }
        } catch (Exception ex) {
            displayError("Error writing time stamp for native libraries");
        }
    }

    // Copy the specified URL to the specified File
    private int copyURLToFile(URLConnection inConnection,
            File outFile) throws IOException {

        int totalNumBytes = 0;
        InputStream in = new BufferedInputStream(inConnection.getInputStream());
        try {
            OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile));
            try {
                totalNumBytes = copyStream(in, out, inConnection.getContentLength());
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }

        return totalNumBytes;
    }

    /**
     * Copy the specified input stream to the specified output stream. The total
     * number of bytes written is returned. If the close flag is set, both
     * streams are closed upon completeion.
     */
    private int copyStream(InputStream in, OutputStream out,
            int totalNumBytes) throws IOException {

        int numBytes = 0;

        final int BUFFER_SIZE = 1000;
        final float pctScale = 100.0f / (float)totalNumBytes;
        byte[] buf = new byte[BUFFER_SIZE];
        setProgress(0);
        while (true) {
            int count;
            if ((count = in.read(buf)) == -1) {
                break;
            }
            out.write(buf, 0, count);
            numBytes += count;
            if (totalNumBytes > 0) {
                setProgress((int)Math.round((float)numBytes * pctScale));
            }
        }
        setProgress(100);

        return numBytes;
    }

    /**
     * Enumerate the list of entries in the jar file and return those that are
     * native library names.
     */
    private Set/*<String>*/ getNativeLibNames(JarFile jarFile) {
        if (VERBOSE) {
            System.err.println("getNativeLibNames:");
        }

        Set/*<String>*/ names = new HashSet/*<String>*/();
        Enumeration/*<JarEntry>*/ entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            String nativeLibName = entry.getName();

            if (VERBOSE) {
                System.err.println("JarEntry : " + nativeLibName);
            }

            // only look at entries with no "/"
            if (nativeLibName.indexOf('/') == -1 &&
                    nativeLibName.indexOf(File.separatorChar) == -1) {

                String lowerCaseName = nativeLibName.toLowerCase();

                // Match entries with correct prefix and suffix (ignoring case)
                if (lowerCaseName.startsWith(nativePrefix) &&
                        nativeLibName.toLowerCase().endsWith(nativeSuffix)) {

                    names.add(nativeLibName);
                }
            }
        }

        return names;
    }

    /**
     * Validate the certificates for each native Lib in the jar file.
     * Throws an IOException if any certificate is not valid.
     */
    private void validateCertificates(JarFile jarFile,
            Set/*<String>*/ nativeLibNames) throws IOException {

        if (DEBUG) {
            System.err.println("validateCertificates:");
        }

        byte[] buf = new byte[1000];
        Enumeration/*<JarEntry>*/ entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            String entryName = entry.getName();

            if (VERBOSE) {
                System.err.println("JarEntry : " + entryName);
            }

            if (nativeLibNames.contains(entryName)) {

                if (DEBUG) {
                    System.err.println("VALIDATE: " + entryName);
                }

                if (!checkNativeCertificates(jarFile, entry, buf)) {
                    throw new IOException("Cannot validate certificate for " + entryName);
                }
            }
        }

    }

    /**
     * Check the native certificates with the ones in the jar file containing the
     * certificates for the JNLPAppletLauncher class (all must match).
     */
    private boolean checkNativeCertificates(JarFile jar, JarEntry entry,
            byte[] buf) throws IOException {

        // API states that we must read all of the data from the entry's
        // InputStream in order to be able to get its certificates

        InputStream is = jar.getInputStream(entry);
        int totalLength = (int) entry.getSize();
        int len;
        while ((len = is.read(buf)) > 0) {
        }
        is.close();

        // locate JNLPAppletLauncher certificates
        Certificate[] appletLauncherCerts = JNLPAppletLauncher.class.getProtectionDomain().
                getCodeSource().getCertificates();
        if (appletLauncherCerts == null || appletLauncherCerts.length == 0) {
            throw new IOException("Cannot find certificates for JNLPAppletLauncher class");
        }

        // Get the certificates for the JAR entry
        Certificate[] nativeCerts = entry.getCertificates();
        if (nativeCerts == null || nativeCerts.length == 0) {
            return false;
        }

        int checked = 0;
        for (int i = 0; i < appletLauncherCerts.length; i++) {
            for (int j = 0; j < nativeCerts.length; j++) {
                if (nativeCerts[j].equals(appletLauncherCerts[i])){
                    checked++;
                    break;
                }
            }
        }
        return  (checked == appletLauncherCerts.length);
    }

    /**
     * Extract the specified set of native libraries in the given jar file.
     */
    private void extractNativeLibs(JarFile jarFile,
            Set/*<String>*/ nativeLibNames) throws IOException {

        if (DEBUG) {
            System.err.println("extractNativeLibs:");
        }

        Enumeration/*<JarEntry>*/ entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = (JarEntry) entries.nextElement();
            String entryName = entry.getName();

            if (VERBOSE) {
                System.err.println("JarEntry : " + entryName);
            }

            if (nativeLibNames.contains(entryName)) {
                // strip prefix & suffix
                String libName = entryName.substring(nativePrefix.length(),
                        entryName.length() - nativeSuffix.length());
                if (DEBUG) {
                    System.err.println("EXTRACT: " + entryName + "(" + libName + ")");
                }

                File nativeLib = new File(nativeTmpDir, entryName);
                InputStream in = new BufferedInputStream(jarFile.getInputStream(entry));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(nativeLib));
                int numBytesWritten = copyStream(in, out, -1);
                in.close();
                out.close();
                nativeLibMap.put(libName, nativeLib.getAbsolutePath());
            }
        }
    }

    /**
     * The true start of the sub applet (invoked in the EDT)
     */
    private void startSubApplet() {
        try {
            subApplet = (Applet)Class.forName(subAppletClassName).newInstance();
            subApplet.setStub(new AppletStubProxy());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            displayError("Class not found: " + subAppletClassName);
            return;
        } catch (Exception ex) {
            ex.printStackTrace();
            displayError("Unable to start " + subAppletDisplayName);
            return;
        }

        add(subApplet, BorderLayout.CENTER);

        try {
            subApplet.init();
            remove(loaderPanel);
            validate();
            checkNoDDrawAndUpdateDeploymentProperties();
            subApplet.start();
            appletStarted = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //08-01-2007: Interface for loading the library
    public interface LibraryLoader
    {
    	public void loadLibrary(String fullLibraryName) throws UnsatisfiedLinkError;
    }
    
    /**
     * Method called by an extension such as JOGL or Java 3D to load the
     * specified library. Applications and applets should not call this method.
     *
     * @param libraryName name of the library to be loaded
     *
     * @throws SecurityException if the caller does not have permission to
     * call System.load
     */
    public static void loadLibrary(String libraryName, LibraryLoader loader) {
        if (VERBOSE) {
            System.err.println("-----------");
            Thread.dumpStack();
        }

        if (DEBUG) {
            System.err.println("JNLPAppletLauncher.loadLibrary(\"" + libraryName + "\")");
        }

        String fullLibraryName = (String) nativeLibMap.get(libraryName);
        if (fullLibraryName == null) {
            // Throw UnsatisfiedLinkError to try to match behavior of System.loadLibrary()
            throw new UnsatisfiedLinkError(libraryName);
        }

        if (DEBUG) {
            System.err.println("    loading: " + fullLibraryName + "");
        }

        //08-01-2007: Added 'System.load(fullLibraryName);' changed by the next code
        //for compatibility with LibLoader useb by NativeFmod/NativeFmodEx/NativeBass
        
        loader.loadLibrary(fullLibraryName);
    }

    private static String toErrorString(Throwable throwable) {
        StringBuffer errStr = new StringBuffer(throwable.toString());
        Throwable cause = throwable.getCause();
        while (cause != null) {
            errStr.append(": ").append(cause);
            cause = cause.getCause();
        }
        return errStr.toString();
    }

    private void displayMessage(final String message) {
        if (progressBar != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setString(message);
                }
            });
        }
    }

    private void displayError(final String errorMessage) {
        // Log message on Java console and display in applet progress bar
        Logger.getLogger("global").severe(errorMessage);
        if (progressBar != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setString("Error : " + errorMessage);
                }
            });
        }
    }

    private void setProgress(final int value) {
        if (progressBar != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    progressBar.setValue(value);
                }
            });
        }
    }

    private void initLoaderLayout() {
        setLayout(new BorderLayout());
        loaderPanel = new JPanel(new BorderLayout());
        if (getBooleanParameter("progressbar")) {
            progressBar = new JProgressBar(0, 100);
            progressBar.setBorderPainted(true);
            progressBar.setStringPainted(true);
            progressBar.setString("Loading...");
        }
        boolean includeImage = false;
        ImageIcon image = null;
        if (subAppletImageURL != null) {
            image = new ImageIcon(subAppletImageURL);
            includeImage = true;
        }
        add(loaderPanel, BorderLayout.SOUTH);
        if (includeImage) {
            loaderPanel.add(new JLabel(image), BorderLayout.CENTER);
            if (progressBar != null) {
                loaderPanel.add(progressBar, BorderLayout.SOUTH);
            }
        } else {
            if (progressBar != null) {
                loaderPanel.add(progressBar, BorderLayout.CENTER);
            }
        }
    }

    private void parseJNLPExtensions(List/*<URL>*/ urls) throws IOException {
        for (Iterator iter = urls.iterator(); iter.hasNext(); ) {
            URL url = (URL) iter.next();
            JNLPParser parser = new JNLPParser(this, url);
            parser.parse();
        }
    }

    private void addJarFile(URL jarFile) {
        jarFiles.add(jarFile);
    }

    private void addNativeJar(URL nativeJar) {
        nativeJars.add(nativeJar);
    }

    /*
     * Debug method to print out resources from the JNLP file
     */
    private static void printResources() {
        System.err.println("  Resources:");
        System.err.println("    Class Jars:");
        doPrint(jarFiles);
        System.err.println();
        System.err.println("    Native Jars:");
        doPrint(nativeJars);
    }

    /*
     * Debug method to print out resources from the JNLP file
     */
    private static void doPrint(Collection/*<URL>*/ urls) {
        for (Iterator iter = urls.iterator(); iter.hasNext(); ) {
            URL url = (URL) iter.next();
            String urlString = url.toExternalForm();
            System.err.println("      " + urlString);
        }
    }

    // Static initializer for JNLPAppletLauncher
    static {
        System.err.println("JNLPAppletLauncher: static initializer");

        String systemOsName = System.getProperty("os.name").toLowerCase();

        if (systemOsName.startsWith("mac")) {
            // Mac OS X
            nativePrefix = "lib";
            nativeSuffix = ".jnilib";
        } else if (systemOsName.startsWith("windows")) {
            // Microsoft Windows
            nativePrefix = "";
            nativeSuffix = ".dll";
        } else {
            // Unix of some variety
            nativePrefix = "lib";
            nativeSuffix = ".so";
        }

        if (DEBUG) {
            System.err.println("os.name = " + systemOsName);
            System.err.println("nativePrefix = " + nativePrefix + "  nativeSuffix = " + nativeSuffix);
        }

        // Create / initialize the temp root directory, starting the Reaper
        // thread to reclaim old installations if necessary. If we get an
        // exception, set an error code so we don't try to start the applets.
        try {
            initTmpRoot();
        } catch (Exception ex) {
            ex.printStackTrace();
            staticInitError = true;
        }
    }


    // -----------------------------------------------------------------------

    /**
     * Proxy implementation class of AppletStub. Delegates to the
     * JNLPAppletLauncher class.
     */
    private class AppletStubProxy implements AppletStub {
        public boolean isActive() {
            return JNLPAppletLauncher.this.isActive();
        }

        public URL getDocumentBase() {
            return JNLPAppletLauncher.this.getDocumentBase();
        }

        public URL getCodeBase() {
            return JNLPAppletLauncher.this.getCodeBase();
        }

        public String getParameter(String name) {
            return JNLPAppletLauncher.this.getParameter(name);
        }

        public AppletContext getAppletContext() {
            return JNLPAppletLauncher.this.getAppletContext();
        }

        public void appletResize(int width, int height) {
            JNLPAppletLauncher.this.resize(width, height);
        }
    }

    /**
     * Parser class for JNLP files for the applet launcher. For simplicitly, we
     * assume that everything of interest is within a single "jnlp" tag and
     * that the "resources" tags are not nested.
     */
    private static class JNLPParser {

        // The following represents the various states we can be in
        private static final int INITIAL = 1;
        private static final int STARTED = 2;
        private static final int IN_JNLP = 3;
        private static final int IN_RESOURCES = 4;
        private static final int SKIP_ELEMENT = 5;

        private static SAXParserFactory factory;
        private static String systemOsName;
        private static String systemOsArch;

        private JNLPAppletLauncher launcher;
        private URL url;
        private InputStream in;
        private JNLPHandler handler;
        private String codebase = "";
        private int state = INITIAL;
        private int prevState = INITIAL;
        private int depth = 0;
        private int skipDepth = -1;

        private JNLPParser(JNLPAppletLauncher launcher, URL url) throws IOException {
            this.launcher = launcher;
            this.url = url;
            this.handler = new JNLPHandler();
        }

        private void parse() throws IOException {
            if (VERBOSE) {
                System.err.println("JNLPParser: " + url.toString());
            }
            try {
                URLConnection conn = url.openConnection();
                conn.connect();
                InputStream in = new BufferedInputStream(conn.getInputStream());

                SAXParser parser = factory.newSAXParser();
                parser.parse(in, handler);
                in.close();
            } catch (ParserConfigurationException ex) {
                throw (IOException) new IOException().initCause(ex);
            } catch (SAXException ex) {
                throw (IOException) new IOException().initCause(ex);
            }
        }

        // Static initializer for JNLPParser
        static {
            factory = SAXParserFactory.newInstance();
            systemOsName = System.getProperty("os.name").toLowerCase();
            systemOsArch = System.getProperty("os.arch").toLowerCase();
            if (DEBUG) {
                System.err.println("os.name = " + systemOsName);
                System.err.println("os.arch = " + systemOsArch);
            }
        }


        /**
         * Handler class containing callback methods for the parser.
         */
        private class JNLPHandler extends DefaultHandler {

            JNLPHandler() {
            }

            /* @Override */
            public void startDocument() {
                if (VERBOSE) {
                    System.err.println("START DOCUMENT: " + url);
                }

                state = STARTED;
                if (VERBOSE) {
                    System.err.println("state = " + state);
                }
            }

            /* @Override */
            public void endDocument() {
                if (VERBOSE) {
                    System.err.println("END DOCUMENT");
                }

                state = INITIAL;
                if (VERBOSE) {
                    System.err.println("state = " + state);
                }
            }

            /* @Override */
            public void startElement(String uri,
                    String localName,
                    String qName,
                    Attributes attributes) throws SAXException {

                ++depth;

                if (VERBOSE) {
                    System.err.println("<" + qName + ">" + " : depth=" + depth);

                    for (int i = 0; i < attributes.getLength(); i++) {
                        System.err.println("    [" + i + "]  " + attributes.getQName(i) +
                                " = \"" + attributes.getValue(i) + "\"");
                    }
                }

                // Parse qName based on current state
                switch (state) {
                case STARTED:
                    if (qName.equals("jnlp")) {
                        state = IN_JNLP;

                        codebase = attributes.getValue("codebase");
                        if (codebase == null) {
                            throw new SAXException("<jnlp> unable to determine codebase");
                        }
                        if (codebase.lastIndexOf('/') != codebase.length()-1) {
                            codebase = codebase + "/";
                        }
                        if (VERBOSE) {
                            System.err.println("JNLP : codebase=" + codebase);
                        }

                        if (VERBOSE) {
                            System.err.println("state = " + state);
                        }
                    } else if (qName.equals("resources")) {
                        throw new SAXException("<resources> tag not within <jnlp> tag");
                    }
                    // Ignore all other tags
                    break;

                case IN_JNLP:
                    if (qName.equals("jnlp")) {
                        throw new SAXException("Nested <jnlp> tags");
                    } else if (qName.equals("resources")) {
                        String osName = attributes.getValue("os");
                        String osArch = attributes.getValue("arch");
                        if ((osName == null || systemOsName.startsWith(osName.toLowerCase())) &&
                                (osArch == null || systemOsArch.startsWith(osArch.toLowerCase()))) {
                            if (VERBOSE) {
                                System.err.println("Loading resources : os=" + osName + "  arch=" + osArch);
                            }
                            state = IN_RESOURCES;
                        } else {
                            prevState = state;
                            skipDepth = depth - 1;
                            state = SKIP_ELEMENT;
                        }

                        if (VERBOSE) {
                            System.err.println("Resources : os=" + osName + "  arch=" + osArch + "  state = " + state);
                        }
                    }
                    break;

                case IN_RESOURCES:
                    try {
                        if (qName.equals("jnlp")) {
                            throw new SAXException("Nested <jnlp> tags");
                        } else if (qName.equals("resources")) {
                            throw new SAXException("Nested <resources> tags");
                        } else if (qName.equals("jar")) {
                            String str = attributes.getValue("href");
                            if (str == null || str.length() == 0) {
                                throw new SAXException("<jar> tag missing href attribute");
                            }
                            String jarFileStr = codebase + str;
                            if (VERBOSE) {
                                System.err.println("Jar: " + jarFileStr);
                            }
                            URL jarFile = new URL(jarFileStr);
                            launcher.addJarFile(jarFile);
                        } else if (qName.equals("nativelib")) {
                            String str = attributes.getValue("href");
                            if (str == null || str.length() == 0) {
                                throw new SAXException("<nativelib> tag missing href attribute");
                            }
                            String nativeLibStr = codebase + str;
                            if (VERBOSE) {
                                System.err.println("Native Lib: " + nativeLibStr);
                            }
                            URL nativeLib = new URL(nativeLibStr);
                            launcher.addNativeJar(nativeLib);
                        } else if (qName.equals("extension")) {
                            String extensionURLString = attributes.getValue("href");
                            if (extensionURLString == null || extensionURLString.length() == 0) {
                                throw new SAXException("<extension> tag missing href attribute");
                            }
                            if (VERBOSE) {
                                System.err.println("Extension: " + extensionURLString);
                            }
                            URL extensionURL = new URL(extensionURLString);
                            JNLPParser parser = new JNLPParser(launcher, extensionURL);
                            parser.parse();
                        } else {
                            prevState = state;
                            skipDepth = depth - 1;
                            state = SKIP_ELEMENT;

                            if (VERBOSE) {
                                System.err.println("state = " + state);
                            }
                        }
                    } catch (IOException ex) {
                        throw (SAXException) new SAXException(ex).initCause(ex);
                    }
                    break;

                case INITIAL:
                case SKIP_ELEMENT:
                default:
                    break;
                }

            }

            /* @Override */
            public void endElement(String uri,
                    String localName,
                    String qName) throws SAXException {

                --depth;

                if (VERBOSE) {
                    System.err.println("</" + qName + ">");
                }

                // Parse qName based on current state
                switch (state) {
                case IN_JNLP:
                    if (qName.equals("jnlp")) {
                        state = STARTED;
                        if (VERBOSE) {
                            System.err.println("state = " + state);
                        }
                    }
                    break;

                case IN_RESOURCES:
                    if (qName.equals("resources")) {
                        state = IN_JNLP;
                        if (VERBOSE) {
                            System.err.println("state = " + state);
                        }
                    }
                    break;

                case SKIP_ELEMENT:
                    if (depth == skipDepth) {
                        state = prevState;
                        skipDepth = -1;
                        if (VERBOSE) {
                            System.err.println("state = " + state);
                        }
                    }
                    break;

                case INITIAL:
                case STARTED:
                default:
                    break;
                }

            }

        }
    }

    //----------------------------------------------------------------------
    // Helper routines for adding -Dsun.java2d.noddraw=true to deployment.properties

    // Get a "boolean" parameter
    private boolean getBooleanParameter(String parameterName) {
        return Boolean.valueOf(getParameter(parameterName)).booleanValue();
    }

    private String getDeploymentPropsDir() {
        final String osName = System.getProperty("os.name").toLowerCase();
        StringBuffer result = new StringBuffer();

        result.append(System.getProperty("user.home"));
        if (osName.startsWith("windows")) {
            if (osName.indexOf("vista") != -1) {
                result.append(File.separator).append("AppData").
                        append(File.separator).append("LocalLow");
            } else {
                result.append(File.separator).append("Application Data");
            }
            result.append(File.separator).append("Sun").
                    append(File.separator).append("Java").
                    append(File.separator).append("Deployment");
        } else if (osName.startsWith("mac")) {
            result.append(File.separator).append("Library").
                    append(File.separator).append("Caches").
                    append(File.separator).append("Java");
        } else {
            result.append(File.separator).append(".java").
                    append(File.separator).append("deployment");
        }

        return result.toString();
    }

    private void checkNoDDrawAndUpdateDeploymentProperties() {
        if (!getBooleanParameter("noddraw.check"))
            return;
        if (System.getProperty("os.name").toLowerCase().startsWith("windows") &&
            !"true".equalsIgnoreCase(System.getProperty("sun.java2d.noddraw"))) {
            if (!SwingUtilities.isEventDispatchThread()) {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                updateDeploymentPropertiesImpl();
                            }
                        });
                } catch (Exception e) {
                }
            } else {
                updateDeploymentPropertiesImpl();
            }
        }
    }

    private void updateDeploymentPropertiesImpl() {
        String userHome = System.getProperty("user.home");
        File dontAskFile = new File(userHome + File.separator + ".jnlp-applet" +
                                    File.separator + DONT_ASK);
        if (dontAskFile.exists())
            return; // User asked us not to prompt again

        int option = 0;

        if (!getBooleanParameter("noddraw.check.silent")) {
            option = JOptionPane.showOptionDialog(null,
                                                  "For best robustness of OpenGL applets on Windows,\n" +
                                                  "we recommend disabling Java2D's use of DirectDraw.\n" +
                                                  "This setting will affect all applets, but is unlikely\n" +
                                                  "to slow other applets down significantly. May we update\n" +
                                                  "your deployment.properties to turn off DirectDraw for\n" +
                                                  "applets? You can change this back later if necessary\n" +
                                                  "using the Java Control Panel, Java tab, under Java\n" +
                                                  "Applet Runtime Settings.",
                                                  "Update deployment.properties?",
                                                  JOptionPane.YES_NO_CANCEL_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE,
                                                  null,
                                                  new Object[] {
                                                      "Yes",
                                                      "No",
                                                      "No, Don't Ask Again"
                                                  },
                                                  "Yes");
        }

        if (option < 0 ||
            option == 1)
            return; // No

        if (option == 2) {
            try {
                dontAskFile.createNewFile();
            } catch (IOException e) {
            }
            return; // No, Don't Ask Again
        }

        try {
            // Must update deployment.properties
            File propsDir = new File(getDeploymentPropsDir());
            if (!propsDir.exists()) {
                // Don't know what's going on or how to set this permanently
                return;
            }

            File propsFile = new File(propsDir, "deployment.properties");
            if (!propsFile.exists()) {
                // Don't know what's going on or how to set this permanently
                return;
            }

            Properties props = new Properties();
            InputStream input = new BufferedInputStream(new FileInputStream(propsFile));
            props.load(input);
            input.close();
            // Search through the keys looking for JRE versions
            Set/*<String>*/ jreVersions = new HashSet/*<String>*/();
            for (Iterator/*<String>*/ iter = props.keySet().iterator(); iter.hasNext(); ) {
                String key = (String) iter.next();
                if (key.startsWith(JRE_PREFIX)) {
                    int idx = key.lastIndexOf(".");
                    if (idx >= 0 && idx > JRE_PREFIX.length()) {
                        String jreVersion = key.substring(JRE_PREFIX.length(), idx);
                        jreVersions.add(jreVersion);
                    }
                }
            }

            // Make sure the currently-running JRE shows up in this set to
            // avoid repeated displays of the dialog. It might not in some
            // upgrade scenarios where there was a pre-existing
            // deployment.properties and the new Java Control Panel hasn't
            // been run yet.
            jreVersions.add(System.getProperty("java.version"));

            // OK, now that we know all JRE versions covered by the
            // deployment.properties, check out the args for each and update
            // them
            for (Iterator/*<String>*/ iter = jreVersions.iterator(); iter.hasNext(); ) {
                String version = (String) iter.next();
                String argKey = JRE_PREFIX + version + ".args";
                String argVal = props.getProperty(argKey);
                if (argVal == null) {
                    argVal = NODDRAW_PROP;
                } else if (argVal.indexOf(NODDRAW_PROP) < 0) {
                    argVal = argVal + " " + NODDRAW_PROP;
                }
                props.setProperty(argKey, argVal);
            }

            OutputStream output = new BufferedOutputStream(new FileOutputStream(propsFile));
            props.store(output, null);
            output.close();

            if (!getBooleanParameter("noddraw.check.silent")) {
                // Tell user we're done
                JOptionPane.showMessageDialog(null,
                                              "For best robustness, we recommend you now exit and\n" +
                                              "restart your web browser. (Note: clicking \"OK\" will\n" +
                                              "not exit your browser.)",
                                              "Browser Restart Recommended",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
