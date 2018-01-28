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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
//    @Qualifier("serieService")
    SerieServiceImpl serieService;

    @RequestMapping(method = RequestMethod.GET,value = "/{id}")
    @ApiOperation(value = "Obtiene todos los datos del anime seleccionado")
    public @ResponseBody
    ResponseEntity<Serie> get(@PathVariable(value = "id", required = true) Integer id) throws Exception {
        Serie serie = serieService.findByIdSerie(id);
        return new ResponseEntity<>(serie, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.GET, value = "/emission")
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

    @RequestMapping(method = RequestMethod.GET, value = "/recent")
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

    @RequestMapping(method = RequestMethod.GET, value = "/search")
    @ApiOperation(value = "Obtiene una lista de series cargados recientemente")
    public @ResponseBody
    ResponseEntity<SearchSerieThumbnails> search(@RequestParam(value = "genders", required = false) String[] genders,
                                                 @RequestParam(value = "year", required = false) Integer[] year,
                                                 @RequestParam(value = "type", required = false) String[] type,
                                                 @RequestParam(value = "state", required = false) Integer[] state,
                                                 @RequestParam(value = "order", required = false, defaultValue = "default") String order,
                                                 @RequestParam(value = "page", required = false) Integer page
    ) throws Exception {
        try {
            Map<String, Object> params = new HashMap<>();
            if (Objects.nonNull(genders)) {
                params.put("genders[]", genders);
            }
            if (Objects.nonNull(year)) {
                params.put("year[]", year);
            }
            if (Objects.nonNull(type)) {
                params.put("type[]", type);
            }
            if (Objects.nonNull(state)) {
                params.put("status[]", state);
            }
            if (Objects.nonNull(order)) {
                params.put("order", order);
            }
            if (Objects.nonNull(page)) {
                params.put("page", page);
            }

            return new ResponseEntity<>(animeFlvDecoder.decodeSerieSearch(params), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(SerieController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }
}
