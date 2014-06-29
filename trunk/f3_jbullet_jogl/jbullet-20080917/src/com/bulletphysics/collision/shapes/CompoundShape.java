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

import java.util.ArrayList;
import java.util.List;
import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.linearmath.VectorUtil;
import cz.advel.stack.Stack;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * CompoundShape allows to store multiple other {@link CollisionShape}s. This allows
 * for concave collision objects. This is more general than the {@link BvhTriangleMeshShape}.
 * 
 * @author jezek2
 */
public class CompoundShape extends CollisionShape {

	private final List<CompoundShapeChild> children = new ArrayList<CompoundShapeChild>();
	private final Vector3f localAabbMin = new Vector3f(1e30f, 1e30f, 1e30f);
	private final Vector3f localAabbMax = new Vector3f(-1e30f, -1e30f, -1e30f);

	private OptimizedBvh aabbTree = null;

	private float collisionMargin = 0f;
	protected final Vector3f localScaling = new Vector3f(1f, 1f, 1f);

	public void addChildShape(Transform localTransform, CollisionShape shape) {
		//m_childTransforms.push_back(localTransform);
		//m_childShapes.push_back(shape);
		CompoundShapeChild child = new CompoundShapeChild();
		child.transform.set(localTransform);
		child.childShape = shape;
		child.childShapeType = shape.getShapeType();
		child.childMargin = shape.getMargin();

		children.add(child);

		// extend the local aabbMin/aabbMax
		Vector3f _localAabbMin = Stack.alloc(Vector3f.class), _localAabbMax = Stack.alloc(Vector3f.class);
		shape.getAabb(localTransform, _localAabbMin, _localAabbMax);

		// JAVA NOTE: rewritten
//		for (int i=0;i<3;i++)
//		{
//			if (this.localAabbMin[i] > _localAabbMin[i])
//			{
//				this.localAabbMin[i] = _localAabbMin[i];
//			}
//			if (this.localAabbMax[i] < _localAabbMax[i])
//			{
//				this.localAabbMax[i] = _localAabbMax[i];
//			}
//		}
		VectorUtil.setMin(this.localAabbMin, _localAabbMin);
		VectorUtil.setMax(this.localAabbMax, _localAabbMax);
	}

	/**
	 * Remove all children shapes that contain the specified shape.
	 */
	public void removeChildShape(CollisionShape shape) {
		boolean done_removing;

		// Find the children containing the shape specified, and remove those children.
		do {
			done_removing = true;

			for (int i = 0; i < children.size(); i++) {
				if (children.get(i).childShape == shape) {
					children.remove(i);
					done_removing = false;  // Do another iteration pass after removing from the vector
					break;
				}
			}
		}
		while (!done_removing);

		recalculateLocalAabb();
	}
	
	public int getNumChildShapes() {
		return children.size();
	}

	public CollisionShape getChildShape(int index) {
		return children.get(index).childShape;
	}

	public Transform getChildTransform(int index, Transform out) {
		out.set(children.get(index).transform);
		return out;
	}

	public List<CompoundShapeChild> getChildList() {
		return children;
	}

	/**
	 * getAabb's default implementation is brute force, expected derived classes to implement a fast dedicated version.
	 */
	@Override
	public void getAabb(Transform trans, Vector3f aabbMin, Vector3f aabbMax) {
		Vector3f localHalfExtents = Stack.alloc(Vector3f.class);
		localHalfExtents.sub(localAabbMax, localAabbMin);
		localHalfExtents.scale(0.5f);

		Vector3f localCenter = Stack.alloc(Vector3f.class);
		localCenter.add(localAabbMax, localAabbMin);
		localCenter.scale(0.5f);

		Matrix3f abs_b = Stack.alloc(trans.basis);
		MatrixUtil.absolute(abs_b);

		Vector3f center = Stack.alloc(localCenter);
		trans.transform(center);

		Vector3f tmp = Stack.alloc(Vector3f.class);

		Vector3f extent = Stack.alloc(Vector3f.class);
		abs_b.getRow(0, tmp);
		extent.x = tmp.dot(localHalfExtents);
		abs_b.getRow(1, tmp);
		extent.y = tmp.dot(localHalfExtents);
		abs_b.getRow(2, tmp);
		extent.z = tmp.dot(localHalfExtents);

		extent.x += getMargin();
		extent.y += getMargin();
		extent.z += getMargin();

		aabbMin.sub(center, extent);
		aabbMax.add(center, extent);
	}

	/**
	 * Re-calculate the local Aabb. Is called at the end of removeChildShapes.
	 * Use this yourself if you modify the children or their transforms.
	 */
	public void recalculateLocalAabb() {
		// Recalculate the local aabb
		// Brute force, it iterates over all the shapes left.
		localAabbMin.set(1e30f, 1e30f, 1e30f);
		localAabbMax.set(-1e30f, -1e30f, -1e30f);

		Vector3f tmpLocalAabbMin = Stack.alloc(Vector3f.class);
		Vector3f tmpLocalAabbMax = Stack.alloc(Vector3f.class);

		// extend the local aabbMin/aabbMax
		for (int j = 0; j < children.size(); j++) {
			children.get(j).childShape.getAabb(children.get(j).transform, tmpLocalAabbMin, tmpLocalAabbMax);
			
			for (int i = 0; i < 3; i++) {
				if (VectorUtil.getCoord(localAabbMin, i) > VectorUtil.getCoord(tmpLocalAabbMin, i)) {
					VectorUtil.setCoord(localAabbMin, i, VectorUtil.getCoord(tmpLocalAabbMin, i));
				}
				if (VectorUtil.getCoord(localAabbMax, i) < VectorUtil.getCoord(tmpLocalAabbMax, i)) {
					VectorUtil.setCoord(localAabbMax, i, VectorUtil.getCoord(tmpLocalAabbMax, i));
				}
			}
		}
	}
	
	@Override
	public void setLocalScaling(Vector3f scaling) {
		localScaling.set(scaling);
	}

	@Override
	public Vector3f getLocalScaling(Vector3f out) {
		out.set(localScaling);
		return out;
	}

	@Override
	public void calculateLocalInertia(float mass, Vector3f inertia) {
		// approximation: take the inertia from the aabb for now
		Transform ident = Stack.alloc(Transform.class);
		ident.setIdentity();
		Vector3f aabbMin = Stack.alloc(Vector3f.class), aabbMax = Stack.alloc(Vector3f.class);
		getAabb(ident, aabbMin, aabbMax);

		Vector3f halfExtents = Stack.alloc(Vector3f.class);
		halfExtents.sub(aabbMax, aabbMin);
		halfExtents.scale(0.5f);

		float lx = 2f * halfExtents.x;
		float ly = 2f * halfExtents.y;
		float lz = 2f * halfExtents.z;

		inertia.x = (mass / 12f) * (ly * ly + lz * lz);
		inertia.y = (mass / 12f) * (lx * lx + lz * lz);
		inertia.z = (mass / 12f) * (lx * lx + ly * ly);
                //                System.out.println("INERTIA : " + inertia.x + " " + inertia.y + " "+inertia.z);
	}
	
	@Override
	public BroadphaseNativeType getShapeType() {
		return BroadphaseNativeType.COMPOUND_SHAPE_PROXYTYPE;
	}

	@Override
	public void setMargin(float margin) {
		collisionMargin = margin;
	}

	@Override
	public float getMargin() {
		return collisionMargin;
	}

	@Override
	public String getName() {
		return "Compound";
	}

	// this is optional, but should make collision queries faster, by culling non-overlapping nodes
	// void	createAabbTreeFromChildren();
	
	public OptimizedBvh getAabbTree() {
		return aabbTree;
	}
	
}
