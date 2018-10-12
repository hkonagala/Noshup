package homeaway.com.placefinder.model.venueModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * API call for a single venue item
 */
public class SingleVenueResponse {

    @SerializedName("venue")
    @Expose
    private Venue venue = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public SingleVenueResponse() {
    }

    /**
     *
     * @param venue
     */
    public SingleVenueResponse(Venue venue) {
        super();
        this.venue = venue;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

}
