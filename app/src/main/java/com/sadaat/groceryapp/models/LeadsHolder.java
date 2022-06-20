package com.sadaat.groceryapp.models;

import java.util.ArrayList;

public class LeadsHolder {
    private ArrayList<LeadsModel> leads;
    private long session;

    public LeadsHolder() {
    }

    public LeadsHolder(ArrayList<LeadsModel> leads, long session) {
        this.leads = leads;
        this.session = session;
    }

    public ArrayList<LeadsModel> getLeads() {
        return leads;
    }

    public void setLeads(ArrayList<LeadsModel> leads) {
        this.leads = leads;
    }

    public long getSession() {
        return session;
    }

    public void setSession(long session) {
        this.session = session;
    }
}
