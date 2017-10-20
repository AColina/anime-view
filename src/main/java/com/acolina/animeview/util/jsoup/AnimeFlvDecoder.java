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

import com.acolina.animeview.config.AppConfig;
import com.acolina.animeview.model.dto.EpisodioThumbnails;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

/**
 *
 * @author angel
 */
@Service
public class AnimeFlvDecoder {

    public List<EpisodioThumbnails> decodeEpisodiosThumbnails(Element element, String selector) throws Exception {
        List<EpisodioThumbnails> eps = new ArrayList<>();
        for (Element el : element.select(selector)) {
            eps.add(decodeEpisodioThumbnails(el));
        }
        return eps;
    }

    public EpisodioThumbnails decodeEpisodioThumbnails(Element element) throws Exception {
        EpisodioThumbnails e = new EpisodioThumbnails();
        Element aTag = element.children().get(0);
        e.setUrl(aTag.attr("href"));
        e.setUrlImg(aTag.select("img").get(0).attr("src"));
        e.setCapitulo(aTag.select(".Capi").get(0).text());
        e.setTitulo(aTag.select(".Title").get(0).text());

        URL urla = new URL(AppConfig.URL.concat(e.getUrlImg()));
        URLConnection conn = urla.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream in = conn.getInputStream();

        String str64 = new String(Base64.encodeBase64(IOUtils.toByteArray(in)));
        e.setBase64(str64);
        return e;
    }
}
