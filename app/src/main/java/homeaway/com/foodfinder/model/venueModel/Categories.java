package homeaway.com.foodfinder.model.venueModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Categories {

    @SerializedName("0")
    @Expose
    private List<CategoryItem> categoryItem;

    /**
     * No args constructor for use in serialization
     *
     */
    public Categories() {
    }

    /**
     *
     * @param categoryItem
     */
    public Categories(List<CategoryItem> categoryItem) {
        super();
        this.categoryItem = categoryItem;
    }

    public List<CategoryItem> getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(List<CategoryItem> categoryItem) {
        this.categoryItem = categoryItem;
    }

}
