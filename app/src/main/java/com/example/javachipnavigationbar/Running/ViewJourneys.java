package com.example.javachipnavigationbar.Running;

import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.javachipnavigationbar.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class ViewJourneys extends ListActivity {
    private TextView dateText;
    private DatePickerDialog.OnDateSetListener dateListener;

    private ListView journeyList;
    private JourneyAdapter adapter;
    private ArrayList<JourneyItem> journeyNames;

    //달력
    Calendar minDate  = Calendar.getInstance();
    Calendar maxDate = Calendar.getInstance();

    /* Class to store all the information needed to display journey row item */
    private class JourneyItem {
        private String name;
        private String strUri;
        private long _id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setStrUri(String strUri) {
            this.strUri = strUri;
        }

        public String getStrUri() {
            return strUri;
        }

        public void set_id(long _id) {
            this._id = _id;
        }

        public long get_id() {
            return _id;
        }
    }

    /* ListView should display journey name along side a custom image uploaded by the user */
    private class JourneyAdapter extends ArrayAdapter<JourneyItem> {
        //
        private ArrayList<JourneyItem> items;

        public JourneyAdapter(Context context, int textViewResourceId, ArrayList<JourneyItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.journeylist, null);
            }

            JourneyItem item = items.get(position);
            if (item != null) {
                TextView text = v.findViewById(R.id.singleJourney);
                ImageView img = v.findViewById(R.id.journeyList_journeyImg);
                if (text != null) {
                    text.setText(item.getName());
                }
                if(img != null) {
                    String strUri = item.getStrUri();
                    if(strUri != null) {
                        try {
                            final Uri imageUri = Uri.parse(strUri);
                            final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            img.setImageBitmap(selectedImage);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        img.setImageDrawable(getResources().getDrawable(R.drawable.running));
                    }
                }
            }
            return v;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_journeys);

        journeyNames = new ArrayList<JourneyItem>();
        adapter = new JourneyAdapter(this, R.layout.journeylist, journeyNames);
        setListAdapter(adapter);
        setUpDateDialogue();

        journeyList.setClickable(true);
        journeyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                JourneyItem o = (JourneyItem) journeyList.getItemAtPosition(position);
                long journeyID = o.get_id();

                // start the single journey activity sending it the journeyID
                Bundle b = new Bundle();
                b.putLong("journeyID", journeyID);
                Intent singleJourney = new Intent(ViewJourneys.this, ViewSingleJourney.class);
                singleJourney.putExtras(b);
                startActivity(singleJourney);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // update the view in-case title or image was changed
        String date = dateText.getText().toString();
        if(!date.toLowerCase().equals("날짜를 선택하세요")) {
            listJourneys(date);
        }
    }

    private void setUpDateDialogue() {
        dateText = findViewById(R.id.selectDateText);
        journeyList = getListView();

        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int yyyy;
                int mm;
                int dd;

                // if first time selecting date choose current date, else last selected date
                if(dateText.getText().toString().toLowerCase().equals("날짜를 선택하세요")) {
                    Calendar calender = Calendar.getInstance();
                    yyyy = calender.get(Calendar.YEAR);
                    mm = calender.get(Calendar.MONTH);
                    dd = calender.get(Calendar.DAY_OF_MONTH);
                } else {
                    String[] dateParts = dateText.getText().toString().split("/");
                    yyyy = Integer.parseInt(dateParts[0]);
                    mm = Integer.parseInt(dateParts[1]) - 1;
                    dd = Integer.parseInt(dateParts[2]);
                }

                DatePickerDialog dialog = new DatePickerDialog(
                        ViewJourneys.this,R.style.DatePickerTheme,
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
                // user has selected a date on which to view journeys
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

                listJourneys(date);
            }
        };
    }

    /* Query database to get all journeys in specified date in dd/mm/yyyy format and display them in listview */
    private void listJourneys(String date) {
        // sqlite server expects yyyy-mm-dd
        String[] dateParts = date.split("/");
        date = dateParts[0] + "-" + dateParts[1] + "-" + dateParts[2];

        Log.d("mdp", "Searching for date " + date);

        Cursor c = getContentResolver().query(JourneyProviderContract.JOURNEY_URI,
                new String[] {JourneyProviderContract.J_ID + " _id", JourneyProviderContract.J_NAME, JourneyProviderContract.J_IMAGE}, JourneyProviderContract.J_DATE + " = ?", new String[] {date}, JourneyProviderContract.J_NAME + " ASC");

        Log.d("mdp", "Journeys Loaded: " + c.getCount());

        // put cursor items into ArrayList and add those items to the adapter
        journeyNames = new ArrayList<JourneyItem>();
        adapter.notifyDataSetChanged();
        adapter.clear();
        adapter.notifyDataSetChanged();
        try {
            while(c.moveToNext()) {
                JourneyItem i = new JourneyItem();
                i.setName(c.getString(c.getColumnIndex(JourneyProviderContract.J_NAME)));
                i.setStrUri(c.getString(c.getColumnIndex(JourneyProviderContract.J_IMAGE)));
                i.set_id(c.getLong(c.getColumnIndex("_id")));
                journeyNames.add(i);
            }
        } finally {
            if(journeyNames != null && journeyNames.size() > 0) {
                adapter.notifyDataSetChanged();
                for(int i = 0; i < journeyNames.size(); i++) {
                    adapter.add(journeyNames.get(i));
                }
            }
            c.close();
            adapter.notifyDataSetChanged();
        }

        /*
        String[] nameCol = new String[] {JourneyProviderContract.J_NAME};
        int[] textViewIds = new int[] {R.id.singleJourney};
        journeyList.setAdapter(new SimpleCursorAdapter(this, R.layout.journeylist, c, nameCol, textViewIds, 0));
        */
    }
}