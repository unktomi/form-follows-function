package org.f3.tools.api;

import java.util.ArrayList;
import java.util.List;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;

/**
 * Saves diagnostics for later test reference.
 * 
 * @author tball
 */
class MockDiagnosticListener<T> implements DiagnosticListener<T> {

    public boolean printErrors = true;

    public void report(Diagnostic<? extends T> d) {
        diagCodes.add(d.getCode());
        if (printErrors)
            System.err.println(d);
    }
    public List<String> diagCodes = new ArrayList<String>();

    public int errors() {
        return diagCodes.size();
    }
}
