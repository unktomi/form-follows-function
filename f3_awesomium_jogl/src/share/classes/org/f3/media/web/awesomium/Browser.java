package org.f3.media.web.awesomium;
import f3.media.scene.AbstractWebBrowser;
import f3.media.scene.AbstractTexture;
import f3.media.input.Keys;
import f3.media.scene.CursorType;
import java.nio.*;
import com.jogamp.opengl.util.texture.*;
import javax.media.opengl.*;
import com.jogamp.opengl.util.*;
import java.util.*;

public class Browser implements AbstractWebBrowser {

    // AK_BACK (08) BACKSPACE key
    static final int AK_BACK = 0x08;
		
    // AK_TAB (09) TAB key
    static final int AK_TAB = 0x09;
		
    // AK_CLEAR (0C) CLEAR key
    static final int AK_CLEAR = 0x0C;
		
    // AK_RETURN (0D)
    static final int AK_RETURN = 0x0D;
		
    // AK_SHIFT (10) SHIFT key
    static final int AK_SHIFT = 0x10;
		
    // AK_CONTROL (11) CTRL key
    static final int AK_CONTROL = 0x11;
		
    // AK_MENU (12) ALT key
    static final int AK_MENU = 0x12;
		
    // AK_PAUSE (13) PAUSE key
    static final int AK_PAUSE = 0x13;
		
    // AK_CAPITAL (14) CAPS LOCK key
    static final int AK_CAPITAL = 0x14;
		
    // AK_KANA (15) Input Method Editor (IME) Kana mode
    static final int AK_KANA = 0x15;
		
    // AK_HANGUEL (15) IME Hanguel mode (maintained for compatibility; use AK_HANGUL)
    // AK_HANGUL (15) IME Hangul mode
    static final int AK_HANGUL = 0x15;
		
    // AK_JUNJA (17) IME Junja mode
    static final int AK_JUNJA = 0x17;
		
    // AK_FINAL (18) IME final mode
    static final int AK_FINAL = 0x18;
		
    // AK_HANJA (19) IME Hanja mode
    static final int AK_HANJA = 0x19;
		
    // AK_KANJI (19) IME Kanji mode
    static final int AK_KANJI = 0x19;
		
    // AK_ESCAPE (1B) ESC key
    static final int AK_ESCAPE = 0x1B;
		
    // AK_CONVERT (1C) IME convert
    static final int AK_CONVERT = 0x1C;
		
    // AK_NONCONVERT (1D) IME nonconvert
    static final int AK_NONCONVERT = 0x1D;
		
    // AK_ACCEPT (1E) IME accept
    static final int AK_ACCEPT = 0x1E;
		
    // AK_MODECHANGE (1F) IME mode change request
    static final int AK_MODECHANGE = 0x1F;
		
    // AK_SPACE (20) SPACEBAR
    static final int AK_SPACE = 0x20;
		
    // AK_PRIOR (21) PAGE UP key
    static final int AK_PRIOR = 0x21;
		
    // AK_NEXT (22) PAGE DOWN key
    static final int AK_NEXT = 0x22;
		
    // AK_END (23) END key
    static final int AK_END = 0x23;
		
    // AK_HOME (24) HOME key
    static final int AK_HOME = 0x24;
		
    // AK_LEFT (25) LEFT ARROW key
    static final int AK_LEFT = 0x25;
		
    // AK_UP (26) UP ARROW key
    static final int AK_UP = 0x26;
		
    // AK_RIGHT (27) RIGHT ARROW key
    static final int AK_RIGHT = 0x27;
		
    // AK_DOWN (28) DOWN ARROW key
    static final int AK_DOWN = 0x28;
		
    // AK_SELECT (29) SELECT key
    static final int AK_SELECT = 0x29;
		
    // AK_PRINT (2A) PRINT key
    static final int AK_PRINT = 0x2A;
		
    // AK_EXECUTE (2B) EXECUTE key
    static final int AK_EXECUTE = 0x2B;
		
    // AK_SNAPSHOT (2C) PRINT SCREEN key
    static final int AK_SNAPSHOT = 0x2C;
		
    // AK_INSERT (2D) INS key
    static final int AK_INSERT = 0x2D;
		
    // AK_DELETE (2E) DEL key
    static final int AK_DELETE = 0x2E;
		
    // AK_HELP (2F) HELP key
    static final int AK_HELP = 0x2F;
		
    // (30) 0 key
    static final int AK_0 = 0x30;
		
    // (31) 1 key
    static final int AK_1 = 0x31;
		
    // (32) 2 key
    static final int AK_2 = 0x32;
		
    // (33) 3 key
    static final int AK_3 = 0x33;
		
    // (34) 4 key
    static final int AK_4 = 0x34;
		
    // (35) 5 key;
    static final int AK_5 = 0x35;
		
    // (36) 6 key
    static final int AK_6 = 0x36;
		
    // (37) 7 key
    static final int AK_7 = 0x37;
		
    // (38) 8 key
    static final int AK_8 = 0x38;
		
    // (39) 9 key
    static final int AK_9 = 0x39;
		
    // (41) A key
    static final int AK_A = 0x41;
		
    // (42) B key
    static final int AK_B = 0x42;
		
    // (43) C key
    static final int AK_C = 0x43;
		
    // (44) D key
    static final int AK_D = 0x44;
		
    // (45) E key
    static final int AK_E = 0x45;
		
    // (46) F key
    static final int AK_F = 0x46;
		
    // (47) G key
    static final int AK_G = 0x47;
		
    // (48) H key
    static final int AK_H = 0x48;
		
    // (49) I key
    static final int AK_I = 0x49;
		
    // (4A) J key
    static final int AK_J = 0x4A;
		
    // (4B) K key
    static final int AK_K = 0x4B;
		
    // (4C) L key
    static final int AK_L = 0x4C;
		
    // (4D) M key
    static final int AK_M = 0x4D;
		
    // (4E) N key
    static final int AK_N = 0x4E;
		
    // (4F) O key
    static final int AK_O = 0x4F;
		
    // (50) P key
    static final int AK_P = 0x50;
		
    // (51) Q key
    static final int AK_Q = 0x51;
		
    // (52) R key
    static final int AK_R = 0x52;
		
    // (53) S key
    static final int AK_S = 0x53;
		
    // (54) T key
    static final int AK_T = 0x54;
		
    // (55) U key
    static final int AK_U = 0x55;
		
    // (56) V key
    static final int AK_V = 0x56;
		
    // (57) W key
    static final int AK_W = 0x57;
		
    // (58) X key
    static final int AK_X = 0x58;
		
    // (59) Y key
    static final int AK_Y = 0x59;
		
    // (5A) Z key
    static final int AK_Z = 0x5A;
		
    // AK_LWIN (5B) Left Windows key (Microsoft Natural keyboard)
    static final int AK_LWIN = 0x5B;
		
    // AK_RWIN (5C) Right Windows key (Natural keyboard)
    static final int AK_RWIN = 0x5C;
		
    // AK_APPS (5D) Applications key (Natural keyboard)
    static final int AK_APPS = 0x5D;
		
    // AK_SLEEP (5F) Computer Sleep key
    static final int AK_SLEEP = 0x5F;
		
    // AK_NUMPAD0 (60) Numeric keypad 0 key
    static final int AK_NUMPAD0 = 0x60;
		
    // AK_NUMPAD1 (61) Numeric keypad 1 key
    static final int AK_NUMPAD1 = 0x61;
		
    // AK_NUMPAD2 (62) Numeric keypad 2 key
    static final int AK_NUMPAD2 = 0x62;
		
    // AK_NUMPAD3 (63) Numeric keypad 3 key
    static final int AK_NUMPAD3 = 0x63;
		
    // AK_NUMPAD4 (64) Numeric keypad 4 key
    static final int AK_NUMPAD4 = 0x64;
		
    // AK_NUMPAD5 (65) Numeric keypad 5 key
    static final int AK_NUMPAD5 = 0x65;
		
    // AK_NUMPAD6 (66) Numeric keypad 6 key
    static final int AK_NUMPAD6 = 0x66;
		
    // AK_NUMPAD7 (67) Numeric keypad 7 key
    static final int AK_NUMPAD7 = 0x67;
		
    // AK_NUMPAD8 (68) Numeric keypad 8 key
    static final int AK_NUMPAD8 = 0x68;
		
    // AK_NUMPAD9 (69) Numeric keypad 9 key
    static final int AK_NUMPAD9 = 0x69;
		
    // AK_MULTIPLY (6A) Multiply key
    static final int AK_MULTIPLY = 0x6A;
		
    // AK_ADD (6B) Add key
    static final int AK_ADD = 0x6B;
		
    // AK_SEPARATOR (6C) Separator key
    static final int AK_SEPARATOR = 0x6C;
		
    // AK_SUBTRACT (6D) Subtract key
    static final int AK_SUBTRACT = 0x6D;
		
    // AK_DECIMAL (6E) Decimal key
    static final int AK_DECIMAL = 0x6E;
		
    // AK_DIVIDE (6F) Divide key
    static final int AK_DIVIDE = 0x6F;
		
    // AK_F1 (70) F1 key
    static final int AK_F1 = 0x70;
		
    // AK_F2 (71) F2 key
    static final int AK_F2 = 0x71;
		
    // AK_F3 (72) F3 key
    static final int AK_F3 = 0x72;
		
    // AK_F4 (73) F4 key
    static final int AK_F4 = 0x73;
		
    // AK_F5 (74) F5 key
    static final int AK_F5 = 0x74;
		
    // AK_F6 (75) F6 key
    static final int AK_F6 = 0x75;
		
    // AK_F7 (76) F7 key
    static final int AK_F7 = 0x76;
		
    // AK_F8 (77) F8 key
    static final int AK_F8 = 0x77;
		
    // AK_F9 (78) F9 key
    static final int AK_F9 = 0x78;
		
    // AK_F10 (79) F10 key
    static final int AK_F10 = 0x79;
		
    // AK_F11 (7A) F11 key
    static final int AK_F11 = 0x7A;
		
    // AK_F12 (7B) F12 key
    static final int AK_F12 = 0x7B;
		
    // AK_F13 (7C) F13 key
    static final int AK_F13 = 0x7C;
		
    // AK_F14 (7D) F14 key
    static final int AK_F14 = 0x7D;
		
    // AK_F15 (7E) F15 key
    static final int AK_F15 = 0x7E;
		
    // AK_F16 (7F) F16 key
    static final int AK_F16 = 0x7F;
		
    // AK_F17 (80H) F17 key
    static final int AK_F17 = 0x80;
		
    // AK_F18 (81H) F18 key
    static final int AK_F18 = 0x81;
		
    // AK_F19 (82H) F19 key
    static final int AK_F19 = 0x82;
		
    // AK_F20 (83H) F20 key
    static final int AK_F20 = 0x83;
		
    // AK_F21 (84H) F21 key
    static final int AK_F21 = 0x84;
		
    // AK_F22 (85H) F22 key
    static final int AK_F22 = 0x85;
		
    // AK_F23 (86H) F23 key
    static final int AK_F23 = 0x86;
		
    // AK_F24 (87H) F24 key
    static final int AK_F24 = 0x87;
		
    // AK_NUMLOCK (90) NUM LOCK key
    static final int AK_NUMLOCK = 0x90;
		
    // AK_SCROLL (91) SCROLL LOCK key
    static final int AK_SCROLL = 0x91;
		
    // AK_LSHIFT (A0) Left SHIFT key
    static final int AK_LSHIFT = 0xA0;
		
    // AK_RSHIFT (A1) Right SHIFT key
    static final int AK_RSHIFT = 0xA1;
		
    // AK_LCONTROL (A2) Left CONTROL key
    static final int AK_LCONTROL = 0xA2;
		
    // AK_RCONTROL (A3) Right CONTROL key
    static final int AK_RCONTROL = 0xA3;
		
    // AK_LMENU (A4) Left MENU key
    static final int AK_LMENU = 0xA4;
		
    // AK_RMENU (A5) Right MENU key
    static final int AK_RMENU = 0xA5;
		
    // AK_BROWSER_BACK (A6) Windows 2000/XP: Browser Back key
    static final int AK_BROWSER_BACK = 0xA6;
		
    // AK_BROWSER_FORWARD (A7) Windows 2000/XP: Browser Forward key
    static final int AK_BROWSER_FORWARD = 0xA7;
		
    // AK_BROWSER_REFRESH (A8) Windows 2000/XP: Browser Refresh key
    static final int AK_BROWSER_REFRESH = 0xA8;
		
    // AK_BROWSER_STOP (A9) Windows 2000/XP: Browser Stop key
    static final int AK_BROWSER_STOP = 0xA9;
		
    // AK_BROWSER_SEARCH (AA) Windows 2000/XP: Browser Search key
    static final int AK_BROWSER_SEARCH = 0xAA;
		
    // AK_BROWSER_FAVORITES (AB) Windows 2000/XP: Browser Favorites key
    static final int AK_BROWSER_FAVORITES = 0xAB;
		
    // AK_BROWSER_HOME (AC) Windows 2000/XP: Browser Start and Home key
    static final int AK_BROWSER_HOME = 0xAC;
		
    // AK_VOLUME_MUTE (AD) Windows 2000/XP: Volume Mute key
    static final int AK_VOLUME_MUTE = 0xAD;
		
    // AK_VOLUME_DOWN (AE) Windows 2000/XP: Volume Down key
    static final int AK_VOLUME_DOWN = 0xAE;
		
    // AK_VOLUME_UP (AF) Windows 2000/XP: Volume Up key
    static final int AK_VOLUME_UP = 0xAF;
		
    // AK_MEDIA_NEXT_TRACK (B0) Windows 2000/XP: Next Track key
    static final int AK_MEDIA_NEXT_TRACK = 0xB0;
		
    // AK_MEDIA_PREV_TRACK (B1) Windows 2000/XP: Previous Track key
    static final int AK_MEDIA_PREV_TRACK = 0xB1;
		
    // AK_MEDIA_STOP (B2) Windows 2000/XP: Stop Media key
    static final int AK_MEDIA_STOP = 0xB2;
		
    // AK_MEDIA_PLAY_PAUSE (B3) Windows 2000/XP: Play/Pause Media key
    static final int AK_MEDIA_PLAY_PAUSE = 0xB3;
		
    // AK_LAUNCH_MAIL (B4) Windows 2000/XP: Start Mail key
    static final int AK_MEDIA_LAUNCH_MAIL = 0xB4;
		
    // AK_LAUNCH_MEDIA_SELECT (B5) Windows 2000/XP: Select Media key
    static final int AK_MEDIA_LAUNCH_MEDIA_SELECT = 0xB5;
		
    // AK_LAUNCH_APP1 (B6) Windows 2000/XP: Start Application 1 key
    static final int AK_MEDIA_LAUNCH_APP1 = 0xB6;
		
    // AK_LAUNCH_APP2 (B7) Windows 2000/XP: Start Application 2 key
    static final int AK_MEDIA_LAUNCH_APP2 = 0xB7;
		
    // AK_OEM_1 (BA) Used for miscellaneous characters; it can vary by keyboard. Windows 2000/XP: For the US standard keyboard, the ';:' key
    static final int AK_OEM_1 = 0xBA;
		
    // AK_OEM_PLUS (BB) Windows 2000/XP: For any country/region, the '+' key
    static final int AK_OEM_PLUS = 0xBB;
		
    // AK_OEM_COMMA (BC) Windows 2000/XP: For any country/region, the ',' key
    static final int AK_OEM_COMMA = 0xBC;
		
    // AK_OEM_MINUS (BD) Windows 2000/XP: For any country/region, the '-' key
    static final int AK_OEM_MINUS = 0xBD;
		
    // AK_OEM_PERIOD (BE) Windows 2000/XP: For any country/region, the '.' key
    static final int AK_OEM_PERIOD = 0xBE;
		
    // AK_OEM_2 (BF) Used for miscellaneous characters; it can vary by keyboard. Windows 2000/XP: For the US standard keyboard, the '/?' key
    static final int AK_OEM_2 = 0xBF;
		
    // AK_OEM_3 (C0) Used for miscellaneous characters; it can vary by keyboard. Windows 2000/XP: For the US standard keyboard, the '`~' key
    static final int AK_OEM_3 = 0xC0;
		
    // AK_OEM_4 (DB) Used for miscellaneous characters; it can vary by keyboard. Windows 2000/XP: For the US standard keyboard, the '[{' key
    static final int AK_OEM_4 = 0xDB;
		
    // AK_OEM_5 (DC) Used for miscellaneous characters; it can vary by keyboard. Windows 2000/XP: For the US standard keyboard, the '\|' key
    static final int AK_OEM_5 = 0xDC;
		
    // AK_OEM_6 (DD) Used for miscellaneous characters; it can vary by keyboard. Windows 2000/XP: For the US standard keyboard, the ']}' key
    static final int AK_OEM_6 = 0xDD;
		
    // AK_OEM_7 (DE) Used for miscellaneous characters; it can vary by keyboard. Windows 2000/XP: For the US standard keyboard, the 'single-quote/double-quote' key
    static final int AK_OEM_7 = 0xDE;
		
    // AK_OEM_8 (DF) Used for miscellaneous characters; it can vary by keyboard.
    static final int AK_OEM_8 = 0xDF;
		
    // AK_OEM_102 (E2) Windows 2000/XP: Either the angle bracket key or the backslash key on the RT 102-key keyboard
    static final int AK_OEM_102 = 0xE2;
		
    // AK_PROCESSKEY (E5) Windows 95/98/Me, Windows NT 4.0, Windows 2000/XP: IME PROCESS key
    static final int AK_PROCESSKEY = 0xE5;
		
    // AK_PACKET (E7) Windows 2000/XP: Used to pass Unicode characters as if they were keystrokes. The AK_PACKET key is the low word of a 32-bit Virtual Key value used for non-keyboard input methods. For more information, see Remark in KEYBDINPUT,SendInput, WM_KEYDOWN, and WM_KEYUP
    static final int AK_PACKET = 0xE7;
		
    // AK_ATTN (F6) Attn key
    static final int AK_ATTN = 0xF6;
		
    // AK_CRSEL (F7) CrSel key
    static final int AK_CRSEL = 0xF7;
		
    // AK_EXSEL (F8) ExSel key
    static final int AK_EXSEL = 0xF8;
		
    // AK_EREOF (F9) Erase EOF key
    static final int AK_EREOF = 0xF9;
		
    // AK_PLAY (FA) Play key
    static final int AK_PLAY = 0xFA;
		
    // AK_ZOOM (FB) Zoom key
    static final int AK_ZOOM = 0xFB;
		
    // AK_NONAME (FC) Reserved for future use
    static final int AK_NONAME = 0xFC;
		
    // AK_PA1 (FD) PA1 key
    static final int AK_PA1 = 0xFD;
		
    // AK_OEM_CLEAR (FE) Clear key

    static final int AK_OEM_CLEAR = 0xFE;
		
    static final int AK_UNKNOWN = 0;

    static int mapKeyCode(int keyCode) {
        Integer val = keyMap.get(keyCode);
        if (val == null) {
            return AK_UNKNOWN;
        }
        return val.intValue();
    }

    static final Map<Integer,Integer> keyMap = new TreeMap();

    static void map(Integer k, Integer val) {
        keyMap.put(k, val);
    }

    static void buildKeyMap() {
	map(Keys.Backspace, AK_BACK);
	map(Keys.Tab, AK_TAB);
	map(Keys.Enter, AK_RETURN);
	map(Keys.Shift, AK_SHIFT);
	map(Keys.Ctrl, AK_CONTROL);
	map(Keys.Alt, AK_MENU);
	map(Keys.PauseBreak, AK_PAUSE);
	map(Keys.CapsLock, AK_CAPITAL);
	map(Keys.Esc, AK_ESCAPE);
	map(Keys.PageUp, AK_PRIOR);
	map(Keys.PageDown, AK_NEXT);
	map(Keys.End, AK_END);
	map(Keys.Home, AK_HOME);
	map(Keys.Left, AK_LEFT); 
	map(Keys.Up, AK_UP);
	map(Keys.Right, AK_RIGHT);
	map(Keys.Down, AK_DOWN);
	map(Keys.Insert, AK_INSERT);
	map(Keys.Delete, AK_DELETE);
	map(Keys._0, AK_0);
	map(Keys._1, AK_1);
	map(Keys._2, AK_2);
	map(Keys._3, AK_3);
	map(Keys._4, AK_4);
	map(Keys._5, AK_5);
	map(Keys._6, AK_6);
	map(Keys._7, AK_7);
	map(Keys._8, AK_8);
	map(Keys._9, AK_9);
	//map(Keys.Semicolon, AK_SEMICOLON);
	//    map(Keys.Equals, AK_EQUALS);
	map(Keys.A, AK_A);
	map(Keys.B, AK_B);
	map(Keys.C, AK_C);
	map(Keys.D, AK_D);
	map(Keys.E, AK_E);
	map(Keys.F, AK_F);
	map(Keys.G, AK_G);
	map(Keys.H, AK_H);
	map(Keys.I, AK_I);
	map(Keys.J, AK_J);
	map(Keys.K, AK_K);
	map(Keys.L, AK_L);
	map(Keys.M, AK_M);
	map(Keys.N, AK_N);
	map(Keys.O, AK_O);
	map(Keys.P, AK_P);
	map(Keys.Q, AK_Q);
	map(Keys.R, AK_R);
	map(Keys.S, AK_S);
	map(Keys.T, AK_T);
	map(Keys.U, AK_U);
	map(Keys.V, AK_V);
	map(Keys.W, AK_W);
	map(Keys.X, AK_X);
	map(Keys.Y, AK_Y);
	map(Keys.Z, AK_Z);
	//    map(Keys.Windows, AK_WINDOWS);
	//    map(Keys.RightClick, AK_CONTEXT_MENU);
	map(Keys.F1, AK_F1);
	map(Keys.F2, AK_F2);
	map(Keys.F3, AK_F3);
	map(Keys.F4, AK_F4);
	map(Keys.F5, AK_F5);
	map(Keys.F6, AK_F6);
	map(Keys.F7, AK_F7);
	map(Keys.F8, AK_F8);
	map(Keys.F9, AK_F9);
	map(Keys.F10, AK_F10);
	map(Keys.F11, AK_F11);
	map(Keys.F12, AK_F12);
	map(Keys.NumLock, AK_NUMLOCK);
	map(Keys.ScrollLock, AK_SCROLL);
    }

    static {
        System.loadLibrary("org.f3.media.web.awesomium");
        buildKeyMap();
    }

    static native void updateAll();
    static native long create(int width, int height);
    static native void resize(long handle, int width, int height);
    static native void destroy(long handle);
    static native int getCursor(long handle);
    static native boolean render(long handle, Buffer buffer, int rowspan, int depth, int[] rectArray);
    static native void injectMouseDown(long handle, int button);
    static native void injectMouseUp(long handle, int button);
    static native void injectMouseMove(long handle, int x, int y);
    static native void injectMouseWheel(long handle, int x, int y);
    static native void injectKeyDown(long handle, int mods, int code);
    static native void injectKeyUp(long handle, int mods, int code);
    static native void injectKeyInput(long handle, int mods, int code, char ch);
    static native void setURL(long handle, String string);
    static native String getURL(long handle);
    static native void setContent(long handle, String string);
    static native void focus(long handle);
    static native void unfocus(long handle);
    static native void setZoom(long handle, int percent);
    static native void resetZoom(long handle);
    enum awe_cursor_type
    {
	AWE_CUR_POINTER,
            AWE_CUR_CROSS,
            AWE_CUR_HAND,
            AWE_CUR_IBEAM,
            AWE_CUR_WAIT,
            AWE_CUR_HELP,
            AWE_CUR_EAST_RESIZE,
            AWE_CUR_NORTH_RESIZE,
            AWE_CUR_NORTHEAST_RESIZE,
            AWE_CUR_NORTHWEST_RESIZE,
            AWE_CUR_SOUTH_RESIZE,
            AWE_CUR_SOUTHEAST_RESIZE,
            AWE_CUR_SOUTHWEST_RESIZE,
            AWE_CUR_WEST_RESIZE,
            AWE_CUR_NORTHSOUTH_RESIZE,
            AWE_CUR_EASTWEST_RESIZE,
            AWE_CUR_NORTHEAST_SOUTHWEST_RESIZE,
            AWE_CUR_NORTHWEST_SOUTHEAST_RESIZE,
            AWE_CUR_COLUMN_RESIZE,
            AWE_CUR_ROW_RESIZE,
            AWE_CUR_MIDDLE_PANNING,
            AWE_CUR_EAST_PANNING,
            AWE_CUR_NORTH_PANNING,
            AWE_CUR_NORTHEAST_PANNING,
            AWE_CUR_NORTHWEST_PANNING,
            AWE_CUR_SOUTH_PANNING,
            AWE_CUR_SOUTHEAST_PANNING,
            AWE_CUR_SOUTHWEST_PANNING,
            AWE_CUR_WEST_PANNING,
            AWE_CUR_MOVE,
            AWE_CUR_VERTICAL_TEXT,
            AWE_CUR_CELL,
            AWE_CUR_CONTEXT_MENU,
            AWE_CUR_ALIAS,
            AWE_CUR_PROGRESS,
            AWE_CUR_NO_DROP,
            AWE_CUR_COPY,
            AWE_CUR_NONE,
            AWE_CUR_NOT_ALLOWED,
            AWE_CUR_ZOOM_IN,
            AWE_CUR_ZOOM_OUT,
            AWE_CUR_CUSTOM
    };
    

    public int getTextureId()  {
        return backingStore == null ? 0 : backingStore.getTextureObject(gl);
    }

    public CursorType getCursorType() {
        // hack...fixme
        int cursor = getCursor(handle);
        for (CursorType c: CursorType.values()) {
            if (c.ordinal() == cursor) {
                return c;
            }
        }
        return CursorType.POINTER;
    }

    GL gl;
    long handle;
    ByteBuffer buffer;
    Texture backingStore;
    TextureData data;
    int width, height;
    int potWidth, potHeight;
    int[] rect = new int[4];

    public void focus() {
        focus(handle);
    }

    public void unfocus() {
        unfocus(handle);
    }

    public String getURL() {
        return getURL(handle);
    }

    public void setURL(String url) {
        setURL(handle, url);
    }

    String currentContent = null;

    public void setContent(String content) {
        setContent(handle, content);
    }

    public String getContent() {
	return currentContent;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected void finalize() {
        if (backingStore != null) {
            final Texture tex = backingStore;
            final GL gl = this.gl;
            org.f3.runtime.Entry.deferAction(new java.lang.Runnable() {
                    public void run() {
                        tex.dispose(gl);
                    }
                });
        }
    }

    public AbstractTexture grabTexture() {
	final Texture tex = backingStore;
	backingStore = null;
	resize(width, height);
	return new AbstractTexture() {
	    public int getWidth() {
		return tex.getWidth();
	    }
	    public int getHeight() {
		return tex.getHeight();
	    }
	    public int getTextureId() {
		return tex.getTextureObject(GLContext.getCurrentGL());
	    }
	};
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        int w = potWidth = pot(width);
        int h = potHeight = pot(height);
        if (handle == 0) {
            handle = create(w, h);
        } else {
            resize(handle, w, h);
        }
        gl = GLContext.getCurrentGL();
        buffer = ByteBuffer.allocateDirect(w*h*4);
        if (backingStore != null) {
            backingStore.dispose(gl);
        }
        backingStore = allocateBackingStore(w, h);
        data = new TextureData(GLProfile.get(GLProfile.GL2),
                               GL.GL_RGBA8,
                               potWidth, potHeight, 
                               0,
                               GL.GL_RGBA,
                               GL.GL_UNSIGNED_BYTE,
                               false, false, true,
                               buffer, null);
        updateImage(buffer, 0, 0, w, h);
    }


    public boolean update() {
        if (handle != 0) {
            if (render(handle, buffer, potWidth * 4, 4, rect)) {
                int x = rect[0];
                int y = rect[1];
                int w = rect[2];
                int h = rect[3];
                updateImage(buffer, x, y, w, h); 
                return true;
            }
        }
        return false;
    }


    void updateImage(ByteBuffer buffer, 
                     int x, int y, int w, int h) {
        //System.out.println("updateImage: " +x +" " + y+" " + w+" "+h +" of " + pot(width) + " " + pot(height));
        if (false) {
            backingStore.updateSubImage(gl, data, 0, x, y, x, y, w, h);
        } else {
            backingStore.updateSubImage(gl, data, 0, 0, 0, 0, 0, potWidth, potHeight);
        }
    }
    
    int pot(int a) {
        int v = 2;
        while (v < a) {
            v <<= 1;
        }
        return v;
    }

    Texture allocateBackingStore(int w, int h) {
        TextureData data = new TextureData(GLProfile.get(GLProfile.GL2),
                                           GL.GL_RGBA8,
                                           w, h, 0,
                                           GL.GL_RGBA,
                                           GL.GL_UNSIGNED_BYTE,
                                           true, false, true,
                                           null, null);
        return TextureIO.newTexture(data);
    }

    public void injectMouseDown(int button) {
        injectMouseDown(handle, button);
    }

    public void injectMouseUp(int button) {
        injectMouseUp(handle, button);
    }

    public void injectMouseWheel(int x, int y) {
	injectMouseWheel(handle, y, x);
    }

    public void injectMouseMove(int x, int y) {
        float cx = x;
        float cy = y;
        // convert from center to top left and scale to our power-of-two size
        float sx = potWidth/(float)width;
        float sy = potHeight/(float)height;
        cx += width/2f;
        cy = height/2f - cy;
        cx *= sx;
        cy *= sy;
        injectMouseMove(handle, (int)Math.round(cx), (int)Math.round(cy));
    }

    public void injectKeyDown(int keyCode, int mods) {
        injectKeyDown(handle, mods, mapKeyCode(keyCode));
    }

    public void injectKeyUp(int keyCode, int mods) {
        injectKeyUp(handle, mods, mapKeyCode(keyCode));
    }

    public void injectKeyInput(int keyCode, int mods, char keyChar) {
        injectKeyInput(handle, mods, mapKeyCode(keyCode), keyChar);
    }
    /*
    JSObject window = null;

    public JSObject getWindow() {
	if (window == null) {
	    if (handle != 0) {
		window = (JSObject)execute_js(handle, "window");
	    }
	}
	return window;
    }
    */
    static native Object execute_js(long handle, String script);
    static native long create_js_array();
    static native long create_js_object();
    static native void destroy_js_array(long h);
    static native void destroy_js_object(long h);
    static native Object invoke(long handle, String method, long args);
    static native long getPropertyNames(long object);
    static native long getMethodNames(long object);
    static native boolean has(long object, String property);
    static native boolean hasMethod(long object, String property);
    static native void put_null(long object, String property);
    static native void put_boolean(long object, String property, boolean value);
    static native void put_int(long object, String property, int value);
    static native void put_double(long object, String property, double value);
    static native void put_string(long object, String property, String value);
    static native void put_object(long object, String property, long handle);
    static native void put_array(long object, String property, long handle);
    static native Object get(long object, String property);
    static native Object get_element(long array, int index);
    static native void put_null_element(long object, int index);
    static native void put_boolean_element(long object, int index, boolean value);
    static native void put_int_element(long object, int index, int value);
    static native void put_double_element(long object, int index, double value);
    static native void put_string_element(long object, int index, String value);
    static native void put_object_element(long object, int index, long handle);
    static native void put_array_element(long object, int index, long handle);
    static native int getSize(long array);

    //    static native long callFunction(long webview, String object, String function, long args, String frame);

}
