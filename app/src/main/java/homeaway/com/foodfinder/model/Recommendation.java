package homeaway.com.foodfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Recommendation {

    @SerializedName("venues")
    @Expose
    private Restaurant restaurant;

    @SerializedName("rating")
    @Expose
    private double rating;

    @SerializedName("isOpen")
    @Expose
    private boolean isOpen;

    public Recommendation(Restaurant restaurant, double rating, boolean isOpen) {
        this.restaurant = restaurant;
        this.rating = rating;
        this.isOpen = isOpen;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
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
