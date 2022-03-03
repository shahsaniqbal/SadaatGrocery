package com.sadaat.groceryapp.formatter;

public class Price {
    private static final String CURRENCY = "Rs";
    public static String format(double price){
        return CURRENCY + ". " + price;
    }
}
