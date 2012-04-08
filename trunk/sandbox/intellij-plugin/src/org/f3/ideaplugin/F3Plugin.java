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

package org.f3.ideaplugin;

import com.intellij.debugger.DebuggerManager;
import com.intellij.debugger.PositionManager;
import com.intellij.debugger.engine.DebugProcess;
import com.intellij.lang.Language;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.Function;
import org.f3.ideaplugin.debug.F3PositionManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * F3SupportLoader
 */
public class F3Plugin implements ApplicationComponent {
    
    public static final Language F3_LANGUAGE = new F3Language();
    public static final LanguageFileType F3_FILE_TYPE = new F3FileType();
    public static final String F3_FILE_EXTENSION = "f3";
    public static final String F3_LANGUAGE_NAME = "F3";
    public static final Icon F3_ICON = IconLoader.getIcon("/icons/f3.png");

    public F3Plugin() {
    }

    public void initComponent() {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                FileTypeManager.getInstance().registerFileType(F3_FILE_TYPE, F3_FILE_EXTENSION);

                ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerAdapter() {
                    public void projectOpened(Project project) {
                        CompilerManager compilerManager = CompilerManager.getInstance(project);
                        compilerManager.addCompiler(new F3Compiler());
                        compilerManager.addCompilableFileType(F3_FILE_TYPE);

                        DebuggerManager.getInstance (project).registerPositionManagerFactory (new Function<DebugProcess, PositionManager>() {
                            public PositionManager fun (DebugProcess debugProcess) {
                                return new F3PositionManager (debugProcess);
                            }
                        });
                    }
                });
            }
        });
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "F3SupportLoader";
    }

}
