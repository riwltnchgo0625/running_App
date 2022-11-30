/*
package com.example.javachipnavigationbar.Chatting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.javachipnavigationbar.R;
import com.example.javachipnavigationbar.User;
import com.example.javachipnavigationbar.model.ChatModel;
import com.example.javachipnavigationbar.users_model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class GroupMessageActivity extends AppCompatActivity {
    Map<String, users_model> users = new HashMap<>();
    private String destinationRoom;
    String Uid;
    EditText editText;
    private String destinationUid;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyy.MM.dd HH:mm");

    List<ChatModel.Comment> comments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        destinationRoom = getIntent().getStringExtra("destinationRoom");
        Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        destinationUid = getIntent().getStringExtra("destinationUid"); //채팅을 당하는 아이디
        editText = (EditText)findViewById(R.id.groupMessageActivity_editText);
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()) {
                    users.put(item.getKey(),item.getValue(users_model.class));
                }

                init();
                //유저를 불러오고
                recyclerView = (RecyclerView) findViewById(R.id.groupMessageActivity_recyclerview);
                recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }
    void init() {
        Button button = (Button) findViewById(R.id.groupMessageActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.Uid = Uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editText.setText("");

                    }
                });
            }
        });
    }
    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<ChatModel.Comment> comments;
        User userModel;

        public GroupMessageRecyclerViewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("User").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModel = dataSnapshot.getValue(User.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        void getMessageList(){
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments");
            valueEventListener=databaseReference.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();

                    for(DataSnapshot item : dataSnapshot.getChildren()) {
                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    //메세지가 갱신
                    notifyDataSetChanged();

                    recyclerView.scrollToPosition(comments.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_msg,parent,false);

            return new GroupMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            GroupMessageViewHolder messageViewHolder = ((GroupMessageViewHolder)holder);

            //내가 보낸 메세지
            if(comments.get(position).Uid.equals(Uid)){
                messageViewHolder.textView_msg.setText(comments.get(position).message);
                messageViewHolder.textView_msg.setBackgroundResource(R.drawable.right_bubble);
                messageViewHolder.LinearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_msg.setTextSize(25);
                messageViewHolder.LinearLayout_main.setGravity(Gravity.RIGHT);
            }
            //상대방이 보낸 메세지
            else {
                Glide.with(holder.itemView.getContext())
                        .load(users.get(comments.get(position).Uid).userphoto)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textView_name.setText(users.get(comments.get(position).Uid).usernm);

                messageViewHolder.LinearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_msg.setBackgroundResource(R.drawable.left_bubble);
                messageViewHolder.textView_msg.setText(comments.get(position).message);
                messageViewHolder.textView_msg.setTextSize(25);
                messageViewHolder.LinearLayout_main.setGravity(Gravity.LEFT);
            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time); //현재 날짜로 알아볼 수 있게 변경

        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class GroupMessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_msg;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout LinearLayout_destination;
            public LinearLayout LinearLayout_main;
            public TextView textView_timestamp;

            public GroupMessageViewHolder(View view) {
                super(view);

                textView_msg = view.findViewById(R.id.tv_msg);
                textView_name = view.findViewById(R.id.msg_tv_name);
                imageView_profile =(ImageView) view.findViewById(R.id.msg_img_profile);
                LinearLayout_destination = view.findViewById(R.id.msg_linear_destination);
                LinearLayout_main = view.findViewById(R.id.msg_linear_main);
                textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textview_timestamp);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (valueEventListener!=null) {
            databaseReference.removeEventListener(valueEventListener);
        }
        finish();
        //overridePendingTransition(R.anim.fromleft,R.anim.toright);
    }
}*/
