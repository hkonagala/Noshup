package homeaway.com.foodfinder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import java.util.List;
import java.util.Optional;

import homeaway.com.foodfinder.activity.DetailsActivity;
import homeaway.com.foodfinder.activity.MapsMarkerActivity;
import homeaway.com.foodfinder.adapter.SearchAdapter;
import homeaway.com.foodfinder.model.venueModel.Contact;
import homeaway.com.foodfinder.model.venueModel.Hours;
import homeaway.com.foodfinder.model.venueModel.Location;
import homeaway.com.foodfinder.model.venueModel.Venue;
import homeaway.com.foodfinder.model.venueModel.VenueDetails;
import homeaway.com.foodfinder.model.venueModel.VenueResponse;
import homeaway.com.foodfinder.network.FourSquareService;
import homeaway.com.foodfinder.network.RetrofitClientInstance;
import homeaway.com.foodfinder.util.Config;
import homeaway.com.foodfinder.util.DateUtil;
import homeaway.com.foodfinder.util.FavoritePreferences;
import homeaway.com.foodfinder.util.PaginationAdapterCallback;
import homeaway.com.foodfinder.util.PaginationScrollListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity implements PaginationAdapterCallback, View.OnClickListener {

    private static final String TAG = SearchActivity.class.getSimpleName();
    FavoritePreferences preferences;

    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    LinearLayoutManager layoutManager;

    //empty layout for network and server issues
    RelativeLayout emptyContainer;
    LottieAnimationView emptyView;

    Retrofit retrofitClientInstance;
    //track subscriptions
    private CompositeDisposable disposable;

    private ProgressDialog pDialog;

    //pagination constants
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    // value can be modified based on the size of response
    private int TOTAL_PAGES = 6;
    private int currentPage = PAGE_START;
    Button retryButton;

    //TODO floating action button 
    FloatingActionButton fab;

    Toolbar toolbar;

    List<Venue> venueList;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //load preferences
        preferences = FavoritePreferences.getFavoritePreferences();


        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getColor(R.color.white));

        recyclerView = findViewById(R.id.search_rv);
        emptyContainer = findViewById(R.id.empty_container);
        emptyView = findViewById(R.id.emptyView_rv);
        retryButton = findViewById(R.id.error_btn_retry);
        fab = findViewById(R.id.search_fab);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        disposable = new CompositeDisposable();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //call to display list of venues in recyclerview
        displayVenues();

        retryButton.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        retryButton.setOnClickListener(null);
        fab.setOnClickListener(null);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.error_btn_retry){
            if(isNetworkConnected()){
                loadFirstPage(getFourSquareService());
//                fab.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.search_fab){
            //go to maps activity
            Gson gson = new Gson();
            String json = gson.toJson(venueList);

            Intent intent = new Intent(this, MapsMarkerActivity.class);
            intent.putExtra("venueListJson", json);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        //dispose subscriptions
        if (disposable != null && !disposable.isDisposed()) {
            disposable.clear();
        }
        super.onDestroy();
    }


    /**
     * build Retrofit and FoursquareService objects for calling the Foursquare API and parsing with GSON
     * @return foursquare api call
     */
    private FourSquareService getFourSquareService() {
        retrofitClientInstance = RetrofitClientInstance.getRetrofitInstance();
        return retrofitClientInstance.create(FourSquareService.class);
    }

    public void displayVenues() {

        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading_message));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        // Call the Foursquare API to display venues from seattle
        FourSquareService foursquare = getFourSquareService();

        searchAdapter = new SearchAdapter(this);
        recyclerView.setAdapter(searchAdapter);
        searchAdapter.setOnClickedListener(itemClickedListener);

        //pagination
        recyclerView.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                // making network delay for API call
                new Handler().postDelayed(() -> loadNextPage(foursquare), 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        Log.i(TAG, "Client Id: [" + Config.FOURSQUARE_CLIENT_ID + "], "
                + "Client Secret: [" + Config.FOURSQUARE_CLIENT_SECRET + "], "
                + "Date: [" + DateUtil.getTodaysDate() + "], "
                + "place: [ " + Config.PLACE + "], "
                + "Query: [" + Config.QUERY + "]"
        );

        loadFirstPage(foursquare);
    }

    private void loadFirstPage(FourSquareService foursquare) {
        Log.i(TAG, "loadFirstPage: ");

        disposable.add(foursquare.searchVenues(
                Config.FOURSQUARE_CLIENT_ID,
                Config.FOURSQUARE_CLIENT_SECRET,
                DateUtil.getTodaysDate(),
                Config.PLACE,
                Config.QUERY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResults, this::handleError));
    }

    private void handleResults(VenueResponse venueResponse) {
        pDialog.dismiss();

        venueList = venueResponse.getResponse().getVenues();
        if( venueList!= null) {
            searchAdapter.addAll(venueList);

            if (currentPage <= TOTAL_PAGES) {
                searchAdapter.addLoadingFooter();
            } else {
                isLastPage = true;
            }
        }
    }

    private void loadNextPage(FourSquareService foursquare) {
        Log.d(TAG, "loadNextPage: " + currentPage);

        disposable.add(foursquare.searchVenues(
                Config.FOURSQUARE_CLIENT_ID,
                Config.FOURSQUARE_CLIENT_SECRET,
                DateUtil.getTodaysDate(),
                Config.PLACE,
                Config.QUERY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleNextResults, this::handleError));
    }

    private void handleNextResults(VenueResponse venueResponse) {
        pDialog.dismiss();
        venueList = venueResponse.getResponse().getVenues();
        if( venueList != null){
            searchAdapter.removeLoadingFooter();
            isLoading = false;

            searchAdapter.addAll(venueList);

            if (currentPage != TOTAL_PAGES) {
                searchAdapter.addLoadingFooter();
            } else {
                isLastPage = true;
            }
        }
    }

    private void handleError(Throwable throwable) {
        pDialog.dismiss();
        Log.e("Observer", ""+ throwable.toString());
        emptyContainer.setVisibility(View.VISIBLE);
        emptyView.playAnimation();
        fab.setVisibility(View.GONE);
//        recyclerView.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
    }

    private SearchAdapter.OnItemClickedListener itemClickedListener = (response, position) -> {
        //redirect to details activity
        if(response != null) {
            getDetails(response.getId());

        }
    };

    /**
     * display details of a venue
     */
    private void getDetails(String id) {
        FourSquareService foursquare = getFourSquareService();
        disposable.add(foursquare.getVenueDetails (
                id,
                Config.FOURSQUARE_CLIENT_ID,
                Config.FOURSQUARE_CLIENT_SECRET,
                DateUtil.getTodaysDate())
                .subscribeOn(Schedulers.newThread()) //work in background thread
                .observeOn(AndroidSchedulers.mainThread()) // executes results on android main thread
                .subscribe(this::handleDetailResults, this::handleSearchError));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleDetailResults(VenueDetails venueDetails) {
        Venue venue = venueDetails.getResponse().getVenue();
        if(venue.getId() != null) {
            Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
            // Passes the extra venue details

            Gson gson = new Gson();
            String jsonFavorites = gson.toJson(venue);
            intent.putExtra("venueJson", jsonFavorites);
            intent.putExtra("ID",venue.getId());
            intent.putExtra("name", venue.getName());

            //icon
            String url = venue.getCategories().get(0).getIcon().getPrefix();
            if(url.contains("ss3.4sqi.net")){
                url = url.replace("ss3.4sqi.net", "foursquare.com");
                url = url + "64" + venue.getCategories().get(0).getIcon().getSuffix();
            }
            Uri uri = Uri.parse(url);
            intent.putExtra("icon", uri);
            intent.putExtra("category", venue.getCategories().get(0).getName());

            //pass hours data if available
            intent.putExtra("openHourStatus",
                    Optional.ofNullable(venue.getHours())
                            .map(Hours::getStatus)
                            .orElse(null));
            intent.putExtra("isOpenNow",
                    Optional.ofNullable(venue.getHours())
                            .map(Hours::getOpen)
                            .orElse(false));

            //check for menu, if yes pass menu link
            intent.putExtra("hasMenu", venue.getHasMenu());
            intent.putExtra("menu", Optional.ofNullable(venue.getMenu())
                    .map(homeaway.com.foodfinder.model.venueModel.Menu::getUrl)
                    .orElse(null));
            intent.putExtra("mobileMenu", Optional.ofNullable(venue.getMenu())
                    .map(homeaway.com.foodfinder.model.venueModel.Menu::getMobileUrl)
                    .orElse(null));
            //otherwise pass url if available
            intent.putExtra("url", venue.getUrl());

            //contact
            intent.putExtra("contact", Optional.ofNullable(venue.getContact())
                    .map(Contact::getFormattedPhone)
                    .orElse(venue.getContact().getPhone()));

            //address
            intent.putExtra("address", venue.getLocation().getFormattedAddress().get(0) +
                    venue.getLocation().getFormattedAddress().get(1)); //full address

            //location for mapview
            intent.putExtra("latitude", Optional.ofNullable(venue.getLocation())
                    .map(Location::getLat)
                    .orElse(0d));
            intent.putExtra("longitude", Optional.ofNullable(venue.getLocation())
                    .map(Location::getLng)
                    .orElse(0d));

            //rating
            intent.putExtra("rating", venue.getRating());

            //rating color
            intent.putExtra("ratingcolor", venue.getRatingColor());
            startActivityForResult(intent, 0);
        }
    }

    //get the status from details activity to update recyclerview with latest changes
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if(data.getStringExtra("passed_item").equals("fromDetailsActivity")){
                searchAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void retryPageLoad() {
        if(isNetworkConnected()){
            loadNextPage(getFourSquareService());
            emptyContainer.setVisibility(View.GONE);
            emptyView.clearAnimation();
//            fab.setVisibility(View.VISIBLE);
        }

    }

    /**
     * searchview implementation for displaying search suggestions and loading the recyclerview with new response
     */

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        SearchView.SearchAutoComplete mSearchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchAutoComplete.setDropDownAnchor(R.id.action_search);
        mSearchAutoComplete.setThreshold(0);

        //search request
        searchView.setQueryHint(getString(R.string.search_restaurants));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                displayRecommendations(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                displayRecommendations(newText);
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    public void displayRecommendations(String mLastQuery) {

        FourSquareService foursquare = getFourSquareService();

        disposable.add(foursquare.SearchRecommendations (
                Config.FOURSQUARE_CLIENT_ID,
                Config.FOURSQUARE_CLIENT_SECRET,
                DateUtil.getTodaysDate(),
                Config.PLACE,
                mLastQuery,
                Config.LIMIT)
                .subscribeOn(Schedulers.newThread()) //work in background thread
                .observeOn(AndroidSchedulers.mainThread()) // executes results on android main thread
                .subscribe(this::handleSearchResults, this::handleSearchError));

    }

    private void handleSearchResults(VenueResponse venueResponse) {
//        if(venueResponse.getResponse().getVenues() != null) {
//            //TODO
//        }
    }

    private void handleSearchError(Throwable throwable) {
        pDialog.dismiss();
        Log.e("Observer", ""+ throwable.toString());
        Toast.makeText(this, getString(R.string.server_error), Toast.LENGTH_LONG).show();
    }


    /**
     * helper method for checking if the app is connected to the network or not
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null) &&
                activeNetwork.isConnectedOrConnecting();
    }
}
