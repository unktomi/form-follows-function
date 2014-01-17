package org.f3.media.audio.impl.fmod;
import org.f3.media.audio.*;
import f3.media.scene.*;
import org.f3.media.audio.Channel;
import org.f3.media.audio.Sound;
import f3.math.*;

public class SoundLoaderImpl implements SoundLoader {

    FmodAudioSystemFactory fac = new FmodAudioSystemFactory();
    AudioSystem sys;

    
    final AbstractSoundListener listener = new AbstractSoundListener() {
            public void setLocation(Point3 pos) {
                sys.getListener().setLocation(pos);
            }
            public void setLocation(float x, float y, float z) {
                sys.getListener().setLocation(x, y, z);
            }
            public Point3 getLocation() {
                return sys.getListener().getLocation();
            }
            public void setVelocity(Vec3 velocity) {
                sys.getListener().setVelocity(velocity);
            }
            public void setVelocity(float x, float y, float z) {
                sys.getListener().setVelocity(x, y, z);
            }
            public Vec3 getVelocity() {
                return sys.getListener().getVelocity();
            }
            public void setForwardOrientation(Vec3 vec) {
                sys.getListener().setForwardOrientation(vec);
            }
            public void setForwardOrientation(float x, float y, float z) {
                sys.getListener().setForwardOrientation(x, y, z);
            }
            public Vec3 getForwardOrientation() {
                return sys.getListener().getForwardOrientation();
            }
            public void setUpwardOrientation(Vec3 vec) {
                sys.getListener().setUpwardOrientation(vec);
            }
            public void setUpwardOrientation(float x, float y, float z) {
                sys.getListener().setUpwardOrientation(x, y, z);
            }
            public Vec3 getUpwardOrientation() {
                return sys.getListener().getUpwardOrientation();
            }
            public void setWorldScale(float x, float y, float z) {
                sys.getListener().setWorldScale(x, y, z);
            }
            public Tuple3 getWorldScale() {
                return sys.getListener().getWorldScale();
            }
        };

    public AbstractSoundListener getListener() {
        return listener;
    }

    AudioSystem getAudioSystem() {
        if (sys == null) {
            sys = fac.createAudioSystem();
        }
        return sys;
    }
        
    public void update() {
        getAudioSystem().update();
    }

    class SoundImpl implements AbstractSound {
        Channel channel;
        Sound sound;
        public long getDuration() {
            return sound.getDurationMillis();
        }
        public boolean is3D() {
            return sound.is3d();
        }
        public void set3D(boolean value) {
            sound.set3d(value);
        }
        public SoundImpl(Channel channel, Sound sound) {
            this.channel = channel;
            this.sound = sound;
        }
        public void setPaused(boolean value) {
            channel.setPaused(value);
        }
        public boolean isPaused() {
            return channel.isPaused();
        }

        // 0.0..1.0
        public void setVolume(float value) {
            channel.setVolume(value);
        }

        public float getVolume() {
            return channel.getVolume();
        }

        public void setMute(boolean value) {
            channel.setMute(value);
        }
        public boolean isMute() {
            return channel.isMute();
        }

        public void stop() {
            channel.stop();
        }

        public float[] getSpectrum(int ch, int size) {
            return channel.getSpectrum(ch, size);
        }
        
        public float[] getWaveData(int ch, int size) {
            return channel.getWaveData(ch, size);
        }

        public void play() {
            channel.play(sound);
        }

        public boolean isPlaying() {
            return channel.isPlaying();
        }

        public long getDelay() {
            return channel.getDelay();
        }

        public void setDelay(long value) {
            channel.setDelay(value);
        }

        public void setFrequency(float value) {
            channel.setFrequency(value);
        }

        public float getFrequency() {
            return channel.getFrequency();
        }

        // -1.0..1.0
        public void setPan(float value) {
            channel.setPan(value);
        }

        public float getPan() {
            return channel.getPan();
        }

        // -1: loop forever
        //  0: 1 shot (no loop)
        //  1..N (loop N times)
        //  Default: -1
        public void setLoopCount(int value) {
            channel.setLoopCount(value);
        }
        public int getLoopCount() {
            return channel.getLoopCount();
        }

        public void setLoopPoints(int start, int end) {
            channel.setLoopPoints(start, end);
        }

        public int getLoopStart() {
            return channel.getLoopStart();
        }

        public int getLoopEnd() {
            return channel.getLoopEnd();
        }

        public void setLocation(Point3 pt) {
            channel.setLocation(pt);
        }

        public void setLocation(float x, float y, float z) {
            channel.setLocation(x, y, z);
        }

        public Point3 getLocation() {
            return channel.getLocation();
        }

        public void setVelocity(Vec3 vec) {
            channel.setVelocity(vec);
        }
        
        public void setVelocity(float x, float y, float z) {
            channel.setVelocity(x, y, z);
        }

        public Vec3 getVelocity() {
            return channel.getVelocity();
        }

        public float getAudibility() {
            return channel.getAudibility();
        }

        public int getPosition() {
            return channel.getPosition();
        }

        public void setPosition(int millis) {
            channel.setPosition(millis);
        }

        public void setMinMaxDistance(float min, float max) {
            channel.setMinMaxDistance(min, max);
        }

        public float getMinDistance() {
            return channel.getMinDistance();
        }

        public float getMaxDistance() {
            return channel.getMaxDistance();
        }

        public void setSoundCone(float insideAngle, float outsideAngle, float outsideVolume) {
            channel.setSoundCone(insideAngle, outsideAngle, outsideVolume);
        }

        public Tuple3 getSoundCone() {
            return channel.getSoundCone();
        }

        public void setConeOrientation(float x, float y, float z) {
            channel.setConeOrientation(x, y, z);
        }

        public Vec3 getConeOrientation() {
            return channel.getConeOrientation();
        }
    }

    public AbstractSound loadSample(String url) {
        try {
            Sample sound = getAudioSystem().createSample(new java.net.URL(url));
            return new SoundImpl(getAudioSystem().createChannel(), sound);
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }

    public AbstractSound loadStream(String url) {
        try {
            Stream sound = getAudioSystem().createStream(new java.net.URL(url));
            return new SoundImpl(getAudioSystem().createChannel(), sound);
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
    }
}