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
package com.acolina.animeview.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author angel
 */
public class IOUtils {

    /**
     * Obtiene la imagen en b64 de cualquier url
     *
     * @param url Url de la imagen
     * @return str en base 64
     * @throws Exception
     */
    public String getImageB64(String url) throws Exception {
        URL urla = new URL(url);
        URLConnection conn = urla.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStream in = conn.getInputStream();

        String str64 = new String(Base64.encodeBase64(org.apache.commons.io.IOUtils.toByteArray(in)));
        return str64;
    }
}
