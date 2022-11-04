package com.ntuproject.fast_and_furious;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomBaseAdapter extends BaseAdapter {

    Context context;
    List<String> area;
    List<Integer> numberOfLots;
    LayoutInflater inflater;

    public CustomBaseAdapter(Context ctx, List<String> area, List<Integer> numberOfLots){
        this.context = ctx;
        this.area = area;
        this.numberOfLots = numberOfLots;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return area.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.activity_parking_custom_list_view,null);
        TextView location = (TextView) convertView.findViewById(R.id.LocationName);
        TextView noOfLots = (TextView) convertView.findViewById(R.id.numberOfLots);
        location.setText(area.get(position));
        noOfLots.setText(""+numberOfLots.get(position));
        return convertView;
    }
}
