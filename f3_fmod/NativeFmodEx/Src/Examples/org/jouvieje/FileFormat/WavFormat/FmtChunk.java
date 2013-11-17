//===============================================================================================
//CDDARIP.EXE
//Copyright (c), Firelight Technologies Pty, Ltd, 1999-2004.
//
//Use CDDA streaming to rip a CD track to a wav file
//===============================================================================================

/**
 * Class used by the Fmod sample : cddarip, record
 * 
 * @author Jérôme JOUVIE (Jouvieje)
 * 
 * WANT TO CONTACT ME ?
 * E-mail :
 * 		jerome.jouvie@gmail.com
 * My web sites :
 * 		http://topresult.tomato.co.uk/~jerome/
 * 		http://jerome.jouvie.free.fr/
 */

package org.jouvieje.FileFormat.WavFormat;

import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_INT;
import static org.jouvieje.FmodEx.Misc.BufferUtils.SIZEOF_SHORT;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.jouvieje.FmodEx.Misc.FileWriterUtils;

public class FmtChunk
{
	public final static int SIZEOF_FMT_CHUNK = RiffChunk.SIZEOF_RIFF_CHUNK + 4*SIZEOF_SHORT + 2*SIZEOF_INT;
	
	private RiffChunk chunk;
	private short wFormatTag;			/* format type */
	private short nChannels;			/* number of channels (i.e. mono, stereo...) */
	private int   nSamplesPerSec;		/* sample rate */
	private int   nAvgBytesPerSec;		/* for buffer estimation */
	private short nBlockAlign;			/* block size of data */
	private short wBitsPerSample;		/* number of bits per sample of mono data */
	
	public FmtChunk(RiffChunk chunk, short wFormatTag, short nChannels, int nSamplesPerSec,
			int nAvgBytesPerSec, short nBlockAlign, short wBitsPerSample)
	{
		this.chunk = chunk;
		this.wFormatTag = wFormatTag;
		this.nChannels = nChannels;
		this.nSamplesPerSec = nSamplesPerSec;
		this.nAvgBytesPerSec = nAvgBytesPerSec;
		this.nBlockAlign = nBlockAlign;
		this.wBitsPerSample = wBitsPerSample;
	}
	
	public RiffChunk getChunk()
	{
		return chunk;
	}
	public void setChunk(RiffChunk chunk)
	{
		this.chunk = chunk;
	}
	
	public int getNAvgBytesPerSec()
	{
		return nAvgBytesPerSec;
	}
	public void setNAvgBytesPerSec(int avgBytesPerSec)
	{
		nAvgBytesPerSec = avgBytesPerSec;
	}
	
	public short getNBlockAlign()
	{
		return nBlockAlign;
	}
	public void setNBlockAlign(short blockAlign)
	{
		nBlockAlign = blockAlign;
	}
	
	public short getNChannels()
	{
		return nChannels;
	}
	public void setNChannels(short channels)
	{
		nChannels = channels;
	}
	
	public int getNSamplesPerSec()
	{
		return nSamplesPerSec;
	}
	public void setNSamplesPerSec(int samplesPerSec)
	{
		nSamplesPerSec = samplesPerSec;
	}
	
	public short getWBitsPerSample()
	{
		return wBitsPerSample;
	}
	public void setWBitsPerSample(short bitsPerSample)
	{
		wBitsPerSample = bitsPerSample;
	}
	
	public short getWFormatTag()
	{
		return wFormatTag;
	}
	public void setWFormatTag(short formatTag)
	{
		wFormatTag = formatTag;
	}
	
	/**
	 * Write an <Code>FmtChunk</code> object into a file.<BR>
	 * Call this methods after <code>WavHearder.writeWavHeader(...)</code>
	 * @param file a file to write in.
	 * @param fmtChunk an <code>FmtChunk</code> object.
	 * @see WavHeader#writeWavHeader(RandomAccessFile, WavHeader)
	 */
	public static void writeFmtChunk(RandomAccessFile file, FmtChunk fmtChunk) throws IOException
	{
		RiffChunk.writeRiffChunk(file, fmtChunk.getChunk());
		FileWriterUtils.writeShort(file, fmtChunk.getWFormatTag());
		FileWriterUtils.writeShort(file, fmtChunk.getNChannels());
		FileWriterUtils.writeInt(file, fmtChunk.getNSamplesPerSec());
		FileWriterUtils.writeInt(file, fmtChunk.getNAvgBytesPerSec());
		FileWriterUtils.writeShort(file, fmtChunk.getNBlockAlign());
		FileWriterUtils.writeShort(file, fmtChunk.getWBitsPerSample());
	}
	public static void putFmtChunk(ByteBuffer buffer, FmtChunk fmtChunk)
	{
		RiffChunk.putRiffChunk(buffer, fmtChunk.getChunk());
		buffer.putShort(fmtChunk.getWFormatTag());
		buffer.putShort(fmtChunk.getNChannels());
		buffer.putInt(fmtChunk.getNSamplesPerSec());
		buffer.putInt(fmtChunk.getNAvgBytesPerSec());
		buffer.putShort(fmtChunk.getNBlockAlign());
		buffer.putShort(fmtChunk.getWBitsPerSample());
	}
}