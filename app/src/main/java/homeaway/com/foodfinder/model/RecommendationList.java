package homeaway.com.foodfinder.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RecommendationList {

    @SerializedName("items")
    @Expose
    private ArrayList<Recommendation> recommendations = null;

    public RecommendationList(ArrayList<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }

    public ArrayList<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(ArrayList<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
