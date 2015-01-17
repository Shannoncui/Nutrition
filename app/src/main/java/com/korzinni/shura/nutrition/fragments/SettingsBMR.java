package com.korzinni.shura.nutrition.fragments;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.korzinni.shura.nutrition.R;

public class SettingsBMR extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    TextView allowance;
    TextView dailyProteins;
    TextView dailyFats;
    TextView dailyCarbohydrates;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_fragment);
        addPreferencesFromResource(R.xml.bmr);

        PreferenceListener listener=new PreferenceListener();
        Preference weight_pref=getPreferenceScreen().findPreference("weight");
        weight_pref.setOnPreferenceChangeListener(listener);

        Preference height_pref=getPreferenceScreen().findPreference("height");
        height_pref.setOnPreferenceChangeListener(listener);

        Preference age_pref=getPreferenceScreen().findPreference("age");
        age_pref.setOnPreferenceChangeListener(listener);
        allowance=(TextView)findViewById(R.id.allowance);
        dailyProteins=(TextView)findViewById(R.id.daily_prot);
        dailyFats=(TextView)findViewById(R.id.daily_fats);
        dailyCarbohydrates=(TextView)findViewById(R.id.daily_carbo);
        fillFields(evaluateAllowance());
    }



    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof ListPreference) {
            ListPreference listPref = (ListPreference) pref;
            pref.setSummary(listPref.getEntry());
        }
        if (pref instanceof EditTextPreference) {
            EditTextPreference listPref = (EditTextPreference) pref;
            pref.setSummary(listPref.getText());
        }
        fillFields(evaluateAllowance());

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
        //PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit();
        PreferenceManager.setDefaultValues(this,R.xml.bmr,false);
        initSummary(getPreferenceScreen());




    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
    private void initSummary(Preference p){
        if(p instanceof PreferenceGroup){
            PreferenceGroup pg=(PreferenceGroup)p;
            for(int i=0;i<pg.getPreferenceCount();i++){
                initSummary(pg.getPreference(i));
            }

        } else{
            updateSummary(p);

        }
    }
    private void updateSummary(Preference p){
        if(p instanceof ListPreference){
            ListPreference lp=(ListPreference)p;
            lp.setSummary(lp.getEntry());
        }if(p instanceof EditTextPreference){
            EditTextPreference etp=(EditTextPreference)p;
            etp.setSummary(etp.getText());
        }
    }
    float evaluateAllowance(){
        String stringWeight=getPreferenceScreen().getSharedPreferences().getString("weight","0.0");
        String stringAge=getPreferenceScreen().getSharedPreferences().getString("age","0.0");
        String stringHeight=getPreferenceScreen().getSharedPreferences().getString("height","0.0");
        String stringSex=getPreferenceScreen().getSharedPreferences().getString("sex","m");
        String stringLevel=getPreferenceScreen().getSharedPreferences().getString("level","0.0");

        float weight=Float.valueOf(stringWeight);
        float age=Float.valueOf(stringAge);
        float height=Float.valueOf(stringHeight);
        float level=Float.valueOf(stringLevel);
        float bmr=0.0f;
        if(stringSex.equals("m")){//mens
            bmr=88.36f+13.4f*weight+4.8f*height-5.7f*age;
        }else{//womens
            Log.d("myApp","evaluate for women");
            bmr=447.6f+9.2f*weight+3.1f*height+4.3f*age;
        }
        return bmr*level;

    }
    private void fillFields(float f){
        float prot;
        float fats;
        float carbo;


        String target=getPreferenceScreen().getSharedPreferences().getString("target","0");

        switch(Integer.valueOf(target)){
            default://hold the weight(ration energy nutrient's 14%30%56%)
                prot=f*0.14f/4.1f;
                fats=f*0.30f/9.3f;
                carbo=f*0.56f/4.1f;



                break;
            case 1://lost the weight (ration energy nutrient's 30%20%50% )allowance-20%
                f*=0.80f;
                prot=f*0.30f/4.1f;
                fats=f*0.20f/9.3f;
                carbo=f*0.50f/4.1f;

                break;
            case 2://set of muscle mass for endomorph(ration energy nutrient's 45%15%40%)allowance+20%
                f*=1.20f;
                prot=f*0.45f/4.1f;
                fats=f*0.15f/9.3f;
                carbo=f*0.40f/4.1f;


                break;
            case 3://set of muscle mass for mesomorph(ration energy nutrient's 35%15%50%)allowance+20%
                f*=1.20f;
                prot=f*0.35f/4.1f;
                fats=f*0.15f/9.3f;
                carbo=f*0.50f/4.1f;


                break;
            case 4://set of muscle mass for ectomorph(ration energy nutrient's 25%20%55%)allowance+20%
                f*=1.20f;
                prot=f*0.25f/4.1f;
                fats=f*0.20f/9.3f;
                carbo=f*0.55f/4.1f;


                break;

        }
        allowance.setText(String.valueOf((int)f));
        dailyProteins.setText(String.valueOf((int)prot));
        dailyFats.setText(String.valueOf((int)fats));
        dailyCarbohydrates.setText(String.valueOf((int)carbo));
    }
    class PreferenceListener implements Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Boolean rtnval = true;
            if (preference.getKey().equals("weight")||
                    preference.getKey().equals("age")||
                    preference.getKey().equals("height")) {

                try{
                    Float.valueOf(newValue.toString());
                }catch (NumberFormatException e) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle("Invalid Input");
                    builder.setMessage("Something's gone wrong...");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    rtnval=false;
                }



            }
            return rtnval;

        }


    }
}
