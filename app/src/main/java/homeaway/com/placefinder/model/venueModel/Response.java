package homeaway.com.placefinder.model.venueModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * API call for list of venues
 */
public class Response {

    @SerializedName("venues")
    @Expose
    private List<Venue> venues = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Response() {
    }

    /**
     *
     * @param venues
     */
    public Response(List<Venue> venues) {
        super();
        this.venues = venues;
    }

    public List<Venue> getVenues() {
        return venues;
    }

    public void setVenues(List<Venue> venues) {
        this.venues = venues;
    }

}
