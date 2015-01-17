package com.korzinni.shura.nutrition.dao;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.korzinni.shura.nutrition.content.MyContractClass;
import com.korzinni.shura.nutrition.model.Meal;
import com.korzinni.shura.nutrition.model.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DAOMeal {
    Context context;
    public DAOMeal(Context context){
        this.context=context.getApplicationContext();
    }

    public List<Meal> getMealByDailyDiet(long idDailyDiet){
        DAOProduct daoProduct=new DAOProduct(context);
        List<Meal> meals=new ArrayList<Meal>();
        //get all names of meals for this day
        String[] projection=new String[]{MyContractClass.Meal.NAME};
        String selection=MyContractClass.Meal.ID_DAILY_DIET+"=?";
        String[] selectionArgs=new String[]{String.valueOf(idDailyDiet)};
        Cursor cursor=context.getContentResolver().query(MyContractClass.Meal.CONTENT_URI,projection,selection,selectionArgs,null);
        Set<String> names=new HashSet<String>();
        while(cursor.moveToNext()){
           names.add(cursor.getColumnName(cursor.getColumnIndex(MyContractClass.Meal.NAME)));
        }
        cursor.close();
        for(String s: names){
            selection=MyContractClass.Meal.ID_DAILY_DIET+"=? AND "+MyContractClass.Meal.NAME+"=?";
            selectionArgs=new String[]{String.valueOf(idDailyDiet),s};
            Cursor cursorForOneMeal=context.getContentResolver().query(MyContractClass.Meal.CONTENT_URI,null,selection,selectionArgs,null);
            if(cursorForOneMeal.moveToNext()){
                Meal meal=new Meal(cursorForOneMeal);
                long idProductOrDish=cursorForOneMeal.getLong(cursorForOneMeal.getColumnIndex(MyContractClass.Meal.ID_PRODUCT_OR_DISH));
                // check weather need add chek to Product/Dish or not
                Product product=daoProduct.getProductById(idProductOrDish);
                int mass=cursorForOneMeal.getInt(cursorForOneMeal.getColumnIndex(MyContractClass.Meal.MASS));
                meal.addIngredient(product,mass);
                while(cursorForOneMeal.moveToNext()){
                    idProductOrDish=cursorForOneMeal.getLong(cursorForOneMeal.getColumnIndex(MyContractClass.Meal.ID_PRODUCT_OR_DISH));
                    // check weather need add chek to Product/Dish or not
                    product=daoProduct.getProductById(idProductOrDish);
                    mass=cursorForOneMeal.getInt(cursorForOneMeal.getColumnIndex(MyContractClass.Meal.MASS));
                    meal.addIngredient(product,mass);
                }
            }
        }
        return meals;
    }
    public void addMeal(Meal meal){

    }

}
