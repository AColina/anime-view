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

import com.acolina.animeview.controller.sync.AnimeFlvSynchronized;
import com.acolina.animeview.model.entity.EpisodeEntity;
import com.acolina.animeview.model.redis.REpisode;
import com.acolina.animeview.repository.mongo.EpisodeMongoRepository;
import com.acolina.animeview.repository.redis.EpisodeRedisRepository;
import com.acolina.animeview.services.EpisodeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.time.temporal.ChronoUnit.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Angel Colina
 * @version 1.0
 */
@Service
public class EpisodeServiceImpl implements EpisodeService {

    private ModelMapper mapper = new ModelMapper();

    @Value("${redis.refres_cache.episode}")
    private Integer refreshTime;
    @Value("${episode_min.to_scan}")
    private Integer episodeMin;

    @Autowired
    private EpisodeMongoRepository repository;

    @Autowired
    private EpisodeRedisRepository redisRepository;

    @Override
    public EpisodeEntity findById(Integer id) {
        return repository.findOne(id);
    }

    @Override
    public Set<REpisode> findRecent() {

        long time = LocalDateTime.now()
                .minusDays(7)
                .atZone(ZoneId.of(ZoneId.SHORT_IDS.get("PST")))
                .toInstant()
                .toEpochMilli();

        Stream<REpisode> stream = StreamSupport.stream(redisRepository.findAll().spliterator(), false);
        Set<REpisode> redisEpisodes = stream.filter(e -> e.getCreationDate() > time)
                .collect(Collectors.toSet());

        if (validRedisData(redisEpisodes.size())) {
            Set<EpisodeEntity> entities = repository.findByCreationDateGreaterThanEqualOrderByCreationDate(time);
            AnimeFlvSynchronized synchronize = AnimeFlvSynchronized.getInstance();
            synchronize.setLastUpdateEpisode(time);
            if (entities.size() != redisEpisodes.size()) {

                Set<REpisode> noneMatch = findUnSaveEpisodes(entities, redisEpisodes);
                redisRepository.save(noneMatch);
                redisEpisodes.addAll(noneMatch);
            }

        }

        return redisEpisodes.stream()
                .sorted(Comparator.comparingLong(REpisode::getCreationDate))
                .collect(Collectors.toSet());
    }

    private boolean validRedisData(int dataSize) {
        AnimeFlvSynchronized syncronice = AnimeFlvSynchronized.getInstance();
        long hours = LocalDateTime.now()
                .until(LocalDateTime
                        .ofInstant(Instant
                                .ofEpochMilli(syncronice.getLastUpdateEpisode()), ZoneId.of(ZoneId.SHORT_IDS.get("PST"))), HOURS);
        return hours >= refreshTime || dataSize <= episodeMin;
    }

    private Set<REpisode> findUnSaveEpisodes(Set<EpisodeEntity> entities, Set<REpisode> redisEpisodes) {
        Stream<REpisode> stream = redisEpisodes.parallelStream();

        return entities.stream()
                .filter(e -> stream.noneMatch(r -> Objects.equals(e.get_id(), r.get_id())))
                .map(e -> mapper.map(e, REpisode.class))
                .collect(Collectors.toSet());
    }
}
