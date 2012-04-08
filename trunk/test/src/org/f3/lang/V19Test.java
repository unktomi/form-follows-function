/*
 * Copyright 2010 F3 Project
 *
 * This file is part of F3. F3 is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * F3 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.f3.lang;

import org.f3.api.F3ScriptEngine;
import java.io.File;
import java.io.FileReader;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test case for the F3 project Issue 19 (http://code.google.com/p/f3/issues/detail?id=19).
 *
 * @author J.H. Kuperus
 */
public class V19Test {

  /**
   * Tests for regression on the isInitialized function.
   */
  @Test
  public void isInitializedNonNullTrue() throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByExtension("f3");
    assertTrue(engine instanceof F3ScriptEngine);
    File script = new File("test/src/org/f3/lang/V19_isInitializedNonNullTrue.f3");
    engine.getContext().setAttribute(ScriptEngine.FILENAME, script.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
    Boolean ret = (Boolean)engine.eval(new FileReader(script));
    assertTrue(ret.booleanValue());
  }

  /**
   * Tests for regression on the isInitialized function.
   */
  @Test
  public void isInitializedNonNullFalse() throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByExtension("f3");
    assertTrue(engine instanceof F3ScriptEngine);
    File script = new File("test/src/org/f3/lang/V19_isInitializedNonNullFalse.f3");
    engine.getContext().setAttribute(ScriptEngine.FILENAME, script.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
    Boolean ret = (Boolean)engine.eval(new FileReader(script));
    assertFalse(ret.booleanValue());
  }

  /**
   * Tests for the actual issue on the isInitialized function.
   */
  @Test
  public void isInitializedNull() throws Exception {
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByExtension("f3");
    assertTrue(engine instanceof F3ScriptEngine);
    File script = new File("test/src/org/f3/lang/V19_isInitializedNull.f3");
    engine.getContext().setAttribute(ScriptEngine.FILENAME, script.getAbsolutePath(), ScriptContext.ENGINE_SCOPE);
    Boolean ret = (Boolean)engine.eval(new FileReader(script));
    assertFalse(ret.booleanValue());
  }

}
