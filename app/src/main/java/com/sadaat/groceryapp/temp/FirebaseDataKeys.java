package com.sadaat.groceryapp.temp;

public class FirebaseDataKeys {
    final static public String STORAGE_BUCKET_ADDRESS = "gs://sadaat-grocery-store.appspot.com";

    public String getMenuRef(){
        return "Categories";
    }
    public String getUsersRef(){
        return "Users";
    }
    public String getItemsRef() { return "Items"; }
    public String getLocationsRef() {
        return "Locations";
    }
    public String getOrdersRef() {
        return "Orders";
    }
    public String getComplaintsRef() {
        return "OrderComplaints";
    }
    public String getSuggestionsRef() {
        return "Suggestions";
    }
    public String getTopSellingRef() {
        return "TopSellingItems";
    }
    public String getSlidesRef() {
        return "Slides";
    }
    public String getLeadsRef() {
        return "Leads";
    }
}
