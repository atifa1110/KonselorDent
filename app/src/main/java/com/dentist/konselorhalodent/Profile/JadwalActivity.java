package com.dentist.konselorhalodent.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dentist.konselorhalodent.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JadwalActivity extends AppCompatActivity {

    private FloatingActionButton btn_tambah;
    private DatabaseReference databaseReferenceJadwal;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private List<Jadwals> jadwalsList;
    private RecyclerView rv_jadwal;
    private JadwalAdapter jadwalAdapter;
    private TextView tv_tidak;
    private ShimmerFrameLayout shimmerFrameLayoutJadwal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jadwal);
        setActionBar();

        rv_jadwal = findViewById(R.id.rv_all_jadwal);
        btn_tambah = findViewById(R.id.btn_tambah);
        tv_tidak = findViewById(R.id.tv_jadwal_tidak);
        shimmerFrameLayoutJadwal = findViewById(R.id.shimmer_jadwal);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        databaseReferenceJadwal = FirebaseDatabase.getInstance().getReference().child(Jadwals.class.getSimpleName());

        jadwalsList = new ArrayList<>();
        rv_jadwal.setLayoutManager(new LinearLayoutManager(this));
        jadwalAdapter = new JadwalAdapter(this, jadwalsList);
        rv_jadwal.setAdapter(jadwalAdapter);

        btn_tambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JadwalActivity.this, TambahJadwalActivity.class);
                startActivity(intent);
            }
        });

        tv_tidak.setVisibility(View.VISIBLE);
        getDataJadwal();
    }

    private void getDataJadwal(){
        tv_tidak.setVisibility(View.GONE);
        shimmerFrameLayoutJadwal.startShimmer();
        //Set query urutkan sesuai tanggal jadwal
        Query query = databaseReferenceJadwal.child(currentUser.getUid()).orderByChild("tanggal");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jadwalsList.clear();
                shimmerFrameLayoutJadwal.stopShimmer();
                shimmerFrameLayoutJadwal.setVisibility(View.GONE);
                if(snapshot.exists()){
                    rv_jadwal.setVisibility(View.VISIBLE);
                    tv_tidak.setVisibility(View.GONE);
                    for (DataSnapshot ds : snapshot.getChildren()){
                        Jadwals jadwals = ds.getValue(Jadwals.class);
                        jadwals.setId(ds.getKey());
                        jadwalsList.add(jadwals);
                        jadwalAdapter.notifyDataSetChanged();
                    }
                }else{
                    rv_jadwal.setVisibility(View.GONE);
                    tv_tidak.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //set action bar
    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Jadwal");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayOptions(actionBar.getDisplayOptions());
        }
    }

    // this event will enable the back , function to the button on press
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}