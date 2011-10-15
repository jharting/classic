package org.jboss.seam.classic.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.seam.classic.config.Conversions.PropertyValue;
import org.jboss.seam.classic.util.Reflections;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.MethodExpression;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.solder.bean.ForwardingInjectionTarget;

public class ConfiguringInjectionTarget<T> extends ForwardingInjectionTarget<T> {

    private Map<Field, InitialValue> fieldInitializers = new HashMap<Field, ConfiguringInjectionTarget.InitialValue>();
    private Map<Method, InitialValue> methodInitializers = new HashMap<Method, ConfiguringInjectionTarget.InitialValue>();
    private InjectionTarget<T> delegate;
    private String name;

    public ConfiguringInjectionTarget(Map<String, PropertyValue> values, InjectionTarget<T> delegate,
            AnnotatedType<T> annotatedType, String name) {
        this.delegate = delegate;
        this.name = name;
        for (Map.Entry<String, Conversions.PropertyValue> value : values.entrySet()) {
            String key = value.getKey();
            Conversions.PropertyValue propertyValue = value.getValue();

            Method setterMethod = null;
            try {
                setterMethod = Reflections.getSetterMethod(annotatedType.getJavaClass(), key);
            } catch (IllegalArgumentException ignored) {
            }
            if (setterMethod != null) {
                if (!setterMethod.isAccessible()) {
                    setterMethod.setAccessible(true);
                }
                Class<?> parameterClass = setterMethod.getParameterTypes()[0];
                Type parameterType = setterMethod.getGenericParameterTypes()[0];
                methodInitializers.put(setterMethod, getInitialValue(propertyValue, parameterClass, parameterType));
            } else {
                Field field = Reflections.getField(annotatedType.getJavaClass(), key);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                fieldInitializers.put(field, getInitialValue(propertyValue, field.getType(), field.getGenericType()));
            }
        }
    }

    @Override
    public void inject(T instance, CreationalContext<T> ctx) {
        delegate.inject(instance, ctx);
        // if ( log.isTraceEnabled() ) log.trace("initializing new instance of: " + name);
        for (Map.Entry<Method, InitialValue> me : methodInitializers.entrySet()) {
            Method method = me.getKey();
            Object initialValue = me.getValue().getValue(method.getParameterTypes()[0]);
            setPropertyValue(instance, method, method.getName(), initialValue);
        }
        for (Map.Entry<Field, InitialValue> me : fieldInitializers.entrySet()) {
            Field field = me.getKey();
            Object initialValue = me.getValue().getValue(field.getType());
            setFieldValue(instance, field, field.getName(), initialValue);
        }
        // if ( log.isTraceEnabled() ) log.trace("done initializing: " + name);
    }

    @Override
    protected InjectionTarget<T> delegate() {
        return delegate;
    }

    private static InitialValue getInitialValue(Conversions.PropertyValue propertyValue, Class<?> parameterClass,
            Type parameterType) {
        if (parameterClass.equals(ValueExpression.class) || parameterClass.equals(MethodExpression.class)
                || propertyValue.isExpression()) {
            return new ELInitialValue(propertyValue, parameterClass, parameterType);
        } else if (propertyValue.isMultiValued()) {
            if (Set.class.isAssignableFrom(parameterClass)) {
                return new SetInitialValue(propertyValue, parameterClass, parameterType);
            } else {
                return new ListInitialValue(propertyValue, parameterClass, parameterType);
            }
        } else if (propertyValue.isAssociativeValued()) {
            return new MapInitialValue(propertyValue, parameterClass, parameterType);
        } else {
            return new ConstantInitialValue(propertyValue, parameterClass, parameterType);
        }
    }

    static interface InitialValue {
        Object getValue(Class<?> type);
    }

    static class ConstantInitialValue implements InitialValue {
        private Object value;

        public ConstantInitialValue(PropertyValue propertyValue, Class<?> parameterClass, Type parameterType) {
            this.value = Conversions.getConverter(parameterClass).toObject(propertyValue, parameterType);
        }

        public Object getValue(Class<?> type) {
            return value;
        }

        @Override
        public String toString() {
            return "ConstantInitialValue(" + value + ")";
        }

    }

    static class ELInitialValue implements InitialValue {
        private String expression;
        // private ValueBinding vb;
        private Conversions.Converter<?> converter;
        private Type parameterType;

        public ELInitialValue(PropertyValue propertyValue, Class<?> parameterClass, Type parameterType) {
            this.expression = propertyValue.getSingleValue();
            this.parameterType = parameterType;
            try {
                this.converter = Conversions.getConverter(parameterClass);
            } catch (IllegalArgumentException iae) {
                // no converter for the type
            }
        }

        public Object getValue(Class<?> type) {
            Object value;
            if (type.equals(ValueExpression.class)) {
                value = createValueExpression();
            } else if (type.equals(MethodExpression.class)) {
                value = createMethodExpression();
            } else {
                value = createValueExpression().getValue();
            }

            if (converter != null && value instanceof String) {
                return converter.toObject(new Conversions.FlatPropertyValue((String) value), parameterType);
            } else if (converter != null && value instanceof String[]) {
                return converter.toObject(new Conversions.MultiPropertyValue((String[]) value, null), parameterType);
            } else {
                return value;
            }
        }

        private ValueExpression<?> createValueExpression() {
            return Expressions.instance().createValueExpression(expression);
        }

        private MethodExpression<?> createMethodExpression() {
            return Expressions.instance().createMethodExpression(expression);
        }

        @Override
        public String toString() {
            return "ELInitialValue(" + expression + ")";
        }

    }

    static class SetInitialValue implements InitialValue {
        private InitialValue[] initialValues;
        private Class<?> elementType;
        private Class<?> collectionClass;

        public SetInitialValue(PropertyValue propertyValue, Class<?> collectionClass, Type collectionType) {
            String[] expressions = propertyValue.getMultiValues();
            initialValues = new InitialValue[expressions.length];
            elementType = Reflections.getCollectionElementType(collectionType);
            if (propertyValue.getType() != null) {
                this.collectionClass = propertyValue.getType();
            } else {
                this.collectionClass = collectionClass;
            }
            for (int i = 0; i < expressions.length; i++) {
                PropertyValue elementValue = new Conversions.FlatPropertyValue(expressions[i]);
                initialValues[i] = getInitialValue(elementValue, elementType, elementType);
            }
        }

        @SuppressWarnings("unchecked")
        public Object getValue(Class<?> type) {
            Set<Object> set;
            // if no configuration has been specified then we first see if
            // the property is an abstract type, if so we create it using newInstance
            if (Modifier.isAbstract(collectionClass.getModifiers()) || Modifier.isInterface(collectionClass.getModifiers())) {
                if (collectionClass == SortedSet.class) {
                    set = new TreeSet<Object>();
                } else {
                    set = new LinkedHashSet<Object>(initialValues.length);
                }
            } else {
                try {
                    set = (Set<Object>) collectionClass.newInstance();
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Cannot instantiate a set of type " + collectionClass
                            + "; try specifying type type in components.xml");
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Cannot cast " + collectionClass + " to java.util.Set");
                } catch (java.lang.InstantiationException e) {
                    throw new IllegalArgumentException("Cannot instantiate a set of type " + collectionClass
                            + "; try specifying type type in components.xml");
                }
            }
            for (InitialValue iv : initialValues) {
                set.add(iv.getValue(elementType));
            }
            return set;
        }

        @Override
        public String toString() {
            return "SetInitialValue(" + elementType.getSimpleName() + ")";
        }
    }

    static class ListInitialValue implements InitialValue {
        private InitialValue[] initialValues;
        private Class<?> elementType;
        private boolean isArray;
        private Class<?> collectionClass;

        public ListInitialValue(PropertyValue propertyValue, Class<?> collectionClass, Type collectionType) {
            String[] expressions = propertyValue.getMultiValues();
            initialValues = new InitialValue[expressions.length];
            isArray = collectionClass.isArray();
            elementType = isArray ? collectionClass.getComponentType() : Reflections.getCollectionElementType(collectionType);
            if (propertyValue.getType() != null) {
                this.collectionClass = propertyValue.getType();
            } else {
                this.collectionClass = collectionClass;
            }
            for (int i = 0; i < expressions.length; i++) {
                PropertyValue elementValue = new Conversions.FlatPropertyValue(expressions[i]);
                initialValues[i] = getInitialValue(elementValue, elementType, elementType);
            }
        }

        @SuppressWarnings("unchecked")
        public Object getValue(Class<?> type) {
            if (isArray) {
                Object array = Array.newInstance(elementType, initialValues.length);
                for (int i = 0; i < initialValues.length; i++) {
                    Array.set(array, i, initialValues[i].getValue(elementType));
                }
                return array;
            } else {
                List<Object> list;
                // if no configuration has been specified then we first see if
                // the property is an abstract type, if so we create it using newInstance
                if (Modifier.isAbstract(collectionClass.getModifiers()) || Modifier.isInterface(collectionClass.getModifiers())) {
                    list = new ArrayList<Object>(initialValues.length);
                } else {
                    try {
                        list = (List<Object>) collectionClass.newInstance();
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException("Cannot instantiate a list of type " + collectionClass
                                + "; try specifying type type in components.xml");
                    } catch (ClassCastException e) {
                        throw new IllegalArgumentException("Cannot cast " + collectionClass + " to java.util.List");
                    } catch (java.lang.InstantiationException e) {
                        throw new IllegalArgumentException("Cannot instantiate a list of type " + collectionClass
                                + "; try specifying type type in components.xml");
                    }
                }
                for (InitialValue iv : initialValues) {
                    list.add(iv.getValue(elementType));
                }
                return list;
            }
        }

        @Override
        public String toString() {
            return "ListInitialValue(" + elementType.getSimpleName() + ")";
        }

    }

    static class MapInitialValue implements InitialValue {
        private Map<InitialValue, InitialValue> initialValues;
        private Class<?> elementType;
        private Class<?> keyType;
        private Class<?> collectionClass;

        public MapInitialValue(PropertyValue propertyValue, Class<?> collectionClass, Type collectionType) {
            Map<String, String> expressions = propertyValue.getKeyedValues();
            initialValues = new LinkedHashMap<InitialValue, InitialValue>(expressions.size());
            elementType = Reflections.getCollectionElementType(collectionType);
            keyType = Reflections.getMapKeyType(collectionType);
            if (propertyValue.getType() != null) {
                this.collectionClass = propertyValue.getType();
            } else {
                this.collectionClass = collectionClass;
            }
            for (Map.Entry<String, String> me : expressions.entrySet()) {
                PropertyValue keyValue = new Conversions.FlatPropertyValue(me.getKey());
                PropertyValue elementValue = new Conversions.FlatPropertyValue(me.getValue());
                initialValues.put(getInitialValue(keyValue, keyType, keyType),
                        getInitialValue(elementValue, elementType, elementType));
            }
        }

        @SuppressWarnings("unchecked")
        public Object getValue(Class<?> type) {
            Map<Object, Object> result;
            if (Modifier.isAbstract(collectionClass.getModifiers()) || Modifier.isInterface(collectionClass.getModifiers())) {
                if (collectionClass == SortedMap.class) {
                    result = new TreeMap<Object, Object>();
                } else {
                    result = new LinkedHashMap<Object, Object>(initialValues.size());
                }
            } else {
                try {
                    result = (Map<Object, Object>) collectionClass.newInstance();
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("Cannot instantiate a map of type " + collectionClass
                            + "; try specifying type type in components.xml");
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("Cannot cast " + collectionClass + " to java.util.Map");
                } catch (java.lang.InstantiationException e) {
                    throw new IllegalArgumentException("Cannot instantiate a map of type " + collectionClass
                            + "; try specifying type type in components.xml");
                }
            }

            for (Map.Entry<InitialValue, InitialValue> me : initialValues.entrySet()) {
                result.put(me.getKey().getValue(keyType), me.getValue().getValue(elementType));
            }
            return result;
        }

        @Override
        public String toString() {
            return "MapInitialValue(" + keyType.getSimpleName() + "," + elementType.getSimpleName() + ")";
        }
    }

    private void setPropertyValue(Object bean, Method method, String name, Object value) {
        try {
            Reflections.invoke(method, bean, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("could not set property value: " + getAttributeMessage(name), e);
        }
    }

    private void setFieldValue(Object bean, Field field, String name, Object value) {
        try {
            Reflections.set(field, bean, value);
        } catch (Exception e) {
            throw new IllegalArgumentException("could not set field value: " + getAttributeMessage(name), e);
        }
    }

    private String getAttributeMessage(String attributeName) {
        return name + '.' + attributeName;
    }
}
