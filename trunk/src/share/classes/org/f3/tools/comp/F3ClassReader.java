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

package org.f3.tools.comp;

import java.util.IdentityHashMap;
import java.util.Set;
import java.util.HashSet;

import javax.tools.JavaFileObject;
import com.sun.tools.mjavac.jvm.ClassReader;
import com.sun.tools.mjavac.code.*;
import com.sun.tools.mjavac.code.Type.*;
import com.sun.tools.mjavac.code.Symbol.*;
import com.sun.tools.mjavac.util.*;
import com.sun.tools.mjavac.util.List;

import static com.sun.tools.mjavac.code.Flags.*;
import static com.sun.tools.mjavac.code.Kinds.*;
import static com.sun.tools.mjavac.code.TypeTags.*;
import org.f3.tools.code.F3ClassSymbol;
import org.f3.tools.code.F3Symtab;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3Types;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.tree.F3TreeMaker;
import org.f3.tools.util.MsgSym;

import org.f3.tools.main.F3Compiler;

/** Provides operations to read a classfile into an internal
 *  representation. The internal representation is anchored in a
 *  F3ClassSymbol which contains in its scope symbol representations
 *  for all other definitions in the classfile. Top-level Classes themselves
 *  appear as members of the scopes of PackageSymbols.
 *
 *  We delegate actual classfile-reading to javac's ClassReader, and then
 *  translates the resulting ClassSymbol to F3ClassSymbol, doing some
 *  renaming etc to make the resulting Symbols and Types match those produced
 *  by the parser.  This munging is incomplete, and there are still places
 *  where the compiler needs to know if a class comes from the parser or a
 *  classfile; those places will hopefully become fewer.
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class F3ClassReader extends ClassReader {
    protected static final Context.Key<ClassReader> backendClassReaderKey =
         new Context.Key<ClassReader>();

    private final F3Defs defs;
    protected final F3Types f3Types;
    protected final F3TreeMaker f3make;

    /** The raw class-reader, shared by the back-end. */
    public ClassReader jreader;

    private final Name functionClassPrefixName;
    private Context ctx;
    private Messages messages;
    
    public static void preRegister(final Context context, final ClassReader jreader) {
        context.put(backendClassReaderKey, jreader);
        Object instance = context.get(classReaderKey);
        if (instance instanceof F3ClassReader)
            ((F3ClassReader) instance).jreader = jreader;
        else
            preRegister(context);
    }
    public static void preRegister(final Context context) {
        context.put(classReaderKey, new Context.Factory<ClassReader>() {
	       public F3ClassReader make() {
		   F3ClassReader reader = new F3ClassReader(context, true);
                   reader.jreader = context.get(backendClassReaderKey);
                   return reader;
	       }
        });
    }

    public static F3ClassReader instance(Context context) {
        F3ClassReader instance = (F3ClassReader) context.get(classReaderKey);
        if (instance == null)
            instance = new F3ClassReader(context, true);
        return instance;
    }

    /** Construct a new class reader, optionally treated as the
     *  definitive classreader for this invocation.
     */
    protected F3ClassReader(Context context, boolean definitive) {
        super(context, definitive);
        defs = F3Defs.instance(context);
        f3Types = F3Types.instance(context);
        f3make = F3TreeMaker.instance(context);
        functionClassPrefixName = names.fromString(F3Symtab.functionClassPrefix);
        ctx = context;
        messages = Messages.instance(context);
    }

    public Name.Table getNames() {
        return names;
    }
    
    /** Reassign names of classes that might have been loaded with
      * their flat names. */
    void fixupFullname (F3ClassSymbol cSym, ClassSymbol jsymbol) {
        if (cSym.fullname != jsymbol.fullname &&
                cSym.owner.kind == PCK && jsymbol.owner.kind == TYP) {
            cSym.owner.members().remove(cSym);
            cSym.name = jsymbol.name;
            ClassSymbol owner = enterClass(((ClassSymbol) jsymbol.owner).flatname);
            cSym.owner = owner;
            cSym.fullname = ClassSymbol.formFullName(cSym.name, owner);
        }
    }

    public F3ClassSymbol enterClass(ClassSymbol jsymbol) {
        Name className = jsymbol.flatname;
        boolean mixin = className.endsWith(defs.mixinClassSuffixName);
        if (mixin)
            className = className.subName(0, className.len - defs.mixinClassSuffixName.len);
        F3ClassSymbol cSym = (F3ClassSymbol) enterClass(className);
        //cSym.flags_field |= jsymbol.flags_field;
        if (mixin)
            cSym.flags_field |= F3Flags.MIXIN;
        else {
            fixupFullname(cSym, jsymbol);
            cSym.jsymbol = jsymbol;
        }
        return cSym;
    }

    /** Define a new class given its name and owner.
     */
    @Override
    public ClassSymbol defineClass(Name name, Symbol owner) {
        ClassSymbol c = new F3ClassSymbol(0, name, owner);
        if (owner.kind == PCK)
            assert classes.get(c.flatname) == null : c;
        c.completer = this;
        return c;
    }

    /* FIXME: The re-written class-reader doesn't translate annotations yet.
 
    protected void attachAnnotations(final Symbol sym) {
        int numAttributes = nextChar();
        if (numAttributes != 0) {
            ListBuffer<CompoundAnnotationProxy> proxies =
                new ListBuffer<CompoundAnnotationProxy>();
            for (int i = 0; i<numAttributes; i++) {
                CompoundAnnotationProxy proxy = readCompoundAnnotation();
                if (proxy.type.tsym == syms.proprietaryType.tsym)
                    sym.flags_field |= PROPRIETARY;
                else {
                        proxies.append(proxy);
                }
            }
            annotate.later(new F3AnnotationCompleter(sym, proxies.toList(), this));
        }
    }

    static public class F3AnnotationCompleter extends AnnotationCompleter {
        F3ClassReader classReader;
        public F3AnnotationCompleter(Symbol sym, List<CompoundAnnotationProxy> l, ClassReader classReader) {
            super(sym, l, classReader);
            this.classReader = (F3ClassReader)classReader;
        }
        // implement Annotate.Annotator.enterAnnotation()
        public void enterAnnotation() {
            JavaFileObject previousClassFile = classReader.currentClassFile;
            try {
                classReader.currentClassFile = classFile;
                List<Attribute.Compound> newList = deproxyCompoundList(l);
                F3Symtab f3Syms = (F3Symtab)classReader.syms;
                for (Attribute.Compound comp : newList) {
                }

                sym.attributes_field = ((sym.attributes_field == null)
                                        ? newList
                                        : newList.prependList(sym.attributes_field));
            } finally {
                classReader.currentClassFile = previousClassFile;
            }
        }
    }
    */

    /** Map javac Type/Symbol to f3 Type/Symbol. */
    IdentityHashMap<Object,Object> typeMap = new IdentityHashMap<Object,Object>();
    
    /** Translate a List of raw JVM types to F3 types. */
    List<Type> translateTypes (List<Type> types) {
        if (types == null)
            return null;
        List<Type> ts = (List<Type>) typeMap.get(types);
        if (ts != null)
            return ts;
        ListBuffer<Type> rs = new ListBuffer<Type>();
        for (List<Type> t = types;
                 t.tail != null;
                 t = t.tail)
            rs.append(translateType(t.head));
        ts = rs.toList();
        typeMap.put(types, ts);
        return ts;
    }

    Type asMethodType(Type type) {
	if (type instanceof ClassType) {
	    TypeSymbol tsym = type.tsym;
	    final ClassType ctype = (ClassType) type;
	    Name flatname = ((ClassSymbol) tsym).flatname;
	    if (flatname.startsWith(functionClassPrefixName)
		&& flatname != functionClassPrefixName) {
		return translateType(type);
	    }
	}
	if (type instanceof MethodType) {
	    return type.asMethodType();
	}
	return null;
    }

    /** Translate raw JVM type to F3 type. */
    Type translateType (Type type) {
        if (type == null)
            return null;
        Type t = (Type) typeMap.get(type);
        if (t != null)
            return t;
        switch (type.tag) {
            case VOID:
                t = syms.voidType;
                break;
            case BOOLEAN:
                t = syms.booleanType;
                break;
            case CHAR:
                t = syms.charType;
                break;
            case BYTE:
                t = syms.byteType;
                break;
            case SHORT:
                t = syms.shortType;
                break;
            case INT:
                t = syms.intType;
                break;
            case LONG:
                t = syms.longType;
                break;
            case DOUBLE:
                t = syms.doubleType;
                break;
            case FLOAT:
                t = syms.floatType;
                break;
            case TYPEVAR:
                TypeVar tv = (TypeVar) type;
                TypeVar tx = new TypeVar(null, (Type) null, (Type) null);
                typeMap.put(type, tx); // In case of a cycle.
                tx.bound = translateType(tv.bound);
                tx.lower = translateType(tv.lower);
                tx.tsym = new TypeSymbol(0, tv.tsym.name, tx, translateSymbol(tv.tsym.owner));
                return tx;
            case FORALL:
                ForAll tf = (ForAll) type;
                t = new ForAll(translateTypes(tf.tvars), translateType(tf.qtype));
                break;
            case WILDCARD:
                WildcardType wt = (WildcardType) type;
                t = new WildcardType(translateType(wt.type), wt.kind,
                        translateTypeSymbol(wt.tsym));
		//System.err.println("wt="+t);
		//System.err.println("t="+t);
                break;
            case CLASS:
                TypeSymbol tsym = type.tsym;
                if (tsym instanceof ClassSymbol) {
                    final ClassType ctype = (ClassType) type;
                    Name flatname = ((ClassSymbol) tsym).flatname;
		    //System.err.println("flatname="+flatname);
                    if (flatname.startsWith(functionClassPrefixName)
                        && flatname != functionClassPrefixName) {
                        t = ((F3Symtab) syms).makeFunctionType(translateTypes(ctype.typarams_field));
			//System.err.println("t="+t);
                        break;
                    }
                    if (tsym.name.endsWith(defs.mixinClassSuffixName)) {
                        t = enterClass((ClassSymbol) tsym).type;
                        break;
                    }
                    if (ctype.isCompound()) {
                        t = types.makeCompoundType(translateTypes(ctype.interfaces_field), translateType(ctype.supertype_field));
                        break;
                    }
                    TypeSymbol sym = translateTypeSymbol(tsym);
                    ClassType ntype;
                    if (tsym.type == type)
                        ntype = (ClassType) sym.type;
                    else
                        ntype = new ClassType(Type.noType, List.<Type>nil(), sym) {
                            boolean completed = false;
                            @Override
                            public Type getEnclosingType() {
                                if (!completed) {
                                    completed = true;
                                    tsym.complete();
                                    super.setEnclosingType(translateType(ctype.getEnclosingType()));
                                }
                                return super.getEnclosingType();
                            }
                            @Override
                            public void setEnclosingType(Type outer) {
                                throw new UnsupportedOperationException();
                            }
                            @Override
                            public boolean equals(Object t) {
                                return super.equals(t);
                            }

                            @Override
                            public int hashCode() {
                                return super.hashCode();
                            }
                        };
                    typeMap.put(type, ntype); // In case of a cycle.
                    ntype.typarams_field = translateTypes(type.getTypeArguments());
                    return ntype;
                }
                break;
            case ARRAY:
                t = new ArrayType(translateType(((ArrayType) type).elemtype), syms.arrayClass);
                break;
            case METHOD:
                t = new MethodType(translateTypes(type.getParameterTypes()),
                        translateType(type.getReturnType()),
                        translateTypes(type.getThrownTypes()),
                        syms.methodClass);
                break;
            default:
                t = type; // FIXME
        }
        typeMap.put(type, t);
        return t;
    }

    TypeSymbol translateTypeSymbol(TypeSymbol tsym) {
        if (tsym == syms.predefClass)
            return tsym;
	if (tsym instanceof ClassSymbol) {
	    ClassSymbol csym = (ClassSymbol) tsym; // FIXME
	    return enterClass(csym);
	}
	return tsym;
    }
    
    Symbol translateSymbol(Symbol sym) {
        if (sym == null)
            return null;
        Symbol s = (Symbol) typeMap.get(sym);
        if (s != null)
            return s;
        if (sym instanceof PackageSymbol)
            s = enterPackage(((PackageSymbol) sym).fullname);
        else if (sym instanceof MethodSymbol) {
            Symbol owner = translateSymbol(sym.owner);
            MethodSymbol m = translateMethodSymbol(sym.flags_field, sym, owner);
	    try {
		((ClassSymbol) owner).members_field.enter(m);
	    } catch (NullPointerException exc) {
		System.err.println("owner="+owner);
		System.err.println("m="+m);
		throw exc;
	    }
            s = m;
        }
        else
            s = translateTypeSymbol((TypeSymbol) sym);
        typeMap.put(sym, s);
        return s;
    }
    
    void popMethodTypeArg(MethodType type) {
        List<Type> argtypes = type.argtypes;
        ListBuffer<Type> lb = ListBuffer.<Type>lb();
        
        for (int i = 1; i < argtypes.size(); i++) {
            lb.append(argtypes.get(i));
        }
        
        type.argtypes = lb.toList();
    }
    
    MethodSymbol translateMethodSymbol(long flags, Symbol sym, Symbol owner) {
        Name name = sym.name;
        Type mtype = sym.type;
        String nameString = name.toString();
        
        int boundStringIndex = nameString.indexOf(F3Defs.boundFunctionDollarSuffix);
        if (boundStringIndex != -1) {
            // this is a bound function
            // remove the bound suffix, and mark as bound
            nameString = nameString.substring(0, boundStringIndex);
            flags |= F3Flags.BOUND;
        }
        F3Symtab f3Syms = (F3Symtab) this.syms;

        for (Attribute.Compound ann : sym.getAnnotationMirrors()) {
            if (ann.type.tsym.flatName() == f3Syms.f3_signatureAnnotationType.tsym.flatName()) {
                String sig = (String)ann.values.head.snd.getValue();
                signatureBuffer = new byte[sig.length()];
                try {
                    mtype = sigToType(names.fromString(sig));
                }
                catch (Exception e) {
		    System.err.println("sig="+sig);
		    e.printStackTrace();
                    throw new AssertionError("Bad F3 signature");
                }
            }
        }
        Type type = translateType(mtype);
        if (type instanceof MethodType) {
            boolean convertToStatic = false;
            
            if (nameString.endsWith(F3Defs.implFunctionSuffix)) {
                nameString = nameString.substring(0, nameString.length() - F3Defs.implFunctionSuffix.length());
                convertToStatic = true;
            }
            
            if (convertToStatic) {
                flags &= ~Flags.STATIC;
                popMethodTypeArg((MethodType)type);
            }
        }
    
        name = names.fromString(nameString);
        
        return new MethodSymbol(flags, name, type, owner);
    }

    // VSGC-2849 - Mixins: Change the mixin interface from $Intf to $Mixin.
    private void checkForIntfSymbol(Symbol sym) throws CompletionFailure {        
        if (sym.name.endsWith(defs.deprecatedInterfaceSuffixName)) {
            String fileString = ((ClassSymbol) sym).classfile.getName();
            String message = messages.getLocalizedString(MsgSym.MESSAGEPREFIX_COMPILER_MISC +
                                                            MsgSym.MESSAGE_DEPRECATED_INTERFACE_CLASS,
                                                         fileString);
            log.rawError(Position.NOPOS, message);
            throw new CompletionFailure(sym, message);
        }
    }

    @Override
    public void complete(Symbol sym) throws CompletionFailure {
        checkForIntfSymbol(sym);
        if (jreader.sourceCompleter == null)
           jreader.sourceCompleter = F3Compiler.instance(ctx);
        if (sym instanceof PackageSymbol) {
            PackageSymbol psym = (PackageSymbol) sym;
            PackageSymbol jpackage;
            if (psym == syms.unnamedPackage)
                jpackage = jreader.syms.unnamedPackage;
            else
                jpackage = jreader.enterPackage(psym.fullname);
            jpackage.complete();
            if (psym.members_field == null) psym.members_field = new Scope(psym);
            for (Scope.Entry e = jpackage.members_field.elems;
                 e != null;  e = e.sibling) {
                 if (e.sym instanceof ClassSymbol) {
                     ClassSymbol jsym = (ClassSymbol) e.sym;
                     if (jsym.name.endsWith(defs.mixinClassSuffixName))
                         continue;
                     F3ClassSymbol csym = enterClass(jsym);
                     psym.members_field.enter(csym);
                     csym.classfile = jsym.classfile;
                     csym.jsymbol = jsym;
                 }
            }
            if (jpackage.exists())
                psym.flags_field |= EXISTS;
        } else {
            sym.owner.complete();
            F3ClassSymbol csym = (F3ClassSymbol) sym;

            ClassSymbol jsymbol = csym.jsymbol;
            if (jsymbol != null && jsymbol.classfile != null && 
                jsymbol.classfile.getKind() == JavaFileObject.Kind.SOURCE &&
                jsymbol.classfile.getName().endsWith(".f3")) {
                SourceCompleter f3SourceCompleter = F3Compiler.instance(ctx);
                f3SourceCompleter.complete(csym);
                return;
            } else { 
                csym.jsymbol = jsymbol = jreader.loadClass(csym.flatname);
            }
            fixupFullname(csym, jsymbol);
            typeMap.put(jsymbol, csym);
            jsymbol.classfile = ((ClassSymbol) sym).classfile;
            
            ClassType ct = (ClassType)csym.type;
            ClassType jt = (ClassType)jsymbol.type;
            csym.members_field = new Scope(csym);

            // flags are derived from flag bits and access modifier annoations
            csym.flags_field = flagsFromAnnotationsAndFlags(jsymbol);
            
            ct.typarams_field = translateTypes(jt.typarams_field);
            ct.setEnclosingType(translateType(jt.getEnclosingType()));
            
            ct.supertype_field = translateType(jt.supertype_field);
            
            // VSGC-2841 - Mixins: Cannot find firePropertyChange method in SwingComboBox.f3
            if (ct.supertype_field != null && 
                ct.supertype_field.tsym != null &&
                ct.supertype_field.tsym.kind == TYP) {
                
            }

            
            ListBuffer<Type> interfaces = new ListBuffer<Type>();
            Type iface = null;
            if (jt.interfaces_field != null) { // true for ErrorType
                for (List<Type> it = jt.interfaces_field;
                     it.tail != null;
                     it = it.tail) {
                    Type itype = it.head;
                    checkForIntfSymbol(itype.tsym);
                    if (((ClassSymbol) itype.tsym).flatname == defs.cObjectName) {
                        csym.flags_field |= F3Flags.F3_CLASS;
                    } else if (((ClassSymbol) itype.tsym).flatname == defs.cMixinName) {
                        csym.flags_field |= F3Flags.MIXIN | F3Flags.F3_CLASS;
                    } else if ((csym.fullname.len + defs.mixinClassSuffixName.len ==
                             ((ClassSymbol) itype.tsym).fullname.len) &&
                            ((ClassSymbol) itype.tsym).fullname.startsWith(csym.fullname) &&
                            itype.tsym.name.endsWith(defs.mixinClassSuffixName)) {
                        iface = itype;
                        iface.tsym.complete();
                        csym.flags_field |= F3Flags.MIXIN | F3Flags.F3_CLASS;
                    } else {
                        itype = translateType(itype);
                        interfaces.append(itype);
                    }
                }
            }
           
            if (iface != null) {
                for (List<Type> it = ((ClassType) iface.tsym.type).interfaces_field;
                 it.tail != null;
                 it = it.tail) {
                    Type itype = it.head;
                    checkForIntfSymbol(itype.tsym);
                    if (((ClassSymbol) itype.tsym).flatname == defs.cObjectName) {
                        csym.flags_field |= F3Flags.F3_CLASS;
                    } else if (((ClassSymbol) itype.tsym).flatname == defs.cMixinName) {
                        csym.flags_field |= F3Flags.MIXIN | F3Flags.F3_CLASS;
                    } else {
                        itype = translateType(itype);
                        interfaces.append(itype);
                    }
                }
            }
            ct.interfaces_field = interfaces.toList();

            // Now translate the members.
            // Do an initial "reverse" pass so we copy the order.
            List<Symbol> symlist = List.nil();
            for (Scope.Entry e = jsymbol.members_field.elems;
                 e != null;  e = e.sibling) {
                if ((e.sym.flags_field & SYNTHETIC) != 0)
                    continue;
                symlist = symlist.prepend(e.sym);
            }
            boolean isF3Class = (csym.flags_field & F3Flags.F3_CLASS) != 0;
            boolean isMixinClass = (csym.flags_field & F3Flags.MIXIN) != 0;
            
            F3VarSymbol scriptAccessSymbol = isF3Class ? f3make.ScriptAccessSymbol(csym) : null;

            Set<Name> priorNames = new HashSet<Name>();
	    enterTypevars(ct);
            handleSyms:
            for (List<Symbol> l = symlist; l.nonEmpty(); l=l.tail) {
                Symbol memsym = l.head;
                Name name = memsym.name;
		
                long flags = 0;
		try {
		    flags = flagsFromAnnotationsAndFlags(memsym);
		} catch (Exception exc) {
		    exc.printStackTrace();
		    continue;
		}
                if ((flags & PRIVATE) != 0)
                    continue;
                boolean sawSourceNameAnnotation = false;
                F3Symtab f3Syms = (F3Symtab) this.syms;

                for (Attribute.Compound a : memsym.getAnnotationMirrors()) {
                    if (a.type.tsym.flatName() == f3Syms.f3_staticAnnotationType.tsym.flatName()) {
                        flags |=  Flags.STATIC;
                    } else if (a.type.tsym.flatName() == f3Syms.f3_defAnnotationType.tsym.flatName()) {
                        flags |=  F3Flags.IS_DEF;
                    } else if (a.type.tsym.flatName() == f3Syms.f3_defaultAnnotationType.tsym.flatName()) {
                        flags |=  F3Flags.DEFAULT;
                    } else if (a.type.tsym.flatName() == f3Syms.f3_publicInitAnnotationType.tsym.flatName()) {
                        flags |=  F3Flags.PUBLIC_INIT;
                    } else if (a.type.tsym.flatName() == f3Syms.f3_publicReadAnnotationType.tsym.flatName()) {
                        flags |=  F3Flags.PUBLIC_READ;
                    } else if (a.type.tsym.flatName() == f3Syms.f3_inheritedAnnotationType.tsym.flatName()) {
                        continue handleSyms;
                    } else if (a.type.tsym.flatName() == f3Syms.f3_sourceNameAnnotationType.tsym.flatName()) {
                        Attribute aa = a.member(name.table.value);
                        Object sourceName = aa.getValue();
                        if (sourceName instanceof String) {
                            name = names.fromString((String) sourceName);
                            sawSourceNameAnnotation = true;
                        }
                    }
                }
                if (memsym instanceof MethodSymbol) {
                    MethodSymbol m = translateMethodSymbol(flags, memsym, csym);     
                    csym.members_field.enter(m);

                }
                else if (memsym instanceof VarSymbol) {
                    // Eliminate any duplicate value/location.
                    if (priorNames.contains(name))
                        continue;
                    Type otype = memsym.type;
                    Type type = translateType(otype);
                    F3VarSymbol v;
                    if (scriptAccessSymbol != null && name == scriptAccessSymbol.name) {
                        v = scriptAccessSymbol;
                    } else {
                        v = new F3VarSymbol(f3Types, names, flags, name, type, csym);
                        csym.addVar(v, (flags & STATIC) != 0);
                    }
                    csym.members_field.enter(v);
                    if ((flags & F3Flags.DEFAULT) != 0) {
                        csym.setDefaultVar(name);
                    }
                    priorNames.add(name);
                }
                else {
                    memsym.flags_field = flags;
                    csym.members_field.enter(translateSymbol(memsym));
                }
            }
        }
    }
    
    private long flagsFromAnnotationsAndFlags(Symbol sym) {
        long initialFlags = sym.flags_field;
        long nonAccessFlags = initialFlags & ~F3Flags.F3AccessFlags;
        long accessFlags = initialFlags & F3Flags.F3AccessFlags;
        F3Symtab f3Syms = (F3Symtab) this.syms;
        for (Attribute.Compound a : sym.getAnnotationMirrors()) {
            if (a.type.tsym.flatName() == f3Syms.f3_protectedAnnotationType.tsym.flatName()) {
                accessFlags = Flags.PROTECTED;
            } else if (a.type.tsym.flatName() == f3Syms.f3_packageAnnotationType.tsym.flatName()) {
                accessFlags = 0L;
            } else if (a.type.tsym.flatName() == f3Syms.f3_publicAnnotationType.tsym.flatName()) {
                accessFlags = Flags.PUBLIC;
            } else if (a.type.tsym.flatName() == f3Syms.f3_scriptPrivateAnnotationType.tsym.flatName()) {
                accessFlags = F3Flags.SCRIPT_PRIVATE;
            }
        }
        if (accessFlags == 0L) {
            accessFlags = F3Flags.PACKAGE_ACCESS;
        }
        return nonAccessFlags | accessFlags;
    }
}
