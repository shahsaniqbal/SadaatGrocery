package com.sadaat.groceryapp.models;

public class AddressModel {
    private String city;
    private String area;
    private String addressLine1;
    private String addressLine2;

    public AddressModel(String city, String area, String addressLine1) {
        this.city = city;
        this.area = area;
        this.addressLine1 = addressLine1;
        this.addressLine2 = "";
    }

    public AddressModel(String city, String area, String addressLine1, String addressLine2) {
        this.city = city;
        this.area = area;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @Override
    public String toString() {
        return addressLine1 + "\n" +
                addressLine2 + "\n" +
                area + ", " + city + ", Pakistan";
    }
}
