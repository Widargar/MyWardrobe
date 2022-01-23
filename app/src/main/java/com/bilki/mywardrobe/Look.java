package com.bilki.mywardrobe;

public class Look {

    private String lookName, lookDescription, lookImageName, lookImageUrl;

    Look(){



    }

    Look(String lookName, String lookDescription, String lookImageName, String lookImageUrl){

        this.lookName = lookName;
        this.lookDescription = lookDescription;
        this.lookImageName = lookImageName;
        this.lookImageUrl = lookImageUrl;

    }

    Look(String lookImageUrl){
        
        this.lookImageUrl = lookImageUrl;

    }

    Look(String lookImageName, String lookImageUrl){

        this.lookImageName = lookImageName;
        this.lookImageUrl = lookImageUrl;

    }

    public String getLookName() {
        return lookName;
    }

    public void setLookName(String lookName) {
        this.lookName = lookName;
    }

    public String getLookDescription() {
        return lookDescription;
    }

    public void setLookDescription(String lookDescription) {
        this.lookDescription = lookDescription;
    }

    public String getLookImageName() {
        return lookImageName;
    }

    public void setLookImageName(String lookImageName) {
        this.lookImageName = lookImageName;
    }

    public String getLookImageUrl() {
        return lookImageUrl;
    }

    public void setLookImageUrl(String lookImageUrl) {
        this.lookImageUrl = lookImageUrl;
    }
}
