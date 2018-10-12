package homeaway.com.placefinder.model.venueModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Contact {

    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("formattedPhone")
    @Expose
    private String formattedPhone;

    /**
     * No args constructor for use in serialization
     *
     */
    public Contact() {
    }

    /**
     *
     * @param phone
     * @param formattedPhone
     */
    public Contact(String phone, String formattedPhone) {
        super();
        this.phone = phone;
        this.formattedPhone = formattedPhone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFormattedPhone() {
        return formattedPhone;
    }

    public void setFormattedPhone(String formattedPhone) {
        this.formattedPhone = formattedPhone;
    }

}
