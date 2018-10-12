package homeaway.com.placefinder.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import homeaway.com.placefinder.R;
import homeaway.com.placefinder.model.venueModel.Venue;
import homeaway.com.placefinder.util.FavoritePreferences;

/**
 * An activity to display all the details about a venue
 */
public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    String detailID, detailName, detailCategory, detailStatus, detailMenu, detailMobileMenu, detailUrl,
            detailContact, detailAddress, detailRatingcolor, detailIcon, venueJSON;
    Double detailRating;
    boolean isOpen, hasMenu;
    double detailLat, detailLng;
    Venue venue;

    //preferences for bookmark
    FavoritePreferences favoritePreferences;

    //coordinator layout
    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
//    FloatingActionButton floatingActionButton;

    //map view
//    MapView mapView;
    private GoogleMap mMap;

    //detail fields to display
    TextView name, category, hours, rating, address;
    ImageButton favorite;
    Button menu, website, contact;

//    String venueFromMap;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        favoritePreferences = FavoritePreferences.getFavoritePreferences();

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        appBarLayout = findViewById(R.id.appbar_lay);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                } else if (isShow) {
                    isShow = false;
                }
            }
        });

        //retrieve intent values
        Intent intent = getIntent();
        venueJSON = intent.getStringExtra("venueJson");
        detailID = intent.getStringExtra("ID");
        detailName = intent.getStringExtra("name");
        detailIcon = intent.getStringExtra("icon");
        detailCategory = intent.getStringExtra("category");
        detailStatus = intent.getStringExtra("openHourStatus");
        isOpen = intent.getBooleanExtra("isOpenNow", false);
        hasMenu = intent.getBooleanExtra("hasMenu", false);
        detailMenu = intent.getStringExtra("menu");
        detailMobileMenu = intent.getStringExtra("mobileMenu");
        detailUrl = intent.getStringExtra("url");
        detailContact = intent.getStringExtra("contact");
        detailAddress = intent.getStringExtra("address");
        detailRating = intent.getDoubleExtra("rating", 0d);
        detailRatingcolor = intent.getStringExtra("ratingcolor");
        detailLat = intent.getDoubleExtra("latitude", 0d);
        detailLng = intent.getDoubleExtra("longitude", 0d);

//        venueFromMap = intent.getStringExtra("venueData");

//        Toast.makeText(this,
//                detailName + " " +
//                        detailID + " " +
//                        detailLat +" " +
//                        detailLng +" " +
//                        detailCategory +" " +
//                        detailStatus +" " +
//                        isOpen +" " +
//                        hasMenu +" " +
//                        detailMenu +" " +
//                        detailMobileMenu +" " +
//                        detailUrl +" " +
//                        detailContact +" " +
//                        detailAddress +" " +
//                        detailRating +" " +
//                        detailIcon, Toast.LENGTH_LONG).show();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        name = findViewById(R.id.detail_name);
        category = findViewById(R.id.detail_category);
        hours = findViewById(R.id.detail_hours);
        favorite = findViewById(R.id.detail_bookmark);
        rating = findViewById(R.id.detail_rating);
        menu = findViewById(R.id.menu);
        website = findViewById(R.id.website);
        contact = findViewById(R.id.detail_phone);
        address = findViewById(R.id.detail_address);

        initDetailView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initDetailView() {

        //get json string from intent and convert to object
        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Venue>(){}.getType();
//        if(getIntent().getStringExtra("venueData").equals(venueFromMap)) {
//            detailsFromMap(gson, type);
//        } else if(getIntent().getStringExtra("venueJson").equals(venueJSON)){
            detailsFromSearch(gson, type);
//        }

    }

//    private void detailsFromMap(Gson gson, Type type) {
//        venue = gson.fromJson(venueFromMap, type);
//        name.setText(venue.getName());
//        category.setText(venue.getCategories().get(0).getName());
//        if (venue.getHours().getOpen() != null && venue.getHours().getStatus() != null) {
//            hours.setText(venue.getHours().getStatus());
//        } else if (venue.getHours().getOpen() == null) {
//            hours.setText(getString(R.string.open_now));
//        } else {
//            hours.setText(getString(R.string.closed));
//        }
//
//        if (venue.getRating() == 0d) {
//            rating.setText(getString(R.string.no_rating));
//        } else {
//            rating.setText(venue.getRating().toString());
//            rating.setBackgroundColor(Integer.parseInt(venue.getRatingColor(), 16));
//        }
//
//        if (venue.getHasMenu()) {
//            setUnderline(getResources().getString(R.string.view_menu), menu);
//            if (venue.getMenu() != null) {
//                menu.setOnClickListener(view -> openBrowser(venue.getMenu().getUrl()));
//            } else {
//                menu.setOnClickListener(view -> openBrowser(venue.getMenu().getMobileUrl()));
//            }
//        } else {
//            menu.setText(getString(R.string.menu_unavailable));
//        }
//
//        if (venue.getUrl() != null) {
//            setUnderline(getResources().getString(R.string.open_website), website);
//            website.setOnClickListener(view -> openBrowser(venue.getUrl()));
//        } else {
//            website.setText(getString(R.string.website_unavailable));
//        }
//
//        if (venue.getContact().getFormattedPhone() != null) {
//            setUnderline(venue.getContact().getFormattedPhone(), contact);
//            contact.setOnClickListener(view -> openCall(venue.getContact().getFormattedPhone()));
//        } else {
//            contact.setText(getString(R.string.no_contact));
//        }
//
//        address.setText(venue.getLocation().getFormattedAddress().toString());
//
//        //get favorite value from preferences
//        if(favoritePreferences.hasFavorited(this, venue.getId())) {
//            favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
//        } else {
//            favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
//        }
//
//        favorite.setOnClickListener(view -> {
//            if(!favoritePreferences.hasFavorited(DetailsActivity.this, venue.getId())) {
//                //save to favorites
//                favoritePreferences.addFavorites(DetailsActivity.this, venue);
//                favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
//                favoritePreferences.isFavorite(DetailsActivity.this, venue.getId(), true);
//                Toast.makeText(this, getString(R.string.add_favorite), Toast.LENGTH_SHORT).show();
//            } else {
//                //already favorited; remove from list
//                favoritePreferences.removeFavorites(DetailsActivity.this, venue);
//                favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
//                favoritePreferences.isFavorite(DetailsActivity.this, venue.getId(), false);
//                Toast.makeText(this, getString(R.string.remove_favorite), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void detailsFromSearch(Gson gson, Type type) {
        venue = gson.fromJson(venueJSON, type);
        name.setText(detailName);
        category.setText(detailCategory);
        if (isOpen && detailStatus != null) {
            hours.setText(detailStatus);
        } else if (isOpen) {
            hours.setText(getString(R.string.open_now));
        } else {
            hours.setText(getString(R.string.closed));
        }

        if (detailRating == 0d) {
            rating.setText(getString(R.string.no_rating));
        } else {
            rating.setText(detailRating.toString());
            rating.setBackgroundColor(Integer.parseInt(detailRatingcolor, 16));
        }

        if (hasMenu) {
            setUnderline(getResources().getString(R.string.view_menu), menu);
            if (detailMenu != null) {
                menu.setOnClickListener(view -> openBrowser(detailMenu));
            } else {
                menu.setOnClickListener(view -> openBrowser(detailMobileMenu));
            }
        } else {
            menu.setText(getString(R.string.menu_unavailable));
        }

        if (detailUrl != null) {
            setUnderline(getResources().getString(R.string.open_website), website);
            website.setOnClickListener(view -> openBrowser(detailUrl));
        } else {
            website.setText(getString(R.string.website_unavailable));
        }

        if (detailContact != null) {
            setUnderline(detailContact, contact);
            contact.setOnClickListener(view -> openCall(detailContact));
        } else {
            contact.setText(getString(R.string.no_contact));
        }

        address.setText(detailAddress);

        //get favorite value from preferences
        if(favoritePreferences.hasFavorited(this, detailID)) {
            favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
        } else {
            favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
        }

        favorite.setOnClickListener(view -> {
            if(!favoritePreferences.hasFavorited(DetailsActivity.this, detailID)) {
                //save to favorites
                favoritePreferences.addFavorites(DetailsActivity.this, venue);
                favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                favoritePreferences.isFavorite(DetailsActivity.this, detailID, true);
                Toast.makeText(this, getString(R.string.add_favorite), Toast.LENGTH_SHORT).show();
            } else {
                //already favorited; remove from list
                favoritePreferences.removeFavorites(DetailsActivity.this, venue);
                favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_white_24dp));
                favoritePreferences.isFavorite(DetailsActivity.this, detailID, false);
                Toast.makeText(this, getString(R.string.remove_favorite), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //implicit intent to open links in browser
    public void openBrowser(String url) {
        Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(implicit);
    }


    //implicit intent to call venue
    public void openCall(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

    //toolbar back button navigation
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //pass bookmark status back to update recyclerview
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("passed_item", "fromDetailsActivity");
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    /**
     * helper method for adding an underline
    * */
    public void setUnderline(String mystring, TextView textView) {
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        textView.setText(content);
    }


    //display map with venue and seattle center markers
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(mMap == null){
            return;
        }

        if (venue != null) {
            if(venue.getLocation() != null) {

                LatLng seattle = new LatLng(47.6062, -122.3321);
                LatLng place = new LatLng(detailLat, detailLng);


                mMap.addMarker(new MarkerOptions()
                        .position(seattle)
                        .title(detailName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(seattle));

                mMap.addMarker(new MarkerOptions()
                        .position(place)
                        .title(detailName)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place));

                mMap.setMaxZoomPreference(30);

            } else {
                LatLng latLng = new LatLng(47.6062, -122.3321);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(detailName)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.setMaxZoomPreference(30);

            }
        }


    }
}
