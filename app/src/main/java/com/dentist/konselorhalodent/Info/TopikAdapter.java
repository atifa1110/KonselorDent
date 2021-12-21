package com.dentist.konselorhalodent.Info;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.dentist.konselorhalodent.Activity.DetailTopikActivity;
import com.dentist.konselorhalodent.Model.Extras;
import com.dentist.konselorhalodent.Model.TopikModel;
import com.dentist.konselorhalodent.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TopikAdapter extends RecyclerView.Adapter<TopikAdapter.TopikViewHolder> {

    private Context context;
    private List<TopikModel> topikModelList;

    public TopikAdapter(Context context, List<TopikModel> topikModelList) {
        this.context = context;
        this.topikModelList = topikModelList;
    }

    @NonNull
    @NotNull
    @Override
    public TopikViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_topik_hangat,parent,false);
        return new TopikViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull TopikAdapter.TopikViewHolder holder, int position) {
        TopikModel topikModel = topikModelList.get(position);

        holder.topikName.setText(topikModel.getJudul());

        String narasi = topikModel.getNarasi();
        narasi = narasi.length()>100?narasi.substring(0,100):narasi;

        holder.topikNarasi.setText(narasi+"...");

        try{
            Glide.with(context)
                    .load(topikModel.getPhoto())
                    .placeholder(R.drawable.ic_add_photo)
                    .centerCrop()
                    .into(holder.photoName);

        }catch (Exception e){
            holder.photoName.setImageResource(R.drawable.ic_add_photo);
        }

        SimpleDateFormat sfd = new SimpleDateFormat("d MMM yyy HH:mm");
        String dateTime = sfd.format(new Date(Long.parseLong(topikModel.getTimestamp())));
        String [] splitString = dateTime.split(" ");
        String topikTime = splitString[0]+" "+splitString[1];

        holder.time.setText(topikTime);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailTopikActivity.class);
                intent.putExtra(Extras.TOPIK, new TopikModel(topikModel.getId(),topikModel.getJudul(),topikModel.getPhoto(),topikModel.getNarasi(),topikModel.getSumber(),topikModel.getTimestamp(),topikModel.getTipe()));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topikModelList.size();
    }

    public class TopikViewHolder extends RecyclerView.ViewHolder{

        private ImageView photoName;
        private TextView topikName,time,topikNarasi;

        public TopikViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            photoName = itemView.findViewById(R.id.iv_photo_topik);
            topikName = itemView.findViewById(R.id.tv_topik);
            topikNarasi = itemView.findViewById(R.id.tv_topik_narasi);
            time = itemView.findViewById(R.id.tv_time_topik);
        }
    }
}

