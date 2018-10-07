package homeaway.com.foodfinder.model;

import com.google.gson.annotations.SerializedName;

public class Location {

    @SerializedName("lat")
    private double lat;

    @SerializedName("lng")
    private double lng;

    @SerializedName("formattedAddress")
    private String address;

    @SerializedName("distance")
    private int distance;

    public Location(double lat, double lng, String address, int distance) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
