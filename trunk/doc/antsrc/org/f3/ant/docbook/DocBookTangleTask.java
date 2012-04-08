/*
 * Copyright 2007-2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.ant.docbook;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;

/**
 * An Ant task to extract F3 and Java example source files from 
 * DocBook sources.  This allows a build to verify that they can be compiled 
 * and possibly executed.
 * 
 * @author Tom Ball
 */
public class DocBookTangleTask extends MatchingTask {
    private File srcDir;
    private File destDir;

    public void setSrcDir(File srcDir) {
        this.srcDir = srcDir;
    }

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    @Override
    public void execute() {
        DocBookTangle.Log log = new DocBookTangle.Log() {
            public void error(String msg, Exception e) {
                log(msg, Project.MSG_ERR);
            }
            public void error(Exception e) {
                log(e, Project.MSG_ERR);
            }
            public void info(String msg) {
                log(msg, Project.MSG_INFO);
            }
            public void verbose(String msg) {
                log(msg, Project.MSG_VERBOSE);
            }
        };
        List<String> mainOpts = new ArrayList<String>();
        if (destDir != null) {
            mainOpts.add("-o");
            mainOpts.add(destDir.getAbsolutePath());
        }
        DirectoryScanner s = getDirectoryScanner(srcDir);
        int count = 0;
        for (String path: s.getIncludedFiles()) {
            if (path.endsWith(".xml")) {
                File srcFile = new File(srcDir, path);
                String destPath = path.substring(0, path.lastIndexOf(".xml")) + "-1.f3";
                File destFile = new File(destDir, destPath);
                if (destFile.exists() && destFile.lastModified() >= srcFile.lastModified())
                    continue;
                destFile.getParentFile().mkdirs();
                mainOpts.add(srcFile.getPath());
                count++;
            }
        }
        if (mainOpts.size() > 0) {
            log("Extracting examples from " + count + " files to " + destDir, Project.MSG_INFO);
            DocBookTangle cp = new DocBookTangle();
            cp.setLog(log);
            boolean ok = cp.run((String[])mainOpts.toArray(new String[mainOpts.size()]));
            if (!ok)
                throw new BuildException("CompileProperties failed.");
        }
    }
}
