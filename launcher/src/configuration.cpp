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

#include <windows.h>
#include <fstream>

#include "configuration.h"

Configuration::Configuration(const std::string& prefix)
: prefix(prefix),
        f3path(""), 
        classpath("."), 
        vmargs(""),
        profile_classpath(""), 
        profile_bootclasspath(""), 
        profile_bootclasspath_prepend(""), 
        profile_bootclasspath_append(""), 
        profile_nativelibpath(""),
        profile_bootnativelibpath(""),
        profile_vmargs(""),
        profile_toolkit(""),
        device_profile("desktop"),
        profile_compile_opts(""),
        profile_filename("desktop.properties") {
}

Configuration::~Configuration() {
}

int Configuration::initConfiguration(int argc, char** argv) {
    int error;
    
    // set inital values
    init();
    
    // read arguments
    if ( (error =  parseArgs(--argc, ++argv)) != (EXIT_SUCCESS) )  {
        return error;
    }
    
    // read config-file
    if ( (error =  readConfigFile()) != (EXIT_SUCCESS) )  {
        return error;
    }
    bool isf3w = (f3cmd == "f3w.exe");

    // evaluate JAVA_HOME, if javacmd not set
    if (javacmd.empty()) {
        const char* s = getenv("JAVA_HOME");
        if (s != NULL) {
            javacmd = s;
            javacmd += isf3w ? "/bin/javaw.exe" : "/bin/java.exe";
            if (! fileExists(javacmd)) {
                javacmd = isf3w ? "javaw.exe" : "java.exe";
            }
        } else {
            javacmd = isf3w ? "javaw.exe" : "java.exe";
        }
    }
    
    return EXIT_SUCCESS;
}

void Configuration::init() {
    const char* s;
    
    // set javaCmd if given directly in _JAVACMD
    s = getenv("_JAVACMD");
    javacmd = (s != NULL)? s : "";
    
    // evaluate CLASSPATH
    s = getenv("CLASSPATH");
    if (s != NULL) {
        classpath += ";";
        classpath += s;
    }
    
    // set default f3path
    char buf[MAX_PATH];
    GetModuleFileName (NULL, buf, MAX_PATH);
    f3path = buf;
    f3path.erase (f3path.rfind("\\"));
    f3path += "\\..";
    f3cmd  = buf;
    f3cmd.erase (0,f3cmd.rfind("\\f3") + 1);

    // set f3args if given directly in _F3_ARGS
    s = getenv("_F3_ARGS");
    f3args = (s != NULL)? s : "";
}

int Configuration::readConfigFile() {
    // find file
    std::string path = f3path;
    path += "\\profiles\\" + profile_filename;
    std::ifstream file(path.c_str());
    if (file == NULL) {
        fprintf (stderr, "Properties-file %s not found.", profile_filename.c_str());
        return (EXIT_FAILURE);
    }
    
    // prepare regular expression
    std::string line, key, value;
    std::string::size_type pos, start, end;
    // if the toolkit isn't set via -Xtoolkit argument, we search for
    // default_toolkit which should be the first line in the properties file,
    // and when we find one we change the prefix to $toolkit_$prefix so that we
    // only load relevant properties;
    // if the tookit was set, we set the prefix to be $toolkit_$prefix so the
    // default_toolkit setting in the property file will be ignored and we
    // will only load properties starting with $toolkit_$prefix which is what
    // we want
    std::string current_prefix =
        profile_toolkit == "" ?
            "default_" :
            profile_toolkit + "_" + prefix;
    
    while (getline (file, line)) {
        // remove comment
        if ((pos = line.find('#')) != std::string::npos) {
            line.erase (pos);
        }
        
        // parse line
        pos = line.find_first_of("=;");
        if (pos > 0 && pos != std::string::npos) {
            start = line.find_first_not_of(" \t\n\r");
            end = line.find_last_not_of(" \t\n\r", pos-1);
            if (start == std::string::npos || end == std::string::npos) {
                continue;
            }
            key   = line.substr (start, end - start + 1);
            if (key.find(current_prefix) != 0) {
                continue;
            }
            key.erase (0, current_prefix.length());

            start = line.find_first_not_of(" \"\t\n\r", pos+1);
            end = line.find_last_not_of(" \"\t\n\r");
            if (start == std::string::npos || end == std::string::npos) {
                continue;
            }
            value = line.substr (start, end - start + 1);

            // evaluate key/value-pair
            if (key == "classpath") {
                profile_classpath = value;
            } else
            if (key == "toolkit") {
                profile_toolkit = value;
                current_prefix = profile_toolkit + "_" + prefix;
            } else
            if (key == "bootclasspath") {
                profile_bootclasspath = value;
            } else
            if (key == "bootclasspath_prepend") {
                profile_bootclasspath_prepend = value;
            } else
            if (key == "bootclasspath_append") {
                profile_bootclasspath_append = value;
            } else
            if (key == "nativelibpath") {
                profile_nativelibpath = value;
            } else
            if (key == "bootnativelibpath") {
                profile_bootnativelibpath = value;
            } else
            if (key == "vmargs_common") {
                profile_vmargs += value + " ";
            } else
            if (key == "security_policy") {
                profile_security_policy = value;
            } else
            if (key == "vmargs_windows") {
                profile_vmargs += value + " ";
            } else
            if (key == "emulator_windows") {
                profile_emulator = value;
            } else
            if (key == "emulator") {
                profile_emulator = value;
            } else
            if (key == "opts") {
                profile_compile_opts = value;
            };
        }
    }
    file.close();
    return (EXIT_SUCCESS);
}

int Configuration::parseArgs(int argc, char** argv) {
    const char *arg;
    bool islauncher = (f3cmd == "f3.exe" || f3cmd == "f3w.exe");
    bool seen_main = FALSE;
    while (argc-- > 0 && (arg = *argv++) != NULL) {

        if ((0 == strcmp("-cp", arg)) || (0 == strcmp("-classpath", arg))) {
            if (argc-- > 0 && (arg = *argv++) != NULL) {
                classpath = arg;
            } else {
                fprintf (stderr, "No argument for classpath found.");
                return (EXIT_FAILURE);
            }
        } else if (islauncher && 0 == strcmp("-jar", arg)) {
             if (argc-- > 0 && (arg = *argv++) != NULL) {
                classpath = arg;
                f3args += " \"";
                f3args += arg;
                f3args += "\"";
                seen_main = true;
            } else {
                fprintf (stderr, "No argument for jar found.");
                return (EXIT_FAILURE);
            }
        } else if (0 == strcmp("-profile", arg)) {
            if (argc-- > 0 && (arg = *argv++) != NULL) {
                profile_filename = arg;
                device_profile   = arg;
                profile_filename += ".properties";
            } else {
                fprintf (stderr, "No argument for profile found.");
                return (EXIT_FAILURE);
            }

        } else if (0 == strcmp("-Xtoolkit", arg)) {
            if (argc-- > 0 && (arg = *argv++) != NULL) {
                std::string toolkit(arg);
                profile_toolkit = toolkit;
            } else {
                fprintf (stderr, "No argument for -Xtoolkit found.");
                return (EXIT_FAILURE);
            }
        } else if (0 == strncmp("-J", arg, 2)) {
            vmargs += " \"";
            vmargs += arg+2;    // skip first two characters "-J"
            vmargs += "\"";
        } else if (islauncher && !seen_main && 0 == strcmp("-version", arg)) {
            f3args = "org.f3.runtime.LauncherHelper -version";
            return (EXIT_SUCCESS);
        } else if (islauncher && !seen_main && 0 == strcmp("-fullversion", arg)) {
            f3args = "org.f3.runtime.LauncherHelper -fullversion";
            return (EXIT_SUCCESS);
        } else if (islauncher && !seen_main && 0 == strcmp("-help", arg)) {
            f3args = "org.f3.runtime.LauncherHelper -help";
            return (EXIT_SUCCESS);
        } else if (islauncher && !seen_main && 0 == strcmp("-?", arg)) {
            f3args = "org.f3.runtime.LauncherHelper -help";
            return (EXIT_SUCCESS);
        } else if (islauncher && !seen_main && 0 == strcmp("-X", arg)) {
            f3args = "org.f3.runtime.LauncherHelper -helpx";
            return (EXIT_SUCCESS);
        } else if (islauncher && 0 == strncmp(arg, "-Djava.library.path", strlen("-Djava.library.path"))) {
            librarypath = arg;
            librarypath.erase(0, strlen("-Djava.library.path="));
        } else if (islauncher && 0 == strncmp(arg, "-Djava.security.policy", strlen("-Djava.security.policy"))) {
            securitypolicy = arg;
            securitypolicy.erase(0, strlen("-Djava.security.policy="));
        } else if (islauncher && !seen_main && 0 == strncmp("-", arg, 1)) {
            vmargs += " \"";
            vmargs += arg;
            vmargs += "\"";
        } else {
            seen_main = TRUE;
            f3args += " \"";
            f3args += arg;
            f3args += "\"";
        }
    }
    return (EXIT_SUCCESS);
}

int Configuration::fileExists(const std::string& path) {
    WIN32_FIND_DATA ffd;
    HANDLE hFind;

    hFind = FindFirstFile(path.c_str(), &ffd);
    if (hFind == INVALID_HANDLE_VALUE) 
    {
        return FALSE;
    }
    else 
    {
        FindClose(hFind);
        return (ffd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) == 0;
    }
}
