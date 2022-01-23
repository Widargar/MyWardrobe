package com.bilki.mywardrobe;

public class LookItems {

    private String clotheName, clotheImageUrl, clotheImageName, clotheDescription, clotheColor, clotheSize, clotheType, clotheSeason;

    public LookItems(){

    }

    public LookItems(String clotheName, String clotheImageUrl, String clotheImageName, String clotheDescription, String clotheColor, String clotheSize, String clotheType, String clotheSeason){

        this.clotheName = clotheName;
        this.clotheImageUrl = clotheImageUrl;
        this.clotheImageName = clotheImageName;
        this.clotheDescription = clotheDescription;
        this.clotheColor = clotheColor;
        this.clotheSize = clotheSize;
        this.clotheType = clotheType;
        this.clotheSeason = clotheSeason;

    }

    public LookItems(String clotheName, String clotheImageUrl){

        this.clotheName = clotheName;
        this.clotheImageUrl = clotheImageUrl;

    }

    public String getClotheName() {
        return clotheName;
    }

    public void setClotheName(String clotheName) {
        this.clotheName = clotheName;
    }

    public String getClotheImageUrl() {
        return clotheImageUrl;
    }

    public void setClotheImageUrl(String clotheImageUrl) {
        this.clotheImageUrl = clotheImageUrl;
    }

    public String getClotheImageName() {
        return clotheImageName;
    }

    public void setClotheImageName(String clotheImageName) {
        this.clotheImageName = clotheImageName;
    }

    public String getClotheDescription() {
        return clotheDescription;
    }

    public void setClotheDescription(String clotheDescription) {
        this.clotheDescription = clotheDescription;
    }

    public String getClotheColor() {
        return clotheColor;
    }

    public void setClotheColor(String clotheColor) {
        this.clotheColor = clotheColor;
    }

    public String getClotheSize() {
        return clotheSize;
    }

    public void setClotheSize(String clotheSize) {
        this.clotheSize = clotheSize;
    }

    public String getClotheType() {
        return clotheType;
    }

    public void setClotheType(String clotheType) {
        this.clotheType = clotheType;
    }

    public String getClotheSeason() {
        return clotheSeason;
    }

    public void setClotheSeason(String clotheSeason) {
        this.clotheSeason = clotheSeason;
    }
}
