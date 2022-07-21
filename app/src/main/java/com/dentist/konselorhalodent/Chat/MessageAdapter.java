package com.dentist.konselorhalodent.Chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dentist.konselorhalodent.Model.Constant;
import com.dentist.konselorhalodent.Model.Dokters;
import com.dentist.konselorhalodent.Model.Konselors;
import com.dentist.konselorhalodent.Model.Pasiens;
import com.dentist.konselorhalodent.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context context;
    private List<Messages> messageList;
    private FirebaseAuth firebaseAuth;

    private ActionMode actionMode;
    private ConstraintLayout selectedView;

    public MessageAdapter(Context context, List<Messages> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @NotNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message_list,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  @NotNull MessageAdapter.MessageViewHolder holder, int position) {
        Messages messages = messageList.get(position);

        firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId= firebaseAuth.getCurrentUser().getUid();
        String fromUserId = messages.getMessageFrom();

        Log.d(currentUserId,"currentID");
        Log.d(fromUserId,"fromUser");

        SimpleDateFormat sfd = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String dateTime = sfd.format(new Date(Long.parseLong(messages.getMessageTime())));
        String [] splitString = dateTime.split(" ");
        String messageTime = splitString[3];
        String datemonth = splitString[0]+" "+splitString[1]+" "+splitString[2];

        holder.tvChatTime.setText(datemonth);
        //check
        if(fromUserId.equals(currentUserId)){
            if(messages.getMessageType().equals(Constant.MESSAGE_TYPE_TEXT)){
                holder.card_llSent.setVisibility(View.VISIBLE);
                holder.tvSentMessageTime.setVisibility(View.VISIBLE);
                holder.ivSent.setVisibility(View.GONE);
                holder.tvImageSentTime.setVisibility(View.GONE);

                holder.card_llSent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //creating a popup menu
                        PopupMenu popup = new PopupMenu(context, holder.card_llSent);
                        //inflating menu from xml resource
                        popup.inflate(R.menu.menu_jadwal);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()){
                                    case R.id.action_hapus:

                                        break;
                                }
                                return false;
                            }
                        });
                        popup.show();
                    }
                });

            }else{
                holder.ivSent.setVisibility(View.VISIBLE);
                holder.tvImageSentTime.setVisibility(View.VISIBLE);
                holder.card_llSent.setVisibility(View.GONE);
                holder.tvSentMessageTime.setVisibility(View.GONE);
            }
            holder.card_llReceived.setVisibility(View.GONE);
            holder.tvReceivedMessageTime.setVisibility(View.GONE);
            holder.ivReceived.setVisibility(View.GONE);
            holder.tvImageReceivedTime.setVisibility(View.GONE);

            holder.tvSentMessage.setText(messages.getMessage());
            holder.tvSentMessageTime.setText(messageTime);
            holder.tvImageSentTime.setText(messageTime);

            try{
                Glide.with(context).load(messages.getMessage())
                        .placeholder(R.drawable.ic_add_photo)
                        .fitCenter()
                        .into(holder.ivSent);
            }catch (Exception e){
                holder.ivSent.setImageResource(R.drawable.ic_add_photo);
            }

        }else{
            if(messages.getMessageType().equals(Constant.MESSAGE_TYPE_TEXT)){
                holder.card_llReceived.setVisibility(View.VISIBLE);
                holder.tvReceivedMessageTime.setVisibility(View.VISIBLE);
                holder.ivReceived.setVisibility(View.GONE);
                holder.tvImageReceivedTime.setVisibility(View.GONE);
                setUserName(messages,holder);
            }else{
                holder.ivReceived.setVisibility(View.VISIBLE);
                holder.tvImageReceivedTime.setVisibility(View.VISIBLE);
                holder.card_llReceived.setVisibility(View.GONE);
                holder.tvReceivedMessageTime.setVisibility(View.GONE);
            }
            holder.card_llSent.setVisibility(View.GONE);
            holder.tvSentMessageTime.setVisibility(View.GONE);
            holder.ivSent.setVisibility(View.GONE);
            holder.tvImageSentTime.setVisibility(View.GONE);

            holder.tvReceivedMessage.setText(messages.getMessage());
            holder.tvReceivedMessageTime.setText(messageTime);
            holder.tvImageReceivedTime.setText(messageTime);

            try{
                Glide.with(context).load(messages.getMessage())
                        .placeholder(R.drawable.ic_add_photo)
                        .fitCenter()
                        .into(holder.ivReceived);
            }catch (Exception e){
                holder.ivReceived.setImageResource(R.drawable.ic_add_photo);
            }
        }

        //tag is a mechanism to make your views remember something,
        // that could be an object an integer a string or anything you like.
        holder.clMessage.setTag(R.id.TAG_MESSAGE, messages.getMessage());
        holder.clMessage.setTag(R.id.TAG_MESSAGE_ID, messages.getMessageId());
        holder.clMessage.setTag(R.id.TAG_MESSAGE_TYPE, messages.getMessageType());

        holder.clMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageType = v.getTag(R.id.TAG_MESSAGE_TYPE).toString();
                Uri uri = Uri.parse(v.getTag(R.id.TAG_MESSAGE).toString());
                if (messageType.equals(Constant.MESSAGE_TYPE_IMAGE)){
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setDataAndType(uri,"image/jpg");
                    context.startActivity(intent);
                }
            }
        });
    }

    private void setUserName(Messages messages, MessageViewHolder holder) {
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Pasiens.class.getSimpleName());
        ref.child(messages.getMessageFrom()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Pasiens pasiens = snapshot.getValue(Pasiens.class);
                    holder.tvReceiveName.setText(pasiens.getNama());
                }else{
                    setKonselorName(messages,holder);
                }
            }

            @Override
            public void onCancelled(@NonNull @com.google.firebase.database.annotations.NotNull DatabaseError error) {

            }
        });
    }

    private void setKonselorName(Messages messages, MessageViewHolder holder) {
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Konselors.class.getSimpleName());
        ref.child(messages.getMessageFrom()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @com.google.firebase.database.annotations.NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Konselors konselors = snapshot.getValue(Konselors.class);
                    holder.tvReceiveName.setText(konselors.getNama());
                }else{
                    setDokterName(messages,holder);
                }
            }

            @Override
            public void onCancelled(@NonNull @com.google.firebase.database.annotations.NotNull DatabaseError error) {

            }
        });
    }

    private void setDokterName(Messages messages, MessageViewHolder holder) {
        //get sender info from uid model
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(Dokters.class.getSimpleName());
        ref.child(messages.getMessageFrom()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Dokters dokters = snapshot.getValue(Dokters.class);
                    holder.tvReceiveName.setText(dokters.getNama());
                    holder.tvReceiveName.setTextColor(context.getResources().getColor(R.color.blue));
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        private CardView card_llSent , card_llReceived;
        private TextView tvSentMessage, tvSentMessageTime, tvReceivedMessage, tvReceivedMessageTime, tvReceiveName;
        private ImageView ivSent, ivReceived;
        private TextView tvImageSentTime, tvImageReceivedTime,tvChatTime;
        private ConstraintLayout clMessage;

        public MessageViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            clMessage = itemView.findViewById(R.id.clMessage);

            card_llSent = itemView.findViewById(R.id.card_llSent);
            tvSentMessage = itemView.findViewById(R.id.tvSentMessage);
            tvSentMessageTime = itemView.findViewById(R.id.tvSentMessageTime);

            card_llReceived = itemView.findViewById(R.id.card_llReceived);
            tvReceiveName = itemView.findViewById(R.id.tvReceivedName);
            tvReceivedMessage = itemView.findViewById(R.id.tvReceivedMessage);
            tvReceivedMessageTime = itemView.findViewById(R.id.tvReceivedMessageTime);

            ivSent = itemView.findViewById(R.id.ivSent);
            tvImageSentTime = itemView.findViewById(R.id.tvSentImageTime);

            ivReceived =itemView.findViewById(R.id.ivReceived);
            tvImageReceivedTime = itemView.findViewById(R.id.tvReceivedImageTime);
            tvChatTime = itemView.findViewById(R.id.tv_chat_time);
        }
    }
}
