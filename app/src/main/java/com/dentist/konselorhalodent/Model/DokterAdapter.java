package com.dentist.konselorhalodent.Model;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dentist.konselorhalodent.R;
import java.util.List;

public class DokterAdapter extends ArrayAdapter<DokterModel> {

    private Context context;
    int layoutResourceId;
    private List<DokterModel> dokters;

    public DokterAdapter(@NonNull Context context, int layoutResourceId,List<DokterModel> dokters) {
        super(context,layoutResourceId, dokters);
        this.context = context;
        this.dokters = dokters;
        this.layoutResourceId=layoutResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        DokterHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new DokterHolder();
            holder.textView = (TextView)row.findViewById(R.id.textview);

            row.setTag(holder);
        }else{
            holder = (DokterHolder) row.getTag();
        }

        DokterModel dokter = dokters.get(position);
        holder.textView.setText(dokter.getNama());

        return row;

    }

    static class DokterHolder{
        TextView textView;
    }
}
