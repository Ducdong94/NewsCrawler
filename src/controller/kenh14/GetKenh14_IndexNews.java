/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.kenh14;

import entity.News;
import java.io.IOException;
import java.util.HashSet;
import model.ObjectModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author dongvu
 */
public class GetKenh14_IndexNews extends Thread {

    @Override
    public void run() {

        HashSet<String> isUnique = new HashSet<>();
        ObjectModel<News> model = new ObjectModel<>(News.class);
        String url = "";
        try {
            Document doc = Jsoup.connect("http://kenh14.vn/").get();
            Elements elements = doc.getElementsByTag("a");
            for (Element element : elements) {
                if (element.attr("href").matches(".*[0-9]{4,17}.chn$") && isUnique.add(element.attr("href"))) {
                    url = element.attr("href");
                    if (!url.contains("http")) {
                        url = "http://kenh14.vn" + url;
                    }
                    News n = new News(url, element.attr("title"), "http://kenh14.vn/");
                    model.Insert(n);
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
