package homeaway.com.placefinder.model.venueModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VenueDetails {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("response")
    @Expose
    private SingleVenueResponse response;

    /**
     * No args constructor for use in serialization
     *
     */
    public VenueDetails() {
    }

    /**
     *
     * @param response
     * @param meta
     */
    public VenueDetails(Meta meta, SingleVenueResponse response) {
        super();
        this.meta = meta;
        this.response = response;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public SingleVenueResponse getResponse() {
        return response;
    }

    public void setResponse(SingleVenueResponse response) {
        this.response = response;
    }

}
