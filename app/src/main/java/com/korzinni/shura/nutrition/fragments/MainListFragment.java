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
import android.widget.Toast;


import com.korzinni.shura.nutrition.Nutrition;
import com.korzinni.shura.nutrition.R;
import com.korzinni.shura.nutrition.adapters.MyAdapter;
import com.korzinni.shura.nutrition.content.MyContractClass;
import com.korzinni.shura.nutrition.dao.DAODish;
import com.korzinni.shura.nutrition.dao.DAOGroupProduct;
import com.korzinni.shura.nutrition.dao.DAOProduct;
import com.korzinni.shura.nutrition.dialogs.DeleteGroupWithProductsDialogFragment;
import com.korzinni.shura.nutrition.dialogs.EditGroupDialog;
import com.korzinni.shura.nutrition.dialogs.SetMassDialogFragment;
import com.korzinni.shura.nutrition.dialogs.SureDialogFragment;
import com.korzinni.shura.nutrition.model.Product;
import com.korzinni.shura.nutrition.model.TypeProduct;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,ExpandableListView.OnChildClickListener,ExpandableListView.OnItemLongClickListener{
    public static final String ADD_DISH="add_dish";
    public static final String ADD_PRODUCT="add_product";
    private static final String SAVE_PRODUCTS="saved_products";
    private static final String SAVE_MASS="saved_mass";
    public static final String SAVE_SELECTED_ID="selected_id";
    private static final int REQUEST_CODE_DELETE_PRODUCT =1005;
    private static final int REQUEST_CODE_DELETE_DISH =1006;
    private static final int REQUEST_CODE_EDIT_GROUP =1007;
    public static final int REQUEST_CODE_SURE_DIALOG_DELETE_GROUP=1008;
    public static final int REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_PRODUCT=1009;
    public static final int REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_DISHES=1010;
    public static final int REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_USED_PRODUCT=1011;
    public static final int REQUEST_CODE_SET_MASS=1012;

    DAODish daoDish;
    DAOGroupProduct daoGroupProduct;
    DAOProduct daoProduct;
    MyAdapter adapter;
    ExpandableListView list;
    InteractionFragmentListener listener;
    boolean [] groups;
    int index;
    int top;
    long selectedId;
    ArrayList<Product> products;
    ArrayList<Integer> mass;
    ActionMode actionMode;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.full_list,menu);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("tag","MainListFragment -- onCreate");
        Log.d("tag","MainListFragment.hashCode() --"+hashCode());
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("tag","MainListFragment -- onResume");
        daoGroupProduct=new DAOGroupProduct(getActivity());
        daoDish=new DAODish(getActivity());
        daoProduct=new DAOProduct(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("tag","MainListFragment -- onCreateView");
        View view=inflater.inflate(R.layout.product_list,null);
        list=(ExpandableListView)view.findViewById(R.id.list);
        list.setSaveEnabled(false);
        adapter=new MyAdapter(null,getActivity(),this);
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
            if(groups!=null&&groups.length==list.getExpandableListAdapter().getGroupCount()){//state has been recovered
                list.setSelectionFromTop(index, top);
                for (int i = 0; i < groups.length; i++) {
                    if (groups[i]) {
                        list.expandGroup(i);
                    }
                }
            }

        }else{
            products=new ArrayList<Product>();
            mass=new ArrayList<Integer>();
        }

        list.setOnChildClickListener(this);
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
        CursorLoader cursorLoader;
        if(id==-1){

            cursorLoader=new CursorLoader(getActivity(), MyContractClass.TypeProduct.CONTENT_URI,null,null,null,null);
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
        FragmentManager fragmentManager=getFragmentManager();
        switch(item.getItemId()) {
            case R.id.new_dish:
                Log.d("tag", "MainListFragment-- onOptionItemSelected--new dish");
                AddDishFragment addDish = (AddDishFragment) fragmentManager.findFragmentByTag(ADD_DISH);
                if (addDish == null) {
                    addDish = AddDishFragment.newInstance(null);
                }
                fragmentManager.
                        beginTransaction().
                        replace(R.id.frame, addDish, ADD_DISH).
                        addToBackStack(null).
                        commit();
                break;
            case R.id.new_product:
                Log.d("tag", "MainListFragment-- onOptionItemSelected--new product");
                AddProductFragment addProduct = (AddProductFragment) fragmentManager.findFragmentByTag(ADD_PRODUCT);
                if (addProduct == null) {
                    addProduct = AddProductFragment.newInstance(null);
                }
                fragmentManager.
                        beginTransaction().
                        replace(R.id.frame, addProduct, ADD_PRODUCT).
                        addToBackStack(null).
                        commit();
                break;
        }
        return true;
    }
    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

        Product product=daoProduct.getProductById(id);
        Bundle bnd=new Bundle();
        bnd.putParcelable(SetMassDialogFragment.PASS_PRODUCT,product);
        SetMassDialogFragment df=new SetMassDialogFragment();
        df.setArguments(bnd);
        df.setTargetFragment(this,REQUEST_CODE_SET_MASS);
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
        Log.d("tag","MainListFragment -- onActivityResult");
            int id=ExpandableListView.getPackedPositionChild(selectedId);
            int groupPosition=ExpandableListView.getPackedPositionGroup(selectedId);
            selectedId=0;
            switch(requestCode){
                case REQUEST_CODE_SET_MASS:
                    if(resultCode==SetMassDialogFragment.OK_RESULT_CODE){
                        Product product=data.getParcelableExtra(SetMassDialogFragment.PASS_PRODUCT);
                        int m=data.getIntExtra(SetMassDialogFragment.PASS_MASS,0);

                        listener.returnDish(product,m);
                    break;

                }
                case REQUEST_CODE_DELETE_DISH:
                    if(resultCode==SureDialogFragment.OK){
                        Log.d("tag","MainListFragment -- REQUEST_CODE_DELETE_DISH");

                    daoDish.deleteDish(id);
                    adapter.notifyDataSetChanged();
                    }
                    break;
                case REQUEST_CODE_DELETE_PRODUCT:
                    if(resultCode==SureDialogFragment.OK){
                        Log.d("tag","MainListFragment -- REQUEST_CODE_DELETE_PRODUCT");

                    daoProduct.deleteProduct(id);
                    adapter.notifyDataSetChanged();
                    }
                    break;
                case REQUEST_CODE_EDIT_GROUP:
                    if(resultCode==EditGroupDialog.OK_RESULT_CODE) {
                        Log.d("tag","MainListFragment -- REQUEST_CODE_EDIT_GROUP");

                        TypeProduct oldType = daoGroupProduct.getGroupById(groupPosition);
                        String stringType = data.getStringExtra(EditGroupDialog.PASS_NAME);
                        TypeProduct typeProduct = daoGroupProduct.checkExistenceGroupProduct(stringType);
                        if (typeProduct == null) {// such group doesn't exist

                            oldType.setName(stringType);
                            daoGroupProduct.updateGroupProduct(oldType);
                            getLoaderManager().getLoader(-1).forceLoad();

                            Toast.makeText(getActivity(), "Имя группы было изменено", Toast.LENGTH_SHORT).show();
                        } else {//such group exist for products or dishes
                            showDialogEditGroup();
                            Toast.makeText(getActivity(), "Это имя уже занято", Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;
                case REQUEST_CODE_SURE_DIALOG_DELETE_GROUP:
                    if(resultCode==SureDialogFragment.OK) {
                        Log.d("tag","MainListFragment -- REQUEST_CODE_SURE_DIALOG_DELETE_GROUP");

                        daoGroupProduct.deleteGroup(groupPosition);
                        getLoaderManager().getLoader(-1).forceLoad();
                        

                    }
                    break;
                case REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_USED_PRODUCT:
                    if(resultCode== DeleteGroupWithProductsDialogFragment.OK){
                        Log.d("tag","MainListFragment -- REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_USED_PRODUCT");

                        daoGroupProduct.deleteGroupWithProducts(groupPosition);
                        getLoaderManager().getLoader(-1).forceLoad();

                    }
                    break;
                case REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_DISHES:
                    if(resultCode==SureDialogFragment.OK){
                        Log.d("tag","MainListFragment -- REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_DISHES");

                        daoGroupProduct.deleteGroupWithDishes(groupPosition);
                        getLoaderManager().getLoader(-1).forceLoad();

                    }
                    break;
                case REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_PRODUCT:
                    if(resultCode==SureDialogFragment.OK){
                        Log.d("tag","MainListFragment -- REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_PRODUCT");

                        daoGroupProduct.deleteGroupWithProducts(groupPosition);
                        getLoaderManager().getLoader(-1).forceLoad();

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
        public void returnDish(Product products,Integer mass);
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

            FragmentManager fragmentManager=getFragmentManager();
            switch (item.getItemId()) {
                case R.id.menu_edit:

                    if(ExpandableListView.getPackedPositionType(selectedId) == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
                        int childPosition = ExpandableListView.getPackedPositionChild(selectedId);
                        // black magic
                        if(daoProduct.isProduct(childPosition)){
                            actionMode.finish();
                            AddProductFragment editProduct = AddProductFragment.newInstance(daoProduct.getProductById(childPosition));

                            fragmentManager.
                                    beginTransaction().
                                    replace(R.id.frame, editProduct,null).
                                    addToBackStack(ADD_PRODUCT).
                                    commit();
                        }else{
                            actionMode.finish();
                            AddDishFragment editDish = AddDishFragment.newInstance(daoDish.getDishById(childPosition));

                            fragmentManager.
                                    beginTransaction().
                                    replace(R.id.frame, editDish, ADD_DISH).
                                    addToBackStack(ADD_DISH).
                                    commit();
                        }
                    }else{
                        showDialogEditGroup();
                    }
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.menu_delete:
                    int childPosition = ExpandableListView.getPackedPositionChild(selectedId);
                    int groupPosition = ExpandableListView.getPackedPositionGroup(selectedId);
                    if(ExpandableListView.getPackedPositionType(selectedId) == ExpandableListView.PACKED_POSITION_TYPE_CHILD){
                        // black magic: position == id
                        if(daoProduct.isProduct(childPosition)){
                            actionMode.finish();
                            showDialogDeleteProduct();
                        }else{
                            actionMode.finish();
                            showDialogDeleteDish();
                        }
                    }else{
                        actionMode.finish();
                        TypeProduct group=daoGroupProduct.getGroupById(groupPosition);
                        List<Product> products= daoProduct.getAllProductsByGroup(groupPosition);
                        boolean groupIsEmpty=products.isEmpty();

                        boolean groupIsContainUsedProduct=false;
                        for(Product p:products){
                            groupIsContainUsedProduct|=daoProduct.isUsedInDish(p.getId());
                            if(groupIsContainUsedProduct){
                                break;
                            }
                        }
                        if(groupIsEmpty){//group empty- can delete
                            showDialogDeleteGroup();
                        }else if(group.isProduct()){//group contain products
                            if(groupIsContainUsedProduct){//products is used in dishes
                                showDialogDeleteGroupWithUsedProducts(groupPosition);
                            }else{//products doesn't used in dishes
                                showDialogDeleteGroupWithProducts();
                            }

                        }else{//group contain dishes
                            showDialogDeleteGroupWithDishes();
                        }
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
    private void showDialogEditGroup(){
        EditGroupDialog dialog=new EditGroupDialog();
        dialog.setTargetFragment(this, REQUEST_CODE_EDIT_GROUP);
        dialog.show(getFragmentManager(),null);
    }
    public void showDialogDeleteGroupWithUsedProducts(long groupId){
        DeleteGroupWithProductsDialogFragment dialog= DeleteGroupWithProductsDialogFragment.newInstance("Удалить Группу",
                "Данная группа содержит продукты используемые в блюдах?",
                "Удалить везде",
                "Используемые продукты",
                "Отмена",
                groupId);
            dialog.setTargetFragment(this, REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_USED_PRODUCT);
            dialog.show(getFragmentManager(),DeleteGroupWithProductsDialogFragment.TAG);
    }
    private void showDialogDeleteGroupWithDishes(){
        SureDialogFragment dialog= SureDialogFragment.newInstance("Удалить Группу",
                "Данная группа содержит блюда, удалить группу вместе с блюдами?",
                "Да",
                "Нет");
        dialog.setTargetFragment(this, REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_DISHES);
        dialog.show(getFragmentManager(),null);
    }
    private void showDialogDeleteGroupWithProducts(){
        SureDialogFragment dialog= SureDialogFragment.newInstance("Удалить Группу",
                "Данная группа содержит продукты, удалить группу вместе с продуктами?",
                "Да",
                "Нет");
        dialog.setTargetFragment(this, REQUEST_CODE_SURE_DIALOG_DELETE_GROUP_WITH_PRODUCT);
        dialog.show(getFragmentManager(),null);
    }
    private void showDialogDeleteGroup(){
        SureDialogFragment dialog= SureDialogFragment.newInstance("Удалить Группу",
                "Вы уверены что хотите удалить группу?",
                "Да",
                "Нет");
        dialog.setTargetFragment(this, REQUEST_CODE_SURE_DIALOG_DELETE_GROUP);
        dialog.show(getFragmentManager(),null);
    }
    private void showDialogDeleteProduct(){
        SureDialogFragment dialog=SureDialogFragment.newInstance("Удаление",
                "Продукт так же будет удален из составов блюд. Вы уверены что хотите удалить продукт?",
                "Да",
                "Нет");
        dialog.setTargetFragment(this, REQUEST_CODE_DELETE_PRODUCT);
        dialog.show(getFragmentManager(),null);
    }
    private void showDialogDeleteDish(){
        SureDialogFragment dialog=SureDialogFragment.newInstance("Удаление",
                "Вы уверены что хотите удалить блюдо?",
                "Да",
                "Нет");
        dialog.setTargetFragment(this, REQUEST_CODE_DELETE_DISH);
        dialog.show(getFragmentManager(),null);
    }

}
