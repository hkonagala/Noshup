package homeaway.com.placefinder.model.venueModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Menu {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("anchor")
    @Expose
    private String anchor;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("mobileUrl")
    @Expose
    private String mobileUrl;

    /**
     * No args constructor for use in serialization
     *
     */
    public Menu() {
    }

    /**
     *
     * @param label
     * @param type
     * @param mobileUrl
     * @param url
     * @param anchor
     */
    public Menu(String type, String label, String anchor, String url, String mobileUrl) {
        super();
        this.type = type;
        this.label = label;
        this.anchor = anchor;
        this.url = url;
        this.mobileUrl = mobileUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public void setMobileUrl(String mobileUrl) {
        this.mobileUrl = mobileUrl;
    }

}
