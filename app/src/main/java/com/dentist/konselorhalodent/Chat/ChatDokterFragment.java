package com.dentist.konselorhalodent.Chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dentist.konselorhalodent.Model.Chats;
import com.dentist.konselorhalodent.Model.Dokters;
import com.dentist.konselorhalodent.Model.Messages;
import com.dentist.konselorhalodent.R;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChatDokterFragment extends Fragment {

    private RecyclerView rvChat;
    private List<Chats> chatList;
    private ChatAdapter chatAdapter;
    private View emptyChat;

    private DatabaseReference databaseReferenceDokters, databaseReferenceChats, databaseReferenceMessages;
    private FirebaseUser currentUser;

    private Query query;

    private ShimmerFrameLayout shimmerFrameLayoutChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_dokter, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inisialisasi semua view
        rvChat = view.findViewById(R.id.rv_chats_personel);
        emptyChat = view.findViewById(R.id.ll_empty_chat);

        shimmerFrameLayoutChat = view.findViewById(R.id.shimmer_chat);

        ////set array list
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(),chatList);

        //set layout
        LinearLayoutManager linearLayout = new LinearLayoutManager(getContext());
        linearLayout.setReverseLayout(true);
        linearLayout.setStackFromEnd(true);

        //set group adapter
        rvChat.setLayoutManager(linearLayout);
        rvChat.setAdapter(chatAdapter);

        //inisialisasi database
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceDokters = FirebaseDatabase.getInstance().getReference().child(NodeNames.DOKTERS);
        databaseReferenceMessages = FirebaseDatabase.getInstance().getReference().child(NodeNames.MESSAGES);
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child(NodeNames.CHATS).child(currentUser.getUid());

        emptyChat.setVisibility(View.VISIBLE);
        loadChats();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadChats();
    }

    private void loadChats(){
        emptyChat.setVisibility(View.GONE);
        shimmerFrameLayoutChat.startShimmer();
        query = databaseReferenceChats.orderByChild(NodeNames.LAST_MESSAGE_TIME);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                shimmerFrameLayoutChat.stopShimmer();
                shimmerFrameLayoutChat.setVisibility(View.GONE);
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        rvChat.setVisibility(View.VISIBLE);
                        emptyChat.setVisibility(View.GONE);
                        Chats chats = ds.getValue(Chats.class);
                        loadDokters(chats);
                        chatList.add(chats);
                    }

                    if(chatList.size()>1){
                        try {
                            Collections.sort(chatList);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }else{
                    rvChat.setVisibility(View.GONE);
                    emptyChat.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),R.string.tidak_ada,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDokters(Chats chats) {
        databaseReferenceDokters.child(chats.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Dokters dokters = snapshot.getValue(Dokters.class);
                    chats.setName(dokters.getNama());
                    chats.setPhoto(dokters.getPhoto());
                    loadLastMessage(chats);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),R.string.tidak_ada,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadLastMessage(Chats chats){
        databaseReferenceMessages.child(currentUser.getUid()).child(chats.getId()).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot ds :snapshot.getChildren()){
                    Messages messages = ds.getValue(Messages.class);
                    chats.setLastMessage(messages.getMessage());
                    chats.setLastMessageTime(messages.getMessageTime());
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Toast.makeText(getContext(),R.string.tidak_ada,Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}