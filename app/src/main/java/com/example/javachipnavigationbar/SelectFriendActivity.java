/*
package com.example.javachipnavigationbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.javachipnavigationbar.Chatting.GroupMessageActivity;
import com.example.javachipnavigationbar.Chatting.MessageActivity;
import com.example.javachipnavigationbar.model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

//그룹채팅 select friend

public class SelectFriendActivity extends AppCompatActivity {

    ChatModel chatModel = new ChatModel();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friend);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.selectFriendActivity_recyclerview);
        recyclerView.setAdapter(new SelectFriendRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Button button = (Button)findViewById(R.id.selectFriendActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                chatModel.User.put(myUid,true);
                FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel);

                Intent intent = new Intent(SelectFriendActivity.this, GroupMessageActivity.class);
                startActivity(intent);
            }
        });
    }
    class SelectFriendRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //강의 -> 우리코드
        //userModels -> users_model
        //UserModel -> User

        List<User> users_model;
        public SelectFriendRecyclerViewAdapter() {
            users_model = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("User").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //누적 데이터 없애주기
                    users_model.clear();
                    //서버에서 넘어온 데이터 담아주기
                    for(DataSnapshot snapshot :dataSnapshot.getChildren()) {
                        User userModel = snapshot.getValue(User.class);
                        if (userModel.Uid.equals(myUid)){
                            continue;
                        }
                        users_model.add(userModel);
                    }
                    //새로고침해주기
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend_select,parent,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with
                    (holder.itemView.getContext())
                    .load(users_model.get(position).ProfileUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into( ((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(users_model.get(position).Name);

            //클릭해서 채팅창 뜨게
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",users_model.get(position).Uid);
                    ActivityOptions activityOptions = null;
                    //애니메이션 효과 넣기
                    //ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getView().getContext(), R.anim.fromright,R.anim.toleft);
                    //startActivity(intent,activityOptions.toBundle());
                    startActivity(intent);
                }
            });

            ((CustomViewHolder) holder).checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //true인 경우 체크된상태
                        chatModel.User.put(users_model.get(position).Uid,true);
                    }
                    else {
                        //체크 취소상태
                        chatModel.User.remove(users_model.get(position));
                    }
                }
            });




        }

        @Override
        public int getItemCount() {
            return users_model.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public CheckBox checkBox;
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
                checkBox = (CheckBox) view.findViewById(R.id.friendItem_checkbox);
            }

        }
    }
}*/
