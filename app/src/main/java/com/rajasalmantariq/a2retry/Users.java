package com.rajasalmantariq.a2retry;

public class Users {
    String name;
    String image;
    String status;
    String thumbnail;

    public String getThumb() {
        return thumbnail;
    }

    public void setThumb(String thumb) {
        this.thumbnail = thumb;
    }


    Users(){

    }

    public Users(String name, String image, String status, String thumb) {
        this.name = name;
        this.image = image;
        this.status = status;
        this.thumbnail = thumb;
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
