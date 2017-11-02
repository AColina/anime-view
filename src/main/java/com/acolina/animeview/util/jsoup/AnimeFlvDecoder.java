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
package com.acolina.animeview.util.jsoup;

import com.acolina.animeview.config.AppConfig;
import com.acolina.animeview.model.dto.EpisodioThumbnails;
import com.acolina.animeview.model.dto.SerieDescriptionThumbnails;
import com.acolina.animeview.model.dto.SerieThumbnails;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

    public List<SerieThumbnails> emissionSeriesThumbnails() throws Exception {
        List<SerieThumbnails> eps = new ArrayList<>();
        Document doc = Jsoup.connect(AppConfig.URL).get();
        doc.select(".Sidebar .Emision ul.ListSdbr li").stream().map((el) -> {
            SerieThumbnails e = new SerieThumbnails();
            Element aTag = el.child(0);
            e.setUrl(aTag.attr("href"));
//            e.setUrlImg(aTag.select("img").get(0).attr("src"));
            e.setTittle(aTag.text());
            return e;
        }).forEachOrdered((e) -> {
            eps.add(e);
        });
        return eps;
    }
}
