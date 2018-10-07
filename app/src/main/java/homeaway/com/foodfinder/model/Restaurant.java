package homeaway.com.foodfinder.model;

import com.google.gson.annotations.SerializedName;

public class Restaurant {

    @SerializedName("name")
    private String restaurantName;

    @SerializedName("shortName")
    private String category;

    @SerializedName("icon")
    private String thumbnailUrl;

    @SerializedName("formattedPhone")
    private String contact;

    @SerializedName("hasMenu")
    private boolean hasMenu;

    @SerializedName("menuUrl")
    private String menuUrl;

    @SerializedName("websiteUrl")
    private String websiteUrl;

    @SerializedName("location")
    private Location location;


    public Restaurant(String restaurantName, String category, String thumbnailUrl, String contact, boolean hasMenu, String menuUrl, String websiteUrl, Location location) {
        this.restaurantName = restaurantName;
        this.category = category;
        this.thumbnailUrl = thumbnailUrl;
        this.contact = contact;
        this.hasMenu = hasMenu;
        this.menuUrl = menuUrl;
        this.websiteUrl = websiteUrl;
        this.location = location;
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

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
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

    public String getMenuUrl() {
        return menuUrl;
    }

    public void setMenuUrl(String menuUrl) {
        this.menuUrl = menuUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
