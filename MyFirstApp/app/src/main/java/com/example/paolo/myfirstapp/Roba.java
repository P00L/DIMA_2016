package com.example.paolo.myfirstapp;

import java.util.Date;

/**
 * Created by paolo on 30/03/2016.
 */
public class Roba {
    private String titolo;
    private String category;
    private Date date;

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
