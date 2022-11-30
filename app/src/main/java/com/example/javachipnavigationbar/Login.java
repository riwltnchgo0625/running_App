package com.example.javachipnavigationbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://runwithme-4783a-default-rtdb.firebaseio.com/");
    FirebaseAuth firebaseAuth;


    EditText login_email;
    EditText login_password;
    Button login_button;
    TextView go_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        login_email = (EditText) findViewById(R.id.login_email);
        login_password = (EditText) findViewById(R.id.login_password);
        login_button = (Button) findViewById(R.id.login_button);


        go_register = (TextView) findViewById(R.id.go_register);

        firebaseAuth = firebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class)); //추가해 줄 ProfileActivity
        }



        //이메일 패스워드 입력
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = login_email.getText().toString().trim();
                String pwd = login_password.getText().toString().trim();

                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Login.this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });


        //가입화면으로
        TextView TextView = findViewById(R.id.go_register);
        TextView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
            }

        });


    }

}