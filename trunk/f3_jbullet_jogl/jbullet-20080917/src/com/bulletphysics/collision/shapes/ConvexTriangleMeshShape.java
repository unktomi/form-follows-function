/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.collision.shapes;

import java.nio.*;
import com.bulletphysics.BulletGlobals;
import com.bulletphysics.util.ObjectPool;
import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.linearmath.*;
import cz.advel.stack.Stack;
import javax.vecmath.Vector3f;
import javax.vecmath.Matrix3f;


public class ConvexTriangleMeshShape extends PolyhedralConvexShape {

    StridingMeshInterface m_stridingMesh;

    public BroadphaseNativeType getShapeType() {
        return BroadphaseNativeType.CONVEX_TRIANGLEMESH_SHAPE_PROXYTYPE;
    }


    public ConvexTriangleMeshShape(StridingMeshInterface meshInterface, boolean calcAabb) {
        m_stridingMesh = meshInterface;
        if (calcAabb) {
            recalcLocalAabb();
        }
    }

    public StridingMeshInterface getMeshInterface() {
        return m_stridingMesh;
    }

    class LocalSupportVertexCallback implements InternalTriangleIndexCallback {
        Vector3f m_supportVertexLocal;
        float m_maxDot;
        Vector3f m_supportVecLocal;
        public LocalSupportVertexCallback(Vector3f supportVecLocal) {
            m_supportVertexLocal = new Vector3f(0, 0, 0);
            m_maxDot = -1e30f;
            m_supportVecLocal = supportVecLocal;
        }

        public void internalProcessTriangleIndex(Vector3f[] triangle, int partId, int triangleIndex) {
            for (int i = 0; i < 3; i++) {
                float dot = m_supportVecLocal.dot(triangle[i]);
                if (dot > m_maxDot) {
                    m_maxDot = dot;
                    m_supportVertexLocal = triangle[i];
                }
            }
        }

        public Vector3f GetSupportVertexLocal() {
            return m_supportVertexLocal;
        }

    }

    Vector3f localGetSupportingVertexWithoutMargin(Vector3f vec0) {
        Vector3f supVec = new Vector3f(0, 0, 0);
        Vector3f vec = vec0;
        float lenSqr = vec.lengthSquared();
        if (lenSqr < .0001) {
            vec.set(1, 0, 0);
        } else {
            float rlen = (float)(1 / Math.sqrt(lenSqr));
            vec.scale(rlen);
        }
        LocalSupportVertexCallback supportCallback = new LocalSupportVertexCallback(vec);
        Vector3f aabbMax = new Vector3f(1e30f, 1e30f, 1e30f);
        Vector3f aabbMin = new Vector3f(-1e30f, -1e30f, -1e30f);
        m_stridingMesh.internalProcessAllTriangles(supportCallback, aabbMin, aabbMax);
        supVec = supportCallback.GetSupportVertexLocal();
        return supVec;
    }

    public void batchedUnitVectorGetSupportingVertexWithoutMargin(Vector3f[] vectors, Vector3f[] supportVerticesOut,
                                                           int numVectors) {
        //        for (int i = 0; i < numVectors; i++) {
        //            supportVertices[i].w = 1e30f;
        //        }
		// JAVA NOTE: rewritten as code used W coord for temporary usage in Vector3
		// TODO: optimize it
        //        float[] wcoords = new float[numVectors];
        
        //        for (int i = 0; i < numVectors; i++) {
        //            // TODO: used w in vector3:
        //            //supportVerticesOut[i].w = -1e30f;
        //            wcoords[i] = -1e30f;
        //        }

        for (int j = 0; j < numVectors; j++) {
            Vector3f vec = vectors[j];
            LocalSupportVertexCallback supportCallback = new LocalSupportVertexCallback(vec);
            Vector3f aabbMax = new Vector3f(1e30f,1e30f,1e30f);
            Vector3f aabbMin = new Vector3f(-1e30f,-1e30f,-1e30f);
            m_stridingMesh.internalProcessAllTriangles(supportCallback, aabbMin, aabbMax);
            supportVerticesOut[j] = supportCallback.GetSupportVertexLocal();
        }
    }

    Vector3f localGetSupportingVertex(Vector3f vec) {
        Vector3f supVertex = new Vector3f(localGetSupportingVertexWithoutMargin(vec));
        if (getMargin() != 0) {
            Vector3f vecnorm = new Vector3f(vec);
            if (vecnorm.lengthSquared() < (BulletGlobals.SIMD_EPSILON*BulletGlobals.SIMD_EPSILON)) {
                vecnorm.set(-1, -1, -1);
            }
            vecnorm.normalize();
            vecnorm.scale(getMargin());
            supVertex.add(vecnorm);
        }
        return supVertex;
    }

    public String getName() {
        return "ConvexTriangleMesh";
    }

    public int getNumVertices() {
        return 0;
    }

    public int getNumEdges() {
        return 0;
    }

    public void getEdge(int i, Vector3f v1, Vector3f v2) {

    }

    public void getVertex(int i, Vector3f v1) {

    }

    public int getNumPlanes() {
        return 0;
    }

    public void getPlane(Vector3f v1, Vector3f v2, int i) {
    }

    public boolean isInside(Vector3f v, float f) {
        return false;
    }


    public void setLocalScaling(Vector3f scaling) {
        m_stridingMesh.setScaling(scaling);
        recalcLocalAabb();
    }

    public Vector3f getLocalScaling() {
        return m_stridingMesh.getScaling(null);
    }


    public void calculatePrincipalAxisTransform(Transform principal, Vector3f inertia, float volume) {

        System.out.println("CALC PRINCIPAL AXIS XFORM");
        class CenterCallback implements InternalTriangleIndexCallback {

            boolean first;
            Vector3f ref;
            Vector3f sum;
            float volume;

            public CenterCallback()  {
                ref = new Vector3f(0 ,0, 0);
                sum = new Vector3f(0 ,0, 0);
                volume = 0;
            }
            
            public void internalProcessTriangleIndex(Vector3f[] triangle, int partId, int triangleIndex) {
                if (first) {
                    ref = new Vector3f(triangle[0]);
                    first = false;
                } else {
                    //btScalar vol = btFabs((triangle[0] - ref).triple(triangle[1] - ref, triangle[2] - ref));
                    //sum += (btScalar(0.25) * vol) * ((triangle[0] + triangle[1] + triangle[2] + ref));
                    //volume += vol;
                    Vector3f tmp1 = new Vector3f();
                    tmp1.sub(triangle[0], ref);
                    Vector3f tmp2 = new Vector3f();
                    tmp2.sub(triangle[1], ref);
                    Vector3f tmp3 = new Vector3f();
                    tmp3.sub(triangle[2], ref);
                    float vol = Math.abs(VectorUtil.triple(tmp1, tmp2, tmp3));
                    tmp1.add(triangle[0], triangle[1]);
                    tmp2.add(tmp1, triangle[2]);
                    tmp3.add(tmp2, ref);
                    tmp3.scale(.25f * vol);
                    sum.add(tmp3);
                    volume += vol;
                }
            }

            public Vector3f getCenter() {
                if (volume > 0) {
                    Vector3f tmp = new Vector3f(sum);
                    tmp.scale(1f/volume);
                    System.out.println("CENTER = " + tmp.x + " " + tmp.y + " " + tmp.z);
                    return tmp;
                }
                System.out.println("CENTER = " + ref.x + " " + ref.y + " " + ref.z);
                return ref;
            }
            
            public float getVolume() {
                return volume/6;
            }
        }

        class InertiaCallback implements InternalTriangleIndexCallback {
            Matrix3f sum;
            Vector3f center;

            public InertiaCallback(Vector3f center) {
                this.sum = new Matrix3f();
                this.center = new Vector3f(center);
            }

            public void internalProcessTriangleIndex(Vector3f triangle[], int partId, int triangleIndex) {
                Vector3f a = new Vector3f();
                Vector3f b = new Vector3f();
                Vector3f c = new Vector3f();
                a.sub(triangle[0], center);
                b.sub(triangle[1], center);
                c.sub(triangle[2], center);
                float valNeg = -Math.abs(VectorUtil.triple(a, b, c)) / 6;
                Matrix3f i = new Matrix3f();
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k <= j; k++) {
                        float val = valNeg * (.1f * (VectorUtil.getCoord(a, j) * VectorUtil.getCoord(a, k) + VectorUtil.getCoord(b, j) * VectorUtil.getCoord(b, k) + VectorUtil.getCoord(c, j) * VectorUtil.getCoord(c, k)) +
                                              .05f * (VectorUtil.getCoord(a, j) * VectorUtil.getCoord(b, k) + VectorUtil.getCoord(a, k) *
                                                      VectorUtil.getCoord(b, j) + VectorUtil.getCoord(a, j) * VectorUtil.getCoord(c, k) +
                                                      VectorUtil.getCoord(a, k) * VectorUtil.getCoord(c, j) + VectorUtil.getCoord(b, j) *
                                                      VectorUtil.getCoord(c, k) + VectorUtil.getCoord(b, k) * VectorUtil.getCoord(c, j)));
                        i.setElement(j, k, val);
                        i.setElement(k, j, val);
                    }
                }
                float i00 = -i.getElement(0, 0);
                float i11 = -i.getElement(1, 1);
                float i22 = -i.getElement(2, 2);
                i.setElement(0, 0, i11 + i22);
                i.setElement(1, 1, i22 + i00);
                i.setElement(2, 2, i00 + i11);
                Vector3f row1 = new Vector3f();
                Vector3f row2 = new Vector3f();
                Vector3f tmp = new Vector3f();

                for (int q = 0; q < 3; q++) {
                    sum.getRow(q, row1);
                    i.getRow(q, row2);
                    tmp.add(row1, row2);
                    sum.setRow(q, tmp);
                }

            }

            public Matrix3f getInertia() {
                return sum;
            }
        }
        CenterCallback centerCallback = new CenterCallback();
        Vector3f aabbMax = new Vector3f(1e30f, 1e30f, 1e30f);
        Vector3f aabbMin = new Vector3f(-1e30f, -1e30f, -1e30f);
        m_stridingMesh.internalProcessAllTriangles(centerCallback, aabbMin, aabbMax);
        Vector3f center = centerCallback.getCenter();
        principal.origin.set(center);
        volume = centerCallback.getVolume();
        
        InertiaCallback inertiaCallback = new InertiaCallback(center);
        m_stridingMesh.internalProcessAllTriangles(inertiaCallback, aabbMin, aabbMax);
        
        Matrix3f i = inertiaCallback.getInertia();
        MatrixUtil.diagonalize(i, principal.basis, .00001f, 20);
        inertia.set(i.getElement(0, 0), i.getElement(1, 1), i.getElement(2, 2));
        inertia.scale(1f/volume);
    }
}