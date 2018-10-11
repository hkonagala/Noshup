package homeaway.com.foodfinder.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.util.Map;

import homeaway.com.foodfinder.R;
import homeaway.com.foodfinder.model.venueModel.Venue;
import homeaway.com.foodfinder.util.FavoritePreferences;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    String detailID, detailName, detailCategory, detailStatus, detailMenu, detailMobileMenu, detailUrl,
            detailContact, detailAddress, detailRatingcolor, detailIcon, venueJSON;
    Double detailRating;
    boolean isOpen, hasMenu;
    double detailLat, detailLng;
    FavoritePreferences favoritePreferences;

    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    FloatingActionButton floatingActionButton;
    MapView mapView;
    TextView name, category, hours, rating, website, menu, contact, address;
    ImageView favorite;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        favoritePreferences = FavoritePreferences.getFavoritePreferences();
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

        Toast.makeText(this,
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
                        detailIcon, Toast.LENGTH_LONG).show();

        initDetailView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initDetailView() {

        Gson gson = new Gson();
        java.lang.reflect.Type type = new TypeToken<Venue>(){}.getType();
        Venue venue = gson.fromJson(venueJSON, type);


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
//                    showOption(R.id.action_info);
                } else if (isShow) {
                    isShow = false;
//                    hideOption(R.id.action_info);
                }
            }
        });

        floatingActionButton = findViewById(R.id.detail_fab);
        mapView = findViewById(R.id.map_view);
        mapView.getMapAsync(this);
        name = findViewById(R.id.detail_name);
        category = findViewById(R.id.detail_category);
        hours = findViewById(R.id.detail_hours);
        favorite = findViewById(R.id.detail_bookmark);
        rating = findViewById(R.id.detail_rating);
        menu = findViewById(R.id.menu);
        website = findViewById(R.id.website);
        contact = findViewById(R.id.detail_phone);
        address = findViewById(R.id.detail_address);

        Picasso.with(this)
                .load(detailIcon)
                .resize(80,80)
                .error(getDrawable(R.drawable.ic_error_black_24dp))
                .into(floatingActionButton);

        name.setText(detailName);
        category.setText(detailCategory);
        if (isOpen && detailStatus != null) {

            hours.setText(getString(R.string.today) + " " + detailStatus);
        } else if (isOpen) {
            hours.setText(getString(R.string.open_now));
        } else {
            hours.setText(getString(R.string.closed));
        }

//        rating.setText(detailRating.toString());

        if (detailRating == 0d) {
            rating.setText(getString(R.string.no_rating));
        } else {
            rating.setText(detailRating.toString());
            rating.setBackgroundColor(Integer.parseInt(detailRatingcolor, 16));
//            String hexValue = detailRatingcolor;
//            if (detailRatingcolor != null) {
//                rating.setTextColor(Integer.parseInt(hexValue, 16));
//            } else {
//                if (detailRating >= 9.0) {
//                    rating.setBackgroundColor(ContextCompat.getColor(this, R.color.Kale));
//                } else if (detailRating >= 8.0) {
//                    rating.setBackgroundColor(ContextCompat.getColor(this, R.color.Guacamole));
//                } else if (detailRating >= 7.0) {
//                    rating.setBackgroundColor(ContextCompat.getColor(this, R.color.Lime));
//                } else if (detailRating >= 6.0) {
//                    rating.setBackgroundColor(ContextCompat.getColor(this, R.color.Banana));
//                } else if (detailRating >= 5.0) {
//                    rating.setBackgroundColor(ContextCompat.getColor(this, R.color.Orange));
//                } else if (detailRating >= 4.0) {
//                    rating.setBackgroundColor(ContextCompat.getColor(this, R.color.MacCheese));
//                } else {
//                    rating.setBackgroundColor(ContextCompat.getColor(this, R.color.Strawberry));
//                }
//            }
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
            setUnderline(getString(R.string.contact) + " " + detailContact, contact);
            contact.setOnClickListener(view -> openCall(contact.getText().toString()));
        } else {
            contact.setText(getString(R.string.no_contact));
        }

        address.setText(getString(R.string.address) + " " + detailAddress);

        //get favorite value
        if(favoritePreferences.hasFavorited(this, detailID)) {
         favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
        } else {
            favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
        }

        favorite.setOnClickListener(view -> {
            Log.i("details", String.valueOf(favoritePreferences.hasFavorited(DetailsActivity.this, detailID)));
            if(!favoritePreferences.hasFavorited(DetailsActivity.this, detailID)) {
                //save to favorites
                favoritePreferences.addFavorites(DetailsActivity.this, venue);
                favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                favoritePreferences.isFavorite(DetailsActivity.this, detailID, true);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            } else {
                //already favorited; remove from list
                favoritePreferences.removeFavorites(DetailsActivity.this, venue);
                favorite.setImageDrawable(getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                favoritePreferences.isFavorite(DetailsActivity.this, detailID, false);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openBrowser(String url) {
        Intent implicit = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(implicit);
    }

    public void openCall(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(callIntent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("passed_item", "fromDetailsActivity");
        setResult(RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    public void setUnderline(String mystring, TextView textView) {
        SpannableString content = new SpannableString(mystring);
        content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        textView.setText(content);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng seattle = new LatLng(47.6062, -122.3321);
        googleMap.addMarker(new MarkerOptions().position(seattle)
                .title("Seattle"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(seattle));
        googleMap.setMaxZoomPreference(15);

        LatLng location = new LatLng(detailLat, detailLng);
        googleMap.addMarker(new MarkerOptions().position(location)
                .title("Seattle"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        googleMap.setMaxZoomPreference(15);



    }
}
