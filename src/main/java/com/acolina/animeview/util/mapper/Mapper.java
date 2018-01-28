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
package com.acolina.animeview.util.mapper;


import com.acolina.animeview.model.entity.Entity;
import com.acolina.animeview.util.reflection.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Angel Colina
 */
public abstract class Mapper {

    public abstract <T> T getMapClass(Class enity);

    public <T> T map(Entity enity) {

        Object instance = getMapClass(enity.getClass());

        if (Objects.nonNull(instance)) {
            Field[] fields = ReflectionUtils.getFields(instance);
            for (Field field : fields) {
                Object value = ReflectionUtils.runGetter(field, enity);
                if (Objects.nonNull(value) && (value instanceof List)) {
                    List valueList = new ArrayList();
                    if (((List) value).size() > 0 && !((List) value).get(0).getClass().isAssignableFrom(Entity.class)) {
                        ReflectionUtils.runSetterWithCast(field, instance, value);
                        continue;
                    }
                    ((List) value)
                            .stream()
                            .map(e -> map((Entity) e))
                            .filter(e -> Objects.nonNull(e))
                            .forEach(e -> {
                                valueList.add(e);
                            });
                    ReflectionUtils.runSetterWithCast(field, instance, valueList);
                } else {
                    ReflectionUtils.runSetterWithCast(field, instance, value);
                }

            }
            return (T) instance;
        }

        return null;
    }
}
