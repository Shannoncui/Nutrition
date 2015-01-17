package com.korzinni.shura.nutrition.fragments;




import android.app.Activity;
import android.content.Intent;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;


import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.adapters.MyAdapter;
import com.korzinni.shura.nutrition.content.MyContractClass;
import com.korzinni.shura.nutrition.dao.DAOProduct;
import com.korzinni.shura.nutrition.dialogs.SetMassDialogFragment;
import com.korzinni.shura.nutrition.model.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,ExpandableListView.OnChildClickListener{
    private static final String SAVE_PRODUCTS="saved_products";
    private static final String SAVE_MASS="saved_mass";
    private static final int REQUEST_CODE=102;

    MyAdapter adapter;
    ExpandableListView list;
    InteractionFragmentListener listener;
    boolean [] groups;
    int index;
    int top;
    ArrayList<Product> products;
    ArrayList<Integer> mass;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.list_product,menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("tag","ProperListFragment -- onCreateView");
        View view=inflater.inflate(R.layout.product_list,null);
        list=(ExpandableListView)view.findViewById(R.id.list);
        list.setSaveEnabled(false);
        adapter=new MyAdapter(null,getActivity(),this);
        list.setAdapter(adapter);
        if(savedInstanceState!=null) {
            products=savedInstanceState.getParcelableArrayList(SAVE_PRODUCTS);
            mass=savedInstanceState.getIntegerArrayList(SAVE_MASS);
            long[] temp = savedInstanceState.getLongArray("openedItem");
            adapter.setOpenedItems(toLongSet(temp));
            index = savedInstanceState.getInt("index");
            top = savedInstanceState.getInt("top");
            groups = savedInstanceState.getBooleanArray("groups");

        }else{
            products=new ArrayList<Product>();
            mass=new ArrayList<Integer>();
        }

        list.setOnChildClickListener(this);

        Loader loader=getLoaderManager().getLoader(-1);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(-1, null, this);
        } else {
            getLoaderManager().initLoader(-1, null, this);
        }


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setHasOptionsMenu(true);
        Log.d("tag","ProperListFragment -- onResume");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("tag","ProperListFragment -- onSaveInstanceState");
        super.onSaveInstanceState(outState);
        ArrayList<Boolean> groups=new ArrayList<Boolean>();
        int index=list.getFirstVisiblePosition();
        int top=list.getChildAt(0).getTop();



        for(int i=0;i<list.getExpandableListAdapter().getGroupCount();i++){
            groups.add(list.isGroupExpanded(i));
        }
        outState.putParcelableArrayList(SAVE_PRODUCTS,products);
        outState.putIntegerArrayList(SAVE_MASS,mass);
        outState.putBooleanArray("groups", toBooleanArray(groups));
        outState.putInt("index", index);
        outState.putInt("top", top);

        outState.putLongArray("openedItem", toLongArray(adapter.getOpenedItems()));


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        if(id==-1){
            String selection=MyContractClass.TypeProduct.IS_PRODUCT+"=?";
            String SelectionArgs[]=new String[]{"1"};
            cursorLoader=new CursorLoader(getActivity(), MyContractClass.TypeProduct.CONTENT_URI,null,selection,SelectionArgs,null);
        }else{
            String selection=MyContractClass.Product.TYPE_ID+"=?";
            String SelectionArgs[]=new String[]{String.valueOf(id)};
            cursorLoader=new CursorLoader(getActivity(),MyContractClass.Product.CONTENT_URI,null,selection,SelectionArgs,null);
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id=loader.getId();
        if(id==-1){
            adapter.setGroupCursor(data);
            // recover position list and numbers expanded group
            //we can't do it in onCreateView because in onCreateView we have empty adapter and getGroupCount() return 0
            if(groups!=null&&groups.length==list.getExpandableListAdapter().getGroupCount()){//state has been recovered
                list.setSelectionFromTop(index, top);
                for (int i = 0; i < groups.length; i++) {
                    if (groups[i]) {
                        list.expandGroup(i);
                    }
                }
            }
        }else{
            adapter.setChildrenCursor(adapter.getPositionById(id),data);

        }

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int id=loader.getId();
        if(id==-1){
            adapter.setGroupCursor(null);
        }else{
            adapter.setChildrenCursor(adapter.getPositionById(id),null);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close_list:
                Log.d("tag", "Nutrition-- onOptionItemSelected--close list");
                getFragmentManager().popBackStack();

                break;
        }
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        DAOProduct dao=new DAOProduct(getActivity());
        Product product=dao.getProductById(id);
        Bundle bnd=new Bundle();
        bnd.putParcelable(SetMassDialogFragment.PASS_PRODUCT,product);
        SetMassDialogFragment df=new SetMassDialogFragment();
        df.setArguments(bnd);
        df.setTargetFragment(this,REQUEST_CODE);
        df.show(getFragmentManager(),"qwe");
        return false;
    }
    boolean[] toBooleanArray(List<Boolean> list){
        int i=0;
        boolean[] array=new boolean[list.size()];
        for(boolean b:list){
            array[i++]=b;
        }
        return array;
    }
    long[] toLongArray(Set<Long> list){
        int i=0;
        long[] array=new long[list.size()];
        for(long l:list){
            array[i++]=l;
        }
        return array;
    }

    Set<Long> toLongSet(long[] array){
        Set<Long> set=new HashSet<Long>();
        for(long l:array){
            set.add(l);
        }
        return set;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("tag","ProperListFragment - onActivityResut");
        if(requestCode==REQUEST_CODE && resultCode==SetMassDialogFragment.OK_RESULT_CODE){
            Product product=data.getParcelableExtra(SetMassDialogFragment.PASS_PRODUCT);
            int m=data.getIntExtra(SetMassDialogFragment.PASS_MASS,0);

            listener.returnProduct(product,m);
        }
    }
    public interface InteractionFragmentListener{
        public void returnProduct(Product products,Integer mass);
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
}

