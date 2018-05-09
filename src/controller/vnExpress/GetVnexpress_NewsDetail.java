/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.vnExpress;

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
public class GetVnexpress_NewsDetail extends Thread {

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
        condition2.setCompareValue("https://vnexpress.net/");
        filter.addField("source", condition2);

        ArrayList<News> listNews = model.getList(news, filter, 0, 0);
        for (News n : listNews) {
            getContent(n);
        }
    }

    private void getContent(News news) {
        try {
            Document doc = Jsoup.connect(news.getUrl()).get();
            String description = doc.select("h2.description").text();
            String title = "";
            String content = "";
            String author = "";
            StringBuilder image = new StringBuilder();

            if (news.getUrl().contains("video.vnexpress.net")) {
                content = doc.getElementsByTag("video").attr("src");
                title = doc.select("#info_inner h1").text();
                description = doc.select("#info_inner .lead_detail").text();
                news.setContent(content);
                news.setTitle(title);
                news.setDescription(description);
                if (!content.isEmpty() && !title.isEmpty()) {
                    news.setStatus(1);
                }
                model.Update(news);
                return;
            }
            if (description.isEmpty()) {
                // Góc nhìn.
                System.err.println("goc nhin");
                title = doc.select("h1.title_gn_detail").text();
                content = doc.getElementsByClass("fck_detail").text();
                image.append(doc.select("#article_detail > section.sidebar_1 > div > div > a > img").attr("src"));
                author = doc.select("#article_detail > section.sidebar_1 > div > p > a").text();
            } else {
                // Các chuyên mục khác.
                title = doc.select("h1.title_news_detail").text();
                content = doc.getElementsByClass("fck_detail").get(0).text();
                Elements elements = doc.getElementsByClass("fck_detail").get(0).getElementsByTag("img");
                for (Element element : elements) {
                    image.append(element.attr("src")).append("*");
                }
                author = doc.select("body > section.container > section.wrap_sidebar_12 > section.sidebar_1 > article > p:last-child > strong").text();
            }
            if (author.isEmpty()) {
                author = doc.select("body > section.container > section.wrap_sidebar_12 > section.sidebar_1 > p > strong").text();
            }

            News n = new News();
            n.setUrl(news.getUrl());
            n.setAuthor(author);
            n.setContent(content);
            n.setTitle(title);
            n.setImage(image.toString());
            n.setDescription(description);
            if (!content.isEmpty() && !title.isEmpty()) {
                n.setStatus(1);
            }
            model.Update(n);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
