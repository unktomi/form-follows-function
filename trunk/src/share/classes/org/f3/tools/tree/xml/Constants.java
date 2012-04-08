/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.tools.tree.xml;

import org.f3.api.tree.TypeTree.Cardinality;
import org.f3.api.F3BindStatus;
import java.util.HashMap;
import java.util.Map;

/**
 * Element, attribute names and values, namespace URI used in XML 
 * representations of ASTs.
 *
 * @author A. Sundararajan
 */
public final class Constants {

    // Don't create me!
    private Constants() {
    }

    // XML namespace URI used for AST XML elements
    public static final String F3ASTXML_NS = "http://f3.org";
    public static final String F3ASTXML_PREFIX = "f3:";

    // doc comments
    public static final String DOC_COMMENT = "doc-comment";

    // script file
    public static final String F3 = "f3";
    public static final String FILE = "file";

    // package and import
    public static final String PACKAGE = "package";
    public static final String IMPORT = "import";

    // statements
    public static final String STATEMENT = "stmt";
    public static final String STATEMENTS = "stmts";
    public static final String EMPTY = "empty";
    public static final String BREAK = "break";
    public static final String CONTINUE = "continue";
    public static final String TRY = "try";
    public static final String CATCH = "catch";
    public static final String CATCHES = "catches";
    public static final String FINALLY = "finally";
    public static final String THROW = "throw";
    public static final String RETURN = "return";

    // literals
    public static final String INT_LITERAL = "int-literal";
    public static final String LONG_LITERAL = "long-literal";
    public static final String FLOAT_LITERAL = "float-literal";
    public static final String DOUBLE_LITERAL = "double-literal";
    public static final String STRING_LITERAL = "string-literal";
    public static final String TIME_LITERAL = "time-literal";
    public static final String LENGTH_LITERAL = "length-literal";
    public static final String ANGLE_LITERAL = "angle-literal";
    public static final String COLOR_LITERAL = "color-literal";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String VALUE = "value";
    public static final String NULL = "null";

    // types
    public static final String TYPE = "type";
    public static final String TYPE_CLASS = "type-class";
    public static final String TYPE_FUNCTIONAL = "type-functional";
    public static final String TYPE_ARRAY = "type-array";
    public static final String TYPE_UNKNOWN = "type-unknown";
    public static final String TYPE_ANY = "type-any";
    public static final String CARDINALITY = "cardinality";

    // expressions
    public static final String PARENTHESIS = "paren";
    public static final String ASSIGNMENT = "assignment";
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String CAST = "cast";
    public static final String INSTANCEOF = "instanceof";
    public static final String INDEX = "index";
    public static final String SELECT = "select";
    public static final String MEMBER = "member";
    public static final String INVOKE = "invoke";
    public static final String METHOD = "method";
    public static final String ARGUMENTS = "args";
    public static final String INDEXOF = "indexof";
    public static final String STRING_EXPRESSION = "string-expr";
    public static final String FORMAT = "format";
    public static final String PART = "part";
    public static final String STR_TRANS_KEY = "translation-key";
    public static final String NEW = "new";
    public static final String OBJECT_LITERAL = "object-literal";
    public static final String OBJECT_LITERAL_INIT = "object-literal-init";
    public static final String BIND_STATUS = "bind-status";
    public static final String SEQUENCE_INDEXED = "seq-indexed";
    public static final String SEQUENCE_SLICE = "seq-slice";
    public static final String SLICE_END_KIND = "slice-end-kind";
    public static final String END_EXCLUSIVE = "exclusive";
    public static final String SEQUENCE_INSERT = "seq-insert";
    public static final String SEQUENCE_DELETE = "seq-delete";
    public static final String ELEMENT = "elem";
    public static final String IF = "if";
    public static final String TEST = "test";
    public static final String THEN = "then";
    public static final String ELSE = "else";
    public static final String FOR = "for";
    public static final String WHERE = "where";
    public static final String IN = "in";
    public static final String WHILE = "while";
    public static final String MISSING_EXPRESSION = "missing-expr";
    public static final String INVALIDATE = "invalidate";

    // interpolation, keyframes
    public static final String KEYFRAME_LITERAL = "keyframe-literal";
    public static final String INTERPOLATION_VALUES = "interpolation-values";
    public static final String START_DURATION = "start-dur";
    public static final String INTERPOLATE = "interpolate";
    public static final String TRIGGER = "trigger";
    public static final String INTERPOLATE_VALUE = "interpolate-value";
    public static final String ATTRIBUTE = "attribute";
    public static final String INTERPOLATION = "interpolation";

    // declarations and misc.
    public static final String NAME = "name";
    public static final String EXTENDS = "extends";
    public static final String MEMBERS = "members";
    public static final String PARAMETERS = "params";
    public static final String RETURN_TYPE = "return-type";
    public static final String INITIAL_VALUE = "init-value";
    public static final String EXPRESSION = "expr";
    public static final String LABEL = "label";
    public static final String CLASS = "class";
    public static final String IDENTIFIER = "ident";
    public static final String KIND = "kind";
    public static final String MODIFIERS = "modifiers";
    public static final String ERROR = "error";
    public static final String OTHER = "other";
    public static final String DEFINITIONS = "defs";
    public static final String ANON_FUNCTION = "anon-function";
    public static final String INIT = "init";
    public static final String POSTINIT = "postinit";
    public static final String OVERRIDE_VAR = "override-var";
    public static final String VAR = "var";
    public static final String DEF = "def";
    public static final String OLD_VALUE = "old-value";
    public static final String ON_REPLACE = "on-replace";
    public static final String ON_INVALIDATE = "on-invalidate";
    public static final String FIRST_INDEX = "first-index";
    public static final String LAST_INDEX = "last-index";
    public static final String NEW_ELEMENTS = "new-elements";
    public static final String BLOCK_EXPRESSION = "block";
    public static final String BOUND = "bound";
    public static final String FUNCTION = "function";
    public static final String SEQUENCE_EMPTY = "seq-empty";
    public static final String SEQUENCE_RANGE = "seq-range";
    public static final String SEQUENCE_EXPLICIT = "seq-explicit";
    public static final String LOWER = "lower";
    public static final String UPPER = "upper";
    public static final String STEP = "step";
    public static final String EXCLUSIVE = "exclusive";
    public static final String SEQUENCE = "seq";
    public static final String ITEMS = "items";
    public static final String FIRST = "first";
    public static final String LAST = "last";
    public static final String BODY = "body";
    public static final String SIZEOF = "sizeof";
    public static final String FILE_EXT = ".f3";
    public static final String FLAGS = "flags";
    public static final String LIST_ITEM = "li";
    public static final String POSITION = "pos";
    public static final String END_POSITION = "end-pos";

    // symbols and types. symbols and types are "networks" as
    // opposed to trees. So, we generate "id" and idref pattern to
    // refer (possibly mutually) between symbols and types.
    public static final String SYMBOLS = "symbols";
    public static final String SYMBOL = "symbol";
    public static final String OWNER = "owner";
    public static final String FULL_NAME = "full-name";
    public static final String TYPES = "types";
    public static final String ID = "id";
    public static final String SYMREF = "symref";
    public static final String TYPEREF = "typeref";
    public static final String SYMID_PREFIX = "sym-";
    public static final String TYPEID_PREFIX = "type-";

    // attribute types used
    public static final String ATTR_ID = "ID";
    public static final String ATTR_IDREF = "IDREF";
    public static final String ATTR_CDATA = "CDATA";
    
    // Java access flags applicable to F3
    public static final String ABSTRACT = "abstract";
    public static final String PROTECTED = "protected";
    public static final String PUBLIC = "public";
    
    // F3 specific access flags
    public static final String DEFAULT = "default";
    public static final String PUBLIC_INIT = "public-init";
    public static final String PUBLIC_READ = "public-read";
    public static final String PACKAGE_ACCESS = "package";
    public static final String SCRIPT_PRIVATE = "script-private";
    public static final String OVERRIDE = "override";
    public static final String MIXIN = "mixin";
    
    // enumeration constants are written with XML naming
    // conventions. i.e., replacing '_' with '-'
    public static String enumToName(Enum e) {
        return e.name().toLowerCase().replace('_', '-');
    }

    public static <T extends Enum<T>> T nameToEnum(Class<T> type, String str) {
        return Enum.valueOf(type, str.toUpperCase().replace('-', '_'));
    }

    public static String cardinalityToString(Cardinality c) {
        return (c == null)? null : enumToName(c);
    }

    private static Map<F3BindStatus, String> bindStatus2String = new HashMap<F3BindStatus, String>();
    static {
        bindStatus2String.put(F3BindStatus.UNBOUND, "unbound");
        bindStatus2String.put(F3BindStatus.UNIDIBIND, "bind");
        bindStatus2String.put(F3BindStatus.BIDIBIND, "bind-with-inverse");
    }

    public static String bindStatusToString(F3BindStatus bs) {
        return (bs == null)? null : bindStatus2String.get(bs);
    }
}
