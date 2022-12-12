package com.dentist.konselorhalodent.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dentist.konselorhalodent.Model.Konselors;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.dentist.konselorhalodent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputLayout tilNama,tilNim,tilNomor,tilJenisKel;
    private TextInputEditText etNama ,etNim ,etNomor,etJenisKel;
    private Button btn_simpan;
    private CircleImageView ivProfile;
    private ProgressDialog progress;
    private BottomSheetDialog bottomSheetDialog;

    private Konselors konselor;
    private ArrayAdapter<String> jenisAdapter;
    private String id,nama,email,photo,ponsel,status,role,nim,angkatan,kelamin;
    private Uri localFileUri, serverFileUri;
    private StorageReference fileStorage;
    private DatabaseReference databaseReferenceKonselor;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setActionBar();

        //inisialisasi view
        tilNama = findViewById(R.id.til_nama_profile);
        tilJenisKel = findViewById(R.id.til_jenis_kelamin);
        tilNim = findViewById(R.id.til_nim_profile);
        tilNomor = findViewById(R.id.til_nomor_ponsel);
        etNama = findViewById(R.id.et_nama_profile);
        etJenisKel = findViewById(R.id.et_jenis_kelamin);
        etNim = findViewById(R.id.et_nim_profile);
        etNomor = findViewById(R.id.et_nomor_profile);
        ivProfile =findViewById(R.id.iv_profile_profile);
        btn_simpan = findViewById(R.id.btn_simpan);

        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_photo);

        progress= new ProgressDialog(this);
        progress.setMessage("Loading..");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        //inisialisasi database
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        databaseReferenceKonselor = FirebaseDatabase.getInstance().getReference().child(Konselors.class.getSimpleName());
        fileStorage = FirebaseStorage.getInstance().getReference();

        btn_simpan.setOnClickListener(this);
        ivProfile.setOnClickListener(this);
        etJenisKel.setOnClickListener(this);

        //jika current user tidak kosong maka akan set data ke dalam input text
        if(currentUser!=null) {
            etNama.setText(currentUser.getDisplayName());
            serverFileUri = currentUser.getPhotoUrl();
            getDataKonselor();
            if(serverFileUri!=null){
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(ivProfile);
            }else {
                ivProfile.setImageResource(R.drawable.ic_user);
            }
        }else{
            etNama.setText("");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_simpan:
                if(inputValidated()) {
                    if (localFileUri != null) {
                        updateUserProfilePhoto();
                    } else {
                        updateUserProfile();
                    }
                }
                break;
            case R.id.iv_profile_profile:
                changeImage();
                break;
            case R.id.delete:
                removePhoto();
                break;
            case R.id.choose:
                chooseGallery();
                break;
            case R.id.et_jenis_kelamin:
                setDropDownMenu();
                break;
            default:
                break;
        }
    }

    private void changeImage(){
        MaterialButton remove = bottomSheetDialog.findViewById(R.id.delete);
        MaterialButton choose = bottomSheetDialog.findViewById(R.id.choose);

        remove.setOnClickListener(this);
        choose.setOnClickListener(this);

        bottomSheetDialog.show();
    }

    private void chooseGallery(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //pindah ke galley
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                localFileUri = data.getData();
                ivProfile.setImageURI(localFileUri);
                bottomSheetDialog.dismiss();
            }
        }
    }

    private void removePhoto(){
        progress.show();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etNama.getText().toString().trim())
                .setPhotoUri(null)
                .build();

        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String userId = currentUser.getUid();
                    databaseReferenceKonselor = FirebaseDatabase.getInstance().getReference().child(Konselors.class.getSimpleName());

                    databaseReferenceKonselor.child(userId).child(NodeNames.PHOTO).setValue(" ").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            progress.dismiss();
                            String filelocal = userId +".jpg";
                            //child dengan file images/ didalamnya ada file local name
                            final StorageReference fileRef = fileStorage.child("images/"+ filelocal);
                            fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    ivProfile.setImageResource(R.drawable.ic_user);
                                    finish();
                                    bottomSheetDialog.dismiss();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void getDataKonselor(){
        databaseReferenceKonselor.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    progress.dismiss();
                    try{
                        Konselors konselors = snapshot.getValue(Konselors.class);
                        etNama.setText(konselors.getNama());
                        etJenisKel.setText(konselors.getKelamin());
                        etNim.setText(konselors.getNim());
                        etNomor.setText(konselors.getPonsel());
                    }catch (Exception e){
                        etNama.setText(" ");
                        etJenisKel.setText(" ");
                        etNim.setText(" ");
                        etNomor.setText(" ");
                    }

                }else{
                    Toast.makeText(EditProfileActivity.this,"Data tidak ada",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(EditProfileActivity.this,"Data tidak ada",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfilePhoto(){
        progress.show();
        //diberi file dengan nama user id .jpg
        String filelocal = firebaseAuth.getUid() +".jpg";
        //child dengan file images/ didalamnya ada file local name
        final StorageReference fileRef = fileStorage.child("images/"+ filelocal);

        fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            serverFileUri = uri;
                            String link = serverFileUri.toString();
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(etNama.getText().toString().trim())
                                    .setPhotoUri(serverFileUri)
                                    .build();

                            currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        id = currentUser.getUid();
                                        nama = etNama.getText().toString();
                                        email = currentUser.getEmail();
                                        photo = link;
                                        ponsel = etNomor.getText().toString();
                                        status = "Online";
                                        role = "Konselor";
                                        nim = etNim.getText().toString();
                                        String setNim = etNim.getText().toString().length()>8?etNim.getText().toString().substring(0,2):etNim.getText().toString();
                                        angkatan = setAngkatan(setNim);
                                        kelamin = etJenisKel.getText().toString();

                                        Konselors konselor = new Konselors(id,nama,email,photo,ponsel,status,role,kelamin,nim,angkatan);

                                        databaseReferenceKonselor.child(id).setValue(konselor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                progress.dismiss();
                                                Toast.makeText(EditProfileActivity.this, R.string.update_successful, Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(EditProfileActivity.this,
                                                getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    private void updateUserProfile(){
        progress.show();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etNama.getText().toString().trim())
                .build();

        currentUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    id = currentUser.getUid();
                    nama = etNama.getText().toString();
                    email = currentUser.getEmail();
                    ponsel = etNomor.getText().toString();
                    status = "Online";
                    role ="Konselor";
                    nim = etNim.getText().toString();
                    String setNim = etNim.getText().toString().length()>8?etNim.getText().toString().substring(0,2):etNim.getText().toString();
                    angkatan = setAngkatan(setNim);
                    kelamin = etJenisKel.getText().toString();

                    if(currentUser.getPhotoUrl()==null){
                        photo = " ";
                    }else{
                        fileStorage.child(currentUser.getPhotoUrl().getLastPathSegment()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d("uri",uri.toString());
                                photo = uri.toString();

                                konselor = new Konselors(id,nama,email,photo,ponsel,status,role,kelamin,nim,angkatan);

                                databaseReferenceKonselor.child(id).setValue(konselor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progress.dismiss();
                                            Toast.makeText(EditProfileActivity.this, R.string.update_successful, Toast.LENGTH_SHORT).show();
                                            finish();
                                        }else{
                                            Toast.makeText(EditProfileActivity.this,
                                                    getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }

                    konselor = new Konselors(id,nama,email,photo,ponsel,status,role,kelamin,nim,angkatan);

                    databaseReferenceKonselor.child(id).setValue(konselor).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progress.dismiss();
                                Toast.makeText(EditProfileActivity.this, R.string.update_successful, Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(EditProfileActivity.this,
                                        getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(EditProfileActivity.this,
                            getString(R.string.failed_to_update, task.getException()), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean inputValidated(){
        boolean res = true;
        if (etNama.getText().toString().isEmpty()){
            res = false;
            tilNama.setError("Error : Nama Kosong");
        }else if (etJenisKel.getText().toString().isEmpty() ){
            res = false;
            tilJenisKel.setError("Error : Jenis Kelamin Kosong");
        }else if(etNim.getText().toString().isEmpty()){
            res = false;
            tilNim.setError("Error : Nim Kosong");
        }else if(etNomor.getText().toString().isEmpty()){
            res = false;
            tilNomor.setError("Error : Nomor Kosong");
        }
        return res;
    }

    private String setAngkatan(String nim){
        if(nim!=null){
            angkatan = "20"+nim;
        }else{
            angkatan = "Kosong";
        }
        return angkatan;
    }

    private void setDropDownMenu(){
        String[] gender = getResources().getStringArray(R.array.genders);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setItems(gender, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                etJenisKel.setText(gender[which]);
            }
        });
        builder.show();
    }

    //set action bar
    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Edit Profile");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setElevation(0);
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