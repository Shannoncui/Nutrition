package com.korzinni.shura.nutrition.dao;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;

import com.korzinni.shura.nutrition.content.MyContractClass;
import com.korzinni.shura.nutrition.model.Product;
import com.korzinni.shura.nutrition.model.TypeProduct;

import java.util.ArrayList;
import java.util.List;


public class DAOGroupProduct {
    Context context;

    public DAOGroupProduct(Context context) {
        this.context = context.getApplicationContext();
    }

    /*
    * return the row ID of the newly inserted row, or -1 if an error occurred
    * */
    public long addGroupProduct(TypeProduct type) {
        Uri uri = context.getContentResolver().insert(MyContractClass.TypeProduct.CONTENT_URI, type.getValues());
        return ContentUris.parseId(uri);
    }
    public int updateGroupProduct(TypeProduct type){
        Uri uri=ContentUris.withAppendedId(MyContractClass.TypeProduct.CONTENT_URI,type.getId());
        return context.getContentResolver().update(uri,type.getValues(),null,null);
    }

    /*
    * check whether exist product with such typeId
    * return row id if exist or -1 if don't exist
    * */
    public TypeProduct checkExistenceGroupProduct(String name) {
        String selection = MyContractClass.TypeProduct.SUGGESTION + " =?";
        String[] selectionArgs = new String[]{name.trim().toLowerCase()};
        TypeProduct typeFromDB=null;
        Cursor cursor = context.getContentResolver().query(MyContractClass.TypeProduct.CONTENT_URI, null, selection, selectionArgs, null);
        if (cursor.moveToNext()) {
            typeFromDB=new TypeProduct(cursor);
        }
        cursor.close();
        return typeFromDB;
    }

    public List<TypeProduct> getAllGroupProducts() {
        List<TypeProduct> typeProducts = new ArrayList<TypeProduct>();
        String selection=MyContractClass.TypeProduct.IS_PRODUCT+"=?";
        String[] selectionArgs=new String[]{"1"};
        Cursor cursor = context.getContentResolver().query(MyContractClass.TypeProduct.CONTENT_URI, null, selection, selectionArgs, null);
        while (cursor.moveToNext()) {
            typeProducts.add(new TypeProduct(cursor));
        }
        cursor.close();
        return typeProducts;
    }
    public List<TypeProduct> getAllGroupDish() {
        List<TypeProduct> typeProducts = new ArrayList<TypeProduct>();
        String selection=MyContractClass.TypeProduct.IS_PRODUCT+"=?";
        String[] selectionArgs=new String[]{"0"};
        Cursor cursor = context.getContentResolver().query(MyContractClass.TypeProduct.CONTENT_URI, null, selection, selectionArgs, null);
        while (cursor.moveToNext()) {
            typeProducts.add(new TypeProduct(cursor));
        }
        cursor.close();
        return typeProducts;
    }
    public TypeProduct getGroupById(long id){
        Uri uri=ContentUris.withAppendedId(MyContractClass.TypeProduct.CONTENT_URI,id);
        Cursor cursor=context.getContentResolver().query(uri,null,null,null,null);
        TypeProduct typeProduct=null;
        if (cursor!=null&&cursor.moveToNext()) {
            typeProduct=new TypeProduct(cursor);
        }
        cursor.close();
        return typeProduct;
    }
    public void deleteGroupWithDishes(long id){
        DAOProduct daoProduct=new DAOProduct(context);
        List<Product> products=daoProduct.getAllProductsByGroup(id);

        ArrayList<ContentProviderOperation> operations=new ArrayList<ContentProviderOperation>();
        Uri uri=ContentUris.withAppendedId(MyContractClass.TypeProduct.CONTENT_URI,id);
        operations.add(ContentProviderOperation.newDelete(uri).
                build());
        operations.add(ContentProviderOperation.newDelete(MyContractClass.Product.CONTENT_URI).
                withSelection(MyContractClass.Product.TYPE_ID+"=?",new String[]{String.valueOf(id)}).
                build());
        for(Product p:products){
            operations.add(ContentProviderOperation.newDelete(MyContractClass.Dish.CONTENT_URI).
                    withSelection(MyContractClass.Dish.ID_DISH+"=?",new String[]{String.valueOf(p.getId())}).
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
    }

    public void deleteGroupWithProducts(long id){
        DAOProduct daoProduct=new DAOProduct(context);
        List<Product> products=daoProduct.getAllProductsByGroup(id);

        ArrayList<ContentProviderOperation> operations=new ArrayList<ContentProviderOperation>();
        Uri uri=ContentUris.withAppendedId(MyContractClass.TypeProduct.CONTENT_URI,id);
        operations.add(ContentProviderOperation.newDelete(uri).
                build());
        operations.add(ContentProviderOperation.newDelete(MyContractClass.Product.CONTENT_URI).
                withSelection(MyContractClass.Product.TYPE_ID+"=?",new String[]{String.valueOf(id)}).
                build());
        for(Product p:products){
            operations.add(ContentProviderOperation.newDelete(MyContractClass.Dish.CONTENT_URI).
                    withSelection(MyContractClass.Dish.ID_PRODUCT+"=?",new String[]{String.valueOf(p.getId())}).
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
    }
    public void deleteGroup(long id){
        Uri uri=ContentUris.withAppendedId(MyContractClass.TypeProduct.CONTENT_URI,id);
        context.getContentResolver().delete(uri,null,null);
    }


}


