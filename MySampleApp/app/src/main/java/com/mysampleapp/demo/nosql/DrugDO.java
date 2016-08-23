package com.mysampleapp.demo.nosql;

import android.os.Parcel;
import android.os.Parcelable;

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

public class DrugDO implements Parcelable {
    private String _userId;
    private String _name;
    private Double _minqty;
    private String _notes;
    private Double _quantity;
    private String _type;
    private Double _weight;

    public DrugDO() {

    }

    // Parcelling part
    public DrugDO(Parcel in) {
        String[] data = new String[6];

        in.readStringArray(data);
        this._name = data[0];
        this._minqty = Double.parseDouble(data[1]);
        this._quantity = Double.parseDouble(data[2]);
        this._type = data[3];
        this._weight = Double.parseDouble(data[4]);
        this._notes = data[5];
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String _minqty_str;
        if (this._minqty == null){
            _minqty_str = "";
        }else{
            _minqty_str = this._minqty.toString();
        }
        String quantity_str;
        if (this._quantity == null){
            quantity_str = "";
        }else{
            quantity_str = this._quantity.toString();
        }
        String weight_str;
        if (this._weight == null){
            weight_str = "";
        }else{
            weight_str = this._weight.toString();
        }
        dest.writeStringArray(new String[]{
                this._name,
                _minqty_str,
                quantity_str,
                this._type,
                weight_str,
                this._notes});

    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DrugDO createFromParcel(Parcel in) {
            return new DrugDO(in);
        }

        public DrugDO[] newArray(int size) {
            return new DrugDO[size];
        }
    };
}
