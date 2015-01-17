package com.korzinni.shura.nutrition.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.korzinni.shura.nutrition.CustomCursorLoader;
import com.korzinni.shura.nutrition.Nutrition;
import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.adapters.OtherAdapter;
import com.korzinni.shura.nutrition.dao.DAODish;
import com.korzinni.shura.nutrition.dao.DAOProduct;
import com.korzinni.shura.nutrition.dao.DAOGroupProduct;
import com.korzinni.shura.nutrition.dialogs.DeleteGroupWithProductsDialogFragment;
import com.korzinni.shura.nutrition.dialogs.SureDialogFragment;
import com.korzinni.shura.nutrition.model.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UsedProduct extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,ExpandableListView.OnItemLongClickListener{
    public static final String ADD_DISH="add_dish";
    public static final String ADD_PRODUCT="add_product";
    public static final String SAVE_SELECTED_ID="selected_id";
    public static final String TAG="used_product";
    private static final String SAVE_PRODUCTS="saved_products";
    private static final String SAVE_MASS="saved_mass";
    private static final int REQUEST_CODE_DELETE_DISH =1006;



    long typeId;
    OtherAdapter adapter;
    ExpandableListView list;
    InteractionFragmentListener listener;
    boolean [] groups;
    int index;
    int top;
    long selectedId;
    ArrayList<Product> products;
    ArrayList<Integer> mass;
    ActionMode actionMode;
    public static final String TYPE_ID="type_id";

    public static UsedProduct newInstance(long typeId){
        Bundle bnd=new Bundle();
        bnd.putLong(TYPE_ID,typeId);
        UsedProduct fragment=new UsedProduct();
        fragment.setArguments(bnd);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typeId=getArguments().getLong(TYPE_ID);

    }

    @Override
    public void onResume() {
        super.onResume();

        Dialog dialog=((DialogFragment)getFragmentManager().findFragmentByTag(DeleteGroupWithProductsDialogFragment.TAG)).getDialog();
        if(dialog.isShowing()){
            dialog.hide();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("tag", "ProperListFragment -- onCreateView");
        View view=inflater.inflate(R.layout.product_list,null);
        list=(ExpandableListView)view.findViewById(R.id.list);
        list.setSaveEnabled(false);
        adapter=new OtherAdapter(null,getActivity(),this);
        list.setAdapter(adapter);
        if(savedInstanceState!=null) {
            selectedId=savedInstanceState.getLong(SAVE_SELECTED_ID);
            products=savedInstanceState.getParcelableArrayList(SAVE_PRODUCTS);
            mass=savedInstanceState.getIntegerArrayList(SAVE_MASS);
            long[] temp = savedInstanceState.getLongArray("openedItem");
            index = savedInstanceState.getInt("index");
            top = savedInstanceState.getInt("top");
            groups = savedInstanceState.getBooleanArray("groups");
            if(temp!=null){
                adapter.setOpenedItems(toLongSet(temp));
            }

        }else{
            products=new ArrayList<Product>();
            mass=new ArrayList<Integer>();
        }
        list.setOnItemLongClickListener(this);


        Loader loader=getLoaderManager().getLoader(-1);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(-1, null, this);
        } else {
            getLoaderManager().initLoader(-1, null, this);
        }


        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("tag","MainListFragment -- onSaveInstanceState");
        super.onSaveInstanceState(outState);
        ArrayList<Boolean> groups=new ArrayList<Boolean>();
        if(list!=null) {
            int index = list.getFirstVisiblePosition();
            int top = list.getChildAt(0).getTop();
            for (int i = 0; i < list.getExpandableListAdapter().getGroupCount(); i++) {
                groups.add(list.isGroupExpanded(i));
            }
            outState.putBooleanArray("groups", toBooleanArray(groups));
            outState.putInt("index", index);
            outState.putInt("top", top);

            outState.putLongArray("openedItem", toLongArray(adapter.getOpenedItems()));
        }
        outState.putParcelableArrayList(SAVE_PRODUCTS,products);
        outState.putIntegerArrayList(SAVE_MASS,mass);
        outState.putLong(SAVE_SELECTED_ID,selectedId);


    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CustomCursorLoader(getActivity(),typeId);
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
            Log.d("tag","onLoadFinished for custom");
            Log.d("tag","id"+id);
            Log.d("tag","position"+adapter.getPositionById(id));
            if (data==null){
                Log.d("tag","data is null");
            }else{
                Log.d("tag","data is not null, count: "+data.getCount());
            }
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

        int id=ExpandableListView.getPackedPositionChild(selectedId);
        int groupPosition=ExpandableListView.getPackedPositionGroup(selectedId);
        selectedId=0;
        switch(requestCode){
            case REQUEST_CODE_DELETE_DISH:
                if(resultCode== SureDialogFragment.OK){
                    DAODish daoDish=new DAODish(getActivity());
                    daoDish.deleteDish(id);
                    getLoaderManager().getLoader(-1).forceLoad();
                    adapter.notifyDataSetChanged();

                }
                break;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (actionMode != null) {
            return false;
        }
        // Start the CAB using the ActionMode.Callback defined above
        actionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(mActionModeCallback);
        view.setSelected(true);
        selectedId=id;
        return true;

    }



    public interface InteractionFragmentListener{
        public void returnProduct(Product products,Integer mass);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener=(InteractionFragmentListener)activity;
        ((Nutrition)activity).setOnBackPressedListener(new Nutrition.OnBackPressedListener() {
            @Override
            public void doBack() {
                FragmentManager fm=getFragmentManager();
                fm.popBackStack();
                ((DialogFragment)fm.findFragmentByTag(DeleteGroupWithProductsDialogFragment.TAG)).getDialog().show();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener=null;
        ((Nutrition)getActivity()).setOnBackPressedListener(null);
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
            DAOProduct daoProduct=new DAOProduct(getActivity());
            DAODish daoDish=new DAODish(getActivity());
            DAOGroupProduct daoType=new DAOGroupProduct(getActivity());
            FragmentManager fragmentManager=getFragmentManager();
            switch (item.getItemId()) {
                case R.id.menu_edit:
                    actionMode.finish();
                    if(ExpandableListView.getPackedPositionType(selectedId) == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
                        int childPosition = ExpandableListView.getPackedPositionChild(selectedId);

                            AddDishFragment editDish = AddDishFragment.newInstance(daoDish.getDishById(childPosition));

                            fragmentManager.
                                    beginTransaction().
                                    replace(R.id.frame, editDish, ADD_DISH).
                                    addToBackStack(ADD_DISH).
                                    commit();
                        }

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.menu_delete:
                    actionMode.finish();
                    if(ExpandableListView.getPackedPositionType(selectedId) == ExpandableListView.PACKED_POSITION_TYPE_CHILD){

                            SureDialogFragment dialog=SureDialogFragment.newInstance("Удаление",
                                    "Вы уверены что хотите удалить блюдо?",
                                    "Да",
                                    "Нет");
                            dialog.setTargetFragment(getFragmentManager().findFragmentByTag(UsedProduct.TAG), REQUEST_CODE_DELETE_DISH);
                            dialog.show(fragmentManager,null);

                    }
                    return true;

                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
        }
    };

}


