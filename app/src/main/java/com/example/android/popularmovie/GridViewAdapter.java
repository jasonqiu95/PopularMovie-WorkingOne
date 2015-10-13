package com.example.android.popularmovie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Chen on 7/20/2015.
 */
public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<String> data;
    private LayoutInflater inflater;

    public GridViewAdapter(Context context, ArrayList<String> data) {
        super(context, R.layout.grid_item_layout, data);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item_layout, parent, false);
        }

        String url = data.get(position);
        Picasso.with(context)
                .load(url)
                //.placeholder(R.raw.placeholder_image)
                .error(R.raw.image_not_found)
                .into((ImageView) convertView);

        return convertView;
    }
}