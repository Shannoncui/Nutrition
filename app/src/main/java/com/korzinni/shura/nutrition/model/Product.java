package com.korzinni.shura.nutrition.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.korzinni.shura.nutrition.content.MyContractClass;


public class Product extends TypeProduct implements Parcelable{

    protected long type_id;
    protected float proteins;
    protected float fats;
    protected float carbohydrates;
    protected float calories;



    public Product(){

    }
    public Product(Cursor cursor) {
       setId(cursor.getLong(cursor.getColumnIndex(MyContractClass.Product.ID)));
       setName(cursor.getString(cursor.getColumnIndex(MyContractClass.Product.NAME)));
       setTypeId(cursor.getLong(cursor.getColumnIndex(MyContractClass.Product.TYPE_ID)));
       setProteins(cursor.getFloat(cursor.getColumnIndex(MyContractClass.Product.PROTEINS)));
       setFats(cursor.getFloat(cursor.getColumnIndex(MyContractClass.Product.FATS)));
       setCarbohydrates(cursor.getFloat(cursor.getColumnIndex(MyContractClass.Product.CARBOHYDRATES)));
       setCalories(cursor.getFloat(cursor.getColumnIndex(MyContractClass.Product.CALORIES)));
    }

    public long getTypeId() {
        return type_id;
    }

    public Product setTypeId(long type_id) {
        this.type_id = type_id;
        return this;
    }



    public void setProteins(float proteins) {
        this.proteins = proteins;

    }

    public float getProteins() {
        return proteins;
    }

    public void setFats(float fats) {
        this.fats = fats;
    }

    public float getFats() {
        return fats;
    }

    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getCalories() {
        return calories;
    }

    public ContentValues getValues(){
        ContentValues values=new ContentValues();
        values.put(MyContractClass.Product.TYPE_ID,type_id);
        values.put(MyContractClass.Product.NAME,name);
        values.put(MyContractClass.Product.SUGGESTION,name.toLowerCase());
        values.put(MyContractClass.Product.PROTEINS, proteins);
        values.put(MyContractClass.Product.FATS,fats);
        values.put(MyContractClass.Product.CARBOHYDRATES,carbohydrates);
        values.put(MyContractClass.Product.CALORIES,calories);
        return values;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(new float[]{proteins,fats,carbohydrates,calories});
        dest.writeString(name);
        dest.writeLongArray(new long[]{id,type_id});
    }
    Creator<Product> creator=new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            Product product=new Product();

            long[] longFields=new long[2];
            source.readLongArray(longFields);
            String name=source.readString();
            float[] floatFields=new float[4];
            source.readFloatArray(floatFields);

            product.setId(longFields[0]);
            product.setTypeId(longFields[1]);
            product.setName(name);
            product.setProteins(floatFields[0]);
            product.setFats(floatFields[1]);
            product.setCarbohydrates(floatFields[2]);
            product.setCalories(floatFields[3]);
            return product;
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
