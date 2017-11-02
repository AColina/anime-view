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
package com.acolina.animeview.controller;

import com.acolina.animeview.config.AppConfig;
import com.acolina.animeview.model.dto.SerieThumbnails;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author angel
 */
@RestController
@RequestMapping("/serie/")
public class SerieController {

    @Autowired
    AnimeFlvDecoder animeFlvDecoder;

    @RequestMapping(method = RequestMethod.GET, value = "recent")
    public @ResponseBody
    ResponseEntity<List<SerieThumbnails>> recent() throws Exception {

        try {
            Document doc = Jsoup.connect(AppConfig.URL).get();
            return new ResponseEntity<>(animeFlvDecoder.decodeSeriesThumbnails(doc, "main .ListAnimes li"), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(EpisodiosController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "emission")
    public @ResponseBody
    ResponseEntity<List<SerieThumbnails>> emission() throws Exception {
        try {
            return new ResponseEntity<>(animeFlvDecoder.emissionSeriesThumbnails(), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(EpisodiosController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }
}
