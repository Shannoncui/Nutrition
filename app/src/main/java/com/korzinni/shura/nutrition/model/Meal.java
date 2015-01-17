package com.korzinni.shura.nutrition.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.korzinni.shura.nutrition.content.MyContractClass;

import java.util.Date;
import java.util.List;

public class Meal extends Dish {
    long idDailyDiet;
    public long getDailyDiet() {
        return idDailyDiet;
    }

    public void setDailyDiet(long idDailyDiet) {
        this.idDailyDiet = idDailyDiet;
    }

    public ContentValues getValues(){
        ContentValues values=new ContentValues();
        values.put(MyContractClass.Meal.ID_DAILY_DIET,idDailyDiet);
        values.put(MyContractClass.Meal.NAME,getName());
        return values;
    }
    public Meal(Cursor cursor){

            setId(cursor.getLong(cursor.getColumnIndex(MyContractClass.Meal.ID)));
            setId(cursor.getLong(cursor.getColumnIndex(MyContractClass.Meal.ID_DAILY_DIET)));
            setName(cursor.getString(cursor.getColumnIndex(MyContractClass.Meal.NAME)));

    }
    public Meal(){

    }
    @Override
    public void evaluateSumm(){
        List<Product> products=getProducts();
        List<Integer> mass=getMass();
        if(!products.isEmpty()&& !mass.isEmpty()){
            resultSumm=0;
            float proteinSumm=0;
            float fatsSumm=0;
            float carbohydratesSumm=0;
            float caloriesSumm=0;

            for(int i=0;i<products.size();i++){
                //mass.get(i)/100 count of proteins in dependent from mass component
                proteinSumm+=products.get(i).getProteins()*(float)mass.get(i)/100f;
                fatsSumm+=products.get(i).getFats()*(float)mass.get(i)/100f;
                carbohydratesSumm+=products.get(i).getCarbohydrates()*(float)mass.get(i)/100f;
                caloriesSumm+=products.get(i).getCalories()*(float)mass.get(i)/100f;
            }
            //count proteins in 100 gramm of dish
            proteins=proteinSumm;
            fats=fatsSumm;
            carbohydrates=carbohydratesSumm;
            calories=caloriesSumm;
        }else{
            proteins=0;
            fats=0;
            carbohydrates=0;
            calories=0;
        }

    }
}