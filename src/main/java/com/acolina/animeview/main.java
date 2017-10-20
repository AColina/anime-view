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
package com.acolina.animeview;


import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author angel
 */
public class main {

    public static void main(String[] args) throws IOException {
        System.out.println("inicia");
//         try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
             System.out.println("cargando pagina");
        // Get the first page
//        final HtmlPage page1 = webClient.getPage("https://animeflv.net/ver/47369/just-because-3");
System.out.println("buscando body");
        // Get the form that we are dealing with and within that form, 
        // find the submit button and the field that we want to change.
//         HtmlDivision cap = page1.querySelector("div.Body .CpCnA");
//             System.out.println(cap);
//             System.out.println(cap.getElementsByTagName("h1").get(0));
//             System.out.println(cap.getElementsByTagName("h2").get(0));
       
        // Change the value of the text field
//        textField.setValueAttribute("root");

        // Now submit the form by clicking the button and get back the second page.
//        final HtmlPage page2 = button.click();
//    }

//        Element cap = doc.select("div.body .CpCnA").get(0);
//
//        String titulo = cap.select("h1.Title").get(0).text();
//        String capitulo = cap.select("h2.SubTitle").get(0).text();
//        List<String> opciones = new ArrayList<>();
//
//        for (Element el : cap.select(".CapiTnv li")) {
//
//        }
    }
}
