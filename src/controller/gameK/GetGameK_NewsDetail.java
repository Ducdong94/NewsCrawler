/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller.gameK;

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
public class GetGameK_NewsDetail extends Thread {

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
        condition2.setCompareValue("http://gamek.vn/");
        filter.addField("source", condition2);

        ArrayList<News> listNews = model.getList(news, filter,0,0);
        for (News n : listNews) {
            System.out.println(n.getUrl());
            getContent(n);
       
        }
    }

    private void getContent(News news) {
        try {
            Document doc = Jsoup.connect(news.getUrl()).get();
            String description = "";
            String title = "";
            String content = "";
            StringBuilder image = new StringBuilder();

            title = doc.select(".topdetail h1").text();
            description = doc.select("div.rightdetail > h2").text();
            content = doc.select(".rightdetail_content").text();
            Elements els = doc.select(".rightdetail_content img");
            for (Element el : els) {
                image.append(el.attr("src")).append("*");
            }
            news.setContent(content);
            news.setTitle(title);
            news.setImage(image.toString());
            news.setDescription(description);
            if (!content.isEmpty() && !title.isEmpty()) {
                news.setStatus(1);
            }

//            System.err.println("========================");
//            System.out.println(news.getUrl());
//            System.out.println("Title: " + news.getTitle());
//            System.out.println("Author: " + news.getAuthor());
//            System.out.println("Content: " + news.getContent());
//            System.out.println("Description: " + news.getDescription());
//            System.out.println("Image: " + news.getImage());
//            System.out.println("Source: " + news.getSource());
//            System.out.println("Status: " + news.getStatus());
            model.Update(news);
        } catch (IOException ex) {
            Logger.getLogger(GetGameK_NewsDetail.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
