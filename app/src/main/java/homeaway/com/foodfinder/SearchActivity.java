package homeaway.com.foodfinder;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;


import com.arlib.floatingsearchview.FloatingSearchView;


import java.util.List;

import homeaway.com.foodfinder.adapter.SearchAdapter;
import homeaway.com.foodfinder.model.Restaurant;
import homeaway.com.foodfinder.model.VenueList;
import homeaway.com.foodfinder.network.FourSquareService;
import homeaway.com.foodfinder.network.RetrofitClientInstance;
import homeaway.com.foodfinder.util.Constant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    FloatingSearchView searchView;
    RecyclerView restaurantsList;
    private List<Restaurant> venueList;
    SearchAdapter searchAdapter;
    RecyclerView.LayoutManager layoutManager;
    Retrofit retrofitClientInstance;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.floating_search_view);
        restaurantsList = findViewById(R.id.search_rv);

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

    private FourSquareService getFourSquareService() {

        // Builds Retrofit and FoursquareService objects for calling the Foursquare API and parsing with GSON
        retrofitClientInstance = RetrofitClientInstance.getRetrofitInstance();
        return retrofitClientInstance.create(FourSquareService.class);
    }

    public void DisplayRestaurants() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading_message));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        FourSquareService foursquare = getFourSquareService();


        // Calls the Foursquare API to display venues from seattle
        Call<VenueList> displayVenuesCall = foursquare.searchRestaurants (
                Constant.FOURSQUARE_CLIENT_ID,
                Constant.FOURSQUARE_CLIENT_SECRET
                );

        displayVenuesCall.enqueue(new Callback<VenueList>() {
            @Override
            public void onResponse(Call<VenueList> call, Response<VenueList> response) {

                //Dismiss Dialog
                pDialog.dismiss();

                if(response.isSuccessful()){
                    // Gets the venue object from the JSON response
                    if (response.body() != null) {
                        try{
                            venueList = response.body().getVenues();
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }

                        // Displays the results in the RecyclerView
                        searchAdapter = new SearchAdapter(getApplicationContext(), venueList);
                        layoutManager = new LinearLayoutManager(getApplicationContext());
                        restaurantsList.setLayoutManager(layoutManager);
                        restaurantsList.setHasFixedSize(true);
                        restaurantsList.setItemAnimator(new DefaultItemAnimator());
                        restaurantsList.setAdapter(searchAdapter);
                    }

                }

            }

            @Override
            public void onFailure(Call<VenueList> call, Throwable t) {
                Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
                finish();
            }
        });

    }


}
