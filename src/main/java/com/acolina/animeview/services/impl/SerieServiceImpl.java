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
package com.acolina.animeview.services.impl;

import com.acolina.animeview.model.entity.SerieEntity;
import com.acolina.animeview.repository.mongo.SerieMongoRepository;
import com.acolina.animeview.services.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Angel Colina
 * @version 1.0
 */
@Service
public class SerieServiceImpl implements SerieService {

    @Autowired
    private SerieMongoRepository repository;


    @Override
    public SerieEntity findById(Integer id) {
        return repository.findOne(id);
    }
}
