package com.korzinni.shura.nutrition.fragments;

import android.support.v4.app.Fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.adapters.MyAdapter;
import com.korzinni.shura.nutrition.content.MyContractClass;


public class SuggestTestFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    MyAdapter adapter;
    ExpandableListView list;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(0,null,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.product_list,null);

        list=(ExpandableListView)view.findViewById(R.id.list);

        return view;
    }




    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MyContractClass.TypeProduct.CONTENT_URI,null,null,null,null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter=new MyAdapter(data,getActivity(),this);
        list.setAdapter(adapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

