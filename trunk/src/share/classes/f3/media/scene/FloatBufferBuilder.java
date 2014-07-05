package f3.media.scene;
import java.nio.*;

public class FloatBufferBuilder {

    static final int INITIAL_CHUNK_SIZE = 128;
    
    static class Chunk {
        float[] storage;
        int size;
        Chunk(int size) {
            this.size = size;
            storage = new float[size];
        }
        Chunk next;
    }

    FloatBuffer cached;
    int cachedCapacity = -1;
    Chunk head;
    
    // There are issues with direct buffers: a minimum 4K allocation
    // size on some platforms, needing to GC the direct buffer
    // instance before its storage can be reclaimed, lack of
    // intrinsics for single-element get() / put() operations on some
    // smaller JVMs. For this reason we assemble into a temporary Java
    // array and copy things over at the last minute.
    private Chunk curChunk;
    private int curIndex;
    private int chunkIndex;

    /** reuses storage */

    public void reset() {
        curIndex = chunkIndex = 0;
        curChunk = head;
        if (cached != null) {
            cached.clear();
        }
    }

    /** frees storage */

    public void clear() {
        curIndex = chunkIndex = 0;
        head = curChunk = null;
        cached = null;
    }

    void expandBuffer(int count) {
        int size = 2;
        final int limit = curIndex + count + INITIAL_CHUNK_SIZE;
        while (size < limit) {
            size <<= 1;
        }
        head = curChunk = new Chunk(size);
        for (int i = 0; i< curIndex; i++) {
            curChunk.storage[i] = cached.get(i);
        }
        cached = null;
        chunkIndex = curIndex;
        cachedCapacity = -1;
    }

    final public void reserve(int count) {
        if (cached != null && cachedCapacity < curIndex+count) {
            expandBuffer(count);
        }
    }

    void addToBuffer(float val1) {
        cached.put(curIndex++, val1);
    }

    void addToBuffer(float val1, float val2) {
        cached.put(curIndex++, val1);
        cached.put(curIndex++, val2);
    }

    void addToBuffer(float val1, float val2, float val3) {
        cached.put(curIndex++, val1);
        cached.put(curIndex++, val2);
        cached.put(curIndex++, val3);
    }

    void addToBuffer(float val1, float val2, float val3, float val4) {
        cached.put(curIndex++, val1);
        cached.put(curIndex++, val2);
        cached.put(curIndex++, val3);
        cached.put(curIndex++, val4);
    }

    void allocateChunk() {
        head = curChunk = new Chunk(INITIAL_CHUNK_SIZE);
        chunkIndex = 0;
    }

    void reallocateChunk() {
        if (curChunk.next == null) {
            curChunk.next = new Chunk(curChunk.size * 2);
        }
        chunkIndex = 0;
        curChunk = curChunk.next;
    }

    void addToChunk(float val) {
        if (curChunk == null) {
            allocateChunk();
        }
        if (chunkIndex == curChunk.size) {
            reallocateChunk();
        }
        curChunk.storage[chunkIndex++] = val;
        curIndex++;
    }

    public FloatBufferBuilder put(float val) {
        add(val);
        return this;
    }

    public void add(float val) {
        reserve(1);
        if (cached != null) {
            addToBuffer(val);
        } else {
            addToChunk(val);
        }
    }

    public void add(float val1, float val2) {
        reserve(2);
        if (cached != null) {
            addToBuffer(val1, val2);
        } else {
            addToChunk(val1);
            addToChunk(val2);
        }
    }

    void addToChunk(float val1, float val2, float val3) {
        addToChunk(val1);
        addToChunk(val2);
        addToChunk(val3);
    }


    final public void add(float val1, float val2, float val3) {
        reserve(3);
        if (cached != null) {
            addToBuffer(val1, val2, val3);
        } else {
            addToChunk(val1, val2, val3);
        }
    }

    void addToChunk(float val1, float val2, float val3, float val4) {
        addToChunk(val1);
        addToChunk(val2);
        addToChunk(val3);
        addToChunk(val4);
    }

    final public void add(float val1, float val2, float val3, float val4) {
        reserve(4);
        if (cached != null) {
            addToBuffer(val1, val2, val3, val4);
        } else {
            addToChunk(val1, val2, val3, val4);
        }
    }

    void addToBuffer(FloatBuffer buf, final int count) {
        for (int i = 0; i < count; i++) {
            cached.put(curIndex+i, buf.get(i));
        }
        curIndex += count;
    }

    void addToChunk(FloatBuffer buf, final int count) {
        for (int i = 0; i < count; i++) {
            addToChunk(buf.get(i));
        }
    }

    final public void add(FloatBuffer buf) {
        int count = buf.limit();
        reserve(count);
        if (cached != null) {
            addToBuffer(buf, count);
        }  else {
            addToChunk(buf, count);
        }
    }

    public FloatBuffer getBuffer() {
        return getBuffer(null);
    }

    public FloatBuffer getBuffer(FloatBuffer store) {
        return getBuffer(store, false);
    }

    public FloatBuffer getBuffer(FloatBuffer store, Boolean cache) {
        if (cached != null) {
            if (cache) {
                cached.limit(curIndex);
                cached.rewind();
                return cached;
            } 
        }
        if (curIndex == 0) {
            return null;
        }
        if (store == null || store.capacity() < curIndex) {
            int chunkCapacity = curIndex+curChunk.size -chunkIndex;
            store = BufferUtils.createFloatBuffer(chunkCapacity);
        } else {
            store.clear();
            store.limit(curIndex);
        }
        Chunk chunk = head;
        while (chunk != curChunk) {
            final float[] storage = chunk.storage;
            final int size = chunk.size;
            for (int i = 0; i < size; i++) {
                store.put(storage[i]);
            }
            chunk = chunk.next;
        }
        final int len = chunkIndex;
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                store.put(curChunk.storage[i]);
            }
        }
        store.rewind();
        store.limit(curIndex);
        if (cache) {
            clear();
            cachedCapacity = store.capacity();
            cached = store;
        }
        return store;
    }

    public float get(int index) {
        if (cached != null) {
            return cached.get(index);
        }
        Chunk c = head;
        while (index > c.size) {
            index -= c.size;
            c = c.next;
        }
        return c.storage[index];
    }


    public float[] getFloatArray() {
        float[] result = new float[curIndex];
        if (cached != null) {
            cached.get(result, 0, result.length);
            return result;
        }
        Chunk chunk = head;
        int i = 0;
        while (chunk != curChunk) {
            System.arraycopy(chunk.storage, 0, result, i, chunk.size);
            i += chunk.size;
            chunk = chunk.next;
        }
        if (chunkIndex > 0) {
            System.arraycopy(curChunk.storage, 0, result, i, chunkIndex);
        }
        return result;
    }


    public int getSize() {
        return curIndex * 4;
    }

    public int getBufferSize() {
        return curIndex;
    }
}
