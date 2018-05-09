/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.vnExpress;

import entity.News;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ObjectModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author dongvu
 */
public class GetVnexpress_IndexNews extends Thread {

    @Override
    public void run() {
        try {
            ObjectModel<News> model = new ObjectModel<>(News.class);
            HashSet<String> isUnique = new HashSet<>();
            Document doc = Jsoup.connect("https://vnexpress.net/").get();
            Elements elements = doc.getElementsByTag("a");
            String url = "";
            for (Element element : elements) {
                if (element.attr("href").matches(".*[0-9]{4,17}.html$") && isUnique.add(element.attr("href"))) {
                    url = element.attr("href");
                    if (!url.contains("https")) {
                        url = "https://vnexpress.net" + url;
                    }
                    News n = new News(url, element.attr("title"), "https://vnexpress.net/");
                    model.Insert(n);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GetVnexpress_IndexNews.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
