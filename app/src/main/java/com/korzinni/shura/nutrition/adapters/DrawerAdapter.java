package com.korzinni.shura.nutrition.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.korzinni.shura.nutrition.R;

import java.util.ArrayList;

public class DrawerAdapter extends BaseAdapter{
    ArrayList<String> list=new ArrayList<String>();
    Context mContext;
    int selectedPosition;
    LayoutInflater inflater;
    public void setSelectedPostion(int position){
        selectedPosition=position;
    }
    public DrawerAdapter(Context context){
        mContext=context;
        //String[] array=context.getResources().getStringArray(R.array.drawer);
        /*for(String s :array){
            list.add(s);
        }*/
        inflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(true){//if preference set
            list.remove(1);
        }
        if(false){//if set preference
            list.remove(3);
        }

    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View view;
            if(convertView==null){

               holder=new ViewHolder();
               view=inflater.inflate(R.layout.item_drawer,null);
               holder.text=(TextView)view.findViewById(R.id.text);
               view.setTag(holder);
            }else{
                view=convertView;
                holder=(ViewHolder)view.getTag();
            }

        holder.text.setText(list.get(position));
            return view;


    }
    private static class ViewHolder{
            TextView text;
    }
}
