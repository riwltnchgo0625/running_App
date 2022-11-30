package com.example.javachipnavigationbar;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.javachipnavigationbar.Chatting.MessageActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PeopleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PeopleFragment extends Fragment {



    public PeopleFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_people,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.peoplefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        /*FloatingActionButton floatingActionButton = (FloatingActionButton)view.findViewById(R.id.peoplefragment_floatingButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(),SelectFriendActivity.class));
            }
        });*/

        return view;
    }
    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        //강의 -> 우리코드
        //userModels -> users_model
        //UserModel -> User

        List<User> users_model;
        public PeopleFragmentRecyclerViewAdapter() {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);
            return new CustomViewHolder(view);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Glide.with
                    (holder.itemView.getContext())
                    .load(users_model.get(position).ProfileUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)holder).imageView);
            ((CustomViewHolder)holder).textView.setText(users_model.get(position).Name);

            //클릭해서 채팅창 뜨게
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getView().getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",users_model.get(position).Uid);
                    //애니메이션 효과 넣기
                    //ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getView().getContext(), R.anim.fromright,R.anim.toleft);
                    //startActivity(intent,activityOptions.toBundle());
                    startActivity(intent);
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
            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imageview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
            }
        }
    }
}