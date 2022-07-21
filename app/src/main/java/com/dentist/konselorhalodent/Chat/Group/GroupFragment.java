package com.dentist.konselorhalodent.Chat.Group;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dentist.konselorhalodent.R;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GroupFragment extends Fragment {

    private RecyclerView rvChat;
    private List<Groups> groupList;
    private GroupAdapter groupAdapter;
    private View emptyChat;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReferenceGroups;
    private FirebaseUser currentUser;

    private ChildEventListener childEventListener;
    private Query query;

    private List<String> userIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //inisialisasi semua view
        rvChat = view.findViewById(R.id.rv_chats_groups);
        emptyChat = view.findViewById(R.id.ll_empty_chat);

        ////set array list
        userIds = new ArrayList<>();
        groupList = new ArrayList<>();
        groupAdapter = new GroupAdapter(getContext(),groupList);

        //set layout
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);

        //set group adapter
        rvChat.setLayoutManager(linearLayout);
        rvChat.setAdapter(groupAdapter);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Silahkan Tunggu..");
        progressDialog.show();

        //inisialisasi database
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceGroups = FirebaseDatabase.getInstance().getReference().child(NodeNames.GROUPS);

        loadGroupChat();
        emptyChat.setVisibility(View.VISIBLE);
    }

    private void loadGroupChat(){
        //set query dengan diurutkan dengan waktu kirim
        query = databaseReferenceGroups.orderByChild(NodeNames.TIME_STAMP);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                progressDialog.dismiss();
                for (DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("Participants").child(currentUser.getUid()).exists()) {
                        emptyChat.setVisibility(View.GONE);
                        Groups groups = ds.getValue(Groups.class);
                        groupList.add(groups);
                    }
                    groupAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}