package com.andnet.gazeta.Models;

import java.io.Serializable;



public class Library implements Serializable{

    private String name;
    private String link;
    private String search_term;
    private String image;

    public Library(String name, String type, String link, String search_term, String image) {
        this.name = name;
        this.link = link;
        this.search_term = search_term;
        this.image = image;
    }

    public Library() {
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSearch_term() {
        return search_term;
    }

    public void setSearch_term(String search_term) {
        this.search_term = search_term;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
