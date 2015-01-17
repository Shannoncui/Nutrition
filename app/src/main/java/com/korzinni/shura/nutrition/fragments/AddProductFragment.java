package com.korzinni.shura.nutrition.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;


import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.dao.DAOProduct;
import com.korzinni.shura.nutrition.dao.DAOGroupProduct;
import com.korzinni.shura.nutrition.dialogs.SureDialogFragment;
import com.korzinni.shura.nutrition.model.Product;
import com.korzinni.shura.nutrition.model.TypeProduct;


public class AddProductFragment extends Fragment {
    public static final String PRODUCT="product";
    public static final int REQUEST_CODE=101;

    MultiAutoCompleteTextView type;
    EditText name;
    EditText proteins;
    EditText fats;
    EditText carbohydrates;
    EditText calories;
    ArrayAdapter typeCompletionAdapter;
    Product product;
    DAOGroupProduct daoType;
    DAOProduct daoProduct;

    public static AddProductFragment newInstance(Product product) {
        AddProductFragment instance = new AddProductFragment();
        Bundle bnd = new Bundle();
        bnd.putParcelable(PRODUCT, product);
        instance.setArguments(bnd);
        return instance;
    }
   @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       if(savedInstanceState!=null){
           product=savedInstanceState.getParcelable(PRODUCT);
       }else{
           product=getArguments().getParcelable(PRODUCT);
           if(product==null){
               product=new Product();
           }
       }
       daoType=new DAOGroupProduct(getActivity());
       daoProduct=new DAOProduct(getActivity());
       setHasOptionsMenu(true);
   }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(PRODUCT,product);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_product,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_product:
            {
                String stringName=name.getText().toString();
                if(TextUtils.isEmpty(stringName)){
                    Toast.makeText(getActivity(),"Введите пожалуйста имя.",Toast.LENGTH_SHORT).show();
                    return false;
                }
                String stringType = type.getText().toString();
                TypeProduct typeProduct=daoType.checkExistenceGroupProduct(stringType);
                long typeId=-1;
                if(typeProduct==null) {// such group doesn't exist
                    typeProduct=new TypeProduct(stringType,true);
                    typeId=daoType.addGroupProduct(typeProduct);
                }else if(typeProduct.isProduct()){//such group exist for products
                    typeId=typeProduct.getId();
                }else{//such group exist for dishes
                    Toast.makeText(getActivity(),"Данная группа сущетвует для добавления блюд, переименуйте группу пожалуйста.",Toast.LENGTH_LONG).show();
                    return false;
                }
                String stringProteins=proteins.getText().toString();
                float floatProteins=TextUtils.isEmpty(stringProteins)? 0:Float.valueOf(stringProteins);
                String stringFats=fats.getText().toString();
                float floatFats=TextUtils.isEmpty(stringFats)? 0:Float.valueOf(stringFats);
                String stringCarbohydrates=carbohydrates.getText().toString();
                float floatCarbohydrates=TextUtils.isEmpty(stringCarbohydrates)? 0:Float.valueOf(stringCarbohydrates);
                String stringCalories=calories.getText().toString();
                float floatCalories=TextUtils.isEmpty(stringCalories)? 0:Float.valueOf(stringCalories);


                product.setName(stringName);
                product.setTypeId(typeId);
                product.setProteins(floatProteins);
                product.setCarbohydrates(floatCarbohydrates);
                product.setFats(floatFats);
                product.setCalories(floatCalories);

                long id=daoProduct.checkExistenceProduct(product);
                if(id>=0){
                    showDialog();
                }else{
                    daoProduct.addProduct(product);
                    Toast.makeText(getActivity(),"Продукт добавлен",Toast.LENGTH_SHORT).show();
                }
            }
                getFragmentManager().popBackStack();

                break;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_product, null);

        type = (MultiAutoCompleteTextView) view.findViewById(R.id.add_product_type);
        typeCompletionAdapter=new ArrayAdapter(getActivity(),android.R.layout.simple_dropdown_item_1line,daoType.getAllGroupProducts());

        type.setAdapter(typeCompletionAdapter);
        type.setTokenizer(new MyTokenizer());

        name = (EditText) view.findViewById(R.id.add_product_name);
        proteins = (EditText) view.findViewById(R.id.add_product_proteins);
        fats = (EditText) view.findViewById(R.id.add_product_fats);
        carbohydrates = (EditText) view.findViewById(R.id.add_product_carbohydrates);
        calories = (EditText) view.findViewById(R.id.add_product_calories);
        if(product!=null){
            long typeId=product.getTypeId();
            type.setText(typeId>0 ? daoType.getGroupById(typeId).getName() : "");
            name.setText(product.getName());
            proteins.setText(String.valueOf(product.getProteins()));
            fats.setText(String.valueOf(product.getFats()));
            carbohydrates.setText(String.valueOf(product.getCarbohydrates()));
            calories.setText(String.valueOf(product.getCalories()));

        }
        return view;
    }


   static class MyTokenizer implements MultiAutoCompleteTextView.Tokenizer {

        @Override
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != ' ') {
                i--;
            }
            while (i < cursor && text.charAt(i) == ' ') {
                i++;
            }

            return i;
        }

        @Override
        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == ' ') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        @Override
        public CharSequence terminateToken(CharSequence text) {
            int i = text.length();

            while (i > 0 && text.charAt(i - 1) == ' ') {
                i--;
            }

            if (i > 0 && text.charAt(i - 1) == ' ') {
                return text;
            } else {
                if (text instanceof Spanned) {
                    SpannableString sp = new SpannableString(text + " ");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                            Object.class, sp, 0);
                    return sp;
                } else {
                    return text + " ";
                }

            }
        }
    }

    void showDialog() {
        SureDialogFragment dialog= SureDialogFragment.newInstance("Добавление продукта",
                "Данный продукт уже существует в этой группе, переписать его?",
                "Да",
                "Нет");
        dialog.setTargetFragment(this,REQUEST_CODE);
        dialog.show(getFragmentManager(),"dialog");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode){
            case SureDialogFragment.OK:
                daoProduct.updateProduct(product);
                break;
            case SureDialogFragment.CANCEL:
                break;
        }
    }
}

