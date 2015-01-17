package com.korzinni.shura.nutrition;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.korzinni.shura.nutrition.content.MyContractClass;

import java.util.ArrayList;
import java.util.List;

public class CustomCursorLoader extends AsyncTaskLoader<Cursor> {
    long typeId;
    private Cursor mData;
    public CustomCursorLoader(Context context,long typeId) {
        super(context);
        this.typeId=typeId;
    }

    @Override
    public Cursor loadInBackground() {
        if(getId()==-1){
            //ids products deleting group(type)
            String selection=MyContractClass.Product.TYPE_ID+"=?";
            String[] selectionArgs=new String[]{String.valueOf(typeId)};
            Cursor cursor=getContext().getContentResolver().query(MyContractClass.Product.CONTENT_URI,null,selection,selectionArgs,null);
            //create selectionArgs with all id products
            List<String> ids=new ArrayList<String>();
            while(cursor.moveToNext()){
                long idProduct=cursor.getLong(cursor.getColumnIndex(MyContractClass.Product.ID));
                ids.add(String.valueOf(idProduct));
            }
            cursor.close();
            selectionArgs=new String[ids.size()];
            selection=MyContractClass.Dish.ID_PRODUCT+"=?";
            ids.toArray(selectionArgs);

            for(int i=0;i<selectionArgs.length-1;i++) {
                selection+=" OR "+MyContractClass.Dish.ID_PRODUCT+"=?";
            }//get ids for all Used products in dish
            Cursor cursorIdUsed=getContext().getContentResolver().query(MyContractClass.Dish.CONTENT_URI,null,selection,selectionArgs,null);
            //create selectionArgs with all used id products
            ids=new ArrayList<String>();
            while(cursorIdUsed.moveToNext()){
                long idUsedProduct=cursorIdUsed.getLong(cursorIdUsed.getColumnIndex(MyContractClass.Dish.ID_PRODUCT));
                ids.add(String.valueOf(idUsedProduct));
            }
            cursorIdUsed.close();
            selectionArgs=new String[ids.size()];

            selection=MyContractClass.Product.ID+"=?";
            ids.toArray(selectionArgs);
            for(int i=0;i<selectionArgs.length-1;i++) {
                selection += " OR " + MyContractClass.Product.ID + "=?";
            }
            //get Products by ids
            Cursor cursorUsedProduct=getContext().getContentResolver().query(MyContractClass.Product.CONTENT_URI,null,selection,selectionArgs,null);
            return cursorUsedProduct;
        }else{

            String selection= MyContractClass.Dish.ID_PRODUCT+"=?";
            String[] selectionArgs=new String[]{String.valueOf(getId())};
            //get all rows contain this id_product from DISH
            Cursor cursor=getContext().getContentResolver().query(MyContractClass.Dish.CONTENT_URI,null,selection,selectionArgs,null);
            List<String> selectionList=new ArrayList<String>();
            while(cursor.moveToNext()){
                long dishId=cursor.getLong(cursor.getColumnIndex(MyContractClass.Dish.ID_DISH));
                //save all id_dish for this id_product
                selectionList.add(String.valueOf(dishId));
            }
            cursor.close();
            selectionArgs=new String[selectionList.size()];

            selection=MyContractClass.Product.ID+"=?";
            selectionList.toArray(selectionArgs);
            for(int i=0;i<selectionArgs.length-1;i++) {
                Log.d("tag", "selectionArgs=" + selectionArgs[i]);
                selection+=" OR "+MyContractClass.Product.ID+"=?";
            }
            Cursor cursorDishes=getContext().getContentResolver().query(MyContractClass.Product.CONTENT_URI,null,selection,selectionArgs,null);
            return cursorDishes;
        }
    }


    @Override
    public void deliverResult(Cursor data) {
        if (isReset()) {
            // The Loader has been reset; ignore the result and invalidate the data.
            releaseResources(data);
            return;
        }

        // Hold a reference to the old data so it doesn't get garbage collected.
        // We must protect it until the new data has been delivered.
        Cursor oldData = mData;
        mData = data;

        if (isStarted()) {
            // If the Loader is in a started state, deliver the results to the
            // client. The superclass method does this for us.
            super.deliverResult(data);
        }

        // Invalidate the old data as we don't need it any more.
        if (oldData != null && oldData != data) {
            releaseResources(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        }

        if (takeContentChanged() || mData == null) {

            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {

        cancelLoad();

    }

    @Override
    protected void onReset() {
        onStopLoading();

        if (mData != null) {
            releaseResources(mData);
            mData = null;
        }
    }

    @Override
    public void onCanceled(Cursor data) {
        super.onCanceled(data);
        releaseResources(data);
    }

    private void releaseResources(Cursor data) {
        data.close();
    }





}
