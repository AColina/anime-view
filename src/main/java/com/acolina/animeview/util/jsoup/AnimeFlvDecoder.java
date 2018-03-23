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
import com.acolina.animeview.model.dto.*;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.acolina.animeview.model.entity.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Angel Colina
 */
@Service
public class AnimeFlvDecoder {

    @Autowired
    private RestTemplate rt;

    @Value("${animeview.url.default}")
    public  String URL;

    private Pattern idPattern = Pattern.compile("\\/\\d+\\/");

    private Integer getId(String url) {
        Matcher matcher = idPattern.matcher(url);
        matcher.find();
        String id = matcher.group();
        return Integer.parseInt(id.substring(1, id.length() - 1));
    }

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
        e.setTitle(aTag.select(".Title").get(0).text());
        e.set_id(getId(e.getUrl()));

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
        e.setTitle(aTag.select(".Title").get(0).text());

        SerieDescriptionThumbnails d = new SerieDescriptionThumbnails();
        Element divTag = element.select(".Anime .Description").get(0);
        d.setRating(divTag.select("p .Vts").get(0).text());
        d.setText(divTag.select("p").get(1).text());
        d.setType(divTag.select("p .Type").get(0).text());
        e.setDescription(d);
        return e;
    }

    public List<Serie> emissionSeriesThumbnails() throws Exception {
        List<Serie> eps = new ArrayList<>();
        Document doc = Jsoup.connect(URL).get();
        for (Element el : doc.select(".Sidebar .Emision ul.ListSdbr li")) {
            Element aTag = el.child(0);
            Serie e = decodeSerie(aTag.attr("href"));
            eps.add(e);
        }

        return eps;
    }

    public Serie decodeSerie(String url) throws Exception {

        Document doc = Jsoup.connect(URL.concat(url)).get();
        Serie s = new Serie();
        s.setUrl(url);
        s.set_id(getId(url));

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

            String title = atag.select(".Title").first().text();
            if (atag.attr("href").equals("#")) {
                NextEpisode epe = new NextEpisode();
                epe.setTitle(title);
                epe.setDate(atag.select(".Date").first().text());
                s.setNextEpisode(epe);
            } else {
                try {
                    Episode epe = new Episode();
                    epe.setUrl(atag.attr("href"));
//                Episode epe = decodeEpisode(atag.attr("href"));
                    epe.setTitle(title);
                    epe.setName(atag.select("p").first().text());
                    s.getEpisodes().add(epe);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        main.select(".WdgtCn .ListEpisodes li a").forEach((Element atag) -> {
            Episode epe = null;
            try {
                epe = new Episode();
                epe.setUrl(atag.attr("href"));
//                epe = decodeEpisode(atag.attr("href"));
                epe.setTitle(atag.text());
                s.getEpisodes().add(epe);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        doc.select(".Main .ListAnmRel li").forEach((Element liTag) -> {
            Links li = new Links();

            li.setTitle(liTag.select("a").first().text());

            li.setText(liTag.text());
            li.setUrl(liTag.select("a").first().attr("href"));
            li.set_id(getId(li.getUrl()));
            s.getLinks().add(li);
        });
        return s;
    }

    public EpisodeTemp decodeEpisodeTemp(String url) throws Exception {

        Document doc = Jsoup.connect(URL.concat(url)).get();
        EpisodeTemp ep = new EpisodeTemp();
        Element container = doc.select(".Body div.Container .CpCn").first();

        Element capOptions = container.select(".CpCnB .CapOptns").first();
        ep.setUrlSerie(capOptions.select(".CapNv .CapNvLs").first().getAllElements().last().attr("href"));
        ep.setIdSerie(getId(ep.getUrlSerie()));

        return ep;
    }

    public Episode decodeEpisode(String url) throws Exception {

        Document doc = Jsoup.connect(URL.concat(url)).get();
        Episode ep = new Episode();
        Element container = doc.select(".Body div.Container .CpCn").first();
        ep.setUrl(url);
        ep.set_id(getId(url));

        Elements eles = doc.select("script");
        String script = eles.get(eles.size() - 3).html();
        ep.setName(container.select(".CpCnA .CapiTop .SubTitle").text());
        Elements servers = container.select(".CpCnA .CapiTnv li");

        Element capOptions = container.select(".CpCnB .CapOptns").first();
        String urlSerie = capOptions.select(".CapNv").first().getAllElements().last().attr("href");
        ep.setIdSerie(getId(urlSerie));

        servers.forEach(li -> {
            Video video = new Video();
            video.setServer(li.attr("title"));
            video.setOption(Integer.parseInt(li.select("a span").text()));
            String format = String.format("video[%d] = ", video.getOption() - 1);
            int indexOf = script.indexOf(format);
            String urlAll = script.substring(indexOf + format.length() + 1, script.indexOf(";", indexOf));
            int httpIndex = urlAll.indexOf("http");
            if (httpIndex == -1) {
                httpIndex = urlAll.indexOf("//");
            }
            video.setUrlVideo(urlAll.substring(httpIndex, urlAll.indexOf("\"", httpIndex)));
            ep.getVideos().add(video);
        });

        return ep;
    }

    public SearchSerieThumbnails decodeSerieSearch(Map<String, Object> urlParam) throws Exception {
        SearchSerieThumbnails seah = new SearchSerieThumbnails();
        StringBuilder url = new StringBuilder(URL.concat("/browse?"));
        for (String key : urlParam.keySet()) {

            Object value = urlParam.get(key);
            if (value instanceof Number) {
                value = ((Number) value).toString();
            }
            if (value instanceof String[]) {
                for (String v : (String[]) value) {
                    url.append(String.format("%s=%s&", URLEncoder.encode(key, "UTF-8"), v));
                }
            } else if (value instanceof Integer[]) {
                for (Integer v : (Integer[]) value) {
                    url.append(String.format("%s=%d&", URLEncoder.encode(key, "UTF-8"), v));
                }
            } else {
                url.append(String.format("%s=%s&", URLEncoder.encode(key, "UTF-8"), value));
            }
        }
        url.deleteCharAt(url.length() - 1);
        ResponseEntity<String> res = rt.exchange(new URI(url.toString()), HttpMethod.GET, null, String.class);

        Document doc = Jsoup.parse(res.getBody());
        seah.setSeries(decodeSeriesThumbnails(doc, ".Body .CpCnA .Main .ListAnimes li"));
        seah.setCurrentPage(Integer.parseInt(doc.select(".Body .Main .NvCnAnm .pagination .active a").first().text()));
        Elements e = doc.select(".Body .Main .NvCnAnm .pagination li a");

        seah.setMaxPage(Integer.parseInt(e.get(e.size() - 2).text()));
        if (seah.getCurrentPage() > 1) {
            seah.setLastPage(seah.getCurrentPage() - 1);
        }
        return seah;
    }
}
