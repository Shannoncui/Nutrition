package com.korzinni.shura.nutrition.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.korzinni.shura.nutrition.content.MyContractClass;

public class TypeProduct implements Parcelable{


    public TypeProduct(){

    }
    public TypeProduct(Cursor cursor){
        setId(cursor.getLong(cursor.getColumnIndex(MyContractClass.TypeProduct.ID)));
        setName(cursor.getString(cursor.getColumnIndex(MyContractClass.TypeProduct.NAME)));
        setIsProduct(cursor.getInt(cursor.getColumnIndex(MyContractClass.TypeProduct.IS_PRODUCT))!=0);
    }

    protected String name;
    protected long id;
    private boolean isProduct;
    public TypeProduct(String name,boolean isProduct){
        this.name=name.trim();
        this.isProduct=isProduct;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isProduct() {
        return isProduct;
    }

    public void setIsProduct(boolean isProduct) {
        this.isProduct = isProduct;
    }

    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(MyContractClass.TypeProduct.NAME, name);
        values.put(MyContractClass.TypeProduct.SUGGESTION, name.toLowerCase());
        values.put(MyContractClass.TypeProduct.IS_PRODUCT, isProduct ? 1 : 0);

        return values;
    }
    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeBooleanArray(new boolean[]{isProduct});
        dest.writeLong(id);
    }
    Creator<TypeProduct> creator=new Creator<TypeProduct>() {
        @Override
        public TypeProduct createFromParcel(Parcel source) {
            TypeProduct type=new TypeProduct();
            type.setId(source.readLong());
            boolean[] temp=new boolean[1];
            source.readBooleanArray(temp);
            type.setIsProduct(temp[0]);
            type.setName(source.readString());
            return type;
        }

        @Override
        public TypeProduct[] newArray(int size) {
            return new TypeProduct[size];
        }
    };
}
