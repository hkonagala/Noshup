package homeaway.com.foodfinder;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.arlib.floatingsearchview.FloatingSearchView;

import homeaway.com.foodfinder.adapter.SearchAdapter;
import homeaway.com.foodfinder.model.venueModel.Response;
import homeaway.com.foodfinder.model.venueModel.VenueResponse;
import homeaway.com.foodfinder.network.FourSquareService;
import homeaway.com.foodfinder.network.RetrofitClientInstance;
import homeaway.com.foodfinder.util.Config;
import homeaway.com.foodfinder.util.DateUtil;
import io.reactivex.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class SearchActivity extends AppCompatActivity {

    FloatingSearchView searchView;

    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    RecyclerView.LayoutManager layoutManager;

    LottieAnimationView emptyView;

    Retrofit retrofitClientInstance;
    private CompositeDisposable disposable;

    private ProgressDialog pDialog;

    String userSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.floating_search_view);
        recyclerView = findViewById(R.id.search_rv);
        emptyView = findViewById(R.id.emptyView_rv);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        disposable = new CompositeDisposable();
        displayVenues();
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
            disposable.dispose();
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

        Log.i("Harika", "Client Id: [" + Config.FOURSQUARE_CLIENT_ID + "], "
                + "Client Secret: [" + Config.FOURSQUARE_CLIENT_SECRET + "], "
                + "Date: [" + DateUtil.getTodaysDate() + "], "
                + "place: [ " + Config.PLACE + "], "
                + "Query: [" + Config.QUERY + "]"
        );

        Call<VenueResponse> displayVenuesCall = foursquare.searchVenues(
                Config.FOURSQUARE_CLIENT_ID,
                Config.FOURSQUARE_CLIENT_SECRET,
                DateUtil.getTodaysDate(),
                Config.PLACE,
                Config.QUERY
        );

        displayVenuesCall.enqueue(new Callback<VenueResponse>() {
            @Override
            public void onResponse(Call<VenueResponse> call, retrofit2.Response<VenueResponse> response) {
                handleResults(response.body().getResponse());
            }

            @Override
            public void onFailure(Call<VenueResponse> call, Throwable t) {
                handleError(t);
            }
        });


//        try {
//            Response venueList = displayVenuesCall.execute().body();
//            handleResults(venueList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        displayVenuesCall.enqueue(new Callback<Response>() {
//            @Override
//            public void onResponse(@NonNull Call<Response> call, @NonNull Response<Response> response) {
//                handleResults(response.body());
//            }
//
//            @Override
//            public void onFailure(Call<Response> call, Throwable t) {
//                handleError(t);
//            }
//        });

//        disposable.add(
//        Response venueList = foursquare.searchVenues(
//                Config.FOURSQUARE_CLIENT_ID,
//                Config.FOURSQUARE_CLIENT_SECRET,
//                DateUtil.getTodaysDate(),
//                Config.PLACE,
//                Config.QUERY)
//                .subscribeOn(Schedulers.io()) //observable does work in a background thread
//                .observeOn(AndroidSchedulers.mainThread()) // executes results on android main thread
//                .blockingFirst();
//        //.subscribe(this::handleResults, this::handleError)
////        );
//        handleResults(venueList);
    }

    private void handleResults(Response response) {
        Log.i("Phani", "handleResults: [Results]" + response.getVenues());
        pDialog.dismiss();

//        if(response.getVenues() != null) {
            searchAdapter = new SearchAdapter(getApplicationContext(), response);
            recyclerView.setAdapter(searchAdapter);
//        } else {
//            Toast.makeText(getApplicationContext(), getString(R.string.app_name), Toast.LENGTH_LONG).show();
//        }
    }

    private void handleError(Throwable throwable) {
        pDialog.dismiss();
        Log.e("Observer", ""+ throwable.toString());
        emptyView.setVisibility(View.VISIBLE);
        emptyView.playAnimation();
        Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
    }

//    public void displayRecommendations() {
//
//        FourSquareService foursquare = getFourSquareService();
//        disposables.add(foursquare.SearchRecommendations (
//                Config.FOURSQUARE_CLIENT_ID,
//                Config.FOURSQUARE_CLIENT_SECRET,
//                DateUtil.getTodaysDate(),
//                Config.PLACE,
//                userSearch,
//                Config.LIMIT)
//                .subscribeOn(Schedulers.newThread()) //work in background thread
//                .observeOn(AndroidSchedulers.mainThread()) // executes results on android main thread
//                .subscribe(this::handleSearchResults, this::handleError));
//
//    }
//
//    private void handleSearchResults(Response venueList) {
//
//        if(venueList.getVenues() != null) {
//            //TODO create another listview for search suggestions
//        }
//    }

}
