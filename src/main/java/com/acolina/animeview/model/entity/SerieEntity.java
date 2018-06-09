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
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Angel Colina
 * @version 1.0
 */
@Document(collection = "series")
public class SerieEntity extends Entity implements IEntity {

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
    private @Transient
    List<EpisodeEntity> episodeEntities;
    private Integer year;

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

    public List<EpisodeEntity> getEpisodeEntities() {
        if (Objects.isNull(episodeEntities)) {
            episodeEntities = new ArrayList<>();
        }
        return episodeEntities;
    }

    public void setEpisodeEntities(List<EpisodeEntity> episodeEntities) {
        this.episodeEntities = episodeEntities;
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
