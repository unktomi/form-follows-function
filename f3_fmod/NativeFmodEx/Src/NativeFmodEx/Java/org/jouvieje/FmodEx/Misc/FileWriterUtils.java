/**
 * 							NativeFmodEx Project
 *
 * Do you want to use FMOD Ex API (www.fmod.org) with the Java language ? I've created NativeFmodEx for you.
 * Copyright © 2005 Jérôme JOUVIE (Jouvieje)
 *
 * Created on 23 feb. 2005
 * @version file v1.0.0
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * My web sites :
 * 		http://jerome.jouvie.free.fr/
 * 		http://topresult.tomato.co.uk/~jerome/
 * 
 * 
 * INTRODUCTION
 * FMOD Ex is an API (Application Programming Interface) that allow you to use music
 * and creating sound effects with a lot of sort of musics.
 * FMOD is at :
 * 		http://www.fmod.org/
 * The reason of this project is that FMOD Ex can't be used direcly with Java, so I've created
 * this project to do this.
 * 
 * 
 * GNU LESSER GENERAL PUBLIC LICENSE
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA 
 */

package org.jouvieje.FmodEx.Misc;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.jouvieje.FmodEx.Misc.BufferUtils;

/**
 * This class allow you to write some datas into a <code>RandomAccessFile</code> object.
 */
public class FileWriterUtils implements SizeOfPrimitive
{
	private static ByteBuffer shortBuffer = null;
	private static ByteBuffer charBuffer = null;
	private static ByteBuffer intBuffer = null;
	private static ByteBuffer longBuffer = null;
	private static ByteBuffer floatBuffer = null;
	private static ByteBuffer doubleBuffer = null;
	
	/**
	 * Write a simple <code>byte</code> into the file.
	 * @param file a file to read in.
	 * @param value a <code>byte</code> to be written into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static void writeByte(RandomAccessFile file, byte value) throws IOException
	{
		file.writeByte(value);
	}
	/**
	 * Convert the <code>short<code> in 2 bytes and write them into the file.
	 * @param file a file to read in.
	 * @param value a <code>short<code> to be written into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static void writeShort(RandomAccessFile file, short value) throws IOException
	{
		synchronized(getShortBuffer())
		{
			shortBuffer.putShort(0, value);
			file.getChannel().write(shortBuffer);
			shortBuffer.rewind();
		}
	}
	/**
	 * Convert the <code>char</code> in 2 bytes and write them into the file.
	 * @param file a file to read in.
	 * @param value a <code>char</code> to be writte into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static void writeChar(RandomAccessFile file, char value) throws IOException
	{
		synchronized(getCharBuffer())
		{
			charBuffer.putChar(0, value);
			file.getChannel().write(charBuffer);
			charBuffer.rewind();
		}
	}
	/**
	 * Convert the <code>int</code> in 4 bytes and write them into the file.
	 * @param file a file to read in.
	 * @param value an <code>int</code> to be written into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static void writeInt(RandomAccessFile file, int value) throws IOException
	{
		synchronized(getIntBuffer())
		{
			intBuffer.putInt(0, value);
			file.getChannel().write(intBuffer);
			intBuffer.rewind();
		}
	}
	/**
	 * Convert the <code>float</code> in 4 bytes and write them into the file.
	 * @param file a file to read in.
	 * @param value a <code>float</code> to be written into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static void writeFloat(RandomAccessFile file, float value) throws IOException
	{
		synchronized(getFloatBuffer())
		{
			floatBuffer.putFloat(0, value);
			file.getChannel().write(floatBuffer);
			floatBuffer.rewind();
		}
	}
	/**
	 * Convert the <code>long</code> in 8 bytes and write them into the file.
	 * @param file a file to read in.
	 * @param value a <code>long</code> to be written into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static void writeLong(RandomAccessFile file, long value) throws IOException
	{
		synchronized(getLongBuffer())
		{
			longBuffer.putLong(0, value);
			file.getChannel().write(longBuffer);
			longBuffer.rewind();
		}
	}
	/**
	 * Convert the <code>double</code> in 8 bytes and write them into the file.
	 * @param file a file to read in.
	 * @param value a <code>double</code> to be written into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static void writeDouble(RandomAccessFile file, double value) throws IOException
	{
		synchronized(getDoubleBuffer())
		{
			doubleBuffer.putDouble(0, value);
			file.getChannel().write(doubleBuffer);
			doubleBuffer.rewind();
		}
	}
	
	/**
	 * Write an entire <code>byte[]</code> into the file.<BR>
	 * This is equivalent to the call <code>writeByteArray(file, datase, 0, datas.length)</code>
	 * @param file a file to read in.
	 * @param datas a <code>byte[]</code> to be written into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static void writeByteArray(RandomAccessFile file, byte[] datas) throws IOException
	{
		writeByteArray(file, datas, 0, datas.length);
	}
	/**
	 * Write a part of a <code>byte[]</code> into the file.
	 * @param file a file to read in.
	 * @param datas a <code>byte[]</code> to write into the file.
	 * @param offset offset from the start of the <code>byte[]</code>.
	 * @param length number of bytes to be written starting from the offset of the <code>byte[]</code>.
	 * @throws IOException if an I/O exception occures.
	 */
	public static void writeByteArray(RandomAccessFile file, byte[] datas, int offset, int length) throws IOException
	{
		file.write(datas, offset, length);
	}
	
	/**
	 * Write an entire <code>ByteBuffer</code> into a file.<BR>
	 * @param file a file to read in.
	 * @param buffer a <code>ByteBuffer</code> to be written into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static int writeByteBuffer(RandomAccessFile file, ByteBuffer buffer) throws IOException
	{
		return file.getChannel().write(buffer);
	}
	/**
	 * Write a part of a <code>ByteBuffer</code> into a file.
	 * @param file a file to read in.
	 * @param buffer a <code>ByteBuffer</code> to be written into the file.
	 * @param length number of bytes to be written into the file.
	 * @throws IOException if an I/O exception occures.
	 */
	public static int writeByteBuffer(RandomAccessFile file, ByteBuffer buffer, int length) throws IOException
	{
		ByteBuffer view = buffer.duplicate();
		view.limit(view.position()+length);
		int written = file.getChannel().write(view);
		buffer.position(view.position());
		return written;
	}
	
					/*PRIVATE*/
	
	private static ByteBuffer getShortBuffer()
	{
		if(shortBuffer == null)
			shortBuffer = BufferUtils.newByteBuffer(SIZEOF_SHORT);
		return shortBuffer;
	}
	private static ByteBuffer getCharBuffer()
	{
		if(charBuffer == null)
			charBuffer = BufferUtils.newByteBuffer(SIZEOF_CHAR);
		return charBuffer;
	}
	private static ByteBuffer getIntBuffer()
	{
		if(intBuffer == null)
			intBuffer = BufferUtils.newByteBuffer(SIZEOF_INT);
		return intBuffer;
	}
	private static ByteBuffer getLongBuffer()
	{
		if(longBuffer == null)
			longBuffer = BufferUtils.newByteBuffer(SIZEOF_LONG);
		return longBuffer;
	}
	private static ByteBuffer getFloatBuffer()
	{
		if(floatBuffer == null)
			floatBuffer = BufferUtils.newByteBuffer(SIZEOF_FLOAT);
		return floatBuffer;
	}
	private static ByteBuffer getDoubleBuffer()
	{
		if(doubleBuffer == null)
			doubleBuffer = BufferUtils.newByteBuffer(SIZEOF_DOUBLE);
		return doubleBuffer;
	}
}