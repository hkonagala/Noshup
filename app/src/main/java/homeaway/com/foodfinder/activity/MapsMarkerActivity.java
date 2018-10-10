package homeaway.com.foodfinder.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import homeaway.com.foodfinder.R;
import homeaway.com.foodfinder.model.venueModel.Venue;

public class MapsMarkerActivity extends AppCompatActivity implements OnMapReadyCallback {

    List<Venue> markers = new ArrayList<>();

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng mDefaultLocation = new LatLng(47.6062, -122.3321);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_marker);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


        for(int i =0; i < markers.size(); i++) {
            String icon = markers.get(i).getCategories().get(i).getIcon().getPrefix();
            if(icon.contains("ss3.4sqi.net")){
                icon = icon.replace("ss3.4sqi.net", "foursquare.com");
                icon = icon + "64" + markers.get(i).getCategories().get(0).getIcon().getSuffix();
            }
            createMarkerForEach(googleMap,
                    markers.get(i).getLocation().getLat(),
                    markers.get(i).getLocation().getLng(),
                    markers.get(i).getName(),
                    icon);
        }
        LatLng seattleCenter = new LatLng(47.6062, -122.3321);
        googleMap.addMarker(new MarkerOptions().position(seattleCenter)
                .title("Marker in Seattle"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seattleCenter));
        googleMap.setMaxZoomPreference(15);
    }

    private void createMarkerForEach(GoogleMap googleMap, Double lat, Double lng, String name, String icon) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .anchor(0.5f, 0.5f)
                .title(name)
                .icon(BitmapDescriptorFactory.fromPath(icon)));
    }


}
