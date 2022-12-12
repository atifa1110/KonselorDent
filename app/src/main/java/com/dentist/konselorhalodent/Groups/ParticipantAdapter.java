package com.dentist.konselorhalodent.Groups;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dentist.konselorhalodent.Model.Dokters;
import com.dentist.konselorhalodent.Utils.Extras;
import com.dentist.konselorhalodent.Model.Konselors;
import com.dentist.konselorhalodent.Model.Pasiens;
import com.dentist.konselorhalodent.Model.Users;
import com.dentist.konselorhalodent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.ParticipantViewHolder> {

    private Context context;
    private List<Users> participantList;

    public ParticipantAdapter(Context context, List<Users> participantList) {
        this.context = context;
        this.participantList = participantList;
    }

    @NonNull
    @NotNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_participant_list,parent,false);
        return new ParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ParticipantViewHolder holder, int position) {
        Users user = participantList.get(position);

        try{
            if (user.getRole().equals("Pasien")){
                setUserName(user,holder);
            }else if(user.getRole().equals("Konselor")){
                setKonselorName(user,holder);
            }else if(user.getRole().equals("Dokter Pengawas")){
                setDokterName(user,holder);
            }

            holder.roleParticipant.setText(user.getRole());

            holder.clParticipant.setTag(R.id.TAG_PARTIPANT_ROLE, user.getRole());
            holder.clParticipant.setTag(R.id.TAG_PARTIPANT_ID, user.getId());

        }catch (Exception e){
            e.printStackTrace();
        }

        holder.clParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = v.getTag(R.id.TAG_PARTIPANT_ID).toString();
                String roleType = v.getTag(R.id.TAG_PARTIPANT_ROLE).toString();
                if (roleType.equals("Pasien")){
                    Intent intent = new Intent(context, DetailPasienActivity.class);
                    intent.putExtra(Extras.USER,id);
                    context.startActivity(intent);
                }
            }
        });
    }

    private void setUserName(Users user, ParticipantViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Pasiens.class.getSimpleName());
        ref.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Pasiens pasiens = snapshot.getValue(Pasiens.class);
                try{
                    holder.userName.setText(pasiens.getNama());
                    holder.userOnline.setText(pasiens.getStatus());
                    holder.userOnline.setTextColor(setColor(pasiens.getStatus()));
                    holder.circleImageView.setImageDrawable(setDrawable(pasiens.getStatus()));
                    Glide.with(context).load(pasiens.getPhoto()).error(R.drawable.ic_user).placeholder(R.drawable.ic_user)
                            .into(holder.photoUser);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setKonselorName(Users user, ParticipantViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Konselors.class.getSimpleName());
        ref.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Konselors konselors = snapshot.getValue(Konselors.class);
                try {
                    holder.userName.setText(konselors.getNama());
                    holder.userOnline.setText(konselors.getStatus());
                    holder.userOnline.setTextColor(setColor(konselors.getStatus()));
                    holder.circleImageView.setImageDrawable(setDrawable(konselors.getStatus()));
                    Glide.with(context).load(konselors.getPhoto()).placeholder(R.drawable.ic_user)
                            .into(holder.photoUser);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setDokterName(Users user, ParticipantViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Dokters.class.getSimpleName());
        ref.child(user.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Dokters dokters = snapshot.getValue(Dokters.class);
                try {
                    holder.userName.setText(dokters.getNama());
                    holder.userOnline.setText(dokters.getStatus());
                    holder.userOnline.setTextColor(setColor(dokters.getStatus()));
                    holder.circleImageView.setImageDrawable(setDrawable(dokters.getStatus()));
                    Glide.with(context).load(dokters.getPhoto()).placeholder(R.drawable.ic_user)
                            .into(holder.photoUser);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public Integer setColor(String online){
        if(online.equals("Online")){
            return Color.parseColor("#00CCBB");
        }else{
            return Color.parseColor("#7C7C7C");
        }
    }

    public Drawable setDrawable(String online){
        if (online.equals("Online")){
            return context.getDrawable(R.drawable.ic_circle_green);
        }else{
            return context.getDrawable(R.drawable.ic_circle_gray);
        }
    }

    @Override
    public int getItemCount() {
        return participantList.size();
    }

    public class ParticipantViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout clParticipant;
        private CircleImageView photoUser,circleImageView;
        private TextView userName,userOnline,roleParticipant;

        public ParticipantViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            clParticipant = itemView.findViewById(R.id.clParticipant);
            circleImageView = itemView.findViewById(R.id.iv_circle);
            photoUser = itemView.findViewById(R.id.iv_profile_user);
            userName = itemView.findViewById(R.id.tv_nama_user);
            userOnline = itemView.findViewById(R.id.tv_online);
            roleParticipant = itemView.findViewById(R.id.tv_role);
        }
    }
}
