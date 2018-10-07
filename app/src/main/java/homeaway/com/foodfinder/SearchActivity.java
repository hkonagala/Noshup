package homeaway.com.foodfinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    FloatingSearchView searchView;
    RecyclerView restaurantsList;

    public static final String CLIENT_ID = "5T2VIFHWWPITXXDVGVZVOXJPPIGR2WV1QLUSSP5URFLGQ2DS";
    public static final String CLIENT_SECRET = "TPTIFH52P3Y0ZR3F0CP1FUQCER5KZR5XW4GF1WPJY50GLJXE";
    private static String DEFAULT_QUERY = "restaurants";
    private static String DATE;
    public static final String SEARCH_API = "https://api.foursquare.com/v2/venues/search" +
            "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&near=Seattle,+WA" +
            "&query=" + DEFAULT_QUERY + "&v=" + DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.floating_search_view);
        restaurantsList = findViewById(R.id.search_rv);

        DATE = getTodayDate();

    }

    @Override
    protected void onResume() {
        super.onResume();

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, String newQuery) {
                //do something
            }
        });

        searchView.setOnLeftMenuClickListener(new FloatingSearchView.OnLeftMenuClickListener() {
            @Override
            public void onMenuOpened() {
                //do something
            }

            @Override
            public void onMenuClosed() {
                //do something
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /***************************************helper methods************************************/

    public String getTodayDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyyMMdd", Locale.US);
        return mdformat.format(calendar.getTime());
    }

}
