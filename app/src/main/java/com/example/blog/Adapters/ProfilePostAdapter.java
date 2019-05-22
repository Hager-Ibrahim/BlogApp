package com.example.blog.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.blog.R;

import java.util.ArrayList;

public class ProfilePostAdapter extends BaseAdapter {

    private Context mContext;
    private final ArrayList<String> gridViewString;

    public ProfilePostAdapter(Context context, ArrayList<String> gridViewString) {
        mContext = context;
        this.gridViewString = gridViewString;
    }

    @Override
    public int getCount() {
        return gridViewString.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            gridViewAndroid = new View(mContext);
            gridViewAndroid = inflater.inflate(R.layout.grid_item, null);
            ImageView imageViewAndroid =  gridViewAndroid.findViewById(R.id.gridImg);
            Glide.with(mContext).load(gridViewString.get(i)).into(imageViewAndroid);

        } else {
            gridViewAndroid = (View) convertView;
        }

        return gridViewAndroid;
    }


}
