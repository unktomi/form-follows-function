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

/**
 * A set of predefined environment PARAMETERS, created by Creative Labs<BR>
 * These are used to initialize an FMOD_REVERB_PROPERTIES structure.<BR>
 * ie<BR>
 * <pre><code>  FMOD_REVERB_PROPERTIES prop = FMOD_REVERB_PROPERTIES.create(FMOD_PRESET_GENERIC);</code></pre>
 */
public interface FMOD_REVERB_PRESETS
{
	public final static int FMOD_PRESET_OFF = 1;
	public final static int FMOD_PRESET_GENERIC = 2;
	public final static int FMOD_PRESET_PADDEDCELL = 3;
	public final static int FMOD_PRESET_ROOM = 4;
	public final static int FMOD_PRESET_BATHROOM = 5;
	public final static int FMOD_PRESET_LIVINGROOM = 6;
	public final static int FMOD_PRESET_STONEROOM = 7;
	public final static int FMOD_PRESET_AUDITORIUM = 8;
	public final static int FMOD_PRESET_CONCERTHALL = 9;
	public final static int FMOD_PRESET_CAVE = 10;
	public final static int FMOD_PRESET_ARENA = 11;
	public final static int FMOD_PRESET_HANGAR = 12;
	public final static int FMOD_PRESET_CARPETTEDHALLWAY = 13;
	public final static int FMOD_PRESET_HALLWAY = 14;
	public final static int FMOD_PRESET_STONECORRIDOR = 15;
	public final static int FMOD_PRESET_ALLEY = 16;
	public final static int FMOD_PRESET_FOREST = 17;
	public final static int FMOD_PRESET_CITY = 18;
	public final static int FMOD_PRESET_MOUNTAINS = 19;
	public final static int FMOD_PRESET_QUARRY = 20;
	public final static int FMOD_PRESET_PLAIN = 21;
	public final static int FMOD_PRESET_PARKINGLOT = 22;
	public final static int FMOD_PRESET_SEWERPIPE = 23;
	public final static int FMOD_PRESET_UNDERWATER = 24;
	public final static int FMOD_PRESET_DRUGGED = 25;
	public final static int FMOD_PRESET_DIZZY = 26;
	public final static int FMOD_PRESET_PSYCHOTIC = 27;
	public final static int FMOD_PRESET_PS2_ROOM = 28;
	public final static int FMOD_PRESET_PS2_STUDIO_A = 29;
	public final static int FMOD_PRESET_PS2_STUDIO_B = 30;
	public final static int FMOD_PRESET_PS2_STUDIO_C = 31;
	public final static int FMOD_PRESET_PS2_HALL = 32;
	public final static int FMOD_PRESET_PS2_SPACE = 33;
	public final static int FMOD_PRESET_PS2_ECHO = 34;
	public final static int FMOD_PRESET_PS2_DELAY = 35;
	public final static int FMOD_PRESET_PS2_PIPE = 36;
}