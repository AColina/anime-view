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

import com.acolina.animeview.model.redis.REpisode;
import com.acolina.animeview.repository.EpisodeRedisRepository;
import com.acolina.animeview.services.EpisodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class EpisodeServiceImpl implements EpisodeService {

    @Autowired
    EpisodeRedisRepository repository;

    @Override
    public List<REpisode> findByCreationDateLessThan(Long creationDate) {

        Stream<REpisode> stream = StreamSupport.stream(repository.findAll().spliterator(), false);
        List<REpisode> lst = stream.filter(e -> e.getCreationDate() < creationDate)
                .collect(Collectors.toList());
        return lst;
    }

    @Override
    public List<REpisode> findEpisodeRecent() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);
        return findByCreationDateLessThan(c.getTimeInMillis() / 1000);
    }
    public Iterable<REpisode> findAll(){
        return repository.findAll();
    }
}
