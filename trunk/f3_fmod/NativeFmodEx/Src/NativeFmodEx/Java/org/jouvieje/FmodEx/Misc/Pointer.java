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

/**
 * A <code>Pointer</code> object is used to holds a part of the memory.<BR>
 * This object is used to store any kind of objects. For example, we can store an array, a Java Object, an FMOD Ex object ...<BR>
 * <BR>
 * <B><U>ACCESSING VALUE</U></B><BR>
 * To access the value store in the memory, you need to know which kind of object is holded.
 * Then, you need to creates an appropriate view of the memory block holded.<BR>
 * In this example, the <code>Pointer</code> object holds a <code>String</code> :<BR>
 * <code><pre>
 * Pointer pointer = ...;   //Object already created that store a String
 * String string = PointerUtils.toString(pointer);
 * </pre></code>
 * The object can also store an FMOD Ex object. For example a Sound :
 * <code><pre>
 * Pointer pointer = ...;   //Object already created that store a Sound
 * Sound sound = Sound.createView(pointer);
 * </pre></code>
 * <B><U>Warning :</B></U><BR>
 * If you create and use an invalide view , the JVM (Java Virtual Machine) can crash without any warning !<BR>
 */
public class Pointer
{
	protected long pointer;
	
	/**
	 * Default constructor.<BR>
	 * No memory is owned, the object holded is null.
	 * The call <code>isNull()</code> will return <code>true</code>.
	 */
	public Pointer()
	{
		this.pointer = 0;
	}
	
	/**
	 * @param pointer address pointer in the memory.
	 */
	protected Pointer(long pointer)
	{
		this.pointer = pointer;
	}
	
	/**
	 * This should only be used if you are sure that this object contains a String value.<BR>
	 * This call :<BR>
	 * <pre><code>   PointerUtils.toString(this)</code></pre>
	 * @return String holded by the object.<BR>
	 * Return null if <code>this.isNull()</code> equals true.
	 */
	public String asString()
	{
		return PointerUtils.toString(this);
	}
	
	public int asInt()
	{
		if(pointer != 0)
			return PointerUtilsJNI.asInt(pointer);
		return 0;
	}
	public long asLong()
	{
		return pointer;
	}
	public float asFloat()
	{
		if(pointer != 0)
			return PointerUtilsJNI.asFloat(pointer);
		return 0;
	}
	public double asDouble()
	{
		if(pointer != 0)
			return PointerUtilsJNI.asDouble(pointer);
		return 0;
	}

	/**
	 * Use to know if two object are equals.<BR>
	 * Do NOT use this :<BR>
	 * <code><pre>
	 * Sound sound1, sound2;
	 * ...
	 * if(sound1 == sound2)
	 * {
	 *     ...
	 * </pre></code>
	 * Use this :<BR>
	 * <code><pre>
	 * Sound sound1, sound2;
	 * ...
	 * if(sound1.equals(sound2))
	 * {
	 *     ...
	 * </pre></code>
	 */
	public boolean equals(Object object)
	{
		if(object instanceof Pointer)
			return pointer == Pointer.getPointer((Pointer)object);
		return false;
	}
	
	/**
	 * 
	 * @return true if no memory is holded (ie object holded in the memory is null).
	 */
	public boolean isNull()
	{
		return pointer == 0;
	}
	
	/**
	 * Share the same memory region that the <code>Pointer</code> object passed as parameter.<BR>
	 * @param source a <code>Pointer</code>.
	 */
	public void shareMemory(Pointer source)
	{
		this.pointer = getPointer(source);
	}
	
	protected static long getPointer(Pointer pointer)
	{
		if(pointer != null)
			return pointer.pointer;
		return 0;
	}
	
						/* JNI access*/
	
	protected static Pointer newPointer(long address)
	{
		return new Pointer(address);
	}
}