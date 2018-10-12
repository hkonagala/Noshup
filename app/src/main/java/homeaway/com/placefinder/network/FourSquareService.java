package homeaway.com.placefinder.network;

import homeaway.com.placefinder.model.venueModel.VenueDetails;
import homeaway.com.placefinder.model.venueModel.VenueResponse;
import homeaway.com.placefinder.model.venueModel.suggestions.SuggestionsResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FourSquareService {

    // Request method and URL specified in the annotation

    //A default request to move the current user to seattle via the Foursquare API:
    // "https://api.foursquare.com/v2/venues/search?client_id=" + CLIENT_ID +
    // "&client_secret=" + CLIENT_SECRET + "&near=Seattle,+WA&query=restaurants&v=20181006";
    @GET("/v2/venues/search")
    Observable<VenueResponse> searchVenues (
            @Query("client_id") String clientID,
            @Query("client_secret") String clientSecret,
            @Query("v") String date,
            @Query("near") String place,
            @Query("query") String query
    );


    //search recommendations based on user input using FourSquare API:
    // "https://api.foursquare.com/v2/venues/explore?client_id=" + CLIENT_ID +
    // "&client_secret=" + CLIENT_SECRET + "&near=Seattle,+WA&intent=tacos&v=20181007";
    @GET("/v2/venues/explore")
    Observable<VenueResponse> SearchRecommendations (
            @Query("client_id") String clientID,
            @Query("client_secret") String clientSecret,
            @Query("v") String date,
            @Query("near") String place,
            @Query("section") String userInput
    );

    //get request for details of a venue
    @GET("/v2/venues/{venueID}")
    Observable<VenueDetails> getVenueDetails (
            @Path("venueID") String id,
            @Query("client_id") String clientID,
            @Query("client_secret") String clientSecret,
            @Query("v") String date

    );

    //get request for auto suggestions
    @GET("/v2/venues/categories")
    Observable<SuggestionsResponse> getSuggestions (
            @Query("client_id") String clientID,
            @Query("client_secret") String clientSecret,
            @Query("v") String date
    );
}
