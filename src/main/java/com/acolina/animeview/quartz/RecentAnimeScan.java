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
import com.acolina.animeview.config.FirebaseConfig;
import com.acolina.animeview.model.dto.EpisodeTemp;
import com.acolina.animeview.model.dto.EpisodioThumbnails;
import com.acolina.animeview.model.entity.Serie;
import com.acolina.animeview.util.jsoup.AnimeFlvDecoder;
import com.algolia.search.APIClient;
import com.algolia.search.ApacheAPIClientBuilder;
import com.algolia.search.Index;
import com.algolia.search.exceptions.AlgoliaException;
import com.algolia.search.objects.IndexSettings;
import com.algolia.search.objects.Query;
import com.algolia.search.responses.SearchResult;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component("recentAnimeScan")
public class RecentAnimeScan {

    @Autowired
    Firestore firestore;
    @Autowired
    AnimeFlvDecoder animeFlvDecoder;

    public void scan() {
        System.out.println("scan");
        try {
            Document doc = Jsoup.connect(AppConfig.URL).get();
            List<EpisodioThumbnails> eps = animeFlvDecoder.decodeEpisodiosThumbnails(doc, "main .ListEpisodios li", false);
            APIClient client = new ApacheAPIClientBuilder("BX5J5YSPTR", "8102ee35d4b9098ad3e8fe8d186e89c8").build();
            Index<Serie> index = client.initIndex("series", Serie.class);

            for (EpisodioThumbnails ep : eps) {

                Query q = new Query(ep.getUrl());
                SearchResult<Serie> lst = index.search(q);
                if (lst.getHits().size() == 0) {
                    EpisodeTemp temp = animeFlvDecoder.decodeEpisode(ep.getUrl());
//                    SearchResult<Serie> serie = index.search(q);
                    DocumentReference docRef = firestore.collection("series").document(temp.getIdSerie().toString());
                    Serie serie = animeFlvDecoder.decodeSerie(temp.getUrlSerie());
                    docRef.set(serie);
                    index.saveObject(serie.getId().toString(), serie).waitForCompletion();
                    System.out.println("update : " + serie.getTitle());

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //
    public static void main(String[] args) {
        FirebaseConfig c = new FirebaseConfig();
        AnimeFlvDecoder animeFlvDecoder = new AnimeFlvDecoder();
        try {
            c.init();
            CollectionReference cr = c.firebaseDatabse().collection("series");
            List<DocumentSnapshot> ds = cr.get().get().getDocuments();

            ds.stream()
                    .map(e -> e.toObject(Serie.class))
                    .filter(s -> s.getEpisodes().size() == 0 && !s.getState().equalsIgnoreCase("PROXIMAMENTE"))
//                    .map(s -> s.getUrl())
                    .forEach((s) -> {
                        try {
//                            Serie s = animeFlvDecoder.decodeSerie(url);
                            System.out.println("tosave : " + s.getUrl());
                            System.out.println("episodes : " + s.getEpisodes().size());
//                            APIClient client = new ApacheAPIClientBuilder("BX5J5YSPTR", "8102ee35d4b9098ad3e8fe8d186e89c8").build();
//                            Index<Serie> index = client.initIndex("series", Serie.class);
//                            DocumentReference docRef = c.firebaseDatabse().collection("series").document(s.getId().toString());
//                            ApiFuture<WriteResult> result = docRef.update("episodes", s.getEpisodes());
//                            ApiFuture<WriteResult> result = docRef.set(s);
//                            index.saveObject(s.getId().toString(), s).waitForCompletion();
//                            System.out.println(s.getTitle() + " Update time : " + result.get().getUpdateTime());

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        AnimeFlvDecoder a = new AnimeFlvDecoder();
//        try {
//            Serie s = a.decodeSerie("/anime/4990/dragon-ball-super");
//            System.out.println("caps : " + s.getEpisodes().size());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
