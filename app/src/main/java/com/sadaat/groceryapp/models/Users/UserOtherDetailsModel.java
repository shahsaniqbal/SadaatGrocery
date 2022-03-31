package com.sadaat.groceryapp.models.Users;

import com.sadaat.groceryapp.models.locations.AddressModel;

public class UserOtherDetailsModel {
    private String imageReference;
    private AddressModel address;

    public UserOtherDetailsModel() {
    }

    public UserOtherDetailsModel(String imageReference, AddressModel address) {
        this.imageReference = imageReference;
        this.address = address;
    }

    public UserOtherDetailsModel(AddressModel address) {
        this.address = address;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    public String getImageReference() {
        return imageReference;
    }

    public void setImageReference(String imageReference) {
        this.imageReference = imageReference;
    }

    @Override
    public String toString() {
        return imageReference.toString();
    }
}
