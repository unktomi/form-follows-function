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

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.f3.tools.antlr.v3Parser;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * F3Parser
 *
 * @author Brian Goetz
 */
public class F3Parser implements PsiParser {
    private final F3ParsingLexer lexer;

    public F3Parser(F3ParsingLexer lexer) {
        this.lexer = lexer;
    }

    @NotNull
    public ASTNode parse(IElementType rootElement, PsiBuilder psiBuilder) {
        List<StreamAction> actions = new ArrayList<StreamAction>();
        final List<ParseError> errors = new ArrayList<ParseError>();

        System.out.printf("%s/%s: starting parsing %d tokens, %d chars %n", Thread.currentThread(), lexer, lexer.getSize(), lexer.getBufferEnd());
        // Potentially inefficient; creating a new ANTLR lexer instead of reusing the one we have
        WrappedAntlrLexer antlrLexer = new WrappedAntlrLexer(new ANTLRStringStream(lexer.getBufferSequence().toString().substring(0, lexer.getBufferEnd())), false, false);
        v3Parser parser = new v3Parser(new CommonTokenStream(antlrLexer)) {
            protected String getParserName() {
                return "org.f3.tools.antlr.v3Parser";
            }

            public void displayRecognitionError(String[] strings, RecognitionException e) {
                errors.add(new ParseError(e, getErrorMessage(e, strings)));
            }
        };
        try {
            v3Parser.script_return antlrParseTree = parser.script();
            scrubTree((CommonTree) antlrParseTree.getTree());
            System.out.printf("finished parsing %d:%d%n", ((CommonTree) antlrParseTree.getTree()).getTokenStartIndex(), ((CommonTree) antlrParseTree.getTree()).getTokenStopIndex());
            BeginMark beginMark = new BeginMark(0);
            actions.add(beginMark);
            if (errors.isEmpty()) {
                // @@@ Do the same with v3Walker, so we can get real structural information
                traverse((CommonTree) antlrParseTree.getTree(), actions);
            } else {
                traverse((CommonTree) antlrParseTree.getTree(), actions);
                for (ParseError error : errors) {
                    int position = error.exception.token.getTokenIndex();
                    int index = findInsertPosition(actions, position);
                    BeginMark beginErrorMark = new BeginMark(position);
                    actions.add(index, beginErrorMark);
                    actions.add(index+1, new ErrorMark(position + 1, beginErrorMark, error.errorString));
                }
            }
            actions.add(new EndMark(lexer.getSize(), beginMark, rootElement));
            applyActions(actions, psiBuilder);
            return psiBuilder.getTreeBuilt();
        } catch (RecognitionException e) {
            throw new RuntimeException("Unexpected exception in parsing", e);
        }
    }

    private int findInsertPosition(List<StreamAction> actions, int tokenPosition) {
        for (int i=0; i<actions.size(); i++) {
            StreamAction a = actions.get(i);
            if (a.position > tokenPosition)
                return i;
        }
        return actions.size();
    }

    private void scrubTree(Tree tree) {
        // The top-level tree often has wrong position info, so start one down from the top
        for (int i = tree.getChildCount() - 1; i >= 0; i--) {
            Tree child = tree.getChild(i);
            if (child.getTokenStartIndex() > child.getTokenStopIndex()) {
                System.out.println("Deleting node " + child);
                tree.deleteChild(i);
                continue;
            }
            if (child.getTokenStartIndex() < 0) {
//                System.out.println("Deleting node " + child);
//                tree.deleteChild(i);
                continue;
            }
            scrubTree(child);
            if (child.getTokenStartIndex() < tree.getTokenStartIndex()) {
                tree.setTokenStartIndex(child.getTokenStartIndex());
            }
            if (child.getTokenStopIndex() > tree.getTokenStopIndex()) {
                tree.setTokenStopIndex(child.getTokenStopIndex());
            }
        }
    }

    private void traverse(CommonTree tree, List<StreamAction> actions) {
        // System.out.printf("Token %s at %d:%d%n", tree.getToken(), tree.getTokenStartIndex(), tree.getTokenStopIndex());
        if (tree.getTokenStartIndex() > tree.getTokenStopIndex())
            return;
        if (tree.getTokenStartIndex() < 0)
            return;
        BeginMark beginMark = new BeginMark(tree.getTokenStartIndex());
        actions.add(beginMark);
        for (int i = 0; i < tree.getChildCount(); i++)
            traverse((CommonTree) tree.getChild(i), actions);
        actions.add(new EndMark(tree.getTokenStopIndex() + 1, beginMark, F3AstNodes.GENERIC_NODE.elementType));
    }


    private void applyActions(List<StreamAction> actions, PsiBuilder builder) {
        for (StreamAction action : actions) {
            while (lexer.getIndex() < action.position && !builder.eof()) {
                builder.getTokenType();
                builder.advanceLexer();
            }
            action.action(builder);
        }
        while (!builder.eof()) {
            builder.getTokenType();
            builder.advanceLexer();
        }
    }

    private static class ParseError {
        public final RecognitionException exception;
        public final String errorString;

        private ParseError(RecognitionException exception, String errorString) {
            this.exception = exception;
            this.errorString = errorString;
        }
    }

    private abstract static class StreamAction implements Comparable<StreamAction> {
        protected final int position;

        protected StreamAction(int pos) {
            position = pos;
        }

        public abstract void action(PsiBuilder builder);

        public int compareTo(StreamAction other) {
            if (position < other.position)
                return -1;
            else if (position > other.position)
                return 1;
            else
                return 0;
        }

		public int hashCode()
		{
			return position;
		}

		public boolean equals(Object obj)
		{
			return (obj instanceof StreamAction) && ((StreamAction)obj).position == position;
		}
	}

    private static class BeginMark extends StreamAction {
        public PsiBuilder.Marker marker;

        BeginMark(int position) {
            super(position);
        }

        public void action(PsiBuilder builder) {
            marker = builder.mark();
        }
    }

    private static class EndMark extends StreamAction {
        private final BeginMark marker;
        private final IElementType elementType;

        EndMark(int position, BeginMark marker, IElementType elementType) {
            super(position);
            this.marker = marker;
            this.elementType = elementType;
        }

        public void action(PsiBuilder builder) {
            marker.marker.done(elementType);
        }
    }

    private static class ErrorMark extends StreamAction {
        private final BeginMark marker;
        private final String errorString;

        ErrorMark(int position, BeginMark marker, String errorString) {
            super(position);
            this.marker = marker;
            this.errorString = errorString;
        }

        public void action(PsiBuilder builder) {
            marker.marker.error(errorString);
        }
    }
}
