package homeaway.com.foodfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recommendation {

    @SerializedName("venues")
    @Expose
    private Venue venue;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("isOpen")
    @Expose
    private boolean isOpen;

    public Recommendation(Venue venue, double rating, boolean isOpen) {
        this.venue = venue;
        this.rating = rating;
        this.isOpen = isOpen;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }
}
