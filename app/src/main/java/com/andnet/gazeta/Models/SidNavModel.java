package com.andnet.gazeta.Models;


public class SidNavModel {

    private String titile;
    private String titielDesc;
    private int background;
    private int imgRes;
    private int viewType;


    public SidNavModel(String titile, String titielDesc, int background, int imgRes, int viewType) {
        this.titile = titile;
        this.titielDesc = titielDesc;
        this.background = background;
        this.imgRes = imgRes;
        this.viewType = viewType;
    }

    public SidNavModel() {
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public String getTitile() {
        return titile;
    }

    public void setTitile(String titile) {
        this.titile = titile;
    }

    public String getTitielDesc() {
        return titielDesc;
    }

    public void setTitielDesc(String titielDesc) {
        this.titielDesc = titielDesc;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }
}
