package com.sadaat.groceryapp.models;

public class SliderModel {
    String id;
    String title;
    String description;
    String imageLink;

    public SliderModel() {
    }

    public SliderModel(String id, String title, String description, String imageLink) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageLink = imageLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
}
