package com.korzinni.shura.nutrition.model;

import android.database.Cursor;
import android.os.Parcel;
import android.util.Log;

import com.korzinni.shura.nutrition.content.MyContractClass;

import java.util.ArrayList;
import java.util.List;

public class Dish extends Product {


    private List<Product> products =new ArrayList<Product>();
    private List<Integer> mass =new ArrayList<Integer>();
    Integer resultSumm;

    public Dish(){
        evaluateSumm();
    }

    public List<Integer> getMass(){
        return mass;
    }
    public Dish(Cursor cursor) {
        setId(cursor.getLong(cursor.getColumnIndex(MyContractClass.Product.ID)));
        setName(cursor.getString(cursor.getColumnIndex(MyContractClass.Product.NAME)));
        setTypeId(cursor.getLong(cursor.getColumnIndex(MyContractClass.Product.TYPE_ID)));
        setProteins(cursor.getFloat(cursor.getColumnIndex(MyContractClass.Product.PROTEINS)));
        setFats(cursor.getFloat(cursor.getColumnIndex(MyContractClass.Product.FATS)));
        setCarbohydrates(cursor.getFloat(cursor.getColumnIndex(MyContractClass.Product.CARBOHYDRATES)));
        setCalories(cursor.getFloat(cursor.getColumnIndex(MyContractClass.Product.CALORIES)));
    }
    public void setMass(List<Integer> mass){
        this.mass=mass;
    }

    public void addIngredient(Product p, Integer m){
        if(getIdsProducts().contains(p.getId())){
            return;
        }else{

            products.add(p);
            mass.add(m);
            evaluateSumm();
        }
    }

    public void removeIngredient(int index){
        products.remove(index);
        mass.remove(index);
        evaluateSumm();
    }
    public void editMass(int index,int m){
        mass.set(index,m);
        evaluateSumm();
    }

    public List<Product> getProducts(){

        return products;
    }

    public void setProducts(List<Product> products){

        this.products=products;
    }

    public List<Long> getIdsProducts(){
        List<Long> ids=new ArrayList<Long>();
        for(Product p:products){
            ids.add(p.getId());
        }
        return ids;
    }
    public void evaluateSumm(){
        if(!products.isEmpty()&& !mass.isEmpty()){
            resultSumm=0;
            for(Integer m:mass){
                resultSumm+=m;
            }
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
            proteins=proteinSumm*100f/(float)resultSumm;
            fats=fatsSumm*100f/(float)resultSumm;
            carbohydrates=carbohydratesSumm*100f/(float)resultSumm;
            calories=caloriesSumm*100f/(float)resultSumm;
        }else{
            proteins=0;
            fats=0;
            carbohydrates=0;
            calories=0;
        }

    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloatArray(new float[]{proteins,fats,carbohydrates,calories});
        dest.writeString(name);
        dest.writeLongArray(new long[]{id,type_id});
        dest.writeTypedList(getProducts());
        int[] massArray=new int[mass.size()];
        int i=0;
        for(Integer m: mass){
            massArray[i++]=m;
        }
        dest.writeIntArray(massArray);
        dest.writeInt(i);
    }
     public Creator<Dish> creator=new Creator<Dish>() {
        @Override
        public Dish createFromParcel(Parcel source) {
            Dish dish=new Dish();
            int arrayLenght=source.readInt();

            int[] massArray=new int[arrayLenght];
            source.readIntArray(massArray);
            List<Product> products=new ArrayList<Product>();
            source.readTypedList(products,Dish.super.creator);
            long[] longFields=new long[2];
            source.readLongArray(longFields);
            String name=source.readString();
            float[] floatFields=new float[4];
            source.readFloatArray(floatFields);

            List<Integer> mass=new ArrayList<Integer>();
            for(int i:massArray){
                mass.add(i);
            }
            dish.setMass(mass);
            dish.setProducts(products);
            dish.setId(longFields[0]);
            dish.setTypeId(longFields[1]);
            dish.setName(name);
            dish.setProteins(floatFields[0]);
            dish.setFats(floatFields[1]);
            dish.setCarbohydrates(floatFields[2]);
            dish.setCalories(floatFields[3]);
            return dish;
        }

        @Override
        public Dish[] newArray(int size) {
            return new Dish[size];
        }
    };

}
