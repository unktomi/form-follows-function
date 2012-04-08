/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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
package com.sun.tools.renamer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;

/**
 *
 * @author ksrini
 */
public class RenamedEntry extends ZipEntry {

    private CheckedOutputStream cos = null;
    private ByteArrayOutputStream baos = null;
    static final CRC32 crc = new CRC32();

    private void initOutputStream() {
        crc.reset();
        baos = new ByteArrayOutputStream();
        cos = new CheckedOutputStream(baos, crc);
    }

    RenamedEntry(String name) {
        super(name);
        initOutputStream();
    }

    RenamedEntry(ZipEntry ze) {
        super(ze);
        initOutputStream();
    }

    RenamedEntry(ZipEntry ze, InputStream in) throws IOException {
        super(ze);
        initOutputStream();
        byte[] buf = new byte[8192];
        int n = in.read(buf);
        while (n > 0) {
            cos.write(buf, 0, n);
            n = in.read(buf);
        }
    }
    
    OutputStream getOutputStream() {
        return cos;
    }

    void finish() throws IOException {
        cos.flush();
        baos.flush();
        this.setCrc(crc.getValue());
        this.setSize(baos.size());
        this.setCompressedSize(-1);
    }

    byte[] getByteArray() {
        if (baos != null) {
                return baos.toByteArray();
        }
        return null;
    }
}
