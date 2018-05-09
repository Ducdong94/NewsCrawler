/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import customAnnotation.Entity;
import customAnnotation.MyId;

/**
 *
 * @author dongvu
 * @since 04/2018
 * @version 1.0
 */
@Entity(tableName = "testEntity")
public class News {

    @MyId
    private String url;
    private String title;
    private String author;
    private String description;
    private String content;
    private String image;
    private String source;
    private int status;

    public News() {
    }

    public News(String url, String title, String source) {
        this.url = url;
        this.title = title;
        this.source = source;
        this.status = 0;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getStatus() {
        return status;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
