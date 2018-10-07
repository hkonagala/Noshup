package homeaway.com.foodfinder.model;

import android.graphics.drawable.Icon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Venue {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("name")
    @Expose
    private String restaurantName;

    @SerializedName("shortName")
    @Expose
    private String category;

    @SerializedName("icon")
    @Expose
    private Icon thumbnail;

    @SerializedName("formattedPhone")
    @Expose
    private String contact;

    @SerializedName("hasMenu")
    @Expose
    private boolean hasMenu;

    @SerializedName("url")
    @Expose
    private String website;

    @SerializedName("location")
    @Expose
    private Location location;


    public Venue(String id, String restaurantName, String category, Icon thumbnail, String contact, boolean hasMenu, String website, Location location) {
        this.id = id;
        this.restaurantName = restaurantName;
        this.category = category;
        this.thumbnail = thumbnail;
        this.contact = contact;
        this.hasMenu = hasMenu;
        this.website = website;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Icon getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Icon thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isHasMenu() {
        return hasMenu;
    }

    public void setHasMenu(boolean hasMenu) {
        this.hasMenu = hasMenu;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
