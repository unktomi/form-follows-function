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

#ifndef _CONFIGURATION_H
#define	_CONFIGURATION_H

#include <string>

class Configuration {
public:
    std::string javacmd;
    std::string f3cmd;
    std::string f3path;
    std::string classpath;
    std::string vmargs;
    std::string f3args;
    std::string librarypath;
    std::string securitypolicy;
    
    std::string profile_classpath;
    std::string profile_bootclasspath;
    std::string profile_bootclasspath_prepend;
    std::string profile_bootclasspath_append;
    std::string profile_nativelibpath;
    std::string profile_bootnativelibpath;
    std::string profile_vmargs;
    std::string profile_security_policy;
    std::string profile_emulator;
    std::string profile_compile_opts;
    bool        is_desktop() { return device_profile == "desktop"; };
    bool        is_mobile()  { return device_profile == "mobile" ; };
    
    Configuration(const std::string& prefix);
    ~Configuration();
    
    int initConfiguration (int argc, char** argv);
    
private:
    void init();
    int readConfigFile();
    int parseArgs(int argc, char** argv);
    int fileExists(const std::string& path);
    
    std::string prefix;
    std::string profile_filename;
    std::string device_profile;
    std::string profile_toolkit;
};

#endif	/* _CONFIGURATION_H */

