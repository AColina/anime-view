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
import com.acolina.animeview.model.dto.SearchSerieThumbnails;
import com.acolina.animeview.model.dto.SerieThumbnails;
import com.acolina.animeview.model.entity.Serie;
import com.acolina.animeview.services.impl.SerieServiceImpl;
import com.acolina.animeview.util.jsoup.AnimeFlvDecoder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * @author angel
 */
@RestController
@RequestMapping("/serie")
@Api(value = "serie", description = "Controlador rest para los serie")
public class SerieController {

    @Autowired
    AnimeFlvDecoder animeFlvDecoder;

    @Autowired
    SerieServiceImpl serieService;

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    @ApiOperation(value = "Obtiene todos los datos del anime seleccionado")
//    @Cacheable(value = "serie", key = "#id", unless = "#result == null")
    public @ResponseBody
    ResponseEntity<Serie> get(@PathVariable(value = "id", required = true) Integer id) throws Exception {
        Serie serie = serieService.findByIdSerie(id);
        return new ResponseEntity<>(serie, HttpStatus.OK);

    }

//    @RequestMapping(method = RequestMethod.GET, value = "/emission")
    @ApiOperation(value = "Obtiene una lista de series en emision")
    public @ResponseBody
    ResponseEntity<List<Serie>> emission() throws Exception {
        try {
            return new ResponseEntity<>(animeFlvDecoder.emissionSeriesThumbnails(), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(SerieController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
    }

//    @RequestMapping(method = RequestMethod.GET, value = "/recent")
    @ApiOperation(value = "Obtiene una lista de series cargados recientemente")
    public @ResponseBody
    ResponseEntity<List<SerieThumbnails>> recent() throws Exception {

        try {
            Document doc = Jsoup.connect(AppConfig.URL).get();
            return new ResponseEntity<>(animeFlvDecoder.decodeSeriesThumbnails(doc, "main .ListAnimes li"), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(SerieController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }


}
