
package org.jouvieje.FileSystem;

import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_FILE_EOF;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_FILE_NOTFOUND;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_INVALID_PARAM;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.jouvieje.FmodEx.Callbacks.FMOD_FILE_CLOSECALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_FILE_OPENCALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_FILE_READCALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_FILE_SEEKCALLBACK;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Examples.Utils.Medias;
import org.jouvieje.FmodEx.Misc.ObjectPointer;
import org.jouvieje.FmodEx.Misc.Pointer;

/**
 * Copyright © 2008 Jérôme JOUVIE (Jouvieje)
 * @author    Jérôme JOUVIE (Jouvieje)
 * @email     jerome.jouvie@gmail.com
 * @website   http://jerome.jouvie.free.fr/
 * =========================================
 * You can use this file with no restriction,
 * as long as you keep this comment unmodified.
 * =========================================
 */
public class JARFileSystem
{
	public final String rootDirectory;
	
	public JARFileSystem(String fileSystemRootDirectory)
	{
		rootDirectory = fileSystemRootDirectory == null ? "" : fileSystemRootDirectory;
	}
	
	public final FMOD_FILE_OPENCALLBACK jarOpen = new FMOD_FILE_OPENCALLBACK(){
		public FMOD_RESULT FMOD_FILE_OPENCALLBACK(String name, int unicode, IntBuffer filesize, Pointer handle, Pointer userdata)
		{
			if(name == null) {
				return FMOD_ERR_FILE_NOTFOUND;
			}
			
//			System.out.println("["+JARFileSystem.class.getSimpleName()+"] Open: "+name);
			ByteBuffer filBuffer = Medias.loadMediaIntoMemory(rootDirectory+name/*String.format("%s%s", rootDirectory, name)*/);
			if(filBuffer == null)  {
				return FMOD_ERR_FILE_NOTFOUND;
			}
			
			filesize.put(0, filBuffer.capacity());
			handle.shareMemory(ObjectPointer.create(filBuffer));
			
			return FMOD_OK;
		}
	};

	public FMOD_FILE_CLOSECALLBACK jarClose = new FMOD_FILE_CLOSECALLBACK(){
		public FMOD_RESULT FMOD_FILE_CLOSECALLBACK(Pointer handle, Pointer userdata)
		{
			if(handle == null || handle.isNull()) {
				return FMOD_ERR_INVALID_PARAM;
			}
			
			ObjectPointer objectPointer = ObjectPointer.createView(handle);
//			ByteBuffer fileBuff = (ByteBuffer)objectPointer.getObject();
			objectPointer.release();
			
//			fileBuff = null;
//			objectPointer = null;
			
			return FMOD_OK;
		}
	};
	
	public FMOD_FILE_READCALLBACK jarRead = new FMOD_FILE_READCALLBACK(){
		public FMOD_RESULT FMOD_FILE_READCALLBACK(Pointer handle, ByteBuffer buffer, int sizebytes, IntBuffer bytesread, Pointer userdata)
		{
			if(handle == null || handle.isNull()) {
				return FMOD_ERR_INVALID_PARAM;
			}
			
			ByteBuffer file = (ByteBuffer)ObjectPointer.createView(handle).getObject();
			ByteBuffer fileChunk = file.duplicate();
			
			//Check the number of bytes to read
			int maxBytes = file.capacity() - fileChunk.position();
			int bytesToRead = (sizebytes > maxBytes) ? maxBytes : sizebytes;
			
			//Read bytesToRead bytes
			fileChunk.limit(fileChunk.position()+bytesToRead);
			if(fileChunk.remaining() != sizebytes) {
				return FMOD_ERR_FILE_EOF;
			}
			buffer.put(fileChunk);
			
			//Move the file pointer of the number of bytes actually read
			file.position(file.position()+bytesToRead);
			bytesread.put(0, bytesToRead);
			
			return FMOD_OK;
		}
	};
	
	public FMOD_FILE_SEEKCALLBACK jarSeek = new FMOD_FILE_SEEKCALLBACK(){
		public FMOD_RESULT FMOD_FILE_SEEKCALLBACK(Pointer handle, int pos, Pointer userdata)
		{
			if(handle == null || handle.isNull()) {
				return FMOD_ERR_INVALID_PARAM;
			}
			
			ByteBuffer file = (ByteBuffer)ObjectPointer.createView(handle).getObject();
			if(pos < 0 || pos > file.capacity()) {
				return FMOD_ERR_FILE_EOF;
			}
			file.position(pos);
			
			return FMOD_OK;
		}
	};
}
