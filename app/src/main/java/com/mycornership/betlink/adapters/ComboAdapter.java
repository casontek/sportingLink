package com.mycornership.betlink.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mycornership.betlink.R;

import java.util.List;

public class ComboAdapter extends ArrayAdapter {
    private List<String> items;

    public ComboAdapter(@NonNull Context context, List<String> data) {
        super(context,0, data);
        items = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return fetchView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_combo_box, parent, false);
        TextView itemView = convertView.findViewById(R.id.item_list_one);
        itemView.setText(items.get(position));
        View view = convertView.findViewById(R.id.divider_view);
        if(position == items.size() - 1){
            view.setVisibility(View.GONE);
        }
        return convertView;
    }

    private View fetchView(int position, View convertView, ViewGroup parent){
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_combo_box_view, parent, false);
        TextView itemView = convertView.findViewById(R.id.item_list_one);
        itemView.setText(items.get(position));

        return convertView;
    }

}
