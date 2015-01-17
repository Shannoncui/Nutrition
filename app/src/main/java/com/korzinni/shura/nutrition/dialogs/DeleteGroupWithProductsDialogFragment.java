package com.korzinni.shura.nutrition.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.fragments.UsedProduct;

public class DeleteGroupWithProductsDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, View.OnClickListener{

    public static final String TITLE="title";
    public static final String TAG="DeleteGroupWithProductsDialogFragment";
    public static final String MESSAGE="MESSAGE";
    public static final String POSITIVE_BUTTON="positive";
    public static final String NEGATIVE_BUTTON="negative";
    public static final String NEUTRAL_BUTTON="neutral";
    public static final String GROUP_ID="droup_id";
    public static final int OK=101;
    public static final int CANCEL=102;
    public static final int NEUTRAL=103;


    public static DeleteGroupWithProductsDialogFragment newInstance(String title,String message,String positiveButton,String neutralButton,String negativeButton,long groupId){

        Bundle bnd=new Bundle();
        bnd.putLong(GROUP_ID,groupId);
        bnd.putString(TITLE,title);
        bnd.putString(MESSAGE,message);
        bnd.putString(POSITIVE_BUTTON,positiveButton);
        bnd.putString(NEUTRAL_BUTTON,neutralButton);
        bnd.putString(NEGATIVE_BUTTON,negativeButton);
        DeleteGroupWithProductsDialogFragment fragment=new DeleteGroupWithProductsDialogFragment();
        fragment.setArguments(bnd);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bnd=getArguments();
        String title=bnd.getString(TITLE);
        String message=bnd.getString(MESSAGE);
        String positiveString=bnd.getString(POSITIVE_BUTTON);
        String neutralString=bnd.getString(NEUTRAL_BUTTON);
        String negativeString=bnd.getString(NEGATIVE_BUTTON);
        Button neutralButton=new Button(getActivity());
        neutralButton.setText(neutralString);
        neutralButton.setBackgroundResource(R.color.weq);
        neutralButton.setOnClickListener(this);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setNegativeButton(negativeString,this)
                .setPositiveButton(positiveString,this)
                .setMessage(message)
                .setView(neutralButton);
        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                getTargetFragment().onActivityResult(getTargetRequestCode(),OK,null);
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                getTargetFragment().onActivityResult(getTargetRequestCode(),CANCEL,null);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        UsedProduct fragment=UsedProduct.newInstance(getArguments().getLong(GROUP_ID));
        getFragmentManager().
                beginTransaction().
                replace(R.id.frame, fragment, UsedProduct.TAG).
                addToBackStack(null).
                commit();
        getDialog().hide();
    }

}
