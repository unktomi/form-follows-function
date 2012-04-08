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

import com.sun.tools.mjavac.code.Flags;
import com.sun.tools.mjavac.util.Context;
import com.sun.tools.mjavac.util.Log;
import org.f3.tools.code.F3Flags;
import org.f3.tools.code.F3VarSymbol;
import org.f3.tools.util.MsgSym;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Collect and print statistics on optimization.
 * 
 * @author Robert Field
 */
public class F3OptimizationStatistics {
    
    private final Log log;

    private int instanceVarCount;
    private int instanceDefCount;

    private int scriptVarCount;
    private int scriptDefCount;

    private int localBoundVarCount;
    private int localBoundDefCount;

    private int localUnboundVarCount;
    private int localUnboundDefCount;
    
    private int proxyMethodCount;
    private int concreteFieldCount;

    private class DecomposeData implements Comparable {
        String name;
        int count;
        int synthVars;
        int shreds;
        DecomposeData(Class tree) {
            this.name = tree.getSimpleName();
        }

        DecomposeData(String name) {
            this.name = name;
        }

        String name() {
            return name;
        }

        public int compareTo(Object o) {
            DecomposeData dd = (DecomposeData) o;
            if (synthVars != dd.synthVars) {
                return dd.synthVars - synthVars;
            } else {
                return name().compareTo(dd.name());
            }
        }
    }
    private Stack<DecomposeData> decomposeStack;
    private DecomposeData unbound;

    private Map<Class, DecomposeData> decomposeMap;
    private Map<Class, Integer> translatorMap;
    
    /**
     * Context set-up
     */
    protected static final Context.Key<F3OptimizationStatistics> f3OptStatKey = new Context.Key<F3OptimizationStatistics>();

    public static F3OptimizationStatistics instance(Context context) {
        F3OptimizationStatistics instance = context.get(f3OptStatKey);
        if (instance == null) {
            instance = new F3OptimizationStatistics(context);
        }
        return instance;
    }

    protected F3OptimizationStatistics(Context context) {
        context.put(f3OptStatKey, this);

        log = Log.instance(context);

        instanceVarCount = 0;
        instanceDefCount = 0;

        scriptVarCount = 0;
        scriptDefCount = 0;

        localBoundVarCount = 0;
        localBoundDefCount = 0;

        localUnboundVarCount = 0;
        localUnboundDefCount = 0;
        
        proxyMethodCount = 0;
        concreteFieldCount = 0;

        decomposeStack = new Stack<DecomposeData>();
        unbound = new DecomposeData("<unbound>");
        unbound.count = 1;
        decomposeStack.push(unbound);
        decomposeMap = new HashMap<Class, DecomposeData>();
        translatorMap = new HashMap<Class, Integer>();
    }

    public void recordDecomposeEnter(Class treeClass) {
        DecomposeData dd = decomposeMap.get(treeClass);
        if (dd == null) {
            dd = new DecomposeData(treeClass);
            decomposeMap.put(treeClass, dd);
        }
        decomposeStack.push(dd);
        dd.count++;
    }

    public void recordDecomposeExit() {
        decomposeStack.pop();
    }

    public void recordSynthVar(String id) {
        decomposeStack.peek().synthVars++;
    }

    public void recordShreds() {
        decomposeStack.peek().shreds++;
    }

    public void recordTranslator(Class translator) {
        Integer mCnt = translatorMap.get(translator);
        int cnt = mCnt==null? 0 : mCnt;
        translatorMap.put(translator, cnt+1);
    }

    public void recordClassVar(F3VarSymbol vsym) {
        boolean isDef = vsym.isDef();
        boolean isScript = vsym.isStatic();
        if (isScript) {
            if (isDef) {
                ++scriptDefCount;
            } else {
                ++scriptVarCount;
            }
        } else {
            if (isDef) {
                ++instanceDefCount;
            } else {
                ++instanceVarCount;
            }
        }
    }

    public void recordLocalVar(F3VarSymbol vsym, boolean isBound, boolean isLocation) {
        boolean isDef = vsym.isDef();
        if (isBound) {
            if (isDef) {
                ++localBoundDefCount;
            } else {
                ++localBoundVarCount;
            }
        } else {
            if (isDef) {
                ++localUnboundDefCount;
            } else {
                ++localUnboundVarCount;
            }
        }
    }

    public void recordProxyMethod() {
        ++proxyMethodCount;
    }

    public void recordConcreteField() {
        ++concreteFieldCount;
    }
    
    private void show(String label, int value) {
        log.note(MsgSym.MESSAGE_F3_OPTIMIZATION_STATISTIC, label, value);
    }
    
    private void printInstanceVariableData() {
        int instanceVariableCount = instanceVarCount + instanceDefCount;

        show("Instance variable count", instanceVariableCount);
        
        show("Instance 'var' count", instanceVarCount);
        
        show("Instance 'def' count", instanceDefCount);
    }
    
    private void printScriptVariableData() {
        int scriptVariableCount = scriptVarCount + scriptDefCount;

        show("Script variable count", scriptVariableCount);
        
        show("Script 'var' count", scriptVarCount);
        
        show("Script 'def' count", scriptDefCount);
    }
    
    private void printLocalVariableData() {
        int localBoundVariableCount = localBoundVarCount + localBoundDefCount;

        show("Local bound variable count", localBoundVariableCount);
        
        show("Local bound 'var' count", localBoundVarCount);
        
        show("Local bound 'def' count", localBoundDefCount);
        
        int localUnboundVariableCount = localUnboundVarCount + localUnboundDefCount;

        show("Local unbound variable count", localUnboundVariableCount);
        
        show("Local unbound 'var' count", localUnboundVarCount);
        
        show("Local unbound 'def' count", localUnboundDefCount);
        
        int localVariableCount = localBoundVariableCount + localUnboundVariableCount;
        int localVarCount = localUnboundVarCount + localBoundVarCount;
        int localDefCount = localUnboundDefCount + localBoundDefCount;

        show("Local variable count", localVariableCount);
        
        show("Local 'var' count", localVarCount);
        
        show("Local 'def' count", localDefCount);
    }
    
    private void printProxyMethodData() {
        show("Proxy method count", proxyMethodCount);
    }
    
    private void printConcreteFieldData() {
        show("Concrete field count", concreteFieldCount);
    }

    private void printTranslators() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        List<Map.Entry<Class, Integer>> me = new ArrayList(translatorMap.entrySet());
        Collections.<Map.Entry<Class, Integer>>sort(me, new Comparator() {

            public int compare(Object o1, Object o2) {
                Map.Entry<Class, Integer> e1 = (Map.Entry<Class, Integer>)o1;
                Map.Entry<Class, Integer> e2 = (Map.Entry<Class, Integer>)o2;
                int v1 = (int)e1.getValue();
                int v2 = (int)e2.getValue();
                if (v1==v2) {
                    return e1.getKey().getName().compareTo(e2.getKey().getName());
                } else {
                    return v2-v1;
                }
            }
        });
        for (Map.Entry<Class, Integer> pair : me) {
            Class k = pair.getKey();
            String name = k.getSimpleName().length()==0? k.getName() : k.getSimpleName();
            printWriter.printf("\n%5d  %s", (int) pair.getValue(), name);
        }
        printWriter.close();
        log.note(MsgSym.MESSAGE_F3_OPTIMIZATION_STATISTIC, "Translators", stringWriter.toString());
    }

    private void printDecompose() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        List<DecomposeData> ldd = new ArrayList(decomposeMap.values());
        ldd.add(unbound);
        Collections.<DecomposeData>sort(ldd);
        printWriter.println("\n\nSynth  Shred  Count  Synth  Shred  Tree");
        printWriter.println(    "  All    All           Per    Per    ");
        for (DecomposeData dd : ldd) {
            printWriter.printf("\n%5d  %5d  %5d  %5.1f  %5.1f  %s",
                    dd.synthVars, dd.shreds, dd.count, ((double)dd.synthVars)/dd.count, ((double)dd.shreds)/dd.count, dd.name());
        }
        printWriter.close();
        log.note(MsgSym.MESSAGE_F3_OPTIMIZATION_STATISTIC, "Decompose", stringWriter.toString());
    }

    public void printData(String which) {
        if (which.contains("i")) {
            printInstanceVariableData();
        }
        if (which.contains("s")) {
            printScriptVariableData();
        }
        if (which.contains("l")) {
            printLocalVariableData();
        }
        if (which.contains("m")) {
            printProxyMethodData();
        }
        if (which.contains("f")) {
            printConcreteFieldData();
        }
        if (which.contains("t")) {
            printTranslators();
        }
        if (which.contains("d")) {
            printDecompose();
        }
    }
 }
