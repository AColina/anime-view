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
package com.acolina.animeview.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author angel
 */
public class SerieThumbnails {

    private String url;
    private String urlImg;
    private String tittle;
    private SerieDescriptionThumbnails description;
    @JsonIgnore
    private String base64;

    public SerieThumbnails() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlImg() {
        return urlImg;
    }

    public void setUrlImg(String urlImg) {
        this.urlImg = urlImg;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }

    public SerieDescriptionThumbnails getDescription() {
        return description;
    }

    public void setDescription(SerieDescriptionThumbnails description) {
        this.description = description;
    }

}
