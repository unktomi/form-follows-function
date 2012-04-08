/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package org.f3.ideaplugin.project;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.f3.ideaplugin.F3Plugin;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.EnumSet;

/**
 * @author David Kaspar
 */
public final class F3SdkType extends SdkType implements ApplicationComponent {

    public static final String F3_EXEC = SystemInfo.isWindows ? "f3.bat" : "f3";
    public static final String JAVA_SDK_PROPERTY_NAME = "java-sdk";

    public F3SdkType () {
        super ("F3 SDK");
    }

    public static F3SdkType getInstance() {
        return ApplicationManager.getApplication().getComponent(F3SdkType.class);
    }

    @Nullable
    public String suggestHomePath () {
        if (SystemInfo.isWindows)
            return "c:\\Program Files\\F3\\f3-sdk1.0pre1\\";
        if (SystemInfo.isMac)
            return "/System/Library/Frameworks/F3.framework/Versions/Current/";
        return null;
    }

    public boolean isValidSdkHome (String path) {
        File home = new File (path);
        File bin = new File (home, "bin");
        if (! bin.exists ())
            return false;
        File f3 = new File (bin, F3_EXEC);
        return f3.exists ();
    }

    @Nullable
    public String getVersionString (String sdkHome) {
        return "1.0";
    }

    public String suggestSdkName (String currentSdkName, String sdkHome) {
        int i = sdkHome.lastIndexOf ('/');
        return i >= 0 ? sdkHome.substring (i + 1) : sdkHome;
    }

    public AdditionalDataConfigurable createAdditionalDataConfigurable (SdkModel sdkModel, SdkModificator sdkModificator) {
        return new F3SdkAdditionalDataConfigurable (sdkModel);
    }

    public void setupSdkPaths (Sdk sdk) {
        SdkModificator modificator = sdk.getSdkModificator ();
        modificator.removeAllRoots ();
        LocalFileSystem localFileSystem = LocalFileSystem.getInstance ();
        JarFileSystem jarFileSystem = JarFileSystem.getInstance ();

        VirtualFile lib = localFileSystem.findFileByPath (getLibraryPath (sdk).replace (File.separatorChar, '/'));
        if (lib != null) {
            for (VirtualFile libFile : lib.getChildren ()) {
                if (libFile.getName ().endsWith (".jar")) {
                    VirtualFile inJar = jarFileSystem.findFileByPath (libFile.getPath () + JarFileSystem.JAR_SEPARATOR);
                    if (inJar != null)
                        modificator.addRoot (inJar, ProjectRootType.CLASS);
                }
            }
        }

        VirtualFile doc = LocalFileSystem.getInstance ().findFileByPath (getJavaDocPath (sdk).replace (File.separatorChar, '/'));
        if (doc != null) {
            for (VirtualFile docFile : doc.getChildren ()) {
                if (docFile.isDirectory ()  &&  localFileSystem.findFileByPath (docFile.getPath () + "/index.html") != null) {
                    modificator.addRoot (docFile, ProjectRootType.JAVADOC);
                }
            }
        }

        F3SdkAdditionalData data = (F3SdkAdditionalData) sdk.getSdkAdditionalData ();
        if (data != null) {
            Sdk javaSdk = data.findSdk ();
            if (javaSdk != null) {
                SdkModificator javaModificator = javaSdk.getSdkModificator ();
                if (javaModificator != null) {
                    for (ProjectRootType type : EnumSet.of (ProjectRootType.SOURCE, ProjectRootType.CLASS, ProjectRootType.JAVADOC)) { // ProjectRootType.values()
                        VirtualFile[] roots = javaModificator.getRoots (type);
                        if (roots != null)
                            for (VirtualFile root : roots)
                                modificator.addRoot (root, type);
                    }
                }
            }
        }
        modificator.commitChanges ();
    }

    private static String getConvertedHomePath (Sdk sdk) {
        String home = sdk.getHomePath ().replace ('/', File.separatorChar);
        if (! home.endsWith (File.separator))
            home += File.separatorChar;
        return home;
    }

    @Nullable
    public String getBinPath (Sdk sdk) {
        return getConvertedHomePath (sdk) + "bin";
    }

    public static String getLibraryPath (Sdk sdk) {
        return getConvertedHomePath (sdk) + "lib";
    }

    public static String getJavaDocPath (Sdk sdk) {
        return getConvertedHomePath (sdk) + "docs";
    }

    @Nullable
    public Sdk getEncapsulatedSdk (Sdk sdk) {
        F3SdkAdditionalData data = (F3SdkAdditionalData) sdk.getSdkAdditionalData ();
        return data != null ? data.findSdk () : null;
    }

    @Nullable
    public String getToolsPath (Sdk sdk) {
        sdk = getEncapsulatedSdk (sdk);
        return sdk != null ? sdk.getSdkType ().getToolsPath (sdk) : null;
    }

    @Nullable
    public String getVMExecutablePath (Sdk sdk) {
        return getBinPath (sdk) + File.separator + F3_EXEC;
    }

    @Nullable
    public String getRtLibraryPath (Sdk sdk) {
        sdk = getEncapsulatedSdk (sdk);
        return sdk != null ? sdk.getSdkType ().getRtLibraryPath (sdk) : null;
    }

    @Nullable
    public SdkAdditionalData loadAdditionalData (Element additional) {
        String value = additional.getAttributeValue (JAVA_SDK_PROPERTY_NAME);
        return value != null ? new F3SdkAdditionalData (value, null) : null;
    }

    public void saveAdditionalData (SdkAdditionalData additionalData, Element additional) {
        if (additionalData instanceof F3SdkAdditionalData) {
            F3SdkAdditionalData data = (F3SdkAdditionalData) additionalData;
            String name = data.getJavaSdkName ();
            if (name != null)
                additional.setAttribute (JAVA_SDK_PROPERTY_NAME, name);
        }
    }

    public String getPresentableName () {
        return "F3 SDK";
    }

    public Icon getIcon () {
        return F3Plugin.F3_ICON;
    }

    public Icon getIconForAddAction () {
        return F3Plugin.F3_ICON;
    }

    @NonNls @NotNull
    public String getComponentName () {
        return "F3SdkType";
    }

    public void initComponent () {
    }

    public void disposeComponent () {
    }

}
