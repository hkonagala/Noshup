package homeaway.com.placefinder.model.venueModel.suggestions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SuggestionResponseBody {

    @SerializedName("categories")
    @Expose
    private List<Category> categories;

    public SuggestionResponseBody() {
    }

    public SuggestionResponseBody(List<Category> categories) {
        this.categories = categories;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
}
