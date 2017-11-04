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
import com.acolina.animeview.model.dto.Serie;
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

    public Serie decodeSerie(String url) throws Exception {

        Document doc = Jsoup.connect(AppConfig.URL.concat(url)).get();
        Serie s = new Serie();
        Element side = doc.select(".Body div.Container aside").first();

        s.setUrlFront(side.select(".AnimeCover img").first().attr("src"));
        s.setState(side.select("p.AnmStts span").first().text());

        Element f = doc.select(".Body .Ficha ").first();
        
        s.setBackgroundImage(f.select("div.Bg").first().attr("style").replace("background-image:url(", "").replace(")", ""));

        Element ficha = doc.select(".Body .Ficha div.Container").first();
        s.setTitle(ficha.select(".Title").text());
        s.setType(ficha.select(".Type").text());

        Element main = doc.select(".Main").first();

        main.select(".Nvgnrs a").forEach((genero) -> {
            s.getGenders().add(genero.text());
        });

        s.setSynopsis(main.select(".WdgtCn .Description p").first().text());

        main.select(".WdgtCn .ListCaps li.fa-play-circle a").forEach((Element atag) -> {
            Serie.Episode epe = new Serie.Episode();

            epe.setTitle(atag.select(".Title").first().text());
            if (atag.attr("href").equals("#")) {
                epe.setDate(atag.select(".Date").first().text());
                s.setNextEpisode(epe);
            } else {

                epe.setName(atag.select("p").first().text());
                epe.setUrl(atag.attr("href"));
                s.getEpisodes().add(epe);
            }
        });

        return s;
    }
}
