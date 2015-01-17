package com.korzinni.shura.nutrition.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.content.MyContractClass;
import com.korzinni.shura.nutrition.dao.DAODailyDiet;
import com.korzinni.shura.nutrition.dao.DAODish;
import com.korzinni.shura.nutrition.dao.DAOGroupProduct;
import com.korzinni.shura.nutrition.dao.DAOMeal;
import com.korzinni.shura.nutrition.model.DailyDiet;
import com.korzinni.shura.nutrition.model.Dish;
import com.korzinni.shura.nutrition.model.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/*
* fragment - container for several other fragments MealFragments
* in the DB DailyDiet it is list of Meal + date
* */
public class DailyDietFragment extends Fragment {
    public static final String MEAL="meal";
    public static final String DAILY_DIET="dailyDiet";
    public static final String TIME_IN_MILLIS="time";
    DailyDiet diet;
    DAOMeal daoMeal;
    DAODailyDiet daoDailyDiet;
    List<Meal> meals;
    long date;
    View view;
    TextView text;
    public static DailyDietFragment newInstance(long date){
        DailyDietFragment fragment = new DailyDietFragment();
        Bundle bnd = new Bundle();
        bnd.putLong(TIME_IN_MILLIS, date);
        fragment.setArguments(bnd);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "=============onCreate :" + toString());
        super.onCreate(savedInstanceState);
        daoDailyDiet=new DAODailyDiet(getActivity());
        if (savedInstanceState != null) {
            diet = savedInstanceState.getParcelable(DAILY_DIET);
        } else {
            if (date == 0) {// setDate() has not called
                date = getArguments().getLong(TIME_IN_MILLIS);
            }
            diet = daoDailyDiet.getDailyDietByDate(date);
            if (diet == null) {
                diet = new DailyDiet();
                diet.setDate(date);
                diet.setId(daoDailyDiet.addDailyDiet(diet));

            }
        }
            daoMeal = new DAOMeal(getActivity());
            meals = daoMeal.getMealByDailyDiet(diet.getId());
            if (meals.isEmpty()) {// if dailyDiet new
                String[] mealNames = getActivity().getResources().getStringArray(R.array.meal_list);
                for (String m : mealNames) {
                    Meal meal = new Meal();
                    meal.setName(m);
                    meal.setDailyDiet(diet.getId());
                    meals.add(meal);
                }
            }



    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DAILY_DIET,diet);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("tag","onCreateView :"+toString());
        view=inflater.inflate(R.layout.daily_diet,null);
        text=(TextView)view.findViewById(R.id.daily_diet_date);
        text.setText(diet.getName());
        int[] frames=new int[]{R.id.frame1,R.id.frame2,R.id.frame3,R.id.frame4,R.id.frame5};
        int i=0;
        for(Meal m:meals){
            MealFragment fragment=(MealFragment)getChildFragmentManager().findFragmentByTag(MEAL+i);
            //Log.d("tag","finded MealFragment not null:"+(fragment!=null));
            if(fragment==null){
                fragment=MealFragment.newInstance(m,MEAL+i);
                //Log.d("tag","new MealFragment :"+fragment.toString());
            }
            getChildFragmentManager().beginTransaction().replace(frames[i],fragment,MEAL+i).addToBackStack(null).commit();
            i++;
        }
        return view;
    }
    /*
    * save changes old DailyDiet if any
    *
    * */
    public void setDate(long date, Context context){
        SimpleDateFormat f=new SimpleDateFormat("dd-MM-yyyy  HH:mm:ss", Locale.getDefault());
        Log.d("tag","setDate"+f.format(date));
        this.date=date;
        daoDailyDiet=new DAODailyDiet(context);
        diet = daoDailyDiet.getDailyDietByDate(date);
        if (diet == null) {
            diet = new DailyDiet();
            diet.setDate(date);
            diet.setId(daoDailyDiet.addDailyDiet(diet));
        }
        daoMeal = new DAOMeal(context);
        meals = daoMeal.getMealByDailyDiet(diet.getId());
        if (meals.isEmpty()) {// if dailyDiet new
            String[] mealNames = context.getResources().getStringArray(R.array.meal_list);
            for (String m : mealNames) {
                Meal meal = new Meal();
                meal.setName(m);
                meal.setDailyDiet(diet.getId());
                meals.add(meal);
            }
        }
        text.setText(diet.getName());
        
        int[] frames=new int[]{R.id.frame1,R.id.frame2,R.id.frame3,R.id.frame4,R.id.frame5};
        int i=0;
        for(Meal m:meals) {
            MealFragment fragment = (MealFragment) getChildFragmentManager().findFragmentByTag(MEAL + i);
            if (fragment == null) {
                fragment = MealFragment.newInstance(m, MEAL + i);
            }else{
                fragment.setMeal(m);
            }
            getChildFragmentManager().beginTransaction().replace(frames[i], fragment, MEAL + i).addToBackStack(null).commit();
            i++;
        }
    }

}

