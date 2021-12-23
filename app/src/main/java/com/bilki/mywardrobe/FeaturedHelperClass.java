package com.bilki.mywardrobe;


public class FeaturedHelperClass {

    int image;
    String name;
    String url;


    public FeaturedHelperClass(){

    }

    public FeaturedHelperClass(int image, String name) {
        this.image = image;
        this.name = name;
    }


    public FeaturedHelperClass(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
