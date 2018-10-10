package homeaway.com.foodfinder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import homeaway.com.foodfinder.R;

public class DetailsActivity extends AppCompatActivity {

    String detailID, detailName, detailCategory, detailStatus, detailMenu, detailMobileMenu, detailUrl, detailContact, detailAddress, detailRatingcolor;
    Double detailRating;
    boolean isOpen, hasMenu;
    double detailLat, detailLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //retrieve intent values
        Intent intent = getIntent();
        detailID = intent.getStringExtra("ID");
        detailName = intent.getStringExtra("name");
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
                detailName + " " +
                        detailID + " " +
                        detailLat +" " +
                        detailLng +" " +
                        detailCategory +" " +
                        detailStatus +" " +
                        isOpen +" " +
                        hasMenu +" " +
                        detailMenu +" " +
                        detailMobileMenu +" " +
                        detailUrl +" " +
                        detailContact +" " +
                        detailAddress +" " +
                        detailRating +" " +
                        detailRatingcolor, Toast.LENGTH_LONG).show();
    }
}
