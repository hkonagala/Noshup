package homeaway.com.foodfinder.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import homeaway.com.foodfinder.R;
import homeaway.com.foodfinder.model.venueModel.Venue;

/**
 * An activity to display a map with all the venues listed
 */
public class MapsMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

    List<Venue> venueList;
    private GoogleMap mMap;
    private Marker previousMarker = null;

    //seattle center
    private final LatLng mDefaultLocation = new LatLng(47.6062, -122.3321);
    private static final int DEFAULT_ZOOM = 15;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_marker);

        toolbar = findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //intent from search activity
        Intent intent = getIntent();
        String list = intent.getStringExtra("venueListJson");
        //convert json string to venue list
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<List<Venue>>(){}.getType();
        venueList = gson.fromJson(list, type);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mMap == null){
            return;
        }
        if(venueList == null || venueList.isEmpty()) {
            //do something
        }

        for (int i = 0; i < venueList.size(); i++) {
            Venue venue = venueList.get(i);
            LatLng position = new LatLng(venue.getLocation().getLat(), venue.getLocation().getLng());

            String venueId = venue.getId();
            String venueName = venue.getName();
            //icon
            String url = venue.getCategories().get(0).getIcon().getPrefix();
            if(url.contains("ss3.4sqi.net")){
                url = url.replace("ss3.4sqi.net", "foursquare.com");
                url = url + "bg_64" + venue.getCategories().get(0).getIcon().getSuffix();
            }
            Uri uri = Uri.parse(url);


            MarkerOptions markerOptions = new MarkerOptions();
            mMap.addMarker(markerOptions.position(position)).setTitle(venueName);

            if (i == 0) {
                CameraPosition camPos = new CameraPosition(position, DEFAULT_ZOOM, 0, 0);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPos));
            }
        }

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setCompassEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String localAddress = marker.getTitle();

                if (previousMarker != null) {
                    previousMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                previousMarker = marker;
                return true;
            }
        });
    }

    //toolbar back button navigation
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
