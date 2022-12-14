package com.example.javachipnavigationbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile_EditActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    //firebase
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rDatabase = FirebaseDatabase.getInstance().getReference("users");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
    private String Uid = user.getUid();
    private FirebaseFirestore firestore;

    private Button btn_save;
    private CircleImageView circleImageView;
    private Uri mImageUri ;
    private boolean isPhotoSelected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // <?????? ????????????>

        //?????????
        TextView textViewUserEmail = findViewById(R.id.User_email);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser userEmail = firebaseAuth.getCurrentUser();
        textViewUserEmail.setText(userEmail.getEmail());

        //?????? uid ????????????
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        String Uid = user.getUid();

        //?????? ?????? ??????
        rDatabase = FirebaseDatabase.getInstance().getReference("User");
        EditText UserName = findViewById(R.id.Username);
        EditText UserWeight = findViewById(R.id.User_weight);
        EditText UserHeight = findViewById(R.id.User_height);
        CircleImageView profile_img = findViewById(R.id.account_img);


        //loadData(); ????????? ????????????
        if (user != null) {
            rDatabase.child(Uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    com.example.javachipnavigationbar.User user1 = datasnapshot.getValue(com.example.javachipnavigationbar.User.class);

                    String Name = user1.Name;
                    String Weight = user1.Weight;
                    String Height = user1.Height;

                    UserName.setText(Name);
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

        //????????? ????????? ??? -> ?????? ?????? ??? ??????
        circleImageView = findViewById(R.id.account_img);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (ContextCompat.checkSelfPermission(Profile_EditActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Profile_EditActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .start(Profile_EditActivity.this);
                    }
                }
            }
        });

        //?????? ?????? ????????? ???, ???????????? ???????????? account fragment??? ????????????.
        btn_save = (Button) findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //???????????? ??? ????????? ??????
                String Email = user.getEmail();
                String name = UserName.getText().toString().trim();
                String Weight = UserWeight.getText().toString().trim();
                String Height = UserHeight.getText().toString().trim();

                //????????? ??????
                storageReference = FirebaseStorage.getInstance().getReference();
                String ProfileUrl = mImageUri.toString();

                //????????? ????????? ?????? ??????
                User user1 = new User(name, Email, Uid, ProfileUrl, Weight, Height);
                HashMap<String, Object> user_data = user1.usertomap();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("User");
                reference.child(Uid).setValue(user_data);

                Toast.makeText(Profile_EditActivity.this, "?????????????????????.", Toast.LENGTH_SHORT).show();

                //account fragment??? ?????????
                finish(); //?????? ???????????? ??????
            }
        });
    }


    //?????? ????????? ????????? ????????? (??????x)
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                circleImageView.setImageURI(mImageUri);

                isPhotoSelected = true;
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, result.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}



