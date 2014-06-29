package com.bulletphysics.collision.dispatch;
import java.util.*;
import com.bulletphysics.collision.broadphase.*;
import com.bulletphysics.collision.shapes.*;
import com.bulletphysics.linearmath.*;
import static com.bulletphysics.linearmath.AabbUtil2.aabbExpand;
import static com.bulletphysics.linearmath.AabbUtil2.rayAabb;
import javax.vecmath.*;

public class GhostObject extends CollisionObject {

    List<CollisionObject> overlappingObjects;

    public GhostObject() {
        internalType = CollisionObjectType.GHOST_OBJECT;
    }

    void addOverlappingObjectInternal(BroadphaseProxy otherProxy,
                                      BroadphaseProxy thisProxy) {
	CollisionObject otherObject = (CollisionObject)otherProxy.clientObject;
	///if this linearSearch becomes too slow (too many overlapping objects) we should add a more appropriate data structure
	int index = overlappingObjects.indexOf(otherObject);
	if (index==overlappingObjects.size())
	{
            //not found
            overlappingObjects.add(otherObject);
	}
    }

    void removeOverlappingObjectInternal(BroadphaseProxy otherProxy,
                                         Dispatcher dispatcher,
                                         BroadphaseProxy thisProxy) {
        CollisionObject otherObject = (CollisionObject)otherProxy.clientObject;
        overlappingObjects.remove(otherObject);
    }
    
    int getNumOverlappingObjects() {
        return overlappingObjects.size();
    }
    
    CollisionObject getOverlappingObject(int index) {
        return overlappingObjects.get(index);
    }

    public List<CollisionObject> getOverlappingPairs() {
        return overlappingObjects;
    }

    static public GhostObject upcast(CollisionObject colObj) {
        return colObj instanceof GhostObject ? (GhostObject)colObj : null;
    }

    void  convexSweepTest(ConvexShape castShape, 
                          Transform convexFromWorld, 
                          Transform convexToWorld, 
                          CollisionWorld.ConvexResultCallback resultCallback, 
                          float allowedCcdPenetration) 
    {
        Transform	convexFromTrans,convexToTrans;
        convexFromTrans = new Transform(convexFromWorld);
        convexToTrans = new Transform(convexToWorld);
        Vector3f castShapeAabbMin = new Vector3f(), castShapeAabbMax = new Vector3f();
        // Compute AABB that encompasses angular movement 
        {
            Vector3f linVel = new Vector3f(), angVel = new Vector3f();
            TransformUtil.calculateVelocity (convexFromTrans, convexToTrans, 1f, linVel, angVel);
            Transform R = new Transform();
            R.setIdentity ();
            R.setRotation (convexFromTrans.getRotation(null));
            castShape.calculateTemporalAabb (R, linVel, angVel, 1f, castShapeAabbMin, castShapeAabbMax);
        }
        
        /// go over all objects, and if the ray intersects their aabb + cast shape aabb,
        // do a ray-shape query using convexCaster (CCD)
        int i;
        for (i=0;i<overlappingObjects.size();i++)
            {
                CollisionObject	collisionObject= overlappingObjects.get(i);
                //only perform raycast if filterMask matches
                if(resultCallback.needsCollision(collisionObject.getBroadphaseHandle())) {
                    //RigidcollisionObject* collisionObject = ctrl->GetRigidcollisionObject();
                    Vector3f collisionObjectAabbMin = new Vector3f(),
                        collisionObjectAabbMax = new Vector3f();
                    collisionObject.getCollisionShape().getAabb(collisionObject.getWorldTransform(null),collisionObjectAabbMin,collisionObjectAabbMax);
                    aabbExpand(collisionObjectAabbMin, collisionObjectAabbMax, castShapeAabbMin, castShapeAabbMax);
                    float[] hitLambda = new float[1];
                    hitLambda[0] = 1f; //could use resultCallback.closestHitFraction, but needs testing
                    Vector3f hitNormal = new Vector3f();
                    if (rayAabb(convexFromWorld.origin,convexToWorld.origin,collisionObjectAabbMin,collisionObjectAabbMax,hitLambda,hitNormal)) {
                        CollisionWorld.objectQuerySingle(castShape, convexFromTrans,convexToTrans,
                                                         collisionObject,
                                                         collisionObject.getCollisionShape(),
                                                         collisionObject.getWorldTransform(null),
                                                         resultCallback,
                                                         allowedCcdPenetration);
                    }
                }
            }
        
    }
    
    void rayTest(Vector3f rayFromWorld, 
                 Vector3f rayToWorld, 
                 CollisionWorld.RayResultCallback resultCallback)
    {
        Transform rayFromTrans = new Transform();
        rayFromTrans.setIdentity();
        rayFromTrans.origin.set(rayFromWorld);
        Transform  rayToTrans = new Transform();
        rayToTrans.setIdentity();
        rayToTrans.origin.set(rayToWorld);
        
        
        int i;
        for (i=0;i<overlappingObjects.size();i++)
            {
                CollisionObject	collisionObject = overlappingObjects.get(i);
                //only perform raycast if filterMask matches
                if(resultCallback.needsCollision(collisionObject.getBroadphaseHandle())) 
                    {
                        CollisionWorld.rayTestSingle(rayFromTrans,rayToTrans,
                                                     collisionObject,
                                                     collisionObject.getCollisionShape(),
                                                     collisionObject.getWorldTransform(null),
                                                     resultCallback);
                    }
            }
    }
}

class PairCachingGhostObject extends GhostObject {
    
    HashedOverlappingPairCache hashPairCache;

    public PairCachingGhostObject() {
        hashPairCache = new HashedOverlappingPairCache();
    }
    void addOverlappingObjectInternal(BroadphaseProxy otherProxy, BroadphaseProxy thisProxy)
{
    BroadphaseProxy actualThisProxy = thisProxy != null ? thisProxy : getBroadphaseHandle();

    CollisionObject otherObject = (CollisionObject)otherProxy.clientObject;
    int index = overlappingObjects.indexOf(otherObject);
    if (index==overlappingObjects.size())
	{
            overlappingObjects.add(otherObject);
            hashPairCache.addOverlappingPair(actualThisProxy,otherProxy);
	}
}

    void removeOverlappingObjectInternal(BroadphaseProxy otherProxy,
                                         Dispatcher dispatcher,
                                         BroadphaseProxy thisProxy1) {
	CollisionObject otherObject = (CollisionObject)otherProxy.clientObject;
	BroadphaseProxy actualThisProxy = thisProxy1 != null? thisProxy1 : getBroadphaseHandle();
        if (overlappingObjects.remove(otherObject)) {
            {
                hashPairCache.removeOverlappingPair(actualThisProxy,otherProxy,dispatcher);
            }
        }
    }
}


class GhostPairCallback implements OverlappingPairCallback {

    public BroadphasePair addOverlappingPair(BroadphaseProxy proxy0,
                                      BroadphaseProxy proxy1) {
        CollisionObject colObj0 = (CollisionObject) proxy0.clientObject;
        CollisionObject colObj1 = (CollisionObject) proxy1.clientObject;
        GhostObject ghost0 = 		GhostObject.upcast(colObj0);
        GhostObject ghost1 = 		GhostObject.upcast(colObj1);
        if (ghost0 != null)
            ghost0.addOverlappingObjectInternal(proxy1, proxy0);
        if (ghost1 != null)
            ghost1.addOverlappingObjectInternal(proxy0, proxy1);
        return null;
    }

    public Object removeOverlappingPair(BroadphaseProxy proxy0,
                                        BroadphaseProxy proxy1,
                                        Dispatcher dispatcher)
	{
            CollisionObject colObj0 = (CollisionObject) proxy0.clientObject;
            CollisionObject colObj1 = (CollisionObject) proxy1.clientObject;
            GhostObject ghost0 = 		GhostObject.upcast(colObj0);
            GhostObject ghost1 = 		GhostObject.upcast(colObj1);
            if (ghost0 != null)
                ghost0.removeOverlappingObjectInternal(proxy1,dispatcher,proxy0);
            if (ghost1 != null)
                ghost1.removeOverlappingObjectInternal(proxy0,dispatcher,proxy1);
            return null;
	}
    
    public void removeOverlappingPairsContainingProxy(BroadphaseProxy proxy0, Dispatcher dispatcher) {
        throw new RuntimeException("Not yet implemented in Bullet");
        //need to keep track of all ghost objects and call them here
        //hashPairCache->removeOverlappingPairsContainingProxy(proxy0,dispatcher);
    }
}
