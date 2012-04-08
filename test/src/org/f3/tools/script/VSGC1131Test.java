/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.f3.tools.script;

import org.f3.api.F3ScriptEngine;
import java.io.File;
import java.io.FileReader;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author tball
 */
public class VSGC1131Test {

    @Test
    public void vsgc1104Test() throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();
        manager.put("greeting", "Hello");
        ScriptEngine engine = manager.getEngineByExtension("f3");
        assertTrue(engine instanceof F3ScriptEngine);
        File script = new File("test/src/org/f3/tools/script/VSGC1131.f3");
        engine.getContext().setAttribute(ScriptEngine.FILENAME, script.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
        Bindings bindings = new SimpleBindings();
        bindings.put("who", "world");
        String ret = (String)engine.eval(new FileReader(script), bindings);
        assertEquals(ret, "Hello, world");
    }
}
