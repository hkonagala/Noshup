package homeaway.com.foodfinder.model.bookmarkModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import homeaway.com.foodfinder.model.venueModel.Venue;

public class Bookmark {

    @SerializedName("venues")
    @Expose
    private Venue venue;
    @Expose
    private boolean isBookmarked;


    /**
     * No args constructor for use in serialization
     *
     */

    public Bookmark() {
    }

    public Bookmark(Venue venue, boolean isBookmarked) {
        this.venue = venue;
        this.isBookmarked = isBookmarked;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public boolean isBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }
}
