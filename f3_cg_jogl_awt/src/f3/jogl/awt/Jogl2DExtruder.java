package f3.jogl.awt;
import java.util.ArrayList;
import java.util.Iterator;
import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import java.nio.*;
import java.awt.Shape;
import java.awt.geom.*;
import f3.media.scene.ImportUtils.*;
import f3.media.scene.FloatBufferBuilder;
import f3.media.scene.IntBufferBuilder;


public class Jogl2DExtruder {

    static float DEFAULT_FLATNESS;
    static {
        String str = System.getProperty("jogl.stage.2d.flatness");
        try {
            DEFAULT_FLATNESS = Float.parseFloat(str);
        } catch (Exception exc) {
            DEFAULT_FLATNESS = 0.05f;
        }
    }
    static void toDoubleArray(double[] a2, float[] a1) {
        for (int i = 0; i< a1.length; i++) {
            a2[i] = a1[i];
        }
    }

    public static class Extrusion {
        Shape shape;
        private float depth = 1f;
        private boolean edgeOnly = false;

        private boolean flipY = false;
        
        private boolean calcNormals = true;
        private float flatness = DEFAULT_FLATNESS;
        
        private Vector3 vecA = new Vector3();
        private Vector3 vecB = new Vector3();
        private Vector3 normal = new Vector3();
        
        final static GLU glu = GLU.createGLU();
        
        private int lastIndex = -1;
        private ArrayList<Integer> listIndex = new ArrayList<Integer>();

        public FloatBufferBuilder vertices = new FloatBufferBuilder();
        public FloatBufferBuilder normals = new FloatBufferBuilder();
        public FloatBufferBuilder texCoords = new FloatBufferBuilder();
        public IntBufferBuilder indices = new IntBufferBuilder();
        int index;
        float[] glNormal = new float[] { 0, 0, 1};

        void glNormal3f(float x, float y, float z) {
            glNormal[0] = x; glNormal[1] = y; glNormal[2] = z;
        }

        void glVertex3fv(float [] xyz, float w) {
            glVertex3f(xyz[0], xyz[1], xyz[2]);
        }

        float translateZ = 0f;

        public boolean gridFit = false;

        AffineTransform textureMatrix;

        Point2D pt = new Point2D.Float();
        Point2D pt2 = new Point2D.Float();

        void glVertex3f(float x, float y, float z) {
            normals.add(glNormal[0], glNormal[1], glNormal[2]);
            float stx = (x - (float)bounds.getX()) / (float)bounds.getWidth();
            float sty = (y - (float)bounds.getY()) / (float)bounds.getHeight();
            if (textureMatrix != null) {
                System.err.println("textureMatrix="+textureMatrix);
                pt.setLocation(stx, sty);
                textureMatrix.transform(pt, pt2);
                System.err.println(pt + " => "+ pt2);
                stx = (float)pt2.getX(); 
                sty = (float)pt2.getY();
            }
            texCoords.add(stx, sty);
            if (gridFit) {
                x = Math.round(x);
                y = Math.round(y);
                z = Math.round(z);
            }
            vertices.add(x, y, z+translateZ);
            indices.add(index++);
        }

        public void setTextureMatrix(AffineTransform tx) {
            textureMatrix = tx;
        }

        public MeshData createMeshData() {
            draw(shape);
            return getMeshData();
        }

        FloatBuffer verticesBuffer;
        FloatBuffer normalsBuffer;
        FloatBuffer texCoordsBuffer;
        IntBuffer indicesBuffer;


        public MeshData getMeshData() {
            MeshData meshData = new MeshData();
            FloatBufferData texCoords = new FloatBufferData(this.texCoords.getBuffer(texCoordsBuffer, true), 2);
            meshData.setVertexBuffer(vertices.getBuffer(verticesBuffer, true));
            meshData.setNormalBuffer(normals.getBuffer(normalsBuffer, true));
            meshData.setTextureCoords(texCoords, 0);
            meshData.setIndexBuffer(indices.getBuffer(indicesBuffer, true));
            return meshData;
        }
        
        /**
         * Instantiates a new  initially rendering in the specified
         * font.
         * 
         * @param font - the initial font for this 
         *        depth - the extruded depth
         * @throws java.lang.NullPointerException
         *             if the supplied font is null
         */
        public Extrusion(Shape shape, boolean flipY, float depth)
        {
            this.shape = shape;
            this.depth = depth;
            this.flipY = flipY;
        }

        public void resetShape(Shape shape) {
            glNormal = new float[] { 0, 0, 1};
            this.shape = shape;
	    index = 0;
            vertices.reset();
            normals.reset();
            texCoords.reset();
            indices.reset();
        }
        
        
        
        /**
         * Determines how long the sides of the rendered text is. In the special
         * case of 0, the rendering is 2D.
         * 
         * @param depth
         *            specifies the z-size of the rendered 3D text. Negative numbers
         *            will be set to 0.
         */
        public void setDepth(float depth)
        {
            if (depth <= 0)
                this.depth = 0;
            else
                this.depth = depth;
        }
        
        /**
         * Retrieves the z-depth used for this TextRenderer3D's text rendering.
         * 
         * @return the z-depth of the rendered 3D text.
         */
        public float getDepth()
        {
            return this.depth;
        }
        
        /**
         * Sets if the text should be rendered as filled polygons or wireframe.
         * 
         * @param fill
         *            if true, uses filled polygons, if false, renderings are
         *            wireframe.
         */
        public void setFill(boolean fill)
        {
            this.edgeOnly = !fill;
        }
        
        /**
         * Determines if the text is being rendered as filled polygons or
         * wireframes.
         * 
         * @return if true, uses filled polygons, if false, renderings are
         *         wireframe.
         */
        public boolean isFill()
        {
            return !this.edgeOnly;
        }
        
        /**
         * Set the flatness to which the glyph's curves will be flattened
         * 
         * @return
         */
        public float getFlatness()
        {
            return flatness;
        }
        
        /**
         * Get the current flatness to which the glyph's curves will be flattened
         * 
         * @return
         */
        public void setFlatness(float flatness)
        {
            this.flatness = flatness;
        }
        
        /**
         * Sets whether the normals will eb calculated for each face
         * 
         * @param mode
         *            the mode to render in. Default is flat.
         */
        public void setCalcNormals( boolean normals)
        {
            this.calcNormals = normals;
        }
        
        /**
         * Gets whether normals are being calculated
         * 
         * @see setNormal
         * @return the normal technique for this TextRenderer3D.
         */
        public boolean getCalcNormals()
        {
            return this.calcNormals;
        }
        
        AffineTransform getTransform() {
            AffineTransform t = new AffineTransform();
            if (flipY) {
                t.scale(1, -1);
                return t;
            }
            return t;
        }

        public void draw(AffineTransform t) {
            draw(shape, t);
        }
        
        /**
         * Renders a string into the specified GL object, starting at the (0,0,0)
         * point in OpenGL coordinates.
         * 
         * @param str
         *            the string to render.
         * @param glu
         *            a GLU instance to use for the text rendering (provided to
         *            prevent continuous re-instantiation of a GLU object)
         * @param gl
         *            the OpenGL context in which to render the text.
         */
        public void draw(Shape gp) {
            draw(gp, null);
        }

        Rectangle2D bounds;

        public void draw(Shape gp, AffineTransform t) {
            this.shape = shape;
            bounds = gp.getBounds2D();
            if (bounds.getWidth() == 0 || bounds.getHeight() == 0) {
                throw new RuntimeException("empty bounds: "+ bounds);
            }
            tess = glu.gluNewTess();
            AffineTransform transform = new AffineTransform();
            if (t != null) {
                transform.concatenate(t);
            }
            transform.concatenate(getTransform());
            PathIterator pi = gp.getPathIterator(transform, flatness);
            
            // dumpShape(gl, gp);
            
            if (calcNormals) {
                glNormal3f(0, 0, 1.0f);
            }
            translateZ = 0;
            tesselateFace(glu, pi, this.edgeOnly, -this.depth/2);
            
            if (this.depth != 0.0)
                {
                    pi = gp.getPathIterator(getTransform(), flatness);
                    
                    if (calcNormals) {
                        glNormal3f(0, 0, 1.0f);
                    }
                    
                    tesselateFace(glu, pi, this.edgeOnly, this.depth/2);
                    
                    pi = gp.getPathIterator(getTransform(), flatness);
                    
                    // TODO: add diagonal corner/VBO technique
                    translateZ = -depth /2;

                    drawSides(pi, this.edgeOnly, this.depth);
                }
            tess = null;
        }        
        
        
        /**
         * Get the bounding box for the supplied string with the current font, etc.
         * 
         * @param str
         * @return
         */
        public Rectangle2D  getBounds( Shape gp )
        {
            return gp.getBounds2D();
        }        
        
        
        // construct the sides of each glyph by walking around and extending each vertex
        // out to the depth of the extrusion
        private void drawSides(PathIterator pi, boolean justBoundary, float depth)
        {
            
            float[] lastCoord = new float[3];
            float[] firstCoord = new float[3];
            float[] coords = new float[6];
            boolean sawEnd = false;
            while ( !pi.isDone() )
                {
                    switch (pi.currentSegment(coords))
                        {
                        case PathIterator.SEG_MOVETO:
                            lastCoord[0] = coords[0];
                            lastCoord[1] = coords[1];
                            firstCoord[0] = coords[0];
                            firstCoord[1] = coords[1];
                            sawEnd = false;
                            break;
                        case PathIterator.SEG_LINETO:
                            if (calcNormals)
                                setNormal(lastCoord[0]-coords[0], 
                                          lastCoord[1]-coords[1], 
                                          0.0f, 0.0f, 0.0f, depth);
                            
                            
                            // 0
                            lastCoord[2] = 0;
                            glVertex3fv(lastCoord, 0);
                            // 1
                            lastCoord[2] = depth;
                            
                            glVertex3fv(lastCoord, 0);
                            // 2
                            coords[2] = depth;
                            
                            glVertex3fv(coords, 0);

                            // 3
                            coords[2] = 0;
                            glVertex3fv(coords, 0);
                            // 0
                            lastCoord[2] = 0;
                            glVertex3fv(lastCoord, 0);

                            // 2
                            coords[2] = depth;
                            glVertex3fv(coords, 0);
                            
                            if (calcNormals)
                                {
                                    lastCoord[0] = coords[0];
                                    lastCoord[1] = coords[1];
                                }
                            break;
                        case PathIterator.SEG_CLOSE:
                            if(calcNormals)
                                setNormal(lastCoord[0]-firstCoord[0], lastCoord[1]-firstCoord[1], 0.0f, 
                                          0.0f, 0.0f, depth );
                            
                            
                            // 0
                            lastCoord[2] = 0;
                            glVertex3fv(lastCoord, 0);
                            // 1
                            lastCoord[2] = depth;
                            glVertex3fv(lastCoord, 0);
                            // 2
                            firstCoord[2] = depth;
                            glVertex3fv(firstCoord, 0);
                            // 3
                            firstCoord[2] = 0;
                            glVertex3fv(firstCoord, 0);
                            // 0
                            lastCoord[2] = 0;
                            glVertex3fv(lastCoord, 0);
                            // 2
                            firstCoord[2] = depth;
                            glVertex3fv(firstCoord, 0);
                            sawEnd = true;
                            break;
                        default:
                            throw new RuntimeException(
                                                       "PathIterator segment not SEG_MOVETO, SEG_LINETO, SEG_CLOSE; Inappropriate font.");
                        }
                    
                    pi.next();
                }
            if (!sawEnd) {
                //                gl.glEnd();
            }
        }
        
        // simple convenience for calculating and setting the normal
        private void setNormal (float x1, float y1, 
                                float z1, float x2, 
                                float y2, float z2 )
        {
            vecA.set( x1, y1, z1);
            vecB.set( x2, y2, z2);
            vecA.cross(vecB, normal);
            normal.normalizeLocal();
            glNormal3f( normal.getX(), normal.getY(), normal.getZ() );
        }

        GLUtessellator tess;
        
        // routine that tesselates the current set of glyphs
        private void tesselateFace(GLU glu, PathIterator pi, boolean justBoundary, float tessZ)
        {
            //            if (true) return;
            GLUtessellatorCallback aCallback = new GLUtesselatorCallbackImpl();
            
            glu.gluTessCallback(tess, glu.GLU_TESS_BEGIN, aCallback);
            glu.gluTessCallback(tess, glu.GLU_TESS_END, aCallback);
            glu.gluTessCallback(tess, glu.GLU_TESS_ERROR, aCallback);
            glu.gluTessCallback(tess, glu.GLU_TESS_VERTEX, aCallback);
            glu.gluTessCallback(tess, glu.GLU_TESS_COMBINE, aCallback);
            glu.gluTessCallback(tess, glu.GLU_TESS_EDGE_FLAG, aCallback);
            
            glu.gluTessNormal(tess, 0.0, 0.0, Math.signum(tessZ));
            
            if ( pi.getWindingRule() == PathIterator.WIND_EVEN_ODD) {
                glu.gluTessProperty(tess, glu.GLU_TESS_WINDING_RULE, glu.GLU_TESS_WINDING_ODD);
            } else  {
                glu.gluTessProperty(tess, glu.GLU_TESS_WINDING_RULE, glu.GLU_TESS_WINDING_NONZERO);
            }
            if (justBoundary)
                glu.gluTessProperty(tess, glu.GLU_TESS_BOUNDARY_ONLY, GL.GL_TRUE);
            else
                glu.gluTessProperty(tess, glu.GLU_TESS_BOUNDARY_ONLY, GL.GL_FALSE);
            
            glu.gluTessBeginPolygon(tess, (double[]) null);
            
            boolean sawEnd = false;
            boolean first = true;
            float lastX = 0;
            float lastY = 0;
            float movx = 0;
            float movy = 0;
            while (!pi.isDone())
                {
                    float[] coords = new float[3];
                    double[] dCoords = new double[3];
                    coords[2] = tessZ;
                    switch (pi.currentSegment(coords))
                        {
                        case PathIterator.SEG_MOVETO:
                            if (first) {
                                first = false;
                            } else if (!sawEnd) {
                                glu.gluTessEndContour(tess);
                            }
                            sawEnd = false;
                            glu.gluTessBeginContour(tess);
                            movx = lastX = coords[0];
                            movy = lastY = coords[1];
                            break;
                        case PathIterator.SEG_LINETO:
                            toDoubleArray(dCoords, coords);
                            glu.gluTessVertex(tess, dCoords, 0, dCoords);
                            break;
                        case PathIterator.SEG_CLOSE:
                            if (true || (lastX != movx || lastY != movy)) {
                                coords[0] = movx;
                                coords[1] = movy;
                                toDoubleArray(dCoords, coords);
                                glu.gluTessVertex(tess, dCoords, 0, dCoords);
                            }
                            glu.gluTessEndContour(tess);
                            sawEnd = true;
                            break;
                        }
                    pi.next();
                }
            if (!sawEnd) {
                glu.gluTessEndContour(tess);
            }
            glu.gluTessEndPolygon(tess);
            glu.gluDeleteTess(tess);
        }
        
        // Private class that implements the required callbacks for the tesselator
        private class GLUtesselatorCallbackImpl extends javax.media.opengl.glu.GLUtessellatorCallbackAdapter
        {
            
            public GLUtesselatorCallbackImpl()
            {

            }
            
            public void begin(int type)
            {
                if (type != GL.GL_TRIANGLES) {
                    throw new Error("require GL_TRIANGLES");
                }
            }
            
            public void vertex(java.lang.Object vertexData)
            {
                double[] coords = (double[]) vertexData;
                
                glVertex3f((float)coords[0],
                           (float)coords[1],
                           (float)coords[2]);
            }
            
            public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
                outData[0] = new double[3];
                double[] vertex = (double[])outData[0];
                vertex[0] = coords[0];
                vertex[1] = coords[1];
                vertex[2] = coords[2];
            }
            
            public void end()
            {
                //gl.glEnd();
            }
        }
    }

    public MeshData extrude(Shape shape, boolean flipY, float depth, float flatness) {
        Extrusion extrusion = new Extrusion(shape, flipY, depth);
        extrusion.flatness = flatness;
        return extrusion.createMeshData();
    }

    public MeshData extrude(Shape shape, boolean flipY, float depth) {
        return extrude(shape, flipY, depth, .05f);
    }


}
