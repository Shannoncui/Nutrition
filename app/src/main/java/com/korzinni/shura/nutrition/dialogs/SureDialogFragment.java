package com.korzinni.shura.nutrition.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

public class SureDialogFragment extends DialogFragment implements DialogInterface.OnClickListener{

    public static final String TITLE="title";
    public static final String MESSAGE="MESSAGE";
    public static final String POSITIVE_BUTTON="positive";
    public static final String NEGATIVE_BUTTON="negative";
    public static final int OK=101;
    public static final int CANCEL=102;


    public static SureDialogFragment newInstance(String title,String message,String positiveButton,String negativeButton){
        Log.d("tag", "SureDialogFragment -- newInstance");
        Bundle bnd=new Bundle();
        bnd.putString(TITLE,title);
        bnd.putString(MESSAGE,message);
        bnd.putString(POSITIVE_BUTTON,positiveButton);
        bnd.putString(NEGATIVE_BUTTON,negativeButton);
        SureDialogFragment fragment=new SureDialogFragment();
        fragment.setArguments(bnd);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("tag", "SureDialogFragment -- onCreateDialog");
        Log.d("tag", "getTargetFragment.hashCode --"+getTargetFragment().hashCode());
        Bundle bnd=getArguments();
        String title=bnd.getString(TITLE);
        String message=bnd.getString(MESSAGE);
        String positiveButton=bnd.getString(POSITIVE_BUTTON);
        String negativeButton=bnd.getString(NEGATIVE_BUTTON);

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setNegativeButton(negativeButton,this)
                .setPositiveButton(positiveButton,this)
                .setMessage(message);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                getTargetFragment().onActivityResult(getTargetRequestCode(),OK,null);
                Log.d("tag","onClick: getTargetRequestCode"+getTargetRequestCode());
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                getTargetFragment().onActivityResult(getTargetRequestCode(),CANCEL,null);
                break;
        }
    }

}
