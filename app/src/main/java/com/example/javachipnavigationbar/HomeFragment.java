package com.example.javachipnavigationbar;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.javachipnavigationbar.Running.JourneyProviderContract;
import com.example.javachipnavigationbar.Running.StatisticsActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class HomeFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference rDatabase = FirebaseDatabase.getInstance().getReference("User");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
    private String Uid = user.getUid();

    //기록 관련
    private TextView recordDistance;
    private TextView distanceToday;
    private TextView timeToday;
    private TextView distanceAllTime;
    private TextView dateText;
    private DatePickerDialog.OnDateSetListener dateListener;
    private Handler postBack = new Handler();

    //달력
    Calendar minDate  = Calendar.getInstance();
    Calendar maxDate = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //회원 이름, 프로필 사진 선언
        TextView UserName =view.findViewById(R.id.Home_Username);
        CircleImageView profile_img = view.findViewById(R.id.Home_img);

        //회원정보
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
        String Uid = user.getUid();
        rDatabase = FirebaseDatabase.getInstance().getReference("User");

        //loadData();
        if(user != null){
            rDatabase.child(Uid).addValueEventListener(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                    com.example.javachipnavigationbar.User user1 = datasnapshot.getValue(com.example.javachipnavigationbar.User.class);

                    String Name = user1.Name;

                    UserName.setText( Name );
                    Picasso.get().load(user1.ProfileUrl).into(profile_img);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Getting Post failed, log a message
                    Log.w("FireBaseData", "loadPost:onCancelled", error.toException());
                }
            });
        }

        /* statics 화면 구성 */
        // 화면에 뜨는 기록 정보들 선언
        recordDistance  = view.findViewById(R.id.Statistics_recordDistance);
        distanceToday   = view.findViewById(R.id.Statistics_distanceToday);
        timeToday       = view.findViewById(R.id.Statistics_timeToday);
        distanceAllTime = view.findViewById(R.id.Statistics_distanceAllTime);
        dateText        = view.findViewById(R.id.Statistics_selectDate);

        setUpDateDialogue();

        return view;
    }


    private void setUpDateDialogue() {
        //날짜 선택
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int yyyy;
                int mm;
                int dd;

                // if first time selecting date choose current date, else last selected date
                if (dateText.getText().toString().toLowerCase().equals("날짜를 선택하세요")) {
                    Calendar calender = Calendar.getInstance();
                    yyyy = calender.get(Calendar.YEAR);
                    mm = calender.get(Calendar.MONTH);
                    dd = calender.get(Calendar.DAY_OF_MONTH);
                } else {
                    String[] dateParts = dateText.getText().toString().split("/");
                    yyyy = Integer.parseInt(dateParts[2]);
                    mm = Integer.parseInt(dateParts[1]) - 1;
                    dd = Integer.parseInt(dateParts[0]);
                }


                //datepicker
                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),R.style.DatePickerTheme,
                        dateListener,
                        yyyy, mm, dd);
                minDate.set(2022,1-1,1);
                dialog.getDatePicker().setMinDate(minDate.getTime().getTime());

                maxDate.set(2099,2-1,17);
                dialog.getDatePicker().setMaxDate(maxDate.getTime().getTime());

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                dialog.show();
            }
        });

        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int yyyy, int mm, int dd) {
                // user has selected a date on which to get statistics about
                mm = mm + 1;
                String date;

                // format the date so its like dd/mm/yyyy
                if (mm < 10) {
                    //월이 10보다 작은 경우

                    if (dd<10) {
                        //일도 10보다 작은경우
                        date = yyyy + "/0" + mm + "/0" + dd;
                    }
                    else {
                        //일은 10보다 크지만 월은 10보다 작은경우
                        //date = dd + "/0" + mm + "/" + yyyy;
                        date = yyyy + "/0" + mm + "/" + dd;
                    }

                } else {
                    //월이 10보다 큰 경우
                    if (dd<10) {
                        //10월 이상 일이 10이하인경우
                        date = yyyy + "/" + mm + "/0" + dd;
                    }
                    else {
                        //date = dd + "/" + mm + "/" + yyyy;
                        date = yyyy + "/" + mm + "/" + dd;
                    }

                }

                dateText.setText(date);
                displayStatsOnDate(date);
            }
        };
    }

    private void displayStatsOnDate(String d) {
        // sqlite server expects yyyy-mm-dd
        final String[] dateParts = d.split("/");
        final String date = dateParts[0] + "-" + dateParts[1] + "-" + dateParts[2];

        // some heavy lifting here so lets run the processing code another thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // get the time today and distance today
                Cursor c = getActivity().getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                        null,JourneyProviderContract.J_DATE + " = ?", new String[] {date}, null);
                double distaneTodayKM = 0;
                long   timeTodayS = 0;
                try {
                    while(c.moveToNext()) {
                        distaneTodayKM += c.getDouble(c.getColumnIndex(com.example.javachipnavigationbar.Running.JourneyProviderContract.J_distance));
                        timeTodayS     += c.getLong(c.getColumnIndex(com.example.javachipnavigationbar.Running.JourneyProviderContract.J_DURATION));
                    }
                } finally {
                    c.close();
                }

                final long hours = timeTodayS / 3600;
                final long minutes = (timeTodayS % 3600) / 60;
                final long seconds = timeTodayS % 60;
                final double distanceTodayKM_ = distaneTodayKM;


                // calculate record distance in 1 day and total distance travelled all time
                c = getActivity().getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                        null, null, null, null);
                double totalDistanceKM = 0;
                double recordDistanceKM = 0;
                try {
                    while(c.moveToNext()) {
                        double d = c.getDouble(c.getColumnIndex(com.example.javachipnavigationbar.Running.JourneyProviderContract.J_distance));
                        if(recordDistanceKM < d) {
                            recordDistanceKM = d;
                        }
                        totalDistanceKM += d;
                    }
                } finally {
                    c.close();
                }

                final double totalDistanceKM_  = totalDistanceKM;
                final double recordDistanceKM_ = recordDistanceKM;


                // load journeys for this week to display on the bar chart
                ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
                float[] distancesOnDays = {0, 0, 0, 0, 0, 0, 0};
                try {
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cal.setTime(sdf.parse(date));
                    cal.setFirstDayOfWeek(Calendar.MONDAY);

                    // set the calendar to monday of the current week
                    cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

                    // print dates of the current week starting on Monday
                    String mon = sdf.format(cal.getTime());
                    for (int i = 0; i <6; i++) {
                        cal.add(Calendar.DATE, 1);
                    }
                    String sun = sdf.format(cal.getTime());

                    Log.d("mdp", "Mon = " + mon + ", Sun = " + sun);
                    c = getActivity().getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                            null, JourneyProviderContract.J_DATE + " BETWEEN ? AND ?",
                            new String[] {mon, sun}, null);
                    try {
                        for(int i = 0; c.moveToNext(); i++) {
                            // put each journey into the bar chart depending on what day it is
                            String date = c.getString(c.getColumnIndex(com.example.javachipnavigationbar.Running.JourneyProviderContract.J_DATE));
                            cal = Calendar.getInstance();
                            cal.setTime(sdf.parse(date));
                            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); // 1 = sunday, 2 = mon ... 7 = sat
                            distancesOnDays[dayOfWeek - 1] += (float) c.getDouble(c.getColumnIndex(com.example.javachipnavigationbar.Running.JourneyProviderContract.J_distance));
                        }
                    } finally {
                        c.close();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
                for(int i = 1; i < distancesOnDays.length; i++) {
                    // don't add sunday first, add it last
                    entries.add(new BarEntry(distancesOnDays[i], i - 1));
                }
                entries.add(new BarEntry(distancesOnDays[0], distancesOnDays.length - 1));
                final ArrayList<BarEntry> entries_ = entries;

                // post back text view updates to UI thread
                postBack.post(new Runnable() {
                    public void run() {
                        distanceToday.setText(String.format("%.2f KM", distanceTodayKM_));
                        timeToday.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                        //recordDistance.setText(String.format("%.2f KM", recordDistanceKM_));
                        distanceAllTime.setText(String.format("%.2f KM", totalDistanceKM_));

                    }
                });
            }
        }).start();

    }




}