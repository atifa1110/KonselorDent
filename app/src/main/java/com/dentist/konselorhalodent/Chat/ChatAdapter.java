package com.dentist.konselorhalodent.Chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dentist.konselorhalodent.Utils.Extras;
import com.dentist.konselorhalodent.Model.NodeNames;
import com.dentist.konselorhalodent.R;
import com.dentist.konselorhalodent.Utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<Chats> chatlist;
    private HashMap<String,String> chatUser;
    private HashMap<String,String> chatPhoto;
    private HashMap<String,String> chatLastMessage;
    private HashMap<String,String> chatLastMessageTime;
    private FirebaseUser currentUser;

    public ChatAdapter(Context context, List<Chats> chatlist) {
        this.context = context;
        this.chatlist = chatlist;
        this.chatUser = new HashMap<>();
        this.chatPhoto = new HashMap<>();
        this.chatLastMessage = new HashMap<>();
        this.chatLastMessageTime = new HashMap<>();
    }

    @NonNull
    @NotNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_list,parent,false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatAdapter.ChatViewHolder holder, int position) {
        Chats chats = chatlist.get(position);

        String nama = chatUser.get(chats.getId());
        String photo = chatPhoto.get(chats.getId());
        String message = chatLastMessage.get(chats.getId());
        String time = chatLastMessageTime.get(chats.getId());

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        try{
            if(nama==null || nama.equals("default")) {
                holder.userName.setText(" ");
            }else{
                holder.userName.setText(nama);
            }

            if(photo==null || photo.equals("default")) {
                holder.ivProfile.setImageResource(R.drawable.ic_user);
            }else{
                Glide.with(context)
                        .load(photo)
                        .placeholder(R.drawable.ic_user)
                        .error(R.drawable.ic_user)
                        .into(holder.ivProfile);
            }

            String msg = message.length()>30?message.substring(0,30):message;
            if(message==null) {
                holder.lastMessage.setText(" ");
            }else{
                if(msg.startsWith("https://firebasestorage")){
                    holder.lastMessage.setText("New Image");
                }else {
                    holder.lastMessage.setText(msg);
                }
            }

            if(time==null) {
                holder.lastMessageTime.setText(" ");
            }else{
                holder.lastMessageTime.setText(Util.getTimeAgo(Long.parseLong(time)));
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        //check if unread not 0
        holder.unreadCount.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(Extras.USER_KEY, chats.getId());
                intent.putExtra(Extras.USER_NAME, nama);
                intent.putExtra(Extras.USER_PHOTO, photo);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Apakah ingin menghapus chat ini?")
                        .setCancelable(false)
                        .setPositiveButton("iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteChat(chats.getId());
                            }
                        }).setNegativeButton("tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatlist.size();
    }


    private void deleteChat(String id){
        DatabaseReference chats = FirebaseDatabase.getInstance().getReference().child(NodeNames.CHATS).child(currentUser.getUid());
        DatabaseReference messages = FirebaseDatabase.getInstance().getReference().child(NodeNames.MESSAGES).child(currentUser.getUid());

        chats.child(id).removeValue();
        messages.child(id).removeValue();
    }


    public void setChatUserName(String id,String nama){
        chatUser.put(id,nama);
    }

    public void setChatPhotoName(String id,String photo){
        chatPhoto.put(id,photo);
    }

    public void setChatLastMessage(String id,String message){
        chatLastMessage.put(id,message);
    }

    public void setChatLastMessageTime(String id,String time){
        chatLastMessageTime.put(id,time);
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder{

        private TextView userName,unreadCount,lastMessage, lastMessageTime;
        private ImageView ivProfile;

        public ChatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            ivProfile = itemView.findViewById(R.id.iv_profile_chat);
            userName = itemView.findViewById(R.id.tv_name_chat);
            unreadCount = itemView.findViewById(R.id.tv_unread_count_chat);
            lastMessage = itemView.findViewById(R.id.tv_last_message_chat);
            lastMessageTime = itemView.findViewById(R.id.tv_last_message_time_chat);
        }
    }
}
