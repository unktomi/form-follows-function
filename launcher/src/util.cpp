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

#include "util.h"

Util::Util() {
}

Util::~Util() {
}

std::string Util::evaluatePath (const std::string& f3path, const std::string& libs) {
    std::string result = libs;
    std::string::size_type start=0;
    while ((start = result.find("${f3_home}", start)) != std::string::npos) {
        result.replace (start, 14 /* length of ${f3_home} */, f3path);
        start += 14;
    }
    start=0;
    while ((start = result.find("/", start)) != std::string::npos) {
        result.replace (start, 1, "\\");
    }
    return result;
}

int Util::createProcess(const std::string& cmd) {
    STARTUPINFO start;
    PROCESS_INFORMATION pi;

    memset (&start, 0, sizeof (start));
    start.cb = sizeof (start);

    bool ldebug = getenv("_F3_LAUNCHER_DEBUG") != NULL;
    #ifdef DEBUG
    ldebug = true;
    #endif
    if (ldebug == true) {
        printf("Cmdline: %s\n", cmd.c_str());
    }
    if (!CreateProcess (NULL, (char*)cmd.c_str(),
                        NULL, NULL, TRUE, NORMAL_PRIORITY_CLASS,
                        NULL, 
                        NULL, // lpCurrentDirectory
                        &start,
                        &pi)) {
        fprintf (stderr, "Cannot start java.exe.");
        return EXIT_FAILURE;
    } else {
        // Wait until child process exits.
        WaitForSingleObject( pi.hProcess, INFINITE );
        unsigned long exitCode;
        GetExitCodeProcess(pi.hProcess, &exitCode);

        // Close process and thread handles. 
        CloseHandle( pi.hProcess );
        CloseHandle( pi.hThread );        
        return exitCode;
    }
    
}
