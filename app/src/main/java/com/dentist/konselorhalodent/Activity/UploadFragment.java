package com.dentist.konselorhalodent.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.dentist.konselorhalodent.Model.NodeNames;
import com.dentist.konselorhalodent.Model.TopikModel;
import com.dentist.konselorhalodent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class UploadFragment extends DialogFragment {

    public static final String TAG = "upload_dialog";

    private Toolbar toolbar;
    private TextInputEditText judul_topik,narasi_topik,sumber_topik;
    private AutoCompleteTextView type_topik;
    private ImageView iv_image_topik;

    private String judul,narasi,sumber;

    private ArrayAdapter tipeAdapter;

    private Uri localFileUri, serverFileUri;
    private StorageReference fileStorage;
    private DatabaseReference databaseReferenceTopik;

    public static UploadFragment display(FragmentManager fragmentManager) {
        UploadFragment exampleDialog = new UploadFragment();
        exampleDialog.show(fragmentManager, TAG);
        return exampleDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false);

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar = view.findViewById(R.id.toolbar);
        judul_topik = view.findViewById(R.id.et_judul_topik);
        narasi_topik = view.findViewById(R.id.et_narasi_topik);
        sumber_topik = view.findViewById(R.id.et_sumber_topik);
        iv_image_topik = view.findViewById(R.id.iv_image_topik);
        type_topik = view.findViewById(R.id.et_tipe_topik);

        databaseReferenceTopik = FirebaseDatabase.getInstance().getReference().child(NodeNames.TOPIKS);

        setDropDownType();
        iv_image_topik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseGallery();
            }
        });

        toolbar.setTitle("Information");
        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.inflateMenu(R.menu.menu_dialog);
        toolbar.setOnMenuItemClickListener(item -> {
            if(localFileUri!=null){
                uploadTopik();
            }else{
                uploadTopikPhoto();
            }
            dismiss();
            return true;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    private void chooseGallery(){
        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //pindah ke galley
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 101);
        }
        else
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},102);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                localFileUri = data.getData();
                iv_image_topik.setImageURI(localFileUri);
            }
        }
    }

    private void setDropDownType(){
        List<String> tipe = new ArrayList<>();
        tipe.add("photo");
        tipe.add("video");
        tipeAdapter = new ArrayAdapter<>(getActivity(),R.layout.dropdown_menu,tipe);
        type_topik.setAdapter(tipeAdapter);
    }

    private void uploadTopikPhoto(){
        if(inputValidated()){
            String timestamp = ""+System.currentTimeMillis();
            //diberi file dengan nama user id .jpg
            String filelocal = timestamp +".jpg";
            //child dengan file images/ didalamnya ada file local name
            StorageReference rootRef = FirebaseStorage.getInstance().getReference();
            final StorageReference fileRef = rootRef.child("topiks/"+ filelocal);

            fileRef.putFile(localFileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                serverFileUri=uri;
                                String link = serverFileUri.toString();

                                TopikModel topik = new TopikModel(timestamp,judul_topik.getText().toString(),link,narasi_topik.getText().toString(),sumber_topik.getText().toString(),timestamp,type_topik.getText().toString());

                                databaseReferenceTopik.child(timestamp).setValue(topik).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d("upload","Berhasil");
                                        }
                                    }
                                });

                            }
                        });
                    }
                }
            });
        }else{
            Toast.makeText(getContext(),"Data belum valid",Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadTopik(){
        if(inputValidated()){
            String timestamp = ""+System.currentTimeMillis();

            TopikModel topik = new TopikModel(timestamp,judul_topik.getText().toString(),"",narasi_topik.getText().toString(),sumber_topik.getText().toString(),timestamp,type_topik.getText().toString());
            databaseReferenceTopik.child(timestamp).setValue(topik).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d("upload","Berhasil");
                    }
                }
            });
        }else{
            Toast.makeText(getContext(),"Data belum valid",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean inputValidated(){
        boolean res = true;
        //jika text email kosong
        if (judul_topik.getText().toString().isEmpty()){
            res = false;
            judul_topik.setError("Silahkan isi field");
            //jika text password kosong
        }else if (narasi_topik.getText().toString().isEmpty()){
            res = false;
            narasi_topik.setError("Silahkan isi field");
            //jika pattern email tidak sesuai dengan text email
        }else if(sumber_topik.getText().toString().isEmpty()){
            res = false;
            sumber_topik.setError("Silahkan isi field");
        }else if(type_topik.getText().toString().isEmpty()) {
            res = false;
            type_topik.setError("Silahkan isi field");
        }
        return res;
    }
}