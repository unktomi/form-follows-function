/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.f3.jdi;

import com.sun.jdi.VoidValue;

/**
 *
 * @author sundar
 */
public class F3VoidValue extends F3Value implements VoidValue {
    public F3VoidValue(F3VirtualMachine f3vm, VoidValue underlying) {
        super(f3vm, underlying);
    }
    
    @Override
    protected VoidValue underlying() {
        return (VoidValue) super.underlying();
    }
}
