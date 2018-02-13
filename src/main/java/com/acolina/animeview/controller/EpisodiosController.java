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

import com.acolina.animeview.model.dto.EpisodioThumbnails;
import com.acolina.animeview.model.redis.REpisode;
import com.acolina.animeview.services.EpisodeService;
import com.acolina.animeview.util.jsoup.AnimeFlvDecoder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author angel
 */

@RestController
@RequestMapping("/episode")
@Api(value = "episode", description = "Controlador rest para los episodios")
public class EpisodiosController {

    @Value("${animeview.url.default}")
    public String URL;

    @Autowired
    AnimeFlvDecoder animeFlvDecoder;

    @Autowired
    EpisodeService service;

    //    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<List<EpisodioThumbnails>> getEpisodio(@RequestParam(required = true, value = "url") String Url) throws Exception {

        try {
            Document doc = Jsoup.connect(URL).get();
            return new ResponseEntity<>(animeFlvDecoder.decodeEpisodiosThumbnails(doc, "main .ListEpisodios li"), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(EpisodiosController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/recent")
    @ApiOperation(value = "Obtiene una lista de episodios cargados recientemente")
    public @ResponseBody
    ResponseEntity<List<REpisode>> recent() throws Exception {

        List<REpisode> list = service.findEpisodeRecent();

        return new ResponseEntity<>(list, HttpStatus.OK);

    }
    @RequestMapping(method = RequestMethod.GET, value = "/")
    @ApiOperation(value = "Obtiene una lista de episodios cargados recientemente")
    public @ResponseBody
    ResponseEntity<Iterable<REpisode>> getAll() throws Exception {

        Iterable<REpisode> list = service.findAll();

        return new ResponseEntity<>(list, HttpStatus.OK);

    }

}
