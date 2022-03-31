package com.sadaat.groceryapp.models.locations;

import java.util.ArrayList;

public class CityModel {
    private String id;
    private String name;

    private ArrayList<AreaModel> areas;

    public CityModel() {
        id = "";
        name = "";
        areas = new ArrayList<>();
    }

    public CityModel(String id, String name, ArrayList<AreaModel> areas) {
        this.id = id;
        this.name = name;
        this.areas = areas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<AreaModel> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<AreaModel> areas) {
        this.areas = areas;
    }

    @Override
    public String toString() {
        return name;
    }
}
