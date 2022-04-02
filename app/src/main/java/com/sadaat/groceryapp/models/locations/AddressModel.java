package com.sadaat.groceryapp.models.locations;

public class AddressModel {
    private CityModel city;
    private AreaModel area;
    private String addressLine1;
    private String addressLine2;

    public AddressModel() {
        this.city = null;
        this.area = null;
        this.addressLine1 = "";
        this.addressLine2 = "";
    }

    public AddressModel(CityModel city, AreaModel area, String addressLine1) {
        this.city = city;
        this.area = area;
        this.addressLine1 = addressLine1;
        this.addressLine2 = "";
    }

    public AddressModel(CityModel city, AreaModel area, String addressLine1, String addressLine2) {
        this.city = city;
        this.area = area;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
    }

    public CityModel getCity() {
        return city;
    }

    public void setCity(CityModel city) {
        this.city = city;
    }

    public AreaModel getArea() {
        return area;
    }

    public void setArea(AreaModel area) {
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

        if (addressLine2.isEmpty()){
            return addressLine1 + ", "+
                    area + ", " + city + ", Pakistan";
        }

        else {
            return addressLine1 + ", " +
                    addressLine2 + ", " +
                    area + ", " + city + ", Pakistan";
        }
    }
}
