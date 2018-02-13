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
import com.acolina.animeview.model.entity.Episode;
import com.acolina.animeview.model.entity.Serie;
import com.acolina.animeview.model.firebase.FSerie;
import com.acolina.animeview.services.EmailService;
import com.acolina.animeview.util.jsoup.AnimeFlvDecoder;
import com.acolina.animeview.util.mapper.algolia.AlgoliaMapper;
import com.algolia.search.APIClient;
import com.algolia.search.Index;
import com.algolia.search.exceptions.AlgoliaException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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
    Firestore firestore;

    @Autowired
    AnimeFlvDecoder animeFlvDecoder;

    @Autowired
    APIClient client;

    @Value("${collection.serie}")
    String collection;

    @Value("${collection.episodes}")
    String episodeCollection;

    @Value("${animeview.url.default}")
    public String URL;

    @Autowired
    EmailService emailService;

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


            DocumentSnapshot ds = firestore.collection(collection)
                    .document(episode.getIdSerie().toString())
                    .get()
                    .get();

            if (!ds.exists()) {

                Serie serie = saveSerie(episode.getUrlSerie());
                algoliaSave(index, serie);
            } else if (!validateExists(episode.getIdSerie(), ep.get_id())) {
                saveEpisode(ep.getUrl());
            } else {
                LOGGER.info("no hay animes que guardar");
            }
        }

    }

    private Serie saveSerie(String url) throws Exception {
        Serie serie = animeFlvDecoder.decodeSerie(url);
        Calendar c = Calendar.getInstance();
        serie.setYear(c.get(Calendar.YEAR));
        ObjectMapper mapper = new ObjectMapper();
        FSerie fs = mapper.readValue(mapper.writeValueAsBytes(serie), FSerie.class);
        firestore
                .collection(collection)
                .document(serie.get_id().toString())
                .set(fs);
        for (Episode e : serie.getEpisodes()) {
            saveEpisode(e.getUrl());
        }
        return serie;
    }

    private void saveEpisode(String url) throws Exception {
        Episode e = animeFlvDecoder.decodeEpisode(url);
        e.setCreationDate(getCurrentTime());
        firestore
                .collection(collection)
                .document(e.getIdSerie().toString())
                .collection(episodeCollection)
                .document(e.get_id().toString())
                .set(e);
        LOGGER.info(String.format("save anime %d-%s", e.get_id(), e.getTitle()));
    }

    private int getCurrentTime() {
        return (int) (new Date().getTime() / 1000);
    }

    private void algoliaSave(Index<ASerie> index, Serie serie) throws AlgoliaException {
        AlgoliaMapper mapper = new AlgoliaMapper();
        ASerie algoliaValue = mapper.map(serie);
        index.saveObject(serie.get_id().toString(), algoliaValue).waitForCompletion();
    }

    private boolean validateExists(Integer idSerie, Integer idEpisode) throws Exception {
        DocumentSnapshot ds = firestore.collection(collection)
                .document(idSerie.toString())
                .collection(episodeCollection)
                .document(idEpisode.toString())
                .get()
                .get();
        return ds.exists();
    }

    private boolean isLast(EpisodioThumbnails episodioThumbnails) {
        boolean last = true;
        if (Objects.isNull(recent) || !episodioThumbnails.getUrl().equalsIgnoreCase(recent.getUrl())) {
            last = false;
            recent = episodioThumbnails;
        }
        return last;

    }

    private Document getDocument() {
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
