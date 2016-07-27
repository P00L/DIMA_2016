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

@DynamoDBTable(tableName = "myfirstapp-mobilehub-1482957139-Drug")

public class DrugDO {
    private String _userId;
    private String _name;
    private Double _minqty;
    private String _notes;
    private Double _quantity;
    private String _type;
    private Double _weight;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "name")
    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return _name;
    }

    public void setName(final String _name) {
        this._name = _name;
    }
    @DynamoDBAttribute(attributeName = "minqty")
    public Double getMinqty() {
        return _minqty;
    }

    public void setMinqty(final Double _minqty) {
        this._minqty = _minqty;
    }
    @DynamoDBAttribute(attributeName = "notes")
    public String getNotes() {
        return _notes;
    }

    public void setNotes(final String _notes) {
        this._notes = _notes;
    }
    @DynamoDBAttribute(attributeName = "quantity")
    public Double getQuantity() {
        return _quantity;
    }

    public void setQuantity(final Double _quantity) {
        this._quantity = _quantity;
    }
    @DynamoDBAttribute(attributeName = "type")
    public String getType() {
        return _type;
    }

    public void setType(final String _type) {
        this._type = _type;
    }
    @DynamoDBAttribute(attributeName = "weight")
    public Double getWeight() {
        return _weight;
    }

    public void setWeight(final Double _weight) {
        this._weight = _weight;
    }

}
