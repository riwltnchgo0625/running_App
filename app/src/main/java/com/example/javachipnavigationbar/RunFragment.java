package com.example.javachipnavigationbar;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.javachipnavigationbar.Running.RecordJourney;

public class RunFragment extends Fragment {
    private CardView run_start;
    private View view;
    private CardView view_journeys;
    private CardView runguide;
    private CardView view_this_week;
    private View view1;


    public RunFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_run, container, false);

        run_start = (CardView) view.findViewById(R.id.free_running);
        run_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), RecordJourney.class);
                Intent intent = new Intent(getActivity(), RecordJourney.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        // Inflate the layout for this fragment


        view_journeys = (CardView) view.findViewById(R.id.view_journeys);

        view_journeys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), RecordJourney.class);
                Intent intent = new Intent(getActivity(), com.example.javachipnavigationbar.Running.ViewJourneys.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        //return view;

        // Inflate the layout for this fragment
        //view1 = inflater.inflate(R.layout.fragment_record, container, false);

        view_this_week = (CardView) view.findViewById(R.id.view_this_week);
        view_this_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), RecordJourney.class);
                Intent intent = new Intent(getActivity(), com.example.javachipnavigationbar.Running.StatisticsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        runguide = (CardView) view.findViewById(R.id.view_running_gide);
        runguide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), RecordJourney.class);
                Intent intent = new Intent(getActivity(), com.example.javachipnavigationbar.Running.RunGuideActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
        return view;
    }
}