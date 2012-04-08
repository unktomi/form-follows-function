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

package org.f3.ideaplugin.debug;

import com.intellij.debugger.NoDataException;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.SourcePosition;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.debugger.requests.ClassPrepareRequestor;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.request.ClassPrepareRequest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author David Kaspar
*/
public class F3PositionManager implements PositionManager {

    private final DebugProcess debugProcess;

    public F3PositionManager (DebugProcess debugProcess) {
        this.debugProcess = debugProcess;
    }

    @Nullable public SourcePosition getSourcePosition (Location location) throws NoDataException {
        if (location == null)
            throw new NoDataException ();
//        F3Plugin.F3_LANGUAGE.getParserDefinition ().createFile ()
//        SourcePosition.createFromLine ();
        return null; // TODO
    }

    @NotNull public List<ReferenceType> getAllClasses (SourcePosition classPosition) throws NoDataException {
// TODO - search source paths for relative path
//        String path = null;
//        for (PsiFile root : classPosition.getFile ().getPsiRoots ()) {
//            path = VfsUtil.getRelativePath (classPosition.getFile ().getVirtualFile (), root.getVirtualFile (), '/');
//            if (path != null)
//                break;
//        }
//        if (path == null)
//            throw new NoDataException ();
//        System.out.println ("path = " + path);
        String className = classPosition.getFile ().getVirtualFile ().getNameWithoutExtension (); // TODO
        return debugProcess.getVirtualMachineProxy ().classesByName (className);
    }

    @NotNull public List<Location> locationsOfLine (ReferenceType type, SourcePosition position) throws NoDataException {
        try {
            // TODO - resolve type in case of inner classes or instances
            int line = position.getLine() + 1;
            List<Location> locations;
            if (debugProcess.getVirtualMachineProxy ().versionHigher ("1.4"))
                locations = type.locationsOfLine (DebugProcess.JAVA_STRATUM, null, line);
            else
                locations = type.locationsOfLine (line);
            if (locations == null  ||  locations.isEmpty())
                throw new NoDataException();
            return locations;
        } catch (AbsentInformationException e) {
            e.printStackTrace (); // TODO
            throw new NoDataException ();
        }
    }

    @Nullable public ClassPrepareRequest createPrepareRequest (ClassPrepareRequestor requestor, SourcePosition position) throws NoDataException {
//        System.out.println ("position.getFile () = " + position.getFile ());
//        System.out.println ("position.getFile ().findElementAt (position.getOffset ()) = " + position.getFile ().findElementAt (position.getOffset ()));
        return debugProcess.getRequestsManager ().createClassPrepareRequest (requestor, position.getFile ().getVirtualFile ().getNameWithoutExtension ()); // TODO
    }

}
