/*
 * Copyright 2017 ECM Solutions.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acolina.animeview.controller;

import com.acolina.animeview.config.AppConfig;
import com.acolina.animeview.model.dto.EpisodioThumbnails;
import com.acolina.animeview.util.jsoup.AnimeFlvDecoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author angel
 */
@RestController
@RequestMapping("/episode/")
public class EpisodiosController {

    @Autowired
    AnimeFlvDecoder animeFlvDecoder;

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<EpisodioThumbnails>> getEpisodio(@RequestParam(required = true, value = "url") String Url) throws Exception {

        try {
            Document doc = Jsoup.connect(AppConfig.URL).get();
            return new ResponseEntity<>(animeFlvDecoder.decodeEpisodiosThumbnails(doc, "main .ListEpisodios li"), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(EpisodiosController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "recent")
    public @ResponseBody
    ResponseEntity<List<EpisodioThumbnails>> findDay() throws Exception {

        try {
            Document doc = Jsoup.connect(AppConfig.URL).get();
            return new ResponseEntity<>(animeFlvDecoder.decodeEpisodiosThumbnails(doc, "main .ListEpisodios li", false), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(EpisodiosController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }

}
