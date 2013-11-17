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

package org.jouvieje.FmodEx.Defines;

public interface INIT_MODES
{
	/** Default initializing mode.<BR>This is equivalent to INIT_FMOD_EX*/
	public final static int INIT_DEFAULT = 0;
	/** Init mode for loading all things requiered by FMOD Ex.*/
	public final static int INIT_FMOD_EX = 1;
	/** Init mode for loading all things requiered by FMOD Ex & FMOD Designer.*/
	public final static int INIT_FMOD_DESIGNER = 2;
	/** Init mode for loading the minimum requiered by FMOD Ex.<BR><U>Remark :</U> Choose this mode only if you don't need Callbacks.*/
	public final static int INIT_FMOD_EX_MINIMUM = 3;
	/** Init mode for loading the minimum requiered by FMOD Ex & FMOD Designer.<BR><U>Remark :</U> Choose this mode only if you don't need Callbacks.*/
	public final static int INIT_FMOD_DESIGNER_MINIMUM = 4;
}