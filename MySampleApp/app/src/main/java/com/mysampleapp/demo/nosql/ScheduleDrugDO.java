package com.mysampleapp.demo.nosql;

import android.os.Parcel;
import android.os.Parcelable;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.Set;

@DynamoDBTable(tableName = "myfirstapp-mobilehub-1482957139-ScheduleDrug")

public class ScheduleDrugDO implements Parcelable {
    private String _userId;
    private Double _alarmId;
    private Set<String> _day;
    private String _drug;
    private Set<Double> _hour;
    private String _notes;

    public ScheduleDrugDO(){}

    // Parcelling part
    public ScheduleDrugDO(Parcel in) {

        _alarmId = in.readByte() == 0x00 ? null : in.readDouble();
        _day = (Set) in.readValue(Set.class.getClassLoader());
        _drug = in.readString();
        _hour = (Set) in.readValue(Set.class.getClassLoader());
        _notes = in.readString();
    }
    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "alarmId")
    @DynamoDBAttribute(attributeName = "alarmId")
    public Double getAlarmId() {
        return _alarmId;
    }

    public void setAlarmId(final Double _alarmId) {
        this._alarmId = _alarmId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        if (_alarmId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(_alarmId);
        }
        dest.writeValue(_day);
        dest.writeString(_drug);
        dest.writeValue(_hour);
        dest.writeString(_notes);
    }

    public static final Parcelable.Creator<ScheduleDrugDO> CREATOR = new Parcelable.Creator<ScheduleDrugDO>() {
        @Override
        public ScheduleDrugDO createFromParcel(Parcel in) {
            return new ScheduleDrugDO(in);
        }

        @Override
        public ScheduleDrugDO[] newArray(int size) {
            return new ScheduleDrugDO[size];
        }
    };
}
