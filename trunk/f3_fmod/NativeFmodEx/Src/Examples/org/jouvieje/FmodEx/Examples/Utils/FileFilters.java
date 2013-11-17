
package org.jouvieje.FmodEx.Examples.Utils;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class FileFilters
{
	public final static FileFilter wavFile = new FileFilter(){
		public String getDescription()
		{
			return "WAV files";
		}
		public boolean accept(File f)
		{
			return f.isDirectory() || extensionOf(f.getName()).equals("wav");
		}
	};

	public final static FileFilter streamableFiles = new FileFilter(){
		private final static String extensions = "mp2;mp3;wav;ogg;wma;asf";
		public String getDescription()
		{
			return "Streamable files (mp2/mp3/wav/ogg/wma/asf)";
		}
		public boolean accept(File f)
		{
			return f.isDirectory() || (extensions.indexOf(extensionOf(f.getName())) != -1);
		}
	};
	
//	public final static FileFilter modMusicFiles = new FileFilter(){
//		private final static String extensions = "mo3;xm;mod;s3m;it;mtm;umx";
//		public String getDescription()
//		{
//			return "MOD music files (mo3/xm/mod/s3m/it/mtm/umx)";
//		}
//		public boolean accept(File f)
//		{
//			return f.isDirectory() || extensions.contains(extensionOf(f.getName()));
//		}
//	};
//	
//	public final static FileFilter sampleFiles = new FileFilter(){
//		private final static String extensions = "wav;aif";
//		public String getDescription()
//		{
//			return "Sample files (wav/aif)";
//		}
//		public boolean accept(File f)
//		{
//			return f.isDirectory() || extensions.contains(extensionOf(f.getName()));
//		}
//	};
//	
//	public final static FileFilter playableFiles = new FileFilter(){
//		private final static String extensions = "mo3;xm;mod;s3m;it;mtm;umx;mp3;mp2;mp1;ogg;wav;aif";
//		public String getDescription()
//		{
//			return "Playable files";
//		}
//		public boolean accept(File f)
//		{
//			return f.isDirectory() || extensions.contains(extensionOf(f.getName()));
//		}
//	};
	
	public final static FileFilter allFiles = new FileFilter(){
		public String getDescription()
		{
			return "All files";
		}
		public boolean accept(File f)
		{
			return true;
		}
	};
	
	private static String extensionOf(String file)
	{
		int index = file.lastIndexOf(".");
		if(index != -1) return file.substring(index+1).toLowerCase();
		return "";
	}
}