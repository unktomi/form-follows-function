package f3.media.scene;
import java.nio.*;

public class BufferUtils {

    static public FloatBuffer clone(FloatBuffer src) {
	src.rewind();
	FloatBuffer copy = createFloatBuffer(src.limit());
	copy.put(src);
	return copy;
    }

    static public ByteBuffer createByteBuffer(int limit) {
        ByteBuffer buf = ByteBuffer.allocateDirect(limit).order(ByteOrder.nativeOrder());
        buf.clear();
        return buf;
    }
    static public FloatBuffer createVector3Buffer(int vertices) {
        return createFloatBuffer(vertices*3);
    }
    static public FloatBuffer createVector2Buffer(int vertices) {
        return createFloatBuffer(vertices*2);
    }
    static public FloatBuffer createFloatBuffer(int limit) {
        FloatBuffer buf = 
            ByteBuffer.allocateDirect(4*limit).order(ByteOrder.nativeOrder()).asFloatBuffer();
        buf.clear();
        return buf;
    }
    static public IntBuffer createIntBuffer(int limit) {
        IntBuffer buf = 
            ByteBuffer.allocateDirect(4*limit).order(ByteOrder.nativeOrder()).asIntBuffer();
        buf.clear();
        return buf;
    }
}