package com.dentist.konselorhalodent.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dentist.konselorhalodent.Model.Extras;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.dentist.konselorhalodent.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPasienActivity extends AppCompatActivity {

    private CircleImageView iv_profil;
    private ImageButton btn_back;
    private TextView tv_umur,tv_email,tv_nama,tv_kelamin,tv_alamat,tv_nomor,tv_jawaban_1,tv_jawaban_2,tv_jawaban_3,tv_jawaban_4;

    private DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
    private DatabaseReference databaseReferenceUserInterview = FirebaseDatabase.getInstance().getReference().child(NodeNames.INTERVIEW);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pasien);

        iv_profil = findViewById(R.id.iv_user);
        tv_umur = findViewById(R.id.tv_umur_user);
        tv_email= findViewById(R.id.tv_email_user_detail);
        tv_nama = findViewById(R.id.tv_nama_user_detail);
        tv_kelamin = findViewById(R.id.tv_kelamin_user);
        tv_alamat = findViewById(R.id.tv_alamat_user);
        tv_nomor = findViewById(R.id.tv_nomer_user);

        tv_jawaban_1 = findViewById(R.id.tv_jawaban_1);
        tv_jawaban_2 = findViewById(R.id.tv_jawaban_2);
        tv_jawaban_3 = findViewById(R.id.tv_jawaban_3);
        tv_jawaban_4 = findViewById(R.id.tv_jawaban_4);

        String id = getIntent().getStringExtra(Extras.USER);

        readUserDatabase(id);

        btn_back = findViewById(R.id.btn_back_detail_user);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void readUserDatabase(String id){
        databaseReferenceUser.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    tv_nama.setText(snapshot.child(NodeNames.NAME).getValue().toString());
                    tv_umur.setText(snapshot.child(NodeNames.USIA).getValue().toString()+" Tahun");
                    tv_email.setText(snapshot.child(NodeNames.EMAIL).getValue().toString());
                    tv_kelamin.setText(snapshot.child(NodeNames.JENIS_KELAMIN).getValue().toString());
                    tv_alamat.setText(snapshot.child(NodeNames.ALAMAT).getValue().toString());
                    tv_nomor.setText("0"+snapshot.child(NodeNames.PHONE_NUMBER).getValue().toString());

                    Glide.with(getApplicationContext()).load(snapshot.child(NodeNames.PHOTO).getValue().toString()).placeholder(R.drawable.ic_user)
                            .into(iv_profil);
                }else{
                    Toast.makeText(DetailPasienActivity.this,R.string.failed_to_read_data,Toast.LENGTH_SHORT);
                }
                readUserInterviewDatabase(id);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(DetailPasienActivity.this,R.string.failed_to_read_data,Toast.LENGTH_SHORT);
            }
        });
    }

    private void readUserInterviewDatabase(String id){
        databaseReferenceUserInterview.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String keluhan = snapshot.child("keluhan").getValue().toString();
                    String pernah_ke_dokter = snapshot.child("pernah_ke_dokter").getValue().toString();
                    String pengobatan = snapshot.child("pengobatan").getValue().toString();
                    String riwayat_penyakit = snapshot.child("riwayat_penyakit").getValue().toString();

                    tv_jawaban_1.setText(pernah_ke_dokter);
                    tv_jawaban_2.setText(keluhan);
                    tv_jawaban_3.setText(pengobatan);
                    tv_jawaban_4.setText(riwayat_penyakit);

                }else{
                    Toast.makeText(DetailPasienActivity.this,R.string.failed_to_read_data,Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(DetailPasienActivity.this,R.string.failed_to_read_data,Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}