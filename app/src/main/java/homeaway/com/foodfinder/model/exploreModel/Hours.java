package homeaway.com.foodfinder.model.exploreModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hours {

    @SerializedName("isOpen")
    @Expose
    private Boolean isOpen;
    @SerializedName("isLocalHoliday")
    @Expose
    private Boolean isLocalHoliday;

    /**
     * No args constructor for use in serialization
     *
     */
    public Hours() {
    }

    /**
     *
     * @param isLocalHoliday
     * @param isOpen
     */
    public Hours(Boolean isOpen, Boolean isLocalHoliday) {
        super();
        this.isOpen = isOpen;
        this.isLocalHoliday = isLocalHoliday;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Boolean getIsLocalHoliday() {
        return isLocalHoliday;
    }

    public void setIsLocalHoliday(Boolean isLocalHoliday) {
        this.isLocalHoliday = isLocalHoliday;
    }

}