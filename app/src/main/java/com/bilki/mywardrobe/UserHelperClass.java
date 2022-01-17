package com.bilki.mywardrobe;

public class UserHelperClass {

    String name, surname, email, birthday, gender, phone, password, imageUrl, imageName;

    public UserHelperClass(){}

    public UserHelperClass(String name, String surname, String email, String birthday, String gender, String phone, String password) {

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.phone = phone;
        this.password = password;

    }

    public UserHelperClass(String name, String surname, String email, String birthday, String gender, String phone) {

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.phone = phone;

    }

    public UserHelperClass(String name, String surname, String email, String birthday, String gender, String phone, String imageUrl, String imageName){

        this.name = name;
        this.surname = surname;
        this.email = email;
        this.birthday = birthday;
        this.gender = gender;
        this.phone = phone;
        this.imageUrl = imageUrl;
        this.imageName = imageName;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
