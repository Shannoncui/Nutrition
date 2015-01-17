package com.korzinni.shura.nutrition.dao;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.korzinni.shura.nutrition.content.MyContractClass;
import com.korzinni.shura.nutrition.model.Dish;
import com.korzinni.shura.nutrition.model.Product;

import java.util.ArrayList;
import java.util.List;

public class DAODish {
Context context;
    public DAODish(Context context){
        this.context=context.getApplicationContext();
    }

    public long addDish(Dish dish){
        List<Long> idsProducts=dish.getIdsProducts();
        List<Integer>  mass=dish.getMass();
        ArrayList<ContentProviderOperation> operations=new ArrayList<ContentProviderOperation>();

        operations.add(ContentProviderOperation.newInsert(MyContractClass.Product.CONTENT_URI).
                withValues(dish.getValues()).
                build());

        for(int i=0;i<idsProducts.size();i++){
           operations.add(ContentProviderOperation.newInsert(MyContractClass.Dish.CONTENT_URI).
                   withValueBackReference(MyContractClass.Dish.ID_DISH,0).//id added previously dish(product)
                   withValue(MyContractClass.Dish.ID_PRODUCT,idsProducts.get(i)).withValue(MyContractClass.Dish.MASS,mass.get(i)).
                   build());
        }
        ContentProviderResult[] result=null;
        try {
          result = context.getContentResolver().applyBatch(MyContractClass.AUTHORITY,operations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        return result!=null? ContentUris.parseId(result[0].uri): -1;
    }
    public long checkExistenceDish(Dish dish){
        String[] projection=new String[]{MyContractClass.Dish.ID};
        String selection=MyContractClass.Dish.SUGGESTION+" =? AND "+MyContractClass.Dish.TYPE_ID+" =?";
        String[] selectionArgs=new String[]{dish.getName().toLowerCase(),String.valueOf(dish.getTypeId())};

        Cursor cursor=context.getContentResolver().query(MyContractClass.Product.CONTENT_URI,projection,selection,selectionArgs,null);

        if(cursor.moveToFirst()){
            long id=cursor.getLong(cursor.getColumnIndex(MyContractClass.Dish.ID));
            cursor.close();
            return id;
        }else{
            cursor.close();
            return -1;
        }
    }
    public int updateDish(Dish dish){
        Log.d("tag","updateDish: \n");
        List<Long> idsProducts=dish.getIdsProducts();
        List<Integer>  mass=dish.getMass();
        Log.d("tag","id_dish:" +String.valueOf(dish.getId()));
        for(long l:idsProducts){
            Log.d("tag","ids:"+String.valueOf(l));
        }
        ArrayList<ContentProviderOperation> operations=new ArrayList<ContentProviderOperation>();
        Uri uri=ContentUris.withAppendedId(MyContractClass.Product.CONTENT_URI,dish.getId());
        operations.add(ContentProviderOperation.newUpdate(uri).
                withValues(dish.getValues()).
                build());
        operations.add(ContentProviderOperation.newDelete(MyContractClass.Dish.CONTENT_URI).
                withSelection(MyContractClass.Dish.ID_DISH+"=?",new String[]{String.valueOf(dish.getId())}).
                build());

        for(int i=0;i<idsProducts.size();i++){
            operations.add(ContentProviderOperation.newInsert(MyContractClass.Dish.CONTENT_URI).
                    withValue(MyContractClass.Dish.ID_DISH, dish.getId()).
                    withValue(MyContractClass.Dish.ID_PRODUCT, idsProducts.get(i)).
                    withValue(MyContractClass.Dish.MASS, mass.get(i)).
                    build());
        }
        ContentProviderResult[] result=null;
        try {
            result = context.getContentResolver().applyBatch(MyContractClass.AUTHORITY,operations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
        return result.length;
    }
    public Dish getDishById(long id){
        Dish dish;
        Uri uri=ContentUris.withAppendedId(MyContractClass.Product.CONTENT_URI,id);
        DAOProduct dao=new DAOProduct(context);
        Cursor cursor=context.getContentResolver().query(uri,null,null,null,null);
        if(cursor.moveToNext()){
            dish=new Dish(cursor);
        }else {
            dish = null;
        }
        Cursor cursorForLists=context.getContentResolver().query(MyContractClass.Dish.CONTENT_URI, null, MyContractClass.Dish.ID_DISH + "=?", new String[]{String.valueOf(dish.getId())}, null);
        while(cursorForLists.moveToNext()){
            long idProduct=cursorForLists.getLong(cursorForLists.getColumnIndex(MyContractClass.Dish.ID_PRODUCT));
            Product product=dao.getProductById(idProduct);
            int mass=cursorForLists.getInt(cursorForLists.getColumnIndex(MyContractClass.Dish.MASS));
            dish.addIngredient(product, mass);
        }
        cursor.close();
        return dish;
    }
    public void deleteDish(long id){
        ArrayList<ContentProviderOperation> operations=new ArrayList<ContentProviderOperation>();
        Uri uri=ContentUris.withAppendedId(MyContractClass.Product.CONTENT_URI,id);
        operations.add(ContentProviderOperation.newDelete(uri).build());
        operations.add(ContentProviderOperation.newDelete(MyContractClass.Dish.CONTENT_URI).
                withSelection(MyContractClass.Dish.ID_DISH+"=?",new String[]{String.valueOf(id)}).
                build());
        ContentProviderResult[] result=null;
        try {
            result = context.getContentResolver().applyBatch(MyContractClass.AUTHORITY,operations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
    public List<Dish> getListDishesForProduct(long productId){
        List<Dish> dishes=new ArrayList<Dish>();
        String selection=MyContractClass.Dish.ID_PRODUCT+"=?";
        String[] selectionArgs=new String[]{String.valueOf(productId)};
        Cursor cursor=context.getContentResolver().query(MyContractClass.Dish.CONTENT_URI,null,selection,selectionArgs,null);
        while(cursor.moveToNext()){
            long dishId=cursor.getLong(cursor.getColumnIndex(MyContractClass.Dish.ID_DISH));
            Dish dish=getDishById(dishId);
            dishes.add(dish);
        }
        cursor.close();
        return dishes;
    }


}
