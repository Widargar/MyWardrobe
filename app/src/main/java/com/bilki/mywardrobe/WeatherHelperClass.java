package com.bilki.mywardrobe;

public class WeatherHelperClass {

    String time, temperature, icon, windSpeed, condition;

//    public WeatherHelperClass(String time, String temperature, String icon, String windSpeed) {
//        this.time = time;
//        this.temperature = temperature;
//        this.icon = icon;
//        this.windSpeed = windSpeed;
//    }

    public WeatherHelperClass(String time, String temperature, String icon, String condition) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
        this.condition = condition;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
