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
package com.acolina.animeview.quartz;

import com.acolina.animeview.model.algolia.ASerie;
import com.acolina.animeview.model.dto.EpisodeTemp;
import com.acolina.animeview.model.dto.EpisodioThumbnails;
import com.acolina.animeview.model.entity.EpisodeEntity;
import com.acolina.animeview.model.entity.SerieEntity;
import com.acolina.animeview.repository.mongo.EpisodeMongoRepository;
import com.acolina.animeview.repository.mongo.SerieMongoRepository;
import com.acolina.animeview.services.EmailService;
import com.acolina.animeview.util.jsoup.AnimeFlvDecoder;
import com.algolia.search.APIClient;
import com.algolia.search.Index;
import com.algolia.search.exceptions.AlgoliaException;
import org.jsoup.Jsoup;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Angel Colina
 */
@Component("recentAnimeScan")
public class RecentAnimeScan {

    private static final Logger LOGGER = Logger.getLogger(RecentAnimeScan.class.getName());

    private static EpisodioThumbnails recent;

    @Autowired
    private AnimeFlvDecoder animeFlvDecoder;

    @Autowired
    private APIClient client;

    @Value("${collection.series}")
    private String collection;

    @Value("${collection.episodes}")
    private String episodeCollection;

    @Value("${animeview.url.default}")
    private String URL;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SerieMongoRepository serieMongoRepository;

    @Autowired
    private EpisodeMongoRepository episodeMongoRepository;

//    @Autowired
//    EpisodeRedisRepository repository;

    public void scan() {
//        System.out.println("scan");
        try {

            List<EpisodioThumbnails> eps = animeFlvDecoder
                    .decodeEpisodiosThumbnails(getDocument(), "main .ListEpisodios li", false);

            if (!eps.isEmpty() && isLast(eps.get(0))) {
                return;
            }

            upgrateSerieInToRepositorys(eps);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void upgrateSerieInToRepositorys(List<EpisodioThumbnails> eps) throws Exception {
        Index<ASerie> index = client.initIndex(collection, ASerie.class);
        for (EpisodioThumbnails ep : eps) {

            EpisodeTemp episode = animeFlvDecoder.decodeEpisodeTemp(ep.getUrl());

            SerieEntity se = serieMongoRepository.findOne(episode.getIdSerie());
            if (Objects.isNull(se)) {
                try {
                    SerieEntity serieEntity = saveSerie(episode.getUrlSerie());
                    algoliaSave(index, serieEntity);
                } catch (Exception ex) {
                    emailService.sendErrorMail("Error al procesar SerieEntity : %s", episode.getUrlSerie());
                    throw ex;
                }
            } else if (!validateExists(ep.get_id())) {
                saveEpisode(ep.getUrl());
            } else {
                LOGGER.info("no hay animes que guardar");
            }
        }

    }

    private SerieEntity saveSerie(String url) throws Exception {
        SerieEntity serieEntity = animeFlvDecoder.decodeSerie(url);
        serieEntity.setYear(LocalDate.now().getYear());
        serieMongoRepository.save(serieEntity);
        for (EpisodeEntity e : serieEntity.getEpisodeEntities()) {
            saveEpisode(e.getUrl());
        }
        return serieEntity;
    }

    private void saveEpisode(String url) throws Exception {
        EpisodeEntity e = animeFlvDecoder.decodeEpisode(url);
        e.setCreationDate(System.currentTimeMillis());
//        REpisode redisEpisode = new REpisode(e);
//        repository.save(redisEpisode);
        episodeMongoRepository.save(e);

        LOGGER.info(String.format("save anime %d-%s", e.get_id(), e.getTitle()));
    }

    private void algoliaSave(Index<ASerie> index, SerieEntity serieEntity) throws AlgoliaException {
        ModelMapper mapper = new ModelMapper();
        ASerie algoliaValue = mapper.map(serieEntity, ASerie.class);
        index.saveObject(serieEntity.get_id().toString(), algoliaValue).waitForCompletion();
    }

    private boolean validateExists(Integer idEpisode) {
        return Objects.nonNull(episodeMongoRepository.findOne(idEpisode));
    }

    private boolean isLast(EpisodioThumbnails episodioThumbnails) {
        boolean last = true;
        if (Objects.isNull(recent) || !episodioThumbnails.getUrl().equalsIgnoreCase(recent.getUrl())) {
            last = false;
            recent = episodioThumbnails;
        }
        return last;

    }

    private org.jsoup.nodes.Document getDocument() {
        try {
            return Jsoup.connect(URL).get();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "", e);
            emailService.sendErrorMail(e);
            System.exit(1);
        }
        throw new RuntimeException("");
    }

}
