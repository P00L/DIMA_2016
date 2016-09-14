package com.mysampleapp.nosqldb.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Set;

@DynamoDBTable(tableName = "myfirstapp-mobilehub-1482957139-User")

public class UserDO {
    private String _userId;
    private String _email;
    private String _address;
    private Set<Double> _birthdate;
    private String _name;
    private Double _phone;
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
    @DynamoDBAttribute(attributeName = "address")
    public String getAddress() {
        return _address;
    }

    public void setAddress(final String _address) {
        this._address = _address;
    }
    @DynamoDBAttribute(attributeName = "birthdate")
    public Set<Double> getBirthdate() {
        return _birthdate;
    }

    public void setBirthdate(final Set<Double> _birthdate) {
        this._birthdate = _birthdate;
    }
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }
    @DynamoDBAttribute(attributeName = "phone")
    public Double getPhone() {
        return _phone;
    }

    public void setPhone(final Double _phone) {
        this._phone = _phone;
    }
    @DynamoDBAttribute(attributeName = "surname")
    public String getSurname() {
        return _surname;
    }

    public void setSurname(final String _surname) {
        this._surname = _surname;
    }

}
