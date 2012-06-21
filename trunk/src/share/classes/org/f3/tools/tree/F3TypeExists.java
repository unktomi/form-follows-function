package org.f3.tools.tree;

import org.f3.api.tree.*;
import org.f3.api.tree.*;
import org.f3.api.tree.Tree.F3Kind;

import com.sun.tools.mjavac.code.Symbol.TypeSymbol;

public class F3TypeExists extends F3Type {
    public F3TypeExists() {
	super(Cardinality.SINGLETON);
    }
    @Override
    public F3Tag getF3Tag() {
        return F3Tag.TYPECLASS;
    }

    @Override
    public F3Kind getF3Kind() {
        return F3Kind.TYPE_CLASS;
    }
    @Override
    public void accept(F3Visitor v) { 
	///v.visitTypeVar(this); 
    }

    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        //return v.visitTypeVar(this, d); !!! FIX ME
	return null;
    }
    
}
