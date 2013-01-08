package org.f3.runtime;
import org.f3.functions.*;

/*
 * Equivalent to Functor.map of a * Accumulator of b
 */

public interface Accumulative<a> 
{
    public <b> b 
	accumulate(b zero, 
		   Function2<? extends b,  ? super b, ? super a> acc);
    public <b> b 
	accumulateBackwards(b zero, 
			    Function2<? extends b, ? super a, ? super b> acc);
}