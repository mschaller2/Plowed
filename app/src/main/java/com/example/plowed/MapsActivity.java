package com.example.plowed;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private LocationManager locationManager;
    private TextView zipCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        zipCode = (TextView) findViewById(R.id.zipCode);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null){
            mapFragment.getMapAsync(this);
        }else{
            Log.e("onCreate MapsActivity", "mapFragment null");
        }
    }
    public void updateLocationInfo(GoogleMap mMap){
        Location location;
        LatLng loc;
        String zip;
        try{
            Intent incoming = getIntent();
            location = (Location) incoming.getParcelableExtra("location");
            zip = incoming.getStringExtra("zip");
            zipCode.setText(String.format("Zip Code: %s", zip));
            if (location != null && !incoming.getBooleanExtra("manual", false)){
                loc = new LatLng(location.getLatitude(), location.getLongitude());
            }else{
                Geocoder geo = new Geocoder(this);
                List<Address> addresses = geo.getFromLocationName(zip, 1);
                loc = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());
            }
        }catch(Exception e){
            Log.e("updateLocationInfo Maps", "Using default location");
            Log.e("updateLocationInfo Maps", e.toString());
            loc = new LatLng(43.071365, -89.406672);
        }
        mMap.addMarker(new MarkerOptions().position(loc).title("Marker in Wisconsin"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, mMap.getMaxZoomLevel() - 3));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        updateLocationInfo(googleMap);
    }

}
