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
    private List<ChatModel> chatList;
    private ChatAdapter chatAdapter;
    private View emptyChat;

    private DatabaseReference databaseReferenceDokters, databaseReferenceChats;
    private FirebaseUser currentUser;

    private ChildEventListener childEventListener;
    private Query query;

    private List<String> userIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_personal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //inisialisasi semua view
        rvChat = view.findViewById(R.id.rv_chats_personel);
        emptyChat = view.findViewById(R.id.ll_empty_chat);

        ////set array list
        userIds = new ArrayList<>();
        chatList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(),chatList);

        //set layout
        LinearLayoutManager linearLayout = new LinearLayoutManager(getActivity());
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
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadChats(snapshot,true ,snapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                loadChats(snapshot,false,snapshot.getKey());
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
        emptyChat.setVisibility(View.GONE);
    }

    private void loadChats(DataSnapshot snapshot,boolean isNew,String userId){
        emptyChat.setVisibility(View.GONE);

        final String lastMessage , lastMessageTime , unreadCount;

        if(snapshot.child(NodeNames.LAST_MESSAGE).getValue()!=null)
            lastMessage = snapshot.child(NodeNames.LAST_MESSAGE).getValue().toString();
        else
            lastMessage = "";

        if(snapshot.child(NodeNames.LAST_MESSAGE_TIME).getValue()!=null)
            lastMessageTime = snapshot.child(NodeNames.LAST_MESSAGE_TIME).getValue().toString();
        else
            lastMessageTime="";

        unreadCount=snapshot.child(NodeNames.UNREAD_COUNT).getValue()==null?
                "0":snapshot.child(NodeNames.UNREAD_COUNT).getValue().toString();

        databaseReferenceDokters.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String fullName = snapshot.child(NodeNames.NAME).getValue()!=null?
                        snapshot.child(NodeNames.NAME).getValue().toString(): "";

                String photoName = snapshot.child(NodeNames.PHOTO).getValue()!=null?
                        snapshot.child(NodeNames.PHOTO).getValue().toString(): "";

                ChatModel chatModel = new ChatModel(userId,fullName,photoName,unreadCount,lastMessage,lastMessageTime);

                if(isNew) {
                    chatList.add(chatModel);
                    userIds.add(userId);
                }else{
                    int indexofClickedUser = userIds.indexOf(userId);
                    chatList.set(indexofClickedUser,chatModel);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(getActivity(), getActivity().getString(R.string.failed_to_fetch_chat_list,error.getMessage()),Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        query.removeEventListener(childEventListener);
    }
}