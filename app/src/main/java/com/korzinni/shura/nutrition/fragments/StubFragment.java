package com.korzinni.shura.nutrition.fragments;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.content.MyContractClass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StubFragment extends Fragment {
    Bundle attrs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        attrs=getArguments();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.stub_fragment,null);
        TextView text=(TextView)view.findViewById(R.id.text);
        if(attrs!=null){
            text.setText(attrs.getString("text"));
        }
        Button addButton=(Button)view.findViewById(R.id.button_x);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


        Resources res=getActivity().getResources();

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
                    group= ContentUris.parseId(getActivity().
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
                    getActivity().getContentResolver().insert(MyContractClass.Product.CONTENT_URI,contentValues);

                }
            }
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            Log.d("myLog","NumberFormatException");
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("myLog", "IOException");
            e.printStackTrace();
        }
    }

        });
        Button searchButton=(Button)view.findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onSearchRequested();
            }
        });
        Button fillButton=(Button)view.findViewById(R.id.button_y);
        fillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductFragment mFragment=new AddProductFragment();
               getFragmentManager()
                       .beginTransaction()
                       .replace(R.id.frame,mFragment)
                       .commit();
            }
        });
        return view;
    }
}
