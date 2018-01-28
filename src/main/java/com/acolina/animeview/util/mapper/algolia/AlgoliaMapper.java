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
package com.acolina.animeview.util.mapper.algolia;


import com.acolina.animeview.util.mapper.Mapper;
import com.acolina.animeview.util.reflection.ReflectionUtils;

/**
 * @author Angel Colina
 */
public class AlgoliaMapper extends Mapper {

    @Override
    public <T> T getMapClass(Class enity) {
        return ReflectionUtils
                .newInstance(String.format("com.acolina.animeview.migrator.model.algolia.A%s", enity.getSimpleName()));
    }
}
