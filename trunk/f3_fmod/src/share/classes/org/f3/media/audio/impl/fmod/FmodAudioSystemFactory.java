package org.f3.media.audio.impl.fmod;
import org.f3.media.audio.*;
import f3.math.Vec3;
import f3.math.Point3;
import f3.math.Tuple3;
import static f3.math.LinearMath.*;
import java.net.URL;
import java.nio.*;
import java.io.*;
import java.util.*;

import org.jouvieje.FmodEx.*;
import org.jouvieje.FmodEx.Sound;
import org.jouvieje.FmodEx.System;
import org.jouvieje.FmodEx.Channel;
import org.jouvieje.FmodEx.DSP;
import org.jouvieje.FmodEx.Misc.*;
import org.jouvieje.FmodEx.ChannelGroup;
import org.jouvieje.FmodEx.Structures.*;
import org.jouvieje.FmodEx.Enumerations.*;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_REUSE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_NONE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCM8;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCM16;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCM24;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCM32;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_PCMFLOAT;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_GCADPCM;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_IMAADPCM;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_VAG;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_MPEG;
import static org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT.FMOD_SOUND_FORMAT_XMA;
import org.jouvieje.FmodEx.Enumerations.FMOD_SOUND_FORMAT;
import org.jouvieje.FmodEx.Enumerations.FMOD_RESULT;
import org.jouvieje.FmodEx.Callbacks.FMOD_SOUND_PCMREADCALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_SOUND_PCMSETPOSCALLBACK;
import org.jouvieje.FmodEx.Callbacks.FMOD_DSP_READCALLBACK;
import org.jouvieje.FmodEx.Structures.FMOD_CREATESOUNDEXINFO;
import org.jouvieje.FmodEx.Structures.FMOD_DSP_DESCRIPTION;
import org.jouvieje.FmodEx.Structures.FMOD_DSP_STATE;
import org.jouvieje.FmodEx.Enumerations.FMOD_DELAYTYPE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_DISTORTION.FMOD_DSP_DISTORTION_LEVEL;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_ECHO.FMOD_DSP_ECHO_DELAY;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_ECHO.FMOD_DSP_ECHO_DRYMIX;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_ECHO.FMOD_DSP_ECHO_WETMIX;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_ECHO.FMOD_DSP_ECHO_DECAYRATIO;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_ECHO.FMOD_DSP_ECHO_MAXCHANNELS;

import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_OSCILLATOR.FMOD_DSP_OSCILLATOR_TYPE;

import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_OSCILLATOR.FMOD_DSP_OSCILLATOR_RATE;

import org.jouvieje.FmodEx.Enumerations.FMOD_DSP_SFXREVERB;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_REVERB.FMOD_DSP_REVERB_DAMP;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_REVERB.FMOD_DSP_REVERB_WETMIX;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_REVERB.FMOD_DSP_REVERB_DRYMIX;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_REVERB.FMOD_DSP_REVERB_MODE;

import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_PARAMEQ.FMOD_DSP_PARAMEQ_CENTER;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_PARAMEQ.FMOD_DSP_PARAMEQ_GAIN;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_CHORUS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_DISTORTION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_ECHO;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_FLANGE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_HIGHPASS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_LOWPASS;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_PARAMEQ;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_OSCILLATOR;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_SFXREVERB;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_TYPE.FMOD_DSP_TYPE_REVERB;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_INITFLAGS.FMOD_INIT_3D_RIGHTHANDED;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_DEFAULT;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_MPEGSEARCH;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_2D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_3D;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_HARDWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_NONBLOCKING;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_SOFTWARE;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_LOOP_NORMAL;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENUSER;
import static org.jouvieje.FmodEx.Defines.FMOD_MODE.FMOD_OPENMEMORY;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_MS;
import static org.jouvieje.FmodEx.Defines.FMOD_TIMEUNIT.FMOD_TIMEUNIT_PCM;
import static org.jouvieje.FmodEx.Defines.VERSIONS.FMOD_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_JAR_VERSION;
import static org.jouvieje.FmodEx.Defines.VERSIONS.NATIVEFMODEX_LIBRARY_VERSION;
import static org.jouvieje.FmodEx.Enumerations.FMOD_CHANNELINDEX.FMOD_CHANNEL_FREE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_ERR_INVALID_HANDLE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_RESULT.FMOD_OK;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGDATATYPE.FMOD_TAGDATATYPE_STRING;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGDATATYPE.FMOD_TAGDATATYPE_INT;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGDATATYPE.FMOD_TAGDATATYPE_FLOAT;
import static org.jouvieje.FmodEx.Enumerations.FMOD_TAGDATATYPE.FMOD_TAGDATATYPE_BINARY;

import org.jouvieje.FmodEx.Enumerations.FMOD_DSP_FFT_WINDOW;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_FFT_WINDOW.FMOD_DSP_FFT_WINDOW_TRIANGLE;
import static org.jouvieje.FmodEx.Enumerations.FMOD_DSP_FFT_WINDOW.FMOD_DSP_FFT_WINDOW_HAMMING;

import org.jouvieje.FmodEx.Defines.INIT_MODES;
import org.f3.media.audio.Sampler;


public class FmodAudioSystemFactory implements AudioSystemFactory {
    

    static final org.f3.media.audio.SyncPoint[] NO_SYNC_POINTS = 
        new org.f3.media.audio.SyncPoint[0];

     ByteBuffer Buffer1;
     ByteBuffer Buffer2;
     ByteBuffer Buffer3;

     ByteBuffer booleanBuffer() {
        if (Buffer3 == null) {
            Buffer3 = ByteBuffer.allocateDirect(1);
        }
        Buffer3.rewind();
        return Buffer3;
    }

     ByteBuffer nativeOrder(int size) { 
        ByteBuffer buf = ByteBuffer.allocateDirect(size);
        buf.order(ByteOrder.nativeOrder());
        return buf;
    }
    
     ByteBuffer nativeOrder1(int size) { 
        if (Buffer1 == null || Buffer1.capacity() < size) {
            Buffer1 = nativeOrder(size);
        }
        Buffer1.rewind();
        return Buffer1;
    }

     ByteBuffer nativeOrder2(int size) { 
        if (Buffer2 == null || Buffer2.capacity() < size) {
            Buffer2 = nativeOrder(size);
        }
        Buffer2.rewind();
        return Buffer2;
    }

     ByteBuffer nativeOrder3(int size) { 
        if (Buffer3 == null || Buffer3.capacity() < size) {
            Buffer3 = nativeOrder(size);
        }
        Buffer3.rewind();
        return Buffer3;
    }

    public  class AudioSystemImpl implements AudioSystem {
        ByteBuffer[] clock = new ByteBuffer[] {nativeOrder(4),
                                               nativeOrder(4)};
        public long getClock() {
            system.getDSPClock(clock[0].asIntBuffer(), clock[1].asIntBuffer());
            long hi = clock[0].getInt(0);
            long lo = clock[1].getInt(0);
            long result = (hi << 32) | (lo & 0xffff);
            return result;
        }

        public int getOutputRate() {
            IntBuffer b1 = nativeOrder(4).asIntBuffer();
            system.getSoftwareFormat(b1, null, null, null, null, null);
            return  b1.get(0);
        }

         class ListenerImpl implements Listener {
            AudioSystemImpl audioSystem;
	     Point3 pos = Point3.get$0();
	     Vec3 vel = Vec3.get$0();
	     Vec3 forward = Vec3.get$Z_AXIS();
	     Vec3 up = Vec3.get$Y_AXIS();
	     Tuple3 worldScale = Tuple3.get$1();

            ListenerImpl(AudioSystemImpl audioSystem) {
                this.audioSystem = audioSystem;
                audioSystem.listenerChanged(this);
            }

            public void setLocation(Point3 pos) {
                setLocation(pos.get$x(), pos.get$y(), pos.get$z());
            }

            public void setLocation(float x, float y, float z) {
                pos = point(x, y, z);
                audioSystem.listenerChanged(this);
            }

            public Point3 getLocation() {
                return pos;
            }
            
            public void setVelocity(Vec3 vel) {
                setVelocity(vel.get$x(), vel.get$y(), vel.get$z());
                audioSystem.listenerChanged(this);
            }

            public Vec3 getVelocity() {
                return vel;
            }

            public void setVelocity(float x, float y, float z) {
                vel = vector(x, y, z);
                audioSystem.listenerChanged(this);
            }
            
            public void setForwardOrientation(Vec3 vec) {
                setForwardOrientation(vec.get$x(), vec.get$y(), vec.get$z());
            }

            public void setForwardOrientation(float x, float y, float z) {
                forward = vector(x, y, z);
                audioSystem.listenerChanged(this);
            }

            public Vec3 getForwardOrientation() {
                return forward;
            }
            
            public void setUpwardOrientation(Vec3 vec) {
                setUpwardOrientation(vec.get$x(), vec.get$y(), vec.get$z());
            }

            public void setUpwardOrientation(float x, float y, float z) {
                up = vector(x, y, z);
            }

            public Vec3 getUpwardOrientation() {
                return up;
            }

            public void setWorldScale(float x, float y, float z) {
                worldScale = vector(x, y, z);
            }

            public Tuple3 getWorldScale() {
                return worldScale;
            }
        }


        public class ChannelGroupImpl implements org.f3.media.audio.ChannelGroup {


            ChannelGroup channelGroup;
            String name;
            AbstractDSP dspHead;
            protected void finalize() {
                if (channelGroup != null) {
                   enqueueForDelete(channelGroup);
                }
            }

            public String getName() {
               return name;
            }

            public ChannelGroupImpl(String name, ChannelGroup channelGroup) {
                this.channelGroup = channelGroup;
                this.name = name;
            }
            List<org.f3.media.audio.Channel> channels = new ArrayList();
            List<org.f3.media.audio.ChannelGroup> channelGroups = new ArrayList();
            org.f3.media.audio.ChannelGroup fxChannelGroup;

            public void addDSP(org.f3.media.audio.DSP dsp) {
                java.lang.System.out.println("adding dsp: "+ dsp + " to " + channelGroup); 
                errorCheck(channelGroup.addDSP(((AbstractDSP)dsp).dsp,
                                               null));
            }

            public void stop() {
                errorCheck(channelGroup.stop());
            }

            public org.f3.media.audio.DSP getDSPHead() {
                if (dspHead == null) {
                    DSP dsp = new DSP();
                    errorCheck(channelGroup.getDSPHead(dsp));
                    dspHead = new AbstractDSP() {};
                    dspHead.dsp = dsp;
                }
                java.lang.System.out.println("dsp.head for " + this + " = "+dspHead);
                return dspHead;
            }

            public org.f3.media.audio.ChannelGroup getRootChannelGroup() {
                return rootChannelGroup;
            }

            public AudioSystem getAudioSystem() {
                return AudioSystemImpl.this;
            }
            
            public org.f3.media.audio.ChannelGroup getChannelGroup() {
                return fxChannelGroup;
            }

            public void setChannelGroup(org.f3.media.audio.ChannelGroup  channelGroup) {
                if (channelGroup != null) {
                    ((ChannelGroupImpl)channelGroup).addChannelGroup(this);
                } else {
                    channelGroup = null;
                }
            }

            public org.f3.media.audio.Channel[] getChannels() {
                org.f3.media.audio.Channel[] result = new org.f3.media.audio.Channel[channels.size()];
                channels.toArray(result);
                return result;
            }

            public org.f3.media.audio.ChannelGroup[] getChannelGroups() {
                org.f3.media.audio.ChannelGroup[] result = new org.f3.media.audio.ChannelGroup[channelGroups.size()];
                channelGroups.toArray(result);
                return result;
            }

            public void addChannelGroup(org.f3.media.audio.ChannelGroup channelGroup) {
                ((ChannelGroupImpl)channelGroup).fxChannelGroup = this;
                //((ChannelGroupImpl)channelGroup).channelGroup.setChannelGroup(this.channelGroup);
                channelGroups.add(channelGroup);
            }

            public void addChannelGroup(int index, org.f3.media.audio.ChannelGroup channelGroup) {
                ((ChannelGroupImpl)channelGroup).fxChannelGroup = this;
                //((ChannelGroupImpl)channelGroup).channelGroup.setChannelGroup(this.channelGroup);
                
                channelGroups.add(index, channelGroup);
            }

            public org.f3.media.audio.ChannelGroup removeChannelGroup(int index) {
                org.f3.media.audio.ChannelGroup result = channelGroups.remove(index);
                ((ChannelGroupImpl)result).fxChannelGroup = null;
                return result;
            }

            public void removeChannelGroup(org.f3.media.audio.ChannelGroup channelGroup) {
                int index = channelGroups.indexOf(channelGroup);
                if (index >= 0) {
                    removeChannelGroup(index);
                }
            }

            public org.f3.media.audio.ChannelGroup getChannelGroup(int index) {
                return channelGroups.get(index);
            }

            public org.f3.media.audio.ChannelGroup setChannelGroup(int index, org.f3.media.audio.ChannelGroup channelGroup) {
                org.f3.media.audio.ChannelGroup old = channelGroups.set(index, channelGroup);
                ((ChannelGroupImpl)channelGroup).fxChannelGroup = null;
                return old;
            }

            public int groups() {
                return channelGroups.size();
            }

            public void addChannel(org.f3.media.audio.Channel channel) {
                ((ChannelImpl)channel).channelGroup = this;
                if (((ChannelImpl)channel).channel != null) {
                    ((ChannelImpl)channel).channel.setChannelGroup(channelGroup);
                }
                channels.add(channel);
            }

            public void addChannel(int index, org.f3.media.audio.Channel channel) {
                ((ChannelImpl)channel).channelGroup = this;
                if (((ChannelImpl)channel).channel != null) {
                    ((ChannelImpl)channel).channel.setChannelGroup(channelGroup);
                }
                channels.add(index, channel);
            }

            public void removeChannel(org.f3.media.audio.Channel channel) {
                int index = channels.indexOf(channel);
                if (index >= 0) {
                    removeChannel(index);
                }
            }

            public org.f3.media.audio.Channel removeChannel(int index) {
                org.f3.media.audio.Channel result = channels.remove(index);
                ((ChannelImpl)result).channelGroup = null;
                return result;
            }

            public org.f3.media.audio.Channel getChannel(int index) {
                return channels.get(index);
            }

            public org.f3.media.audio.Channel setChannel(int index, org.f3.media.audio.Channel channel) {
                org.f3.media.audio.Channel old = channels.set(index, channel);
                ((ChannelImpl)channel).channelGroup = null;
                return old;
            }

            public int channels() {
                return channels.size();
            }

            public void setPaused(boolean value) {
                channelGroup.setPaused(value);
            }

            public boolean isPaused() {
                ByteBuffer buf = booleanBuffer();
                channelGroup.getPaused(buf);
                buf.rewind();
                return buf.get(0) != 0;
            }
            
            public void setVolume(float value) {
                java.lang.System.out.println("setting channel group volume: "+ value);
                channelGroup.setVolume(value);
            }

            public float getVolume() {
                FloatBuffer buf = nativeOrder1(4).asFloatBuffer();
                channelGroup.getVolume(buf);
                buf.rewind();
                return buf.get(0);
            }
            
            public void setMute(boolean value) {
                channelGroup.setMute(value);
            }

            public boolean isMute() {
                ByteBuffer buf = booleanBuffer();
                channelGroup.getMute(buf);
                buf.rewind();
                return buf.get(0) != 0;
            }

            float min = 0;
            float max = 0;
            
            public float[] getSpectrum(int channelOffset, int size) {
                ChannelGroup channel = channelGroup;
                if (channel != null) {
                    FloatBuffer buf = nativeOrder1(size*4).asFloatBuffer();
                    channel.getSpectrum(buf, size, channelOffset,
                                        FMOD_DSP_FFT_WINDOW.FMOD_DSP_FFT_WINDOW_BLACKMANHARRIS);
                    buf.rewind();
                    float[] result = new float[size];
                    buf.get(result);
                    for (int i = 0; i < result.length; i++) {
                        if (result[i] < min) { min = result[i]; }
                        if (result[i] > max) { max = result[i]; }
                    }
                    float d = max - min;
                    for (int i = 0; i < result.length; i++) {
                        result[i] = (result[i] - min)/d;
                        if (Float.isNaN(result[i])) { result[i] = 0; }
                    }
                    return result;
                }
                return null;
            }

            public float[] getWaveData(int channelOffset, int size) {
                ChannelGroup channel = channelGroup;
                if (channel != null) {
                    FloatBuffer buf = nativeOrder1(size*4).asFloatBuffer();
                    channel.getWaveData(buf, size, channelOffset);
                    buf.rewind();
                    float[] result = new float[size];
                    buf.get(result);
                    return result;
                }
                return null;
            }
        }

        public class ChannelImpl implements org.f3.media.audio.Channel {

            Channel channel;
            org.f3.media.audio.ChannelGroup channelGroup;
            long delay;
            Point3 location = Point3.get$0();
            Vec3 velocity = Vec3.get$0();
            AbstractDSP dspHead;

            public void stop() {
                if (channel != null) {
                    if (isPlaying()) {
                        errorCheck(channel.stop());
                    }
                    channel = null;
                }
            }

            public AudioSystem getAudioSystem() {
                return AudioSystemImpl.this;
            }

            public int getPosition() {
                if (channel == null || channel.isNull()) {
                    return 0;
                }
                ByteBuffer buf = nativeOrder1(8);
                /*errorCheck(*/channel.getPosition(buf.asIntBuffer(), FMOD_TIMEUNIT_MS);//);
                return buf.getInt(0);
            }

            public void setPosition(int pos) {
                if (channel != null) {
                    try {
                        //errorCheck1(
                        channel.setPosition(pos, FMOD_TIMEUNIT_MS)
                            //    )
                        ;
                    } catch (Error e) {
                        java.lang.System.out.println("failed to set pos to "+ pos);
                        throw e;
                    }
                }
            }

            public void addDSP(org.f3.media.audio.DSP dsp) {
                java.lang.System.out.println("adding dsp: "+ dsp + " to " + channel);
                errorCheck(channel.addDSP(((AbstractDSP)dsp).dsp, null));
            }

            public org.f3.media.audio.ChannelGroup getRootChannelGroup() {
                return rootChannelGroup;
            }
            
            public org.f3.media.audio.ChannelGroup getChannelGroup() {
                return channelGroup;
            }

            public void setChannelGroup(org.f3.media.audio.ChannelGroup  channelGroup) {
                if (channelGroup != null) {
                    ((ChannelGroupImpl)channelGroup).addChannel(this);
                } else {
                    channelGroup = null;
                }
            }

            public org.f3.media.audio.DSP getDSPHead() {
                if (dspHead == null) {
                    dspHead = new AbstractDSP() {};
                    if (channel != null) {
                        DSP dsp = new DSP();
                        errorCheck(channel.getDSPHead(dsp));
                        dspHead.dsp = dsp;
                    }
                }
                java.lang.System.out.println("dsp.head for " + this + " = "+dspHead);
                return dspHead;
            }
            
            void setChannel(Channel channel) {
                this.channel = channel;
            }

            public void play(org.f3.media.audio.Sound sound,
                             String name) {
                //TBD
            }

            public void play(org.f3.media.audio.Sound sound,
                             int index) {
                //TBD
            }

            public void play(org.f3.media.audio.Sound sound) {
                //TBD
            }

            public void play(org.f3.media.audio.DSP dsp) {
                //TBD
            }

            public void setLocation(Point3 vec) {
                setLocation(vec.get$x(), vec.get$y(), vec.get$z());
            }

            public void setLocation(float x, float y, float z) {
                location = point(x, y, z);
                update3DAttributes();
            }

            public Point3 getLocation() {
                return location;
            }

            public void setVelocity(Vec3 vec) {
                setVelocity(vec.get$x(), vec.get$y(), vec.get$z());
            }

            public void setVelocity(float x, float y, float z) {
                velocity = vector(x, y, z);
                update3DAttributes();
            }

            public Vec3 getVelocity() {
                return velocity;
            }

            void update3DAttributes() {
                if (channel != null && !channel.isNull()) {
                    FMOD_VECTOR v = null;
                    FMOD_VECTOR p = null;
                    try {
                        if (velocity != null) {
                            v = FMOD_VECTOR.create();
                            v.setX(velocity.get$x()); v.setY(velocity.get$y()); v.setZ(velocity.get$z());
                        }
                        if (location != null) {
                            p = FMOD_VECTOR.create();
                            p.setX(location.get$x()); p.setY(location.get$y()); p.setZ(location.get$z());
                        }
                        channel.set3DAttributes(p, v);
                    } finally {
                        if (v != null) v.release();
                        if (p != null) p.release();
                    }
                }
            }

            public boolean isPlaying()  {
                if (channel != null) {
                    if (channel.isNull()) {
                        //channel = null;
                    } else {
                        ByteBuffer buf = booleanBuffer();
                        channel.isPlaying(buf);
                        buf.rewind();
                        return buf.get(0) != 0;
                    }
                }
                return false;
            }

            protected void finalize() {
                if (channel != null) {
                    enqueueForDelete(channel);
                }
            }

            public void setPaused(boolean value) {
                if (channel != null && !channel.isNull()) {
                    channel.setPaused(value);
                }
            }

            public long getDelay() {
                return delay;
            }

            public void setDelay(long value) {
                delay = value;
                if (channel != null && !channel.isNull()) {
                    int lo = (int)(value & 0xffff);
                    int hi = (int)((value >>> 32) & 0xffff);
                    java.lang.System.out.println("set delay to " + value);
                    channel.setDelay(FMOD_DELAYTYPE.FMOD_DELAYTYPE_DSPCLOCK_START,
                                     hi,
                                     lo);
                }
            }

            public boolean isPaused() {
                if (channel != null && !channel.isNull()) {
                    ByteBuffer buf = booleanBuffer();
                    channel.getPaused(buf);
                    buf.rewind();
                    return buf.get(0) != 0;
                }
                return false;
            }
            
            public void setVolume(float value) {
                if (channel != null && !channel.isNull()) {
                    channel.setVolume(value);
                }
            }

            public float getAudibility() {
                if (channel != null && !channel.isNull()) {
                    FloatBuffer buf = nativeOrder1(4).asFloatBuffer();
                    channel.getAudibility(buf);
                    buf.rewind();
                    return buf.get(0);
                }
                return 0;
            }
            float minDist;
            float maxDist;
            public void setMinMaxDistance(float min, float max) {
                channel.set3DMinMaxDistance(min, max);
                minDist = min;
                maxDist = max;
            }

            public float getMinDistance() {
                return minDist;
            }

            public float getMaxDistance() {
                return maxDist;
            }

            public void setSoundCone(float insideAngle, float outsideAngle, float outsideVolume) {
                channel.set3DConeSettings(insideAngle, outsideAngle, outsideVolume);
            }

            public Vec3 getSoundCone() {
                FloatBuffer buf1 = nativeOrder1(4).asFloatBuffer();
                FloatBuffer buf2 = nativeOrder2(4).asFloatBuffer();
                FloatBuffer buf3 = nativeOrder3(4).asFloatBuffer();
                channel.get3DConeSettings(buf1, buf2, buf3);
                return vector(buf1.get(), buf2.get(), buf3.get());
            }

            public void setConeOrientation(float x, float y, float z) {
                FMOD_VECTOR v = FMOD_VECTOR.create();
                v.setX(x);
                v.setY(y);
                v.setZ(z);
                channel.set3DConeOrientation(v);
                v.release();
            }

            public Vec3 getConeOrientation() {
                FMOD_VECTOR v = FMOD_VECTOR.create();
                channel.get3DConeOrientation(v);
                Vec3 result = vector(v.getX(), v.getY(), v.getZ());
                v.release();
                return result;
            }

            public float getVolume() {
                if (channel != null && !channel.isNull()) {
                    FloatBuffer buf = nativeOrder1(4).asFloatBuffer();
                    channel.getVolume(buf);
                    buf.rewind();
                    return buf.get(0);
                }
                return 0;
            }
            
            public void setFrequency(float value) {
                if (channel != null && !channel.isNull()) {
                    channel.setFrequency(value);
                }
            }

            public float getFrequency() {
                if (channel != null && !channel.isNull()) {
                    FloatBuffer buf = nativeOrder1(4).asFloatBuffer();
                    channel.getFrequency(buf);
                    buf.rewind();
                    return buf.get(0);
                }
                return 0;
            }
            
            public void setPan(float value) {
                if (channel != null && !channel.isNull()) {
                    channel.setPan(value);
                }
            }

            public float getPan() {
                if (channel != null && !channel.isNull()) {
                    FloatBuffer buf = nativeOrder1(4).asFloatBuffer();
                    channel.getPan(buf);
                    buf.rewind();
                    return buf.get(0);
                }
                return 0;
            }
            
            public void setMute(boolean value) {
                if (channel != null && !channel.isNull()) {
                    channel.setMute(value);
                }
            }

            public boolean isMute() {
                if (channel != null && !channel.isNull()) {
                    ByteBuffer buf = nativeOrder1(1);
                    channel.getMute(buf);
                    buf.rewind();
                    return buf.get(0) != 0;
                }
                return false;
            }
            
            public void setLoopCount(int value) {
                if (channel != null  && !channel.isNull()) {
                    java.lang.System.out.println("setting loop count: "+ value);
                    errorCheck(channel.setLoopCount(value));
                }
            }

            public int getLoopCount() {
                if (channel != null && !channel.isNull()) {
                    IntBuffer buf = nativeOrder1(4).asIntBuffer();
                    java.lang.System.out.println("getting loop count");
                    channel.getLoopCount(buf);
                    buf.rewind();
                    return buf.get(0);
                }
                return 0;
            }

            public void setLoopPoints(int start, int end) {
                if (channel != null && !channel.isNull()) {
                    java.lang.System.out.println("setting loop points " + start + " " + end);
                    errorCheck(channel.setLoopPoints(start, FMOD_TIMEUNIT_MS,
                                                     end, FMOD_TIMEUNIT_MS));
                }
            }

            public int getLoopStart() {
                if (channel != null && !channel.isNull()) {
                    ByteBuffer buf1 = nativeOrder1(4);
                    ByteBuffer buf2 = nativeOrder2(4);
                    java.lang.System.out.println("getting loop points");
                    errorCheck(channel.getLoopPoints(buf1.asIntBuffer(),
                                                     FMOD_TIMEUNIT_MS,
                                                     buf2.asIntBuffer(),
                                                     FMOD_TIMEUNIT_MS));
                    return buf1.get(0);
                }
                return 0;
            }

            public int getLoopEnd() {
                if (channel != null && !channel.isNull()) {
                    ByteBuffer buf1 = nativeOrder1(4);
                    ByteBuffer buf2 = nativeOrder2(4);
                    java.lang.System.out.println("getting loop points");
                    errorCheck(channel.getLoopPoints(buf1.asIntBuffer(),
                                                     FMOD_TIMEUNIT_MS,
                                                     buf2.asIntBuffer(),
                                                     FMOD_TIMEUNIT_MS));
                    return buf2.get(0);
                }
                return 0;
            }
            

            float min = 0;
            float max = 0;
            
            public float[] getSpectrum(int channelOffset, int size) {
                if (channel != null && !channel.isNull()) {
                    FloatBuffer buf = nativeOrder1(size*4).asFloatBuffer();
                    channel.getSpectrum(buf, size, channelOffset,
                                        FMOD_DSP_FFT_WINDOW.FMOD_DSP_FFT_WINDOW_BLACKMANHARRIS);
                    buf.rewind();
                    float[] result = new float[size];
                    buf.get(result);
                    for (int i = 0; i < result.length; i++) {
                        if (result[i] < min) { min = result[i]; }
                        if (result[i] > max) { max = result[i]; }
                    }
                    float d = max - min;
                    for (int i = 0; i < result.length; i++) {
                        result[i] = (result[i] - min)/d;
                        if (Float.isNaN(result[i])) { result[i] = 0; }
                    }
                    return result;
                }
                return null;
            }

            public float[] getWaveData(int channelOffset, int size) {
                if (channel != null && !channel.isNull()) {
                    FloatBuffer buf = nativeOrder1(size*4).asFloatBuffer();
                    channel.getWaveData(buf, size, channelOffset);
                    buf.rewind();
                    float[] result = new float[size];
                    buf.get(result);
                    return result;
                }
                return null;
            }

        }

        public class FreeChannelImpl extends ChannelImpl {

            float frequency = 44100;
            float pan = 0;
            int loopCount = -1;
            float volume = 1;
            boolean paused = false;
            boolean mute = false;

            FreeChannelImpl() {

            }

            public void setPaused(boolean value) {
                paused = value;
                super.setPaused(value);
            }

            public void setFrequency(float value) {
                frequency = value;
                super.setFrequency(value);
            }
            public float getFrequency() {
                return frequency;
            }
            
            // -1.0..1.0
            public void setPan(float value) {
                pan = value;
                super.setPan(value);
            }
            public float getPan() {
                return pan;
            }
            
            public void setLoopCount(int value) {
                loopCount = value;
                super.setLoopCount(value);
            }
            public int getLoopCount() {
                return loopCount;
            }

            public void setVolume(float value) {
                volume = value;
                super.setVolume(value);
            }

            public float getVolume() {
                return volume;
            }

            public void setMute(boolean value) {
                this.mute = value;
                super.setMute(value);
            }

            public boolean isMute() {
                return mute;
            }

            public void play(org.f3.media.audio.Sound sound,
                             String name) {
                Subsound[] sub = sound.getSubsounds();
                for (int i = 0; i < sub.length; i++) {
                    if (sub[i].getName().equals(name)) {
                        play(sub[i]);
                        return;
                    }
                }
                throw new RuntimeException("Sound not found: "+ name + " in " + sound.getName());
            }

            public void play(org.f3.media.audio.Sound sound,
                             int index) {
                Subsound[] sub = sound.getSubsounds();
                if (index >= 0 && index < sub.length) {
                    play(sub[index]);
                    return;
                }                
                throw new RuntimeException("No subsound at index "+index);
            }

            public void play(org.f3.media.audio.Sound sound) {
                min = 0;
                max = 0;
                if (channel != null && !channel.isNull()) {
                    channel.stop();
                } else {
                    channel = new Channel();
                }
                if (sound == null || ((SoundImpl)sound).sound == null ||
                    ((SoundImpl)sound).sound.isNull()) {
                    // @TODO?
                    return;
                }
                errorCheck(system.playSound(
                                            FMOD_CHANNEL_REUSE,
                                            ((SoundImpl)sound).sound,
                                            true,
                                            channel));
                setChannel(channel);
                if (channelGroup != null) {
                    channel.setChannelGroup(((ChannelGroupImpl)channelGroup).channelGroup);
                }
                java.lang.System.out.println("playing channel");
                setLoopCount(loopCount);
                setPan(pan);
                setVolume(volume);
                setMute(mute);
                setFrequency(frequency);
                if (dspHead != null) {
                    DSP dsp = new DSP();
                    errorCheck(channel.getDSPHead(dsp));
                    dspHead.dsp = dsp;
                }
                if (delay != 0) {
                    setDelay(delay);
                } else {
                    ByteBuffer buf1 = nativeOrder1(4);
                    ByteBuffer buf2 = nativeOrder2(4);
                    channel.getDelay(FMOD_DELAYTYPE.FMOD_DELAYTYPE_DSPCLOCK_START,
                                     buf1.asIntBuffer(), buf2.asIntBuffer());
                    long hi = buf1.getInt(0);
                    long lo = (buf2.getInt(0) & 0xffff);
                    delay = (hi << 32) | lo;
                }
                setPaused(paused);
            }

            public void play(org.f3.media.audio.DSP dsp) {
                min = 0;
                max = 0;
                if (channel == null) {
                    channel = new Channel();
                }
                errorCheck(system.playDSP(FMOD_CHANNEL_REUSE,
                                          ((AbstractDSP)dsp).dsp,
                                          true,
                                          channel));
                setChannel(channel);
                if (channelGroup != null) {
                    channel.setChannelGroup(((ChannelGroupImpl)channelGroup).channelGroup);
                }
                java.lang.System.out.println("playing channel");
                setLoopCount(loopCount);
                setPan(pan);
                setVolume(volume);
                setMute(mute);
                setFrequency(frequency);
                if (dspHead != null) {
                    DSP d = new DSP();
                    errorCheck(channel.getDSPHead(d));
                    dspHead.dsp = d;
                }
                if (delay != 0) {
                    setDelay(delay);
                }
                ByteBuffer buf1 = nativeOrder1(4);
                ByteBuffer buf2 = nativeOrder2(4);
                channel.getDelay(FMOD_DELAYTYPE.FMOD_DELAYTYPE_DSPCLOCK_START,
                                 buf1.asIntBuffer(), buf2.asIntBuffer());
                long hi = buf1.getInt(0);
                long lo = (buf2.getInt(0) & 0xffff);
                delay = (hi << 24) | lo;
                setPaused(paused);
            }
        }

        public class SubsoundImpl extends SoundImpl 
            implements 
                org.f3.media.audio.Subsound {
            org.f3.media.audio.Sound parent;
            int index;
            public SubsoundImpl(org.f3.media.audio.Sound parent,
                                int index,
                                Sound sound) {
                super(sound);
                this.index = index;
                this.parent = parent;
            }

            public org.f3.media.audio.Sound getParent() {
                return parent;
            }

            public int getIndex() {
                return index;
            }
        }
        
        public class SoundImpl implements org.f3.media.audio.Sound {
            Sound sound;
            org.f3.media.audio.Subsound[] subSounds;
            org.f3.media.audio.SyncPoint[] syncPoints;
            String name;
            int[] playlist;
            int playlistSize;
            long durationMillis;
            boolean is3d;
            float minDistance;
            float maxDistance;
            int channels;
            int format;
            org.f3.media.audio.Tag[] tags;

            public int getFormat() {
                return format;
            }

            class SyncPointImpl implements org.f3.media.audio.SyncPoint {
                String name;
                long millis;

                SyncPointImpl(String name, long millis) {
                    this.name = name;
                    this.millis = millis;
                }

                public String getName() {
                    return name;
                }

                public long getOffset() {
                    return millis;
                }
                
                public org.f3.media.audio.Sound getSound() {
                    return SoundImpl.this;
                }
            }

            public org.f3.media.audio.SyncPoint[] getSyncPoints()  {
                if (sound != null && syncPoints == null) {
                    IntBuffer b = nativeOrder1(4).asIntBuffer();
                    errorCheck(sound.getNumSyncPoints(b));
                    int num = b.get(0);
                    syncPoints = new SyncPoint[num];
                    java.lang.System.out.println("found " + num + " sync points");
                    if (num > 0) {
                        ByteBuffer nameBuf = ByteBuffer.allocateDirect(1024);
                        byte[] nameBytes = new byte[1024];
                        for (int i = 0; i < num; i++) {
                            FMOD_SYNCPOINT point = new FMOD_SYNCPOINT();
                            errorCheck(sound.getSyncPoint(i, point));
                            nameBuf.rewind();
                            String name = "";
                            errorCheck(sound.getSyncPointInfo(point,
                                                              nameBuf, 1024,
                                                              b,
                                                              FMOD_TIMEUNIT_MS));
                            try {
                                nameBuf.get(nameBytes);
                                String str = new String(nameBytes, "utf-8");
                                int zero = str.indexOf('\0');
                                if (zero >= 0) {
                                    str = str.substring(0, zero);
                                }
                                name = str;
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            java.lang.System.out.println("sync point name: "+ name);
                            java.lang.System.out.println("sync point offset: "+ b.get(0));
                            syncPoints[i] = new SyncPointImpl(name, b.get(0));
                            point.release();
                        }
                    }
                }
                return syncPoints == null ? NO_SYNC_POINTS : syncPoints;
            }
                

            class TagImpl implements org.f3.media.audio.Tag {
                int index;
                String name;
                Object data;
                TagImpl(int index) {
                    this.index = index;
                }
                public Object getData() {
                    if (data == null) {
                        FMOD_TAG tag = FMOD_TAG.create();
                        try {
                            if (sound.getTag(null, index, tag) == FMOD_RESULT.FMOD_OK) {
                                java.lang.System.out.println("tag.data="+tag.getData());
                            } else {
                                java.lang.System.out.println("no tag: "+ name);
                                return null;
                            }
                            name = tag.getName();

                            if (tag.getDataType() == FMOD_TAGDATATYPE_STRING) {
                                data = tag.getData().asString();
                            } else if (tag.getDataType() == FMOD_TAGDATATYPE_INT) {
                                data = tag.getData().asInt();
                            } else if (tag.getDataType() == FMOD_TAGDATATYPE_FLOAT) {
                                data = tag.getData().asFloat();
                            } else if (tag.getDataType() == FMOD_TAGDATATYPE_BINARY) {
                                ByteBuffer buffer = PointerUtils.createView(tag.getData(), tag.getDataLen());
                                byte[] bytes = new byte[tag.getDataLen()];
                                buffer.get(bytes);
                                data = bytes;
                            } else {
                                // fix me
                                data = tag.getData().asString();
                            }
                        } finally {
                            tag.release();
                        }
                    }
                    return data;
                }
                public String getName() {
                    if (name == null) {
                        FMOD_TAG tag = FMOD_TAG.create();
                        try {
                            if (sound.getTag(null, index, tag) == FMOD_RESULT.FMOD_OK) {
                                java.lang.System.out.println("tag.data="+tag.getData());
                            } else {
                                java.lang.System.out.println("no tag: "+ name);
                                return null;
                            }
                            name = tag.getName();
                        } finally {
                            tag.release();
                        }
                    }
                    return name;
                }
            }
            

            SoundImpl(Sound sound) {
                this.sound = sound;
                init();
            }

            public void init() {
                if (!sound.isNull()) {
                    ByteBuffer buf = nativeOrder1(8);
                    buf.asIntBuffer().put(0, 0);
                    buf.asIntBuffer().put(1, 0);
                    java.lang.System.out.println("getting sound length");
                    errorCheck(sound.getLength(buf.asIntBuffer(), 
                                               FMOD_TIMEUNIT_MS));
                    durationMillis = buf.getInt(0);
                    IntBuffer channels = nativeOrder1(4).asIntBuffer();
                    IntBuffer bits = nativeOrder2(4).asIntBuffer();
                    FMOD_SOUND_TYPE[] type = new FMOD_SOUND_TYPE[1];
                    FMOD_SOUND_FORMAT[] format = new FMOD_SOUND_FORMAT[1];
                    errorCheck(sound.getFormat(type, format, channels, bits));
                    this.channels = channels.get(0);
                    java.lang.System.out.println("format="+format[0]);
                    int fmt = SoundFormat.NONE;
                    if (format[0].equals(FMOD_SOUND_FORMAT_PCM8)) {
                        fmt = SoundFormat.PCM8;
                    } else if (format[0].equals(FMOD_SOUND_FORMAT_PCM16)) {
                        fmt = SoundFormat.PCM16;
                    } else if (format[0].equals(FMOD_SOUND_FORMAT_PCM24)) {
                        fmt = SoundFormat.PCM24;
                    } else if (format[0].equals(FMOD_SOUND_FORMAT_PCM32)) {
                        fmt = SoundFormat.PCM32;
                    } else {
                        // fix me...
                    }
                    this.format = fmt;
                    java.lang.System.out.println("chs="+this.channels);
                }
            }

            public void dispose() {
                if (sound != null) {
                    Sound s = sound;
                    sound = null;
                    enqueueForDelete(s);
                }
            }
            
            public org.f3.media.audio.Tag[] getTags() {
                if (tags == null) {
                    int count = getNumTags();
                    tags = new org.f3.media.audio.Tag[count];
                    for (int i = 0; i < count; i++) {
                        tags[i] = new TagImpl(i);
                    }
                }
                return tags;
            }


            public void sample(int offset,
                               int length,
                               Sampler sampler) {
                ByteBuffer[] ptr1 = new ByteBuffer[1];
                ByteBuffer[] ptr2 = new ByteBuffer[1];
                IntBuffer lb1 = nativeOrder1(4).asIntBuffer();
                IntBuffer lb2 = nativeOrder2(4).asIntBuffer();
                errorCheck(sound.lock(offset, length, ptr1, ptr2, lb1, lb2));
                int len1 = lb1.get();
                int len2 = lb2.get();
                sampler.sample(this, ptr1[0], len1, ptr2[0], len2);
                errorCheck(sound.unlock(ptr1[0], ptr2[0], len1, len2));
            }

            public int getNumTags() {
                ByteBuffer buf = nativeOrder1(4);
                errorCheck(sound.getNumTags(buf.asIntBuffer(), null));
                return buf.asIntBuffer().get(0);
            }


            public int getPlaylistSize() {
                return playlistSize;
            }

            public long getDurationMillis() {
                return durationMillis;
            }

            void setPlaylistSize(int value) {
                playlistSize = value;
            }

            public void setMinMaxDistance(float min, float max) {
                minDistance = min; maxDistance = max;
                java.lang.System.out.println("SETTING MIN MAX DISTANCE " + min + " "+ max);
                errorCheck(sound.set3DMinMaxDistance(min, max));
            }

            public float getMinDistance() {
                return minDistance;
            }

            public float getMaxDistance() {
                return maxDistance;
            }

            public void setSoundCone(float insideAngle, float outsideAngle, float outsideVolume) {
                sound.set3DConeSettings(insideAngle, outsideAngle, outsideVolume);
            }

            public Vec3 getSoundCone() {
                FloatBuffer buf1 = nativeOrder1(4).asFloatBuffer();
                FloatBuffer buf2 = nativeOrder2(4).asFloatBuffer();
                FloatBuffer buf3 = nativeOrder3(4).asFloatBuffer();
                sound.get3DConeSettings(buf1, buf2, buf3);
                return vector(buf1.get(), buf2.get(), buf3.get());
            }

            public void set3d(boolean value) {
                ByteBuffer buf = nativeOrder1(4);
                errorCheck(sound.getMode(buf.asIntBuffer()));
                int mode = buf.getInt(0);
                if (value) {
                    mode |= FMOD_3D;
                    mode &= ~FMOD_2D;
                    java.lang.System.out.println("SETTING 3D MODE");
                } else {
                    mode &= ~FMOD_3D;
                    mode |= FMOD_2D;
                }
                errorCheck(sound.setMode(mode));
                if (value) {
                    Vec3 cone = getSoundCone();
                    //java.lang.System.out.println("cone: "+cone.x + " " + cone.y + " " + cone.z);
                }
            }

            public boolean is3d() {
                ByteBuffer buf = nativeOrder1(4);
                errorCheck(sound.getMode(buf.asIntBuffer()));
                int mode = buf.getInt(0);
                return (mode & FMOD_3D)  != 0;
            }

            public void setPlaylist(int[] playlist) {
                this.playlist = new int[playlist.length];
                for (int i = 0; i < playlist.length; i++) {
                    this.playlist[i] = playlist[i];
                }
                ByteBuffer buf = nativeOrder1(playlist.length*4);
                buf.asIntBuffer().put(playlist);
                buf.rewind();
                java.lang.System.out.println("seting playlist sentence "+ playlist.length);
                errorCheck(sound.setSubSoundSentence(buf.asIntBuffer(), playlist.length));
            }

            public void setPlaylist(Subsound[] playlist) {
                this.playlist = new int[playlist.length];
                for (int i = 0; i < playlist.length; i++) {
                    if (playlist[i].getParent() != this) {
                        throw new RuntimeException("wrong parent");
                    }
                    this.playlist[i] = playlist[i].getIndex();
                }
                ByteBuffer buf = nativeOrder1(this.playlist.length*4);
                buf.asIntBuffer().put(this.playlist);
                buf.rewind();
                java.lang.System.out.println("setting sentence: "+ playlist.length);
                errorCheck(sound.setSubSoundSentence(buf.asIntBuffer(), playlist.length));
            }

            public int[] getPlaylistIndices() {
                int[] playlist = new int[this.playlist.length];
                for (int i = 0; i < this.playlist.length; i++) {
                    playlist[i] = this.playlist[i];
                }
                return playlist;
            }

            public void setSubsound(int i, 
                                    org.f3.media.audio.Sound sound) {
                java.lang.System.out.println("setting sub sound: "+ i);
                errorCheck(this.sound.setSubSound(i, ((SoundImpl)sound).sound));
            }

            public String getName() {
                if (name == null) {
                    if (sound != null) {
                        byte[] nameBytes = new byte[1024];
                        ByteBuffer nameBuf = ByteBuffer.allocateDirect(1024);
                        errorCheck(sound.getName(nameBuf, 1024));
                        nameBuf.get(nameBytes);
                        try {
                            String str = new String(nameBytes, "utf-8");
                            int zero = str.indexOf('\0');
                            if (zero >= 0) {
                                str = str.substring(0, zero);
                            }
                            name = str;
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return name;
            }

            public Subsound[] getSubsounds() {
                if (subSounds == null) {
                    if (sound != null) {
                        ByteBuffer buf = nativeOrder1(4);
                        sound.getNumSubSounds(buf.asIntBuffer());
                        int count = buf.getInt(0);
                        subSounds = new org.f3.media.audio.Subsound[count];
                        for (int i = 0; i < count; i++) {
                            Sound sub = new Sound();
                            errorCheck(sound.getSubSound(i, sub));
                            subSounds[i] = new SubsoundImpl(this, i, sub);
                        }
                    } 
                }
                return subSounds;
            }

            public int getChannels() {
                return channels;
            }

            protected void finalize() {
                java.lang.System.out.println("FINALIZE "+ this);
                dispose();
            }
        }

        public class StreamImpl extends SoundImpl implements Stream {
            URL url;
            StreamImpl(URL url, Sound sound) {
                super(sound);
                this.url = url;
            }
            public URL getURL() {
                return url;
            }
        }
        
        public class SampleImpl extends SoundImpl implements Sample {
            SampleImpl(Sound sound, ByteBuffer bb) {
                super(sound);
                this.buffer = bb;
            }
            ByteBuffer buffer;
            public int getPCMLength() {
                if (!sound.isNull()) {
                    ByteBuffer buf = nativeOrder1(8);
                    buf.asIntBuffer().put(0, 0);
                    buf.asIntBuffer().put(1, 0);
                    java.lang.System.out.println("getting sound length");
                    errorCheck(sound.getLength(buf.asIntBuffer(), 
                                               FMOD_TIMEUNIT_PCM));
                    return buf.getInt(0);
                }
                return 0;
            }
        }

        public class SignalImpl 
            extends SoundImpl 
            implements Signal, FMOD_SOUND_PCMREADCALLBACK, FMOD_SOUND_PCMSETPOSCALLBACK {
            SignalGenerator generator;
            int frequency;
            int bufferSize;
            int length;
            public int getLength() {
                return length;
            }
            public int getBufferSize() {
                return bufferSize;
            }
            /*
            public int getChannels() {
                return channels;
            }
            */
            public int getFrequency() {
                return frequency;
            }
            public SignalGenerator getSignalGenerator() {
                return generator;
            }
            SignalImpl(Sound sound,
                       int bufferSize,
                       int length,
                       int channels,
                       int frequency,
                       int format,
                       SignalGenerator generator) {
                super(sound);
                this.generator = generator;
                this.bufferSize = bufferSize;
                this.length = length;
                this.channels = channels;
                this.frequency = frequency;
                this.format = format;
            }

            public FMOD_RESULT FMOD_SOUND_PCMREADCALLBACK(Sound sound, ByteBuffer data, int datalen) {
                generator.generateSignal(data, datalen);
                return FMOD_OK;
            }

            public FMOD_RESULT FMOD_SOUND_PCMSETPOSCALLBACK(Sound sound, int subsound, int position, int postype) {
                // TBD
                return FMOD_OK;
            }
        }

        public AudioSystemImpl(System system) {
            this.system = system;
            systemMaster = new ChannelGroup();
            system.getMasterChannelGroup(systemMaster);            
            this.rootChannelGroup = new ChannelGroupImpl("", systemMaster);
        }

        List<Sound> soundDeleteQueue = new LinkedList();
        List<DSP> dspDeleteQueue = new LinkedList();
        List<Channel> channelDeleteQueue = new LinkedList();
        List<ChannelGroup> channelGroupDeleteQueue = new LinkedList();

        void collectResources() {
            synchronized(soundDeleteQueue) {
                if (soundDeleteQueue.size() > 0) {
                    for (Sound sound: soundDeleteQueue) {
                        if (!sound.isNull()) {
                            sound.release();
                        }
                    }
                    java.lang.System.out.println("releasing "+soundDeleteQueue.size()+" sounds");
                    soundDeleteQueue.clear();
                }
            }

            synchronized(channelDeleteQueue) {
                for (Channel channel: channelDeleteQueue) {
                    channel.stop();
                }
                channelDeleteQueue.clear();
            }

            synchronized(channelGroupDeleteQueue) {
                for (ChannelGroup channelGroup: channelGroupDeleteQueue) {
                    channelGroup.release();
                }
                channelGroupDeleteQueue.clear();
            }

            synchronized(dspDeleteQueue) {
                for (DSP dsp: dspDeleteQueue) {
                    dsp.release();
                }
                dspDeleteQueue.clear();
            }
            
        }

        void enqueueForDelete(Sound sound) {
            synchronized (soundDeleteQueue) {
                soundDeleteQueue.add(sound);
            }
        }


        void enqueueForDelete(DSP dsp) {
            synchronized (dspDeleteQueue) {
                dspDeleteQueue.add(dsp);
            }
        }

        void enqueueForDelete(Channel channel) {
            synchronized (channelDeleteQueue) {
                channelDeleteQueue.add(channel);
            }
        }

        void enqueueForDelete(ChannelGroup channelGroup) {
            synchronized (channelGroupDeleteQueue) {
                channelGroupDeleteQueue.add(channelGroup);
            }
        }
        
        protected void finalize() {
            if (system != null) {
                system.release();
                system = null;
            }
        }

        ChannelGroupImpl rootChannelGroup;
        ChannelGroup systemMaster;
        System system;
        ListenerImpl listener;

        public Listener getListener() {
            if (listener == null) {
                listener = new ListenerImpl(this);
            }
            return listener;
        }

        boolean listenerChanged = false;

        void listenerChanged(Listener listener) {
            listenerChanged = true;
        }
        
        FMOD_VECTOR pos = FMOD_VECTOR.create();
        FMOD_VECTOR vel = FMOD_VECTOR.create();
        FMOD_VECTOR fwd = FMOD_VECTOR.create();
        FMOD_VECTOR up = FMOD_VECTOR.create();

        public void doListenerChange(Listener listener) {
            Point3 v1 = listener.getLocation();
            Vec3 v2 = listener.getVelocity();
            Vec3 v3 = listener.getForwardOrientation();
            Vec3 v4 = listener.getUpwardOrientation();
            Tuple3 v5 = listener.getWorldScale();
            pos.setX(v1.get$x()); pos.setY(v1.get$y()); pos.setZ(v1.get$z());
            vel.setX(v2.get$x()); vel.setY(v2.get$y()); vel.setZ(v2.get$z());
            fwd.setX(v3.get$x()); fwd.setY(v3.get$y()); fwd.setZ(v3.get$z());
            up.setX(v4.get$x()); up.setY(v4.get$y()); up.setZ(v4.get$z());
            system.set3DListenerAttributes(0, pos, vel, fwd, up);
            system.set3DSettings(v5.get$x(), v5.get$y(), v5.get$z());
        }

        public org.f3.media.audio.ChannelGroup getRootChannelGroup() {
            return rootChannelGroup;
        }

        public void update() {
            collectResources();
            if (listenerChanged) {
                listenerChanged = false;
                doListenerChange(getListener());
            }
            if (system != null) {
                system.update();
            }
            //MasterTimer.flush();
        }

        public org.f3.media.audio.ChannelGroup createChannelGroup(String name) {
            ChannelGroup cg = new ChannelGroup();
            system.createChannelGroup(name, cg);
            ChannelGroupImpl result = new ChannelGroupImpl(name, cg);
            rootChannelGroup.addChannelGroup(result);
            return result;
        }

        public org.f3.media.audio.Channel createChannel() {
            return new FreeChannelImpl();
        }

        public Stream createStream(URL url) {
            Sound sound = new Sound();
            String str = url.toString();
            if (str.startsWith("file:")) {
                str = str.substring(5);
            }
            FMOD_RESULT result = 
                system.createStream(str,
                                    FMOD_DEFAULT,
                                    null,
                                    sound);
            try {
                errorCheck(result);
            } catch (Exception e) {
                throw new RuntimeException("Can't load stream: "+ url, e);
            }
            return new StreamImpl(url, sound);
        }

        public org.f3.media.audio.Stream 
            createStreamPlaylist(int frequency,
                                 int channels,
                                 int format,
                                 int playlistSize) {
            Sound sound = new Sound();
            FMOD_CREATESOUNDEXINFO info = FMOD_CREATESOUNDEXINFO.create();
            info.setNumSubsounds(playlistSize);
            info.setNumChannels(channels);
            info.setDefaultFrequency(frequency);
            info.setFormat(mapSoundFormat(format));
            try {
                FMOD_RESULT result = 
                    system.createStream((String)null,
                                        FMOD_LOOP_NORMAL | FMOD_OPENUSER,
                                        info,
                                        sound);
                errorCheck(result);
                StreamImpl ret = new StreamImpl(null, sound);
                ret.setPlaylistSize(playlistSize);
                java.lang.System.out.println("created stream play list");
                return ret;
            } catch (Exception e) {
                throw new RuntimeException("Can't create playlist:"+ playlistSize, e);
            } finally {
                info.release();
            }
        }

        public org.f3.media.audio.Sample 
            createSamplePlaylist(int frequency,
                                 int channels,
                                 int format,
                                 int playlistSize) {
            Sound sound = new Sound();
            FMOD_CREATESOUNDEXINFO info = FMOD_CREATESOUNDEXINFO.create();
            info.setNumSubsounds(playlistSize);
            info.setNumChannels(channels);
            info.setFormat(mapSoundFormat(format));
            info.setDefaultFrequency(frequency);
            try {
                FMOD_RESULT result = 
                    system.createSound((String)null,
                                       FMOD_DEFAULT,
                                       info,
                                       sound);
                errorCheck(result);
                SampleImpl ret = new SampleImpl(sound, null);
                ret.setPlaylistSize(playlistSize);
                return ret;
            } catch (Exception e) {
                throw new RuntimeException("Can't create playlist:"+ playlistSize, e);
            } finally {
                info.release();
            }
        }
        
        public org.f3.media.audio.Sample createSample(URL url) {
            String str = url.toString();
            if (str.startsWith("file:")) {
                str = str.substring(5);
            }
            try {
                if ("jar".equals(url.getProtocol())) {
                    return createSample(url.openStream());
                }
                Sound sound = new Sound();
                errorCheck(system.createSound(str, 
                                              FMOD_DEFAULT | FMOD_LOOP_NORMAL, 
                                              null,
                                              sound));
                return new SampleImpl(sound, null);
            } catch (Exception e) {
                throw new RuntimeException("failed to load sample: "+ url, e);
            }
        }

        public org.f3.media.audio.Sample createSample(InputStream is) {
            // TBD
            FMOD_CREATESOUNDEXINFO exinfo = FMOD_CREATESOUNDEXINFO.create();
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buf = new byte[8192];
                int count;
                while ((count = is.read(buf, 0, buf.length)) != -1) {
                    baos.write(buf, 0, count);
                }
                byte[] array = baos.toByteArray();
                ByteBuffer bb = ByteBuffer.allocateDirect(array.length);
                bb.put(array);
                bb.rewind();
                Sound sound = new Sound();
		exinfo.setLength(bb.capacity());
                FMOD_RESULT result = 
                    system.createSound(bb,  FMOD_OPENMEMORY | FMOD_LOOP_NORMAL , exinfo, sound);
                errorCheck(result);
                return new SampleImpl(sound, bb);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                exinfo.release();
            }
        }

        public abstract class AbstractDSP 
            implements org.f3.media.audio.DSP {
            DSP dsp;
            boolean active = true;
            boolean bypass = false;
            List<org.f3.media.audio.DSP> inputs = new ArrayList();
            List<org.f3.media.audio.DSP> outputs = new ArrayList();

            public void addInput(org.f3.media.audio.DSP input) {
                if (inputs.contains(input)) {
                    return;
                }
                inputs.add(input);
                dsp.addInput(((AbstractDSP)input).dsp, null);
                ((AbstractDSP)input).addOutput(this);
            }

            public void disconnectFrom(org.f3.media.audio.DSP dsp) {
                inputs.remove(dsp);
                outputs.remove(dsp);
                errorCheck(this.dsp.disconnectFrom(((AbstractDSP)dsp).dsp));
            }

            public void disconnect() {
                for (org.f3.media.audio.DSP i : inputs) {
                    ((AbstractDSP)i).removeOutput(this);
                }
                for (org.f3.media.audio.DSP i : outputs) {
                    ((AbstractDSP)i).removeInput(this);
                }
                inputs.clear();
                outputs.clear();
                errorCheck(dsp.disconnectAll(true, true));
            }

            public void reset() {
                errorCheck(dsp.reset());
            }
            
            void addOutput(org.f3.media.audio.DSP dsp) {
                outputs.add(dsp);
            }

            void removeOutput(org.f3.media.audio.DSP dsp) {
                outputs.remove(dsp);
            }
            
            void removeInput(org.f3.media.audio.DSP dsp) {
                inputs.remove(dsp);
            }
            
            public org.f3.media.audio.DSP[] getInputs() {
                org.f3.media.audio.DSP[] result = 
                    new org.f3.media.audio.DSP[inputs.size()];
                inputs.toArray(result);
                return result;
            }

            public org.f3.media.audio.DSP[] getOutputs() {
                ByteBuffer buf = nativeOrder1(4);
                dsp.getNumOutputs(buf.asIntBuffer());
                int count = buf.getInt(0);
                java.lang.System.out.println("output count="+count);
                org.f3.media.audio.DSP[] result = 
                    new org.f3.media.audio.DSP[outputs.size()];
                outputs.toArray(result);
                return result;
            }
            
            public void setActive(boolean value) {
                active = value;
                dsp.setActive(value);
            }

            public boolean isActive() {
                return active;
            }

            public void setBypass(boolean value) {
                bypass = value;
                dsp.setBypass(value);
            }
            
            public boolean isBypass() {
                return bypass;
            }

            protected void finalize() {
                if (dsp != null) {
                    enqueueForDelete(dsp);
                }
            }
        }

        public class CustomDSPImpl extends AbstractDSP
                                           implements FMOD_DSP_READCALLBACK {
            Processor processor;

            public CustomDSPImpl(Processor processor) {
                this.processor = processor;
                dsp = new DSP();
                FMOD_DSP_DESCRIPTION dspdesc = FMOD_DSP_DESCRIPTION.create(); 
		
                dspdesc.setName("My first DSP unit");
                dspdesc.setChannels(0);					       
                dspdesc.setRead(this); 
                errorCheck(system.createDSP(dspdesc, dsp)); 
            }

            public FMOD_RESULT 
                FMOD_DSP_READCALLBACK(FMOD_DSP_STATE dsp_state, 
                                      FloatBuffer inbuffer, 
                                      FloatBuffer outbuffer,
                                      int length, 
                                      int inchannels, int outchannels)
		{
                    processor.process(inbuffer, outbuffer, length,
                                      inchannels, outchannels);
                    return FMOD_OK;
                }
        }

        public class OscillatorImpl extends AbstractDSP
            implements org.f3.media.audio.DSP.Oscillator {

            int type = SINE;
            float rate = 220.0f;

            // Waveform type
            public void setType(int type) {
                this.type = type;
                errorCheck(dsp.setParameter(FMOD_DSP_OSCILLATOR_TYPE.asInt(),
                                            type));
            }

            public int getType() {
                return type;
            }

            //Frequency of the sinewave in hz. 1.0 to 22000.0. Default = 220.0. 
            public void setRate(float value) {
                this.rate = value;
                errorCheck(dsp.setParameter(FMOD_DSP_OSCILLATOR_RATE.asInt(),
                                            value));
            }

            public float getRate() {
                return rate;
            }

            OscillatorImpl() {
                dsp = new DSP();
                errorCheck(system.createDSPByType(FMOD_DSP_TYPE_OSCILLATOR, 
                                                  dsp));
            }
        }

        public class DistortionImpl extends AbstractDSP 
            implements org.f3.media.audio.DSP.Distortion {

            float level = 0.5f;

            public float getLevel() {
                return level;
            }

            public void setLevel(float value) {
                level = value;
                errorCheck(dsp.setParameter(FMOD_DSP_DISTORTION_LEVEL.asInt(),
                                            value));
            }

            public DistortionImpl() {
                dsp = new DSP();
                errorCheck(system.createDSPByType(FMOD_DSP_TYPE_DISTORTION, dsp));
            }
        }

        public class SFXReverbImpl extends AbstractDSP
            implements org.f3.media.audio.DSP.SFXReverb {

            float dryLevel = 0, roomEffectLevel = 0, roomEffectHighFrequencyLevel = 0,
                roomEffectRolloffFactor = 10, decayTime = 1, decayRatio = 0.5f,
                reflectionsLevel = -10000, reflectionsDelay = 0.02f,
                reverbLevel = 0, reverbDelay = 0.04f, diffusion = 100, density = 100,
                highFrequencyReference = 5000, roomEffectLowFrequencyLevel = 250,
                lowFrequencyReference = 250;
                

            public float getDryLevel() { return dryLevel; }
            public void setDryLevel(float value) {
                dryLevel = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_DRYLEVEL.asInt(),
                                            value));
            }
            
            // Room effect level in mB
            // -10000.0..0.0, default 0.0
            public float getRoomEffectLevel() { return roomEffectLevel; }
            public void setRoomEffectLevel(float value) {
                roomEffectLevel = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_ROOM.asInt(),
                                            value));
            }
            
            // Room effect High Frequency
            // Room effect level in mB
            // -10000.0..0.0, default 0.0
            public float getRoomEffectHighFrequencyLevel() {
                return roomEffectHighFrequencyLevel;
            }
            public void setRoomEffectHighFrequencyLevel(float value) {
                roomEffectHighFrequencyLevel = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_ROOMHF.asInt(),
                                            value));
            }
            
            // 0.0..10.0, default: 10.0
            public float getRoomEffectRolloffFactor() {
                return roomEffectRolloffFactor;
            }
            public void setRoomEffectRolloffFactor(float value) {
                roomEffectRolloffFactor = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_ROOMROLLOFFFACTOR.asInt(), value));
            }
            
            // 0.1..20.0, default 1.0
            public float getDecayTime() {
                return decayTime;
            }
            public void setDecayTime(float value) {
                decayTime = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_DECAYTIME.asInt(),
                                            value));
            }
            
            // 0.1..2.0, default 0.5
            public float getDecayRatio() {
                return decayRatio;
            }
            public void setDecayRatio(float value) {
                decayRatio = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_DECAYHFRATIO.asInt(),
                                            value));
            }
            
            // -10000.0..1000.0, default: -10000.0
            public float getReflectionsLevel() {
                return reflectionsLevel;
            }
            public void setReflectionsLevel(float value) {
                reflectionsLevel = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_REFLECTIONSLEVEL.asInt(),
                                            value));
            }
            
            // 0.0..0.3, default: 0.02
            public float getReflectionsDelay() {
                return reflectionsDelay;
            }
            public void setReflectionsDelay(float value) {
                reflectionsDelay = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_REFLECTIONSDELAY.asInt(),
                                            value));
            }
            
            // Late reverberation level in mB
            // -10000.0..2000.0, default: 0.0
            public float getReverbLevel() {
                return reverbLevel;
            }
            public void setReverbLevel(float value) {
                reverbLevel = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_REVERBLEVEL.asInt(),
                                            value));
            }
            
            // Late reverberation delay in seconds
            // 0.0..0.1, default: 0.04
            public float getReverbDelay() {
                return reverbDelay;
            }
            public void setReverbDelay(float value) {
                reverbDelay = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_REVERBDELAY.asInt(),
                                            value));
            }
            
            // echo density in percent
            // 0.0..100.0, default: 100.0
            public float getDiffusion() {
                return diffusion;
            }
            public void setDiffusion(float value) {
                diffusion = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_DIFFUSION.asInt(),
                                            value));
            }
            
            // modal density in percent
            // 0.0..100.0, default: 100.0
            public float getDensity() {
                return density;
            }
            public void setDensity(float value) {
                density = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_DENSITY.asInt(),
                                            value));
            }
            
            // Reference highg frequence in Hz
            // 20.0..20000.0, default: 5000.0
            public float getHighFrequencyReference() {
                return highFrequencyReference;
            }
            public void setHighFrequencyReference(float value) {
                highFrequencyReference = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_HFREFERENCE.asInt(),
                                            value));
            }
            
            
            // Room effect Low Frequency
            // Room effect level in mB
            // -10000.0..0.0, default 0.0
            public float getRoomEffectLowFrequencyLevel() {
                return roomEffectLowFrequencyLevel;
            }
            public void setRoomEffectLowFrequencyLevel(float value) {
                roomEffectLowFrequencyLevel = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_ROOMLF.asInt(),
                                            value));
            }
            
            // Reference low frequency in Hz
            // 20.0..1000.0, default: 250.0
            public float getLowFrequencyReference() {
                return lowFrequencyReference;
            }
            public void setLowFrequencyReference(float value) {
                lowFrequencyReference = value;
                errorCheck(dsp.setParameter(FMOD_DSP_SFXREVERB.FMOD_DSP_SFXREVERB_LFREFERENCE.asInt(),
                                            value));
            }

            public SFXReverbImpl() {
                dsp = new DSP();
                errorCheck(system.createDSPByType(FMOD_DSP_TYPE_SFXREVERB, dsp));
            }
            
        }

        public class ReverbImpl extends AbstractDSP 
            implements org.f3.media.audio.DSP.Reverb {

            float damp = 0.5f;
            float wetMix = 0.33f;
            float dryMix = 1.0f;
            boolean freeze = false;

            public float getDamp() {
                return damp;
            }

            public void setDamp(float value) {
                damp = value;
                errorCheck(dsp.setParameter(FMOD_DSP_REVERB_DAMP.asInt(),
                                            value));
            }

            public float getWetMix() {
                return wetMix;
            }

            public void setWetMix(float value) {
                wetMix = value;
                errorCheck(dsp.setParameter(FMOD_DSP_REVERB_WETMIX.asInt(),
                                            value));
            }

            public float getDryMix() {
                return dryMix;
            }

            public void setDryMix(float value) {
                dryMix = value;
                errorCheck(dsp.setParameter(FMOD_DSP_REVERB_DRYMIX.asInt(),
                                            value));
            }

            public boolean getFreeze() {
                return freeze;
            }

            public void setFreeze(boolean value) {
                freeze = value;
                errorCheck(dsp.setParameter(FMOD_DSP_REVERB_MODE.asInt(),
                                            value ? 1 : 0));
            }

            public ReverbImpl() {
                dsp = new DSP();
                errorCheck(system.createDSPByType(FMOD_DSP_TYPE_REVERB, dsp));
            }
        }

        public class EchoImpl extends AbstractDSP 
            implements org.f3.media.audio.DSP.Echo {
            int maxChannels = 0;
            int delay = 500;
            float decayRatio = 0.5f;
            float dryMix = 1;
            float wetMix = 1;
            //0..16

            public int getMaxChannels() {
                return maxChannels;
            }

            public void setMaxChannels(int value) {
                maxChannels = value;
                errorCheck(dsp.setParameter(FMOD_DSP_ECHO_MAXCHANNELS.asInt(),
                                            value));
            }
            
            // 0.0..1.0, default 0.5

            //Echo delay in ms. 10 to 5000. Default = 500.
            public int getDelay() {
                return delay;
            }

            public void setDelay(int value) {
                delay = value;
                errorCheck(dsp.setParameter(FMOD_DSP_ECHO_MAXCHANNELS.asInt(),
                                            value));
            }
            
            public float getDecayRatio() {
                return decayRatio;
            }

            public void setDecayRatio(float value) {
                decayRatio = value;
                errorCheck(dsp.setParameter(FMOD_DSP_ECHO_DECAYRATIO.asInt(),
                                            value));
            }
            
            public float getDryMix() {
                return dryMix;
            }

            public void setDryMix(float value) {
                errorCheck(dsp.setParameter(FMOD_DSP_ECHO_DRYMIX.asInt(),
                                            value));
            }
            
            public float getWetMix() {
                return wetMix;
            }

            public void setWetMix(float value) {
                errorCheck(dsp.setParameter(FMOD_DSP_ECHO_WETMIX.asInt(),
                                            value));
            }

            public EchoImpl() {
                dsp = new DSP();
                errorCheck(system.createDSPByType(FMOD_DSP_TYPE_ECHO, dsp));
            }
        }

        FMOD_SOUND_FORMAT mapSoundFormat(int format) {
            FMOD_SOUND_FORMAT fmod = null;
            switch (format) {
            default:
            case SoundFormat.NONE:
                fmod = FMOD_SOUND_FORMAT_NONE;
                break;
            case SoundFormat.PCM8:
                fmod = FMOD_SOUND_FORMAT_PCM8;
                break;
            case SoundFormat.PCM16:
                fmod = FMOD_SOUND_FORMAT_PCM16;
                break;
            case SoundFormat.PCM24:
                fmod = FMOD_SOUND_FORMAT_PCM24;
                break;
            case SoundFormat.PCM32:
                fmod = FMOD_SOUND_FORMAT_PCM32;
                break;
            case SoundFormat.PCMFLOAT:
                fmod = FMOD_SOUND_FORMAT_PCMFLOAT;
                break;
            case SoundFormat.GCADPCM:
                fmod = FMOD_SOUND_FORMAT_GCADPCM;
                break;
            case SoundFormat.IMAADPCM:
                fmod = FMOD_SOUND_FORMAT_IMAADPCM;
                break;
            case SoundFormat.VAG:
                fmod = FMOD_SOUND_FORMAT_VAG;
                break;
            case SoundFormat.XMA:
                fmod = FMOD_SOUND_FORMAT_XMA;
                break;
            case SoundFormat.MPEG:
                fmod = FMOD_SOUND_FORMAT_MPEG;
                break;
            }
            java.lang.System.out.println("format="+fmod);
            return fmod;
        }

        public Signal createSignal(SignalGenerator generator,
                                   int bufferSize,
                                   int length,
                                   int channels,
                                   int frequency,
                                   int format) {
            // TBD
            FMOD_SOUND_FORMAT fmod = mapSoundFormat(format);
            Sound sound = new Sound();
            SignalImpl sig = new SignalImpl(sound,
                                            bufferSize,
                                            length,
                                            channels,
                                            frequency,
                                            format,
                                            generator);
            FMOD_CREATESOUNDEXINFO createsoundexinfo = FMOD_CREATESOUNDEXINFO.create();
	    createsoundexinfo.setDecodeBufferSize(bufferSize);
	    createsoundexinfo.setLength(length);
	    createsoundexinfo.setNumChannels(channels);
	    createsoundexinfo.setDefaultFrequency(frequency);	
	    createsoundexinfo.setFormat(fmod);
	    createsoundexinfo.setPcmReadCallback(sig);
	    createsoundexinfo.setPcmSetPosCallback(sig);
            errorCheck(system.createSound((String)null,
                                          FMOD_OPENUSER | FMOD_LOOP_NORMAL,
                                          createsoundexinfo, sound));
            return sig;
        }

        // User-defined
        public org.f3.media.audio.DSP createDSP(Processor processor) {
            return new CustomDSPImpl(processor);
        }

        // Bulitins:

        //This unit does nothing but take inputs and mix them together 
        //then feed the result to the soundcard unit.
        public org.f3.media.audio.DSP createMixer() {
            // TBD
            return null;
        }
    
        //This unit generates sine/square/saw/triangle or noise tones.
        public org.f3.media.audio.DSP.Oscillator createOscillator() {
            return new OscillatorImpl();
        }
    
        //This unit filters sound using a high quality, resonant lowpass filter 
        // algorithm but consumes more CPU time.
        public org.f3.media.audio.DSP createLowpass() {
            // TBD
            return null;
        }
    
        //This unit filters sound using a resonant lowpass filter algorithm that 
        //is used in Impulse Tracker, but with limited cutoff range (0 to 8060hz).
        public org.f3.media.audio.DSP createITLowpass() {
            // TBD
            return null;
        }

        //This unit filters sound using a resonant highpass filter algorithm.
        public org.f3.media.audio.DSP createHighpass() {
            // TBD
            return null;
        }

        //This unit produces an echo on the sound and fades out at the desired 
        //rate.
        public org.f3.media.audio.DSP.Echo createEcho() {
            return new EchoImpl();
        }

        //This unit produces a flange effect on the sound.
        public org.f3.media.audio.DSP createFlange() {
            // TBD
            return null;
        }

        //This unit distorts the sound.
        public org.f3.media.audio.DSP.Distortion createDistortion() {
            return new DistortionImpl();
        }
        
        //This unit normalizes or amplifies the sound to a certain level.
        public org.f3.media.audio.DSP createNormalize() {
            // TBD
            return null;
        }

        //This unit attenuates or amplifies a selected frequency range.
        public org.f3.media.audio.DSP createParamEQ() {
            // TBD
            return null;
        }

        //This unit bends the pitch of a sound without changing the speed of 
        // playback.
        public org.f3.media.audio.DSP createPitchShift() {
            // TBD
            return null;
        }

        //This unit produces a chorus effect on the sound.
        public org.f3.media.audio.DSP createChorus() {
            // TBD
            return null;
        }

        //This unit produces a reverb effect on the sound.
        public org.f3.media.audio.DSP.Reverb createReverb() {
            return new ReverbImpl();
        }

        //This unit produces an echo on the sound and fades out at the desired 
        //rate as is used in Impulse Tracker.
        public org.f3.media.audio.DSP createITEcho() {
            // TBD
            return null;
        }

        //This unit implements dynamic compression (linked multichannel, wideband)
        public org.f3.media.audio.DSP createCompressor() {
            // TBD
            return null;
        }

        //This unit implements SFX reverb
        public org.f3.media.audio.DSP.SFXReverb createSFXReverb() {
            return new SFXReverbImpl();
        }

        //This unit filters sound using a simple lowpass with no resonance, 
        //but has flexible cutoff and is fast. 
        public org.f3.media.audio.DSP createLowpassSimple() {
            // TBD
            return null;
        }
    }

    private  void errorCheck(FMOD_RESULT result) {
        try {
            errorCheck1(result);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    private  void errorCheck1(FMOD_RESULT result) {
        if (result != FMOD_RESULT.FMOD_OK) {
            RuntimeException t = new RuntimeException(FmodEx.FMOD_ErrorString(result));
            //java.lang.System.out.println(t);
            throw t;
        }
    }

    private  boolean InitFlag;

    private  void Init() {

        if (!InitFlag) {
            InitFlag = true;
            try {
                //                java.lang.reflect.Field field = 
                //                    Init.class.getField("DEBUG");
                //                field.set(null, Boolean.TRUE);
                Init.loadLibraries(INIT_MODES.INIT_FMOD_EX);
                java.lang.System.out.println("loaded fmod libraries");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    class AudioDriverImpl implements AudioDriver {
        int index;
        String name;
        int minfreq;
        int maxfreq;
        int caps;
        public AudioDriverImpl(int index, 
                               String name,
                               int caps,
                               int minfreq,
                               int maxfreq) {
            this.index = index;
            this.name = name;
            this.caps = caps;
            this.minfreq = minfreq;
            this.maxfreq = maxfreq;
        }

        public String getName() {
            return name;
        }

        public int getMinimumFrequency() {
            return minfreq;
        }

        public int getMaximumFrequency() {
            return maxfreq;
        }

        public int getCapabilities() {
            return caps;
        }
        
        public AudioSystem createAudioSystem() {
            System system = new System();
            errorCheck(FmodEx.System_Create(system));
            system.setDriver(index);
            int mixBufferSize = FmodAudioSystemFactory.this.mixBufferSize;
            int ringBufferCount = FmodAudioSystemFactory.this.ringBufferCount;
            ByteBuffer b1 = nativeOrder1(4);
            ByteBuffer b2 = nativeOrder2(4);
            if (mixBufferSize != DEFAULT || ringBufferCount != DEFAULT) {
                errorCheck(system.getDSPBufferSize(b1.asIntBuffer(),
                                                   b2.asIntBuffer()));
                
                if (mixBufferSize == DEFAULT) {
                    mixBufferSize = b1.getInt(0);
                }
                if (ringBufferCount == DEFAULT) {
                    ringBufferCount = b2.getInt(0);
                }
                java.lang.System.out.println("setting dsp buffer size "+ mixBufferSize + " count = " + ringBufferCount);
                errorCheck(system.setDSPBufferSize(mixBufferSize, ringBufferCount));
            }
            errorCheck(system.init(maxChannels, FMOD_INIT_NORMAL |
                                   FMOD_INIT_3D_RIGHTHANDED, null));
            system.getDSPBufferSize(b1.asIntBuffer(),
                                    b2.asIntBuffer());
            java.lang.System.out.println("actual mix buffer size "+ b1.getInt(0) + " count="+b2.getInt(0));
            errorCheck(system.set3DSettings(1, 1, 1));                
            return new AudioSystemImpl(system);
        }

    }
    
    public AudioDriver[] getAudioDrivers() {
        if (drivers == null) {
            Init();
            System system = new System();
            errorCheck(FmodEx.System_Create(system));
            ByteBuffer buf = nativeOrder(4);
            errorCheck(system.getNumDrivers(buf.asIntBuffer()));
            int numDrivers = buf.get(0);
            drivers = new AudioDriver[numDrivers];
            ByteBuffer driverName = ByteBuffer.allocateDirect(256);
            byte[] nameBytes = new byte[256];
            ByteBuffer buf2 = nativeOrder(4);
            ByteBuffer buf3 = nativeOrder(4);
            for (int i = 0; i < numDrivers; i++) {
                driverName.rewind();
                errorCheck(system.getDriverInfo(i, driverName, 
                                                driverName.capacity(), null));
                errorCheck(system.getDriverCaps(i, 
                                                buf.asIntBuffer(), 
                                                buf2.asIntBuffer(), 
                                                buf3.asIntBuffer(), 
                                                null));
                int caps = buf.get(0);
                int minfreq = buf2.get(0);
                int maxfreq = buf3.get(0);
                driverName.rewind();
                driverName.get(nameBytes);
                String name = new String(nameBytes);
                int term = name.indexOf('\0');
                if (term > 0) {
                    name = name.substring(0, term);
                }
                drivers[i] = new AudioDriverImpl(i,
                                                 name,
                                                 caps, 
                                                 minfreq, 
                                                 maxfreq);
            }
            system.release();
        }
        return drivers;
    }

    AudioDriver[] drivers;

    public AudioSystem createAudioSystem() {
        Init();
	System system = new System();
        FMOD_RESULT result = FmodEx.System_Create(system);
        int version;
        errorCheck(result);
        ByteBuffer buffer = nativeOrder(4);
        result = system.getVersion(buffer.asIntBuffer());
        errorCheck(result);
        version = buffer.getInt(0);
        if (version < FMOD_VERSION) {
            throw new RuntimeException("Bad FMOD version, expected greater than " + FMOD_VERSION);
        }
        int mixBufferSize = this.mixBufferSize;
        int ringBufferCount = this.ringBufferCount;
        ByteBuffer b1 = nativeOrder1(4);
        ByteBuffer b2 = nativeOrder2(4);
        if (mixBufferSize != DEFAULT || ringBufferCount != DEFAULT) {
            errorCheck(system.getDSPBufferSize(b1.asIntBuffer(),
                                               b2.asIntBuffer()));
            if (mixBufferSize == DEFAULT) {
                mixBufferSize = b1.getInt(0);
            }
            if (ringBufferCount == DEFAULT) {
                ringBufferCount = b2.getInt(0);
            }
            java.lang.System.out.println("setting dsp buffer size "+ mixBufferSize + " count = " + ringBufferCount);
            errorCheck(system.setDSPBufferSize(mixBufferSize, ringBufferCount));
        }
        result = system.init(maxChannels, FMOD_INIT_NORMAL |
                             FMOD_INIT_3D_RIGHTHANDED, null);
        errorCheck(result);
        errorCheck(system.getDSPBufferSize(b1.asIntBuffer(),
                                           b2.asIntBuffer()));
        java.lang.System.out.println("actual mix buffer size "+ b1.getInt(0) + " count="+b2.getInt(0));
        errorCheck(system.set3DSettings(1, 1, 1));
        return new AudioSystemImpl(system);
    }

    int mixBufferSize = DEFAULT;
    int ringBufferCount = DEFAULT;
    int maxChannels = 1000;

    public void setMaxChannels(int value ) {
        if (value == DEFAULT) {
            maxChannels = 1000;
        } else {
            maxChannels = value;
        }
    }

    public int getMaxChannels() {
        return maxChannels;
    }

    public void setMixBufferSize(int value) {
        mixBufferSize = value;
    }

    public int getMixBufferSize() {
        return mixBufferSize;
    }

    public void setMixBufferCount(int value) {
        ringBufferCount = value;
    }

    public int getMixBufferCount() {
        return ringBufferCount;
    }
}
