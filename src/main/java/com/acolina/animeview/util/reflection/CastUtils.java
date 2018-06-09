/*
 * Copyright (C) 2017 Pivotal Software, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.acolina.animeview.util.reflection;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Angel Colina
 * @version 1.0
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class CastUtils {

    public static Object[] cast(Class[] toCast, Object... params) {

        Object objects[] = new Object[params.length];
        for (int i = 0; i < objects.length; i++) {
            String value;
            if (params[i] instanceof String) {
                value = (String) params[i];
            } else if (params[i] instanceof Number) {
                objects[i] = castToNumber(params[i]);
                continue;
            } else if ((params[i] instanceof Collection) || (params[i] instanceof Map)) {
                objects[i] = castList(params[i], toCast[i]);
                continue;
            } else {
                objects[i] = params[i];
                continue;
            }
            // CAST TO String
            if (toCast[i].equals(String.class)) {
                objects[i] = value;
            } else // CAST TO CHAR
                if (Character.class.isAssignableFrom(toCast[i]) || char.class.isAssignableFrom(toCast[i])) {
                    objects[i] = value.length() > 0 ? value.charAt(0) : ' ';
                } else // CAST TO BOOLEAN
                    if (toCast[i].getSimpleName().equalsIgnoreCase(boolean.class.getSimpleName())) {
                        objects[i] = Boolean.valueOf(value);
                    } else // CAST TO NUMBER
                        if (((ClassUtils.isPrimitiveOrWrapper(toCast[i]) && !toCast[i].equals(boolean.class))
                                || Number.class.isAssignableFrom(toCast[i]))) {

                            try {
                                String typeName = toCast[i].getSimpleName().equals("Integer") ? "Int"
                                        : StringUtils.capitalize(toCast[i].getSimpleName());

                                Method m = NumberUtils.class.getDeclaredMethod("to" + typeName, String.class);
                                m.setAccessible(true);
                                objects[i] = castToNumber(ReflectionUtils.runMethod(new NumberUtils(), m, value));

                            } catch (NoSuchMethodException | SecurityException ex) {
                                Logger.getLogger(CastUtils.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
        }

        return objects;
    }

    public static Object castList(Object object, Class clase) {

        if ((object instanceof Map) && (List.class.isAssignableFrom(clase))) {
            object = new ArrayList<>(((Map) object).values());
        } else if ((object instanceof Collection) && (List.class.isAssignableFrom(clase))) {
            object = new ArrayList<>((Collection) object);
        }

        return object;
    }

    public static Number castToNumber(Object o) {

        if (o == null) {
            return null;
        }
        Number numero = (Number) o;
        if (numero.intValue() == 0) {
            return 0;
        }
        if (numero instanceof Byte) {
            return numero.byteValue();
        } else if (numero instanceof Short) {
            return numero.shortValue();
        } else if (numero instanceof Integer) {
            return numero.intValue();
        } else if (numero instanceof Long) {
            return numero.longValue();
        } else if (numero instanceof Float) {
            return numero.floatValue();
        } else if (numero instanceof Double) {
            return numero.doubleValue();
        } else {
            return null;
        }
    }

    public static Number castToNumber(Number o) {

        if (o == null) {
            return null;
        }
        Number numero = (Number) o;
        if (numero.intValue() == 0) {
            return 0;
        }
        if (numero.intValue() < Byte.MAX_VALUE && numero.intValue() > Byte.MIN_VALUE) {
            return numero.byteValue();
        } else if (numero.intValue() < Short.MAX_VALUE && numero.intValue() > Short.MIN_VALUE) {
            return numero.shortValue();
        } else if (numero.longValue() < Integer.MAX_VALUE && numero.longValue() > Integer.MIN_VALUE) {
            return numero.intValue();
        } else if (numero.floatValue() < Long.MAX_VALUE && numero.floatValue() > Long.MIN_VALUE) {
            return numero.longValue();
        } else if (numero.doubleValue() < Float.MAX_VALUE && numero.doubleValue() > Float.MIN_VALUE) {
            return numero.floatValue();
        } else {
            return numero.doubleValue();
        }
    }
}
