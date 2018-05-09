/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.kenh14;

import entity.CompareOperator;
import entity.Filter;
import entity.Filter.Conditions;
import entity.News;
import java.io.IOException;
import java.util.ArrayList;
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
public class GetKenh14_NewsDetail extends Thread {

    private ObjectModel<News> model = new ObjectModel<>(News.class);

    @Override
    public void run() {
        News news = new News();
        Filter filter = new Filter();

        Conditions condition1 = filter.new Conditions();
        condition1.setCompare(CompareOperator.EQUAL);
        condition1.setCompareValue("0");
        filter.addField("status", condition1);

        Conditions condition2 = filter.new Conditions();
        condition2.setCompare(CompareOperator.EQUAL);
        condition2.setCompareValue("http://kenh14.vn/");
        filter.addField("source", condition2);

        ArrayList<News> listNews = model.getList(news, filter,0,0);
        for (News n : listNews) {
            System.out.println(n.getTitle());
            getContent(n);
        }
    }

    private void getContent(News news) {
        String title = "";
        String content = "";
        String description = "";
        String author = "";
        StringBuilder image = new StringBuilder();

        try {
            Document doc = Jsoup.connect(news.getUrl()).get();
            if (news.getUrl().contains("/video/")) {
                // Trường hợp link là trang tin video.                
                content = doc.select("div.videoplayerDetail").attr("data-src");
                news.setContent(content);
                if (!content.isEmpty()) {
                    news.setStatus(1);
                }
                model.Update(news);
                return;
            }
            description = doc.select("h2.knc-sapo").text();
            title = doc.select("h1.kbwc-title").text();
            content = doc.select("div.knc-content").text();
            if (content.isEmpty()) {
                content = doc.select("div.VCSortableInPreviewMode").attr("data-src");
                System.err.println(content);
            }
            author = doc.select("div.knc-content p:last-child i").text();
            if (author.length() > 20) {
                author = "";
            }
            Elements eles = doc.select("div.knc-content img");
            for (Element ele : eles) {
                // Nối nhiều link ảnh khi link có nhiều ảnh.
                image.append(ele.attr("src")).append("*");
            }

            news.setAuthor(author);
            news.setContent(content);
            news.setTitle(title);
            news.setImage(image.toString());
            news.setDescription(description);
            if (!content.isEmpty() && !title.isEmpty()) {
                news.setStatus(1);
            }
            model.Update(news);
        } catch (IOException ex) {
            Logger.getLogger(GetKenh14_NewsDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
