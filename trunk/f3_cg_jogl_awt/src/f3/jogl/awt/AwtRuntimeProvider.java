package f3.jogl.awt;

public class AwtRuntimeProvider implements org.f3.runtime.RuntimeProvider {

    public boolean usesRuntimeLibrary(Class application) {
        return true;
    }
    
    /**
     * Starts execution of the Visage application.
     * 
     * @param entryPoint the application method to execute.
     */
    public Object run(final java.lang.reflect.Method entryPoint, final String... args) throws Throwable {
	System.err.println("args="+args);
        deferAction(new Runnable() {
                public void run() {
                    try {
                        System.out.println("AWT running "+Thread.currentThread()+": "+entryPoint);
			System.err.println("args="+args);
			org.f3.runtime.sequence.Sequence seq = 
			    org.f3.runtime.sequence.Sequences.make(org.f3.runtime.TypeInfo.String, args);
			System.err.println("seq="+seq);
                        entryPoint.invoke(null, seq);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        return null;
    }

    public void deferAction(Runnable action) {
        java.awt.EventQueue.invokeLater(action);
    }

    /**
     * Exit Visage application
     * <p>
     * Do any any nessecary cleanup here so the runtime
     * can exit cleanly
     */
    public void exit() {
        System.exit(0);
    }
}