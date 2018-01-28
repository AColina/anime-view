/**
 * Copyright (c) 2015 by Consultoria y Aplicaciones Avanzadas de ECM, S.A. de C.V. All Rights Reserved.
 */
package com.acolina.animeview.util.reflection;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Angel Colina
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class ReflectionUtils {
    /**
     * Metodo que genera una instancia a partir de una clase
     *
     * @param clase  a instanciar
     * @param values array de valores que posee el constructor
     * @return nueva instancia de la clase
     */
    public static <T> T newInstance(Class<T> clase, Object... values) {
        return newInstanceForVector(clase, values);
    }

    /**
     * Metodo que genera una instancia a partir de una clase
     *
     * @param claseName a instanciar
     *                  array de valores que posee el constructor
     * @return nueva instancia de la clase
     */
    public static <T> T newInstance(String claseName) {
        try {

            Class<T> clase = (Class<T>) Class.forName(claseName);
            return newInstance(clase);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Metodo que genera una instancia a partir de una clase
     *
     * @param clase  a instanciar
     * @param values array de valores que posee el constructor
     * @return nueva instancia de la clase
     */
    public static <T> T newInstanceForVector(Class<T> clase, Object[] values) {
        try {
            Class[] c = new Class[values.length];
            for (int i = 0; i < values.length; i++) {
                c[i] = values[i].getClass();
            }
            if (values.length == 0) {
                return clase.newInstance();
            } else {
                return clase.getConstructor(c).newInstance(values);
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Obtiene un field a partir de un nombre y de una instancia
     *
     * @param object instancia del objeto a obtener el field
     * @param name   nombre tal cual del field
     * @return instancia del field del objeto
     */
    public static Field getField(Object object, String name) {
        return getField(object.getClass(), name);
    }

    /**
     * Obtiene un field a partir de un nombre y de una instancia
     *
     * @param clase a obtener el field
     * @param name  nombre del field ignorando mayusculas y minusculas
     * @return instancia del field de la clase
     */
    public static Field getField(Class clase, String name) {
        for (Field field : getFields(clase)) {
            if (field.getName().equalsIgnoreCase(name)) {
                return field;
            }
        }
        if (clase.getSuperclass() != null) {
            return getField(clase.getSuperclass(), name);
        }
        throw new NoSuchFieldError(String.format("El atributo %s no fue encontrado", name));
    }

    public static Field[] getFields(Object instance) {
        return getFields(instance.getClass());
    }

    public static Field[] getFields(Class clase) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(clase.getDeclaredFields()));
        fields.addAll(Arrays.asList(clase.getFields()));
        return fields.toArray(new Field[fields.size()]);
    }

    public static Field[] getAllFields(Class clase) {
        Set<Field> fields = new HashSet<>();

        while (clase != null) {
            fields.addAll(Arrays.asList(getFields(clase)));
            clase = clase.getSuperclass();
        }
        return fields.toArray(new Field[fields.size()]);
    }

    public static <T> T runMethod(Object intance, Method method, Object... args) {
        try {
            method.setAccessible(true);
            return (T) method.invoke(intance, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static <T> T runGetter(String fieldName, Object intance) {
        return runGetter(getField(intance, fieldName), intance);
    }

    public static <T> T runGetter(Field field, Object intance) {
        Method method;

        if (field.getType().equals(boolean.class)) {
            method = getMethod(intance, "is", field);
        } else {
            method = getMethod(intance, "get", field);
        }
        if (method == null) {
            throw new NoSuchMethodError(
                    String.format("El metodo get del atributo %s no fue encontrado", field.getName()));
        }

        return runMethod(intance, method);
    }

    public static boolean runSetter(String fieldName, Object intance, Object... args) {
        return runSetter(getField(intance, fieldName), intance, args);
    }

    public static boolean runSetter(Field field, Object instance, Object... args) {
        Method method = getMethod(instance, "set", field, getArgumentsClass(instance, field));

        if (method == null) {
            throw new NoSuchMethodError(
                    String.format("El metodo set del atributo %s no fue encontrado ", field.getName()));
        }
        return runMethod(instance, method, args) != null;
    }

    public static <T> T getFieldValue(Field f, Object instance) {
        try {
            f.setAccessible(true);
            return (T) f.get(instance);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            Logger.getLogger(ReflectionUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static boolean runSetterWithCast(Field field, Object instance, Object... values) {
        return ReflectionUtils.runSetter(field, instance,
                CastUtils.cast(ReflectionUtils.getArgumentsClass(instance, field), values));
    }

    public static Method getMethod(Object intance, String nameMethod) {
        return getMethod(intance.getClass(), nameMethod);
    }

    public static Method getMethod(Class clase, String nameMethod) {
        for (Method m : getMethods(clase)) {
            if (m.getName().equalsIgnoreCase(nameMethod)) {
                return m;
            }
        }
        if (clase.getSuperclass() != null) {
            return getMethod(clase.getSuperclass(), nameMethod);
        }
        throw new NoSuchFieldError(String.format("El metodo %s no fue encontrado", nameMethod));
    }

    public static Method getMethod(Object instance, String preffix, Field field, Class... args) {
        return getMethod(instance.getClass(), preffix, field, args);
    }

    public static Method getMethod(Class clase, String preffix, Field field, Class... args) {
        try {
            return clase.getMethod(preffix + StringUtils.capitalize(field.getName()), args);
        } catch (NoSuchMethodException | SecurityException ex) {
            try {
                return clase.getDeclaredMethod(preffix + StringUtils.capitalize(field.getName()), args);
            } catch (NoSuchMethodException | SecurityException ex1) {
                if (clase.getSuperclass() != null) {
                    getMethod(clase.getSuperclass(), preffix, field, args);
                }
            }
        }
        return null;
    }

    public static Method[] getMethods(Class clase) {
        List<Method> methods = new ArrayList<>();
        methods.addAll(Arrays.asList(clase.getDeclaredMethods()));
        methods.addAll(Arrays.asList(clase.getMethods()));
        return methods.toArray(new Method[methods.size()]);
    }

    public static Method[] getAllMethods(Class clase) {
        Set<Method> methods = new HashSet<>();

        while (clase != null) {
            methods.addAll(Arrays.asList(getMethods(clase)));
            clase = clase.getSuperclass();
        }
        return methods.toArray(new Method[methods.size()]);
    }

    public static Class[] getArgumentsClass(Object... args) {
        Class[] clases = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            clases[i] = args[i].getClass();
        }
        return clases;
    }

    public static Class[] getArgumentsClass(Object instance, Field field) {
        return getArgumentsClass(instance.getClass(), field);
    }

    public static Class[] getArgumentsClass(Class clase, Field field) {
        Method m = getMethod(clase, "set" + field.getName());
        if (m == null) {
            return null;
        }
        return m.getParameterTypes();
    }

    public static boolean containsAnnotation(Class clase, Class annotation) {
        return clase.isAnnotationPresent(annotation);
    }
}
