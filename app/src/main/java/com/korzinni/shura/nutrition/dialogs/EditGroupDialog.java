package com.korzinni.shura.nutrition.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.korzinni.shura.nutrition.R;

public class EditGroupDialog extends DialogFragment implements View.OnClickListener {
    public static final int OK_RESULT_CODE = 101;
    public static final int CANCEL_RESULT_CODE = 102;
    public static final String PASS_NAME = "pass_name";

    EditText newName;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_group, null);
        newName = (EditText) view.findViewById(R.id.new_name);
        Button posButton=(Button)view.findViewById(R.id.pos_button);
        posButton.setOnClickListener(this);
        Button negButton=(Button)view.findViewById(R.id.neg_button);
        negButton.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Введите новое имя")
                .setView(view);

        return builder.create();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pos_button:
                Intent intent=new Intent();
                intent.putExtra(PASS_NAME,newName.getText().toString());
                getTargetFragment().onActivityResult(getTargetRequestCode(),OK_RESULT_CODE,intent);
                getDialog().dismiss();
                break;
            case R.id.neg_button:
                getTargetFragment().onActivityResult(getTargetRequestCode(),CANCEL_RESULT_CODE,null);
                getDialog().dismiss();
                break;
        }
    }
}