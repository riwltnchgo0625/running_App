package com.example.javachipnavigationbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.example.javachipnavigationbar.Chatting.GroupMessageActivity;
import com.example.javachipnavigationbar.Community_ui.AddPostActivity;
import com.example.javachipnavigationbar.Community_ui.Post;
import com.example.javachipnavigationbar.Community_ui.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


// 컴뮤니티 라인
public class ChatFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private Toolbar mainToolbar;
    private FirebaseFirestore firestore;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private PostAdapter adapter;
    private List<Post> list;
    private Query query;
    private ListenerRegistration listenerRegistration;
    private List<User> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        /*BottomNavigationView bottomNavigationView =  view.findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        //Set home selected
        bottomNavigationView.setSelectedItemId(R.id.community);*/

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        //mainToolbar = findViewById(R.id.main_toolbar);

        mRecyclerView = view.findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        list = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new PostAdapter(getActivity() , list, usersList);

        mRecyclerView.setAdapter(adapter);

        fab = view.findViewById(R.id.floatingActionButton);
        /*setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Community");*/

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startActivity(new Intent(ChatFragment.this , AddPostActivity.class));*/
                Intent intent = new Intent(view.getContext(), AddPostActivity.class);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);
            }
        });

        if (firebaseAuth.getCurrentUser() != null){

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isBottom = !mRecyclerView.canScrollVertically(1);
                    if (isBottom)
                        Toast.makeText(getActivity().getApplicationContext(), "Reached Bottom", Toast.LENGTH_SHORT).show();
                }
            });
            query = firestore.collection("Posts").orderBy("time" , Query.Direction.DESCENDING);
            listenerRegistration = query.addSnapshotListener((Activity) getActivity().getApplicationContext(), new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    for (DocumentChange doc : value.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){
                            String postId = doc.getDocument().getId();
                            Post post = doc.getDocument().toObject(Post.class).withId(postId);
                            String postUserId = doc.getDocument().getString("user");
                            firestore.collection("User").document(postUserId).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                User users = task.getResult().toObject(User.class);
                                                usersList.add(users);
                                                list.add(post);
                                                adapter.notifyDataSetChanged();
                                            }else{
                                                Toast.makeText(getActivity().getApplicationContext(), task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }
                    listenerRegistration.remove();
                }
            });
        }

        /*protected void onStart() {
            super.onStart();
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null){

                Intent intent = new Intent(view.getContext(), Login.class);
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(intent);

            }else{
                String currentUserId = firebaseAuth.getCurrentUser().getUid();
                firestore.collection("User").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            if (!task.getResult().exists()){
                                startActivity(new Intent(Community.this , SetUpActivity.class));
                                finish();
                                Intent intent = new Intent(view.getContext(), Login.class);
                                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                view.getContext().startActivity(intent);
                            }
                        }
                    }
                });
            }

        }*/


        return view;
    }














    //위에 점세개 눌렀을 때 나오는거 : 로그아웃, 프로필 수정 (프로필 수정을 info에 연결했음)
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile_menu){
            startActivity(new Intent(Community.this , SetUpActivity.class));
        }else if(item.getItemId() == R.id.sign_out_menu){
            firebaseAuth.signOut();
            startActivity(new Intent(Community.this , SignInActivity.class));
            finish();
        }
        return true;
    }*/

}
