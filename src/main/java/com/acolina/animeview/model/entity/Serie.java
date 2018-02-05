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
package com.acolina.animeview.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * @author Angel Colina
 */
@Document(collection = "animeflv")
public class Serie extends Entity {

    @Id
    public String _id;
    @Indexed(unique = true)
    private Integer idSerie;
    private String url;
    private String title;
    private String urlFront;
    private String synopsis;
    private List<String> genders;
    private List<Links> links;
    private String state;
    private String type;
    private String backgroundImage;
    private String rating;
    private NextEpisode nextEpisode;
    @JsonIgnore
    private List<Episode> episodes;
    private Integer year;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getIdSerie() {
        return idSerie;
    }

    public void setIdSerie(Integer idSerie) {
        this.idSerie = idSerie;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlFront() {
        return urlFront;
    }

    public void setUrlFront(String urlFront) {
        this.urlFront = urlFront;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public List<String> getGenders() {
        if (Objects.isNull(genders)) {
            genders = new ArrayList<>();
        }
        return genders;
    }

    public void setGenders(List<String> genders) {
        this.genders = genders;
    }

    public List<Links> getLinks() {
        if (Objects.isNull(links)) {
            links = new ArrayList<>();
        }
        return links;
    }

    public void setLinks(List<Links> links) {
        this.links = links;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<Episode> getEpisodes() {
        if (Objects.isNull(episodes)) {
            episodes = new ArrayList<>();
        }
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        this.episodes = episodes;
    }

    public NextEpisode getNextEpisode() {
        return nextEpisode;
    }

    public void setNextEpisode(NextEpisode nextEpisode) {
        this.nextEpisode = nextEpisode;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
