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

#ifdef PROJECT_F3

#include "configuration.h"
#include "util.h"

#include <string>

int main(int argc, char** argv) {
    Configuration config("execute_");
    Util util;
    int error;
    
    if ( (error =  config.initConfiguration(argc, argv)) != (EXIT_SUCCESS) )  {
        return error;
    }
    
    // construct command
    std::string cmd;

    if(config.profile_emulator.empty()) {
        cmd = "\"" + config.javacmd + "\" ";
    } else {
        cmd = "\"" + util.evaluatePath(config.f3path, config.profile_emulator) + "\" ";
    }

    if (! config.vmargs.empty()) {
        cmd += config.vmargs + " ";
    }
    if (! config.profile_vmargs.empty()) {
        cmd += config.profile_vmargs + " ";
    }
    if (! config.profile_nativelibpath.empty()) {
        if (config.librarypath.empty()) {
            cmd += "-Djava.library.path=\"" + util.evaluatePath(config.f3path, config.profile_nativelibpath) + "\" ";
        } else {
            cmd += "-Djava.library.path=\"" + config.librarypath + ";" + util.evaluatePath(config.f3path, config.profile_nativelibpath) + "\" ";
        }
    } else if (! config.librarypath.empty()) {
        cmd += "-Djava.library.path=\"" + config.librarypath  + "\" ";
    }
    if (! config.profile_bootnativelibpath.empty()) {
        cmd += "-Dsun.boot.library.path=\"" + util.evaluatePath(config.f3path, config.profile_bootnativelibpath + "\" ");
    }
    if (! config.securitypolicy.empty()) {
        cmd += "-Djava.security.policy=\"" + config.securitypolicy + "\" ";
    } else if (! config.profile_security_policy.empty()) {
        cmd += "-Djava.security.policy=\"" + util.evaluatePath(config.f3path, config.profile_security_policy + "\" ");
    }
    if (! config.profile_bootclasspath_prepend.empty()) {
        cmd += "\"-Xbootclasspath/p:" + util.evaluatePath(config.f3path, config.profile_bootclasspath_prepend) + "\" ";
    }
    if (! config.profile_bootclasspath_append.empty()) {
        cmd += "\"-Xbootclasspath/a:" + util.evaluatePath(config.f3path, config.profile_bootclasspath_append) + "\" ";
    }
    if (! config.profile_bootclasspath.empty()) {
        cmd += "\"-Xbootclasspath:" + util.evaluatePath(config.f3path, config.profile_bootclasspath) + "\" ";
    }
    if (! config.profile_classpath.empty()) {
        cmd += "-classpath \"" + util.evaluatePath(config.f3path, config.profile_classpath);
        if (! config.classpath.empty()) {
            cmd += ";" + config.classpath;
        }
        cmd += "\" ";
    } else if (! config.classpath.empty()) {
        cmd += "-classpath \"" + config.classpath + "\" ";
    }

    cmd += "org.f3.runtime.Main ";
    cmd += config.f3args;
    

    return util.createProcess(cmd);
}

#endif  /* PROJECT_F3 */
