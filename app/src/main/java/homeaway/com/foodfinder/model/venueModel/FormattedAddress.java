package homeaway.com.foodfinder.model.venueModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FormattedAddress {

    @SerializedName("0")
    @Expose
    private String firstLine;
    @SerializedName("1")
    @Expose
    private String secLine;

    /**
     * No args constructor for use in serialization
     *
     */
    public FormattedAddress() {
    }

    /**
     *
     * @param firstLine
     * @param secLine
     */
    public FormattedAddress(String firstLine, String secLine) {
        super();
        this.firstLine = firstLine;
        this.secLine = secLine;
    }

    public String get0() {
        return firstLine;
    }

    public void set0(String firstLine) {
        this.firstLine = firstLine;
    }

    public String get1() {
        return secLine;
    }

    public void set1(String secLine) {
        this.secLine = secLine;
    }

}
