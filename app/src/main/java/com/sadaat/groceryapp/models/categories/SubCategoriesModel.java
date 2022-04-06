package com.sadaat.groceryapp.models.categories;

public class SubCategoriesModel {

    private String docID;
    private String title;
    private String description;
    private String imageRef;

    public SubCategoriesModel() {

    }

    public SubCategoriesModel(String docID, String title, String description, String imageRef) {
        this.docID = docID;
        this.title = title;
        this.description = description;
        this.imageRef = imageRef;
    }

    public SubCategoriesModel(String docID, SubCategoriesModel subCategoryModel) {
        this(
                docID,
                subCategoryModel.getTitle(),
                subCategoryModel.getDescription(),
                subCategoryModel.imageRef);
    }


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

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }
}
