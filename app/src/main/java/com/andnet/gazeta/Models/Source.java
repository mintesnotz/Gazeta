package com.andnet.gazeta.Models;


public class Source {

    private String name;
    private String link;
    private String logo;
    private String image;
    private boolean allowed;

    public Source(String name, String link, String logo, String image, boolean allowed) {
        this.name = name;
        this.link = link;
        this.logo = logo;
        this.image = image;
        this.allowed = allowed;
    }

    public Source() {}

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public boolean isAllowed() {
        return allowed;
    }

    public void setAllowed(boolean allowed) {
        this.allowed = allowed;
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
