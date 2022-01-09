package com.dentist.konselorhalodent.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dentist.konselorhalodent.Model.KonselorModel;
import com.dentist.konselorhalodent.Model.MainActivity;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.dentist.konselorhalodent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputEditText etEmail, etName, etPassword, etConfirmPassword, etNomor;
    private Button btn_daftar;
    private Toolbar toolbar;
    private String email, nama, password, confirmPassword,nomor;
    private ProgressDialog progress;

    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //inisialisasi view
        etEmail = findViewById(R.id.et_email_signup);
        etName = findViewById(R.id.et_nama_signup);
        etPassword = findViewById(R.id.et_pass_signup);
        etConfirmPassword = findViewById(R.id.et_confirmpass_signup);
        etNomor = findViewById(R.id.et_nomor_signup);
        btn_daftar = findViewById(R.id.btn_daftar);
        toolbar = findViewById(R.id.toolbar);

        //inisialisasi progress bar
        progress = new ProgressDialog(this);
        progress.setMessage("Sign Up .. Silahkan Tunggu..");

        //set button click
        btn_daftar.setOnClickListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_daftar:
                progress.show();
                signUp(v);
                break;
        }
    }

    public void updateDatabase(){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim())
                .build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    String userId = firebaseUser.getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.KONSELORS);

                    KonselorModel konselorModel = new KonselorModel(userId,"",etEmail.getText().toString(),"",etNomor.getText().toString(),"","","","","");

                    databaseReference.child(userId).setValue(konselorModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                //Toast.makeText(SignUpActivity.this, R.string.user_created_success, Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                progress.dismiss();
                            }else{
                                progress.dismiss();
                                Toast.makeText(SignUpActivity.this,
                                        getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    public void signUp(View v){
        email = etEmail.getText().toString().trim();
        password =etPassword.getText().toString().trim();

        if(inputValidated()){
            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        firebaseUser = firebaseAuth.getCurrentUser();
                        updateDatabase();
                    }else {
                        progress.dismiss();
                        Toast.makeText(SignUpActivity.this,
                                getString(R.string.sign_up_failed, task.getException()), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public boolean inputValidated(){
        boolean res = true;

        email = etEmail.getText().toString().trim();
        nama = etName.getText().toString().trim();
        password =etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();
        nomor = etNomor.getText().toString().trim();

        if(email.isEmpty()){
            res =false;
            etEmail.setError("Silahkan isi field");
        }else if(nama.isEmpty()){
            res=false;
            etName.setError("Silahkan isi field");
        }else if(password.isEmpty()){
            res = false;
            etPassword.setError("Silahkan isi field");
        }else if(confirmPassword.isEmpty()){
            res = false;
            etConfirmPassword.setError("Silahkan isi field");
        }else if(nomor.isEmpty()){
            res = false;
            etNomor.setError("Silahkan isi field");
        }else if(!password.equals(confirmPassword)){
            res = false;
            etConfirmPassword.setError("Password tidak cocok");
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            res =false;
            etEmail.setError("Masukkan Email yang benar");
        }
        return res;
    }

}