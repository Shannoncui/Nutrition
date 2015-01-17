package com.korzinni.shura.nutrition.adapters;


import android.content.Context;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.korzinni.shura.nutrition.Nutrition;
import com.korzinni.shura.nutrition.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyAdapter extends CursorTreeAdapter {

    private Set<Long> openedItems=new HashSet<Long>();
    private List<PositionAndId> positionAndIds=new ArrayList<PositionAndId>();//<index- groupPosition, value- groupId>
    LoaderManager manager;
    LayoutInflater inflater;
    LoaderManager.LoaderCallbacks callbacks;
    Context context;

    public MyAdapter(Cursor cursor, Context context,LoaderManager.LoaderCallbacks callbacks) {
        super(cursor, context);
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        manager=((Nutrition)context).getSupportLoaderManager();
        this.callbacks=callbacks;
        this.context=context.getApplicationContext();
    }


    public int getPositionById(int id) {
        for(PositionAndId p:positionAndIds){
            if(p.id==id){
                return p.position;
            }
        }
        return -1;
    }

    public int getIdByPosition(int position){
        for(PositionAndId p:positionAndIds){
            if(p.position==position){
                return p.id;
            }
        }
        return -1;
    }

    public Set<Long> getOpenedItems() {
        return openedItems;
    }

    public void setOpenedItems(Set<Long> set) {
        openedItems=set;
    }

    public void addOpenedItems(long index) {

        openedItems.add(index);
    }
    public void deleteOpenedItems(long id){
        openedItems.remove(id);
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        long groupId = groupCursor.getLong(groupCursor.getColumnIndex("_id"));
        int groupPosition= groupCursor.getPosition();
        positionAndIds.add(new PositionAndId( groupPosition, (int) groupId));
        Loader loader=manager.getLoader((int)groupId);
        if(loader!=null&&!loader.isReset()){
            manager.restartLoader((int)groupId,null,callbacks);
        }else{
            manager.initLoader((int)groupId,null,callbacks);
        }

        return null;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean isExpanded, ViewGroup parent) {
        View view=inflater.inflate(R.layout.item_group_list,parent,false);
        return view;

    }

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
        TextView text=(TextView)view.findViewById(R.id.text);
        text.setText(cursor.getString(cursor.getColumnIndex("name")));
    }


    @Override
    protected View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {

        return inflater.inflate(R.layout.item_products_list, parent, false);

    }

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean isLastChild) {
        float value_proteins=cursor.getFloat(cursor.getColumnIndex("proteins"));
        float value_fats=cursor.getFloat(cursor.getColumnIndex("fats"));
        float value_carbohydrates=cursor.getFloat(cursor.getColumnIndex("carbohydrates"));
        float value_calories=cursor.getFloat(cursor.getColumnIndex("calories"));
        DecimalFormat f=new DecimalFormat("0.0");

        new ViewSetter(view).
                    setProteins(f.format(value_proteins)).
                    setFats(f.format(value_fats)).
                    setCarbohydrates(f.format(value_carbohydrates)).
                    setCalories(f.format(value_calories)).
                    setName(cursor.getString(cursor.getColumnIndex("name"))).
                    setCertainPicture(new float[]{value_proteins, value_fats, value_carbohydrates});


    }
    public static int findMax(float[] array){
        float value=0;
        int index=-1;
        for(int i=0;i<array.length;i++){
            if(value<=array[i]){
                value=array[i];
                index=i;
            }
        }
        return index;
    }

    public static class ViewSetter{
        private View view;
        private ImageView image;
        private TextView name;
        private TextView proteins;
        private TextView carbohydrates;
        private TextView fats;
        private TextView calories;

        public ViewSetter(View view){
            this.view=view;
            image=(ImageView)view.findViewById(R.id.picture);
            name=(TextView)view.findViewById(R.id.name_product);
            proteins=(TextView)view.findViewById(R.id.count_proteins);
            carbohydrates=(TextView)view.findViewById(R.id.count_carbohydrates);
            fats=(TextView)view.findViewById(R.id.count_fats);
            calories=(TextView)view.findViewById(R.id.count_calories);
        }


        public ViewSetter setName(String text){
            name.setText(text);
            return this;
        }
        public ViewSetter setProteins(String text){
            proteins.setText(R.string.prot);
            proteins.append(text);
            return this;
        }
        public ViewSetter setCarbohydrates(String text){
            carbohydrates.setText(R.string.carbo);
            carbohydrates.append(text);
            return this;
        }
        public ViewSetter setFats(String text){
            fats.setText(R.string.fats);
            fats.append(text);
            return this;
        }
        public ViewSetter setCalories(String text){
            calories.setText(R.string.calo);
            calories.append(text);
            return this;
        }
        public ViewSetter setCertainPicture(float[] array){
            switch(findMax(array)) {
                case 0:
                    image.setImageResource(R.drawable.green_ball);
                    break;
                case 1:
                    image.setImageResource(R.drawable.yellow_ball);
                    break;
                case 2:
                    image.setImageResource(R.drawable.red_ball);
                    break;
                default:

            }
            return this;
        }

        public View getView(){
            return view;
        }


    }
    // class for store mapping between position groups and related loaders
    private class PositionAndId{
        //group position
        int position;

        //loader id
        int id;
        PositionAndId(int position,int id){
            this.position=position;
            this.id=id;
        }
    }

}
