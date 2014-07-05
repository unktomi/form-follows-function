package f3.media.scene.meshdeform;

// Ported from maya open source code

public class IKSolverImpl {

    private static final float kEpsilon = 5.0e-4f;

    public static float angle(Vec3f vec1, Vec3f vec2) {
        Vec3f tmp = new Vec3f(vec2);
        tmp.normalize();
        Vec3f tmp1 = new Vec3f(vec1);
        tmp1.normalize();
        float cosine = (float)tmp1.dot(tmp);
        float angle;
        if (cosine >= 1.0)
            angle = 0.0f;
        else if (cosine <= -1.0)
            angle = (float)Math.PI;
        else
            angle = (float)Math.acos(cosine);
        return angle;
    }

    static boolean equivalent(float x, float y, float tolerance) {
        return Math.abs(x - y) <= tolerance;
    }

    static boolean isParallel(Vec3f vec1, Vec3f vec2) {
    	Vec3f v1 = new Vec3f(vec1), v2 = new Vec3f(vec2);
	v1.normalize(); 
	v2.normalize();
	float dotPrd = (float)v1.dot(v2);
	return equivalent(Math.abs(dotPrd), (float) 1.0, kEpsilon);
    }

    static Rotf makeRotf(Vec3f a, float angle) {
        Rotf q = new Rotf(a, angle);
        return q;
    }

    static Rotf makeRotf(Vec3f a, Vec3f b) {
        return makeRotf(a, b, null);
    }

    static float get(Vec3f v, int i) {
        switch (i) { 
        case 0: 
            return v.x(); 
        case 1: 
            return v.y(); 
        case 2: 
            return v.z(); 
        };
        return 0;
    }

    static Rotf makeRotf(Vec3f a, Vec3f b, Vec3f preferredAxis) {
        a = new Vec3f(a);
        a.normalize();
        b = new Vec3f(b);
        b.normalize();
        float factor = a.length() * b.length();
        if (Math.abs(factor) > kEpsilon) {
           // Vectors have length > 0
            float dot = a.dot(b) / factor;
            float theta = (float) Math.acos(Math.max(-1.0, Math.min(dot, 1.0)));
            Vec3f pivotVector = new Vec3f();
            pivotVector.cross(a, b);
            if (dot < 0 && pivotVector.length() < kEpsilon) {
                // Vectors parallel and opposite direction, therefore a rotation
                // of 180 degrees about any vector perpendicular to this vector
                // will rotate vector a onto vector b.
                if (preferredAxis == null) {
                    // The following guarantees the dot-product will be 0.0.
                    int dominantIndex;
                    if (Math.abs(a.x()) > Math.abs(a.y())) {
                        if (Math.abs(a.x()) > Math.abs(a.z())) {
                            dominantIndex = 0;
                        } else {
                            dominantIndex = 2;
                        }
                    } else {
                        if (Math.abs(a.y()) > Math.abs(a.z())) {
                            dominantIndex = 1;
                        } else {
                            dominantIndex = 2;
                        }
                    }
                    float[] p = new float[3];
                    p[dominantIndex] = -get(a, (dominantIndex+1)%3);
                    p[(dominantIndex+1)%3] = get(a, dominantIndex);
                    p[(dominantIndex+2)%3] = 0;
                    pivotVector = new Vec3f(p[0], p[1], p[2]);
                } else {
                    // Use the preferred axis
                    pivotVector = preferredAxis;
                }
            } else {
                // Nearly parallel vectors -- don't let
                // roundoff error produce wildly wrong results
                return new Rotf(0, 0, 0, 1);
            }
            return makeRotf(pivotVector, (float)theta);
        } else {
            return new Rotf(0, 0, 0, 1);
        }
    }

    void apply(Rotf q, Vec3f vec, Vec3f output) {
        if (vec.x() == 0 && vec.y() == 0 && vec.z() == 0) {
            output.set(0, 0, 0);
            return;
        } 
        final float x0 = 
            q.w() * q.w() * vec.x() + 2 * q.y() * q.w() * vec.z() - 2 * q.z() * q.w()
            * vec.y() + q.x() * q.x() * vec.x() + 2 * q.y() * q.x() * vec.y() + 2 * q.z()
                * q.x() * vec.z() - q.z() * q.z() * vec.x() - q.y() * q.y() * vec.x();
        final float y0 = 
            2 * q.x() * q.y() * vec.x() + q.y() * q.y() * vec.y() + 2 * q.z() * q.y()
            * vec.z() + 2 * q.w() * q.z() * vec.x() - q.z() * q.z() * vec.y() + q.w() * q.w()
            * vec.y() - 2 * q.x() * q.w() * vec.z() - q.x() * q.x() * vec.y();
        final float z0 = 
            2 * q.x() * q.z() * vec.x() + 2 * q.y() * q.z() * vec.y() + q.z() * q.z()
            * vec.z() - 2 * q.w() * q.y() * vec.x() - q.y() * q.y() * vec.z() + 2 * q.w()
            * q.x() * vec.y() - q.x() * q.x() * vec.z() + q.w() * q.w() * vec.z();
        output.set(x0, y0, z0);
    }

    public void solveIK(Vec3f startJointPos,
                        Vec3f midJointPos,
                        Vec3f effectorPos,
                        Vec3f handlePos,
                        Vec3f poleVector,
                        float twistValue,
                        Rotf qStart,
                        Rotf qMid)
    //
    // This is method that actually computes the IK solution.
    //
    {
        // vector from startJoint to midJoint
        Vec3f vector1 = new Vec3f();
        vector1.sub(midJointPos, startJointPos);
        // vector from midJoint to effector
        Vec3f vector2 = new Vec3f();
        vector2.sub(effectorPos, midJointPos);
        
        // vector from startJoint to handle
        Vec3f vectorH = new Vec3f();
        vectorH.sub(handlePos, startJointPos);
        // vector from startJoint to effector
        Vec3f vectorE = new Vec3f();
        vectorE.sub(effectorPos, startJointPos);
        // lengths of those vectors
        float length1 = (float)vector1.length();
        float length2 = (float)vector2.length();
        float lengthH = (float)vectorH.length();
        // component of the vector1 orthogonal to the vectorE
        Vec3f vector0 = new Vec3f();
        vector0.set(vectorE);
        vector0.scale(vector1.dot(vectorE)/vectorE.dot(vectorE));
        vector0.sub(vector1, vector0);
        
        //////////////////////////////////////////////////////////////////
        // calculate q12 which solves for the midJoint rotation
        //////////////////////////////////////////////////////////////////
        // angle between vector1 and vector2
        float vectorAngle12 = angle(vector1, vector2);
        // vector orthogonal to vector1 and 2
        Vec3f vectorCross12 = new Vec3f();
        vectorCross12.cross(vector1, vector2);
        float lengthHsquared = lengthH*lengthH;
        // angle for arm extension 
        float cos_theta = 
            (lengthHsquared - length1*length1 - length2*length2)
            /(2*length1*length2);
        if (cos_theta > 1) 
            cos_theta = 1;
        else if (cos_theta < -1) 
            cos_theta = -1;
        float theta = (float) Math.acos(cos_theta);
        // quaternion for arm extension
        Rotf q12 = makeRotf(vectorCross12, theta - vectorAngle12);
	
        //////////////////////////////////////////////////////////////////
        // calculate qEH which solves for effector rotating onto the handle
        //////////////////////////////////////////////////////////////////
        // vector2 with quaternion q12 applied
        apply(q12, vector2, vector2);
        // vectorE with quaternion q12 applied
        vectorE.add(vector1, vector2);
        // quaternion for rotating the effector onto the handle
        Rotf qEH = makeRotf(vectorE, vectorH);
        
        //////////////////////////////////////////////////////////////////
        // calculate qNP which solves for the rotate plane
        //////////////////////////////////////////////////////////////////
        // vector1 with quaternion qEH applied
        apply(qEH, vector1, vector1);
        if (isParallel(vector1, vectorH)) {
            // singular case, use orthogonal component instead
            apply(qEH, vector0, vector1);
        }
        // quaternion for rotate plane
        Rotf qNP;
        if (!isParallel(poleVector, vectorH) && (lengthHsquared != 0)) {
            // component of vector1 orthogonal to vectorH
            Vec3f vectorN = new Vec3f();
            vectorN.set(vectorH);
            vectorN.scale(vector1.dot(vectorH)/lengthHsquared);
            vectorN.sub(vector1, vectorN);
            
            // component of pole vector orthogonal to vectorH
            Vec3f vectorP = new Vec3f();
            vectorP.set(vectorH);
            vectorP.scale((poleVector.dot(vectorH))/lengthHsquared);
            vectorP.sub(poleVector, vectorP);

            float dotNP = (float) ((vectorN.dot(vectorP))/(vectorN.length()*vectorP.length()));
            if (Math.abs(dotNP + 1.0) < kEpsilon) {
                // singular case, rotate halfway around vectorH
                Rotf qNP1 = makeRotf(vectorH, (float)Math.PI);
                qNP = qNP1;
            }
            else {
                Rotf qNP2 = makeRotf(vectorN, vectorP);
                qNP = qNP2;
            }
        } else {
            qNP = new Rotf(0, 0, 0, 1);
        }
        
        //////////////////////////////////////////////////////////////////
        // calculate qTwist which adds the twist
        //////////////////////////////////////////////////////////////////
        Rotf qTwist = makeRotf(vectorH, twistValue);
        
        // quaternion for the mid joint
        qMid.set(q12);	
        // concatenate the quaternions for the start joint
        Rotf r = qTwist.times(qNP);
        qStart.mul(r, qEH);
    }
}
