package com.korzinni.shura.nutrition.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.model.Product;


public class SetMassDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    public static final String PASS_PRODUCT="product";
    public static final String PASS_MASS="mass_pass";
    public static final int OK_RESULT_CODE=101;
    public static final int CANCEL_RESULT_CODE=102;

    EditText mass;
    TextView text;
    Product product;
    View view;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater=getActivity().getLayoutInflater();
        view=inflater.inflate(R.layout.set_mass,null);
        product=getArguments().getParcelable(PASS_PRODUCT);
        mass=(EditText)view.findViewById(R.id.set_mass_edit_mass);
        text=(TextView)view.findViewById(R.id.set_mass_name);
        text.setText(product.getName());
        text.append(" (г):");
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setTitle("Укажите массу")
                .setView(view)
                .setNegativeButton("Отмена",this)
                .setPositiveButton("Ok",this);
        return builder.create();
    }



    @Override
    public void onClick(DialogInterface dialog, int which) {


        switch(which){
            case DialogInterface.BUTTON_POSITIVE:
                String stringMass=mass.getText().toString();
                if(TextUtils.isEmpty(stringMass)) {
                    Toast.makeText(getActivity(), "Введите массу в граммах", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent();
                intent.putExtra(PASS_MASS,Integer.valueOf(stringMass));
                intent.putExtra(PASS_PRODUCT,product);
                getTargetFragment().onActivityResult(getTargetRequestCode(), OK_RESULT_CODE, intent);
                break;
            case DialogInterface.BUTTON_NEGATIVE:

                break;
        }
    }
}
