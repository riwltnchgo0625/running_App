package com.example.javachipnavigationbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.HashMap;

public class Register extends AppCompatActivity {


    EditText register_email;
    EditText register_name;
    EditText register_password;
    EditText register_weight;
    EditText register_height;
    Button signup_button;
    TextView go_register;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference rDatabase;

    // create object of DatabaseReference class to access firebase's Realtime Database
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://runwithme-4783a-default-rtdb.firebaseio.com/");
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_name = (EditText) findViewById(R.id.register_name);
        register_email = (EditText) findViewById(R.id.register_email);
        register_password = (EditText) findViewById(R.id.register_password);
        register_height = (EditText) findViewById(R.id.register_height);
        register_weight = (EditText) findViewById(R.id.register_weight);
        signup_button = (Button) findViewById(R.id.signup_button);
        go_register = (TextView) findViewById(R.id.go_register);

        //firebase정의
        rDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //로그인되어있으면 바로 메인으로
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }

        //Register 버튼 누를시 작동
        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = register_email.getText().toString().trim();
                String Password = register_password.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String Email = user.getEmail();
                                    String Uid = user.getUid();
                                    String name = register_name.getText().toString().trim();
                                    String ProfileUrl = "https://ifh.cc/g/nW7yqq.png";
                                    String Walking = "0";
                                    String Weight = register_weight.getText().toString().trim();
                                    String Height = register_height.getText().toString().trim();

                                    //User user1 = new User(name, Email, Uid, ProfileUrl, Walking, Weight, Height);
                                    User user1 = new User(name, Email, Uid, ProfileUrl, Weight, Height);
                                    HashMap<String, Object> user_data = user1.usertomap();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("User");
                                    reference.child(Uid).setValue(user_data);


                                    Intent intent = new Intent(Register.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(Register.this, "등록 에러", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                        });

            }
        });


        //login now 버튼
        TextView TextView = findViewById(R.id.go_login);
        TextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
            }

        });

    }

    }
