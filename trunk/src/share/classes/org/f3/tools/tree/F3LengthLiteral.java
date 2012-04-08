/*
 * Copyright (c) 2010-2011, F3 Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name F3 nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.f3.tools.tree;

import org.f3.api.tree.*;
import org.f3.api.tree.Tree.F3Kind;
import f3.lang.LengthUnit;

/**
 * Tree node for length literals, such as "100cm" or "3mm".
 * @author Stephen Chin <steveonjava@gmail.com>
 */
public class F3LengthLiteral extends F3Expression implements LengthLiteralTree {
    public F3Literal value;
    public LengthUnit units;
    
    protected F3LengthLiteral(){
        this.value = null;
        this.units = null;
    }

    protected F3LengthLiteral(F3Literal value, LengthUnit units) {
        this.value = value;
        this.units = units;
    }

    @Override
    public F3Tag getF3Tag() {
        return F3Tag.LENGTH_LITERAL;
    }

    @Override
    public void accept(F3Visitor v) {
        v.visitLengthLiteral(this);
    }

    @Override
    public F3Kind getF3Kind() {
        return F3Kind.LENGTH_LITERAL;
    }

    @Override
    public <R, D> R accept(F3TreeVisitor<R, D> visitor, D data) {
        return visitor.visitLengthLiteral(this, data);
    }

    @Override
    public LengthUnit getUnits() {
        return units;
    }

    @Override
    public F3Literal getValue() {
        return value;
    }
}
