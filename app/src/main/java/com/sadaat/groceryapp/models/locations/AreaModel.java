package com.sadaat.groceryapp.models.locations;

public class AreaModel {
    private String id;
    private String name;

    public AreaModel() {
        id = "";
        name = "";
    }

    public AreaModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public AreaModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
