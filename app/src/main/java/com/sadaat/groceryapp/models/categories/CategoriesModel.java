package com.sadaat.groceryapp.models.categories;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CategoriesModel {

    private String docID;
    private String title;
    private String description;
    private ArrayList<SubCategoriesModel> subCategories;
    private String imageRef;

    public CategoriesModel() {

    }

    public CategoriesModel(String docID, String title, String description, ArrayList<SubCategoriesModel> subCategories, String imageRef) {
        this.docID = docID;
        this.title = title;
        this.description = description;
        if (subCategories != null) {
            this.subCategories = subCategories;
        } else {
            this.subCategories = new ArrayList<>();
        }
        this.imageRef = imageRef;
    }

    public CategoriesModel(String docID, CategoriesModel categoriesModel) {
        this(
                docID,
                categoriesModel.getTitle(),
                categoriesModel.getDescription(),
                categoriesModel.getSubCategories(),
                categoriesModel.imageRef);
    }


    // Custom Getters and Setters // Code By Ahsan Sheikh // 24-12-2021

    public int getSubCategoriesCount() {

        if (subCategories == null) {
            return -1;
        }

        return subCategories.size();
    }


    @NonNull
    @Override
    public String toString() {
        return title;
    }


    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
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

    public ArrayList<SubCategoriesModel> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(ArrayList<SubCategoriesModel> subCategories) {
        this.subCategories = subCategories;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }
}
