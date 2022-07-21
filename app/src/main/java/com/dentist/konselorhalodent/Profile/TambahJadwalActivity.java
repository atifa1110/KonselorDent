package com.dentist.konselorhalodent.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.dentist.konselorhalodent.Model.Dokters;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.dentist.konselorhalodent.Model.Preference;
import com.dentist.konselorhalodent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TambahJadwalActivity extends AppCompatActivity {

    private TextInputEditText input_mulai,input_selesai,input_tanggal;
    private TextInputLayout inputLayout_mulai,inputLayout_selesai,inputLayout_tanggal;
    private AutoCompleteTextView et_dokter;
    private Button btn_simpan;

    private DokterAdapter dokterAdapter;
    private List<Dokters> dokterList = new ArrayList<>();
    private DatabaseReference databaseReferenceDokter,databaseReferenceJadwal;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_jadwal);
        setActionBar();

        dokterAdapter = new DokterAdapter(this,R.layout.dropdown_menu,dokterList);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceDokter = FirebaseDatabase.getInstance().getReference().child(NodeNames.DOKTERS);
        databaseReferenceJadwal = FirebaseDatabase.getInstance().getReference().child(NodeNames.JADWAL);

        input_mulai = findViewById(R.id.et_jadwal_from);
        input_selesai = findViewById(R.id.et_jadwal_to);
        input_tanggal = findViewById(R.id.et_tanggal);
        inputLayout_mulai = findViewById(R.id.til_jadwal_from);
        inputLayout_selesai = findViewById(R.id.til_jadwal_to);;
        inputLayout_tanggal = findViewById(R.id.til_tanggal);;
        et_dokter = findViewById(R.id.et_jadwal_dokter);;
        btn_simpan = findViewById(R.id.btn_simpan);;

        btn_simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadJadwal();
            }
        });

        et_dokter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dokters dokter = (Dokters) parent.getAdapter().getItem(position);
                et_dokter.setText(dokter.getNama());
                Preference.setKeyDokterId(getApplicationContext(),dokter.getId());
            }
        });

        setTime();
        getDataDokter();
    }

    private void uploadJadwal() {
        String timestamp = ""+System.currentTimeMillis();
        Jadwals jadwals = new Jadwals(currentUser.getUid(),Preference.getKeyDokterId(getApplicationContext()),input_tanggal.getText().toString(),input_mulai.getText().toString(),input_selesai.getText().toString());
        databaseReferenceJadwal.child(currentUser.getUid()).child(timestamp).setValue(jadwals).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(TambahJadwalActivity.this, R.string.update_successful, Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(TambahJadwalActivity.this,
                            getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDataDokter(){
        databaseReferenceDokter.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if(ds.exists()){
                        Dokters dokters = ds.getValue(Dokters.class);
                        dokterList.add(dokters);
                        dokterAdapter.notifyDataSetChanged();
                    }
                    et_dokter.setAdapter(dokterAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void setTime(){
        inputLayout_mulai.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        // set the title for the alert dialog
                        .setTitleText("SELECT YOUR TIMING")
                        .setHour(0)
                        .setMinute(0)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "fragment_tag");
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pickedHour = materialTimePicker.getHour();
                        int pickedMinute = materialTimePicker.getMinute();
                        String menit,jam;
                        if(pickedHour<10) {
                            jam = "0"+pickedHour;
                            if(pickedMinute==0){
                                menit="00";
                            }else{
                                menit= String.valueOf(pickedMinute);
                            }
                        }else{
                            jam = String.valueOf(pickedHour);
                            if(pickedMinute==0){
                                menit="00";
                            }else{
                                menit= String.valueOf(pickedMinute);
                            }
                        }
                        String jam_from = jam+":"+menit;
                        Preference.setKeyJadwalFrom(getApplicationContext(),jam_from);
                        input_mulai.setText(jam_from);
                    }
                });
            }
        });

        inputLayout_selesai.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                        // set the title for the alert dialog
                        .setTitleText("SELECT YOUR TIMING")
                        .setHour(0)
                        .setMinute(0)
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .build();

                materialTimePicker.show(getSupportFragmentManager(), "fragment_tag");
                materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pickedHour = materialTimePicker.getHour();
                        int pickedMinute = materialTimePicker.getMinute();
                        String menit,jam;
                        if(pickedHour<10) {
                            jam = "0"+pickedHour;
                            if(pickedMinute==0){
                                menit="00";
                            }else{
                                menit= String.valueOf(pickedMinute);
                            }
                        }else{
                            jam = String.valueOf(pickedHour);
                            if(pickedMinute==0){
                                menit="00";
                            }else{
                                menit= String.valueOf(pickedMinute);
                            }
                        }
                        String jam_to = jam+":"+menit;
                        Preference.setKeyJadwalTo(getApplicationContext(),jam_to);
                        input_selesai.setText(jam_to);
                    }
                });
            }
        });


        inputLayout_tanggal.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Select date")
                        .build();
                datePicker.show(getSupportFragmentManager(), "date");
                datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object selection) {
                        String stringSelection = String.valueOf(selection);
                        Long convertedLong = Long.parseLong(stringSelection);
                        SimpleDateFormat sfd = new SimpleDateFormat("EEE, dd MMMM yyyy");
                        String dateString = sfd.format(new Date(convertedLong));
                        input_tanggal.setText(dateString);
                    }
                });
            }
        });

    }
    //set action bar
    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Tambah Jadwal");
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