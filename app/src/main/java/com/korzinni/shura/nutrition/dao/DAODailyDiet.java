package com.korzinni.shura.nutrition.dao;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.korzinni.shura.nutrition.content.MyContractClass;
import com.korzinni.shura.nutrition.model.DailyDiet;
import com.korzinni.shura.nutrition.model.TypeProduct;

import java.util.ArrayList;
import java.util.List;

public class DAODailyDiet {
    Context context;
    public DAODailyDiet(Context context){

        this.context=context.getApplicationContext();
    }

    public DailyDiet getDailyDietByDate(long date){
        String selection=MyContractClass.DailyDiet.DATE+"=?";
        String[] selectionArgs=new String[]{String.valueOf(date)};
        Cursor cursor=context.getContentResolver().query(MyContractClass.DailyDiet.CONTENT_URI,null,selection,selectionArgs,null);
        DailyDiet diet=null;
        if(cursor.moveToNext()){
            diet=new DailyDiet(cursor);
        }
        cursor.close();
        return diet;
    }
    public long addDailyDiet( DailyDiet diet){
         Uri uri = context.getContentResolver().insert(MyContractClass.DailyDiet.CONTENT_URI, diet.getValues());
         return ContentUris.parseId(uri);

    }
    public List<DailyDiet> getAllDailyDiet(){
        List<DailyDiet> diets=new ArrayList<DailyDiet>();
        Cursor cursor=context.getContentResolver().query(MyContractClass.DailyDiet.CONTENT_URI,null,null,null,null);
        while(cursor.moveToNext()){
            diets.add(new DailyDiet(cursor));
        }
        cursor.close();
        return diets;
    }
}
