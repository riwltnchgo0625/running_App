package com.example.javachipnavigationbar.Running;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.javachipnavigationbar.R;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecordJourney extends AppCompatActivity implements OnMapReadyCallback {
    private com.example.javachipnavigationbar.Running.GifPlayer gif;
    private LocationService.LocationServiceBinder locationService;

    private TextView distanceText;
    private TextView avgSpeedText;
    private TextView durationText;

    private Button playButton;
    private Button stopButton;
    private static final int PERMISSION_GPS_CODE = 1;

    // will poll the location service for distance and duration
    private Handler postBack = new Handler();

    private ServiceConnection lsc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            locationService = (LocationService.LocationServiceBinder) iBinder;

            // if currently tracking then enable stopButton and disable startButton
            initButtons();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (locationService != null) {
                        // get the distance and duration from the surface
                        float d = (float) locationService.getDuration();
                        long duration = (long) d;  // in seconds
                        float distance = locationService.getDistance();

                        long hours = duration / 3600;
                        long minutes = (duration % 3600) / 60;
                        long seconds = duration % 60;

                        float avgSpeed = 0;
                        if(d != 0) {
                            avgSpeed = distance / (d / 3600);
                        }

                        final String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                        final String dist = String.format("%.2f KM", distance);
                        final String avgs = String.format("%.2f KM/H", avgSpeed);

                        postBack.post(new Runnable() {
                            @Override
                            public void run() {
                                // post back changes to UI thread
                                durationText.setText(time);
                                avgSpeedText.setText(avgs);
                                distanceText.setText(dist);
                            }
                        });

                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            locationService = null;
        }
    };

    // whenever activity is reloaded while still tracking a journey (if back button is clicked for example)
    private void initButtons() {
        // no permissions means no buttons
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            return;
        }

        // if currently tracking then enable stopButton and disable startButton
        if(locationService != null && locationService.currentlyTracking()) {
            stopButton.setEnabled(true);
            playButton.setEnabled(false);
            gif.play();
        } else {
            stopButton.setEnabled(false);
            playButton.setEnabled(true);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_journey);

        gif = findViewById(R.id.gif);
        gif.setGifImageResource(R.drawable.running_man);
        gif.pause();

        distanceText = findViewById(R.id.distanceText);
        durationText = findViewById(R.id.durationText);
        avgSpeedText = findViewById(R.id.avgSpeedText);

        playButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        // connect to service to see if currently tracking before enabling a button
        stopButton.setEnabled(false);
        playButton.setEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // register broadcast receiver to receive low battery broadcasts
        try {
            MyReceiver receiver = new MyReceiver();
            registerReceiver(receiver, new IntentFilter(
                    Intent.ACTION_BATTERY_LOW));
        } catch(IllegalArgumentException  e) {
        }



        handlePermissions();

        // start the service so that it persists outside of the lifetime of this activity
        // and also bind to it to gain control over the service
        startService(new Intent(this, LocationService.class));
        bindService(
                new Intent(this, LocationService.class), lsc, Context.BIND_AUTO_CREATE);
    }


    public void onClickPlay(View view) {
        gif.play();
        // start the timer and tracking GPS locations
        locationService.playJourney();
        playButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void onClickStop(View view) {
        // save the current journey to the database
        float distance = locationService.getDistance();
        locationService.saveJourney();

        playButton.setEnabled(false);
        stopButton.setEnabled(false);

        gif.pause();

        DialogFragment modal = FinishedTrackingDialogue.newInstance(String.format("%.2f KM", distance));
        modal.show(getSupportFragmentManager(), "Finished");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // un-register this receiver since we only need it while recording GPS
        try {
            MyReceiver receiver = new MyReceiver();
            unregisterReceiver(receiver);
        } catch(IllegalArgumentException  e) {
        }

        // unbind to the service (if we are the only binding activity then the service will be destroyed)
        if(lsc != null) {
            unbindService(lsc);
            lsc = null;
        }
    }



    public static class FinishedTrackingDialogue extends DialogFragment {
        public static  FinishedTrackingDialogue newInstance(String distance) {
            Bundle savedInstanceState = new Bundle();
            savedInstanceState.putString("distance", distance);
            FinishedTrackingDialogue frag = new FinishedTrackingDialogue();
            frag.setArguments(savedInstanceState);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("저장되었습니다. 총 " + getArguments().getString("distance") + "를 달렸습니다.")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // go back to home screen
                            getActivity().finish();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


    // PERMISSION THINGS

    @Override
    public void onRequestPermissionsResult(int reqCode, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(reqCode, permissions, results);
        switch (reqCode) {
            case PERMISSION_GPS_CODE:
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    initButtons();
                    if (locationService != null) {
                        locationService.notifyGPSEnabled();
                    }
                } else {
                    // permission denied, disable GPS tracking buttons
                    stopButton.setEnabled(false);
                    playButton.setEnabled(false);
                }
                return;

        }
    }


    public static class NoPermissionDialogue extends DialogFragment {
        public static  NoPermissionDialogue newInstance() {
            NoPermissionDialogue frag = new NoPermissionDialogue();
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("GPS is required to track your journey!")
                    .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // user agreed to enable GPS
                            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_CODE);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // dialogue was cancelled
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private void handlePermissions() {
        // if don't have GPS permissions then request this permission from the user.
        // if not granted the permission disable the start button
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // the user has already declined request to allow GPS
                // give them a pop up explaining why its needed and re-ask
                DialogFragment modal = NoPermissionDialogue.newInstance();
                modal.show(getSupportFragmentManager(), "Permissions");
            } else {
                // request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS_CODE);
            }

        }
    }

    private GoogleMap googleMap;



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // 37.557667, 126.926546 홍대입구역
        LatLng latLng = new LatLng(37.56, 126.97);
        //private final LatLng mDefaultLocation = new LatLng(37.56, 126.97);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(18));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("서울");
        googleMap.addMarker(markerOptions);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermissionWithRationale();
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermissionWithRationale() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("위치정보")
                        .setMessage("이 앱을 사용하기 위해서는 위치정보에 접근이 필요합니다. 위치정보 접근을 허용하여 주세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(RecordJourney.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        }).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }


}
