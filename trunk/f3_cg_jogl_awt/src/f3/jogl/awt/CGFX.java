package f3.jogl.awt;
import javax.media.opengl.*;
import com.jogamp.opengl.cg.*;
import static com.jogamp.opengl.cg.CgGL.*;
import java.net.URL;
import java.io.*;
import java.util.*;
import java.nio.*;

public class CGFX {

    private static void checkCgError()  {
        int err = CgGL.cgGetError();
        if (err != CgGL.CG_NO_ERROR) {
            System.out.println("CG error: " + CgGL.cgGetErrorString(err));
            throw new RuntimeException(CgGL.cgGetErrorString(err));
        }
    } 

    Map<String,Effect> effects = new HashMap();

    public void clearEffects() { effects.clear(); }

    CGcontext _context;
    int vp;
    int fp;

    CGcontext getContext() {
        if (_context == null) {
            _context = CgGL.cgCreateContext();
            //CgGL.cgGLSetDebugMode(true);
            CgGL.cgGLRegisterStates(_context);
            checkCgError();
            CgGL.cgGLSetManageTextureParameters(_context, true);
            checkCgError();
            vp = cgGLGetLatestProfile(CG_GL_VERTEX);
            fp = cgGLGetLatestProfile(CG_GL_FRAGMENT);
            cgGLSetOptimalOptions(vp);
            cgGLSetOptimalOptions(fp);
            checkCgError();
        }
        return _context;
    }

    public Effect createEffect(URL u) {
        String key = u.toString();
        Effect result = effects.get(key);
        if (result == null) {
            result = new Effect(u, this);
            effects.put(key, result);
            result.load();
        }
        return result;
    }

    public static class Effect {

        URL url;
        CGFX _context;
        Map<String, CGparameter> _params = new HashMap();
        Map<String, CGtechnique> _techs = new HashMap();

        public Effect(URL u, CGFX context) {
            url = u;
            _context = context;
        }

        public CGeffect getEffect() {
            if (_effect == null) {
                load();
            }
            return _effect;
        }

        String readFile(URL url) throws Exception {
            String result = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#include")) {
                    line = line.substring(8).trim();
                    int left = line.indexOf("\"");
                    int right = line.lastIndexOf("\"");
                    line = line.substring(left+1, right);
                    result += readFile(new URL(url, line));
                    result += "\n";
                } else {
                    result += line + "\r\n";
                }
            }
            return result;
        }

        public void load() {
            try {
                int error = 0;
                String result = readFile(url);
                System.out.println("loading cgfx "+url);
                _effect = CgGL.cgCreateEffect( _context.getContext(), result, null ); 
                if( _effect == null ) {
                    error = CgGL.cgGetError(); 
                    String str = url+CgGL.cgGetLastListing(_context.getContext());
                    System.out.println(str);
                    throw new RuntimeException(str);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public Parameter[] getParameters() {
            List<Parameter> result = new LinkedList();
            CGparameter p = CgGL.cgGetFirstEffectParameter(getEffect());
            while (p != null) {
                result.add(createParameter(p));
                p = CgGL.cgGetNextParameter(p);
            }
            return result.toArray(new Parameter[result.size()]);
        }
	
        public void setFirstTechnique() {
            _currTechnique = CgGL.cgGetFirstTechnique( getEffect() );    
            if ( _currTechnique == null ) System.err.println( "(ShaderCGFX)  technique is null" );
            // Get technique's first pass
            _currPass = CgGL.cgGetFirstPass( _currTechnique );
        }

        CGtechnique getNamedTechnique(CGeffect effect, String name) {
            CGtechnique result = _techs.get(name);
            if (result == null) {
                result = CgGL.cgGetNamedTechnique(effect, name);
                _techs.put(name, result);
            }
            return result;
        }

        String currentTechnique;
        boolean passDirty = true;
        
        public void setTechnique( String name ) {
            if (name.equals(currentTechnique)) {
                _currPass = CgGL.cgGetFirstPass( _currTechnique );
                return;
            }
            currentTechnique = name;
            _currTechnique = getNamedTechnique( getEffect(), name );    
            if( _currTechnique == null ) System.err.println( "(ShaderCGFX)  technique '" + name + "' is null" );
            
            if (!CgGL.cgValidateTechnique(_currTechnique)) {
                System.out.println(url+ ": technique isn't valid: " + name);
                System.err.println( "(ShaderCGFX)  Last Listing: " + CgGL.cgGetLastListing(_context.getContext()) ); 
            } 
            // Get technique's first pass
            _currPass = CgGL.cgGetFirstPass( _currTechnique );
            passDirty = true;
        }

        public boolean hasBlendOnFirstPass() {
            // fix me
            CGpass pass =  CgGL.cgGetFirstPass( _currTechnique );
            for (CGstateassignment a = cgGetFirstStateAssignment(pass); a != null;
                 a = cgGetNextStateAssignment(a)) {
                CGstate state = cgGetStateAssignmentState(a);
                String name = cgGetStateName(state);
                if (name == "BlendEnable") {
                    //boolean[] result = CGPlus.getBoolStateAssignmentValues(a);
                    //if (result != null && result.length > 0) {
                    //    return result[0];
                    //}
                } 
            }
            return false;
        }

        Set<String> getPassStates() {
            return getPassStates(_currPass);
        }

        Set<String> getPassStates(CGpass pass) {
            Set<String> passStates = new HashSet<String>();
            for (CGstateassignment a = cgGetFirstStateAssignment(pass); a != null;
                 a = cgGetNextStateAssignment(a)) {
                CGstate state = cgGetStateAssignmentState(a);
                //                System.out.println("setting pass state " + cgGetStateName(state));
                passStates.add(cgGetStateName(state));
            }
            return passStates;
        }
	
        public void setPass() {
            if (passDirty) {
                CgGL.cgSetPassState( _currPass );
                int error = CgGL.cgGetError(); 
                if (error != 0) {
                    System.err.println( "(ShaderCGFX)  Error String: " + CgGL.cgGetErrorString(error) ); 
                }
                //                passDirty = false;
            }
        }
        
        public boolean nextPass() {
            _currPass = CgGL.cgGetNextPass( _currPass );
            if (_currPass != null && cgIsPass(_currPass)) {
                CgGL.cgSetPassState( _currPass );
                return true;
            }
            return false;
        }
        
        public void resetPass() {
            CgGL.cgResetPassState( _currPass ); 
        }
        
        public CGpass getFirstTechniquePass() {
            _currTechnique = CgGL.cgGetFirstTechnique( getEffect() );    
            if( _currTechnique == null ) System.err.println( "(ShaderCGFX)  first technique is null" );
            
            return CgGL.cgGetFirstPass( _currTechnique );
        } 
        
        public CGpass getTechniqueFirstPass( String name ) {
            _currTechnique = CgGL.cgGetNamedTechnique( getEffect(), name );    
            if( _currTechnique == null ) System.err.println( "(ShaderCGFX)  technique '" + name + "' is null" );
            
            return CgGL.cgGetFirstPass( _currTechnique );
        } 

        CGparameter getNamedEffectParameter(CGeffect effect, String param) {
            CGparameter result = _params.get(param);
            if (result == null) {
                result = CgGL.cgGetNamedEffectParameter(effect, param);
                _params.put(param, result);
            }
            return result;
        }

        public int getTextureUnit(String param) {
            CGparameter p = null;

            //CGprogram prog = cgGetPassProgram(_currPass, CG_FRAGMENT_DOMAIN);
            //p = cgGetNamedParameter(prog, param);
            p = cgGetNamedEffectParameter(getEffect(), param);
            int unit = cgGLGetTextureEnum(p) - GL.GL_TEXTURE0;
            return unit;
        }
	
        public void setTextureParameter( String param, int val ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgGLSetTextureParameter( p, val );
            else System.err.println( "(ShaderCGFX)  Cant find texture parameter" );
            CgGL.cgSetSamplerState( p );
        }
        
        public void setParameter1f( String param, float x ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter1f( p, x );
            else System.err.println( "(ShaderCGFX)  param1f is null" ); 
        }
        
        public void setParameter1i( String param, int x ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter1i( p, x );
            else System.err.println( "(ShaderCGFX)  param1f is null" ); 
        }
        
        public void setParameter2f( String param, float x, float y ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter2f( p, x, y );
            else System.err.println( "(ShaderCGFX)  param2f is null" ); 
        }
        
        public void setParameter3f( String param, float x, float y, float z ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter3f( p, x, y, z );
            else System.err.println( "(ShaderCGFX)  param3f is null" ); 
        }
	
        public void setParameter3fv( String param, float[] v ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter3fv( p, v, 0 );
            else System.err.println( "(ShaderCGFX)  param3fv is null" ); 
        }
	
        public void setParameter4f( String param, float x, float y, float z, float w ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter4f( p, x, y, z, w );
            else System.err.println( "(ShaderCGFX)  param4f is null" ); 
        }
        
        public void setParameter4fv( String param, float[] v ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter4fv( p, v, 0 );
            else System.err.println( "(ShaderCGFX)  param4fv is null" ); 
        }

        public void setMatrixParameter(String param, String semantic) {
            int matrix = 0;
            int op = CG_GL_MATRIX_IDENTITY;
            if (semantic.equals("WORLD")) {
                matrix = CG_GL_MODELVIEW_MATRIX;
            } else if (semantic.equals("VIEW")) {
                matrix = CG_GL_MODELVIEW_MATRIX;
            } else if (semantic.equals("VIEWINVERSE")) {
                matrix = CG_GL_MODELVIEW_MATRIX;
                op = CG_GL_MATRIX_INVERSE;
            } else if (semantic.equals("WORLDVIEW")) {
                matrix = CG_GL_MODELVIEW_MATRIX;
            } else if (semantic.equals("WORLDVIEWINVERSE")) {
                matrix = CG_GL_MODELVIEW_MATRIX;
                op = CG_GL_MATRIX_INVERSE;
            } else if (semantic.equals("WORLDVIEWPROJECTION")) {
                matrix = CG_GL_MODELVIEW_PROJECTION_MATRIX;
                op = CG_GL_MATRIX_INVERSE;
            }  else {
                System.out.println("fix me semantic: "+semantic);
            }
            setMatrixParameterSemantic(semantic, matrix, op);
        }
        
        public void setMatrixParameterSemantic( String param, int matrix, int matrixType ) {
            CGparameter p = null;
            p = CgGL.cgGetEffectParameterBySemantic( getEffect(), param );
            if( p != null )	CgGL.cgGLSetStateMatrixParameter( p, matrix, matrixType );
            else 	System.err.println( "(ShaderCGFX)  setParameterSemantic(mat, type) is null for "+param );
        }
        
        public void setMatrixParameterSemantic( String param, float[] v ) {
            CGparameter p = null;
            p = CgGL.cgGetEffectParameterBySemantic( getEffect(), param );
            //p = getNamedEffectParameter( getEffect(), param );
            if( p != null )	CgGL.cgGLSetMatrixParameterfr( p, v, 0 );
            else 	System.err.println( "(ShaderCGFX)  setParameterSemantic(v[]) is null" );
        }
	
        public void setParameterSemantic( String param, float x ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter1f( p, x );
            else System.err.println( "(ShaderCGFX)  setParameterSemantic(x) is null" ); 
        }
        
        public void setParameterSemantic( String param, float x, float y ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter2f( p, x, y );
            else System.err.println( "(ShaderCGFX)  setParameterSemantic(x, y) is null" ); 		
        }
        
        public void setParameterSemantic( String param, float x, float y, float z ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter3f( p, x, y, z );
            else System.err.println( "(ShaderCGFX)  setParameterSemantic(x, y, z) is null" ); 				
        }
        public void setParameterSemantic( String param, float x, float y, float z, float w ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null ) CgGL.cgSetParameter4f( p, x, y, z, w );
            else System.err.println( "(ShaderCGFX)  setParameterSemantic(x, y, z, w) is null" ); 						
        }
        
        public void setParameter4x4fr( String param, float[] v ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null )	CgGL.cgGLSetMatrixParameterfr( p, v, 0 );
            else System.err.println( "(ShaderCGFX)  matrix4x4f param is null" );
        }

        public void setParameter4x4fc( String param, float[] v ) {
            CGparameter p = null;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null )	CgGL.cgGLSetMatrixParameterfc( p, v, 0 );
            else System.err.println( "(ShaderCGFX)  matrix4x4f param is null" );
        }
        
        public void setParameter4x4f( String param, int matrix, int matrixType ) {
            CGparameter p;
            p = getNamedEffectParameter( getEffect(), param );
            if( p != null )	CgGL.cgGLSetStateMatrixParameter( p, matrix, matrixType );
            else System.err.println( "(ShaderCGFX)  matrix4x4f semantic param is null" );
        }
        
        public void setParameter4x4fBySemantic( String semanticName, int matrix, int matrixType ) {
           
            CGparameter p;
            p = CgGL.cgGetEffectParameterBySemantic( getEffect(), semanticName );
            if( p != null )	CgGL.cgGLSetStateMatrixParameter( p, matrix, matrixType );
            else System.err.println( "(ShaderCGFX)  matrix4x4f semantic param is null" );
            
        }

        public String[] getSamplerStateAssignmentNames(String sampler) {
            CGparameter p;
            p = getNamedEffectParameter( getEffect(), sampler );
            CGstateassignment sa;
            List<String> result = new LinkedList();
            for (sa = cgGetFirstSamplerStateAssignment(p); sa != null; 
                 sa = cgGetNextStateAssignment(sa)) {
                result.add(cgGetStateName(cgGetSamplerStateAssignmentState(sa)));
            }
            return result.toArray(new String[result.size()]);
        }

        CGstateassignment getSamplerStateAssignment(CGparameter p, String name) {
            CGstateassignment sa;
            for (sa = cgGetFirstSamplerStateAssignment(p); sa != null; 
                 sa = cgGetNextStateAssignment(sa)) {
                if (name.equals(cgGetStateName(cgGetSamplerStateAssignmentState(sa)))) {
                    return sa;
                }
            }
            return null;
        }

        public Object getSamplerStateAssignmentValue(String sampler, String name) {
            CGparameter p;
            p = getNamedEffectParameter( getEffect(), sampler );
            if (p == null) {
                return null;
            }
            CGstateassignment sa = cgGetNamedSamplerStateAssignment(p, name);
            checkCgError();
            if (sa == null) {
                return null;
            }
            if (!cgIsStateAssignment(sa)) {
                return null;
            }
            int type = cgGetStateType(cgGetSamplerStateAssignmentState(sa));
            //System.out.println("type of " + name + " = " + cgGetTypeString(type));
            checkCgError();
            Object result = null;
            switch (type) {
            case CG_INT:
                //result = CGPlus.getIntStateAssignmentValues(sa);
                break;
            case CG_FLOAT:
                //result =  CGPlus.getFloatStateAssignmentValues(sa);
                break;
            case CG_BOOL:
                //result = CGPlus.getBoolStateAssignmentValues(sa);
                break;
            }
            checkCgError();
            return result;
        }

        public void setupSampler(String param, int tex) {
            CGparameter p;
            p = getNamedEffectParameter(getEffect(), param);
            //            System.out.println("set up sampler param " + p + " " + tex);
            cgGLSetupSampler(p, tex);
            checkCgError();
        }
        
        public void enableSampler(String param, int tex) {
            CGparameter p;
            p = getNamedEffectParameter( getEffect(), param );
            //            System.out.println("set up sampler param " + p + " " + tex);
            cgGLEnableTextureParameter(p);
            checkCgError();
        }


        CGeffect _effect;
        CGtechnique _currTechnique;
        CGpass _currPass;
    }

    public static class StructParameter extends Parameter {

        Parameter[] members;
        
        public Parameter[] getMembers() {
            return members;
        }

        public String getType() {
            return "struct";
        }

        StructParameter(CGparameter param, Parameter[] members) {
            super(param);
            this.members = members;
        }
    }

    public static class Parameter {
        
        static Parameter[] EMPTY = new Parameter[0];

        CGparameter p;

        Parameter(CGparameter p) {
            this.p = p;
        }

        public Parameter[] getMembers() {
            return EMPTY;
        }

        public String getName() {
            return CgGL.cgGetParameterName(p);
        }

        public String getType() {
            int type = CgGL.cgGetParameterType(p);
            return CgGL.cgGetTypeString(type);
        }

        public String getSemantic() {
            return CgGL.cgGetParameterSemantic(p);
        }
        public void set(int value) {
            cgSetParameter1i(p, value);
        }
        public void set(float value) {
            cgSetParameter1f(p, value);
        }
        public void set(float value1, float value2) {
            cgSetParameter2f(p, value1, value2);
        }
        public void set(float value1, float value2, float value3) {
            cgSetParameter3f(p, value1, value2, value3);
        }
        public void set(float value1, float value2, float value3, float value4) {
            cgSetParameter4f(p, value1, value2, value3, value4);
        }
        public void setc(float[] value) {
            cgGLSetMatrixParameterfc(p, value, 0);
        }
        public void setr(float[] value) {
            cgGLSetMatrixParameterfr(p, value, 0);
        }
        public void set(FloatBuffer value) {
            cgSetParameter1fv(p, value);
        }
    }

    static Parameter createParameter(CGparameter p) {
        int type = CgGL.cgGetParameterType(p);
        if (type == 1) { // CG_STRUCT
            CGparameter pp = CgGL.cgGetFirstStructParameter(p);
            List<Parameter> parms = new LinkedList();
            while (pp != null) {
                parms.add(createParameter(pp));
                pp = CgGL.cgGetNextParameter(pp);
            }
            Parameter[] arr = new Parameter[parms.size()];
            parms.toArray(arr);
            return new StructParameter(p, arr);
        }
        return new Parameter(p);
    }
}
