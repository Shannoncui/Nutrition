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
import com.korzinni.shura.nutrition.model.Product;
import com.korzinni.shura.nutrition.model.TypeProduct;

import java.util.ArrayList;
import java.util.List;


public class DAOProduct {
Context context;
    public DAOProduct(Context context){
        this.context=context.getApplicationContext();

    }
    /*
    * return the row ID of the newly inserted row, or -1 if an error occurred
    * */
    public long  addProduct(Product product){
        Uri uri=context.getContentResolver().insert(MyContractClass.Product.CONTENT_URI,product.getValues());
        return ContentUris.parseId(uri);
    }

    public int updateProduct(Product product){
        Uri uri=ContentUris.withAppendedId(MyContractClass.Product.CONTENT_URI,product.getId());
        return context.getContentResolver().update(uri,product.getValues(),null,null);
    }
    /*
    * check whether exist product with such typeId
    * return row id if exist or -1 if don't exist
    * */
    public long checkExistenceProduct(Product product){
        String[] projection=new String[]{MyContractClass.Product.ID};
        String selection=MyContractClass.Product.SUGGESTION+" =? AND "+MyContractClass.Product.TYPE_ID+" =?";
        String[] selectionArgs=new String[]{product.getName().toLowerCase(),String.valueOf(product.getTypeId())};

        Cursor cursor=context.getContentResolver().query(MyContractClass.Product.CONTENT_URI,projection,selection,selectionArgs,null);

        if(cursor.moveToFirst()){
            long id=cursor.getLong(cursor.getColumnIndex(MyContractClass.Product.ID));
            cursor.close();
            return id;
        }else{
            cursor.close();
            return -1;
        }
    }

    public List<Product> getAllProducts(){
        List<Product> products=new ArrayList<Product>();
        Cursor cursor=context.getContentResolver().query(MyContractClass.Product.CONTENT_URI,null,null,null,null);
        while(cursor.moveToNext()){
            products.add(new Product(cursor));
        }
        cursor.close();
        return products;
    }

    public List<Product> getAllProductsByGroup(long typeId){
        List<Product> products=new ArrayList<Product>();
        String selection=MyContractClass.Product.TYPE_ID+" =?";
        String[] selectionArgs=new String[]{String.valueOf(typeId)};
        Cursor cursor=context.getContentResolver().query(MyContractClass.Product.CONTENT_URI,null,selection,selectionArgs,null);
        while(cursor.moveToNext()){
            products.add(new Product(cursor));
        }
        cursor.close();
        return products;
    }

    public Product getProductById(long id){
        Product product;
        Uri uri=ContentUris.withAppendedId(MyContractClass.Product.CONTENT_URI,id);
        Cursor cursor=context.getContentResolver().query(uri,null,null,null,null);
        if(cursor.moveToNext()){
            product=new Product(cursor);
        }else {
            product = null;
        }
        cursor.close();
        return product;
    }
    public boolean isProduct(long id){
        long typeId=getProductById(id).getTypeId();
        Log.d("tag","name: "+getProductById(id).getName());
        DAOGroupProduct daoGroupProduct =new DAOGroupProduct(context);
        TypeProduct type= daoGroupProduct.getGroupById(typeId);

        return type.isProduct();
    }
    public void deleteProduct(long id){
        Uri uri=ContentUris.withAppendedId(MyContractClass.Product.CONTENT_URI,id);
        ArrayList<ContentProviderOperation> operations=new ArrayList<ContentProviderOperation>();
        operations.add(ContentProviderOperation.newDelete(uri).build());
        operations.add(ContentProviderOperation.newDelete(MyContractClass.Dish.CONTENT_URI).
                withSelection(MyContractClass.Dish.ID_PRODUCT+"=?",new String[]{String.valueOf(id)}).
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
    public boolean isUsedInDish(long id){
        String selection=MyContractClass.Dish.ID_PRODUCT+"=?";
        String[] selectionArgs=new String[]{String.valueOf(id)};
        Cursor cursor=context.getContentResolver().query(MyContractClass.Dish.CONTENT_URI,null,selection,selectionArgs,null);
        if(cursor.moveToNext()){
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

}
