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

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.projectRoots.SdkModel;

/**
 * @author David Kaspar
 */
public final class F3SdkAdditionalData implements SdkAdditionalData {

    private final String javaSdkName;
    private final SdkModel sdkModel;

    public F3SdkAdditionalData (String javaSdkName, SdkModel sdkModel) {
        this.javaSdkName = javaSdkName;
        this.sdkModel = sdkModel;
    }

    public String getJavaSdkName () {
        return javaSdkName;
    }

    public F3SdkAdditionalData clone () throws CloneNotSupportedException {
        return (F3SdkAdditionalData) super.clone ();
    }

    public void checkValid (SdkModel sdkModel) throws ConfigurationException {
        if (javaSdkName == null)
            throw new ConfigurationException ("No Java SDK configured");
        if (findJavaSdk (javaSdkName, sdkModel) == null)
            throw new ConfigurationException ("Cannot find Java SDK");
    }

    Sdk findSdk () {
        return findJavaSdk (javaSdkName, sdkModel);
    }

    private static Sdk findJavaSdk (String sdkName, SdkModel sdkModel) {
        for (Sdk sdk : ProjectJdkTable.getInstance ().getAllJdks ()) {
            if (sdk.getName ().equals (sdkName))
                return sdk;
        }
        if (sdkModel != null) {
            for (Sdk sdk : sdkModel.getSdks ()) {
                if (sdk.getName ().equals (sdkName))
                    return sdk;
            }
        }
        return null;
    }

}
