/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.gameK;

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
public class GetGameK_IndexNews extends Thread {

    @Override
    public void run() {
        ObjectModel<News> model = new ObjectModel<>(News.class);
        try {
            HashSet<String> isUnique = new HashSet<>();
            Document doc = Jsoup.connect("http://gamek.vn/").get();
            Elements elements = doc.getElementsByTag("a");
            String url = "";
            for (Element element : elements) {
                System.out.println(element.attr("href"));
                if (element.attr("href").matches(".*[0-9]{4,17}.chn$") && isUnique.add(element.attr("href"))) {
                    url = element.attr("href");
                    if (!url.contains("http")) {
                        url = "http://gamek.vn" + url;
                    }
                    News n = new News(url, element.attr("title"), "http://gamek.vn/");
                    model.Insert(n);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GetGameK_IndexNews.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
