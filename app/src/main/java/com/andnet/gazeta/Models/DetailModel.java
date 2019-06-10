package com.andnet.gazeta.Models;



public class DetailModel {


    private String value;
    private int type;
    private News news;
    private String audio_cover_image;

    public DetailModel(int type, String value) {
        this.value = value;
        this.type = type;
    }

    public DetailModel() {
    }

    public String getAudio_cover_image() {
        return audio_cover_image;
    }

    public void setAudio_cover_image(String audio_cover_image) {
        this.audio_cover_image = audio_cover_image;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public News getNews() {
        return news;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
