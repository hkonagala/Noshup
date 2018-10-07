package homeaway.com.foodfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VenueList {

    @SerializedName("venues")
    @Expose
    private List<Restaurant> venues;

    public VenueList(List<Restaurant> venues) {
        this.venues = venues;
    }

    public List<Restaurant> getVenues() {
        return venues;
    }

    public void setVenues(List<Restaurant> venues) {
        this.venues = venues;
    }
}
