package com.dentist.konselorhalodent.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dentist.konselorhalodent.Model.Dokters;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.dentist.konselorhalodent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JadwalAdapter extends RecyclerView.Adapter<JadwalAdapter.JadwalViewHolder>{

    private Context context;
    private List<Jadwals> jadwals;

    public JadwalAdapter(Context context, List<Jadwals> jadwals) {
        this.context = context;
        this.jadwals = jadwals;
    }

    @NonNull
    @Override
    public JadwalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_jadwal_layout,parent,false);
        return new JadwalAdapter.JadwalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JadwalViewHolder holder, int position) {
        Jadwals jadwal = jadwals.get(position);

        holder.jadwalTanggal.setText(jadwal.getTanggal());
        setDokterName(jadwal,holder);
        holder.jadwalJam.setText(jadwal.getMulai()+" - "+ jadwal.getSelesai());

        holder.btn_option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.btn_option);
                //inflating menu from xml resource
                popup.inflate(R.menu.menu_jadwal);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_hapus:
                                hapusJadwal(jadwal);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }


    private void hapusJadwal(Jadwals jadwals){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Jadwals.class.getSimpleName()).child(jadwals.getKonselor_id());
        ref.child(jadwals.getId()).removeValue();
    }

    private void setDokterName(Jadwals jadwals, JadwalViewHolder holder){
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Dokters.class.getSimpleName());
        ref.child(jadwals.getDokter_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Dokters dokters = snapshot.getValue(Dokters.class);
                    holder.jadwalDokter.setText("Diawasi oleh : "+dokters.getNama());
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return jadwals.size();
    }

    public class JadwalViewHolder extends RecyclerView.ViewHolder {

        private TextView jadwalTanggal,jadwalDokter,jadwalJam,btn_option;

        public JadwalViewHolder(@NonNull View itemView) {
            super(itemView);

            jadwalTanggal = itemView.findViewById(R.id.tv_tanggal_jadwal);
            jadwalDokter = itemView.findViewById(R.id.tv_dokter_jadwal);
            jadwalJam = itemView.findViewById(R.id.tv_jam_jadwal);
            btn_option = itemView.findViewById(R.id.btn_options);
        }
    }
}