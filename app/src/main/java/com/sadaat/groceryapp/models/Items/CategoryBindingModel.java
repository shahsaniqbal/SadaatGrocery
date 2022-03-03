package com.sadaat.groceryapp.models.Items;

public class CategoryBindingModel {
    private String docIDCategory;
    private String subCategoryID;

    public CategoryBindingModel() {
    }

    public CategoryBindingModel(String docIDCategory, String subCategoryID) {
        this.docIDCategory = docIDCategory;
        this.subCategoryID = subCategoryID;
    }

    public String getDocIDCategory() {
        return docIDCategory;
    }

    public void setDocIDCategory(String docIDCategory) {
        this.docIDCategory = docIDCategory;
    }

    public String getSubCategoryID() {
        return subCategoryID;
    }

    public void setSubCategoryID(String subCategoryID) {
        this.subCategoryID = subCategoryID;
    }
}
