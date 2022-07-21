package com.dentist.konselorhalodent.Chat.Chat;

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
import android.widget.TextView;

import com.dentist.konselorhalodent.Model.Dokters;
import com.dentist.konselorhalodent.Profile.DokterAdapter;
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


public class ChatPersonalFragment extends Fragment {

    private RecyclerView rvChat;
    private List<Chats> chatList;
    private ChatAdapter chatAdapter;
    private View emptyChat;

    private DatabaseReference databaseReferenceDokters, databaseReferenceChats;
    private FirebaseUser currentUser;

    private ChildEventListener childEventListener;
    private Query query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_personal, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inisialisasi semua view
        rvChat = view.findViewById(R.id.rv_chats_personel);
        emptyChat = view.findViewById(R.id.ll_empty_chat);

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
        databaseReferenceChats = FirebaseDatabase.getInstance().getReference().child(NodeNames.CHATS).child(currentUser.getUid());

        //set query dengan diurutkan dengan waktu kirim
        query = databaseReferenceChats.orderByChild(NodeNames.LAST_MESSAGE_TIME);

        loadChats();
    }

    private void loadChats(){
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    chatList.clear();
                    emptyChat.setVisibility(View.GONE);
                    for (DataSnapshot ds : snapshot.getChildren()){
                        String id = ds.getKey();
                        String lastMessage = ds.child(NodeNames.LAST_MESSAGE).getValue().toString();
                        String lastMessageTime = ds.child(NodeNames.LAST_MESSAGE_TIME).getValue().toString();
                        String unreadCount = ds.child(NodeNames.UNREAD_COUNT).getValue().toString();

                        databaseReferenceDokters.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Dokters dokters = snapshot.getValue(Dokters.class);
                                Chats chats = new Chats(id,dokters.getNama(),dokters.getPhoto(),unreadCount,lastMessage,lastMessageTime);
                                chatList.add(chats);
                                chatAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }else{
                    chatList.clear();
                    emptyChat.setVisibility(View.VISIBLE);
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