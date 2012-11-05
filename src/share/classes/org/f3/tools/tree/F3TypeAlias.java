
package org.f3.tools.tree;

import org.f3.api.tree.*;
import org.f3.api.tree.Tree.F3Kind;
import com.sun.tools.mjavac.util.*;
import static com.sun.tools.mjavac.code.Symbol.*;


public class F3TypeAlias extends F3TypeAny { // hack

    public Name id;
    public List<F3Expression> typeArgs;
    public F3Type type;
    public TypeSymbol tsym;
    protected F3TypeAlias(Name id, List<F3Expression> typeArgs,
			  F3Type type) {
	super(type.getCardinality());
	this.id = id;
	this.typeArgs = typeArgs;
	this.type = type;
    }

    public Name getIdentifier() {
        return id;
    }

    public F3Kind getF3Kind() {
	return type.getF3Kind();
    }

    /*
    //@Override
    public <R, D> R accept(F3TreeVisitor<R, D> v, D d) {
        //return v.visit(this, d);
	return null;
    }
    @Override
    public void accept(F3Visitor v) {
        //v.visit(type);
    }
    */


    @Override
    public F3Tag getF3Tag() {
        return F3Tag.TYPE_ALIAS;
    }
}

