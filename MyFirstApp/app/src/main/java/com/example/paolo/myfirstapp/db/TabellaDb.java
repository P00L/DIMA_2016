package com.example.paolo.myfirstapp.db;

/**
 * Created by paolo on 01/04/2016.
 */
public class TabellaDb {

    private String column_1;
    private String column_2;
    private int id;

    public TabellaDb(String column_1, String column_2, int id) {
        this.column_1 = column_1;
        this.column_2 = column_2;
        this.id = id;
    }

    public String getColumn_1() {
        return column_1;
    }

    public void setColumn_1(String column_1) {
        this.column_1 = column_1;
    }

    public String getColumn_2() {
        return column_2;
    }

    public void setColumn_2(String column_2) {
        this.column_2 = column_2;
    }

    public int getIds() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
