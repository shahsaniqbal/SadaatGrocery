package com.sadaat.groceryapp.models.Users;

import com.sadaat.groceryapp.models.AddressModel;

public class UserOtherDetailsModel {
    private AddressModel address;

    public UserOtherDetailsModel(AddressModel address) {
        this.address = address;
    }

    public AddressModel getAddress() {
        return address;
    }

    public void setAddress(AddressModel address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return address.toString();
    }
}
