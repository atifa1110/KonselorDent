package com.dentist.konselorhalodent.Chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.dentist.konselorhalodent.R;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private List<GroupModel> groupList;
    private GroupAdapter groupAdapter;
    private View emptyChat;

    private DatabaseReference databaseReferenceGroups;
    private FirebaseUser currentUser;

    private ChildEventListener childEventListener;
    private Query query;

    private List<String> userIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group, container, false);
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

        //inisialisasi database
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceGroups = FirebaseDatabase.getInstance().getReference().child(NodeNames.GROUPS);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(item);
        itemTouchHelper.attachToRecyclerView(rvChat);

        //set query dengan diurutkan dengan waktu kirim
        query = databaseReferenceGroups.orderByChild(NodeNames.TIME_STAMP);
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadGroupChat(snapshot,true ,snapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadGroupChat(snapshot,false,snapshot.getKey());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        //ada query dengan child listener
        query.addChildEventListener(childEventListener);
        //emptyChat.setVisibility(View.VISIBLE);
    }

    ItemTouchHelper.SimpleCallback item = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull @com.google.firebase.database.annotations.NotNull RecyclerView recyclerView, @NonNull @com.google.firebase.database.annotations.NotNull RecyclerView.ViewHolder viewHolder, @NonNull @com.google.firebase.database.annotations.NotNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull @com.google.firebase.database.annotations.NotNull RecyclerView.ViewHolder viewHolder, int direction) {
            GroupModel deletedChat = groupList.get(viewHolder.getAdapterPosition());
            String chatId = deletedChat.getGroupId();

            // below line is to remove item from our array list.
            groupList.remove(viewHolder.getAdapterPosition());

            // below line is to notify our item is removed from adapter.
            groupAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());

            deleteChat(chatId);
        }
    };

    private void deleteChat(String chatId){
        databaseReferenceGroups.child(currentUser.getUid()).child(chatId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @com.google.firebase.database.annotations.NotNull Task<Void> task) {

            }
        });
    }

    private void loadGroupChat(DataSnapshot snapshot,boolean isNew,String groupId){
        emptyChat.setVisibility(View.GONE);
        if(snapshot.child("Participants").child(currentUser.getUid()).exists()){
            String groupTitle= snapshot.child("groupTitle").getValue().toString();
            String groupIcon = snapshot.child("groupIcon").getValue().toString();
            String timestamp = snapshot.child("timestamp").getValue().toString();

            GroupModel groupModel = new GroupModel(groupId,groupTitle,groupIcon,timestamp);
            if(isNew) {
                groupList.add(groupModel);
                userIds.add(groupId);
            }else{
                int indexofClickedUser = userIds.indexOf(groupId);
                groupList.set(indexofClickedUser,groupModel);
            }
            groupAdapter.notifyDataSetChanged();
        }
    }

    private void searchGroupChat(final String query) {
        emptyChat.setVisibility(View.GONE);
        databaseReferenceGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @com.google.firebase.database.annotations.NotNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String groupId = ds.getKey();
                    if (ds.child("Participants").child(currentUser.getUid()).exists()) {
                        if(ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())){
                            String groupTitle = ds.child("groupTitle").getValue().toString();
                            String groupIcon = ds.child("groupIcon").getValue().toString();
                            String timestamp = ds.child("timestamp").getValue().toString();

                            GroupModel groupModel = new GroupModel(groupId, groupTitle, groupIcon, timestamp);
                            groupList.add(groupModel);
                        }
                    }
                    groupAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        query.removeEventListener(childEventListener);
    }
}