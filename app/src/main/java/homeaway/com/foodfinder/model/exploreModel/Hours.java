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

    @SerializedName("status")
    @Expose
    private String status;

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
     * @param status
     */
    public Hours(Boolean isOpen, Boolean isLocalHoliday, String status) {
        this.isOpen = isOpen;
        this.isLocalHoliday = isLocalHoliday;
        this.status = status;
    }

    public Boolean getOpen() {
        return isOpen;
    }

    public void setOpen(Boolean open) {
        isOpen = open;
    }

    public Boolean getLocalHoliday() {
        return isLocalHoliday;
    }

    public void setLocalHoliday(Boolean localHoliday) {
        isLocalHoliday = localHoliday;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}