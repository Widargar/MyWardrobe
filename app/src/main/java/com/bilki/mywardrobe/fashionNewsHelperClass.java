package com.bilki.mywardrobe;

public class fashionNewsHelperClass {

    int image;
    String name, article;

    public fashionNewsHelperClass(int image, String name, String article){

        this.image = image;
        this.name = name;
        this.article = article;

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

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }
}
