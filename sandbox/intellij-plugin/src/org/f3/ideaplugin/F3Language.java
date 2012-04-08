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

package org.f3.ideaplugin;

import com.intellij.lang.*;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.tree.IElementType;
import org.f3.ideaplugin.parsing.F3Annotator;
import org.f3.ideaplugin.parsing.F3Highlighter;
import org.f3.ideaplugin.parsing.F3ParserDefinition;
import org.f3.ideaplugin.parsing.F3Tokens;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * F3Language
 *
 * @author Brian Goetz
 */
public class F3Language extends Language {

    private final F3ParserDefinition definition = new F3ParserDefinition();
    private PairedBraceMatcher braceMatcher;

    public F3Language() {
        super(F3Plugin.F3_LANGUAGE_NAME, "text/plain");
    }

    @NotNull
    public SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
        return new F3Highlighter();
    }

    @Nullable
    public ParserDefinition getParserDefinition() {
        return definition;
    }

    @Nullable
    public ExternalAnnotator getExternalAnnotator () {
        return new F3Annotator ();
    }

    @Nullable
    public PairedBraceMatcher getPairedBraceMatcher() {
        if (braceMatcher == null)
            braceMatcher = new PairedBraceMatcher() {
                private final BracePair[] PAIRS = new BracePair[]{
                        new BracePair('(', F3Tokens.LPAREN.elementType, ')', F3Tokens.RPAREN.elementType, false),
                        new BracePair('[', F3Tokens.LBRACKET.elementType, ']', F3Tokens.RBRACKET.elementType, false),
                        new BracePair('{', F3Tokens.LBRACE.elementType, '}', F3Tokens.RBRACE.elementType, true)
                };

                public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType iElementType, @Nullable IElementType iElementType1) {
                    return true;
                }

                public BracePair[] getPairs() {
                    return PAIRS;
                }
            };
        return braceMatcher;
    }

    @Nullable
    public Commenter getCommenter() {
        return COMMENTER;
    }

    @Nullable
    public FoldingBuilder getFoldingBuilder () {
        return new FoldingBuilder () {
            public FoldingDescriptor[] buildFoldRegions (ASTNode node, Document document) {
                // TODO
                printAST (node, 0);
                return new FoldingDescriptor[0];
            }

            public String getPlaceholderText (ASTNode node) {
                return "A Node"; // TODO
            }
            public boolean isCollapsedByDefault (ASTNode node) {
                return true; // TODO
            }
        };
    }

    private static final Commenter COMMENTER = new Commenter() {
        public String getLineCommentPrefix() {
            return "//";
        }

        public boolean isLineCommentPrefixOnZeroColumn() {
            return false;
        }

        public String getBlockCommentPrefix() {
            return "/*";
        }

        public String getBlockCommentSuffix() {
            return "*/";
        }
    };

    private static void printAST (ASTNode node, int level) {
        for (int i = 0; i < level; i ++)
            System.out.print ("    ");
        System.out.print (node.getElementType () + ": ");
        try {
            System.out.println (node.getText ().substring (0, 20).replaceAll ("\n", "<BR>"));
        } catch (Exception e) {
            System.out.println ("ERROR_WHILE_GETTING_NODE_TEXT");
        }
        ASTNode child = node.getFirstChildNode ();
        level ++;
        while (child != null) {
            printAST (child, level);
            child = child.getTreeNext ();
        }
    }

}
