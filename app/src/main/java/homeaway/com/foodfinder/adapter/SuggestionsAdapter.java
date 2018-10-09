package homeaway.com.foodfinder.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import homeaway.com.foodfinder.R;
import homeaway.com.foodfinder.model.venueModel.Response;
import homeaway.com.foodfinder.model.venueModel.Venue;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {

    // The application context for getting resources
    private Context context;

    // The list of results from the Foursquare API
    private Response results;

    public SuggestionsAdapter(Context context, Response results) {
        this.context = context;
        this.results = results;
    }

    @NonNull
    @Override
    public SuggestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionsAdapter.ViewHolder holder, int position) {
        // Sets each view with the appropriate venue details
        Venue venue = results.getVenues().get(position);

        holder.suggestionName.setText(venue.getName());
        if(venue.getHours().getIsOpen()){
            holder.suggestionHours.setText(context.getResources().getString(R.string.open_now));
        } else {
            holder.suggestionHours.setText(context.getResources().getString(R.string.closed));
        }

        double ratingColor = Double.parseDouble(venue.getRatingColor());
        if (ratingColor >= 9.0) {
            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Kale));
        } else if (ratingColor >= 8.0) {
            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Guacamole));
        } else if (ratingColor >= 7.0) {
            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Lime));
        } else if (ratingColor >= 6.0) {
            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Banana));
        } else if (ratingColor >= 5.0) {
            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Orange));
        } else if (ratingColor >= 4.0) {
            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.MacCheese));
        } else {
            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Strawberry));
        }
    }

    @Override
    public int getItemCount() {
        List<Venue> venues = results.getVenues();
        if (venues != null) {
            return venues.size();
        } else {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView suggestionName, suggestionHours, suggestionRating;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            suggestionName = itemView.findViewById(R.id.suggestion_name);
            suggestionHours = itemView.findViewById(R.id.suggestion_hours);
            suggestionRating = itemView.findViewById(R.id.suggestion_rating);
        }

        @Override
        public void onClick(View view) {
            //do something
        }
    }
}
