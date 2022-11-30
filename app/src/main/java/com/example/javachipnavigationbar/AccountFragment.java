package com.example.javachipnavigationbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

//내 정보
public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    Button logout_button;
    Button modify_button;

    //firebase
    private  FirebaseDatabase firebaseDatabase;
    private DatabaseReference rDatabase = FirebaseDatabase.getInstance().getReference("User");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
    private String Uid = user.getUid();


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        TextView textViewUserEmail = view.findViewById(R.id.User_email);

        //이메일 띄우기
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userEmail = firebaseAuth.getCurrentUser();
        textViewUserEmail.setText(userEmail.getEmail());

        //최근 uid 가져오기
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        String Uid = user.getUid();

        //유저 이름 가져오기
        rDatabase = FirebaseDatabase.getInstance().getReference("User");
        TextView UserName = view.findViewById(R.id.Username);

        TextView UserWeight = view.findViewById(R.id.User_weight);
        TextView UserHeight = view.findViewById(R.id.User_height);
        //사진
        CircleImageView profile_img = view.findViewById(R.id.account_img);



        if(user != null){
            rDatabase.child(Uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    com.example.javachipnavigationbar.User user1 = datasnapshot.getValue(com.example.javachipnavigationbar.User.class);

                    String Name = user1.Name;
                    String Weight = user1.Weight;
                    String Height = user1.Height;

                    UserName.setText( Name);
                    UserWeight.setText(Weight);
                    UserHeight.setText(Height);
                    
                    Picasso.get().load(user1.ProfileUrl).into(profile_img);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Getting Post failed, log a message
                    Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
                }
            });
        }

        //로그아웃, 수정 버튼 선언
        logout_button = (Button) view.findViewById(R.id.logout_button);
        modify_button = (Button) view.findViewById(R.id.modify_button);


        if(firebaseAuth.getCurrentUser() == null) {
            getActivity().finish();
            Intent intent = new Intent(view.getContext(), Login.class);
            intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            view.getContext().startActivity(intent);
        }

        //로그아웃 버튼 클릭
        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                firebaseAuth.signOut();
                getActivity().finish();
                Intent intent = new Intent(view.getContext(), Login.class);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });

        modify_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Profile_EditActivity.class);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });


       return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}