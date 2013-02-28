package f3.media.scene;
import java.nio.*;
import java.util.*;

public class ImportUtils
{
    //from ardor3d

    public static class Matrix3 {
	Vector3 columns[];
	public Matrix3() {
	    columns = new Vector3[3];
	    columns[0] = new Vector3(1, 0, 0);
	    columns[1] = new Vector3(0, 1, 0);
	    columns[2] = new Vector3(0, 0, 1);
	}
	public Matrix3 clone() {
	    Matrix3 dup = new Matrix3();
	    for (int i = 0; i < 3; i++) {
		dup.columns[i].set(columns[i]);
	    }
	    return dup;
	}
	public void setColumn(int i, Vector3 v) {
	    columns[i].set(v);
	}
	public Vector3 getColumn(int i, Vector3 store) {
	    if (store == null) {
		store = new Vector3(columns[i]);
	    } else {
		store.set(columns[i]);
	    }
	    return store;
	}
	public void addLocal(Matrix3 mat) {
	    for (int i = 0; i < columns.length; i++) {
		columns[i].addLocal(mat.columns[i]);
	    }
	}
	public float getValue(int row, int col) {
	    return columns[col].getValue(row);
	}
	public boolean equals(Object obj) {
	    if (obj instanceof Matrix3) {
		Matrix3 mat = (Matrix3)obj;
		for (int i = 0; i < columns.length; i++) {
		    if (!columns[i].equals(mat.columns[i])) {
			return false;
		    }
		}
		return true;
	    } 
	    return false;
	}
	
	public int hashCode() {
	    int result = 17;
	    for (int i = 0; i < 3; i++) {
		for (int j = 0; j < 3; j++) {
		    float cell = getValue(i, j);
		    final int val = Float.floatToIntBits(cell);
		    result += 37 * result + val; 
		}
	    }
	    return result;
	}
    }
    
    // from ardor3d
    
    public static class Vector3 {
	float data[] = new float[3];
	public Vector3() {}
	public Vector3(float x, float y, float z) {
	    data[0] = x;
	    data[1] = y;
	    data[2] = z;
	}
	public Vector3(Vector3 init) {
	    data[0] = init.data[0];
	    data[1] = init.data[1];
	    data[2] = init.data[2];
	}
	public Vector3 zero() {
	    set(0, 0, 0);
	    return this;
	}
	public Vector3 set(Vector3 v) {
	    set(v.getX(), v.getY(), v.getZ());
	    return this;
	}
	public Vector3 set(float x, float y) { // as if Vector2 class
	    data[0] = x;
	    data[1] = y;
	    data[2] = 0;
	    return this;
	}
	public Vector3 set(float x, float y, float z) {
	    data[0] = x; data[1] = y; data[2] = z;
	    return this;
	}
	public float getValue(int i) {
	    return data[i];
	}
	public void setValue(int i, float value) {
	    data[i] = value;
	}
	public Vector3 multiplyLocal(float v) {
	    for (int i = 0; i < 3; i++) {
		data[i] *= v;
	    }
	    return this;
	}
	public Vector3 multiply(float v, Vector3 store) {
	    if (store == null) {
		store = new Vector3(this);
	    } else {
		store.set(this);
	    }
	    store.multiplyLocal(v);
	    return store;
	}
	public Vector3 addLocal(float x, float y, float z) {
	    data[0] += x;
	    data[1] += y;
	    data[2] += z;
	    return this;
	}
	public Vector3 addLocal(Vector3 v) {
	    for (int i = 0; i < 3; i++) {
		data[i] += v.data[i];
	    }
	    return this;
	}
	public Vector3 add(Vector3 v, Vector3 store) {
	    if (store == null) {
		store = new Vector3(this);
	    } else {
		store.set(this);
	    }
	    store.addLocal(v);
	    return store;
	}
	public Vector3 subtractLocal(Vector3 v) {
	    subtractLocal(v.getX(), v.getY(), v.getZ());
	    return this;
	}
	public Vector3 subtractLocal(float x, float y, float z) {
	    return addLocal(-x, -y, -z);
	}
	public Vector3 subtract(Vector3 v, Vector3 store) {
	    if (store == null) {
		store = new Vector3(this);
	    } else {
		store.set(this);
	    }
	    store.subtractLocal(v);
	    return store;
	}
	public Vector3 cross(Vector3 v, Vector3 store) {
	    if (store == null) {
		store = new Vector3(this);
	    } else {
		store.set(this);
	    }
	    store.crossLocal(v);
	    return store;
	}
	public Vector3 crossLocal(Vector3 v) {
	    float vx = v.data[0], vy = v.data[1], vz = v.data[2];
	    return crossLocal(vx, vy, vz);
	}
	public Vector3 crossLocal(float vx, float vy, float vz) {
	    float x = data[0], y = data[1], z = data[2];
	    data[0] = y*vz - z*vy;
	    data[1] = z*vx - x*vz;
	    data[2] = x*vy - y*vx;
	    return this;
	}
	public float dot(Vector3 v) {
	    float proj = 0;
	    for (int i = 0; i < 3; i++) {
		proj += data[i] * v.data[i];
	    }
	    return proj;
	}
	public float getX() {return data[0];}
	public float getY() {return data[1];}
	public float getZ() {return data[2];}
	public void setX(float x) {data[0] = x;}
	public void setY(float y) {data[1] = y;}
	public void setZ(float z) {data[2] = z;}
	public Vector3 normalizeLocal() {
	    float len = length();
	    if (len != 0) {
		multiplyLocal(1f / len);
	    }
	    return this;
	}
	public Vector3 normalize(Vector3 store) {
	    if (store == null) store = new Vector3(this);
	    store.normalizeLocal();
	    return store;
	}
	public float lengthSquared() {
	    return dot(this);
	}
	public float length() {
	    float lenSq = lengthSquared();
	    return (float)Math.sqrt(lenSq);
	}
	public String toString() {
	    return getX() + ", "+ getY() + ", "+ getZ();
	}
	public boolean equals(Object obj) {
	    if (obj instanceof Vector3) {
		Vector3 vec = (Vector3)obj;
		return vec.getX() == getX() && vec.getY() == getY() && vec.getZ() == getZ();
	    }
	    return false;
	}
	public int hashCode() {
	    int hash = 37;
	    hash += 37 * hash + Float.floatToIntBits(getX());
	    hash += 37 * hash + Float.floatToIntBits(getY());
	    hash += 37 * hash + Float.floatToIntBits(getZ());
	    return hash;
	}
    }
    
    // from o3d

    public static void addTangentBinormalStreams(IntBuffer ib,
						 FloatBuffer vb,
						 FloatBuffer tb,
						 FloatBuffer nb,
						 FloatBuffer tanb,
						 FloatBuffer binb) 
    {
        // Generates a key for the tangentFrames map from a position and normal
        // vector. Rounds position and normal to allow some tolerance.
        Map<String, Matrix3> map = new java.util.HashMap();
        Matrix3 tmpMat = new Matrix3();
	/*
        IntBuffer ib = meshData.getIndexBuffer();
        FloatBuffer vb = meshData.getVertexBuffer();
        FloatBuffer tb = meshData.getTextureBuffer(0);
        FloatBuffer nb = meshData.getNormalBuffer();
        FloatBuffer tanb = BufferUtils.createVector3Buffer(ib.limit());
        FloatBuffer binb = BufferUtils.createVector3Buffer(ib.limit());
	*/
        int  numTriangles = ib.limit() / 3;
        Vector3 tangent = new Vector3();
        Vector3 binormal = new Vector3();
        Vector3[] uvs = new Vector3[3];
        Vector3[] positions = new Vector3[3];
        Vector3[] normals = new Vector3[3];
        Vector3 edge1 = new Vector3();
        Vector3 edge2 = new Vector3();
        for (int i = 0; i < 3; i++) {
            uvs[i] = new Vector3();
            positions[i] = new Vector3();
            normals[i] = new Vector3();
        }
        for (int triangleIndex = 0; triangleIndex < numTriangles; ++triangleIndex) {
            // Get the vertex indices, uvs and positions for the triangle.
            for (int i = 0; i < 3; ++i) {
                int index = ib.get(triangleIndex*3+i);
                float u = tb.get(index*2);
                float v = tb.get(index*2+1);
                uvs[i].set(u, v);
                float x = vb.get(index * 3);
                float y = vb.get(index * 3+1);
                float z = vb.get(index * 3+2);
                positions[i].set(x, y, z); 
                x = nb.get(index *3);
                y = nb.get(index *3+1);
                z = nb.get(index *3+2);
                normals[i].set(x, y, z);
            }

            // Calculate the tangent and binormal for the triangle using method
            // described in Maya documentation appendix A: tangent and binormal
            // vectors.
            tangent.set(0, 0, 0);
            binormal.set(0, 0, 0);
            for (int axis = 0; axis < 3; ++axis) {
                edge1.set(positions[1].getValue(axis) - positions[0].getValue(axis),
                          uvs[1].getX() - uvs[0].getX(), uvs[1].getY() - uvs[0].getY());
                edge2.set(positions[2].getValue(axis) - positions[0].getValue(axis),
                          uvs[2].getX() - uvs[0].getX(), uvs[2].getY() - uvs[0].getY());
                Vector3 edgeCross = edge1.crossLocal(edge2);
                if (edgeCross.getX() == 0) {
                    edgeCross.setX(1);
                }
                tangent.setValue(axis, -edgeCross.getY() / edgeCross.getX());
                binormal.setValue(axis, -edgeCross.getZ() / edgeCross.getX());
            }
            // Normalize the tangent and binornmal.
            tangent.normalizeLocal();
            binormal.normalizeLocal();
            for (int i = 0; i < 3; i++) {
                addTangentFrame(map, tmpMat, positions[i], 
                                normals[i], tangent, binormal);
            }
        }
        for (int i = 0; i < ib.limit(); i++) {
            int index = ib.get(i);
            float x = vb.get(index * 3);
            float y = vb.get(index * 3+1);
            float z = vb.get(index * 3+2);
            positions[0].set(x, y, z); 
            x = nb.get(index *3);
            y = nb.get(index *3+1);
            z = nb.get(index *3+2);
            normals[0].set(x, y, z);
            Vector3 pos = positions[0];
            Vector3 norm = normals[0];
            Matrix3 frame = getTangentFrame(map, pos, norm);
            // Orthonormalize the tangent with respect to the normal.
            Vector3 tan = frame.getColumn(0, null);
            tan.subtractLocal(norm.multiply(norm.dot(tan), null));
            float tanLength = tan.length();
            if (tanLength > 0.001f) {
                tan.multiplyLocal(1f / tanLength);
            }
            // Orthonormalize the binormal with respect to the normal and the tangent.
            Vector3 binorm = frame.getColumn(1, null);
            binorm.subtractLocal(tan.multiply(tan.dot(binorm), null));
            binorm.subtractLocal(norm.multiply(norm.dot(binorm), null));
            float binormalLength = binorm.length();
            if (binormalLength > 0.001f) {
                binorm.multiplyLocal(1f / binormalLength);
            }
            //System.out.println("norm: "+ norm);
            //System.out.println("tangent: "+ tan);
            //System.out.println("binorm: "+ binorm);
            for (int j = 0; j < 3; j++) {
                tanb.put(index*3 + j, tan.getValue(j));
                binb.put(index*3 + j, binorm.getValue(j));
            }
        }
    }


    static void addTangentFrame(Map<String, Matrix3> map,
				Matrix3 tmp,
				Vector3 position,
				Vector3 normal,
				Vector3 tangent,
				Vector3 binormal) 
    {
        String key = tangentFrameKey(position, normal);
        Matrix3 mat = map.get(key);
        if (mat == null) {
            mat = new Matrix3();
            map.put(key, mat);
        }
        tmp.setColumn(0, tangent);
        tmp.setColumn(1, binormal);
        mat.addLocal(tmp);
    }

    static Matrix3 getTangentFrame(Map<String, Matrix3> map,
				   Vector3 position, Vector3 normal) {
        String key = tangentFrameKey(position, normal);
        return map.get(key);
    }
    
    static String tangentFrameKey(Vector3 position,
                                  Vector3 normal) {
        String result = "[[";
        String sep = "";
        for (int i = 0; i < 3; i++) {
            result += sep;
            result += Math.round(position.getValue(i) * 100);
            sep = ",";
        }
        result += "],[";
        sep = "";
        for (int i = 0; i < 3; i++) {
            result += sep;
            result += Math.round(normal.getValue(i) * 100);
            sep = ",";
        }
        result += "]]";
        return result;
    }             

    // from ardor3d

    public static class MeshData {
	IntBuffer indexBuffer;
	FloatBufferData vertexCoords = new FloatBufferData(null, 3);
	List<FloatBufferData> textureCoords = new ArrayList();
	FloatBufferData normalCoords;
	FloatBufferData tangentCoords;
	FloatBufferData binormalCoords;
	public void setIndexBuffer(IntBuffer buf) {
	    indexBuffer = buf;
	}
	public IntBuffer getIndexBuffer() {
	    return indexBuffer;
	}
	public void setVertexCoords(FloatBufferData buf) {
	    vertexCoords = buf;
	}
	public FloatBufferData getVertexCoords() {
	    return vertexCoords;
	}
	public FloatBuffer getVertexBuffer() {
	    return vertexCoords.getBuffer();
	}
	public void setVertexBuffer(FloatBuffer buffer) {
	    setVertexCoords(new FloatBufferData(buffer, 3));
	}
	public void setTextureCoords(FloatBufferData coords, int i) {
	    while (textureCoords.size() <= i) {
		textureCoords.add(null);
	    }
	    textureCoords.set(i, coords);
	}
	public FloatBufferData getTextureCoords(int i) {
	    if (i >= textureCoords.size()) {
		return null;
	    }
	    return textureCoords.get(i);
	}
	public List<FloatBufferData> getTextureCoords() {
	    return textureCoords;
	}
	public FloatBuffer getTextureBuffer(int i) {
	    FloatBufferData coords = getTextureCoords(i);
	    if (coords == null) {
		return null;
	    }
	    return coords.getBuffer();
	}
	public void setTextureBuffer(FloatBuffer buffer, int i) {
	    setTextureCoords(new FloatBufferData(buffer, 2), i);
	}
	public void setNormalCoords(FloatBufferData coords) {
	    normalCoords = coords;
	}
	public FloatBuffer getNormalBuffer() {
	    return normalCoords.getBuffer();
	}
	public void setNormalBuffer(FloatBuffer buffer) {
	    setNormalCoords(new FloatBufferData(buffer, 3));
	}
	public FloatBufferData getNormalCoords() {
	    return normalCoords;
	}
	public void setTangentCoords(FloatBufferData coords) {
	    tangentCoords = coords;
	}
	public FloatBuffer getTangentBuffer() {
	    if (tangentCoords == null) return null;
	    return tangentCoords.getBuffer();
	}
	public void setTangentBuffer(FloatBuffer buffer) {
	    setTangentCoords(new FloatBufferData(buffer, 3));
	}
	public FloatBufferData getTangentCoords() {
	    return tangentCoords;
	}
	public void setBinormalCoords(FloatBufferData coords) {
	    binormalCoords = coords;
	}
	public FloatBuffer getBinormalBuffer() {
	    if (binormalCoords == null) return null;
	    return binormalCoords.getBuffer();
	}
	public void setBinormalBuffer(FloatBuffer buffer) {
	    setBinormalCoords(new FloatBufferData(buffer, 3));
	}
	public FloatBufferData getBinormalCoords() {
	    return binormalCoords;
	}
    }

    public static class FloatBufferData {
	FloatBuffer buffer;
	int valuesPerTuple;
	public FloatBufferData(FloatBuffer buf, int valuesPerTuple) {
	    this.buffer = buf;
	    this.valuesPerTuple = valuesPerTuple;
	}
	public FloatBuffer getBuffer() {return buffer;}
	public int getCoordsPerVertex() {return valuesPerTuple;}
	public void setBuffer(FloatBuffer buf) {
	    buffer = buf;
	}
	public int getCount() {
	    return buffer.limit() / valuesPerTuple;
	}
    }
}