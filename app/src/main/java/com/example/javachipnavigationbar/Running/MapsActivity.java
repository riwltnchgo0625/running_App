package com.example.javachipnavigationbar.Running;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.javachipnavigationbar.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import static com.example.javachipnavigationbar.Running.RecordJourney.MY_PERMISSIONS_REQUEST_LOCATION;

/* Page to display journey of a user on the map */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private long journeyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle bundle = getIntent().getExtras();
        journeyID = bundle.getLong("journeyID");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        //37.557552, 126.924767 홍대입구역

        LatLng latLng = new LatLng(37.56, 126.97);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(20)); //카메라 줌 레벨 확대
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("서울");
        mMap.addMarker(markerOptions);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
        } else {
            checkLocationPermissionWithRationale();
        }

        // draw polyline
        Cursor c = getContentResolver().query(com.example.javachipnavigationbar.Running.JourneyProviderContract.LOCATION_URI,
                null, com.example.javachipnavigationbar.Running.JourneyProviderContract.L_JID + " = " + journeyID, null, null);

        PolylineOptions line = new PolylineOptions().clickable(false);
        LatLng firstLoc = null;
        LatLng lastLoc = null;
        try {
            while (c.moveToNext()) {
                LatLng loc = new LatLng(c.getDouble(c.getColumnIndex(JourneyProviderContract.L_LATITUDE)),
                        c.getDouble(c.getColumnIndex(JourneyProviderContract.L_LONGITUDE)));
                if (c.isFirst()) {
                    firstLoc = loc;
                }
                if (c.isLast()) {
                    lastLoc = loc;
                }
                line.add(loc);
            }
        } finally {
            c.close();
        }



        float zoom = 18.0f;
        if (lastLoc != null && firstLoc != null) {
            //mMap.addMarker(new MarkerOptions().position(firstLoc).title("Start"));

            int width1= 150;
            int height1 = 150;
            BitmapDrawable start = (BitmapDrawable)getResources().getDrawable(R.drawable.start);
            Bitmap b1 = start.getBitmap();
            Bitmap iconstart = Bitmap.createScaledBitmap(b1, width1, height1, false);
            mMap.addMarker(new MarkerOptions().position(firstLoc).title("Start").icon(BitmapDescriptorFactory.fromBitmap(iconstart)));


            int width2 = 150;
            int height2 = 150;
            BitmapDrawable finish = (BitmapDrawable)getResources().getDrawable(R.drawable.finish);
            Bitmap b2 = finish.getBitmap();
            Bitmap iconfinish = Bitmap.createScaledBitmap(b2, width2, height2, false);
            mMap.addMarker(new MarkerOptions().position(lastLoc).title("Finish").icon(BitmapDescriptorFactory.fromBitmap(iconfinish)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, zoom));
        }
        mMap.addPolyline(line);
    }

    private void checkLocationPermissionWithRationale() {


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }


    }
}
