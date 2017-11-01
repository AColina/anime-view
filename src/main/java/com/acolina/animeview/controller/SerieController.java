/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    @RequestMapping(method = RequestMethod.GET, value = "toDay")
    public @ResponseBody
    ResponseEntity<List<SerieThumbnails>> findDay() throws Exception {

        try {
            Document doc = Jsoup.connect(AppConfig.URL).get();
            return new ResponseEntity<>(animeFlvDecoder.decodeSeriesThumbnails(doc, "main .ListAnimes li"), HttpStatus.OK);
        } catch (Exception ex) {
            Logger.getLogger(EpisodiosController.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }

    }
}
