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
package com.acolina.animeview.util.jsoup;

import com.acolina.animeview.model.dto.EpisodioThumbnails;
import com.acolina.animeview.model.dto.SerieDescriptionThumbnails;
import com.acolina.animeview.model.dto.SerieThumbnails;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

/**
 *
 * @author angel
 */
@Service
public class AnimeFlvDecoder {

    public List<EpisodioThumbnails> decodeEpisodiosThumbnails(Element element, String selector) throws Exception {
        return decodeEpisodiosThumbnails(element, selector, true);
    }

    public List<EpisodioThumbnails> decodeEpisodiosThumbnails(Element element, String selector, boolean defaultImage) throws Exception {
        List<EpisodioThumbnails> eps = new ArrayList<>();
        for (Element el : element.select(selector)) {
            eps.add(decodeEpisodioThumbnails(el, defaultImage));
        }
        return eps;
    }

    public EpisodioThumbnails decodeEpisodioThumbnails(Element element, boolean defaultImage) throws Exception {
        EpisodioThumbnails e = new EpisodioThumbnails();
        Element aTag = element.children().get(0);
        e.setUrl(aTag.attr("href"));
        String defaultStr = aTag.select("img").get(0).attr("src");
        e.setUrlImg(defaultImage
                ? defaultStr
                : "/uploads/animes/covers".concat(defaultStr.substring(defaultStr.indexOf("thumbs") + 6))
        );
        e.setEpisode(aTag.select(".Capi").get(0).text());
        e.setTittle(aTag.select(".Title").get(0).text());

        return e;
    }

    public List<SerieThumbnails> decodeSeriesThumbnails(Element element, String selector) throws Exception {
        List<SerieThumbnails> eps = new ArrayList<>();
        for (Element el : element.select(selector)) {
            eps.add(decodeSerieThumbnails(el));
        }
        return eps;
    }

    public SerieThumbnails decodeSerieThumbnails(Element element) throws Exception {
        SerieThumbnails e = new SerieThumbnails();
        Element aTag = element.select(".Anime a").get(0);
        e.setUrl(aTag.attr("href"));
        e.setUrlImg(aTag.select("img").get(0).attr("src"));
        e.setTittle(aTag.select(".Title").get(0).text());

        SerieDescriptionThumbnails d = new SerieDescriptionThumbnails();
        Element divTag = element.select(".Anime .Description").get(0);
        d.setRating(divTag.select("p .Vts").get(0).text());
        d.setText(divTag.select("p").get(1).text());
        d.setType(divTag.select("p .Type").get(0).text());
        e.setDescription(d);
        return e;
    }
}
