package com.korzinni.shura.nutrition.content;

import android.net.Uri;

public final class MyContractClass {
    public static final String AUTHORITY="com.korzinin.Nutrition.provider";
    public static final Uri AUTHORITY_URI=Uri.parse("content://"+AUTHORITY);

    public static class TypeProduct{
        public static final String TABLE_NAME="types";
        public static final Uri CONTENT_URI=Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE="vnd.android.cursor.dir/vnd.korzinin.Nutrition.provider.types";
        public static final String CONTENT_ITEM_TYPE="vnd.android.cursor.item/vnd.korzinin.Nutrition.provider.types";

        public static final String ID="_id";
        public static final String NAME="name";
        public static final String SUGGESTION="suggestion";
        public static final String IS_PRODUCT="is_product";


    }
    public static class Product extends TypeProduct{
        public static final String TABLE_NAME="products";
        public static final Uri CONTENT_URI=Uri.withAppendedPath(AUTHORITY_URI, Product.TABLE_NAME);
        public static final String CONTENT_TYPE="vnd.android.cursor.dir/vnd.korzinin.Nutrition.provider.products";
        public static final String CONTENT_ITEM_TYPE="vnd.android.cursor.item/vnd.korzinin.Nutrition.provider.products";

        public static final String TYPE_ID="type_id";
        public static final String PROTEINS="proteins";
        public static final String FATS="fats";
        public static final String CARBOHYDRATES="carbohydrates";
        public static final String CALORIES="calories";
    }
    public static class Dish extends Product{
        public static final String TABLE_NAME="dishes";
        public static final Uri CONTENT_URI=Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE="vnd.android.cursor.dir/vnd.korzinin.Nutrition.provider.dishes";
        public static final String CONTENT_ITEM_TYPE="vnd.android.cursor.item/vnd.korzinin.Nutrition.provider.dishes";

        public static final String ID_PRODUCT="_id_product";
        public static final String ID_DISH="_id_dish";
        public static final String MASS="mass";

    }
    public static class Meal{
        public static final String TABLE_NAME="meals";
        public static final Uri CONTENT_URI=Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE="vnd.android.cursor.dir/vnd.korzinin.Nutrition.provider.meals";
        public static final String CONTENT_ITEM_TYPE="vnd.android.cursor.item/vnd.korzinin.Nutrition.provider.meals";

        public static final String ID="_id";
        public static final String NAME="name";
        public static final String ID_PRODUCT_OR_DISH ="id_product_or_dish";
        public static final String ID_DAILY_DIET="id_daily_diet";
        public static final String MASS="mass";

    }

    public static class DailyDiet{
        public static final String TABLE_NAME="daily_diets";
        public static final Uri CONTENT_URI=Uri.withAppendedPath(AUTHORITY_URI, TABLE_NAME);
        public static final String CONTENT_TYPE="vnd.android.cursor.dir/vnd.korzinin.Nutrition.provider.daily_diets";
        public static final String CONTENT_ITEM_TYPE="vnd.android.cursor.item/vnd.korzinin.Nutrition.provider.daily_diets";

        public static final String ID="_id";
        public static final String DATE="date";

    }






}
