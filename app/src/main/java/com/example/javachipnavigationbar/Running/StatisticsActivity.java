package com.example.javachipnavigationbar.Running;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.javachipnavigationbar.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class StatisticsActivity extends AppCompatActivity {

    private BarChart barChart;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        // 화면에 뜨는 기록 정보들
        recordDistance = findViewById(R.id.Statistics_recordDistance);
        distanceToday = findViewById(R.id.Statistics_distanceToday);
        timeToday = findViewById(R.id.Statistics_timeToday);
        distanceAllTime = findViewById(R.id.Statistics_distanceAllTime);
        dateText = findViewById(R.id.Statistics_selectDate);
        barChart = findViewById(R.id.barchart);

        setUpDateDialogue();


    }

    private void setUpDateDialogue() {
        //select date
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
                        StatisticsActivity.this, R.style.DatePickerTheme,
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
        //final String date = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
        final String date = dateParts[0] + "-" + dateParts[1] + "-" + dateParts[2];

        // some heavy lifting here so lets run the processing code another thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // get the time today and distance today
                Cursor c = getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                        null, JourneyProviderContract.J_DATE + " = ?", new String[]{date}, null);
                double distaneTodayKM = 0;
                long timeTodayS = 0;
                try {
                    while (c.moveToNext()) {
                        distaneTodayKM += c.getDouble(c.getColumnIndex(com.example.javachipnavigationbar.Running.JourneyProviderContract.J_distance));
                        timeTodayS += c.getLong(c.getColumnIndex(com.example.javachipnavigationbar.Running.JourneyProviderContract.J_DURATION));
                    }
                } finally {
                    c.close();
                }

                final long hours = timeTodayS / 3600;
                final long minutes = (timeTodayS % 3600) / 60;
                final long seconds = timeTodayS % 60;
                final double distanceTodayKM_ = distaneTodayKM;


                // calculate record distance in 1 day and total distance travelled all time
                c = getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                        null, null, null, null);
                double totalDistanceKM = 0;
                double recordDistanceKM = 0;
                try {
                    while (c.moveToNext()) {
                        double d = c.getDouble(c.getColumnIndex(com.example.javachipnavigationbar.Running.JourneyProviderContract.J_distance));
                        if (recordDistanceKM < d) {
                            recordDistanceKM = d;
                        }
                        totalDistanceKM += d;
                    }
                } finally {
                    c.close();
                }

                final double totalDistanceKM_ = totalDistanceKM;
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
                    for (int i = 0; i < 6; i++) {
                        cal.add(Calendar.DATE, 1);
                    }
                    String sun = sdf.format(cal.getTime());

                    Log.d("mdp", "Mon = " + mon + ", Sun = " + sun);
                    c = getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                            null, JourneyProviderContract.J_DATE + " BETWEEN ? AND ?",
                            new String[]{mon, sun}, null);
                    try {
                        for (int i = 0; c.moveToNext(); i++) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int i = 1; i < distancesOnDays.length; i++) {
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
                        recordDistance.setText(String.format("%.2f KM", recordDistanceKM_));
                        distanceAllTime.setText(String.format("%.2f KM", totalDistanceKM_));
                        loadBarChart(entries_);
                    }
                });
            }
        }).start();

    }


    private void loadBarChart(ArrayList<BarEntry> entries) {
        BarDataSet bardataset = new BarDataSet(entries, "날짜");
        bardataset.setColor(Color.WHITE);

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("월");
        labels.add("화");
        labels.add("수");
        labels.add("목");
        labels.add("금");
        labels.add("토");
        labels.add("일");

        BarData data = new BarData(labels, bardataset);
        barChart.setData(data); // set the data and list of labels into chart
        barChart.setDescription("Distance (KM)");  // set the description
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setDescriptionColor(Color.WHITE);


        //x축
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinValue(0f);
        xAxis.setTextColor(ContextCompat.getColor(barChart.getContext(), R.color.white));

        barChart.animateY(3000);
        data.setValueTextColor(Color.parseColor("#ffffff"));

        //y축 왼쪽
        YAxis yAxisLeft = barChart.getAxisLeft();
        yAxisLeft.setTextColor(ContextCompat.getColor(barChart.getContext(), R.color.white));

        //y축 오른쪽
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setTextColor(ContextCompat.getColor(barChart.getContext(), R.color.white));



    }
}