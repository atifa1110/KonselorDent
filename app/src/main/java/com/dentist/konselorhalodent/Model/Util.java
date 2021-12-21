package com.dentist.konselorhalodent.Model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dentist.konselorhalodent.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Util {

    public static void updateDeviceToken(Context context, String token) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference databaseReference = rootRef.child(NodeNames.TOKENS).child(currentUser.getUid());

            HashMap<String,String> hashMap = new HashMap<>();
            hashMap.put(NodeNames.DEVICE_TOKEN,token);

            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(context,"Gagal menyimpan Token Device",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public static void updateChatDetails(Context context, String currentUserId, String chatUserId, String lastMessage){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        //asking the chat user whenever we sent the message unread count will
        //increament
        DatabaseReference chatRef = rootRef.child(NodeNames.CHATS).child(chatUserId).child(currentUserId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String currentCount = "0";
                //jika sudah ada di database
                if(snapshot.child(NodeNames.UNREAD_COUNT).getValue()!=null){
                    currentCount = snapshot.child(NodeNames.UNREAD_COUNT).getValue().toString();
                }

                Map chatMap = new HashMap();
                chatMap.put(NodeNames.TIME_STAMP, ServerValue.TIMESTAMP);
                //convert string into integer whenever sending a message it will increment the unread data
                chatMap.put(NodeNames.UNREAD_COUNT,Integer.valueOf(currentCount)+1);
                chatMap.put(NodeNames.LAST_MESSAGE,lastMessage);
                chatMap.put(NodeNames.LAST_MESSAGE_TIME,ServerValue.TIMESTAMP);

                //put the chat map into chat userMap
                Map chatUserMap = new HashMap();
                chatUserMap.put(NodeNames.CHATS+ "/" + chatUserId +"/" +currentUserId,chatMap);

                rootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                        if(error!=null){
                            //Toast.makeText(context,context.getString(R.string.something_wrong,error.getMessage()),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public static void sendNotification(Context context,String title , String message , String userId){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseReference = rootRef.child(NodeNames.TOKENS).child(userId);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(NodeNames.DEVICE_TOKEN).getValue()!=null){
                    String deviceToken = snapshot.child(NodeNames.DEVICE_TOKEN).getValue().toString().trim();
                    JSONObject notification = new JSONObject();
                    JSONObject notificationData = new JSONObject();

                    try {
                        notificationData.put(Constant.NOTIFICATION_TITLE,title.trim());
                        notificationData.put(Constant.NOTIFICATION_MESSAGE,message.trim());

                        notification.put(Constant.NOTIFICATION_TO,deviceToken);
                        notification.put(Constant.NOTIFICATION_DATA,notificationData);

                        Log.d(notificationData.toString(),"notificationData");
                        Log.d(notification.toString(),"notification");

                        String fcmApiUrl = "https://fcm.googleapis.com/fcm/send";
                        String contentType = "application/jcon";

                        Response.Listener successListener = new Response.Listener() {
                            @Override
                            public void onResponse(Object response) {
                                Toast.makeText(context,"Notification Sent",Toast.LENGTH_SHORT).show();
                            }
                        };

                        Response.ErrorListener errorListener = new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context,context.getString(R.string.failed_to_send_notification,error.getMessage()),Toast.LENGTH_SHORT).show();
                            }
                        };

                        //using volley to make web api
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,fcmApiUrl,notification,
                                successListener,errorListener){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put("Authorization","key="+Constant.FIREBASE_KEY);
                                params.put("Sender", "Id=" +Constant.SENDER_ID);
                                params.put("Content-Type",contentType);

                                return params;
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(context);
                        requestQueue.add(jsonObjectRequest);

                    } catch (JSONException e) {
                        Toast.makeText(context,context.getString(R.string.failed_to_send_notification,e.getMessage()),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context,context.getString(R.string.failed_to_send_notification,error.getMessage()),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static String getTimeAgo(long time) {
        //set date format
        SimpleDateFormat sfd = new SimpleDateFormat("EEE dd/MM/yyyy HH:mm");
        String dateTime = sfd.format(time);

        //split date format
        String [] splitString = dateTime.split(" ");
        String day = splitString[0];
        String date = splitString[1];
        String lastMessageTime = splitString[2];

        long now = System.currentTimeMillis();

        // Get msec from each, and subtract.
        final long diff = now - time;

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours   = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);

        String text = null;
        if (minutes < 1) {
            return text = "Just now";
        } else if (hours < 24) {
            return text = lastMessageTime;
        } else if (hours < 48) {
            return text = "Yesterday";
        }else if(days < 7) {
            return text = day;
        }else{
            return text = date;
        }
    }
}

