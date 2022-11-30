package com.example.javachipnavigationbar.Running;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.javachipnavigationbar.R;

import java.io.InputStream;

public class EditJourney extends AppCompatActivity {
    private final int RESULT_LOAD_IMG = 1;

    private ImageView journeyImg;
    private EditText titleET;
    private EditText commentET;
    private RatingBar rating;
    private long journeyID;

    private Uri selectedJourneyImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_journey);

        Bundle bundle = getIntent().getExtras();

        //데이터 선언
        journeyImg = findViewById(R.id.journeyImg);
        titleET = findViewById(R.id.titleEditText);
        commentET = findViewById(R.id.commentEditText);
        journeyID = bundle.getLong("journeyID");
        rating = (RatingBar)findViewById(R.id.ratingBarInficator);

        //저장 버튼
       Button button5 = (Button)findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToMain();
            }
        });

        selectedJourneyImg = null;
        populateEditText();

    }


    public  void returnToMain() {

        Intent intent = new Intent();

        Uri rowQueryUri = Uri.withAppendedPath(JourneyProviderContract.JOURNEY_URI, "" + journeyID);

        //수정 데이터 저장 (ET를 TV로)
        ContentValues cv = new ContentValues();
        cv.put(JourneyProviderContract.J_COMMENT, commentET.getText().toString());
        cv.put(JourneyProviderContract.J_NAME, titleET.getText().toString());
        cv.put(JourneyProviderContract.J_RATING, rating.getRating());

        //수정 데이터 - 사진 null일 경우 실행
        if(selectedJourneyImg != null) {
            cv.put(JourneyProviderContract.J_IMAGE, selectedJourneyImg.toString());
        }

        getContentResolver().update(rowQueryUri, cv, null, null);

        //전달
        setResult(RESULT_OK,intent);
        finish();
    }

    /* Load activity to choose an image */
    public void onClickChangeImage(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        // get the URI from the selected image
        switch(reqCode) {
            case RESULT_LOAD_IMG: {
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();

                        // make the URI persistent
                        getContentResolver().takePersistableUriPermission(imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        journeyImg.setImageBitmap(selectedImage);
                        selectedJourneyImg = imageUri;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(EditJourney.this, "You didn't pick an Image",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    /* Give the edit texts some initial text from what they were, get this by accessing DB */
    @SuppressLint("Range")
    private void populateEditText() {
        Cursor c = getContentResolver().query(Uri.withAppendedPath(JourneyProviderContract.JOURNEY_URI,
                journeyID + ""), null, null, null, null);

        //수정 시 커서를 맨 앞으로
        if(c.moveToFirst()) {
            //제목, 코멘트, 별점 저장된 데이터 불러오기 (수정 전 데이터)
            titleET.setText(c.getString(c.getColumnIndex(JourneyProviderContract.J_NAME)));
            commentET.setText(c.getString(c.getColumnIndex(JourneyProviderContract.J_COMMENT)));
            rating.setRating(c.getInt(c.getColumnIndex(JourneyProviderContract.J_RATING)));

            //사용자가 지정한 이미지가 있을시, 해당 이미지 로드 / 없을시 default 이미지 로드
            String strUri = c.getString(c.getColumnIndex(JourneyProviderContract.J_IMAGE));
            if(strUri != null) {
                try {
                    final Uri imageUri = Uri.parse(strUri);
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    journeyImg.setImageBitmap(selectedImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
