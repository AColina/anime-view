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

import com.acolina.animeview.model.dto.response.ResponseDTO;
import com.acolina.animeview.model.entity.EpisodeEntity;
import com.acolina.animeview.services.EpisodeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Angel Colina
 * @version 1.0
 */
@RestController
@RequestMapping("/episode")
@Api(value = "episode", description = "Controlador rest para los episodios")
public class EpisodiosController {


    @Autowired
    private EpisodeService service;

    @RequestMapping(value = "/{idEpisode}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<ResponseDTO<EpisodeEntity>> getEpisode(@PathVariable(required = true, value = "idEpisode") Integer id) throws Exception {
        return ResponseDTO.ok(service.findById(id));

    }

}
