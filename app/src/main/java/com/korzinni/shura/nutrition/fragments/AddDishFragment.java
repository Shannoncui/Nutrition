package com.korzinni.shura.nutrition.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;


import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.dao.DAODish;
import com.korzinni.shura.nutrition.dao.DAOGroupProduct;
import com.korzinni.shura.nutrition.dialogs.SetMassDialogFragment;
import com.korzinni.shura.nutrition.dialogs.SureDialogFragment;
import com.korzinni.shura.nutrition.model.Dish;
import com.korzinni.shura.nutrition.model.Product;
import com.korzinni.shura.nutrition.model.TypeProduct;
import com.korzinni.shura.nutrition.views.CustomTextView;

import java.text.DecimalFormat;
import java.util.List;

public class AddDishFragment extends Fragment implements View.OnLongClickListener{

    public static final String DISH="dish";
    public static final String NAME="name";
    public static final String MASS="mass";
    public static final int REQUEST_CODE_SURE_DIALOG_UPDATE =202;
    public static final int REQUEST_CODE_EDIT_INGREDIENT =203;
    public static final int REQUEST_CODE_SURE_DIALOG_DELETE_INGREDIENTS =204;

    ArrayAdapter typeCompletionAdapter;
    MultiAutoCompleteTextView type;
    EditText name;
    DAOGroupProduct daoType;
    DAODish daoDish;
    CustomTextView proteins;
    CustomTextView fats;
    CustomTextView carbohydrates;
    TextView calories;
    LayoutInflater inflater;
    LinearLayout layout;
    Dish dish;
    ActionMode actionMode;
    SparseArray<View> ingredients;
    int selectedId;
    private InteractionFragmentListener listener;

    public static AddDishFragment newInstance(Dish dish){
        AddDishFragment instance=new AddDishFragment();
        Bundle bnd=new Bundle();
        bnd.putParcelable(DISH,dish);
        instance.setArguments(bnd);
        return instance;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "AddDishFragment-- onCreate");
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            dish=savedInstanceState.getParcelable(DISH);
        }else{
            dish=getArguments().getParcelable(DISH);
            if(dish==null){
                dish=new Dish();
            }
        }
        daoType=new DAOGroupProduct(getActivity());
        daoDish=new DAODish(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(DISH, dish);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.add_dish,null);
        this.inflater=inflater;
        layout=(LinearLayout)view.findViewById(R.id.linear_layout);
        proteins=(CustomTextView)view.findViewById(R.id.add_dish_display_protein);
        fats=(CustomTextView)view.findViewById(R.id.add_dish_display_fats);
        carbohydrates=(CustomTextView)view.findViewById(R.id.add_dish_display_carbohydrates);
        calories=(TextView)view.findViewById(R.id.add_dish_display_calories);
        type = (MultiAutoCompleteTextView) view.findViewById(R.id.add_dish_type);
        typeCompletionAdapter=new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line,daoType.getAllGroupDish());

        type.setAdapter(typeCompletionAdapter);
        type.setTokenizer(new AddProductFragment.MyTokenizer());

        name = (EditText) view.findViewById(R.id.add_dish_name);
        if(dish!=null){
            long typeId=dish.getTypeId();
            type.setText(typeId>0 ? daoType.getGroupById(typeId).getName() : "");
            name.setText(dish.getName());
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        createListIngredients(layout,inflater);
        fillTextViews();
    }

    public void setProduct(Product product,Integer mass) {

        dish.addIngredient(product, mass);

        if(proteins!=null){//if fragment doesn't in top after change configuration onCreateView doesn't call(only onCreate), and view component can be null
           fillTextViews();
        }

    }

    public interface InteractionFragmentListener{

        public void getListProducts();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_dish,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.save_new_dish:
                String stringName=name.getText().toString();
                if(TextUtils.isEmpty(stringName)){
                    Toast.makeText(getActivity(),"Введите пожалуйста название.",Toast.LENGTH_SHORT).show();
                    return false;
                }
                String stringType = type.getText().toString();
                TypeProduct typeProduct=daoType.checkExistenceGroupProduct(stringType);
                long typeId=-1;
                if(typeProduct==null) {// such group doesn't exist
                    typeProduct=new TypeProduct(stringType,false);
                    typeId=daoType.addGroupProduct(typeProduct);
                }else if(!typeProduct.isProduct()){//such group exist for dishes
                    typeId=typeProduct.getId();
                }else{//such group exist for products
                    Toast.makeText(getActivity(),"Данная группа сущетвует для добавления продуктов, переименуйте группу пожалуйста.",Toast.LENGTH_LONG).show();
                    return false;
                }
                dish.setName(stringName);
                dish.setTypeId(typeId);
                long id=daoDish.checkExistenceDish(dish);

                if(id>=0){
                    dish.setId(id);
                    showDialogSureUpdate();
                }else{
                    daoDish.addDish(dish);
                    Toast.makeText(getActivity(),"Блюдо добавлено",Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }
                break;
            case R.id.show_list_products:
                listener.getListProducts();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void createListIngredients(ViewGroup parent,LayoutInflater inflater){
        parent.removeAllViews();
        List<Product> products=dish.getProducts();
        List<Integer> mass=dish.getMass();
        ingredients=new SparseArray<View>();
        for(int i=0;i<products.size();i++){
            View view=inflater.inflate(android.R.layout.simple_list_item_2,null);
            view.setId(i);
            view.setBackgroundResource(R.drawable.asdasd);
            view.setOnLongClickListener(this);
            ingredients.put(i, view);
            TextView nameView=(TextView)view.findViewById(android.R.id.text1);
            TextView massView=(TextView)view.findViewById(android.R.id.text2);
            nameView.setText(products.get(i).getName());
            massView.setText("Масса: " + mass.get(i).toString() + " г.");
            parent.addView(view);
        }
    }
    @Override
    public boolean onLongClick(View v) {
        if (actionMode != null) {
            return false;
        }
        // Start the CAB using the ActionMode.Callback defined above
        actionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(mActionModeCallback);
        v.setSelected(true);
        selectedId=v.getId();
        return true;
    }
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.edit_delete, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_edit:
                    showDialogEditMassIngredint();
                    return true;
                case R.id.menu_delete:
                    showDialogSureDeleteIngredient();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            View view;
            if ((view=ingredients.get(selectedId))!=null){
                view.setSelected(false);
            }
        }
    };

    void showDialogSureUpdate() {
        SureDialogFragment dialog= SureDialogFragment.newInstance("Добавление блюда",
                "Данное блюдо уже существует в этой группе, переписать его?",
                "Да",
                "Нет");
        dialog.setTargetFragment(this, REQUEST_CODE_SURE_DIALOG_UPDATE);
        dialog.show(getFragmentManager(),null);
    }
    void showDialogSureDeleteIngredient() {
        SureDialogFragment dialog= SureDialogFragment.newInstance("Удалить ингридиент",
                "Вы уверены что хотите удалить этот ингридиент?",
                "Да",
                "Нет");
        dialog.setTargetFragment(this, REQUEST_CODE_SURE_DIALOG_DELETE_INGREDIENTS);
        dialog.show(getFragmentManager(),null);
    }
    void showDialogEditMassIngredint(){//ingredient
        SetMassDialogFragment dialog=new SetMassDialogFragment();
        Bundle bnd=new Bundle();
        bnd.putParcelable(SetMassDialogFragment.PASS_PRODUCT,dish.getProducts().get(selectedId));
        dialog.setArguments(bnd);
        dialog.setTargetFragment(this,REQUEST_CODE_EDIT_INGREDIENT);
        dialog.show(getFragmentManager(),null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_CODE_SURE_DIALOG_UPDATE:
                    if(resultCode==SureDialogFragment.OK){
                        daoDish.updateDish(dish);
                        Toast.makeText(getActivity(),"Блюдо исправленно",Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();
                    }
                break;
            case REQUEST_CODE_EDIT_INGREDIENT:
                if(resultCode==SetMassDialogFragment.OK_RESULT_CODE){
                    int m=data.getIntExtra(SetMassDialogFragment.PASS_MASS, 0);
                    dish.editMass(selectedId,m);
                    createListIngredients(layout,inflater);
                    if(actionMode!=null)
                    actionMode.finish();
                }
                break;
            case REQUEST_CODE_SURE_DIALOG_DELETE_INGREDIENTS:
                if(resultCode==SureDialogFragment.OK){
                    dish.removeIngredient(selectedId);
                    Log.d("tag","count ingredients: "+dish.getProducts().size());
                    Log.d("tag","selected id: "+selectedId);
                    for(int i=0;i<dish.getProducts().size();i++){
                        Log.d("tag",dish.getProducts().get(i).getName()+" : "+dish.getMass().get(i));
                        Log.d("tag","prod: "+dish.getProteins());
                        Log.d("tag","fats: "+dish.getFats());
                        Log.d("tag","carbo: "+dish.getCarbohydrates());
                        Log.d("tag","calo: "+dish.getCalories());
                    }
                    Log.d("tag","prod: "+dish.getProteins());
                    Log.d("tag","fats: "+dish.getFats());
                    Log.d("tag","carbo: "+dish.getCarbohydrates());
                    Log.d("tag","calo: "+dish.getCalories());
                    createListIngredients(layout,inflater);
                    fillTextViews();
                    if(actionMode!=null)
                    actionMode.finish();

                }
        }
    }
    private void fillTextViews(){
        DecimalFormat f=new DecimalFormat("0.0");
        proteins.setText(f.format(dish.getProteins()),null);
        carbohydrates.setText(f.format(dish.getCarbohydrates()),null);
        fats.setText(f.format(dish.getFats()),null);
        calories.setText(f.format(dish.getCalories()),null);
    }
}
