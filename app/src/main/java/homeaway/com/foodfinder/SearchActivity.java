package homeaway.com.foodfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.arlib.floatingsearchview.FloatingSearchView;

import homeaway.com.foodfinder.activity.DetailsActivity;
import homeaway.com.foodfinder.adapter.SearchAdapter;
import homeaway.com.foodfinder.model.venueModel.VenueResponse;
import homeaway.com.foodfinder.network.FourSquareService;
import homeaway.com.foodfinder.network.RetrofitClientInstance;
import homeaway.com.foodfinder.util.BookmarkPreferences;
import homeaway.com.foodfinder.util.Config;
import homeaway.com.foodfinder.util.DateUtil;
import homeaway.com.foodfinder.util.PaginationAdapterCallback;
import homeaway.com.foodfinder.util.PaginationScrollListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity implements PaginationAdapterCallback {

    BookmarkPreferences preferences;
    FloatingSearchView searchView;

    RecyclerView recyclerView;

    SearchAdapter searchAdapter;
    LinearLayoutManager layoutManager;

    RelativeLayout emptyContainer;
    LottieAnimationView emptyView;

    Retrofit retrofitClientInstance;

    //track subscriptions
    private CompositeDisposable disposable;

    private ProgressDialog pDialog;

    String userSearch;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //load preferences
        preferences = BookmarkPreferences.getBookmarkPreferences();

        searchView = findViewById(R.id.floating_search_view);
        recyclerView = findViewById(R.id.search_rv);
        emptyContainer = findViewById(R.id.empty_container);
        emptyView = findViewById(R.id.emptyView_rv);
        retryButton = findViewById(R.id.error_btn_retry);
        fab = findViewById(R.id.search_fab);
        //handling fab scroll behavior
//        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
//        p.setBehavior(new ScrollAwareFABBehavior(this, null));
//        fab.setLayoutParams(p);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        disposable = new CompositeDisposable();
        displayVenues();

        retryButton.setOnClickListener(view -> loadFirstPage(getFourSquareService()));
        fab.setOnClickListener(view -> {
            //go to maps activity
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        searchView.setOnQueryChangeListener((oldQuery, newQuery) -> {
            userSearch = newQuery;
//            displayRecommendations();
//            Toast.makeText(getApplicationContext(), userSearch, Toast.LENGTH_LONG).show();
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

    @Override
    protected void onDestroy() {
        //dispose subscriptions
        if (disposable != null && !disposable.isDisposed()) {
            disposable.clear();
        }

        super.onDestroy();
    }

    private FourSquareService getFourSquareService() {

        // Builds Retrofit and FoursquareService objects for calling the Foursquare API and parsing with GSON
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

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage(foursquare);
                    }
                }, 1000);
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

        Log.i("Harika", "Client Id: [" + Config.FOURSQUARE_CLIENT_ID + "], "
                + "Client Secret: [" + Config.FOURSQUARE_CLIENT_SECRET + "], "
                + "Date: [" + DateUtil.getTodaysDate() + "], "
                + "place: [ " + Config.PLACE + "], "
                + "Query: [" + Config.QUERY + "]"
        );

        loadFirstPage(foursquare);

//        Call<VenueResponse> displayVenuesCall = foursquare.searchVenues(
//                Config.FOURSQUARE_CLIENT_ID,
//                Config.FOURSQUARE_CLIENT_SECRET,
//                DateUtil.getTodaysDate(),
//                Config.PLACE,
//                Config.QUERY
//        );

        /*displayVenuesCall.enqueue(new Callback<VenueResponse>() {
            @Override
            public void onResponse(Call<VenueResponse> call, retrofit2.Response<VenueResponse> response) {
                handleResults(response.body().getResponse());
            }

            @Override
            public void onFailure(Call<VenueResponse> call, Throwable t) {
                handleError(t);
            }
        });*/
    }

    private void loadFirstPage(FourSquareService foursquare) {
        Log.i("harika", "loadFirstPage: ");

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

        if(venueResponse.getResponse().getVenues() != null) {
            searchAdapter.addAll(venueResponse.getResponse().getVenues());

            if (currentPage <= TOTAL_PAGES) {
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
//        emptyView.setVisibility(View.VISIBLE);
        emptyView.playAnimation();
//        retryButton.setVisibility(View.VISIBLE);
        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
    }

    private void loadNextPage(FourSquareService foursquare) {
        Log.d("harika", "loadNextPage: " + currentPage);

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
        if(venueResponse.getResponse().getVenues() != null){
            searchAdapter.removeLoadingFooter();
            isLoading = false;

            searchAdapter.addAll(venueResponse.getResponse().getVenues());

            if (currentPage != TOTAL_PAGES) {
                searchAdapter.addLoadingFooter();
            } else {
                isLastPage = true;
            }
        }
    }

    private SearchAdapter.OnItemClickedListener itemClickedListener = (response, position) -> {
        //redirect to details activity
        if(response != null){
            Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
            // Passes the extra venue details
            intent.putExtra("name", response.getName());
            intent.putExtra("ID", response.getId());
            intent.putExtra("latitude", response.getLocation().getLat());
            intent.putExtra("longitude", response.getLocation().getLng());
            startActivity(intent);
        }
    };

    /*private void handleResults(Response response) {
        Log.i("harika", "handleResults: [Results]" + response.getVenues());
        pDialog.dismiss();

        if(response.getVenues() != null) {
            searchAdapter = new SearchAdapter(getApplicationContext(), response);
            recyclerView.setAdapter(searchAdapter);
        }
    }*/



    public void displayRecommendations() {

        FourSquareService foursquare = getFourSquareService();
        disposable.add(foursquare.SearchRecommendations (
                Config.FOURSQUARE_CLIENT_ID,
                Config.FOURSQUARE_CLIENT_SECRET,
                DateUtil.getTodaysDate(),
                Config.PLACE,
                userSearch,
                Config.LIMIT)
                .subscribeOn(Schedulers.newThread()) //work in background thread
                .observeOn(AndroidSchedulers.mainThread()) // executes results on android main thread
                .subscribe(this::handleSearchResults, this::handleSearchError));

    }

    private void handleSearchResults(VenueResponse venueResponse) {
        if(venueResponse.getResponse().getVenues() != null) {
            //TODO
        }
    }

    private void handleSearchError(Throwable throwable) {
        Log.e("Observer", ""+ throwable.toString());
        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
    }


    @Override
    public void retryPageLoad() {
        loadNextPage(getFourSquareService());
    }
}
