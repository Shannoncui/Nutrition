package com.korzinni.shura.nutrition;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;


import com.korzinni.shura.nutrition.content.MyContractClass;
import com.korzinni.shura.nutrition.dao.DAODailyDiet;
import com.korzinni.shura.nutrition.fragments.DailyDietFragment;
import com.korzinni.shura.nutrition.fragments.DailyDietSliderFragment;
import com.korzinni.shura.nutrition.fragments.MealFragment;
import com.korzinni.shura.nutrition.fragments.SettingsBMR;
import com.korzinni.shura.nutrition.fragments.UsedProduct;
import com.korzinni.shura.nutrition.fragments.AddDishFragment;
import com.korzinni.shura.nutrition.fragments.MainListFragment;
import com.korzinni.shura.nutrition.fragments.ProductListFragment;
import com.korzinni.shura.nutrition.model.DailyDiet;
import com.korzinni.shura.nutrition.model.Product;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Nutrition extends ActionBarActivity implements ProductListFragment.InteractionFragmentListener,
                                                            AddDishFragment.InteractionFragmentListener,
                                                            MainListFragment.InteractionFragmentListener,
                                                            UsedProduct.InteractionFragmentListener,
                                                            MealFragment.InteractionFragmentListener{
    public static final String FULL_LIST="full_list_tag";
    public static final String LIST_PRODUCT="list_product_tag";
    public static final String ADD_DISH="add_dish";
    public static final String ADD_PRODUCT="add_product";
    public static final String DAILY_DIET_SLIDER="daily_diet_slider";
    public String[] mealNames;
    String wichMeal;
    DAODailyDiet daoDailyDiet;
    private OnBackPressedListener onBackPressedListener;
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition);
        Log.d("tag", "Nutrition-- onCreate");
        /*mealNames=getResources().getStringArray(R.array.meal_list);
        daoDailyDiet=new DAODailyDiet(this);
        Calendar c=Calendar.getInstance();
        DailyDiet diet=daoDailyDiet.getDailyDietByDate(c.getTimeInMillis());
        if(diet==null){
            diet=new DailyDiet();
            diet.setDate(c.getTimeInMillis());
            diet.setId(daoDailyDiet.addDailyDiet(diet));
        }*/
        //add slider
        DailyDietSliderFragment fragment=(DailyDietSliderFragment)getSupportFragmentManager().findFragmentByTag(DAILY_DIET_SLIDER);
        if(fragment==null){
            fragment=new DailyDietSliderFragment();
        }
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.frame,fragment,DAILY_DIET_SLIDER).
                addToBackStack(null).
                commit();


    }

    @Override
    public void getFullList(String tag) {

        wichMeal=tag;
        fragmentManager=getSupportFragmentManager();
        Log.d("tag","Nutrition-- getFullList");
        MainListFragment listProducts=(MainListFragment)fragmentManager.findFragmentByTag(FULL_LIST);
        if(listProducts==null){
            listProducts=new MainListFragment();
        }
        fragmentManager.
                beginTransaction().
                replace(R.id.frame,listProducts,FULL_LIST).
                addToBackStack(null).
                commit();
    }
    //MainListFrafment interface
    @Override
    public void returnDish(Product product, Integer mass) {
        Log.d("tag","Nutrition-- returnDish");
        DailyDietSliderFragment fragment=(DailyDietSliderFragment)getSupportFragmentManager().findFragmentByTag(DAILY_DIET_SLIDER);
        FragmentManager fm=fragment.getCurrentDailyDietFagment().getChildFragmentManager();
        MealFragment mealFragment=(MealFragment)fm.findFragmentByTag(wichMeal);
        if(mealFragment==null){
            mealFragment=MealFragment.newInstance(null,wichMeal);
        }
        Log.d("tag", "Nutrition-- where return :" + mealFragment.toString());
        mealFragment.setDish(product, mass);
        Toast.makeText(this,"Блюдо добавленно",Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener==null){
            super.onBackPressed();
        }else{
            onBackPressedListener.doBack();
        }

    }
    // interface implemented in DeleteGroupWithDialogFragment
    public interface OnBackPressedListener{
       void doBack();
    }
    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener){
        this.onBackPressedListener=onBackPressedListener;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nutrition,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.fill_db:
            {


                Resources res=getResources();

                InputStreamReader isr=new InputStreamReader((res.openRawResource(R.raw.prob1)));
                BufferedReader bufReader=new BufferedReader(isr);
                String line;
                String name;
                long group=0;
                String tempString;
                String[] digits;
                float prot;
                float fat;
                float carbo;
                float calo;
                try {
                    while((line=bufReader.readLine())!=null){
                        if(line.length()<2){
                            continue;
                        }
                        int index=line.indexOf('>');
                        if(index<0){
                            ContentValues cv=new ContentValues();
                            cv.put(MyContractClass.TypeProduct.NAME,line.trim());
                            cv.put(MyContractClass.TypeProduct.SUGGESTION,line.trim().toLowerCase());
                            cv.put(MyContractClass.TypeProduct.IS_PRODUCT,1);
                            group= ContentUris.parseId(
                                    getContentResolver().
                                            insert(MyContractClass.TypeProduct.CONTENT_URI, cv));
                        }else {


                            name = line.substring(0, index);
                            tempString = line.substring(index+2, line.length()
                            );
                            digits = tempString.split(" ");

                            if (digits[0].contains("-")) {
                                prot = 0.0f;
                            } else {

                                prot = Float.valueOf(digits[0]);
                            }
                            if (digits[1].contains("-")) {
                                fat = 0.0f;
                            } else {

                                fat = Float.valueOf(digits[1]);
                            }
                            if (digits[2].contains("-")) {
                                carbo = 0.0f;
                            } else {

                                carbo = Float.valueOf(digits[2]);
                            }
                            if (digits[3].contains("-")) {
                                calo = 0.0f;
                            } else {

                                calo = Float.valueOf(digits[3]);
                            }

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MyContractClass.Product.TYPE_ID, group);
                            contentValues.put(MyContractClass.Product.NAME, name.trim());
                            contentValues.put(MyContractClass.Product.SUGGESTION, name.trim().toLowerCase());
                            contentValues.put(MyContractClass.Product.PROTEINS, prot);
                            contentValues.put(MyContractClass.Product.FATS, fat);
                            contentValues.put(MyContractClass.Product.CARBOHYDRATES, carbo);
                            contentValues.put(MyContractClass.Product.CALORIES, calo);
                            getContentResolver().insert(MyContractClass.Product.CONTENT_URI,contentValues);

                        }
                    }
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    Log.d("myLog", "NumberFormatException");
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    Log.d("myLog", "IOException");
                    e.printStackTrace();
                }
            }
                break;
            case R.id.show_full_list:
                fragmentManager=getSupportFragmentManager();

                MainListFragment fullList=(MainListFragment)fragmentManager.findFragmentByTag(FULL_LIST);
                if(fullList==null){
                    fullList=new MainListFragment();
                    fragmentManager.
                            beginTransaction().
                            replace(R.id.frame, fullList, FULL_LIST).
                            commit();
                }
                break;
            case R.id.settings:
                Intent intent=new Intent(this,SettingsBMR.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //ProductListFragment interface
    @Override
    public void returnProduct(Product product,Integer mass) {
        Log.d("tag","Nutrition-- returnProduct");
        AddDishFragment addDish=(AddDishFragment)fragmentManager.findFragmentByTag(ADD_DISH);
        if(addDish==null){
            addDish=AddDishFragment.newInstance(null);
        }

        addDish.setProduct(product,mass);
        Toast.makeText(this,"Продукт добавлен в состав бюда",Toast.LENGTH_SHORT ).show();
    }


    //AddDishFragment interface
    @Override
    public void getListProducts() {
        Log.d("tag","Nutrition-- getListProducts");
        ProductListFragment listProducts=(ProductListFragment)fragmentManager.findFragmentByTag(LIST_PRODUCT);
        if(listProducts==null){
            listProducts=new ProductListFragment();
        }
        fragmentManager.
                beginTransaction().
                replace(R.id.frame,listProducts,LIST_PRODUCT).
                addToBackStack(null).
                commit();
    }





}
