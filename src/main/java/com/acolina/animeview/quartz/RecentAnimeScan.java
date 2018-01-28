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

import com.acolina.animeview.config.AppConfig;
import com.acolina.animeview.model.dto.EpisodeTemp;
import com.acolina.animeview.model.dto.EpisodioThumbnails;
import com.acolina.animeview.model.entity.Episode;
import com.acolina.animeview.model.entity.Serie;
import com.acolina.animeview.util.jsoup.AnimeFlvDecoder;
import com.algolia.search.APIClient;
import com.algolia.search.Index;
import com.algolia.search.exceptions.AlgoliaException;
import com.algolia.search.objects.Query;
import com.algolia.search.responses.SearchResult;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Angel Colina
 */
@Component("recentAnimeScan")
public class RecentAnimeScan {

    private static EpisodioThumbnails recent;

    @Autowired
    Firestore firestore;

    @Autowired
    AnimeFlvDecoder animeFlvDecoder;

    @Autowired
    APIClient client;

    @Value("${animeFlv}")
    String collection;

    public void scan() {
        System.out.println("scan");
        try {
            Document doc = Jsoup.connect(AppConfig.URL).get();
            List<EpisodioThumbnails> eps = animeFlvDecoder.decodeEpisodiosThumbnails(doc, "main .ListEpisodios li", false);

            if (!eps.isEmpty() && isLast(eps.get(0))) {
                System.out.println("isLast");
                return;
            }

            Index<Serie> index = client.initIndex(collection, Serie.class);

            upgrateSerieInToRepositorys(eps, index);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void upgrateSerieInToRepositorys(List<EpisodioThumbnails> eps, Index<Serie> index) throws Exception {
        for (EpisodioThumbnails ep : eps) {

            if (validateWithAlgolia(index, ep.getUrl())) {
                EpisodeTemp temp = animeFlvDecoder.decodeEpisodeTemp(ep.getUrl());
//                    SearchResult<Serie> serie = index.search(q);
                DocumentReference docRef = firestore.collection(collection).document(temp.getIdSerie().toString());
                Serie serie = animeFlvDecoder.decodeSerie(temp.getUrlSerie());

                setCreationDateToRecenEpisode(serie, ep.getUrl());

                docRef.set(serie);
                index.saveObject(serie.getIdSerie().toString(), serie).waitForCompletion();
                System.out.println("Serie save : " + serie.getUrl());
            }
        }
    }

    private void setCreationDateToRecenEpisode(Serie serie, String url) {

        Episode episode = serie.getEpisodes()
                .stream()
                .filter(f -> f.getUrl().equals(url))
                .findFirst().get();

        episode.setCreationDate((int) (new Date().getTime() / 1000));
    }

    private boolean validateWithAlgolia(Index<Serie> index, String url) throws AlgoliaException {
        Query q = new Query(url);
        SearchResult<Serie> lst = index.search(q);
        return lst.getHits().size() == 0;
    }

    private boolean isLast(EpisodioThumbnails episodioThumbnails) {
        boolean last = true;
        if (Objects.isNull(recent) || !episodioThumbnails.getUrl().equalsIgnoreCase(recent.getUrl())) {
            last = false;
            recent = episodioThumbnails;
        }
        return last;

    }

    //
    public static void main(String[] args) throws Exception {
//        FirebaseConfig c = new FirebaseConfig();
        AnimeFlvDecoder animeFlvDecoder = new AnimeFlvDecoder();
        Episode e=animeFlvDecoder.decodeEpisode("/ver/48003/miira-no-kaikata-1");
        System.out.println(e);
//        try {
//            c.init();
//            CollectionReference cr = c.firebaseDatabse().collection("series");
//            List<DocumentSnapshot> ds = cr.get().get().getDocuments();
//
//            ds.stream()
//                    .map(e -> e.toObject(Serie.class))
//                    .filter(s -> s.getEpisodes().size() == 0 && !s.getState().equalsIgnoreCase("PROXIMAMENTE"))
////                    .map(s -> s.getUrl())
//                    .forEach((s) -> {
//                        try {
////                            Serie s = animeFlvDecoder.decodeSerie(url);
//                            System.out.println("tosave : " + s.getUrl());
//                            System.out.println("episodes : " + s.getEpisodes().size());
//                            APIClient client = new ApacheAPIClientBuilder("BX5J5YSPTR", "8102ee35d4b9098ad3e8fe8d186e89c8").build();
//                            Index<Serie> index = client.initIndex("series", Serie.class);
//                            DocumentReference docRef = c.firebaseDatabse().collection("series").document(s.getId().toString());
//                            ApiFuture<WriteResult> result = docRef.update("episodes", s.getEpisodes());
//                            ApiFuture<WriteResult> result = docRef.set(s);
//                            index.saveObject(s.getId().toString(), s).waitForCompletion();
//                            System.out.println(s.getTitle() + " Update time : " + result.get().getUpdateTime());

//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    });
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }

}
