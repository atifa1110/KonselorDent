package com.dentist.konselorhalodent.Profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dentist.konselorhalodent.SignIn.SignInActivity;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.dentist.konselorhalodent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment implements View.OnClickListener{

    private TextView nama, email;
    private Button btn_edit_profile,btn_keluar,btn_jadwal;
    private ImageView ivProfile;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private DatabaseReference databaseReferenceKonselor,databaseReferenceToken;
    private Uri serverFileUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nama = view.findViewById(R.id.tv_nama_profile_fragment);
        email = view.findViewById(R.id.tv_email_profile_fragment);
        ivProfile = view.findViewById(R.id.iv_profile_profile_fragment);
        btn_edit_profile = view.findViewById(R.id.btn_edit_profile);
        btn_keluar = view.findViewById(R.id.btn_keluar);
        btn_jadwal = view.findViewById(R.id.btn_jadwal);

        btn_edit_profile.setOnClickListener(this);
        btn_jadwal.setOnClickListener(this);
        btn_keluar.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        databaseReferenceToken = FirebaseDatabase.getInstance().getReference().child(NodeNames.TOKENS).child(currentUser.getUid());
        databaseReferenceKonselor = FirebaseDatabase.getInstance().getReference().child(NodeNames.KONSELORS).child(currentUser.getUid());
        loadProfile();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadProfile();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_edit_profile :
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_keluar:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Apakah Anda yakin ingin keluar?")
                        .setCancelable(false)
                        .setPositiveButton("iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        }).setNegativeButton("tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;
            case R.id.btn_jadwal:
                Intent jadwal = new Intent(getActivity(), JadwalActivity.class);
                startActivity(jadwal);
                break;
            default:
                break;
        }
    }

    public void loadProfile(){
        if(firebaseAuth!=null){
            nama.setText(currentUser.getDisplayName());
            email.setText(currentUser.getEmail());
            serverFileUri= currentUser.getPhotoUrl();
            if(serverFileUri!=null)
            {
                Glide.with(this)
                        .load(serverFileUri)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(ivProfile);
            }
        }else{
            nama.setText("");
            email.setText("");
        }
    }

    public void logout(){
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        //databaseReferenceToken.setValue(null);
        databaseReferenceKonselor.child(NodeNames.ONLINE).setValue("Offline");
        startActivity(new Intent(getActivity(), SignInActivity.class));
        getActivity().finish();
    }
}