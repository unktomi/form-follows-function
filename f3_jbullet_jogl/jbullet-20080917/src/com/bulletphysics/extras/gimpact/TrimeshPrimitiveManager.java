/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * This source file is part of GIMPACT Library.
 *
 * For the latest info, see http://gimpact.sourceforge.net/
 *
 * Copyright (c) 2007 Francisco Leon Najera. C.C. 80087371.
 * email: projectileman@yahoo.com
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

package com.bulletphysics.extras.gimpact;

import com.bulletphysics.collision.shapes.ScalarType;
import com.bulletphysics.collision.shapes.StridingMeshInterface;
import com.bulletphysics.collision.shapes.VertexData;
import com.bulletphysics.extras.gimpact.BoxCollision.AABB;
import cz.advel.stack.Stack;
import java.nio.*;
import javax.vecmath.Vector3f;

/**
 *
 * @author jezek2
 */
class TrimeshPrimitiveManager extends PrimitiveManagerBase {

	public float margin;
	public StridingMeshInterface meshInterface;
	public final Vector3f scale = new Vector3f();
	public int part;
	public int lock_count;
	public VertexData vertexData = new VertexData();

	private final int[] tmpIndices = new int[3];
	
	public TrimeshPrimitiveManager() {
		meshInterface = null;
		part = 0;
		margin = 0.01f;
		scale.set(1f, 1f, 1f);
		lock_count = 0;
		vertexData.vertexbase = null;
		vertexData.numverts = 0;
		vertexData.stride = 0;
		vertexData.indexbase = null;
		vertexData.indexstride = 0;
		vertexData.numfaces = 0;
	}

	public TrimeshPrimitiveManager(TrimeshPrimitiveManager manager) {
		meshInterface = manager.meshInterface;
		part = manager.part;
		margin = manager.margin;
		scale.set(manager.scale);
		lock_count = 0;
		vertexData.vertexbase = null;
		vertexData.numverts = 0;
		vertexData.stride = 0;
		vertexData.indexbase = null;
		vertexData.indexstride = 0;
		vertexData.numfaces = 0;
	}

	public TrimeshPrimitiveManager(StridingMeshInterface meshInterface, int part) {
		this.meshInterface = meshInterface;
		this.part = part;
		this.meshInterface.getScaling(scale);
		margin = 0.1f;
		lock_count = 0;
		vertexData.vertexbase = null;
		vertexData.numverts = 0;
		vertexData.stride = 0;
		vertexData.indexbase = null;
		vertexData.indexstride = 0;
		vertexData.numfaces = 0;
	}

	public void lock() {
		if (lock_count > 0) {
			lock_count++;
			return;
		}
		meshInterface.getLockedReadOnlyVertexIndexBase(vertexData, part);

		lock_count = 1;
	}

	public void unlock() {
		if (lock_count == 0) {
			return;
		}
		if (lock_count > 1) {
			--lock_count;
			return;
		}
		meshInterface.unLockReadOnlyVertexBase(part);
		vertexData.vertexbase = null;
		lock_count = 0;
	}
	
	@Override
	public boolean is_trimesh() {
		return true;
	}

	@Override
	public int get_primitive_count() {
		return vertexData.numfaces;
	}

	public int get_vertex_count() {
		return vertexData.numverts;
	}

	public void get_indices(int face_index, int[] out) {
		if (vertexData.indicestype == ScalarType.SHORT) {
                    /*
			ByteBuffer s_indices_ptr = vertexData.indexbase;
			int s_indices_idx = face_index * vertexData.indexstride;
			out[0] = s_indices_ptr.getShort(s_indices_idx + 2 * 0) & 0xFFFF;
			out[1] = s_indices_ptr.getShort(s_indices_idx + 2 * 1) & 0xFFFF;
			out[2] = s_indices_ptr.getShort(s_indices_idx + 2 * 2) & 0xFFFF;
                    */
		}
		else {
			IntBuffer i_indices_ptr = vertexData.indexbase;
			int i_indices_idx = face_index * vertexData.indexstride;
			out[0] = i_indices_ptr.get(i_indices_idx +  0);
			out[1] = i_indices_ptr.get(i_indices_idx +  1);
			out[2] = i_indices_ptr.get(i_indices_idx + 2);
		}
	}

	public void get_vertex(int vertex_index, Vector3f vertex) {
		if (vertexData.type == ScalarType.DOUBLE) {
                    /*
			ByteBuffer dvertices_ptr = vertexData.vertexbase;
			int dvertices_idx = vertex_index * vertexData.stride;
			vertex.x = (float) (dvertices_ptr.getDouble(dvertices_idx + 8 * 0) * scale.x);
			vertex.y = (float) (dvertices_ptr.getDouble(dvertices_idx + 8 * 1) * scale.y);
			vertex.z = (float) (dvertices_ptr.getDouble(dvertices_idx + 8 * 2) * scale.z);
                    */
		}
		else {
			FloatBuffer svertices_ptr = vertexData.vertexbase;
			int svertices_idx = vertex_index * vertexData.stride;
			vertex.x = svertices_ptr.get(svertices_idx + 0) * scale.x;
			vertex.y = svertices_ptr.get(svertices_idx +  1) * scale.y;
			vertex.z = svertices_ptr.get(svertices_idx +  2) * scale.z;
		}
	}
	
	@Override
	public void get_primitive_box(int prim_index, AABB primbox) {
		PrimitiveTriangle triangle = Stack.alloc(PrimitiveTriangle.class);
		get_primitive_triangle(prim_index, triangle);
		primbox.calc_from_triangle_margin(
				triangle.vertices[0],
				triangle.vertices[1], triangle.vertices[2], triangle.margin);
	}

	@Override
	public void get_primitive_triangle(int prim_index, PrimitiveTriangle triangle) {
		get_indices(prim_index, tmpIndices);
		get_vertex(tmpIndices[0], triangle.vertices[0]);
		get_vertex(tmpIndices[1], triangle.vertices[1]);
		get_vertex(tmpIndices[2], triangle.vertices[2]);
		triangle.margin = margin;
	}

	public void get_bullet_triangle(int prim_index, TriangleShapeEx triangle) {
		get_indices(prim_index, tmpIndices);
		get_vertex(tmpIndices[0], triangle.vertices1[0]);
		get_vertex(tmpIndices[1], triangle.vertices1[1]);
		get_vertex(tmpIndices[2], triangle.vertices1[2]);
		triangle.setMargin(margin);
	}
	
}
