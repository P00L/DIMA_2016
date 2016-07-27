package com.mysampleapp.demo.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "myfirstapp-mobilehub-1482957139-Doctor")

public class DoctorDO {
    private String _userId;
    private String _email;
    private Boolean _active;
    private String _address;
    private String _name;
    private Double _phoneNumber;
    private String _surname;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "email")
    @DynamoDBAttribute(attributeName = "email")
    public String getEmail() {
        return _email;
    }

    public void setEmail(final String _email) {
        this._email = _email;
    }
    @DynamoDBAttribute(attributeName = "active")
    public Boolean getActive() {
        return _active;
    }

    public void setActive(final Boolean _active) {
        this._active = _active;
    }
    @DynamoDBAttribute(attributeName = "address")
    public String getAddress() {
        return _address;
    }

    public void setAddress(final String _address) {
        this._address = _address;
    }
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }
    @DynamoDBAttribute(attributeName = "phoneNumber")
    public Double getPhoneNumber() {
        return _phoneNumber;
    }

    public void setPhoneNumber(final Double _phoneNumber) {
        this._phoneNumber = _phoneNumber;
    }
    @DynamoDBAttribute(attributeName = "surname")
    public String getSurname() {
        return _surname;
    }

    public void setSurname(final String _surname) {
        this._surname = _surname;
    }

}
