package homeaway.com.placefinder.model.venueModel.suggestions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import homeaway.com.placefinder.model.venueModel.Meta;

public class SuggestionsResponse {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("response")
    @Expose
    private SuggestionResponseBody response;

    public SuggestionsResponse() {
    }

    public SuggestionsResponse(Meta meta, SuggestionResponseBody response) {
        this.meta = meta;
        this.response = response;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public SuggestionResponseBody getResponse() {
        return response;
    }

    public void setResponse(SuggestionResponseBody response) {
        this.response = response;
    }
}
