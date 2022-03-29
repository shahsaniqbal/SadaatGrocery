package com.sadaat.groceryapp.models;

import java.util.ArrayList;
import java.util.Arrays;

public class CategoriesModel {

    private String docID;
    private String title;
    private String description;
    private boolean isParent;
    private Boolean hasSubcategories;
    private ArrayList<CategoriesModel> subCategories;
    private String imageRef;

    public CategoriesModel() {

    }

/*    public CategoriesModel(String title, String description, Boolean isParent) {
        this.title = title;
        this.description = description;
        this.isParent = isParent;
        this.hasSubcategories = false;
        subCategories = new ArrayList<>();
    }

    public CategoriesModel(String docID, CategoriesModel modelWithoutID) {
        this.docID = docID;
        this.title = modelWithoutID.getTitle();
        this.description = modelWithoutID.getDescription();
        this.hasSubcategories = modelWithoutID.getHasSubcategories();
        this.subCategories = modelWithoutID.getSubCategories();
        this.isParent = modelWithoutID.isParent();

    }

    public CategoriesModel(String title, String description, Boolean isParent, Boolean hasSubcategories, ArrayList<CategoriesModel> subCategories) {
        this(title,description, hasSubcategories);
        this.hasSubcategories = hasSubcategories;
        this.subCategories = subCategories;
    }

    private CategoriesModel(String docID, String title, String description, Boolean hasSubcategories, ArrayList<CategoriesModel> subCategories) {
        this.docID = docID;
        this.title = title;
        this.description = description;
        this.hasSubcategories = hasSubcategories;
        this.subCategories = subCategories;
    }*/

    public CategoriesModel(String docID, String title, String description, boolean isParent, Boolean hasSubcategories, ArrayList<CategoriesModel> subCategories, String imageRef) {
        this.docID = docID;
        this.title = title;
        this.description = description;
        this.hasSubcategories = hasSubcategories;
        this.subCategories = new ArrayList<CategoriesModel>();
        if (subCategories != null){
            this.subCategories = subCategories;
        }
        else{
            this.subCategories = new ArrayList<CategoriesModel>();
        }
        this.isParent = isParent;
        this.imageRef = imageRef;
    }

    public CategoriesModel(String docID, CategoriesModel categoriesModel) {
        this(docID,
                categoriesModel.getTitle(),
                categoriesModel.getDescription(),
                categoriesModel.isParent(),
                categoriesModel.getHasSubcategories(),
                categoriesModel.getSubCategories(),
                categoriesModel.imageRef);
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


    public boolean isParent() {
        return isParent;
    }

    public void setParent(boolean parent) {
        isParent = parent;
    }

    @Override
    public String toString() {
        return title;
    }


    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public void setSubCategories(ArrayList<CategoriesModel> subCategories) {
        this.subCategories = (subCategories);
    }
}
