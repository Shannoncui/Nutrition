package com.korzinni.shura.nutrition.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.dao.DAODish;
import com.korzinni.shura.nutrition.dao.DAOGroupProduct;
import com.korzinni.shura.nutrition.dialogs.SetMassDialogFragment;
import com.korzinni.shura.nutrition.dialogs.SureDialogFragment;
import com.korzinni.shura.nutrition.model.Dish;
import com.korzinni.shura.nutrition.model.Meal;
import com.korzinni.shura.nutrition.model.Product;
import com.korzinni.shura.nutrition.views.CustomTextView;

import java.text.DecimalFormat;
import java.util.List;


public class MealFragment extends Fragment implements View.OnClickListener,View.OnLongClickListener,View.OnTouchListener{

    public static final String NAME="name";
    public static final String DAILY_DIET="daily_diet";
    public static final String MEAL="meal";
    public static final String STORE_TAG="save_tag";
    public static final int REQUEST_CODE_EDIT_DISH =303;
    public static final int REQUEST_CODE_SURE_DIALOG_DELETE_DISH =304;
    public String  TAG;

    View view;
    ImageButton showList;
    ImageButton edit;
    ImageButton delete;
    LayoutInflater inflater;
    LinearLayout layout;

    CustomTextView proteins;
    CustomTextView fats;
    CustomTextView carbohydrates;
    TextView calories;

    Meal meal;
    SparseArray<View> ingredients;
    int selectedId;
    InteractionFragmentListener listener;
    public static MealFragment newInstance(Meal meal,String tag){
        MealFragment fragment=new MealFragment();
        Bundle bnd=new Bundle();
        bnd.putParcelable(MEAL, meal);
        bnd.putString(STORE_TAG,tag);

        fragment.setArguments(bnd);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
       // Log.d("tag","onCreate :"+toString());
        ingredients=new SparseArray<View>();
        TAG=getArguments().getString(STORE_TAG);
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            meal=savedInstanceState.getParcelable(MEAL);
           // Log.d("tag","restore meal");
        }else{
            meal=getArguments().getParcelable(MEAL);
           // Log.d("tag","meal from args");
            if(meal==null){
                meal=new Meal();
               // Log.d("tag","new meal");
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Log.d("tag","onSaveInstanceState :"+toString());
        super.onSaveInstanceState(outState);
        outState.putParcelable(MEAL, meal);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      //  Log.d("tag","onCreateView :"+toString());
        this.inflater=inflater;
        view = inflater.inflate(R.layout.meal,null);
        proteins=(CustomTextView)view.findViewById(R.id.meal_display_protein);
        fats=(CustomTextView)view.findViewById(R.id.meal_display_fats);
        carbohydrates=(CustomTextView)view.findViewById(R.id.meal_display_carbohydrates);
        calories=(TextView)view.findViewById(R.id.meal_display_calories);
        TextView nameTextView=(TextView)view.findViewById(R.id.meal_name);
        nameTextView.setText(meal.getName());
        layout=(LinearLayout)view.findViewById(R.id.meal_layout);
        showList=(ImageButton)view.findViewById(R.id.meal_show_list);
        showList.setOnClickListener(this);
        edit=(ImageButton)view.findViewById(R.id.meal_edit);
        edit.setOnClickListener(this);
        delete=(ImageButton)view.findViewById(R.id.meal_delete);
        delete.setOnClickListener(this);
        //for hidden pseudoActionMode after dismiss items
        LinearLayout layout1=(LinearLayout)view.findViewById(R.id.indicators);
        RelativeLayout layout2=(RelativeLayout)view.findViewById(R.id.buttons_and_other_stuff);
        layout1.setOnTouchListener(this);
        layout2.setOnTouchListener(this);
        return view;
    }
    @Override
    public void onResume() {
       // Log.d("tag","onResume :"+toString());
        super.onResume();
        createListIngredients(layout,inflater);
        fillTextViews();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.meal_show_list:
                listener.getFullList(TAG);
                break;
            case R.id.meal_edit:
                cancelSelection();
                showDialogEditMassIngredint();
                break;
            case R.id.meal_delete:
                cancelSelection();
                showDialogSureDeleteIngredient();
                break;
            default:
                hideActions();
                cancelSelection();
                break;

        }
    }
    void showDialogSureDeleteIngredient() {
        SureDialogFragment dialog= SureDialogFragment.newInstance("Удалить ингридиент",
                "Вы уверены что хотите удалить это блюдо?",
                "Да",
                "Нет");
        dialog.setTargetFragment(this, REQUEST_CODE_SURE_DIALOG_DELETE_DISH);
        dialog.show(getFragmentManager(),null);
    }
    void showDialogEditMassIngredint(){//ingredient
        SetMassDialogFragment dialog=new SetMassDialogFragment();
        Bundle bnd=new Bundle();
        bnd.putParcelable(SetMassDialogFragment.PASS_PRODUCT,meal.getProducts().get(selectedId));
        dialog.setArguments(bnd);
        dialog.setTargetFragment(this, REQUEST_CODE_EDIT_DISH);
        dialog.show(getFragmentManager(),null);
    }

    private void createListIngredients(ViewGroup parent,LayoutInflater inflater){
        parent.removeAllViews();
        List<Product> products=meal.getProducts();
        List<Integer> mass=meal.getMass();
        ingredients=new SparseArray<View>();
        for(int i=0;i<products.size();i++){
            View view=inflater.inflate(android.R.layout.simple_list_item_2,null);
            view.setId(i);
            view.setBackgroundResource(R.drawable.asdasd);
            view.setOnLongClickListener(this);
            view.setOnClickListener(this);
            ingredients.put(i, view);
            TextView nameView=(TextView)view.findViewById(android.R.id.text1);
            TextView massView=(TextView)view.findViewById(android.R.id.text2);
            nameView.setText(products.get(i).getName());
            massView.setText("Масса: " + mass.get(i).toString() + " г.");
            parent.addView(view);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){

            case REQUEST_CODE_EDIT_DISH:
                if(resultCode== SetMassDialogFragment.OK_RESULT_CODE){
                    int m=data.getIntExtra(SetMassDialogFragment.PASS_MASS, 0);
                    meal.editMass(selectedId,m);
                    createListIngredients(layout,inflater);
                    hideActions();
                    fillTextViews();
                }
                break;
            case REQUEST_CODE_SURE_DIALOG_DELETE_DISH:
                if(resultCode==SureDialogFragment.OK){
                    meal.removeIngredient(selectedId);

                    createListIngredients(layout,inflater);
                    hideActions();
                    fillTextViews();

                }
        }
    }
    private void fillTextViews(){
        DecimalFormat f=new DecimalFormat("0.0");
        proteins.setText(f.format(meal.getProteins()),null);
        carbohydrates.setText(f.format(meal.getCarbohydrates()),null);
        fats.setText(f.format(meal.getFats()),null);
        calories.setText(f.format(meal.getCalories()),null);
    }
    @Override
    public boolean onLongClick(View v) {
        cancelSelection();
        showActions();
        selectedId=v.getId();
        return true;
    }


    public void hideActions(){
        showList.setVisibility(View.VISIBLE);
        edit.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
    }
    private void showActions(){
        showList.setVisibility(View.GONE);
        edit.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);
    }
    public void cancelSelection(){
        View view=ingredients.get(selectedId);
            if(view!=null){
                view.setSelected(false);
            }

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener=(InteractionFragmentListener)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
    }

    public interface InteractionFragmentListener{

        public void getFullList(String tagMeal);
    }
    public void setDish(Product product,Integer mass) {
        Log.d("tag","setDish "+toString());
        meal.addIngredient(product, mass);

        if(proteins!=null){//if fragment doesn't in top after change configuration onCreateView doesn't call(only onCreate), and view component can be null
            fillTextViews();

        }

    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            hideActions();
            cancelSelection();
            return true;
        }
        return false;
    }
    public void setMeal(Meal meal){
        this.meal=meal;
        createListIngredients(layout,inflater);
        hideActions();
        fillTextViews();
    }

}
