/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
