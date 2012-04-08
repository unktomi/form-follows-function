package org.f3.runtime;

/**
 * NumericTypeInfo
 *
 * @author Brian Goetz
 */
public class NumericTypeInfo<T extends Number> extends TypeInfo<T> {
    public NumericTypeInfo(T defaultValue, TypeInfo.Types type) {
        super(defaultValue, type);
    }

    @Override
    public boolean isNumeric() {
        return true;
    }

    public long longValue(T value) {
        return value.longValue();
    }

    public int intValue(T value) {
        return value.intValue();
    }

    public short shortValue(T value) {
        return value.shortValue();
    }

    public byte byteValue(T value) {
        return value.byteValue();
    }

    public double doubleValue(T value) {
        return value.doubleValue();
    }

    public float floatValue(T value) {
        return value.floatValue();
    }

    public T[] makeArray(int size) {
        return Util.<T>newNumberArray(size);
    }
    // This ugly and not typesafe construct eliminates lots of small classes, which add a lot to our static footprint.
    // Such optimizations are ugly but needed for smaller-memory platforms.
    @SuppressWarnings("unchecked")
    public<V extends Number> T asPreferred(NumericTypeInfo<V> otherType, V otherValue) {
        switch (type) {
            case INT: return (T) (Integer) otherType.intValue(otherValue);
            case SHORT: return (T) (Short) otherType.shortValue(otherValue);
            case BYTE: return (T) (Byte) otherType.byteValue(otherValue);
            case LONG: return (T) (Long) otherType.longValue(otherValue);
            case FLOAT: return (T) (Float) otherType.floatValue(otherValue);
            case DOUBLE: return (T) (Double) otherType.doubleValue(otherValue);
            default:
                throw new IllegalArgumentException();
        }
    }
}
