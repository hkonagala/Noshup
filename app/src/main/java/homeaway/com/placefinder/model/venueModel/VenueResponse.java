package homeaway.com.placefinder.model.venueModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VenueResponse {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("response")
    @Expose
    private Response response;

    /**
     * No args constructor for use in serialization
     *
     */
    public VenueResponse() {
    }

    /**
     *
     * @param response
     * @param meta
     */
    public VenueResponse(Meta meta, Response response) {
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

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

}
