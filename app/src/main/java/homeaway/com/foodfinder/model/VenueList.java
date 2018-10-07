package homeaway.com.foodfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class VenueList {

    @SerializedName("venues")
    @Expose
    private ArrayList<Restaurant> venues = null;

    public VenueList(ArrayList<Restaurant> venues) {
        this.venues = venues;
    }

    public ArrayList<Restaurant> getVenues() {
        return venues;
    }

    public void setVenues(ArrayList<Restaurant> venues) {
        this.venues = venues;
    }
}
