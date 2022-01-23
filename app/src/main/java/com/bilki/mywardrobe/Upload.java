package com.bilki.mywardrobe;

public class Upload {

    private String mName, mDescription, mImageUrl, mColor, mSize, mType, mSeason, mImageName;

    public Upload(){

    }

    public Upload(String name, String imageUrl, String imageName, String description, String color, String size, String type, String season){

        if (name.trim().equals("")){

            name = "No Name";

        }

        mDescription = description;
        mName = name;
        mImageUrl = imageUrl;
        mImageName = imageName;
        mColor = color;
        mSize = size;
        mType = type;
        mSeason = season;

    }

    public Upload(String imageUrl){

        mImageUrl = imageUrl;

    }

    public Upload(String imageUrl, String name){

        mImageUrl = imageUrl;
        mName = name;

    }



    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String mColor) {
        this.mColor = mColor;
    }

    public String getSize() {
        return mSize;
    }

    public void setSize(String mSize) {
        this.mSize = mSize;
    }

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    public String getSeason() {
        return mSeason;
    }

    public void setSeason(String mSeason) {
        this.mSeason = mSeason;
    }

    public String getImageName() {
        return mImageName;
    }

    public void setImageName(String mImageName) {
        this.mImageName = mImageName;
    }
}
