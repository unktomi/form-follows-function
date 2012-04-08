package org.f3.tools.script;

import org.f3.api.F3ScriptEngine;
import java.io.File;
import java.io.FileReader;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author Robert Field
 * cloned from:
 * @author tball
 */
public class VSGC1865Test {

    @Test
    public void VSGC1865Test() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByExtension("f3");
        assertTrue(engine instanceof F3ScriptEngine);
        File script = new File("test/src/org/f3/tools/script/VSGC1865.f3");
        engine.getContext().setAttribute(ScriptEngine.FILENAME, script.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
        Boolean ret = (Boolean)engine.eval(new FileReader(script));
        assertTrue(ret.booleanValue());
    }
}
