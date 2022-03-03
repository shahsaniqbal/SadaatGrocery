package com.sadaat.groceryapp.models;

import java.util.ArrayList;

public class CategoriesModel {

    private String docID;
    private String title;
    private String description;
    private Boolean hasSubcategories;
    private ArrayList<CategoriesModel> subCategories;

    public CategoriesModel() {
    }

    public CategoriesModel(String title, String description) {
        this.title = title;
        this.description = description;
        this.hasSubcategories = false;
        subCategories = new ArrayList<>();

    }

    public CategoriesModel(String docID, CategoriesModel modelWithoutID) {
        this.title = modelWithoutID.getTitle();
        this.description = modelWithoutID.getDescription();
        this.hasSubcategories = modelWithoutID.getHasSubcategories();
        this.subCategories = modelWithoutID.getSubCategories();

    }

    public CategoriesModel(String title, String description, Boolean hasSubcategories, ArrayList<CategoriesModel> subCategories) {
        this.title = title;
        this.description = description;
        this.hasSubcategories = hasSubcategories;
        this.subCategories = subCategories;
    }

    public CategoriesModel(String docID, String title, String description) {
        this.docID = docID;
        this.title = title;

        if (description==null){
            this.description = "";

        }
        else{
            this.description = description;

        }

        this.hasSubcategories = false;
        this.subCategories = new ArrayList<>();
    }

    public CategoriesModel(String docID, String title, String description, Boolean hasSubcategories, ArrayList<CategoriesModel> subCategories) {
        this.docID = docID;
        this.title = title;
        this.description = description;
        this.hasSubcategories = hasSubcategories;
        this.subCategories = subCategories;
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

    public ArrayList<CategoriesModel> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(Boolean ifHasSubcategories, ArrayList<CategoriesModel> subCategories) {
        this.subCategories = subCategories;
        hasSubcategories = ifHasSubcategories;
    }

    public Boolean getHasSubcategories() {
        return hasSubcategories;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    // Custom Getters and Setters // Code By Ahsan Sheikh // 24-12-2021

    public int getSubCategoriesCount(){

        if (subCategories == null) {
            return -1;
        }

        return subCategories.size();
    }



    @Override
    public String toString() {
        return title;
    }


}
