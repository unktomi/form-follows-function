/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package org.f3.ideaplugin.parsing;

import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import org.f3.ideaplugin.F3Language;
import org.f3.ideaplugin.F3File;
import org.jetbrains.annotations.NotNull;

/**
 * F3ParserDefinition
 *
 * @author Brian Goetz
 */
public class F3ParserDefinition implements ParserDefinition {

    private final ThreadLocal<F3ParsingLexer> lexerHolder = new ThreadLocal<F3ParsingLexer>();
    private final IFileElementType fileElementType = new IFileElementType(Language.findInstance(F3Language.class));

    @NotNull
    public Lexer createLexer(Project project) {
        assert(lexerHolder.get() == null);
        F3ParsingLexer lex = new F3ParsingLexer();
        lexerHolder.set(lex);
        return lex;
    }

    public PsiParser createParser(Project project) {
        F3ParsingLexer lexer = lexerHolder.get();
        assert(lexer != null);
        lexerHolder.set(null);
        return new F3Parser(lexer);
    }

    public IFileElementType getFileNodeType() {
        return fileElementType;
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return F3Tokens.WHITESPACE;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return F3Tokens.COMMENTS;
    }

    public PsiFile createFile(FileViewProvider fileViewProvider) {
        return new F3File(fileViewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode astNode, ASTNode astNode1) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode astNode) {
        return new ASTWrapperPsiElement(astNode);
    }
}
