package homeaway.com.foodfinder.network;

import homeaway.com.foodfinder.model.RecommendationList;
import homeaway.com.foodfinder.model.VenueList;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface FourSquareService {

    // Request method and URL specified in the annotation

    //A default request to move the current user to seattle via the Foursquare API:
    // "https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID +
    // "&client_secret=" + CLIENT_SECRET + "&near=Seattle,+WA&query=restaurants&v=20181006";
    @GET("venues/search?near=Seattle,+WA&query=restaurants&v=20181010")
    Call<VenueList> searchRestaurants (
            @Query("client_id") String clientID,
            @Query("client_secret") String clientSecret
    );


    //search recommendations based on user input using FourSquare API:
    // "https://api.foursquare.com/v2/venues/explore?client_id=" + CLIENT_ID +
    // "&client_secret=" + CLIENT_SECRET + "&near=Seattle,+WA&intent=tacos&v=20181007";
    @GET("venues/explore?near=Seattle,+WA&v=20181010&limit=5")
    Call<RecommendationList> SearchRecommendations (
            @Query("client_id") String clientID,
            @Query("client_secret") String clientSecret,
            @Query("intent") String userInput
    );


}
