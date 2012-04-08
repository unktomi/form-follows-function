/*
 * Copyright 2010 Sun Microsystems, Inc.  All Rights Reserved.
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

package org.f3.jdi;

import org.f3.jdi.event.F3EventQueue;
import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.ClassLoaderReference;
import com.sun.jdi.ClassObjectReference;
import com.sun.jdi.Field;
import com.sun.jdi.Location;
import com.sun.jdi.Method;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ClassType;
import com.sun.jdi.Value;
import com.sun.jdi.ShortValue;
import com.sun.jdi.InvalidTypeException;
import com.sun.jdi.InvocationException;
import com.sun.jdi.ClassNotLoadedException;
import com.sun.jdi.IncompatibleThreadStateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author sundar
 */
public class F3ReferenceType extends F3Type implements ReferenceType {
    public F3ReferenceType(F3VirtualMachine f3vm, ReferenceType underlying) {
        super(f3vm, underlying);
    }

    public byte[] constantPool() {
        return underlying().constantPool();
    }

    public int constantPoolCount() {
        return underlying().constantPoolCount();
    }

    public List<ObjectReference> instances(long count) {
        return F3Wrapper.wrapObjectReferences(virtualMachine(), underlying().instances(count));
    }

    public int majorVersion() {
        return underlying().majorVersion();
    }

    public int minorVersion() {
        return underlying().minorVersion();
    }

    public List<Field> allFields() {
        return F3Wrapper.wrapFields(virtualMachine(), underlying().allFields());
    }

    public List<Location> allLineLocations() throws AbsentInformationException {
        return F3Wrapper.wrapLocations(virtualMachine(), underlying().allLineLocations());
    }

    public List<Location> allLineLocations(String stratum, String sourceName) throws AbsentInformationException {
        return F3Wrapper.wrapLocations(virtualMachine(), underlying().allLineLocations(stratum, sourceName));
    }

    public List<Method> allMethods() {
        return F3Wrapper.wrapMethods(virtualMachine(), underlying().allMethods());
    }

    public List<String> availableStrata() {
        return underlying().availableStrata();
    }

    public ClassLoaderReference classLoader() {
        return F3Wrapper.wrap(virtualMachine(), underlying().classLoader());
    }

    public ClassObjectReference classObject() {
        return F3Wrapper.wrap(virtualMachine(), underlying().classObject());
    }

    public String defaultStratum() {
        return underlying().defaultStratum();
    }

    public boolean failedToInitialize() {
        return underlying().failedToInitialize();
    }

    // return null if there is no field or the name is ambigous. 
    public F3Field fieldByName(String name) {
        // There could be both an F3 field $xxx and a java field xxx
        Field javaField = underlying().fieldByName(name);
        Field f3Field = underlying().fieldByName("$" + name);
        if (javaField == null) {
            if (f3Field == null ) {
                // an ivar that is a referenced in an outer class can be prefixed with
                //  'classname$'
                return null;
            }
            // we'll return f3Field
        } else {
            if (f3Field != null) {
                // we found both name and $name
                return null;
            }
            f3Field = javaField;
        }
        return F3Wrapper.wrap(virtualMachine(), f3Field);
    }

    public List<Field> fields() {
        return F3Wrapper.wrapFields(virtualMachine(), underlying().fields());
    }

    public String genericSignature() {
        return underlying().genericSignature();
    }

    // The RefType for the ....$Script class for this class if there is one
    private ReferenceType scriptType ;

    public int getFlagWord(Field field) {
        // could this be a java field inherited by an f3 class??
        if (!isF3Type()) {
            return 0;
        }
        if (scriptType == null) {
            ReferenceType jdiRefType = underlying();
            String jdiRefTypeName = jdiRefType.name();
            String scriptClassName = jdiRefTypeName;
            int lastDot = scriptClassName.lastIndexOf('.');
            if (lastDot != -1) {
                scriptClassName = scriptClassName.substring(lastDot + 1);
            }
            scriptClassName = jdiRefTypeName + "$" + scriptClassName + "$Script";
            List<ReferenceType> rtx =  virtualMachine().classesByName(scriptClassName);
            if (rtx.size() != 1) {
                System.out.println("--F3JDI Error: Can't find the class: " + scriptClassName);
                return 0;
            }
            scriptType = rtx.get(0);
        }
        Field jdiField = F3Wrapper.unwrap(field); 
        String jdiFieldName = jdiField.name();
        String vflgFieldName = "VFLG" + jdiFieldName;

        Field  vflgField = scriptType.fieldByName(vflgFieldName);
        if (vflgField == null) {
            // not all fields have a VFLG, eg, a private field that isn't accessed
            return 0;
        }
        Value vflgValue = F3Wrapper.unwrap(scriptType).getValue(F3Wrapper.unwrap(vflgField));
        return((ShortValue)vflgValue).value();
    }

    private boolean areFlagBitsSet(Field field, int mask) {
        return (getFlagWord(field) & mask) == mask;
    }

    private Map<Field, Value> getValuesCommon(List<? extends Field> wrappedFields, boolean doInvokes) throws
        InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException {
        // We will find fields which have no getters, and call the underlying
        // getValues to get values for all of them in one fell swoop.
        Map<Field, Field> unwrappedToWrappedMap = new HashMap<Field, Field>();
        List<Field> noGetterUnwrappedFields = new ArrayList<Field>();    // fields that don't have getters

        // For fields that do have getters, we will just return VoidValue for them if
        // or we will call F3GetValue for each, depending on doInvokes
        Map<Field, Value> result = new HashMap<Field, Value>();

        // Create the above Maps and lists
        for (Field wrappedField : wrappedFields) {
            Field unwrapped = F3Wrapper.unwrap(wrappedField);
            if (isF3Type()) {
                List<Method> mth = underlying().methodsByName("get" + unwrapped.name());
                if (mth.size() == 0) {
                    // No getter
                    unwrappedToWrappedMap.put(unwrapped, wrappedField);
                    noGetterUnwrappedFields.add(unwrapped);
                } else {
                    // Field has a getter
                    if (doInvokes) {
                        result.put(wrappedField, getValue(wrappedField));
                    } else {
                        result.put(wrappedField, virtualMachine().voidValue());
                    }
                }
            } else {
                // Java type
                unwrappedToWrappedMap.put(unwrapped, wrappedField);
                noGetterUnwrappedFields.add(unwrapped);
            }                
        }

        // Get values for all the noGetter fields.  Note that this gets them in a single JDWP trip
        Map<Field, Value> unwrappedFieldValues = underlying().getValues(noGetterUnwrappedFields);

        // for each input Field, create a result map entry with that field as the
        // key, and the value returned by getValues, or null if the field is invalid.

        // Make a pass over the unwrapped no getter fields and for each, put its
        // wrapped version, and wrapped value into the result Map.
        for (Map.Entry<Field, Field> unwrappedEntry: unwrappedToWrappedMap.entrySet()) {
            Field wrappedField = unwrappedEntry.getValue();
            Value resultValue = F3Wrapper.wrap(virtualMachine(), 
                                             unwrappedFieldValues.get(unwrappedEntry.getKey()));
            result.put(wrappedField, resultValue);
        }
        return result;
    }

    /**
     * JDI addition: Determines if the value of a field of this reference type is invalid.  A value
     * is invalid if a new value has been specified for the field, but not yet
     * stored into the field, for example, because the field is lazily bound.
     *
     * @return <code>true</code> if the value of the specified field is invalid; false otherwise.
     */
    public boolean isInvalid(Field field) {
        return areFlagBitsSet(field, virtualMachine().F3InvalidFlagMask());
    }

    /**
     * JDI addition: Determines if a field of this reference type can be modified.  For example,
     * an field declared with a bind cannot be modified.
     *
     * @return <code>true</code> if the specified field is read only; false otherwise.
     */
    public boolean isReadOnly(Field field) {
        return areFlagBitsSet(field, virtualMachine().F3ReadOnlyFlagMask());
    }

    /**
     * JDI addition: Determines if a field was declared with a bind clause.
     *
     * @return <code>true</code> if the specified field was declared with a bind clause; false otherwise.
     */
    public boolean isBound(Field field) {
        return areFlagBitsSet(field, virtualMachine().F3BoundFlagMask());
    }

    /**
     * JDI addition: Determines if this is a F3 class.
     *
     * @return <code>true</code> if this is a F3 class; false otherwise.
     */
    public boolean isF3Type() {
        return false;
    }

    // Each internal class name is the name of its containing class followed by $digit.
    // (Except for the $Script class)
    private Pattern f3Pat1 = Pattern.compile("\\$[0-9].*");
    private boolean isUserClassSet = false;
    private F3ClassType userClass = null;

    /**
     * JDI addition: Return the F3 user class associated with this reference type.
     *
     * The F3 compiler can generate several classes for a given class defined by the user.
     * Given one of these internal classes, this method will return the ReferenceType for the
     * associated user class.
     *
     * @return the F3 user class associated with this reference type if there is one, else null.
     */
    public F3ClassType f3UserClass() {
        if (isUserClassSet) {
            return userClass;
        }

        isUserClassSet = true;
        if (!isF3Type()) {
            return null;
        }
        
        String className = name();
        int firstDollar = className.indexOf('$');
        if (firstDollar == -1) {
            return null;
        }

        String[] hit = f3Pat1.split(className, 0);
        if (hit.length != 1) {
            return null;
        }
        if (!hit[0].equals(className)) {
            List<ReferenceType> userClasses = virtualMachine().classesByName(hit[0]);
            if (userClasses.size() != 1) {
                // can't happen
                return null;
            }
            userClass = (F3ClassType)userClasses.get(0);
            return userClass;
        }

        if (className.indexOf("$Script") == -1) {
            return null;
        }
        
        // This is a $Script class, so we want the scriptClass
        userClass = scriptClass();
        return userClass;
    }

    private boolean isTopClassSet = false;
    private F3ClassType topClass = null;
    /**
     * JDI addition: Return the script class associated with this reference type.
     *
     * The F3 compiler can generate several classes for a given F3 file.  
     * Given one of these classes, this method will return the associated ReferenceType 
     * for the containing script class.
     *
     * @return the F3 class that contains this class if there is one, else null.
     */
    public F3ClassType scriptClass() {
        if (isTopClassSet) {
            return topClass;
        }

        isTopClassSet = true;
        if (!isF3Type()) {
            return null;
        }
        if (!(this instanceof F3ClassType)) {
            return null;
        }

        // Get the name of the top class.  First choice is the filename.  2nd choice
        // is the part of this name before the first $.
        String topClassName ;
        String thisName = name();
        int firstDollar = thisName.indexOf('$');
        if (firstDollar == -1) {
            topClass = (F3ClassType)this;
            return topClass;
        }
        topClassName = thisName.substring(0, firstDollar);

        List<ReferenceType> xx = virtualMachine().classesByName(topClassName);
        if (xx.size() != 1) {
            //  shouldn't happen
            return null;
        }

        if (!(xx.get(0) instanceof F3ClassType)) {
            // shouldn't happen
            return null;
        }
        
        topClass = (F3ClassType)xx.get(0);
        return topClass;
    }

    /**
     * JDI extension: This will call the get function for the field if one exists via invokeMethod.
     * The call to invokeMethod is preceded by a call to {@link F3EventQueue#setEventControl(boolean)} passing true
     * and is followed by a call to {@link F3EventQueue#setEventControl(boolean)} passing false.
     *
     * If an invokeMethod Exception occurs, it is saved and can be accessed by calling 
     * {@link F3VirtualMachine#lastFieldAccessException()}. In this case,
     * the default value for the type of the field is returned for a PrimitiveType,
     * while null is returned for a non PrimitiveType.
     */
    public Value getValue(Field field) {
        virtualMachine().setLastFieldAccessException(null);
        Field jdiField = F3Wrapper.unwrap(field);
        if (!isF3Type()) {
            return F3Wrapper.wrap(virtualMachine(), underlying().getValue(jdiField));
        }

        //get$xxxx methods exist for fields except private fields which have no binders

        List<Method> mth = underlying().methodsByName("get" + jdiField.name());
        if (mth.size() == 0) {
            return F3Wrapper.wrap(virtualMachine(), underlying().getValue(jdiField));
        }
        Exception theExc = null;
        F3EventQueue eq = virtualMachine().eventQueue();
        try {
            eq.setEventControl(true);
            return ((F3ClassType)this).invokeMethod(virtualMachine().uiThread(), mth.get(0), new ArrayList<Value>(0), ClassType.INVOKE_SINGLE_THREADED);
        } catch(InvalidTypeException ee) {
            theExc = ee;
        } catch(ClassNotLoadedException ee) {
            theExc = ee;
        } catch(IncompatibleThreadStateException ee) {
            theExc = ee;
        } catch(InvocationException ee) {
            theExc = ee;
        } finally {
            eq.setEventControl(false);
        }
        // We don't have to catch IllegalArgumentException.  It is an unchecked exception for invokeMethod
        // and for getValue

        virtualMachine().setLastFieldAccessException(theExc);
        try {
            return virtualMachine().defaultValue(field.type());
        } catch(ClassNotLoadedException ee) {
            // The type has to be a ReferenceType for which we return null;
            return null;
        }
    }

    /**
     * JDI extension: This will call the get function for a field if one exists via invokeMethod.
     * The call to invokeMethod is preceded by a call to {@link F3EventQueue#setEventControl(boolean)}
     * passing true and is followed by a call to {@link F3EventQueue#setEventControl(boolean)} passing false.
     *
     * If an invokeMethod Exception occurs, it is saved and can be accessed by calling 
     * {@link F3VirtualMachine#lastFieldAccessException()}. In this case,
     * the default value for the type of the field is returned for a PrimitiveType,
     * while null is returned for a non PrimitiveType.
     */
    public Map<Field, Value> getValues(List<? extends Field> wrappedFields) {
        virtualMachine().setLastFieldAccessException(null);

        // We will find fields which have no getters, and call the underlying
        // getValues to get values for all of them in one fell swoop.
        Map<Field, Field> unwrappedToWrappedMap = new HashMap<Field, Field>();
        List<Field> noGetterUnwrappedFields = new ArrayList<Field>();    // fields that don't have getters

        // But first, for fields that do have getters, call invokeMethod
        // or we will call F3GetValue for each, depending on doInvokes
        Map<Field, Value> result = new HashMap<Field, Value>();

        // Create the above Maps and lists
        for (Field wrappedField : wrappedFields) {
            Field unwrapped = F3Wrapper.unwrap(wrappedField);
            if (isF3Type()) {
                List<Method> mth = underlying().methodsByName("get" + unwrapped.name());
                if (mth.size() == 0) {
                    // No getter
                    unwrappedToWrappedMap.put(unwrapped, wrappedField);
                    noGetterUnwrappedFields.add(unwrapped);
                } else {
                    // Field has a getter
                    result.put(wrappedField, getValue(wrappedField));
                }
            } else {
                // Java type
                unwrappedToWrappedMap.put(unwrapped, wrappedField);
                noGetterUnwrappedFields.add(unwrapped);
            }                
        }

        // Get values for all the noGetter fields.  Note that this gets them in a single JDWP trip
        Map<Field, Value> unwrappedFieldValues = underlying().getValues(noGetterUnwrappedFields);

        // for each input Field, create a result map entry with that field as the
        // key, and the value returned by getValues, or null if the field is invalid.

        // Make a pass over the unwrapped no getter fields and for each, put its
        // wrapped version, and wrapped value into the result Map.
        for (Map.Entry<Field, Field> unwrappedEntry: unwrappedToWrappedMap.entrySet()) {
            Field wrappedField = unwrappedEntry.getValue();
            Value resultValue = F3Wrapper.wrap(virtualMachine(), 
                                             unwrappedFieldValues.get(unwrappedEntry.getKey()));
            result.put(wrappedField, resultValue);
        }
        return result;
    }

    public boolean isAbstract() {
        return underlying().isAbstract();
    }

    public boolean isFinal() {
        return underlying().isFinal();
    }

    public boolean isInitialized() {
        return underlying().isInitialized();
    }

    public boolean isPrepared() {
        return underlying().isPrepared();
    }

    public boolean isStatic() {
        return underlying().isStatic();
    }

    public boolean isVerified() {
        return underlying().isVerified();
    }

    public List<Location> locationsOfLine(int lineNumber) throws AbsentInformationException {
        return F3Wrapper.wrapLocations(virtualMachine(), underlying().locationsOfLine(lineNumber));
    }

    public List<Location> locationsOfLine(String stratum, String sourceName, int line) throws AbsentInformationException {
        return F3Wrapper.wrapLocations(virtualMachine(), underlying().locationsOfLine(stratum, sourceName, line));
    }

    public List<Method> methods() {
        return F3Wrapper.wrapMethods(virtualMachine(), underlying().methods());
    }

    public List<Method> methodsByName(String name) {
        return F3Wrapper.wrapMethods(virtualMachine(), underlying().methodsByName(name));
    }

    public List<Method> methodsByName(String name, String signature) {
        return F3Wrapper.wrapMethods(virtualMachine(), underlying().methodsByName(name, signature));
    }

    public List<ReferenceType> nestedTypes() {
        return F3Wrapper.wrapReferenceTypes(virtualMachine(), underlying().nestedTypes());
    }

    public String sourceDebugExtension() throws AbsentInformationException {
        return underlying().sourceDebugExtension();
    }

    public String sourceName() throws AbsentInformationException {
        return underlying().sourceName();
    }

    public List<String> sourceNames(String stratum) throws AbsentInformationException {
        return underlying().sourceNames(stratum);
    }

    public List<String> sourcePaths(String stratum) throws AbsentInformationException {
        return underlying().sourcePaths(stratum);
    }

    public List<Field> visibleFields() {
        return F3Wrapper.wrapFields(virtualMachine(), underlying().visibleFields());
    }

    public List<Method> visibleMethods() {
        return F3Wrapper.wrapMethods(virtualMachine(), underlying().visibleMethods());
    }

    public int compareTo(ReferenceType o) {
        return underlying().compareTo(F3Wrapper.unwrap(o));
    }

    public boolean isPackagePrivate() {
        return underlying().isPackagePrivate();
    }

    public boolean isPrivate() {
        return underlying().isPrivate();
    }

    public boolean isProtected() {
        return underlying().isProtected();
    }

    public boolean isPublic() {
        return underlying().isPublic();
    }

    public int modifiers() {
        return underlying().modifiers();
    }

    @Override
    protected ReferenceType underlying() {
        return (ReferenceType) super.underlying();
    }
    
    public ReferenceType _underlying() {
        return (ReferenceType)super.underlying();
    }

    private boolean isInternalJavaTypeSet = false;
    private boolean internalJavaType = false;
    public boolean isInternalJavaType() {
        if (!isInternalJavaTypeSet) {
            String myName = name();
            if ("org.f3.runtime.F3Base".equals(myName) ||
                "org.f3.runtime.F3Object".equals(myName) ||
                myName.startsWith("org.f3.functions.Function")) {
                internalJavaType = true;
                isInternalJavaTypeSet = true;
            }
        }
        return internalJavaType;
    }
}
