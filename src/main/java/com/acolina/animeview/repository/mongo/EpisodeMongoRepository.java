/*
 * Copyright (C) 2018 Pivotal Software, Inc.
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
package com.acolina.animeview.repository.mongo;

import com.acolina.animeview.model.entity.EpisodeEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author Angel Colina
 * @version 1.0
 */
@Repository
@Transactional
public interface EpisodeMongoRepository extends AbstractMongoRepository<EpisodeEntity> {

    EpisodeEntity findBy_id(Integer id);

    EpisodeEntity findByCreationDateBetween(Long firstDate, Long lastDate);

    Set<EpisodeEntity> findByCreationDateGreaterThanEqualOrderByCreationDate(Long date);

}
