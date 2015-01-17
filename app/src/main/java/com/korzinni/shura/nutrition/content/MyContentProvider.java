package com.korzinni.shura.nutrition.content;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;


public class MyContentProvider extends ContentProvider {
    private DBHelper helper;
    private SQLiteDatabase db;
    private final String T=getClass().getSimpleName();
    private static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PRODUCTS=1;
    private static final int PRODUCTS_ID=2;
    private static final int PRODUCT_TYPE=3;
    private static final int PRODUCT_TYPE_ID=4;
    private static final int SUGGEST=5;
    private static final int DISH=6;
    private static final int DISH_ID=7;
    private static final int MEAL=8;
    private static final int MEAL_ID=9;
    private static final int DAILY_DIET=10;
    private static final int DAILY_DIET_ID=11;

    @Override
    public boolean onCreate() {
        helper=new DBHelper(getContext());
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.Product.TABLE_NAME,PRODUCTS);
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.Product.TABLE_NAME+"/#",PRODUCTS_ID);
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.TypeProduct.TABLE_NAME,PRODUCT_TYPE);
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.TypeProduct.TABLE_NAME+"/#",PRODUCT_TYPE_ID);
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.Dish.TABLE_NAME,DISH);
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.Dish.TABLE_NAME+"/#",DISH_ID);
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.Meal.TABLE_NAME,MEAL);
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.Meal.TABLE_NAME+"/#",MEAL_ID);
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.DailyDiet.TABLE_NAME,DAILY_DIET);
        mUriMatcher.addURI(MyContractClass.AUTHORITY,MyContractClass.DailyDiet.TABLE_NAME+"/#",DAILY_DIET_ID);

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db=helper.getWritableDatabase();
        Log.d(T,db.getPath());
        Log.d(T,uri.toString());

        Cursor cursor=null;
        String lastPathSegment=uri.getLastPathSegment();
        switch(mUriMatcher.match(uri)){

            case PRODUCTS:

               cursor= db.query(MyContractClass.Product.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PRODUCTS_ID:
                selection=MyContractClass.Product.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};

                cursor= db.query(MyContractClass.Product.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
            break;
            case PRODUCT_TYPE:
                cursor=db.query(MyContractClass.TypeProduct.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PRODUCT_TYPE_ID:

                selection=MyContractClass.TypeProduct.ID+" =?";
                selectionArgs=new String[]{lastPathSegment};
                cursor=  db.query(MyContractClass.TypeProduct.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case SUGGEST:
                selection=SearchManager.SUGGEST_COLUMN_TEXT_1+" LIKE ? ";
                selectionArgs=new String[]{"%"+lastPathSegment+"%"};
                cursor=db.query(MyContractClass.Product.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case DISH:
                cursor=db.query(MyContractClass.Dish.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case DISH_ID:
                selection=MyContractClass.Dish.ID+" =?";
                selectionArgs=new String[]{lastPathSegment};
                cursor=db.query(MyContractClass.Dish.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case MEAL:
                cursor=db.query(MyContractClass.Meal.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case MEAL_ID:
                selection=MyContractClass.Meal.ID+" =?";
                selectionArgs=new String[]{lastPathSegment};
                cursor=db.query(MyContractClass.Meal.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case DAILY_DIET:
                cursor=db.query(MyContractClass.DailyDiet.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case DAILY_DIET_ID:
                selection=MyContractClass.DailyDiet.ID+" =?";
                selectionArgs=new String[]{lastPathSegment};
                cursor=db.query(MyContractClass.DailyDiet.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        String mime=null;
        switch (mUriMatcher.match(uri)) {

            case PRODUCTS:
                mime=MyContractClass.Product.CONTENT_TYPE;
                break;
            case PRODUCTS_ID:
                mime=MyContractClass.Product.CONTENT_ITEM_TYPE;
                break;
            case PRODUCT_TYPE:
                mime=MyContractClass.TypeProduct.CONTENT_TYPE;
                break;
            case PRODUCT_TYPE_ID:
                mime=MyContractClass.TypeProduct.CONTENT_ITEM_TYPE;
                break;
            case DISH:
                mime=MyContractClass.Dish.CONTENT_TYPE;
                break;
            case DISH_ID:
                mime=MyContractClass.Dish.CONTENT_ITEM_TYPE;
                break;
            case MEAL:
                mime=MyContractClass.Meal.CONTENT_TYPE;
                break;
            case MEAL_ID:
                mime=MyContractClass.Meal.CONTENT_ITEM_TYPE;
                break;
            case DAILY_DIET:
                mime=MyContractClass.DailyDiet.CONTENT_TYPE;
                break;
            case DAILY_DIET_ID:
                mime=MyContractClass.DailyDiet.CONTENT_ITEM_TYPE;
                break;
        }
        return mime;
}
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db=helper.getWritableDatabase();
        long id=-1;
        switch(mUriMatcher.match(uri)){
            case PRODUCTS:
                id=db.insert(MyContractClass.Product.TABLE_NAME,null,values);
                break;
            case PRODUCT_TYPE:
                id=db.insert(MyContractClass.TypeProduct.TABLE_NAME,null,values);
                break;
            case DISH:
                id=db.insert(MyContractClass.Dish.TABLE_NAME,null,values);
                break;
            case MEAL:
                id=db.insert(MyContractClass.Meal.TABLE_NAME,null,values);
                break;
            case DAILY_DIET:
                id=db.insert(MyContractClass.DailyDiet.TABLE_NAME,null,values);
                break;

        }
        uri=ContentUris.withAppendedId(uri,id);
        return id<0 ? null:uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int returned=0;
        db=helper.getWritableDatabase();
        String lastPathSegment=uri.getLastPathSegment();
        switch (mUriMatcher.match(uri)) {

            case PRODUCTS:
                returned=db.delete(MyContractClass.Product.TABLE_NAME,selection,selectionArgs);
                break;
            case PRODUCTS_ID:
                selection=MyContractClass.Product.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.delete(MyContractClass.Product.TABLE_NAME,selection,selectionArgs);
                break;
            case DISH:
                returned=db.delete(MyContractClass.Dish.TABLE_NAME,selection,selectionArgs);
                break;
            case DISH_ID:
                selection=MyContractClass.Dish.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.delete(MyContractClass.Dish.TABLE_NAME,selection,selectionArgs);
                break;
            case PRODUCT_TYPE:
                returned=db.delete(MyContractClass.TypeProduct.TABLE_NAME,selection,selectionArgs);
                break;
            case PRODUCT_TYPE_ID:
                selection=MyContractClass.TypeProduct.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.delete(MyContractClass.TypeProduct.TABLE_NAME,selection,selectionArgs);
                break;
            case MEAL:
                returned=db.delete(MyContractClass.Meal.TABLE_NAME,selection,selectionArgs);
                break;
            case MEAL_ID:
                selection=MyContractClass.Meal.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.delete(MyContractClass.Meal.TABLE_NAME,selection,selectionArgs);
                break;
            case DAILY_DIET:
                returned=db.delete(MyContractClass.DailyDiet.TABLE_NAME,selection,selectionArgs);
                break;
            case DAILY_DIET_ID:
                selection=MyContractClass.DailyDiet.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.delete(MyContractClass.DailyDiet.TABLE_NAME,selection,selectionArgs);
                break;
        }
        return returned;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int returned=0;
        db=helper.getWritableDatabase();
        String lastPathSegment=uri.getLastPathSegment();
        switch (mUriMatcher.match(uri)) {

            case PRODUCTS:
                returned=db.update(MyContractClass.Product.TABLE_NAME,values,selection,selectionArgs);
                break;
            case PRODUCTS_ID:
                selection=MyContractClass.Product.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.update(MyContractClass.Product.TABLE_NAME,values,selection,selectionArgs);
                break;
            case DISH:
                returned=db.update(MyContractClass.Dish.TABLE_NAME,values,selection,selectionArgs);
                break;
            case DISH_ID:
                selection=MyContractClass.Dish.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.update(MyContractClass.Dish.TABLE_NAME,values,selection,selectionArgs);
                break;
            case PRODUCT_TYPE:
                returned=db.update(MyContractClass.TypeProduct.TABLE_NAME,values,selection,selectionArgs);
                break;
            case PRODUCT_TYPE_ID:
                selection=MyContractClass.Dish.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.update(MyContractClass.TypeProduct.TABLE_NAME,values,selection,selectionArgs);
                break;
            case MEAL:
                returned=db.update(MyContractClass.Meal.TABLE_NAME,values,selection,selectionArgs);
                break;
            case MEAL_ID:
                selection=MyContractClass.Meal.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.update(MyContractClass.Meal.TABLE_NAME,values,selection,selectionArgs);
                break;
            case DAILY_DIET:
                returned=db.update(MyContractClass.DailyDiet.TABLE_NAME,values,selection,selectionArgs);
                break;
            case DAILY_DIET_ID:
                selection=MyContractClass.DailyDiet.ID+"=?";
                selectionArgs=new String[]{lastPathSegment};
                returned=db.update(MyContractClass.DailyDiet.TABLE_NAME,values,selection,selectionArgs);
                break;
        }
        return returned;
    }

    private class DBHelper extends SQLiteOpenHelper{
        private static final int VERSION=15;
        private static final String DB_NAME="nutrition";
        public DBHelper(Context context) {
            super(context,DB_NAME, null,VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            createTables(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if(newVersion>oldVersion){
                dropTables(db);
                createTables(db);

            }
        }
        private void createTables(SQLiteDatabase db){
            db.execSQL("create table " + MyContractClass.TypeProduct.TABLE_NAME + "("+
                    MyContractClass.TypeProduct.ID+" Integer primary key autoincrement, "+
                    MyContractClass.TypeProduct.IS_PRODUCT+" Integer not null, "+
                    MyContractClass.TypeProduct.SUGGESTION+"  varchar(50) not null unique, "+
                    MyContractClass.TypeProduct.NAME + " varchar(50) not null unique"+
                    ");");
            db.execSQL("create table " + MyContractClass.Product.TABLE_NAME + "("+
                    MyContractClass.Product.ID+" Integer primary key autoincrement," +
                    MyContractClass.Product.NAME + " varchar(50) not null, " +
                    MyContractClass.Product.SUGGESTION + " varchar(50) not null, " +
                    MyContractClass.Product.TYPE_ID + " Integer not null, " +
                    MyContractClass.Product.PROTEINS + " float not null, " +
                    MyContractClass.Product.FATS + " float not null, " +
                    MyContractClass.Product.CARBOHYDRATES + " float not null, " +
                    MyContractClass.Product.CALORIES + " float not null, " +
                    "UNIQUE ("+MyContractClass.Product.SUGGESTION+","+MyContractClass.Product.NAME +","+MyContractClass.Product.TYPE_ID+"),"+
                    "FOREIGN KEY ("+MyContractClass.Product.TYPE_ID+") REFERENCES "+MyContractClass.TypeProduct.TABLE_NAME+"("+MyContractClass.TypeProduct.ID+")"+
                    ");");
            db.execSQL("create table " + MyContractClass.Dish.TABLE_NAME+ "(" +
                    MyContractClass.Dish.ID+" integer primary key autoincrement," +
                    MyContractClass.Dish.ID_DISH + " long not null, " +
                    MyContractClass.Dish.ID_PRODUCT + " long not null, " +
                    MyContractClass.Dish.MASS + " integer not null," +
                    "UNIQUE("+MyContractClass.Dish.ID_DISH+","+MyContractClass.Dish.ID_PRODUCT+"));");

            db.execSQL("create table "+MyContractClass.Meal.TABLE_NAME+"("+
                    MyContractClass.Meal.ID+" integer primary key autoincrement," +
                    MyContractClass.Meal.NAME + " varchar(50) not null, " +
                    MyContractClass.Meal.ID_PRODUCT_OR_DISH + " long not null, " +
                    MyContractClass.Meal.MASS + " integer not null, " +
                    MyContractClass.Meal.ID_DAILY_DIET + " long not null," +
                    "UNIQUE("+MyContractClass.Meal.ID_DAILY_DIET+","+MyContractClass.Meal.NAME+"));");


            db.execSQL("create table "+MyContractClass.DailyDiet.TABLE_NAME+"("+
                    MyContractClass.DailyDiet.ID+" integer primary key autoincrement," +
                    MyContractClass.DailyDiet.DATE + " long not null" +
                    ");");
        }
        public void dropTables(SQLiteDatabase db){
            db.execSQL("drop table if exists "+MyContractClass.TypeProduct.TABLE_NAME);

            db.execSQL("drop table if exists "+MyContractClass.Product.TABLE_NAME);
            db.execSQL("drop table if exists "+MyContractClass.Dish.TABLE_NAME);
            db.execSQL("drop table if exists "+MyContractClass.Meal.TABLE_NAME);
            db.execSQL("drop table if exists "+MyContractClass.DailyDiet.TABLE_NAME);
        }
    }
}
