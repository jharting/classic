package cz.muni.fi.xharting.classic.util;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Reflection utilities. Selected methods are copied from Seam 2.2
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 * 
 */
public class ClassicReflections {

    public static Class<?> getCollectionElementType(Type collectionType) {
        if (!(collectionType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("collection type not parameterized");
        }
        Type[] typeArguments = ((ParameterizedType) collectionType).getActualTypeArguments();
        if (typeArguments.length == 0) {
            throw new IllegalArgumentException("no type arguments for collection type");
        }
        Type typeArgument = typeArguments.length == 1 ? typeArguments[0] : typeArguments[1]; // handle Maps
        if (typeArgument instanceof ParameterizedType) {
            typeArgument = ((ParameterizedType) typeArgument).getRawType();
        }
        if (!(typeArgument instanceof Class)) {
            throw new IllegalArgumentException("type argument not a class");
        }
        return (Class<?>) typeArgument;
    }

    public static Class<?> getMapKeyType(Type collectionType) {
        if (!(collectionType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("collection type not parameterized");
        }
        Type[] typeArguments = ((ParameterizedType) collectionType).getActualTypeArguments();
        if (typeArguments.length == 0) {
            throw new IllegalArgumentException("no type arguments for collection type");
        }
        Type typeArgument = typeArguments[0];
        if (!(typeArgument instanceof Class)) {
            throw new IllegalArgumentException("type argument not a class");
        }
        return (Class<?>) typeArgument;
    }

    public static Method getSetterMethod(Class<?> clazz, String name) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.startsWith("set") && method.getParameterTypes().length == 1) {
                if (Introspector.decapitalize(methodName.substring(3)).equals(name)) {
                    return method;
                }
            }
        }
        throw new IllegalArgumentException("no such setter method: " + clazz.getName() + '.' + name);
    }

    public static Field getField(Class<?> clazz, String name) {
        for (Class<?> superClass = clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(name);
            } catch (NoSuchFieldException nsfe) {
            }
        }
        throw new IllegalArgumentException("no such field: " + clazz.getName() + '.' + name);
    }
}
