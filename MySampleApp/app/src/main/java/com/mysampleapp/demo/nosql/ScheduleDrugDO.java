package com.mysampleapp.demo.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Set;

@DynamoDBTable(tableName = "myfirstapp-mobilehub-1482957139-ScheduleDrug")

public class ScheduleDrugDO {
    private String _userId;
    private Set<String> _day;
    private String _drug;
    private Set<Double> _hour;
    private String _notes;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBAttribute(attributeName = "day")
    public Set<String> getDay() {
        return _day;
    }

    public void setDay(final Set<String> _day) {
        this._day = _day;
    }
    @DynamoDBAttribute(attributeName = "drug")
    public String getDrug() {
        return _drug;
    }

    public void setDrug(final String _drug) {
        this._drug = _drug;
    }
    @DynamoDBAttribute(attributeName = "hour")
    public Set<Double> getHour() {
        return _hour;
    }

    public void setHour(final Set<Double> _hour) {
        this._hour = _hour;
    }
    @DynamoDBAttribute(attributeName = "notes")
    public String getNotes() {
        return _notes;
    }

    public void setNotes(final String _notes) {
        this._notes = _notes;
    }

}
