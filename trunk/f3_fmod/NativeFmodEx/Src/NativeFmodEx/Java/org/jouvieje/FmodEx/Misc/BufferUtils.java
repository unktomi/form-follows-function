/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */

package org.jouvieje.FmodEx.Misc;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

/**
 * Utility routines for dealing with direct buffers.<BR>
 * <BR>
 * <U><B>ABOUT THIS CLASS</B></U><BR>
 * <BR>
 * This class is based on <B><code>BufferUtils</code></B> of <B><A HREF="https://jogl.dev.java.net/">JOGL</A></B>
 * from the package <code>net.java.games.jogl.util</code><BR>
 * I've added in it few others routines.<BR>
 * <BR>
 * <BR>
 * Here is the copyright related to this file :<BR>
 * <P>
 * <pre>Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MIDROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.</pre>
 * </P>
 */
public class BufferUtils implements SizeOfPrimitive
{
	/**
	 * Allocate a new direct buffer
	 * @param nbElements size of the ByteBuffer in bytes.
	 * @return new direct buffer that holds nbElements bytes.
	 */
	public static ByteBuffer newByteBuffer(int nbElements)
	{
		ByteBuffer buffer = ByteBuffer.allocateDirect(nbElements);
		buffer.order(ByteOrder.nativeOrder());
		return buffer;
	}
	/**
	 * Allocate a new direct buffer
	 * @param nbElements size of the ByteBuffer in chars.
	 * @return new direct buffer that holds nbElements chars.
	 */
	public static CharBuffer newCharBuffer(int nbElements)
	{
		ByteBuffer buffer = newByteBuffer(nbElements*SIZEOF_CHAR);
		return buffer.asCharBuffer();
	}
	
	/**
	 * Allocate a new direct buffer
	 * @param nbElements size of the ByteBuffer in shorts.
	 * @return new direct buffer that holds nbElements shorts.
	 */
	public static ShortBuffer newShortBuffer(int nbElements)
	{
		ByteBuffer buffer = newByteBuffer(nbElements*SIZEOF_SHORT);
		return buffer.asShortBuffer();
	}
	/**
	 * Allocate a new direct buffer
	 * @param nbElements size of the ByteBuffer in ints.
	 * @return new direct buffer that holds nbElements bytes.
	 */
	public static IntBuffer newIntBuffer(int nbElements)
	{
		ByteBuffer buffer = newByteBuffer(nbElements*SIZEOF_INT);
		return buffer.asIntBuffer();
	}
	/**
	 * Allocate a new direct buffer
	 * @param nbElements size of the ByteBuffer in longs.
	 * @return new direct buffer that holds nbElements longs.
	 */
	public static LongBuffer newLongBuffer(int nbElements)
	{
		ByteBuffer buffer = newByteBuffer(nbElements*SIZEOF_LONG);
		return buffer.asLongBuffer();
	}
	/**
	 * Allocate a new direct buffer
	 * @param nbElements size of the ByteBuffer in floats.
	 * @return new direct buffer that holds nbElements floats.
	 */
	public static FloatBuffer newFloatBuffer(int nbElements)
	{
		ByteBuffer buffer = newByteBuffer(nbElements*SIZEOF_FLOAT);
		return buffer.asFloatBuffer();
	}
	/**
	 * Allocate a new direct buffer
	 * @param nbElements size of the ByteBuffer in doubles.
	 * @return new direct buffer that holds nbElements doubles.
	 */
	public static DoubleBuffer newDoubleBuffer(int nbElements)
	{
		ByteBuffer buffer = newByteBuffer(nbElements*SIZEOF_DOUBLE);
		return buffer.asDoubleBuffer();
	}
	
	/**
	 * Make a copy of an existing buffer (src) to a new buffer (dest)
	 * @param src existing buffer to make a copy
	 * @return the new buffer copied
	 */
	public static ByteBuffer copyByteBuffer(ByteBuffer src)
	{
		ByteBuffer dest = newByteBuffer(src.capacity());
		src.rewind();
		dest.put(src);
		return dest;
	}
	/**
	 * Make a copy of an existing buffer (src) to a new buffer (dest)
	 * @param src existing buffer to make a copy
	 * @return the new buffer copied
	 */
	public static ShortBuffer copyShortBuffer(ShortBuffer src)
	{
		ShortBuffer dest = newShortBuffer(src.capacity());
		src.rewind();
		dest.put(src);
		return dest;
	}
	/**
	 * Make a copy of an existing buffer (src) to a new buffer (dest)
	 * @param src existing buffer to make a copy
	 * @return the new buffer copied
	 */
	public static IntBuffer copyIntBuffer(IntBuffer src)
	{
		IntBuffer dest = newIntBuffer(src.capacity());
		src.rewind();
		dest.put(src);
		return dest;
	}
	/**
	 * Make a copy of an existing buffer (src) to a new buffer (dest)
	 * @param src existing buffer to make a copy
	 * @return the new buffer copied
	 */
	public static LongBuffer copyLongBuffer(LongBuffer src)
	{
		LongBuffer dest = newLongBuffer(src.capacity());
		src.rewind();
		dest.put(src);
		return dest;
	}
	/**
	 * Make a copy of an existing buffer (src) to a new buffer (dest)
	 * @param src existing buffer to make a copy
	 * @return the new buffer copied
	 */
	public static FloatBuffer copyFloatBuffer(FloatBuffer src)
	{
		FloatBuffer dest = newFloatBuffer(src.capacity());
		src.rewind();
		dest.put(src);
		return dest;
	}
	/**
	 * Make a copy of an existing buffer (src) to a new buffer (dest)
	 * @param src existing buffer to make a copy
	 * @return the new buffer copied
	 */
	public static DoubleBuffer copyDoubleBuffer(DoubleBuffer src)
	{
		DoubleBuffer dest = newDoubleBuffer(src.capacity());
		src.rewind();
		dest.put(src);
		return dest;
	}
	
	/**
	 * @param buffer a buffer
	 * @return the capacity in byts of the buffer.<BR>
	 * -1 if the buffer type is unknow
	 */
	public static int getCapacityInBytes(Buffer buffer)
	{
		if(buffer instanceof ByteBuffer)
			return buffer.capacity();
		if(buffer instanceof CharBuffer)
			return buffer.capacity()*SIZEOF_CHAR;
		if(buffer instanceof ShortBuffer)
			return buffer.capacity()*SIZEOF_SHORT;
		if(buffer instanceof IntBuffer)
			return buffer.capacity()*SIZEOF_INT;
		if(buffer instanceof LongBuffer)
			return buffer.capacity()*SIZEOF_LONG;
		if(buffer instanceof FloatBuffer)
			return buffer.capacity()*SIZEOF_FLOAT;
		if(buffer instanceof DoubleBuffer)
			return buffer.capacity()*SIZEOF_DOUBLE;
		else
			return -1;
	}
	/**
	 * @param buffer a buffer
	 * @return the capacity in byts of the buffer.<BR>
	 * -1 if the buffer type is unknow
	 */
	public static int getPositionInBytes(Buffer buffer)
	{
		if(buffer != null)
		{
			if(buffer instanceof ByteBuffer)
				return buffer.position();
			if(buffer instanceof CharBuffer)
				return buffer.position()*SIZEOF_CHAR;
			if(buffer instanceof ShortBuffer)
				return buffer.position()*SIZEOF_SHORT;
			if(buffer instanceof IntBuffer)
				return buffer.position()*SIZEOF_INT;
			if(buffer instanceof LongBuffer)
				return buffer.position()*SIZEOF_LONG;
			if(buffer instanceof FloatBuffer)
				return buffer.position()*SIZEOF_FLOAT;
			if(buffer instanceof DoubleBuffer)
				return buffer.position()*SIZEOF_DOUBLE;
		}
		return -1;
	}
	
	/**
	 * Retrieve the <code>String</code> stored in the <code>Buffer</code> (Null terminated String).<BR>
	 * @param buffer a buffer that holds a <code>String</code>.
	 * @return the <code>String</code> holded by the <code>Buffer</code>.<BR>
	 * <code>null</code> if no <code>String</code> stored in the <code>Buffer</code>.
	 * @see #toString(ByteBuffer, int, int)
	 */
	public static String toString(ByteBuffer buffer)
	{
		long address = BufferUtilsJNI.getBufferAddress(buffer, getPositionInBytes(buffer));
		return (address == 0) ? null : PointerUtilsJNI.Pointer_toString(address);
	}
	/**
	 * Retrieve the <code>String</code> stored in the <code>Buffer</code>.
	 * @param buffer a buffer that holds a String.
	 * @param offset offset (in bytes) from the current position in the ByteBuffer.
	 * @param length length of the String to retrieve from the buffer.
	 * @return the string composed of the length first characters stored into the Buffer
	 * @see #toString(ByteBuffer)
	 */
	public static String toString(ByteBuffer buffer, int offset, int length)
	{
		byte[] bytes = new byte[length];
		int position = buffer.position();
		buffer.position(position+offset);
		buffer.get(bytes, 0, length);
		buffer.rewind();
		buffer.position(position);
		return new String(bytes);
	}
	
	/**
	 * Relative put operation.<BR>
	 * Write a <code>String</code> in a <code>ByteBuffer</code> object.<BR><BR>
	 * In some case, you also need to put a NULL terminal character at the end of the string. For this use <code>putNullTerminalCharacter</code>.
	 * @param buffer a destination buffer for the String.
	 * @param s a String to copy in the Buffer
	 * @see #putNullTerminal(ByteBuffer)
	 */
	public static void putString(ByteBuffer buffer, String s)
	{
		buffer.put(s.getBytes());
	}
	/**
	 * Relative put operation.<BR>
	 * Write a <i>NULL terminal character</i> in a <code>ByteBuffer</code> object.
	 * @param buffer a destination buffer for the String.
	 */
	public static void putNullTerminal(ByteBuffer buffer)
	{
		BufferUtilsJNI.writeNullTerminal(buffer, getPositionInBytes(buffer));
	}
	
	/**
	 * Create a view of the <code>Buffer</code> object as a <code>Pointer</code> object.<br>
	 * @param buffer a <B>direct</B> <code>ByteBuffer</code>
	 * @return the Pointer view.
	 */
	public static Pointer createView(Buffer buffer)
	{
		long address = BufferUtilsJNI.getBufferAddress(buffer, getPositionInBytes(buffer));
		return (address == 0) ? null : new Pointer(address);
	}
}