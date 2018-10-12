package homeaway.com.placefinder;

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
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import homeaway.com.placefinder.activity.DetailsActivity;
import homeaway.com.placefinder.activity.MapsMarkerActivity;
import homeaway.com.placefinder.adapter.SearchAdapter;
import homeaway.com.placefinder.model.venueModel.Contact;
import homeaway.com.placefinder.model.venueModel.Hours;
import homeaway.com.placefinder.model.venueModel.Location;
import homeaway.com.placefinder.model.venueModel.Venue;
import homeaway.com.placefinder.model.venueModel.VenueDetails;
import homeaway.com.placefinder.model.venueModel.VenueResponse;
import homeaway.com.placefinder.model.venueModel.suggestions.Category;
import homeaway.com.placefinder.model.venueModel.suggestions.SuggestionsResponse;
import homeaway.com.placefinder.network.FourSquareService;
import homeaway.com.placefinder.network.RetrofitClientInstance;
import homeaway.com.placefinder.util.Config;
import homeaway.com.placefinder.util.DateUtil;
import homeaway.com.placefinder.util.FavoritePreferences;
import homeaway.com.placefinder.util.KeyboardUtil;
import homeaway.com.placefinder.util.PaginationAdapterCallback;
import homeaway.com.placefinder.util.PaginationScrollListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * Activity which displays a list of suggestions, search results from foursquare api and redirects to maps and details activity
 */
public class SearchActivity extends AppCompatActivity implements PaginationAdapterCallback, View.OnClickListener, AdapterView.OnItemClickListener {

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

    //floating action button
    FloatingActionButton fab;
    AutoCompleteTextView searchView;

    //search suggestion values
    String searchText;
    List<Venue> venueList;
    List<Category> suggestions;
    ArrayAdapter<String> adapter;

    private Instant previousSuggestionLoadedTime;

    private SuggestionsResponse suggestionsResponse;

    List<String> suggestionStrings;


    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        previousSuggestionLoadedTime = Instant.now().minusSeconds(60*60);

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //call to display list of venues in recyclerview
        displayVenues();

        retryButton.setOnClickListener(this);
        fab.setOnClickListener(this);
        displaySuggestions();
        searchView.setOnItemClickListener(this);

        searchView.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                searchText = searchView.getText().toString();
                displayRecommendations(searchText);
                KeyboardUtil.hideKeyboard(SearchActivity.this);
                return true;
            }
            return false;
        });

    }


    private void initViews() {
        //load preferences
        preferences = FavoritePreferences.getFavoritePreferences();

        recyclerView = findViewById(R.id.search_rv);
        emptyContainer = findViewById(R.id.empty_container);
        emptyView = findViewById(R.id.emptyView_rv);
        retryButton = findViewById(R.id.error_btn_retry);
        fab = findViewById(R.id.search_fab);
        searchView = findViewById(R.id.search_edittext);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        disposable = new CompositeDisposable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        retryButton.setOnClickListener(null);
        fab.setOnClickListener(null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String selection = (String)adapterView.getItemAtPosition(i);
        displayRecommendations(selection);
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

        FourSquareService foursquare = setupPaginationList();
        loadFirstPage(foursquare);
    }

    private FourSquareService setupPaginationList() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(getString(R.string.loading_message));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        searchAdapter = new SearchAdapter(this);
        recyclerView.setAdapter(searchAdapter);
        searchAdapter.setOnClickedListener(itemClickedListener);

        // Call the Foursquare API to display venues from seattle
        FourSquareService foursquare = getFourSquareService();
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
        return foursquare;
    }


    @TargetApi(Build.VERSION_CODES.N)
    private void loadFirstPage(FourSquareService foursquare) {
        Log.i(TAG, "loadFirstPage: ");

        emptyContainer.setVisibility(View.GONE);
        emptyView.clearAnimation();
//        recyclerView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
//        searchView.setText("");

        String searchString = Config.QUERY;
        AtomicReference<String> atomicString = new AtomicReference<>();
        Optional.ofNullable(searchView)
                .map(AutoCompleteTextView::getText)
                .map(Editable::toString)
                .ifPresent(atomicString::set);
        if (atomicString.get() != null) {
            searchString = atomicString.get();
        }

        disposable.add(foursquare.searchVenues(
                Config.FOURSQUARE_CLIENT_ID,
                Config.FOURSQUARE_CLIENT_SECRET,
                DateUtil.getTodaysDate(),
                Config.PLACE,
                searchString)
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
                    .map(homeaway.com.placefinder.model.venueModel.Menu::getUrl)
                    .orElse(null));
            intent.putExtra("mobileMenu", Optional.ofNullable(venue.getMenu())
                    .map(homeaway.com.placefinder.model.venueModel.Menu::getMobileUrl)
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
        }

    }

    public void displayRecommendations(String mLastQuery) {

        FourSquareService foursquare = setupPaginationList();

        disposable.add(foursquare.searchVenues(
                Config.FOURSQUARE_CLIENT_ID,
                Config.FOURSQUARE_CLIENT_SECRET,
                DateUtil.getTodaysDate(),
                Config.PLACE,
                mLastQuery)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResults, this::handleError));
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void displaySuggestions() {

        if (!isNotEmpty(suggestionStrings)
                || suggestionsResponse == null
                || Instant.now().toEpochMilli() - previousSuggestionLoadedTime.toEpochMilli() >= 60*60*1000) {
            FourSquareService fourSquareService = getFourSquareService();
            disposable.add(fourSquareService.getSuggestions(
                    Config.FOURSQUARE_CLIENT_ID,
                    Config.FOURSQUARE_CLIENT_SECRET,
                    DateUtil.getTodaysDate())
                    .subscribeOn(Schedulers.newThread()) //work in background thread
                    .observeOn(AndroidSchedulers.mainThread()) // executes results on android main thread
                    .subscribe((suggestionsResponse) -> {
                        handleSearchResults(suggestionsResponse);
                    }, this::handleSearchError));
            previousSuggestionLoadedTime = Instant.now();
        }
    }

    private void handleSearchResults(SuggestionsResponse suggestionsResponse) {
        if(suggestionsResponse != null){
            this.suggestionsResponse = suggestionsResponse;
            suggestions = suggestionsResponse.getResponse().getCategories();
            suggestionStrings = parseSuggestionsResponse(suggestionsResponse);
            adapter = new ArrayAdapter<>
                    (this, android.R.layout.simple_list_item_1, suggestionStrings);
            //Getting the instance of AutoCompleteTextView
            searchView.setThreshold(1);//will start working from first character
            searchView.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
            searchView.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }



    private List<String> parseSuggestionsResponse(SuggestionsResponse suggestionsResponse) {
        List<String> suggestionsStrings = new ArrayList<>();

        if (suggestionsResponse != null && suggestionsResponse.getResponse() != null) {
            List<Category> categories = suggestionsResponse.getResponse().getCategories();
            if (categories != null) {
                parseCategories(suggestionsStrings, categories);
            }

        }

        return suggestionsStrings;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void parseCategories(List<String> suggestionsStrings, List<Category> categories) {
        categories.forEach(category -> {
            if (isNotBlank(category.getName())) {
                suggestionsStrings.add(category.getName());
            }
//            if (isNotBlank(category.getShortName())) {
//                suggestionsStrings.add(category.getShortName());
//            }
            if (isNotEmpty(category.getCategories())) {
                parseCategories(suggestionsStrings, category.getCategories());
            }
        });
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

    /**
     * string utils
     * @param s
     * @return
     */
    private boolean isNotBlank(String s) {
         return s != null && !s.isEmpty();
    }

    private boolean isNotEmpty(Collection c) {
        return c != null && !c.isEmpty();
    }
}
