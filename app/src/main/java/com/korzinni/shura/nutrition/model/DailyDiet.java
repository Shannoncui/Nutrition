package com.korzinni.shura.nutrition.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.korzinni.shura.nutrition.content.MyContractClass;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DailyDiet implements Parcelable{
    private long id;
    private long date;
    private String name;
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public final void setDate(long date){
        this.date=date;
        SimpleDateFormat f=new SimpleDateFormat("dd-MM-yyyy",Locale.getDefault());
        setName(f.format(date));

    }

    public long getDate() {
        return date;
    }


    public DailyDiet(){

    }
    public DailyDiet(Cursor cursor){
        setId(cursor.getLong(cursor.getColumnIndex(MyContractClass.DailyDiet.ID)));
        setDate(cursor.getLong(cursor.getColumnIndex(MyContractClass.DailyDiet.DATE)));
    }
    public DailyDiet(long date){
        setDate(date);
    }
    public ContentValues getValues(){
        ContentValues values=new ContentValues();
        values.put(MyContractClass.DailyDiet.DATE,date);
        return values;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(date);
        dest.writeLong(id);
    }
    Creator<DailyDiet> creator=new Creator<DailyDiet>() {
        @Override
        public DailyDiet createFromParcel(Parcel source) {
            DailyDiet diet=new DailyDiet();
            diet.setId(source.readLong());
            diet.setDate(source.readLong());
            return diet;
        }

        @Override
        public DailyDiet[] newArray(int size) {
            return new DailyDiet[size];
        }
    };
}
