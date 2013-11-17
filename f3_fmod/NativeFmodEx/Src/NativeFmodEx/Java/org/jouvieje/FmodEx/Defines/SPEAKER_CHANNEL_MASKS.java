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
 * Microsoft speaker channel mask, as defined for <code>WAVEFORMATEXTENSIBLE</code> and is found in <code>ksmedia.h</code>.<BR>
 * <BR>
 * Channel mask can be used to set the value of <code>FMOD_CODEC_WAVEFORMAT.channelmask</code>.
 */
public interface SPEAKER_CHANNEL_MASKS
{
	public final static int SPEAKER_FRONT_LEFT = 0x1;
	public final static int SPEAKER_FRONT_RIGHT = 0x2;
	public final static int SPEAKER_FRONT_CENTER = 0x4;
	public final static int SPEAKER_LOW_FREQUENCY = 0x8;
	public final static int SPEAKER_BACK_LEFT = 0x10;
	public final static int SPEAKER_BACK_RIGHT = 0x20;
	public final static int SPEAKER_FRONT_LEFT_OF_CENTER = 0x40;
	public final static int SPEAKER_FRONT_RIGHT_OF_CENTER = 0x80;
	public final static int SPEAKER_BACK_CENTER = 0x100;
	public final static int SPEAKER_SIDE_LEFT = 0x200;
	public final static int SPEAKER_SIDE_RIGHT = 0x400;
	public final static int SPEAKER_TOP_CENTER = 0x800;
	public final static int SPEAKER_TOP_FRONT_LEFT = 0x1000;
	public final static int SPEAKER_TOP_FRONT_CENTER = 0x2000;
	public final static int SPEAKER_TOP_FRONT_RIGHT = 0x4000;
	public final static int SPEAKER_TOP_BACK_LEFT = 0x8000;
	public final static int SPEAKER_TOP_BACK_CENTER = 0x10000;
	public final static int SPEAKER_TOP_BACK_RIGHT = 0x20000;
}