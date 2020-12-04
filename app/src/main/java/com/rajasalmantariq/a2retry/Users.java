package com.rajasalmantariq.a2retry;

public class Users {
    String name;
    String image;
    String status;
    String thumbnail;
    String number;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getThumb() {
        return thumbnail;
    }

    public void setThumb(String thumb) {
        this.thumbnail = thumb;
    }


    Users(){

    }

    public Users(String name, String image, String status, String thumb, Long number) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumbnail = thumb;
        this.number=Long.toString(number);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }
}
