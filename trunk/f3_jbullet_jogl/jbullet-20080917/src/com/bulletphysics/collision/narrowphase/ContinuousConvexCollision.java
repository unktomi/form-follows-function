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

package com.bulletphysics.collision.narrowphase;

import com.bulletphysics.linearmath.*;
import com.bulletphysics.collision.shapes.*;
import javax.vecmath.Vector3f;

/**
 * btContinuousConvexCollision implements angular and linear time of impact for convex objects. <br>
 * Based on Brian Mirtich's Conservative Advancement idea (PhD thesis). <br>
 * Algorithm operates in worldspace, in order to keep inbetween motion globally consistent. <br>
 * It uses GJK at the moment. Future improvement would use minkowski sum / supporting vertex, merging innerloops
 */

public class ContinuousConvexCollision implements ConvexCast {
    private SimplexSolverInterface simplexSolver;
    private ConvexPenetrationDepthSolver penetrationDepthSolver;
    private ConvexShape convexA;
    private ConvexShape convexB;

    public ContinuousConvexCollision(ConvexShape convexA,
                                     ConvexShape convexB,
                                     SimplexSolverInterface simplexSolver,
                                     ConvexPenetrationDepthSolver penetrationDepthSolver) {
        this.simplexSolver = simplexSolver;
        this.penetrationDepthSolver = penetrationDepthSolver;
        this.convexA = convexA;
        this.convexB = convexB;
    }

    /// This maximum should not be necessary. It allows for untested/degenerate cases in production code.
    /// You don't want your game ever to lock-up.
    private static final int MAX_ITERATIONS = 64;

    public boolean calcTimeOfImpact(Transform fromA,
                                    Transform toA,
                                    Transform fromB,
                                    Transform toB,
                                    CastResult result) {
	simplexSolver.reset();

	/// compute linear and angular velocity for this interval, to interpolate
	Vector3f linVelA = new Vector3f(), angVelA = new Vector3f(), linVelB = new Vector3f(), angVelB = new Vector3f();
	TransformUtil.calculateVelocity(fromA,toA,1.0f,linVelA,angVelA);
	TransformUtil.calculateVelocity(fromB,toB,1.0f,linVelB,angVelB);

        float boundingRadiusA = convexA.getAngularMotionDisc();
	float boundingRadiusB = convexB.getAngularMotionDisc();

	float maxAngularProjectedVelocity = angVelA.length() * boundingRadiusA + angVelB.length() * boundingRadiusB;
	Vector3f relLinVel = new Vector3f();
        relLinVel.sub(linVelB, linVelA);

	float relLinVelocLength = relLinVel.length();
	
	if ((relLinVelocLength+maxAngularProjectedVelocity) == 0.0f)
		return false;

	float radius = 0.001f;

	float lambda = 0.0f;
	Vector3f v = new Vector3f(1,0,0);

	int maxIter = MAX_ITERATIONS;

	Vector3f n = new Vector3f();
	boolean hasResult = false;
	Vector3f c = new Vector3f();

        float lastLambda = lambda;
	//btScalar epsilon = btScalar(0.001);

	int numIter = 0;
	//first solution, using GJK

	Transform identityTrans = new Transform();
	identityTrans.setIdentity();

	SphereShape raySphere = new SphereShape(0.0f);
	raySphere.setMargin(0.0f);

//	result.drawCoordSystem(sphereTr);

	PointCollector pointCollector1 = new PointCollector();

	{
		
                GjkPairDetector gjk = new GjkPairDetector();
                gjk.init(convexA,convexB,simplexSolver,penetrationDepthSolver);
		GjkPairDetector.ClosestPointInput input = new GjkPairDetector.ClosestPointInput();
	
		//we don't use margins during CCD
	//	gjk.setIgnoreMargin(true);

		input.transformA.set(fromA);
		input.transformB.set(fromB);
		gjk.getClosestPoints(input, pointCollector1, null);

		hasResult = pointCollector1.hasResult;
		c.set(pointCollector1.pointInWorld);
	}

	if (hasResult)
	{
		float dist = pointCollector1.distance;
		n.set(pointCollector1.normalOnBInWorld);

		float projectedLinearVelocity = relLinVel.dot(n);
		
		//not close enough
		while (dist > radius)
		{
			numIter++;
			if (numIter > maxIter)
			{
				return false; //todo: report a failure
			}
			float dLambda = 0.0f;

			projectedLinearVelocity = relLinVel.dot(n);

			//calculate safe moving fraction from distance / (linear+rotational velocity)
			
			//btScalar clippedDist  = GEN_min(angularConservativeRadius,dist);
			//btScalar clippedDist  = dist;
			dLambda = dist / (projectedLinearVelocity+ maxAngularProjectedVelocity);
			
			lambda = lambda + dLambda;

			if (lambda > 1.0f)
				return false;

			if (lambda < 0.0f)
				return false;

			//todo: next check with relative epsilon
			if (lambda <= lastLambda)
			{
				return false;
				//n.setValue(0,0,0);
			}
			lastLambda = lambda;

			//interpolate to next lambda
			Transform interpolatedTransA = new Transform(),interpolatedTransB = new Transform(),relativeTrans = new Transform();

			TransformUtil.integrateTransform(fromA,linVelA,angVelA,lambda,interpolatedTransA);
			TransformUtil.integrateTransform(fromB,linVelB,angVelB,lambda,interpolatedTransB);
			interpolatedTransB.inverseTimes(interpolatedTransA, relativeTrans);

			result.debugDraw( lambda );

			PointCollector pointCollector = new PointCollector();
			GjkPairDetector gjk = new GjkPairDetector();
                        gjk.init(convexA,convexB,simplexSolver,penetrationDepthSolver);
			GjkPairDetector.ClosestPointInput input = new GjkPairDetector.ClosestPointInput();
			input.transformA.set(interpolatedTransA);
			input.transformB.set(interpolatedTransB);
			gjk.getClosestPoints(input,pointCollector,null);
			if (pointCollector.hasResult)
			{
				if (pointCollector.distance < 0.0f)
				{
					//degenerate ?!
					result.fraction = lastLambda;
					n.set(pointCollector.normalOnBInWorld);
					result.normal.set(n);//.setValue(1,1,1);// = n;
					result.hitPoint.set(pointCollector.pointInWorld);
					return true;
				}
				c.set(pointCollector.pointInWorld);		
				n.set(pointCollector.normalOnBInWorld);
				dist = pointCollector.distance;
			} else
			{
				//??
				return false;
			}

		}

		//don't report time of impact for motion away from the contact normal (or causes minor penetration)
		if ((projectedLinearVelocity+ maxAngularProjectedVelocity)<=result.allowedPenetration)//SIMD_EPSILON)
			return false;

		result.fraction = lambda;
		result.normal.set(n);
		result.hitPoint.set(c);
		return true;
	}

	return false;

/*
//todo:
	//if movement away from normal, discard result
	btVector3 move = transBLocalTo.getOrigin() - transBLocalFrom.getOrigin();
	if (result.m_fraction < btScalar(1.))
	{
		if (move.dot(result.m_normal) <= btScalar(0.))
		{
		}
	}
*/
        
    }
}
